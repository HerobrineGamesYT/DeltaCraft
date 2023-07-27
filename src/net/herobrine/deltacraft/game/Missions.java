package net.herobrine.deltacraft.game;

import net.herobrine.deltacraft.game.Bosses;
import org.bukkit.ChatColor;
import org.bukkit.Material;

public enum Missions {

	MISSION1(Material.BOOK_AND_QUILL, ChatColor.GRAY + "Mission 1 - " + ChatColor.DARK_GRAY + "A Dark Step",
			new String[] { ChatColor.GRAY + "You take a step into the Dark...",
					ChatColor.DARK_GRAY + "Something doesn't feel quite right.",},
			Bosses.LANCER, false),
	DIMENTIO_TEST(Material.DIAMOND, ChatColor.RED + "Dimentio Test", new String[] {ChatColor.GRAY + "This is a test mission."}, Bosses.DIMENTIO, true);

	private Material item;
	private String name;
	private String[] description;
	private Bosses bosses;
	private boolean isTestMission;

	private Missions(Material item, String name, String[] description, Bosses boss, boolean isTestMission) {

		this.item = item;
		this.name = name;
		this.description = description;
		this.bosses = boss;
		this.isTestMission = isTestMission;

	}


	public boolean isTestMission() {return isTestMission;}

	public Material getItem() {
		return item;

	}

	public String getName() {
		return name;

	}

	public String[] getDescription() {
		return description;

	}

	public Bosses getBoss() {
		return bosses;
	}

}
