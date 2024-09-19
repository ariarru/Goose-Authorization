'use client';
import { useState } from "react";
import { addNewUser } from "../admin-stage/adminServerActions";

export default function NewUsersForm(){

    const [username, setUsername] = useState();
    const [email, setEmail] = useState();
    const [firstPassword, setFirstPassword] = useState();
    const [isAdmin, setIsAdmin] = useState(false);

    async function sendData(){
        const result = await addNewUser(username, email, firstPassword, isAdmin);
  
        if(result){
            alert("User successfully added");
        } else {
            alert("Could not insert user");
        }

        setUsername('');
        setFirstPassword('');
        setEmail('');
        setIsAdmin(false);
    }

    const btnClasses ="my-2 p-2 rounded text-white bg-indigo-500 hover:bg-indigo-600";


    return(
        <div>
            <form onSubmit={e => {e.preventDefault(); sendData();}} className="flex flex-col gap-2 p-5 text-sm w-fit border-2 border-gray-200 rounded">
                <p className="text-gray-400">Create a new user</p>
                <label htmlFor="username"> Username: </label>
                <input className='border-2 border-gray-200 rounded m-[-3px]' type='text' id="username" 
                    onChange={e => setUsername(e.target.value)} value={username} ></input>

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
                
                <button type='submit' className={`${btnClasses}`}>Add User</button> 
            </form>
        </div>
    );
}