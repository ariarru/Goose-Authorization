import SideBar from '../components/layout/SideBar';
import SideBarBtn from '../components/layout/SideBarBtn';

export default function StagePage(){
    return(
        <main className='flex flex-row'>
            <section className='w-[20%] mx-8 my-4'>
                <p className="text-2xl py-4 px-6">Admin Stage</p>
                <SideBar>
                    <SideBarBtn text={"Manage Rooms"}></SideBarBtn>
                    <SideBarBtn text={"Manage Devices"}></SideBarBtn>
                    <SideBarBtn text={"Manage Users"}></SideBarBtn>
                </SideBar>
            </section>

        </main>
    );
}