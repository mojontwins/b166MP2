package net.minecraft.world.level.tile;

import java.util.Random;

import com.mojang.nbt.NBTTagCompound;

import net.minecraft.src.MathHelper;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.item.EntityItem;
import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.World;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.tile.entity.TileEntity;
import net.minecraft.world.level.tile.entity.TileEntityFurnace;

public class BlockFurnace extends BlockContainer {
	private Random furnaceRand = new Random();
	private final boolean isActive;
	private static boolean keepFurnaceInventory = false;

	protected BlockFurnace(int i1, boolean z2) {
		super(i1, Material.rock);
		this.isActive = z2;
		this.blockIndexInTexture = 45;
	}

	public int idDropped(int i1, Random random2, int i3) {
		return Block.stoneOvenIdle.blockID;
	}

	public void onBlockAdded(World world1, int i2, int i3, int i4) {
		super.onBlockAdded(world1, i2, i3, i4);
		this.setDefaultDirection(world1, i2, i3, i4);
	}

	private void setDefaultDirection(World world1, int i2, int i3, int i4) {
		if(!world1.isRemote) {
			int i5 = world1.getBlockId(i2, i3, i4 - 1);
			int i6 = world1.getBlockId(i2, i3, i4 + 1);
			int i7 = world1.getBlockId(i2 - 1, i3, i4);
			int i8 = world1.getBlockId(i2 + 1, i3, i4);
			byte b9 = 3;
			if(Block.opaqueCubeLookup[i5] && !Block.opaqueCubeLookup[i6]) {
				b9 = 3;
			}

			if(Block.opaqueCubeLookup[i6] && !Block.opaqueCubeLookup[i5]) {
				b9 = 2;
			}

			if(Block.opaqueCubeLookup[i7] && !Block.opaqueCubeLookup[i8]) {
				b9 = 5;
			}

			if(Block.opaqueCubeLookup[i8] && !Block.opaqueCubeLookup[i7]) {
				b9 = 4;
			}

			world1.setBlockMetadataWithNotify(i2, i3, i4, b9);
		}
	}

	public int getBlockTexture(IBlockAccess iBlockAccess1, int i2, int i3, int i4, int i5) {
		if(i5 < 2) {
			return 17;
		} else {
			int i6 = iBlockAccess1.getBlockMetadata(i2, i3, i4);
			return i5 != i6 ? this.blockIndexInTexture : (this.isActive ? this.blockIndexInTexture + 16 : this.blockIndexInTexture - 1);
		}
	}

	public void randomDisplayTick(World world1, int i2, int i3, int i4, Random random5) {
		if(this.isActive) {
			int i6 = world1.getBlockMetadata(i2, i3, i4);
			float f7 = (float)i2 + 0.5F;
			float f8 = (float)i3 + 0.0F + random5.nextFloat() * 6.0F / 16.0F;
			float f9 = (float)i4 + 0.5F;
			float f10 = 0.52F;
			float f11 = random5.nextFloat() * 0.6F - 0.3F;
			if(i6 == 4) {
				world1.spawnParticle("smoke", (double)(f7 - f10), (double)f8, (double)(f9 + f11), 0.0D, 0.0D, 0.0D);
				world1.spawnParticle("flame", (double)(f7 - f10), (double)f8, (double)(f9 + f11), 0.0D, 0.0D, 0.0D);
			} else if(i6 == 5) {
				world1.spawnParticle("smoke", (double)(f7 + f10), (double)f8, (double)(f9 + f11), 0.0D, 0.0D, 0.0D);
				world1.spawnParticle("flame", (double)(f7 + f10), (double)f8, (double)(f9 + f11), 0.0D, 0.0D, 0.0D);
			} else if(i6 == 2) {
				world1.spawnParticle("smoke", (double)(f7 + f11), (double)f8, (double)(f9 - f10), 0.0D, 0.0D, 0.0D);
				world1.spawnParticle("flame", (double)(f7 + f11), (double)f8, (double)(f9 - f10), 0.0D, 0.0D, 0.0D);
			} else if(i6 == 3) {
				world1.spawnParticle("smoke", (double)(f7 + f11), (double)f8, (double)(f9 + f10), 0.0D, 0.0D, 0.0D);
				world1.spawnParticle("flame", (double)(f7 + f11), (double)f8, (double)(f9 + f10), 0.0D, 0.0D, 0.0D);
			}

		}
	}

