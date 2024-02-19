package net.herobrine.deltacraft.traits;

import java.util.Collections;
import java.util.HashMap;
import net.citizensnpcs.api.ai.AttackStrategy;
import net.citizensnpcs.api.ai.Navigator;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.persistence.Persist;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitName;
import net.citizensnpcs.util.NMS;
import net.herobrine.deltacraft.DeltaCraft;
import net.herobrine.deltacraft.characters.CustomEntityManager;
import net.herobrine.deltacraft.characters.attack.AttackTypes;
import net.herobrine.deltacraft.characters.attack.Attacker;
import net.herobrine.deltacraft.game.Characters;
import net.herobrine.deltacraft.items.ItemTypes;
import net.herobrine.gamecore.Arena;
import net.herobrine.gamecore.Manager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;

@TraitName("aggressivetrait")
public class AggressiveTrait extends Trait implements Attacker {

    DeltaCraft plugin;
    Player target;
    boolean hasTarget;
    AttackTypes currentAttack;

    private Characters character;

    int range;
    long lastAttack;
    private Arena arena;
    public AggressiveTrait() {
        super("aggressivetrait");
        plugin = JavaPlugin.getPlugin(DeltaCraft.class);
        this.range = 0;
        this.lastAttack = 0;
    }



    // see the 'Persistence API' section
    @Persist("mysettingname") boolean automaticallyPersistedSetting = false;


    // An example event handler. All traits will be registered automatically as Spigot event Listeners
    @EventHandler
    public void click(NPCRightClickEvent event){
        //Handle a click on a NPC. The event has a getNPC() method.
        //Be sure to check event.getNPC() == this.getNPC() so you only handle clicks on this NPC!

    }

    // Called every tick
    @Override
    public void run() {

        if(npc.getEntity() != null && character != null) handleTargeting();
    }

    //Run code when your trait is attached to a NPC.
    //This is called BEFORE onSpawn, so npc.getEntity() will return null
    //This would be a good place to load configurable defaults for new NPCs.
    @Override
    public void onAttach() {
        plugin.getServer().getLogger().info(npc.getName() + "has been assigned AggressiveTrait!");
    }

    // Run code when the NPC is despawned. This is called before the entity actually despawns so npc.getEntity() is still valid.
    @Override
    public void onDespawn() {
    }

    //Run code when the NPC is spawned. Note that npc.getEntity() will be null until this method is called.
    //This is called AFTER onAttach and AFTER Load when the server is started.
    @Override
    public void onSpawn() {

    }

    //run code when the NPC is removed. Use this to tear down any repeating tasks.
    @Override
    public void onRemove() {
    }

    @Override
    public void pickAttack() {
    currentAttack = randomAttack(character, character.getAbilities());
    if (currentAttack.hasWeapon()) {
        LivingEntity ent = (LivingEntity) npc.getEntity();
        ent.getEquipment().setItemInHand(ItemTypes.build(currentAttack.getWeapon()));
    }
    }

    @Override
    public AttackTypes randomAttack(Characters character, AttackTypes[] desiredAttacks) {
        return desiredAttacks[ThreadLocalRandom.current().nextInt(0, desiredAttacks.length)];
    }

    @Override
    public AttackStrategy getTargetingMethod() {
        AttackStrategy strategy = (attacker, target) -> false;

            if (hasTarget()) strategy = (attacker,target) -> {
                if(currentAttack == null) return false;
                if (currentAttack.getRange() == 0) npc.getNavigator().setTarget(target, true);
                else if (target.getLocation().distanceSquared(npc.getEntity().getLocation()) < currentAttack.getRange()*currentAttack.getRange()) {
                    Function<Navigator, Location> func = (navigator) -> target.getEyeLocation();
                    npc.getNavigator().getLocalParameters().lookAtFunction(func);
                    if (System.currentTimeMillis() - lastAttack >= currentAttack.getAttackSpeed()) {
                        lastAttack = System.currentTimeMillis();
                        arena.getDeltaGame().getAttackManager().getAttackFromType(currentAttack).doAttack(target, npc);
                    }
                }
                else npc.getNavigator().setTarget(target.getLocation());
            return true;
        };

        else if (currentAttack.getRange() == 0) npc.getNavigator().cancelNavigation();

        return strategy;
    }
       @Override
       public void handleTargeting() {
           if (currentAttack == null && character != null) pickAttack();

           if (arena == null) this.arena = Manager.getArena(npc.getEntity().getWorld());
           if (!hasTarget()) {
               arena.sendDebugMessage(ChatColor.GOLD + "[DEBUG] " + npc.getName() + ChatColor.GREEN + " no target");
              HashMap<Double, Player> distance = new HashMap<>();
               for (UUID uuid: arena.getPlayers()) {
                   Player player = Bukkit.getPlayer(uuid);
                   double dist = player.getLocation().distanceSquared(npc.getEntity().getLocation());
                 if (dist <= range*range) distance.put(dist, player);
                }
                if (distance.isEmpty()) return;
                Player target = distance.get(Collections.min(distance.keySet()));

                if (hasTarget() && currentAttack != null) pickAttack();

                setTarget(target);
                 }

            else {
               Player victim = target;
               double dist = victim.getLocation().distanceSquared(npc.getEntity().getLocation());
               if (dist > range*range) {
                   setTarget(null);
                   return;
                }
                boolean tryAttack = getTargetingMethod().handle((LivingEntity) npc.getEntity(), victim);
               arena.sendDebugMessage(ChatColor.GOLD + "[DEBUG] " + npc.getName() + ChatColor.RED + " try to attack "  + tryAttack);
                if(!tryAttack) setTarget(null);
            }

        }



        public boolean hasTarget() {return target != null;}

        public void setTarget(Player target) {this.target = target;}

    // CALL THIS METHOD AND SET YOUR ENTITIES RANGE WHEN ASSIGNING THE TRAIT! THIS IS THE SIGHT RANGE! IT IS REQUIRED FOR ALL ENTITIES!
    // MELEE ATTACKS ARE HANDLED VIA ATTACK RANGE.
        public void setRange(int range) {this.range = range;}

        // THIS SETS THE BASE SPEED. I would use speed modifiers but I don't see the point unless I need to increase the speed by a certain %.
        // This works, so if it ain't broke don't fix it.
        // Default speed is 1f.
        public void setWalkSpeed(float speed) {npc.getNavigator().getLocalParameters().baseSpeed(speed);}

        public void setCharacter(Characters character) {this.character = character;}
}


