package net.minecraft.world.level.tile;

import java.util.Random;

import net.minecraft.src.MathHelper;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.World;
import net.minecraft.world.level.material.Material;

public class BlockRedstoneRepeater extends BlockDirectional {
	public static final double[] repeaterTorchOffset = new double[]{-0.0625D, 0.0625D, 0.1875D, 0.3125D};
	private static final int[] repeaterState = new int[]{1, 2, 3, 4};
	private final boolean isRepeaterPowered;

	protected BlockRedstoneRepeater(int blockID, boolean powered) {
		super(blockID, 6, Material.circuits);
		this.isRepeaterPowered = powered;
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.125F, 1.0F);
	}

	public boolean renderAsNormalBlock() {
		return false;
	}

	public boolean canPlaceBlockAt(World world, int x, int y, int z) {
		Block block = world.getBlock(x, y - 1, z);
		if(block != null && !block.hasSolidTop(world.getBlockMetadata(x, y - 1, z))) return false;
		return super.canPlaceBlockAt(world, x, y, z);
	}

	public boolean canBlockStay(World world, int x, int y, int z) {
		Block block = world.getBlock(x, y - 1, z);
		return block != null && block.hasSolidTop(world.getBlockMetadata(x, y - 1, z));
	}

	public void updateTick(World world, int x, int y, int z, Random rand) {
		int meta = world.getBlockMetadata(x, y, z);
		boolean it = this.ignoreTick(world, x, y, z, meta);
		if(this.isRepeaterPowered && !it) {
			world.setBlockAndMetadataWithNotify(x, y, z, Block.redstoneRepeaterIdle.blockID, meta);
		} else if(!this.isRepeaterPowered) {
			world.setBlockAndMetadataWithNotify(x, y, z, Block.redstoneRepeaterActive.blockID, meta);
			if(!it) {
				int state = (meta & 12) >> 2;
				world.scheduleBlockUpdate(x, y, z, Block.redstoneRepeaterActive.blockID, repeaterState[state] * 2);
			}
		}

	}

	@Override
	public int getTextureTop() {
		return this.isRepeaterPowered ? 147 : 131;
	}

	@Override
	public int getTextureBottom() {
		return this.isRepeaterPowered ? 99 : 115;
	}

	@Override
	public int getTextureFront() {
		return 5;
	}

	@Override
	public int getTextureSide() {
		return 5;
	}

	@Override
	public int getTextureBack() {
		return 5;
	}
	
	public boolean shouldSideBeRendered(IBlockAccess world, int x, int y, int z, int side) {
		return side != 0 && side != 1;
	}

	public int getRenderType() {
		return 15;
	}

	public int getBlockTextureFromSide(int side) {
		return this.getBlockTextureFromSideAndMetadata(side, 0);
	}

	public boolean isIndirectlyPoweringTo(World world, int i2, int i3, int i4, int i5) {
		return this.isPoweringTo(world, i2, i3, i4, i5);
	}

	public boolean isPoweringTo(IBlockAccess iBlockAccess1, int i2, int i3, int i4, int i5) {
		if(!this.isRepeaterPowered) {
			return false;
		} else {
			int i6 = getDirection(iBlockAccess1.getBlockMetadata(i2, i3, i4));
			return i6 == 0 && i5 == 3 ? true : (i6 == 1 && i5 == 4 ? true : (i6 == 2 && i5 == 2 ? true : i6 == 3 && i5 == 5));
		}
	}

	public void onNeighborBlockChange(World world, int i2, int i3, int i4, int i5) {
		if(!this.canBlockStay(world, i2, i3, i4)) {
			this.dropBlockAsItem(world, i2, i3, i4, world.getBlockMetadata(i2, i3, i4), 0);
			world.setBlockWithNotify(i2, i3, i4, 0);
			world.notifyBlocksOfNeighborChange(i2 + 1, i3, i4, this.blockID);
			world.notifyBlocksOfNeighborChange(i2 - 1, i3, i4, this.blockID);
			world.notifyBlocksOfNeighborChange(i2, i3, i4 + 1, this.blockID);
			world.notifyBlocksOfNeighborChange(i2, i3, i4 - 1, this.blockID);
			world.notifyBlocksOfNeighborChange(i2, i3 - 1, i4, this.blockID);
			world.notifyBlocksOfNeighborChange(i2, i3 + 1, i4, this.blockID);
		} else {
			int i6 = world.getBlockMetadata(i2, i3, i4);
			boolean z7 = this.ignoreTick(world, i2, i3, i4, i6);
			int i8 = (i6 & 12) >> 2;
			if(this.isRepeaterPowered && !z7) {
				world.scheduleBlockUpdate(i2, i3, i4, this.blockID, repeaterState[i8] * 2);
			} else if(!this.isRepeaterPowered && z7) {
				world.scheduleBlockUpdate(i2, i3, i4, this.blockID, repeaterState[i8] * 2);
			}

		}
	}

	private boolean ignoreTick(World world, int i2, int i3, int i4, int i5) {
		int i6 = getDirection(i5);
		switch(i6) {
		case 0:
			return world.isBlockIndirectlyProvidingPowerTo(i2, i3, i4 + 1, 3) || world.getBlockId(i2, i3, i4 + 1) == Block.redstoneWire.blockID && world.getBlockMetadata(i2, i3, i4 + 1) > 0;
		case 1:
			return world.isBlockIndirectlyProvidingPowerTo(i2 - 1, i3, i4, 4) || world.getBlockId(i2 - 1, i3, i4) == Block.redstoneWire.blockID && world.getBlockMetadata(i2 - 1, i3, i4) > 0;
		case 2:
			return world.isBlockIndirectlyProvidingPowerTo(i2, i3, i4 - 1, 2) || world.getBlockId(i2, i3, i4 - 1) == Block.redstoneWire.blockID && world.getBlockMetadata(i2, i3, i4 - 1) > 0;
		case 3:
			return world.isBlockIndirectlyProvidingPowerTo(i2 + 1, i3, i4, 5) || world.getBlockId(i2 + 1, i3, i4) == Block.redstoneWire.blockID && world.getBlockMetadata(i2 + 1, i3, i4) > 0;
		default:
			return false;
		}
	}

	public boolean blockActivated(World world, int i2, int i3, int i4, EntityPlayer entityPlayer5) {
		int i6 = world.getBlockMetadata(i2, i3, i4);
		int i7 = (i6 & 12) >> 2;
		i7 = i7 + 1 << 2 & 12;
		world.setBlockMetadataWithNotify(i2, i3, i4, i7 | i6 & 3);
		return true;
	}

	public boolean canProvidePower() {
		return true;
	}

	public void onBlockPlacedBy(World world, int i2, int i3, int i4, EntityLiving entityLiving) {
		int i6 = ((MathHelper.floor_double((double)(entityLiving.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3) + 2) % 4;
		world.setBlockMetadataWithNotify(i2, i3, i4, i6);
		boolean z7 = this.ignoreTick(world, i2, i3, i4, i6);
		if(z7) {
			world.scheduleBlockUpdate(i2, i3, i4, this.blockID, 1);
		}

	}

	public void onBlockAdded(World world, int i2, int i3, int i4) {
		world.notifyBlocksOfNeighborChange(i2 + 1, i3, i4, this.blockID);
		world.notifyBlocksOfNeighborChange(i2 - 1, i3, i4, this.blockID);
		world.notifyBlocksOfNeighborChange(i2, i3, i4 + 1, this.blockID);
		world.notifyBlocksOfNeighborChange(i2, i3, i4 - 1, this.blockID);
		world.notifyBlocksOfNeighborChange(i2, i3 - 1, i4, this.blockID);
		world.notifyBlocksOfNeighborChange(i2, i3 + 1, i4, this.blockID);
	}

	public void onBlockDestroyedByPlayer(World world, int i2, int i3, int i4, int i5) {
		if(this.isRepeaterPowered) {
			world.notifyBlocksOfNeighborChange(i2 + 1, i3, i4, this.blockID);
			world.notifyBlocksOfNeighborChange(i2 - 1, i3, i4, this.blockID);
			world.notifyBlocksOfNeighborChange(i2, i3, i4 + 1, this.blockID);
			world.notifyBlocksOfNeighborChange(i2, i3, i4 - 1, this.blockID);
			world.notifyBlocksOfNeighborChange(i2, i3 - 1, i4, this.blockID);
			world.notifyBlocksOfNeighborChange(i2, i3 + 1, i4, this.blockID);
		}

		super.onBlockDestroyedByPlayer(world, i2, i3, i4, i5);
	}

	public boolean isOpaqueCube() {
		return false;
	}

	public int idDropped(int i1, Random random2, int i3) {
		return Item.redstoneRepeater.shiftedIndex;
	}

	public void randomDisplayTick(World world, int i2, int i3, int i4, Random random5) {
		if(this.isRepeaterPowered) {
			int i6 = world.getBlockMetadata(i2, i3, i4);
			int i7 = getDirection(i6);
			double d8 = (double)((float)i2 + 0.5F) + (double)(random5.nextFloat() - 0.5F) * 0.2D;
			double d10 = (double)((float)i3 + 0.4F) + (double)(random5.nextFloat() - 0.5F) * 0.2D;
			double d12 = (double)((float)i4 + 0.5F) + (double)(random5.nextFloat() - 0.5F) * 0.2D;
			double d14 = 0.0D;
			double d16 = 0.0D;
			if(random5.nextInt(2) == 0) {
				switch(i7) {
				case 0:
					d16 = -0.3125D;
					break;
				case 1:
					d14 = 0.3125D;
					break;
				case 2:
					d16 = 0.3125D;
					break;
				case 3:
					d14 = -0.3125D;
				}
			} else {
				int i18 = (i6 & 12) >> 2;
				switch(i7) {
				case 0:
					d16 = repeaterTorchOffset[i18];
					break;
				case 1:
					d14 = -repeaterTorchOffset[i18];
					break;
				case 2:
					d16 = -repeaterTorchOffset[i18];
					break;
				case 3:
					d14 = repeaterTorchOffset[i18];
				}
			}

			world.spawnParticle("reddust", d8 + d14, d10, d12 + d16, 0.0D, 0.0D, 0.0D);
		}
	}

}
