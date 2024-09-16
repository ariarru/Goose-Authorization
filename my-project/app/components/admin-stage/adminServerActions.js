import { createClient } from "@/app/utils/supabaseClient";



export async function addNewUser(username, email, pw, isAdmin){
    const supabase = createClient();
    const {
        data: { session }
    } = await supabase.auth.getSession();

    if(session){
        //chiama function per inserire l'utente
    } else{
        return null;
    }
}