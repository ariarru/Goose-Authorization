'use client';
import { Popup } from "leaflet";
import { useEffect, useState } from "react";
import { Marker, useMapEvents } from "react-leaflet";

export default function LocationMarker() {
    const [position, setPosition] = useState(null);

    useMapEvents({
      click(e) {
        setPosition(e.latlng);
      },
    });
    
    return position === null ? null : (
      <Marker position={position}>
        <Popup>You are here</Popup>
      </Marker>
    )
  }