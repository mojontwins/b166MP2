package net.minecraft.world.level.levelgen;

import java.util.Random;

import net.minecraft.world.level.World;
import net.minecraft.world.level.levelgen.synth.NoiseGeneratorOctavesInfdev;
import net.minecraft.world.level.tile.Block;

public class ChunkProviderInfdev extends ChunkProviderGenerate {
	private NoiseGeneratorOctavesInfdev infminLimitArray;
	private NoiseGeneratorOctavesInfdev infmaxLimitArray;
	private NoiseGeneratorOctavesInfdev infmainArray;
	
	public ChunkProviderInfdev(World world, long seed) {
		this(world, seed, true);
	}
	
	public ChunkProviderInfdev(World world, long seed, boolean mapFeaturesEnabled) {
		super(world, seed, mapFeaturesEnabled);
		
		// Reset the randomizer so worlds are seed-accurate with vanilla
		this.rand = new Random(seed);
		
		this.infminLimitArray = new NoiseGeneratorOctavesInfdev(this.rand, 16);
		this.infmaxLimitArray = new NoiseGeneratorOctavesInfdev(this.rand, 16);
		this.infmainArray = new NoiseGeneratorOctavesInfdev(this.rand, 8);
		
		System.out.println ("Chunk Provider Infdev");
	}
	
	public void generateTerrain(int chunkX, int chunkZ, byte[] blocks) {
		byte sectionSize = 4;
		byte b5 = 32;
		byte seaLevel = 64;
		
		for(int xSection = 0; xSection < sectionSize; ++xSection) {
			for(int zSection = 0; zSection < sectionSize; ++zSection) {
				double[][] terrainNoise = new double[33][4];
				int x = (chunkX << 2) + xSection;
				int z = (chunkZ << 2) + zSection;

				for(int i = 0; i < terrainNoise.length; ++i) {
					terrainNoise[i][0] = this.initializeNoiseField((double)x, (double)i, (double)z);
					terrainNoise[i][1] = this.initializeNoiseField((double)x, (double)i, (double)(z + 1));
					terrainNoise[i][2] = this.initializeNoiseField((double)(x + 1), (double)i, (double)z);
					terrainNoise[i][3] = this.initializeNoiseField((double)(x + 1), (double)i, (double)(z + 1));
				}

				for(int ySection = 0; ySection < b5; ++ySection) {
					double noiseA = terrainNoise[ySection][0];
					double noiseB = terrainNoise[ySection][1];
					double noiseC = terrainNoise[ySection][2];
					double noiseD = terrainNoise[ySection][3];
					double noiseAinc = terrainNoise[ySection + 1][0];
					double noiseBinc = terrainNoise[ySection + 1][1];
					double noiseCinc = terrainNoise[ySection + 1][2];
					double noiseDinc = terrainNoise[ySection + 1][3];

					for(int y = 0; y < 4; ++y) {
						double yscalingFactor = (double)y / 4.0D;
						double curNoiseA = noiseA + (noiseAinc - noiseA) * yscalingFactor;
						double curNoiseB = noiseB + (noiseBinc - noiseB) * yscalingFactor;
						double curNoiseC = noiseC + (noiseCinc - noiseC) * yscalingFactor;
						double curNoiseD = noiseD + (noiseDinc - noiseD) * yscalingFactor;

						for(x = 0; x < 4; ++x) {
							double xScalingFactor = (double)x / 4.0D;
							double volminLimitArray = curNoiseA + (curNoiseC - curNoiseA) * xScalingFactor;
							double volmaxLimitArray = curNoiseB + (curNoiseD - curNoiseB) * xScalingFactor;
							int indexInBlockArray = x + (xSection << 2) << 11 | 0 + (zSection << 2) << 7 | (ySection << 2) + y;

							for(z = 0; z < 4; ++z) {
								double zScalingFactor = (double)z / 4.0D;
								double volume = volminLimitArray + (volmaxLimitArray - volminLimitArray) * zScalingFactor;
								int blockID = 0;
								
								if((ySection << 2) + y < seaLevel) {
									blockID = Block.waterStill.blockID;
								}

								if(volume > 0.0D) {
									blockID = Block.stone.blockID;
								}

								blocks[indexInBlockArray] = (byte)blockID;
								indexInBlockArray += 128;
							}
						}
					}
				}
			}
		}	
	}
	
	private double initializeNoiseField(double d1, double d3, double d5) {
		double d7;
		if((d7 = d3 * 4.0D - 64.0D) < 0.0D) {
			d7 *= 3.0D;
		}

		double d9;
		double d13;
		if((d9 = this.infmainArray.generateNoiseOctaves(d1 * 684.412D / 80.0D, d3 * 684.412D / 400.0D, d5 * 684.412D / 80.0D) / 2.0D) < -1.0D) {
			if((d13 = this.infminLimitArray.generateNoiseOctaves(d1 * 684.412D, d3 * 984.412D, d5 * 684.412D) / 512.0D - d7) < -10.0D) {
				d13 = -10.0D;
			}

			if(d13 > 10.0D) {
				d13 = 10.0D;
			}
		} else if(d9 > 1.0D) {
			if((d13 = this.infmaxLimitArray.generateNoiseOctaves(d1 * 684.412D, d3 * 984.412D, d5 * 684.412D) / 512.0D - d7) < -10.0D) {
				d13 = -10.0D;
			}

			if(d13 > 10.0D) {
				d13 = 10.0D;
			}
		} else {
			double d15 = this.infminLimitArray.generateNoiseOctaves(d1 * 684.412D, d3 * 984.412D, d5 * 684.412D) / 512.0D - d7;
			double d17 = this.infmaxLimitArray.generateNoiseOctaves(d1 * 684.412D, d3 * 984.412D, d5 * 684.412D) / 512.0D - d7;
			if(d15 < -10.0D) {
				d15 = -10.0D;
			}

			if(d15 > 10.0D) {
				d15 = 10.0D;
			}

			if(d17 < -10.0D) {
				d17 = -10.0D;
			}

			if(d17 > 10.0D) {
				d17 = 10.0D;
			}

			double d19 = (d9 + 1.0D) / 2.0D;
			d13 = d15 + (d17 - d15) * d19;
		}

		return d13;
	}
}
