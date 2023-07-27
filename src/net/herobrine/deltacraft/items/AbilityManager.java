package net.herobrine.deltacraft.items;

import net.herobrine.deltacraft.items.abilities.AbilityTest;
import net.herobrine.deltacraft.items.abilities.InstantTransmission;

public class AbilityManager {


    private int id;
    public AbilityManager(int id) {
        this.id = id;
        for (ItemTypes item : ItemTypes.values()) { for (ItemAbilities ability : item.getAbilities()) {initializeAbility(item, ability, id); }
        }
    }


    public void initializeAbility(ItemTypes type, ItemAbilities ability, int id) {
        switch (ability) {
            case ABILITY_TEST:
                new AbilityTest(ability, type, id);
                break;
            case SNEAK_TEST:
                break;
            case HAXOR:
                break;
            case INSTANT_TRANSMISSION:
                new InstantTransmission(ability, type, id);
                break;
            default:
                return;
        }
    }

}
