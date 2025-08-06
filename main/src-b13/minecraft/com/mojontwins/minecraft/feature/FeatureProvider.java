package com.mojontwins.minecraft.feature;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import net.minecraft.world.level.World;
import net.minecraft.world.level.biome.BiomeGenBase;
import net.minecraft.world.level.chunk.Chunk;
import net.minecraft.world.level.chunk.ChunkCoordIntPair;
import net.minecraft.world.level.chunk.IChunkProvider;

public class FeatureProvider {
	public World world;
	public IChunkProvider chunkProvider; 
	
	private static final List<Class<?>> registeredFeatures;
	
	/*
	 * This list includes a list of features generated during this session 
	 */
	private HashMap<Long, Feature> featureList;
	
	/*
	 * Just a small safeguard in case one of your features is not very respectful
	 */
	private HashSet<Long> generatedChunks;
	
	public FeatureProvider(World world, IChunkProvider chunkProvider) {
		this.world = world;
		this.chunkProvider = chunkProvider;
		this.featureList = new HashMap<Long, Feature> ();
		this.generatedChunks = new HashSet<Long> ();
	}
	
	public static void registerFeature(Class<?> featureClass) {
		registeredFeatures.add(featureClass);
	}

	/*
	 * Selects a feature at random for chunkX, chunkZ. If succeeded, feature is initialized and returned.
	 */
	public Feature getFeatureForChunkCoords(int chunkX, int chunkZ) { 
		// Provide a feature.
		Feature feature = null;
		
		// Seed must be consistent
		long seed = this.world.getSeed() + chunkX * 25117 + chunkZ * 151121;
		
		// Get the biome
		BiomeGenBase biome = this.world.getWorldChunkManager().getBiomeGenAt((chunkX << 4) + 8, (chunkZ << 4) + 8);
		
		// Select a feature. First feature from the list which can be spawned is selected!
		Iterator<Class<?>> featureClassIterator = registeredFeatures.iterator();
		while(featureClassIterator.hasNext()) {					
			try {
				Class<?> featureClass = featureClassIterator.next();
				if(featureClass != null) {
					feature = (Feature)featureClass
							.getConstructor(new Class[] {
								World.class,
								Integer.TYPE,
								Integer.TYPE,
								FeatureProvider.class})
							.newInstance(new Object[] {world, chunkX, chunkZ, this});
					}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			// Check if this feature can spawn with this chunk as its center
			if(feature.shouldFeatureSpawn(this.chunkProvider, this.world, new Random(seed), biome, chunkX, chunkZ)) {
				break;
			} else feature = null;
		}
		
		if(feature == null) return null;
		
		// Check if it's not too close to other features
		int separation = feature.minimumSeparation();
		Iterator<Feature> iterator = this.featureList.values().iterator();
		while(iterator.hasNext()) {
			Feature otherFeature = iterator.next();
			if(
					/*otherFeature.getClass() == feature.getClass() &&*/ (
						Math.abs(feature.centerX - otherFeature.centerX) <= separation || 
						Math.abs(feature.centerZ - otherFeature.centerZ) <= separation
					)
			) {
				return null;
			}
		}
		
		// Set this feature up (for instance creating a list of pieces or creating a logic representation of it)
		feature.setup(world, new Random(seed), biome, chunkX, chunkZ);
		
		// Return so it can be processed
		return feature;
	}
	
	/*
	 *  Iterates 10x10 chunks centered on chunkX, chunkZ searching for near features.
	 *  If found, they are stored in `featureList` and their generation stage is executed.
	 */	
	public boolean getNearestFeatures(int chunkX, int chunkZ, Chunk chunk) {
		boolean featureInChunk = false;
		
		HashSet<Long> featureHashes = new HashSet<Long>();
		
		for(int i = 0; i <= 3; i ++) {
			for(int x = chunkX - i; x <= chunkX + i; x ++) {
				for(int z = chunkZ - i; z <= chunkZ + i; z ++) {
					Feature feature = null;
					
					// First check already calculated features
					long chunkHash = ChunkCoordIntPair.chunkXZ2Int(x, z);
					feature = this.featureList.get(chunkHash); 
					
					// Not found, get a new (possibly)
					if(feature == null) {
						feature = this.getFeatureForChunkCoords(x, z);
						
						if(feature != null) {
							// If we got a feature for this chunk, store
							this.featureList.put(chunkHash, feature);	
							
						}
					}
					
					if(feature != null && feature.getFeatureRadius() >= i) {
						featureHashes.add(chunkHash);
					}
				}
			}
		}
		
		Iterator<Long> iterator = featureHashes.iterator();
		while(iterator.hasNext()) {
			this.featureList.get(iterator.next()).generate(chunkX, chunkZ, chunk);
			featureInChunk = true;
		}
		
		return featureInChunk;
	}

	/* 
	 *  Populates structures for this chunk
	 */
	public boolean populateFeatures(World world, Random rand, int chunkX, int chunkZ) {
		boolean featureInChunk = false; 
		
		// Failsafe (shouldn't fire, but just in case)
		long thisChunkHash = ChunkCoordIntPair.chunkXZ2Int(chunkX, chunkZ);
		if(this.generatedChunks.contains(thisChunkHash)) { 
			System.out.println ("Already calculated / calculating " + chunkX + " " + chunkZ); 
			return true;
		}
		this.generatedChunks.add(thisChunkHash);
				
		// Look which nearby features would affect this chunk
		int i = 3; {
			for(int x = chunkX - i; x <= chunkX + i; x ++) {
				for(int z = chunkZ - i; z <= chunkZ + i; z ++) {
					long chunkHash = ChunkCoordIntPair.chunkXZ2Int(x, z);
					
					Feature feature = this.featureList.get(chunkHash);
					if(feature != null &&
							Math.abs(feature.originChunkX - x) <= feature.getFeatureRadius() &&
							Math.abs(feature.originChunkZ - z) <= feature.getFeatureRadius()
					) {
							
						feature.populate(world, rand, chunkX, chunkZ);
						featureInChunk = true;
					}					
				}
			}
		}

		return featureInChunk;
	}
	
	static {
		/*
		 * Register features here. Notice that order is important. 
		 */
		registeredFeatures = new ArrayList<Class<?>> ();
		
		registerFeature(FeatureVolcano.class);
		//registerFeature(FeatureHollowHill.class);
	}
}
