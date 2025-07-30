package net.minecraft.world.level.levelgen;

import java.util.List;
import java.util.Random;

import com.mojontwins.minecraft.feature.FeatureProvider;

import net.minecraft.world.entity.EnumCreatureType;
import net.minecraft.world.level.ChunkPosition;
import net.minecraft.world.level.SpawnListEntry;
import net.minecraft.world.level.World;
import net.minecraft.world.level.biome.BiomeGenBase;
import net.minecraft.world.level.chunk.Chunk;
import net.minecraft.world.level.chunk.IChunkProvider;
import net.minecraft.world.level.chunk.storage.IProgressUpdate;
import net.minecraft.world.level.levelgen.feature.WorldGenFire;
import net.minecraft.world.level.levelgen.feature.WorldGenFlowers;
import net.minecraft.world.level.levelgen.feature.WorldGenGlowStone1;
import net.minecraft.world.level.levelgen.feature.WorldGenGlowStone2;
import net.minecraft.world.level.levelgen.feature.WorldGenHellLava;
import net.minecraft.world.level.levelgen.feature.WorldGenerator;
import net.minecraft.world.level.levelgen.synth.NoiseGeneratorOctaves;
import net.minecraft.world.level.tile.Block;
import net.minecraft.world.level.tile.BlockSand;

public class ChunkProviderHell implements IChunkProvider {
	private Random hellRNG;
	private NoiseGeneratorOctaves netherNoiseGen1;
	private NoiseGeneratorOctaves netherNoiseGen2;
	private NoiseGeneratorOctaves netherNoiseGen3;
	private NoiseGeneratorOctaves slowsandGravelNoiseGen;
	private NoiseGeneratorOctaves netherrackExculsivityNoiseGen;
	public NoiseGeneratorOctaves netherNoiseGen6;
	public NoiseGeneratorOctaves netherNoiseGen7;
	private World worldObj;
	private double[] terrainNoise;
	//public MapGenNetherBridge genNetherBridge = new MapGenNetherBridge();
	private double[] slowsandNoise = new double[256];
	private double[] gravelNoise = new double[256];
	private double[] netherrackExclusivityNoise = new double[256];
	private MapGenBase netherCaveGenerator = new MapGenCavesHell();
	private FeatureProvider featureProvider;

	double[] noiseData1;
	double[] noiseData2;
	double[] noiseData3;
	double[] noiseData4;
	double[] noiseData5;
	private BiomeGenBase[] biomesForGeneration;

	public ChunkProviderHell(World world1, long j2) {
		this.worldObj = world1;
		this.hellRNG = new Random(j2);
		this.netherNoiseGen1 = new NoiseGeneratorOctaves(this.hellRNG, 16);
		this.netherNoiseGen2 = new NoiseGeneratorOctaves(this.hellRNG, 16);
		this.netherNoiseGen3 = new NoiseGeneratorOctaves(this.hellRNG, 8);
		this.slowsandGravelNoiseGen = new NoiseGeneratorOctaves(this.hellRNG, 4);
		this.netherrackExculsivityNoiseGen = new NoiseGeneratorOctaves(this.hellRNG, 4);
		this.netherNoiseGen6 = new NoiseGeneratorOctaves(this.hellRNG, 10);
		this.netherNoiseGen7 = new NoiseGeneratorOctaves(this.hellRNG, 16);
		
		this.featureProvider = new FeatureProvider(worldObj, this);
	}

