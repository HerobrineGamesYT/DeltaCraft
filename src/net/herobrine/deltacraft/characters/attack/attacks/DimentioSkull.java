package net.herobrine.deltacraft.characters.attack.attacks;

import net.citizensnpcs.api.npc.NPC;
import net.herobrine.deltacraft.characters.attack.Attack;
import net.herobrine.deltacraft.characters.attack.AttackTypes;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

public class DimentioSkull extends Attack {

    public DimentioSkull(AttackTypes type, int id) {
        super(type, id);
    }

    @Override
    public void doAttack(Entity target, NPC npc) {
        doSkullLogic((LivingEntity) target, npc);
    }
}
