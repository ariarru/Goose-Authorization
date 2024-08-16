'use client';
import { useEffect, useState } from "react";
import Synchronize from "ol-ext/interaction/Synchronize";
import MyMap from "./MyMap";

export default function MapContainer(){
    const [mapObject, setMapObject] = useState(null);
    useEffect(() => {
        if(!mapObject) return;
        var synchronize = new Synchronize({ maps: [mapObject] });
        mapObject.addInteraction( synchronize );
        return () => {
        if(mapObject) mapObject.removeInteraction(synchronize);
        }
    }, [mapObject]);

    return(
        <div className="flex h-[70vh] gap-[2px] bg-white/70 m-8" >
          <div className='relative w-[70vw] border border-transparent'>
            <MyMap setMap1Object={setMapObject}/>
          </div>
        </div>
    );
}