	public void generateNetherTerrain(int i1, int i2, byte[] b3) {
		byte b4 = 4;
		byte b5 = 32;
		int i6 = b4 + 1;
		byte b7 = 17;
		int i8 = b4 + 1;
		this.terrainNoise = this.initializeNoiseField(this.terrainNoise, i1 * b4, 0, i2 * b4, i6, b7, i8);

		for (int i9 = 0; i9 < b4; ++i9) {
			for (int i10 = 0; i10 < b4; ++i10) {
				for (int i11 = 0; i11 < 16; ++i11) {
					double d12 = 0.125D;
					double d14 = this.terrainNoise[((i9 + 0) * i8 + i10 + 0) * b7 + i11 + 0];
					double d16 = this.terrainNoise[((i9 + 0) * i8 + i10 + 1) * b7 + i11 + 0];
					double d18 = this.terrainNoise[((i9 + 1) * i8 + i10 + 0) * b7 + i11 + 0];
					double d20 = this.terrainNoise[((i9 + 1) * i8 + i10 + 1) * b7 + i11 + 0];
					double d22 = (this.terrainNoise[((i9 + 0) * i8 + i10 + 0) * b7 + i11 + 1] - d14) * d12;
					double d24 = (this.terrainNoise[((i9 + 0) * i8 + i10 + 1) * b7 + i11 + 1] - d16) * d12;
					double d26 = (this.terrainNoise[((i9 + 1) * i8 + i10 + 0) * b7 + i11 + 1] - d18) * d12;
					double d28 = (this.terrainNoise[((i9 + 1) * i8 + i10 + 1) * b7 + i11 + 1] - d20) * d12;

					for (int i30 = 0; i30 < 8; ++i30) {
						double d31 = 0.25D;
						double d33 = d14;
						double d35 = d16;
						double d37 = (d18 - d14) * d31;
						double d39 = (d20 - d16) * d31;

						for (int i41 = 0; i41 < 4; ++i41) {
							int i42 = i41 + i9 * 4 << 11 | 0 + i10 * 4 << 7 | i11 * 8 + i30;
							short s43 = 128;
							double d44 = 0.25D;
							double d46 = d33;
							double d48 = (d35 - d33) * d44;

							for (int i50 = 0; i50 < 4; ++i50) {
								int i51 = 0;
								if (i11 * 8 + i30 < b5) {
									i51 = Block.lavaStill.blockID;
								}

								if (d46 > 0.0D) {
									i51 = Block.netherrack.blockID;
								}

								b3[i42] = (byte) i51;
								i42 += s43;
								d46 += d48;
							}

							d33 += d37;
							d35 += d39;
						}

						d14 += d22;
						d16 += d24;
						d18 += d26;
						d20 += d28;
					}
				}
			}
		}

	}

