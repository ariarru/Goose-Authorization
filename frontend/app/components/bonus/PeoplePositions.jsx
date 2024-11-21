'use client';
import { useEffect, useState } from 'react';
import L, { marker } from 'leaflet';
import { LayerGroup, Marker } from 'react-leaflet';
import { getPositionPeople } from '../admin-stage/adminServerActions';
import * as turf from 'turf'
import { destination } from 'turf-destination'

function movePoint(lat, lng){
    // Punto di partenza
    const point = turf.point([lat, lng]); // [lng, lat]
    // Offset in metri e direzione (in gradi)
    const distance = 0.0015; // Distanza in km (2 metri)
    const bearing = Math.floor(Math.random() * (360 + 1)) - 180; 
    // Calcola il nuovo punto
    const newPoint = turf.destination(point, distance, bearing);
    return newPoint.geometry.coordinates;
}


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
                // Calcola una nuova posizione alterata
                const modifiedCenter = movePoint(value.room_center.lat, value.room_center.long);
                    
                //aggiungi a markers l'oggetto
                const newMarker = {
                    k : parseInt(value.id_room) + m,
                    center : modifiedCenter
                }
                markers.push(newMarker);
            }
        })
        console.log("markers", markers)
    
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
