package net.herobrine.deltacraft.objects.puzzles;

import org.bukkit.ChatColor;

public enum PuzzleTypes {
    SEQUENCE_PUZZLE(ChatColor.GRAY + "Click the numbers in order!", 1), LIGHT_PUZZLE(ChatColor.GRAY + "Make all the lights the same color!", 1),
    LIGHT_SWITCH(ChatColor.GRAY + "Turn on the lights!", 1), BUTTON_TIMING(ChatColor.GRAY + "Hit the button on time!", 1),
    LANE_MERGE(ChatColor.GRAY + "Merge the lanes!", 1),
    DOOR(ChatColor.GRAY + "Serve the cars!", 1);

    private String display;
    private int maxPlayers;

    private PuzzleTypes(String display, int maxPlayers) {
        this.display = display;
        this.maxPlayers = maxPlayers;
    }

    public String getDisplay() {return display;}
    public int getMaxPlayers() {return maxPlayers;}


}
