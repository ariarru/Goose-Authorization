import os
from dotenv import load_dotenv
from supabase import create_client, Client
import numpy as np

# Carica le variabili dal file .env
load_dotenv(dotenv_path='.env.local')

# Recupera le variabili
supabase_url = os.getenv("NEXT_PUBLIC_SUPABASE_URL")
supabase_key = os.getenv("NEXT_PUBLIC_SUPABASE_ANON_KEY")
"""print(f"URL di Supabase: {supabase_url}")
print(f"Chiave di Supabase: {supabase_key}")"""

# Configura Supabase - crea una connessione a Supabase
supabase: Client = create_client(supabase_url, supabase_key)

def recupero_dati():
    # Recupera i dati storici delle stanze dal database
    response = supabase.table("Rooms").select("room_name, rilevazione").execute()
    return response.data

# Rimuove eventuali caratteri finali dai MAC address
def puliz_mac(mac):
    return mac.strip(':')

def calc_similarity(current_scan, dati_db):
    best_match = None
    best_similarity_score = float('-inf')
    
    for room in dati_db:
        # Verifica se la chiave 'rilevazione' è presente e non è None
        if room['rilevazione'] is None:
            #print("Rilevazione mancante per la stanza:", room['room_name'])
            continue  # Salta questa stanza se non c'è rilevazione

        room_similarity_score = 0
        access_point_count = 0  # Inizializza il conteggio degli access point

        for rilevazione_key, rilevazione_data in room['rilevazione'].items():
            if isinstance(rilevazione_data, dict):
                dizionario_AP = {
                    puliz_mac(ap_value['MAC']): ap_value['RSSI'] for ap_value in rilevazione_data['wifi_data'].values()
                }

                for ap in current_scan['wifi_data'].values():
                    mac = puliz_mac(ap['MAC'])
                    rssi = ap['RSSI']
                    if mac in dizionario_AP:
                        access_point_count += 1  # Incrementa il conteggio per ogni MAC trovato
                        historical_rssi = dizionario_AP[mac]
                        difference = abs(historical_rssi - rssi)
                        room_similarity_score += (100 - difference)

        if access_point_count > 0:
            room_similarity_score /= access_point_count  # Calcola la media delle similarità
        else:
            room_similarity_score = 0  # Imposta il punteggio a 0 se non ci sono AP

        if room_similarity_score > best_similarity_score:
            best_similarity_score = room_similarity_score
    
    return best_similarity_score

def calc_soglia():
    dati_db = recupero_dati()
    lista_best_similarity_score = []

    for room in dati_db:
        if room['rilevazione'] is None:  
            #print("Rilevazione mancante per la stanza:", room['room_name'])  
            continue 

        for rilevazione_key, rilevazione_data in room['rilevazione'].items():
            if isinstance(rilevazione_data, dict):
                current_scan = rilevazione_data

                best_similarity_score = calc_similarity(current_scan, dati_db)  
                room_name = room['room_name']  # Ottieni il nome della stanza corrente

                # Aggiungi il nome della stanza e il relativo punteggio di similarità migliore alla lista
                lista_best_similarity_score.append((room_name, best_similarity_score))


    # Calcola e stampa la media dei punteggi
    media = np.mean([score for _, score in lista_best_similarity_score])
    min_score = np.min([score for _, score in lista_best_similarity_score])
    max_score = np.max([score for _, score in lista_best_similarity_score])
    soglia = media - (max_score - min_score)

    print("Media del Punteggio di Similarità Migliore:", media)
    print("MIN del Punteggio di Similarità Migliore:", min_score)
    print("MAX del Punteggio di Similarità Migliore:", max_score)
    print("La soglia per il punteggio di Similarità Migliore che utilizzeremo nel codice di wifi fingerprinting è:", soglia)

# Avvia la funzione di calcolo della soglia
calc_soglia()
