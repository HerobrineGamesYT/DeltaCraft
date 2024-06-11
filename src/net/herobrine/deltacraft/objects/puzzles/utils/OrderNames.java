package net.herobrine.deltacraft.objects.puzzles.utils;

public enum OrderNames {
    TAYLOR("Taylor", false), JOSH("Josh", false), DIEGO("Diego", false),
    AZEEM("Azeem", false), CHRISTIAN("Christian", false), TANNER("Tanner", true), LILY("Lily", true),
    ADALIA("Adalia", false), CAMERON("Cameron", false), CHRISTEN("Christen", false), JAMES("James", false),
    ADRIEL("Adriel", false), SARAH("Sarah", false), MELISSA("Melissa", false), SAVANNAH("Savannah",false),
    JACOB("Jacob", false), SUMMER("Summer",false), KAYLEA("Kaylea", false), ADAGNIS("Adagnis", false),
    JENNY("Jenny", false), GENESIS("Genesis", false), ISSAC("Issac", false), ISAIAH("Isaiah", false),
    AJ("AJ", false), NOAH("Noah", false), SHY("Shy",false), NATIA("NaTia", false);

    // Experimental easter egg mode that has a 1% roll upon order creation- it will only activate if this boolean is true- making the easter egg have a <1% chance of activating.
    // This may be changed in the future to something else.
    String display;
    boolean hasEasterEgg;

    private OrderNames(String display, boolean hasEasterEgg) {
        this.display = display;
        this.hasEasterEgg = hasEasterEgg;
    }

    public String getDisplay() {return display;}
    public boolean hasEasterEgg() {return hasEasterEgg;}
}
