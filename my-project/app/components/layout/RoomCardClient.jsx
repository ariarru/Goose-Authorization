'use client'
import { useRouter } from 'next/navigation';

export default function RoomCardClient(id){

    const router = useRouter();

    async function del(){
        const result = await deleteRoom(id);
        console.log(result);
        if(result){
            alert("Room deleted");
            router.refresh();
        } else {
            alert("Could not delete room");
        }
    }

    return(
        <div className="flex flex-row gap-2">
            <button className='rounded bg-indigo-400 text-white px-4 py-1 hover:bg-indigo-600'>Edit</button>
            <form onSubmit={(e)=> {e.preventDefault; del;}}>
             <button type="submit" className='rounded bg-rose-400 text-white px-4 py-1 hover:bg-red-600'>Delete</button>
            </form>
        </div>
    )
}
