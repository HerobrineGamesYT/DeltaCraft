package net.herobrine.deltacraft.objects.puzzles;

import javafx.scene.effect.Light;
import net.herobrine.deltacraft.objects.DeltaObject;
import net.herobrine.deltacraft.objects.puzzles.type.*;

import javax.sound.midi.Sequence;
import java.util.concurrent.ThreadLocalRandom;

public class PuzzleManager {


    public static Puzzle selectPuzzle(PuzzleTypes[] desiredPuzzles, DeltaObject object) {
        PuzzleTypes chosenPuzzle =  desiredPuzzles[ThreadLocalRandom.current().nextInt(0, desiredPuzzles.length)];
        switch(chosenPuzzle) {
            case LIGHT_PUZZLE:
                return new LightsPuzzle(object, chosenPuzzle);
            case SEQUENCE_PUZZLE:
                return new SequencePuzzle(object, chosenPuzzle);
            case LIGHT_SWITCH:
                return new LightSwitchPuzzle(object, chosenPuzzle);
            case BUTTON_TIMING:
                return new ButtonTimingPuzzle(object, chosenPuzzle);
            case LANE_MERGE:
                return new LaneMergePuzzle(object, chosenPuzzle);
            case DOOR:
                return new DoorPuzzle(object, chosenPuzzle, false);
            default: return null;
        }
    }

}
