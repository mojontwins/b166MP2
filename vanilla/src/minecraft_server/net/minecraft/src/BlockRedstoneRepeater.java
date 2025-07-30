package net.minecraft.src;

import java.util.Random;

public class BlockRedstoneRepeater extends Block {
	public static final double[] field_22014_a = new double[]{-0.0625D, 0.0625D, 0.1875D, 0.3125D};
	private static final int[] field_22013_b = new int[]{1, 2, 3, 4};
	private final boolean field_22015_c;

	protected BlockRedstoneRepeater(int i1, boolean z2) {
		super(i1, 6, Material.circuits);
		this.field_22015_c = z2;
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.125F, 1.0F);
	}

	public boolean func_28025_b() {
		return false;
	}

	public boolean canPlaceBlockAt(World world1, int i2, int i3, int i4) {
		return !world1.isBlockOpaqueCube(i2, i3 - 1, i4) ? false : super.canPlaceBlockAt(world1, i2, i3, i4);
	}

	public boolean canBlockStay(World world1, int i2, int i3, int i4) {
		return !world1.isBlockOpaqueCube(i2, i3 - 1, i4) ? false : super.canBlockStay(world1, i2, i3, i4);
	}

	public void updateTick(World world1, int i2, int i3, int i4, Random random5) {
		int i6 = world1.getBlockMetadata(i2, i3, i4);
		boolean z7 = this.func_22012_g(world1, i2, i3, i4, i6);
		if(this.field_22015_c && !z7) {
			world1.setBlockAndMetadataWithNotify(i2, i3, i4, Block.redstoneRepeaterIdle.blockID, i6);
		} else if(!this.field_22015_c) {
			world1.setBlockAndMetadataWithNotify(i2, i3, i4, Block.redstoneRepeaterActive.blockID, i6);
			if(!z7) {
				int i8 = (i6 & 12) >> 2;
				world1.scheduleUpdateTick(i2, i3, i4, Block.redstoneRepeaterActive.blockID, field_22013_b[i8] * 2);
			}
		}

	}

	public int getBlockTextureFromSideAndMetadata(int i1, int i2) {
		return i1 == 0 ? (this.field_22015_c ? 99 : 115) : (i1 == 1 ? (this.field_22015_c ? 147 : 131) : 5);
	}

	public int getBlockTextureFromSide(int i1) {
		return this.getBlockTextureFromSideAndMetadata(i1, 0);
	}

	public boolean isIndirectlyPoweringTo(World world1, int i2, int i3, int i4, int i5) {
		return this.isPoweringTo(world1, i2, i3, i4, i5);
	}

	public boolean isPoweringTo(IBlockAccess iBlockAccess1, int i2, int i3, int i4, int i5) {
		if(!this.field_22015_c) {
			return false;
		} else {
			int i6 = iBlockAccess1.getBlockMetadata(i2, i3, i4) & 3;
			return i6 == 0 && i5 == 3 ? true : (i6 == 1 && i5 == 4 ? true : (i6 == 2 && i5 == 2 ? true : i6 == 3 && i5 == 5));
		}
	}

	public void onNeighborBlockChange(World world1, int i2, int i3, int i4, int i5) {
		if(!this.canBlockStay(world1, i2, i3, i4)) {
			this.dropBlockAsItem(world1, i2, i3, i4, world1.getBlockMetadata(i2, i3, i4));
			world1.setBlockWithNotify(i2, i3, i4, 0);
		} else {
			int i6 = world1.getBlockMetadata(i2, i3, i4);
			boolean z7 = this.func_22012_g(world1, i2, i3, i4, i6);
			int i8 = (i6 & 12) >> 2;
			if(this.field_22015_c && !z7) {
				world1.scheduleUpdateTick(i2, i3, i4, this.blockID, field_22013_b[i8] * 2);
			} else if(!this.field_22015_c && z7) {
				world1.scheduleUpdateTick(i2, i3, i4, this.blockID, field_22013_b[i8] * 2);
			}

		}
	}

	private boolean func_22012_g(World world1, int i2, int i3, int i4, int i5) {
		int i6 = i5 & 3;
		switch(i6) {
		case 0:
			return world1.isBlockIndirectlyProvidingPowerTo(i2, i3, i4 + 1, 3);
		case 1:
			return world1.isBlockIndirectlyProvidingPowerTo(i2 - 1, i3, i4, 4);
		case 2:
			return world1.isBlockIndirectlyProvidingPowerTo(i2, i3, i4 - 1, 2);
		case 3:
			return world1.isBlockIndirectlyProvidingPowerTo(i2 + 1, i3, i4, 5);
		default:
			return false;
		}
	}

	public boolean blockActivated(World world1, int i2, int i3, int i4, EntityPlayer entityPlayer5) {
		int i6 = world1.getBlockMetadata(i2, i3, i4);
		int i7 = (i6 & 12) >> 2;
		i7 = i7 + 1 << 2 & 12;
		world1.setBlockMetadataWithNotify(i2, i3, i4, i7 | i6 & 3);
		return true;
	}

	public boolean canProvidePower() {
		return false;
	}

	public void onBlockPlacedBy(World world1, int i2, int i3, int i4, EntityLiving entityLiving5) {
		int i6 = ((MathHelper.floor_double((double)(entityLiving5.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3) + 2) % 4;
		world1.setBlockMetadataWithNotify(i2, i3, i4, i6);
		boolean z7 = this.func_22012_g(world1, i2, i3, i4, i6);
		if(z7) {
			world1.scheduleUpdateTick(i2, i3, i4, this.blockID, 1);
		}

	}

	public void onBlockAdded(World world1, int i2, int i3, int i4) {
		world1.notifyBlocksOfNeighborChange(i2 + 1, i3, i4, this.blockID);
		world1.notifyBlocksOfNeighborChange(i2 - 1, i3, i4, this.blockID);
		world1.notifyBlocksOfNeighborChange(i2, i3, i4 + 1, this.blockID);
		world1.notifyBlocksOfNeighborChange(i2, i3, i4 - 1, this.blockID);
		world1.notifyBlocksOfNeighborChange(i2, i3 - 1, i4, this.blockID);
		world1.notifyBlocksOfNeighborChange(i2, i3 + 1, i4, this.blockID);
	}

	public boolean isOpaqueCube() {
		return false;
	}

	public int idDropped(int i1, Random random2) {
		return Item.redstoneRepeater.shiftedIndex;
	}
}
