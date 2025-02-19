'use server'
import { createServer } from "@/app/utils/supabaseServer";
import RemoveDevices from "../admin-stage/RemoveDevices";
import RemoveUser from "../admin-stage/RemoveUser";
import AddDevicesBtn from "../admin-stage/AddDeviceBtn";
import AddUserBtn from "../admin-stage/AddUserBtn";
import EditNotification from "../admin-stage/EditNotification";

export default async function RoomCardServer({roomId, allDevs, allUsers}){
    const supabase = createServer();

    var result = await supabase.rpc("get_users_from_room_id", {id: roomId});
    const users = result.data;
    result = await supabase.rpc("get_devices_from_room_id", {id: roomId});
    const devices = result.data;
    result = await supabase.rpc("get_notif", {_room_id: roomId});
    const currentNotificationType = result.data.length > 0 ? result.data[0] : "popup";
    


    return(
        <div>
            <div className="flex flex-col gap-1 mb-2">
                <div className="flex flex-row gap-2 align-middle items-center mb-2">
                    <p className="text-gray-400">List of Devices</p>
                    <AddDevicesBtn devices={allDevs} roomId={roomId}/>
                </div>
                
                {devices?.length > 0 ? devices?.map(d => (
                    <div className="flex flex-row gap-3 py-1 px-3 rounded bg-gray-300 ml-[-4px] text-left items-center" key={d.dev_id + roomId}>
                        <p className="text-gray-700 text-sm" key={d.dev_id}>{d.name} - {d.category}</p>
                        <RemoveDevices id={d.dev_id} room={roomId} key={d.name}/>
                    </div>
                )) : (<p></p>)}
            </div>
            
            <div className="flex flex-col gap-1">
                <div className="flex flex-row gap-2 align-middle items-center mb-2">
                    <p className="text-gray-400">List of Users</p>
                    <AddUserBtn allUs={allUsers} roomId={roomId}/>
                </div>
                {users?.map(u => (
                    <div className="flex flex-row gap-3 py-1 px-3 rounded bg-gray-300 ml-[-4px] text-left items-center" key={u.user_id + roomId}>
                        <p className="text-gray-700 text-sm" key={u.user_id}>{u.username}</p>
                        <RemoveUser id={u.user_id} room={roomId} key={u.username}/>
                    </div>
                ))}
            </div>

            <div className="flex flex-col gap-1">
               <EditNotification id={roomId} currentType={currentNotificationType}/>
            </div>
        </div>
    );
}