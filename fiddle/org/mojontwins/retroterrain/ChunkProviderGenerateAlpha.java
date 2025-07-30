package org.mojontwins.retroterrain;

import java.util.Random;

import net.minecraft.src.World;

public class ChunkProviderGenerateAlpha extends ChunkProviderGenerateRetro {
	private NoiseGeneratorOctavesAlpha minLimitNoiseA;
	private NoiseGeneratorOctavesAlpha maxLimitNoiseA;
	private NoiseGeneratorOctavesAlpha mainNoiseA;

	private NoiseGeneratorOctavesAlpha scaleNoiseA;
	private NoiseGeneratorOctavesAlpha depthNoiseA;

	public ChunkProviderGenerateAlpha(World world1, long j2, boolean z4) {
		super(world1, j2, z4);
		
		// Ways to reset rng
		this.rand = new Random(j2);
		
		this.minLimitNoiseA = new NoiseGeneratorOctavesAlpha(this.rand, 16);
		this.maxLimitNoiseA = new NoiseGeneratorOctavesAlpha(this.rand, 16);
		this.mainNoiseA = new NoiseGeneratorOctavesAlpha(this.rand, 8);
		
		this.noiseGenSandOrGravel = new NoiseGeneratorOctavesBeta(this.rand, 4);
		this.noiseStone = new NoiseGeneratorOctavesBeta(this.rand, 4);
		
		this.scaleNoiseA = new NoiseGeneratorOctavesAlpha(this.rand, 10);
		this.depthNoiseA = new NoiseGeneratorOctavesAlpha(this.rand, 16);
		
		System.out.println ("Chunk Provider Alpha");
	}

	// Alpha noise is very simmilar to Beta noise, but 
	// - it is not affected by t/h,
	// - scale & depth noise have different scaling.
	// - minor bugs in NoiseGeneratorPerlin make monoliths.

	protected double[] initializeNoiseField(double[] densityMapArray, int x, int y, int z, int xSize, int ySize, int zSize) {
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
