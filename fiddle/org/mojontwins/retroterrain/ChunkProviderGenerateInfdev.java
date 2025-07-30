package org.mojontwins.retroterrain;

import java.util.Random;

import net.minecraft.src.BiomeGenBase;
import net.minecraft.src.Block;
import net.minecraft.src.World;

public class ChunkProviderGenerateInfdev extends ChunkProviderGenerateRetro {
	private NoiseGeneratorOctavesInfdev infNoise1;
	private NoiseGeneratorOctavesInfdev infNoise2;
	private NoiseGeneratorOctavesInfdev infNoise3;
	
	public ChunkProviderGenerateInfdev(World world1, long j2, boolean z4) {
		super(world1, j2, z4);
		
		// Ways to reset rng
		this.rand = new Random(j2);
		
		this.infNoise1 = new NoiseGeneratorOctavesInfdev(this.rand, 16);
		this.infNoise2 = new NoiseGeneratorOctavesInfdev(this.rand, 16);
		this.infNoise3 = new NoiseGeneratorOctavesInfdev(this.rand, 8);
		
		System.out.println ("Chunk Provider Infdev");
	}
	
	public void generateTerrain(int chunkX, int chunkZ, byte[] blocks, BiomeGenBase[] biomeGenBase4, double[] d5) {
		byte b4 = 4;
		byte b5 = 32;
		int seaLevel = 64;
		
		for(int xSection = 0; xSection < b4; ++xSection) {
			for(int zSection = 0; zSection < b4; ++zSection) {
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
							double volNoise1 = curNoiseA + (curNoiseC - curNoiseA) * xScalingFactor;
							double volNoise2 = curNoiseB + (curNoiseD - curNoiseB) * xScalingFactor;
							int indexInBlockArray = x + (xSection << 2) << 11 | 0 + (zSection << 2) << 7 | (ySection << 2) + y;

							for(z = 0; z < 4; ++z) {
								double temperature = d5[(xSection * 4 + x) * 16 + zSection * 4 + z];
								
								double zScalingFactor = (double)z / 4.0D;
								double volume = volNoise1 + (volNoise2 - volNoise1) * zScalingFactor;
								int blockID = 0;
								
								if((ySection << 2) + y < seaLevel) {
									if(temperature < 0.5D && (ySection << 2) + y >= seaLevel - 1) {
										blockID = Block.ice.blockID;
									} else {
										blockID = Block.waterStill.blockID;
									}
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
		if((d9 = this.infNoise3.generateNoiseOctaves(d1 * 684.412D / 80.0D, d3 * 684.412D / 400.0D, d5 * 684.412D / 80.0D) / 2.0D) < -1.0D) {
			if((d13 = this.infNoise1.generateNoiseOctaves(d1 * 684.412D, d3 * 984.412D, d5 * 684.412D) / 512.0D - d7) < -10.0D) {
				d13 = -10.0D;
			}

			if(d13 > 10.0D) {
				d13 = 10.0D;
			}
		} else if(d9 > 1.0D) {
			if((d13 = this.infNoise2.generateNoiseOctaves(d1 * 684.412D, d3 * 984.412D, d5 * 684.412D) / 512.0D - d7) < -10.0D) {
				d13 = -10.0D;
			}

			if(d13 > 10.0D) {
				d13 = 10.0D;
			}
		} else {
			double d15 = this.infNoise1.generateNoiseOctaves(d1 * 684.412D, d3 * 984.412D, d5 * 684.412D) / 512.0D - d7;
			double d17 = this.infNoise2.generateNoiseOctaves(d1 * 684.412D, d3 * 984.412D, d5 * 684.412D) / 512.0D - d7;
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
