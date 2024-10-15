'use server';
import Card from './Card';
import RoomCardClient from './RoomCardClient';
import RoomCardServer from './RoomCardServer';

export default async function RoomCards({rm, children}){

    return(
        <Card add='border-2 border-gray-200 rounded p-2 h-fit w-fit'>
            <div className="flex flex-col gap-1">
                <div className="flex flex-row gap-2 items-center">
                    <p className='font-semibold w-fit text-lg uppercase'>{rm.name}</p>
                    <p className='text-gray-400 text-sm w-fit whitespace-nowrap'> - {rm.id}</p>
                    { rm.floor ? (<p className='text-gray-400 text-sm w-fit whitespace-nowrap'>- Floor: {rm.floor}</p>) : <p className='text-gray-400 text-sm w-fit '></p>
                }
                </div>
                
                
                <RoomCardServer roomId={rm.id} allDevs={children[0]} allUsers={children[1]}></RoomCardServer>
                <RoomCardClient id={rm.id}></RoomCardClient>
            </div>
            
        </Card>
    );
};