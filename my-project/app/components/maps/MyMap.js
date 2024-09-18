'use client'
import { useEffect, useRef } from 'react';
import 'ol/ol.css';
import Map from 'ol/Map';
import View from 'ol/View';
import TileLayer from 'ol/layer/Tile';
import OSM from 'ol/source/OSM';

export default function MyMap({setMyMapObject, lat, long, z, data}){

const myMapContainer = useRef();
  // on component mount create the map and set the map refrences to the state
  useEffect(() => {
    const myMap = new Map({
      layers: [
        new TileLayer({
          source: new OSM(),
        }), 
      ],
      view: new View({
        //Coordinate System: WGS 84 / Pseudo-Mercator-EPSG:3857
        //https://epsg.io/map
        center: [long, lat], // Longitude, Latitude
        zoom: z,
      }),
    });

    myMap.setTarget(myMapContainer.current);
    setMyMapObject(myMap);
    
    // on component unmount remove the map refrences to avoid unexpected behaviour
    return () => {
      myMap.setTarget(undefined);
      setMyMapObject(null);
    };
  }, []);
  
  function getCoord(e){
    console.log(e);
    console.log("long:"+e.coordinates[0]+"-lat:"+e.coordinates[1]);
  }
  

  return (
    <div ref={myMapContainer} className="absolute inset-0">

    </div>
      );
};


/*new VectorLayer({
          source: data,
          style: new Stroke({
            color: 'red',
          }),
          fill: new Fill({
            color: 'rgba(255, 0, 0, 0.25)',
          }),
        }),*/