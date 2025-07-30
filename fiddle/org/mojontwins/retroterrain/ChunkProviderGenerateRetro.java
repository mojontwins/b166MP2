package org.mojontwins.retroterrain;

import java.util.List;
import java.util.Random;

import net.minecraft.src.BiomeGenBase;
import net.minecraft.src.Block;
import net.minecraft.src.BlockSand;
import net.minecraft.src.Chunk;
import net.minecraft.src.ChunkCoordIntPair;
import net.minecraft.src.ChunkPosition;
import net.minecraft.src.EnumCreatureType;
import net.minecraft.src.IChunkProvider;
import net.minecraft.src.IProgressUpdate;
import net.minecraft.src.MapGenBase;
import net.minecraft.src.MapGenCaves;
import net.minecraft.src.MapGenMineshaft;
import net.minecraft.src.MapGenRavine;
import net.minecraft.src.MapGenStronghold;
import net.minecraft.src.MapGenVillage;
import net.minecraft.src.SpawnerAnimals;
import net.minecraft.src.World;
import net.minecraft.src.WorldChunkManager;
import net.minecraft.src.WorldGenDungeons;
import net.minecraft.src.WorldGenLakes;

public class ChunkProviderGenerateRetro implements IChunkProvider {
	protected Random rand;
	private NoiseGeneratorOctavesBeta minLimitNoise;
	private NoiseGeneratorOctavesBeta maxLimitNoise;
	private NoiseGeneratorOctavesBeta mainNoise;
	protected NoiseGeneratorOctavesBeta noiseGenSandOrGravel;
	protected NoiseGeneratorOctavesBeta noiseStone;
	private NoiseGeneratorOctavesBeta scaleNoise;
	private NoiseGeneratorOctavesBeta depthNoise;
	public NoiseGeneratorOctavesBeta mobSpawnerNoise;
	protected World worldObj;
	protected final boolean mapFeaturesEnabled;
	protected double[] terrainNoise;
	protected double[] sandNoise = new double[256];
	protected double[] gravelNoise = new double[256];
	protected double[] stoneNoise = new double[256];
	private MapGenBase caveGenerator = new MapGenCaves();
	private MapGenStronghold strongholdGenerator = new MapGenStronghold();
	private MapGenVillage villageGenerator = new MapGenVillage(0);
	private MapGenMineshaft mineshaftGenerator = new MapGenMineshaft();
	private MapGenBase ravineGenerator = new MapGenRavine();
	private BiomeGenBase[] biomesForGeneration;
	protected double[] mainArray;
	protected double[] minLimitArray;
	protected double[] maxLimitArray;
	protected double[] scaleArray;
	protected double[] depthArray;
	float[] distanceArray;
	int[][] unusedIntArray32x32 = new int[32][32];
	WorldChunkManagerBeta chunkManagerBeta = null;

	public ChunkProviderGenerateRetro(World world1, long j2, boolean z4) {
		this.worldObj = world1;
		this.mapFeaturesEnabled = z4;
		this.rand = new Random(j2);
		
		this.minLimitNoise = new NoiseGeneratorOctavesBeta(this.rand, 16);
		this.maxLimitNoise = new NoiseGeneratorOctavesBeta(this.rand, 16);
		this.mainNoise = new NoiseGeneratorOctavesBeta(this.rand, 8);
		
		this.noiseGenSandOrGravel = new NoiseGeneratorOctavesBeta(this.rand, 4);
		this.noiseStone = new NoiseGeneratorOctavesBeta(this.rand, 4);
		
		this.scaleNoise = new NoiseGeneratorOctavesBeta(this.rand, 10);
		this.depthNoise = new NoiseGeneratorOctavesBeta(this.rand, 16);
		
		this.mobSpawnerNoise = new NoiseGeneratorOctavesBeta(this.rand, 8);
		
		this.chunkManagerBeta = (WorldChunkManagerBeta)(this.worldObj.getWorldChunkManager());
		
	}

