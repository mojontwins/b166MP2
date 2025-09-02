package net.minecraft.world.level.biome;

import java.util.Random;

import net.minecraft.world.entity.animal.EntityBoar;
import net.minecraft.world.entity.animal.EntityPig;
import net.minecraft.world.level.SpawnListEntry;
import net.minecraft.world.level.levelgen.feature.WorldGenerator;
import net.minecraft.world.level.levelgen.feature.trees.WorldGenPineTree;
import net.minecraft.world.level.levelgen.feature.trees.WorldGenTaiga1;
import net.minecraft.world.level.levelgen.feature.trees.WorldGenTaiga2;

public class BiomeGenSuperTaiga extends BiomeGenBaseBeta {

	public BiomeGenSuperTaiga(int biomeID) {
		super(biomeID);

		this.spawnableCreatureList.replaceAll(
				e -> 
					e.entityClass == EntityPig.class ? 
							new SpawnListEntry(EntityBoar.class, 10, 4, 4) 
						: 
							e
		);
			
		// And some tweaks...
		this.biomeDecorator.extraTreesPerChunk = 6;
		this.biomeDecorator.grassPerChunk = 1;
	}

	public WorldGenerator getRandomWorldGenForTrees(Random rand) {
		int treeMainSelector = rand.nextInt(3);
		switch(treeMainSelector) {
		case 0: 
			return new WorldGenPineTree(8 + rand.nextInt(8), false);
		case 1:
			return new WorldGenPineTree(4 + rand.nextInt(4), false);
		default:
			return rand.nextInt(3) == 0 ? new WorldGenTaiga1() : new WorldGenTaiga2();
		}	
	}
}
