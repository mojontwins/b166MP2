package net.minecraft.world.level.levelgen.genlayer;

import net.minecraft.world.level.WorldType;

public abstract class GenLayerHell extends GenLayer {
	
	public GenLayerHell(long j1) {
		super(j1);
	}

	public static GenLayer[] initializeAllBiomeGenerators(long seed, WorldType worldType2) {
		int biomesize = 2;
		
		GenLayer obj = new GenLayerHellStarter(1L);
		obj = new GenLayerHellFuzzyZoom(2000L, (obj));
		for(int i = 1; i < 3; i++) { obj = new GenLayerHellZoom(2000L + i, (obj)); }
		obj = GenLayerHellZoom.zoomLayer(1000L, ((obj)), 0);
		obj = new GenLayerHellBiomes(200L, ((obj)), worldType2, false);
		obj = GenLayerHellZoom.zoomLayer(1000L, ((obj)), 2);
		for(int j = 0; j < biomesize; j++) { obj = new GenLayerHellZoom(1000L + j, (obj)); }
		GenLayerVoronoiZoom genlayervoronoizoom = new GenLayerVoronoiZoom(10L, ((obj)));
		(obj).initWorldGenSeed(seed);
		genlayervoronoizoom.initWorldGenSeed(seed);
		return (new GenLayer[] { obj, genlayervoronoizoom });
	}

}
