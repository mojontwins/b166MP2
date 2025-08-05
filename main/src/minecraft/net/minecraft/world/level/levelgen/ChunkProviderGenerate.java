package net.minecraft.world.level.levelgen;

import java.util.List;
import java.util.Random;

import com.mojontwins.minecraft.feature.FeatureProvider;

import net.minecraft.src.MathHelper;
import net.minecraft.world.entity.EnumCreatureType;
import net.minecraft.world.level.ChunkPosition;
import net.minecraft.world.level.SpawnListEntry;
import net.minecraft.world.level.SpawnerAnimals;
import net.minecraft.world.level.World;
import net.minecraft.world.level.biome.BiomeGenBase;
import net.minecraft.world.level.chunk.Chunk;
import net.minecraft.world.level.chunk.IChunkProvider;
import net.minecraft.world.level.chunk.storage.IProgressUpdate;
import net.minecraft.world.level.levelgen.feature.WorldGenDungeons;
import net.minecraft.world.level.levelgen.feature.WorldGenDungeonsWater;
import net.minecraft.world.level.levelgen.feature.WorldGenLakes;
import net.minecraft.world.level.levelgen.synth.NoiseGeneratorOctaves;
import net.minecraft.world.level.tile.Block;
import net.minecraft.world.level.tile.BlockSand;

public class ChunkProviderGenerate implements IChunkProvider {
	protected Random rand;
	protected NoiseGeneratorOctaves minLimitNoise;
	protected NoiseGeneratorOctaves maxLimitNoise;
	protected NoiseGeneratorOctaves mainNoise;
	protected NoiseGeneratorOctaves noiseStone;
	protected NoiseGeneratorOctaves scaleNoise;
	protected NoiseGeneratorOctaves depthNoise;
	
	protected World worldObj;
	protected final boolean mapFeaturesEnabled;
	
	protected double[] terrainNoise;
	protected double[] stoneNoise = new double[256];
	
	protected MapGenBase caveGenerator = new MapGenCaves();
	protected MapGenBase ravineGenerator = new MapGenRavine();
	
	// Multi-chunk features	
	public FeatureProvider featureProvider;
	
	protected BiomeGenBase[] biomesForGeneration;
	
	protected double[] mainArray;
	protected double[] minLimitArray;
	protected double[] maxLimitArray;
	protected double[] scaleArray;
	protected double[] depthArray;

	float[] distanceArray;
	int[][] unusedArray = new int[32][32];
	protected boolean isOcean;

	public ChunkProviderGenerate(World world1, long j2, boolean z4) {
		this.worldObj = world1;
		this.mapFeaturesEnabled = z4;
		this.rand = new Random(j2);
		this.minLimitNoise = new NoiseGeneratorOctaves(this.rand, 16);
		this.maxLimitNoise = new NoiseGeneratorOctaves(this.rand, 16);
		this.mainNoise = new NoiseGeneratorOctaves(this.rand, 8);
		this.noiseStone = new NoiseGeneratorOctaves(this.rand, 4);
		this.scaleNoise = new NoiseGeneratorOctaves(this.rand, 10);
		this.depthNoise = new NoiseGeneratorOctaves(this.rand, 16);
		
		this.featureProvider = new FeatureProvider(worldObj, this);
	}

