package net.minecraft.world.level.biome;

import java.util.Random;

import net.minecraft.world.level.Weather;
import net.minecraft.world.level.levelgen.feature.WorldGenerator;
import net.minecraft.world.level.levelgen.feature.trees.WorldGenTrees;

public class BiomeGenSwamp extends BiomeGenBaseBeta {

	protected BiomeGenSwamp(int biomeID) {
		super(biomeID);
		
		this.weather = Weather.hot;
		this.biomeDecorator.waterlilyPerChunk = 8;
	}
	
	public WorldGenerator getRandomWorldGenForTrees(Random rand) {
		return (WorldGenerator)(rand.nextInt(4) == 0 ? new WorldGenTrees(true, rand.nextInt(4) - 2, 0, 0, true) : this.worldGenSwamp);
	}
}