	public int getBlockTextureFromSide(int i1) {
		return i1 < 2 ? 1 : (i1 == 3 ? this.blockIndexInTexture - 1 : this.blockIndexInTexture);
	}

	public boolean blockActivated(World world1, int i2, int i3, int i4, EntityPlayer entityPlayer5) {
		if(world1.isRemote) {
			return true;
		} else {
			TileEntityFurnace tileEntityFurnace6 = (TileEntityFurnace)world1.getBlockTileEntity(i2, i3, i4);
			
			if(tileEntityFurnace6 != null) {
				entityPlayer5.displayGUIFurnace(tileEntityFurnace6);
			}

			return true;
		}
	}

	public static void updateFurnaceBlockState(boolean z0, World world1, int i2, int i3, int i4) {
		int i5 = world1.getBlockMetadata(i2, i3, i4);
		TileEntity tileEntity6 = world1.getBlockTileEntity(i2, i3, i4);
		keepFurnaceInventory = true;
		if(z0) {
			world1.setBlockWithNotify(i2, i3, i4, Block.stoneOvenActive.blockID);
		} else {
			world1.setBlockWithNotify(i2, i3, i4, Block.stoneOvenIdle.blockID);
		}

		keepFurnaceInventory = false;
		world1.setBlockMetadataWithNotify(i2, i3, i4, i5);
		if(tileEntity6 != null) {
			tileEntity6.validate();
			world1.setBlockTileEntity(i2, i3, i4, tileEntity6);
		}

	}

	public TileEntity getBlockEntity() {
		return new TileEntityFurnace();
	}

	public void onBlockPlacedBy(World world1, int i2, int i3, int i4, EntityLiving entityLiving) {
		int i6 = MathHelper.floor_double((double)(entityLiving.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
		if(i6 == 0) {
			world1.setBlockMetadataWithNotify(i2, i3, i4, 2);
		}

		if(i6 == 1) {
			world1.setBlockMetadataWithNotify(i2, i3, i4, 5);
		}

		if(i6 == 2) {
			world1.setBlockMetadataWithNotify(i2, i3, i4, 3);
		}

		if(i6 == 3) {
			world1.setBlockMetadataWithNotify(i2, i3, i4, 4);
		}

	}

	public void onBlockRemoval(World world1, int i2, int i3, int i4) {
		if(!keepFurnaceInventory) {
			TileEntityFurnace tileEntityFurnace5 = (TileEntityFurnace)world1.getBlockTileEntity(i2, i3, i4);
			if(tileEntityFurnace5 != null) {
				for(int i6 = 0; i6 < tileEntityFurnace5.getSizeInventory(); ++i6) {
					ItemStack itemStack7 = tileEntityFurnace5.getStackInSlot(i6);
					if(itemStack7 != null) {
						float f8 = this.furnaceRand.nextFloat() * 0.8F + 0.1F;
						float f9 = this.furnaceRand.nextFloat() * 0.8F + 0.1F;
						float f10 = this.furnaceRand.nextFloat() * 0.8F + 0.1F;

						while(itemStack7.stackSize > 0) {
							int i11 = this.furnaceRand.nextInt(21) + 10;
							if(i11 > itemStack7.stackSize) {
								i11 = itemStack7.stackSize;
							}

							itemStack7.stackSize -= i11;
							EntityItem entityItem12 = new EntityItem(world1, (double)((float)i2 + f8), (double)((float)i3 + f9), (double)((float)i4 + f10), new ItemStack(itemStack7.itemID, i11, itemStack7.getItemDamage()));
							if(itemStack7.hasTagCompound()) {
								entityItem12.item.setTagCompound((NBTTagCompound)itemStack7.getTagCompound().copy());
							}

							float f13 = 0.05F;
							entityItem12.motionX = (double)((float)this.furnaceRand.nextGaussian() * f13);
							entityItem12.motionY = (double)((float)this.furnaceRand.nextGaussian() * f13 + 0.2F);
							entityItem12.motionZ = (double)((float)this.furnaceRand.nextGaussian() * f13);
							world1.spawnEntityInWorld(entityItem12);
						}
					}
				}
			}
		}

		super.onBlockRemoval(world1, i2, i3, i4);
	}
}
