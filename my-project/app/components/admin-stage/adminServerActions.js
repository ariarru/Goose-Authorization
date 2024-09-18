import { createClient } from "@/app/utils/supabaseClient";



export async function addNewUser(username, email, pw, isAdmin){
    const supabase = createClient();
    const session = async () => {await supabase.auth.getSession();}

    if(session){
        const result = await supabase.rpc("insert_user", {_username: username, _email: email, _password: pw, _admin: isAdmin});
        result.error ? console.log(result.error) : 0;
        return result;
    } else{
        return null;
    }
}

export async function addNewRoom(){
    const supabase = createClient();
    const session = async () => {await supabase.auth.getSession();}

    if(session){
        //const result = await supabase.rpc("insert_user", {_username: username, _email: email, _password: pw, _admin: isAdmin});
        //result.error ? console.log(result.error) : 0;
        //return result;
    } else{
        return null;
    }
}