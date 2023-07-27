package net.herobrine.deltacraft.characters.attack.attacks;

import net.citizensnpcs.api.npc.NPC;
import net.herobrine.deltacraft.characters.attack.Attack;
import net.herobrine.deltacraft.characters.attack.AttackTypes;
import net.herobrine.deltacraft.game.CustomProjectiles;
import net.herobrine.gamecore.GameCoreMain;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.UUID;

public class DimentioBeam extends Attack {
    public DimentioBeam(AttackTypes type, int id) {
        super(type, id);
    }

    @Override
    public void doAttack(Entity target, NPC npc) {
        doParticleLogic((LivingEntity) target, npc);
    }
}
