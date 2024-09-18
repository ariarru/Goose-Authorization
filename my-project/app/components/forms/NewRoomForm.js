'use client';
import { useState } from "react";
import { addNewUser } from "../admin-stage/adminServerActions";

export default function NewRoomForm(){

    const [name, setName] = useState();
    //select categories with checkbox? -> then getDeviceCategories from supabase


    async function sendData(){
        const result = await addNewRoom();
        console.log("result");
        console.log(result);
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
                <label htmlFor="username"> Room's name: </label>
                <input className='border-2 border-gray-200 rounded m-[-3px]' type='text' id="username" 
                    onChange={e => setName(e.target.value)} value={name} ></input>
{/*
                <label htmlFor="email"> E-mail: </label>
                <input className='border-2 border-gray-200 rounded m-[-3px]' type='text' id="email" 
                    onChange={e => setEmail(e.target.value)} value={email}></input>

                <label htmlFor="pw"> First password: </label>
                <input className='border-2 border-gray-200 rounded m-[-3px]' type='password' id="pw" 
                    onChange={e => setFirstPassword(e.target.value)} value={firstPassword}></input>

                <div className="flex flex-row gap-2 items-center">
                    <label htmlFor="adminBool">Admin: </label>
                    <input type="checkbox" id="adminBool" 
                        onChange={e => setIsAdmin(e.target.checked)} checked={isAdmin}></input>
                </div>
*/
}
                
                
                <button type='submit' className={`${btnClasses}`}>Add New Room</button> 
            </form>
        </div>
    );
}