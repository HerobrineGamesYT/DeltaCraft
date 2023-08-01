package net.herobrine.deltacraft.traits;

import net.citizensnpcs.api.event.NPCClickEvent;
import net.citizensnpcs.api.event.NPCLeftClickEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.persistence.Persist;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitName;
import net.citizensnpcs.trait.HologramTrait;
import net.herobrine.core.HerobrinePVPCore;
import net.herobrine.deltacraft.DeltaCraft;
import net.herobrine.deltacraft.game.Characters;
import net.herobrine.gamecore.Arena;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collections;
import java.util.HashMap;
import java.util.UUID;

@TraitName("sequenceddialogue")
public class SequentialDialogueTrait extends Trait {
    private DeltaCraft plugin;
    private HashMap<Long, String> dialogue;

    private HashMap<UUID, Boolean> isSpeaking;
    private long ticks;

    private long timedSayTick;
    private boolean isBoss;

    private boolean hasClickSay;

    private boolean hasActiveDialogue = true;

    private boolean hasVoice = false;

    private Sound voice;
    private float voicePitch;
    private  ChatColor color;
    private Arena arena;

    private Characters characterType;

    private long lastLine;



    public SequentialDialogueTrait() {
        super("sequenceddialogue");
        plugin = JavaPlugin.getPlugin(DeltaCraft.class);
        dialogue = new HashMap<>();
        ticks = 0;
        timedSayTick = 0;
        isBoss = false;
        hasClickSay = false;
        isSpeaking = new HashMap<UUID, Boolean>();
        lastLine = 0;
    }



    // see the 'Persistence API' section
    @Persist("mysettingname") boolean automaticallyPersistedSetting = false;


    // Trigger a timed say dialogue sequence on click. You must set this up in the Character subclass if you have an NPC that supports it.
    @EventHandler
    public void click(NPCClickEvent event){
        if (!event.getNPC().equals(this.getNPC())) return;

        if (hasClickSay) timedSay(event.getClicker(), dialogue);

    }


    // For some odd reason regular NPCClickEvent doesn't work in citizens, so we have to do it this way.
    @EventHandler

    public void leftClick(NPCLeftClickEvent e) {
        if (!e.getNPC().equals(this.getNPC())) return;

        if (isSpeaking.containsKey(e.getClicker().getUniqueId())) {
            if(isSpeaking.get(e.getClicker().getUniqueId())) return;
        }
        if (hasClickSay) timedSay(e.getClicker(), dialogue);
    }

    @EventHandler

    public void rightClick(NPCRightClickEvent e) {
        if (!e.getNPC().equals(this.getNPC())) return;

        if (isSpeaking.containsKey(e.getClicker().getUniqueId())) {
            if(isSpeaking.get(e.getClicker().getUniqueId())) return;
        }

        if (hasClickSay) timedSay(e.getClicker(), dialogue);
    }


    // Called every tick - this is for NPCs with active dialogue that plays right from onSpawn. This is on by default. If you want a regular NPC that
    // has dialogue on click, disable this when attaching the trait. Then you can setup your dialogue map and enable hasClickSay.
    @Override
    public void run() {
        if (!hasActiveDialogue) return;

       if (dialogue.containsKey(ticks)) say(dialogue.get(ticks));

       if (ticks - lastLine > 25L && characterType.isBoss()) npc.getOrAddTrait(HologramTrait.class).setLine(1, "");

       if(hasActiveDialogue) ticks++;
    }

    //Run code when your trait is attached to a NPC.
    //This is called BEFORE onSpawn, so npc.getEntity() will return null
    //This would be a good place to load configurable defaults for new NPCs.
    @Override
    public void onAttach() {
        plugin.getServer().getLogger().info(npc.getName() + "has been assigned SequentialDialogueTrait!");
    }

    // Run code when the NPC is despawned. This is called before the entity actually despawns so npc.getEntity() is still valid.
    @Override
    public void onDespawn() {
    }

