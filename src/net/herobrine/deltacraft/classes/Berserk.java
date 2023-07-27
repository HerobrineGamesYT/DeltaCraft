package net.herobrine.deltacraft.classes;

import net.herobrine.deltacraft.items.ItemTypes;
import net.herobrine.gamecore.Class;
import net.herobrine.gamecore.ClassTypes;
import net.herobrine.gamecore.Manager;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class Berserk extends Class {

    public Berserk(UUID uuid) {super(uuid, ClassTypes.BERSERK);}

    @Override
    public void onStart(Player player) {

        ItemStack helmet = ItemTypes.build(ItemTypes.BERS_HELMET);
        ItemStack chestplate = ItemTypes.build(ItemTypes.BERS_CHESTPLATE);
        ItemStack leggings = ItemTypes.build(ItemTypes.BERS_LEGGINGS);
        ItemStack boots = ItemTypes.build(ItemTypes.BERS_BOOTS);

        ItemStack weapon = ItemTypes.build(ItemTypes.BERS_WEAPON);


        player.getEquipment().setArmorContents(new ItemStack[] {boots, leggings, chestplate, helmet});
        player.getInventory().addItem(weapon);


        Manager.getArena(player).getDeltaGame().getStats(player).setupEquipment(player);

    }
}
