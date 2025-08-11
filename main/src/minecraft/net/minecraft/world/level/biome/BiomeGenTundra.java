package net.minecraft.world.level.biome;

import java.util.Random;

import net.minecraft.world.entity.animal.EntityColdCow;
import net.minecraft.world.entity.animal.EntityCow;
import net.minecraft.world.level.SpawnListEntry;
import net.minecraft.world.level.Weather;
import net.minecraft.world.level.levelgen.feature.WorldGenerator;
import net.minecraft.world.level.levelgen.feature.trees.WorldGenTaiga1;
import net.minecraft.world.level.levelgen.feature.trees.WorldGenTrees;

public class BiomeGenTundra extends BiomeGenBaseBeta {

	public BiomeGenTundra(int i1) {
		super(i1);
		this.spawnableCreatureList.replaceAll(
				e -> 
					e.entityClass == EntityCow.class ? 
							new SpawnListEntry(EntityColdCow.class, 10, 4, 4) 
						: 
							e
		);
		
		this.weather = Weather.cold;
		
		// And some tweaks...
		this.biomeDecorator.extraTreesPerChunk = -10;
		this.biomeDecorator.flowersPerChunk = 0;
	}


	public WorldGenerator getRandomWorldGenForTrees(Random rand) {
		return (WorldGenerator)(rand.nextInt(3) == 0 ? new WorldGenTaiga1() : new WorldGenTrees(true, -2, 1, 1, false));
	}
}
