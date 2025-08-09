package net.minecraft.world.level.tile;

import java.util.Iterator;
import java.util.Random;

import com.mojang.nbt.NBTTagCompound;

import net.minecraft.src.MathHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.animal.EntityOcelot;
import net.minecraft.world.entity.item.EntityItem;
import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.inventory.IInventory;
import net.minecraft.world.inventory.InventoryLargeChest;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.World;
import net.minecraft.world.level.creative.CreativeTabs;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.tile.entity.TileEntity;
import net.minecraft.world.level.tile.entity.TileEntityChest;
import net.minecraft.world.phys.AxisAlignedBB;

public class BlockChest extends BlockContainer {
	private Random random = new Random();

	protected BlockChest(int i1) {
		super(i1, Material.wood);
		this.blockIndexInTexture = 26;
		this.displayOnCreativeTab = CreativeTabs.tabDeco;
	}

	public boolean isOpaqueCube() {
		return false;
	}

	public boolean renderAsNormalBlock() {
		return true;
	}

	public int getRenderType() {
		return 0;
	}

	public void onBlockAdded(World world1, int i2, int i3, int i4) {
		super.onBlockAdded(world1, i2, i3, i4);
		this.unifyAdjacentChests(world1, i2, i3, i4);
		int i5 = world1.getBlockId(i2, i3, i4 - 1);
		int i6 = world1.getBlockId(i2, i3, i4 + 1);
		int i7 = world1.getBlockId(i2 - 1, i3, i4);
		int i8 = world1.getBlockId(i2 + 1, i3, i4);
		if(i5 == this.blockID) {
			this.unifyAdjacentChests(world1, i2, i3, i4 - 1);
		}

		if(i6 == this.blockID) {
			this.unifyAdjacentChests(world1, i2, i3, i4 + 1);
		}

		if(i7 == this.blockID) {
			this.unifyAdjacentChests(world1, i2 - 1, i3, i4);
		}

		if(i8 == this.blockID) {
			this.unifyAdjacentChests(world1, i2 + 1, i3, i4);
		}

	}

