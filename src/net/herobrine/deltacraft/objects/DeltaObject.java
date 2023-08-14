package net.herobrine.deltacraft.objects;

import net.herobrine.deltacraft.DeltaCraft;
import net.herobrine.gamecore.Arena;
import net.herobrine.gamecore.Manager;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldParticles;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.Listener;

public abstract class DeltaObject implements Listener {
    private ObjectTypes type;
    private Objects object;
    private int id;
    private Arena arena;
    public DeltaObject(ObjectTypes type, Objects object, int id) {
        this.type = type;
        this.object = object;
        this.id = id;

        arena = Manager.getArena(id);

        Bukkit.getPluginManager().registerEvents(this, DeltaCraft.getInstance());
    }

    public abstract void initObject();


    // types are: CLUSTER, CIRCLE
    public void createParticles(Location loc) {
        PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(object.getParticle(), true,
                (float) loc.getX(), (float) loc.getY(), (float) loc.getZ(), 0F, 0F,0F, 0F, 10, null);

        arena.sendPacket(packet);
    }



    public abstract void destroyObject();
}
