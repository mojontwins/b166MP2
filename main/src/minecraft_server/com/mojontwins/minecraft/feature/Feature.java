package com.mojontwins.minecraft.feature;

import java.util.Random;

import net.minecraft.world.level.World;
import net.minecraft.world.level.biome.BiomeGenBase;
import net.minecraft.world.level.chunk.Chunk;
import net.minecraft.world.level.chunk.IChunkProvider;

public abstract class Feature {
	/*
	 * A random number between 0 and this will be generated and compared with each feature's `getSpawnChance`
	 * If it's less than getSpawnChance the feature can spawn (if `shouldSpawn` returns true)
	 */
	public static final int MAXCHANCE = 4096; // 1024;
	
	public World world;
	public int originChunkX, originChunkZ;
	public int centerX, centerZ;
	public FeatureAABB featureAABB;

	/*
	 * Use a copy of the provider so features can reach out to get important stuff from elsewhere
	 */
	public FeatureProvider featureProvider;
	
	/*
	 * Base constructor stores the origin chunk coordinates and calculate the feature center in blocks
	 */
	public Feature(World world, int originChunkX, int originChunkZ, FeatureProvider featureProvider) {	
		this.world = world;
		this.originChunkX = originChunkX;
		this.originChunkZ = originChunkZ;
		this.centerX = this.originChunkX * 16 + 8;
		this.centerZ = this.originChunkZ * 16 + 8;
	
		this.featureAABB = new FeatureAABB(
				(originChunkX - this.getFeatureRadius()) << 4, 
				0,
				(originChunkZ - this.getFeatureRadius()) << 4, 
				15 + ((originChunkX + this.getFeatureRadius()) << 4), 
				127,
				15 + ((originChunkZ + this.getFeatureRadius()) << 4) + 15
		);
		
		this.featureProvider = featureProvider;
	}

	/*
	 * Return this feature radius 1-3
	 */
	public abstract int getFeatureRadius();
	
	/*
	 * Return spawn chance (1-1024).
	 */
	public abstract int getSpawnChance();
	
	/*
	 * Once this feature is created and validated, this method is called to set some stuff up
	 */
	public void setup(World world, Random rand, BiomeGenBase biome, int chunkX, int chunkZ) {
	}
	
	/*
	 * Called by the feature provider. Usually you don't want to override this method.
	 */
	public boolean shouldFeatureSpawn(IChunkProvider chunkProvider, World world, Random rand, BiomeGenBase biome, int chunkX, int chunkZ) {
		return rand.nextInt(MAXCHANCE) < this.getSpawnChance() && this.shouldSpawn(chunkProvider, world, rand, biome, chunkX, chunkZ);
	}
	
	/*
	 * Return true if this feature can spawn with these chunk coordinates as a center.
	 */
	public abstract boolean shouldSpawn(IChunkProvider chunkProvider, World world, Random rand, BiomeGenBase biome, int chunkX, int chunkZ);
	
	/*
	 * Minimum amount of chunks this feature should be apart from other features
	 */
	public int minimumSeparation() {
		return this.getFeatureRadius();
	}
	
	/*
	 * Called during the generation stage (in `provideChunk`). You usually want to read/write to `chunk.blocks` directly
	 */
	public abstract void generate(int chunkX, int chunkZ, Chunk chunk);
	
	/*
	 * Called during the population stage (in `populate`).
	 */
	public abstract void populate(World world, Random rand, int chunkX, int chunkZ);
}
