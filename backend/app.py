import os
import sys
from enum import Enum
from flask_cors import CORS
from dotenv import load_dotenv
from flask import Flask, jsonify, request
from fingerprinting import fingerprinting  
from controllo_dispositivi import controllo_dispositivi


app = Flask(__name__)
CORS(app)  # Abilita CORS per tutte le rotte

'''FLASK_URL="http://localhost:5001"
FINGERPRINT_URL="http://localhost:5001/api/fingerprint"
CONTROLLORBLE_URL="http://localhost:5001/api/controlloBle"
'''

FLASK_URL = "http://backend-service:5001"
FINGERPRINT_URL = f"{FLASK_URL}/api/fingerprint"
CONTROLLORBLE_URL = f"{FLASK_URL}/api/controlloBle"


@app.route('/api/fingerprint', methods=['POST'])
def fingerprint():
    # Ottieni i dati dal corpo della richiesta JSON
    data = request.get_json()

    # Assicurati che i dati siano corretti
    if 'json_input' not in data or 'user_id' not in data:
        return jsonify({"error": "Input JSON e user_id richiesti"}), 400

    json_input = data['json_input']
    user_id = data['user_id']

    # Log dei dati ricevuti
    #print(f"Received json_input: {json_input}")
    #print(f"Received user_id: {user_id}")

    # Esegui il calcolo dei dati (o la predizione)
    room_id, contr_ble = fingerprinting(json_input, user_id)
    
    # Log del risultato
    print("predicted_room:", room_id, "contr_ble:",contr_ble)

    # Restituisci i dati come JSON
    return jsonify({"predicted_room": room_id, "contr_ble": contr_ble})












@app.route('/api/controlloBle', methods=['POST'])
def controlloBle():
###############################################################
    #TODO: gestire quando NON ci sono segnali bluetooth
    # serve anche id utente???
###############################################################

    # Ottieni i dati dal corpo della richiesta JSON
    data = request.get_json()
    
    # Log dei dati ricevuti per il debug
    print(f"Dati ricevuti: {data}")

    # Assicurati che i dati siano corretti
    if 'room_id' not in data or 'lista_disp' not in data or 'out' not in data:
        return jsonify({"error": "Input room_id e lista_disp richiesti"}), 400

    room_id = data['room_id']
    lista_disp = data['lista_disp']  # Modificato per usare la chiave corretta
    out = data['out']

    # Converti room_id in intero
    try:
        room_id = int(room_id)  # Conversione di room_id in intero
    except ValueError:
        return jsonify({"error": "room_id deve essere un intero valido"}), 400

    # Converti out in intero
    try:
        out = int(out)  # Conversione di room_id in intero
    except ValueError:
        return jsonify({"error": "out deve essere un intero valido"}), 400
    
    # Esegui il calcolo dei dati
    risposta = controllo_dispositivi(room_id, lista_disp, out)
    
    return str(risposta.value),200

# Rotta per restituire gli URL completi
@app.route('/api/urls', methods=['GET'])
def get_urls():
    return jsonify({
        "flask_url": FLASK_URL,
        "fingerprint_url": FINGERPRINT_URL,
        "controllo_ble_url": CONTROLLORBLE_URL
    })

# Rotta di base
@app.route('/')
def home():
    return "Backend is working!"

if __name__ == '__main__':
    # Configura il server Flask per ascoltare su tutte le interfacce e la porta 5001
    app.run(debug=True, host="0.0.0.0", port=5001)  