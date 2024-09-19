import dynamic from "next/dynamic";

export default function ManageRooms({roomsData}){
    //mostra mappa x
    //mostra stanze x
    //pulsante aggiungi stanza
    //pulsante seleziona stanza
    //pulsante elimina stanza
    //pulsante modifica stanza

    const MyMap = dynamic(() => import('../maps/MyMap'), {ssr: false});

    const btnClasses ="my-0 p-2 rounded text-white bg-indigo-500 hover:bg-indigo-600 w-64";
    
    return(
        <div className="flex flex-col">
            <p className="text-3xl text-center text-gray-400 pt-2">Your rooms</p>
            {roomsData ? (<p className="text-sm text-center"></p>) : 
            (<p className="text-sm text-center text-gray-400 pt-2">There are no data availables, please insert new values</p>)}
            <div className="flex flex-row gap-2 p-2">
                <section className="flex flex-col gap-4 mt-8">  
                    <button className={`${btnClasses}`}>Add New Room</button>
                    <button className={`${btnClasses}`}>Delete Room</button>
                    <button className={`${btnClasses}`}>Edit Room</button>        
                </section>
                <MyMap width={"w-4/5 h-[60vh] mt-4"} >
                    {roomsData}
                </MyMap>
                
            </div>
        </div>
    );
}