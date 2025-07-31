package net.minecraft.world.level.biome;

public class BiomeGenSavanna extends BiomeGenBase {

	public BiomeGenSavanna(int i1) {
		super(i1);
		
		// And some tweaks...
		this.biomeDecorator.extraTreesPerChunk = -5;
		this.biomeDecorator.flowersPerChunk = 0;
		this.biomeDecorator.deadBushPerChunk = 2;
		this.biomeDecorator.cactiPerChunk = 1;
	}

}
