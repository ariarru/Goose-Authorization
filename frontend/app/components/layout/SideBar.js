import Card from './Card';

export default function SideBar({children}){

    return(
        <Card add="flex flex-col p-6 w-48">
            {children}
        </Card>
    );

}