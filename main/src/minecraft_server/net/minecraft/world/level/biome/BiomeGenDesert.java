package net.minecraft.world.level.biome;

import net.minecraft.world.level.Weather;
import net.minecraft.world.level.tile.Block;

public class BiomeGenDesert extends BiomeGenBaseBeta {

	protected BiomeGenDesert(int i1) {
		super(i1);
		
		this.weather = Weather.desert;

		// Desert generates a sandy surface
		this.topBlock = this.fillerBlock = (byte) Block.sand.blockID;
		
		// With no animals
		this.spawnableCreatureList.clear();
		
		// And some tweaks...
		this.biomeDecorator.extraTreesPerChunk = -20;
		this.biomeDecorator.deadBushPerChunk = 2;
		this.biomeDecorator.cactiPerChunk = 10;
	}
}
