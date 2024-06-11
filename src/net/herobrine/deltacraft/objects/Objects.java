package net.herobrine.deltacraft.objects;

import net.herobrine.deltacraft.items.ItemAbilities;
import net.minecraft.server.v1_8_R3.EnumParticle;
import org.bukkit.ChatColor;
import org.bukkit.Material;

public enum Objects {
    PORTAL_CLUSTER(EnumParticle.PORTAL, ObjectTypes.PARTICLE, null),
    HEART_PILLAR(null, ObjectTypes.INTERACTABLE, Material.QUARTZ_BLOCK),
    PUZZLE_TERMINAL(null, ObjectTypes.PUZZLE, Material.COMMAND),
    PURE_HEART_GREEN(null, ObjectTypes.INTERACTABLE, null),
    PURE_HEART_ORANGE(null, ObjectTypes.INTERACTABLE, null),
    PURE_HEART_RED( null, ObjectTypes.INTERACTABLE, null),
    PURE_HEART_PURPLE(null, ObjectTypes.INTERACTABLE, null),
    PURE_HEART_YELLOW(null, ObjectTypes.INTERACTABLE, null),
    PURE_HEART_BLUE(null, ObjectTypes.INTERACTABLE, null),
    PURE_HEART_WHITE(null, ObjectTypes.INTERACTABLE, null),
    PURE_HEART_CYAN(null, ObjectTypes.INTERACTABLE, null),
    CAR(null, ObjectTypes.INVENTORY, Material.WOOL),
    ORDER(null, ObjectTypes.INVENTORY, Material.REDSTONE),
    EXPO(null, ObjectTypes.INVENTORY, Material.INK_SACK);


    private EnumParticle particle;
    private ObjectTypes type;

    private Material blockType;


    private Objects (EnumParticle particle, ObjectTypes type, Material blockType) {
        this.particle = particle;
        this.type = type;
        this.blockType = blockType;

    }


    public EnumParticle getParticle() {return particle;}
    public ObjectTypes getType() {return type;}

    public Material getBlockType() {return blockType;}




}
