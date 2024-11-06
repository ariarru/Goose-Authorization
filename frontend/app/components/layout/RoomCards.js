'use server';
import Card from './Card';
import RoomCardClient from './RoomCardClient';
import RoomCardServer from './RoomCardServer';
import RoomCardEdit from './RoomCardEdit';

export default async function RoomCards({rm, children}){

    return(
        <Card add='border-2 border-gray-200 rounded p-2 h-fit w-fit'>
            <div className="flex flex-col gap-1">
                <RoomCardEdit roomInfo={rm}></RoomCardEdit>  
                <RoomCardServer roomId={rm.id} allDevs={children[0]} allUsers={children[1]}></RoomCardServer>
                <RoomCardClient id={rm.id}></RoomCardClient>
            </div>
            
        </Card>
    );
};