import { createServer } from '@/app/utils/supabaseServer';
import NewUsersForm from '../forms/NewUsersForm';
import UserCard from '../layout/UserCard';
import { redirect } from 'next/navigation';

export default async function ManageUsers({children}){
    //TODO: aggiungi dispositivo utente

    const supabase = createServer();

    const {data, error} = await supabase.auth.getSession();

    if (!data.session) {
        console.log(error);
        redirect("./");
    }

    let thereIsData = (children?.length > 0);
 
    return(
    <div className="flex flex-col">
            <p className="text-3xl text-center text-gray-400 pt-2">Your users:</p>
            { thereIsData ? <p className="text-sm text-center text-gray-400 "></p> :
                <p className="text-sm text-center text-gray-400 pt-2">There are no data availables, please insert new values</p>
        
            }
           
           <section className='flex flex-row gap-8 w-fit'>
                <section className="flex flex-col gap-4 mt-8">
                    <NewUsersForm></NewUsersForm>               
                </section>
                <section className='flex flex-col md:grid p-2 mt-6 md:grid-cols-4 md:gap-10'>
                    {children?.map(user => (
                    <UserCard userInfo={user} key={user.user_id}></UserCard>
                    ))}
                </section>
           </section>
            
            
        
    </div>);
}