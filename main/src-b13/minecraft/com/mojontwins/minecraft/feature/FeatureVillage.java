package com.mojontwins.minecraft.feature;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import net.minecraft.world.level.World;
import net.minecraft.world.level.biome.BiomeGenBase;
import net.minecraft.world.level.chunk.Chunk;

public abstract class FeatureVillage extends Feature {
	public Random randPieces;

	protected List<FeatureBuilding> buildingList;
	private List<Class<?>> availableBuildingsUnrolled;
	
	public FeatureVillage(World world, int originChunkX, int originChunkZ, FeatureProvider featureProvider) {
		super(world, originChunkX, originChunkZ, featureProvider);

		this.buildingList = new ArrayList<FeatureBuilding>();
		this.availableBuildingsUnrolled = new ArrayList<Class<?>>();
		
		// Unroll building list
		AvailableBuildingWithProbability[] availableBuildings = this.getAvailableBuildings();
		for(int i = 0; i < availableBuildings.length; i ++) {
			for(int j = 0; j < availableBuildings[i].probability; j ++) {
				this.availableBuildingsUnrolled.add(availableBuildings[i].buildingClass);
			}
		}

	}

	@Override
	public void setup(World world, Random rand, BiomeGenBase biome, int chunkX, int chunkZ) {
		long seed = this.world.getSeed() + originChunkX * 25117 + originChunkZ * 151121;
		this.randPieces = new Random(seed);
		
		// Fill list
		// This list will always be the same 'cause it depends on the ramdomSeed which
		// only depends on the world's seed and the feature center chunk coordinates.
		this.generateBuildingList();
	}
	
	@Override
	public void generate(int chunkX, int chunkZ, Chunk chunk) {
	}

	@Override
	public void populate(World world, Random rand, int chunkX, int chunkZ) {
		// Render list (at least this chunk!)
		Iterator<FeatureBuilding> iterator = this.buildingList.iterator();
		
		FeatureAABB chunkAABB = new FeatureAABB(chunkX << 4, 0, chunkZ << 4, (chunkX << 4) + 15, 127, (chunkZ << 4) + 15);	
		while(iterator.hasNext()) {
			FeatureBuilding building = iterator.next();
			if(chunkAABB.collidesWith(building.aabb)) {
				building.populate(chunkX, chunkZ);
			} 
		}
	}
	
	public Class<?> getRandomBuildingClass() {
		return this.availableBuildingsUnrolled.get(this.randPieces.nextInt(this.availableBuildingsUnrolled.size()));
	}
	
	public abstract AvailableBuildingWithProbability[] getAvailableBuildings();
	
	public abstract void generateBuildingList();	
}
