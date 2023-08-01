package net.herobrine.deltacraft.game;

import net.herobrine.core.HerobrinePVPCore;
import net.herobrine.core.SkullMaker;
import net.herobrine.gamecore.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

public class Menus {

    public static void applyClassSelector(Player player) {
        Inventory classSelector = Bukkit.createInventory(null, 54,
                ChatColor.translateAlternateColorCodes('&', "&c&lDELTACRAFT &7- Class Selector"));

        int[] classSlots = new int[] {29,30,31,32,33};
        ClassTypes[] classes = new ClassTypes[] {ClassTypes.HEALER_DELTACRAFT, ClassTypes.MAGE, ClassTypes.BERSERK, ClassTypes.ARCHER_DELTACRAFT, ClassTypes.TANK};
        int i = 0;
        for (ClassTypes type : classes) {

            ItemBuilder stack = new ItemBuilder(type.getMaterial());
            stack.setDisplayName(type.getDisplay());
            stack.setLore(Arrays.asList(type.getDescription()));

            ItemBuilder notSelected = new ItemBuilder(Material.INK_SACK, (short) 8);
            //slot+9 is where these items should go
            ItemBuilder selected = new ItemBuilder(Material.INK_SACK, (short) 10);

            notSelected.setDisplayName(ChatColor.YELLOW + "Click to select!");
            selected.setDisplayName(ChatColor.GREEN + "Selected");
            classSelector.setItem(classSlots[i], stack.build());
            classSelector.setItem(classSlots[i] + 9, notSelected.build());
            i++;

        }

        int[] playerHeadSlots = new int[] {4,3,5,2,6};
        i = 0;
        Arena arena = Manager.getArena(player);
        for (UUID uuid : arena.getPlayers()) {
            Player players = Bukkit.getPlayer(uuid);
            ItemBuilder skull = new ItemBuilder(Material.SKULL_ITEM, (short) 3);
            skull.setHead(players.getName());
            skull.setDisplayName(ChatColor.GREEN + players.getName());
            ItemBuilder readyItem = new ItemBuilder(Material.STAINED_GLASS_PANE, (short) 5);
            readyItem.setDisplayName(ChatColor.GREEN + "Ready");
            //slot+9 is where these items should go
            ItemBuilder notReadyItem = new ItemBuilder(Material.STAINED_GLASS_PANE, (short) 14);
            notReadyItem.setDisplayName(ChatColor.RED + "Not Ready");
            if (players == player) notReadyItem.setLore(Arrays.asList(ChatColor.GRAY + "Click to mark yourself as ready",
                    ChatColor.GRAY + "to start!", "", ChatColor.YELLOW + "Click to ready up!"));

            classSelector.setItem(playerHeadSlots[i], skull.build());

            if (arena.getDeltaGame().getReadiedPlayers().contains(players.getUniqueId())) classSelector.setItem(playerHeadSlots[i] + 9, readyItem.build());
            else classSelector.setItem(playerHeadSlots[i] + 9, notReadyItem.build());

            i++;
            }

            player.openInventory(setSelectedClasses(classSelector, arena, player));
        }



    public static Inventory setSelectedClasses(Inventory inventory, Arena arena, Player player) {

        int[] classSlots = new int[]{29, 30, 31, 32, 33};
        ClassTypes[] classArray = new ClassTypes[] {ClassTypes.HEALER_DELTACRAFT, ClassTypes.MAGE, ClassTypes.BERSERK, ClassTypes.ARCHER_DELTACRAFT, ClassTypes.TANK};
        ClassTypes ownClassType = null;
        for (UUID uuid : arena.getClasses().keySet()) {
            Player players = Bukkit.getPlayer(uuid);
        if (player.getUniqueId() == uuid) ownClassType = arena.getClass(uuid);
        }

        // slot+9 is where the items should go to place directly under their class icon
        for (int i = 0; i < 5; i++) {
            ItemBuilder notSelected = new ItemBuilder(Material.INK_SACK, (short) 8);
            notSelected.setDisplayName(ChatColor.RED + "None Selected");
            notSelected.setLore(ChatColor.YELLOW + "Click to select!");
            notSelected.addItemFlag(ItemFlag.HIDE_ENCHANTS);
            ItemBuilder selected = new ItemBuilder(Material.INK_SACK, (short) 10);
            selected.addItemFlag(ItemFlag.HIDE_ENCHANTS);
            selected.setDisplayName(ChatColor.GREEN + "Selected By:");

            if (getUsersCountOfClass(classArray[i], arena) > 0) {

            selected.setAmount(getUsersCountOfClass(classArray[i], arena));
            if (ownClassType != null) if (ownClassType.equals(classArray[i])) selected.addEnchant(Enchantment.DURABILITY, 1);
            ArrayList<String> lore = new ArrayList<>();
            for (UUID uuid : getUsersOfClass(classArray[i], arena)) {
                Player player1 = Bukkit.getPlayer(uuid);
                lore.add(HerobrinePVPCore.getFileManager().getRank(player1).getColor() + player1.getName());
            }
            selected.setLore(lore);

            inventory.setItem(classSlots[i] + 9, selected.build());
            }

            else inventory.setItem(classSlots[i] + 9, notSelected.build());
        }


    return inventory;
    }

