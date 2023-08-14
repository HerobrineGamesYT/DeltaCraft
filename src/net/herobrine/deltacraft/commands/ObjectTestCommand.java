package net.herobrine.deltacraft.commands;

import net.herobrine.core.HerobrinePVPCore;
import net.herobrine.core.Ranks;
import net.herobrine.deltacraft.objects.Objects;
import net.herobrine.deltacraft.objects.dungeon.PortalCluster;
import net.herobrine.gamecore.GameState;
import net.herobrine.gamecore.Games;
import net.herobrine.gamecore.Manager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class ObjectTestCommand implements CommandExecutor {

    boolean hasPortalSpawned = false;
    ArrayList<PortalCluster> clusterTest = new ArrayList<>();
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            Ranks rank = HerobrinePVPCore.getFileManager().getRank(player);
            if (rank.getPermLevel() < 9) {
                player.sendMessage(ChatColor.RED + "No permission!");
                return false;
            }

            if (!Manager.isPlaying(player)) {
                player.sendMessage(ChatColor.RED + "You aren't playing!");
                return false;
            }

            if (!Manager.getArena(player).getGame().equals(Games.DELTARUNE)) {
                player.sendMessage(ChatColor.RED + "You aren't playing Deltacraft!");
                return false;
            }

            if (Manager.getArena(player).getState() != GameState.LIVE) {
                player.sendMessage(ChatColor.RED + "The game isn't live!");
                return false;
            }

            if (args.length != 1) {
                player.sendMessage(ChatColor.RED + "Invalid usage! Usage: /testobject <OBJECT>");
                return false;
            }

            if (args[0].equalsIgnoreCase("PORTAL_CLUSTER") && !hasPortalSpawned) {
                hasPortalSpawned = true;
                player.sendMessage(ChatColor.GREEN + "Spawned a test Portal Cluster!");
                PortalCluster cluster = (PortalCluster) Manager.getArena(player).getDeltaGame().getObjectManager().createObject(Objects.PORTAL_CLUSTER);
                cluster.setSpawnPoint(new Location(player.getWorld(), -218.0, 4.0, -33.0));
                cluster.setDestination(new Location(player.getWorld(), -234, 4.0, -34));
                clusterTest.add(cluster);
            }

            else if (args[0].equalsIgnoreCase("PORTAL_CLUSTER") && hasPortalSpawned) {
                clusterTest.get(0).destroyObject();
                hasPortalSpawned = false;
                clusterTest.remove(0);
                player.sendMessage(ChatColor.GREEN + "Removed the test Portal Cluster!");
            }

        }

        return false;
    }
}
