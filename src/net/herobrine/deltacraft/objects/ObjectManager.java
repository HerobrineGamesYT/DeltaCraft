package net.herobrine.deltacraft.objects;

import net.herobrine.deltacraft.objects.dungeon.PortalCluster;
import net.herobrine.deltacraft.objects.dungeon.PureHeart;
import net.herobrine.deltacraft.objects.inventory.Car;
import net.herobrine.deltacraft.objects.inventory.Expo;
import net.herobrine.deltacraft.objects.inventory.Order;
import net.herobrine.deltacraft.objects.puzzles.DimentioBossTerminal;
import net.herobrine.deltacraft.objects.puzzles.PuzzleTypes;
import org.bukkit.inventory.Inventory;

import javax.management.openmbean.KeyAlreadyExistsException;
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
            case PUZZLE_TERMINAL:
                DimentioBossTerminal terminal = new DimentioBossTerminal(type.getType(), type, id, uuid);
                registerObject(terminal, uuid);
                return terminal;
            default:
                return null;
        }

    }

    public DeltaObject createSpecialPuzzleObject(Objects type, PuzzleTypes puzzle) {
        UUID uuid = UUID.randomUUID();
        switch (type) {
            case PUZZLE_TERMINAL:
                DimentioBossTerminal terminal = new DimentioBossTerminal(type.getType(), type, id, uuid, puzzle);
                registerObject(terminal, uuid);
                return terminal;
            default: return null;
        }
    }

    public DeltaObject createInventoryObject(Objects type, Inventory inventory) {
        UUID uuid = UUID.randomUUID();
        switch(type) {
            case CAR:
                Car car = new Car(type.getType(), type, id, uuid, inventory);
                registerObject(car, uuid);
                return car;
            case ORDER:
                Order order = new Order(type.getType(), type, id, uuid, inventory);
                registerObject(order, uuid);
                return order;
            case EXPO:
                Expo expo = new Expo(type.getType(), type, id, uuid, inventory);
                registerObject(expo, uuid);
                return expo;
            default:
                return null;
        }
    }



    public void registerObject(DeltaObject object, UUID uuid) throws KeyAlreadyExistsException {
        if (activeObjects.containsKey(uuid)) throw new KeyAlreadyExistsException("An object already exists with this UUID. " + activeObjects.get(uuid));
        else activeObjects.put(uuid, object);
    }
    public void unregisterObject(UUID uuid) {
        getActiveObjects().get(uuid).setState(ObjectState.DESTROYED);
        activeObjects.remove(uuid);
    }

    public HashMap<UUID, DeltaObject> getActiveObjects() {return activeObjects;}
}