	/*
	 * public void replaceBlocksForBiome(int i1, int i2, byte[] b3) { byte b4 = 64;
	 * double d5 = 8.0D / 256D; this.slowsandNoise =
	 * this.slowsandGravelNoiseGen.generateNoiseOctaves(this.slowsandNoise, i1 * 16,
	 * i2 * 16, 0, 16, 16, 1, d5, d5, 1.0D); this.gravelNoise =
	 * this.slowsandGravelNoiseGen.generateNoiseOctaves(this.gravelNoise, i1 * 16,
	 * 109, i2 * 16, 16, 1, 16, d5, 1.0D, d5); this.netherrackExclusivityNoise =
	 * this.netherrackExculsivityNoiseGen.generateNoiseOctaves(this.
	 * netherrackExclusivityNoise, i1 * 16, i2 * 16, 0, 16, 16, 1, d5 * 2.0D, d5 *
	 * 2.0D, d5 * 2.0D);
	 * 
	 * for(int i7 = 0; i7 < 16; ++i7) { for(int i8 = 0; i8 < 16; ++i8) { boolean z9
	 * = this.slowsandNoise[i7 + i8 * 16] + this.hellRNG.nextDouble() * 0.2D > 0.0D;
	 * boolean z10 = this.gravelNoise[i7 + i8 * 16] + this.hellRNG.nextDouble() *
	 * 0.2D > 0.0D; int i11 = (int)(this.netherrackExclusivityNoise[i7 + i8 * 16] /
	 * 3.0D + 3.0D + this.hellRNG.nextDouble() * 0.25D); int i12 = -1; byte b13 =
	 * (byte)Block.netherrack.blockID; byte b14 = (byte)Block.netherrack.blockID;
	 * 
	 * for(int i15 = 127; i15 >= 0; --i15) { int i16 = (i8 * 16 + i7) * 128 + i15;
	 * if(i15 >= 127 - this.hellRNG.nextInt(5)) { b3[i16] =
	 * (byte)Block.bedrock.blockID; } else if(i15 <= 0 + this.hellRNG.nextInt(5)) {
	 * b3[i16] = (byte)Block.bedrock.blockID; } else { byte b17 = b3[i16]; if(b17 ==
	 * 0) { i12 = -1; } else if(b17 == Block.netherrack.blockID) { if(i12 == -1) {
	 * if(i11 <= 0) { b13 = 0; b14 = (byte)Block.netherrack.blockID; } else if(i15
	 * >= b4 - 4 && i15 <= b4 + 1) { b13 = (byte)Block.netherrack.blockID; b14 =
	 * (byte)Block.netherrack.blockID; if(z10) { b13 = (byte)Block.gravel.blockID; }
	 * 
	 * if(z10) { b14 = (byte)Block.netherrack.blockID; }
	 * 
	 * if(z9) { b13 = (byte)Block.slowSand.blockID; }
	 * 
	 * if(z9) { b14 = (byte)Block.slowSand.blockID; } }
	 * 
	 * if(i15 < b4 && b13 == 0) { b13 = (byte)Block.lavaStill.blockID; }
	 * 
	 * i12 = i11; if(i15 >= b4 - 1) { b3[i16] = b13; } else { b3[i16] = b14; } }
	 * else if(i12 > 0) { --i12; b3[i16] = b14; } } } } } }
	 * 
	 * }
	 */
	public void replaceBlocksForBiome(int par1, int par2, byte[] par3ArrayOfByte, BiomeGenBase[] par4ArrayOfBiomeGenBase) {

		byte b0 = 32;
		double d0 = 0.03125D;
		slowsandNoise = slowsandGravelNoiseGen.generateNoiseOctaves(slowsandNoise, par1 * 16, par2 * 16, 0, 16, 16, 1, d0, d0, 1.0D);
		gravelNoise = slowsandGravelNoiseGen.generateNoiseOctaves(gravelNoise, par1 * 16, 109, par2 * 16, 16, 1, 16, d0, 1.0D, d0);
		netherrackExclusivityNoise = netherrackExculsivityNoiseGen.generateNoiseOctaves(netherrackExclusivityNoise,	par1 * 16, par2 * 16, 0, 16, 16, 1, d0 * 2.0D, d0 * 2.0D, d0 * 2.0D);

		for (int k = 0; k < 16; ++k) {
			for (int l = 0; l < 16; ++l) {
				BiomeGenBase biomegenbase = par4ArrayOfBiomeGenBase[l + k * 16];

				boolean flag = slowsandNoise[k + l * 16] + hellRNG.nextDouble() * 0.2D > 0.0D;
				boolean flag1 = gravelNoise[k + l * 16] + hellRNG.nextDouble() * 0.2D > 0.0D;
				int i1 = (int) (netherrackExclusivityNoise[k + l * 16] / 3.0D + 3.0D + hellRNG.nextDouble() * 0.25D);
				int j1 = -1;
				byte b1 = biomegenbase.getTopBlock(this.hellRNG);
				byte b2 = biomegenbase.getFillBlock(this.hellRNG);

				for (int k1 = 127; k1 >= 0; --k1) {
					int l1 = (l * 16 + k) * 128 + k1;

					if (k1 < 127 - hellRNG.nextInt(5) && k1 > 0 + hellRNG.nextInt(5)) {
						byte b3 = par3ArrayOfByte[l1];

						if (b3 == 0) {
							j1 = -1;
						} else if (b3 == Block.netherrack.blockID) {
							if (j1 == -1) {
								if (i1 <= 0) {
									b1 = 0;
									b2 = (byte) Block.netherrack.blockID;
								} else if (k1 >= b0 - 4 && k1 <= b0 + 1) {
									b1 = (byte) biomegenbase.getTopBlock(this.hellRNG);
									b2 = (byte) biomegenbase.getFillBlock(this.hellRNG);

									if (flag1) {
										b1 = (byte) Block.gravel.blockID;
									}

									if (flag1) {
										b2 = (byte) Block.netherrack.blockID;
									}

									if (flag) {
										b1 = (byte) Block.slowSand.blockID;
									}

									if (flag) {
										b2 = (byte) Block.slowSand.blockID;
									}
								}

								if (k1 < b0 && b1 == 0) {
									b1 = (byte) Block.lavaStill.blockID;
								}

								j1 = i1;

								if (k1 >= b0 - 1) {
									par3ArrayOfByte[l1] = b1;
								} else {
									par3ArrayOfByte[l1] = b2;
								}
							} else if (j1 > 0) {
								--j1;
								par3ArrayOfByte[l1] = b2;
							}
						}
					} else {
						par3ArrayOfByte[l1] = (byte) Block.bedrock.blockID;
					}
				}
			}
		}
	}

