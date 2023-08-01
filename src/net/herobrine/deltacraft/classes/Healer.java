package net.herobrine.deltacraft.classes;

import net.herobrine.deltacraft.items.ItemTypes;
import net.herobrine.gamecore.Class;
import net.herobrine.gamecore.ClassTypes;
import net.herobrine.gamecore.Manager;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class Healer extends Class {

    public Healer(UUID uuid) {super(uuid, ClassTypes.HEALER_DELTACRAFT);}
    @Override
    public void onStart(Player player) {
        ItemStack helmet = ItemTypes.build(ItemTypes.HEALER_HELMET);
        ItemStack chestplate = ItemTypes.build(ItemTypes.HEALER_CHESTPLATE);
        ItemStack leggings = ItemTypes.build(ItemTypes.HEALER_LEGGINGS);
        ItemStack boots = ItemTypes.build(ItemTypes.HEALER_BOOTS);


        player.getEquipment().setArmorContents(new ItemStack[] {boots, leggings, chestplate, helmet});

        Manager.getArena(player).getDeltaGame().getStats(player).setupEquipment(player);
    }
}
