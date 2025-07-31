package net.minecraft.world.level;

import java.util.List;
import java.util.Random;

import net.minecraft.world.level.biome.BiomeCache;
import net.minecraft.world.level.biome.BiomeGenBase;
import net.minecraft.world.level.biome.RetroBiomeLookup;
import net.minecraft.world.level.levelgen.synth.NoiseGeneratorOctaves2Beta;

public class WorldChunkManagerBeta extends WorldChunkManager {
	
	private NoiseGeneratorOctaves2Beta noiseTemperature;
	private NoiseGeneratorOctaves2Beta noiseHumidity;
	private NoiseGeneratorOctaves2Beta noiseVariation;

	public double[] temperature;
	public double[] humidity;
	public double[] variation;
	public double[] bigamplitude;
	
	public BiomeGenBase[] generatedBiomes;

	public WorldChunkManagerBeta() {
		this.biomeCache = new BiomeCache(this);
	}

	public WorldChunkManagerBeta(World world) {
		this();
		
		this.noiseTemperature = new NoiseGeneratorOctaves2Beta(new Random(world.getSeed() * 9871L), 4);
		this.noiseHumidity = new NoiseGeneratorOctaves2Beta(new Random(world.getSeed() * 39811L), 4);
		this.noiseVariation = new NoiseGeneratorOctaves2Beta(new Random(world.getSeed() * 543321L), 2);
	}

	public BiomeGenBase[] getBiomesForGeneration(int i1, int i2, int i3, int i4) {
		this.generatedBiomes = this.loadBlockGeneratorData(this.generatedBiomes, i1, i2, i3, i4);
		return this.generatedBiomes;
	}
	
	
	public BiomeGenBase[] loadBlockGeneratorData(BiomeGenBase[] biomeGenArray, int xPos, int zPos, int width, int length) {
		if(biomeGenArray == null || biomeGenArray.length < width * length) {
			biomeGenArray = new BiomeGenBase[width * length];
		}

		this.temperature = this.noiseTemperature.generateNoiseOctaves(this.temperature, (double)xPos, (double)zPos, width, width, 0.02500000037252903D, 0.02500000037252903D, 0.25D);
		this.humidity = this.noiseHumidity.generateNoiseOctaves(this.humidity, (double)xPos, (double)zPos, width, width, (double)0.05F, (double)0.05F, 0.3333333333333333D);
		this.variation = this.noiseVariation.generateNoiseOctaves(this.variation, (double)xPos, (double)zPos, width, width, 0.25D, 0.25D, 0.5882352941176471D);
		int biomeIndex = 0;

		for(int x = 0; x < width; ++x) {
			for(int z = 0; z < length; ++z) {
				double d9 = this.variation[biomeIndex] * 1.1D + 0.5D;
				double d11 = 0.01D;
				double d13 = 1.0D - d11;
				double temperature = (this.temperature[biomeIndex] * 0.15D + 0.7D) * d13 + d9 * d11;
				d11 = 0.002D;
				d13 = 1.0D - d11;
				double humidity = (this.humidity[biomeIndex] * 0.15D + 0.5D) * d13 + d9 * d11;
				temperature = 1.0D - (1.0D - temperature) * (1.0D - temperature);
				if(temperature < 0.0D) {
					temperature = 0.0D;
				}

				if(humidity < 0.0D) {
					humidity = 0.0D;
				}

				if(temperature > 1.0D) {
					temperature = 1.0D;
				}

				if(humidity > 1.0D) {
					humidity = 1.0D;
				}

				this.temperature[biomeIndex] = temperature;
				this.humidity[biomeIndex] = humidity;
				biomeGenArray[biomeIndex++] = RetroBiomeLookup.getBiomeFromLookup(temperature, humidity);
			}
		}

		return biomeGenArray;
	}
	
	// Interface to work alongside release API
	
