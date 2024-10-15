'use server'
import { createServer } from "@/app/utils/supabaseServer";
import RemoveDevices from "../admin-stage/RemoveDevices";
import AddDevicesBtn from "../admin-stage/AddDeviceBtn"
export default async function RoomCardServer({roomId, allDevs}){
    const supabase = createServer();

    var result = await supabase.rpc("get_users_from_room_id", {id: roomId});
    const users = result.data;
    result = await supabase.rpc("get_devices_from_room_id", {id: roomId});
    const devices = result.data;
    
    
    return(
        <div>
            <div className="flex flex-col gap-1 mb-2">
                <div className="flex flex-row gap-2 align-middle items-center mb-2">
                    <p className="text-gray-400">List of Devices</p>
                    <AddDevicesBtn devices={allDevs} roomId={roomId}/>
                </div>
                
                {devices?.length > 0 ? devices?.map(d => (
                    <div className="flex flex-row gap-3 py-1 px-3 rounded bg-gray-300 ml-[-4px] text-left items-center">
                        <p className="text-gray-700 text-sm" key={d.dev_id}>{d.name} - {d.category}</p>
                        <RemoveDevices id={d.dev_id} key={d.name}/>
                        
                    </div>
                )) : (<p></p>)
            }
            </div>
            <div>
                <div className="flex flex-row gap-2 align-middle items-center mb-2">
                        <p className="text-gray-400">List of Users</p>
                        <AddDevicesBtn devices={allDevs} roomId={roomId.roomId}/>
                    </div>
                {users?.map(u => (
                    <p key={u.user_id}>{u.name}</p>
                ))}
            </div>
        </div>
    )

}