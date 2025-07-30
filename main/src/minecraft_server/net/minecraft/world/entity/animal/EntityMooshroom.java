package net.minecraft.world.entity.animal;

import net.minecraft.world.entity.item.EntityItem;
import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.World;
import net.minecraft.world.level.tile.Block;

public class EntityMooshroom extends EntityCow {
	public EntityMooshroom(World world1) {
		super(world1);
		this.texture = "/mob/redcow.png";
		this.setSize(0.9F, 1.3F);
	}

	public boolean interact(EntityPlayer entityPlayer1) {
		ItemStack itemStack2 = entityPlayer1.inventory.getCurrentItem();
		if(itemStack2 != null && itemStack2.itemID == Item.bowlEmpty.shiftedIndex && this.getGrowingAge() >= 0) {
			if(itemStack2.stackSize == 1) {
				entityPlayer1.inventory.setInventorySlotContents(entityPlayer1.inventory.currentItem, new ItemStack(Item.bowlSoup));
				return true;
			}

			if(entityPlayer1.inventory.addItemStackToInventory(new ItemStack(Item.bowlSoup)) && !entityPlayer1.capabilities.isCreativeMode) {
				entityPlayer1.inventory.decrStackSize(entityPlayer1.inventory.currentItem, 1);
				return true;
			}
		}

		if(itemStack2 != null && itemStack2.itemID == Item.shears.shiftedIndex && this.getGrowingAge() >= 0) {
			this.setDead();
			this.worldObj.spawnParticle("largeexplode", this.posX, this.posY + (double)(this.height / 2.0F), this.posZ, 0.0D, 0.0D, 0.0D);
			if(!this.worldObj.isRemote) {
				EntityCow entityCow3 = new EntityCow(this.worldObj);
				entityCow3.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
				entityCow3.setEntityHealth(this.getHealth());
				entityCow3.renderYawOffset = this.renderYawOffset;
				this.worldObj.spawnEntityInWorld(entityCow3);

				for(int i4 = 0; i4 < 5; ++i4) {
					this.worldObj.spawnEntityInWorld(new EntityItem(this.worldObj, this.posX, this.posY + (double)this.height, this.posZ, new ItemStack(Block.mushroomRed)));
				}
			}

			return true;
		} else {
			return super.interact(entityPlayer1);
		}
	}

	public EntityAnimal spawnBabyAnimal(EntityAnimal entityAnimal1) {
		return new EntityMooshroom(this.worldObj);
	}
}
