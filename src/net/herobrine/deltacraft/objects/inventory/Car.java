package net.herobrine.deltacraft.objects.inventory;

import net.herobrine.deltacraft.objects.*;
import net.herobrine.deltacraft.objects.Objects;
import net.herobrine.deltacraft.objects.puzzles.PuzzleTypes;
import net.herobrine.deltacraft.objects.puzzles.type.DoorPuzzle;
import net.herobrine.deltacraft.objects.puzzles.utils.AStarNode2D;
import net.herobrine.deltacraft.objects.puzzles.utils.AStarPath2D;
import net.herobrine.deltacraft.objects.puzzles.utils.OrderStatus;
import net.herobrine.deltacraft.utils.NBTReader;
import net.herobrine.gamecore.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class Car extends DeltaObject implements InventoryObject {
    public Car(ObjectTypes type, Objects object, int id, UUID uuid, Inventory inventory) {
        super(type, object, id, uuid);
        this.inventory = inventory;
        item = new ItemBuilder(object.getBlockType());
        // initObject needs to be called after default data is set in puzzle class.
    }

    Inventory inventory;

    ItemBuilder item;
    ItemStack stack;
    NBTReader reader;
    int currentSlot;
    int targetSlot;
    ChatColor color;
    int durability;
    long lastPathTime = 0;
    long pathSpeed = 1000;
    int sequence;
    boolean isSequenced = false;

    boolean isStopped = false;
    boolean diagonalTravel = false;
    Car target;
    Order order;

    ArrayList<Integer> currentPath = new ArrayList<>();

    public List<Integer> findPath() {
        AStarNode2D startNode = AStarNode2D.toNode(currentSlot,9);
        AStarNode2D endNode = AStarNode2D.toNode(targetSlot,9);
        AStarPath2D aStarPath2D = new AStarPath2D(startNode, endNode);
        if (color == ChatColor.BLUE && !isSequenced) diagonalTravel = true;
        aStarPath2D.setDiagonals(diagonalTravel);
        return aStarPath2D.find();
    }

    @Override
    public void pickSlot() {
        currentPath.clear();
        List<Integer> list = findPath();
        currentPath.addAll(list);
    }

    public void travelToNextSlot() {
        if (!arena.getDeltaGame().getObjectManager().getActiveObjects().containsKey(uuid)) return;

        if (inventory.getItem(currentPath.get(0)) != null) return;
        reader = new NBTReader(stack);
        reader.writeStringNBT("uuid", uuid::toString);
        inventory.setItem(currentSlot, new ItemStack(Material.AIR));
        inventory.setItem(currentPath.get(0), reader.toBukkit());
        currentSlot = currentPath.get(0);
        currentPath.remove(0);
        lastPathTime = System.currentTimeMillis();
    }

    @Override
    public void initObject() {
        state = ObjectState.ACTIVE;
        item.setDurability((short)durability);
        item.setDisplayName(color + "Unsequenced Car");
        stack = item.build();
        reader = new NBTReader(stack);
        reader.writeStringNBT("uuid", uuid::toString);
        inventory.setItem(currentSlot, reader.toBukkit());
    }

    @Override
    public void destroyObject() {
       if(inventory != null) inventory.setItem(currentSlot, new ItemStack(Material.AIR));
       arena.getDeltaGame().getObjectManager().unregisterObject(uuid);
    }

    public void remove() {
        if(inventory != null) inventory.setItem(currentSlot, new ItemStack(Material.AIR));
    }

    public void setSequenced(boolean isSequenced, int sequence) {
        this.isSequenced = isSequenced;
        this.sequence = sequence;
        if (isSequenced) {
            if(sequence != 1) setStopped(true);
            item.setGlow(true);
            item.setAmount(sequence);
            item.setDisplayName(color + "Car " + sequence);
            stack = item.build();
            inventory.setItem(currentSlot, stack);
        }
    }

    public void setDiagonalTravel(boolean travel) {
        this.diagonalTravel = travel;
    }
    public void tenderCarForDoor(int sequence) {
        this.isSequenced = true;
        this.sequence = sequence;
    }
    public void spawnCarForDoor(Order order, DoorPuzzle door, int sequence) {
        this.isSequenced = true;
        this.sequence = sequence;
        this.inventory = door.getInventory();
        item.setGlow(true);
        switch (order.getStatus()) {
            case BAGGING:
                this.color = ChatColor.RED;
                break;
            case STUFFING:
                this.color = ChatColor.BLUE;
                break;
            case READY:
                this.color = ChatColor.GREEN;
                break;
            default: return;
        }
        if (order.hasEasterEgg) {
            this.color = ChatColor.LIGHT_PURPLE;
            this.pathSpeed = 500;
        }
        setDurabilityFromColor();

        item.setDisplayName(color + order.name.getDisplay());
        item.setAmount(sequence);
        item.setGlow(false);

        stack = item.build();
        setOrder(order);
        setCurrentSlot(door.carSpawnSlot);
        setTargetSlot(door.carDestinationSlot);
        getCurrentPath().clear();
        inventory.setItem(currentSlot, stack);
    }

    public void spawnCarForDoor(Order order, DoorPuzzle door) {
        this.isSequenced = true;
        this.inventory = door.getInventory();
        switch (order.getStatus()) {
            case BAGGING:
                this.color = ChatColor.RED;
                break;
            case STUFFING:
                this.color = ChatColor.BLUE;
                break;
            case READY:
                this.color = ChatColor.GREEN;
                break;
            default: return;
        }
        if (order.hasEasterEgg) {
            this.color = ChatColor.LIGHT_PURPLE;
            this.pathSpeed = 500;
        }
        setDurabilityFromColor();

        item.setDisplayName(color + order.name.getDisplay());
        item.setDurability((short)durability);
        item.setAmount(sequence);
        item.setGlow(false);

        stack = item.build();
        setCurrentSlot(door.carSpawnSlot);
        setTargetSlot(door.carDestinationSlot);
        getCurrentPath().clear();
        reader = new NBTReader(stack);
        reader.writeStringNBT("uuid", uuid::toString);
        inventory.setItem(currentSlot, reader.toBukkit());
    }

    public void spawnCarForDoor(DoorPuzzle door) {
        this.isSequenced = true;
        this.inventory = door.getInventory();
        item.setGlow(true);
        switch (order.getStatus()) {
            case BAGGING:
                this.color = ChatColor.RED;
                break;
            case STUFFING:
                this.color = ChatColor.BLUE;
                break;
            case READY:
                this.color = ChatColor.GREEN;
                break;
            default: return;
        }
        if (order.hasEasterEgg) {
            this.color = ChatColor.LIGHT_PURPLE;
            this.pathSpeed = 500;
        }
        setDurabilityFromColor();
        item.setDisplayName(color + order.name.getDisplay());
        item.setDurability((short) durability);
        item.setAmount(sequence);
        item.setGlow(false);

        stack = item.build();
        setCurrentSlot(door.carSpawnSlot);
        setTargetSlot(door.carDestinationSlot);
        getCurrentPath().clear();
        reader = new NBTReader(stack);
        reader.writeStringNBT("uuid", uuid::toString);
        inventory.setItem(currentSlot, reader.toBukkit());
    }

    public boolean isSequenced() {return isSequenced;}

    public int getSequence() {return sequence;}

    public boolean hasTarget() {return target != null;}

    public boolean isStopped() {return isStopped;}
    public void setStopped(boolean stopped) {
        this.isStopped = stopped;
        if (stopped) {
            if (inventory.getTitle().equalsIgnoreCase(PuzzleTypes.LANE_MERGE.getDisplay())) item.setLore(Arrays.asList(ChatColor.YELLOW + "Car is currently stopped..", ChatColor.YELLOW + "Click again to let it go!"));
            else item.setLore(Arrays.asList(ChatColor.YELLOW + "Car is currently stopped...", "", ChatColor.YELLOW + "An " + ChatColor.GREEN + "Expo " + ChatColor.YELLOW + "is nearby!"));
            stack = item.build();
            reader = new NBTReader(stack);
            reader.writeStringNBT("uuid", uuid::toString);
            inventory.setItem(currentSlot, reader.toBukkit());
            return;
        }
        item.setLore((ArrayList<String>) null);
        stack = item.build();
        reader = new NBTReader(stack);
        reader.writeStringNBT("uuid", uuid::toString);
        inventory.setItem(currentSlot, reader.toBukkit());
    }

    public void arrivedAtDoor() {
        // why do i have a common part in the ifs?? Because for some reason the lore wont set if I don't update it. I dont know why!!! it's very frustrating.
        if (!order.getStatus().equals(OrderStatus.READY)) item.setLore(Arrays.asList(ChatColor.YELLOW + "Waiting on " + color +  order.name.getDisplay() + "'s Order", "", ChatColor.YELLOW + "Click to pull this car!"));
        else item.setLore(Arrays.asList(ChatColor.YELLOW + "Waiting on " + color +  order.name.getDisplay() + "'s Order", "", ChatColor.YELLOW + "That order is " + ChatColor.GREEN + "READY" + ChatColor.YELLOW + "!", ChatColor.YELLOW + "Click to deliver it!"));

        stack = item.build();
        item.setGlow(true);
        stack = item.build();
        reader = new NBTReader(stack);
        reader.writeStringNBT("uuid", uuid::toString);
        inventory.setItem(currentSlot, reader.toBukkit());
        isStopped = true;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Order getOrder() {return order;}
    public Car getTarget() {return target;}

    @Override
    public int getCurrentSlot() {return currentSlot;}

    @Override
    public void setCurrentSlot(int slot) {this.currentSlot = slot;}
    public int getTargetSlot() {return targetSlot;}

    public void setTargetSlot(int slot) {this.targetSlot = slot;}

    public void setColor(ChatColor color) {this.color = color;}

    public void setDurability(int durability) {this.durability = durability;}

    public long getLastPathTime() {return lastPathTime;}

    public void setDurabilityFromColor() {
        switch (color) {
            case RED:
                this.durability = 14;
                break;
            case GREEN:
                this.durability = 5;
                break;
            case BLUE:
                this.durability = 11;
                break;
            case LIGHT_PURPLE:
                this.durability = 2;
            default: return;
        }
    }
    public void setLastPathTime(long pathTime) {this.lastPathTime = pathTime;}

    public long getPathSpeed() {return pathSpeed;}
    public void setPathSpeed(long pathSpeed) {this.pathSpeed = pathSpeed;}
    public ArrayList<Integer> getCurrentPath() {return currentPath;}
    public String getDisplay() {return item.getItemMeta().getDisplayName();}
    public void setTarget(Car car) {
        this.target = car;
        item.setLore(ChatColor.YELLOW + "Following " + car.getDisplay());
        stack = item.build();
        reader = new NBTReader(stack);
        reader.writeStringNBT("uuid", uuid::toString);
        inventory.setItem(currentSlot, reader.toBukkit());
    }

}