	public void onBlockPlacedBy(World world1, int i2, int i3, int i4, EntityLiving entityLiving) {
		int i6 = world1.getBlockId(i2, i3, i4 - 1);
		int i7 = world1.getBlockId(i2, i3, i4 + 1);
		int i8 = world1.getBlockId(i2 - 1, i3, i4);
		int i9 = world1.getBlockId(i2 + 1, i3, i4);
		byte b10 = 0;
		int i11 = MathHelper.floor_double((double)(entityLiving.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
		if(i11 == 0) {
			b10 = 2;
		}

		if(i11 == 1) {
			b10 = 5;
		}

		if(i11 == 2) {
			b10 = 3;
		}

		if(i11 == 3) {
			b10 = 4;
		}

		if(i6 != this.blockID && i7 != this.blockID && i8 != this.blockID && i9 != this.blockID) {
			world1.setBlockMetadataWithNotify(i2, i3, i4, b10);
		} else {
			if((i6 == this.blockID || i7 == this.blockID) && (b10 == 4 || b10 == 5)) {
				if(i6 == this.blockID) {
					world1.setBlockMetadataWithNotify(i2, i3, i4 - 1, b10);
				} else {
					world1.setBlockMetadataWithNotify(i2, i3, i4 + 1, b10);
				}

				world1.setBlockMetadataWithNotify(i2, i3, i4, b10);
			}

			if((i8 == this.blockID || i9 == this.blockID) && (b10 == 2 || b10 == 3)) {
				if(i8 == this.blockID) {
					world1.setBlockMetadataWithNotify(i2 - 1, i3, i4, b10);
				} else {
					world1.setBlockMetadataWithNotify(i2 + 1, i3, i4, b10);
				}

				world1.setBlockMetadataWithNotify(i2, i3, i4, b10);
			}
		}

	}

	public void unifyAdjacentChests(World world1, int i2, int i3, int i4) {
		if(!world1.isRemote) {
			int i5 = world1.getBlockId(i2, i3, i4 - 1);
			int i6 = world1.getBlockId(i2, i3, i4 + 1);
			int i7 = world1.getBlockId(i2 - 1, i3, i4);
			int i8 = world1.getBlockId(i2 + 1, i3, i4);
			int i10;
			int i11;
			byte b13;
			int i14;
			if(i5 != this.blockID && i6 != this.blockID) {
				if(i7 != this.blockID && i8 != this.blockID) {
					b13 = 3;
					if(Block.opaqueCubeLookup[i5] && !Block.opaqueCubeLookup[i6]) {
						b13 = 3;
					}

					if(Block.opaqueCubeLookup[i6] && !Block.opaqueCubeLookup[i5]) {
						b13 = 2;
					}

					if(Block.opaqueCubeLookup[i7] && !Block.opaqueCubeLookup[i8]) {
						b13 = 5;
					}

					if(Block.opaqueCubeLookup[i8] && !Block.opaqueCubeLookup[i7]) {
						b13 = 4;
					}
				} else {
					i10 = world1.getBlockId(i7 == this.blockID ? i2 - 1 : i2 + 1, i3, i4 - 1);
					i11 = world1.getBlockId(i7 == this.blockID ? i2 - 1 : i2 + 1, i3, i4 + 1);
					b13 = 3;
					if(i7 == this.blockID) {
						i14 = world1.getBlockMetadata(i2 - 1, i3, i4);
					} else {
						i14 = world1.getBlockMetadata(i2 + 1, i3, i4);
					}

					if(i14 == 2) {
						b13 = 2;
					}

					if((Block.opaqueCubeLookup[i5] || Block.opaqueCubeLookup[i10]) && !Block.opaqueCubeLookup[i6] && !Block.opaqueCubeLookup[i11]) {
						b13 = 3;
					}

					if((Block.opaqueCubeLookup[i6] || Block.opaqueCubeLookup[i11]) && !Block.opaqueCubeLookup[i5] && !Block.opaqueCubeLookup[i10]) {
						b13 = 2;
					}
				}
			} else {
				i10 = world1.getBlockId(i2 - 1, i3, i5 == this.blockID ? i4 - 1 : i4 + 1);
				i11 = world1.getBlockId(i2 + 1, i3, i5 == this.blockID ? i4 - 1 : i4 + 1);
				b13 = 5;
				if(i5 == this.blockID) {
					i14 = world1.getBlockMetadata(i2, i3, i4 - 1);
				} else {
					i14 = world1.getBlockMetadata(i2, i3, i4 + 1);
				}

				if(i14 == 4) {
					b13 = 4;
				}

				if((Block.opaqueCubeLookup[i7] || Block.opaqueCubeLookup[i10]) && !Block.opaqueCubeLookup[i8] && !Block.opaqueCubeLookup[i11]) {
					b13 = 5;
				}

				if((Block.opaqueCubeLookup[i8] || Block.opaqueCubeLookup[i11]) && !Block.opaqueCubeLookup[i7] && !Block.opaqueCubeLookup[i10]) {
					b13 = 4;
				}
			}

			world1.setBlockMetadataWithNotify(i2, i3, i4, b13);
		}
	}

	public int getBlockTexture(IBlockAccess iBlockAccess1, int i2, int i3, int i4, int i5) {
		if(i5 == 1) {
			return this.blockIndexInTexture - 1;
		} else if(i5 == 0) {
			return this.blockIndexInTexture - 1;
		} else {
			int i6 = iBlockAccess1.getBlockId(i2, i3, i4 - 1);
			int i7 = iBlockAccess1.getBlockId(i2, i3, i4 + 1);
			int i8 = iBlockAccess1.getBlockId(i2 - 1, i3, i4);
			int i9 = iBlockAccess1.getBlockId(i2 + 1, i3, i4);
			int i10;
			int i11;
			int i12;
			byte b13;
			if(i6 != this.blockID && i7 != this.blockID) {
				if(i8 != this.blockID && i9 != this.blockID) {
					byte b14 = 3;
					if(Block.opaqueCubeLookup[i6] && !Block.opaqueCubeLookup[i7]) {
						b14 = 3;
					}

					if(Block.opaqueCubeLookup[i7] && !Block.opaqueCubeLookup[i6]) {
						b14 = 2;
					}

					if(Block.opaqueCubeLookup[i8] && !Block.opaqueCubeLookup[i9]) {
						b14 = 5;
					}

					if(Block.opaqueCubeLookup[i9] && !Block.opaqueCubeLookup[i8]) {
						b14 = 4;
					}

					return i5 == b14 ? this.blockIndexInTexture + 1 : this.blockIndexInTexture;
				} else if(i5 != 4 && i5 != 5) {
					i10 = 0;
					if(i8 == this.blockID) {
						i10 = -1;
					}

					i11 = iBlockAccess1.getBlockId(i8 == this.blockID ? i2 - 1 : i2 + 1, i3, i4 - 1);
					i12 = iBlockAccess1.getBlockId(i8 == this.blockID ? i2 - 1 : i2 + 1, i3, i4 + 1);
					if(i5 == 3) {
						i10 = -1 - i10;
					}

					b13 = 3;
					if((Block.opaqueCubeLookup[i6] || Block.opaqueCubeLookup[i11]) && !Block.opaqueCubeLookup[i7] && !Block.opaqueCubeLookup[i12]) {
						b13 = 3;
					}

					if((Block.opaqueCubeLookup[i7] || Block.opaqueCubeLookup[i12]) && !Block.opaqueCubeLookup[i6] && !Block.opaqueCubeLookup[i11]) {
						b13 = 2;
					}

					return (i5 == b13 ? this.blockIndexInTexture + 16 : this.blockIndexInTexture + 32) + i10;
				} else {
					return this.blockIndexInTexture;
				}
			} else if(i5 != 2 && i5 != 3) {
				i10 = 0;
				if(i6 == this.blockID) {
					i10 = -1;
				}

				i11 = iBlockAccess1.getBlockId(i2 - 1, i3, i6 == this.blockID ? i4 - 1 : i4 + 1);
				i12 = iBlockAccess1.getBlockId(i2 + 1, i3, i6 == this.blockID ? i4 - 1 : i4 + 1);
				if(i5 == 4) {
					i10 = -1 - i10;
				}

				b13 = 5;
				if((Block.opaqueCubeLookup[i8] || Block.opaqueCubeLookup[i11]) && !Block.opaqueCubeLookup[i9] && !Block.opaqueCubeLookup[i12]) {
					b13 = 5;
				}

				if((Block.opaqueCubeLookup[i9] || Block.opaqueCubeLookup[i12]) && !Block.opaqueCubeLookup[i8] && !Block.opaqueCubeLookup[i11]) {
					b13 = 4;
				}

				return (i5 == b13 ? this.blockIndexInTexture + 16 : this.blockIndexInTexture + 32) + i10;
			} else {
				return this.blockIndexInTexture;
			}
		}
	}

	public int getBlockTextureFromSide(int i1) {
		return i1 == 1 ? this.blockIndexInTexture - 1 : (i1 == 0 ? this.blockIndexInTexture - 1 : (i1 == 3 ? this.blockIndexInTexture + 1 : this.blockIndexInTexture));
	}

	public boolean canPlaceBlockAt(World world1, int i2, int i3, int i4) {
		int i5 = 0;
		if(world1.getBlockId(i2 - 1, i3, i4) == this.blockID) {
			++i5;
		}

		if(world1.getBlockId(i2 + 1, i3, i4) == this.blockID) {
			++i5;
		}

		if(world1.getBlockId(i2, i3, i4 - 1) == this.blockID) {
			++i5;
		}

		if(world1.getBlockId(i2, i3, i4 + 1) == this.blockID) {
			++i5;
		}

		return i5 > 1 ? false : (this.isThereANeighborChest(world1, i2 - 1, i3, i4) ? false : (this.isThereANeighborChest(world1, i2 + 1, i3, i4) ? false : (this.isThereANeighborChest(world1, i2, i3, i4 - 1) ? false : !this.isThereANeighborChest(world1, i2, i3, i4 + 1))));
	}

	private boolean isThereANeighborChest(World world1, int i2, int i3, int i4) {
		return world1.getBlockId(i2, i3, i4) != this.blockID ? false : (world1.getBlockId(i2 - 1, i3, i4) == this.blockID ? true : (world1.getBlockId(i2 + 1, i3, i4) == this.blockID ? true : (world1.getBlockId(i2, i3, i4 - 1) == this.blockID ? true : world1.getBlockId(i2, i3, i4 + 1) == this.blockID)));
	}

	public void onNeighborBlockChange(World world1, int i2, int i3, int i4, int i5) {
		super.onNeighborBlockChange(world1, i2, i3, i4, i5);
		TileEntityChest tileEntityChest6 = (TileEntityChest)world1.getBlockTileEntity(i2, i3, i4);
		if(tileEntityChest6 != null) {
			tileEntityChest6.updateContainingBlockInfo();
		}

	}

	public void onBlockRemoval(World world1, int i2, int i3, int i4) {
		TileEntityChest tileEntityChest5 = (TileEntityChest)world1.getBlockTileEntity(i2, i3, i4);
		if(tileEntityChest5 != null) {
			for(int i6 = 0; i6 < tileEntityChest5.getSizeInventory(); ++i6) {
				ItemStack itemStack7 = tileEntityChest5.getStackInSlot(i6);
				if(itemStack7 != null) {
					float f8 = this.random.nextFloat() * 0.8F + 0.1F;
					float f9 = this.random.nextFloat() * 0.8F + 0.1F;

					EntityItem entityItem12;
					for(float f10 = this.random.nextFloat() * 0.8F + 0.1F; itemStack7.stackSize > 0; world1.spawnEntityInWorld(entityItem12)) {
						int i11 = this.random.nextInt(21) + 10;
						if(i11 > itemStack7.stackSize) {
							i11 = itemStack7.stackSize;
						}

						itemStack7.stackSize -= i11;
						entityItem12 = new EntityItem(world1, (double)((float)i2 + f8), (double)((float)i3 + f9), (double)((float)i4 + f10), new ItemStack(itemStack7.itemID, i11, itemStack7.getItemDamage()));
						float f13 = 0.05F;
						entityItem12.motionX = (double)((float)this.random.nextGaussian() * f13);
						entityItem12.motionY = (double)((float)this.random.nextGaussian() * f13 + 0.2F);
						entityItem12.motionZ = (double)((float)this.random.nextGaussian() * f13);
						if(itemStack7.hasTagCompound()) {
							entityItem12.item.setTagCompound((NBTTagCompound)itemStack7.getTagCompound().copy());
						}
					}
				}
			}
		}

		super.onBlockRemoval(world1, i2, i3, i4);
	}

	public boolean blockActivated(World world1, int i2, int i3, int i4, EntityPlayer entityPlayer5) {
		Object object6 = (TileEntityChest)world1.getBlockTileEntity(i2, i3, i4);
		if(object6 == null) {
			return true;
		} else if(world1.isBlockNormalCube(i2, i3 + 1, i4)) {
			return true;
		} else if(makeCatWantThis(world1, i2, i3, i4)) {
			return true;
		} else if(world1.getBlockId(i2 - 1, i3, i4) == this.blockID && (world1.isBlockNormalCube(i2 - 1, i3 + 1, i4) || makeCatWantThis(world1, i2 - 1, i3, i4))) {
			return true;
		} else if(world1.getBlockId(i2 + 1, i3, i4) == this.blockID && (world1.isBlockNormalCube(i2 + 1, i3 + 1, i4) || makeCatWantThis(world1, i2 + 1, i3, i4))) {
			return true;
		} else if(world1.getBlockId(i2, i3, i4 - 1) != this.blockID || !world1.isBlockNormalCube(i2, i3 + 1, i4 - 1) && !makeCatWantThis(world1, i2, i3, i4 - 1)) {
			if(world1.getBlockId(i2, i3, i4 + 1) != this.blockID || !world1.isBlockNormalCube(i2, i3 + 1, i4 + 1) && !makeCatWantThis(world1, i2, i3, i4 + 1)) {
				if(world1.getBlockId(i2 - 1, i3, i4) == this.blockID) {
					object6 = new InventoryLargeChest("Large chest", (TileEntityChest)world1.getBlockTileEntity(i2 - 1, i3, i4), (IInventory)object6);
				}

				if(world1.getBlockId(i2 + 1, i3, i4) == this.blockID) {
					object6 = new InventoryLargeChest("Large chest", (IInventory)object6, (TileEntityChest)world1.getBlockTileEntity(i2 + 1, i3, i4));
				}

				if(world1.getBlockId(i2, i3, i4 - 1) == this.blockID) {
					object6 = new InventoryLargeChest("Large chest", (TileEntityChest)world1.getBlockTileEntity(i2, i3, i4 - 1), (IInventory)object6);
				}

				if(world1.getBlockId(i2, i3, i4 + 1) == this.blockID) {
					object6 = new InventoryLargeChest("Large chest", (IInventory)object6, (TileEntityChest)world1.getBlockTileEntity(i2, i3, i4 + 1));
				}

				if(world1.isRemote) {
					return true;
				} else {
					entityPlayer5.displayGUIChest((IInventory)object6);
					return true;
				}
			} else {
				return true;
			}
		} else {
			return true;
		}
	}

	public TileEntity getBlockEntity() {
		return new TileEntityChest();
	}

	private static boolean makeCatWantThis(World world0, int i1, int i2, int i3) {
		Iterator<Entity> iterator4 = world0.getEntitiesWithinAABB(EntityOcelot.class, AxisAlignedBB.getBoundingBoxFromPool((double)i1, (double)(i2 + 1), (double)i3, (double)(i1 + 1), (double)(i2 + 2), (double)(i3 + 1))).iterator();

		EntityOcelot entityOcelot6;
		do {
			if(!iterator4.hasNext()) {
				return false;
			}

			Entity entity5 = (Entity)iterator4.next();
			entityOcelot6 = (EntityOcelot)entity5;
		} while(!entityOcelot6.isSitting());

		return true;
	}
}
