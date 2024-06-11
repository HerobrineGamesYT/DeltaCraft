package net.herobrine.deltacraft.objects.puzzles;

public interface LinkedPuzzle {
    void setLinkedPuzzle(Puzzle puzzle);
    Puzzle getLinkedPuzzle();
    void setPrimaryPuzzle(boolean isPrimaryPuzzle);
    boolean isPrimaryPuzzle();
}
