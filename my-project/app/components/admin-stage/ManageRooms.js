'use client'
import { useEffect, useState } from "react";
import MapContainer from "../MapContainer";

export default function ManageRooms({roomsData}){
    //mostra mappa
    //mostra stanze
    //pulsante aggiungi stanza
    //pulsante seleziona stanza
    //pulsante elimina stanza
    //pulsante modifica stanza

    const coordinatesBolo = {lat: 44.497439, long: 11.356414};

    const [lat, setLat] = useState(); //11.356414
    const [long, setLong] = useState(); //11.356414
    

    function goTo(){
        
    }

    const btnClasses ="my-0 p-2 rounded text-white bg-indigo-500 hover:bg-indigo-600";
    
    return(
        <div className="flex flex-row gap-2 p-2">
            <section className="flex flex-col gap-4 mt-8">
                <button className={`${btnClasses}`}>Add Room</button>
                <button className={`${btnClasses}`}>Delete Room</button>
                <button className={`${btnClasses}`}>Edit Room</button>
                <form className="flex flex-col gap-2">
                    <label className="text-gray-400">Latitude:</label>
                    <input className="border-solid border-slate-500 rounded p-2 shadow-md"
                        type="number" value={lat} onChange={e => setLat(e.target.value)}></input>
                    <label className="text-gray-400">Longitude:</label>
                    <input className="border-solid border-slate-500 rounded p-2 shadow-md"
                        type="number" value={long} onChange={e => setLong(e.target.value)}></input>
                    <button type="submit" className="text-white bg-indigo-600 p-2 rounded" onClick={goTo}>Go</button>
                </form>
            </section>
            <MapContainer width={"w-[50vw]"} height={"h-[70vh]"} 
                latitute={lat} 
                longitude={long} 
                zoomIn={20}></MapContainer>
        </div>
    );
}