package net.herobrine.deltacraft.items.abilities;

import net.herobrine.deltacraft.items.AbilityTypes;
import net.herobrine.deltacraft.items.ItemAbilities;
import net.herobrine.deltacraft.items.ItemAbility;
import net.herobrine.deltacraft.items.ItemTypes;
import net.herobrine.gamecore.*;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashMap;
import java.util.UUID;

public class AbilityTest extends ItemAbility {

    private Arena arena;
    private HashMap<UUID, Long> cooldown;

    public AbilityTest(ItemAbilities ability, ItemTypes item, int id) {
        super(ItemAbilities.ABILITY_TEST, item, id);
        this.cooldown = new HashMap<UUID, Long>();
    }

    public HashMap<UUID, Long> getCooldown() {
        return cooldown;
    }

    @Override
    public void doAbility(Player player) {
        player.sendMessage(ChatColor.GREEN + "The ability worked!");
        player.playSound(player.getLocation(), Sound.VILLAGER_YES, 1f, 1f);
    }



}
