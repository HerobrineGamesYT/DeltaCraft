package net.herobrine.deltacraft.objects.puzzles;

import net.herobrine.deltacraft.objects.DeltaObject;
import net.herobrine.deltacraft.objects.ObjectTypes;
import net.herobrine.deltacraft.objects.Objects;
import net.herobrine.gamecore.Arena;
import net.herobrine.gamecore.Manager;
import org.bukkit.ChatColor;
import org.bukkit.Location;

import java.util.UUID;

public class DimentioBossTerminal extends DeltaObject implements PuzzleObject{

    private Puzzle puzzle;
    public DimentioBossTerminal(ObjectTypes type, Objects object, int id, UUID uuid, Puzzle puzzle, Location spawnLocation) {
        super(type, object, id, uuid);
        this.puzzle = puzzle;
        setLocation(spawnLocation);
        initObject();
    }

    @Override
    public void initObject() {
        if (getLocation() == null) {
            arena.sendDebugMessage(ChatColor.GOLD + "[DEBUG]" + ChatColor.RED + " Error initializing object DimentioBossTerminal. Location is null");
            return;
        }
        getLocation().getWorld().getBlockAt(getLocation()).setType(object.getBlockType());
    }

    @Override
    public void destroyObject() {

    }

    @Override
    public void onComplete() {
        puzzle.setComplete(true);
    }
}
