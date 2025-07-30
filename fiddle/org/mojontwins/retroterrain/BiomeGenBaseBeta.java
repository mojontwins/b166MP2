package org.mojontwins.retroterrain;

import java.util.Random;

import net.minecraft.src.BiomeGenBase;
import net.minecraft.src.World;

public class BiomeGenBaseBeta extends BiomeGenBase {
	public static final BiomeGenBase rainforest = (new BiomeGenRainforest(100)).setColor(588342).setBiomeName("Rainforest");
	public static final BiomeGenBase seasonalForest = (new BiomeGenForestBeta(101)).setColor(10215459).setBiomeName("Seasonal Forest");
	public static final BiomeGenBase savanna = (new BiomeGenDesertBeta(102)).setColor(14278691).setBiomeName("Savanna");
	public static final BiomeGenBase shrubland = (new BiomeGenDesertBeta(103)).setColor(10595616).setBiomeName("Shrubland");
	
	public BiomeDecoratorBeta biomeDecorator;
	
	protected BiomeGenBaseBeta(int i1) {
		super(i1);
		this.biomeDecorator = this.createBiomeDecorator();
	}

	protected BiomeDecoratorBeta createBiomeDecorator() {
		return new BiomeDecoratorBeta(this);
	}
	
	public void decorate(World world1, Random random2, int i3, int i4) {
		this.decorate(world1, random2, i3, i4);
	}
}
