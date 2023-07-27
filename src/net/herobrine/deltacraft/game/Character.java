package net.herobrine.deltacraft.game;

import com.sun.xml.internal.ws.api.addressing.WSEndpointReference;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.DespawnReason;
import net.citizensnpcs.api.event.NPCLeftClickEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.trait.HologramTrait;
import net.citizensnpcs.trait.SkinTrait;
import net.herobrine.core.HerobrinePVPCore;
import net.herobrine.deltacraft.DeltaCraft;
import net.herobrine.deltacraft.characters.CustomEntityManager;
import net.herobrine.deltacraft.characters.attack.Attack;
import net.herobrine.deltacraft.characters.attack.AttackTypes;
import net.herobrine.gamecore.Arena;
import net.herobrine.gamecore.Manager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.LazyMetadataValue;
import org.bukkit.metadata.MetadataValue;

import java.util.concurrent.Callable;

public abstract class Character implements Listener {

    protected String name;
    protected String displayName;
    protected EntityType type;
    protected double health;
    protected boolean isBoss;
    protected boolean hasDialogue;
    protected boolean isVulnerable;
    protected String skinData;
    protected String skinSignature;

    protected Location spawnLoc;
    private NPC npc;
    private LivingEntity npcEnt;

    private Characters character;



    public Character(String name, String displayName, EntityType type, double health, boolean isBoss, boolean hasDialogue,  boolean isVulnerable,
                     String skinData, String skinSignature, Location spawnLoc, Characters character) {


        this.name = name;
        this.displayName = displayName;
        this.type = type;
        this.health = health;
        this.isBoss = isBoss;
        this.hasDialogue = hasDialogue;
        this.isVulnerable = isVulnerable;
        this.skinData = skinData;
        this.skinSignature = skinSignature;
        this.spawnLoc = spawnLoc;
        this.character = character;

        if(type.equals(EntityType.PLAYER)) npc = CitizensAPI.getNPCRegistry().createNPC(type, displayName);
        else npc = CitizensAPI.getNPCRegistry().createNPC(type, "");
        if (isVulnerable) npc.setProtected(false);

        Bukkit.getPluginManager().registerEvents(this, DeltaCraft.getInstance());
    }


     public void spawnNPC(Location location) {
        npc.spawn(location);
        npcEnt = (LivingEntity) npc.getEntity();
       if(type.equals(EntityType.PLAYER)) npc.getOrAddTrait(SkinTrait.class).setTexture(skinData, skinSignature);
       else npc.getOrAddTrait(HologramTrait.class).addLine(displayName);
      if(health > 0) npc.getOrAddTrait(HologramTrait.class).addLine("" + ChatColor.GREEN + Math.round(health) + ChatColor.RED + "❤");
      else npc.getOrAddTrait(HologramTrait.class).addLine(HerobrinePVPCore.translateString("&e&lCLICK"));

      CustomEntityManager.getEntityMap().put(npcEnt, this);
    }

     public void removeNPC() {
        CustomEntityManager.getEntityMap().remove(npcEnt);
        npc.destroy();
     }

     public Characters getCharacter() {return character;}

     public void updateNPCHealth() {
        int index;
        if (type.equals(EntityType.PLAYER)) index = 0;
        else index = 1;
        npc.getOrAddTrait(HologramTrait.class).setLine(index, "" + ChatColor.GREEN + Math.round(health) + ChatColor.RED + "❤");
    }


     // player = player that killed this enemy.
     public void killNPC(Player player) {
        removeNPC();
        player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1f, 2f);
     }

     public void setHealth(int health) {
        this.health = health;
        updateNPCHealth();
    }

    public int getHealth() {return (int)Math.round(health);}

    public double getMaxHealth() {return npcEnt.getMaxHealth();}

    public String getName() {return name;}

    public EntityType getType() {return type;}

     public NPC getNPC() {
        return npc;
     }


     @EventHandler
     public abstract void onRightClick(NPCRightClickEvent e);

     @EventHandler
     public abstract void onLeftClick(NPCLeftClickEvent e);


}
