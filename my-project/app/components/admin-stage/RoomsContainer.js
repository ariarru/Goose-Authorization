import { createClient } from '@/app/utils/supabaseClient';
import Card from '../layout/Card'
import ManageRooms from './ManageRooms'
import { redirect } from 'next/navigation';
import VectorSource from 'ol/source/Vector';
import GeoJSON from 'ol/format/GeoJSON';

export default async function RoomsContainer(){
    const supabase = createClient();

    const session = async () => {await supabase.auth.getSession();}

    if (!session) {
      redirect("./");
    }

   //const {data, error} = await supabase.rpc();
   const roomsData = new VectorSource({
    features: new GeoJSON({
        dataProjection: 'EPSG:4326',
        featureProjection: 'EPSG:3857',
    }).readFeatures(JSON.stringify(data)),
   });
    return(
        <Card>
            <ManageRooms></ManageRooms>
        </Card>
    );
}