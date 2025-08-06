package net.minecraft.world.level.levelgen.genlayer;

import net.minecraft.world.level.WorldType;

public abstract class GenLayerAlpha extends GenLayer {

	public GenLayerAlpha(long j1) {
		super(j1);
	}

	public static GenLayer[] initializeAllBiomeGenerators(long l, WorldType worldType) {
		// TODO : How many zooms in release vanilla after selecting biomes? replicate!
		
		GenLayerBiomesAlpha genLayerBiomesAlpha = new GenLayerBiomesAlpha(1L, worldType);
		GenLayerVoronoiZoom genLayerVoronoiZoom = new GenLayerVoronoiZoom(10L, genLayerBiomesAlpha);
		
		genLayerBiomesAlpha.initWorldGenSeed(l);
		genLayerVoronoiZoom.initWorldGenSeed(l);
		
		return new GenLayer[]{genLayerBiomesAlpha, genLayerVoronoiZoom, genLayerBiomesAlpha};
	}


}
