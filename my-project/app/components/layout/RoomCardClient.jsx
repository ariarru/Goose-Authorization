'use client'
import { useRouter } from 'next/navigation';
import { updateRoomStatus } from '../admin-stage/adminServerActions';
import { useState } from 'react';

export default function RoomCardClient({id}){
    const router = useRouter();

    async function del(){
        const result = await deleteRoom(id);
        if(result){
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
            <form onSubmit={(e)=> {e.preventDefault; del;}}>
             <button type="submit" className='rounded bg-rose-400 text-white px-4 py-1 hover:bg-red-600'>Delete</button>
            </form>
            <button onClick={(e)=> {e.preventDefault; removeRest();}} className='rounded bg-[#dc2f02]/50 text-white px-4 py-1 hover:bg-[#dc2f02] text-sm'>Remove restriction</button>
        </div>
    )
}
