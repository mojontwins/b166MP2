package net.minecraft.world.level.levelgen;

import java.util.Random;

import net.minecraft.src.MathHelper;
import net.minecraft.world.level.ISurface;
import net.minecraft.world.level.World;
import net.minecraft.world.level.tile.Block;
import net.minecraft.world.level.tile.IGround;

public class MapGenCavesBeta extends MapGenBase {
	protected void generateLargeCaveNode(int chunkX, int chunkZ, byte[] data, double x, double y, double z) {
		this.generateCaveNode(chunkX, chunkZ, data, x, y, z, 1.0F + this.rand.nextFloat() * 6.0F, 0.0F, 0.0F, -1, -1, 0.5D); 
	}

	protected void generateCaveNode(int chunkX, int chunkZ, byte[] data, double x, double y, double z, float nodeSize, float yaw, float pitch, int i13, int i14, double horzVertRatio) {
		double chunkCenterX = (double)(chunkX * 16 + 8);
		double chunkCenterZ = (double)(chunkZ * 16 + 8);
		float f21 = 0.0F;
		float f22 = 0.0F;
		Random random23 = new Random(this.rand.nextLong());
		if(i14 <= 0) {
			int i24 = this.range * 16 - 16;
			i14 = i24 - random23.nextInt(i24 / 4);
		}

		boolean z52 = false;
		if(i13 == -1) {
			i13 = i14 / 2;
			z52 = true;
		}

		int i25 = random23.nextInt(i14 / 2) + i14 / 4;
		boolean turnFaster = random23.nextInt(6) == 0;

		for(; i13 < i14; ++i13) {
			double amplitudeHorz = 1.5D + (double)(MathHelper.sin((float)i13 * (float)Math.PI / (float)i14) * nodeSize * 1.0F); 
			double amplitudeVert = amplitudeHorz * horzVertRatio;
			float dHorz = MathHelper.cos(pitch);
			float dVert = MathHelper.sin(pitch);
			x += (double)(MathHelper.cos(yaw) * dHorz);
			y += (double)dVert;
			z += (double)(MathHelper.sin(yaw) * dHorz);
			
			if(turnFaster) {
				pitch *= 0.92F;
			} else {
				pitch *= 0.7F;
			}

			pitch += f22 * 0.1F;
			yaw += f21 * 0.1F;
			f22 *= 0.9F;
			f21 *= 0.75F;
			f22 += (random23.nextFloat() - random23.nextFloat()) * random23.nextFloat() * 2.0F;
			f21 += (random23.nextFloat() - random23.nextFloat()) * random23.nextFloat() * 4.0F;
			if(!z52 && i13 == i25 && nodeSize > 1.0F) {
				this.generateCaveNode(chunkX, chunkZ, data, x, y, z, random23.nextFloat() * 0.5F + 0.5F, yaw - (float)Math.PI / 2F, pitch / 3.0F, i13, i14, 1.0D);
				this.generateCaveNode(chunkX, chunkZ, data, x, y, z, random23.nextFloat() * 0.5F + 0.5F, yaw + (float)Math.PI / 2F, pitch / 3.0F, i13, i14, 1.0D);
				return;
			}

			if(z52 || random23.nextInt(4) != 0) {
				double d33 = x - chunkCenterX;
				double d35 = z - chunkCenterZ;
				double d37 = (double)(i14 - i13);
				double d39 = (double)(nodeSize + 2.0F + 16.0F);
				if(d33 * d33 + d35 * d35 - d37 * d37 > d39 * d39) {
					return;
				}

				if(x >= chunkCenterX - 16.0D - amplitudeHorz * 2.0D && z >= chunkCenterZ - 16.0D - amplitudeHorz * 2.0D && x <= chunkCenterX + 16.0D + amplitudeHorz * 2.0D && z <= chunkCenterZ + 16.0D + amplitudeHorz * 2.0D) {
					int x1 = MathHelper.floor_double(x - amplitudeHorz) - chunkX * 16 - 1;
					int x2 = MathHelper.floor_double(x + amplitudeHorz) - chunkX * 16 + 1;
					int y1 = MathHelper.floor_double(y - amplitudeVert) - 1;
					int y2 = MathHelper.floor_double(y + amplitudeVert) + 1;
					int z1 = MathHelper.floor_double(z - amplitudeHorz) - chunkZ * 16 - 1;
					int z2 = MathHelper.floor_double(z + amplitudeHorz) - chunkZ * 16 + 1;
					if(x1 < 0) {
						x1 = 0;
					}

					if(x2 > 16) {
						x2 = 16;
					}

					if(y1 < 1) {
						y1 = 1;
					}

					if(y2 > 120) {
						y2 = 120;
					}

					if(z1 < 0) {
						z1 = 0;
					}

					if(z2 > 16) {
						z2 = 16;
					}

					boolean isInWater = false;

					for(int ix = x1; !isInWater && ix < x2; ++ix) {
						for(int iz = z1; !isInWater && iz < z2; ++iz) {
							for(int iy = y2 + 1; !isInWater && iy >= y1 - 1; --iy) {
								// int idx = (ix * 16 + iz) * 128 + iy;
								int idx = ix << 11 | iz << 7 | iy; 
								if(iy >= 0 && iy < 128) {
									
									if(data[idx] == Block.waterMoving.blockID || data[idx] == Block.waterStill.blockID) {
										isInWater = true;
									}

									if(iy != y1 - 1 && ix != x1 && ix != x2 - 1 && iz != z1 && iz != z2 - 1) {
										iy = y1;
									}
								}
							}
						}
					}

					if(!isInWater) {
						for(int ix = x1; ix < x2; ++ix) {
							double dx = ((double)(ix + chunkX * 16) + 0.5D - x) / amplitudeHorz;

							for(int iz = z1; iz < z2; ++iz) {
								double dz = ((double)(iz + chunkZ * 16) + 0.5D - z) / amplitudeHorz;
								int index = ix << 11 | iz << 7 | y2;
								boolean hitSurface = false;

								if(dx * dx + dz * dz < 1.0D) {
									for(int iy = y2 - 1; iy >= y1; --iy) {
										double dy = ((double)iy + 0.5D - y) / amplitudeVert;
										
										if(dy > -0.7D && dx * dx + dy * dy + dz * dz < 1.0D) {
											int blockID = data[index] & 0xff;
											Block block = Block.blocksList[blockID];
											
											if (block != null && (block instanceof ISurface) && blockID != Block.sand.blockID) {
												hitSurface = true;
											}

											if (block != null && (block instanceof IGround) && blockID != Block.sand.blockID) {
												if(iy < 10) {
													data[index] = (byte)Block.lavaMoving.blockID;
												} else {
													data[index] = 0;
													if(hitSurface && data[index - 1] == Block.dirt.blockID) {
														data[index - 1] = (byte)Block.grass.blockID;
													}
												}
											}
										}

										--index;
									}
								}
							}
						}

						if(z52) {
							break;
						}
					}
				}
			}
		}

	}

