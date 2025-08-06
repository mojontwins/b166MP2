package net.minecraft.world.level.biome;

import java.util.Random;

import net.minecraft.world.level.levelgen.feature.WorldGenBigTree;
import net.minecraft.world.level.levelgen.feature.WorldGenTallGrass;
import net.minecraft.world.level.levelgen.feature.WorldGenTrees;
import net.minecraft.world.level.levelgen.feature.WorldGenerator;
import net.minecraft.world.level.tile.Block;

public class BiomeGenRainforest extends BiomeGenBaseBeta {
	protected BiomeGenRainforest(int i1) {
		super(i1);
		
		// And some tweaks...
		this.biomeDecorator.extraTreesPerChunk = 10;
		this.biomeDecorator.flowersPerChunk = 4;
		this.biomeDecorator.grassPerChunk = 10;
	}

	public WorldGenerator getRandomWorldGenForTrees(Random random1) {
		return (WorldGenerator)(random1.nextInt(3) == 0 ? new WorldGenBigTree(false) : new WorldGenTrees(false));
	}
	
	public WorldGenerator getRandomWorldGenForGrass(Random rand) {
		return (WorldGenerator)(rand.nextInt(4) == 0 ? new WorldGenTallGrass(Block.tallGrass.blockID, 2) : super.getRandomWorldGenForGrass(rand));
	}
}
