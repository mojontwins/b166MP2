package net.minecraft.src;

import java.util.List;

public class WorldInfo {
	private long randomSeed;
	private int spawnX;
	private int spawnY;
	private int spawnZ;
	private long worldTime;
	private long lastTimePlayed;
	private long sizeOnDisk;
	private NBTTagCompound field_22195_h;
	private int field_22194_i;
	private String levelName;
	private int saveVersion;
	private boolean isRaining;
	private int rainTime;
	private boolean isThundering;
	private int thunderTime;

	public WorldInfo(NBTTagCompound nBTTagCompound1) {
		this.randomSeed = nBTTagCompound1.getLong("RandomSeed");
		this.spawnX = nBTTagCompound1.getInteger("SpawnX");
		this.spawnY = nBTTagCompound1.getInteger("SpawnY");
		this.spawnZ = nBTTagCompound1.getInteger("SpawnZ");
		this.worldTime = nBTTagCompound1.getLong("Time");
		this.lastTimePlayed = nBTTagCompound1.getLong("LastPlayed");
		this.sizeOnDisk = nBTTagCompound1.getLong("SizeOnDisk");
		this.levelName = nBTTagCompound1.getString("LevelName");
		this.saveVersion = nBTTagCompound1.getInteger("version");
		this.rainTime = nBTTagCompound1.getInteger("rainTime");
		this.isRaining = nBTTagCompound1.getBoolean("raining");
		this.thunderTime = nBTTagCompound1.getInteger("thunderTime");
		this.isThundering = nBTTagCompound1.getBoolean("thundering");
		if(nBTTagCompound1.hasKey("Player")) {
			this.field_22195_h = nBTTagCompound1.getCompoundTag("Player");
			this.field_22194_i = this.field_22195_h.getInteger("Dimension");
		}

	}

	public WorldInfo(long j1, String string3) {
		this.randomSeed = j1;
		this.levelName = string3;
	}

	public WorldInfo(WorldInfo worldInfo1) {
		this.randomSeed = worldInfo1.randomSeed;
		this.spawnX = worldInfo1.spawnX;
		this.spawnY = worldInfo1.spawnY;
		this.spawnZ = worldInfo1.spawnZ;
		this.worldTime = worldInfo1.worldTime;
		this.lastTimePlayed = worldInfo1.lastTimePlayed;
		this.sizeOnDisk = worldInfo1.sizeOnDisk;
		this.field_22195_h = worldInfo1.field_22195_h;
		this.field_22194_i = worldInfo1.field_22194_i;
		this.levelName = worldInfo1.levelName;
		this.saveVersion = worldInfo1.saveVersion;
		this.rainTime = worldInfo1.rainTime;
		this.isRaining = worldInfo1.isRaining;
		this.thunderTime = worldInfo1.thunderTime;
		this.isThundering = worldInfo1.isThundering;
	}

	public NBTTagCompound func_22185_a() {
		NBTTagCompound nBTTagCompound1 = new NBTTagCompound();
		this.saveNBTTag(nBTTagCompound1, this.field_22195_h);
		return nBTTagCompound1;
	}

	public NBTTagCompound func_22183_a(List list1) {
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

		this.saveNBTTag(nBTTagCompound2, nBTTagCompound4);
		return nBTTagCompound2;
	}

	private void saveNBTTag(NBTTagCompound nBTTagCompound1, NBTTagCompound nBTTagCompound2) {
		nBTTagCompound1.setLong("RandomSeed", this.randomSeed);
		nBTTagCompound1.setInteger("SpawnX", this.spawnX);
		nBTTagCompound1.setInteger("SpawnY", this.spawnY);
		nBTTagCompound1.setInteger("SpawnZ", this.spawnZ);
		nBTTagCompound1.setLong("Time", this.worldTime);
		nBTTagCompound1.setLong("SizeOnDisk", this.sizeOnDisk);
		nBTTagCompound1.setLong("LastPlayed", System.currentTimeMillis());
		nBTTagCompound1.setString("LevelName", this.levelName);
		nBTTagCompound1.setInteger("version", this.saveVersion);
		nBTTagCompound1.setInteger("rainTime", this.rainTime);
		nBTTagCompound1.setBoolean("raining", this.isRaining);
		nBTTagCompound1.setInteger("thunderTime", this.thunderTime);
		nBTTagCompound1.setBoolean("thundering", this.isThundering);
		if(nBTTagCompound2 != null) {
			nBTTagCompound1.setCompoundTag("Player", nBTTagCompound2);
		}

	}

	public long getRandomSeed() {
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

	public int getDimension() {
		return this.field_22194_i;
	}

	public void setWorldTime(long j1) {
		this.worldTime = j1;
	}

	public void setSizeOnDisk(long j1) {
		this.sizeOnDisk = j1;
	}

	public void setSpawnPosition(int i1, int i2, int i3) {
		this.spawnX = i1;
		this.spawnY = i2;
		this.spawnZ = i3;
	}

	public void setLevelName(String string1) {
		this.levelName = string1;
	}

	public int getVersion() {
		return this.saveVersion;
	}

	public void setVersion(int i1) {
		this.saveVersion = i1;
	}

	public boolean getIsThundering() {
		return this.isThundering;
	}

	public void setIsThundering(boolean z1) {
		this.isThundering = z1;
	}

	public int getThunderTime() {
		return this.thunderTime;
	}

	public void setThunderTime(int i1) {
		this.thunderTime = i1;
	}

	public boolean getIsRaining() {
		return this.isRaining;
	}

	public void setIsRaining(boolean z1) {
		this.isRaining = z1;
	}

	public int getRainTime() {
		return this.rainTime;
	}

	public void setRainTime(int i1) {
		this.rainTime = i1;
	}
}