	public Chunk loadChunk(int i1, int i2) {
		return this.provideChunk(i1, i2);
	}

	public Chunk provideChunk(int i1, int i2) {
		this.hellRNG.setSeed((long) i1 * 341873128712L + (long) i2 * 132897987541L);
		byte[] b3 = new byte[32768];
		this.generateNetherTerrain(i1, i2, b3);
		
		this.biomesForGeneration = worldObj.getWorldChunkManager().loadBlockGeneratorData(biomesForGeneration, i1 * 16, i2 * 16, 16, 16);
		
		this.replaceBlocksForBiome(i1, i2, b3, this.biomesForGeneration);
		
		this.netherCaveGenerator.generate(this, this.worldObj, i1, i2, b3);
		//this.genNetherBridge.generate(this, this.worldObj, i1, i2, b3);
		
		Chunk chunk4 = new Chunk(this.worldObj, b3, i1, i2);
		
		this.featureProvider.getNearestFeatures(i1, i2, chunk4);
		
		BiomeGenBase[] biomeGenBase5 = this.worldObj.getWorldChunkManager().loadBlockGeneratorData((BiomeGenBase[]) null, i1 * 16, i2 * 16, 16, 16);
		byte[] b6 = chunk4.getBiomeArray();

		for (int i7 = 0; i7 < b6.length; ++i7) {
			b6[i7] = (byte) biomeGenBase5[i7].biomeID;
		}
		
		return chunk4;
	}

