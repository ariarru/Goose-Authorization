'use server';
import Card from '../layout/Card'
import ManageRooms from './ManageRooms'

export default async function RoomsContainer(){
    

    return(
        <Card>
            <ManageRooms></ManageRooms>
        </Card>
    );
}