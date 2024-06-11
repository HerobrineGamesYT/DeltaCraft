package net.herobrine.deltacraft.objects.inventory;

import net.herobrine.deltacraft.DeltaCraft;
import net.herobrine.deltacraft.events.OrderStatusUpdateEvent;
import net.herobrine.deltacraft.objects.*;
import net.herobrine.deltacraft.objects.puzzles.utils.OrderNames;
import net.herobrine.deltacraft.objects.puzzles.utils.OrderStatus;
import net.herobrine.deltacraft.utils.NBTReader;
import net.herobrine.gamecore.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Predicate;

public class Order extends DeltaObject implements InventoryObject {
    Inventory inventory;
    public OrderStatus status;
    OrderNames name;
    BukkitRunnable prepTimer;
    boolean hasEasterEgg;
    boolean isOffScreen;
    HashMap<DeltaObject, DeltaObject> orderToCarMap = new HashMap<>();
    int stuffComplexity;
    int bagComplexity;

    int prepTime;
    long ticks = 0;

    int orderRow;
    int currentSlot = 0;

    boolean isMoving = false;

    // Prep times are in seconds, from lowest to highest complexity.
    int[] baggingTimes = new int[] {3, 5, 10};
    int[] stuffingTimes = new int[] {1, 4, 15};
    int[] baseOrderSlots = new int[] {16,15,14};
    int[] baggingSlots;
    int[] stuffingSlots;
    int[] readySlots;
    ItemStack stack;
    NBTReader reader;
    ItemBuilder item = new ItemBuilder(Material.REDSTONE);
    public Order(ObjectTypes type, Objects object, int id, UUID uuid, Inventory inventory) {
        super(type, object, id, uuid);
        this.inventory = inventory;
        ThreadLocalRandom rand = ThreadLocalRandom.current();
        int sComplexity = rand.nextInt(1,4);
        int bComplexity = rand.nextInt(1,4);
        this.stuffComplexity = sComplexity;
        this.bagComplexity = bComplexity;
        while(this.name == null) {this.name = generateName();}
        this.hasEasterEgg = rand.nextDouble() <= .01 && this.name.hasEasterEgg();
        this.status = OrderStatus.BAGGING;
    }

    @Override
    public void pickSlot() {
        if (isOffScreen) {
            if (getCurrentSlot() != 0) inventory.setItem(getCurrentSlot(), new ItemStack(Material.AIR));
            return;
        }
        setupItemInfo();
        // Setup and start prep timers based on order status.
    switch (status) {
        case BAGGING:
            this.prepTime = baggingTimes[bagComplexity - 1];
            startPrepTimer();
            break;
        case STUFFING:
            this.prepTime = stuffingTimes[stuffComplexity - 1];
            startPrepTimer();
            break;
        default: break;
    }
    if (!isOffScreen) {
        if (isMoving && getCurrentSlot() != 0) {
            inventory.setItem(getCurrentSlot(), new ItemStack(Material.AIR));
            isMoving = false;
        }
        setCurrentSlot(baseOrderSlots[status.ordinal()] + (9*orderRow));
        inventory.setItem(getCurrentSlot(), reader.toBukkit());
    }
    }


