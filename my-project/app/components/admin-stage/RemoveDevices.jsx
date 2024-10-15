'use client'
import { useRouter } from "next/navigation";
import deleteDevFromRoom from "../admin-stage/adminServerActions";


export default function RemoveDevices(id){

    const router = useRouter();

    async function del(){
        const result = deleteDevFromRoom(id)
        console.log(result);
        if(result){
            alert("Room deleted");
            router.refresh();
        } else {
            alert("Could not delete room");
        }
    }

    return(
        <form onSubmit={(e)=> {e.preventDefault; del;}}>
            <button className=" hover:rounded hover:cursor p-1" type="submit">
                <svg xmlns="http://www.w3.org/2000/svg" width="1em" height="1em" viewBox="0 0 16 16">
                    <path className="hover:text-gray-900 text-[#8E96A4]" fill="currentColor" fillRule="evenodd" d="M9 2H7a.5.5 0 0 0-.5.5V3h3v-.5A.5.5 0 0 0 9 2m2 1v-.5a2 2 0 0 0-2-2H7a2 2 0 0 0-2 2V3H2.251a.75.75 0 0 0 0 1.5h.312l.317 7.625A3 3 0 0 0 5.878 15h4.245a3 3 0 0 0 2.997-2.875l.318-7.625h.312a.75.75 0 0 0 0-1.5zm.936 1.5H4.064l.315 7.562A1.5 1.5 0 0 0 5.878 13.5h4.245a1.5 1.5 0 0 0 1.498-1.438zm-6.186 2v5a.75.75 0 0 0 1.5 0v-5a.75.75 0 0 0-1.5 0m3.75-.75a.75.75 0 0 1 .75.75v5a.75.75 0 0 1-1.5 0v-5a.75.75 0 0 1 .75-.75" clipRule="evenodd"/>
                </svg>
            </button>
        </form>
    );
}