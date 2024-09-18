'use client'
import "leaflet/dist/leaflet.css";
import { MapContainer, Polygon, Polyline, SVGOverlay, TileLayer } from "react-leaflet";

export default function MyMap({width, height, children}){

  // Impedisce il rendering su SSR
  if (typeof window === 'undefined') {
    return null; // Evita l'errore, il codice non verrà eseguito lato server
  }
  children.forEach(room => {
    console.log(room);
    console.log(room.vertices.coordinates[0]);
  })

  const testPolygon = [
      [ 44.497088262070264, 11.356317361310317], 
      [44.49687101665043, 11.356342097919253], 
      [44.496881581253376, 11.356550789385466], 
      [ 44.497095830705774, 11.356535029682021], 
      [44.497088262070264, 11.356317361310317],
  ];
  
  const purpleOptions = { color: 'purple'};
  

  return (
    <div className={`${width} ${height}`}>
    <MapContainer style={{ height: "100%"}} center={[44.49698630328074, 11.35632947953695]} zoom={18} maxZoom={25}>
      <TileLayer
          attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
          url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
          maxZoom={25}
      />

  <Polygon pathOptions={purpleOptions} positions={testPolygon}/>
    <SVGOverlay attributes={purpleOptions} bounds={testPolygon}>
      <text x="20%" y="50%" >text </text>
    </SVGOverlay>


      {/*<LocationMarker/>}
      {
          children.forEach(room => {
            let color = 0;
            if(room.)
            <Polygon pathOptions={purpleOptions} positions={testPolygon}/>
            <SVGOverlay attributes={purpleOptions} bounds={room.vertices.coordinates[0]} key={room.room_id}>
              <text x="20%" y="50%" > {room.name} </text>
            </SVGOverlay>
            
          }) */
      }
      </MapContainer>
    </div>
      );
};

