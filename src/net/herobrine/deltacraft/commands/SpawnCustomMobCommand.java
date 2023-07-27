package net.herobrine.deltacraft.commands;

import net.herobrine.core.HerobrinePVPCore;
import net.herobrine.deltacraft.characters.CustomEntityManager;
import net.herobrine.deltacraft.game.Characters;
import net.herobrine.gamecore.Arena;
import net.herobrine.gamecore.GameState;
import net.herobrine.gamecore.Games;
import net.herobrine.gamecore.Manager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpawnCustomMobCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (HerobrinePVPCore.getFileManager().getRank(player).getPermLevel() < 9) {
                player.sendMessage(ChatColor.RED + "You can't use this!");
                return false;
            }

            if (args.length < 1) {
                player.sendMessage(ChatColor.RED + "Invalid usage! Usage: /spawncustommob <mob>");
                return false;
            }

            if (!Manager.isPlaying(player)) {
                player.sendMessage(ChatColor.RED + "You aren't playing!");
                return false;
            }

            Arena arena = Manager.getArena(player);

            if (!arena.getGame(arena.getID()).equals(Games.DELTARUNE) || !arena.getState().equals(GameState.LIVE)) {
                player.sendMessage(ChatColor.RED + "You aren't in a live Delta Craft game!");
                return false;
            }

            try {
               if(!Characters.valueOf(args[0]).isBoss()) {
                   CustomEntityManager.spawnCustomMob(Characters.valueOf(args[0]), arena, player.getLocation());
                   player.sendMessage(ChatColor.GREEN + "Successfully spawned a " + ChatColor.YELLOW + Characters.valueOf(args[0]));
               }
               else player.sendMessage(ChatColor.RED + "You can't spawn boss mobs using this command! Try starting the boss fight instead.");


            }
            catch(Exception e) {player.sendMessage(ChatColor.RED + "Invalid mob type!");}
        }

        return false;
    }
}
