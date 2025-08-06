package net.minecraft.world.level.tile;

import java.util.Random;

import com.mojang.nbt.NBTTagCompound;

import net.minecraft.src.MathHelper;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.item.EntityItem;
import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.entity.projectile.EntityArrow;
import net.minecraft.world.entity.projectile.EntityEgg;
import net.minecraft.world.entity.projectile.EntitySmallFireball;
import net.minecraft.world.entity.projectile.EntitySnowball;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemMonsterPlacer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.World;
import net.minecraft.world.level.creative.CreativeTabs;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.tile.entity.TileEntity;
import net.minecraft.world.level.tile.entity.TileEntityDispenser;

public class BlockDispenser extends BlockContainer {
	private Random random = new Random();

	protected BlockDispenser(int i1) {
		super(i1, Material.rock);
		this.blockIndexInTexture = 45;
		
		this.displayOnCreativeTab = CreativeTabs.tabRedstone;
	}

	public int tickRate() {
		return 4;
	}

	public int idDropped(int i1, Random random2, int i3) {
		return Block.dispenser.blockID;
	}

	public void onBlockAdded(World world1, int i2, int i3, int i4) {
		super.onBlockAdded(world1, i2, i3, i4);
		this.setDispenserDefaultDirection(world1, i2, i3, i4);
	}

	private void setDispenserDefaultDirection(World world1, int i2, int i3, int i4) {
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
		if(i5 == 1) {
			return this.blockIndexInTexture + 17;
		} else if(i5 == 0) {
			return this.blockIndexInTexture + 17;
		} else {
			int i6 = iBlockAccess1.getBlockMetadata(i2, i3, i4);
			return i5 != i6 ? this.blockIndexInTexture : this.blockIndexInTexture + 1;
		}
	}

	public int getBlockTextureFromSide(int i1) {
		return i1 == 1 ? this.blockIndexInTexture + 17 : (i1 == 0 ? this.blockIndexInTexture + 17 : (i1 == 3 ? this.blockIndexInTexture + 1 : this.blockIndexInTexture));
	}

	public boolean blockActivated(World world1, int i2, int i3, int i4, EntityPlayer entityPlayer5) {
		if(world1.isRemote) {
			return true;
		} else {
			TileEntityDispenser tileEntityDispenser6 = (TileEntityDispenser)world1.getBlockTileEntity(i2, i3, i4);
			if(tileEntityDispenser6 != null) { 
				entityPlayer5.displayGUIDispenser(tileEntityDispenser6);
			}

			return true;
		}
	}

	private void dispenseItem(World world1, int i2, int i3, int i4, Random random5) {
		int i6 = world1.getBlockMetadata(i2, i3, i4);
		byte b9 = 0;
		byte b10 = 0;
		if(i6 == 3) {
			b10 = 1;
		} else if(i6 == 2) {
			b10 = -1;
		} else if(i6 == 5) {
			b9 = 1;
		} else {
			b9 = -1;
		}

		TileEntityDispenser tileEntityDispenser11 = (TileEntityDispenser)world1.getBlockTileEntity(i2, i3, i4);
		if(tileEntityDispenser11 != null) {
			ItemStack itemStack12 = tileEntityDispenser11.getRandomStackFromInventory();
			double d13 = (double)i2 + (double)b9 * 0.6D + 0.5D;
			double d15 = (double)i3 + 0.5D;
			double d17 = (double)i4 + (double)b10 * 0.6D + 0.5D;
			if(itemStack12 == null) {
				world1.playAuxSFX(1001, i2, i3, i4, 0);
			} else {
				if(itemStack12.itemID == Item.arrow.shiftedIndex) {
					EntityArrow entityArrow27 = new EntityArrow(world1, d13, d15, d17);
					entityArrow27.setArrowHeading((double)b9, (double)0.1F, (double)b10, 1.1F, 6.0F);
					entityArrow27.doesArrowBelongToPlayer = true;
					world1.spawnEntityInWorld(entityArrow27);
					world1.playAuxSFX(1002, i2, i3, i4, 0);
				} else if(itemStack12.itemID == Item.egg.shiftedIndex) {
					EntityEgg entityEgg26 = new EntityEgg(world1, d13, d15, d17);
					entityEgg26.setThrowableHeading((double)b9, (double)0.1F, (double)b10, 1.1F, 6.0F);
					world1.spawnEntityInWorld(entityEgg26);
					world1.playAuxSFX(1002, i2, i3, i4, 0);
				} else if(itemStack12.itemID == Item.snowball.shiftedIndex) {
					EntitySnowball entitySnowball25 = new EntitySnowball(world1, d13, d15, d17);
					entitySnowball25.setThrowableHeading((double)b9, (double)0.1F, (double)b10, 1.1F, 6.0F);
					world1.spawnEntityInWorld(entitySnowball25);
					world1.playAuxSFX(1002, i2, i3, i4, 0);
				} /*else if(itemStack12.itemID == Item.potion.shiftedIndex && ItemPotion.isSplash(itemStack12.getItemDamage())) {
					EntityPotion entityPotion24 = new EntityPotion(world1, d13, d15, d17, itemStack12.getItemDamage());
					entityPotion24.setThrowableHeading((double)b9, (double)0.1F, (double)b10, 1.375F, 3.0F);
					world1.spawnEntityInWorld(entityPotion24);
					world1.playAuxSFX(1002, i2, i3, i4, 0);
				} else if(itemStack12.itemID == Item.expBottle.shiftedIndex) {
					EntityExpBottle entityExpBottle19 = new EntityExpBottle(world1, d13, d15, d17);
					entityExpBottle19.setThrowableHeading((double)b9, (double)0.1F, (double)b10, 1.375F, 3.0F);
					world1.spawnEntityInWorld(entityExpBottle19);
					world1.playAuxSFX(1002, i2, i3, i4, 0);
				} */else if(itemStack12.itemID == Item.monsterPlacer.shiftedIndex) {
					ItemMonsterPlacer.hatchEgg(world1, itemStack12.getItemDamage(), d13 + (double)b9 * 0.3D, d15 - 0.3D, d17 + (double)b10 * 0.3D);
					world1.playAuxSFX(1002, i2, i3, i4, 0);
				} else if(itemStack12.itemID == Item.fireballCharge.shiftedIndex) {
					EntitySmallFireball entitySmallFireball22 = new EntitySmallFireball(world1, d13 + (double)b9 * 0.3D, d15, d17 + (double)b10 * 0.3D, (double)b9 + random5.nextGaussian() * 0.05D, random5.nextGaussian() * 0.05D, (double)b10 + random5.nextGaussian() * 0.05D);
					world1.spawnEntityInWorld(entitySmallFireball22);
					world1.playAuxSFX(1009, i2, i3, i4, 0);
				} else {
					EntityItem entityItem23 = new EntityItem(world1, d13, d15 - 0.3D, d17, itemStack12);
					double d20 = random5.nextDouble() * 0.1D + 0.2D;
					entityItem23.motionX = (double)b9 * d20;
					entityItem23.motionY = (double)0.2F;
					entityItem23.motionZ = (double)b10 * d20;
					entityItem23.motionX += random5.nextGaussian() * (double)0.0075F * 6.0D;
					entityItem23.motionY += random5.nextGaussian() * (double)0.0075F * 6.0D;
					entityItem23.motionZ += random5.nextGaussian() * (double)0.0075F * 6.0D;
					world1.spawnEntityInWorld(entityItem23);
					world1.playAuxSFX(1000, i2, i3, i4, 0);
				}

				world1.playAuxSFX(2000, i2, i3, i4, b9 + 1 + (b10 + 1) * 3);
			}
		}

	}

