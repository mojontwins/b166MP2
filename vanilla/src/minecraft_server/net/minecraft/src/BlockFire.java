package net.minecraft.src;

import java.util.Random;

public class BlockFire extends Block {
	private int[] chanceToEncourageFire = new int[256];
	private int[] abilityToCatchFire = new int[256];

	protected BlockFire(int i1, int i2) {
		super(i1, i2, Material.fire);
		this.setTickOnLoad(true);
	}

	public void func_28028_f() {
		this.setBurnRate(Block.planks.blockID, 5, 20);
		this.setBurnRate(Block.fence.blockID, 5, 20);
		this.setBurnRate(Block.stairCompactPlanks.blockID, 5, 20);
		this.setBurnRate(Block.wood.blockID, 5, 5);
		this.setBurnRate(Block.leaves.blockID, 30, 60);
		this.setBurnRate(Block.bookShelf.blockID, 30, 20);
		this.setBurnRate(Block.tnt.blockID, 15, 100);
		this.setBurnRate(Block.field_9031_X.blockID, 60, 100);
		this.setBurnRate(Block.cloth.blockID, 30, 60);
	}

	private void setBurnRate(int i1, int i2, int i3) {
		this.chanceToEncourageFire[i1] = i2;
		this.abilityToCatchFire[i1] = i3;
	}

	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world1, int i2, int i3, int i4) {
		return null;
	}

	public boolean isOpaqueCube() {
		return false;
	}

	public boolean func_28025_b() {
		return false;
	}

	public int quantityDropped(Random random1) {
		return 0;
	}

	public int tickRate() {
		return 40;
	}

	public void updateTick(World world1, int i2, int i3, int i4, Random random5) {
		boolean z6 = world1.getBlockId(i2, i3 - 1, i4) == Block.bloodStone.blockID;
		if(!this.canPlaceBlockAt(world1, i2, i3, i4)) {
			world1.setBlockWithNotify(i2, i3, i4, 0);
		}

		if(!z6 && world1.func_27068_v() && (world1.func_27072_q(i2, i3, i4) || world1.func_27072_q(i2 - 1, i3, i4) || world1.func_27072_q(i2 + 1, i3, i4) || world1.func_27072_q(i2, i3, i4 - 1) || world1.func_27072_q(i2, i3, i4 + 1))) {
			world1.setBlockWithNotify(i2, i3, i4, 0);
		} else {
			int i7 = world1.getBlockMetadata(i2, i3, i4);
			if(i7 < 15) {
				world1.setBlockMetadata(i2, i3, i4, i7 + random5.nextInt(3) / 2);
			}

			world1.scheduleUpdateTick(i2, i3, i4, this.blockID, this.tickRate());
			if(!z6 && !this.func_268_g(world1, i2, i3, i4)) {
				if(!world1.isBlockOpaqueCube(i2, i3 - 1, i4) || i7 > 3) {
					world1.setBlockWithNotify(i2, i3, i4, 0);
				}

			} else if(!z6 && !this.canBlockCatchFire(world1, i2, i3 - 1, i4) && i7 == 15 && random5.nextInt(4) == 0) {
				world1.setBlockWithNotify(i2, i3, i4, 0);
			} else {
				this.tryToCatchBlockOnFire(world1, i2 + 1, i3, i4, 300, random5, i7);
				this.tryToCatchBlockOnFire(world1, i2 - 1, i3, i4, 300, random5, i7);
				this.tryToCatchBlockOnFire(world1, i2, i3 - 1, i4, 250, random5, i7);
				this.tryToCatchBlockOnFire(world1, i2, i3 + 1, i4, 250, random5, i7);
				this.tryToCatchBlockOnFire(world1, i2, i3, i4 - 1, 300, random5, i7);
				this.tryToCatchBlockOnFire(world1, i2, i3, i4 + 1, 300, random5, i7);

				for(int i8 = i2 - 1; i8 <= i2 + 1; ++i8) {
					for(int i9 = i4 - 1; i9 <= i4 + 1; ++i9) {
						for(int i10 = i3 - 1; i10 <= i3 + 4; ++i10) {
							if(i8 != i2 || i10 != i3 || i9 != i4) {
								int i11 = 100;
								if(i10 > i3 + 1) {
									i11 += (i10 - (i3 + 1)) * 100;
								}

								int i12 = this.getChanceOfNeighborsEncouragingFire(world1, i8, i10, i9);
								if(i12 > 0) {
									int i13 = (i12 + 40) / (i7 + 30);
									if(i13 > 0 && random5.nextInt(i11) <= i13 && (!world1.func_27068_v() || !world1.func_27072_q(i8, i10, i9)) && !world1.func_27072_q(i8 - 1, i10, i4) && !world1.func_27072_q(i8 + 1, i10, i9) && !world1.func_27072_q(i8, i10, i9 - 1) && !world1.func_27072_q(i8, i10, i9 + 1)) {
										int i14 = i7 + random5.nextInt(5) / 4;
										if(i14 > 15) {
											i14 = 15;
										}

										world1.setBlockAndMetadataWithNotify(i8, i10, i9, this.blockID, i14);
									}
								}
							}
						}
					}
				}

			}
		}
	}

	private void tryToCatchBlockOnFire(World world1, int i2, int i3, int i4, int i5, Random random6, int i7) {
		int i8 = this.abilityToCatchFire[world1.getBlockId(i2, i3, i4)];
		if(random6.nextInt(i5) < i8) {
			boolean z9 = world1.getBlockId(i2, i3, i4) == Block.tnt.blockID;
			if(random6.nextInt(i7 + 10) < 5 && !world1.func_27072_q(i2, i3, i4)) {
				int i10 = i7 + random6.nextInt(5) / 4;
				if(i10 > 15) {
					i10 = 15;
				}

				world1.setBlockAndMetadataWithNotify(i2, i3, i4, this.blockID, i10);
			} else {
				world1.setBlockWithNotify(i2, i3, i4, 0);
			}

			if(z9) {
				Block.tnt.onBlockDestroyedByPlayer(world1, i2, i3, i4, 0);
			}
		}

	}

	private boolean func_268_g(World world1, int i2, int i3, int i4) {
		return this.canBlockCatchFire(world1, i2 + 1, i3, i4) ? true : (this.canBlockCatchFire(world1, i2 - 1, i3, i4) ? true : (this.canBlockCatchFire(world1, i2, i3 - 1, i4) ? true : (this.canBlockCatchFire(world1, i2, i3 + 1, i4) ? true : (this.canBlockCatchFire(world1, i2, i3, i4 - 1) ? true : this.canBlockCatchFire(world1, i2, i3, i4 + 1)))));
	}

	private int getChanceOfNeighborsEncouragingFire(World world1, int i2, int i3, int i4) {
		byte b5 = 0;
		if(!world1.isAirBlock(i2, i3, i4)) {
			return 0;
		} else {
			int i6 = this.getChanceToEncourageFire(world1, i2 + 1, i3, i4, b5);
			i6 = this.getChanceToEncourageFire(world1, i2 - 1, i3, i4, i6);
			i6 = this.getChanceToEncourageFire(world1, i2, i3 - 1, i4, i6);
			i6 = this.getChanceToEncourageFire(world1, i2, i3 + 1, i4, i6);
			i6 = this.getChanceToEncourageFire(world1, i2, i3, i4 - 1, i6);
			i6 = this.getChanceToEncourageFire(world1, i2, i3, i4 + 1, i6);
			return i6;
		}
	}

	public boolean isCollidable() {
		return false;
	}

	public boolean canBlockCatchFire(IBlockAccess iBlockAccess1, int i2, int i3, int i4) {
		return this.chanceToEncourageFire[iBlockAccess1.getBlockId(i2, i3, i4)] > 0;
	}

	public int getChanceToEncourageFire(World world1, int i2, int i3, int i4, int i5) {
		int i6 = this.chanceToEncourageFire[world1.getBlockId(i2, i3, i4)];
		return i6 > i5 ? i6 : i5;
	}

	public boolean canPlaceBlockAt(World world1, int i2, int i3, int i4) {
		return world1.isBlockOpaqueCube(i2, i3 - 1, i4) || this.func_268_g(world1, i2, i3, i4);
	}

	public void onNeighborBlockChange(World world1, int i2, int i3, int i4, int i5) {
		if(!world1.isBlockOpaqueCube(i2, i3 - 1, i4) && !this.func_268_g(world1, i2, i3, i4)) {
			world1.setBlockWithNotify(i2, i3, i4, 0);
		}
	}

	public void onBlockAdded(World world1, int i2, int i3, int i4) {
		if(world1.getBlockId(i2, i3 - 1, i4) != Block.obsidian.blockID || !Block.portal.tryToCreatePortal(world1, i2, i3, i4)) {
			if(!world1.isBlockOpaqueCube(i2, i3 - 1, i4) && !this.func_268_g(world1, i2, i3, i4)) {
				world1.setBlockWithNotify(i2, i3, i4, 0);
			} else {
				world1.scheduleUpdateTick(i2, i3, i4, this.blockID, this.tickRate());
			}
		}
	}
}
