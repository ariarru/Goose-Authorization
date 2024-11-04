import numpy as np
import os
from dotenv import load_dotenv
from supabase import create_client, Client


# Carica le variabili dal file .env
load_dotenv(dotenv_path='my-project/.env.local')

# Recupera le variabili
supabase_url = os.getenv("NEXT_PUBLIC_SUPABASE_URL")
supabase_key = os.getenv("NEXT_PUBLIC_SUPABASE_ANON_KEY")

# Configura Supabase - crea una connessione a Supabase
supabase: Client = create_client(supabase_url, supabase_key)

def recupero_dati():
    #Recupera i dati storici delle stanze dal database
    response = supabase.table("Rooms").select("room_name, rilevazione").execute()
    return response.data

#Rimuove eventuali caratteri finali dai MAC address
def puliz_mac(mac):
    return mac.strip(':')


def calc_soglia_percentile(dati_db):
    all_rssi_values = []

    for room in dati_db:
        for rilevazione_key, rilevazione_data in room['rilevazione'].items():
            if isinstance(rilevazione_data, dict): #Controlla se i dati sono un dizionario
                dizionario_AP = { #Crea un dizionario con i MAC e i loro valori RSSI
                    puliz_mac(ap_value['MAC']): ap_value['RSSI'] for ap_value in rilevazione_data['wifi_data'].values()
                }
                all_rssi_values.extend(dizionario_AP.values()) # Aggiungi i valori RSSI alla lista

    # Calcola il 25° percentile come soglia
    # il che significa che il 75% dei valori sono superiori a questo
    soglia = np.percentile(all_rssi_values, 25) if all_rssi_values else -100  # Un valore di default se non ci sono dati
    soglia_inferiore = np.percentile(all_rssi_values, 25) if all_rssi_values else -100
    soglia_superiore = np.percentile(all_rssi_values, 75) if all_rssi_values else -100

    return soglia_inferiore, soglia_superiore

def main():
    dati_db = recupero_dati()
    soglia_inferiore, soglia_superiore = calc_soglia_percentile(dati_db)
    print("soglia_i:", soglia_inferiore, "soglia_s:", soglia_superiore)

main()