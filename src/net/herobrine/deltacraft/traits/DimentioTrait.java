package net.herobrine.deltacraft.traits;

import net.citizensnpcs.api.ai.AttackStrategy;
import net.citizensnpcs.api.event.NPCLeftClickEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.persistence.Persist;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitName;
import net.citizensnpcs.trait.RotationTrait;
import net.citizensnpcs.util.NMS;
import net.herobrine.core.HerobrinePVPCore;
import net.herobrine.core.SkullMaker;
import net.herobrine.deltacraft.DeltaCraft;
import net.herobrine.deltacraft.characters.attack.AttackTypes;
import net.herobrine.deltacraft.characters.attack.Attacker;
import net.herobrine.deltacraft.game.Characters;
import net.herobrine.gamecore.Arena;
import net.herobrine.gamecore.Manager;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntity;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@TraitName("dimentiotrait")
public class DimentioTrait extends Trait implements Attacker {
    DeltaCraft plugin;
        public DimentioBossState state;
        long ticks;

        int range;

        long lastAttack = 0;

        Arena arena;

        Player target;

        boolean hasTarget;

        ArmorStand chaosHeart;

        AttackTypes currentAttack;

        // this trait was made for a specific entity, so we know its ok to do this.
        // normally, we would use CustomEntityManager to determine what this value should be.
        public Characters character = Characters.DIMENTIO;

    public DimentioTrait() {
        super("dimentiotrait");
        plugin = JavaPlugin.getPlugin(DeltaCraft.class);
        state = DimentioBossState.INACTIVE;
         this.ticks = 0;
         this.range = 15;
        hasTarget = false;
    }



    // see the 'Persistence API' section
    @Persist("mysettingname") boolean automaticallyPersistedSetting = false;


    // An example event handler. All traits will be registered automatically as Spigot event Listeners
    @EventHandler
    public void click(NPCRightClickEvent event){
        //Handle a click on a NPC. The event has a getNPC() method.
        //Be sure to check event.getNPC() == this.getNPC() so you only handle clicks on this NPC!

    }

    @EventHandler
    public void leftClick(NPCLeftClickEvent e) {
        if (e.getNPC() == this.getNPC()) {if (state.equals(DimentioBossState.INVINCIBLE_PHASE)) arena.sendMessage(HerobrinePVPCore.translateString("&d[BOSS] Dimentio&f: Hahahaha! You can't do anymore damage than a puny little fly stuck in a bug zapper!"));}
    }

    // Called every tick
    @Override
    public void run() {

        if(!state.equals(DimentioBossState.STARTING) && !state.equals(DimentioBossState.ENDING) && !state.equals(DimentioBossState.INACTIVE)) handleTargeting();

        if (state.equals(DimentioBossState.STARTING) && ticks == 20L)  arena = Manager.getArena(npc.getEntity().getWorld());

        if(state.equals(DimentioBossState.STARTING) && ticks == 120L) {
            Location loc = npc.getEntity().getLocation();
            Vector dir = loc.getDirection();
            dir.multiply(5);
            loc.add(dir);
            loc.add(0, 3, 0);


            chaosHeart = (ArmorStand) loc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
            loc.getWorld().strikeLightningEffect(loc);
            chaosHeart.setGravity(false);
            chaosHeart.setVisible(false);
            chaosHeart.setCustomName(ChatColor.DARK_GRAY + "Chaos Heart");
            chaosHeart.setCustomNameVisible(true);

            chaosHeart.setHelmet(new SkullMaker(HerobrinePVPCore.translateString("&8Chaos Heart"), Arrays.asList("hah", "imagine getting this item, cringe"),
                    "http://textures.minecraft.net/texture/8b659b8c32d00a307b1a0bf0d5396aff57e209c4399d53aa12c9b915620f5b12").getSkull());

        }

        if (state.equals(DimentioBossState.STARTING) && ticks == 155L) {
            Location loc = npc.getEntity().getLocation();
            Vector dir = loc.getDirection();

            dir.multiply(6);
            loc.add(dir);
            loc.add(0, 4, 0);

            getNPC().getOrAddTrait(SequentialDialogueTrait.class).say("HAHAHA! You thought this was going to be easy?");
            new BukkitRunnable() {

                @Override
                public void run() {
                    getNPC().getOrAddTrait(SequentialDialogueTrait.class).say("Watch as I consume the Chaos Heart!");
                    npc.setFlyable(true);
                    npc.getNavigator().getLocalParameters().baseSpeed(0.90f);
                    arena.playSound(Sound.GHAST_FIREBALL);
                    npc.getNavigator().setTarget(loc);
                }
            }.runTaskLater(DeltaCraft.getInstance(), 15L);
        }

        if (state.equals(DimentioBossState.STARTING) && ticks > 185L) {
            if(!npc.getNavigator().isNavigating() && chaosHeart != null) {
                // dimentio has reached chaos heart

                Location loc = chaosHeart.getLocation();
                loc.getWorld().createExplosion(loc.getX(), loc.getY(), loc.getZ(), 4F, false, false);
                loc.getWorld().strikeLightningEffect(loc);
                arena.playSound(Sound.WITHER_SPAWN);

                chaosHeart.remove();

                // we're gonna have this message send when transitioning from invincible to running 1
                // arena.sendMessage(HerobrinePVPCore.translateString("&d[BOSS] Dimentio&f: Hahahaha! I am now UNSTOPPABLE! You pathetic heroes always think you can defeat me."));
                npc.setProtected(true);
                npc.getNavigator().getLocalParameters().baseSpeed(1f);

                state = DimentioBossState.INVINCIBLE_PHASE;

            }
        }


        if (state.equals(DimentioBossState.RUNNING_1)) {

        }

     if(!state.equals(DimentioBossState.INACTIVE) && !state.equals(DimentioBossState.ENDING))   ticks++;
    }

