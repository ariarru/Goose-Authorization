import { createClient } from "@/app/utils/supabaseClient";



export async function addNewUser(username, email, pw, isAdmin){
    const supabase = createClient();
    const session = await supabase.auth.getSession();

    if(session){
        const result = await supabase.rpc("insert_user", {_username: username, _email: email, _password: pw, _admin: isAdmin});
        result.error ? console.log(result.error) : 0;
        return result;
    } else{
        return null;
    }
}

export async function addNewRoom(name, vertices, piano){
    const supabase = createClient();
    const session = await supabase.auth.getSession();

    if(session){
        // Chiudi il poligono aggiungendo il primo vertice alla fine se non coincide già
        if (vertices.length > 0 && vertices[0] !== vertices[vertices.length - 1]) {
            vertices.push(vertices[0]);
        }

        // Crea il GeoJSON di tipo Polygon
        const geometry = {
            type: "Polygon",
            coordinates: [vertices] // Le coordinate di un poligono devono essere un array di array
        };

        let data = {_piano: piano, _room_name: name, _vertices: geometry};
        console.log(data);
        const result = await supabase.rpc("insert_room", data);
        console.log(result);

        return result;
    } else{
        return null;
    }
}

export async function deleteRoom(roomId){
    const supabase = createClient();
    const session = await supabase.auth.getSession();

    if(session){
        //const result = await supabase.rpc("insert_user", {_username: username, _email: email, _password: pw, _admin: isAdmin});
        //result.error ? console.log(result.error) : 0;
        //return result;
    } else{
        return null;
    }
}

export async function getAuthUsersFromRoomId(roomId){
    const supabase = createClient();
    const session = await supabase.auth.getSession();
    const result = await supabase.rpc("get_users_from_room_id", {id: roomId});
    result.error ? console.log(result.error) : 0;
    return result; 
}

export async function getDevicesFromRoomId(roomId){
    const supabase = createClient();
    //const session = await supabase.auth.getSession();
    const result = await supabase.rpc("get_devices_from_room_id", {id: roomId});
    result.error ? console.log(result.error) : 0;
    return result; 
}

export async function addUserDevice(userId, deviceName){
    const supabase = createClient();
    const session = await supabase.auth.getSession();

    if(session){
        const result = await supabase.rpc();
        result.error ? console.log(result.error) : 0;
        return result;
    } else{
        return null;
    }
}