	private double[] initializeNoiseField(double[] d1, int i2, int i3, int i4, int i5, int i6, int i7) {
		if (d1 == null) {
			d1 = new double[i5 * i6 * i7];
		}

		double d8 = 684.412D;
		double d10 = 2053.236D;
		this.noiseData4 = this.netherNoiseGen6.generateNoiseOctaves(this.noiseData4, i2, i3, i4, i5, 1, i7, 1.0D, 0.0D,
				1.0D);
		this.noiseData5 = this.netherNoiseGen7.generateNoiseOctaves(this.noiseData5, i2, i3, i4, i5, 1, i7, 100.0D,
				0.0D, 100.0D);
		this.noiseData1 = this.netherNoiseGen3.generateNoiseOctaves(this.noiseData1, i2, i3, i4, i5, i6, i7, d8 / 80.0D,
				d10 / 60.0D, d8 / 80.0D);
		this.noiseData2 = this.netherNoiseGen1.generateNoiseOctaves(this.noiseData2, i2, i3, i4, i5, i6, i7, d8, d10,
				d8);
		this.noiseData3 = this.netherNoiseGen2.generateNoiseOctaves(this.noiseData3, i2, i3, i4, i5, i6, i7, d8, d10,
				d8);
		int i12 = 0;
		int i13 = 0;
		double[] d14 = new double[i6];

		int i15;
		for (i15 = 0; i15 < i6; ++i15) {
			d14[i15] = Math.cos((double) i15 * Math.PI * 6.0D / (double) i6) * 2.0D;
			double d16 = (double) i15;
			if (i15 > i6 / 2) {
				d16 = (double) (i6 - 1 - i15);
			}

			if (d16 < 4.0D) {
				d16 = 4.0D - d16;
				d14[i15] -= d16 * d16 * d16 * 10.0D;
			}
		}

		for (i15 = 0; i15 < i5; ++i15) {
			for (int i36 = 0; i36 < i7; ++i36) {
				double d17 = (this.noiseData4[i13] + 256.0D) / 512.0D;
				if (d17 > 1.0D) {
					d17 = 1.0D;
				}

				double d19 = 0.0D;
				double d21 = this.noiseData5[i13] / 8000.0D;
				if (d21 < 0.0D) {
					d21 = -d21;
				}

				d21 = d21 * 3.0D - 3.0D;
				if (d21 < 0.0D) {
					d21 /= 2.0D;
					if (d21 < -1.0D) {
						d21 = -1.0D;
					}

					d21 /= 1.4D;
					d21 /= 2.0D;
					d17 = 0.0D;
				} else {
					if (d21 > 1.0D) {
						d21 = 1.0D;
					}

					d21 /= 6.0D;
				}

				d17 += 0.5D;
				d21 = d21 * (double) i6 / 16.0D;
				++i13;

				for (int i23 = 0; i23 < i6; ++i23) {
					double d24 = 0.0D;
					double d26 = d14[i23];
					double d28 = this.noiseData2[i12] / 512.0D;
					double d30 = this.noiseData3[i12] / 512.0D;
					double d32 = (this.noiseData1[i12] / 10.0D + 1.0D) / 2.0D;
					if (d32 < 0.0D) {
						d24 = d28;
					} else if (d32 > 1.0D) {
						d24 = d30;
					} else {
						d24 = d28 + (d30 - d28) * d32;
					}

					d24 -= d26;
					double d34;
					if (i23 > i6 - 4) {
						d34 = (double) ((float) (i23 - (i6 - 4)) / 3.0F);
						d24 = d24 * (1.0D - d34) + -10.0D * d34;
					}

					if ((double) i23 < d19) {
						d34 = (d19 - (double) i23) / 4.0D;
						if (d34 < 0.0D) {
							d34 = 0.0D;
						}

						if (d34 > 1.0D) {
							d34 = 1.0D;
						}

						d24 = d24 * (1.0D - d34) + -10.0D * d34;
					}

					d1[i12] = d24;
					++i12;
				}
			}
		}

		return d1;
	}

	public boolean chunkExists(int i1, int i2) {
		return true;
	}
	
	protected void genStandardOre(int attempts, WorldGenerator worldGenerator2, int y1, int y2, int x0, int z0) {
		for(int i = 0; i < attempts; ++i) {
			int x = x0 + this.hellRNG.nextInt(16);
			int y = this.hellRNG.nextInt(y2 - y1) + y1;
			int z = z0 + this.hellRNG.nextInt(16);
			worldGenerator2.generate(this.worldObj, this.hellRNG, x, y, z);
		}

	}

