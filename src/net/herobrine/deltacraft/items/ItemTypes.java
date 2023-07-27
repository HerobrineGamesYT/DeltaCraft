package net.herobrine.deltacraft.items;

import net.herobrine.core.HerobrinePVPCore;
import net.herobrine.core.ItemBuilder;
import net.herobrine.core.SkullMaker;
import net.herobrine.deltacraft.utils.NBTReader;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.NBTTagList;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;

public enum ItemTypes {

    BERS_WEAPON(ChatColor.GREEN + "Berserker Sword",
            new String[] {ChatColor.GREEN + "Test"}, null, Material.DIAMOND_SWORD,
            15, 0, 0, 100, 5, new ItemAbilities[] {ItemAbilities.ABILITY_TEST}, false, null, true, false,
            null),
    BERS_HELMET(ChatColor.GREEN + "Berserker Helmet", new String[] {}, null, Material.LEATHER_HELMET, 0, 75, 50, 0, 3,
            new ItemAbilities[] {}, true, Color.GREEN, true, false, null),
    BERS_CHESTPLATE(ChatColor.GREEN + "Berserker Chestplate", new String[] {ChatColor.GREEN + "This is lore!", ChatColor.GREEN + "mhm, that's lore alright."},
            new String[]{ChatColor.GRAY + "Wearing this chestplate also",ChatColor.GRAY + "makes you feel awesome!"}, Material.LEATHER_CHESTPLATE,
            0, 150, 100, 0, 3, new ItemAbilities[]{ItemAbilities.SNEAK_TEST}, true, Color.GREEN, true, false,
            null),
    BERS_LEGGINGS(ChatColor.GREEN + "Berserker Leggings", new String[] {}, null, Material.LEATHER_LEGGINGS, 0, 100, 75, 0,3,
            new ItemAbilities[] {}, true, Color.GREEN, true, false, null),

    BERS_BOOTS(ChatColor.GREEN + "Berserker Boots", new String[] {}, null, Material.LEATHER_BOOTS, 0, 50, 50, 0, 3,
            new ItemAbilities[] {}, true, Color.GREEN, true, false, null),

