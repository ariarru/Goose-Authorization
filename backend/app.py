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

'''FLASK_URL="https://localhost:5001"
FINGERPRINT_URL="https://localhost:5001/api/fingerprint"
CONTROLLORBLE_URL="https://localhost:5001/api/controlloBle"
'''

FLASK_URL = "https://backend-service:5001"
FINGERPRINT_URL = f"{FLASK_URL}/api/fingerprint"
CONTROLLORBLE_URL = f"{FLASK_URL}/api/controlloBle"


@app.route('/api/fingerprint', methods=['POST'])
def fingerprint():
    print("=== Received fingerprint request ===")
    try:
        # Ottieni i dati dal corpo della richiesta JSON
        data = request.get_json()
        print("Raw request data:", request.get_data())
        print("Parsed JSON data:", data)

        # Assicurati che i dati siano corretti
        if 'json_input' not in data or 'user_id' not in data:
            print("Missing required fields")
            return jsonify({"error": "Input JSON e user_id richiesti"}), 400
        
        # Verifica che la lista_disp non sia vuota
        if not data['json_input']:
            print("Empty json_input")
            return jsonify({"error": "json_input non può essere vuota"}), 400

        if not data['user_id']:
            print("Empty user_id")
            return jsonify({"error": "user_id non può essere vuota"}), 400
        
        json_input = data['json_input']
        user_id = data['user_id']

        # Log dei dati ricevuti
        print(f"Processing request with:")
        print(f"json_input: {json_input}")
        print(f"user_id: {user_id}")

        # Esegui il calcolo dei dati (o la predizione)
        result = fingerprinting(json_input, user_id)
        print(f"Fingerprinting result: {result}")
        
        # Distinguere tra ROOM_NOT_FOUND e il risultato normale
        if isinstance(result, int):  # ROOM_NOT_FOUND restituisce un intero
            return jsonify({
                "result": result,
            })
        elif isinstance(result, tuple):  # Caso normale con room_id, room_name e contr_ble
            room_id, room_name, contr_ble = result
            return jsonify({
                "predicted_room": room_id,
                "room_name": room_name,
                "contr_ble": contr_ble
            })
    except Exception as e:
        print(f"Error in fingerprint endpoint: {str(e)}")
        import traceback
        print("Traceback:", traceback.format_exc())
        return jsonify({"error": str(e)}), 500





@app.route('/api/controlloBle', methods=['POST'])
def controlloBle():
    # Ottieni i dati dal corpo della richiesta JSON
    data = request.get_json()
    
    # Log dei dati ricevuti per il debug
    print(f"Dati ricevuti: {data}")

    # Assicurati che i dati siano corretti
    if 'room_id' not in data or 'lista_disp' not in data or 'user_id' not in data:
        return jsonify({"error": "Input room_id, lista_disp, user_id richiesti"}), 400
    
    # Verifica che uno dergli input non sia vuoto
    if not data['room_id']:
        print('====ROOM ID ERROR====')
        return jsonify({"error": "room_id non può essere vuota"}), 400

    if not data['user_id']:
        print('====USER ID ERROR====')
        return jsonify({"error": "user_id non può essere vuota"}), 400

    room_id = data['room_id']
    lista_disp = data['lista_disp']  
    user_id = data['user_id']
    
    # Converti room_id in intero
    try:
        room_id = int(room_id)  # Conversione di room_id in intero
    except ValueError:
        return jsonify({"error": "room_id deve essere un intero valido"}), 400

    # Converti user_id in intero
    try:
        user_id = int(user_id)  # Conversione di room_id in intero
    except ValueError:
        return jsonify({"error": "room_id deve essere un intero valido"}), 400
    
    # Gestisci lista_disp per assicurarti che sia trattato correttamente
    if lista_disp == 101:  # Se lista_disp è esattamente 101, trattalo come un intero
        pass  
    elif not isinstance(lista_disp, list):  # Se non è né un 101 né una lista, errore
        return jsonify({"error": "lista_disp deve essere 101 o una lista valida"}), 400
    
    # Esegui il calcolo dei dati
    risposta = controllo_dispositivi(room_id, lista_disp, user_id)
    
    return jsonify({"response": risposta.value}), 200





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
    # Configura il server Flask per ascoltare su tutte le interfacce e la porta 5001 con SSL
    ssl_context = ('certs/cert.pem', 'certs/key.pem')
    app.run(debug=True, host="0.0.0.0", port=5001, ssl_context=ssl_context)