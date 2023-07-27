package net.herobrine.deltacraft.items;

import net.herobrine.deltacraft.items.abilities.AbilityTest;
import org.bukkit.ChatColor;

public enum ItemAbilities {

    ABILITY_TEST(ChatColor.GOLD + "Test", new String[] {ChatColor.GRAY + "This is just a test ability."}, 50, 5000, AbilityTypes.RIGHT_CLICK, false),
    SNEAK_TEST(ChatColor.GOLD + "Sneak Test", new String[]{ChatColor.GRAY + "Plays a sound when you sneak!", ChatColor.GRAY + "It's not free to use."},
            50, 1000, AbilityTypes.SNEAK, false),
    HAXOR(ChatColor.GOLD + "You're Cool!", new String[]{ChatColor.GRAY + "It is very generous of you", ChatColor.GRAY + "to make people these for free!"}, 0, 0,
            AbilityTypes.PASSIVE, false),

    INSTANT_TRANSMISSION(ChatColor.GOLD + "Instant Transmission", new String[] {ChatColor.GRAY + "Teleport 8 blocks ahead of you",
            ChatColor.GRAY + "and gain a small " + ChatColor.GREEN + "speed" + ChatColor.GRAY + " boost",
            ChatColor.GRAY + "for " + ChatColor.GREEN + "3" + ChatColor.GRAY + " seconds"}, 30, 0, AbilityTypes.RIGHT_CLICK, true),
    PURE_HEART(ChatColor.GOLD + "Savior", new String[] {ChatColor.GRAY + "Place the pure heart in ", ChatColor.GRAY + "a heart pillar to save the world!"},
            0, 0, AbilityTypes.RIGHT_CLICK, true);


    private String display;

    private String[] description;

    private int manaCost;
    private long cooldown;
    private AbilityTypes type;

    private boolean hasSpecialCase;


    private ItemAbilities(String display, String[] description, int manaCost, long cooldown, AbilityTypes type, boolean hasSpecialCase) {
    this.display = display;
    this.description = description;
    this.manaCost = manaCost;
    this.cooldown = cooldown;
    this.type = type;
    this.hasSpecialCase = hasSpecialCase;

    }

    public String getDisplay() {return display;}
    public String[] getDescription() {return description;}

    public int getManaCost() {return manaCost;}


    public static boolean hasManaCost(ItemAbilities type) {return type.getManaCost() > 0;}

    public static boolean hasCooldown(ItemAbilities type) {return type.getCooldown() > 0;}
    public long getCooldown() {return cooldown;}

    public AbilityTypes getType() {return type;}

    public boolean hasSpecialCase() {return hasSpecialCase;}




}


