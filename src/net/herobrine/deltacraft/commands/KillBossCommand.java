package net.herobrine.deltacraft.commands;

import net.herobrine.core.HerobrinePVPCore;
import net.herobrine.gamecore.Arena;
import net.herobrine.gamecore.GameState;
import net.herobrine.gamecore.Games;
import net.herobrine.gamecore.Manager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KillBossCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
       if (sender instanceof Player) {
           Player player = (Player) sender;

           if (HerobrinePVPCore.getFileManager().getRank(player).getPermLevel() >= 9) {
               if (Manager.isPlaying(player)) {
                   Arena arena = Manager.getArena(player);
                   if (!arena.getGame(arena.getID()).equals(Games.DELTARUNE) || !arena.getState().equals(GameState.LIVE)) {
                       player.sendMessage(ChatColor.RED + "You are not in a game of Delta Craft or the game you are in is not live.");
                       return false;
                   }

                   if (arena.getDeltaGame().isBossActive()) arena.getDeltaGame().stopBoss();

                   else player.sendMessage(ChatColor.RED + "The boss is not active!");


               }
               else player.sendMessage(ChatColor.RED + "You aren't in a game!");
           }
           else player.sendMessage(ChatColor.RED + "You can't use this!");
       }


        return false;
    }
}
