'use client'
import SideBar from '../layout/SideBar';
import SideBarBtn from '../layout/SideBarBtn';


export default function StageContainer({children}){

    return(
        <div className='flex w-full'>
                <article className='flex flex-row w-full my-4'>  
                    <section className='w-[20%] mx-8'>
                        <p className="text-2xl py-4 px-6">Admin Stage</p>
                        <SideBar>
                            <SideBarBtn dst={"/stage/rooms"} text={"Manage Rooms"}></SideBarBtn>
                            <SideBarBtn dst={"/stage/devices"} text={"Manage Devices"}></SideBarBtn>
                            <SideBarBtn dst={"/stage/users"} text={"Manage Users"}></SideBarBtn>
                            <SideBarBtn dst={"/"} text={"Logout"}></SideBarBtn>
                        </SideBar>
                    </section>
                    <section className='my-8 w-full h-full mx-4 mr-12'>
                        {children}
                            
                    </section>
                </article>
        </div>
    );
}