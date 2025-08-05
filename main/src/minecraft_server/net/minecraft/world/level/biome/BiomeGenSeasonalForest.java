package net.minecraft.world.level.biome;

public class BiomeGenSeasonalForest extends BiomeGenBaseBeta {

	public BiomeGenSeasonalForest(int biomeID) {
		super(biomeID);

		// And some tweaks...
		this.biomeDecorator.extraTreesPerChunk = 2;
		this.biomeDecorator.flowersPerChunk = 4;
		this.biomeDecorator.grassPerChunk = 2;
	}

}
