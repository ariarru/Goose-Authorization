'use server';
import { redirect } from 'next/navigation';
import LoginFormContainer from '../components/LoginFormContainer'
import { createClient } from '../utils/supabaseServer';

export default async function LoginPage(){

    const supabase = createClient();
    const {session} = await supabase.auth.getSession();

    if(session) {
      redirect("/stage");
    }
    
    return(
        <main className='w-full p-8 flex vertical center items-center justify-items-center'>
            <LoginFormContainer></LoginFormContainer>
        </main>
    );

}