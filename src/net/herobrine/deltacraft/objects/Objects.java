package net.herobrine.deltacraft.objects;

import net.herobrine.deltacraft.items.ItemAbilities;
import net.minecraft.server.v1_8_R3.EnumParticle;
import org.bukkit.ChatColor;
import org.bukkit.Material;

public enum Objects {
    PORTAL_CLUSTER(EnumParticle.PORTAL, ObjectTypes.PARTICLE),
    HEART_PILLAR(null, ObjectTypes.INTERACTABLE),
    PUZZLE_TERMINAL(null, ObjectTypes.PUZZLE),
    PURE_HEART_GREEN(null, ObjectTypes.INTERACTABLE),
    PURE_HEART_ORANGE(null, ObjectTypes.INTERACTABLE),
    PURE_HEART_RED( null, ObjectTypes.INTERACTABLE),
    PURE_HEART_PURPLE(null, ObjectTypes.INTERACTABLE),
    PURE_HEART_YELLOW(null, ObjectTypes.INTERACTABLE),
    PURE_HEART_BLUE(null, ObjectTypes.INTERACTABLE),
    PURE_HEART_WHITE(null, ObjectTypes.INTERACTABLE),
    PURE_HEART_CYAN(null, ObjectTypes.INTERACTABLE);


    private EnumParticle particle;
    private ObjectTypes type;


    private Objects (EnumParticle particle, ObjectTypes type) {
        this.particle = particle;
        this.type = type;

    }


    public EnumParticle getParticle() {return particle;}
    public ObjectTypes getType() {return type;}




}