    //Run code when your trait is attached to a NPC.
    //This is called BEFORE onSpawn, so npc.getEntity() will return null
    //This would be a good place to load configurable defaults for new NPCs.
    @Override
    public void onAttach() {
        plugin.getServer().getLogger().info(npc.getName() + "has been assigned the Dimentio Trait!");
    }

    // Run code when the NPC is despawned. This is called before the entity actually despawns so npc.getEntity() is still valid.
    @Override
    public void onDespawn() {
        if (chaosHeart != null) chaosHeart.remove();
        state = DimentioBossState.ENDING;
    }

    //Run code when the NPC is spawned. Note that npc.getEntity() will be null until this method is called.
    //This is called AFTER onAttach and AFTER Load when the server is started.
    @Override
    public void onSpawn() {
    state = DimentioBossState.STARTING;
    arena = Manager.getArena(npc.getEntity().getWorld());
    }

    //run code when the NPC is removed. Use this to tear down any repeating tasks.
    @Override
    public void onRemove() {
        state = DimentioBossState.ENDING;
        arena.sendMessage(HerobrinePVPCore.translateString("&d[BOSS] Dimentio&f: I lost...but my story is far from over! You have not heard the end of Dimentio...just you wait!"));
    }

    public boolean hasTarget() {
        if (target != null) {
            return true;
        }
        return false;
    }

    public void setTarget(Player player) {
        target = player;
    }

    @Override
    public void handleTargeting() {
        if (currentAttack == null) pickAttack();
        if (!hasTarget()) {
            arena.sendDebugMessage(ChatColor.GOLD + "[DEBUG] " + npc.getName() + ChatColor.GREEN + " no target");
            HashMap<Double, Player> distance = new HashMap<>();
            for (UUID uuid: arena.getPlayers()) {
                Player player = Bukkit.getPlayer(uuid);
                double dist = player.getLocation().distanceSquared(npc.getEntity().getLocation());
                if (dist <= range*range) {
                    distance.put(dist, player);
                    arena.sendDebugMessage(ChatColor.GOLD + "[DEBUG] " + npc.getName() + ChatColor.GREEN + " potential target found " + player.getName());
                }
            }
            if (distance.isEmpty()) return;
            Player target = distance.get(Collections.min(distance.keySet()));

            arena.sendDebugMessage(ChatColor.GOLD + "[DEBUG] " + npc.getName() + ChatColor.GREEN + " target: " + target.getName());
            if (!hasTarget() && currentAttack != null) pickAttack();
            npc.setFlyable(true);
            setTarget(target);

            arena.sendDebugMessage(ChatColor.GOLD + "[DEBUG] " + npc.getName() + ChatColor.GREEN + " target real: " + this.target.getName());
        }

        else {
            Player victim = target;
            if (Manager.getArena(target) == null || !Manager.getArena(target).equals(arena)) return;
            double dist = victim.getLocation().distanceSquared(npc.getEntity().getLocation());
            if (dist > range*range) {
                arena.sendDebugMessage(ChatColor.GOLD + "[DEBUG] " + npc.getName() + ChatColor.RED + " target cancelled");
                setTarget(null);
                return;
            }
            boolean tryAttack = getTargetingMethod().handle((LivingEntity) npc.getEntity(), victim);
            arena.sendDebugMessage(ChatColor.GOLD + "[DEBUG] " + npc.getName() + ChatColor.RED + " Try to attack: " + tryAttack);
            if(!tryAttack) setTarget(null);
        }

    }


