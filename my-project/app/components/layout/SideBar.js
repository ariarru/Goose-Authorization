import Card from './Card';

export default function SideBar({children}){

    return(
        <Card add="flex flex-col p-6 ">
            {children}
        </Card>
    );

}