	protected void recursiveGenerate(World world, int i2, int i3, int chunkX, int chunkZ, byte[] data) {
		int i7 = this.rand.nextInt(this.rand.nextInt(this.rand.nextInt(40) + 1) + 1);
		if(this.rand.nextInt(15) != 0) {
			i7 = 0;
		}

		for(int i8 = 0; i8 < i7; ++i8) {
			double x = (double)(i2 * 16 + this.rand.nextInt(16));
			double y = (double)this.rand.nextInt(this.rand.nextInt(120) + 8);
			double z = (double)(i3 * 16 + this.rand.nextInt(16));
			int i15 = 1;
			if(this.rand.nextInt(4) == 0) {
				this.generateLargeCaveNode(chunkX, chunkZ, data, x, y, z);
				i15 += this.rand.nextInt(4);
			}

			for(int i16 = 0; i16 < i15; ++i16) {
				float yaw = this.rand.nextFloat() * (float)Math.PI * 2.0F;
				float pitch = (this.rand.nextFloat() - 0.5F) * 2.0F / 8.0F;
				float nodeSize = this.rand.nextFloat() * 2.0F + this.rand.nextFloat();
				
				this.generateCaveNode(chunkX, chunkZ, data, x, y, z, nodeSize, yaw, pitch, 0, 0, 1.0D);
			}
		}

	}
}
