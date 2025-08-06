package net.minecraft.world.level.tile;

import java.util.Random;

import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.World;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.AxisAlignedBB;
import net.minecraft.world.phys.MovingObjectPosition;
import net.minecraft.world.phys.Vec3D;

public class BlockDoor extends Block {
	protected BlockDoor(int i1, Material material2) {
		super(i1, material2);
		this.blockIndexInTexture = 97;
		if(material2 == Material.iron) {
			++this.blockIndexInTexture;
		}

		float f3 = 0.5F;
		float f4 = 1.0F;
		this.setBlockBounds(0.5F - f3, 0.0F, 0.5F - f3, 0.5F + f3, f4, 0.5F + f3);
	}

	public int getBlockTexture(IBlockAccess iBlockAccess1, int i2, int i3, int i4, int i5) {
		if(i5 != 0 && i5 != 1) {
			int i6 = this.getFullMetadata(iBlockAccess1, i2, i3, i4);
			int i7 = this.blockIndexInTexture;
			if((i6 & 8) != 0) {
				i7 -= 16;
			}

			int i8 = i6 & 3;
			boolean z9 = (i6 & 4) != 0;
			if(!z9) {
				if(i8 == 0 && i5 == 5) {
					i7 = -i7;
				} else if(i8 == 1 && i5 == 3) {
					i7 = -i7;
				} else if(i8 == 2 && i5 == 4) {
					i7 = -i7;
				} else if(i8 == 3 && i5 == 2) {
					i7 = -i7;
				}

				if((i6 & 16) != 0) {
					i7 = -i7;
				}
			} else if(i8 == 0 && i5 == 2) {
				i7 = -i7;
			} else if(i8 == 1 && i5 == 5) {
				i7 = -i7;
			} else if(i8 == 2 && i5 == 3) {
				i7 = -i7;
			} else if(i8 == 3 && i5 == 4) {
				i7 = -i7;
			}

			return i7;
		} else {
			return this.blockIndexInTexture;
		}
	}

	public boolean isOpaqueCube() {
		return false;
	}

	public boolean getBlocksMovement(IBlockAccess iBlockAccess1, int i2, int i3, int i4) {
		int i5 = this.getFullMetadata(iBlockAccess1, i2, i3, i4);
		return (i5 & 4) != 0;
	}

	public boolean renderAsNormalBlock() {
		return false;
	}

	public int getRenderType() {
		return 7;
	}

