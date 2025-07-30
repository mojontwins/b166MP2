package net.minecraft.world.level.biome;

public class BiomeCacheBlock {
	public float[] temperatureValues;
	public float[] rainfallValues;
	public BiomeGenBase[] biomes;
	public int xPosition;
	public int zPosition;
	public long lastAccessTime;
	final BiomeCache biomeCache;

	public BiomeCacheBlock(BiomeCache biomeCache1, int i2, int i3) {
		this.biomeCache = biomeCache1;
		this.temperatureValues = new float[256];
		this.rainfallValues = new float[256];
		this.biomes = new BiomeGenBase[256];
		this.xPosition = i2;
		this.zPosition = i3;
		BiomeCache.getChunkManager(biomeCache1).getTemperatures(this.temperatureValues, i2 << 4, i3 << 4, 16, 16);
		BiomeCache.getChunkManager(biomeCache1).getRainfall(this.rainfallValues, i2 << 4, i3 << 4, 16, 16);
		BiomeCache.getChunkManager(biomeCache1).getBiomeGenAt(this.biomes, i2 << 4, i3 << 4, 16, 16, false);
	}

	public BiomeGenBase getBiomeGenAt(int i1, int i2) {
		return this.biomes[i1 & 15 | (i2 & 15) << 4];
	}
}
