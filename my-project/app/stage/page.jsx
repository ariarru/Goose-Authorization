import { redirect } from 'next/navigation';
import StageContainer from '../components/admin-stage/StageContainer'
import { createServer } from '../utils/supabaseServer';

export default async function StagePage(){
    const supabase = createServer();
    const {data, error} = await supabase.auth.getSession();

   if(!data.session){
        console.log(error);
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