package net.herobrine.deltacraft.classes;

import net.herobrine.deltacraft.items.ItemTypes;
import net.herobrine.gamecore.Class;
import net.herobrine.gamecore.ClassTypes;
import net.herobrine.gamecore.Manager;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class Archer extends Class {

   public Archer(UUID uuid) {super(uuid, ClassTypes.ARCHER_DELTACRAFT);}
    @Override
    public void onStart(Player player) {
        ItemStack helmet = ItemTypes.build(ItemTypes.ARCHER_HELMET);
        ItemStack chestplate = ItemTypes.build(ItemTypes.ARCHER_CHESTPLATE);
        ItemStack leggings = ItemTypes.build(ItemTypes.ARCHER_LEGGINGS);
        ItemStack boots = ItemTypes.build(ItemTypes.ARCHER_BOOTS);


        player.getEquipment().setArmorContents(new ItemStack[] {boots, leggings, chestplate, helmet});

        Manager.getArena(player).getDeltaGame().getStats(player).setupEquipment(player);
    }
}
