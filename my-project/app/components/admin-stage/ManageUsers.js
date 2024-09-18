import NewUsersForm from '../forms/NewUsersForm';
import UserCard from '../layout/UserCard';

export default function ManageUsers({children}){
    //vedi elenco utenti
    //aggiungi utente
    //elimina utente
    //gestisci accessi utente

    let thereIsData = (children?.length > 0);
 
    return(
    <div className="flex flex-col">
            <p className="text-3xl text-center text-gray-400 pt-2">Your users:</p>
            { thereIsData ? <p className="text-sm text-center text-gray-400 "></p> :
                <p className="text-sm text-center text-gray-400 pt-2">There are no data availables, please insert new values</p>
        
            }
           
           <section className='flex flex-row gap-8'>
                <section className="flex flex-col gap-4 mt-8">
                    <NewUsersForm></NewUsersForm>               
                </section>
                <section className='grid p-2 mt-6 grid-cols-4 gap-10'>
                    {children?.map(user => (
                    <UserCard userInfo={user} key={user.user_id}></UserCard>
                    ))}
                </section>
           </section>
            
            
        
    </div>);
}