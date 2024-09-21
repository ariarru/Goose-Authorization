'use client';
import "leaflet/dist/leaflet.css";
import { MapContainer, TileLayer } from "react-leaflet";


export default function MyMap({width, height, zoom, children}){

  return (
    <div className={`${width} ${height}`}>
    <MapContainer style={{ height: "100%"}} center={[44.49698630328074, 11.35632947953695]} zoom={zoom ? zoom : 18} maxZoom={25}>
      <TileLayer
          attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
          url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
          maxZoom={25}
      />
      {children}
      </MapContainer>
    </div>
      );
};

