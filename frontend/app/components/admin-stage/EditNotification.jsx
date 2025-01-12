'use client'
import { updateRoomNotificationType } from "../admin-stage/adminServerActions";

export default function EditNotification({currentType, id}) {

    const handle = async (formData) => {
        const newType = formData.get('notificationType');
        const res = await updateRoomNotificationType(id, newType);
        if(res == true){
            console.log("Successfully updated!")
            alert("Successfully updated!")
        }

    }
    
    return (
        <div className="flex flex-row gap-2 align-middle items-center mb-2">
        <p className="text-gray-400">Notification Type</p>
        <form action={handle}>
            <select 
                name="notificationType" 
                defaultValue={currentType}
                className="bg-gray-300 rounded px-2 py-1 text-sm text-gray-700"
            >
                <option value="popup">Popup</option>
                <option value="lights">Lights</option>
                <option value="sound">Sound</option>
            </select>
            <button 
                type="submit" 
                className={" ml-2 bg-sky-500 hover:bg-sky-600 text-white px-2 py-1 rounded text-sm"}
            >
                Update
            </button>
        </form>
    </div>
    )
}