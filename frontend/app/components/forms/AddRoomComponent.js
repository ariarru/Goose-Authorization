'use client';

import { useState } from "react";
import Card from "../layout/Card";
import ClickOnMapEvent from '../maps/ClickOnMapEvent';
import dynamic from "next/dynamic";
import { CircleMarker } from "react-leaflet";
import { addNewRoom, addNewRoomFromJson } from "../admin-stage/adminServerActions";
import { useRouter } from "next/navigation";



export default function AddRoom(){
    const router = useRouter();
    const [hidden, setHidden] = useState(true);
    const [name, setName] = useState('');
    const [floor, setFloor] = useState('');
    const [vertices, setVertices] = useState([]);
    const [isRestricted, setIsRestricted] = useState(false);
    const [mapText, setMapText] = useState("Show Map");
    const [jsonFile, setJsonFile] = useState(null);

    const handleMapClick = (newCoordinate) => {
        setVertices([...vertices, newCoordinate]);
    };

    const handleJsonFileChange = (e) => {
        const file = e.target.files[0];
        if (file) {
            const reader = new FileReader();
            reader.onload = (event) => {
                try {
                    const jsonData = JSON.parse(event.target.result);
                    setJsonFile(jsonData);
                } catch (error) {
                    alert('Invalid JSON file');
                    setJsonFile(null);
                }
            };
            reader.readAsText(file);
        }
    };

    async function add(){
        
        if(!floor){
            alert("Please insert floor")
        }
        if(!name){
            alert("Please insert the room&#39;s name")
        }
        let result = null;
        if(jsonFile){
            result = await addNewRoomFromJson(name, vertices, floor, isRestricted, jsonFile);
        } else {
            result = await addNewRoom(name, vertices, floor, isRestricted);
        }
        if(!result.error){
            alert("Room successfully added");
            router.refresh();
        } else{
            alert("Could not add new room");
        }
        setName('');
        setFloor('');
        setVertices([]);
        setIsRestricted(false);
        setHidden(true);
        setJsonFile(null);
    }


    const MyMap = dynamic(() => import('../maps/MyMap'), {ssr: false});

    return(
            <div className={`contents w-full`}>
               <Card add="border-2 border-indigo-100">
                    <form className="flex flex-col gap-2" onSubmit={(e)=> {e.preventDefault(); add();}}>
                        <div className="flex flex-col md:flex-row gap-4">
                            <div className="flex flex-col gap-2 text-left w-fit">
                                <p className="text-base text-gray-600 text-center">Compile form to insert a new room</p>
                                    <label htmlFor="name" className="text-sm max-w-40" >Room&#39;s name:</label>
                                    <input id="name" type="text" value={name} className="border-2 rounded border-gray-200 px-1" 
                                        placeholder="Insert room's name" onChange={(e)=> {e.preventDefault(); setName(e.target.value);}}/>
                                    <label htmlFor="floor" className="text-sm" >Floor:</label>
                                    <input id="floor" type="text" value={floor} className="border-2 rounded border-gray-200 px-1" 
                                        placeholder="Insert floor's number" onChange={(e)=> {e.preventDefault(); setFloor(e.target.value);}}/>
                                <div className="flex flex-row gap-4 items-center">
                                    <label htmlFor="restr" >Restricted access:</label>
                                    <input id="restr" type="checkbox" value={isRestricted} checked={isRestricted} className="border-2 rounded border-gray-200 px-1" 
                                        onChange={(e)=> {e.preventDefault(); setIsRestricted(!isRestricted);}}/>
                                </div>
                                
                                    <div className="flex flex-col gap-2">
                                        <label htmlFor="jsonUpload" className="text-sm">Upload JSON Configuration:</label>
                                        <input 
                                            id="jsonUpload" 
                                            type="file" 
                                            accept=".json"
                                            className="border-2 rounded border-gray-200 px-1"
                                            onChange={(e)=> {e.preventDefault(); handleJsonFileChange(e);}}
                                            disabled={!isRestricted}
                                        />
                                        {jsonFile && (
                                            <p className="text-xs text-gray-600">
                                                Uploaded: {jsonFile.name}
                                            </p>
                                        )}
                                    </div>
                            
                            </div>
                            <div className="inline-block h-[250px] min-h-[1em] w-0.5 self-stretch bg-neutral-100 opacity-100 dark:opacity-50">

                            </div>
                            <div className="flex flex-row gap-4 text-left max-w-1/2">
                                <section className="flex flex-col gap-2 text-left max-w-1/2 mt-6">
                                    <label className="text-sm">Vertices:</label>
                                    <p className="text-xs text-gray-400">To select vertices, please click on the map below</p>
                                    <ol>
                                            {vertices.map((vertex, index) => (
                                                    <li key={`${vertex[0]}-${vertex[1]}`}>Lat: {vertex[0]}, Lng: {vertex[1]}</li>
                                                ))}
                                        </ol> 

                                </section>
                                <div className="inline-block h-[250px] min-h-[1em] w-0.5 self-stretch bg-neutral-100 opacity-100 dark:opacity-50">

                                </div>
                                <section className="flex flex-col gap-2 text-left max-w-1/2">
                                    <button onClick={(e) => {e.preventDefault(); 
                                                            setHidden(!hidden); 
                                                            hidden ? setMapText("Hide Map") :setMapText("Show Map");}} 
                                        className="w-fit bg-indigo-300 rounded p-2 text-white text-sm">{mapText}</button>
                                    <MyMap width={"w-96"} height={`${hidden ? 'h-0' : 'h-48'}`} zoom={18}>
                                        <ClickOnMapEvent onMapClick={handleMapClick}/>
                                        {vertices.map((vertex, index) => (
                                                    <CircleMarker center={vertex} key={index} pathOptions={{ fillColor: 'blue' }} radius={3}/>
                                                ))};
                                    </MyMap>
                                </section>
                                
                            </div>
                        </div>    
                        <button type="submit" className="my-0 p-2 rounded text-white bg-indigo-500 hover:bg-indigo-600 w-64 text-center self-center" >Add New Room</button>
                    </form>
               </Card>
            </div>
    );
}