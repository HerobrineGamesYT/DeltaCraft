package net.herobrine.deltacraft.objects.inventory;

import net.herobrine.deltacraft.DeltaCraft;
import net.herobrine.deltacraft.objects.*;
import net.herobrine.deltacraft.objects.puzzles.PuzzleGameState;
import net.herobrine.deltacraft.objects.puzzles.type.DoorPuzzle;
import net.herobrine.deltacraft.objects.puzzles.utils.AStarNode2D;
import net.herobrine.deltacraft.objects.puzzles.utils.AStarPath2D;
import net.herobrine.deltacraft.objects.puzzles.utils.ExpoState;
import net.herobrine.deltacraft.utils.NBTReader;
import net.herobrine.gamecore.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class Expo extends DeltaObject implements InventoryObject {

    BukkitRunnable pathingRunnable;
    private ExpoState expoState;
    private Order currentOrder;
    private Inventory inventory;
    private ItemBuilder item;
    private ItemStack stack;
    private NBTReader reader;
    private int currentSlot;
    private int targetSlot;
    private int spawnSlot;

    private boolean isWaiting = false;

    private boolean movedObstructingExpo = false;
    private boolean hasReachedTarget = false;
    private ArrayList<Integer> currentPath = new ArrayList<>();
    private long pathSpeed = 800;
    private long lastPathTime = 0;
    private DoorPuzzle door;

    public Expo(ObjectTypes type, Objects object, int id, UUID uuid, Inventory inventory) {
        super(type, object, id, uuid);
        this.inventory = inventory;
        item = new ItemBuilder(object.getBlockType());
        // Will need to change this durability if the block type is changed from INK_SACK. But I don't plan on ever doing that.
        item.setDurability((short) 10);
    }
    public void setOrder(Order order) {
        if (order != null) {
            this.currentOrder = order;
            setExpoState(ExpoState.BUSY);
        }
    }


    public void handlePathing() {
        // If an expo is not meant to be moving, it won't.
        if (getExpoState() != ExpoState.BUSY) return;

        if (currentSlot == targetSlot && targetSlot == spawnSlot && getExpoState().equals(ExpoState.BUSY)) {
            setExpoState(ExpoState.ACTIVE);
            currentPath.clear();
            pathingRunnable.cancel();
            pathingRunnable = null;
            return;
        }
        // If an expo doesn't have a path, a new one will be generated based on its current slot and target slot.
        if (currentPath.isEmpty()) {
            pickSlot();
            handlePathing();
            return;
        }

    else if (System.currentTimeMillis() - lastPathTime >= pathSpeed) {
       // lastPathTime = System.currentTimeMillis();
        if (isWaiting) {
            checkIfWaiting();
            return;
        }
        if (currentPath.get(0).equals(targetSlot) && !hasReachedTarget) {
            if (targetSlot == spawnSlot) {
                hasReachedTarget = true;
                //Expo is returning to its home slot. It will become available again, and it's pathing will be reset.
             travelToNextSlot();
             setExpoState(ExpoState.ACTIVE);
             currentPath.clear();
             pathingRunnable.cancel();
             pathingRunnable = null;
             return;
            }
        if (door.isCarPulled(currentOrder.getCar()) || targetSlot == door.pulledSlot) {
            hide();
            if (door.isCarPulled(currentOrder.getCar())) {
                hasReachedTarget = true;
                setExpoState(ExpoState.INACTIVE);
                ThreadLocalRandom rand = ThreadLocalRandom.current();
                int randSecs = rand.nextInt(0, 3);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        setExpoState(ExpoState.BUSY);
                        item.setLore(Arrays.asList(ChatColor.YELLOW + "Status:" +  ChatColor.RED + " Busy", "", ChatColor.YELLOW + "Returning to position!"));
                        updateItem();
                        targetSlot = spawnSlot;
                        currentPath.clear();
                        door.addScore();
                        door.getActiveOrders().remove(currentOrder);
                        door.getPulledCars().remove(currentOrder.getCar());
                        currentOrder = null;
                        door.updatePulledIndicator();
                    }
                }.runTaskLater(DeltaCraft.getInstance(), randSecs * 20L);

            }
            return;
        }

        else if (targetSlot == door.carSpawnSlot - 9) {
            hasReachedTarget = true;
            setExpoState(ExpoState.INACTIVE);
            hide();
            ThreadLocalRandom rand = ThreadLocalRandom.current();
            int randSecs = rand.nextInt(0, 3);
            new BukkitRunnable() {
                @Override
                public void run() {
                    setExpoState(ExpoState.BUSY);
                    item.setLore(Arrays.asList(ChatColor.YELLOW + "Status:" +  ChatColor.RED + " Busy", "", ChatColor.YELLOW + "Returning to position!"));
                    updateItem();
                    targetSlot = spawnSlot;
                    currentPath.clear();
                    door.addScore();
                    door.getActiveOrders().remove(currentOrder);
                    door.getCarSpawnWaitingList().remove(currentOrder.getCar());
                    currentOrder = null;
                }
            }.runTaskLater(DeltaCraft.getInstance(), randSecs * 20L);
            return;
        }

        else {
            setExpoState(ExpoState.INACTIVE);
            ThreadLocalRandom rand = ThreadLocalRandom.current();
            int randIndex = rand.nextInt(0,1);
            Car car = currentOrder.getCar();
            car.setDiagonalTravel(true);
            car.setTargetSlot(door.getExitSlots()[randIndex]);
            car.getCurrentPath().clear();
            car.setStopped(false);
            setExpoState(ExpoState.BUSY);
            item.setLore(Arrays.asList(ChatColor.YELLOW + "Status:" +  ChatColor.RED + " Busy", "", ChatColor.YELLOW + "Returning to position!"));
            updateItem();
            targetSlot = spawnSlot;
            currentPath.clear();
            door.addScore();
            door.getActiveOrders().remove(currentOrder);
            currentOrder = null;
            return;
        }
        }
        travelToNextSlot();

    }
    }

    public void startPathingRunnable() {
        pathingRunnable = new BukkitRunnable() {
            @Override
            public void run() {
                if (getState() != ObjectState.ACTIVE) {
                    cancel();
                    return;
                }
                if (door.getState() != PuzzleGameState.ACTIVE) {
                    cancel();
                    return;
                }
                handlePathing();
            }
        };

        pathingRunnable.runTaskTimer(DeltaCraft.getInstance(), 0L, 1L);
    }

    public void travelToNextSlot() {
        if (!arena.getDeltaGame().getObjectManager().getActiveObjects().containsKey(uuid)) return;
        movedObstructingExpo = false;
        if (inventory.getItem(currentPath.get(0)) != null) {
        try {
            NBTReader reader = new NBTReader(inventory.getItem(currentPath.get(0)));
            if (!reader.getStringNBT("uuid").isPresent()) return;
            String uuid = reader.getStringNBT("uuid").get();
            arena.sendDebugMessage(ChatColor.GREEN + "[EXPO DEBUG] " + ChatColor.WHITE + "Item in the way! " + inventory.getItem(currentPath.get(0)).getType());
            DeltaObject obj = arena.getDeltaGame().getObjectManager().getActiveObjects().get(UUID.fromString(uuid));
            arena.sendMessage(ChatColor.GREEN + "[EXPO DEBUG] " + ChatColor.WHITE + "I was able to detect a " + obj);
            if (obj instanceof Expo) {
                Expo expo = (Expo) obj;
                if (expo.getExpoState().equals(ExpoState.ACTIVE)) {
                    arena.sendDebugMessage(ChatColor.GREEN + "[EXPO DEBUG] " + ChatColor.WHITE + "Moving an active expo out of the way!");
                    expo.hide();
                    expo.setWaiting(true);

                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            if (expo.getExpoState() == ExpoState.ACTIVE) {
                                expo.checkIfWaitingForActive();
                                if (!expo.isWaiting()) {
                                    expo.show();
                                    cancel();
                                }
                            }
                            else cancel();
                        }
                    }.runTaskTimer(DeltaCraft.getInstance(), 25L, 1L);
                } else if (expo.getExpoState().equals(ExpoState.BUSY)) {
                    arena.sendDebugMessage(ChatColor.GREEN + "[EXPO DEBUG] " + ChatColor.WHITE + "Moving a busy expo out of the way!");
                    expo.setWaiting(true);
                    expo.hide();
                }
            }
            if (obj instanceof Car) {
                Car car = (Car) obj;
                if (currentOrder.getCar().equals(car)) {
                    setExpoState(ExpoState.INACTIVE);
                    ThreadLocalRandom rand = ThreadLocalRandom.current();
                    int randIndex = rand.nextInt(0, 1);
                    car.setDiagonalTravel(true);
                    car.setTargetSlot(door.getExitSlots()[randIndex]);
                    car.getCurrentPath().clear();
                    car.setStopped(false);
                    door.addScore();
                    door.getActiveOrders().remove(currentOrder);
                    currentOrder = null;
                    setExpoState(ExpoState.BUSY);
                    item.setLore(Arrays.asList(ChatColor.YELLOW + "Status:" + ChatColor.RED + " Busy", "", ChatColor.YELLOW + "Returning to position!"));
                    updateItem();
                    targetSlot = spawnSlot;
                    currentPath.clear();
                }
            } else return;

        }
        catch(IllegalArgumentException ignored) {}
        }
        inventory.setItem(currentSlot, new ItemStack(Material.AIR));
        reader = new NBTReader(stack);
        reader.writeStringNBT("uuid", uuid::toString);
        inventory.setItem(currentPath.get(0), reader.toBukkit());
        currentSlot = currentPath.get(0);
        currentPath.remove(0);
        lastPathTime = System.currentTimeMillis();
    }

    // When state is updated, you can also update the item info according to the current Expo State.
    // If the expo status is set to busy, and the expo has an order, you can also make the Expo start delivering the order.
    public void setExpoState(ExpoState state) {
        this.expoState = state;
        if (state.equals(ExpoState.BUSY)) {
            item.setDisplayName(ChatColor.GRAY + "Expo");
            item.setDurability((short) 8);
            item.setLore(Arrays.asList(ChatColor.YELLOW + "Status: " + ChatColor.RED + "Busy", "",
                    ChatColor.YELLOW + "Running: " + ChatColor.GREEN + currentOrder.name.getDisplay()));
            updateItem();
            if (!door.hasCar() && door.getPulledCars().size() < 3) {
                targetSlot = door.pulledSlot;
                door.sendCarToPulledSpot(currentOrder.getCar());
            }
           else if (door.isCarPulled(currentOrder.getCar())) targetSlot = door.pulledSlot;
           else if (currentOrder.getCar().getCurrentSlot() == 0) targetSlot = door.carSpawnSlot - 9;
           else targetSlot = currentOrder.getCar().getCurrentSlot();
            startPathingRunnable();
        }
        else if (state.equals(ExpoState.ACTIVE)) {
          item.setDisplayName(ChatColor.GREEN + "Expo");
          item.setDurability((short) 10);
          item.setLore(Arrays.asList(ChatColor.YELLOW + "Status: " + ChatColor.GREEN + "Available", "", ChatColor.YELLOW + "Select an order to assign!"));
          updateItem();
        }
    }
    public Order getCurrentOrder() {return currentOrder;}
    public ExpoState getExpoState() {return expoState;}
    public void setDoor(DoorPuzzle door) {this.door = door;}

    public void checkIfWaiting() {isWaiting = inventory.getItem(currentPath.get(0)) != null;}
    public void checkIfWaitingForActive() {isWaiting = inventory.getItem(currentSlot) != null;}
    @Override
    public void initObject() {
    state = ObjectState.ACTIVE;
    item.setDisplayName(ChatColor.GREEN + "Expo");
    item.setLore(Arrays.asList(ChatColor.YELLOW + "Status: " + ChatColor.GREEN + "Available", "", ChatColor.YELLOW + "Select an order to assign!"));
    int[] spawnSlots = new int[]{13,22,31};
    spawnSlot = spawnSlots[door.getExpos().size() - 1];
    if (inventory.getItem(spawnSlot) != null) {
        for (int slot : spawnSlots) {
            if(inventory.getItem(slot) == null) {
                currentSlot = slot;
                break;
            }
        }
    }
    else currentSlot = spawnSlot;
    updateItem();
    expoState = ExpoState.ACTIVE;
    }

    @Override
    public void destroyObject() {
    pathingRunnable.cancel();
    if (inventory != null) hide();
    arena.getDeltaGame().getObjectManager().unregisterObject(uuid);
    }

    @Override
    public void pickSlot() {
        AStarNode2D startNode = AStarNode2D.toNode(currentSlot,9);
        AStarNode2D endNode = AStarNode2D.toNode(targetSlot,9);
        AStarPath2D aStarPath2D = new AStarPath2D(startNode, endNode);
        aStarPath2D.setDiagonals(true);
        currentPath.clear();
        currentPath.addAll(aStarPath2D.find());
        hasReachedTarget = false;
    }

    public void updateItem() {
        stack = item.build();
        reader = new NBTReader(stack);
        reader.writeStringNBT("uuid", uuid::toString);
        inventory.setItem(currentSlot, reader.toBukkit());
    }

    public void move(int slot) {
        inventory.setItem(currentSlot, new ItemStack(Material.AIR));
        setCurrentSlot(slot);
        updateItem();
    }

    public void hide() {inventory.setItem(currentSlot, new ItemStack(Material.AIR));}

    public void show() {inventory.setItem(currentSlot, reader.toBukkit());}

    public boolean isWaiting() {return isWaiting;}
    public void setWaiting(boolean isWaiting) {this.isWaiting = isWaiting;}

    @Override
    public int getCurrentSlot() {
        return currentSlot;
    }

    @Override
    public void setCurrentSlot(int slot) {
    this.currentSlot = slot;
    }
}


