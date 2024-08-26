'use client'
import { useEffect, useRef } from 'react';
import 'ol/ol.css';
import Map from 'ol/Map';
import View from 'ol/View';
import TileLayer from 'ol/layer/Tile';
import OSM from 'ol/source/OSM';

export default function MyMap({setMap1Object, lat, long, zoom, data}){

const map1Container = useRef();
  // on component mount create the map and set the map refrences to the state
  useEffect(() => {
    const map1 = new Map({
      layers: [
        new TileLayer({
          source: new OSM(),
        }),
        new VectorLayer({
          source: data,
          style: new Stroke({
            color: 'red',
          }),
          fill: new Fill({
            color: 'rgba(255, 0, 0, 0.25)',
          }),
        }),
      ],
      view: new View({
        //Coordinate System: WGS 84 / Pseudo-Mercator-EPSG:3857
        //https://epsg.io/map
        center: [long, lat], // Longitude, Latitude
        zoom: zoom
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
