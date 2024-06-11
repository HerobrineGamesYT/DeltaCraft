package net.herobrine.deltacraft.objects.puzzles;

import net.herobrine.core.HerobrinePVPCore;
import net.herobrine.core.SongPlayer;
import net.herobrine.core.Songs;
import net.herobrine.deltacraft.DeltaCraft;
import net.herobrine.deltacraft.objects.DeltaObject;
import net.herobrine.deltacraft.objects.ObjectState;
import net.herobrine.deltacraft.objects.ObjectTypes;
import net.herobrine.deltacraft.objects.Objects;
import net.herobrine.gamecore.Arena;
import net.herobrine.gamecore.Manager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class DimentioBossTerminal extends DeltaObject implements PuzzleObject{

    private Puzzle puzzle;
    private ArmorStand puzzleStatus;
    public DimentioBossTerminal(ObjectTypes type, Objects object, int id, UUID uuid) {
        super(type, object, id, uuid);
        this.puzzle = PuzzleManager.selectPuzzle(new PuzzleTypes[] {PuzzleTypes.SEQUENCE_PUZZLE, PuzzleTypes.LIGHT_PUZZLE, PuzzleTypes.LIGHT_SWITCH, PuzzleTypes.BUTTON_TIMING, PuzzleTypes.LANE_MERGE, PuzzleTypes.DOOR}, this);
        initObject();
    }

    public DimentioBossTerminal(ObjectTypes type, Objects object, int id, UUID uuid, PuzzleTypes puzzleType) {
        super(type, object, id, uuid);
        this.puzzle = PuzzleManager.selectPuzzle(new PuzzleTypes[] {puzzleType}, this);
        initObject();
    }

    @Override
    public void initObject() {
        state = ObjectState.ACTIVE;
        new BukkitRunnable() {
            @Override
            public void run() {
                if (getLocation() != null) {
                    cancel();
                    getLocation().getWorld().getBlockAt(getLocation()).setType(object.getBlockType());
                    puzzleStatus = (ArmorStand) getLocation().getWorld().spawnEntity(getLocation().add(0, 0.5, 0), EntityType.ARMOR_STAND);
                    puzzleStatus.setVisible(false);
                    puzzleStatus.setGravity(false);
                    puzzleStatus.setCustomName(HerobrinePVPCore.translateString("&e&lCLICK"));
                    puzzleStatus.setCustomNameVisible(true);
                }
            }
        }.runTaskTimer(DeltaCraft.getInstance(), 0L, 1L);
    }
    @Override
    public void destroyObject() {
        arena.getDeltaGame().getObjectManager().unregisterObject(uuid);
        puzzle.setState(PuzzleGameState.DESTROYED);
        getLocation().getWorld().getBlockAt(getLocation()).setType(Material.AIR);
        setLocation(null);
        puzzleStatus.setCustomName(ChatColor.GREEN + "This puzzle has been completed!");
        new BukkitRunnable() {
            @Override
            public void run() {
                puzzleStatus.remove();
                puzzleStatus = null;
            }
        }.runTaskLater(DeltaCraft.getInstance(), 30L);
    }

    @Override
    public void onComplete(Player player) {
        player.sendMessage(ChatColor.GREEN + "You have completed the Puzzle " + puzzle.getType() + "!");
        SongPlayer.playSong(player, Songs.WSGWIN);
        destroyObject();
    }

    public Puzzle getPuzzle() {return puzzle;}

}
