package net.herobrine.deltacraft.items;

import net.herobrine.core.HerobrinePVPCore;

public enum AbilityTypes {
    RIGHT_CLICK(HerobrinePVPCore.translateString("&e&lRIGHT CLICK")),
    LEFT_CLICK(HerobrinePVPCore.translateString("&e&lLEFT CLICK")),
    SNEAK(HerobrinePVPCore.translateString("&e&lSNEAK")),
    PASSIVE(HerobrinePVPCore.translateString("&e&lPASSIVE")),
    RIGHT_CLICK_SPECIAL(HerobrinePVPCore.translateString("&e&lRIGHT CLICK"));


    private String loreName;
    private AbilityTypes(String loreName) {this.loreName = loreName;}

    public String getLoreName() {return loreName;}
}
