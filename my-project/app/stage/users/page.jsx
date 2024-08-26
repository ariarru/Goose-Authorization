import ManageUsers from '../../components/admin-stage/ManageUsers'
import StageContainer from '../../components/admin-stage/StageContainer'
import Card from '../../components/layout/Card'


export default function UserStage(){
    return(
        <StageContainer>
            <Card>
                <ManageUsers></ManageUsers>
            </Card>
        </StageContainer>
        )
}