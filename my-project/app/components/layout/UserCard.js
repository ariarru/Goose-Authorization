import Card from './Card';
import AddDevice from '../forms/AddDevice'


export default function UserCard({userInfo}){
    return(
        <Card add='border-2 border-gray-200 rounded p-2 h-fit'>
           <div className='flex flex-col gap-4'>
            <div className='flex flex-row gap-2 items-center'> 
                <svg xmlns="http://www.w3.org/2000/svg" width="0.88em" height="1em" viewBox="0 0 448 512">
                    <path fill="rgb(115 115 115)" d="M313.6 304c-28.7 0-42.5 16-89.6 16s-60.8-16-89.6-16C60.2 304 0 364.2 0 438.4V464c0 26.5 21.5 48 48 48h352c26.5 0 48-21.5 48-48v-25.6c0-74.2-60.2-134.4-134.4-134.4M400 464H48v-25.6c0-47.6 38.8-86.4 86.4-86.4c14.6 0 38.3 16 89.6 16c51.7 0 74.9-16 89.6-16c47.6 0 86.4 38.8 86.4 86.4zM224 288c79.5 0 144-64.5 144-144S303.5 0 224 0S80 64.5 80 144s64.5 144 144 144m0-240c52.9 0 96 43.1 96 96s-43.1 96-96 96s-96-43.1-96-96s43.1-96 96-96"/>
                </svg>
                <p className='font-bold'>{userInfo.username}</p>
                {userInfo.admin ? (<svg xmlns="http://www.w3.org/2000/svg" width="1em" height="1em" viewBox="0 0 24 24">
                        <path fill="#00a6fb" d="M12 12h7c-.53 4.11-3.28 7.78-7 8.92zH5V6.3l7-3.11M12 1L3 5v6c0 5.55 3.84 10.73 9 12c5.16-1.27 9-6.45 9-12V5z"/></svg>) 
                : (<p></p>)}
            </div>
           <p className='text-sm text-gray-500'>Associates devices:</p>
                <p>
                    {userInfo.devices?.map(dev => 
                        (<p>{dev}</p>))}
                </p>
                <AddDevice id={userInfo.id} room={false} user={true}></AddDevice>
            </div> 
        </Card>
    );

}