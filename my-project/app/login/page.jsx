'use server';
import { redirect } from 'next/navigation';
import LoginFormContainer from '../components/LoginFormContainer'
import { createClient } from '../utils/supabaseClient';


export default async function LoginPage(){

    const supabase = createClient();
    const {user} = await supabase.auth.getUser();
    console.log("login session:");
    console.log(user);
    if(user) {
      redirect("/stage");
    }
    
    return(
        <main className='w-full p-8 flex vertical center items-center justify-items-center'>
            <LoginFormContainer></LoginFormContainer>
        </main>
    );

}