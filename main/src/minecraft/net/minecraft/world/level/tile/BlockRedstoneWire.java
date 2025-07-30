package net.minecraft.world.level.tile;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import net.minecraft.world.Direction;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ChunkPosition;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.World;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.AxisAlignedBB;

public class BlockRedstoneWire extends Block {
	private boolean wiresProvidePower = true;
	private Set<ChunkPosition> blocksNeedingUpdate = new HashSet<ChunkPosition>();

	public BlockRedstoneWire(int i1, int i2) {
		super(i1, i2, Material.circuits);
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.0625F, 1.0F);
	}

	public int getBlockTextureFromSideAndMetadata(int i1, int i2) {
		return this.blockIndexInTexture;
	}

	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int i2, int i3, int i4) {
		return null;
	}

	public boolean isOpaqueCube() {
		return false;
	}

	public boolean renderAsNormalBlock() {
		return false;
	}

	public int getRenderType() {
		return 5;
	}

	public int colorMultiplier(IBlockAccess world, int i2, int i3, int i4) {
		return 8388608;
	}

	public boolean canPlaceBlockAt(World world, int x, int y, int z) {
		Block block = world.getBlock(x, y - 1, z);
		return block != null && block.supportsRedstone(world.getBlockMetadata(x, y - 1, z));
	}

	private void updateAndPropagateCurrentStrength(World world, int i2, int i3, int i4) {
		this.calculateCurrentChanges(world, i2, i3, i4, i2, i3, i4);
		ArrayList<ChunkPosition> arrayList5 = new ArrayList<ChunkPosition>(this.blocksNeedingUpdate);
		this.blocksNeedingUpdate.clear();

		for(int i6 = 0; i6 < arrayList5.size(); ++i6) {
			ChunkPosition chunkPosition7 = (ChunkPosition)arrayList5.get(i6);
			world.notifyBlocksOfNeighborChange(chunkPosition7.x, chunkPosition7.y, chunkPosition7.z, this.blockID);
		}

	}

	private void calculateCurrentChanges(World world, int i2, int i3, int i4, int i5, int i6, int i7) {
		int i8 = world.getBlockMetadata(i2, i3, i4);
		int i9 = 0;
		this.wiresProvidePower = false;
		boolean z10 = world.isBlockIndirectlyGettingPowered(i2, i3, i4);
		this.wiresProvidePower = true;
		int i11;
		int i12;
		int i13;
		if(z10) {
			i9 = 15;
		} else {
			for(i11 = 0; i11 < 4; ++i11) {
				i12 = i2;
				i13 = i4;
				if(i11 == 0) {
					i12 = i2 - 1;
				}

				if(i11 == 1) {
					++i12;
				}

				if(i11 == 2) {
					i13 = i4 - 1;
				}

				if(i11 == 3) {
					++i13;
				}

				if(i12 != i5 || i3 != i6 || i13 != i7) {
					i9 = this.getMaxCurrentStrength(world, i12, i3, i13, i9);
				}

				if(this.isBlockGood(world, i12, i3, i13) && !this.isBlockGood(world, i2, i3 + 1, i4)) {
					if(i12 != i5 || i3 + 1 != i6 || i13 != i7) {
						i9 = this.getMaxCurrentStrength(world, i12, i3 + 1, i13, i9);
					}
				} else if(!this.isBlockGood(world, i12, i3, i13) && (i12 != i5 || i3 - 1 != i6 || i13 != i7)) {
					i9 = this.getMaxCurrentStrength(world, i12, i3 - 1, i13, i9);
				}
			}

			if(i9 > 0) {
				--i9;
			} else {
				i9 = 0;
			}
		}

		if(i8 != i9) {
			world.editingBlocks = true;
			world.setBlockMetadataWithNotify(i2, i3, i4, i9);
			world.markBlocksDirty(i2, i3, i4, i2, i3, i4);
			world.editingBlocks = false;

			for(i11 = 0; i11 < 4; ++i11) {
				i12 = i2;
				i13 = i4;
				int i14 = i3 - 1;
				if(i11 == 0) {
					i12 = i2 - 1;
				}

				if(i11 == 1) {
					++i12;
				}

				if(i11 == 2) {
					i13 = i4 - 1;
				}

				if(i11 == 3) {
					++i13;
				}

				if(this.isBlockGood(world, i12, i3, i13)) {
					i14 += 2;
				}

				int i16 = this.getMaxCurrentStrength(world, i12, i3, i13, -1);
				i9 = world.getBlockMetadata(i2, i3, i4);
				if(i9 > 0) {
					--i9;
				}

				if(i16 >= 0 && i16 != i9) {
					this.calculateCurrentChanges(world, i12, i3, i13, i2, i3, i4);
				}

				i16 = this.getMaxCurrentStrength(world, i12, i14, i13, -1);
				i9 = world.getBlockMetadata(i2, i3, i4);
				if(i9 > 0) {
					--i9;
				}

				if(i16 >= 0 && i16 != i9) {
					this.calculateCurrentChanges(world, i12, i14, i13, i2, i3, i4);
				}
			}

			if(i8 < i9 || i9 == 0) {
				this.blocksNeedingUpdate.add(new ChunkPosition(i2, i3, i4));
				this.blocksNeedingUpdate.add(new ChunkPosition(i2 - 1, i3, i4));
				this.blocksNeedingUpdate.add(new ChunkPosition(i2 + 1, i3, i4));
				this.blocksNeedingUpdate.add(new ChunkPosition(i2, i3 - 1, i4));
				this.blocksNeedingUpdate.add(new ChunkPosition(i2, i3 + 1, i4));
				this.blocksNeedingUpdate.add(new ChunkPosition(i2, i3, i4 - 1));
				this.blocksNeedingUpdate.add(new ChunkPosition(i2, i3, i4 + 1));
			}
		}

	}

	private void notifyWireNeighborsOfNeighborChange(World world, int i2, int i3, int i4) {
		if(world.getBlockId(i2, i3, i4) == this.blockID) {
			world.notifyBlocksOfNeighborChange(i2, i3, i4, this.blockID);
			world.notifyBlocksOfNeighborChange(i2 - 1, i3, i4, this.blockID);
			world.notifyBlocksOfNeighborChange(i2 + 1, i3, i4, this.blockID);
			world.notifyBlocksOfNeighborChange(i2, i3, i4 - 1, this.blockID);
			world.notifyBlocksOfNeighborChange(i2, i3, i4 + 1, this.blockID);
			world.notifyBlocksOfNeighborChange(i2, i3 - 1, i4, this.blockID);
			world.notifyBlocksOfNeighborChange(i2, i3 + 1, i4, this.blockID);
		}
	}

	public void onBlockAdded(World world, int i2, int i3, int i4) {
		super.onBlockAdded(world, i2, i3, i4);
		if(!world.isRemote) {
			this.updateAndPropagateCurrentStrength(world, i2, i3, i4);
			world.notifyBlocksOfNeighborChange(i2, i3 + 1, i4, this.blockID);
			world.notifyBlocksOfNeighborChange(i2, i3 - 1, i4, this.blockID);
			this.notifyWireNeighborsOfNeighborChange(world, i2 - 1, i3, i4);
			this.notifyWireNeighborsOfNeighborChange(world, i2 + 1, i3, i4);
			this.notifyWireNeighborsOfNeighborChange(world, i2, i3, i4 - 1);
			this.notifyWireNeighborsOfNeighborChange(world, i2, i3, i4 + 1);
			if(this.isBlockGood(world, i2 - 1, i3, i4)) {
				this.notifyWireNeighborsOfNeighborChange(world, i2 - 1, i3 + 1, i4);
			} else {
				this.notifyWireNeighborsOfNeighborChange(world, i2 - 1, i3 - 1, i4);
			}

			if(this.isBlockGood(world, i2 + 1, i3, i4)) {
				this.notifyWireNeighborsOfNeighborChange(world, i2 + 1, i3 + 1, i4);
			} else {
				this.notifyWireNeighborsOfNeighborChange(world, i2 + 1, i3 - 1, i4);
			}

			if(this.isBlockGood(world, i2, i3, i4 - 1)) {
				this.notifyWireNeighborsOfNeighborChange(world, i2, i3 + 1, i4 - 1);
			} else {
				this.notifyWireNeighborsOfNeighborChange(world, i2, i3 - 1, i4 - 1);
			}

			if(this.isBlockGood(world, i2, i3, i4 + 1)) {
				this.notifyWireNeighborsOfNeighborChange(world, i2, i3 + 1, i4 + 1);
			} else {
				this.notifyWireNeighborsOfNeighborChange(world, i2, i3 - 1, i4 + 1);
			}

		}
	}

	public void onBlockRemoval(World world, int i2, int i3, int i4) {
		super.onBlockRemoval(world, i2, i3, i4);
		if(!world.isRemote) {
			world.notifyBlocksOfNeighborChange(i2, i3 + 1, i4, this.blockID);
			world.notifyBlocksOfNeighborChange(i2, i3 - 1, i4, this.blockID);
			world.notifyBlocksOfNeighborChange(i2 + 1, i3, i4, this.blockID);
			world.notifyBlocksOfNeighborChange(i2 - 1, i3, i4, this.blockID);
			world.notifyBlocksOfNeighborChange(i2, i3, i4 + 1, this.blockID);
			world.notifyBlocksOfNeighborChange(i2, i3, i4 - 1, this.blockID);
			this.updateAndPropagateCurrentStrength(world, i2, i3, i4);
			this.notifyWireNeighborsOfNeighborChange(world, i2 - 1, i3, i4);
			this.notifyWireNeighborsOfNeighborChange(world, i2 + 1, i3, i4);
			this.notifyWireNeighborsOfNeighborChange(world, i2, i3, i4 - 1);
			this.notifyWireNeighborsOfNeighborChange(world, i2, i3, i4 + 1);
			if(this.isBlockGood(world, i2 - 1, i3, i4)) {
				this.notifyWireNeighborsOfNeighborChange(world, i2 - 1, i3 + 1, i4);
			} else {
				this.notifyWireNeighborsOfNeighborChange(world, i2 - 1, i3 - 1, i4);
			}

			if(this.isBlockGood(world, i2 + 1, i3, i4)) {
				this.notifyWireNeighborsOfNeighborChange(world, i2 + 1, i3 + 1, i4);
			} else {
				this.notifyWireNeighborsOfNeighborChange(world, i2 + 1, i3 - 1, i4);
			}

			if(this.isBlockGood(world, i2, i3, i4 - 1)) {
				this.notifyWireNeighborsOfNeighborChange(world, i2, i3 + 1, i4 - 1);
			} else {
				this.notifyWireNeighborsOfNeighborChange(world, i2, i3 - 1, i4 - 1);
			}

			if(this.isBlockGood(world, i2, i3, i4 + 1)) {
				this.notifyWireNeighborsOfNeighborChange(world, i2, i3 + 1, i4 + 1);
			} else {
				this.notifyWireNeighborsOfNeighborChange(world, i2, i3 - 1, i4 + 1);
			}

		}
	}

	private int getMaxCurrentStrength(World world, int i2, int i3, int i4, int i5) {
		if(world.getBlockId(i2, i3, i4) != this.blockID) {
			return i5;
		} else {
			int i6 = world.getBlockMetadata(i2, i3, i4);
			return i6 > i5 ? i6 : i5;
		}
	}

	public void onNeighborBlockChange(World world, int i2, int i3, int i4, int i5) {
		if(!world.isRemote) {
			int i6 = world.getBlockMetadata(i2, i3, i4);
			boolean z7 = this.canPlaceBlockAt(world, i2, i3, i4);
			if(!z7) {
				this.dropBlockAsItem(world, i2, i3, i4, i6, 0);
				world.setBlockWithNotify(i2, i3, i4, 0);
			} else {
				this.updateAndPropagateCurrentStrength(world, i2, i3, i4);
			}

			super.onNeighborBlockChange(world, i2, i3, i4, i5);
		}
	}

	public int idDropped(int i1, Random random2, int i3) {
		return Item.redstone.shiftedIndex;
	}

	public boolean isIndirectlyPoweringTo(World world, int i2, int i3, int i4, int i5) {
		return !this.wiresProvidePower ? false : this.isPoweringTo(world, i2, i3, i4, i5);
	}

	public boolean isPoweringTo(IBlockAccess world, int i2, int i3, int i4, int i5) {
		if(!this.wiresProvidePower) {
			return false;
		} else if(world.getBlockMetadata(i2, i3, i4) == 0) {
			return false;
		} else if(i5 == 1) {
			return true;
		} else {
			boolean z6 = isPoweredOrRepeater(world, i2 - 1, i3, i4, 1) || !this.isBlockGood(world, i2 - 1, i3, i4) && isPoweredOrRepeater(world, i2 - 1, i3 - 1, i4, -1);
			boolean z7 = isPoweredOrRepeater(world, i2 + 1, i3, i4, 3) || !this.isBlockGood(world, i2 + 1, i3, i4) && isPoweredOrRepeater(world, i2 + 1, i3 - 1, i4, -1);
			boolean z8 = isPoweredOrRepeater(world, i2, i3, i4 - 1, 2) || !this.isBlockGood(world, i2, i3, i4 - 1) && isPoweredOrRepeater(world, i2, i3 - 1, i4 - 1, -1);
			boolean z9 = isPoweredOrRepeater(world, i2, i3, i4 + 1, 0) || !this.isBlockGood(world, i2, i3, i4 + 1) && isPoweredOrRepeater(world, i2, i3 - 1, i4 + 1, -1);
			if(!this.isBlockGood(world, i2, i3 + 1, i4)) {
				if(this.isBlockGood(world, i2 - 1, i3, i4) && isPoweredOrRepeater(world, i2 - 1, i3 + 1, i4, -1)) {
					z6 = true;
				}

				if(this.isBlockGood(world, i2 + 1, i3, i4) && isPoweredOrRepeater(world, i2 + 1, i3 + 1, i4, -1)) {
					z7 = true;
				}

				if(this.isBlockGood(world, i2, i3, i4 - 1) && isPoweredOrRepeater(world, i2, i3 + 1, i4 - 1, -1)) {
					z8 = true;
				}

				if(this.isBlockGood(world, i2, i3, i4 + 1) && isPoweredOrRepeater(world, i2, i3 + 1, i4 + 1, -1)) {
					z9 = true;
				}
			}

			return !z8 && !z7 && !z6 && !z9 && i5 >= 2 && i5 <= 5 ? true : (i5 == 2 && z8 && !z6 && !z7 ? true : (i5 == 3 && z9 && !z6 && !z7 ? true : (i5 == 4 && z6 && !z8 && !z9 ? true : i5 == 5 && z7 && !z8 && !z9)));
		}
	}

	public boolean isBlockGood(IBlockAccess world, int x, int y, int z) {
		Block block = world.getBlock(x, y, z);
		return block != null && block.supportsRedstone(world.getBlockMetadata(x, y, z));
	}

	public boolean canProvidePower() {
		return this.wiresProvidePower;
	}

	public void randomDisplayTick(World world, int i2, int i3, int i4, Random random5) {
		int i6 = world.getBlockMetadata(i2, i3, i4);
		if(i6 > 0) {
			double d7 = (double)i2 + 0.5D + ((double)random5.nextFloat() - 0.5D) * 0.2D;
			double d9 = (double)((float)i3 + 0.0625F);
			double d11 = (double)i4 + 0.5D + ((double)random5.nextFloat() - 0.5D) * 0.2D;
			float f13 = (float)i6 / 15.0F;
			float f14 = f13 * 0.6F + 0.4F;
			if(i6 == 0) {
				f14 = 0.0F;
			}

			float f15 = f13 * f13 * 0.7F - 0.5F;
			float f16 = f13 * f13 * 0.6F - 0.7F;
			if(f15 < 0.0F) {
				f15 = 0.0F;
			}

			if(f16 < 0.0F) {
				f16 = 0.0F;
			}

			world.spawnParticle("reddust", d7, d9, d11, (double)f14, (double)f15, (double)f16);
		}

	}

	public static boolean isPowerProviderOrWire(IBlockAccess iBlockAccess0, int i1, int i2, int i3, int i4) {
		int i5 = iBlockAccess0.getBlockId(i1, i2, i3);
		if(i5 == Block.redstoneWire.blockID) {
			return true;
		} else if(i5 == 0) {
			return false;
		} else if(i5 != Block.redstoneRepeaterIdle.blockID && i5 != Block.redstoneRepeaterActive.blockID) {
			return Block.blocksList[i5].canProvidePower() && i4 != -1;
		} else {
			int i6 = iBlockAccess0.getBlockMetadata(i1, i2, i3);
			return i4 == (i6 & 3) || i4 == Direction.footInvisibleFaceRemap[i6 & 3];
		}
	}

	public static boolean isPoweredOrRepeater(IBlockAccess iBlockAccess0, int i1, int i2, int i3, int i4) {
		if(isPowerProviderOrWire(iBlockAccess0, i1, i2, i3, i4)) {
			return true;
		} else {
			int i5 = iBlockAccess0.getBlockId(i1, i2, i3);
			if(i5 == Block.redstoneRepeaterActive.blockID) {
				int i6 = iBlockAccess0.getBlockMetadata(i1, i2, i3);
				return i4 == (i6 & 3);
			} else {
				return false;
			}
		}
	}
}
