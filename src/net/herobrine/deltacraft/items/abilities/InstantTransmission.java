package net.herobrine.deltacraft.items.abilities;

import net.herobrine.deltacraft.DeltaCraft;
import net.herobrine.deltacraft.items.ItemAbilities;
import net.herobrine.deltacraft.items.ItemAbility;
import net.herobrine.deltacraft.items.ItemTypes;
import net.herobrine.deltacraft.items.SpecialCase;
import net.herobrine.gamecore.Arena;
import net.herobrine.gamecore.GameCoreMain;
import net.herobrine.gamecore.GameState;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.UUID;

public class InstantTransmission extends ItemAbility implements SpecialCase {
    public InstantTransmission(ItemAbilities ability, ItemTypes item, int id) {
        super(ItemAbilities.INSTANT_TRANSMISSION, item, id);
    }


    private long lastCastTime;
    private float walkSpeed;

    @Override
    public void doAbility(Player player) {
        walkSpeed = 0.2F;
        lastCastTime = System.currentTimeMillis();


        boolean solidFound = false;
        Location loc = player.getLocation();
        Vector dir = loc.getDirection();
        // will check for the next 8 blocks forward if the player will face a solid block and if it does it will set the teleport location
        // to the one before it
        for (int i = 1; i<9; i++) {
            loc = player.getLocation();
            dir = loc.getDirection();
            dir.multiply(i);
            loc.add(dir);
            loc.add(0, .5, 0);

            if (loc.getBlock().getType().isSolid()) {
                loc = player.getLocation();
                dir = loc.getDirection();
                dir.multiply(i - 1);
                loc.add(dir);
                solidFound = true;
                doNoPass(player);
                break;
            }
        }

        if (!solidFound) {
            loc = player.getLocation();
            dir = loc.getDirection();
            dir.multiply(8); // 8 blocks away
            loc.add(dir);
        }

        if (!loc.getBlock().getRelative(BlockFace.UP).getType().equals(Material.AIR)) loc.add(0, 0.5, 0);
        if (loc.subtract(0, 0.6, 0).getBlock().getType().isSolid()) loc.add(0, 1.2, 0);
        player.playSound(player.getLocation(), Sound.ENDERMAN_TELEPORT, 1f, 1f);
        player.teleport(loc);
        player.setWalkSpeed(0.4F);

        new BukkitRunnable() {
            @Override
            public void run() {
                if(System.currentTimeMillis() - lastCastTime >= 3000) player.setWalkSpeed(walkSpeed);
            }
        }.runTaskLater(DeltaCraft.getInstance(), 65L);
    }



    @Override
    public boolean doesCasePass(Player player) {
        Location loc;
        Vector dir;
        for (int i = 1; i<3; i++) {
            loc = player.getLocation();
            dir = loc.getDirection();
            dir.multiply(i);
            loc.add(dir);
            loc.add(0,.5,0);

            if (loc.getBlock().getType().isSolid()) {
                return false;
            }
        }

        return true;
    }

    @Override
    public void doNoPass(Player player) {
        player.sendMessage(ChatColor.RED + "There are blocks in the way!");
    }
}
