package net.minecraft.world.level.levelgen;

import net.minecraft.src.MathHelper;
import net.minecraft.world.level.World;
import net.minecraft.world.level.biome.BiomeGenBase;
import net.minecraft.world.level.tile.Block;

public class ChunkProviderAmplified extends ChunkProviderBeta {
	private final double[] terrainNoise;
	private final boolean amplified = true;
	private final float[] parabolicField;

	public ChunkProviderAmplified(World world1, long j2, boolean z4) {
		super(world1, j2, z4);
		this.terrainNoise = new double[825];
		this.parabolicField = new float[25];

		for (int x = -2; x <= 2; ++x) {
			for (int z = -2; z <= 2; ++z) {
				float var7 = 10.0F / MathHelper.sqrt_float((float) (x * x + z * z) + 0.2F);
				this.parabolicField[x + 2 + (z + 2) * 5] = var7;
			}
		}
	}
	
	@Override
	protected byte[] createByteArray() {
		return new byte[65536];
	}

	public void generateTerrain(int chunkX, int chunkZ, byte[] blocks) {
		byte var4 = 63;
		this.initializeNoiseField(chunkX * 4, 0, chunkZ * 4);

		for (int sectionX = 0; sectionX < 4; ++sectionX) {
			int idxZ1 = sectionX * 5;
			int idxZ2 = (sectionX + 1) * 5;

			for (int sectionZ = 0; sectionZ < 4; ++sectionZ) {
				int idx1 = (idxZ1 + sectionZ) * 33;
				int idx2 = (idxZ1 + sectionZ + 1) * 33;
				int idx3 = (idxZ2 + sectionZ) * 33;
				int idx4 = (idxZ2 + sectionZ + 1) * 33;

				for (int sectionY = 0; sectionY < 32; ++sectionY) {
					double noiseSpeed = 0.125D;
					double noiseA = this.terrainNoise[idx1 + sectionY];
					double noiseB = this.terrainNoise[idx2 + sectionY];
					double noiseC = this.terrainNoise[idx3 + sectionY];
					double noiseD = this.terrainNoise[idx4 + sectionY];
					double noiseAinc = (this.terrainNoise[idx1 + sectionY + 1] - noiseA) * noiseSpeed;
					double noiseBinc = (this.terrainNoise[idx2 + sectionY + 1] - noiseB) * noiseSpeed;
					double noiseCinc = (this.terrainNoise[idx3 + sectionY + 1] - noiseC) * noiseSpeed;
					double noiseDinc = (this.terrainNoise[idx4 + sectionY + 1] - noiseD) * noiseSpeed;

					for (int y = 0; y < 8; ++y) {
						double scalingFactor = 0.25D;
						double curNoiseA = noiseA;
						double curNoiseB = noiseB;
						double curNoiseAinc = (noiseC - noiseA) * scalingFactor;
						double curNoiseBinc = (noiseD - noiseB) * scalingFactor;

						for (int x = 0; x < 4; ++x) {
							int idx = (x + (sectionX << 2)) << 12 | (0 + (sectionZ << 2)) << 8 | (sectionY << 3) + y;

							short yy = 256;
							idx -= yy;
							
							double var46 = 0.25D;
							double var50 = (curNoiseB - curNoiseA) * var46;
							double var48 = curNoiseA - var50;

							for (int z = 0; z < 4; ++z) {
								if ((var48 += var50) > 0.0D) {
									blocks[idx += yy] = (byte) Block.stone.blockID;
								} else if (sectionY * 8 + y < var4) {
									blocks[idx += yy] = (byte) Block.waterStill.blockID;
								} else {
									blocks[idx += yy] = 0;
								}
							}

							curNoiseA += curNoiseAinc;
							curNoiseB += curNoiseBinc;
						}

						noiseA += noiseAinc;
						noiseB += noiseBinc;
						noiseC += noiseCinc;
						noiseD += noiseDinc;
					}
				}
			}
		}
	}

