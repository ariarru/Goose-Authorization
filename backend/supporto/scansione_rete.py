from hmac import new
import time
import json
import os
from pywifi import PyWiFi, const, Profile

# Funzione per la scansione Wi-Fi
def scan_wifi():
    wifi = PyWiFi()
    iface = wifi.interfaces()[0]  # Seleziona la prima interfaccia Wi-Fi

    try:
        iface.scan()  # Inizia la scansione
        time.sleep(2)  # Attende 2 secondi per permettere la conclusione della scansione
        scan_results = iface.scan_results()  # Ottiene i risultati della scansione
        
        wifi_data = []

        for network in scan_results:
            # Definisce il dizionario wifi_data con SSID, indirizzo MAC e RSSI per ogni AP rilevato
            wifi_data.append({
                "SSID": network.ssid,
                "MAC": network.bssid,
                "RSSI": network.signal
            })

        return wifi_data

    except Exception as e:
        print("Errore durante la scansione Wi-Fi:", e)
        return []  # Ritorna una lista vuota se si verifica un errore


# Funzione per salvare i dati in un file JSON
def save_data_to_file(wifi_data, filename):
    try:
        # Crea la directory se non esiste
        os.makedirs(os.path.dirname(filename), exist_ok=True)

        # Controlla se il file esiste già
        if os.path.exists(filename):
            with open(filename, 'r') as f:
                try:
                    json_data = json.load(f)
                except json.JSONDecodeError:
                    json_data = {}
        else:
            # Se non esiste, inizializza la struttura del file JSON
            json_data = {}
            print("File non esistente, inizializzazione del file JSON...")

        # Determina il prossimo numero per la nuova rilevazione
        last_key = 0
        if json_data:
            last_key = max([int(key.replace("rilevazione", "")) for key in json_data.keys() if key.startswith("rilevazione")] + [0])
        
        new_key = f"rilevazione{last_key + 1}"
        print("new_key", new_key)
        
        # Aggiunge i dati della nuova scansione
        json_data[new_key] = {
            "wifi_data": wifi_data
        }

        # Salva i dati nel file JSON
        with open(filename, 'w') as f:
            json.dump(json_data, f, indent=4)
        print(f"Dati salvati correttamente in {filename}")
    
    except Exception as e:
        print("Errore durante il salvataggio dei dati nel file:", e)