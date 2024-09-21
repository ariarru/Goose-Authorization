import dynamic from "next/dynamic";
import RoomCards from "../layout/RoomCards";
import AddRoomComponent from '../forms/AddRoomComponent';
import SelectablePolygon from '../maps/SelectablePolygon';
import { createClient } from '@/app/utils/supabaseClient';
import { redirect } from 'next/navigation';




export default async function ManageRooms(){
    //mostra mappa x
    //mostra stanze x
    //pulsante aggiungi stanza
    //pulsante seleziona stanza
    //pulsante elimina stanza
    //pulsante modifica stanza
    const supabase = createClient();

    const session = async () => {await supabase.auth.getSession();}

    if (!session) {
      redirect("./");
    }
    const rooms = await supabase.rpc("get_all_rooms");

    const MyMap = dynamic(() => import('../maps/MyMap'), {ssr: false});


    return(
        <div className="flex flex-col">
            <p className="text-3xl text-center text-gray-400 pt-2">Your rooms</p>

            {rooms.data ? (<p className="text-sm text-center"></p>) : 
            (<p className="text-sm text-center text-gray-400 pt-2">There are no data availables, please insert new values</p>)}

            <div className="flex flex-row gap-2 p-2">
            <section className="grid-cols-2 gap-4 mt-8 w-1/2">  
                    {rooms.data?.map(r => (
                        <RoomCards rm={r} key={r.room_id}></RoomCards>
                    ))}

            </section>
                <MyMap width={"w-[40vw] h-[30vh] mt-4"} >
                    {rooms.data?.map( rm => (
                        <SelectablePolygon coords={rm.geojson_vertices} name={rm.room_name} key={rm.room_id}></SelectablePolygon>
                        
                    ))}

                </MyMap>
                
            </div>
            <AddRoomComponent></AddRoomComponent>
        </div>
    );
}