    TANK_HELMET(ChatColor.RED + "Tank Helmet", new String[] {}, null, Material.LEATHER_HELMET, 0, 150, 200, 0,0,
            new ItemAbilities[] {}, true, Color.GRAY, true, false, null),
    TANK_CHESTPLATE(ChatColor.RED + "Tank Chestplate", new String[] {}, null, Material.LEATHER_CHESTPLATE, 0, 300, 500, 0,0,
            new ItemAbilities[] {}, true, Color.GRAY, true, false,  null),
    TANK_LEGGINGS(ChatColor.RED + "Tank Leggings", new String[] {}, null, Material.LEATHER_LEGGINGS, 0, 250, 400, 0,0,
            new ItemAbilities[] {}, true, Color.GRAY , true, false, null),
    TANK_BOOTS(ChatColor.RED + "Tank Boots", new String[] {}, null, Material.LEATHER_BOOTS, 0, 100, 100, 0,0,
            new ItemAbilities[] {}, true, Color.GRAY, true, false, null),
    THX_HAXOR(ChatColor.GOLD + "Thanks Haxor!", new String[] {ChatColor.GRAY + "You made a really", ChatColor.GRAY + "cool render!"},
            new String[] {ChatColor.GRAY + "I'm developing my own", ChatColor.GRAY + "dungeons mode based off",
                    ChatColor.BLUE + "skyblock" + ChatColor.GRAY + ", and it's encouraging to have", ChatColor.GRAY + "inspiration in the background!"} ,
            Material.GOLDEN_APPLE, 69, 420, 9000, 999, 9001, new ItemAbilities[]{ItemAbilities.HAXOR}, false, null, true, false
    ,null),
    TELEPORT_WAND(ChatColor.GREEN + "Teleport Wand" ,new String[] {}, null, Material.STICK, 0 ,0 , 0,0 ,0,
            new ItemAbilities[] {ItemAbilities.INSTANT_TRANSMISSION}, false, null, true, false, null),
    ENEMY_BOW(ChatColor.RED + "Enemy Bow" , new String[] {}, null, Material.BOW, 45, 0, 0, 0,0,new ItemAbilities[] {}, false, null, true, false, null),
    PURE_HEART_GREEN(ChatColor.GREEN + "Pure Heart", new String[] {ChatColor.YELLOW + "Put this in a", ChatColor.LIGHT_PURPLE + "Pure Heart Pillar",
            ChatColor.YELLOW + "before it's too " + ChatColor.RED + "late!"}, null, null, 0, 0, 0,0, 0, new ItemAbilities[] {ItemAbilities.PURE_HEART}, false, null, false, true,
            "http://textures.minecraft.net/texture/dff850f8e638920024a818137c393b3c8816e0d19780027e34f80cc9f1e4fde6"),
    PURE_HEART_ORANGE(ChatColor.GOLD + "Pure Heart", new String[] {ChatColor.YELLOW + "Put this in a", ChatColor.LIGHT_PURPLE + "Pure Heart Pillar",
            ChatColor.YELLOW + "before it's too " + ChatColor.RED + "late!"}, null, null, 0, 0,0, 0, 0, new ItemAbilities[] {ItemAbilities.PURE_HEART}, false, null, false, true,
            "http://textures.minecraft.net/texture/33f7d2d8e45ca92adbed1b9b8852e3f0df1c16f29faaf7676a48c7cffe24e39d"),
    PURE_HEART_RED(ChatColor.RED + "Pure Heart", new String[] {ChatColor.YELLOW + "Put this in a", ChatColor.LIGHT_PURPLE + "Pure Heart Pillar",
            ChatColor.YELLOW + "before it's too " + ChatColor.RED + "late!"}, null, null, 0, 0, 0, 0, 0, new ItemAbilities[] {ItemAbilities.PURE_HEART}, false, null, false, true,
            "http://textures.minecraft.net/texture/e2a4ee1805cb702321d5f6887d472180e32d42152cdda9562570a66ed43aa303"),
    PURE_HEART_PURPLE(ChatColor.LIGHT_PURPLE + "Pure Heart", new String[] {ChatColor.YELLOW + "Put this in a", ChatColor.LIGHT_PURPLE + "Pure Heart Pillar",
            ChatColor.YELLOW + "before it's too " + ChatColor.RED + "late!"}, null, null, 0, 0, 0, 0, 0, new ItemAbilities[] {ItemAbilities.PURE_HEART}, false, null, false, true,
            "http://textures.minecraft.net/texture/c7fb397d7b3d2295451a8c293a308c49aac4114ecede3f3d83b14bb37de71d26"),
    PURE_HEART_YELLOW(ChatColor.YELLOW + "Pure Heart", new String[] {ChatColor.YELLOW + "Put this in a", ChatColor.LIGHT_PURPLE + "Pure Heart Pillar",
            ChatColor.YELLOW + "before it's too " + ChatColor.RED + "late!"}, null, null, 0, 0, 0, 0, 0, new ItemAbilities[] {ItemAbilities.PURE_HEART}, false, null, false, true,
            "http://textures.minecraft.net/texture/dcde3cf654c63fe8a9a3d19638a9eb2d5d9383a665f36dff06fcfbe13253e738"),
    PURE_HEART_BLUE(ChatColor.DARK_AQUA + "Pure Heart", new String[] {ChatColor.YELLOW + "Put this in a", ChatColor.LIGHT_PURPLE + "Pure Heart Pillar",
            ChatColor.YELLOW + "before it's too " + ChatColor.RED + "late!"}, null, null, 0, 0, 0, 0,0, new ItemAbilities[] {ItemAbilities.PURE_HEART}, false, null, false, true,
            "http://textures.minecraft.net/texture/75dda4b6cdd0b76cc50b2a479720bb02305710ff9d4494efa4b26dfe74697ed0"),
    PURE_HEART_WHITE(ChatColor.WHITE + "Pure Heart", new String[] {ChatColor.YELLOW + "Put this in a", ChatColor.LIGHT_PURPLE + "Pure Heart Pillar",
            ChatColor.YELLOW + "before it's too " + ChatColor.RED + "late!"}, null, null, 0, 0, 0, 0, 0, new ItemAbilities[] {ItemAbilities.PURE_HEART}, false, null, false, true,
            "http://textures.minecraft.net/texture/d8306d28a6ab60dfe12931bd0372f1f28efcd52656309a321dba2e5325d59af"),
    PURE_HEART_CYAN(ChatColor.AQUA + "Pure Heart", new String[] {ChatColor.YELLOW + "Put this in a", ChatColor.LIGHT_PURPLE + "Pure Heart Pillar",
            ChatColor.YELLOW + "before it's too " + ChatColor.RED + "late!"}, null, null, 0, 0, 0, 0,0, new ItemAbilities[] {ItemAbilities.PURE_HEART}, false, null, false, true,
            "http://textures.minecraft.net/texture/ada5e9602a6b0523a2e4204770e913d79abdfee23bbd6c82ff3d5d3308fd298");



    private String display;
    private String[] lore;
    private String[] secondaryLore;
    private Material material;
    private int damage;
    private int defense;
    private int health;
    private int mana;
    private int strength;
    private ItemAbilities[] abilities;

    private boolean isLeatherArmor;

    private Color leatherArmorColor;

    private boolean glow;

    private boolean isSkull;

