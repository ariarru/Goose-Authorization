'use client'
import { createClient } from '../utils/supabaseClient';
import LoginForm from '../components/forms/Login-Form';
import { redirect, useRouter } from 'next/navigation';


export default function LoginFormContainer(){
    const supabase = createClient();
    const router = useRouter();

    const session = async () => {await supabase.auth.getSession();}

    if (session) {
      redirect("/stage");
    }
    var hasLogged = false;
    //questo teoricamente è solo per gli amministratori
    async function login(email, pw){
        const { data, error } = await supabase.auth.signInWithPassword({
            email: email,
            password: pw,
            options: {
              redirectTo: 'http://localhost:3000/auth/callback'
            }
          })

          console.log(data);
        if(data){
            alert("Welcome");
            router.refresh();
        }
        if(error){
          alert("Wrong credentials, please retry\n"+error);
          return;
        }
        
    }
    
    return(
        <div className='mx-auto'>
            <LoginForm loginFunction={login}></LoginForm>
        </div>
    );
}