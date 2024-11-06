'use client'
import { useState } from "react";


export default function AddDevice({id, user}){

    const [hidden, setHidden] = useState(true);
    const [nameDevice, setNameDevice] = useState('');

    function add(){
        if(hidden){
            setHidden(false);
        } else{
            if(user){
                //aggiungi dispositivo a utente
            }
            setHidden(true);
            setNameDevice('');
        }
        
    }
    return(
            <form onSubmit={(e)=> {e.preventDefault(); add();}} className="flex flex-col gap-2">
                <div className={`flex-col ${hidden ? "hidden": "flex"}`}>
                    <label className="text-sm">Device name:</label>
                    <input placeholder="Insert device's name" value={nameDevice} onChange={(e)=> {e.preventDefault(); setNameDevice(e.target.value)}}
                        type="text" className="text-sm p-1 mx-[-0.25rem] border-2 border-gray-300 rounded"/>
                </div>
                <button className='text-white py-1 px-2 bg-indigo-500 rounded text-sm' type="submit">Add device</button>
            </form>
    );
}