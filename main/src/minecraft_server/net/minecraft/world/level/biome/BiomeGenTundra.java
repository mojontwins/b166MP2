package net.minecraft.world.level.biome;

public class BiomeGenTundra extends BiomeGenBase {

	public BiomeGenTundra(int i1) {
		super(i1);
		
		// And some tweaks...
		this.biomeDecorator.extraTreesPerChunk = -20;
		this.biomeDecorator.flowersPerChunk = 0;
	}

}
