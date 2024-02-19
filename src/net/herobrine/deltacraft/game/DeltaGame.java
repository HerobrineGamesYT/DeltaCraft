package net.herobrine.deltacraft.game;

import net.herobrine.core.HerobrinePVPCore;
import net.herobrine.core.SongPlayer;
import net.herobrine.deltacraft.ConfigManager;
import net.herobrine.deltacraft.DeltaCraft;
import net.herobrine.deltacraft.characters.Dimentio;
import net.herobrine.deltacraft.characters.attack.AttackManager;
import net.herobrine.deltacraft.items.AbilityManager;
import net.herobrine.deltacraft.items.ItemTypes;
import net.herobrine.deltacraft.objects.ObjectManager;
import net.herobrine.deltacraft.utils.NBTReader;
import net.herobrine.gamecore.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.libs.jline.internal.Log;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class DeltaGame {

private Arena arena;
private Missions mission;


private Character boss;

private HashMap<UUID, PlayerStats> playerStatsMap;

private HashMap<UUID, Entity> lastHitEntity;

private HashMap<UUID, Long> lastHitTime;

private ArrayList<UUID> readiedPlayers;

private HashMap<UUID, Long> lastClusterUse;


private int seconds;

private boolean bossActive;

private int completionPercentage;

private AbilityManager abilityManager;

private AttackManager attackManager;

private ObjectManager objectManager;

private boolean areAbilitiesInitialized;

private boolean areAttacksInitialized;

private boolean areObjectsInitialized;

    public DeltaGame(Arena arena) {
        this.arena = arena;
        this.seconds = 0;
        this.completionPercentage = 0;
        this.playerStatsMap = new HashMap<>();
        this.lastHitTime = new HashMap<>();
        this.lastHitEntity = new HashMap<>();
        this.areAbilitiesInitialized = false;
        this.areAttacksInitialized = false;
        this.areObjectsInitialized = false;
        this.readiedPlayers = new ArrayList<>();
        this.lastClusterUse = new HashMap<>();
    }

    public void startMission(GameType type) {
       if(!areAbilitiesInitialized) {
           abilityManager = new AbilityManager(arena.getID());
           areAbilitiesInitialized = true;
       }

       if (!areAttacksInitialized) {
           attackManager = new AttackManager(arena);
           areAttacksInitialized = true;
       }

       if (!areObjectsInitialized) {
           objectManager = new ObjectManager(arena.getID());
           areObjectsInitialized = true;
       }

        lastClusterUse.clear();
        mission = Missions.valueOf(type.name());
        DateFormat df = new SimpleDateFormat("MM/dd/yy");
        Date dateobj = new Date();
        seconds = 0;
        completionPercentage = 0;
        bossActive = false;
        if(playerStatsMap != null) playerStatsMap.clear();
        if (lastHitEntity != null) lastHitEntity.clear();
        if (lastHitTime != null) lastHitTime.clear();
        if (mission.isTestMission()) {

            for (UUID uuid: arena.getPlayers()) playerStatsMap.put(uuid, new PlayerStats(1000, 1000, 50, 250, 250, 5));

            for (UUID uuid: arena.getPlayers()) {

                //DELTACRAFT
                //2/12/23 dc4 11
                //            10
                //Time Elapsed: 3:00 9
                //Dungeon Cleared: 100% 8
                //                      7
                //Player1 1000❤        6
                //Player2 1000❤        5
                //Player3 1000❤        4
                //Player4 1000❤        3
                //                      2
                // Development Server   1

                Player player = Bukkit.getPlayer(uuid);

                player.setMaxHealth(40.0);
                player.setHealth(40.0);

                Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
                Objective obj = board.registerNewObjective("game", "dummy");
                obj.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&c&lDELTACRAFT"));
                obj.setDisplaySlot(DisplaySlot.SIDEBAR);

                Team dateAndID = board.registerNewTeam("dateandid");
                dateAndID.addEntry(ChatColor.DARK_RED.toString());
                dateAndID.setPrefix(ChatColor.GRAY + df.format(dateobj) + ChatColor.DARK_GRAY + " dc" + arena.getID());
                obj.getScore(ChatColor.DARK_RED.toString()).setScore(11);

                Score blank1 = obj.getScore(" ");
                blank1.setScore(10);

                Team timer = board.registerNewTeam("timer");
                timer.addEntry(ChatColor.LIGHT_PURPLE.toString());
                timer.setPrefix(ChatColor.GREEN + "Time Elapsed ");
                String time = String.format("%02d:%02d", seconds / 60, seconds % 60);
                timer.setSuffix(ChatColor.GREEN + time);
                obj.getScore(ChatColor.LIGHT_PURPLE.toString()).setScore(9);

                Team dungeonCleared = board.registerNewTeam("clearPercentage");
                dungeonCleared.addEntry(ChatColor.RED.toString());
                String dungeonClearSub = subString("&aDungeon Cleare", 16);
                dungeonCleared.setPrefix(ChatColor.translateAlternateColorCodes('&', dungeonClearSub));
                dungeonCleared.setSuffix(ChatColor.GREEN + "d: " + completionPercentage + "%");

                obj.getScore(ChatColor.RED.toString()).setScore(8);


                Score blank2 = obj.getScore("  ");
                blank2.setScore(7);


                Team playerTeam = board.registerNewTeam("regularPlayer");
                playerTeam.setDisplayName(ChatColor.GREEN + "PLAYER");
                playerTeam.setPrefix(ChatColor.GREEN + "");
                playerTeam.setAllowFriendlyFire(false);

                int i = 0;
                int score = 6;

                ChatColor[] colors = new ChatColor[] {ChatColor.BLUE, ChatColor.DARK_PURPLE, ChatColor.BLACK, ChatColor.DARK_GRAY, ChatColor.DARK_GREEN};
                for (UUID uuid1 : arena.getPlayers()) {
                Player player1 = Bukkit.getPlayer(uuid1);
                if (i > 4) break;

                playerTeam.addPlayer(player1);

                Team playerHealthDisplay = board.registerNewTeam("healthdisplay" + i);
                playerHealthDisplay.addEntry(colors[i].toString());
                playerHealthDisplay.setPrefix(ChatColor.GREEN + player1.getName());
                playerHealthDisplay.setSuffix(" " + ChatColor.GREEN + playerStatsMap.get(player1.getUniqueId()).getHealth() + ChatColor.RED + "❤");
                obj.getScore(colors[i].toString()).setScore(score);

                score--;
                i++;


                }



                Score blank3 = obj.getScore("   ");
                blank3.setScore(2);

                Score ip;
                if (HerobrinePVPCore.getFileManager().getEnvironment().equalsIgnoreCase("DEV")) ip = obj.getScore(ChatColor.translateAlternateColorCodes('&', "&cDevelopment Server"));
                else ip = obj.getScore(ChatColor.translateAlternateColorCodes('&', "&cherobrinepvp.beastmc.com"));
                ip.setScore(1);


                player.setScoreboard(board);

                player.teleport(ConfigManager.getSpawnLocation(arena.getID()));

            }

            for (UUID uuid: arena.getClasses().keySet()) arena.getClasses().get(uuid).onStart(Bukkit.getPlayer(uuid));

            arena.setState(GameState.LIVE);
            startTime();
            arena.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aWelcome to &c&lDELTACRAFT&a! &aThere's not much yet" +
                    " &ato see here. &a&lCome back soon though!"));

        }



    }
    public void setBossState(boolean state) {
        bossActive = state;
    }

    public AttackManager getAttackManager() {return attackManager;}

    public AbilityManager getAbilityManager() {return abilityManager;}

    public ObjectManager getObjectManager() {return objectManager;}

    public HashMap<UUID, Long> getLastClusterUse() {return lastClusterUse;}

    public void startBoss() {
        bossActive = true;
        switch(mission.getBoss()) {
            case DIMENTIO:
                boss = new Dimentio(new Location(Bukkit.getWorld("dungeonTest"),-218.5, 5, -55.5), arena, Characters.DIMENTIO);
                break;
            case LANCER:
               arena.sendMessage(ChatColor.RED + "How did you do this? There's literally no way to play this boss yet.");
                break;

            default:
                return;
        }
    }

    public void stopBoss() {
        if (bossActive) {
            boss.removeNPC();
            for (UUID uuid: arena.getPlayers()) {
                Player player = Bukkit.getPlayer(uuid);
                SongPlayer.stopSong(player);
            }
        }
    }

    public void checkIfReady() {
        if (!arena.getState().equals(GameState.RECRUITING)) return;
        boolean shouldCountdown = true;
       for (UUID uuid : arena.getPlayers()) {
            if (!getReadiedPlayers().contains(uuid)) shouldCountdown = false;
        }

       if (shouldCountdown) {
           arena.setState(GameState.COUNTDOWN);
           arena.getCountdown().begin();
           arena.sendMessage(ChatColor.GREEN + "All players have readied up! Countdown is starting...");
       }
    }

    public boolean isBossActive() {
        return bossActive;
    }

    public ArrayList<UUID> getReadiedPlayers(){return readiedPlayers;}

    public PlayerStats getStats(Player player){return playerStatsMap.get(player.getUniqueId());}
    public PlayerStats getStats(UUID uuid){return playerStatsMap.get(uuid);}

    public Entity getLastEntityHit(Player player) {return lastHitEntity.get(player.getUniqueId());}

    public long getLastHitTime(Player player) {return lastHitTime.get(player.getUniqueId());}

    public void setLastHitTime(Player player, long time) {lastHitTime.put(player.getUniqueId(),time);}

    public void setLastHitEntity(Player player, Entity ent) {lastHitEntity.put(player.getUniqueId(), ent);}

    public boolean hasHitEntity(Player player) {return lastHitTime.containsKey(player.getUniqueId()) && lastHitEntity.containsKey(player.getUniqueId());}

    public void updatePlayerStats(Player player){
        updateStatsFromEquipment(player);

        int health = getStats(player).getHealth();
        int defense = getStats(player).getDefense();
        int mana = getStats(player).getMana();
        int intelligence = getStats(player).getIntelligence();

        GameCoreMain.getInstance().sendActionBar(player, "&c" + health + "❤   &a" + defense + "❈ Defense   &b" + mana + "/" + intelligence + "✎ Mana");
    }

    public void updateStatsFromEquipment(Player player) {
        getStats(player).updateEquipment(player);
    }



    public void regenPlayerMana(Player player) {
        int randomNumber = ThreadLocalRandom.current().nextInt(1, 5);

        double regenPercent = randomNumber *.01;

        int mana = getStats(player).getMana();
        int intelligence = getStats(player).getIntelligence();

        int newMana = (int)Math.round((double)mana*regenPercent);

        if (newMana < 1) newMana = 1;

        int newManaReal = newMana + mana;
        boolean shouldUseSpecial = false;
        if (newManaReal > intelligence) {
            shouldUseSpecial = true;
            newManaReal = intelligence;
        }

        if (!shouldUseSpecial) getStats(player).setMana(newManaReal);
        else getStats(player).setManaSpecial(newManaReal);
        updatePlayerStats(player);


    }

    public void regenPlayerHealth(Player player) {
        int randomNumber = ThreadLocalRandom.current().nextInt(1, 3);

        double regenPercent = randomNumber *.01;

        int health = getStats(player).getHealth();
        int maxHealth = getStats(player).getMaxHealth();

        int newHealth = (int)Math.round((double)health*regenPercent);

        if (newHealth < 1) newHealth = 1;

        int newHealthReal = newHealth + health;
        if (newHealthReal > maxHealth) newHealthReal = maxHealth;

        getStats(player).setHealth(newHealthReal);
        double healthPercent = (double)newHealthReal / (double)maxHealth;
        double playerHealth = player.getMaxHealth() * healthPercent;


        if(playerHealth != 0) player.setHealth(playerHealth);
        updatePlayerStats(player);
    }

    public static String subString(String string, int max) {
        String subbed = string;
        if (string.length() > max) {
            subbed = subbed.substring(0, max);
        }
        if (subbed.length() > max) {

            subString(subbed, max);
        }
        return subbed;
    }

    public void startTime() {
        new BukkitRunnable() {
            @Override
            public void run() {

                if (arena.getState() != GameState.LIVE) {
                    cancel();
                    return;
                }

                for (UUID uuid: arena.getPlayers()) {
                    Player player = Bukkit.getPlayer(uuid);

                    String time = String.format("%02d:%02d", seconds / 60, seconds % 60);


                    if (player.getScoreboard().getObjective(DisplaySlot.SIDEBAR).getDisplayName().contains("DELTACRAFT")) {
                        player.getScoreboard().getTeam("timer").setSuffix(ChatColor.GREEN + time);

                        int i = 0;
                        for (UUID uuid1 : arena.getPlayers()) {
                            if(i > 4) break;

                           if(playerStatsMap.get(uuid1).getHealth() == 0) player.getScoreboard().getTeam("healthdisplay" + i).setSuffix(" " + ChatColor.RED + "DEAD");

                           else player.getScoreboard().getTeam("healthdisplay" + i).setSuffix(" " + ChatColor.GREEN + playerStatsMap.get(uuid1).getHealth() + ChatColor.RED + "❤");

                           i++;
                        }

                    }

                    updatePlayerStats(player);
                    if(getStats(player).getMana() < getStats(player).getIntelligence()) regenPlayerMana(player);
                    if(getStats(player).getHealth() < getStats(player).getMaxHealth()) regenPlayerHealth(player);
                }


                seconds++;
            }

        }.runTaskTimer(DeltaCraft.getInstance(), 0L, 20L);
    }

}
