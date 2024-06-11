package net.herobrine.deltacraft.objects.puzzles;

import java.util.ArrayList;
import net.herobrine.gamecore.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class PuzzleMenus {



 ThreadLocalRandom rand;
    public Inventory createGUI(PuzzleTypes type) {
        switch(type) {
            case LIGHT_PUZZLE:
                return generateLightsInventory();
            case SEQUENCE_PUZZLE:
                return generateSequenceInventory();
            case LIGHT_SWITCH:
                return generateLightSwitchInventory();
            case BUTTON_TIMING:
                return generateButtonTimingInventory();
            case LANE_MERGE:
                return generateLaneMergingInventory();
            case DOOR:
                return generateDoorInventory();
            default: return null;

        }
    }


    public Inventory generateLightsInventory() {
        rand = ThreadLocalRandom.current();
        Inventory inventory = Bukkit.createInventory(null, InventoryType.DISPENSER, PuzzleTypes.LIGHT_PUZZLE.getDisplay());
        int[] data = new int[] {14, 3, 11, 13,10,5};
        String[] glassTypes = new String[] {ChatColor.RED + "RED", ChatColor.BLUE + "AQUA", ChatColor.DARK_BLUE + "BLUE", ChatColor.DARK_GREEN + "GREEN",
                ChatColor.LIGHT_PURPLE + "PURPLE", ChatColor.GREEN + "LIME"};
        int slot = 0;
        while (slot < 9) {
            int randIndex = rand.nextInt(0,5);
            ItemBuilder item = new ItemBuilder(Material.STAINED_GLASS_PANE);
            item.setDurability((short) data[randIndex]);
            item.setDisplayName(glassTypes[randIndex]);
            inventory.setItem(slot, item.build());
            slot++;
        }
        return inventory;
    }

    public Inventory generateLightSwitchInventory() {
        rand = ThreadLocalRandom.current();
        Inventory inventory = Bukkit.createInventory(null, InventoryType.DISPENSER, PuzzleTypes.LIGHT_SWITCH.getDisplay());
        int off = 14;
        int on = 5;

        int slot = 0;
        while (slot < 9) {
            int randNumb = rand.nextInt(1,3);
            ItemBuilder item = new ItemBuilder(Material.STAINED_GLASS_PANE);
            item.setDurability((short)off);
            item.setDisplayName(ChatColor.YELLOW + "Click to turn on!");
            if (randNumb < 2) {
                item.setDurability((short)on);
                item.setDisplayName(ChatColor.GRAY + "");
            }

            inventory.setItem(slot, item.build());
            slot++;
        }
        return inventory;
    }

    public Inventory generateSequenceInventory() {
        rand = ThreadLocalRandom.current();
        Inventory inventory = Bukkit.createInventory(null, 18, PuzzleTypes.SEQUENCE_PUZZLE.getDisplay());

        int i = 1;
        List<Integer> slots = new ArrayList<>();
        slots.addAll(Arrays.asList(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17));
        ItemBuilder pane = new ItemBuilder(Material.STAINED_GLASS_PANE);
        pane.setDisplayName(ChatColor.YELLOW + "Click me!");
        pane.setDurability((short) 14);
        while (i <19) {
            pane.setAmount(i);
            int randIndex = rand.nextInt(0, slots.size());
            int randSlot = slots.get(randIndex);
            inventory.setItem(randSlot, pane.build());
            slots.remove(randIndex);
            i++;
        }
        return inventory;
    }

    public Inventory generateButtonTimingInventory() {
        Inventory inventory = Bukkit.createInventory(null, 54, PuzzleTypes.BUTTON_TIMING.getDisplay());
        int[] fillerSlots = new int[] {0,2,3,4,5,6,7,8,9,17,18,26,27,35,36,44,45,47,48,49,50,51,52,53};
        int[] defaultIndicatorSlots = new int[] {1, 46};
        int[] defaultWireSlots = new int[] {10,11,12,13,14,15};
        int[] buttonSlots = new int[] {16,25,34,43};
        int[] wireSlots = new int[] {19,20,21,22,23,24,28,29,30,31,32,33,37,38,39,40,41,42};
        ItemBuilder glass = new ItemBuilder(Material.STAINED_GLASS_PANE);
        ItemBuilder button = new ItemBuilder(Material.STAINED_CLAY);


        for (int slot : fillerSlots) {
            glass.setDurability((short) 7);
            glass.setDisplayName(ChatColor.GRAY + "");
            inventory.setItem(slot, glass.build());
        }

        for (int slot : defaultIndicatorSlots) {
            glass.setDurability((short) 10);
            glass.setDisplayName(ChatColor.GRAY + "");
            inventory.setItem(slot, glass.build());
        }

        for (int slot : defaultWireSlots) {
            glass.setDurability((short)14);
            glass.setDisplayName(ChatColor.GRAY + "");
            inventory.setItem(slot, glass.build());
        }

        for (int slot : buttonSlots) {
            if (slot == 16) {
                button.setDurability((short)5);
                button.setDisplayName(ChatColor.YELLOW + "Click me!");
                inventory.setItem(slot, button.build());
                continue;
            }
            button.setDurability((short) 14);
          button.setDisplayName(ChatColor.YELLOW + "Complete the previous line to activate!");
          inventory.setItem(slot, button.build());
        }
        for (int slot : wireSlots) {
         glass.setDurability((short) 15);
         glass.setDisplayName(ChatColor.GRAY + "");
         inventory.setItem(slot, glass.build());
        }

        return inventory;
    }

    public Inventory generateLaneMergingInventory() {
       Inventory inventory = Bukkit.createInventory(null, 54, PuzzleTypes.LANE_MERGE.getDisplay());
        int[] fillerSlots = new int[] {0,2,5,6,8,9,17,18,26,27,35,36,44,45,46,47,48,49,51,52,53};
        int[] laneStarters = new int[] {1, 3, 7};
        int scoreSlot = 4;
        int goalSlot = 50;

        ItemBuilder glass = new ItemBuilder(Material.STAINED_GLASS_PANE);
        ItemBuilder redstone = new ItemBuilder(Material.REDSTONE);
        for (int slot : fillerSlots) {
            glass.setDurability((short) 7);
            glass.setDisplayName(ChatColor.GRAY + "");
            inventory.setItem(slot, glass.build());
        }

        int i = 1;
        ChatColor color = null;
        for (int slot : laneStarters) {
            if (i==1) {
                glass.setDurability((short) 14);
                color = ChatColor.RED;
            }
            if (i==2) {
                glass.setDurability((short)5);
                color = ChatColor.GREEN;
            }
            if (i==3) {
                glass.setDurability((short)11);
                color = ChatColor.BLUE;
            }
            glass.setDisplayName(color + "Lane" + i);

            inventory.setItem(slot, glass.build());
            i++;
        }

        redstone.setDisplayName(ChatColor.RED + "Objects Sequenced");
        inventory.setItem(scoreSlot, redstone.build());

        glass.setDisplayName(ChatColor.YELLOW + "Merge Point");
        glass.setDurability((short) 4);
        inventory.setItem(goalSlot, glass.build());
       return inventory;
    }

    public Inventory generateDoorInventory() {
        Inventory inventory = Bukkit.createInventory(null, 54, PuzzleTypes.DOOR.getDisplay());
        int[] fillerSlots = new int[] {0,1,2,8,9,17,18,26,27,35,36,44,50,51,52,53};
        int[] statusSlots = new int[] {5,6,7};
        int[] exitSlots = new int[] {46,47};
        int spawnSlot = 3;
        int scoreSlot = 4;
        int pulledTracker = 48;
        int departureTrackerSlot = 45;
        int departureRateSlot = 49;

        ItemBuilder glass = new ItemBuilder(Material.STAINED_GLASS_PANE);
        ItemBuilder redstone = new ItemBuilder(Material.REDSTONE);
        ItemBuilder clock = new ItemBuilder(Material.WATCH);
       for (int slot : fillerSlots) {
           glass.setDurability((short) 7);
           glass.setDisplayName(ChatColor.GRAY + "");
           inventory.setItem(slot, glass.build());
       }
       int i = 1;
       for (int slot : statusSlots) {
           if (i==1) {
               glass.setDurability((short) 5);
               glass.setDisplayName(ChatColor.GREEN + "Ready");
           }
           if (i==2) {
               glass.setDurability((short)11);
               glass.setDisplayName(ChatColor.BLUE + "Stuffing");
           }
           if (i==3) {
               glass.setDurability((short)14);
               glass.setDisplayName(ChatColor.RED + "Bagging");
           }
           inventory.setItem(slot, glass.build());
           i++;
       }

       glass.setDurability((short) 4);
       glass.setAmount(1);
       glass.setDisplayName(ChatColor.YELLOW + "Spawn Point");
       inventory.setItem(spawnSlot, glass.build());

       redstone.setDisplayName(ChatColor.RED + "Served Cars");
       inventory.setItem(scoreSlot, redstone.build());

       glass.setDurability((short) 5);
       glass.setDisplayName(ChatColor.GREEN + "Departure Mark");
       glass.setLore(Arrays.asList(ChatColor.GRAY + "A car is counted as departed", ChatColor.GRAY + "once they pass this point."));
       inventory.setItem(departureTrackerSlot, glass.build());

       glass.setDurability((short) 13);
       glass.setDisplayName(ChatColor.GREEN + "Pulled Counter");
       glass.setLore(Arrays.asList(ChatColor.GRAY + "You don't have any", ChatColor.GRAY + "cars pulled right now!"));
       inventory.setItem(pulledTracker, glass.build());

        for (int slot : exitSlots) {
            glass.setDurability((short) 14);
            glass.setDisplayName(ChatColor.RED + "Exit");
            glass.setLore(ChatColor.GRAY + "Cars will exit here!");
            inventory.setItem(slot, glass.build());
        }

       clock.setDisplayName(ChatColor.GREEN + "Departure Rate");
       inventory.setItem(departureRateSlot, clock.build());
       return inventory;
    }

}
