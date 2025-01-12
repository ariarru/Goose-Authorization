'use client'
import Card from './Card';
import { deleteUser } from '../admin-stage/adminServerActions';
import { useRouter } from 'next/navigation';


export default function UserCard({userInfo}){

    const router = useRouter();

    async function delete_user(){
        const result = await deleteUser(userInfo.user_id);
        if(result){
            alert("User sucessfully removed");
            window.location.reload();
        } else {
            alert("User not removed due to error");
        }
    }

    console.log(userInfo);
    return(
        <Card add='border-2 border-gray-200 rounded p-2 h-fit w-fit'>
           <div className='flex flex-col gap-4 w-fit'>
            <div className='flex flex-row gap-2 items-center'> 
                <svg xmlns="http://www.w3.org/2000/svg" width="0.88em" height="1em" viewBox="0 0 448 512">
                    <path fill="rgb(115 115 115)" d="M313.6 304c-28.7 0-42.5 16-89.6 16s-60.8-16-89.6-16C60.2 304 0 364.2 0 438.4V464c0 26.5 21.5 48 48 48h352c26.5 0 48-21.5 48-48v-25.6c0-74.2-60.2-134.4-134.4-134.4M400 464H48v-25.6c0-47.6 38.8-86.4 86.4-86.4c14.6 0 38.3 16 89.6 16c51.7 0 74.9-16 89.6-16c47.6 0 86.4 38.8 86.4 86.4zM224 288c79.5 0 144-64.5 144-144S303.5 0 224 0S80 64.5 80 144s64.5 144 144 144m0-240c52.9 0 96 43.1 96 96s-43.1 96-96 96s-96-43.1-96-96s43.1-96 96-96"/>
                </svg>
                <p className='font-bold'>{userInfo.username}</p>
            </div>
            <div className='flex flex-row gap-2 items-center w-fit'> 
                <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 16 16"><path fill="currentColor" fill-rule="evenodd" d="M14.95 3.684L8.637 8.912a1 1 0 0 1-1.276 0l-6.31-5.228A1 1 0 0 0 1 4v8a1 1 0 0 0 1 1h12a1 1 0 0 0 1-1V4a1 1 0 0 0-.05-.316M2 2h12a2 2 0 0 1 2 2v8a2 2 0 0 1-2 2H2a2 2 0 0 1-2-2V4a2 2 0 0 1 2-2m-.21 1l5.576 4.603a1 1 0 0 0 1.27.003L14.268 3z"/></svg>
                <p className='text-gray-500 text-sm'>{userInfo.email}</p>
            </div>
            <button className='rounded bg-rose-400 text-white px-4 py-1 hover:bg-red-600' onClick={(e)=>{e.preventDefault; delete_user();}}>Delete user</button>
        </div>
        </Card>
    );

}