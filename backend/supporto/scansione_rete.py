import time
import json
import os
from pywifi import PyWiFi, const, Profile

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

        #print("Dati Wi-Fi trovati:", wifi_data)  # Messaggio di debug
        return wifi_data

    except Exception as e:
        print("Errore durante la scansione Wi-Fi:", e)
        return []  # Ritorna una lista vuota se si verifica un errore


def save_data_to_file(wifi_data, filename):
    try:
        # Crea la directory se non esiste
        os.makedirs(os.path.dirname(filename), exist_ok=True)
        print(f"Directory {os.path.dirname(filename)} creata/verificata.")  # Messaggio di debug

        # Controlla se il file esiste già
        if os.path.exists(filename):
            # Se esiste, lo apre e continua a scriverci
            with open(filename, 'r') as f:
                try:
                    json_data = json.load(f)
                except json.JSONDecodeError:
                    json_data = {}
        else:
            # Se non esiste, inizializza la struttura del file JSON
            json_data = {}

        # Determina il prossimo numero per la nuova rilevazione
        last_key = max([int(key.replace("rilevazione", "")) for key in json_data.keys()] + [0])
        new_key = f"rilevazione{last_key + 1}"

        # Aggiunge i dati della nuova scansione
        json_data[new_key] = {
            "wifi_data": {f"AP{i+1}": {
                "SSID": data["SSID"],
                "MAC": data["MAC"],
                "RSSI": data["RSSI"]
            } for i, data in enumerate(wifi_data)}
        }

        # Salva i dati nel file JSON
        with open(filename, 'w') as f:
            json.dump(json_data, f, indent=4)
        print(f"Dati salvati nel file: {filename}")  # Messaggio di debug

    except Exception as e:
        print("Errore durante il salvataggio dei dati nel file:", e)


if __name__ == "__main__":
    wifi_data = scan_wifi()  
    if wifi_data:
        save_data_to_file(wifi_data, filename='backend/rilevazioni/cucinaCla.json') #rilevazione
    else:
        print("Nessun dato Wi-Fi trovato. Il file non è stato creato.")


