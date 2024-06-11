package net.herobrine.deltacraft.objects.puzzles.type;

import net.herobrine.deltacraft.DeltaCraft;
import net.herobrine.deltacraft.objects.DeltaObject;
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
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class LightSwitchPuzzle extends Puzzle {
    public LightSwitchPuzzle(DeltaObject object, PuzzleTypes type) {
        super(object, type);
    }

    @Override
    public void startPuzzle(Player player) {
        setState(PuzzleGameState.ACTIVE);
        if (activePlayers.size() >= type.getMaxPlayers())
            player.sendMessage(ChatColor.RED + "This puzzle is already being completed!");
        else {
            activePlayers.add(player.getUniqueId());
            player.openInventory(DeltaCraft.getInstance().getPuzzleMenuManager().createGUI(type));
        }
    }


    @EventHandler
    public void onInvClick(InventoryClickEvent e) {
        if (!e.getClickedInventory().getTitle().equals(type.getDisplay())) return;
        Player player = (Player) e.getWhoClicked();
        if (!Manager.isPlaying(player)) return;
        if (!activePlayers.contains(player.getUniqueId())) return;
        Arena arena = Manager.getArena(player);
        if (!arena.getGame().equals(Games.DELTARUNE) || !arena.getState().equals(GameState.LIVE)) return;
        e.setCancelled(true);
        if (!getState().equals(PuzzleGameState.ACTIVE)) return;
        boolean disregardInput = e.getCurrentItem().getDurability() == (short) 5;
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

        if (disregardInput) return;

        player.playSound(player.getLocation(), Sound.NOTE_PLING, 1f, 1.25f);
        ItemBuilder item = new ItemBuilder(Material.STAINED_GLASS_PANE);
        item.setDurability((short)5);
        item.setDisplayName(ChatColor.GRAY + "");
        e.getClickedInventory().setItem(e.getSlot(), item.build());

        boolean shouldComplete = true;
        for (ItemStack stack : e.getClickedInventory().getContents()) {
            if (stack.getDurability() != (short) 5) shouldComplete = false;
        }

        if (shouldComplete) {
            setComplete(true);
            player.closeInventory();
            try {
                Method onComplete = object.getClass().getDeclaredMethod("onComplete", Player.class);
                onComplete.setAccessible(true);
                onComplete.invoke(object, player);
                onComplete.setAccessible(false);
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException ex) {
                player.sendMessage(ChatColor.RED + "There was an error completing this puzzle.. Does this implement PuzzleObject?");
            }
        }


    }
}
