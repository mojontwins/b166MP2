package net.minecraft.world.level.biome;

import java.util.Random;

import net.minecraft.world.entity.animal.EntityBoar;
import net.minecraft.world.entity.animal.EntityColdCow;
import net.minecraft.world.entity.animal.EntityCow;
import net.minecraft.world.entity.animal.EntityPig;
import net.minecraft.world.entity.animal.EntityWolf;
import net.minecraft.world.level.SpawnListEntry;
import net.minecraft.world.level.Weather;
import net.minecraft.world.level.levelgen.feature.WorldGenerator;
import net.minecraft.world.level.levelgen.feature.trees.WorldGenTaiga1;
import net.minecraft.world.level.levelgen.feature.trees.WorldGenTaiga2;

public class BiomeGenTaiga extends BiomeGenBaseBeta {
	public BiomeGenTaiga(int biomeID) {
		super(biomeID);
		
		this.weather = Weather.cold;
		
		this.spawnableCreatureList.add(new SpawnListEntry(EntityWolf.class, 2, 6, 6));
		this.spawnableCreatureList.replaceAll(
				e -> 
					e.entityClass == EntityPig.class ? 
							new SpawnListEntry(EntityBoar.class, 10, 4, 4) 
						: 
							e.entityClass == EntityCow.class ?
									new SpawnListEntry(EntityColdCow.class, 10, 4, 4)
								:
									e
		);
		
		// And some tweaks...
		this.biomeDecorator.extraTreesPerChunk = 5;
		this.biomeDecorator.grassPerChunk = 1;
	}

	public WorldGenerator getRandomWorldGenForTrees(Random random1) {
		return (WorldGenerator)(random1.nextInt(3) == 0 ? new WorldGenTaiga1() : new WorldGenTaiga2());
	}
}
