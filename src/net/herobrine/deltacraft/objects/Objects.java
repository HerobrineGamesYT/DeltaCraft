package net.herobrine.deltacraft.objects;

import net.minecraft.server.v1_8_R3.EnumParticle;
import org.bukkit.Material;

public enum Objects {
    PORTAL_CLUSTER(EnumParticle.PORTAL, ObjectTypes.PARTICLE), HEART_PILLAR(null, ObjectTypes.INTERACTABLE), PUZZLE_TERMINAL(null, ObjectTypes.PUZZLE);


    private EnumParticle particle;
    private ObjectTypes type;

    private Objects(EnumParticle particle, ObjectTypes type) {
        this.particle = particle;
        this.type = type;

    }


    public EnumParticle getParticle() {return particle;}
    public ObjectTypes getType() {return type;}




}
