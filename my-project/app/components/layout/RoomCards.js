'use client';
import { useRouter } from 'next/navigation';
import { deleteRoom, getAuthUsersFromRoomId, getDevicesFromRoomId } from '../admin-stage/adminServerActions';
import Card from './Card';

export default function RoomCards({rm}){
  
    const router = useRouter();

    async function del(){
        const result = await deleteRoom(rm.room_id);
        if(result){
            alert("Room deleted");
            router.refresh();
        } else {
            alert("Could not delete room");
        }
    }

    async function getDevices(){
        const result = await getDevicesFromRoomId(rm.room_id);
        console.log("devices");
        console.log(result);
        if(result){
            return result.data;
        } 
    }

    async function getUsers(){
        const result = await getAuthUsersFromRoomId(rm.room_id);
        console.log("users");
        console.log(result);
        if(result){
            return result.data;
        } 
    }

    const dev = getDevices();
    const authUsers = getUsers();

    return(
        <Card add='border-2 border-gray-200 rounded p-2 h-fit w-fit'>
            <div className="flex flex-col gap-1">
                <div className="flex flex-row gap-2 items-center">
                    <p className='font-semibold w-fit text-lg uppercase'>{rm.room_name}</p>
                    <p className='text-gray-400 text-sm w-fit '> - {rm.room_id}</p>
                </div>
                { rm.piano ? (<p>Floor: {rm.piano}</p>) : <p></p>
                }
                <div>
                    {dev.length > 0 ? dev.map(d => (
                        <p key={d.dev_id}>{d.name} - {d.category}</p>
                    )) : <p></p>}
                </div>
                <div className="flex flex-row gap-2">
                    <button className='rounded bg-indigo-400 text-white px-4 py-1 hover:bg-indigo-600'>Edit</button>
                    <button className='rounded bg-rose-400 text-white px-4 py-1 hover:bg-red-600' onClick={del}>Delete</button>
                </div>
            </div>
            
        </Card>
    );
};