'use client';
import { useEffect, useRef } from 'react';
import 'ol/ol.css';
import Map from 'ol/Map';
import View from 'ol/View';
import TileLayer from 'ol/layer/Tile';
import OSM from 'ol/source/OSM';
import { fromLonLat } from 'ol/proj'; // Aggiungiamo questa per la conversione delle coordinate

export default function ClickMap({setClickMapObject, lat, long, z, data}){

  const clickMapContainer = useRef();
  
  useEffect(() => {
    const clickMap = new Map({
      target: clickMapContainer.current,
      layers: [
        new TileLayer({
          source: new OSM(),
        }), 
      ],
      view: new View({
        center: fromLonLat([long, lat]), // Conversione delle coordinate da longitudine e latitudine
        zoom: z,
      }),
    });

    setClickMapObject(clickMap);

    // Aggiungiamo il listener per l'evento di click
    clickMap.on('singleclick', function (evt) {
      const clickedCoord = evt.coordinate;
      console.log('Coordinate:', clickedCoord);
    });

    // Pulizia all'unmount del componente
    return () => {
      clickMap.setTarget(undefined);
      setClickMapObject(null);
    };
  }, []);

  return (
    <div ref={clickMapContainer} className="absolute inset-0 w-full h-full"></div>
  );
};
