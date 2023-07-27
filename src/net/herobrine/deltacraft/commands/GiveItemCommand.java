package net.herobrine.deltacraft.commands;

import net.herobrine.core.HerobrinePVPCore;
import net.herobrine.deltacraft.items.ItemTypes;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GiveItemCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (HerobrinePVPCore.getFileManager().getRank(player).getPermLevel() >= 9) {
                if (args.length == 0) player.sendMessage(ChatColor.RED + "Invalid usage! Usage: /giveitem ITEM_NAME");
                else {
                    try {
                        ItemTypes item = ItemTypes.valueOf(args[0]);
                        player.getInventory().addItem(ItemTypes.build(item));
                        player.sendMessage(ChatColor.GREEN + "You have been given a " + item.getDisplay() + ChatColor.GREEN + "!");
                    }
                    catch(IllegalArgumentException e){player.sendMessage(ChatColor.RED + "No item exists with that name!");}

                }

            }
            else player.sendMessage(ChatColor.RED + "No permission.");

        }


        return false;
    }
}
