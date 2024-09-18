import { createClient } from '@/app/utils/supabaseClient';
import Card from '../layout/Card'
import ManageRooms from './ManageRooms'
import { redirect } from 'next/navigation';

export default async function RoomsContainer(){
    const supabase = createClient();

    const session = async () => {await supabase.auth.getSession();}

    if (!session) {
      redirect("./");
    }
    const rooms = await supabase.from("Rooms").select();

    return(
        <Card>
            <ManageRooms roomsData={rooms.data}></ManageRooms>
        </Card>
    );
}