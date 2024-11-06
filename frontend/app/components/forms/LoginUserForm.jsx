'use client'
import Card from "../layout/Card";
import { useState } from "react"

export default function LoginUserForm({fun}){

    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');

    function handleLogin(){
        if(username === ''){
            alert('please insert username');
        }
        if(password === ''){
            alert('please insert password');
        }
        fun(username, password);
    }

    return(
        <Card add="justify-center flex w-96 py-8">
            <form className='flex flex-col py-4 px-8 gap-2.5 w-full'>
                <label htmlFor='username' className='text-gray-500'>
                Insert your username:
                </label>
                <input
                value={username}
                type='text'
                id='username'
                placeholder="Should be in form: name.surname"
                onChange={e => setUsername(e.target.value)}
                className='shadow-sm shadow-slate-400 rounded-md leading-8 p-1'
                />

                <label htmlFor='password' className='text-gray-500'>
                Insert your password:
                </label>
                <input
                value={password}
                type='password'
                id='pw'
                placeholder="Insert your password"
                onChange={e => setPassword(e.target.value)}
                className='shadow-sm shadow-slate-400 rounded-md leading-8 p-1'
                />
                <div className='flex gap-2 justify-center'>
                <button
                    type='button'
                    onClick={handleLogin}
                    className='shadow-sm shadow-slate-300 bg-indigo-600 hover:bg-indigo-700 w-fit p-2 px-3 mt-3 -mb-3 mr-3 rounded text-white self-center'
                >
                    Login
                </button>
                
                </div>
            </form>
        </Card>
    )
}