	public void generateTerrain(int chunkX, int chunkZ, byte[] blocks, BiomeGenBase[] biomeGenBase4, double[] d5) {
		double noiseScale = 0.125D;
		double scalingFactor = 0.25D;
		double densityVariationSpeed = 0.25D;
		int heightShift = 128;

		byte quadrantSize = 4;
		int sectionHeight = 128 / 8;
		int seaLevel = 64;
		int xSize = quadrantSize + 1;
		int ySize = 128 / 8 + 1;
		int zSize = quadrantSize + 1;
		this.terrainNoise = this.initializeNoiseField(this.terrainNoise, chunkX * quadrantSize, 0, chunkZ * quadrantSize, xSize, ySize, zSize);

		for(int xSection = 0; xSection < quadrantSize; ++xSection) {
			for(int zSection = 0; zSection < quadrantSize; ++zSection) {
				for(int ySection = 0; ySection < sectionHeight; ++ySection) {
					
					double densityMinXMinYMinZ = this.terrainNoise[((xSection + 0) * zSize + zSection + 0) * ySize + ySection + 0];
					double densityMinXMinYMaxZ = this.terrainNoise[((xSection + 0) * zSize + zSection + 1) * ySize + ySection + 0];
					double densityMaxXMinYMinZ = this.terrainNoise[((xSection + 1) * zSize + zSection + 0) * ySize + ySection + 0];
					double densityMaxXMinYMaxZ = this.terrainNoise[((xSection + 1) * zSize + zSection + 1) * ySize + ySection + 0];
					double yLerpAmountMinXMinZ = (this.terrainNoise[((xSection + 0) * zSize + zSection + 0) * ySize + ySection + 1] - densityMinXMinYMinZ) * noiseScale;
					double yLerpAmountMinXMaxZ = (this.terrainNoise[((xSection + 0) * zSize + zSection + 1) * ySize + ySection + 1] - densityMinXMinYMaxZ) * noiseScale;
					double yLerpAmountMaxXMinZ = (this.terrainNoise[((xSection + 1) * zSize + zSection + 0) * ySize + ySection + 1] - densityMaxXMinYMinZ) * noiseScale;
					double yLerpAmountMaxXMaxZ = (this.terrainNoise[((xSection + 1) * zSize + zSection + 1) * ySize + ySection + 1] - densityMaxXMinYMaxZ) * noiseScale;

					for(int y = 0; y < 8; ++y) {
						
						double curDensityMinXMinYMinZ = densityMinXMinYMinZ;
						double curDensityMinXMinYMaxZ = densityMinXMinYMaxZ;
						double xLerpAmountMinZ = (densityMaxXMinYMinZ - densityMinXMinYMinZ) * scalingFactor;
						double xLerpAmountMaxZ = (densityMaxXMinYMaxZ - densityMinXMinYMaxZ) * scalingFactor;

						for(int x = 0; x < 4; ++x) {
							
							int indexInBlockArray = x + xSection * 4 << 11 | 0 + zSection * 4 << 7 | ySection * 8 + y;
							
							double densityIncrement = (curDensityMinXMinYMaxZ - curDensityMinXMinYMinZ) * densityVariationSpeed;
							double density = curDensityMinXMinYMinZ - densityIncrement;

							for(int z = 0; z < 4; ++z) {
								double temperature = d5[(xSection * 4 + x) * 16 + zSection * 4 + z];

								int blockID = 0;
								
								if(ySection * 8 + y < seaLevel) {
									if(temperature < 0.5D && ySection * 8 + y >= seaLevel - 1) {
										blockID = Block.ice.blockID;
									} else {
										blockID = Block.waterStill.blockID;
									}
								}

								if(density > 0.0D) {
									blockID = Block.stone.blockID;
								}

								blocks[indexInBlockArray] = (byte)blockID;
								indexInBlockArray += heightShift;
								density += densityIncrement;
							}

							curDensityMinXMinYMinZ += xLerpAmountMinZ;
							curDensityMinXMinYMaxZ += xLerpAmountMaxZ;
						}

						densityMinXMinYMinZ += yLerpAmountMinXMinZ;
						densityMinXMinYMaxZ += yLerpAmountMinXMaxZ;
						densityMaxXMinYMinZ += yLerpAmountMaxXMinZ;
						densityMaxXMinYMaxZ += yLerpAmountMaxXMaxZ;
					}
				}
			}
		}

	}

