package net.minecraft.world.level.levelgen.feature.trees;

import java.util.Random;

import net.minecraft.src.MathHelper;
import net.minecraft.world.level.World;
import net.minecraft.world.level.levelgen.feature.WorldGenerator;
import net.minecraft.world.level.tile.Block;

public class WorldGenHugeTrees extends WorldGenerator {
	private final int height;
	private final int woodMetadata;
	private final int leavesMetadata;

	public WorldGenHugeTrees(boolean z1, int i2, int i3, int i4) {
		super(z1);
		this.height = i2;
		this.woodMetadata = i3;
		this.leavesMetadata = i4;
	}

	public boolean generate(World world1, Random random2, int i3, int i4, int i5) {
		int i6 = random2.nextInt(3) + this.height;
		boolean z7 = true;
		if(i4 >= 1 && i4 + i6 + 1 <= 256) {
			int i8;
			int i10;
			int i11;
			int i12;
			for(i8 = i4; i8 <= i4 + 1 + i6; ++i8) {
				byte b9 = 2;
				if(i8 == i4) {
					b9 = 1;
				}

				if(i8 >= i4 + 1 + i6 - 2) {
					b9 = 2;
				}

				for(i10 = i3 - b9; i10 <= i3 + b9 && z7; ++i10) {
					for(i11 = i5 - b9; i11 <= i5 + b9 && z7; ++i11) {
						if(i8 >= 0 && i8 < 256) {
							i12 = world1.getBlockId(i10, i8, i11);
							if(i12 != 0 && i12 != Block.leaves.blockID && i12 != Block.grass.blockID && i12 != Block.dirt.blockID && i12 != Block.wood.blockID && i12 != Block.sapling.blockID) {
								z7 = false;
							}
						} else {
							z7 = false;
						}
					}
				}
			}

			if(!z7) {
				return false;
			} else {
				i8 = world1.getBlockId(i3, i4 - 1, i5);
				if((i8 == Block.grass.blockID || i8 == Block.dirt.blockID) && i4 < 256 - i6 - 1) {
					world1.setBlock(i3, i4 - 1, i5, Block.dirt.blockID);
					world1.setBlock(i3 + 1, i4 - 1, i5, Block.dirt.blockID);
					world1.setBlock(i3, i4 - 1, i5 + 1, Block.dirt.blockID);
					world1.setBlock(i3 + 1, i4 - 1, i5 + 1, Block.dirt.blockID);
					this.func_48192_a(world1, i3, i5, i4 + i6, 2, random2);

					for(int i14 = i4 + i6 - 2 - random2.nextInt(4); i14 > i4 + i6 / 2; i14 -= 2 + random2.nextInt(4)) {
						float f15 = random2.nextFloat() * (float)Math.PI * 2.0F;
						i11 = i3 + (int)(0.5F + MathHelper.cos(f15) * 4.0F);
						i12 = i5 + (int)(0.5F + MathHelper.sin(f15) * 4.0F);
						this.func_48192_a(world1, i11, i12, i14, 0, random2);

						for(int i13 = 0; i13 < 5; ++i13) {
							i11 = i3 + (int)(1.5F + MathHelper.cos(f15) * (float)i13);
							i12 = i5 + (int)(1.5F + MathHelper.sin(f15) * (float)i13);
							this.setBlockAndMetadata(world1, i11, i14 - 3 + i13 / 2, i12, Block.wood.blockID, this.woodMetadata);
						}
					}

					for(i10 = 0; i10 < i6; ++i10) {
						i11 = world1.getBlockId(i3, i4 + i10, i5);
						if(i11 == 0 || i11 == Block.leaves.blockID) {
							this.setBlockAndMetadata(world1, i3, i4 + i10, i5, Block.wood.blockID, this.woodMetadata);
							if(i10 > 0) {
								/*
								if(random2.nextInt(3) > 0 && world1.isAirBlock(i3 - 1, i4 + i10, i5)) {
									this.setBlockAndMetadata(world1, i3 - 1, i4 + i10, i5, Block.vine.blockID, 8);
								}

								if(random2.nextInt(3) > 0 && world1.isAirBlock(i3, i4 + i10, i5 - 1)) {
									this.setBlockAndMetadata(world1, i3, i4 + i10, i5 - 1, Block.vine.blockID, 1);
								}
								*/
							}
						}

						if(i10 < i6 - 1) {
							i11 = world1.getBlockId(i3 + 1, i4 + i10, i5);
							if(i11 == 0 || i11 == Block.leaves.blockID) {
								this.setBlockAndMetadata(world1, i3 + 1, i4 + i10, i5, Block.wood.blockID, this.woodMetadata);
								if(i10 > 0) {
									/*
									if(random2.nextInt(3) > 0 && world1.isAirBlock(i3 + 2, i4 + i10, i5)) {
										this.setBlockAndMetadata(world1, i3 + 2, i4 + i10, i5, Block.vine.blockID, 2);
									}

									if(random2.nextInt(3) > 0 && world1.isAirBlock(i3 + 1, i4 + i10, i5 - 1)) {
										this.setBlockAndMetadata(world1, i3 + 1, i4 + i10, i5 - 1, Block.vine.blockID, 1);
									}
									*/
								}
							}

							i11 = world1.getBlockId(i3 + 1, i4 + i10, i5 + 1);
							if(i11 == 0 || i11 == Block.leaves.blockID) {
								this.setBlockAndMetadata(world1, i3 + 1, i4 + i10, i5 + 1, Block.wood.blockID, this.woodMetadata);
								if(i10 > 0) {
									/*
									if(random2.nextInt(3) > 0 && world1.isAirBlock(i3 + 2, i4 + i10, i5 + 1)) {
										this.setBlockAndMetadata(world1, i3 + 2, i4 + i10, i5 + 1, Block.vine.blockID, 2);
									}

									if(random2.nextInt(3) > 0 && world1.isAirBlock(i3 + 1, i4 + i10, i5 + 2)) {
										this.setBlockAndMetadata(world1, i3 + 1, i4 + i10, i5 + 2, Block.vine.blockID, 4);
									}
									*/
								}
							}

							i11 = world1.getBlockId(i3, i4 + i10, i5 + 1);
							if(i11 == 0 || i11 == Block.leaves.blockID) {
								this.setBlockAndMetadata(world1, i3, i4 + i10, i5 + 1, Block.wood.blockID, this.woodMetadata);
								if(i10 > 0) {
									/*
									if(random2.nextInt(3) > 0 && world1.isAirBlock(i3 - 1, i4 + i10, i5 + 1)) {
										this.setBlockAndMetadata(world1, i3 - 1, i4 + i10, i5 + 1, Block.vine.blockID, 8);
									}

									if(random2.nextInt(3) > 0 && world1.isAirBlock(i3, i4 + i10, i5 + 2)) {
										this.setBlockAndMetadata(world1, i3, i4 + i10, i5 + 2, Block.vine.blockID, 4);
									}
									*/
								}
							}
						}
					}

					return true;
				} else {
					return false;
				}
			}
		} else {
			return false;
		}
	}

	private void func_48192_a(World world1, int i2, int i3, int i4, int i5, Random random6) {
		byte b7 = 2;

		for(int i8 = i4 - b7; i8 <= i4; ++i8) {
			int i9 = i8 - i4;
			int i10 = i5 + 1 - i9;

			for(int i11 = i2 - i10; i11 <= i2 + i10 + 1; ++i11) {
				int i12 = i11 - i2;

				for(int i13 = i3 - i10; i13 <= i3 + i10 + 1; ++i13) {
					int i14 = i13 - i3;
					if((i12 >= 0 || i14 >= 0 || i12 * i12 + i14 * i14 <= i10 * i10) && (i12 <= 0 && i14 <= 0 || i12 * i12 + i14 * i14 <= (i10 + 1) * (i10 + 1)) && (random6.nextInt(4) != 0 || i12 * i12 + i14 * i14 <= (i10 - 1) * (i10 - 1)) && !Block.opaqueCubeLookup[world1.getBlockId(i11, i8, i13)]) {
						this.setBlockAndMetadata(world1, i11, i8, i13, Block.leaves.blockID, this.leavesMetadata);
					}
				}
			}
		}

	}
}
