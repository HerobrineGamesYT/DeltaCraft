package net.herobrine.deltacraft.objects.dungeon;

import net.herobrine.deltacraft.objects.DeltaObject;
import net.herobrine.deltacraft.objects.ObjectState;
import net.herobrine.deltacraft.objects.ObjectTypes;
import net.herobrine.deltacraft.objects.Objects;

import java.util.UUID;

public class DungeonKey extends DeltaObject {
    public DungeonKey(ObjectTypes type, Objects object, int id, UUID uuid) {
        super(type, object, id, uuid);
    }

    @Override
    public void initObject() {
    state = ObjectState.ACTIVE;
    }

    @Override
    public void destroyObject() {
        arena.getDeltaGame().getObjectManager().unregisterObject(uuid);
    }
}
