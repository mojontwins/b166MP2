package net.minecraft.world.level.biome;

import java.util.Random;

import net.minecraft.world.entity.animal.EntityGoat;
import net.minecraft.world.level.SpawnListEntry;
import net.minecraft.world.level.levelgen.feature.WorldGenerator;
import net.minecraft.world.level.levelgen.feature.trees.WorldGenShrub;
import net.minecraft.world.level.levelgen.feature.trees.WorldGenTrees;

public class BiomeGenShrubland extends BiomeGenBaseBeta {

	protected BiomeGenShrubland(int i1) {
		super(i1);
		this.spawnableCreatureList.add(new SpawnListEntry(EntityGoat.class, 4, 4, 4));
	}

	public WorldGenerator getRandomWorldGenForTrees(Random random1) {
		return (WorldGenerator)(random1.nextInt(5) == 0 ? new WorldGenTrees(false) : new WorldGenShrub(2, 2));
	}
}
