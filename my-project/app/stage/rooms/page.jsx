import StageContainer from "../../components/admin-stage/StageContainer";
import RoomsContainer from "../../components/admin-stage/RoomsContainer";
import { createClient } from "@/app/utils/supabaseServer";

export default function RoomStage(){

    const supabase = createClient();
    const session = async () => {await supabase.auth.getSession();}

    supabase.rpc();

    return(
        <StageContainer>
            <RoomsContainer></RoomsContainer>
        </StageContainer>
        
    );
}