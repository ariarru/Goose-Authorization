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


export async function getRooms(){
    const supabase = createClient();
    const result = await supabase.rpc("get_rooms");
    result.error ? console.log(result.error) : 0;
    return result; 
}



export default async function deleteDevFromRoom(id){
    console.log("im' hereee")
    const supabase = createClient();
    const {data, error} = await supabase.rpc("delete_dev_device", {d_id:id}); 
    console.log(data);
    return error ? error : true;
}

export async function addDevFromRoom(devId, roomId){
    console.log("room", roomId)
    console.log("dev", devId)
    const supabase = createClient();
    const {data, error} = await supabase.rpc("insert_room_sdevice", {_room_id : roomId, _device_s_id : devId}); 
    console.log("room", roomId)
    console.log(data);
    console.log(error);
    return error ? error : true;
}