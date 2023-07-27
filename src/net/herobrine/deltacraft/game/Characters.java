package net.herobrine.deltacraft.game;

import net.herobrine.deltacraft.characters.Dimentio;
import net.herobrine.deltacraft.characters.DummyAttacker;
import net.herobrine.deltacraft.characters.DungeonMaster;
import net.herobrine.deltacraft.characters.TestDummy;
import net.herobrine.deltacraft.characters.attack.AttackTypes;
import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;

public enum Characters {

	DIMENTIO(ChatColor.LIGHT_PURPLE + "Dimentio", ChatColor.LIGHT_PURPLE, 15000, true, EntityType.PLAYER, new AttackTypes[]{AttackTypes.DIMENTIO_BEAM, AttackTypes.DIMENTIO_STAR,
	AttackTypes.DIMENTIO_CLONE, AttackTypes.TIME_WARP}, false, "ewogICJ0aW1lc3RhbXAiIDogMTY2NTY2NzQzOTgyOCwKICAicHJvZmlsZUlkIiA6ICIxOTQzY2VmYzM4NWM0YTJjYWJiZGViODBjZTIwM2RjZSIsCiAgInByb2ZpbGVOYW1lIiA6ICJzb29vb29vb29vb29vb29wIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2I1M2NiYTFjZTVkMjhmZmNiZDAzMmQ2ZGNhNDY2NmIwNWI1M2ZlODdmMTgyZTg5NjQ0MzI1NjdhMWJiOTZkOGMiLAogICAgICAibWV0YWRhdGEiIDogewogICAgICAgICJtb2RlbCIgOiAic2xpbSIKICAgICAgfQogICAgfQogIH0KfQ==",
			"NHfPexlkLX9IfS0BwnHSfj6QvCLze9by6O5LVHYWObuvXeJiDUj7CbJh68eTDfoXoGD41pLosoxzb1FqeTDnULmwfFKvG3sQrxh5BkTXaT07FqmfeFfd2/tExo9qTBWzlrl8HjujywbLYGch7JNNEnz4wGceickj8uvNszH+1aYGhtwmvL4lItbpepmGneCk63FAsLXDuoROSYl2/iKShYLEo7ebRaO4veqz9dxLBP37NHLxovWJyBLeBnQNAaW0NTdc6Pa0MU238VG18xQm2WIN+1hTL0+LY3PrRYRzZeYMoBSKjG4yU/QxTYPIo7YX4+PJIy6ixJFT0xD50ATtCANw2a5mhXonTG+zSi3MxvCyOLSQa73cuhd0zajgywwBV3roTB6hJMstG3lRCp6ECrc21eYDAP+o6sa+mEC5XQXPKj0TZeH/E0rOIVIenXf9LB2t5vY6Y3IfSwS/VXUuw9z3+IxFzprd/v1lX7gNrqPsh7EVAD7zdoiwq6Mh7kEVDgrab+RyFhP8coZwgEGgtAjd+Gd0grwdicPHDbavIVFwLHl8qLuJZtYOuCpipqPtc05/S3yoLjPeeufI5WJeSAACXyL+iQa9PrLj3Nwn8eKYSZOev0oqz3BinVZfVylBoGbY44E18wpoYMiGownBJlFDTk7uGC2Bhbkwu2B/Jb8=",
			Dimentio.class),
	DUMMY(ChatColor.GREEN + "Dummy", ChatColor.GREEN, 1500, false, EntityType.PLAYER,null, true, null, null, TestDummy.class),

	DUNGEON_MASTER(ChatColor.GRAY + "Dungeon Master", ChatColor.GRAY, 0, false, EntityType.PLAYER, null, false, null, null, DungeonMaster.class),

	DUMMY_ATTACKER(ChatColor.RED + "Aggressive Dummy", ChatColor.RED, 1500, false, EntityType.PLAYER,
			new AttackTypes[] {AttackTypes.BASIC_MELEE, AttackTypes.BASIC_RANGED}, true, null, null, DummyAttacker.class);


	private String display;
	private ChatColor color;
	private int health;
	private boolean isBoss;

	private EntityType type;

	private AttackTypes[] abilities;
	private boolean isVulnerable;

	private String skinTexture;

	private String skinSignature;

	private Class<? extends Character> character;



	private Characters(String display, ChatColor color, int health, boolean isBoss, EntityType type, AttackTypes[] abilities, boolean isVulnerable, String skinTexture, String skinSignature,
					   Class<? extends Character> character) {

		this.display = display;
		this.color = color;
		this.health = health;
		this.isBoss = isBoss;
		this.type = type;
		this.abilities = abilities;
		this.isVulnerable = isVulnerable;
		this.skinTexture = skinTexture;
		this.skinSignature = skinSignature;
		this.character = character;
	}

	public String getDisplay() {
		return display;
	}

	public ChatColor getColor(){return color;}

	public int getHealth() {
		return health;
	}

	public EntityType getType() {
		return type;
	}

	public AttackTypes[] getAbilities() {
		return abilities;
	}

	public boolean isVulnerable() {
		return isVulnerable;
	}

	public String getSkinTexture() {
		return skinTexture;
	}

	public String getSkinSignature() {
		return skinSignature;
	}

	public boolean isBoss() {
		return isBoss;
	}

	public Class<? extends Character> getCharacter() {
		return character;
	}
}
