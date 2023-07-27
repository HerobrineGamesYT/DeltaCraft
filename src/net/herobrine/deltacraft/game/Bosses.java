package net.herobrine.deltacraft.game;

import net.herobrine.deltacraft.characters.attack.AttackTypes;

public enum Bosses {

	LANCER("Lancer", false, true, 10, null, false),
	CHAOS_KING("Chaos King", false, true, 10, null, false),
	QUEEN("Queen", false, false, 10, null, false),
	MIKE("Mike", false, false, 0, null, false),
	SPAMTON("Spamton", false, false, 0, null, false),
	SPAMTON_NEO("Spamton NEO", false, false, 0, null, false),
	DIMENTIO("Dimentio", false, true, 10, new AttackTypes[]{AttackTypes.TIME_WARP, AttackTypes.DIMENTIO_BEAM}, true);

	private String name;
	private boolean canSpare;
	private boolean loopingAttacks;
	private int maxTurns;
	private AttackTypes[] attacks;
	private boolean isOpenPlayBoss;

	private Bosses(String name, boolean canSpare, boolean loopingAttacks, int maxTurns, AttackTypes[] attacks, boolean isOpenPlayBoss) {
		this.name = name;
		this.canSpare = canSpare;
		this.loopingAttacks = loopingAttacks;
		this.maxTurns = maxTurns;
		this.attacks = attacks;
		this.isOpenPlayBoss = isOpenPlayBoss;

	}

	public String getName() {return name;}
	public boolean canSpare() {return canSpare;}
	public boolean hasLoopingAttacks() {return loopingAttacks;}
	public int getMaxTurns() {return maxTurns;}
	public AttackTypes[] getAttacks() {return attacks;}
	public boolean getOpenPlay() {return isOpenPlayBoss;}
}


