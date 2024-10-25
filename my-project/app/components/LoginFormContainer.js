'use client'
import { createClient } from '../utils/supabaseClient';
import LoginForm from '../components/forms/Login-Form';
import { useRouter } from 'next/navigation';
import LoginUserForm from './forms/LoginUserForm';


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

    async function loginUser(username, pw){
      const {data, error} = await supabase.rpc('login', {_username: username, _password: pw});

      console.log(data);
      if(!error){
        router.push('./user');
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