'use client'
import { Polygon, Popup, SVGOverlay, useMapEvent } from "react-leaflet";
import { useState } from "react";

// Componente che gestisce il poligono e il click
export default function SelectablePolygon({ coords, name, id }) {
  const [isSelected, setIsSelected] = useState(false);

  useMapEvent('click', (event) => {
    const layerPoint = event.latlng;
    
    const insidePolygon = coords.some(point => {
      return Math.abs(point[0] - layerPoint.lat) < 0.0001 && Math.abs(point[1] - layerPoint.lng) < 0.0001;
    });

    if (insidePolygon) {
      setIsSelected(!isSelected);  

    }
  });


  const polygonOptions = { color: isSelected ? "blue" : "purple" };

  return isSelected ? (
    <Polygon pathOptions={polygonOptions} positions={coords} >
            <Popup>{name} - {id}</Popup>
        </Polygon>
  ) :
  (
    <Polygon pathOptions={polygonOptions} positions={coords} >
         <SVGOverlay attributes={polygonOptions} bounds={coords}>
            <text x="10%" y="50%" >{name} </text>
        </SVGOverlay>
    </Polygon>
  );
}
