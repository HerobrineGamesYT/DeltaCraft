package net.herobrine.deltacraft.game;

import net.herobrine.deltacraft.utils.NBTReader;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PlayerStats {

    private int health;

    private int maxHealth;
    private int defense;
    private int mana;
    private int intelligence;
    private int strength;

    private int lastRegenMana;

    private boolean manaUnchanged = false;

    // Used to make sure we have no oddities in player stats. Stat increases are calculated from this base set everytime stats need to be updated through equipment.

    // {maxHealth, defense, intelligence, strength}
    // Why do we not include mana? Because equipment can't increase the mana stat directly. Only intelligence.
    // We also don't include health in this for the same reason. If a player takes their armor off they may lose some max health.
    // If they have more health than max health, their health will be set back down accordingly. Same goes for mana/intelligence.
    // However, we will set a boolean to true in these cases, so that if a player puts the equipment back on, and their stats haven't changed, then they get their stats back.
    // If their stats change in that time, then we'll set it to false.
    private int[] statsBeforeEquipment;

    public PlayerStats(int health, int maxHealth, int defense, int mana, int intelligence, int strength) {
        this.health = health;
        this.maxHealth = maxHealth;
        this.defense = defense;
        this.mana = mana;
        this.intelligence = intelligence;
        this.strength = strength;
        this.lastRegenMana = mana;
    }

    // THIS SHOULD BE SET UP IN THE ONSTART METHOD FOR EVERY PLAYER CLASS.
    public void setupEquipment(Player player) {
        statsBeforeEquipment = new int[]{getMaxHealth(), getDefense(), getIntelligence(), getStrength()};
        int health = 0;
        int defense = 0;
        int intelligence = 0;
        int strength = 0;

        for (ItemStack stack : player.getEquipment().getArmorContents()) {
            try {
                if(stack != null) {
                    NBTReader reader = new NBTReader(stack);
                    health = health + reader.getIntNBT("health").get();
                    defense = defense + reader.getIntNBT("defense").get();
                    intelligence = intelligence + reader.getIntNBT("mana").get();
                    strength = strength + reader.getIntNBT("strength").get();
                }
            }
            catch(Exception ignored) {}
        }
        if (player.getItemInHand() != null && !player.getItemInHand().getType().equals(Material.AIR)) {
            NBTReader reader = new NBTReader(player.getItemInHand());
            health = health + reader.getIntNBT("health").get();
            defense = defense + reader.getIntNBT("defense").get();
            intelligence = intelligence + reader.getIntNBT("mana").get();
            strength = strength + reader.getIntNBT("strength").get();
        }

        // We set the values for the one time setup.
        // All original values from init/onStart are stored in the statsBeforeEquipment array.
        setMaxHealth(health + getMaxHealth());
        setHealth(getMaxHealth());
        setDefense(getDefense() + defense);
        setIntelligence(getIntelligence() + intelligence);
        setStrength(getStrength() + strength);
    }

    public void updateEquipment(Player player) {
        int health = 0;
        int defense = 0;
        int intelligence = 0;
        int strength = 0;
        for (ItemStack stack : player.getEquipment().getArmorContents()) {
            try {

                if(stack != null) {
                    NBTReader reader = new NBTReader(stack);
                    health = health + reader.getIntNBT("health").get();
                    defense = defense + reader.getIntNBT("defense").get();
                    intelligence = intelligence + reader.getIntNBT("mana").get();
                    strength = strength + reader.getIntNBT("strength").get();
                }
            }
            catch(Exception ignored) {}
        }

        if (player.getItemInHand() != null && !player.getItemInHand().getType().equals(Material.AIR)) {
            NBTReader reader = new NBTReader(player.getItemInHand());
            health = health + reader.getIntNBT("health").get();
            defense = defense + reader.getIntNBT("defense").get();
            intelligence = intelligence + reader.getIntNBT("mana").get();
            strength = strength + reader.getIntNBT("strength").get();
        }
        boolean shouldIncreaseHP = getHealth() == getMaxHealth();
        setMaxHealth(health + statsBeforeEquipment[0]);
        if (shouldIncreaseHP) setHealth(getMaxHealth());
        if(getMaxHealth() < getHealth()) setHealth(getMaxHealth());
        setDefense(defense + statsBeforeEquipment[1]);
        setIntelligence(intelligence + statsBeforeEquipment[2]);

        if (getIntelligence() > lastRegenMana && mana < lastRegenMana) setMana(lastRegenMana);
        if (lastRegenMana > getIntelligence() || mana > getIntelligence()) setManaSpecial(getIntelligence());
        setStrength(strength + statsBeforeEquipment[3]);
    }

    public int getHealth() {return health;}

    public int getMaxHealth() {return maxHealth;}
    public int getDefense() {return defense;}

    public int getMana() {return mana;}

    public int getIntelligence() {return intelligence;}

    public int getStrength() {return strength;}




    public void setHealth(int health) {
        this.health = health;
    }

    public void setMaxHealth(int health) {this.maxHealth = health;}

    public void setDefense(int defense) {this.defense = defense;}

    public void setMana(int mana) {
        lastRegenMana = mana;
        this.mana = mana;
    }

    public void setManaSpecial(int mana) {
        this.mana = mana;
    }

    public void setIntelligence(int intelligence) {this.intelligence = intelligence;}

    public void setStrength(int strength) {this.strength = strength;}

    public int[] getStatsBeforeEquipment() {return statsBeforeEquipment;}




}
