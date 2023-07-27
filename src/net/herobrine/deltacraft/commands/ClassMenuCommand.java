package net.herobrine.deltacraft.commands;

import net.herobrine.core.HerobrinePVPCore;
import net.herobrine.core.Ranks;
import net.herobrine.deltacraft.game.Menus;
import net.herobrine.gamecore.Arena;
import net.herobrine.gamecore.GameState;
import net.herobrine.gamecore.Games;
import net.herobrine.gamecore.Manager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;

public class ClassMenuCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
        Player player = (Player) sender;
        Ranks rank = HerobrinePVPCore.getFileManager().getRank(player);

        if (rank.getPermLevel() < 9) {
            player.sendMessage(ChatColor.RED + "You can't use this!");
            return false;
        }

        if (!Manager.isPlaying(player)) {
            player.sendMessage(ChatColor.RED + "You aren't playing!");
            return false;
        }

        Arena arena = Manager.getArena(player);

        if (!arena.getGame().equals(Games.DELTARUNE) || arena.getState().equals(GameState.LIVE)) {
            player.sendMessage(ChatColor.RED + "You aren't in a NON-LIVE DeltaCraft game!");
            return false;
        }

        Menus.applyClassSelector(player);

        }

        return false;
    }
}
