package net.herobrine.deltacraft.characters;

import net.citizensnpcs.api.ai.Navigator;
import net.citizensnpcs.api.event.*;
import net.citizensnpcs.api.npc.AbstractNPC;
import net.citizensnpcs.api.npc.BlockBreaker;
import net.citizensnpcs.trait.SkinTrait;
import net.herobrine.core.HerobrinePVPCore;
import net.herobrine.deltacraft.DeltaCraft;
import net.herobrine.deltacraft.game.Characters;
import net.herobrine.deltacraft.traits.DimentioTrait;
import net.herobrine.deltacraft.traits.SequentialDialogueTrait;
import net.herobrine.gamecore.Arena;
import net.herobrine.gamecore.Manager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

public class Dimentio extends net.herobrine.deltacraft.game.Character {
    HashMap<Long, String> dialogueSequence = new HashMap<>();

    long ticks = 0;
    Arena arena;

    Characters character;

    public Dimentio(Location spawnLocation, Arena arena, Characters character) {
    super(character.name(), character.getDisplay(), character.getType(),
            character.getHealth(),true, true, character.isVulnerable(), character.getSkinTexture(), character.getSkinSignature(), spawnLocation, character);
    this.arena = arena;
    this.character = character;
    getNPC().getOrAddTrait(SkinTrait.class).setSkinName("Dimentio");
    getNPC().getOrAddTrait(DimentioTrait.class);
    //spawn npc
        spawnNPC(spawnLocation);
    }


    @Override
    public void onRightClick(NPCRightClickEvent e) {

    }

    @Override
    public void onLeftClick(NPCLeftClickEvent e) {

    }


    // IMPORTANT!! BOSS DIALOGUE MUST BE LIMITED TO 64 CHARACTERS PER LINE OR IT CANT BE DISPLAYED ON HOLOGRAM AND YOULL HAVE CONSOLE ERRORS
    @EventHandler
    public void onSpawn(NPCSpawnEvent e) {
        getNPC().getOrAddTrait(SequentialDialogueTrait.class).setupDialogueMap(new String[] {"Why hello there! And who might you be?",
                        "Another challenger, stepping foot into MY domain?", "I must inform you that this is a fool's errand!", "Whatever your reasons, prepare for a taste of my power!"},
                new long[] {0L, 20L, 40L, 60L}, character, arena);
    }
}
