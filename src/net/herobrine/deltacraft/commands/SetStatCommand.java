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

public class SetStatCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (HerobrinePVPCore.getFileManager().getRank(player).getPermLevel() < 9) {
                player.sendMessage(ChatColor.RED + "You can't use this!");
                return false;
            }

            if (args.length < 2) {
                player.sendMessage(ChatColor.RED + "Invalid usage! Usage: /setstats <stat> <val>");
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
                int value = Integer.parseInt(args[1]);
                switch (args[0]) {
                    case "defense":
                        arena.getDeltaGame().getStats(player).setDefense(value);

                        player.sendMessage(ChatColor.GREEN + "Set your defense to " + ChatColor.GREEN + value + "❈");
                        break;

                    case "mana":
                        if (value > arena.getDeltaGame().getStats(player).getIntelligence())
                            arena.getDeltaGame().getStats(player).setIntelligence(value);
                        arena.getDeltaGame().getStats(player).setMana(value);

                        player.sendMessage(ChatColor.GREEN + "Set your mana to " + ChatColor.AQUA + value + "✎");
                        break;

                    case "health":
                        if (value > arena.getDeltaGame().getStats(player).getMaxHealth())
                            arena.getDeltaGame().getStats(player).setMaxHealth(value);

                        arena.getDeltaGame().getStats(player).setHealth(value);

                        player.sendMessage(ChatColor.GREEN + "Set your health to " + ChatColor.RED + value  + "❤");
                        break;

                    default:
                        player.sendMessage(ChatColor.RED + "Invalid stat type!");
                        return false;
                }

                arena.getDeltaGame().updatePlayerStats(player);
            } catch (Exception e) {
                player.sendMessage(ChatColor.RED + "Invalid value!");


            }

        }
        return false;
    }
}
