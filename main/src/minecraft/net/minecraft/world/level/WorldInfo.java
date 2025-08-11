package net.minecraft.world.level;

import java.util.List;

import com.mojang.nbt.NBTTagCompound;

import net.minecraft.world.GameRules;
import net.minecraft.world.entity.player.EntityPlayer;

public class WorldInfo {
	private long randomSeed;
	private WorldType terrainType = WorldType.DEFAULT;
	private int spawnX;
	private int spawnY;
	private int spawnZ;
	private long worldTime;
	private long lastTimePlayed;
	private long sizeOnDisk;
	private NBTTagCompound playerTag;
	private int dimension;
	private String levelName;
	private int saveVersion;
	
	private boolean raining;
	private int rainTime;
	private boolean thundering;
	private int thunderTime;
	private boolean snowing;
	private int snowingTime;
	
	private boolean sandstorming;
	private int sandstormingTime;
	
	private int gameType;
	private boolean mapFeaturesEnabled;
	private boolean hardcore = false;
	private boolean enableCheats = false;
	private boolean snowCovered = false;
	private boolean enableSeasons = false;

	public WorldInfo(NBTTagCompound nbt) {
		this.randomSeed = nbt.getLong("RandomSeed");
		this.snowCovered = nbt.getBoolean("SnowCovered");
		
		if(nbt.hasKey("generatorName")) {
			String string2 = nbt.getString("generatorName");

			this.terrainType = WorldType.parseWorldType(string2);
			
			if(this.terrainType == null) {
				this.terrainType = WorldType.DEFAULT;
			} else if(this.terrainType.func_48626_e()) {
				int i3 = 0;
				if(nbt.hasKey("generatorVersion")) {
					i3 = nbt.getInteger("generatorVersion");
				}

				this.terrainType = this.terrainType.func_48629_a(i3);
			}
		} 

		this.gameType = nbt.getInteger("GameType");
		
		if(nbt.hasKey("MapFeatures")) {
			this.mapFeaturesEnabled = nbt.getBoolean("MapFeatures");
		} else {
			this.mapFeaturesEnabled = true;
		}
		
		this.enableCheats = nbt.getBoolean("EnableCheats");
		
		this.spawnX = nbt.getInteger("SpawnX");
		this.spawnY = nbt.getInteger("SpawnY");
		this.spawnZ = nbt.getInteger("SpawnZ");
		this.worldTime = nbt.getLong("Time");
		this.lastTimePlayed = nbt.getLong("LastPlayed");
		this.sizeOnDisk = nbt.getLong("SizeOnDisk");
		this.levelName = nbt.getString("LevelName");
		this.saveVersion = nbt.getInteger("version");
		this.rainTime = nbt.getInteger("rainTime");
		this.raining = nbt.getBoolean("raining");
		this.thunderTime = nbt.getInteger("thunderTime");
		this.thundering = nbt.getBoolean("thundering");
		this.snowingTime = nbt.getInteger("snowingTime");
		this.snowing = nbt.getBoolean("snowing");
		this.thunderTime = nbt.getInteger("thunderTime");
		this.thundering = nbt.getBoolean("thundering");
		this.sandstorming = nbt.getBoolean("sandstorming");
		this.sandstormingTime = nbt.getInteger("sandstormingTime");
		
		if(nbt.hasKey("DayOfTheYear")) {
			Seasons.dayOfTheYear = nbt.getInteger("DayOfTheYear");
		} else if(this.snowCovered) {
			Seasons.dayOfTheYear = 0;
		} else {
			Seasons.dayOfTheYear = Seasons.SEASON_DURATION * 2;
		}
		
		this.hardcore = nbt.getBoolean("hardcore");
		
		this.enableSeasons = nbt.getBoolean("EnableSeasons");
		
		GameRules.loadRules(nbt);
		GameRules.refreshAllRules();
		
		if(nbt.hasKey("Player")) {
			this.playerTag = nbt.getCompoundTag("Player");
			this.dimension = this.playerTag.getInteger("Dimension");
		}


	}

	public String toString() {
		return  "Level Name:" + this.levelName +", RandomSeed: " + this.randomSeed + ", Terrain: " + this.terrainType + ", sizeOnDisk: " + this.sizeOnDisk + ", player: " + this.playerTag;
	}
	
