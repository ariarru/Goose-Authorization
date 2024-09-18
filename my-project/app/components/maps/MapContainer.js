//it doesnt work inside other client components
'use client';
import { useEffect, useState } from "react";
import Synchronize from "ol-ext/interaction/Synchronize";
import MyMap from "./MyMap";

export default function MapContainer({width, height, latitute, longitude, zoomIn, datas}){

    const [mapObject, setMapObject] = useState(null);
    useEffect(() => {
        if(!mapObject) return;
        var synchronize = new Synchronize({ maps: [mapObject] });
        mapObject.addInteraction( synchronize );
        return () => {
          if(mapObject) mapObject.removeInteraction(synchronize);
        }
    }, [mapObject]);

    if(datas){
      return(
        <div className={`flex gap-[2px] bg-white/70 m-8`} >
          <div className={`relative ${width} ${height} border border-transparent`}>
            <MyMap setMyMapObject={setMapObject} lat={latitute} long={longitude} z={zoomIn} data={datas} />
          </div>
        </div>
    );
    }
    

    return(
        <div className={`flex gap-[2px] bg-white/70 m-8`} >
          <div className={`relative ${width} ${height} border border-transparent`}>
            <MyMap setMyMapObject={setMapObject} lat={latitute} long={longitude} z={zoomIn} />
          </div>
        </div>
    );
}