	public AxisAlignedBB getSelectedBoundingBoxFromPool(World world1, int i2, int i3, int i4) {
		this.setBlockBoundsBasedOnState(world1, i2, i3, i4);
		return super.getSelectedBoundingBoxFromPool(world1, i2, i3, i4);
	}

	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world1, int i2, int i3, int i4) {
		this.setBlockBoundsBasedOnState(world1, i2, i3, i4);
		return super.getCollisionBoundingBoxFromPool(world1, i2, i3, i4);
	}

	public void setBlockBoundsBasedOnState(IBlockAccess iBlockAccess1, int i2, int i3, int i4) {
		this.setDoorRotation(this.getFullMetadata(iBlockAccess1, i2, i3, i4));
	}

	public int getDoorOrientation(IBlockAccess iBlockAccess1, int i2, int i3, int i4) {
		return this.getFullMetadata(iBlockAccess1, i2, i3, i4) & 3;
	}

	public boolean func_48213_h(IBlockAccess iBlockAccess1, int i2, int i3, int i4) {
		return (this.getFullMetadata(iBlockAccess1, i2, i3, i4) & 4) != 0;
	}

	private void setDoorRotation(int i1) {
		float f2 = 0.1875F;
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 2.0F, 1.0F);
		int i3 = i1 & 3;
		boolean z4 = (i1 & 4) != 0;
		boolean z5 = (i1 & 16) != 0;
		if(i3 == 0) {
			if(!z4) {
				this.setBlockBounds(0.0F, 0.0F, 0.0F, f2, 1.0F, 1.0F);
			} else if(!z5) {
				this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, f2);
			} else {
				this.setBlockBounds(0.0F, 0.0F, 1.0F - f2, 1.0F, 1.0F, 1.0F);
			}
		} else if(i3 == 1) {
			if(!z4) {
				this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, f2);
			} else if(!z5) {
				this.setBlockBounds(1.0F - f2, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
			} else {
				this.setBlockBounds(0.0F, 0.0F, 0.0F, f2, 1.0F, 1.0F);
			}
		} else if(i3 == 2) {
			if(!z4) {
				this.setBlockBounds(1.0F - f2, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
			} else if(!z5) {
				this.setBlockBounds(0.0F, 0.0F, 1.0F - f2, 1.0F, 1.0F, 1.0F);
			} else {
				this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, f2);
			}
		} else if(i3 == 3) {
			if(!z4) {
				this.setBlockBounds(0.0F, 0.0F, 1.0F - f2, 1.0F, 1.0F, 1.0F);
			} else if(!z5) {
				this.setBlockBounds(0.0F, 0.0F, 0.0F, f2, 1.0F, 1.0F);
			} else {
				this.setBlockBounds(1.0F - f2, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
			}
		}

	}

	public void onBlockClicked(World world1, int i2, int i3, int i4, EntityPlayer entityPlayer5) {
		this.blockActivated(world1, i2, i3, i4, entityPlayer5);
	}

	public boolean blockActivated(World world1, int i2, int i3, int i4, EntityPlayer entityPlayer5) {
		if(this.blockMaterial == Material.iron) {
			return true;
		} else {
			int i6 = this.getFullMetadata(world1, i2, i3, i4);
			int i7 = i6 & 7;
			i7 ^= 4;
			if((i6 & 8) != 0) {
				world1.setBlockMetadataWithNotify(i2, i3 - 1, i4, i7);
				world1.markBlocksDirty(i2, i3 - 1, i4, i2, i3, i4);
			} else {
				world1.setBlockMetadataWithNotify(i2, i3, i4, i7);
				world1.markBlocksDirty(i2, i3, i4, i2, i3, i4);
			}

			world1.playAuxSFXAtEntity(entityPlayer5, 1003, i2, i3, i4, 0);
			return true;
		}
	}

	public void onPoweredBlockChange(World world1, int i2, int i3, int i4, boolean z5) {
		int i6 = this.getFullMetadata(world1, i2, i3, i4);
		boolean z7 = (i6 & 4) != 0;
		if(z7 != z5) {
			int i8 = i6 & 7;
			i8 ^= 4;
			if((i6 & 8) != 0) {
				world1.setBlockMetadataWithNotify(i2, i3 - 1, i4, i8);
				world1.markBlocksDirty(i2, i3 - 1, i4, i2, i3, i4);
			} else {
				world1.setBlockMetadataWithNotify(i2, i3, i4, i8);
				world1.markBlocksDirty(i2, i3, i4, i2, i3, i4);
			}

			world1.playAuxSFXAtEntity((EntityPlayer)null, 1003, i2, i3, i4, 0);
		}
	}

	public void onNeighborBlockChange(World world1, int i2, int i3, int i4, int i5) {
		int i6 = world1.getBlockMetadata(i2, i3, i4);
		if((i6 & 8) != 0) {
			if(world1.getBlockId(i2, i3 - 1, i4) != this.blockID) {
				world1.setBlockWithNotify(i2, i3, i4, 0);
			}

			if(i5 > 0 && i5 != this.blockID) {
				this.onNeighborBlockChange(world1, i2, i3 - 1, i4, i5);
			}
		} else {
			boolean z7 = false;
			if(world1.getBlockId(i2, i3 + 1, i4) != this.blockID) {
				world1.setBlockWithNotify(i2, i3, i4, 0);
				z7 = true;
			}

			if(!world1.isBlockNormalCube(i2, i3 - 1, i4)) {
				world1.setBlockWithNotify(i2, i3, i4, 0);
				z7 = true;
				if(world1.getBlockId(i2, i3 + 1, i4) == this.blockID) {
					world1.setBlockWithNotify(i2, i3 + 1, i4, 0);
				}
			}

			if(z7) {
				if(!world1.isRemote) {
					this.dropBlockAsItem(world1, i2, i3, i4, i6, 0);
				}
			} else {
				boolean z8 = world1.isBlockIndirectlyGettingPowered(i2, i3, i4) || world1.isBlockIndirectlyGettingPowered(i2, i3 + 1, i4);
				if((z8 || i5 > 0 && Block.blocksList[i5].canProvidePower() || i5 == 0) && i5 != this.blockID) {
					this.onPoweredBlockChange(world1, i2, i3, i4, z8);
				}
			}
		}

	}

	public int idDropped(int i1, Random random2, int i3) {
		return (i1 & 8) != 0 ? 0 : (this.blockMaterial == Material.iron ? Item.doorSteel.shiftedIndex : Item.doorWood.shiftedIndex);
	}

	public MovingObjectPosition collisionRayTrace(World world1, int i2, int i3, int i4, Vec3D vec3D5, Vec3D vec3D6) {
		this.setBlockBoundsBasedOnState(world1, i2, i3, i4);
		return super.collisionRayTrace(world1, i2, i3, i4, vec3D5, vec3D6);
	}

	public boolean canPlaceBlockAt(World world1, int i2, int i3, int i4) {
		return i3 >= 255 ? false : world1.isBlockNormalCube(i2, i3 - 1, i4) && super.canPlaceBlockAt(world1, i2, i3, i4) && super.canPlaceBlockAt(world1, i2, i3 + 1, i4);
	}

	public int getMobilityFlag() {
		return 1;
	}

	public int getFullMetadata(IBlockAccess iBlockAccess1, int i2, int i3, int i4) {
		int i5 = iBlockAccess1.getBlockMetadata(i2, i3, i4);
		boolean z6 = (i5 & 8) != 0;
		int i7;
		int i8;
		if(z6) {
			i7 = iBlockAccess1.getBlockMetadata(i2, i3 - 1, i4);
			i8 = i5;
		} else {
			i7 = i5;
			i8 = iBlockAccess1.getBlockMetadata(i2, i3 + 1, i4);
		}

		boolean z9 = (i8 & 1) != 0;
		int i10 = i7 & 7 | (z6 ? 8 : 0) | (z9 ? 16 : 0);
		return i10;
	}
}
