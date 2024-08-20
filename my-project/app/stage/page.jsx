import StageContainer from '../components/admin-stage/StageContainer'

export default function StagePage(){

    return(
        <main className='flex flex-row'>
            <StageContainer >
                <div className='text-center content-center w-full h-full text-gray-500'> 
                            Please select a division from the sidebar 
                        </div>
            </StageContainer>
        </main>
    );
}