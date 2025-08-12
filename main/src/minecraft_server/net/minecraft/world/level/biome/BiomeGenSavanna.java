package net.minecraft.world.level.biome;

import java.util.Random;

import net.minecraft.world.entity.animal.EntityGoat;
import net.minecraft.world.level.BlockState;
import net.minecraft.world.level.SpawnListEntry;
import net.minecraft.world.level.Weather;
import net.minecraft.world.level.levelgen.feature.WorldGenerator;
import net.minecraft.world.level.levelgen.feature.trees.WorldGenTrees;
import net.minecraft.world.level.tile.Block;

public class BiomeGenSavanna extends BiomeGenBaseBeta {

	public BiomeGenSavanna(int i1) {
		super(i1);
		this.weather = Weather.hot;
		
		this.spawnableCaveCreatureList.add(new SpawnListEntry(EntityGoat.class, 4, 4, 4));
		
		// And some tweaks...
		this.biomeDecorator.extraTreesPerChunk = -2;
		this.biomeDecorator.flowersPerChunk = 0;
		this.biomeDecorator.deadBushPerChunk = 2;
		this.biomeDecorator.cactiPerChunk = 1;
	}

	@Override
	public WorldGenerator getRandomWorldGenForTrees(Random rand) {
		if(rand.nextBoolean()) return new WorldGenTrees(true).withFruit(new BlockState(Block.treeFruit, 0), 8);
		return super.getRandomWorldGenForTrees(rand);
	}
}