	public void replaceBlocksForBiome(int chunkX, int chunkZ, byte[] blocks, BiomeGenBase[] biomes) {
		byte seaLevel = 64;
		double d6 = 8.0D / 256D;

		this.sandNoise = this.noiseGenSandOrGravel.generateNoiseOctaves(this.sandNoise, (double)(chunkX * 16), (double)(chunkZ * 16), 0D, 16, 16, 1, d6, d6, 1.0D);
		this.gravelNoise = this.noiseGenSandOrGravel.generateNoiseOctaves(this.gravelNoise, (double)(chunkX * 16), 109.0134D, (double)(chunkZ * 16), 16, 1, 16, d6, 1.0D, d6);
		this.stoneNoise = this.noiseStone.generateNoiseOctaves(this.stoneNoise, (double)(chunkX * 16), (double)(chunkZ * 16), 0D, 16, 16, 1, d6 * 2.0D, d6 * 2.0D, d6 * 2.0D);

		for(int z = 0; z < 16; ++z) {
			for(int x = 0; x < 16; ++x) {
				int genIndex = z + x * 16;

				BiomeGenBase biomeGen = biomes[genIndex];

				boolean generateSand = this.sandNoise[genIndex] + this.rand.nextDouble() * 0.2D > 0.0D;
				boolean generateGravel = this.gravelNoise[genIndex] + this.rand.nextDouble() * 0.2D > 3.0D;
				
				int height = (int)(this.stoneNoise[genIndex] / 3.0D + 3.0D + this.rand.nextDouble() * 0.25D);
				int stoneHeight = -1;
				
				byte topBlock = biomeGen.topBlock;
				byte fillerBlock = biomeGen.fillerBlock;

				for(int y = 127; y >= 0; --y) {
					int index = (x * 16 + z) * 128 + y;

					if(y <= 0 + this.rand.nextInt(5)) {
						blocks[index] = (byte)Block.bedrock.blockID;
					} else {
						byte blockID = blocks[index];
						if(blockID == 0) {
							stoneHeight = -1;
						} else if(blockID == Block.stone.blockID) {
							if(stoneHeight == -1) {
								if(height <= 0) {
									topBlock = 0;
									fillerBlock = (byte)Block.stone.blockID;
								} else if(y >= seaLevel - 4 && y <= seaLevel + 1) {
									topBlock = biomeGen.topBlock;
									fillerBlock = biomeGen.fillerBlock;

									if(generateGravel) {
										topBlock = 0;
										fillerBlock = (byte)Block.gravel.blockID;
									}

									if(generateSand) {
										topBlock = (byte)Block.sand.blockID;
										fillerBlock = (byte)Block.sand.blockID;
									}
								}

								if(y < seaLevel && topBlock == 0) {
									topBlock = (byte)Block.waterStill.blockID;
								}

								stoneHeight = height;
								if(y >= seaLevel - 1) {
									blocks[index] = topBlock;
								} else {
									blocks[index] = fillerBlock;
								}
							} else if(stoneHeight > 0) {
								--stoneHeight;
								blocks[index] = fillerBlock;
								if(stoneHeight == 0 && fillerBlock == Block.sand.blockID) {
									stoneHeight = this.rand.nextInt(4);
									fillerBlock = (byte)Block.sandStone.blockID;
								}
							}
						}
					}
				}
			}
		}

	}

	public Chunk loadChunk(int i1, int i2) {
		return this.provideChunk(i1, i2);
	}

	public Chunk provideChunk(int chunkX, int chunkZ) {
		this.rand.setSeed((long)chunkX * 341873128712L + (long)chunkZ * 132897987541L);

		byte[] blocks = new byte[16 * 128 * 16];
		
		this.biomesForGeneration = this.worldObj.getWorldChunkManager().loadBlockGeneratorData(this.biomesForGeneration, chunkX * 16, chunkZ * 16, 16, 16);
		double[] d5 = this.chunkManagerBeta.temperature;
		
		this.generateTerrain(chunkX, chunkZ, blocks, this.biomesForGeneration, d5);
		this.replaceBlocksForBiome(chunkX, chunkZ, blocks, this.biomesForGeneration);
		this.caveGenerator.generate(this, this.worldObj, chunkX, chunkZ, blocks);
		this.ravineGenerator.generate(this, this.worldObj, chunkX, chunkZ, blocks);
		if(this.mapFeaturesEnabled) {
			this.mineshaftGenerator.generate(this, this.worldObj, chunkX, chunkZ, blocks);
			this.villageGenerator.generate(this, this.worldObj, chunkX, chunkZ, blocks);
			this.strongholdGenerator.generate(this, this.worldObj, chunkX, chunkZ, blocks);
		}

		Chunk chunk4 = new Chunk(this.worldObj, blocks, chunkX, chunkZ);
		byte[] biomes = chunk4.getBiomeArray();
		for(int i6 = 0; i6 < biomes.length; ++i6) {
			biomes[i6] = (byte)this.biomesForGeneration[i6].biomeID;
		}
		
		chunk4.generateSkylightMap();
		return chunk4;
	}

