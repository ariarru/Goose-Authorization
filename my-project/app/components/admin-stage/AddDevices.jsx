'use client'
import { useRouter } from 'next/navigation';
import { addDevFromRoom } from './adminServerActions'
export default function AddDevices({devs, roomId}){

    const router = useRouter()

    async function add(id){
        const result = await addDevFromRoom(id, roomId);
        if(result != true){
            alert(result);
            console.log(result);
        } else {
            alert("Device inserted");
            router.refresh();
        }
    }
    return(
    <div className='w-fit items-center bg-slate-100 rounded-xl p-1 h-fit py-2 justify-items-center flex flex-col relative left-1/4 '>
        {devs?.map((d) =>(
            <button onClick={(e)=>{ e.preventDefault; add(d.device_s_id);}} className="text-black text-sm h:bg-slate-200 p-1" key={d.device_s_id}>{d.device_s_name} - {d.category}</button>
        )
        )}
    </div>
    )
}