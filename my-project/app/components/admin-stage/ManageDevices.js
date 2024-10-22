import Card from "../layout/Card";

export default function ManageDevices({children}){
    console.log('children', children);
    return(
    <div>
        <p className="text-3xl text-center text-gray-400 pt-2">Your devices:</p>

            {children?.length > 0  ? <p></p>: 
            (<p className="text-sm text-center text-gray-400 pt-2">There are no data availables, please insert new values</p>)}
        <div className="flex flex-row gap-3 mx-auto w-fit mt-2 ">
            {children.map((d) => (
                    <Card key={d.category} >
                       <p className="font-bold">{d.category}</p> 
                    </Card>
                )) }
        </div>
    </div>);
}