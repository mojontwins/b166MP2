package net.minecraft.src;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class BlockRedstoneWire extends Block {
	private boolean wiresProvidePower = true;
	private Set field_21032_b = new HashSet();

	public BlockRedstoneWire(int i1, int i2) {
		super(i1, i2, Material.circuits);
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.0625F, 1.0F);
	}

	public int getBlockTextureFromSideAndMetadata(int i1, int i2) {
		return this.blockIndexInTexture;
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

	public boolean canPlaceBlockAt(World world1, int i2, int i3, int i4) {
		return world1.isBlockOpaqueCube(i2, i3 - 1, i4);
	}

	private void updateAndPropagateCurrentStrength(World world1, int i2, int i3, int i4) {
		this.func_21031_a(world1, i2, i3, i4, i2, i3, i4);
		ArrayList arrayList5 = new ArrayList(this.field_21032_b);
		this.field_21032_b.clear();

		for(int i6 = 0; i6 < arrayList5.size(); ++i6) {
			ChunkPosition chunkPosition7 = (ChunkPosition)arrayList5.get(i6);
			world1.notifyBlocksOfNeighborChange(chunkPosition7.x, chunkPosition7.y, chunkPosition7.z, this.blockID);
		}

	}

	private void func_21031_a(World world1, int i2, int i3, int i4, int i5, int i6, int i7) {
		int i8 = world1.getBlockMetadata(i2, i3, i4);
		int i9 = 0;
		this.wiresProvidePower = false;
		boolean z10 = world1.isBlockIndirectlyGettingPowered(i2, i3, i4);
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
					i9 = this.getMaxCurrentStrength(world1, i12, i3, i13, i9);
				}

				if(world1.isBlockOpaqueCube(i12, i3, i13) && !world1.isBlockOpaqueCube(i2, i3 + 1, i4)) {
					if(i12 != i5 || i3 + 1 != i6 || i13 != i7) {
						i9 = this.getMaxCurrentStrength(world1, i12, i3 + 1, i13, i9);
					}
				} else if(!world1.isBlockOpaqueCube(i12, i3, i13) && (i12 != i5 || i3 - 1 != i6 || i13 != i7)) {
					i9 = this.getMaxCurrentStrength(world1, i12, i3 - 1, i13, i9);
				}
			}

			if(i9 > 0) {
				--i9;
			} else {
				i9 = 0;
			}
		}

		if(i8 != i9) {
			world1.editingBlocks = true;
			world1.setBlockMetadataWithNotify(i2, i3, i4, i9);
			world1.markBlocksDirty(i2, i3, i4, i2, i3, i4);
			world1.editingBlocks = false;

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

				if(world1.isBlockOpaqueCube(i12, i3, i13)) {
					i14 += 2;
				}

				boolean z15 = false;
				int i16 = this.getMaxCurrentStrength(world1, i12, i3, i13, -1);
				i9 = world1.getBlockMetadata(i2, i3, i4);
				if(i9 > 0) {
					--i9;
				}

				if(i16 >= 0 && i16 != i9) {
					this.func_21031_a(world1, i12, i3, i13, i2, i3, i4);
				}

				i16 = this.getMaxCurrentStrength(world1, i12, i14, i13, -1);
				i9 = world1.getBlockMetadata(i2, i3, i4);
				if(i9 > 0) {
					--i9;
				}

				if(i16 >= 0 && i16 != i9) {
					this.func_21031_a(world1, i12, i14, i13, i2, i3, i4);
				}
			}

			if(i8 == 0 || i9 == 0) {
				this.field_21032_b.add(new ChunkPosition(i2, i3, i4));
				this.field_21032_b.add(new ChunkPosition(i2 - 1, i3, i4));
				this.field_21032_b.add(new ChunkPosition(i2 + 1, i3, i4));
				this.field_21032_b.add(new ChunkPosition(i2, i3 - 1, i4));
				this.field_21032_b.add(new ChunkPosition(i2, i3 + 1, i4));
				this.field_21032_b.add(new ChunkPosition(i2, i3, i4 - 1));
				this.field_21032_b.add(new ChunkPosition(i2, i3, i4 + 1));
			}
		}

	}

	private void notifyWireNeighborsOfNeighborChange(World world1, int i2, int i3, int i4) {
		if(world1.getBlockId(i2, i3, i4) == this.blockID) {
			world1.notifyBlocksOfNeighborChange(i2, i3, i4, this.blockID);
			world1.notifyBlocksOfNeighborChange(i2 - 1, i3, i4, this.blockID);
			world1.notifyBlocksOfNeighborChange(i2 + 1, i3, i4, this.blockID);
			world1.notifyBlocksOfNeighborChange(i2, i3, i4 - 1, this.blockID);
			world1.notifyBlocksOfNeighborChange(i2, i3, i4 + 1, this.blockID);
			world1.notifyBlocksOfNeighborChange(i2, i3 - 1, i4, this.blockID);
			world1.notifyBlocksOfNeighborChange(i2, i3 + 1, i4, this.blockID);
		}
	}

	public void onBlockAdded(World world1, int i2, int i3, int i4) {
		super.onBlockAdded(world1, i2, i3, i4);
		if(!world1.singleplayerWorld) {
			this.updateAndPropagateCurrentStrength(world1, i2, i3, i4);
			world1.notifyBlocksOfNeighborChange(i2, i3 + 1, i4, this.blockID);
			world1.notifyBlocksOfNeighborChange(i2, i3 - 1, i4, this.blockID);
			this.notifyWireNeighborsOfNeighborChange(world1, i2 - 1, i3, i4);
			this.notifyWireNeighborsOfNeighborChange(world1, i2 + 1, i3, i4);
			this.notifyWireNeighborsOfNeighborChange(world1, i2, i3, i4 - 1);
			this.notifyWireNeighborsOfNeighborChange(world1, i2, i3, i4 + 1);
			if(world1.isBlockOpaqueCube(i2 - 1, i3, i4)) {
				this.notifyWireNeighborsOfNeighborChange(world1, i2 - 1, i3 + 1, i4);
			} else {
				this.notifyWireNeighborsOfNeighborChange(world1, i2 - 1, i3 - 1, i4);
			}

			if(world1.isBlockOpaqueCube(i2 + 1, i3, i4)) {
				this.notifyWireNeighborsOfNeighborChange(world1, i2 + 1, i3 + 1, i4);
			} else {
				this.notifyWireNeighborsOfNeighborChange(world1, i2 + 1, i3 - 1, i4);
			}

			if(world1.isBlockOpaqueCube(i2, i3, i4 - 1)) {
				this.notifyWireNeighborsOfNeighborChange(world1, i2, i3 + 1, i4 - 1);
			} else {
				this.notifyWireNeighborsOfNeighborChange(world1, i2, i3 - 1, i4 - 1);
			}

			if(world1.isBlockOpaqueCube(i2, i3, i4 + 1)) {
				this.notifyWireNeighborsOfNeighborChange(world1, i2, i3 + 1, i4 + 1);
			} else {
				this.notifyWireNeighborsOfNeighborChange(world1, i2, i3 - 1, i4 + 1);
			}

		}
	}

	public void onBlockRemoval(World world1, int i2, int i3, int i4) {
		super.onBlockRemoval(world1, i2, i3, i4);
		if(!world1.singleplayerWorld) {
			world1.notifyBlocksOfNeighborChange(i2, i3 + 1, i4, this.blockID);
			world1.notifyBlocksOfNeighborChange(i2, i3 - 1, i4, this.blockID);
			this.updateAndPropagateCurrentStrength(world1, i2, i3, i4);
			this.notifyWireNeighborsOfNeighborChange(world1, i2 - 1, i3, i4);
			this.notifyWireNeighborsOfNeighborChange(world1, i2 + 1, i3, i4);
			this.notifyWireNeighborsOfNeighborChange(world1, i2, i3, i4 - 1);
			this.notifyWireNeighborsOfNeighborChange(world1, i2, i3, i4 + 1);
			if(world1.isBlockOpaqueCube(i2 - 1, i3, i4)) {
				this.notifyWireNeighborsOfNeighborChange(world1, i2 - 1, i3 + 1, i4);
			} else {
				this.notifyWireNeighborsOfNeighborChange(world1, i2 - 1, i3 - 1, i4);
			}

			if(world1.isBlockOpaqueCube(i2 + 1, i3, i4)) {
				this.notifyWireNeighborsOfNeighborChange(world1, i2 + 1, i3 + 1, i4);
			} else {
				this.notifyWireNeighborsOfNeighborChange(world1, i2 + 1, i3 - 1, i4);
			}

			if(world1.isBlockOpaqueCube(i2, i3, i4 - 1)) {
				this.notifyWireNeighborsOfNeighborChange(world1, i2, i3 + 1, i4 - 1);
			} else {
				this.notifyWireNeighborsOfNeighborChange(world1, i2, i3 - 1, i4 - 1);
			}

			if(world1.isBlockOpaqueCube(i2, i3, i4 + 1)) {
				this.notifyWireNeighborsOfNeighborChange(world1, i2, i3 + 1, i4 + 1);
			} else {
				this.notifyWireNeighborsOfNeighborChange(world1, i2, i3 - 1, i4 + 1);
			}

		}
	}

	private int getMaxCurrentStrength(World world1, int i2, int i3, int i4, int i5) {
		if(world1.getBlockId(i2, i3, i4) != this.blockID) {
			return i5;
		} else {
			int i6 = world1.getBlockMetadata(i2, i3, i4);
			return i6 > i5 ? i6 : i5;
		}
	}

	public void onNeighborBlockChange(World world1, int i2, int i3, int i4, int i5) {
		if(!world1.singleplayerWorld) {
			int i6 = world1.getBlockMetadata(i2, i3, i4);
			boolean z7 = this.canPlaceBlockAt(world1, i2, i3, i4);
			if(!z7) {
				this.dropBlockAsItem(world1, i2, i3, i4, i6);
				world1.setBlockWithNotify(i2, i3, i4, 0);
			} else {
				this.updateAndPropagateCurrentStrength(world1, i2, i3, i4);
			}

			super.onNeighborBlockChange(world1, i2, i3, i4, i5);
		}
	}

	public int idDropped(int i1, Random random2) {
		return Item.redstone.shiftedIndex;
	}

	public boolean isIndirectlyPoweringTo(World world1, int i2, int i3, int i4, int i5) {
		return !this.wiresProvidePower ? false : this.isPoweringTo(world1, i2, i3, i4, i5);
	}

	public boolean isPoweringTo(IBlockAccess iBlockAccess1, int i2, int i3, int i4, int i5) {
		if(!this.wiresProvidePower) {
			return false;
		} else if(iBlockAccess1.getBlockMetadata(i2, i3, i4) == 0) {
			return false;
		} else if(i5 == 1) {
			return true;
		} else {
			boolean z6 = isPowerProviderOrWire(iBlockAccess1, i2 - 1, i3, i4) || !iBlockAccess1.isBlockOpaqueCube(i2 - 1, i3, i4) && isPowerProviderOrWire(iBlockAccess1, i2 - 1, i3 - 1, i4);
			boolean z7 = isPowerProviderOrWire(iBlockAccess1, i2 + 1, i3, i4) || !iBlockAccess1.isBlockOpaqueCube(i2 + 1, i3, i4) && isPowerProviderOrWire(iBlockAccess1, i2 + 1, i3 - 1, i4);
			boolean z8 = isPowerProviderOrWire(iBlockAccess1, i2, i3, i4 - 1) || !iBlockAccess1.isBlockOpaqueCube(i2, i3, i4 - 1) && isPowerProviderOrWire(iBlockAccess1, i2, i3 - 1, i4 - 1);
			boolean z9 = isPowerProviderOrWire(iBlockAccess1, i2, i3, i4 + 1) || !iBlockAccess1.isBlockOpaqueCube(i2, i3, i4 + 1) && isPowerProviderOrWire(iBlockAccess1, i2, i3 - 1, i4 + 1);
			if(!iBlockAccess1.isBlockOpaqueCube(i2, i3 + 1, i4)) {
				if(iBlockAccess1.isBlockOpaqueCube(i2 - 1, i3, i4) && isPowerProviderOrWire(iBlockAccess1, i2 - 1, i3 + 1, i4)) {
					z6 = true;
				}

				if(iBlockAccess1.isBlockOpaqueCube(i2 + 1, i3, i4) && isPowerProviderOrWire(iBlockAccess1, i2 + 1, i3 + 1, i4)) {
					z7 = true;
				}

				if(iBlockAccess1.isBlockOpaqueCube(i2, i3, i4 - 1) && isPowerProviderOrWire(iBlockAccess1, i2, i3 + 1, i4 - 1)) {
					z8 = true;
				}

				if(iBlockAccess1.isBlockOpaqueCube(i2, i3, i4 + 1) && isPowerProviderOrWire(iBlockAccess1, i2, i3 + 1, i4 + 1)) {
					z9 = true;
				}
			}

			return !z8 && !z7 && !z6 && !z9 && i5 >= 2 && i5 <= 5 ? true : (i5 == 2 && z8 && !z6 && !z7 ? true : (i5 == 3 && z9 && !z6 && !z7 ? true : (i5 == 4 && z6 && !z8 && !z9 ? true : i5 == 5 && z7 && !z8 && !z9)));
		}
	}

	public boolean canProvidePower() {
		return this.wiresProvidePower;
	}

	public static boolean isPowerProviderOrWire(IBlockAccess iBlockAccess0, int i1, int i2, int i3) {
		int i4 = iBlockAccess0.getBlockId(i1, i2, i3);
		return i4 == Block.redstoneWire.blockID ? true : (i4 == 0 ? false : Block.blocksList[i4].canProvidePower());
	}
}
