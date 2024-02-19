package net.herobrine.deltacraft.objects;

import net.herobrine.deltacraft.objects.dungeon.PortalCluster;
import net.herobrine.deltacraft.objects.dungeon.PureHeart;

public class ObjectManager {

    private int id;

    public ObjectManager(int id) {
        this.id = id;
    }

    public DeltaObject createObject(Objects type){
        switch(type) {
            case PORTAL_CLUSTER:
                return new PortalCluster(type.getType(), type, id);
            case PURE_HEART_RED:
            case PURE_HEART_GREEN:
            case PURE_HEART_BLUE:
            case PURE_HEART_CYAN:
            case PURE_HEART_WHITE:
            case PURE_HEART_ORANGE:
            case PURE_HEART_PURPLE:
            case PURE_HEART_YELLOW:
                return new PureHeart(type.getType(), type, id);
            default:
                return null;
        }

    }

}
