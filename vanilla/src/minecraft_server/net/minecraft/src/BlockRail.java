package net.minecraft.src;

import java.util.Random;

public class BlockRail extends Block {
	private final boolean field_27034_a;

	public static final boolean func_27029_g(World world0, int i1, int i2, int i3) {
		int i4 = world0.getBlockId(i1, i2, i3);
		return i4 == Block.minecartTrack.blockID || i4 == Block.railPowered.blockID || i4 == Block.railDetector.blockID;
	}

	public static final boolean func_27030_c(int i0) {
		return i0 == Block.minecartTrack.blockID || i0 == Block.railPowered.blockID || i0 == Block.railDetector.blockID;
	}

	protected BlockRail(int i1, int i2, boolean z3) {
		super(i1, i2, Material.circuits);
		this.field_27034_a = z3;
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.125F, 1.0F);
	}

	public boolean func_27028_d() {
		return this.field_27034_a;
	}

	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world1, int i2, int i3, int i4) {
		return null;
	}

	public boolean isOpaqueCube() {
		return false;
	}

	public MovingObjectPosition collisionRayTrace(World world1, int i2, int i3, int i4, Vec3D vec3D5, Vec3D vec3D6) {
		this.setBlockBoundsBasedOnState(world1, i2, i3, i4);
		return super.collisionRayTrace(world1, i2, i3, i4, vec3D5, vec3D6);
	}

	public void setBlockBoundsBasedOnState(IBlockAccess iBlockAccess1, int i2, int i3, int i4) {
		int i5 = iBlockAccess1.getBlockMetadata(i2, i3, i4);
		if(i5 >= 2 && i5 <= 5) {
			this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.625F, 1.0F);
		} else {
			this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.125F, 1.0F);
		}

	}

	public int getBlockTextureFromSideAndMetadata(int i1, int i2) {
		if(this.field_27034_a) {
			if(this.blockID == Block.railPowered.blockID && (i2 & 8) == 0) {
				return this.blockIndexInTexture - 16;
			}
		} else if(i2 >= 6) {
			return this.blockIndexInTexture - 16;
		}

		return this.blockIndexInTexture;
	}

	public boolean func_28025_b() {
		return false;
	}

	public int quantityDropped(Random random1) {
		return 1;
	}

	public boolean canPlaceBlockAt(World world1, int i2, int i3, int i4) {
		return world1.isBlockOpaqueCube(i2, i3 - 1, i4);
	}

	public void onBlockAdded(World world1, int i2, int i3, int i4) {
		if(!world1.singleplayerWorld) {
			this.func_4038_g(world1, i2, i3, i4, true);
		}

	}

	public void onNeighborBlockChange(World world1, int i2, int i3, int i4, int i5) {
		if(!world1.singleplayerWorld) {
			int i6 = world1.getBlockMetadata(i2, i3, i4);
			int i7 = i6;
			if(this.field_27034_a) {
				i7 = i6 & 7;
			}

			boolean z8 = false;
			if(!world1.isBlockOpaqueCube(i2, i3 - 1, i4)) {
				z8 = true;
			}

			if(i7 == 2 && !world1.isBlockOpaqueCube(i2 + 1, i3, i4)) {
				z8 = true;
			}

			if(i7 == 3 && !world1.isBlockOpaqueCube(i2 - 1, i3, i4)) {
				z8 = true;
			}

			if(i7 == 4 && !world1.isBlockOpaqueCube(i2, i3, i4 - 1)) {
				z8 = true;
			}

			if(i7 == 5 && !world1.isBlockOpaqueCube(i2, i3, i4 + 1)) {
				z8 = true;
			}

			if(z8) {
				this.dropBlockAsItem(world1, i2, i3, i4, world1.getBlockMetadata(i2, i3, i4));
				world1.setBlockWithNotify(i2, i3, i4, 0);
			} else if(this.blockID == Block.railPowered.blockID) {
				boolean z9 = world1.isBlockIndirectlyGettingPowered(i2, i3, i4) || world1.isBlockIndirectlyGettingPowered(i2, i3 + 1, i4);
				z9 = z9 || this.func_27032_a(world1, i2, i3, i4, i6, true, 0) || this.func_27032_a(world1, i2, i3, i4, i6, false, 0);
				boolean z10 = false;
				if(z9 && (i6 & 8) == 0) {
					world1.setBlockMetadataWithNotify(i2, i3, i4, i7 | 8);
					z10 = true;
				} else if(!z9 && (i6 & 8) != 0) {
					world1.setBlockMetadataWithNotify(i2, i3, i4, i7);
					z10 = true;
				}

				if(z10) {
					world1.notifyBlocksOfNeighborChange(i2, i3 - 1, i4, this.blockID);
					if(i7 == 2 || i7 == 3 || i7 == 4 || i7 == 5) {
						world1.notifyBlocksOfNeighborChange(i2, i3 + 1, i4, this.blockID);
					}
				}
			} else if(i5 > 0 && Block.blocksList[i5].canProvidePower() && !this.field_27034_a && RailLogic.getNAdjacentTracks(new RailLogic(this, world1, i2, i3, i4)) == 3) {
				this.func_4038_g(world1, i2, i3, i4, false);
			}

		}
	}

	private void func_4038_g(World world1, int i2, int i3, int i4, boolean z5) {
		if(!world1.singleplayerWorld) {
			(new RailLogic(this, world1, i2, i3, i4)).func_596_a(world1.isBlockIndirectlyGettingPowered(i2, i3, i4), z5);
		}
	}

	private boolean func_27032_a(World world1, int i2, int i3, int i4, int i5, boolean z6, int i7) {
		if(i7 >= 8) {
			return false;
		} else {
			int i8 = i5 & 7;
			boolean z9 = true;
			switch(i8) {
			case 0:
				if(z6) {
					++i4;
				} else {
					--i4;
				}
				break;
			case 1:
				if(z6) {
					--i2;
				} else {
					++i2;
				}
				break;
			case 2:
				if(z6) {
					--i2;
				} else {
					++i2;
					++i3;
					z9 = false;
				}

				i8 = 1;
				break;
			case 3:
				if(z6) {
					--i2;
					++i3;
					z9 = false;
				} else {
					++i2;
				}

				i8 = 1;
				break;
			case 4:
				if(z6) {
					++i4;
				} else {
					--i4;
					++i3;
					z9 = false;
				}

				i8 = 0;
				break;
			case 5:
				if(z6) {
					++i4;
					++i3;
					z9 = false;
				} else {
					--i4;
				}

				i8 = 0;
			}

			return this.func_27031_a(world1, i2, i3, i4, z6, i7, i8) ? true : z9 && this.func_27031_a(world1, i2, i3 - 1, i4, z6, i7, i8);
		}
	}

	private boolean func_27031_a(World world1, int i2, int i3, int i4, boolean z5, int i6, int i7) {
		int i8 = world1.getBlockId(i2, i3, i4);
		if(i8 == Block.railPowered.blockID) {
			int i9 = world1.getBlockMetadata(i2, i3, i4);
			int i10 = i9 & 7;
			if(i7 == 1 && (i10 == 0 || i10 == 4 || i10 == 5)) {
				return false;
			}

			if(i7 == 0 && (i10 == 1 || i10 == 2 || i10 == 3)) {
				return false;
			}

			if((i9 & 8) != 0) {
				if(!world1.isBlockIndirectlyGettingPowered(i2, i3, i4) && !world1.isBlockIndirectlyGettingPowered(i2, i3 + 1, i4)) {
					return this.func_27032_a(world1, i2, i3, i4, i9, z5, i6 + 1);
				}

				return true;
			}
		}

		return false;
	}

	static boolean func_27033_a(BlockRail blockRail0) {
		return blockRail0.field_27034_a;
	}
}
