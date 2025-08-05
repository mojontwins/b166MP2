package net.minecraft.world.level.levelgen.synth;

import java.util.Random;

public class NoiseGeneratorOctavesBeta extends NoiseGenerator {
	private NoiseGeneratorPerlinBeta[] generatorCollection;
	private int numOctaves;

	public NoiseGeneratorOctavesBeta(Random random1, int i2) {
		this.numOctaves = i2;
		this.generatorCollection = new NoiseGeneratorPerlinBeta[i2];

		for(int i3 = 0; i3 < i2; ++i3) {
			this.generatorCollection[i3] = new NoiseGeneratorPerlinBeta(random1);
		}

	}

	public double getDensity(double x, double z) {
		double density = 0.0D;
		double factor = 1.0D;

		for(int i = 0; i < this.numOctaves; ++i) {
			density += this.generatorCollection[i].generateNoise(x * factor, z * factor) / factor;
			factor /= 2.0D;
		}

		return density;
	}

	public double[] generateNoiseOctaves(
			double[] noiseArray, 
			double x, double y, double z, 
			int xSize, int ySize, int zSize, 
			double scaleX, double scaleY, double scaleZ
	) {
		if(noiseArray == null) {
			noiseArray = new double[xSize * ySize * zSize];
		} else {
			for(int i = 0; i < noiseArray.length; ++i) {
				noiseArray[i] = 0.0D;
			}
		}

		double factor = 1.0D;

		for(int i = 0; i < this.numOctaves; ++i) {
			this.generatorCollection[i].generateNoise(noiseArray, x, y, z, xSize, ySize, zSize, scaleX * factor, scaleY * factor, scaleZ * factor, factor);
			factor /= 2.0D;
		}

		return noiseArray;
	}

	public double[] generateNoiseOctaves(
			double[] noiseArray, 
			int x, int z, 
			int xSize, int zSize, 
			double scaleX, double scaleZ, double d10
	) {
		return this.generateNoiseOctaves(noiseArray, (double)x, 10.0D, (double)z, xSize, 1, zSize, scaleX, 1.0D, scaleZ);
	}
}
