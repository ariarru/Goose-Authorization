import dynamic from "next/dynamic";
import RoomCards from "../layout/RoomCards";
import RoomCardSimple from "../layout/RoomCardSimple";
import AddRoomComponent from '../forms/AddRoomComponent';
import SelectablePolygon from '../maps/SelectablePolygon';
import { redirect } from 'next/navigation';
import { createServer } from "@/app/utils/supabaseServer";




export default async function ManageRooms(){
    //mostra mappa x
    //mostra stanze x
    //pulsante aggiungi stanza
    //pulsante seleziona stanza x
    //pulsante elimina stanza x
    //pulsante modifica stanza
    const supabase = createServer();

    const {data, error} = await supabase.auth.getSession();

    if (!data.session) {
        console.log(error);
      redirect("./");
    }

    const rooms = await supabase.rpc("get_all_rooms");
    const allDevices = await supabase.rpc("get_all_sdevices");
    const allUsers = await supabase.rpc("get_all_users");

    const MyMap = dynamic(() => import('../maps/MyMap'), {ssr: false});

    console.log(rooms.data[0])

    return(
        <div className="flex flex-col">
            <p className="text-3xl text-center text-gray-400 pt-2">Your rooms</p>

            {rooms.data ? (<p className="text-sm text-center"></p>) : 
            (<p className="text-sm text-center text-gray-400 pt-2">There are no data availables, please insert new values</p>)}

            <div className=" grid grid-cols-2 gap-2 p-2 w-full">
                <section className="flex flex-row flex-wrap gap-2 mt-8 w-full h-fit ">  
                        {rooms.data?.map(r => 
                            (r.restriction ?  (
                                <RoomCards rm={r} key={r.id}> 
                                {allDevices.data} 
                                {allUsers.data}
                                </RoomCards>
                                ) :
                                (
                                    <RoomCardSimple rm={r} key={r.id}></RoomCardSimple>
                                ) 
                            ) 
                    )}

                </section>
                <section className="flex flex-col gap-1 text-right w-full right-0 items-end">
                    <MyMap width={"w-[30vw] h-[30vh] mt-4"} >
                        {rooms.data?.map( rm => (
                            <SelectablePolygon coords={rm.geojson_vertices} name={rm.name} key={rm.id}></SelectablePolygon>
                            
                        ))}
                    </MyMap>
                   <AddRoomComponent></AddRoomComponent>

                </section>
            </div>
            
        </div>
    );
}