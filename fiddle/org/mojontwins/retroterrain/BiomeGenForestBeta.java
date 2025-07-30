package org.mojontwins.retroterrain;

import net.minecraft.src.BiomeGenForest;

public class BiomeGenForestBeta extends BiomeGenForest {

	public BiomeGenForestBeta(int i1) {
		super(i1);
		// TODO Auto-generated constructor stub
	}

	public BiomeGenForestBeta setColor(int c) {
		super.setColor(c);
		return this;
	}
	
	public BiomeGenForestBeta setBiomeName (String name) {
		super.setBiomeName(name);
		return this;
	}

}
