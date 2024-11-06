export default function Icon({classes, children}){

    classes += " w-fit h-fit hover:cursor-pointer";
    return(
        <div className={classes}>
            {children}
        </div>
    );
}