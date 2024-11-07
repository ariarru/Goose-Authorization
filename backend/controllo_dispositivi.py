import os
import sys  
from dotenv import load_dotenv
from supabase import create_client, Client

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

# Funzione per recuperare i dispositivi necessari dal database per una stanza specifica
def recupera_disp(_room_id):
    try:
        # Query alla tabella "Room_S_Device" per il dato room_id
        response = supabase.table("Room_S_Device").select("device_s_id").eq("room_id", _room_id).execute()
        
        if response.data:
            # Estrai gli ID dei dispositivi trovati
            device_s_id = [row['device_s_id'] for row in response.data]
            
            # Se sono stati trovati ID dei dispositivi, esegui una query alla tabella "Safety_Devices" per i loro nomi
            if device_s_id:
                # Usa 'in_' per ottenere i nomi dei dispositivi che corrispondono agli ID dei dispositivi
                devices_response = supabase.table("Safety_Devices").select("device_s_name").in_("device_s_id", device_s_id).execute()
                
                if devices_response.data:
                    # Restituisci i nomi dei dispositivi
                    return [row['device_s_name'] for row in devices_response.data]
                else:
                    return None
            else:
                return None
        else:
            return None
    except Exception as e:
        print(f"Errore nel recupero dei dispositivi: {e}")
        return None

# Funzione per controllare se ci sono dispositivi necessari per una stanza data
def controllo_dispositivi(_room_id, lista_disp):
    disp_manc = []
    devices_s = recupera_disp(_room_id)
    
    if devices_s is None: 
        print("Nessun dispositivo necessario")
        return True, "L'utente ha tutti i dispositivi"
    else:
        for disp_necessari in devices_s:
            if disp_necessari not in lista_disp:
                disp_manc.append(disp_necessari)
    
    if len(disp_manc) == 0:
        print("L'utente ha tutti i dispositivi")
        return True
    else:
        print("A l'utente mancano i seguenti:", disp_manc)
        return disp_manc