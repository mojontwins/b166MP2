package net.minecraft.world.level;

public final class WorldSettings {
	private final long seed;
	private final int gameType;
	private final boolean mapFeaturesEnabled;
	private final boolean hardcoreEnabled;
	private final boolean enableCheats;
	private final boolean snowCovered;
	private final boolean enableSeasons;
	private final WorldType terrainType;

	public WorldSettings(long seed, int gameMode, boolean features, boolean hardcore, boolean cheats, boolean enableSeasons, WorldType worldType) {
		this.seed = seed;
		this.gameType = gameMode;
		this.mapFeaturesEnabled = features;
		this.hardcoreEnabled = hardcore;
		this.terrainType = worldType;
		this.enableCheats = cheats;
		this.snowCovered = false;
		this.enableSeasons = enableSeasons;
	}
	
	// Alpha new world uses this constructor:
	public WorldSettings(long seed, int gameMode, boolean cheats, boolean snowCovered, boolean enableSeasons, WorldType worldType) {
		this.seed = seed;
		this.gameType = gameMode;
		this.mapFeaturesEnabled = true;
		this.hardcoreEnabled = false;
		this.enableCheats = cheats;
		this.terrainType = worldType;
		this.snowCovered = snowCovered;
		this.enableSeasons = enableSeasons;
	}
	
	public long getSeed() {
		return this.seed;
	}

	public int getGameType() {
		return this.gameType;
	}

	public boolean getHardcoreEnabled() {
		return this.hardcoreEnabled;
	}

	public boolean isMapFeaturesEnabled() {
		return this.mapFeaturesEnabled;
	}

	public WorldType getTerrainType() {
		return this.terrainType;
	}

	public static int validGameType(int i0) {
		switch(i0) {
		case 0:
		case 1:
			return i0;
		default:
			return 0;
		}
	}

	public boolean isEnableCheats() {
		return enableCheats;
	}

	public boolean isSnowCovered() {
		return snowCovered;
	}

	public boolean isEnableSeasons() {
		return enableSeasons;
	}
}
