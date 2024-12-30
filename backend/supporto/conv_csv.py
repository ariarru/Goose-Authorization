import os
import pandas as pd
from dotenv import load_dotenv
from supabase import create_client, Client

# Calcola il percorso relativo
dotenv_path = os.path.join(os.path.dirname(__file__), '..','..', 'frontend', '.env.local')

# Risolvi il percorso relativo in un percorso assoluto
dotenv_path = os.path.abspath(dotenv_path)

# Carica il file .env.local dal percorso assoluto
load_dotenv(dotenv_path)

# Recupera le variabili di ambiente
supabase_url = os.getenv("NEXT_PUBLIC_SUPABASE_URL")
supabase_key = os.getenv("NEXT_PUBLIC_SUPABASE_ANON_KEY")

# Configura Supabase - crea una connessione a Supabase
supabase: Client = create_client(supabase_url, supabase_key)

# Recupera i dati delle stanze dal database
def recupero_dati_rooms():
    response = supabase.table("Rooms").select("room_name, rilevazione").execute()
    return response.data

# Recupera i dati delle stanze
rooms = recupero_dati_rooms()

# Lista per raccogliere i dati
data = []

# Set per memorizzare i MAC unici
mac_addresses = set()

# Estrai i MAC e gli RSSI dai dati delle rilevazioni
for room in rooms:
    # Verifica se la chiave 'rilevazione' è presente e non è None
    if room.get('rilevazione') is None:
        continue  # Salta questa stanza se non c'è rilevazione
        
    for rilevazione_key, rilevazione_data in room['rilevazione'].items():
        if isinstance(rilevazione_data, dict):
            # Assicurati che 'wifi_data' esista
            if 'wifi_data' in rilevazione_data:
                # Crea un dizionario temporaneo per la stanza
                temp_data = {'room': room["room_name"]}
                
                for ap_value in rilevazione_data['wifi_data'].values():
                    mac = ap_value['MAC']
                    rssi = ap_value['RSSI']
                    
                    # Aggiungi il MAC address al set
                    mac_addresses.add(mac)
                    
                    # Aggiungi il valore RSSI per il MAC address
                    temp_data[mac] = rssi
                
                # Aggiungi i dati della stanza alla lista
                data.append(temp_data)

# Crea un DataFrame dai dati
df = pd.DataFrame(data)

# Riempi i NaN con 0
df.fillna(value=0, inplace=True)

# Ordina le colonne in modo che 'room' sia la prima
cols = ['room'] + sorted(df.columns.difference(['room']).tolist())
df = df[cols]

# Salva il DataFrame in un file CSV
csv_path = os.path.join(os.path.dirname(os.path.dirname(__file__)), 'wifi_data_with_rooms.csv')
df.to_csv(csv_path, index=False)

print("CSV generato con successo.")