    @Override
    public AttackStrategy getTargetingMethod() {
        AttackStrategy strategy = (attacker, target) -> false;

        switch (state) {
            case INVINCIBLE_PHASE:
                strategy = (attacker, target) -> {

                 if(target == null || currentAttack == null)   return false;

                 Location location = npc.getEntity().getLocation();
                 Location n = location.setDirection(target.getLocation().subtract(location).toVector());
                 float pit = n.getPitch();
                 float yaw = n.getYaw();
                 NMS.look(npc.getEntity(), target.getEyeLocation(), false, false);
                // arena.sendPacket(new PacketPlayOutEntity.PacketPlayOutEntityLook(npc.getEntity().getEntityId(), (byte) ((yaw % 360.) * 256 / 360), (byte) ((pit % 360.) * 256 / 360), false));
                 if(npc.getNavigator().isNavigating()) {
                     npc.getNavigator().cancelNavigation();
                     npc.getNavigator().setTarget(target.getLocation().add(0, 4, 0));
                    }
                 else {
                     npc.getNavigator().setTarget(target.getLocation().add(0, 3, 0));
                 }

                 if (System.currentTimeMillis() - lastAttack >= currentAttack.getAttackSpeed() &&
                         target.getLocation().distanceSquared(npc.getEntity().getLocation()) <= currentAttack.getRange() * currentAttack.getRange()) {
                     arena.getDeltaGame().getAttackManager().getAttackFromType(currentAttack).doAttack(target, npc);
                     lastAttack = System.currentTimeMillis();
                 }
                 return true;
                };
                break;

            case RUNNING_1:


                break;

            case RUNNING_2:

                break;


            case RUNNING_3:

                break;
            default: strategy = (attacker, target) -> false;
        }

    return strategy;
    }


    @Override
    public void pickAttack() {
        switch(state) {
            case INVINCIBLE_PHASE:
                currentAttack = randomAttack(character, new AttackTypes[]{AttackTypes.DIMENTIO_BEAM, AttackTypes.DIMENTIO_STAR});
                break;
            case RUNNING_1:
                currentAttack = randomAttack(character, new AttackTypes[]{AttackTypes.DIMENTIO_BEAM, AttackTypes.DIMENTIO_STAR});
                break;

            case RUNNING_2:
                currentAttack = randomAttack(character, new AttackTypes[]{AttackTypes.DIMENTIO_BEAM, AttackTypes.DIMENTIO_STAR});
                break;

            case RUNNING_3:
                currentAttack = randomAttack(character, new AttackTypes[]{AttackTypes.DIMENTIO_BEAM, AttackTypes.DIMENTIO_STAR});
                break;

            case FIGHT_1:
                currentAttack = randomAttack(character, new AttackTypes[]{AttackTypes.DIMENTIO_BEAM, AttackTypes.DIMENTIO_STAR, AttackTypes.DIMENTIO_CLONE});
                break;

            case FIGHT_2:

                break;

            default:
                return;
        }
    }

    @Override
    public AttackTypes randomAttack(Characters character, AttackTypes[] desiredAttacks) {

        AttackTypes type = desiredAttacks[ThreadLocalRandom.current().nextInt(0, desiredAttacks.length)];

        arena.sendDebugMessage(ChatColor.GOLD + "[DEBUG] " + npc.getName() + ChatColor.GREEN + " Picked Attack: " + type.name());
        return type;
    }

}


