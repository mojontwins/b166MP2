package net.minecraft.world.level.biome;

import java.util.Random;

import net.minecraft.world.entity.animal.EntityWolf;
import net.minecraft.world.level.SpawnListEntry;
import net.minecraft.world.level.levelgen.feature.WorldGenBigTree;
import net.minecraft.world.level.levelgen.feature.WorldGenForest;
import net.minecraft.world.level.levelgen.feature.WorldGenTrees;
import net.minecraft.world.level.levelgen.feature.WorldGenerator;

public class BiomeGenForest extends BiomeGenBaseBeta {
	public BiomeGenForest(int biomeID) {
		super(biomeID);
		this.spawnableCreatureList.add(new SpawnListEntry(EntityWolf.class, 2, 4, 6));
		
		// And some tweaks...
		this.biomeDecorator.extraTreesPerChunk = 5;
		this.biomeDecorator.grassPerChunk = 2;
	}

	public WorldGenerator getRandomWorldGenForTrees(Random random1) {
		return (WorldGenerator)(random1.nextInt(5) == 0 ? new WorldGenForest(false) : (random1.nextInt(3) == 0 ? new WorldGenBigTree(false) : new WorldGenTrees(false)));
	}
}