    public static void updateMenuFor(Arena arena, Player player) {
        InventoryView inventory = player.getOpenInventory();
        if (inventory == null) return;
        if (!inventory.getTitle().equals(ChatColor.translateAlternateColorCodes('&', "&c&lDELTACRAFT &7- Class Selector"))) return;
        int[] classSlots = new int[]{29, 30, 31, 32, 33};
        int[] playerHeadSlots = new int[] {4,3,5,2,6};
        ClassTypes[] classArray = new ClassTypes[] {ClassTypes.HEALER_DELTACRAFT, ClassTypes.MAGE, ClassTypes.BERSERK, ClassTypes.ARCHER_DELTACRAFT, ClassTypes.TANK};
        ClassTypes ownClassType = null;
        for (UUID uuid : arena.getClasses().keySet()) {
            Player players = Bukkit.getPlayer(uuid);
            if (player.getUniqueId() == uuid) ownClassType = arena.getClass(uuid);
        }

        // slot+9 is where the items should go to place directly under their class icon
        for (int i = 0; i < 5; i++) {
            ItemBuilder notSelected = new ItemBuilder(Material.INK_SACK, (short) 8);
            notSelected.setDisplayName(ChatColor.RED + "None Selected");
            notSelected.setLore(ChatColor.YELLOW + "Click to select!");
            notSelected.addItemFlag(ItemFlag.HIDE_ENCHANTS);
            ItemBuilder selected = new ItemBuilder(Material.INK_SACK, (short) 10);
            selected.addItemFlag(ItemFlag.HIDE_ENCHANTS);
            selected.setDisplayName(ChatColor.GREEN + "Selected By:");

            if (getUsersCountOfClass(classArray[i], arena) > 0) {

                selected.setAmount(getUsersCountOfClass(classArray[i], arena));
                if (ownClassType != null) if (ownClassType.equals(classArray[i])) selected.addEnchant(Enchantment.DURABILITY, 1);
                ArrayList<String> lore = new ArrayList<>();
                for (UUID uuid : getUsersOfClass(classArray[i], arena)) {
                    Player player1 = Bukkit.getPlayer(uuid);
                    lore.add(HerobrinePVPCore.getFileManager().getRank(player1).getColor() + player1.getName());
                }
                selected.setLore(lore);

                inventory.setItem(classSlots[i] + 9, selected.build());
            }

            else inventory.setItem(classSlots[i] + 9, notSelected.build());
        }

        int i = 0;
        for (UUID uuid : arena.getPlayers()) {
            Player players = Bukkit.getPlayer(uuid);
            ItemBuilder readyItem = new ItemBuilder(Material.STAINED_GLASS_PANE, (short) 5);
            readyItem.setDisplayName(ChatColor.GREEN + "Ready");
            //slot+9 is where these items should go
            ItemBuilder notReadyItem = new ItemBuilder(Material.STAINED_GLASS_PANE, (short) 14);
            notReadyItem.setDisplayName(ChatColor.RED + "Not Ready");
            if (players == player) notReadyItem.setLore(Arrays.asList(ChatColor.GRAY + "Click to mark yourself as ready",
                    ChatColor.GRAY + "to start!", "", ChatColor.YELLOW + "Click to ready up!"));

            if (arena.getDeltaGame().getReadiedPlayers().contains(players.getUniqueId())) inventory.setItem(playerHeadSlots[i] + 9, readyItem.build());
            else inventory.setItem(playerHeadSlots[i] + 9, notReadyItem.build());

            i++;
        }
    }

    public static int getUsersCountOfClass(ClassTypes type, Arena arena) {
        int count = 0;
        for (UUID uuid : arena.getClasses().keySet()) {
            if (arena.getClass(uuid).equals(type)) count = count + 1;
        }
        return count;
    }

    public static ArrayList<UUID> getUsersOfClass(ClassTypes type, Arena arena) {
        ArrayList<UUID> users = new ArrayList<>();
        for (UUID uuid : arena.getClasses().keySet()) {
            if (arena.getClass(uuid).equals(type)) users.add(uuid);
        }
        return users;
    }


}