    public void pickSlotWithoutTimer() {
        setupItemInfo();
        if (isOffScreen) {
            if (getCurrentSlot() != 0) inventory.setItem(getCurrentSlot(), new ItemStack(Material.AIR));
        }
        else {
            if (isMoving && getCurrentSlot() != 0) {
                inventory.setItem(getCurrentSlot(), new ItemStack(Material.AIR));
                isMoving = false;
            }
            setCurrentSlot(baseOrderSlots[status.ordinal()] + (9*orderRow));
            inventory.setItem(getCurrentSlot(), reader.toBukkit());
        }
    }
    // Will adapt the Order's ItemBuilder based on the order's current status. This makes it so that the pick slot methods only have to set the slot and update the GUI.
    public void setupItemInfo() {
        switch (status) {
            case BAGGING:
                //Setup item info for orders in BAGGING.
                item.setDisplayName(ChatColor.RED + name.getDisplay() + "'s" + " Order");
                item.setLore(Arrays.asList(ChatColor.YELLOW + "Currently " + ChatColor.RED + "Bagging", ChatColor.YELLOW + "Bagging Complexity: " + ChatColor.RED + bagComplexity,
                        ChatColor.GRAY + "",
                        ChatColor.YELLOW + "Moving to " + ChatColor.BLUE + "Stuffing " + ChatColor.YELLOW + "in " + ChatColor.RED + baggingTimes[bagComplexity - 1] + "s"));

                stack = item.build();
                reader = new NBTReader(stack);
                reader.writeStringNBT("order_name", name::name);
                reader.writeStringNBT("uuid", uuid::toString);
                break;
            case STUFFING:
                //Setup item info for orders in STUFFING.
                item.setDisplayName(ChatColor.BLUE + name.getDisplay() + "'s" + " Order");
                item.setType(Material.INK_SACK);
                item.setDurability((short) 4);
                item.setLore(Arrays.asList(ChatColor.YELLOW + "Currently " + ChatColor.BLUE + "Stuffing", ChatColor.YELLOW + "Stuffing Complexity: " + ChatColor.BLUE + stuffComplexity,
                        ChatColor.GRAY + "",
                        ChatColor.YELLOW + "Moving to " + ChatColor.GREEN + "Ready " + ChatColor.YELLOW + "in " + ChatColor.RED + stuffingTimes[stuffComplexity - 1] + "s"));

                stack = item.build();
                reader = new NBTReader(stack);
                reader.writeStringNBT("order_name", name::name);
                reader.writeStringNBT("uuid", uuid::toString);
                break;
            case READY:
                //Setup item info for READY orders.
                item = new ItemBuilder(Material.EMERALD);
                item.setDisplayName(ChatColor.GREEN + name.getDisplay() + "'s" + " Order");
                if (!getCar().isSequenced()) item.setLore(Arrays.asList(ChatColor.YELLOW + "Currently" + ChatColor.GREEN + " Ready", ChatColor.GRAY + "", ChatColor.YELLOW + "Waiting on " + ChatColor.RED + "Sequencing" + ChatColor.YELLOW + "..."));
                else {
                    item.setGlow(true);
                    item.setAmount(getCar().getSequence());
                    item.setLore(Arrays.asList(ChatColor.YELLOW + "Currently" + ChatColor.GREEN + " Ready", ChatColor.YELLOW + "Order Number: " + ChatColor.GREEN + getCar().getSequence(),
                            ChatColor.GRAY + "", ChatColor.YELLOW + "Click to deliver!"));
                }

                stack = item.build();
                reader = new NBTReader(stack);
                reader.writeStringNBT("order_name", name::name);
                reader.writeStringNBT("uuid", uuid::toString);
                break;
            default: return;
        }
    }

    public OrderNames generateName() {
        ThreadLocalRandom rand = ThreadLocalRandom.current();
        int num = rand.nextInt(OrderNames.values().length);

        OrderNames name = OrderNames.values()[num];
        for (ItemStack stack : inventory.getContents()) {
            if (stack != null) {
                if (hasName(name, stack)) return null;
            }
        }
        return name;
    }

    public boolean hasName(OrderNames name, ItemStack ref) {
        NBTReader reader = new NBTReader(ref);
        try {return name.equals(OrderNames.valueOf(reader.getStringNBT("order_name").get()));}
        catch(NullPointerException | IllegalArgumentException e) {return false;}

    }

    public void setMoving(boolean isMoving) {this.isMoving = isMoving;}
    public boolean isMoving() {return isMoving;}

