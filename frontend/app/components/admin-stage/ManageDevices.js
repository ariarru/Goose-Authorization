import Card from "../layout/Card";
import HandleDevices from "./HandleDevices";

export default function ManageDevices({data}){

    return(
    <div>
        <p className="text-3xl text-center text-gray-400 pt-2">Your devices:</p>
            {data?.length > 0  ? <p></p>: 
            (<p className="text-sm text-center text-gray-400 pt-2">There are no data availables, please insert new values</p>)}
            <div className="flex flex-row gap-3 w-fit mt-2">
                <HandleDevices initialDevices={data || []} />
            </div>
    </div>);
}