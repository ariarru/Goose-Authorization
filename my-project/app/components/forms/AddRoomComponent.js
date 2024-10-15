'use client';

import { useState } from "react";
import Card from "../layout/Card";
import ClickOnMapEvent from '../maps/ClickOnMapEvent';
import dynamic from "next/dynamic";
import { CircleMarker } from "react-leaflet";
import { addNewRoom } from "../admin-stage/adminServerActions";
import { useRouter } from "next/navigation";

const MyMap = dynamic(() => import('../maps/MyMap'), {
        ssr: false,
        loading: () => <p>Loading map...</p>
    });

export default function AddRoom(){

    const router =useRouter();
    const [hidden, setHidden] = useState(true);
    const [name, setName] = useState();
    const [floor, setFloor] = useState();
    const [vertices, setVertices] = useState([]);

    const handleMapClick = (newCoordinate) => {
        setVertices([...vertices, newCoordinate]);
    };

    async function add(){
        if(!floor){
            alert("Please insert floor")
        }
        if(!name){
            alert("Please insert the room's name")
        }
        const result = await addNewRoom(name, vertices, floor);
        if(!result.error){
            alert("Room successfully added");
            router.refresh();
        } else{
            alert("Could not add new room");
        }
        setName('');
        setFloor('');
        setVertices([]);
        setHidden(true);
    }

    return(
        <div>
            <div className={`flex relative w-[32rem]`}>
               <Card add="w-fit border-2 border-indigo-100">
                    <form className="flex flex-col gap-2 text-left" onSubmit={(e)=> {e.preventDefault(); add();}}>
                        <p className="text-base text-gray-600 text-center">Compile form to insert a new room</p>
                        <div className="flex flex-row gap-4 items-center">
                            <label htmlFor="name" >Room's name:</label>
                            <input id="name" type="text" value={name} className="border-2 rounded border-gray-200" 
                                placeholder="Insert room's name" onChange={(e)=> {e.preventDefault(); setName(e.target.value);}}/>
                        </div>
                        <div className="flex flex-row gap-4 items-center">
                            <label htmlFor="floor" >Floor:</label>
                            <input id="floor" type="text" value={floor} className="border-2 rounded border-gray-200" 
                                placeholder="Insert floor's number" onChange={(e)=> {e.preventDefault(); setFloor(e.target.value);}}/>
                        </div>
                        
                        <p className="text-sm text-gray-400">To select vertices, please click on the map below</p>
                        <label>Vertices:</label>
                           <ol>
                                {vertices.map((vertex, index) => (
                                        <li key={index}>Lat: {vertex[0]}, Lng: {vertex[1]}</li>
                                    ))}
                            </ol> 
                        <button onClick={(e) => {e.preventDefault(); setHidden(!hidden);}} className="w-fit bg-indigo-300 rounded p-2 text-white text-sm">show map</button>
                        <MyMap width={"w-96"} height={`${hidden ? 'h-0' : 'h-48'}`} zoom={18}>
                            <ClickOnMapEvent onMapClick={handleMapClick}/>
                            {vertices.map((vertex, index) => (
                                        <CircleMarker center={vertex} key={index} pathOptions={{ fillColor: 'blue' }} radius={3}/>
                                    ))};
                        </MyMap>
                        <button type="submit" className="my-0 p-2 rounded text-white bg-indigo-500 hover:bg-indigo-600 w-64 text-center self-center" >Add New Room</button>
                    </form>
               </Card>
            </div>

        </div>
    );
}