	public void onNeighborBlockChange(World world1, int i2, int i3, int i4, int i5) {
		if(i5 > 0 && Block.blocksList[i5].canProvidePower()) {
			boolean z6 = world1.isBlockIndirectlyGettingPowered(i2, i3, i4) || world1.isBlockIndirectlyGettingPowered(i2, i3 + 1, i4);
			if(z6) {
				world1.scheduleBlockUpdate(i2, i3, i4, this.blockID, this.tickRate());
			}
		}

	}

	public void updateTick(World world1, int i2, int i3, int i4, Random random5) {
		if(!world1.isRemote && (world1.isBlockIndirectlyGettingPowered(i2, i3, i4) || world1.isBlockIndirectlyGettingPowered(i2, i3 + 1, i4))) {
			this.dispenseItem(world1, i2, i3, i4, random5);
		}

	}

	public TileEntity getBlockEntity() {
		return new TileEntityDispenser();
	}

	public void onBlockPlacedBy(World world1, int i2, int i3, int i4, EntityLiving entityLiving5) {
		int i6 = MathHelper.floor_double((double)(entityLiving5.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
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
		TileEntityDispenser tileEntityDispenser5 = (TileEntityDispenser)world1.getBlockTileEntity(i2, i3, i4);
		if(tileEntityDispenser5 != null) {
			for(int i6 = 0; i6 < tileEntityDispenser5.getSizeInventory(); ++i6) {
				ItemStack itemStack7 = tileEntityDispenser5.getStackInSlot(i6);
				if(itemStack7 != null) {
					float f8 = this.random.nextFloat() * 0.8F + 0.1F;
					float f9 = this.random.nextFloat() * 0.8F + 0.1F;
					float f10 = this.random.nextFloat() * 0.8F + 0.1F;

					while(itemStack7.stackSize > 0) {
						int i11 = this.random.nextInt(21) + 10;
						if(i11 > itemStack7.stackSize) {
							i11 = itemStack7.stackSize;
						}

						itemStack7.stackSize -= i11;
						EntityItem entityItem12 = new EntityItem(world1, (double)((float)i2 + f8), (double)((float)i3 + f9), (double)((float)i4 + f10), new ItemStack(itemStack7.itemID, i11, itemStack7.getItemDamage()));
						if(itemStack7.hasTagCompound()) {
							entityItem12.item.setTagCompound((NBTTagCompound)itemStack7.getTagCompound().copy());
						}

						float f13 = 0.05F;
						entityItem12.motionX = (double)((float)this.random.nextGaussian() * f13);
						entityItem12.motionY = (double)((float)this.random.nextGaussian() * f13 + 0.2F);
						entityItem12.motionZ = (double)((float)this.random.nextGaussian() * f13);
						world1.spawnEntityInWorld(entityItem12);
					}
				}
			}
		}

		super.onBlockRemoval(world1, i2, i3, i4);
	}
}
