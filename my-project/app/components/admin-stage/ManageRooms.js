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
    const [lat, setLat] = useState(11.356414); //11.356414
    const [long, setLong] = useState(44.497213); //1263045.455384

    let completeLat = 5542735.559814; //lat bolo
    let completeLong =1263045.455384; //long bolo

    function goTo(){ 
        completeLat=lat;
        completeLong= long;
    }

    
    return(
        <div className="flex flex-row gap-2 p-2">
            <section className="flex flex-col gap-4">
                <button>Add Room</button>
                <button>Add Room</button>
                <button>Add Room</button>
                 
                <form className="flex flex-col gap-2">
                    <label className="text-gray-400">Latitude:</label>
                    <input className="border-solid border-slate-500 rounded p-2 shadow-md"
                        type="number" value={lat} onChange={e => setLat(e.target.value)}></input>
                    <label className="text-gray-400">Longitude:</label>
                    <input className="border-solid border-slate-500 rounded p-2 shadow-md"
                        type="number" value={long} onChange={e => setLong(e.target.value)}></input>
                    <button type="submit" className="text-white bg-sky-600 p-2 rounded" onClick={goTo}>Go</button>
                </form>
                
            </section>
            <MapContainer width={"w-[50vw]"} height={"h-[70vh]"} 
                latitute={lat} 
                longitude={long} 
                zoomIn={20}></MapContainer>
        </div>
    );
}