	public WorldInfo(WorldSettings worldSettings, String levelName) {
		this.randomSeed = worldSettings.getSeed();
		this.gameType = worldSettings.getGameType();
		this.mapFeaturesEnabled = worldSettings.isMapFeaturesEnabled();
		this.levelName = levelName;
		this.hardcore = worldSettings.getHardcoreEnabled();
		this.enableCheats = worldSettings.isEnableCheats();
		this.terrainType = worldSettings.getTerrainType();
		this.snowCovered = worldSettings.isSnowCovered();
		this.enableSeasons = worldSettings.isEnableSeasons();
		GameRules.refreshAllRules();
	}

	public WorldInfo(WorldInfo worldInfo) {
		this.randomSeed = worldInfo.randomSeed;
		this.terrainType = worldInfo.terrainType;
		this.gameType = worldInfo.gameType;
		this.mapFeaturesEnabled = worldInfo.mapFeaturesEnabled;
		this.enableCheats = worldInfo.enableCheats;
		this.spawnX = worldInfo.spawnX;
		this.spawnY = worldInfo.spawnY;
		this.spawnZ = worldInfo.spawnZ;
		this.worldTime = worldInfo.worldTime;
		this.lastTimePlayed = worldInfo.lastTimePlayed;
		this.sizeOnDisk = worldInfo.sizeOnDisk;
		this.playerTag = worldInfo.playerTag;
		this.dimension = worldInfo.dimension;
		this.levelName = worldInfo.levelName;
		this.saveVersion = worldInfo.saveVersion;
		this.rainTime = worldInfo.rainTime;
		this.raining = worldInfo.raining;
		this.thunderTime = worldInfo.thunderTime;
		this.thundering = worldInfo.thundering;
		this.snowingTime = worldInfo.snowingTime;;
		this.snowing = worldInfo.snowing;
		this.sandstormingTime = worldInfo.sandstormingTime;
		this.sandstorming = worldInfo.sandstorming;
		this.hardcore = worldInfo.hardcore;
		this.snowCovered = worldInfo.snowCovered;
		this.enableSeasons = worldInfo.enableSeasons;
		GameRules.refreshAllRules();
	}

	public NBTTagCompound getNBTTagCompound() {
		NBTTagCompound nbt = new NBTTagCompound();
		this.updateTagCompound(nbt, this.playerTag);
		return nbt;
	}

	public NBTTagCompound getNBTTagCompoundWithPlayers(List<EntityPlayer> list1) {
		NBTTagCompound nBTTagCompound2 = new NBTTagCompound();
		EntityPlayer entityPlayer3 = null;
		NBTTagCompound nBTTagCompound4 = null;
		if(list1.size() > 0) {
			entityPlayer3 = (EntityPlayer)list1.get(0);
		}

		if(entityPlayer3 != null) {
			nBTTagCompound4 = new NBTTagCompound();
			entityPlayer3.writeToNBT(nBTTagCompound4);
		}

		this.updateTagCompound(nBTTagCompound2, nBTTagCompound4);
		return nBTTagCompound2;
	}

	private void updateTagCompound(NBTTagCompound nbt, NBTTagCompound nbtPlayer) {
		nbt.setLong("RandomSeed", this.randomSeed);
		nbt.setString("generatorName", this.terrainType.getWorldTypeName());
		nbt.setInteger("generatorVersion", this.terrainType.getGeneratorVersion());
		nbt.setInteger("GameType", this.gameType);
		nbt.setBoolean("MapFeatures", this.mapFeaturesEnabled);
		nbt.setBoolean("EnableCheats", this.enableCheats);
		nbt.setInteger("SpawnX", this.spawnX);
		nbt.setInteger("SpawnY", this.spawnY);
		nbt.setInteger("SpawnZ", this.spawnZ);
		nbt.setLong("Time", this.worldTime);
		nbt.setLong("SizeOnDisk", this.sizeOnDisk);
		nbt.setLong("LastPlayed", System.currentTimeMillis());
		nbt.setString("LevelName", this.levelName);
		nbt.setInteger("version", this.saveVersion);
		nbt.setInteger("rainTime", this.rainTime);
		nbt.setBoolean("raining", this.raining);
		nbt.setInteger("thunderTime", this.thunderTime);
		nbt.setBoolean("thundering", this.thundering);
		nbt.setInteger("snowingTime", this.snowingTime);
		nbt.setBoolean("snowing", this.snowing);
		nbt.setInteger("sandstromingTime", this.sandstormingTime);
		nbt.setBoolean("sandstorming", this.sandstorming);
		nbt.setInteger("DayOfTheYear", Seasons.dayOfTheYear);
		nbt.setBoolean("hardcore", this.hardcore);
		nbt.setBoolean("SnowCovered", this.snowCovered);
		nbt.setBoolean("EnableSeasons", this.enableSeasons);

		GameRules.saveRules(nbt);
		
		if(nbtPlayer != null) {
			nbt.setCompoundTag("Player", nbtPlayer);
		}

	}

