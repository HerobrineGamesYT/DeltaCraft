package net.herobrine.deltacraft.characters.attack.attacks;

import net.citizensnpcs.api.npc.NPC;
import net.herobrine.deltacraft.characters.attack.Attack;
import net.herobrine.deltacraft.characters.attack.AttackTypes;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

public class FireballAttack extends Attack {
    public FireballAttack(AttackTypes type, int id) {super(type, id);}

    @Override
    public void doAttack(Entity target, NPC npc) {
        target.sendMessage(ChatColor.RED + "We just tried to attack you.");
        doShooterLogic((LivingEntity) target, npc);
    }
}
