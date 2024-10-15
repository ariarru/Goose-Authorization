'use server'
import ManageUsers from '../../components/admin-stage/ManageUsers'
import StageContainer from '../../components/admin-stage/StageContainer'
import Card from '../../components/layout/Card'
import { createServer } from '@/app/utils/supabaseServer';



export default async function UserStage(){
    const supabase = createServer();

    const {data, error} = await supabase.rpc("get_all_users");
    
    if(error){
        console.log(error);
    }


    return(
        <StageContainer>
            <Card>
                <ManageUsers>
                    {data}
                </ManageUsers>
            </Card>
        </StageContainer>
        )
}