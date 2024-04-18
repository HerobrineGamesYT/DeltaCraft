package net.herobrine.deltacraft.objects.dungeon;

import net.herobrine.deltacraft.DeltaCraft;
import net.herobrine.deltacraft.objects.DeltaObject;
import net.herobrine.deltacraft.objects.ObjectTypes;
import net.herobrine.deltacraft.objects.Objects;
import net.herobrine.gamecore.Arena;
import net.herobrine.gamecore.Manager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class PortalCluster extends DeltaObject {

    Location spawnPoint;
    Location destination;
    Arena arena;
    PortalCluster destinationCluster;

    boolean spawnClusterAtDestination;
    boolean isDestinationClusterSpawned;

    BukkitRunnable collisionRunnable;
    BukkitRunnable effectRunnable;
    public PortalCluster(ObjectTypes type, Objects object, int id) {
        super(type, object, id);
        this.arena = Manager.getArena(id);
        initObject();
    }
    @Override
    public void initObject() {
        collisionRunnable = new BukkitRunnable() {
            @Override
            public void run() {
                if (spawnPoint == null || destination == null) return;
                if (spawnClusterAtDestination && !isDestinationClusterSpawned) {
                    isDestinationClusterSpawned = true;
                    PortalCluster returnCluster = new PortalCluster(ObjectTypes.PARTICLE, Objects.PORTAL_CLUSTER, id);
                    returnCluster.setSpawnPoint(destination);
                    returnCluster.setDestination(spawnPoint);
                    destinationCluster = returnCluster;
                }
                for(Entity ent : spawnPoint.getWorld().getNearbyEntities(spawnPoint, 0.5, 0.5, 0.5)) {
                    if (ent instanceof Player) {
                        Player player = (Player) ent;

                        if (Manager.isPlaying(player)) {
                            long lastTeleport = 0;
                            if (arena.getDeltaGame().getLastClusterUse().get(player.getUniqueId()) != null) lastTeleport = arena.getDeltaGame().getLastClusterUse().get(player.getUniqueId());
                            if (System.currentTimeMillis() - lastTeleport < 3000) return;
                            arena.getDeltaGame().getLastClusterUse().put(player.getUniqueId(), System.currentTimeMillis());
                            player.teleport(destination.clone().add(0, 1, 0));
                            player.sendMessage(ChatColor.LIGHT_PURPLE + "You've been teleported by the Portal Cluster!");
                            player.playSound(player.getLocation(), Sound.ENDERMAN_TELEPORT, 1f, 1f);
                        }
                    }
                }
            }
        };
    collisionRunnable.runTaskTimer(DeltaCraft.getInstance(), 0L, 1L);

      effectRunnable = new BukkitRunnable() {
          @Override
          public void run() {
              if (spawnPoint == null) return;
              Location l = spawnPoint;
              double r = 2.0;
              for(double phi = 0; phi <= Math.PI; phi += Math.PI / 15) {
                  double y = r * Math.cos(phi) + 1.5;
                  for(double theta = 0; theta <= 2 * Math.PI; theta += Math.PI / 30) {
                      double x = r * Math.cos(theta) * Math.sin(phi);
                      double z = r * Math.sin(theta) * Math.sin(phi);
                      l.add(x, y, z);
                      createParticles(l);
                      l.subtract(x, y, z);
                  }
              }
          }
      };
      effectRunnable.runTaskTimer(DeltaCraft.getInstance(), 0L, 45L);
    }

    @Override
    public void destroyObject() {
        collisionRunnable.cancel();
        effectRunnable.cancel();
        if (getDestinationCluster() != null) getDestinationCluster().destroyObject();
    }


    public PortalCluster getDestinationCluster() {
        if (destinationCluster != null) return destinationCluster;
        else return null;
    }
    public Location getDestination() {return destination;}

    public Location getSpawnPoint() {return spawnPoint;}

    public void spawnClusterAtDestination(boolean spawnClusterAtDestination) {this.spawnClusterAtDestination = spawnClusterAtDestination;}
    public void setSpawnPoint(Location spawnPoint) {this.spawnPoint = spawnPoint;}
    public void setDestination(Location destination) {this.destination = destination;}
}
