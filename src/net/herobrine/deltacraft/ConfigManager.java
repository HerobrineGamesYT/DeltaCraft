package net.herobrine.deltacraft;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class ConfigManager {

   private static DeltaCraft main;

    public ConfigManager(DeltaCraft main) {
        ConfigManager.main = main;
        main.saveDefaultConfig();
    }


    public static Location getSpawnLocation(int arenaID) {
       return new Location(Bukkit.getWorld(main.getConfig().getString("missions." + arenaID + ".world")),
               main.getConfig().getDouble("missions." + arenaID + ".x"),
                main.getConfig().getDouble("missions." + arenaID + ".y"), main.getConfig().getDouble("missions." + arenaID + ".z"),
                main.getConfig().getInt("missions." + arenaID + ".pitch"), main.getConfig().getInt("missions." + arenaID + ".yaw"));
    }
}
