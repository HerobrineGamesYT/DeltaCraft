package net.herobrine.deltacraft.game;

import net.herobrine.core.SkullMaker;
import net.herobrine.gamecore.Arena;
import net.minecraft.server.v1_8_R3.EnumParticle;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldParticles;
import org.bukkit.Location;
import org.bukkit.entity.*;

import java.util.Arrays;

public enum CustomProjectiles {

	FIREBALL(false, false, null, 0, 0, 0,0 ,null, Fireball.class),

	ARROW(false, false, null, 0,0,0,0, null, Arrow.class),
	DIMENTIO_BEAM(false, true, EnumParticle.PORTAL, 50, 0,0,0, null, null),
	DIMENTIO_SKULL(true, false, null, 0, 0,0,0, "http://textures.minecraft.net/texture/45f5cab199508725ea2bbe3ed2d84b891c5d8b7ee7eaedc69e67196dc6fee3d9", null);


	private boolean isSkull;
	private boolean isParticle;
	private EnumParticle particle;

	// used for particles
	private int amount;

	// RGB values will only be filled in for particles that support it.
	private int red;

	private int green;

	private int blue;

	// texture url will only be provided for skulls
	private String url;

	// only provided for projectile types that are arrows/fireballs. you know, bukkit supported projectiles.
	private Class<? extends Projectile> projectile;




	private CustomProjectiles(boolean isSkull, boolean isParticle, EnumParticle particle, int amount, int red, int green, int blue, String url, Class<? extends Projectile> projectile) {
		this.isSkull = isSkull;
		this.isParticle = isParticle;
		this.particle = particle;
		this.amount = amount;
		this.red = red;
		this.green = green;
		this.blue = blue;
		this.url = url;
		this.projectile = projectile;
	}




	public PacketPlayOutWorldParticles createParticle(Location location, CustomProjectiles projectile) {

		if (projectile.getParticle().equals(EnumParticle.REDSTONE) || projectile.getParticle().equals(EnumParticle.BLOCK_DUST)) {
			return new PacketPlayOutWorldParticles(projectile.getParticle(), true, (float) location.getX(), (float) location.getY(),
					(float) location.getZ(), (float)projectile.red()/255, (float)projectile.green()/255, (float)projectile.blue()/255,
					(float) projectile.getAmount(), 0);
		}

		return new PacketPlayOutWorldParticles(projectile.getParticle(), true,
				(float) location.getX(), (float) location.getY(), (float) location.getZ(), 0, 0, 0, 0, projectile.getAmount(), null);
	}

	public ArmorStand createSkull(CustomProjectiles projectile, Location loc) {
		SkullMaker skull = new SkullMaker("", Arrays.asList(""), projectile.getURL());

		ArmorStand stand = (ArmorStand) loc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);

		stand.setVisible(false);
		stand.setSmall(true);
		stand.setMarker(true);
		stand.setHelmet(skull.getSkull());

		return stand;
	}


	public boolean isSkull() {return isSkull;}

	public boolean isParticle() {return isParticle;}

	public EnumParticle getParticle() {return particle;}

	public int getAmount() {return amount;}

	public int red() {return red;}

	public int green() {return green;}

	public int blue() {return blue;}

	public String getURL(){return url;}

	public Class<? extends Projectile> getProjectile() {return projectile;}
}
