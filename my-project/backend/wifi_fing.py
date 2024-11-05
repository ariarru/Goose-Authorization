import os
from anyio import current_time
from dotenv import load_dotenv
from supabase import create_client, Client
import json
import sys
import datetime
current_time = datetime.datetime.now()

# Carica le variabili dal file .env
load_dotenv(dotenv_path='.env.local')

# Recupera le variabili
supabase_url = os.getenv("NEXT_PUBLIC_SUPABASE_URL")
supabase_key = os.getenv("NEXT_PUBLIC_SUPABASE_ANON_KEY")

# Configura Supabase - crea una connessione a Supabase
supabase: Client = create_client(supabase_url, supabase_key)

# Recupera i dati storici delle stanze dal database
def recupero_dati():
    response = supabase.table("Rooms").select("room_name, vertices, rilevazione").execute()
    return response.data

# Ritorna is_restricted se è True devi fare il controllo ble
def contr_disp(room_name):
    query = supabase.table("Rooms").select("is_restricted, room_id").eq("room_name", room_name).execute()
    
    if query.data and len(query.data) > 0:
        is_restricted = query.data[0]["is_restricted"]
        room_id = query.data[0]["room_id"]
        return is_restricted, room_id


# Rimuove eventuali caratteri finali dai MAC address
def puliz_mac(mac):
    return mac.strip(':')

def calc_similarity(current_scan, dati_db):
    best_match = None
    best_similarity_score = float('-inf')
    
    for room in dati_db:
        # Verifica se la chiave 'rilevazione' è presente e non è None
        if room['rilevazione'] is None:
            continue  # Salta questa stanza se non c'è rilevazione
       
        room_similarity_score = 0
        access_point_count = 0

        # Loop dinamico su tutte le rilevazioni disponibili nella stanza
        for rilevazione_key, rilevazione_data in room['rilevazione'].items():
            if not isinstance(rilevazione_data, dict):
                continue
                            
            dizionario_AP = {
                puliz_mac(ap_value['MAC']): ap_value['RSSI'] for ap_value in rilevazione_data['wifi_data'].values()
            }
            
            for ap in current_scan['wifi_data'].values():
                mac = puliz_mac(ap['MAC'])
                rssi = ap['RSSI']
                if mac in dizionario_AP:
                    historical_rssi = dizionario_AP[mac]
                    difference = abs(historical_rssi - rssi)
                    room_similarity_score += (100 - difference)
                    access_point_count += 1
                    
        # Calcola il punteggio medio di similarità
        if access_point_count > 0:
            room_similarity_score /= access_point_count

        if room_similarity_score > best_similarity_score:
            best_similarity_score = room_similarity_score
            best_match = room
    
    return best_match, best_similarity_score

# Calcola i pesi per le posizioni in base alle misurazioni RSSI. 
# Somma le differenze assolute tra i RSSI dell'utente e quelli registrati 
# per ciascun AP e crea una lista di tuple (posizione, peso).
def calculate_weights(user_rssi,dict_AP):
    weights = []
    for measurement in dict_AP:
        position, rssi_values = measurement
        # Calcola la somma delle differenze assolute tra RSSI
        weight = sum(abs(user - prev) for user, prev in zip(user_rssi, rssi_values))
        weights.append((position, weight))
    return weights

# Normalizza i pesi, calcolando la frazione di ciascun peso rispetto al 
# totale.
def normalize_weights(weights):
    total_weight = sum(w for _, w in weights)
    return [(pos, w / total_weight) for pos, w in weights]

# Calcola la posizione stimata dell'utente usando i pesi normalizzati. 
# Utilizza i valori di coordinate delle posizioni pesate dai rispettivi pesi.
def estimate_position(normalized_weights):
    x_estimated = sum(pos['coordinates'][0][0][0] * weight for pos, weight in normalized_weights)
    y_estimated = sum(pos['coordinates'][0][0][1] * weight for pos, weight in normalized_weights)
    
    return (x_estimated, y_estimated)

# Pulizia dei MAC address nel current_scan
def pulizia_current_scan(current_scan):
    for ap_data in current_scan['wifi_data'].values():
        ap_data['MAC'] = puliz_mac(ap_data['MAC'])

