'use client'
import { useState } from 'react';
import {updateRoom } from '../admin-stage/adminServerActions';
import { useRouter } from "next/navigation";


export default function RoomCardEdit({roomInfo}){
    const router = useRouter();
    const [editMode, setEditMode] = useState(false);
    const [name, setName] = useState(roomInfo.name);
    const [floor, setFloor] = useState(roomInfo.floor);
    const [isRestricted, setIsRestricted] = useState(roomInfo.restriction);



    async function saveEdits(){
        setEditMode(false);
        const result = await updateRoom(roomInfo.id, name, floor, isRestricted);
        if(result){
            alert("Updated Successfully!");
            router.refresh();
        } else {
            alert("Something went wrong!");
        }
    }

    return(
        <div>
        { editMode ? (
            <form action={(e) => { e.preventDefault; saveEdits();}} className='flex flex-col gap-2 text-sm w-fit'>
            <p className='text-center text-slate-500'>Edit the room&#39;s specific:</p>
            <div className='flex flex-row gap-3'>
                <label htmlFor='name'>Name:</label>
                <input id='name' type='text' value={name} onChange={(e)=> {e.preventDefault(); setName(e.target.value);}}
                className="border-2 rounded border-gray-200 px-2 font-semibold max-w-40"/> 
            </div>
            <div className='flex flex-row gap-3'>
                <label>Id:</label>
                <p className=" px-2" >{roomInfo.id}</p>
            </div>
            <div className='flex flex-row gap-3'>
                <label htmlFor='floor'>Floor:</label>
                <input id='floor' type='text' value={floor} onChange={(e)=> {e.preventDefault(); setFloor(e.target.value);}}
                    className="border-2 rounded border-gray-200 px-2 max-w-40"/>
            </div>
            <div className='flex flex-row gap-3'>

            <label htmlFor="restr" >Restrict access:</label>
                <input id="restr" type="checkbox" value={isRestricted} checked={isRestricted} className="border-2 rounded border-gray-200 px-1" 
                        onChange={(e)=> {e.preventDefault(); setIsRestricted(!isRestricted);}}/>
            </div>
            <button type='submit' className='rounded text-white text-sm px-4 py-1 bg-indigo-600'>Save</button>
        </form>  
        ) : (
            <div className="flex flex-row gap-2 items-center w-full">
                    <p className='font-semibold w-fit text-lg uppercase'>{roomInfo.name}</p>
                    <p className='text-gray-400 text-sm w-fit whitespace-nowrap'> - {roomInfo.id}</p>
                    { roomInfo.floor ? (<p className='text-gray-400 text-sm w-fit whitespace-nowrap'>- Floor: {roomInfo.floor}</p>) : <p className='text-gray-400 text-sm w-fit '></p>
                }
                <button className='rounded bg-indigo-400 text-white px-1 py-1 hover:bg-indigo-600 ml-auto' onClick={(e)=> {e.preventDefault; setEditMode(!editMode);}}>
                    <svg xmlns="http://www.w3.org/2000/svg" width="1em" height="1em" viewBox="0 0 24 24"><g fill="none" stroke="currentColor" strokeLinecap="round" strokeLinejoin="round" strokeWidth="2">
                        <path d="M12 3H5a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/>
                        <path d="M18.375 2.625a1 1 0 0 1 3 3l-9.013 9.014a2 2 0 0 1-.853.505l-2.873.84a.5.5 0 0 1-.62-.62l.84-2.873a2 2 0 0 1 .506-.852z"/>
                        </g>
                    </svg>
                </button>

            </div>
        )

        }
    </div>       
    );
}