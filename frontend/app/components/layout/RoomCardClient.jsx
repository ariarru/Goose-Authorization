'use client'
import { useRouter } from 'next/navigation';
import { updateRoomStatus, deleteRoom } from '../admin-stage/adminServerActions';

export default function RoomCardClient({id}){
    const router = useRouter();

    async function del(){
        const result = await deleteRoom(id);
        if(result == true){
            alert("Room deleted");
            router.refresh();
        } else {
            alert("Could not delete room");
        }
    }

    async function removeRest(){ 
        const result = await updateRoomStatus(id, false);
        if(result == true){
            alert("Restriction removed");
            router.refresh();
        } else {
            alert("Could not remove restriction");
        }
    }

    return(
        <div className="flex flex-row gap-1">
            <button onClick={(ev)=> {ev.preventDefault; del();}} className='rounded bg-rose-400 text-white px-4 py-1 hover:bg-red-600'>Delete</button>
            <button onClick={(e)=> {e.preventDefault; removeRest();}} className='rounded bg-[#dc2f02]/50 text-white px-4 py-1 hover:bg-[#dc2f02] text-sm'>Remove restriction</button>
        </div>
    )
}
