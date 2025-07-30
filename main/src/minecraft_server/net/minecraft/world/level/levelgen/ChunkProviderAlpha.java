package net.minecraft.world.level.levelgen;

import java.util.Random;

import net.minecraft.world.level.World;
import net.minecraft.world.level.biome.BiomeGenBase;
import net.minecraft.world.level.levelgen.synth.NoiseGeneratorOctavesAlpha;

public class ChunkProviderAlpha extends ChunkProviderGenerate {

	public NoiseGeneratorOctavesAlpha minLimitNoiseA;
	public NoiseGeneratorOctavesAlpha maxLimitNoiseA;
	public NoiseGeneratorOctavesAlpha mainNoiseA;
	public NoiseGeneratorOctavesAlpha noiseGenSandOrGravel;
	public NoiseGeneratorOctavesAlpha noiseStone;
	public NoiseGeneratorOctavesAlpha scaleNoiseA;
	public NoiseGeneratorOctavesAlpha depthNoiseA;
	double[] depthArray;
	
	private double[] sandNoise = new double[256];
	private double[] gravelNoise = new double[256];
	
	public ChunkProviderAlpha(World world, long seed, boolean mapFeaturesEnabled) {
		super(world, seed, mapFeaturesEnabled);

		// Reset the randomizer so worlds are seed-accurate with vanilla
		this.rand = new Random(seed);
		
		this.minLimitNoiseA = new NoiseGeneratorOctavesAlpha(this.rand, 16);
		this.maxLimitNoiseA = new NoiseGeneratorOctavesAlpha(this.rand, 16);
		this.mainNoiseA = new NoiseGeneratorOctavesAlpha(this.rand, 8);
		this.noiseGenSandOrGravel = new NoiseGeneratorOctavesAlpha(this.rand, 4);
		this.noiseStone = new NoiseGeneratorOctavesAlpha(this.rand, 4);
		this.scaleNoiseA = new NoiseGeneratorOctavesAlpha(this.rand, 10);
		this.depthNoiseA = new NoiseGeneratorOctavesAlpha(this.rand, 16);
	}
	
	public void replaceBlocksForBiome(int chunkX, int chunkZ, byte[] blocks, byte[] metadata, BiomeGenBase[] biomes) {
		int seaLevel = (this instanceof ChunkProviderSky) ? -1 : 64;
		double d5 = 8.0D / 256D;
		this.sandNoise = this.noiseGenSandOrGravel.generateNoiseOctaves(this.sandNoise, (double)(chunkX * 16), (double)(chunkZ * 16), 0.0D, 16, 16, 1, d5, d5, 1.0D);
		this.gravelNoise = this.noiseGenSandOrGravel.generateNoiseOctaves(this.gravelNoise, (double)(chunkZ * 16), 109.0134D, (double)(chunkX * 16), 16, 1, 16, d5, 1.0D, d5);
		this.stoneNoise = this.noiseStone.generateNoiseOctaves(this.stoneNoise, (double)(chunkX * 16), (double)(chunkZ * 16), 0.0D, 16, 16, 1, d5 * 2.0D, d5 * 2.0D, d5 * 2.0D);

		BiomeGenBase biomeGen;

		for(int z = 0; z < 16; ++z) {
			for(int x = 0; x < 16; ++x) {		
				biomeGen = biomes[z | (x << 4)];

				int noiseIndex = z | (x << 4);
				biomeGen.replaceBlocksForBiome(this, this.worldObj, this.rand, 
						chunkX, chunkZ, x, z, 
						blocks, metadata, seaLevel, 
						this.sandNoise[noiseIndex], this.gravelNoise[noiseIndex], this.stoneNoise[noiseIndex]
				);
			}
		}

	}
	
	// Alpha noise is very similar to Beta noise, but 
	// - it is not affected by t/h,
	// - scale & depth noise have different scaling.
	// - minor bugs in NoiseGeneratorPerlin make monoliths.

	public double[] initializeNoiseField(double[] densityMapArray, int x, int y, int z, int xSize, int ySize, int zSize) {
		if(densityMapArray == null) {
			densityMapArray = new double[xSize * ySize * zSize];
		}

		double scaleXZ = 684.412D;
		double scaleY = 684.412D;

		this.scaleArray = this.scaleNoiseA.generateNoiseOctaves(this.scaleArray, (double)x, (double)y, (double)z, xSize, 1, zSize, 1.0D, 0.0D, 1.0D);
		this.depthArray = this.depthNoiseA.generateNoiseOctaves(this.depthArray, (double)x, (double)y, (double)z, xSize, 1, zSize, 100.0D, 0.0D, 100.0D);
		this.mainArray = this.mainNoiseA.generateNoiseOctaves(this.mainArray, (double)x, (double)y, (double)z, xSize, ySize, zSize, scaleXZ / 80.0D, scaleY / 160.0D, scaleXZ / 80.0D);
		this.minLimitArray = this.minLimitNoiseA.generateNoiseOctaves(this.minLimitArray, (double)x, (double)y, (double)z, xSize, ySize, zSize, scaleXZ, scaleY, scaleXZ);
		this.maxLimitArray = this.maxLimitNoiseA.generateNoiseOctaves(this.maxLimitArray, (double)x, (double)y, (double)z, xSize, ySize, zSize, scaleXZ, scaleY, scaleXZ);
		
		int mainIndex = 0;
		int depthScaleIndex = 0;

		for(int dx = 0; dx < xSize; ++dx) {
			for(int dz = 0; dz < zSize; ++dz) {

				double scale = (this.scaleArray[depthScaleIndex] + 256.0D) / 512.0D;
				if(scale > 1.0D) {
					scale = 1.0D;
				}

				double depth = this.depthArray[depthScaleIndex] / 8000.0D;
				if(depth < 0.0D) {
					depth = -depth;
				}

				depth = depth * 3.0D - 3.0D;
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

					depth /= 6.0D;
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
						double d35 = (double)((float)(dy - (ySize - 4)) / 3.0F);
						density = density * (1.0D - d35) + -10.0D * d35;
					}

					densityMapArray[mainIndex] = density;
					++mainIndex;
				}
			}
		}

		return densityMapArray;
	}

}