	public void replaceBlocksForBiome(int chunkX, int chunkZ, byte[] blocks, byte[] metadata, BiomeGenBase[] biomes) {
		byte seaLevel = 63;
		double d6 = 8.0D / 256D;
		this.stoneNoise = this.noiseStone.generateNoiseOctaves(this.stoneNoise, chunkX * 16, chunkZ * 16, 0, 16, 16, 1, d6 * 2.0D, d6 * 2.0D, d6 * 2.0D);

		for(int z = 0; z < 16; ++z) {
			for(int x = 0; x < 16; ++x) {
				BiomeGenBase biomeGenBase10 = biomes[z | (x << 4)];
				float f11 = biomeGenBase10.getFloatTemperature();
				int i12 = (int)(this.stoneNoise[z + x * 16] / 3.0D + 3.0D + this.rand.nextDouble() * 0.25D);
				int run = -1;
				byte topBlock = biomeGenBase10.getTopBlock(this.rand);
				byte fillBlock = biomeGenBase10.getFillBlock(this.rand);

				for(int y = 255; y >= 0; --y) {				
					int idx = x << 12 | z << 8 | y;

					if(y <= 0 + this.rand.nextInt(5)) {
						blocks[idx] = (byte)Block.bedrock.blockID;
					} else {
						byte blockID = blocks[idx];
						if(blockID == 0) {
							run = -1;
						} else if(blockID == Block.stone.blockID) {
							if(run == -1) {
								if(i12 <= 0) {
									topBlock = 0;
									fillBlock = (byte)Block.stone.blockID;
								} else if(y >= seaLevel - 4 && y <= seaLevel + 1) {
									topBlock = biomeGenBase10.getTopBlock(this.rand);
									fillBlock = biomeGenBase10.getFillBlock(this.rand);
								}

								if(y < seaLevel && topBlock == 0) {
									if(f11 < 0.15F) {
										topBlock = (byte)Block.ice.blockID;
									} else {
										topBlock = (byte)Block.waterStill.blockID;
									}
								}

								run = i12;
								if(y >= seaLevel - 1) {
									blocks[idx] = topBlock;
								} else {
									blocks[idx] = fillBlock;
								}
							} else if(run > 0) {
								--run;
								blocks[idx] = fillBlock;
								if(run == 0 && fillBlock == Block.sand.blockID) {
									run = this.rand.nextInt(4);
									fillBlock = (byte)Block.sandStone.blockID;
								}
							}
						}
					}
				}
			}
		}

	}
	
	private void initializeNoiseField(int x, int y, int z) {
		this.scaleArray = this.depthNoise.generateNoiseOctaves(this.scaleArray, x, z, 5, 5, 200.0D, 200.0D, 0.5D);
		this.mainArray = this.mainNoise.generateNoiseOctaves(this.mainArray, x, y, z, 5, 33, 5, 8.555150000000001D, 4.277575000000001D, 8.555150000000001D);
		this.minLimitArray = this.minLimitNoise.generateNoiseOctaves(this.minLimitArray, x, y, z, 5, 33, 5, 684.412D, 684.412D, 684.412D);
		this.maxLimitArray = this.maxLimitNoise.generateNoiseOctaves(this.maxLimitArray, x, y, z, 5, 33, 5, 684.412D, 684.412D, 684.412D);
		int var12 = 0;
		int var13 = 0;
		for (int var16 = 0; var16 < 5; ++var16) {
			for (int var17 = 0; var17 < 5; ++var17) {
				float var18 = 0.0F;
				float var19 = 0.0F;
				float var20 = 0.0F;
				byte var21 = 2;
				BiomeGenBase var22 = this.biomesForGeneration[var16 + 2 + (var17 + 2) * 10];

				for (int var23 = -var21; var23 <= var21; ++var23) {
					for (int var24 = -var21; var24 <= var21; ++var24) {
						BiomeGenBase var25 = this.biomesForGeneration[var16 + var23 + 2 + (var17 + var24 + 2) * 10];
						float var26 = var25.minHeight;
						float var27 = var25.maxHeight;

						if (this.amplified && var26 > 0.0F) {
							var26 = 1.0F + var26 * 2.0F;
							var27 = 1.0F + var27 * 4.0F;
						}

						float var28 = this.parabolicField[var23 + 2 + (var24 + 2) * 5] / (var26 + 2.0F);

						if (var25.minHeight > var22.minHeight) {
							var28 /= 2.0F;
						}

						var18 += var27 * var28;
						var19 += var26 * var28;
						var20 += var28;
					}
				}

				var18 /= var20;
				var19 /= var20;
				var18 = var18 * 0.9F + 0.1F;
				var19 = (var19 * 4.0F - 1.0F) / 8.0F;
				double var46 = this.scaleArray[var13] / 8000.0D;

				if (var46 < 0.0D) {
					var46 = -var46 * 0.3D;
				}

				var46 = var46 * 3.0D - 2.0D;

				if (var46 < 0.0D) {
					var46 /= 2.0D;

					if (var46 < -1.0D) {
						var46 = -1.0D;
					}

					var46 /= 1.4D;
					var46 /= 2.0D;
				} else {
					if (var46 > 1.0D) {
						var46 = 1.0D;
					}

					var46 /= 8.0D;
				}

				++var13;
				double var47 = (double) var19;
				double var48 = (double) var18;
				var47 += var46 * 0.2D;
				var47 = var47 * 8.5D / 8.0D;
				double var29 = 8.5D + var47 * 4.0D;

				for (int var31 = 0; var31 < 33; ++var31) {
					double var32 = ((double) var31 - var29) * 12.0D * 128.0D / 256.0D / var48;

					if (var32 < 0.0D) {
						var32 *= 4.0D;
					}

					double var34 = this.maxLimitArray[var12] / 512.0D;
					double var36 = this.mainArray[var12] / 512.0D;
					double var38 = (this.minLimitArray[var12] / 10.0D + 1.0D) / 2.0D;
					double var40 = MathHelper.denormalizeClamp(var34, var36, var38) - var32;

					if (var31 > 29) {
						double var42 = (double) ((float) (var31 - 29) / 3.0F);
						var40 = var40 * (1.0D - var42) + -10.0D * var42;
					}

					this.terrainNoise[var12] = var40;
					++var12;
				}
			}
		}
	}
}
