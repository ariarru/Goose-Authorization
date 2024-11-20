'use client';
import { useEffect, useState } from 'react';
import L from 'leaflet';
import { LayerGroup, Marker } from 'react-leaflet';
import { getPositionPeople } from '../admin-stage/adminServerActions';


export default function PeopleComponent() {
    const icon = new L.icon ({
        iconUrl: './user-icon.svg',
        iconSize: [20, 20],
        iconAnchor: [6, 12]
    })
    const [people, setPeople] = useState([]);
    var markers = [];

    const get_people = async () => {
        const result = await getPositionPeople();
        setPeople(result);
        
    };

    useEffect(() => {
        get_people();
        
        // Imposta il timer per richiamarlo ogni 3 secondi
        const interval = setInterval(() => {
        get_people();
        }, 3000);
        // Pulisci l'intervallo quando il componente si smonta
        return () => clearInterval(interval);
    }, []);
      
    if (people.length === 0) {
        return null; // o un loading, se necessario
    } else{
        //prendo il valore per ogni elemento dell'array
        people.map( (value) => {
            for(let m=0; m< value.people_count; m++){
                const randomOffset = () => (Math.random() * 0.000000002 - 0.000000001); 
                // Calcola una nuova posizione alterata
                const modifiedCenter = [
                    value.room_center[0] + randomOffset(), // Modifica latitudine
                    value.room_center[1] + randomOffset()  // Modifica longitudine
                ];
                //aggiungi a markers l'oggetto
                const newMarker = {
                    k : parseInt(value.id_room) + m,
                    center : modifiedCenter
                }
                markers.push(newMarker);
            }
        })
    }
    
    return(
        <>
            {  markers?.map( (singleMarker) =>
                (<Marker 
                    key={singleMarker.k} 
                    position={[singleMarker.center[0], singleMarker.center[1]]}  
                    icon={icon}
                    />
                ))
            }
           
        </>
    );
    
}
