package net.herobrine.deltacraft.characters.attack;

import net.citizensnpcs.api.ai.AttackStrategy;
import net.herobrine.deltacraft.game.Characters;

public interface Attacker {


    // The responsibility of the attack picker is to pick an attack that matches the state the entity or boss currently is in.
    // It will choose an attack produced by randomAttack that best suits it.
    // The purpose of an Attacker interface is to make it a little more simple to implement hostile traits.
    public void pickAttack();

    public AttackTypes randomAttack(Characters character,AttackTypes[] desiredAttacks);

    public AttackStrategy getTargetingMethod();

    public void handleTargeting();




   // COPY AND PASTE METHOD TEMPLATES!

    //@Override
    //    public void handleTargeting() {
    //        if (currentAttack == null) pickAttack();
    //        if (!hasTarget()) {
    //            HashMap<Double, Player> distance = new HashMap<>();
    //            for (UUID uuid: arena.getPlayers()) {
    //                Player player = Bukkit.getPlayer(uuid);
    //                double dist = player.getLocation().distanceSquared(npc.getEntity().getLocation());
    //                if (dist <= range*range) {
    //                    distance.put(dist, player);
    //                    arena.sendMessage(ChatColor.GREEN + "potential target found " + player.getName());
    //                }
    //            }
    //            if (distance.isEmpty()) return;
    //            Player target = distance.get(Collections.min(distance.keySet()));
    //
    //            arena.sendMessage(ChatColor.GREEN + "target: " + target.getName());
    //            if (!hasTarget() && currentAttack != null) pickAttack();
    //            setTarget(target);
    //
    //        }
    //
    //        else {
    //            Player victim = target;
    //            double dist = victim.getLocation().distanceSquared(npc.getEntity().getLocation());
    //            if (dist > range*range) {
    //                setTarget(null);
    //                return;
    //            }
    //            boolean tryAttack = getTargetingMethod().handle((LivingEntity) npc.getEntity(), victim);
    //            if(!tryAttack) setTarget(null);
    //        }
    //
    //    }
}
