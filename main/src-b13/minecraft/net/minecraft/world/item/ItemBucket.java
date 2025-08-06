package net.minecraft.world.item;

import net.minecraft.world.entity.animal.EntityCow;
import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.level.World;
import net.minecraft.world.level.creative.CreativeTabs;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.tile.Block;
import net.minecraft.world.phys.EnumMovingObjectType;
import net.minecraft.world.phys.MovingObjectPosition;

public class ItemBucket extends Item {
	private int isFull;

	public ItemBucket(int i1, int i2) {
		super(i1);
		this.maxStackSize = 1;
		this.isFull = i2;
		
		this.displayOnCreativeTab = CreativeTabs.tabMisc;
	}

	public ItemStack onItemRightClick(ItemStack itemStack1, World world2, EntityPlayer entityPlayer3) {
		float f4 = 1.0F;
		double d5 = entityPlayer3.prevPosX + (entityPlayer3.posX - entityPlayer3.prevPosX) * (double)f4;
		double d7 = entityPlayer3.prevPosY + (entityPlayer3.posY - entityPlayer3.prevPosY) * (double)f4 + 1.62D - (double)entityPlayer3.yOffset;
		double d9 = entityPlayer3.prevPosZ + (entityPlayer3.posZ - entityPlayer3.prevPosZ) * (double)f4;
		boolean z11 = this.isFull == 0;
		MovingObjectPosition movingObjectPosition12 = this.getMovingObjectPositionFromPlayer(world2, entityPlayer3, z11);
		if(movingObjectPosition12 == null) {
			return itemStack1;
		} else {
			if(movingObjectPosition12.typeOfHit == EnumMovingObjectType.TILE) {
				int i13 = movingObjectPosition12.blockX;
				int i14 = movingObjectPosition12.blockY;
				int i15 = movingObjectPosition12.blockZ;
				if(!world2.canMineBlock(entityPlayer3, i13, i14, i15)) {
					return itemStack1;
				}

				if(this.isFull == 0) {
					if(!entityPlayer3.canPlayerEdit(i13, i14, i15)) {
						return itemStack1;
					}

					if(world2.getBlockMaterial(i13, i14, i15) == Material.water && world2.getBlockMetadata(i13, i14, i15) == 0) {
						world2.setBlockWithNotify(i13, i14, i15, 0);
						if(entityPlayer3.capabilities.isCreativeMode) {
							return itemStack1;
						}

						return new ItemStack(Item.bucketWater);
					}

					if(world2.getBlockMaterial(i13, i14, i15) == Material.lava && world2.getBlockMetadata(i13, i14, i15) == 0) {
						world2.setBlockWithNotify(i13, i14, i15, 0);
						if(entityPlayer3.capabilities.isCreativeMode) {
							return itemStack1;
						}

						return new ItemStack(Item.bucketLava);
					}
				} else {
					if(this.isFull < 0) {
						return new ItemStack(Item.bucketEmpty);
					}

					if(movingObjectPosition12.sideHit == 0) {
						--i14;
					}

					if(movingObjectPosition12.sideHit == 1) {
						++i14;
					}

					if(movingObjectPosition12.sideHit == 2) {
						--i15;
					}

					if(movingObjectPosition12.sideHit == 3) {
						++i15;
					}

					if(movingObjectPosition12.sideHit == 4) {
						--i13;
					}

					if(movingObjectPosition12.sideHit == 5) {
						++i13;
					}

					if(!entityPlayer3.canPlayerEdit(i13, i14, i15)) {
						return itemStack1;
					}

					if(world2.isAirBlock(i13, i14, i15) || !world2.getBlockMaterial(i13, i14, i15).isSolid()) {
						if(world2.worldProvider.isHellWorld && this.isFull == Block.waterMoving.blockID) {
							world2.playSoundEffect(d5 + 0.5D, d7 + 0.5D, d9 + 0.5D, "random.fizz", 0.5F, 2.6F + (world2.rand.nextFloat() - world2.rand.nextFloat()) * 0.8F);

							for(int i16 = 0; i16 < 8; ++i16) {
								world2.spawnParticle("largesmoke", (double)i13 + Math.random(), (double)i14 + Math.random(), (double)i15 + Math.random(), 0.0D, 0.0D, 0.0D);
							}
						} else {
							world2.setBlockAndMetadataWithNotify(i13, i14, i15, this.isFull, 0);
						}

						if(entityPlayer3.capabilities.isCreativeMode) {
							return itemStack1;
						}

						return new ItemStack(Item.bucketEmpty);
					}
				}
			} else if(this.isFull == 0 && movingObjectPosition12.entityHit instanceof EntityCow) {
				return new ItemStack(Item.bucketMilk);
			}

			return itemStack1;
		}
	}
}
