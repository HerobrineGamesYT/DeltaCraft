package net.herobrine.deltacraft.objects.puzzles.type;

import net.herobrine.core.HerobrinePVPCore;
import net.herobrine.deltacraft.DeltaCraft;
import net.herobrine.deltacraft.objects.DeltaObject;
import net.herobrine.deltacraft.objects.ObjectState;
import net.herobrine.deltacraft.objects.Objects;
import net.herobrine.deltacraft.objects.inventory.Car;
import net.herobrine.deltacraft.objects.puzzles.Puzzle;
import net.herobrine.deltacraft.objects.puzzles.PuzzleGameState;
import net.herobrine.deltacraft.objects.puzzles.PuzzleTypes;
import net.herobrine.gamecore.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class LaneMergePuzzle extends Puzzle {
    public LaneMergePuzzle(DeltaObject object, PuzzleTypes type) {
        super(object, type);
    }
    HashMap<DeltaObject, Integer> objectMap = new HashMap<>();

    ItemBuilder scoreKeeper = new ItemBuilder(Material.REDSTONE);

    Arena arena;
    Player player;
    int maxObjects = 1;
    int goalSlot = 50;
    int scoreSlot = 4;
    int score = 1;
    int currentSequence = 1;
    int nextSequence = 1;
    long spawnSpeed = 20;
    int ticks = 0;
    boolean hasCarSelected = false;
    Car selectedCar;
    BukkitRunnable spawnRunnable;
    ArrayList<UUID> scoredCars = new ArrayList<>();
    @Override
    public void startPuzzle(Player player) {
        setState(PuzzleGameState.ACTIVE);
        if (activePlayers.size() >= type.getMaxPlayers()) {
            player.sendMessage(ChatColor.RED + "This puzzle is already being completed!");
            return;
        }
        activePlayers.add(player.getUniqueId());

        this.player = player;
        this.arena = Manager.getArena(player);
        player.openInventory(DeltaCraft.getInstance().getPuzzleMenuManager().createGUI(type));
        scoreKeeper.setDisplayName(ChatColor.RED + "Objects Sequenced");
        scoreKeeper.setAmount(score);



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

                if (ticks % spawnSpeed == 0) spawn();
                ticks++;
            }
        };
    spawnRunnable.runTaskTimer(DeltaCraft.getInstance(), 2L, 1L);

    new BukkitRunnable() {
        @Override
        public void run() {
            pathingRunnable();
        }
    }.runTaskLater(DeltaCraft.getInstance(), 2L);
    }


    public void spawn() {
        int[] spawnSlots = new int[] {10, 12, 16};
        ThreadLocalRandom rand = ThreadLocalRandom.current();
        int index = rand.nextInt(1,4);
        Car car = null;
        if (getInventory().getItem(spawnSlots[index - 1]) != null) return;
        if (objectMap.size() >= maxObjects) {
            player.sendMessage(ChatColor.RED + "[SEQUENCER DEBUG] " + ChatColor.WHITE + "Too many objects in the map! Not spawning...");
            return;
        }
        switch (spawnSlots[index - 1]) {
            case 10:
                car = (Car) arena.getDeltaGame().getObjectManager().createInventoryObject(Objects.CAR, getInventory());
                objectMap.put(car, 10);
                car.setCurrentSlot(10);
                car.setDurability(14);
                car.setTargetSlot(goalSlot);
                car.setColor(ChatColor.RED);
                car.initObject();
                break;
            case 12:
                car = (Car) arena.getDeltaGame().getObjectManager().createInventoryObject(Objects.CAR, getInventory());
                objectMap.put(car, 12);
                car.setCurrentSlot(12);
                car.setDurability(5);
                car.setTargetSlot(goalSlot);
                car.setColor(ChatColor.GREEN);
                car.setPathSpeed(800);
                car.initObject();
                break;
            case 16:
                car = (Car) arena.getDeltaGame().getObjectManager().createInventoryObject(Objects.CAR, getInventory());
                objectMap.put(car, 16);
                car.setCurrentSlot(16);
                car.setDurability(11);
                car.setTargetSlot(goalSlot);
                car.setColor(ChatColor.BLUE);
                car.initObject();
                break;
            default: return;

        }

    }

    public void handlePathing(Car car) {
        if (car.getCurrentPath().isEmpty()) {
             car.pickSlot();
          //  if (car.getCurrentSlot() == 10) car.getCurrentPath().addAll(Arrays.asList(20, 30, 40 , 50));
          //  if (car.getCurrentSlot() == 12) car.getCurrentPath().addAll(Arrays.asList(21, 31, 40, 41, 50));
          //  if (car.getCurrentSlot() == 16) car.getCurrentPath().addAll(Arrays.asList(25, 33, 41, 50));
            handlePathing(car);
        }
        else if (System.currentTimeMillis() - car.getLastPathTime() >= car.getPathSpeed()) {
            if (car.hasTarget()) {
                Car target = car.getTarget();
                //TODO make car pathfind to target's current slot.
            }
            if (car.isStopped()) return;

            boolean hasScored = false;
            if (car.getCurrentPath().get(0).equals(goalSlot) && !scoredCars.contains(car.getUUID())) {
                hasScored = true;
                scoredCars.add(car.getUUID());
                if (selectedCar == car) {
                    selectedCar = null;
                    hasCarSelected = false;
                }
                car.destroyObject();
                if (car.isSequenced() && car.getSequence() == currentSequence) {
                    score = score + 1;
                    player.playSound(player.getLocation(), Sound.SUCCESSFUL_HIT, 1f, score/10f);
                    currentSequence = currentSequence + 1;
                }
                else if (car.isSequenced() && car.getSequence() < currentSequence) {
                    player.playSound(player.getLocation(), Sound.FIRE_IGNITE, 1f, .7f);
                }
                else {
                    score = score - 1;
                    player.playSound(player.getLocation(), Sound.WITHER_HURT, .6f, .7f);
                    if (car.isSequenced()) currentSequence = car.getSequence() + 1;
                }
                scoreKeeper.setAmount(score);
                getInventory().setItem(scoreSlot, scoreKeeper.build());
                if (score < 1) {
                    player.sendMessage(ChatColor.RED + "Too many cars came unsequenced!");
                    player.playSound(player.getLocation(), Sound.GLASS, 1f, 0.7f);
                    player.closeInventory();
                    return;
                }
                if (score > 20) {
                    long seconds = ticks / 20;
                    long minutes = seconds / 60;
                    long secondsReal = seconds - (minutes*60);
                    setComplete(true);
                    if (minutes > 0) player.sendMessage(HerobrinePVPCore.translateString("&aYou sequenced &6" + (score - 1)  + "&a cars in &6" + minutes + "&a minutes and &6" + secondsReal  + "&a seconds!"));
                    else player.sendMessage(HerobrinePVPCore.translateString("&aYou sequenced &6" + (score - 1) + "&a cars in &6" + secondsReal + "&a seconds!"));

                    player.closeInventory();
                    try {
                        Method onComplete = object.getClass().getDeclaredMethod("onComplete", Player.class);
                        onComplete.setAccessible(true);
                        onComplete.invoke(object, player);
                        onComplete.setAccessible(false);
                    } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException ex) {
                        player.sendMessage(ChatColor.RED + "There was an error completing this puzzle.. Does this implement PuzzleObject?");
                    }
                    return;
                }
            }
            car.travelToNextSlot();
            if (!hasScored) objectMap.put(car, car.getCurrentSlot());
        }
    }

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
                Map<DeltaObject, Integer> updatedObjectMap = new HashMap<>();

                for (DeltaObject obj : objectMap.keySet()) {
                    if (arena.getState() != GameState.LIVE || !getState().equals(PuzzleGameState.ACTIVE)) {
                        cancel();
                        return;
                    }
                    handlePathing((Car) obj);
                    updatedObjectMap.put(obj, ((Car) obj).getCurrentSlot());
                }
                // Replace the original map with the updated copy
                objectMap.clear();
                for (DeltaObject obj : updatedObjectMap.keySet()) {
                    Car car = (Car) obj;
                    if (obj.getState() == ObjectState.ACTIVE) objectMap.put(obj, car.getCurrentSlot());
                }

            }

        }.runTaskTimer(DeltaCraft.getInstance(), 2L, 1L);
    }

    public void resetPuzzle() {
        maxObjects = 1;
        goalSlot = 50;
        score = 1;
        selectedCar = null;
        hasCarSelected = false;
        nextSequence = 1;
        currentSequence = 1;
        ticks = 0;
        spawnSpeed = 20;
        for (DeltaObject obj : objectMap.keySet()) { if(obj.getState() != ObjectState.DESTROYED) obj.destroyObject();}
        objectMap.clear();
        scoredCars.clear();
    }

    public Inventory getInventory() {
        return player.getOpenInventory().getTopInventory();
    }

    public DeltaObject getKeysByValue(HashMap<DeltaObject, Integer> map, int value) {
        for (Map.Entry<DeltaObject, Integer> entry : objectMap.entrySet()) {
            if (entry.getValue().equals(value)) return entry.getKey();
        }
        return null;
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
        if (getKeysByValue(objectMap, e.getSlot()) == null) disregardInput = true;
        if (getKeysByValue(objectMap, e.getSlot()) != null) {
            Car car = (Car) getKeysByValue(objectMap, e.getSlot());
            if (car.isSequenced() && !car.isStopped() && !hasCarSelected) disregardInput = true;
            if (car.isSequenced() && car.hasTarget()) disregardInput = true;
        }
        if (disregardInput) return;

        Car car = (Car) getKeysByValue(objectMap, e.getSlot());

        // SCRAPPED FOR NOW.
     //   if (hasCarSelected && car != selectedCar && car.isSequenced()) {
      //      selectedCar.setTarget(car);
      //      selectedCar.setStopped(false);
      //      player.playSound(player.getLocation(), Sound.CHICKEN_EGG_POP, 1f, 1f);
      //      selectedCar = null;
      //      hasCarSelected = false;
     //   }
        if (car.isStopped()) {
            car.setStopped(false);
            player.playSound(player.getLocation(), Sound.WOOD_CLICK, 1f, 1f);
            return;
        }
        if (nextSequence != 1) {
            hasCarSelected = true;
            selectedCar = car;
        }
        if (!car.isSequenced()) {
            car.setSequenced(true, nextSequence);
            nextSequence = nextSequence + 1;
        }
    }
    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        if (e.getInventory().getTitle().equalsIgnoreCase(type.getDisplay())) {
            resetPuzzle();
            Player player = (Player) e.getPlayer();
            activePlayers.remove(player.getUniqueId());
            if (activePlayers.size() == 0) setState(PuzzleGameState.INACTIVE);
        }
    }

}
