package net.herobrine.deltacraft.characters;

import net.citizensnpcs.api.event.*;
import net.citizensnpcs.trait.HologramTrait;
import net.citizensnpcs.trait.SkinTrait;
import net.herobrine.core.HerobrinePVPCore;
import net.herobrine.deltacraft.DeltaCraft;
import net.herobrine.deltacraft.game.Character;
import net.herobrine.deltacraft.game.Characters;
import net.herobrine.deltacraft.game.Menus;
import net.herobrine.deltacraft.traits.SequentialDialogueTrait;
import net.herobrine.gamecore.Arena;
import net.herobrine.gamecore.GameState;
import net.herobrine.gamecore.Games;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.UUID;

public class DungeonMaster extends Character {

    private Characters character;

    private Arena arena;

    private HashMap<UUID, Boolean> hasTalked = new HashMap<>();
    long key = 0;
    public DungeonMaster(Location loc, Arena arena, Characters character) {
        super(character.name(), character.getDisplay(), character.getType(),
                character.getHealth(),false, false, character.isVulnerable(), character.getSkinTexture(), character.getSkinSignature(), loc, character);

        this.arena = arena;
        this.character = character;
        spawnNPC(loc);

        getNPC().getOrAddTrait(SequentialDialogueTrait.class).setHasActiveDialogue(false);
        getNPC().getOrAddTrait(SequentialDialogueTrait.class).setHasClickSay(false);
        getNPC().getOrAddTrait(SequentialDialogueTrait.class).setupVoice(Sound.VILLAGER_IDLE, 0.8f);
    }

    @Override
    @EventHandler
    public void onRightClick(NPCRightClickEvent e) {
        if (!e.getNPC().equals(this.getNPC())) return;

        Player player = e.getClicker();


        if (arena.getState() == GameState.LIVE) {
            getNPC().getOrAddTrait(SequentialDialogueTrait.class).say("Good luck, " + player.getName() + ".", player);
            return;
        }
        boolean hasPlayedBefore = HerobrinePVPCore.getFileManager().getGameStats(player.getUniqueId(), Games.DELTARUNE, "roundsPlayed") > 0;

        if (hasTalked.containsKey(player.getUniqueId())) {
            Menus.applyClassSelector(player);
            return;
        }

        if (!hasPlayedBefore) {
            HashMap<Long, String> dialogueMap = getNPC().getOrAddTrait(SequentialDialogueTrait.class).getDialogueMap();
            getNPC().getOrAddTrait(SequentialDialogueTrait.class).timedSay(player, dialogueMap);
            new BukkitRunnable() {
                @Override
                public void run() {
                    hasTalked.put(player.getUniqueId(), true);
                }
            }.runTaskLater(DeltaCraft.getInstance(), key);
        }
    }

    @Override
    @EventHandler
    public void onLeftClick(NPCLeftClickEvent e) {
        if (!e.getNPC().equals(this.getNPC())) return;

        Player player = e.getClicker();

        if (arena.getState() == GameState.LIVE) {
            getNPC().getOrAddTrait(SequentialDialogueTrait.class).say("Good luck, " + player.getName() + ".", player);
            return;
        }
        boolean hasPlayedBefore = HerobrinePVPCore.getFileManager().getGameStats(player.getUniqueId(), Games.DELTARUNE, "roundsPlayed") > 0;

        if (hasTalked.containsKey(player.getUniqueId())) {
            Menus.applyClassSelector(player);
            return;
        }

        if (!hasPlayedBefore) {
        HashMap<Long, String> dialogueMap = getNPC().getOrAddTrait(SequentialDialogueTrait.class).getDialogueMap();
        getNPC().getOrAddTrait(SequentialDialogueTrait.class).timedSay(player, dialogueMap);
        new BukkitRunnable() {
            @Override
            public void run() { HashMap<Long, String> dialogueMap = getNPC().getOrAddTrait(SequentialDialogueTrait.class).getDialogueMap();
                getNPC().getOrAddTrait(SequentialDialogueTrait.class).timedSay(player, dialogueMap);
                hasTalked.put(player.getUniqueId(), true);
            }
        }.runTaskLater(DeltaCraft.getInstance(), key);
        }
    }


    @EventHandler
    public void onSpawn(NPCSpawnEvent e) {
        if (!e.getNPC().equals(this.getNPC())) return;
        getNPC().getOrAddTrait(SequentialDialogueTrait.class).setupDialogueMap(new String[] {"Hello there traveler! Welcome to DeltaCraft.",
        "DeltaCraft is a dungeons minigame full of surprises.", "&dBosses, &bSecrets, &cEnemies, &fand more lie ahead.", "You're going to need some gear to get started." +
                        "\nClick me again to select your class!"}, new long[] {0L, 20L, 40L, 60L},
                character, arena);
        key = 60L;

    }


    @EventHandler
    public void onDespawn(NPCRemoveEvent e) {
        if(e.getNPC() == this.getNPC()) hasTalked.clear();
    }
}
