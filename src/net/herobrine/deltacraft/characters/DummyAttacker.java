package net.herobrine.deltacraft.characters;

import net.citizensnpcs.api.event.NPCLeftClickEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.event.NPCSpawnEvent;
import net.herobrine.deltacraft.game.Character;
import net.herobrine.deltacraft.game.Characters;
import net.herobrine.deltacraft.traits.AggressiveTrait;
import net.herobrine.gamecore.Arena;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;

public class DummyAttacker extends Character {

    public DummyAttacker(Location loc, Arena arena, Characters character) {
        super(character.name(), character.getDisplay(), character.getType(),
                character.getHealth(),false, false, character.isVulnerable(), character.getSkinTexture(), character.getSkinSignature(), loc, character);
        spawnNPC(loc);
    }
    @Override
    public void onRightClick(NPCRightClickEvent e) {

    }

    @Override
    public void onLeftClick(NPCLeftClickEvent e) {

    }



    @EventHandler
    public void onSpawn(NPCSpawnEvent e) {
        getNPC().getOrAddTrait(AggressiveTrait.class).setCharacter(getCharacter());
        getNPC().getOrAddTrait(AggressiveTrait.class).setRange(25);
    }
}
