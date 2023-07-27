package net.herobrine.deltacraft.characters.attack;

import net.herobrine.deltacraft.characters.attack.attacks.DimentioBeam;
import net.herobrine.deltacraft.characters.attack.attacks.DimentioSkull;
import net.herobrine.deltacraft.characters.attack.attacks.FireballAttack;
import net.herobrine.gamecore.Arena;
import net.herobrine.gamecore.Manager;

import java.util.HashMap;

public class AttackManager {

    public HashMap<AttackTypes, Attack> attackMap;
    private Arena arena;
    public AttackManager(Arena arena) {
    this.arena = arena;
    this.attackMap = new HashMap<AttackTypes, Attack>();

    for (AttackTypes attack : AttackTypes.values()) initAttack(attack);
    }

    public void initAttack(AttackTypes type) {
        switch(type) {
            case DIMENTIO_BEAM:
                attackMap.put(type, new DimentioBeam(type, arena.getID()));
                break;
            case DIMENTIO_STAR:
                attackMap.put(type, new DimentioSkull(type, arena.getID()));
                break;
            case BASIC_RANGED:
                attackMap.put(type, new FireballAttack(type, arena.getID()));
                break;
            default:
                return;
        }
    }

    public Attack getAttackFromType(AttackTypes type) {return attackMap.get(type);}

}
