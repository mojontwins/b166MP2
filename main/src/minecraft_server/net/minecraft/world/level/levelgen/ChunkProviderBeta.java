package net.minecraft.world.level.levelgen;

import java.util.Random;

import net.minecraft.world.level.World;
import net.minecraft.world.level.WorldChunkManagerBeta;
import net.minecraft.world.level.chunk.Chunk;
import net.minecraft.world.level.levelgen.synth.NoiseGeneratorOctavesBeta;
import net.minecraft.world.level.tile.Block;

public class ChunkProviderBeta extends ChunkProviderAlpha {
	
	private NoiseGeneratorOctavesBeta minLimitNoise;
	private NoiseGeneratorOctavesBeta maxLimitNoise;
	private NoiseGeneratorOctavesBeta mainNoise;
	protected NoiseGeneratorOctavesBeta noiseGenSandOrGravel;
	protected NoiseGeneratorOctavesBeta noiseStone;
	private NoiseGeneratorOctavesBeta scaleNoise;
	private NoiseGeneratorOctavesBeta depthNoise;
	public NoiseGeneratorOctavesBeta mobSpawnerNoise;

	public ChunkProviderBeta(World world, long seed, boolean mapFeaturesEnabled) {
		super(world, seed, mapFeaturesEnabled);
		
		// Reset the randomizer so worlds are seed-accurate with vanilla
		this.rand = new Random(seed);
		
		this.minLimitNoise = new NoiseGeneratorOctavesBeta(this.rand, 16);
		this.maxLimitNoise = new NoiseGeneratorOctavesBeta(this.rand, 16);
		this.mainNoise = new NoiseGeneratorOctavesBeta(this.rand, 8);
		
		this.noiseGenSandOrGravel = new NoiseGeneratorOctavesBeta(this.rand, 4);
		this.noiseStone = new NoiseGeneratorOctavesBeta(this.rand, 4);
		
		this.scaleNoise = new NoiseGeneratorOctavesBeta(this.rand, 10);
		this.depthNoise = new NoiseGeneratorOctavesBeta(this.rand, 16);
		
		this.mobSpawnerNoise = new NoiseGeneratorOctavesBeta(this.rand, 8);
	}
	
	public void generateTerrain(int chunkX, int chunkZ, byte[] blocks) {
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

								int blockID = 0;
								
								if(ySection * 8 + y < seaLevel) {
									blockID = Block.waterStill.blockID;
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
	
	// I have to override provideChunk 'cause beta needs the biomes generated before generateTerrain is called.
	@Override
	public Chunk provideChunk(int chunkX, int chunkZ) {
		this.rand.setSeed((long)chunkX * 341873128712L + (long)chunkZ * 132897987541L);

		byte[] blockArray = this.createByteArray();
		byte[] metadataArray = this.createByteArray();
		
		this.biomesForGeneration = this.worldObj.getWorldChunkManager().loadBlockGeneratorData(this.biomesForGeneration, chunkX * 16, chunkZ * 16, 16, 16);
		
		this.generateTerrain(chunkX, chunkZ, blockArray);
		this.replaceBlocksForBiome(chunkX, chunkZ, blockArray, metadataArray, this.biomesForGeneration);
		
		this.caveGenerator.generate(this, this.worldObj, chunkX, chunkZ, blockArray);
		this.ravineGenerator.generate(this, this.worldObj, chunkX, chunkZ, blockArray);
		
		Chunk chunk = new Chunk(this.worldObj, blockArray, chunkX, chunkZ);

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

	@Override
	public double[] initializeNoiseField(double[] densityMapArray, int x, int y, int z, int xSize, int ySize, int zSize) {
		if(densityMapArray == null) {
			densityMapArray = new double[xSize * ySize * zSize];
		}

		double scaleXZ = 684.412D;
		double scaleY = 684.412D;
				
		double[] temperatureArray = ((WorldChunkManagerBeta)this.worldObj.getWorldChunkManager()).temperature;
		double[] humidityArray = ((WorldChunkManagerBeta)this.worldObj.getWorldChunkManager()).humidity;

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
}
