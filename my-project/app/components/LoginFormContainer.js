'use client'
import { createClient } from '../utils/supabaseClient';
import LoginForm from '../components/forms/Login-Form';
import { useRouter } from 'next/navigation';


export default function LoginFormContainer(){
    const supabase = createClient();
    const router = useRouter();

        
    //questo teoricamente è solo per gli amministratori
    async function login(email, pw){
        const { data, error } = await supabase.auth.signInWithPassword({
            email: email,
            password: pw,
            options: {
              redirectTo: 'http://localhost:3000/auth/callback'
            }
          })

        if(data){
            console.log(data)
            alert("Welcome");
            const session = await supabase.auth.getSession();
            console.log(session);
            router.push('/stage');
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