	public void populate(IChunkProvider iChunkProvider1, int xChunk, int zChunk) {
		BlockSand.fallInstantly = true;
		int x0 = xChunk * 16;
		int z0 = zChunk * 16;
		BiomeGenBase biomeGen = this.worldObj.getBiomeGenForCoords(x0 + 16, z0 + 16);

		this.hellRNG.setSeed(this.worldObj.getSeed());
		long j7 = this.hellRNG.nextLong() / 2L * 2L + 1L;
		long j9 = this.hellRNG.nextLong() / 2L * 2L + 1L;
		this.hellRNG.setSeed((long) xChunk * j7 + (long) zChunk * j9 ^ this.worldObj.getSeed());
		boolean hadCustomFeat = false;

		//this.genNetherBridge.generateStructuresInChunk(this.worldObj, this.hellRNG, xChunk, zChunk);
		hadCustomFeat = this.featureProvider.populateFeatures(worldObj, hellRNG, xChunk, zChunk);		
		
		// Nether ores
		//this.genStandardOre(16, new WorldGenMinableNether(Block.oreQuartz.blockID, 24), 10, 117, x0, z0);
		//this.genStandardOre(4, new WorldGenMinableNether(Block.oreNetherDiamond.blockID, 8), 10, 117, x0, z0);
		//this.genStandardOre(10, new WorldGenMinableNether(Block.oreNetherGold.blockID, 16), 10, 117, x0, z0);
		
		int i, x, y, z, attempts;

		for (i = 0; i < 8; ++i) {
			x = x0 + this.hellRNG.nextInt(16) + 8;
			y = this.hellRNG.nextInt(120) + 4;
			z = z0 + this.hellRNG.nextInt(16) + 8;
			(new WorldGenHellLava(Block.lavaMoving.blockID)).generate(this.worldObj, this.hellRNG, x, y, z);
		}

		attempts = this.hellRNG.nextInt(this.hellRNG.nextInt(10) + 1) + 1;

		for (i = 0; i < attempts; ++i) {
			x = x0 + this.hellRNG.nextInt(16) + 8;
			y = this.hellRNG.nextInt(120) + 4;
			z = z0 + this.hellRNG.nextInt(16) + 8;
			(new WorldGenFire()).generate(this.worldObj, this.hellRNG, x, y, z);
		}

		attempts = this.hellRNG.nextInt(this.hellRNG.nextInt(10) + 1);

		for (i = 0; i < attempts; ++i) {
			x = x0 + this.hellRNG.nextInt(16) + 8;
			y = this.hellRNG.nextInt(120) + 4;
			z = z0 + this.hellRNG.nextInt(16) + 8;
			(new WorldGenGlowStone1()).generate(this.worldObj, this.hellRNG, x, y, z);
		}

		for (i = 0; i < 10; ++i) {
			x = x0 + this.hellRNG.nextInt(16) + 8;
			y = this.hellRNG.nextInt(128);
			z = z0 + this.hellRNG.nextInt(16) + 8;
			(new WorldGenGlowStone2()).generate(this.worldObj, this.hellRNG, x, y, z);
		}

		if (this.hellRNG.nextInt(1) == 0) {
			x = x0 + this.hellRNG.nextInt(16) + 8;
			y = this.hellRNG.nextInt(128);
			z = z0 + this.hellRNG.nextInt(16) + 8;
			(new WorldGenFlowers(Block.mushroomBrown.blockID)).generate(this.worldObj, this.hellRNG, x, y, x);
		}

		if (this.hellRNG.nextInt(1) == 0) {
			x = x0 + this.hellRNG.nextInt(16) + 8;
			y = this.hellRNG.nextInt(128);
			z = z0 + this.hellRNG.nextInt(16) + 8;
			(new WorldGenFlowers(Block.mushroomRed.blockID)).generate(this.worldObj, this.hellRNG, x, y, z);
		}

		biomeGen.decorate(this.worldObj, this.hellRNG, x0, z0, hadCustomFeat);

		BlockSand.fallInstantly = false;
	}

	public boolean saveChunks(boolean z1, IProgressUpdate iProgressUpdate2) {
		return true;
	}

	public boolean unload100OldestChunks() {
		return false;
	}

	public boolean canSave() {
		return true;
	}

	public String makeString() {
		return "HellRandomLevelSource";
	}

	public List<SpawnListEntry> getPossibleCreatures(EnumCreatureType enumCreatureType1, int i2, int i3, int i4) {
		/*if (enumCreatureType1 == EnumCreatureType.monster && this.genNetherBridge.hasStructureAt(i2, i3, i4)) {
			return this.genNetherBridge.getSpawnList();
		} else*/ {
			BiomeGenBase biomeGenBase5 = this.worldObj.getBiomeGenForCoords(i2, i4);
			return biomeGenBase5 == null ? null : biomeGenBase5.getSpawnableList(enumCreatureType1);
		}
	}

	public ChunkPosition findClosestStructure(World world1, String string2, int i3, int i4, int i5) {
		return null;
	}
	
	public Chunk justGenerate(int posX, int posZ) {
		return this.provideChunk(posX, posZ);
	}
}
