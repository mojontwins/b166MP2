package org.mojontwins.retroterrain;

import java.util.Random;

import net.minecraft.src.EntityWolf;
import net.minecraft.src.SpawnListEntry;
import net.minecraft.src.WorldGenerator;

public class BiomeGenRainforest extends BiomeGenBaseBeta {
	protected BiomeGenRainforest(int i1) {
		super(i1);
		this.spawnableCreatureList.add(new SpawnListEntry(EntityWolf.class, 5, 4, 4));
		this.biomeDecorator.setTreesPerChunk(16);
		this.biomeDecorator.setGrassPerChunk(8);
	}

	public WorldGenerator getRandomWorldGenForTrees(Random random1) {
		return (WorldGenerator)(random1.nextInt(3) == 0 ? this.worldGenBigTree : this.worldGenTrees);
	}
	
	public BiomeGenRainforest setColor(int c) {
		super.setColor(c);
		return this;
	}
	
	public BiomeGenRainforest setBiomeName (String name) {
		super.setBiomeName(name);
		return this;
	}
}
