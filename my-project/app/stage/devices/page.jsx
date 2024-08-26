import ManageDevices from '../../components/admin-stage/ManageDevices';
import StageContainer from '../../components/admin-stage/StageContainer'
import Card from '../../components/layout/Card';

export default function DeviceStage(){
    return(
        <StageContainer>
            <Card>
                <ManageDevices></ManageDevices>
            </Card>
        </StageContainer>
        
    );

}