def calcola_posizione(current_scan, dati_db):
    # Estrai i dati di RSSI per le rilevazioni precedenti
    dict_AP = []

    for room in dati_db:
        # Verifica se la chiave 'rilevazione' è presente e non è None
        if room['rilevazione'] is None:
            continue  # Salta questa stanza se non c'è rilevazione
        
        for rilevazione_key, rilevazione_data in room['rilevazione'].items():
            if isinstance(rilevazione_data, dict):
                # Crea un dizionario AP che associa i MAC address ai loro RSSI
                dizionario_AP = {
                    puliz_mac(ap_value['MAC']): ap_value['RSSI'] for ap_value in rilevazione_data['wifi_data'].values()
                }
                dict_AP.append((room['vertices'], list(dizionario_AP.values())))

    # Calcola la posizione stimata usando i segnali RSSI
    user_rssi = [ap['RSSI'] for ap in current_scan['wifi_data'].values()]
    
    weights = calculate_weights(user_rssi, dict_AP)
    normalized_weights = normalize_weights(weights)

    if normalized_weights:
        estimated_position = estimate_position(normalized_weights)
        return estimated_position
    else:
        print("Non ci sono misurazioni sufficienti per stimare la posizione.")
        return None

def access_log(user_id, room_id):
    msg = None
    
    # Controlla se l'utente è presente, prendendo in caso il suo ultimo accesso
    presenza = supabase.table("Access_Logs")\
        .select("*")\
        .eq("user_id", user_id)\
        .order("log_id", desc=True)\
        .limit(1)\
        .execute()

    # Se l'utente non è in Access_logs oppure il suo ultimo log ha segnati sia timestamp che returned_time allora faccio un nuovo insert
    if not presenza.data:  # Se non ci sono log per questo utente
        if room_id == -1:
            msg = "Nessuna azione necessaria"
            return msg

        insert = supabase.table("Access_Logs").insert({
            "user_id": user_id,
            "room_id": room_id,
            "timestamp": current_time.strftime("%H:%M:%S"),
            "returned_time": None  # Imposta il returned_time come None per un nuovo accesso
        }).execute()

        print("insert data: ", insert.data)

        # Controlla se l'operazione è andata a buon fine
        if insert.data is not None:
            msg = "Record inserito con successo"
        else:
            msg = f"Errore nell'inserimento del record"

    elif presenza.data and presenza.data[0].get("returned_time") is not None:
        # L'utente esiste e l'ultimo accesso ha timestamp e returned_time
        if room_id == -1:
            msg = "Nessuna azione necessaria"
            return msg

        # Inserisci un nuovo log
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
            msg = f"Errore nell'inserimento del record"

    elif presenza.data and (presenza.data[0].get("timestamp") is not None and presenza.data[0].get("returned_time") is None) and (presenza.data[0].get("room_id") != room_id):
        # Aggiorna il returned_time per la stanza precedente
        aggiorna = supabase.table("Access_Logs").update({
            "returned_time": current_time.strftime("%H:%M:%S")
        }).eq("log_id", presenza.data[0].get("log_id")).execute()
        
        print("agg data:", aggiorna.data)
        
        if aggiorna.data is not None:
            msg = "Record aggiornato con successo"
        else:
            msg = "Errore nell'aggiornamento del record: " 

              
    return msg



def fingerprinting(file_name,user_id):
    # Carica i dati JSON per la scansione attuale
    with open(file_name, "r") as file:
        current_scan = json.load(file)

    pulizia_current_scan(current_scan)

    # Funzione principale per eseguire il Wi-Fi fingerprinting
    dati_db = recupero_dati()
    
    # Filtra i dati per escludere quelli con 'vertices' a None
    dati_db = [dato for dato in dati_db if dato.get('vertices') is not None]

    best_match, best_similarity_score = calc_similarity(current_scan, dati_db)

    SIMILARITY_THRESHOLD = 91.61454450843858  # soglia di similarità calcolata a priori

    if best_match is not None and best_similarity_score > SIMILARITY_THRESHOLD:
        print(f"La stanza più probabile è: {best_match['room_name']} con un punteggio di similarità di {best_similarity_score:.2f}.")
    else:
        best_match['room_name'] =None
        room_id =-1
        print("L'utente è fuori dalla stanza.")

    estimated_position = calcola_posizione(current_scan, dati_db)
    if estimated_position:
        # Mostra la posizione stimata
        print(f"Posizione stimata dell'utente: {estimated_position}")
    else:
        print("Non ci sono misurazioni sufficienti per stimare la posizione.")

    if best_match.get('room_name') is not None:
        contr_ble, room_id = contr_disp(best_match['room_name'])         
        print("Devo controllare i BLE?:", contr_ble)
        #print("room_id=", room_id)
        #print("user_id=",user_id)
        
    #controlla e aggiorna Access log
    msg = access_log(user_id, room_id)
    print(msg)


    

if __name__ == "__main__":    
    # Verifica che sia stato fornito un argomento
    if len(sys.argv) < 3:
        print("Utilizzo: python wi.py <nome_file_json>")
        sys.exit(1)
    
    # Prendi il nome del file JSON dal primo argomento
    file_name = sys.argv[1]
    user_id = sys.argv[2]
    
    # Chiama la funzione fingerprinting
    fingerprinting(file_name, user_id)
