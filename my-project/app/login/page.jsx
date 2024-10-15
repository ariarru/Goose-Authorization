'use server';
import { redirect } from 'next/navigation';
import LoginFormContainer from '../components/LoginFormContainer'
import { createServer } from '../utils/supabaseServer';


export default async function LoginPage(){

    const supabase = createServer();
    const {data, error} = await supabase.auth.getSession();
    if(data.session) {
      redirect("/stage");
    } else if(error){
        alert(error);
    }
    
    return(
        <main className='w-full p-8 flex vertical center items-center justify-items-center'>
            <LoginFormContainer></LoginFormContainer>
        </main>
    );

}