    public void startPrepTimer() {
      //  arena.sendDebugMessage("Starting prep timer. Time: " + prepTime);
        this.prepTimer = new BukkitRunnable() {
            @Override
            public void run() {
                if (getState() != ObjectState.ACTIVE) {
                    cancel();
                    return;
                }
                if (prepTime == 0) {
                    cancel();
                    if (status.equals(OrderStatus.BAGGING)) {
                        setStatus(OrderStatus.STUFFING);
                        return;
                    }
                    if (status.equals(OrderStatus.STUFFING)) {
                        setStatus(OrderStatus.READY);
                        return;
                    }
                }
                if (hasEasterEgg && ticks % 10L == 0) {
                    prepTime--;
                    if (status.equals(OrderStatus.BAGGING)) item.updateLine(3, ChatColor.YELLOW + "Moving to " + ChatColor.BLUE + "Stuffing " + ChatColor.YELLOW + "in " + ChatColor.RED + prepTime + "s");
                    else if (status.equals(OrderStatus.STUFFING)) item.updateLine(3, ChatColor.YELLOW + "Moving to " + ChatColor.GREEN + "Ready " + ChatColor.YELLOW + "in " + ChatColor.RED + prepTime + "s");
                    updateItem();
                }
                if (!hasEasterEgg && ticks % 20L == 0) {
                    prepTime--;
                    if (status.equals(OrderStatus.BAGGING)) item.updateLine(3, ChatColor.YELLOW + "Moving to " + ChatColor.BLUE + "Stuffing " + ChatColor.YELLOW + "in " + ChatColor.RED + prepTime + "s");
                    else if (status.equals(OrderStatus.STUFFING)) item.updateLine(3, ChatColor.YELLOW + "Moving to " + ChatColor.GREEN + "Ready " + ChatColor.YELLOW + "in " + ChatColor.RED + prepTime + "s");
                    updateItem();
                }
             ticks++;
            }
        };

        prepTimer.runTaskTimer(DeltaCraft.getInstance(), 1L, 1L);
    }

    public void updateItem() {
        stack = item.build();
        reader = new NBTReader(stack);
        reader.writeStringNBT("order_name", name::name);
        reader.writeStringNBT("uuid", uuid::toString);
        inventory.setItem(getCurrentSlot(), reader.toBukkit());
    }

    public boolean isOffScreen() {return isOffScreen;}
    public void setOffScreen(boolean offScreen) {this.isOffScreen = offScreen;}
    @Override
    public int getCurrentSlot() {return currentSlot;}

    @Override
    public void setCurrentSlot(int slot) {
       if(getCurrentSlot() != 0) inventory.setItem(getCurrentSlot(), new ItemStack(Material.AIR));
       this.currentSlot = slot;
       if(slot != 0 && !isOffScreen) updateItem();
    }

    @Override
    public void initObject() {
    state = ObjectState.ACTIVE;
    setStatus(OrderStatus.BAGGING);
    }

    @Override
    public void destroyObject() {
    state = ObjectState.DESTROYED;
    prepTimer.cancel();
    arena.getDeltaGame().getObjectManager().unregisterObject(uuid);
    if (inventory != null) inventory.remove(reader.toBukkit());
    }
    public void setStatus(OrderStatus status) {
        this.status = status;
        OrderStatusUpdateEvent event = new OrderStatusUpdateEvent(status, this);
        Bukkit.getServer().getPluginManager().callEvent(event);
       // This will be run by the Puzzle Class instead after it verifies in the event that the order won't need to be offscreen in the next stage. pickSlot();
    }
    public OrderStatus getStatus() {return status;}
    public Car getCar() {return (Car) orderToCarMap.get(this);}
    public void assignOrderToCar(Car car) {orderToCarMap.put(this, car);}

    public OrderNames getName() {return name;}

    public int getRow() {return orderRow;}

    public void setRowAndSlot(int row) {
        this.orderRow = row;
        this.isOffScreen = row > 3;
        if(!isOffScreen) {
            setupItemInfo();
            setCurrentSlot(baseOrderSlots[status.ordinal()] + (9*orderRow));
            inventory.setItem(getCurrentSlot(), reader.toBukkit());
        }
    }
    public void setRow(int row) {
        this.orderRow = row;
        this.isOffScreen = row > 3;
    }

}

