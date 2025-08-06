package net.minecraft.world.level.biome;

import net.minecraft.world.level.Weather;

public class BiomeGenSavanna extends BiomeGenBaseBeta {

	public BiomeGenSavanna(int i1) {
		super(i1);
		this.weather = Weather.hot;
		
		// And some tweaks...
		this.biomeDecorator.extraTreesPerChunk = -5;
		this.biomeDecorator.flowersPerChunk = 0;
		this.biomeDecorator.deadBushPerChunk = 2;
		this.biomeDecorator.cactiPerChunk = 1;
	}

}