	protected double[] initializeNoiseField(double[] densityMapArray, int x, int y, int z, int xSize, int ySize, int zSize) {
		if(densityMapArray == null) {
			densityMapArray = new double[xSize * ySize * zSize];
		}

		double scaleXZ = 684.412D;
		double scaleY = 684.412D;
				
		double[] temperatureArray = this.chunkManagerBeta.temperature;
		double[] humidityArray = this.chunkManagerBeta.humidity;

		this.scaleArray = this.scaleNoise.generateNoiseOctaves(this.scaleArray, x, z, xSize, zSize, 1.121D, 1.121D, 0.5D);
		this.depthArray = this.depthNoise.generateNoiseOctaves(this.depthArray, x, z, xSize, zSize, 200.0D, 200.0D, 0.5D);
		this.mainArray = this.mainNoise.generateNoiseOctaves(this.mainArray, (double)x, (double)y, (double)z, xSize, ySize, zSize, scaleXZ / 80.0D, scaleY / 160.0D, scaleXZ / 80.0D);
		this.minLimitArray = this.minLimitNoise.generateNoiseOctaves(this.minLimitArray, (double)x, (double)y, (double)z, xSize, ySize, zSize, scaleXZ, scaleY, scaleXZ);
		this.maxLimitArray = this.maxLimitNoise.generateNoiseOctaves(this.maxLimitArray, (double)x, (double)y, (double)z, xSize, ySize, zSize, scaleXZ, scaleY, scaleXZ);
		
		int mainIndex = 0;
		int depthScaleIndex = 0;
		int quadrantSize = 16 / xSize;

		for(int dx = 0; dx < xSize; ++dx) {
			int thX = dx * quadrantSize + quadrantSize / 2;
			
			for(int dz = 0; dz < zSize; ++dz) {
				int thZ = dz * quadrantSize + quadrantSize / 2;
				
				// In beta, scale array is affected by biome t/h

				double t = temperatureArray[thX * 16 + thZ];
				double h = humidityArray[thX * 16 + thZ] * t;

				double hInv = 1.0D - h;
				hInv *= hInv;
				hInv *= hInv;
				hInv = 1.0D - hInv;
				double scale = (this.scaleArray[depthScaleIndex] + 256.0D) / 512.0D;
				scale *= hInv;
				if(scale > 1.0D) {
					scale = 1.0D;
				}
				
				//
				
				double depth = this.depthArray[depthScaleIndex] / 8000.0D;
				if(depth < 0.0D) {
					depth = -depth * 0.3D;
				}

				depth = depth * 3.0D - 2.0D;
				if(depth < 0.0D) {
					depth /= 2.0D;
					if(depth < -1.0D) {
						depth = -1.0D;
					}

					depth /= 1.4D;
					depth /= 2.0D;
					scale = 0.0D;
				} else {
					if(depth > 1.0D) {
						depth = 1.0D;
					}

					depth /= 8.0D;
				}

				if(scale < 0.0D) {
					scale = 0.0D;
				}

				scale += 0.5D;

				depth = depth * (double)ySize / 16.0D;
				double offsetY = (double)ySize / 2.0D + depth * 4.0D;
				
				++depthScaleIndex;

				for(int dy = 0; dy < ySize; ++dy) {
					double density = 0.0D;

					double densityOffset = ((double)dy - offsetY) * 12.0D / scale;
					
					if(densityOffset < 0.0D) {
						densityOffset *= 4.0D;
					}

					double minDensity = this.minLimitArray[mainIndex] / 512.0D;
					double maxDensity = this.maxLimitArray[mainIndex] / 512.0D;
					double mainDensity = (this.mainArray[mainIndex] / 10.0D + 1.0D) / 2.0D;
					if(mainDensity < 0.0D) {
						density = minDensity;
					} else if(mainDensity > 1.0D) {
						density = maxDensity;
					} else {
						density = minDensity + (maxDensity - minDensity) * mainDensity;
					}

					density -= densityOffset;
					if(dy > ySize - 4) {
						double d40 = (double)((float)(dy - (ySize - 4)) / 3.0F);
						density = density * (1.0D - d40) + -10.0D * d40;
					}

					densityMapArray[mainIndex] = density;
					++mainIndex;
				}
			}
		}

		return densityMapArray;
	}

