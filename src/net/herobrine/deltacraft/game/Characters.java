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

	DUNGEON_MASTER(ChatColor.GRAY + "Dungeon Master", ChatColor.GRAY, 0, false, EntityType.PLAYER, null, false, "ewogICJ0aW1lc3RhbXAiIDogMTY4MzgxODAxODk3NiwKICAicHJvZmlsZUlkIiA6ICJhMjk1ODZmYmU1ZDk0Nzk2OWZjOGQ4ZGE0NzlhNDNlZSIsCiAgInByb2ZpbGVOYW1lIiA6ICJMZXZlMjQiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTgzZmFiNTQzNDc2ZmQxNjI0NTY4NTk0ODA0YWY1Y2NiZTdkYWIxZGE2NDg0NmQ0Yzc4ODA2ZmY3OWEyOTYxNSIsCiAgICAgICJtZXRhZGF0YSIgOiB7CiAgICAgICAgIm1vZGVsIiA6ICJzbGltIgogICAgICB9CiAgICB9CiAgfQp9",
			"cXZZv9yFCXOqY8FkxnbXsCubrsk/Qu7oY9kWYIWjzdcPlMyqpsulM/YpmeG+MQ62W85DFCeV9YKLrvg3j3pOMJNePGCADZY/JXKTYYg/U1Fp7lkhkqLZuxqo1Irpl700VqOsNf+iQUouU3UuOiSL3OB4/p3XFUbcrw6PRZfIFfwIl2r78CjgE5c93E5ooM7xn1APxS1ZTxdab1zWFuQ5eYQDh0EghC9/nm12Ye2rKU+rWRtcVgIyd8WAF48qzqA6MWmy3krBVTkNRdkBNgSajAd40cgx9SwoW2305OJaG3+PQBWyTXCmsFVVi26mLJZJjS4DYNDHQwxV29Y1O/Oie9csavcA3ztGIr4Dilv6ih7N3zrvckKT6IzgXHgyCULthFnulx8oYxCHxvMHB2Neetk+0UTRmZr8nLqLOrq+N1AcsghB+etrQv5tmOpV52LoytQXjSKCnJwZI8FEK/kOyWvu/zMiJg3SdqxMIPSZ5nLt4z1Q2kDbHDTRZe7uvkQrFSM7ZpshgGBp9BIhCMglNF88J7iisTd/1+MNIXtQ6xIJjnkaLXfvljN9exO2V1pZU5SoUYz+Ix+yw1QMeWIbJLN2HW7Xq/RbA82eiEU8fa+osxWl4P9nxdo5TPWyuLWoOKZME2XvAahmgDOsAYu6HM8Zd4NfW+W7M25ixqIlX3I=", DungeonMaster.class),

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