    private String url;
    private ItemTypes(String display, String[] lore, String[] secondaryLore, Material material, int damage, int defense, int health, int mana, int strength,
                      ItemAbilities[] abilties, boolean isLeatherArmor, Color leatherArmorColor, boolean glow, boolean isSkull, String url) {

        this.display = display;
        this.lore = lore;
        this.secondaryLore = secondaryLore;
        this.material = material;
        this.damage = damage;
        this.defense = defense;
        this.health = health;
        this.mana = mana;
        this.strength = strength;
        this.abilities = abilties;
        this.isLeatherArmor = isLeatherArmor;
        this.leatherArmorColor = leatherArmorColor;
        this.glow = glow;
        this.isSkull = isSkull;
        this.url = url;

    }
    public String getDisplay() {return display;}
    public String[] getLore() {return lore;}

    public String[] getSecondaryLore() {return secondaryLore;}
    public Material getMaterial() {return material;}
    public int getDamage() {return damage;}
    public int getDefense() {return defense;}

    public int getHealth() {return health;}
    public int getMana() {return mana;}
    public int getStrength() {return strength;}
    // An array of abilities that the item has.
    public ItemAbilities[] getAbilities() {return abilities;}
    public boolean isLeatherArmor() {return isLeatherArmor;}
    public Color getLeatherArmorColor() {return leatherArmorColor;}

    public boolean glow(){return glow;}

    public boolean isSkull() {return isSkull;}

    public String getURL() {return url;}

    public boolean hasNoStats() {return getDamage() == 0 && getStrength() == 0 && getDefense() == 0 && getMana() == 0;}

    public static ItemStack build(ItemTypes type) {
        if (type.isSkull()) {
            SkullMaker skull = new SkullMaker(type.getDisplay(), doLore(type), type.getURL());

            NBTReader reader = new NBTReader(skull.getSkull());

            reader.writeStringNBT("id", type::name);
            reader.writeIntNBT("damage", type::getDamage);
            reader.writeIntNBT("strength", type::getStrength);
            reader.writeIntNBT("defense", type::getDefense);
            reader.writeIntNBT("health", type::getHealth);
            reader.writeIntNBT("mana", type::getMana);

            return reader.toBukkit();
        }

        ItemBuilder item = new ItemBuilder(type.getMaterial());

        item.addItemFlag(ItemFlag.HIDE_ATTRIBUTES);
        item.addItemFlag(ItemFlag.HIDE_ENCHANTS);
        item.addItemFlag(ItemFlag.HIDE_UNBREAKABLE);
        item.setUnbreakable(true);

        item.setDisplayName(type.getDisplay());


        item.setLore(doLore(type));


        if (type.isLeatherArmor()) item.setColor(type.getLeatherArmorColor());
        if(type.glow()) item.addEnchant(Enchantment.DURABILITY, 1);

        ItemStack itemStack = item.build();

        NBTReader reader = new NBTReader(itemStack);

        reader.writeStringNBT("id", type::name);
        reader.writeIntNBT("damage", type::getDamage);
        reader.writeIntNBT("strength", type::getStrength);
        reader.writeIntNBT("defense", type::getDefense);
        reader.writeIntNBT("health", type::getHealth);
        reader.writeIntNBT("mana", type::getMana);


        return reader.toBukkit();
    }

    public static ArrayList<String> doLore(ItemTypes type) {
        ArrayList<String> lore = new ArrayList<String>();
        if (type.getDamage() != 0) lore.add(ChatColor.GRAY + "Damage: " + ChatColor.RED + "+" + type.getDamage());
        if (type.getStrength() != 0) lore.add(ChatColor.GRAY + "Strength: " + ChatColor.RED + "+" + type.getStrength());
        if (type.getDefense() != 0) lore.add(ChatColor.GRAY + "Defense: " + ChatColor.GREEN + "+" + type.getDefense());
        if (type.getHealth() != 0) lore.add(ChatColor.GRAY + "Health: " + ChatColor.GREEN + "+" + type.getHealth());
        if (type.getMana() != 0) lore.add(ChatColor.GRAY + "Intelligence: " + ChatColor.GREEN + "+" + type.getMana());

        //We'll add a blank line if the item has any stats, for UI cleanliness between the stats and lore.
        if(!type.hasNoStats()) lore.add(" ");

        try {lore.addAll(Arrays.asList(type.getLore())); }
        catch(NullPointerException e) {lore.add(ChatColor.RED + "No description set.");}

        for (ItemAbilities ability : type.getAbilities()) {
            lore.add("");
            lore.add(HerobrinePVPCore.translateString("&6Ability: " + ability.getDisplay() + " " + ability.getType().getLoreName()));
            lore.addAll(Arrays.asList(ability.getDescription()));

            if(ability.getManaCost() != 0) lore.add(ChatColor.DARK_GRAY + "Mana Cost: " + ChatColor.DARK_AQUA + ability.getManaCost());
            if (ability.getCooldown() != 0) lore.add(ChatColor.DARK_GRAY + "Cooldown: " + ChatColor.GREEN + ability.getCooldown() / 1000 + "s");
        }

        if(type.getSecondaryLore() != null) {
            lore.add("");
            lore.addAll(Arrays.asList(type.getSecondaryLore()));
        }

        return lore;
    }

}
