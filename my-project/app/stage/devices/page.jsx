import { createClient } from '@/app/utils/supabaseClient';
import ManageDevices from '../../components/admin-stage/ManageDevices';
import StageContainer from '../../components/admin-stage/StageContainer'
import Card from '../../components/layout/Card';

export default async function DeviceStage(){
    const supabase = createClient();
    const session = async () => {await supabase.auth.getSession();}

    if(!session){
        redirect("./");
    } 

    const devices = await supabase.rpc("get_all_categories");
    const devicesForRooms = await supabase.rpc("get_device_categories_each_room");

    console.log(devicesForRooms);

    return(
        <StageContainer>
            <Card>
                <ManageDevices>{devices.data}</ManageDevices>
            </Card>
        </StageContainer>
        
    );

}