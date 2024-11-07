from flask import Flask, jsonify, request
from flask_cors import CORS
import os
import sys
from dotenv import load_dotenv
from fingerprinting import fingerprinting  
from controllo_dispositivi import controllo_dispositivi

app = Flask(__name__)
CORS(app)  # Abilita CORS per tutte le rotte

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
    predicted_room, contr_ble = fingerprinting(json_input, user_id)
    
    # Log del risultato
    print("predicted_room:", predicted_room, "contr_ble:",contr_ble)

    # Restituisci i dati come JSON
    return jsonify({"predicted_room": predicted_room, "contr_ble": contr_ble})


@app.route('/api/controlloBle', methods=['POST'])
def controlloBle():
    # Ottieni i dati dal corpo della richiesta JSON
    data = request.get_json()

    # Assicurati che i dati siano corretti
    if 'room_id' not in data or 'lista-disp' not in data:
        return jsonify({"error": "Input room_id e lista_disp richiesti"}), 400

    room_id = data['room_id']
    lista_disp = data['lista_disp']
    
    # Converti room_id in intero
    try:
        room_id = int(room_id)  # Conversione di room_id in intero
    except ValueError:
        return jsonify({"error": "room_id deve essere un intero valido"}), 400

    # Log dei dati ricevuti
    #print(f"Received room_id: {room_id}")
    #print(f"Received lista_disp: {lista_disp}")

    # Esegui il calcolo dei dati
    risposta = controllo_dispositivi(room_id, lista_disp)
    
    # Log del risultato
    if isinstance(risposta, bool):
        print("L'utente ha tutti i dispositivi")
        response = {"return": True}
        return jsonify(response)
    
    else:
        print("Lista dispositivi mancanti:", risposta)
        response = {"return": risposta}
        return jsonify(response)


if __name__ == '__main__':
    # Configura il server Flask per ascoltare su tutte le interfacce e la porta 5001
    app.run(debug=True, host="0.0.0.0", port=5001)  