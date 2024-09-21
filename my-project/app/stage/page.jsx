import { redirect } from 'next/navigation';
import StageContainer from '../components/admin-stage/StageContainer'
import { createClient } from '../utils/supabaseClient';

export default async function StagePage(){
    const supabase = createClient();
    const {session} = await supabase.auth.getSession();
    console.log(session);
    
    if(!session){
        redirect("./");
    } 

    return(
        <main className='flex flex-row'>
            <StageContainer >
                <div className='text-center content-center w-full h-full text-gray-500'> 
                            Please select a division from the sidebar 
                        </div>
            </StageContainer>
        </main>
    );
}