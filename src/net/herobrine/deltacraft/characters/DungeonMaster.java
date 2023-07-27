package net.herobrine.deltacraft.characters;

import net.citizensnpcs.api.event.NPCLeftClickEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.event.NPCSpawnEvent;
import net.citizensnpcs.trait.HologramTrait;
import net.herobrine.core.HerobrinePVPCore;
import net.herobrine.deltacraft.game.Character;
import net.herobrine.deltacraft.game.Characters;
import net.herobrine.deltacraft.traits.SequentialDialogueTrait;
import net.herobrine.gamecore.Arena;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;

public class DungeonMaster extends Character {

    private Characters character;

    private Arena arena;
    public DungeonMaster(Location loc, Arena arena, Characters character) {
        super(character.name(), character.getDisplay(), character.getType(),
                character.getHealth(),false, false, character.isVulnerable(), character.getSkinTexture(), character.getSkinSignature(), loc, character);

        this.arena = arena;
        this.character = character;
        spawnNPC(loc);

        getNPC().getOrAddTrait(SequentialDialogueTrait.class).setHasActiveDialogue(false);
        getNPC().getOrAddTrait(SequentialDialogueTrait.class).setHasClickSay(true);
        getNPC().getOrAddTrait(SequentialDialogueTrait.class).setupVoice(Sound.VILLAGER_IDLE, 0.8f);
    }

    @Override
    public void onRightClick(NPCRightClickEvent e) {

    }

    @Override
    public void onLeftClick(NPCLeftClickEvent e) {

    }


    @EventHandler
    public void onSpawn(NPCSpawnEvent e) {
        getNPC().getOrAddTrait(SequentialDialogueTrait.class).setupDialogueMap(new String[] {"Hello there friend! Welcome to DeltaCraft.",
        "DeltaCraft is a dungeons minigame full of surprises.", "It's currently under development, so come back soon for more updates!"}, new long[] {0L, 20L, 40L},
                character, arena);
    }
}
