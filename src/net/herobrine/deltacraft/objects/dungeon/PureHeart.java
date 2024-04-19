package net.herobrine.deltacraft.objects.dungeon;

import net.herobrine.deltacraft.DeltaCraft;
import net.herobrine.deltacraft.items.ItemTypes;
import net.herobrine.deltacraft.objects.DeltaObject;
import net.herobrine.deltacraft.objects.ObjectTypes;
import net.herobrine.deltacraft.objects.Objects;
import net.herobrine.gamecore.Manager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.util.UUID;

public class PureHeart extends DeltaObject {


    BukkitRunnable collisionRunnable;
    BukkitRunnable effectRunnable;
    private Location spawnPoint;

    ArmorStand heart;
    ItemTypes heartItem;

    boolean pickedUp = false;

    boolean isStandSpawned = false;

    public PureHeart(ObjectTypes type, Objects object, int id, UUID uuid) {
        super(type, object, id, uuid);
        this.heartItem = ItemTypes.valueOf(object.name());
        initObject();
    }

    @Override
    public void initObject() {
        collisionRunnable = new BukkitRunnable() {
            @Override
            public void run() {
                if (spawnPoint == null) return;

                for(Entity ent : spawnPoint.getWorld().getNearbyEntities(spawnPoint, 0.5, 0.5, 0.5)) {
                    if (ent instanceof Player) {
                        Player player = (Player) ent;

                        if (Manager.isPlaying(player) && !pickedUp) {
                            pickedUp = true;
                            player.getInventory().addItem(ItemTypes.build(heartItem));
                            player.playSound(player.getLocation(), Sound.ITEM_PICKUP, 1f, 1f);
                            Manager.getArena(id).sendMessage(ChatColor.GREEN + player.getName() + " picked up a " + heartItem.getDisplay() + ChatColor.GREEN + "!");
                            destroyObject();
                        }
                    }
                }
            }
        };

        collisionRunnable.runTaskTimer(DeltaCraft.getInstance(), 0L, 1L);


        effectRunnable = new BukkitRunnable() {
            double rad = 0;
            @Override
            public void run() {
                if (spawnPoint == null) return;

                if (!isStandSpawned) {
                    isStandSpawned = true;
                    heart = (ArmorStand) spawnPoint.getWorld().spawnEntity(spawnPoint, EntityType.ARMOR_STAND);
                    heart.setGravity(false);
                    heart.setVisible(false);
                    heart.setCustomName(heartItem.getDisplay());
                    heart.setCustomNameVisible(true);
                    heart.setHelmet(ItemTypes.build(heartItem));
                }

                if (rad < 360) {
                    rad+=3.0; //increase to have head rotate faster.
                    Location newLoc = heart.getLocation();
                    newLoc.setYaw((float)rad);
                    heart.teleport(newLoc);
                    return;
                }
                rad = 0;

            }
        };

        effectRunnable.runTaskTimer(DeltaCraft.getInstance(), 0L, 1L);

    }

    @Override
    public void destroyObject() {
    collisionRunnable.cancel();
    effectRunnable.cancel();
    heart.remove();
    arena.getDeltaGame().getObjectManager().unregisterObject(getUUID());
    }


    public Location getSpawnPoint() {return spawnPoint;}

    public void setSpawnPoint(Location spawnPoint) {this.spawnPoint = spawnPoint;}
}
