import { createClient } from '@supabase/supabase-js';
import numeric from 'numeric';


// Configura Supabase - crea una connessione a Supabase
const supabaseUrl = 'https://cdutvkhtyqcsorzvmuzg.supabase.co';
const supabaseKey = 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImNkdXR2a2h0eXFjc29yenZtdXpnIiwicm9sZSI6ImFub24iLCJpYXQiOjE3MjM1NDMwOTYsImV4cCI6MjAzOTExOTA5Nn0.rE0B4Uvj6083r_QgGJy_KtizUfjY2bO8SMcmZFf3LRI';
const supabase = createClient(supabaseUrl, supabaseKey);

/*if (!supabase) {
    console.error('Impossibile connettersi a Supabase');
} else {
    console.log('Connessione a Supabase riuscita');
}*/

async function getVertices(_room_id) {
    //console.log('In funzione con ID stanza:', _room_id);
    const { data, error } = await supabase
        .rpc('get_room_vertices', { _room_id });
      
    if (error) {
        console.error('Errore nel recupero dei dati:', error);
        return null;
    }

    if (!data) {
        console.warn('Nessun dato trovato per l\'ID stanza:', _room_id);
        return null;
    }

    //Stampa il risultato in console
    //console.log('Risultato della query:', JSON.stringify(data, null, 2));
    
    return data.coordinates[0];
}



// Funzione principale per calcolare le trilaterazioni
async function calculateTrilateration(_room_id, d1=1, d2 =1.5 ) {
    const vertices = await getVertices(_room_id);
 
    if (vertices) {
        const selectedPoints = vertices.slice(0, 2);

        // Stampa i punti selezionati
        selectedPoints.forEach((point, index) => {
            console.log(`Punto ${index + 1}:`, point);
        });

        // Accedi ai valori specifici
        const x_v1 = selectedPoints[0][0]; // Primo valore del Punto 1 (x)
        const y_v1 = selectedPoints[0][1]; // Secondo valore del Punto 1 (y)
        const x_v2 = selectedPoints[1][0]; // Primo valore del Punto 2 (x)
        const y_v2 = selectedPoints[1][1]; // Secondo valore del Punto 2 (y)

        // Funzione non lineare da minimizzare
        function objectiveFunction(vars) {
            const x_t = vars[0];
            const y_t = vars[1];
            
            const eq1 = Math.pow((x_t - x_v1), 2) + Math.pow((y_t - y_v1), 2) - Math.pow(d1, 2);
            const eq2 = Math.pow((x_t - x_v2), 2) + Math.pow((y_t - y_v2), 2) - Math.pow(d2, 2);
            
            // Somma degli errori quadrati
            return eq1 ** 2 + eq2 ** 2;
        }

        // Ipotesi iniziale
        const initialGuess = [parseFloat(x_v1.toFixed(2)), parseFloat(x_v2.toFixed(2))];

        // Risoluzione del sistema usando il metodo di minimizzazione
        const result = numeric.uncmin(objectiveFunction, initialGuess);

        const x_t = result.solution[0];
        const y_t = result.solution[1];
                
        // Restituisce la coppia (x_t, y_t)
        console.log(`Coordinate del target: x_t = ${x_t}, y_t = ${y_t}`);
        return { x_t, y_t };

    } else {
        console.log('Impossibile recuperare i punti.');
    }


}

 




// Esegui la funzione principale
calculateTrilateration(1);
