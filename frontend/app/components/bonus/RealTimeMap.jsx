import dynamic from "next/dynamic";
import { getPositionPeople, getRooms } from '../admin-stage/adminServerActions';
import PeoplePositions  from './PeoplePositions'
import SelectablePolygon from '../maps/SelectablePolygon';




export default async function RealTimeMap(){

    const rooms = await getRooms();
    const MyMap = dynamic(() => import('../maps/MyMap'), {ssr: false});

    return(
        <div className='text-right md:text-center content-center w-full h-full'>
            <MyMap width={"w-10/12 md:w-[70vw] h-[70vh] "} >
                {rooms?.data?.map( rm => (
                            <SelectablePolygon coords={rm.vertices} name={rm.room_name} key={rm.room_id}></SelectablePolygon>
                            
                        ))}
                <PeoplePositions></PeoplePositions>
            </MyMap>
        </div>
    )
}
    
