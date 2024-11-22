// Funzione per chiamare l'API /api/fingerprint

function chiamareFingerprint() {
  const url = 'http://localhost:5001/api/fingerprint';  // URL del backend

  // Raccogli i dati dai campi di input
  const jsonInput = JSON.parse(document.getElementById('jsoninput').value);  // Assicurati che l'input JSON sia valido
  const userId = document.getElementById('userid').value;  // Recupera l'ID utente

  // Verifica che l'input sia valido
  if (!jsonInput || !userId) {
    alert('Per favore, inserisci tutti i campi richiesti.');
    return;
  }

  // Dati da inviare al backend
  const data = {
    json_input: jsonInput,
    user_id: userId
  };

  // Invia la richiesta POST al backend
  fetch(url, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(data)  // Converte i dati in formato JSON
  })
  .then(response => {
    if (!response.ok) {
      throw new Error('Errore nella risposta del server');
    }
    return response.json();  // Restituisce la risposta come JSON
  })
  .then(data => {
    // Log della risposta
    console.log('Risposta dell\'API /api/fingerprint:', data);
    const { predicted_room, contr_ble } = data;
    document.getElementById('output').innerHTML = `Predicted Room: ${predicted_room}, Contr BLE: ${contr_ble}`;
  })
  .catch(error => {
    console.error('Errore nella richiesta:', error);
    document.getElementById('output').innerHTML = 'Errore durante la chiamata all\'API.';
  });
}

// Funzione per chiamare l'API /api/controlloBle
function chiamareControlloBle() {
  const url = 'http://localhost:5001/api/controlloBle';  // URL del backend

  // Raccogli i dati dai campi di input
  const roomId = document.getElementById('room-id').value;  // Recupera l'ID della stanza
  const listaDisp = document.getElementById('dispositivi').value.split(',').map(d => d.trim());  // Lista di dispositivi

  // Verifica che l'input sia valido
  if (!roomId || listaDisp.length === 0) {
    alert('Per favore, inserisci tutti i campi richiesti.');
    return;
  }

  // Dati da inviare al backend
  const data = {
    room_id: roomId,
    lista_disp: listaDisp
  };

  // Invia la richiesta POST al backend
  fetch(url, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(data)  // Converte i dati in formato JSON
  })
  .then(response => {
    if (!response.ok) {
      throw new Error('Errore nella risposta del server');
    }
    return response.json();  // Restituisce la risposta come JSON
  })
  .then(data => {
    // Log della risposta
    console.log('Risposta dell\'API /api/controlloBle:', data);
    const risultato = data.return;
    document.getElementById('output').innerHTML = `Risultato: ${risultato}`;
  })
  .catch(error => {
    console.error('Errore nella richiesta:', error);
    document.getElementById('output').innerHTML = 'Errore durante la chiamata all\'API.';
  });
}

// Gestione eventi per i pulsanti
document.getElementById('fingerprint-btn').addEventListener('click', chiamareFingerprint);
document.getElementById('controlloBle-btn').addEventListener('click', chiamareControlloBle);
