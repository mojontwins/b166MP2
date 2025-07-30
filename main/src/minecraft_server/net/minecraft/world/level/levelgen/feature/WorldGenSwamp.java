package net.minecraft.world.level.levelgen.feature;

import java.util.Random;

import net.minecraft.world.level.World;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.tile.Block;

public class WorldGenSwamp extends WorldGenerator {
	public boolean generate(World world1, Random random2, int i3, int i4, int i5) {
		int i6;
		for(i6 = random2.nextInt(4) + 5; world1.getBlockMaterial(i3, i4 - 1, i5) == Material.water; --i4) {
		}

		boolean z7 = true;
		if(i4 >= 1 && i4 + i6 + 1 <= 128) {
			int i8;
			int i10;
			int i11;
			int i12;
			for(i8 = i4; i8 <= i4 + 1 + i6; ++i8) {
				byte b9 = 1;
				if(i8 == i4) {
					b9 = 0;
				}

				if(i8 >= i4 + 1 + i6 - 2) {
					b9 = 3;
				}

				for(i10 = i3 - b9; i10 <= i3 + b9 && z7; ++i10) {
					for(i11 = i5 - b9; i11 <= i5 + b9 && z7; ++i11) {
						if(i8 >= 0 && i8 < 128) {
							i12 = world1.getBlockId(i10, i8, i11);
							if(i12 != 0 && i12 != Block.leaves.blockID) {
								if(i12 != Block.waterStill.blockID && i12 != Block.waterMoving.blockID) {
									z7 = false;
								} else if(i8 > i4) {
									z7 = false;
								}
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
				if((i8 == Block.grass.blockID || i8 == Block.dirt.blockID) && i4 < 128 - i6 - 1) {
					this.setBlock(world1, i3, i4 - 1, i5, Block.dirt.blockID);

					int i13;
					int i16;
					for(i16 = i4 - 3 + i6; i16 <= i4 + i6; ++i16) {
						i10 = i16 - (i4 + i6);
						i11 = 2 - i10 / 2;

						for(i12 = i3 - i11; i12 <= i3 + i11; ++i12) {
							i13 = i12 - i3;

							for(int i14 = i5 - i11; i14 <= i5 + i11; ++i14) {
								int i15 = i14 - i5;
								if((Math.abs(i13) != i11 || Math.abs(i15) != i11 || random2.nextInt(2) != 0 && i10 != 0) && !Block.opaqueCubeLookup[world1.getBlockId(i12, i16, i14)]) {
									this.setBlock(world1, i12, i16, i14, Block.leaves.blockID);
								}
							}
						}
					}

					for(i16 = 0; i16 < i6; ++i16) {
						i10 = world1.getBlockId(i3, i4 + i16, i5);
						if(i10 == 0 || i10 == Block.leaves.blockID || i10 == Block.waterMoving.blockID || i10 == Block.waterStill.blockID) {
							this.setBlock(world1, i3, i4 + i16, i5, Block.wood.blockID);
						}
					}

					for(i16 = i4 - 3 + i6; i16 <= i4 + i6; ++i16) {
						i10 = i16 - (i4 + i6);
						i11 = 2 - i10 / 2;

						for(i12 = i3 - i11; i12 <= i3 + i11; ++i12) {
							for(i13 = i5 - i11; i13 <= i5 + i11; ++i13) {
								if(world1.getBlockId(i12, i16, i13) == Block.leaves.blockID) {
									if(random2.nextInt(4) == 0 && world1.getBlockId(i12 - 1, i16, i13) == 0) {
										this.generateVines(world1, i12 - 1, i16, i13, 8);
									}

									if(random2.nextInt(4) == 0 && world1.getBlockId(i12 + 1, i16, i13) == 0) {
										this.generateVines(world1, i12 + 1, i16, i13, 2);
									}

									if(random2.nextInt(4) == 0 && world1.getBlockId(i12, i16, i13 - 1) == 0) {
										this.generateVines(world1, i12, i16, i13 - 1, 1);
									}

									if(random2.nextInt(4) == 0 && world1.getBlockId(i12, i16, i13 + 1) == 0) {
										this.generateVines(world1, i12, i16, i13 + 1, 4);
									}
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

	private void generateVines(World world1, int i2, int i3, int i4, int i5) {
		/*
		this.setBlockAndMetadata(world1, i2, i3, i4, Block.vine.blockID, i5);
		int i6 = 4;

		while(true) {
			--i3;
			if(world1.getBlockId(i2, i3, i4) != 0 || i6 <= 0) {
				return;
			}

			this.setBlockAndMetadata(world1, i2, i3, i4, Block.vine.blockID, i5);
			--i6;
		}
		*/
	}
}
