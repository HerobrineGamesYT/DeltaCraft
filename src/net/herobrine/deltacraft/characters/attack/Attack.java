package net.herobrine.deltacraft.characters.attack;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.herobrine.deltacraft.DeltaCraft;
import net.herobrine.deltacraft.game.CustomProjectiles;
import net.herobrine.deltacraft.items.ItemTypes;
import net.herobrine.gamecore.Arena;
import net.herobrine.gamecore.Manager;
import net.minecraft.server.v1_8_R3.DataWatcher;
import net.minecraft.server.v1_8_R3.EntityLiving;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityEquipment;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityMetadata;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.*;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.util.function.Predicate;

public abstract class Attack implements Listener {

    protected AttackTypes type;
    protected Arena arena;

    public Attack(AttackTypes type, int id) {
        this.type = type;
        this.arena = Manager.getArena(id);
    }


    public abstract void doAttack(Entity target, NPC npc);


    public void doParticleLogic(LivingEntity target, NPC npc) {
        if (type.getAttackSound() != null) {
            Player victim = (Player) target;
            victim.playSound(victim.getLocation(), type.getAttackSound(), 1f, type.getAttackSoundPitch());
        }

        Location start = npc.getEntity().getLocation().add(0, 1, 0);
        Location end = target.getLocation();
        int pointsPerLine = type.getProjectile().getAmount();
        double d = start.distance(end) / pointsPerLine;
        Predicate<Location> operationPerPoint = location -> !location.getBlock().getType().isSolid();

        boolean hasHit = false;
        for (int i = 0; i < pointsPerLine; i++) {
            Location l = start.clone();
            Vector direction = end.toVector().subtract(start.toVector()).normalize();
            Vector v = direction.multiply(i * d);
            l.add(v.getX(), v.getY(), v.getZ());

            for(Entity ent : l.getWorld().getNearbyEntities(l, .6, 1, .6)) {
                if (ent instanceof Player && !CitizensAPI.getNPCRegistry().isNPC(ent)) {
                    Player victim = (Player) ent;
                    if (Manager.getArena(victim) == null) return;
                    if (Manager.getArena(victim) != this.arena) return;
                    if(hasHit) return;
                    hasHit = true;
                    EntityDamageEvent event = new EntityDamageEvent(target, EntityDamageEvent.DamageCause.CUSTOM, type.getBaseDamage());
                    Bukkit.getPluginManager().callEvent(event);
                    victim.setLastDamageCause(event);
                    victim.damage(0);
                }
            }
            if (operationPerPoint == null) {
                arena.sendPacket(type.getProjectile().createParticle(l, type.getProjectile()));
                continue;
            }
            if (operationPerPoint.test(l)) {
                arena.sendPacket(type.getProjectile().createParticle(l, type.getProjectile()));
            }
        }


    }


    public void doSkullLogic(LivingEntity target, NPC npc) {
        if (type.getAttackSound() != null) {
            Player victim = (Player) target;
            victim.playSound(victim.getLocation(), type.getAttackSound(), 1f, type.getAttackSoundPitch());
        }
        Location loc = npc.getEntity().getLocation();
        Vector dir = loc.getDirection();

        dir.multiply(2);
        loc.add(dir);

        ArmorStand stand = type.getProjectile().createSkull(type.getProjectile(), loc);

        new BukkitRunnable() {
            int skullTimer = 0;
            @Override
            public void run() {
                Location from = stand.getLocation();
                Location to = target.getLocation();
                if (from.getBlock().getType().isSolid()){
                    cancel();
                    stand.remove();
                    return;
                }
                boolean didHit = false;
                for (Entity ent : from.getWorld().getNearbyEntities(from, .6f, 1f, .6f)) {
                    if (ent instanceof Player && !CitizensAPI.getNPCRegistry().isNPC(ent)) {
                        Player victim = (Player) ent;
                        if (Manager.getArena(victim) == null) return;
                        if (Manager.getArena(victim) != arena) return;
                        EntityDamageEvent event = new EntityDamageEvent(target, EntityDamageEvent.DamageCause.CUSTOM, type.getBaseDamage());
                        Bukkit.getPluginManager().callEvent(event);
                        victim.setLastDamageCause(event);
                        victim.damage(0);
                        didHit = true;
                    }
                }
                if (didHit) {
                    cancel();
                    stand.remove();
                }
                stand.setHeadPose(new EulerAngle(Math.random(), Math.random(), Math.random()));
                Vector vFrom = from.toVector();
                Vector vTo = to.toVector();
                Vector direction = vTo.subtract(vFrom).normalize();

                // fun fact: if you don't have gravity on your armorstand, this doesn't work!!
                stand.setVelocity(direction);
                skullTimer++;
            }
        }.runTaskTimer(DeltaCraft.getInstance(), 0L, 1L);


    }

    public void doShooterLogic(LivingEntity target, NPC npc) {
        boolean shouldChargeBow = type.hasWeapon() && type.getWeapon().getMaterial().equals(Material.BOW);
        LivingEntity ent = (LivingEntity) npc.getEntity();
        CraftLivingEntity craftLiving = (CraftLivingEntity) ent;
        EntityLiving living = craftLiving.getHandle();
        arena.sendPacket(new PacketPlayOutEntityEquipment(ent.getEntityId(), 0, CraftItemStack.asNMSCopy(ItemTypes.build(type.getWeapon()))));
        long attackStart = System.currentTimeMillis();
        DataWatcher datawatcher = null;
        DataWatcher entWatcher = living.getDataWatcher();
        arena.sendMessage(ChatColor.RED +  "" + shouldChargeBow);
        if (shouldChargeBow){
            CraftHumanEntity ent1 = (CraftHumanEntity) npc.getEntity();
            datawatcher = new DataWatcher(ent1.getHandle());
            datawatcher.a(0, (byte) 16);
            arena.sendPacket(new PacketPlayOutEntityMetadata(ent.getEntityId(), datawatcher, true));
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                if (System.currentTimeMillis() - attackStart >= type.getAttackChargeSpeed()) {
                    cancel();
                    if (npc.getEntity() == null) return;
                    LivingEntity ent = (LivingEntity) npc.getEntity();
                    arena.sendPacket(new PacketPlayOutEntityMetadata(ent.getEntityId(), entWatcher, true));
                    if (type.getAttackSound() != null) arena.playSound(type.getAttackSound(), 1f, type.getAttackSoundPitch());
                    ent.launchProjectile(type.getProjectile().getProjectile());
                }
            }


        }.runTaskTimer(DeltaCraft.getInstance(), 0L, 1L);

    }
}