	public void generateTerrain(int chunkX, int chunkZ, byte[] blocks) {
		int quadrantSize = 4;
		int seaLevel = (this instanceof ChunkProviderSky) ? -1 : 64;
		int cellSize = quadrantSize + 1;
		int columnSize = 17;
		int biomeQuadrantSize = quadrantSize + 1;
		
		double scalingFactor = 0.25D;
		this.biomesForGeneration = this.worldObj.getWorldChunkManager().getBiomesForGeneration(this.biomesForGeneration, chunkX * 4 - 2, chunkZ * 4 - 2, biomeQuadrantSize + 5, biomeQuadrantSize + 5);
		this.terrainNoise = this.initializeNoiseField(this.terrainNoise, chunkX * quadrantSize, 0, chunkZ * quadrantSize, cellSize, columnSize, cellSize);
		
		this.isOcean = true;

		// Split in 4x16x4 sections
		for(int xSection = 0; xSection < quadrantSize; ++xSection) {
			for(int zSection = 0; zSection < quadrantSize; ++zSection) {		
				for(int ySection = 0; ySection < 16; ++ySection) {

					double densityMinXMinYMinZ = this.terrainNoise[((xSection + 0) * cellSize + zSection + 0) * columnSize + ySection + 0];
					double densityMinXMinYMaxZ = this.terrainNoise[((xSection + 0) * cellSize + zSection + 1) * columnSize + ySection + 0];
					double densityMaxXMinYMinZ = this.terrainNoise[((xSection + 1) * cellSize + zSection + 0) * columnSize + ySection + 0];
					double densityMaxXMinYMaxZ = this.terrainNoise[((xSection + 1) * cellSize + zSection + 1) * columnSize + ySection + 0];
					double yLerpAmountMinXMinZ = (this.terrainNoise[((xSection + 0) * cellSize + zSection + 0) * columnSize + ySection + 1] - densityMinXMinYMinZ) * 0.125D;
					double yLerpAmountMinXMaxZ = (this.terrainNoise[((xSection + 0) * cellSize + zSection + 1) * columnSize + ySection + 1] - densityMinXMinYMaxZ) * 0.125D;
					double yLerpAmountMaxXMinZ = (this.terrainNoise[((xSection + 1) * cellSize + zSection + 0) * columnSize + ySection + 1] - densityMaxXMinYMinZ) * 0.125D;
					double yLerpAmountMaxXMaxZ = (this.terrainNoise[((xSection + 1) * cellSize + zSection + 1) * columnSize + ySection + 1] - densityMaxXMinYMaxZ) * 0.125D;

					for(int y = 0; y < 8; ++y) {						
						double curDensityMinXMinYMinZ = densityMinXMinYMinZ;
						double curDensityMinXMinYMaxZ = densityMinXMinYMaxZ;
						double xLerpAmountMinZ = (densityMaxXMinYMinZ - densityMinXMinYMinZ) * scalingFactor;
						double xLerpAmountMaxZ = (densityMaxXMinYMaxZ - densityMinXMinYMaxZ) * scalingFactor;

						for(int x = 0; x < 4; ++x) {
							int indexInBlockArray = (x + (xSection << 2)) << 11 | (0 + (zSection << 2)) << 7 | (ySection << 3) + y;
							
							double density = curDensityMinXMinYMinZ;
							double densityIncrement = (curDensityMinXMinYMaxZ - curDensityMinXMinYMinZ) * 0.25D;

							int yy = ySection * 8 + y;
							for(int z = 0; z < 4; ++z) {
								
								int blockID = 0;

								// Fill with water 								
								if(yy < seaLevel) {
									blockID = Block.waterStill.blockID;
								}

								// World density positive: fill with block.
								if(density > 0.0D) {
									blockID = Block.stone.blockID;
								}

								blocks[indexInBlockArray] = (byte)blockID;

								// Next Z
								indexInBlockArray += 128;
								density += densityIncrement;
								
								// Ocean detector
								if(yy == seaLevel - 1) this.isOcean &= (blockID != Block.stone.blockID);
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

	public void replaceBlocksForBiome(int chunkX, int chunkZ, byte[] blocks, byte[] metadata, BiomeGenBase[] biomes) {
		int seaLevel = 64;
		double d6 = 8.0D / 256D;
		this.stoneNoise = this.noiseStone.generateNoiseOctaves(this.stoneNoise, chunkX * 16, chunkZ * 16, 0, 16, 16, 1, d6 * 2.0D, d6 * 2.0D, d6 * 2.0D);

		BiomeGenBase biomeGen;
		
		for(int z = 0; z < 16; ++z) {
			for(int x = 0; x < 16; ++x) {
				biomeGen = biomes[x + z * 16];
				
				biomeGen.replaceBlocksForBiome(this, this.worldObj, this.rand, 
						chunkX, chunkZ, x, z, 
						blocks, metadata, seaLevel, 
						0D, 0D, this.stoneNoise[z + x * 16]
				);
			}
		}
	}
				
	public Chunk loadChunk(int i1, int i2) {
		return this.provideChunk(i1, i2);
	}
	
	protected byte[] createByteArray() {
		return new byte[32768];
	}

	public Chunk provideChunk(int chunkX, int chunkZ) {
		this.rand.setSeed((long)chunkX * 341873128712L + (long)chunkZ * 132897987541L);

		byte[] blockArray = this.createByteArray();
		byte[] metadataArray = this.createByteArray();
		this.generateTerrain(chunkX, chunkZ, blockArray);

		this.biomesForGeneration = this.worldObj.getWorldChunkManager().loadBlockGeneratorData(this.biomesForGeneration, chunkX * 16, chunkZ * 16, 16, 16);
		
		this.replaceBlocksForBiome(chunkX, chunkZ, blockArray, metadataArray, this.biomesForGeneration);
		
		this.caveGenerator.generate(this, this.worldObj, chunkX, chunkZ, blockArray);
		this.ravineGenerator.generate(this, this.worldObj, chunkX, chunkZ, blockArray);
		
		Chunk chunk = new Chunk(this.worldObj, blockArray, metadataArray, chunkX, chunkZ);

		if (this.mapFeaturesEnabled) {
			this.featureProvider.getNearestFeatures(chunkX, chunkZ, chunk);
		} 

		byte[] biomeArray = chunk.getBiomeArray();

		for(int i6 = 0; i6 < biomeArray.length; ++i6) {
			biomeArray[i6] = (byte)this.biomesForGeneration[i6].biomeID;
		}

		chunk.generateSkylightMap();
		return chunk;
	}

	public Chunk justGenerate(int chunkX, int chunkZ) {
		this.rand.setSeed((long)chunkX * 341873128712L + (long)chunkZ * 132897987541L);
		byte[] blockArray = this.createByteArray();
		this.generateTerrain(chunkX, chunkZ, blockArray);
		
		Chunk chunk = new Chunk(this.worldObj, blockArray, chunkX, chunkZ);
		
		return chunk;
	}

	public double[] initializeNoiseField(double[] densityMapArray, int x, int y, int z, int xSize, int ySize, int zSize) {
		if(densityMapArray == null) {
			densityMapArray = new double[xSize * ySize * zSize];
		}

		if(this.distanceArray == null) {
			this.distanceArray = new float[25];

			for(int xx = -2; xx <= 2; ++xx) {
				for(int zz = -2; zz <= 2; ++zz) {
					float f10 = 10.0F / MathHelper.sqrt_float((float)(xx * xx + zz * zz) + 0.2F);
					this.distanceArray[xx + 2 + (zz + 2) * 5] = f10;
				}
			}
		}

		double scaleXZ = 684.412D;
		double scaleY = 684.412D;
		this.scaleArray = this.scaleNoise.generateNoiseOctaves(this.scaleArray, x, z, xSize, zSize, 1.121D, 1.121D, 0.5D);
		this.depthArray = this.depthNoise.generateNoiseOctaves(this.depthArray, x, z, xSize, zSize, 200.0D, 200.0D, 0.5D);
		this.mainArray = this.mainNoise.generateNoiseOctaves(this.mainArray, x, y, z, xSize, ySize, zSize, scaleXZ / 80.0D, scaleY / 160.0D, scaleXZ / 80.0D);
		this.minLimitArray = this.minLimitNoise.generateNoiseOctaves(this.minLimitArray, x, y, z, xSize, ySize, zSize, scaleXZ, scaleY, scaleXZ);
		this.maxLimitArray = this.maxLimitNoise.generateNoiseOctaves(this.maxLimitArray, x, y, z, xSize, ySize, zSize, scaleXZ, scaleY, scaleXZ);
		
		int mainIndex = 0;
		int depthScaleIndex = 0;

		for(int dx = 0; dx < xSize; ++dx) {
			for(int dz = 0; dz < zSize; ++dz) {
				
				// Modify maxHeight / minHeight to soften biome borders

				float maxHeightScaled = 0.0F;
				float minHeightScaled = 0.0F;
				float totalDistance = 0.0F;
				byte radius = 2;
				BiomeGenBase biomeGen = this.biomesForGeneration[dx + 2 + (dz + 2) * (xSize + 5)];

				for(int xx = -radius; xx <= radius; ++xx) {
					for(int zz = -radius; zz <= radius; ++zz) {
						BiomeGenBase biomeGenBase23 = this.biomesForGeneration[dx + xx + 2 + (dz + zz + 2) * (xSize + 5)];
						float distance = this.distanceArray[xx + 2 + (zz + 2) * 5] / (biomeGenBase23.minHeight + 2.0F);
						if(biomeGenBase23.minHeight > biomeGen.minHeight) {
							distance /= 2.0F;
						}

						maxHeightScaled += biomeGenBase23.maxHeight * distance;
						minHeightScaled += biomeGenBase23.minHeight * distance;
						totalDistance += distance;
					}
				}

				float avgMaxHeight = maxHeightScaled / totalDistance;
				float avgMinHeight = minHeightScaled / totalDistance;
				
				avgMaxHeight = avgMaxHeight * 0.9F + 0.1F;
				avgMinHeight = (avgMinHeight * 4.0F - 1.0F) / 8.0F;
				
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
				} else {
					if(depth > 1.0D) {
						depth = 1.0D;
					}

					depth /= 8.0D;
				}

				++depthScaleIndex;

				for(int dy = 0; dy < ySize; ++dy) {
					double adjustedDepth = (double)avgMinHeight;
					double adjustedScale = (double)avgMaxHeight;
					adjustedDepth += depth * 0.2D;
					adjustedDepth = adjustedDepth * (double)ySize / 16.0D;
					double offsetY = (double)ySize / 2.0D + adjustedDepth * 4.0D;
					
					double density = 0.0D;
					double densityOffset = ((double)dy - offsetY) * 12.0D * 128.0D / 128.0D / adjustedScale;
					
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

	public void populate(IChunkProvider iChunkProvider1, int chunkX, int chunkZ) {
		BlockSand.fallInstantly = true;
		int x0 = chunkX * 16;
		int z0 = chunkZ * 16;
		
		BiomeGenBase biomeGen = this.worldObj.getBiomeGenForCoords(x0 + 16, z0 + 16);
		this.rand.setSeed(this.worldObj.getSeed());
		long j7 = this.rand.nextLong() / 2L * 2L + 1L;
		long j9 = this.rand.nextLong() / 2L * 2L + 1L;
		this.rand.setSeed((long)chunkX * j7 + (long)chunkZ * j9 ^ this.worldObj.getSeed());
		boolean z11 = false;
		boolean hadCustomFeat = false;		
		
		if(this.mapFeaturesEnabled) {	
			hadCustomFeat = this.featureProvider.populateFeatures(worldObj, rand, chunkX, chunkZ);		
		}
		
		int x;
		int y;
		int z;
		if(!z11 && this.rand.nextInt(4) == 0) {
			x = x0 + this.rand.nextInt(16) + 8;
			y = this.rand.nextInt(128);
			z = z0 + this.rand.nextInt(16) + 8;
			(new WorldGenLakes(Block.waterStill.blockID)).generate(this.worldObj, this.rand, x, y, z);
		}

		if(!z11 && this.rand.nextInt(8) == 0) {
			x = x0 + this.rand.nextInt(16) + 8;
			y = this.rand.nextInt(this.rand.nextInt(120) + 8);
			z = z0 + this.rand.nextInt(16) + 8;
			if(y < 63 || this.rand.nextInt(10) == 0) {
				(new WorldGenLakes(Block.lavaStill.blockID)).generate(this.worldObj, this.rand, x, y, z);
			}
		}

		for(int i = 0; i < 8; ++i) {
			x = x0 + this.rand.nextInt(16) + 8;
			y = this.rand.nextInt(128);
			z = z0 + this.rand.nextInt(16) + 8;
			if((new WorldGenDungeons(biomeGen)).generate(this.worldObj, this.rand, x, y, z)) {
				;
			}
		}
		
		biomeGen.decorate(this.worldObj, this.rand, x0, z0, hadCustomFeat);
		
		SpawnerAnimals.performWorldGenSpawning(this.worldObj, biomeGen, x0 + 8, z0 + 8, 16, 16, this.rand);
		x0 += 8;
		z0 += 8;

		for(int i = 0; i < 2; ++i) {
			x = x0 + this.rand.nextInt(16) + 8;
			y = this.rand.nextInt(512);
			z = z0 + this.rand.nextInt(16) + 8;
			if((new WorldGenDungeonsWater(biomeGen)).generate(this.worldObj, this.rand, x, y, z)) {
				;
			}
		}

		for(x = 0; x < 16; ++x) {
			for(z = 0; z < 16; ++z) {
				biomeGen = this.worldObj.getBiomeGenForCoords(x, z);
				y = this.worldObj.getPrecipitationHeight(x0 + x, z0 + z);
				if(this.worldObj.canFreezeWaterDirectly(x + x0, y - 1, z + z0, biomeGen)) {
					this.worldObj.setBlockWithNotify(x + x0, y - 1, z + z0, Block.ice.blockID);
				}

				if(this.worldObj.canSnowAt(x + x0, y, z + z0, true, biomeGen)) {
					this.worldObj.setBlockWithNotify(x + x0, y, z + z0, Block.snow.blockID);
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

	public List<SpawnListEntry> getPossibleCreatures(EnumCreatureType enumCreatureType1, int i2, int i3, int i4) {
		BiomeGenBase biomeGenBase5 = this.worldObj.getBiomeGenForCoords(i2, i4);
		return biomeGenBase5 == null ? null : biomeGenBase5.getSpawnableList(enumCreatureType1);
	}

	public ChunkPosition findClosestStructure(World world1, String string2, int i3, int i4, int i5) {
		return null;
	}
}
