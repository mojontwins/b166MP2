package net.minecraft.world.level.biome;

import java.util.Random;

import net.minecraft.world.level.levelgen.feature.WorldGenTaiga1;
import net.minecraft.world.level.levelgen.feature.WorldGenTaiga2;
import net.minecraft.world.entity.animal.EntityWolf;
import net.minecraft.world.level.SpawnListEntry;
import net.minecraft.world.level.Weather;
import net.minecraft.world.level.levelgen.feature.WorldGenerator;

public class BiomeGenTaiga extends BiomeGenBaseBeta {
	public BiomeGenTaiga(int biomeID) {
		super(biomeID);
		
		this.weather = Weather.cold;
		
		this.spawnableCreatureList.add(new SpawnListEntry(EntityWolf.class, 2, 6, 6));
		
		// And some tweaks...
		this.biomeDecorator.extraTreesPerChunk = 5;
		this.biomeDecorator.grassPerChunk = 1;
	}

	public WorldGenerator getRandomWorldGenForTrees(Random random1) {
		return (WorldGenerator)(random1.nextInt(3) == 0 ? new WorldGenTaiga1() : new WorldGenTaiga2());
	}
}
