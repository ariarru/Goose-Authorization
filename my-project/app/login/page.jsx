import LoginForm from '../components/forms/Login-Form';

export default function LoginPage(){
    //questo teoricamente è solo per gli amministratori
    return(
        <main className='w-full p-8 flex vertical center items-center justify-items-center'>
            <LoginForm></LoginForm>
        </main>
    );

}