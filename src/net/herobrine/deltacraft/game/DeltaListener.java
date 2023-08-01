package net.herobrine.deltacraft.game;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.CitizensEnableEvent;
import net.citizensnpcs.api.event.NPCDamageByEntityEvent;
import net.citizensnpcs.api.npc.NPC;
import net.herobrine.core.HerobrinePVPCore;
import net.herobrine.core.SongPlayer;
import net.herobrine.core.Songs;
import net.herobrine.deltacraft.DeltaCraft;
import net.herobrine.deltacraft.characters.CustomEntityManager;
import net.herobrine.deltacraft.items.ItemTypes;
import net.herobrine.deltacraft.utils.NBTReader;
import net.herobrine.gamecore.*;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.NoSuchElementException;
import java.util.UUID;

public class DeltaListener implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        Arena arena;
        if (Manager.isPlaying(player))  arena = Manager.getArena(player);
        else return;

        if(e.getClickedBlock() == null) return;

        if (arena.getGame(arena.getID()).equals(Games.DELTARUNE) && arena.getState().equals(GameState.LIVE) && e.getClickedBlock().getType().equals(Material.STAINED_CLAY)) {
            if (arena.getDeltaGame().isBossActive()){
                player.sendMessage(ChatColor.RED + "You already started the boss, idiot.");
                return;
            }
            player.sendMessage(ChatColor.RED + "Hey, this would start the boss if you actually coded that part of the game.");
            player.sendMessage(ChatColor.GREEN + "Instead, why don't you listen to the boss music?");
            arena.getDeltaGame().setBossState(true);
            Bukkit.getScheduler().runTaskLater(DeltaCraft.getInstance(), () -> {
                arena.getDeltaGame().startBoss();
                player.getLocation().getWorld().strikeLightningEffect(new Location(Bukkit.getWorld("dungeonTest"),-218.5, 5, -55.5));
                player.sendMessage(ChatColor.RED + "Yeah, the boss would've spawned just there. If you weren't stupid.");
            }, 280L);
           for(UUID uuid: arena.getPlayers()){
               Player player1 = Bukkit.getPlayer(uuid);
               SongPlayer.playSong(player1, Songs.ULTIMATE_SHOW);
           }
        }


    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            if (!CitizensAPI.getNPCRegistry().isNPC(e.getEntity())) {
                Player player = (Player) e.getEntity();

                Arena arena;
                if (Manager.isPlaying(player)) arena = Manager.getArena(player);

                else return;

                if (arena.getGame(arena.getID()).equals(Games.DELTARUNE) && arena.getState().equals(GameState.LIVE)) {
                   /* We'll deal damage as raw MC damage, HOWEVER, we will never let the player die in the raw MC fashion. We shall run a calculation to
                   determine how much damage we should be dealing to the player's health in the game, and how that should reflect on their health bar.
                   In the event that we must "kill" the player, we'll just simply teleport them and do all the yada yada we need to do.*/

                    int health = arena.getDeltaGame().getStats(player).getHealth();
                    int maxHealth = arena.getDeltaGame().getStats(player).getMaxHealth();
                    int defense = arena.getDeltaGame().getStats(player).getDefense();
                    double damage = e.getDamage();
                    if (damage == 0) return;

                    double damageReduction = (double)defense / ((double)defense + 100);

                    int trueDamage;
                    if (defense != 0) trueDamage = (int)Math.round(damage - (damage * damageReduction));
                    else trueDamage = (int)Math.round(damage);

                    player.sendMessage(ChatColor.GREEN + "You just took " + ChatColor.RED + trueDamage + "❁ Damage!");
                    int newHealth = health - trueDamage;
                    if(!(newHealth <= 0)) arena.getDeltaGame().getStats(player).setHealth(newHealth);
                    else {
                        player.sendMessage(ChatColor.RED + "You have died!");
                    }

                    double healthPercent = (double)newHealth / (double)maxHealth;
                    double playerHealth = player.getMaxHealth() * healthPercent;

                    e.setDamage(0.0);
                    if (playerHealth != 0) player.setHealth(playerHealth);


                }
            }

        }


    }


    @EventHandler
    public void onNPCDamage(NPCDamageByEntityEvent e) {
        if (Manager.getArena(e.getNPC().getEntity().getWorld()) == null) return;
        if (!Manager.getArena(e.getNPC().getEntity().getWorld()).getGame().equals(Games.DELTARUNE)) return;
        Arena arena = Manager.getArena(e.getNPC().getEntity().getWorld());

        NPC npc = e.getNPC();
        LivingEntity npcEnt = (LivingEntity) e.getNPC().getEntity();

        if (!CustomEntityManager.getEntityMap().containsKey(npcEnt)) return;
        if (npc.isProtected()) return;

        if (e.getDamager() instanceof Player) {
            Player damager = (Player) e.getDamager();


            if (arena.getDeltaGame().hasHitEntity(damager) && npcEnt.getType().equals(EntityType.PLAYER)) {
                if(arena.getDeltaGame().getLastEntityHit(damager).equals(npcEnt)) {
                    if (System.currentTimeMillis() - arena.getDeltaGame().getLastHitTime(damager) < 500) return;
                }
            }

            if (damager.getItemInHand() != null) {

                try {
                    NBTReader reader = new NBTReader(damager.getItemInHand());
                    ItemTypes item = ItemTypes.valueOf(reader.getStringNBT("id").get());
                    int damage = reader.getIntNBT("damage").get();
                    int strength = reader.getIntNBT("strength").get();

                    int dmg = damage*strength;
                    Character character = CustomEntityManager.getCharFromEnt(npcEnt);
                    int health = character.getHealth();
                    if (health - dmg <= 0) {
                        character.killNPC(damager);
                        damager.sendMessage(ChatColor.GREEN + "You killed a " + character.getName() + "!");

                    }
                    e.setDamage(0);
                    if (character.getType().equals(EntityType.PLAYER)) npcEnt.setVelocity(damager.getLocation().getDirection().multiply(0.5).add(new Vector(0, 0.4, 0)));
                    arena.getDeltaGame().setLastHitEntity(damager, npcEnt);
                    arena.getDeltaGame().setLastHitTime(damager, System.currentTimeMillis());
                    npcEnt.damage(0.0);
                    character.setHealth(health - dmg);
                    damager.sendMessage(ChatColor.GREEN + "You just dealt " + ChatColor.RED + dmg + "❁ Damage!");
                }

                catch(Exception exception) {
                    e.setDamage(0);
                    arena.getDeltaGame().setLastHitEntity(damager, npcEnt);
                    arena.getDeltaGame().setLastHitTime(damager, System.currentTimeMillis());
                    npcEnt.damage(0);
                    if (npcEnt.getType().equals(EntityType.PLAYER)) npcEnt.setVelocity(damager.getLocation().getDirection().multiply(0.5).add(new Vector(0, 0.4, 0)));
                    damager.sendMessage(ChatColor.GREEN + "You just dealt " + ChatColor.RED + "0" + "❁ Damage!");
                }
            }
        }
    }

    @EventHandler
    public void onInvClick(InventoryClickEvent e) {
        if (!e.getClickedInventory().getTitle().equals(ChatColor.translateAlternateColorCodes('&', "&c&lDELTACRAFT &7- Class Selector"))) return;

        Inventory inv = e.getClickedInventory();
        InventoryView view = e.getView();
        int clickedSlot = e.getSlot();
        Arena arena = Manager.getArena((Player)e.getWhoClicked());
        Player player = (Player) e.getWhoClicked();
        if (e.getCurrentItem() == null || e.getCurrentItem().getType().equals(Material.AIR) || e.getCurrentItem().getType().equals(Material.SKULL_ITEM)) return;
        try {

            if (e.getCurrentItem().getType().equals(Material.STAINED_GLASS_PANE)) {
               if (inv.getItem(e.getSlot() - 9).getItemMeta().getDisplayName().contains(player.getName())) {
                   if (!arena.getDeltaGame().getReadiedPlayers().contains(player.getUniqueId())) {
                       arena.getDeltaGame().getReadiedPlayers().add(player.getUniqueId());
                       arena.sendMessage(HerobrinePVPCore.getFileManager().getRank(player).getColor() + player.getName() + ChatColor.GREEN + " is now ready!");
                       arena.getDeltaGame().checkIfReady();
                   }
                   Menus.updateMenuFor(arena, player);
                   for (UUID uuid: arena.getPlayers()) {
                       Player player1 = Bukkit.getPlayer(uuid);
                       if(player1 != player) Menus.updateMenuFor(arena, player1);
                   }
                }
               return;
            }

            String name = ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName());
            if(name.equals("Healer")) name = "HEALER_DELTACRAFT";
            if (name.equals("Archer")) name = "ARCHER_DELTACRAFT";
            if (name.equals("Berserker")) name = "BERSERK";
            ClassTypes type = ClassTypes.valueOf(name.toUpperCase());
            arena.sendMessage(ChatColor.GREEN + player.getName() + " selected the " + type.getDisplay() + ChatColor.GREEN + " class!");
            player.playSound(player.getLocation(), Sound.NOTE_STICKS, 1f, 1f);
            arena.setClass(player.getUniqueId(), type);

            if (!arena.getDeltaGame().getReadiedPlayers().contains(player.getUniqueId())) {
                arena.getDeltaGame().getReadiedPlayers().add(player.getUniqueId());
                arena.sendMessage(HerobrinePVPCore.getFileManager().getRank(player).getColor() + player.getName() + ChatColor.GREEN + " is now ready!");
            }

            Menus.updateMenuFor(arena, player);
            for (UUID uuid: arena.getPlayers()) {
                Player player1 = Bukkit.getPlayer(uuid);
               if(player1 != player) Menus.updateMenuFor(arena, player1);
            }
        }
        catch(Exception exception) {
            ItemStack item = inv.getItem(clickedSlot - 9);
            String name = ChatColor.stripColor(item.getItemMeta().getDisplayName());
            if(name.equals("Healer")) name = "HEALER_DELTACRAFT";
            if (name.equals("Archer")) name = "ARCHER_DELTACRAFT";
            if (name.equals("Berserker")) name = "BERSERK";
            ClassTypes type = ClassTypes.valueOf(name.toUpperCase());
            arena.sendMessage(ChatColor.GREEN + player.getName() + " selected the " + type.getDisplay() + ChatColor.GREEN + " class!");
            player.playSound(player.getLocation(), Sound.NOTE_STICKS, 1f, 1f);
            arena.setClass(player.getUniqueId(), type);

            if (!arena.getDeltaGame().getReadiedPlayers().contains(player.getUniqueId())) {
                arena.getDeltaGame().getReadiedPlayers().add(player.getUniqueId());
                arena.sendMessage(HerobrinePVPCore.getFileManager().getRank(player).getColor() + player.getName() + ChatColor.GREEN + " is now ready!");
                arena.getDeltaGame().checkIfReady();
            }
            Menus.updateMenuFor(arena, player);
            for (UUID uuid: arena.getPlayers()) {
                Player player1 = Bukkit.getPlayer(uuid);
                if(player1 != player) Menus.updateMenuFor(arena, player1);
            }
        }


    }

}
