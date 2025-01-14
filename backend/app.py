import os
import sys
from enum import Enum
from flask_cors import CORS
from dotenv import load_dotenv
from flask import Flask, jsonify, request
from fingerprinting import fingerprinting  
from controllo_dispositivi import controllo_dispositivi
from supporto.scansione_rete import save_data_to_file, scan_wifi
from supabase import create_client, Client
from access import handle_login, LoginError

app = Flask(__name__)
CORS(app)  # Abilita CORS per tutte le rotte

'''FLASK_URL="https://localhost:5001"
FINGERPRINT_URL="https://localhost:5001/api/fingerprint"
CONTROLLORBLE_URL="https://localhost:5001/api/controlloBle"
EFFECT_LOGIN= "https://localhost:5001/api/login"
'''

FLASK_URL = "https://backend-service:5001"
FINGERPRINT_URL = f"{FLASK_URL}/api/fingerprint"
CONTROLLORBLE_URL = f"{FLASK_URL}/api/controlloBle"
EFFECT_LOGIN = f"{FLASK_URL}/api/login"


@app.route('/api/fingerprint', methods=['POST'])
def fingerprint():
    print("=== Received fingerprint request ===")
    try:
        # Ottieni i dati dal corpo della richiesta JSON
        data = request.get_json()

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

        result = fingerprinting(json_input, user_id)
        print(f"Fingerprinting result: {result}")
        
        # Gestisci risultato con diversi tipi
        if len(result) == 3:  # Caso base
            room_id, room_name, contr_ble = result
            return jsonify({
                "predicted_room": room_id,
                "room_name": room_name,
                "contr_ble": contr_ble
            })
        
        elif len(result) == 4:  # Caso non autorizzato
            code, predicted_room, room_id, notif_type = result
            return jsonify({
                "error": "Unauthorized access: User does not have permission for this room",
                "code": code,
                "predicted_room": predicted_room,
                "room_id": room_id,
                "notif_type": notif_type[0]['notification_preference']
            }), 400
        
        elif len(result) == 2:  # Caso errore
            code, message = result
            return jsonify({
                "error": message,
                "code": code
            }), 400
        
        else:
            return jsonify({"error": "Unexpected result from fingerprinting"}), 500
        
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
        room_id = int(room_id) 
    except ValueError:
        return jsonify({"error": "room_id deve essere un intero valido"}), 400

    # Converti user_id in intero
    try:
        user_id = int(user_id)
    except ValueError:
        return jsonify({"error": "room_id deve essere un intero valido"}), 400
    
    # Gestisci lista_disp per assicurarti che sia trattato correttamente
    if isinstance(lista_disp, list):  # Se non è una lista, errore
        return jsonify({"error": "lista_disp deve essereuna lista valida"}), 400
    
    # Esegui il calcolo dei dati
    errore, notif_type = controllo_dispositivi(room_id, lista_disp, user_id)
    
    return jsonify({"response": errore.value, "notif_type": notif_type}), 200




@app.route('/api/login', methods=['POST'])
def login():
    try:
        data = request.get_json()
        if not data or 'username' not in data or 'password' not in data:
            return jsonify({"error": "Username and password are required"}), 400

        username = data['username']
        password = data['password']

        if not username or not password:
            return jsonify({"error": "Empty fields not allowed"}), 400

        result = handle_login(username, password)
        return jsonify(result)

    except LoginError as e:
        return jsonify({"error": str(e)}), 401
    except Exception as e:
        return jsonify({"error": "Login failed"}), 500




# Rotta per restituire gli URL completi
@app.route('/api/urls', methods=['GET'])
def get_urls():
    return jsonify({
        "flask_url": FLASK_URL,
        "fingerprint_url": FINGERPRINT_URL,
        "controllo_ble_url": CONTROLLORBLE_URL,
        "login_url": EFFECT_LOGIN
    })


# Rotta di base
@app.route('/')
def home():
    return "Backend is working!"

if __name__ == '__main__':
    # Configura il server Flask per ascoltare su tutte le interfacce e la porta 5001 con SSL
    ssl_context = ('certs/cert.pem', 'certs/key.pem')
    app.run(debug=True, host="0.0.0.0", port=5001, ssl_context=ssl_context)
