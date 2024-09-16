import Card from '../layout/Card';
import MapContainer from '../MapContainer';

export default function RoomCard({roomid, geoData, devices}){
    return(
        <Card className='flex flex-row'>
            <MapContainer>
            </MapContainer>
            <div className='flex flex-column p-2'>
                <p className='text-base text-black font-bold'>Room ${roomid}</p>
                {devices?.map( singleDevice => (
                    <div className='p-2 text-sm text-slate-600'>${singleDevice.name}</div>
                ))}
            </div>
        </Card>
    )
}