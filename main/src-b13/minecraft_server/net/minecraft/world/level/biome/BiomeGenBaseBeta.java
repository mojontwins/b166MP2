package net.minecraft.world.level.biome;

import java.util.Random;

import net.minecraft.world.level.Weather;
import net.minecraft.world.level.World;
import net.minecraft.world.level.chunk.IChunkProvider;
import net.minecraft.world.level.levelgen.ChunkProviderSky;
import net.minecraft.world.level.tile.Block;

public class BiomeGenBaseBeta extends BiomeGenBase {

	protected boolean genBeaches = true;
	
	public BiomeGenBaseBeta(int id) {
		super(id);
	}

	protected BiomeDecorator createBiomeDecorator() {
		return new BiomeDecoratorBeta(this);
	}
	
	public void replaceBlocksForBiome(
			IChunkProvider generator, World world, Random rand, 
			int chunkX, int chunkZ, int x, int z, 
			byte[] blocks, byte[] metadata, int seaLevel, 
			double sandNoise, double gravelNoise, double stoneNoise
		) {
		
		boolean generateSand = sandNoise + rand.nextDouble() * 0.2D > 0.0D;
		boolean generateGravel = gravelNoise + rand.nextDouble() * 0.2D > 3.0D;
		int height = (int)(stoneNoise / 3.0D + 3.0D + rand.nextDouble() * 0.25D);
		int monolithNoise = (int)(stoneNoise / 3.0D + 3.0D + rand.nextDouble() * 0.5D);

		int stoneHeight = -1;
		byte topBlock = this.getTopBlock(rand);
		byte fillerBlock = this.fillerBlock;

		for(int y = 127; y >= 0; --y) {
			int index = x << 11 | z << 7 | y; // (x * 16 + z) * 128 + y
			
			if (y > 127 - monolithNoise) {
				blocks[index] = (byte)0;
				stoneHeight = -1;
			} else if(y <= 0 + rand.nextInt(5) && !(generator instanceof ChunkProviderSky)) {
				blocks[index] = (byte)Block.bedrock.blockID;
			} else {
				byte blockID = blocks[index];
				byte meta = metadata[index];
				if(blockID == 0) {
					stoneHeight = -1;
				} else if(blockID == Block.stone.blockID && meta == 0) {
					if(stoneHeight == -1) {
						if(height <= 0) {
							topBlock = 0;
							fillerBlock = (byte)Block.stone.blockID;
						} else if(y >= seaLevel - 4 && y <= seaLevel + 1) {
							topBlock = this.topBlock;
							fillerBlock = this.fillerBlock;
							if(generateGravel) {
								topBlock = 0;
							}
							
							if(this.genBeaches ) {
								if(generateGravel) {
									fillerBlock = (byte)Block.gravel.blockID;
								}

								if(generateSand) {
									topBlock = (byte)Block.sand.blockID;
									fillerBlock = (byte)Block.sand.blockID;
								}
							}
						}

						if(y < seaLevel && topBlock == 0) {
							if(this.weather == Weather.cold) {
								topBlock = (byte)Block.ice.blockID;
							} else {
								topBlock = (byte)Block.waterStill.blockID;
							}
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
						if(stoneHeight == 0 && fillerBlock == this.sandstoneGenTriggerer()) {
							stoneHeight = rand.nextInt(4);
							fillerBlock = this.sandstoneGenBlock();
						}
					}
				} else if(blockID == Block.waterStill.blockID && y == seaLevel - 1 && this.weather == Weather.cold) {
					blocks[index] = (byte)Block.ice.blockID;
				}
			}
		}
	}
}
