package net.herobrine.deltacraft.objects;

import net.herobrine.deltacraft.objects.dungeon.PortalCluster;
import net.herobrine.deltacraft.objects.dungeon.PureHeart;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.UUID;

public class ObjectManager {

    private int id;
    private HashMap<UUID, DeltaObject> activeObjects;

    public ObjectManager(int id) {
        this.id = id;
        this.activeObjects = new HashMap<UUID, DeltaObject>();
    }

    public DeltaObject createObject(Objects type){
        UUID uuid = UUID.randomUUID();
        switch(type) {
            case PORTAL_CLUSTER:
                PortalCluster cluster = new PortalCluster(type.getType(), type, id, uuid);
                registerObject(cluster, uuid);
                return cluster;
            case PURE_HEART_RED:
            case PURE_HEART_GREEN:
            case PURE_HEART_BLUE:
            case PURE_HEART_CYAN:
            case PURE_HEART_WHITE:
            case PURE_HEART_ORANGE:
            case PURE_HEART_PURPLE:
            case PURE_HEART_YELLOW:
                PureHeart heart = new PureHeart(type.getType(), type, id, uuid);
                registerObject(heart, uuid);
                return heart;
            default:
                return null;
        }

    }


    public void registerObject(DeltaObject object, UUID uuid) throws ConcurrentModificationException {
        if (activeObjects.containsKey(uuid)) throw new ConcurrentModificationException("An object already exists with this UUID. " + activeObjects.get(uuid));
        else activeObjects.put(uuid, object);
    }
    public void unregisterObject(UUID uuid) {activeObjects.remove(uuid);}

    public HashMap<UUID, DeltaObject> getActiveObjects() {return activeObjects;}
}
