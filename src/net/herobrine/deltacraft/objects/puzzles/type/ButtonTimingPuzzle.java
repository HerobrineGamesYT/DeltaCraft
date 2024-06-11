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
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.ThreadLocalRandom;

public class ButtonTimingPuzzle extends Puzzle {
    public ButtonTimingPuzzle(DeltaObject object, PuzzleTypes type) {
        super(object, type);
    }
    int activeRow = 1;
    int currentSlot = 1;
    int indicatorSlot = 1;
    int indiciatorSlot1 = 46;

    long tickSpeed = 20;

    boolean isPaused = false;

    boolean defaultSlotSetup = false;

    @Override
    public void startPuzzle(Player player) {
        setState(PuzzleGameState.ACTIVE);
        if (activePlayers.size() >= type.getMaxPlayers()) {
            player.sendMessage(ChatColor.RED + "This puzzle is already being completed!");
            return;
        }
        activePlayers.add(player.getUniqueId());
        player.openInventory(DeltaCraft.getInstance().getPuzzleMenuManager().createGUI(type));

        new BukkitRunnable() {
            int i = 0;
            @Override
            public void run() {
                if (!getState().equals(PuzzleGameState.ACTIVE)) {
                    cancel();
                    resetPuzzle();
                }
                if (!defaultSlotSetup) {
                    ThreadLocalRandom rand = ThreadLocalRandom.current();
                    int randomSlot = rand.nextInt(1,6);
                    setCurrentSlot(randomSlot + 9);
                    ItemBuilder slot = new ItemBuilder(Material.STAINED_GLASS_PANE);
                    slot.setDurability((short) 5);
                    slot.setDisplayName(ChatColor.GRAY + "");
                    player.getOpenInventory().getTopInventory().setItem(randomSlot + 9, slot.build());
                    defaultSlotSetup = true;
                }
                if (isPaused) return;
                if (i % tickSpeed != 0) {
                    i++;
                    return;}
                i++;

                ItemBuilder indicator = new ItemBuilder(Material.STAINED_GLASS_PANE);
                ItemBuilder filler = new ItemBuilder(Material.STAINED_GLASS_PANE);

                indicator.setDisplayName(ChatColor.GRAY + "");
                filler.setDisplayName(ChatColor.GRAY + "");
                indicator.setDurability((short) 10);
                filler.setDurability((short) 7);
                if (indicatorSlot == 6) {
                    player.getOpenInventory().getTopInventory().setItem(6, filler.build());
                    player.getOpenInventory().getTopInventory().setItem(51, filler.build());
                    indicatorSlot = 1;
                    indiciatorSlot1 = 46;
                    player.getOpenInventory().getTopInventory().setItem(1, indicator.build());
                    player.getOpenInventory().getTopInventory().setItem(46, indicator.build());
                    player.playSound(player.getLocation(), Sound.CLICK, 1f, 1f);
                    return;
                }

                indicatorSlot = indicatorSlot + 1;
                indiciatorSlot1 = indiciatorSlot1 + 1;
                player.getOpenInventory().getTopInventory().setItem(indicatorSlot, indicator.build());
                player.getOpenInventory().getTopInventory().setItem(indiciatorSlot1, indicator.build());
                player.getOpenInventory().getTopInventory().setItem(indicatorSlot - 1, filler.build());
                player.getOpenInventory().getTopInventory().setItem(indiciatorSlot1 - 1, filler.build());
                player.playSound(player.getLocation(), Sound.CLICK, 1f, 1f);
            }
        }.runTaskTimer(DeltaCraft.getInstance(), 0L, 1L);

    }


    public void resetPuzzle() {
        activeRow = 1;
        currentSlot = 1;
        indicatorSlot = 1;
        indiciatorSlot1 = 46;
        tickSpeed = 20;
        isPaused = false;
        defaultSlotSetup = false;
    }

    public void setCurrentSlot(int slot) {this.currentSlot = slot;}
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
        boolean disregardInput = !e.getCurrentItem().getType().equals(Material.STAINED_CLAY) || e.getCurrentItem().getDurability() != 5;
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

        int targetSlot = indicatorSlot + (9 * activeRow);
        boolean shouldFail = targetSlot != currentSlot;

        if (shouldFail) {
            ItemBuilder errorButton = new ItemBuilder(Material.STAINED_CLAY);
            errorButton.setDurability((short) 15);
            errorButton.setDisplayName(ChatColor.RED + "Wrong timing! Try Again!");
            e.getClickedInventory().setItem(e.getSlot(), errorButton.build());
            isPaused = true;
            player.playSound(player.getLocation(), Sound.ENDERMAN_TELEPORT, 1f, .75f);
            new BukkitRunnable() {
                @Override
                public void run() {
                    ItemBuilder button = new ItemBuilder(Material.STAINED_CLAY);
                    button.setDurability((short) 5);
                    button.setDisplayName(ChatColor.YELLOW + "Click me!");
                    e.getClickedInventory().setItem(e.getSlot(), button.build());
                    isPaused = false;
                }
            }.runTaskLater(DeltaCraft.getInstance(), 30L);
            return;
        }
        activeRow = activeRow + 1;
        player.playSound(player.getLocation(), Sound.NOTE_PLING, 1f, 1.25f);

        if (activeRow > 4) {
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
            return;
        }

        ThreadLocalRandom rand = ThreadLocalRandom.current();
        int newSlot = rand.nextInt(1, 6);
        newSlot = newSlot + (activeRow*9);
        currentSlot = newSlot;
        tickSpeed = tickSpeed - 5;
        ItemBuilder button = new ItemBuilder(Material.STAINED_CLAY);
        button.setDurability((short)13);
        button.setDisplayName(ChatColor.GREEN + "Completed!");
        ItemBuilder wire = new ItemBuilder(Material.STAINED_GLASS_PANE);
        wire.setDurability((short) 5);
        wire.setDisplayName(ChatColor.GRAY + "");

        int buttonSlot = 7 + ((activeRow - 1) * 9);
        int newButtonSlot = 7 + activeRow * 9;
        e.getClickedInventory().setItem(buttonSlot, button.build());
        e.getClickedInventory().setItem(currentSlot, wire.build());
        button.setDurability((short) 5);
        button.setDisplayName(ChatColor.YELLOW + "Click me!");
        e.getClickedInventory().setItem(newButtonSlot, button.build());

        int i = 1;
        while (i < 7) {
        e.getClickedInventory().setItem(i + ((activeRow - 1) * 9), wire.build());
        i++;
        }
        wire.setDurability((short) 14);
        int i2 = 1;
        while (i2 < 7) {
            int slot = i2 + activeRow * 9;
            if (slot != currentSlot) e.getClickedInventory().setItem(slot, wire.build());
            i2++;
        }

    }
    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        Player player = (Player) e.getPlayer();
        activePlayers.remove(player.getUniqueId());
        if (activePlayers.size() == 0) {
            setState(PuzzleGameState.INACTIVE);
            resetPuzzle();
        }

    }


}
