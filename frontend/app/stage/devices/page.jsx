import ManageDevices from '../../components/admin-stage/ManageDevices';
import StageContainer from '../../components/admin-stage/StageContainer'
import Card from '../../components/layout/Card';
import { createServer } from '@/app/utils/supabaseServer';


export default async function DeviceStage(){
    const supabase = createServer();
    const session = await supabase.auth.getSession();

    if(!session){
        redirect("./");
    } 

    const devices = await supabase.rpc("get_all_categories");

    console.log("devices", devices);

    return(
        <StageContainer>
            <Card>
                <ManageDevices>{devices.data}</ManageDevices>
            </Card>
        </StageContainer>
        
    );

}