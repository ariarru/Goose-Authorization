'use server';
import dynamic from "next/dynamic";
import { createServer } from '../utils/supabaseServer';
import SelectablePolygon from "../components/maps/SelectablePolygon";
import GetWifiBtn from '../components/layout/GetWifiBtn';



export default async function UserHomePage(){

    const MyMap = dynamic(() => import('../components/maps/MyMap'), {ssr: false});

    const supabase = createServer();

    const rooms = await supabase.rpc('get_all_rooms');

    

    return(
        <main className="flex flex-col items-center gap-3">
            <p className="mx-auto mt-2 text-2xl">Welcome back!</p>
            <MyMap width={"w-[40vw]"} height={"h-[50vh]"} zoom={18}>
            {rooms.data?.map( rm => (
                <SelectablePolygon coords={rm.geojson_vertices} name={rm.name} key={rm.id}></SelectablePolygon>
                
            ))}
            </MyMap>
            <GetWifiBtn></GetWifiBtn>
        </main>
    );
}