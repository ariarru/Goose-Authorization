import MapContainer from "../MapContainer";

export default function ManageRooms({roomsData}){
    //mostra mappa
    //mostra stanze
    //pulsante aggiungi stanza
    //pulsante seleziona stanza
    //pulsante elimina stanza
    //pulsante modifica stanza

    const coordinatesBolo = {lat: 5542671.018744591, long: 1264194.4476738372};


    const btnClasses ="my-0 p-2 rounded text-white bg-indigo-500 hover:bg-indigo-600 w-64";
    
    return(
        <div className="flex flex-col">
            <p className="text-3xl text-center text-gray-400 pt-2">Your rooms</p>
            {roomsData? roomsData : 
            (<p className="text-sm text-center text-gray-400 pt-2">There are no data availables, please insert new values</p>)}
            <div className="flex flex-row gap-2 p-2">
                <section className="flex flex-col gap-4 mt-8">
                    <button className={`${btnClasses}`}>Add Room</button>
                    <button className={`${btnClasses}`}>Delete Room</button>
                    <button className={`${btnClasses}`}>Edit Room</button>
                    
                </section>
                <MapContainer width={"w-[50vw]"} height={"h-[70vh]"} 
                    latitute={coordinatesBolo.lat} 
                    longitude={coordinatesBolo.long} 
                    zoomIn={20}></MapContainer>
            </div>
        </div>
    );
}