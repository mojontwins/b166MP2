package net.minecraft.world.level.biome;

public class BiomeGenPlains extends BiomeGenBase {

	protected BiomeGenPlains(int i1) {
		super(i1);

		// And some tweaks...
		this.biomeDecorator.extraTreesPerChunk = -20;
		this.biomeDecorator.grassPerChunk = 10;
	}

}