	public boolean isSnowCovered() {
		return snowCovered;
	}

	public void setSnowCovered(boolean snowCovered) {
		this.snowCovered = snowCovered;
	}

	public long getSeed() {
		return this.randomSeed;
	}

	public int getSpawnX() {
		return this.spawnX;
	}

	public int getSpawnY() {
		return this.spawnY;
	}

	public int getSpawnZ() {
		return this.spawnZ;
	}

	public long getWorldTime() {
		return this.worldTime;
	}

	public long getSizeOnDisk() {
		return this.sizeOnDisk;
	}

	public NBTTagCompound getPlayerNBTTagCompound() {
		return this.playerTag;
	}

	public int getDimension() {
		return this.dimension;
	}

	public void setSpawnX(int i1) {
		this.spawnX = i1;
	}

	public void setSpawnY(int i1) {
		this.spawnY = i1;
	}

	public void setSpawnZ(int i1) {
		this.spawnZ = i1;
	}

	public void setWorldTime(long j1) {
		this.worldTime = j1;
	}

	public void setPlayerNBTTagCompound(NBTTagCompound nbtPlayer) {
		this.playerTag = nbtPlayer;
	}

	public void setSpawnPosition(int x, int y, int z) {
		this.spawnX = x;
		this.spawnY = y;
		this.spawnZ = z;
	}

	public String getWorldName() {
		return this.levelName;
	}

	public void setWorldName(String string1) {
		this.levelName = string1;
	}

	public int getSaveVersion() {
		return this.saveVersion;
	}

	public void setSaveVersion(int i1) {
		this.saveVersion = i1;
	}

	public long getLastTimePlayed() {
		return this.lastTimePlayed;
	}

	public boolean isThundering() {
		return this.thundering;
	}

	public void setThundering(boolean z1) {
		this.thundering = z1;
	}

	public int getThunderTime() {
		return this.thunderTime;
	}

	public void setThunderTime(int i1) {
		this.thunderTime = i1;
	}

	public boolean isRaining() {
		return this.raining;
	}

	public void setRaining(boolean z1) {
		this.raining = z1;
	}

	public int getRainTime() {
		return this.rainTime;
	}

	public void setRainTime(int i1) {
		this.rainTime = i1;
	}
	
	public boolean isSandstorming() {
		return this.sandstorming;
	}
	
	public void setSandstorming(boolean sandstorming) {
		this.sandstorming = sandstorming; 
	}
	
	public int getSandstormingTime() {
		return this.sandstormingTime;
	}
	
	public void setSandstormingTime(int sandstormingTime) {
		this.sandstormingTime = sandstormingTime;
	}

	public int getGameType() {
		return this.gameType;
	}

	public void setGameType(int gameType) {
		this.gameType = gameType;
	}

	public boolean isMapFeaturesEnabled() {
		return this.mapFeaturesEnabled;
	}
	
	public boolean isEnableCheats() {
		return this.enableCheats;
	}

	public boolean isHardcoreModeEnabled() {
		return this.hardcore;
	}

	public WorldType getTerrainType() {
		return this.terrainType;
	}

	public void setTerrainType(WorldType worldType1) {
		this.terrainType = worldType1;
	}

	public boolean isSnowing() {
		return snowing;
	}

	public void setSnowing(boolean snowing) {
		this.snowing = snowing;
	}

	public int getSnowingTime() {
		return snowingTime;
	}

	public void setSnowingTime(int snowingTime) {
		this.snowingTime = snowingTime;
	}

	public boolean isEnableSeasons() {
		return enableSeasons;
	}

	public void setEnableSeasons(boolean enableSeasons) {
		this.enableSeasons = enableSeasons;
	}
}
