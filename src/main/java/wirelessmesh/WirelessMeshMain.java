package wirelessmesh;

import service.Wirelessmeshservice;
import service.Wirelessmeshpresenceservice;
import wirelessmesh.domain.*;
import io.cloudstate.javasupport.CloudState;

import domain.*;

public class WirelessMeshMain {

    public static void main(String... args)  throws Exception  {
        new CloudState()
                .registerEventSourcedEntity(
                        DeviceEntity.class,
                        Wirelessmeshservice.getDescriptor().findServiceByName("WirelessMeshService"),
                        Devicedomain.getDescriptor())
                .registerCrdtEntity(
                            PresenceDeviceEntity.class,
                            Wirelessmeshpresenceservice.getDescriptor().findServiceByName("WirelessMeshPresenceService"))
                .start()
                .toCompletableFuture()
                .get();
    }
}
