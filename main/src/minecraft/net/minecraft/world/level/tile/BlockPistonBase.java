package net.minecraft.world.level.tile;

import net.minecraft.src.MathHelper;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.item.EntityMovingPiston;
import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.level.World;
import net.minecraft.world.level.material.Material;

public class BlockPistonBase extends Block {
	private boolean sticky;

	public BlockPistonBase(int i1, int i2, boolean z3) {
		super(i1, Material.iron);
		this.sticky = z3;
		this.blockIndexInTexture = i2;
	}

	private void toggleBlock(World world1, int i2, int i3, int i4, boolean z5) {
		int i6 = world1.getBlockMetadata(i2, i3, i4);
		if((i6 & 8) == 0) {
			if(!z5) {
				return;
			}
		} else if(z5) {
			return;
		}

		i6 &= 7;
		int i7 = i2;
		int i8 = i3;
		int i9 = i4;
		if(i6 == 0) {
			i8 = i3 - 1;
		}

		if(i6 == 1) {
			++i8;
		}

		if(i6 == 2) {
			i9 = i4 - 1;
		}

		if(i6 == 3) {
			++i9;
		}

		if(i6 == 4) {
			i7 = i2 - 1;
		}

		if(i6 == 5) {
			++i7;
		}

		int i10 = world1.getBlockId(i7, i8, i9);
		if((i10 == Block.classicPiston.blockID || i10 == Block.classicStickyPiston.blockID) && !z5) {
			BlockPiston.resetBase = false;
			EntityMovingPiston.buildRetractingPistons(world1, i7, i8, i9, i2, i3, i4, i6, this.sticky);
			world1.setBlockWithNotify(i7, i8, i9, 0);
			BlockPiston.resetBase = true;
		} else if(z5) {
			EntityMovingPiston movingPiston11 = new EntityMovingPiston(world1, i2, i3, i4, this.sticky);
			if(!EntityMovingPiston.buildPistons(movingPiston11, i7, i8, i9, i6)) {
				return;
			}

			world1.setBlockMetadataWithNotify(i2, i3, i4, i6 | 8);
			world1.markBlockAsNeedsUpdate(i2, i3, i4);
		}

	}

	public void onNeighborBlockChange(World world1, int i2, int i3, int i4, int i5) {
		if(i5 > 0 && Block.blocksList[i5].canProvidePower()) {
			boolean z6 = world1.isBlockIndirectlyGettingPowered(i2, i3, i4) || world1.isBlockIndirectlyGettingPowered(i2, i3 + 1, i4);
			this.toggleBlock(world1, i2, i3, i4, z6);
		}
	}

	public boolean isOpaqueCube() {
		return false;
	}

	public boolean renderAsNormalBlock() {
		return false;
	}

	public int getRenderType() {
		return 109;
	}

	private int getData(World world1, int i2, int i3, int i4, EntityLiving entityLiving5) {
		if(MathHelper.abs((float)entityLiving5.posX - (float)i2) < 2.0F && MathHelper.abs((float)entityLiving5.posZ - (float)i4) < 2.0F) {
			if(entityLiving5.posY - (double)i3 > 2.0D) {
				return 1;
			}

			if((double)i3 - entityLiving5.posY > 0.0D) {
				return 0;
			}
		}

		int i6 = MathHelper.floor_double((double)(entityLiving5.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
		if(i6 == 0) {
			return 2;
		} else if(i6 == 1) {
			return 5;
		} else if(i6 == 2) {
			return 3;
		} else if(i6 == 3) {
			return 4;
		} else {
			throw new IllegalStateException("Impossible values!");
		}
	}

	public boolean blockActivated(World world1, int i2, int i3, int i4, EntityPlayer entityPlayer5) {
		if(world1.isRemote) {
			return true;
		} else {
			int i6 = world1.getBlockMetadata(i2, i3, i4);
			if((i6 & 8) != 0) {
				return false;
			} else {
				int i7 = this.getData(world1, i2, i3, i4, entityPlayer5);
				if(i7 == i6) {
					return false;
				} else {
					world1.setBlockMetadataWithNotify(i2, i3, i4, i7);
					world1.markBlockAsNeedsUpdate(i2, i3, i4);
					return true;
				}
			}
		}
	}

	public void onBlockPlacedBy(World world1, int i2, int i3, int i4, EntityLiving entityLiving5) {
		world1.setBlockMetadata(i2, i3, i4, this.getData(world1, i2, i3, i4, entityLiving5));
		this.onNeighborBlockChange(world1, i2, i3, i4, Block.redstoneWire.blockID);
	}

	public void onPistonPushed(World world1, int i2, int i3, int i4) {
		this.onNeighborBlockChange(world1, i2, i3, i4, Block.redstoneWire.blockID);
	}
}
