'use client';
import { useState } from "react";

export default function NewRoomForm(){

    const [name, setName] = useState('');
    //select categories with checkbox? -> then getDeviceCategories from supabase


    async function sendData(){
        const result = await addNewRoom();
        if(result){
            alert("Room successfully added");
        } else {
            alert("Could not insert room");
        }

        setName('');
    }

    const btnClasses ="my-2 p-2 rounded text-white bg-indigo-500 hover:bg-indigo-600";


    return(
        <div>
            <form onSubmit={e => {e.preventDefault(); sendData();}} className="flex flex-col gap-2 p-5 text-sm w-fit border-2 border-gray-200 rounded">
                <p className="text-gray-400">Add a new room</p>
                <label htmlFor="username"> Room&#39;s name: </label>
                <input className='border-2 border-gray-200 rounded m-[-3px]' type='text' id="username" 
                    onChange={e => setName(e.target.value)} value={name} ></input>
                
                
                <button type='submit' className={`${btnClasses}`}>Add New Room</button> 
            </form>
        </div>
    );
}