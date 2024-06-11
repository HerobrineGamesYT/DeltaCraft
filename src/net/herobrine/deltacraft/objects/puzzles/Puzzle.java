package net.herobrine.deltacraft.objects.puzzles;

import net.herobrine.deltacraft.DeltaCraft;
import net.herobrine.deltacraft.objects.DeltaObject;
import net.herobrine.gamecore.Manager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public abstract class Puzzle implements Listener {
    protected DeltaObject object;
    boolean isComplete;
    protected ArrayList<UUID> activePlayers;

    protected PuzzleTypes type;

    protected PuzzleGameState state;
    public Puzzle(DeltaObject object, PuzzleTypes type) {
        this.object = object;
        this.type = type;
        this.state = PuzzleGameState.INACTIVE;
        this.isComplete = false;
        this.activePlayers = new ArrayList<UUID>();
        Bukkit.getPluginManager().registerEvents(this, DeltaCraft.getInstance());
    }

    public abstract void startPuzzle(Player player);
    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (!Manager.isPlaying(e.getPlayer())) return;
        if (object.getLocation() == null) return;
        if (Manager.getArena(e.getPlayer()) != Manager.getArena(object.getLocation().getWorld())) return;
        if (getState().equals(PuzzleGameState.DESTROYED)) return;
        if (e.getClickedBlock().getLocation() == null) return;
        Player player = e.getPlayer();
        Location loc = e.getClickedBlock().getLocation();
        if (!object.getLocation().getBlock().getLocation().equals(loc)) return;
        // After this, we have confirmed that the block clicked is this class instance's puzzle. As a result, we should go ahead and start the puzzle if it hasn't already been completed.
        // The puzzle subclass can still reject this call to start, if the puzzle already has the maximum number of active players.
      if (!isComplete()) startPuzzle(player);
      else if (getState().equals(PuzzleGameState.DESTROYED)) return;
      else player.sendMessage(ChatColor.RED + "The puzzle is already completed!");
    }
    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        Player player = (Player) e.getPlayer();
        activePlayers.remove(player.getUniqueId());
        if (activePlayers.size() == 0) setState(PuzzleGameState.INACTIVE);
    }

    public void setComplete(boolean isComplete) {this.isComplete = isComplete;}
    public boolean isComplete() {return isComplete;}

    public PuzzleTypes getType() {return type;}

    public PuzzleGameState getState(){return state;}
    public void setState(PuzzleGameState state) {this.state = state;}


}
