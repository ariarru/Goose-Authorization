import os
import sys  
from dotenv import load_dotenv
from supabase import create_client, Client

# Carica le variabili d'ambiente dal file .env
load_dotenv(dotenv_path='.env.local')

# Recupera le variabili d'ambiente
supabase_url = os.getenv("NEXT_PUBLIC_SUPABASE_URL")
supabase_key = os.getenv("NEXT_PUBLIC_SUPABASE_ANON_KEY")

# Configura il client Supabase
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
    disp_manc=[]
    devices_s = recupera_disp(_room_id)
    
    if devices_s is None: 
        print("Nessun dispositivo necessario")
        return True
    else:
        for disp_necessari in devices_s:
            if disp_necessari not in lista_disp:
               disp_manc.append(disp_necessari)
    
    if len(disp_manc)==0:
        print("L'utente ha tutti i dispositivi")
        return True
    else:
        print ("A l'utente mancano i seguenti:", disp_manc) 
        return disp_manc


if __name__ == "__main__":    
    # Verifica che sia stato fornito un argomento
    if len(sys.argv) < 3:
        print("Utilizzo: python controllo_dispositivi.py <room_id> <device_list>")
        sys.exit(1)
    
    # Prendi l'ID della stanza dal primo argomento e converti in intero
    try:
        _room_id = int(sys.argv[1])
    except ValueError:
        print("L'ID della stanza deve essere un numero intero.")
        sys.exit(1)
    
    device_list = sys.argv[2].split(',')
    lista_disp = [device.strip() for device in device_list]  # Pulisci gli spazi

    """print("File nella directory:", os.listdir(os.getcwd()))
    print("Directory Corrente:", os.getcwd())
    print("ID della Stanza:", _room_id)
    print("Lista dei Dispositivi:", lista_disp)"""

    # Chiama la funzione controllo_dispositivi
    controllo_dispositivi(_room_id,lista_disp)  

