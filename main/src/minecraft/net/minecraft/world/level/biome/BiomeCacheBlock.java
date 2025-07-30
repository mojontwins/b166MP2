package net.minecraft.world.level.biome;

public class BiomeCacheBlock {
	public float[] temperatureValues;
	public float[] rainfallValues;
	public BiomeGenBase[] biomes;
	public int xPosition;
	public int zPosition;
	public long lastAccessTime;
	final BiomeCache biomeCache;

	public BiomeCacheBlock(BiomeCache cache, int chunkX, int chunkZ) {
		this.biomeCache = cache;
		this.temperatureValues = new float[256];
		this.rainfallValues = new float[256];
		this.biomes = new BiomeGenBase[256];
		this.xPosition = chunkX;
		this.zPosition = chunkZ;
		BiomeCache.getChunkManager(cache).getTemperatures(this.temperatureValues, chunkX << 4, chunkZ << 4, 16, 16);
		BiomeCache.getChunkManager(cache).getRainfall(this.rainfallValues, chunkX << 4, chunkZ << 4, 16, 16);
		BiomeCache.getChunkManager(cache).getBiomeGenAt(this.biomes, chunkX << 4, chunkZ << 4, 16, 16, false);
	}

	public BiomeGenBase getBiomeGenAt(int x, int z) {
		return this.biomes[x & 15 | (z & 15) << 4];
	}

	public float getTemperatureAt(int x, int z) {
		return this.temperatureValues[x & 15 | (z & 15) << 4];
	}
	
	public float getRainfallAt(int x, int z) {
		return this.rainfallValues[x & 15 | (z & 15) << 4];
	}
}
