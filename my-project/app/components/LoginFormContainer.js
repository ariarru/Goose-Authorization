'use client'
import { createClient } from '../utils/supabaseClient';
import LoginForm from '../components/forms/Login-Form';


export default async function LoginFormContainer(){
    const supabase = createClient();

    const {
      data: { session }
    } = await supabase.auth.getSession();

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
        if(data){
            hasLogged=data;
        }
    }
    if(hasLogged){
        
    }
    return(
        <div>
            <LoginForm loginFunction={login}></LoginForm>
        </div>
    );
}