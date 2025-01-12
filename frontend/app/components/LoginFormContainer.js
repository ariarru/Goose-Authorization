'use client'
import { createClient } from '../utils/supabaseClient';
import LoginForm from '../components/forms/Login-Form';
import { useRouter } from 'next/navigation';


export default function LoginFormContainer(){
    const supabase = createClient();
    const router = useRouter();

        
    async function login(email, pw){
        const { data, error } = await supabase.auth.signInWithPassword({
            email: email,
            password: pw,
            options: {
              redirectTo: 'http://localhost:3000/auth/callback'
            }
          })

        if(data){
            alert("Welcome");
            router.push('/stage');
        }
        if(error){
          alert("There has been an error, please retry\n"+error);
          return;
        }
    }

    return(
        <div className='mx-auto h-full flex flex-col items-center gap-10 mt-8 md:flex-row'>
            <section className='flex flex-col'>
              <p className='text-3xl font-bold mx-auto mb-2  text-gray-600'>Login as Admin</p>
              <LoginForm loginFunction={login}></LoginForm>
            </section>
        </div>
    );
}