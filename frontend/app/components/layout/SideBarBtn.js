import Link from "next/link";

export default function SideBarBtn({text, dst}){
    return(
        <Link href={`${dst}`} 
        className="p-2 hover:bg-sky-600 hover:text-white hover:cursor-pointer rounded">
            <button >
                {text}
            </button>
        </Link>
        
)};