	public boolean chunkExists(int i1, int i2) {
		return true;
	}

	public void populate(IChunkProvider iChunkProvider1, int i2, int i3) {
		BlockSand.fallInstantly = true;
		int i4 = i2 * 16;
		int i5 = i3 * 16;
		BiomeGenBase biomeGenBase6 = this.worldObj.getWorldChunkManager().getBiomeGenAt(i4 + 16, i5 + 16);
		this.rand.setSeed(this.worldObj.getSeed());
		long j7 = this.rand.nextLong() / 2L * 2L + 1L;
		long j9 = this.rand.nextLong() / 2L * 2L + 1L;
		this.rand.setSeed((long)i2 * j7 + (long)i3 * j9 ^ this.worldObj.getSeed());
		boolean z11 = false;
		if(this.mapFeaturesEnabled) {
			this.mineshaftGenerator.generateStructuresInChunk(this.worldObj, this.rand, i2, i3);
			z11 = this.villageGenerator.generateStructuresInChunk(this.worldObj, this.rand, i2, i3);
			this.strongholdGenerator.generateStructuresInChunk(this.worldObj, this.rand, i2, i3);
		}

		int i12;
		int i13;
		int i14;
		if(!z11 && this.rand.nextInt(4) == 0) {
			i12 = i4 + this.rand.nextInt(16) + 8;
			i13 = this.rand.nextInt(128);
			i14 = i5 + this.rand.nextInt(16) + 8;
			(new WorldGenLakes(Block.waterStill.blockID)).generate(this.worldObj, this.rand, i12, i13, i14);
		}

		if(!z11 && this.rand.nextInt(8) == 0) {
			i12 = i4 + this.rand.nextInt(16) + 8;
			i13 = this.rand.nextInt(this.rand.nextInt(128 - 8) + 8);
			i14 = i5 + this.rand.nextInt(16) + 8;
			if(i13 < 63 || this.rand.nextInt(10) == 0) {
				(new WorldGenLakes(Block.lavaStill.blockID)).generate(this.worldObj, this.rand, i12, i13, i14);
			}
		}

		for(i12 = 0; i12 < 8; ++i12) {
			i13 = i4 + this.rand.nextInt(16) + 8;
			i14 = this.rand.nextInt(128);
			int i15 = i5 + this.rand.nextInt(16) + 8;
			if((new WorldGenDungeons()).generate(this.worldObj, this.rand, i13, i14, i15)) {
				;
			}
		}

		biomeGenBase6.decorate(this.worldObj, this.rand, i4, i5);
		SpawnerAnimals.performWorldGenSpawning(this.worldObj, biomeGenBase6, i4 + 8, i5 + 8, 16, 16, this.rand);
		i4 += 8;
		i5 += 8;

		for(i12 = 0; i12 < 16; ++i12) {
			for(i13 = 0; i13 < 16; ++i13) {
				i14 = this.worldObj.getPrecipitationHeight(i4 + i12, i5 + i13);
				if(this.worldObj.isBlockHydratedDirectly(i12 + i4, i14 - 1, i13 + i5)) {
					this.worldObj.setBlockWithNotify(i12 + i4, i14 - 1, i13 + i5, Block.ice.blockID);
				}

				if(this.worldObj.canSnowAt(i12 + i4, i14, i13 + i5)) {
					this.worldObj.setBlockWithNotify(i12 + i4, i14, i13 + i5, Block.snow.blockID);
				}
			}
		}

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
		return "RandomLevelSource";
	}

	public List getPossibleCreatures(EnumCreatureType enumCreatureType1, int i2, int i3, int i4) {
		BiomeGenBase biomeGenBase5 = this.worldObj.getBiomeGenForCoords(i2, i4);
		return biomeGenBase5 == null ? null : biomeGenBase5.getSpawnableList(enumCreatureType1);
	}

	public ChunkPosition findClosestStructure(World world1, String string2, int i3, int i4, int i5) {
		return "Stronghold".equals(string2) && this.strongholdGenerator != null ? this.strongholdGenerator.getNearestInstance(world1, i3, i4, i5) : null;
	}

}