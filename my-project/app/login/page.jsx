'use server';
import LoginFormContainer from '../components/LoginFormContainer'

export default async function LoginPage(){
    
    return(
        <main className='w-full p-8 flex vertical center items-center justify-items-center'>
            <LoginFormContainer></LoginFormContainer>
        </main>
    );

}