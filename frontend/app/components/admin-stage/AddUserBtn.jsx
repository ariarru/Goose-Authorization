'use client'
import { useState } from 'react'
import AddUsers from './AddUsers'


export default function AddUserBtn({allUs, roomId}){

    const [hidden, setHidden] = useState(true);
    
    return(
    <div className='relative flex flex-row w-fit items-center bg-slate-100 rounded-xl p-1 text-center h-fit py-0 justify-items-center'>
        <button className='text-slate-600 m-0 items-center justify-items-center pt-1' onClick={(e)=> { e.preventDefault; setHidden(!hidden);}}>
        <svg xmlns="http://www.w3.org/2000/svg" width="1em" height="1em" viewBox="0 0 24 24"><path fill="currentColor" d="M18 10h-4V6a2 2 0 0 0-4 0l.071 4H6a2 2 0 0 0 0 4l4.071-.071L10 18a2 2 0 0 0 4 0v-4.071L18 14a2 2 0 0 0 0-4"/></svg>        
        </button>
        <div className={`${hidden ? 'hidden': 'block'} absolute w-fit`}>
            <AddUsers users={allUs} roomId={roomId}/>
        </div>
        
    </div>

    )
}