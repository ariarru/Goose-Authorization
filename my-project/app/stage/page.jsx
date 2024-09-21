import { redirect } from 'next/navigation';
import StageContainer from '../components/admin-stage/StageContainer'
import { createClient } from '../utils/supabaseClient';

export default async function StagePage(){
    console.log('IN HERE');
    const supabase = createClient();
    const {user} = await supabase.auth.getUser();
    console.log("session");
    console.log(user);

   /*if(!user){
       redirect("./");
    } */

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