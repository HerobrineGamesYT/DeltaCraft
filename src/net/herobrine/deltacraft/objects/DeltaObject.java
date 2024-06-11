package net.herobrine.deltacraft.objects;

import net.herobrine.deltacraft.DeltaCraft;
import net.herobrine.gamecore.Arena;
import net.herobrine.gamecore.Manager;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldParticles;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.Listener;

import java.util.UUID;

public abstract class DeltaObject implements Listener {
    protected ObjectTypes type;
    protected Objects object;
    protected int id;
    protected Arena arena;
    protected UUID uuid;
    protected ObjectState state;
    private Location location;
    public DeltaObject(ObjectTypes type, Objects object, int id, UUID uuid) {
        this.type = type;
        this.object = object;
        this.id = id;
        this.uuid = uuid;
        this.state = ObjectState.INACTIVE;

        arena = Manager.getArena(id);

        Bukkit.getPluginManager().registerEvents(this, DeltaCraft.getInstance());
    }

    public abstract void initObject();


    // Used to get locations for block-based objects.
    public UUID getUUID() {return uuid;}
    public Location getLocation() {return location;}
    public void setLocation(Location loc) {this.location = loc;}
    // types are: CLUSTER, CIRCLE
    public void createParticles(Location loc) {
        PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(object.getParticle(), true,
                (float) loc.getX(), (float) loc.getY(), (float) loc.getZ(), 0F, 0F,0F, 0F, 10, null);

        arena.sendPacket(packet);
    }


    public ObjectState getState() {return state;}
    public void setState(ObjectState state) {this.state = state;}

    public abstract void destroyObject();
}
