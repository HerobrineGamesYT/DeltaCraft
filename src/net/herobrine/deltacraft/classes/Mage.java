package net.herobrine.deltacraft.classes;

import net.herobrine.deltacraft.items.ItemTypes;
import net.herobrine.gamecore.Class;
import net.herobrine.gamecore.ClassTypes;
import net.herobrine.gamecore.Manager;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class Mage extends Class {


    public Mage(UUID uuid) {super(uuid, ClassTypes.MAGE);}
    @Override
    public void onStart(Player player) {
        ItemStack helmet = ItemTypes.build(ItemTypes.MAGE_HELMET);
        ItemStack chestplate = ItemTypes.build(ItemTypes.MAGE_CHESTPLATE);
        ItemStack leggings = ItemTypes.build(ItemTypes.MAGE_LEGGINGS);
        ItemStack boots = ItemTypes.build(ItemTypes.MAGE_BOOTS);


        player.getEquipment().setArmorContents(new ItemStack[] {boots, leggings, chestplate, helmet});

        Manager.getArena(player).getDeltaGame().getStats(player).setupEquipment(player);
    }
}
