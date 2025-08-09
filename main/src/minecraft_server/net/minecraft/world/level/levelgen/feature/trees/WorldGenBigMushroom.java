package net.minecraft.world.level.levelgen.feature.trees;

import java.util.Random;

import net.minecraft.world.level.World;
import net.minecraft.world.level.levelgen.feature.WorldGenerator;

public class WorldGenBigMushroom extends WorldGenerator {
	protected int mushroomType = -1;

	public WorldGenBigMushroom(int i1) {
		super(true);
		this.mushroomType = i1;
	}

	public WorldGenBigMushroom() {
		super(false);
	}

	public boolean generate(World world1, Random random2, int i3, int i4, int i5) {
		
		return false;
		// Not yet!
		
		/*
		int i6 = random2.nextInt(2);
		if(this.mushroomType >= 0) {
			i6 = this.mushroomType;
		}

		int i7 = random2.nextInt(3) + 4;
		boolean z8 = true;
		if(i4 >= 1 && i4 + i7 + 1 < 256) {
			int i9;
			int i11;
			int i12;
			int i13;
			for(i9 = i4; i9 <= i4 + 1 + i7; ++i9) {
				byte b10 = 3;
				if(i9 == i4) {
					b10 = 0;
				}

				for(i11 = i3 - b10; i11 <= i3 + b10 && z8; ++i11) {
					for(i12 = i5 - b10; i12 <= i5 + b10 && z8; ++i12) {
						if(i9 >= 0 && i9 < 256) {
							i13 = world1.getBlockId(i11, i9, i12);
							if(i13 != 0 && i13 != Block.leaves.blockID) {
								z8 = false;
							}
						} else {
							z8 = false;
						}
					}
				}
			}

			if(!z8) {
				return false;
			} else {
				i9 = world1.getBlockId(i3, i4 - 1, i5);
				if(i9 != Block.leaves.blockID && i9 != Block.dirt.blockID && i9 != Block.grass.blockID && i9 != Block.mycelium.blockID && i9 != Block.netherrack.blockID ) {
					return false;
					
				} else {
					this.setBlockAndMetadata(world1, i3, i4 - 1, i5, Block.dirt.blockID, 0);
					int i16 = i4 + i7;
					if(i6 == 1) {
						i16 = i4 + i7 - 3;
					}

					for(i11 = i16; i11 <= i4 + i7; ++i11) {
						i12 = 1;
						if(i11 < i4 + i7) {
							++i12;
						}

						if(i6 == 0) {
							i12 = 3;
						}

						for(i13 = i3 - i12; i13 <= i3 + i12; ++i13) {
							for(int i14 = i5 - i12; i14 <= i5 + i12; ++i14) {
								int i15 = 5;
								if(i13 == i3 - i12) {
									--i15;
								}

								if(i13 == i3 + i12) {
									++i15;
								}

								if(i14 == i5 - i12) {
									i15 -= 3;
								}

								if(i14 == i5 + i12) {
									i15 += 3;
								}

								if(i6 == 0 || i11 < i4 + i7) {
									if((i13 == i3 - i12 || i13 == i3 + i12) && (i14 == i5 - i12 || i14 == i5 + i12)) {
										continue;
									}

									if(i13 == i3 - (i12 - 1) && i14 == i5 - i12) {
										i15 = 1;
									}

									if(i13 == i3 - i12 && i14 == i5 - (i12 - 1)) {
										i15 = 1;
									}

									if(i13 == i3 + (i12 - 1) && i14 == i5 - i12) {
										i15 = 3;
									}

									if(i13 == i3 + i12 && i14 == i5 - (i12 - 1)) {
										i15 = 3;
									}

									if(i13 == i3 - (i12 - 1) && i14 == i5 + i12) {
										i15 = 7;
									}

									if(i13 == i3 - i12 && i14 == i5 + (i12 - 1)) {
										i15 = 7;
									}

									if(i13 == i3 + (i12 - 1) && i14 == i5 + i12) {
										i15 = 9;
									}

									if(i13 == i3 + i12 && i14 == i5 + (i12 - 1)) {
										i15 = 9;
									}
								}

								if(i15 == 5 && i11 < i4 + i7) {
									i15 = 0;
								}

								if((i15 != 0 || i4 >= i4 + i7 - 1) && !Block.opaqueCubeLookup[world1.getBlockId(i13, i11, i14)]) {
									this.setBlockAndMetadata(world1, i13, i11, i14, Block.mushroomCapBrown.blockID + i6, i15);
								}
							}
						}
					}

					for(i11 = 0; i11 < i7; ++i11) {
						i12 = world1.getBlockId(i3, i4 + i11, i5);
						if(!Block.opaqueCubeLookup[i12]) {
							this.setBlockAndMetadata(world1, i3, i4 + i11, i5, Block.mushroomCapBrown.blockID + i6, 10);
						}
					}

					return true;
				}
			}
		} else {
			return false;
		}
		*/
	}
}
