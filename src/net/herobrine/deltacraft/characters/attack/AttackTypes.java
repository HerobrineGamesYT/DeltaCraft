package net.herobrine.deltacraft.characters.attack;

import net.herobrine.deltacraft.game.CustomProjectiles;
import net.herobrine.deltacraft.items.ItemTypes;
import org.bukkit.Sound;

public enum AttackTypes {


	DIMENTIO_BEAM(300, true, false, CustomProjectiles.DIMENTIO_BEAM, 8, 1100, 0, Sound.BAT_IDLE, 1.25f, null, false),
	DIMENTIO_STAR(400, true, false, CustomProjectiles.DIMENTIO_SKULL, 10, 900, 0, Sound.WITHER_SHOOT, .85f, null, false),
	TIME_WARP(250, false, true, null, 35, 0, 0, Sound.WITHER_SPAWN,  .8f, null, false),
	DIMENTIO_CLONE(0, false, true, null, 0, 0, 0, Sound.WITHER_SPAWN, 1.5f, null, false),
	BASIC_MELEE(100, false, false, null, 0, 0, 0, null, 1f, ItemTypes.BERS_WEAPON, true),
	BASIC_RANGED(125, true, false, CustomProjectiles.ARROW, 15, 2000, 1500, Sound.SHOOT_ARROW, 1f, ItemTypes.ENEMY_BOW, true);



	private int baseDamage;
	private boolean isProjectileAttack;

	private boolean isAbility;
	private CustomProjectiles projectile;


	// 0 for melee attacks or abilities with no range
	private int range;

	// in ms
	private long attackSpeed;

	// in ms, only used for attacks that use projectiles.
	private long attackChargeSpeed;

	private Sound attackSound;
	private float attackSoundPitch;

	private ItemTypes weapon;

	private boolean hasWeapon;


	private AttackTypes(int baseDamage, boolean isProjectileAttack, boolean isAbility, CustomProjectiles projectile, int range, long attackSpeed, long attackChargeSpeed, Sound attackSound, float attackSoundPitch,
						ItemTypes weapon, boolean hasWeapon) {
		this.baseDamage = baseDamage;
		this.isProjectileAttack = isProjectileAttack;
		this.isAbility = isAbility;
		this.projectile = projectile;
		this.range = range;
		this.attackSpeed = attackSpeed;
		this.attackChargeSpeed = attackChargeSpeed;
		this.attackSound = attackSound;
		this.attackSoundPitch = attackSoundPitch;
		this.weapon = weapon;
		this.hasWeapon = hasWeapon;
	}


	public int getBaseDamage() {return baseDamage;}

	public boolean isProjectileAttack() {return isProjectileAttack;}

	public boolean isAbility() {return isAbility;}

	public CustomProjectiles getProjectile() {return projectile;}

	public int getRange() {return range;}

	public long getAttackSpeed() {return attackSpeed;}

	public Sound getAttackSound() {return attackSound;}

	public float getAttackSoundPitch() {return attackSoundPitch;}

	public ItemTypes getWeapon() {return weapon;}

	public boolean hasWeapon() {return hasWeapon;}

	public long getAttackChargeSpeed() {return attackChargeSpeed;}
}
