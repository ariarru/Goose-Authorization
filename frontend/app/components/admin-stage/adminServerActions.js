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

export async function deleteUser(userId){
    const supabase = createClient();
    const session = await supabase.auth.getSession();

    if(session){
        const result = await supabase.rpc("delete_user", {_user_id: userId});
        result.error ? console.log(result.error) : 0;
        return true;
    } else{
        return false;
    }
}

export async function addNewRoom(name, vertices, piano, restricted){
    const supabase = createClient();
    const session = await supabase.auth.getSession();

    if(session){
        if (vertices.length > 0 && vertices[0] !== vertices[vertices.length - 1]) {
            vertices.push(vertices[0]);
        }

        const geometry = {
            type: "Polygon",
            coordinates: [vertices] 
        };

        let data = {_piano: piano, _room_name: name, _vertices: geometry, _is_restricted: restricted};
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
        const {data, error} = await supabase.rpc("delete_room", {_room_id: roomId});
        console.log(error);
        return error ? error : true;
    } else{
        return null;
    }
}

export async function getAuthUsersFromRoomId(roomId){
    const supabase = createClient();
    const session = await supabase.auth.getSession();
    if(session){
        const result = await supabase.rpc("get_users_from_room_id", {id: roomId});
        result.error ? console.log(result.error) : 0;
    return result; 
    } else{
        return false;
    }
}

export async function getDevicesFromRoomId(roomId){
    const supabase = createClient();
    const session = await supabase.auth.getSession();
    if(session){
        const result = await supabase.rpc("get_devices_from_room_id", {id: roomId});
        result.error ? console.log(result.error) : 0;
        return result; 
    } else{
        return false;
    }
}

export async function addUserDevice(userId, deviceName){
    const supabase = createClient();
    const session = await supabase.auth.getSession();

    if(session){
        const result = await supabase.rpc();
        result.error ? console.log(result.error) : 0;
        return result;
    } else{
        return false;
    }
}


export async function getRooms(){
    const supabase = createClient();
    const session = await supabase.auth.getSession();
    if(session){
        const result = await supabase.rpc("get_rooms");
        result.error ? console.log(result.error) : 0;
        return result;
    } else{
        return false;
    } 
}

export async function deleteDevFromRoom(id, room){
    const supabase = createClient();
    const session = await supabase.auth.getSession();
    if(session){
        const {data, error} = await supabase.rpc("remove_dev_from_room", {dev_id:id, _room_id: room}); 
        console.log("error", error)
        return error ? error : true;
    } else{
        return false;
    }
}

export async function addDevFromRoom(devId, roomId){
    const supabase = createClient();
    const session = await supabase.auth.getSession();
    if(session){
        const {data, error} = await supabase.rpc("insert_room_sdevice", {_room_id : roomId, _device_s_id : devId}); 
        return error ? error : true;
    } else{
        return false;
    }
}


export async function deleteUserFromRoom(userId, roomId){
    const supabase = createClient();
    const session = await supabase.auth.getSession();
    if(session){
        const {data, error} = await supabase.rpc("remove_user_from_room", {user_id: userId, room_id: roomId }); 
        console.log(error)
        return error ? error : true;
    } else{
        return false;
    }
}

export async function addUserFromRoom(userId, roomId){
    const supabase = createClient();
    const session = await supabase.auth.getSession();
    if(session){
        const {data, error} = await supabase.rpc("insert_room_authorizations", {_room_id : roomId, _user_id : userId}); 
        return error ? error : true;
    } else{
        return false;
    }
}

export async function updateRoom(roomId, name, floor, restricted){
    const supabase = createClient();
    const session = await supabase.auth.getSession();
    if(session){
        const {data, error} = await supabase.rpc("update_room", {_room_id : roomId, new_room_name : name, new_piano : floor, new_is_restricted: restricted}); 
        return error ? error : true;
    } else{
        return false;
    }

}

export async function updateRoomStatus(roomId, restricted){
    const supabase = createClient();
    const session = await supabase.auth.getSession();
    if(session){
        const {data, error} = await supabase.rpc("update_room_restriction", {new_is_restricted: restricted, r_id : roomId}); 

        return error ? error : true;
    } else{
        return false;
    }
}

export async function updateRoomNotificationType(roomId, notificationType) {
    const supabase = createClient();
    const session = await supabase.auth.getSession();

    if(session){
        const {data, error} = await supabase.rpc("update_notif_preference", {
            _room_id: roomId, 
            _notification_type: notificationType
        });
        error ? console.log(error) : 0;
        return error ? false : true;
    } else{
        return null;
    }
}

//bonus
export async function getPositionPeople(){
    const supabase = createClient();
    const session = await supabase.auth.getSession();
    if(session){
        const {data, error} = await supabase.rpc("get_people_in_rooms"); 
        if(error){
            console.log("ERROR", error)
            return false;
        } else {
            return data;
        }
    } else{
        return false;
    }
}