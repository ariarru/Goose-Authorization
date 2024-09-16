export default function ManageDevices({children}){
    return(
    <div>
        <p className="text-3xl text-center text-gray-400 pt-2">Your devices:</p>
            {children?.lenght > 0  ? children : 
            (<p className="text-sm text-center text-gray-400 pt-2">There are no data availables, please insert new values</p>)}
        {children}
    </div>);
}