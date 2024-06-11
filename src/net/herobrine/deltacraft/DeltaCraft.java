package net.herobrine.deltacraft;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import net.citizensnpcs.Citizens;
import net.citizensnpcs.api.CitizensAPI;
import net.herobrine.core.HerobrinePVPCore;
import net.herobrine.deltacraft.commands.*;
import net.herobrine.deltacraft.game.DeltaListener;
import net.herobrine.deltacraft.objects.puzzles.PuzzleMenus;
import net.herobrine.deltacraft.traits.AggressiveTrait;
import net.herobrine.deltacraft.traits.DimentioTrait;
import net.herobrine.deltacraft.traits.SequentialDialogueTrait;
import net.herobrine.gamecore.GameCoreMain;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class DeltaCraft extends JavaPlugin {

	public static boolean isCitizensEnabled;
	public static DeltaCraft instance;

	private PuzzleMenus puzzleMenuManager;

	@Override
	public void onEnable() {
		instance = this;
		if (getGameCoreAPI() == null || getCustomAPI() == null) {
			System.out.println("[DELTA CRAFT] You can't use this plugin without HBPVP Core and GameCore!");
			Bukkit.getServer().getPluginManager().disablePlugin(this);
		}

		if (getCitizensApi() == null) {
			System.out.println("[DELTA CRAFT] Citizens API is missing. What are you doing?");
			Bukkit.getServer().getPluginManager().disablePlugin(this);
		}

		if (getAPI() == null) {
			System.out.println("[DELTA CRAFT] WorldEdit API is missing. What are you doing?");
			Bukkit.getServer().getPluginManager().disablePlugin(this);
		}

		new ConfigManager(this);

		puzzleMenuManager = new PuzzleMenus();
		Bukkit.getPluginManager().registerEvents(new DeltaListener(), this);
		CitizensAPI.getTraitFactory().registerTrait(net.citizensnpcs.api.trait.TraitInfo.create(AggressiveTrait.class));
		CitizensAPI.getTraitFactory().registerTrait(net.citizensnpcs.api.trait.TraitInfo.create(SequentialDialogueTrait.class));
		CitizensAPI.getTraitFactory().registerTrait(net.citizensnpcs.api.trait.TraitInfo.create(DimentioTrait.class));

		getCommand("killboss").setExecutor(new KillBossCommand());
		getCommand("giveitem").setExecutor(new GiveItemCommand());
		getCommand("setstats").setExecutor(new SetStatCommand());
		getCommand("spawncustommob").setExecutor(new SpawnCustomMobCommand());
		getCommand("classmenutest").setExecutor(new ClassMenuCommand());
		getCommand("testobject").setExecutor(new ObjectTestCommand());
	}

	public static DeltaCraft getInstance() {return instance;}

	public WorldEditPlugin getAPI() {
		Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
		if (plugin instanceof WorldEditPlugin) {
			return (WorldEditPlugin) plugin;
		} else {
			return null;
		}
	}
	public GameCoreMain getGameCoreAPI() {
		Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("GameCore");
		if (plugin instanceof GameCoreMain) {
			return (GameCoreMain) plugin;
		} else {
			return null;
		}
	}

	public PuzzleMenus getPuzzleMenuManager() {return puzzleMenuManager;}
	public HerobrinePVPCore getCustomAPI() {
		Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("HBPVP-Core");
		if (plugin instanceof HerobrinePVPCore) {
			return (HerobrinePVPCore) plugin;
		} else {
			return null;
		}
	}

	public Citizens getCitizensApi() {
		Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("Citizens");
		if (plugin instanceof Citizens) {
			return (Citizens) plugin;
		}
		else {
		return null;
		}

	}
}
