package net.herobrine.deltacraft.items.abilities;

import net.herobrine.deltacraft.items.ItemAbilities;
import net.herobrine.deltacraft.items.ItemAbility;
import net.herobrine.deltacraft.items.ItemTypes;
import net.herobrine.deltacraft.items.SpecialCase;
import org.bukkit.entity.Player;

public class PureHeart extends ItemAbility implements SpecialCase {

    public PureHeart(ItemAbilities ability, ItemTypes item, int id) {
        super(ItemAbilities.PURE_HEART, item, id);
    }

    @Override
    public void doAbility(Player player) {

    }

    @Override
    public boolean doesCasePass(Player player) {
        return false;
    }

    @Override
    public void doNoPass(Player player) {

    }
}
