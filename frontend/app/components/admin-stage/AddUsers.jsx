'use client'
import { useRouter } from 'next/navigation';
import {addUserFromRoom} from '../admin-stage/adminServerActions'

export default function AddUsers({users, roomId}){

    const router = useRouter()

    async function add(id){
        const result = await addUserFromRoom(id, roomId);
        if(result != true){
            alert(result);
            console.log(result);
        } else {
            alert("User inserted");
            router.refresh();
        }
    }
    return(
    <div className='w-fit items-center bg-slate-100  rounded-xl p-1 h-fit py-2 justify-items-center flex flex-col relative left-1/4 '>
        {users?.map((u) =>(
            <button onClick={(e)=>{ e.preventDefault; add(u.user_id);}} className="text-black text-sm hover:bg-slate-200  p-1" key={u.user_id}>{u.username}</button>
        )
        )}
    </div>
    )
}