'use server';
import Card from './Card';
import RoomCardClient from './RoomCardClient';
import RoomCardServer from './RoomCardServer';

export default async function RoomCards({rm, children}){

    return(
        <Card add='border-2 border-gray-200 rounded p-2 h-fit w-fit'>
            <div className="flex flex-col gap-1">
                <div className="flex flex-row gap-2 items-center">
                    <p className='font-semibold w-fit text-lg uppercase'>{rm.room_name}</p>
                    <p className='text-gray-400 text-sm w-fit '> - {rm.room_id}</p>
                    { rm.piano ? (<p className='text-gray-400 text-sm w-fit '>- Floor: {rm.piano}</p>) : <p className='text-gray-400 text-sm w-fit '></p>
                }
                </div>
                
                
                <RoomCardServer roomId={rm.room_id} allDevs={children[1]}></RoomCardServer>
                <RoomCardClient id={rm.room_id}></RoomCardClient>
            </div>
            
        </Card>
    );
};