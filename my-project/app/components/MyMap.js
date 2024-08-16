'use client'
import { useEffect, useRef } from 'react';
import 'ol/ol.css';
import Map from 'ol/Map';
import View from 'ol/View';
import TileLayer from 'ol/layer/Tile';
import OSM from 'ol/source/OSM';

export default function MyMap({setMap1Object}){
const map1Container = useRef();
  // on component mount create the map and set the map refrences to the state
  useEffect(() => {
    const map1 = new Map({
      layers: [
        new TileLayer({
          source: new OSM(),
        }),
      ],
      view: new View({
        //Coordinate System: WGS 84 / Pseudo-Mercator-EPSG:3857
        //https://epsg.io/map
        center: [1263045.455384, 5542735.559814], // Longitude, Latitude
        zoom: 14
      }),
    });
    map1.setTarget(map1Container.current);
    setMap1Object(map1);
    // on component unmount remove the map refrences to avoid unexpected behaviour
    return () => {
      map1.setTarget(undefined);
      setMap1Object(null);
    };
  }, []);

  return (<div ref={map1Container} className="absolute inset-0"></div>);
};