	public float[] getRainfall(float[] rainfallValues, int x0, int z0, int sizeX, int sizeZ) {
		IntCache.resetIntCache();
		
		if (rainfallValues == null || rainfallValues.length < sizeX * sizeZ) {
			rainfallValues = new float [sizeX * sizeZ];
		}
		
		this.loadBlockGeneratorData(this.generatedBiomes, x0, z0, sizeX, sizeZ);
		
		for (int z = 0; z < sizeZ; z ++) {
			for (int x = 0; x < sizeX; x ++) {
				rainfallValues [x | z << 4] = (float) this.humidity [z | x << 4];
			}
		}
		
		return rainfallValues;
	}
	
	public float[] getTemperatures(float[] temperatureValues, int x0, int z0, int sizeX, int sizeZ) {
		IntCache.resetIntCache();
		
		if (temperatureValues == null || temperatureValues.length < sizeX * sizeZ) {
			temperatureValues = new float [sizeX * sizeZ];
		}
		
		this.loadBlockGeneratorData(this.generatedBiomes, x0, z0, sizeX, sizeZ);
		
		for (int z = 0; z < sizeZ; z ++) {
			for (int x = 0; x < sizeX; x ++) {
				temperatureValues [x | z << 4] = (float) this.temperature [z | x << 4];
			}
		}
		
		return temperatureValues;
	}
	
	public BiomeGenBase[] getBiomeGenAt(BiomeGenBase[] biomeGenArray, int x0, int z0, int sizeX, int sizeZ, boolean exactChunk) {
		IntCache.resetIntCache();
		
		if(biomeGenArray == null || biomeGenArray.length < sizeX * sizeZ) {
			biomeGenArray = new BiomeGenBase[sizeX * sizeZ];
		}
		
		if(exactChunk && sizeX == 16 && sizeZ == 16 && (x0 & 15) == 0 && (z0 & 15) == 0) {
			BiomeGenBase[] biomeGenBase9 = this.biomeCache.getCachedBiomes(x0, z0);
			System.arraycopy(biomeGenBase9, 0, biomeGenArray, 0, sizeX * sizeZ);
			
		} else {
			this.generatedBiomes = this.loadBlockGeneratorData(this.generatedBiomes, x0, z0, sizeX, sizeZ);
			
			for (int z = 0; z < sizeZ; z ++) {
				for (int x = 0; x < sizeX; x ++) {
					biomeGenArray [x | z << 4] = this.generatedBiomes [z | x << 4];
				}
			}
		}
		
		return biomeGenArray;
	}
	
	// This method is used to look for a viable biome to spawn a village... but...
	public boolean areBiomesViable(int i1, int i2, int i3, List<BiomeGenBase> list4) {
		return true;
	}
	
	// This method is used to look for a cool spawn position and to place strongholds.
	// It looks for any biome in biomelist and returns its location.
	public ChunkPosition findBiomePosition(int x0, int z0, int radius, List<BiomeGenBase> biomeList, Random rand) {
		int x1 = x0 - radius >> 2;
		int z1 = z0 - radius >> 2;
		int x2 = x0 + radius >> 2;
		int z2 = z0 + radius >> 2;
		int width = x2 - x1 + 1;
		int length = z2 - z1 + 1;
		
		BiomeGenBase[] foundBiomes = null;
		foundBiomes = this.loadBlockGeneratorData(foundBiomes, x1, z1, width, length);
		
		ChunkPosition biomePos = null;
		int attempt = 0;

		for(int i = 0; i < foundBiomes.length; ++i) {
			int x = x1 + i % width << 2;
			int z = z1 + i / width << 2;
			BiomeGenBase foundBiome = foundBiomes[i];
			if(biomeList.contains(foundBiome) && (biomePos == null || rand.nextInt(attempt + 1) == 0)) {
				biomePos = new ChunkPosition(x, 0, z);
				++attempt;
			}
		}

		return biomePos;
	}
}
