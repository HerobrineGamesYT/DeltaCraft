package net.herobrine.deltacraft.objects.puzzles.type;

import net.herobrine.core.HerobrinePVPCore;
import net.herobrine.deltacraft.DeltaCraft;
import net.herobrine.deltacraft.events.OrderStatusUpdateEvent;
import net.herobrine.deltacraft.objects.DeltaObject;
import net.herobrine.deltacraft.objects.ObjectState;
import net.herobrine.deltacraft.objects.Objects;
import net.herobrine.deltacraft.objects.inventory.Car;
import net.herobrine.deltacraft.objects.inventory.Expo;
import net.herobrine.deltacraft.objects.puzzles.utils.ExpoState;
import net.herobrine.deltacraft.objects.inventory.Order;
import net.herobrine.deltacraft.objects.puzzles.LinkedPuzzle;
import net.herobrine.deltacraft.objects.puzzles.Puzzle;
import net.herobrine.deltacraft.objects.puzzles.PuzzleGameState;
import net.herobrine.deltacraft.objects.puzzles.PuzzleTypes;
import net.herobrine.deltacraft.objects.puzzles.utils.OrderComparator;
import net.herobrine.deltacraft.objects.puzzles.utils.OrderStatus;
import net.herobrine.deltacraft.utils.NBTReader;
import net.herobrine.gamecore.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class DoorPuzzle extends Puzzle implements LinkedPuzzle {
    boolean isLinked;
    Puzzle linkedPuzzle;
    Player player;
    Arena arena;
    BukkitRunnable spawnRunnable;
    Car carAtDoor;
    int score = 0;
    int sequence = 0;
    long ticks = 0;
    int maxObjects = 1;

   public int carSpawnSlot = 12;
   public int carDestinationSlot = 39;
   public int pulledSlot = 48;
    long spawnSpeed = 20L;
    int[] baggingSlots = new int[] {16,25,34,43};
    int[] stuffingSlots = new int[] {15,24,33,42};
    int[] readySlots = new int[] {14,23,32,41};

    int[] exitSlots = new int[] {46, 47};

    // bagging -> stuffing - > ready
    int[] offscreenIndicators = new int[] {52,51,50};
    HashMap<DeltaObject, Integer> objectMap = new HashMap<>();
    ArrayList<Order> offscreenOrders = new ArrayList<>();

    ArrayList<Order> activeOrders = new ArrayList<>();
    ArrayList<Expo> expos = new ArrayList<>();
    HashMap<Car, Integer> pendingCars = new HashMap<>();
    ArrayList<Car> pulledCars = new ArrayList<>();

    ArrayList<Car> carSpawnWaitingList = new ArrayList<>();
    ItemBuilder scoreKeeper = new ItemBuilder(Material.REDSTONE);
    ItemBuilder offScreenIndicator = new ItemBuilder(Material.STAINED_GLASS_PANE);

    public DoorPuzzle(DeltaObject object, PuzzleTypes type, boolean isLinked) {
        super(object, type);
        this.isLinked = isLinked;
    }

    @Override
    public void startPuzzle(Player player) {
    //TODO Setup logic for if Door Puzzle is linked to Sequencer.
        setState(PuzzleGameState.ACTIVE);
        if (activePlayers.size() >= type.getMaxPlayers()) {
            player.sendMessage(ChatColor.RED + "This puzzle is already being completed!");
            return;
        }
        activePlayers.add(player.getUniqueId());

        this.player = player;
        this.arena = Manager.getArena(player);
        player.openInventory(DeltaCraft.getInstance().getPuzzleMenuManager().createGUI(type));

        spawnRunnable =  new BukkitRunnable() {

            @Override
            public void run() {
                if (!getState().equals(PuzzleGameState.ACTIVE)) {
                    cancel();
                    resetPuzzle();
                }
                if (activePlayers.size() < 1) {
                    cancel();
                    return;
                }
                if (!getInventory().getTitle().equalsIgnoreCase(type.getDisplay())) {
                    cancel();
                    return;
                }
                if (isComplete()) {
                    cancel();
                    return;
                }
                long time = ticks / 20;
                if (score > 1) maxObjects = 2;
                if (score > 6 || time > 20) maxObjects = 4;
                if (score > 10 || time > 30) maxObjects = 6;
                if (score >= 15) maxObjects = 10;
                if (time > 15) spawnSpeed = 15L;
                if (time > 30) spawnSpeed = 10L;
                if (time > 60) spawnSpeed = 5L;

                if (ticks % spawnSpeed == 0) makeOrder();
                ticks++;
            }
        };
        spawnRunnable.runTaskTimer(DeltaCraft.getInstance(), 2L, 1L);
        pathingRunnable();
    }

    @Override
    public void setLinkedPuzzle(Puzzle puzzle) {
    this.linkedPuzzle = puzzle;
    }

    @Override
    public Puzzle getLinkedPuzzle() {
        return linkedPuzzle;
    }

    @Override
    public void setPrimaryPuzzle(boolean isPrimaryPuzzle) {
    }


    public void makeOrder() {
        if (activeOrders.size() >= maxObjects) return;
    Order order = (Order) arena.getDeltaGame().getObjectManager().createInventoryObject(Objects.ORDER, getInventory());
    Car car = (Car) arena.getDeltaGame().getObjectManager().createInventoryObject(Objects.CAR, getInventory());
    car.setColor(ChatColor.BLUE);
    car.setDurability(3);
    order.assignOrderToCar(car);
    car.setOrder(order);
    activeOrders.add(order);
    int i = 0;
    for (int slot : baggingSlots) {
        if (getInventory().getItem(slot) != null) i++;
    }
    order.setRow(i);
    if (!order.isOffScreen()) order.initObject();
    if (order.getCurrentSlot() != 0) objectMap.put(order, order.getCurrentSlot());
    else {
        offscreenOrders.add(order);
        updateOffscreenCounter();
    }

    shouldSpawnExpo();
    if (!isLinked) {
        ThreadLocalRandom rand = ThreadLocalRandom.current();
        int randInt = rand.nextInt(2, 30);
        tenderRunnable(order, randInt);
    }
    }

    public void shouldSpawnExpo() {
        if (expos.size() < activeOrders.size() / 4  && expos.size() < 3) {
        Expo expo = (Expo) arena.getDeltaGame().getObjectManager().createInventoryObject(Objects.EXPO, getInventory());
        expo.setDoor(this);
        expos.add(expo);
        expo.initObject();
        }
    }
    public void spawnCar(Order order) {
    if (!canSpawnCar()) {
        carSpawnWaitingList.add(order.getCar());
        order.getCar().setOrder(order);
        return;
    }
    if (!carSpawnWaitingList.isEmpty()) {
        spawnCar();
        carSpawnWaitingList.add(order.getCar());
        return;
    }
    order.getCar().spawnCarForDoor(order, this);
    pendingCars.put(order.getCar(), carSpawnSlot);
    }

    public void spawnCar() {
        Car car = carSpawnWaitingList.get(0);
        car.spawnCarForDoor(this);
        pendingCars.put(car, carSpawnSlot);
        carSpawnWaitingList.remove(0);
    }

    // Used in the LinkedPuzzle variant to create an order on the Door side when a car spawns at Sequencing.
    public void makeOrder(Car car) {

    }
    public Expo getExpoForOrder(Order order) {
        for (Expo expo : expos) {
            if (expo.getCurrentOrder() == order) return expo;
        }
        return null;
    }

    public ArrayList<Expo> getExpos() {return expos;}
    public ArrayList<Order> getActiveOrders() {return activeOrders;}

    public boolean canSpawnCar() {
        return getInventory().getItem(carSpawnSlot) == null;
    }

    public void updateRow(Order order) {
        int i = 0;
        switch (order.getStatus()) {
            case BAGGING:
                for (int slot : baggingSlots) {
                    if (getInventory().getItem(slot) != null) i++;
                }
                break;
            case STUFFING:
                for (int slot : stuffingSlots) {
                    if (getInventory().getItem(slot) != null) i++;
                }
                break;
            case READY:
                for (int slot : readySlots) {
                    if (getInventory().getItem(slot) != null) i++;
                }
                break;
            default: return;
        }
        order.setRow(i);
    }
    public void updateOffscreenCounter() {
    int offscreenBagging = 0;
    int offscreenStuffing = 0;
    int offscreenReady = 0;
    for (Order order : offscreenOrders) {
        switch (order.getStatus()) {
            case BAGGING:
                offscreenBagging = offscreenBagging + 1;
                break;
            case STUFFING:
                offscreenStuffing = offscreenStuffing + 1;
                break;
            case READY:
                offscreenReady = offscreenReady + 1;
                break;
            default: break;
        }
    }
    if (offscreenBagging > 0) {
        offScreenIndicator.setDisplayName(ChatColor.GOLD + "Offscreen Counter");
        offScreenIndicator.setAmount(offscreenBagging);
        offScreenIndicator.setDurability((short) 1);
        offScreenIndicator.setLore(ChatColor.YELLOW + "You are " + ChatColor.RED + offscreenBagging + ChatColor.YELLOW + " offscreen!");
    }
    else {
        offScreenIndicator.setDisplayName(ChatColor.GREEN + "Offscreen Counter");
        offScreenIndicator.setDurability((short) 13);
        offScreenIndicator.setLore(ChatColor.GRAY + "You aren't offscreen!");
        offScreenIndicator.setAmount(1);
    }
    getInventory().setItem(offscreenIndicators[0], offScreenIndicator.build());
    if (offscreenStuffing > 0) {
        offScreenIndicator.setDisplayName(ChatColor.GOLD + "Offscreen Counter");
        offScreenIndicator.setAmount(offscreenStuffing);
        offScreenIndicator.setDurability((short) 1);
        offScreenIndicator.setLore(ChatColor.YELLOW + "You are " + ChatColor.RED + offscreenStuffing + ChatColor.YELLOW + " offscreen!");
    }
    else {
        offScreenIndicator.setDisplayName(ChatColor.GREEN + "Offscreen Counter");
        offScreenIndicator.setDurability((short) 13);
        offScreenIndicator.setLore(ChatColor.GRAY + "You aren't offscreen!");
        offScreenIndicator.setAmount(1);
    }
    getInventory().setItem(offscreenIndicators[1], offScreenIndicator.build());
    if (offscreenReady > 0) {
        offScreenIndicator.setDisplayName(ChatColor.GOLD + "Offscreen Counter");
        offScreenIndicator.setAmount(offscreenReady);
        offScreenIndicator.setDurability((short) 1);
        offScreenIndicator.setLore(ChatColor.YELLOW + "You are " + ChatColor.RED + offscreenReady + ChatColor.YELLOW + " offscreen!");
    }
    else {
        offScreenIndicator.setDisplayName(ChatColor.GREEN + "Offscreen Counter");
        offScreenIndicator.setDurability((short) 13);
        offScreenIndicator.setLore(ChatColor.GRAY + "You aren't offscreen!");
        offScreenIndicator.setAmount(1);
    }
        getInventory().setItem(offscreenIndicators[2], offScreenIndicator.build());
    }

    public void moveAllBags() {
        NBTReader reader;
        int i = 0;
        boolean move = false;
        for (int slot : baggingSlots) {
            if (move && getInventory().getItem(slot) != null) {
                ItemStack stack = getInventory().getItem(slot);
                reader = new NBTReader(stack);
                UUID uuid = null;
                if (reader.getStringNBT("uuid").isPresent()) uuid = UUID.fromString(reader.getStringNBT("uuid").get());
                Order order = null;
                if (uuid != null) order = (Order) arena.getDeltaGame().getObjectManager().getActiveObjects().get(uuid);
                if (order != null) order.setCurrentSlot(baggingSlots[i - 1]);
            }
            if (getInventory().getItem(slot) == null) move = true;
            i++;
        }
        moveOffscreenBags(OrderStatus.BAGGING);
        move = false;
        i = 0;
        for (int slot : stuffingSlots) {
            if (move && getInventory().getItem(slot) != null) {
                ItemStack stack = getInventory().getItem(slot);
                reader = new NBTReader(stack);
                UUID uuid = null;
                if (reader.getStringNBT("uuid").isPresent()) uuid = UUID.fromString(reader.getStringNBT("uuid").get());
                Order order = null;
                if (uuid != null) order = (Order) arena.getDeltaGame().getObjectManager().getActiveObjects().get(uuid);
                if (order != null) order.setCurrentSlot(stuffingSlots[i - 1]);
            }
            if (getInventory().getItem(slot) == null) move = true;
            i++;
        }
        moveOffscreenBags(OrderStatus.STUFFING);
        //move = false;
        //i = 0;
        //for (int slot : readySlots) {
       //     if (move && getInventory().getItem(slot) != null) {
        //        ItemStack stack = getInventory().getItem(slot);
         //       reader = new NBTReader(stack);
        //        UUID uuid = null;
        //        if (reader.getStringNBT("uuid").isPresent()) uuid = UUID.fromString(reader.getStringNBT("uuid").get());
         //       Order order = null;
        //        if (uuid != null) order = (Order) arena.getDeltaGame().getObjectManager().getActiveObjects().get(uuid);
         //       if (order != null) order.setCurrentSlot(readySlots[i - 1]);
        //        move = false;
         //   }
       //     if (getInventory().getItem(slot) == null) move = true;
       //     i++;
       // }
        checkForPriority();
        //moveOffscreenBags(OrderStatus.READY);
    }
    public void moveOffscreenBags(OrderStatus status) {
        while (!isScreenFull(status) && getFirstOffscreen(status) != null) {
            Order order = getFirstOffscreen(status);
            order.setOffScreen(false);
            offscreenOrders.remove(order);
            updateRow(order);
            order.pickSlot();
        }
        updateOffscreenCounter();
    }

    public boolean isScreenFull(OrderStatus status) {
        for (int slot : getArrayForStatus(status)) {
            if (getInventory().getItem(slot) == null) return false;
        }
        return true;
    }

    // Called when an order is sequenced to update its position in the GUI.
    public void checkForPriority() {
        ArrayList<Order> priorityList = new ArrayList<>();
        ArrayList<Order> secondaryList = new ArrayList<>();
        for (Order orders : getAllOrdersWithStatus(OrderStatus.READY)) {
            if (orders.getCar().isSequenced()) priorityList.add(orders);
            else secondaryList.add(orders);
        }
        // Sort the orders in sequenced order. This list will include offscreen orders. If an offscreen order is sequenced, it will be added to this list and sorted.
        Collections.sort(priorityList, new OrderComparator());
        //priorityList.sort(new OrderComparator());
        // Add the unsequenced orders to the end of the list, to make them go offscreen if necessary.
        priorityList.addAll(secondaryList);
        int i = 0;
        for (Order orders : priorityList) {
            arena.sendDebugMessage(ChatColor.GOLD + "[DOOR DEBUG] " + ChatColor.WHITE + "Setting " + isCarPulled(orders.getCar()) + " " + orders.getName().getDisplay() + "'s Order (" + orders.getCar().getSequence() + ") to row #" + i);
            orders.setRowAndSlot(i);
            if (orders.isOffScreen() && !offscreenOrders.contains(orders)) offscreenOrders.add(orders);
            i++;
        }
        moveOffscreenBags(OrderStatus.READY);
    }

    public void tenderRunnable(Order order, long delay) {
        new BukkitRunnable() {
            @Override
            public void run() {
                sequence = sequence + 1;
                order.getCar().tenderCarForDoor(sequence);
                ThreadLocalRandom rand = ThreadLocalRandom.current();
                int randInt = rand.nextInt(2,5);
               if (order.getStatus().equals(OrderStatus.READY) && isLinked) checkForPriority();
               scheduleSpawning(order, randInt);
            }
        }.runTaskLater(DeltaCraft.getInstance(), delay);
    }

    public void scheduleSpawning(Order order, long delay) {
        new BukkitRunnable() {
            @Override
            public void run() {
            spawnCar(order);
            }
        }.runTaskLater(DeltaCraft.getInstance(), delay);
    }

    public void moveBags(OrderStatus status) {
        if (status.equals(OrderStatus.READY)) {
            checkForPriority();
            return;
        }
        NBTReader reader;
        int i = 0;
        boolean move = false;
        for (int slot : getArrayForStatus(status)) {
            if (move && getInventory().getItem(slot) != null) {
                ItemStack stack = getInventory().getItem(slot);
                reader = new NBTReader(stack);
                UUID uuid = null;
                if (reader.getStringNBT("uuid").isPresent()) uuid = UUID.fromString(reader.getStringNBT("uuid").get());
                Order order = null;
                if (uuid != null) order = (Order) arena.getDeltaGame().getObjectManager().getActiveObjects().get(uuid);
                if (order != null) order.setCurrentSlot(getArrayForStatus(status)[i - 1]);
            }
            if (getInventory().getItem(slot) == null) move = true;
            i++;
        }
        moveOffscreenBags(status);
    }

    public void moveReadyBags() {
        NBTReader reader;
        int i = 0;
        boolean move = false;
        for (int slot : getArrayForStatus(OrderStatus.READY)) {
            if (move && getInventory().getItem(slot) != null) {
                ItemStack stack = getInventory().getItem(slot);
                reader = new NBTReader(stack);
                UUID uuid = null;
                if (reader.getStringNBT("uuid").isPresent()) uuid = UUID.fromString(reader.getStringNBT("uuid").get());
                Order order = null;
                if (uuid != null) order = (Order) arena.getDeltaGame().getObjectManager().getActiveObjects().get(uuid);
                if (order != null) order.setCurrentSlot(getArrayForStatus(OrderStatus.READY)[i - 1]);
            }
            if (getInventory().getItem(slot) == null) move = true;
            i++;
        }
        moveOffscreenBags(OrderStatus.READY);
    }
    public ArrayList<Order> getAllOrdersWithStatus(OrderStatus status) {
        ArrayList<Order> orderList = new ArrayList<>();
        for (Order order : activeOrders) {if (order.getStatus().equals(status)) orderList.add(order);}
        return orderList;
    }

    public int[] getArrayForStatus(OrderStatus status) {
        switch(status) {
            case BAGGING: return baggingSlots;
            case STUFFING: return stuffingSlots;
            case READY: return readySlots;
            default: return null;
        }
    }

    public Order getFirstOffscreen(OrderStatus status) {
        for (Order order : offscreenOrders) {
            if (order.getStatus() == status) return order;
        }
        return null;
    }

    public void handleCarPathing(Car car) {
        if (carAtDoor != null) {if (carAtDoor.equals(car)) return;}
        if (car.getOrder() == null) {
            pendingCars.remove(car);
            car.destroyObject();
            return;
        }
        if (car.getDisplay() == null) {
            pendingCars.remove(car);
            car.destroyObject();
            return;
        }
        else if (System.currentTimeMillis() - car.getLastPathTime() >= car.getPathSpeed()) {
            if (car.getCurrentPath().isEmpty() && carAtDoor != car) {
                car.pickSlot();
                handleCarPathing(car);
                return;
            }
            if (car.isStopped()) return;
            car.setLastPathTime(System.currentTimeMillis());
           if (car.getTargetSlot() == exitSlots[0] || car.getTargetSlot() == exitSlots[1]) arena.sendDebugMessage(car.getDisplay() + " Path: " + car.getCurrentPath());
           if (getExpoForOrder(car.getOrder()) != null && car.getTargetSlot() != pulledSlot) {
                Expo expo = getExpoForOrder(car.getOrder());
                if (getAdjacentSlots(car.getCurrentSlot()).contains(expo.getCurrentSlot()) && expo.getExpoState().equals(ExpoState.BUSY)) car.setStopped(true);
                return;
            }
            if (car.getCurrentPath().get(0).equals(carDestinationSlot) && car.getTargetSlot() == carDestinationSlot) {
                if (carAtDoor != null) return;
                carAtDoor = car;
                car.arrivedAtDoor();
                car.travelToNextSlot();
                return;
            }
            if (car.getCurrentPath().get(0).equals(pulledSlot)) {
                pullCar(car);
                return;
            }
            if (car.getCurrentPath().get(0).equals(exitSlots[0]) || car.getCurrentPath().get(0).equals(exitSlots[1])) {
                car.destroyObject();
                pendingCars.remove(car);
                return;
            }
            car.travelToNextSlot();
        }

    }


    public ArrayList<Integer> getAdjacentSlots(int slot) {
        int[] directions = {-9, 9, -1, 1, -10, -8, 10, 8}; // Up, down, left, right, diagonals
        int[] adjacentSlots = new int[directions.length];
        for (int i = 0; i < directions.length; i++) {
            adjacentSlots[i] = slot + directions[i];
        }
       ArrayList<Integer> slots = new ArrayList<>();
        for (int slotz : adjacentSlots) {slots.add(slotz);}
        return slots;
    }

    // Used when car is pulled at the door.
    public void pullCar(Car car) {
        if (pulledCars.size() == 3) return;
        car.destroyObject();
        if (carAtDoor == car) carAtDoor = null;
        player.playSound(player.getLocation(), Sound.WOOD_CLICK, 1f, 1.1f);
        pulledCars.add(car);
        pendingCars.remove(car);
        updatePulledIndicator();
    }

    // Used when car is pulled by an Expo.
    public void sendCarToPulledSpot(Car car) {
    car.setTargetSlot(pulledSlot);
    car.setPathSpeed(Math.round(car.getPathSpeed() - (car.getPathSpeed() * .3)));
    car.pickSlot();
    }

    public boolean hasCar() {return carAtDoor != null;}

    public void pathingRunnable() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!getState().equals(PuzzleGameState.ACTIVE)) {
                    cancel();
                    resetPuzzle();
                }
                if (!getInventory().getTitle().equalsIgnoreCase(type.getDisplay())) {
                    cancel();
                    return;
                }
                if (isComplete()) {
                    cancel();
                    return;
                }
                if (arena.getState() != GameState.LIVE || !getState().equals(PuzzleGameState.ACTIVE)) {
                    cancel();
                    return;
                }
                for (Car obj : pendingCars.keySet()) {
                    handleCarPathing(obj);
                }
            }

        }.runTaskTimer(DeltaCraft.getInstance(), 2L, 1L);
    }
    public void deliverOrder(Order order) {
    if (order.getCar() != carAtDoor) {
        if (canExpedite()) deliverOrder(order, getFirstAvailableExpo());
        else player.playSound(player.getLocation(), Sound.CAT_HISS, .7f, .8f);
        return;
    }
    if (order.getStatus() != OrderStatus.READY) pullCar(order.getCar());
    else if (carAtDoor == order.getCar()) {
    pendingCars.remove(order.getCar());
    order.getCar().destroyObject();
    order.setStatus(OrderStatus.DELIVERED);
    order.destroyObject();
    activeOrders.remove(order);
    moveBags(OrderStatus.READY);
    addScore();
    carAtDoor = null;
      }
    }

    public void updatePulledIndicator() {
        ItemBuilder item = new ItemBuilder(Material.STAINED_GLASS_PANE);
        if (pulledCars.size() == 0) {
            item.setDisplayName(ChatColor.GREEN + "Pulled Counter");
            item.setDurability((short) 13);
            item.setAmount(1);
            item.setLore(Arrays.asList(ChatColor.GRAY + "You don't have any", ChatColor.GRAY + "cars pulled right now!"));
        }
        else {
            item.setDisplayName(ChatColor.LIGHT_PURPLE + "Pulled Counter");
            item.setAmount(pulledCars.size());
            item.setDurability((short) 10);
            ArrayList<String> lore = new ArrayList<>();
           if (pulledCars.size() != 1) lore.add(ChatColor.YELLOW + "You have " + ChatColor.RED + pulledCars.size() + ChatColor.YELLOW + " cars pulled:");
           else lore.add(ChatColor.YELLOW + "You have " + ChatColor.RED + pulledCars.size() + ChatColor.YELLOW + " car pulled:");
           for (Car car : pulledCars) {
               lore.add(ChatColor.YELLOW + "- " + car.getDisplay());
           }
           item.setLore(lore);
        }
        getInventory().setItem(pulledSlot, item.build());
    }
    public void addScore() {
        score = score + 1;
        player.playSound(player.getLocation(), Sound.SUCCESSFUL_HIT, 1f, score/10f);

        if (score > 30) {
            setComplete(true);
            long seconds = ticks / 20;
            long minutes = seconds / 60;
            long secondsReal = seconds - (minutes*60);
            if (minutes > 0) player.sendMessage(HerobrinePVPCore.translateString("&aYou served &6" + (score - 1)  + "&a cars in &6" + minutes + "&a minutes and &6" + secondsReal  + "&a seconds!"));
            else player.sendMessage(HerobrinePVPCore.translateString("&aYou served &6" + (score - 1) + "&a cars in &6" + secondsReal + "&a seconds!"));
            player.closeInventory();
            try {
                Method onComplete = object.getClass().getDeclaredMethod("onComplete", Player.class);
                onComplete.setAccessible(true);
                onComplete.invoke(object, player);
                onComplete.setAccessible(false);
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException ex) {
                player.sendMessage(ChatColor.RED + "There was an error completing this puzzle.. Does this object implement PuzzleObject?");
            }
        }
    }
    public ArrayList<Car> getPulledCars() {return pulledCars;}
    public ArrayList<Car> getCarSpawnWaitingList() {return carSpawnWaitingList;}
    public boolean isCarPulled(Car car) {return pulledCars.contains(car);}

    public boolean canExpedite() {
        for (Expo expo : expos) {
            if (expo.getExpoState().equals(ExpoState.ACTIVE)) return true;
        }
        return false;
    }
    public Expo getFirstAvailableExpo() {
        for (Expo expo : expos) {
            if (expo.getExpoState().equals(ExpoState.ACTIVE)) return expo;
        }
        return null;
    }

    public int[] getExitSlots() {
        return exitSlots;
    }

    public void deliverOrder(Order order, Expo expo) {
        order.setStatus(OrderStatus.DELIVERED);
        order.destroyObject();
        activeOrders.remove(order);
        expo.setOrder(order);
        moveBags(OrderStatus.READY);
    }
    @Override
    public boolean isPrimaryPuzzle() {
        return false;
    }
    public Inventory getInventory() {return player.getOpenInventory().getTopInventory();}

    public void resetPuzzle() {
        for (Order order : activeOrders) {order.destroyObject();}
        for (Car car : pendingCars.keySet()) {car.destroyObject();}
        for (Expo expo : expos) {expo.destroyObject();}
        carAtDoor = null;
        offscreenOrders.clear();
        objectMap.clear();
        pendingCars.clear();
        pulledCars.clear();
        carSpawnWaitingList.clear();
        expos.clear();
        activeOrders.clear();
        ticks = 0;
        score = 0;
        sequence = 0;
        maxObjects = 1;
    }
    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!e.getClickedInventory().getTitle().equals(type.getDisplay())) return;
        Player player = (Player) e.getWhoClicked();
        if (!activePlayers.contains(player.getUniqueId())) return;
        if (!Manager.isPlaying(player)) return;
        Arena arena = Manager.getArena(player);
        if (!arena.getGame().equals(Games.DELTARUNE) || !arena.getState().equals(GameState.LIVE)) return;
        e.setCancelled(true);
        if (!getState().equals(PuzzleGameState.ACTIVE)) return;
        boolean disregardInput = false;
        if (activePlayers.size() > type.getMaxPlayers()) {
            int extraPlayers = activePlayers.size() - type.getMaxPlayers();
            while (extraPlayers != 0) {
                Player extraPlayer = Bukkit.getPlayer(activePlayers.get(type.getMaxPlayers() - 1 + extraPlayers));
                extraPlayer.closeInventory();
                extraPlayer.sendMessage(ChatColor.RED + "Hey you! You're not supposed to be doing this puzzle right now!");
                extraPlayer.playSound(extraPlayer.getLocation(), Sound.ENDERMAN_TELEPORT, 1f, 1f);
                if (player == extraPlayer) disregardInput = true;
                extraPlayers--;
            }
        }
        if(disregardInput) return;
        if(e.getCurrentItem() == null) return;
        if (e.getCurrentItem().getType() == Material.AIR) return;
        NBTReader reader = new NBTReader(e.getCurrentItem());
        String uuid = null;
        if (reader.getStringNBT("uuid").isPresent()) uuid = reader.getStringNBT("uuid").get();
        if (uuid == null) return;
        try {
            DeltaObject obj = arena.getDeltaGame().getObjectManager().getActiveObjects().get(UUID.fromString(uuid));

            if (obj instanceof Car) {
                Car car = (Car) obj;
                if (car != carAtDoor) return;
                deliverOrder(car.getOrder());
                return;
            }
            if (obj instanceof Order) {
                Order order = (Order) obj;
                if (order.getStatus() != OrderStatus.READY) return;
                if (!order.getCar().isSequenced()) return;
                deliverOrder(order);
            }
            if (obj instanceof Expo) {
                Expo expo = (Expo) obj;
                if (expo.getExpoState() != ExpoState.ACTIVE) return;
                for (Order order : getAllOrdersWithStatus(OrderStatus.READY)) {
                    if (order.getCar().isSequenced() && order.getCar() != carAtDoor) {
                        deliverOrder(order, expo);
                        player.playSound(player.getLocation(), Sound.CHICKEN_EGG_POP, 1f, 1.1f);
                        break;
                    }
                }
            }

        }
        catch(IllegalArgumentException ex) {return;}

    }

    @EventHandler
    public void onStatusUpdate(OrderStatusUpdateEvent e) {
        if (e.getStatus().equals(OrderStatus.DELIVERED)) return;
        if (carAtDoor != null) {
            if (carAtDoor.getOrder().equals(e.getOrder()) && e.getStatus().equals(OrderStatus.READY)) carAtDoor.arrivedAtDoor();
        }
        boolean wasOffscreen = e.getOrder().isOffScreen();
        if (isScreenFull(e.getStatus()) && !e.getStatus().equals(OrderStatus.READY)) {
            updateRow(e.getOrder());
            if (!offscreenOrders.contains(e.getOrder())) offscreenOrders.add(e.getOrder());
            getInventory().setItem(e.getOrder().getCurrentSlot(), new ItemStack(Material.AIR));
            updateOffscreenCounter();
        }
        else if (!isScreenFull(e.getStatus()) && !e.getStatus().equals(OrderStatus.READY)) {
          if(wasOffscreen) offscreenOrders.remove(e.getOrder());
          if(!wasOffscreen) e.getOrder().setMoving(true);
          updateRow(e.getOrder());
          e.getOrder().pickSlot();
        }
        moveAllBags();

    }
}
