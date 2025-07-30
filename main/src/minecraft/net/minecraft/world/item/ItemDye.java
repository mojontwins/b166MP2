package net.minecraft.world.item;

import java.util.List;

import net.minecraft.src.MathHelper;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.IDyeableEntity;
import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.level.World;
import net.minecraft.world.level.creative.CreativeTabs;
import net.minecraft.world.level.tile.Block;
import net.minecraft.world.level.tile.BlockCloth;
import net.minecraft.world.level.tile.BlockCrops;
import net.minecraft.world.level.tile.BlockMushroom;
import net.minecraft.world.level.tile.BlockSapling;

public class ItemDye extends Item {
	
	public static final String[] dyeColorNames = new String[]{
		"black", "red", "green", "brown", "blue", "purple", "cyan", "silver", 
		"gray", "pink", "lime", "yellow", "lightBlue", "magenta", "orange", "white"
	};

	public ItemDye(int itemID) {
		super(itemID);
		this.setHasSubtypes(true);
		this.setMaxDamage(0);
		
		this.displayOnCreativeTab = CreativeTabs.tabMaterials;
	}

	public int getIconFromDamage(int damage) {
		int dyeIndex = MathHelper.clamp_int(damage, 0, 15);
		return this.iconIndex + dyeIndex % 8 * 16 + dyeIndex / 8;
	}

	public String getItemNameIS(ItemStack itemStack) {
		int dyeIndex = MathHelper.clamp_int(itemStack.getItemDamage(), 0, 15);
		return super.getItemName() + "." + dyeColorNames[dyeIndex];
	}

	public boolean onItemUse(ItemStack itemStack, EntityPlayer entityPlayer, World world, int x, int y, int z, int side) {
		if(!entityPlayer.canPlayerEdit(x, y, z)) {
			return false;
		} else {

			// Damage 15 is white dye or bone meal. Used to fertilize:
			
			if(itemStack.getItemDamage() == 15) {
				int i8 = world.getBlockId(x, y, z);
				if(i8 == Block.sapling.blockID) {
					if(!world.isRemote) {
						((BlockSapling)Block.sapling).growTree(world, x, y, z, world.rand);
						--itemStack.stackSize;
					}

					return true;
				}

				if(i8 == Block.mushroomBrown.blockID || i8 == Block.mushroomRed.blockID) {
					if(!world.isRemote && ((BlockMushroom)Block.blocksList[i8]).fertilizeMushroom(world, x, y, z, world.rand)) {
						--itemStack.stackSize;
					}

					return true;
				}

				if(i8 == Block.crops.blockID) {
					if(!world.isRemote) {
						((BlockCrops)Block.crops).fertilize(world, x, y, z);
						--itemStack.stackSize;
					}

					return true;
				}

				if(i8 == Block.grass.blockID) {
					if(!world.isRemote) {
						--itemStack.stackSize;

						label73:
						for(int i9 = 0; i9 < 128; ++i9) {
							int i10 = x;
							int i11 = y + 1;
							int i12 = z;

							for(int i13 = 0; i13 < i9 / 16; ++i13) {
								i10 += itemRand.nextInt(3) - 1;
								i11 += (itemRand.nextInt(3) - 1) * itemRand.nextInt(3) / 2;
								i12 += itemRand.nextInt(3) - 1;
								if(world.getBlockId(i10, i11 - 1, i12) != Block.grass.blockID || world.isBlockNormalCube(i10, i11, i12)) {
									continue label73;
								}
							}

							if(world.getBlockId(i10, i11, i12) == 0) {
								if(itemRand.nextInt(5) != 0) {
									//world.setBlockAndMetadataWithNotify(i10, i11, i12, Block.tallGrass.blockID, 1);
								} /*else if(itemRand.nextInt(5) == 0) {
									world.setBlockAndMetadataWithNotify(i10, i11, i12, Block.customFlower.blockID, itemRand.nextInt(8));
								} */else if(itemRand.nextInt(3) != 0) {
									world.setBlockWithNotify(i10, i11, i12, Block.plantYellow.blockID);
								} else {
									world.setBlockWithNotify(i10, i11, i12, Block.plantRed.blockID);
								}
							}
						}
					}

					return true;
				}
			}

			return false;
		}
	}

	public void useItemOnEntity(ItemStack theStack, EntityLiving entityLiving) {
		
		// You can color dyeable entities

		if(entityLiving instanceof IDyeableEntity) {
			IDyeableEntity theEntity = (IDyeableEntity)entityLiving;
			int color = BlockCloth.getFleeceColorFromDamage(theStack.getItemDamage());
			if(theEntity.admitsDyeing() && theEntity.getDyeColor() != color) {
				theEntity.setDyeColor(color);
				--theStack.stackSize;
			}
		}

	}
	
	public void getSubItems(int blockID, CreativeTabs creativeTab, List<ItemStack> itemStacks) {
		for(int i = 0; i < dyeColorNames.length; i ++) {
			itemStacks.add(new ItemStack(blockID, 1, i));
		}
	}
}
