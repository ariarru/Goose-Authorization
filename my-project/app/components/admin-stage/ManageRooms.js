'use client'
import { useState } from "react";
import MapContainer from "../MapContainer";

export default function ManageRooms(){
    //mostra mappa
    //mostra stanze
    //pulsante aggiungi stanza
    //pulsante seleziona stanza
    //pulsante elimina stanza
    //pulsante modifica stanza

    //GoToBtn
    const {lat, setLat} = useState('');
    const {long, setLong} = useState('');

    return(
        <div className="flex flex-row gap-2 p-2">
            <section className="flex flex-col gap-4">
                <button>Add Room</button>
                <button>Add Room</button>
                <button>Add Room</button>

                <form className="flex flex-col gap-2">
                    <label className="text-gray-400">Latitude</label>
                    <input type="text" value={lat} onChange={e => setLat(e.target.value)}></input>
                    <label className="text-gray-400">Latitude</label>
                    <input type="text" value={long} onChange={e => setLong(e.target.value)}></input>
                    <button className="text-white bg-sky-600 p-2 rounded">Go</button>
                </form>
                
            </section>
            <MapContainer width={"w-[50vw]"} height={"h-[70vh]"} 
                latitute={5542735.559814} 
                longitude={1263045.455384} 
                zoomIn={16}></MapContainer>
        </div>
    );
}