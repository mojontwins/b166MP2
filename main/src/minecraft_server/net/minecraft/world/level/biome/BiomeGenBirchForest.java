package net.minecraft.world.level.biome;

import java.util.Random;

import net.minecraft.world.level.levelgen.feature.WorldGenerator;
import net.minecraft.world.level.levelgen.feature.trees.WorldGenAlder;
import net.minecraft.world.level.levelgen.feature.trees.WorldGenAspen;
import net.minecraft.world.level.levelgen.feature.trees.WorldGenEucalyptusBig;
import net.minecraft.world.level.levelgen.feature.trees.WorldGenForest;

public class BiomeGenBirchForest extends BiomeGenForest {

	public BiomeGenBirchForest(int biomeID) {
		super(biomeID);
	}

	public WorldGenerator getRandomWorldGenForTrees(Random rand) {
		if(rand.nextInt(64) == 0) return new WorldGenEucalyptusBig(false);
		return rand.nextInt(5) == 0 ? 
				(rand.nextInt(3) == 0 ? 
						new WorldGenAlder(6 + rand.nextInt(3), 5, 4) 
					: 
						new WorldGenAspen(8 + rand.nextInt(3))
				)
			:
				new WorldGenForest(false); 
	}
}
