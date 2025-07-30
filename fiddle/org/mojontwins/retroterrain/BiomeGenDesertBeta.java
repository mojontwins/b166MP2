package org.mojontwins.retroterrain;

import net.minecraft.src.BiomeGenDesert;

public class BiomeGenDesertBeta extends BiomeGenDesert {

	public BiomeGenDesertBeta(int i1) {
		super(i1);
		// TODO Auto-generated constructor stub
	}

	public BiomeGenDesertBeta setColor(int c) {
		super.setColor(c);
		return this;
	}
	
	public BiomeGenDesertBeta setBiomeName (String name) {
		super.setBiomeName(name);
		return this;
	}
}
