package net.herobrine.deltacraft.classes;

import net.herobrine.gamecore.Class;
import net.herobrine.gamecore.ClassTypes;
import org.bukkit.entity.Player;

import java.util.UUID;

public class Healer extends Class {

    public Healer(UUID uuid) {super(uuid, ClassTypes.HEALER_DELTACRAFT);}
    @Override
    public void onStart(Player player) {

    }
}
