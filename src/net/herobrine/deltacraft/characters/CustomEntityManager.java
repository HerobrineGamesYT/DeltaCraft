package net.herobrine.deltacraft.characters;

import net.herobrine.deltacraft.game.Character;
import net.herobrine.deltacraft.game.Characters;
import net.herobrine.gamecore.Arena;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

import java.util.HashMap;

public class CustomEntityManager {
   public static HashMap<Entity, Character> entityMap = new HashMap<Entity, Character>();




    public static HashMap<Entity, Character> getEntityMap() {return entityMap;}

    public static Character getCharFromEnt(Entity ent) {return entityMap.get(ent);}


    public static void spawnCustomMob(Characters character, Arena arena, Location loc) {
        if(character.isBoss()) return;



        switch(character) {
            case DUMMY:
                new TestDummy(loc, arena, character);
                break;

            case DUNGEON_MASTER:
                new DungeonMaster(loc, arena, character);
                break;

            case DUMMY_ATTACKER:
                new DummyAttacker(loc, arena, character);
                break;
            default:
                return;
        }

    }
}
