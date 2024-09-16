import NewUsersForm from './NewUsersForm';


export default function ManageUsers({children}){
    //vedi elenco utenti
    //aggiungi utente
    //elimina utente
    //gestisci accessi utente


    return(
    <div className="flex flex-col">
            <p className="text-3xl text-center text-gray-400 pt-2">Your users:</p>
            {children.lenght > 0  ? children : 
            (<p className="text-sm text-center text-gray-400 pt-2">There are no data availables, please insert new values</p>)}
           
           <section>
            <section className="flex flex-col gap-4 mt-8 w-32">
                <NewUsersForm></NewUsersForm>               
            </section>
            <section>
                {children.map(user => (
                <p> {user.username} </p>
                ))}
            </section>
           </section>
            
            
        
    </div>);
}