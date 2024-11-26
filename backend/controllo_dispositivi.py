import os
import sys  
import enum
from dotenv import load_dotenv
from supabase import create_client, Client

# Codice da ritornare all'app
class Codes(enum.Enum):
    AREA_NOT_RESTRICTED = 21
    MISSING_DEVICE = 30
    EXIT_MISSING_DEVICE = 31
    WRONG_DEVICE = 32
    HAS_RIGHT_DEVICES = 33
    

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
def controllo_dispositivi(_room_id, lista_disp, out):
    disp_manc = []
    devices_s = recupera_disp(_room_id)
    
    if devices_s is None: 
        print("Nessun dispositivo necessario")
        return Codes.AREA_NOT_RESTRICTED
    else:
        for disp_necessari in devices_s:
            if disp_necessari not in lista_disp:
                disp_manc.append(disp_necessari)
    
    if len(disp_manc) == 0:
        print("L'utente ha tutti i dispositivi")
        return Codes.HAS_RIGHT_DEVICES
    elif out==0:
        print("A l'utente mancano i seguenti:", disp_manc)
        #return disp_manc
        return Codes.MISSING_DEVICE
    else:
        print("A l'utente (in uscita) mancano i seguenti:", disp_manc)
        #return disp_manc
        return Codes.EXIT_MISSING_DEVICE
    


    #Se cambia stanza gli devo dire di chiamare due volte il seguente codice ma 
    # out = 1 --> è uscito dalla stanza e sto controllando se ha tutti i dispositivi
    # out = 0 --> è entrato dalla stanza 