package net.minecraft.world.level.biome;

import net.minecraft.world.entity.animal.EntityWolf;
import net.minecraft.world.level.SpawnListEntry;

public class BiomeGenSeasonalForest extends BiomeGenBaseBeta {

	public BiomeGenSeasonalForest(int biomeID) {
		super(biomeID);
		this.spawnableCreatureList.add(new SpawnListEntry(EntityWolf.class, 2, 4, 6));

		// And some tweaks...
		this.biomeDecorator.extraTreesPerChunk = 2;
		this.biomeDecorator.flowersPerChunk = 4;
		this.biomeDecorator.grassPerChunk = 2;
	}

}
