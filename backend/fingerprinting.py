import os
import sys
import json
import enum
import datetime
import pandas as pd
from anyio import current_time
from dotenv import load_dotenv
from supabase import create_client, Client
from sklearn.preprocessing import LabelEncoder
from sklearn.ensemble import RandomForestClassifier
from sklearn.model_selection import train_test_split

# Codice da ritornare all'app
class Codes(enum.Enum):
    ROOM_NOT_FOUND = 40
    UNAUTHORIZED_USER = 41

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



# Funzione per controllare se la stanza ha delle restrizioni
def contr_disp(room_id):
    query = supabase.table("Rooms").select("is_restricted").eq("room_id", room_id).execute()
    
    if query.data and len(query.data) > 0:
        is_restricted = query.data[0]["is_restricted"]

        return is_restricted


def insert_funz(user_id, room_id):
    insert = supabase.table("Access_Logs").insert({
        "user_id": user_id,
        "room_id": room_id,
        "timestamp": current_time.strftime("%Y-%m-%d %H:%M:%S"),
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
    new_room_id = room_id
    msg = None
    presenza = supabase.table("Access_Logs")\
        .select("*")\
        .eq("user_id", user_id)\
        .order("log_id", desc=True)\
        .limit(1)\
        .execute()
    
       
    # Caso1: l'utente non ha un registro attivo
    if not presenza.data: 
        # Inserisci un nuovo record per la stanza in entrata
        msg= insert_funz(user_id, room_id)
        
    #Caso2: l'ultimo access log dell'utente è completo
    elif presenza.data and presenza.data[0].get("returned_time") is not None:
        # Inserisci un nuovo record per la stanza in entrata
        msg  = insert_funz(user_id, room_id)

    
    # Caso3: L'utente è attualmente in una stanza diversa
    elif presenza.data and \
        (presenza.data[0].get("timestamp") is not None and presenza.data[0].get("returned_time") is None) and \
        (presenza.data[0].get("room_id") != room_id):
      
        new_room_id = presenza.data[0].get("room_id")

        aggiorna = supabase.table("Access_Logs").update({
            "returned_time": current_time.strftime("%Y-%m-%d %H:%M:%S")
        }).eq("log_id", presenza.data[0].get("log_id")).execute()
        
        print("agg data:", aggiorna.data)
        
        if aggiorna.data is not None:
            msg = "Record aggiornato con successo"
        else:
            msg = "Errore nell'aggiornamento del record: " 
   
    #Caso4: L'utente è nella stessa stanza
    elif presenza.data and \
         (presenza.data[0].get("timestamp") is not None and presenza.data[0].get("returned_time") is None) and \
         (presenza.data[0].get("room_id") == room_id):

        msg = "L'utente è già nella stanza specificata."

    return msg, new_room_id


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

    try:
        prediction = model.predict(input_df)
        room_prediction = le.inverse_transform(prediction)[0]
        return room_prediction
    except Exception as e:
        print(f"Errore nella predizione della stanza: {e}")
        return Codes.ROOM_NOT_FOUND.value


# Funzione principale
def fingerprinting(json_data, user_id):
    # Assumi che `json_data` contenga i dati JSON da elaborare.
    try:
        # Estrai i dati wifi dal json_input
        if isinstance(json_data, dict) and 'wifi_data' in json_data:
            current_scan = json_data['wifi_data']
        else:
            print("Formato dati non valido:", json_data)
            return None, None, None
    except Exception as e:
        print(f"Errore nel caricamento dei dati: {e}")
        return None, None, None

    dati_db = recupero_dati()
    dati_db = [dato for dato in dati_db if dato.get('vertices') is not None]


    df = load_data()
    X, y, le = prepare_data(df)
    model = train_model(X, y)

    predicted_room = predict_room(model, le, json_data)
    print(f"La stanza prevista è: {predicted_room}")
    
    # Query per ottenere il room_id
    query = supabase.table("Rooms").select("room_id").eq("room_name", predicted_room).execute()
    
    # Estrai il room_id dal risultato della query
    if query.data and len(query.data) > 0:
        room_id = query.data[0].get('room_id')
        print(f"Room ID trovato: {room_id}")
    else:
        print("Nessuna stanza trovata con questo nome")
        return None, None, None
    
    #se l'utente non può essere in quella stanza interrompi exec.
    print(user_id, room_id)
    queryResult = supabase.rpc("ut_autorizzato", {
        '_room_id': room_id,
        '_user_id': user_id
    }).execute()
    
    print("query:", queryResult)
    if queryResult.data == False:
        result, new_room_id = access_log(user_id, room_id)
        return Codes.UNAUTHORIZED_USER.value

    result, new_room_id = access_log(user_id, room_id)
    print(result)

    contr_ble = contr_disp(new_room_id)
    print("Devo controllare i BLE?:", contr_ble)  
    
    return new_room_id, predicted_room, contr_ble
