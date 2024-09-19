'use client';
import "leaflet/dist/leaflet.css";
import { MapContainer, TileLayer } from "react-leaflet";
import SelectablePolygon from './SelectablePolygon'


export default function MyMap({width, height, children}){


  const testPolygon = [
      [ 44.497088262070264, 11.356317361310317], 
      [44.49687101665043, 11.356342097919253], 
      [44.496881581253376, 11.356550789385466], 
      [ 44.497095830705774, 11.356535029682021], 
      [44.497088262070264, 11.356317361310317],
  ];
    

  return (
    <div className={`${width} ${height}`}>
    <MapContainer style={{ height: "100%"}} center={[44.49698630328074, 11.35632947953695]} zoom={18} maxZoom={25}>
      <TileLayer
          attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
          url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
          maxZoom={25}
      />
      
        <SelectablePolygon coords={testPolygon} name="room 1"></SelectablePolygon>

      </MapContainer>
    </div>
      );
};

