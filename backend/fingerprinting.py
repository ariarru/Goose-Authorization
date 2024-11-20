import os
import json
import sys
import datetime
import pandas as pd
from anyio import current_time
from dotenv import load_dotenv
from supabase import create_client, Client
from sklearn.model_selection import train_test_split
from sklearn.ensemble import RandomForestClassifier
from sklearn.preprocessing import LabelEncoder

current_time = datetime.datetime.now()

# Calcola il percorso relativo
dotenv_path = os.path.join(os.path.dirname(__file__), '..', 'frontend', '.env.local')

# Risolvi il percorso relativo in un percorso assoluto
dotenv_path = os.path.abspath(dotenv_path)

# Carica il file .env.local dal percorso assoluto
load_dotenv(dotenv_path)

# Recupera le variabili di ambiente
supabase_url = os.getenv("NEXT_PUBLIC_SUPABASE_URL")
supabase_key = os.getenv("NEXT_PUBLIC_SUPABASE_ANON_KEY")

# Configura Supabase - crea una connessione a Supabase
supabase: Client = create_client(supabase_url, supabase_key)

# Funzione per recuperare i dati storici delle stanze dal database
def recupero_dati():
    response = supabase.table("Rooms").select("room_name, vertices, rilevazione").execute()
    return response.data

# Funzione per controllare se l'utente esiste nel database
def check_user_exists(user_id):
    query = supabase.table("Users").select("user_id").eq("user_id", user_id).execute()
    return query.data is not None and len(query.data) > 0

# Funzione per controllare se il dispositivo è ristretto
def contr_disp(room_name):
    query = supabase.table("Rooms").select("is_restricted, room_id").eq("room_name", room_name).execute()
    
    if query.data and len(query.data) > 0:
        is_restricted = query.data[0]["is_restricted"]
        room_id = query.data[0]["room_id"]
        return is_restricted, room_id

# Funzione per calcolare i pesi basati sulle misurazioni RSSI
def calculate_weights(user_rssi, dict_AP):
    weights = []
    for measurement in dict_AP:
        position, rssi_values = measurement
        weight = sum(abs(user - prev) for user, prev in zip(user_rssi, rssi_values))
        weights.append((position, weight))
    return weights

# Normalizza i pesi
def normalize_weights(weights):
    total_weight = sum(w for _, w in weights)
    return [(pos, w / total_weight) for pos, w in weights]

# Stima la posizione dell'utente utilizzando i pesi normalizzati
def estimate_position(normalized_weights):
    x_estimated = sum(pos['coordinates'][0][0][0] * weight for pos, weight in normalized_weights)
    y_estimated = sum(pos['coordinates'][0][0][1] * weight for pos, weight in normalized_weights)
    
    return (x_estimated, y_estimated)

# Calcola la posizione stimata dell'utente
def calcola_posizione(current_scan, dati_db):
    dict_AP = []

    for room in dati_db:
        if room['rilevazione'] is None:
            continue
        
        for rilevazione_key, rilevazione_data in room['rilevazione'].items():
            if isinstance(rilevazione_data, dict):
                dizionario_AP = {
                    ap_value['MAC']: ap_value['RSSI'] for ap_value in rilevazione_data['wifi_data'].values()
                }
                dict_AP.append((room['vertices'], list(dizionario_AP.values())))

    user_rssi = [ap['RSSI'] for ap in current_scan['wifi_data'].values()]
    
    weights = calculate_weights(user_rssi, dict_AP)
    normalized_weights = normalize_weights(weights)

    if normalized_weights:
        estimated_position = estimate_position(normalized_weights)
        return estimated_position
    else:
        print("Non ci sono misurazioni sufficienti per stimare la posizione.")
        return None

def insert_funz(user_id, room_id):
    insert = supabase.table("Access_Logs").insert({
        "user_id": user_id,
        "room_id": room_id,
        "timestamp": current_time.strftime("%H:%M:%S"),
        "returned_time": None
        }).execute()

    print("insert data: ", insert.data)

    if insert.data is not None:
        msg = "Record inserito con successo"
    else:
        msg = "Errore nell'inserimento del record"

    return msg 

# Registra l'accesso alle stanze
def access_log(user_id, room_id):
    msg = None
    presenza = supabase.table("Access_Logs")\
        .select("*")\
        .eq("user_id", user_id)\
        .order("log_id", desc=True)\
        .limit(1)\
        .execute()

    if not presenza.data: 
        msg= insert_funz(user_id, room_id)

    elif presenza.data and presenza.data[0].get("returned_time") is not None:
        msg  = insert_funz(user_id, room_id)

    elif presenza.data and (presenza.data[0].get("timestamp") is not None and presenza.data[0].get("returned_time") is None) and (presenza.data[0].get("room_id") != room_id):
        aggiorna = supabase.table("Access_Logs").update({
            "returned_time": current_time.strftime("%H:%M:%S")
        }).eq("log_id", presenza.data[0].get("log_id")).execute()
        
        print("agg data:", aggiorna.data)
        
        if aggiorna.data is not None:
            msg = "Record aggiornato con successo"
        else:
            msg = "Errore nell'aggiornamento del record: " 

    return msg

# Carica i dati per la previsione della stanza
def load_data():
    df = pd.read_csv('/backend/wifi_data_with_rooms.csv')
    return df

# Prepara i dati per l'addestramento
def prepare_data(df):
    df.fillna(0, inplace=True)
    le = LabelEncoder()
    df['room'] = le.fit_transform(df['room'])
    X = df.drop(columns=['room'])  
    y = df['room']
    return X, y, le

# Addestra il modello Random Forest
def train_model(X, y):
    X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=42)
    model = RandomForestClassifier(n_estimators=100, random_state=42)
    model.fit(X_train, y_train)

    print(f"Accuracy: {model.score(X_test, y_test):.2f}")
    return model

# Predici la stanza basata sulla scansione attuale
def predict_room(model, le, json_data):
    wifi_data = json_data['wifi_data']
    input_data = {info['MAC']: info['RSSI'] for info in wifi_data.values()}

    input_df = pd.DataFrame(columns=model.feature_names_in_)
    for col in model.feature_names_in_:
        input_df[col] = [input_data.get(col, 0)]

    prediction = model.predict(input_df)
    room_prediction = le.inverse_transform(prediction)[0]
    return room_prediction

# Funzione principale
def fingerprinting(json_data,user_id):
    # Assumi che `json_data` contenga i dati JSON da elaborare.
    try:
        current_scan = json_data  # Non leggiamo più da un file, ma prendiamo i dati direttamente
    except Exception as e:
        print(f"Errore nel caricamento dei dati: {e}")
        return None, None

    dati_db = recupero_dati()
    dati_db = [dato for dato in dati_db if dato.get('vertices') is not None]

    estimated_position = calcola_posizione(current_scan, dati_db)
    if estimated_position:
        print(f"Posizione stimata dell'utente: {estimated_position}")
    else:
        print("Non ci sono misurazioni sufficienti per stimare la posizione.")

    df = load_data()
    X, y, le = prepare_data(df)
    model = train_model(X, y)

    predicted_room = predict_room(model, le, current_scan)
    print(f"La stanza prevista è: {predicted_room}")

    contr_ble, room_id = contr_disp(predicted_room)
    print("Devo controllare i BLE?:", contr_ble)

    #msg = access_log(user_id, room_id)
    #print(msg)

    return predicted_room, contr_ble