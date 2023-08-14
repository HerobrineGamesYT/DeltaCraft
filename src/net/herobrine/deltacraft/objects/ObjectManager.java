package net.herobrine.deltacraft.objects;

import net.herobrine.deltacraft.objects.dungeon.PortalCluster;

public class ObjectManager {

    private int id;

    public ObjectManager(int id) {
        this.id = id;
    }

    public DeltaObject createObject(Objects type){
        switch(type) {
            case PORTAL_CLUSTER:
                return new PortalCluster(type.getType(), type, id);
            default:
                return null;
        }

    }

}
