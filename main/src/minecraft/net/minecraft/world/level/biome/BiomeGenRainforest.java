package net.minecraft.world.level.biome;

import java.util.Random;

import net.minecraft.world.entity.animal.EntityWolf;
import net.minecraft.world.level.BlockState;
import net.minecraft.world.level.SpawnListEntry;
import net.minecraft.world.level.levelgen.feature.WorldGenBigTree;
import net.minecraft.world.level.levelgen.feature.WorldGenTallGrass;
import net.minecraft.world.level.levelgen.feature.WorldGenerator;
import net.minecraft.world.level.levelgen.feature.trees.WorldGenTrees;
import net.minecraft.world.level.tile.Block;

public class BiomeGenRainforest extends BiomeGenBaseBeta {
	protected BiomeGenRainforest(int i1) {
		super(i1);
		this.spawnableCreatureList.add(new SpawnListEntry(EntityWolf.class, 2, 4, 6));
		
		// And some tweaks...
		this.biomeDecorator.extraTreesPerChunk = 10;
		this.biomeDecorator.flowersPerChunk = 4;
		this.biomeDecorator.grassPerChunk = 10;
	}
	
	public WorldGenerator getRandomWorldGenForGrass(Random rand) {
		return (WorldGenerator)(rand.nextInt(4) == 0 ? new WorldGenTallGrass(Block.tallGrass.blockID, 2) : super.getRandomWorldGenForGrass(rand));
	}
	
	@Override
	public WorldGenerator getRandomWorldGenForTrees(Random rand) {
		if(rand.nextInt(64) == 0) return new WorldGenTrees(true).withFruit(new BlockState(Block.treeFruit, 0), 8);
		return (WorldGenerator)(rand.nextInt(3) == 0 ? new WorldGenBigTree(false) : new WorldGenTrees(false));
	}
}
