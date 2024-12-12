
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
    EXTRA_DEVICES= 35
    

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
                devices_response = supabase.table("Safety_Devices").select("MAC").in_("device_s_id", device_s_id).execute()
                
                if devices_response.data:
                    # Restituisci i nomi dei dispositivi
                    return [row['MAC'] for row in devices_response.data]
                else:
                    return None
            else:
                return None
        else:
            return None
    except Exception as e:
        print(f"Errore nel recupero dei dispositivi: {e}")
        return None
    


def recupera_Noti_preferenza(room_id):
        # Query alla tabella "Room_Authorizations" per recuperare il tipo di notifica per quella stanza 
        response = supabase.table("Notification").select("notification_preference").eq("room_id", room_id).execute()
        if len(response.data) ==0:
            return "popup"
        return response.data

# Funzione per calcolare la distanza e filtrare dispositivi posseduti
def tracking(lista_disp):
    A = -60  # Potenza a 1 metro (in dBm)
    n = 2.5  # Esponente di attenuazione
    disp_possed = []  # Lista dei dispositivi posseduti

    for disp_data in lista_disp.values():
        address = disp_data['ADDRESS']
        rssi = int(disp_data['RSSI'])  # Convertiamo l'RSSI in un intero
        distanza = 10 ** ((A - rssi) / (10 * n))  # Formula per stimare la distanza
        print(f"Device: {address}, RSSI: {rssi}, Distanza stimata: {distanza:.2f} metri")

        # Criterio per considerare un dispositivo "posseduto" (esempio: entro 1 metro)
        if distanza <= 1:
            disp_possed.append(address)

    return disp_possed

# Funzione per controllare se ci sono dispositivi necessari per una stanza data
def controllo_dispositivi(_room_id, lista_disp, user_id):
    disp_manc = []
    devices_s = recupera_disp(_room_id)
    presenza = supabase.table("Access_Logs")\
        .select("*")\
        .eq("user_id", user_id)\
        .order("log_id", desc=True)\
        .limit(1)\
        .execute()
    
    notif_type = recupera_Noti_preferenza(_room_id)
    
    #dispositivi posseduti dall'utente 
    disp_possed = tracking(lista_disp)


    # Caso 1: La stanza non richiede dispositivi di sicurezza
    if devices_s is None:
        if disp_possed: 
            print("L'utente ha dispositivi, ma la stanza non li richiede")
            return Codes.EXTRA_DEVICES, notif_type
        else:
            print("La stanza non prevede dispositivi di sicurezza. OK tutto apposto.")
            return Codes.AREA_NOT_RESTRICTED , notif_type
    
    # Caso 2: La lista passata è vuota ma la stanza richiede dispositivi di sicurezza
    if disp_possed==101:  # Verifica se la lista è vuota
        print("La stanza prevede dispositivi di sicurezza, ma l'utente non ha alcun dispositivo di sicurezza.")
        return Codes.MISSING_DEVICE, notif_type

    # Controllo dei dispositivi mancanti
    for disp_necessari in devices_s:
        if disp_necessari not in disp_possed:
            disp_manc.append(disp_necessari)
    
    if len(disp_manc) == 0:
        # Caso 3: L'utente ha tutti i dispositivi necessari
        print("L'utente ha tutti i dispositivi")
        return Codes.HAS_RIGHT_DEVICES, notif_type
    elif presenza.data and presenza.data[0].get("returned_time") is not None:
        # Caso 4: L'utente è in uscita e mancano dispositivi
        print("A l'utente (in uscita) mancano i seguenti:", disp_manc)
        return Codes.EXIT_MISSING_DEVICE, notif_type
    else:
        # Caso 5: Mancano dispositivi mentre l'utente è ancora nella stanza
        print("A l'utente mancano i seguenti:", disp_manc)
        return Codes.MISSING_DEVICE, notif_type
        
