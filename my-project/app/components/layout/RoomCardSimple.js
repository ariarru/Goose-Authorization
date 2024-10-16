'use client';
import { useState } from 'react';
import { deleteRoom, updateRoom } from '../admin-stage/adminServerActions';
import Card from './Card';
import { useRouter } from 'next/navigation';

export default function RoomCardSimple({rm}){

    const router = useRouter();
    const [edit, setEdit] = useState(false);
    const [name, setName] = useState(rm.name);
    const [floor, setFloor] = useState(rm.floor);
    const [isRestricted, setIsRestricted] = useState(false);

    async function saveEdits(){
        setEdit(false);
        const result = await updateRoom(rm.id, name, floor, isRestricted);
        if(result){
            alert("Updated Successfully!");
            router.refresh();
        } else {
            alert("Something went wrong!");
        }
    }


    async function del(){
        const result = await deleteRoom(id);
        if(result != true){
            alert("Room deleted");
            router.refresh();
        } else {
            alert("Could not delete room");
        }
    }

    return(
        <Card add='border-2 border-gray-200 rounded p-2 h-fit w-fit'>
            <div className="flex flex-col gap-1">
                { edit ? (
                    <form action={(e) => { e.preventDefault; saveEdits();}} className='flex flex-col gap-2 text-sm w-fit'>
                        <p className='text-center text-slate-500'>Edit the room's specific:</p>
                        <div className='flex flex-row gap-3'>
                            <label htmlFor='name'>Name:</label>
                            <input id='name' type='text' value={name} onChange={(e)=> {e.preventDefault(); setName(e.target.value);}}
                            className="border-2 rounded border-gray-200 px-2 font-semibold max-w-40"/> 
                        </div>
                        <div className='flex flex-row gap-3'>
                            <label>Id:</label>
                            <p className=" px-2" >{rm.id}</p>
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
                ) :
                    (
                        <div className="flex flex-row gap-2 items-center w-fit break-keep">
                            <p className='font-semibold w-fit text-lg uppercase'>{rm.name}</p>
                            <p className='text-gray-400 text-sm w-fit break-keep'> - {rm.id}</p>
                            { rm.floor ? (<p className='text-gray-400 text-sm break-keep'>- Floor: {rm.floor}</p>) : <p className='text-gray-400 text-sm w-fit '></p>
                        }
                        </div>
                    )

                }
                
                
                <div className="flex flex-row gap-2">
                    <button onClick={(e) => {e.preventDefault; setEdit(!edit)}} className='rounded bg-indigo-400 text-white px-4 py-1 hover:bg-indigo-600'>Edit</button>
                    <form onSubmit={(e)=> {e.preventDefault; del;}}>
                    <button type="submit" className='rounded bg-rose-400 text-white px-4 py-1 hover:bg-red-600'>Delete</button>
                    </form>
                </div>
            </div>
            
        </Card>
    );
};