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
import org.bukkit.event.player.PlayerInteractEvent;

public abstract class Puzzle implements Listener {
    DeltaObject object;
    boolean isComplete;

    private Puzzle(DeltaObject object) {
        this.object = object;
        this.isComplete = false;
        Bukkit.getPluginManager().registerEvents(this, DeltaCraft.getInstance());
    }

    abstract void startPuzzle(Player player);
    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (!Manager.isPlaying(e.getPlayer())) return;
        if (object.getLocation() == null) return;
        if (Manager.getArena(e.getPlayer()) != Manager.getArena(object.getLocation().getWorld())) return;

        Player player = e.getPlayer();
        Location loc = e.getClickedBlock().getLocation();
        if (!object.getLocation().equals(loc)) return;
        // After this, we have confirmed that the block clicked is this class instance's puzzle. As a result, we should go ahead and start the puzzle if it hasn't already been completed.
      if (shouldStartPuzzle()) startPuzzle(player);
      else player.sendMessage(ChatColor.RED + "The puzzle is already completed!");
    }

    public boolean shouldStartPuzzle() {return !isComplete();}

    public void setComplete(boolean isComplete) {this.isComplete = isComplete;}
    public boolean isComplete() {return isComplete;}



}