    //Run code when the NPC is spawned. Note that npc.getEntity() will be null until this method is called.
    //This is called AFTER onAttach and AFTER Load when the server is started.
    @Override
    public void onSpawn() {

    }

    //run code when the NPC is removed. Use this to tear down any repeating tasks.
    @Override
    public void onRemove() {

    }

    public void say(String line) {
        lastLine = ticks;
        if (hasVoice) arena.playSound(voice, 1f, voicePitch);
        if (isBoss) {
            arena.sendMessage(color + "[BOSS] " + characterType.getDisplay() + ChatColor.WHITE + ": " + HerobrinePVPCore.translateString(line));
           if (npc.getOrAddTrait(HologramTrait.class).getLines().size() > 1) npc.getOrAddTrait(HologramTrait.class).setLine(1, HerobrinePVPCore.translateString("&f&l" + line));
           else npc.getOrAddTrait(HologramTrait.class).addLine(HerobrinePVPCore.translateString("&f&l" + line));
        }
        else arena.sendMessage(ChatColor.YELLOW + "[NPC] " + characterType.getDisplay() + ChatColor.WHITE + ": " + HerobrinePVPCore.translateString(line));

    }

    public void say(String line, Player player) {
        if (hasVoice) player.playSound(player.getLocation(), voice, 1f, voicePitch);
        if (isBoss) player.sendMessage(color + "[BOSS] " + characterType.getDisplay() + ChatColor.WHITE + ": " + HerobrinePVPCore.translateString(line));

        else player.sendMessage(ChatColor.YELLOW + "[NPC] " + characterType.getDisplay() + ChatColor.WHITE + ": " + HerobrinePVPCore.translateString(line));
    }


    // This is used when a player clicks on an NPC directly with sequential dialogue.
    // This method can also be used for timed dialogues of any kind, for a specific player.
    public void timedSay(Player player, HashMap<Long, String> lines) {
        if (isSpeaking.containsKey(player.getUniqueId())) {
            if (isSpeaking.get(player.getUniqueId())) return;
        }
        isSpeaking.put(player.getUniqueId(), true);
        long lastTick = Collections.max(lines.keySet());
        final long[] tick = {0};
        new BukkitRunnable() {
            @Override
            public void run() {
                if (tick[0] > lastTick) {
                    cancel();
                    isSpeaking.put(player.getUniqueId(), false);
                }

                if (lines.containsKey(tick[0])) {
                    lastLine = ticks;
                    if(hasVoice) player.playSound(player.getLocation(), voice, 1f, voicePitch);
                    if (isBoss) player.sendMessage(color + "[BOSS] " + characterType.getDisplay() + ChatColor.WHITE + ": " + HerobrinePVPCore.translateString(lines.get(tick[0])));
                    else player.sendMessage(ChatColor.YELLOW + "[NPC] " + characterType.getDisplay() + ChatColor.WHITE + ": " + HerobrinePVPCore.translateString(lines.get(tick[0])));
                }


                tick[0]++;
            }

        }.runTaskTimer(DeltaCraft.getInstance(), 0L, 1L);
    }

    public void setTicks(long tick) {ticks = tick;}

    public void setHasClickSay(boolean hasTimedSay) {hasClickSay = hasTimedSay;}

    public void setHasActiveDialogue(boolean active) {hasActiveDialogue = active;}

    public void setupVoice(Sound sound, float pitch) {
        voice = sound;
        voicePitch = pitch;
        hasVoice = true;
    }

    public HashMap<Long, String> getDialogueMap() {return dialogue;}

    public void setupDialogueMap(String[] text, long[] ticks, Characters character, Arena a) {
        isBoss = character.isBoss();
        color = character.getColor();
        arena = a;
        characterType = character;

        for (int i = 0; i < text.length; i++) dialogue.put(ticks[i], text[i]);

        this.ticks = 0;
    }

}

