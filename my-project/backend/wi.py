import os
from dotenv import load_dotenv
from supabase import create_client, Client
import json

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

# Rimuove eventuali caratteri finali dai MAC address
def puliz_mac(mac):
    return mac.strip(':')

def calc_similarity(current_scan, dati_db):
    best_match = None
    best_similarity_score = float('-inf')
    
    for room in dati_db:
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

#Calcola la posizione stimata dell'utente usando i pesi normalizzati. 
# Utilizza i valori di coordinate delle posizioni pesate dai rispettivi pesi.
def estimate_position(normalized_weights):
    x_estimated = sum(pos['coordinates'][0][0][0] * weight for pos, weight in normalized_weights)
    y_estimated = sum(pos['coordinates'][0][0][1] * weight for pos, weight in normalized_weights)
    
    return (x_estimated, y_estimated)


def fingerprintig(file_name):
    # Carica i dati JSON per la scansione attuale
    with open(file_name, "r") as file:
        current_scan = json.load(file)

    # Pulizia dei MAC address nel current_scan
    for ap_data in current_scan['wifi_data'].values():
        ap_data['MAC'] = puliz_mac(ap_data['MAC'])

    # Funzione principale per eseguire il Wi-Fi fingerprinting
    dati_db = recupero_dati()
    
    # Filtra i dati per escludere quelli con 'vertices' a None
    dati_db = [dato for dato in dati_db if dato.get('vertices') is not None]

    best_match, best_similarity_score = calc_similarity(current_scan, dati_db)

    SIMILARITY_THRESHOLD = 91.61454450843858  # soglia di similarità calcolata a priori

    if best_match is not None and best_similarity_score > SIMILARITY_THRESHOLD:
        print(f"La stanza più probabile è: {best_match['room_name']} con un punteggio di similarità di {best_similarity_score:.2f}.")
    else:
        print("L'utente è fuori dalla stanza.")

    # Estrai i dati di RSSI per le rilevazioni precedenti
    dict_AP = []
    for room in dati_db:
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

    if normalized_weights:  # Assicurati che ci siano pesi normalizzati
        estimated_position = estimate_position(normalized_weights)
        # Mostra la posizione stimata
        print(f"Posizione stimata dell'utente: {estimated_position}")
    else:
        print("Non ci sono misurazioni sufficienti per stimare la posizione.")


if __name__ == "__main__":
    import sys
    
    # Verifica che sia stato fornito un argomento
    if len(sys.argv) < 2:
        print("Utilizzo: python wi.py <nome_file_json>")
        sys.exit(1)
    
    # Prendi il nome del file JSON dal primo argomento
    file_name = sys.argv[1]
    
    # Chiama la funzione fingerprinting
    fingerprintig(file_name) 