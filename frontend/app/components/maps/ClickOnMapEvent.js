import { useMapEvent } from "react-leaflet";


export default function ClickOnMapEvent({onMapClick}){
    useMapEvent('click', (event) => {
        const { lat, lng } = event.latlng;
        onMapClick([lat, lng]); 
      });
    
    return null;
}