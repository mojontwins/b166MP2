package net.minecraft.world.entity.animal;

import com.mojang.nbt.NBTTagCompound;

import net.minecraft.world.GameRules;
import net.minecraft.world.entity.ai.EntityAIFollowParent;
import net.minecraft.world.entity.ai.EntityAILookIdle;
import net.minecraft.world.entity.ai.EntityAIMate;
import net.minecraft.world.entity.ai.EntityAIPanic;
import net.minecraft.world.entity.ai.EntityAISwimming;
import net.minecraft.world.entity.ai.EntityAITempt;
import net.minecraft.world.entity.ai.EntityAIWander;
import net.minecraft.world.entity.ai.EntityAIWatchClosest;
import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.World;

public class EntityCow extends EntityAnimal {
	public EntityCow(World world1) {
		super(world1);
		this.texture = "/mob/cow.png";
		this.setSize(0.9F, 1.3F);
		this.getNavigator().setAvoidsWater(true);
		this.tasks.addTask(0, new EntityAISwimming(this));
		this.tasks.addTask(1, new EntityAIPanic(this, 0.38F));
		if (GameRules.boolRule("canBreedAnimals")) {
			this.tasks.addTask(2, new EntityAIMate(this, 0.2F));
		}
		this.tasks.addTask(3, new EntityAITempt(this, 0.25F, Item.wheat.shiftedIndex, false));
		this.tasks.addTask(4, new EntityAIFollowParent(this, 0.25F));
		this.tasks.addTask(5, new EntityAIWander(this, 0.2F));
		this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
		this.tasks.addTask(7, new EntityAILookIdle(this));
	}

	public boolean isAIEnabled() {
		return true;
	}

	public int getMaxHealth() {
		return 10;
	}

	public void writeEntityToNBT(NBTTagCompound compoundTag) {
		super.writeEntityToNBT(compoundTag);
	}

	public void readEntityFromNBT(NBTTagCompound compoundTag) {
		super.readEntityFromNBT(compoundTag);
	}

	protected String getLivingSound() {
		return "mob.cow";
	}

	protected String getHurtSound() {
		return "mob.cowhurt";
	}

	protected String getDeathSound() {
		return "mob.cowhurt";
	}

	protected float getSoundVolume() {
		return 0.4F;
	}

	protected int getDropItemId() {
		return Item.leather.shiftedIndex;
	}

	protected void dropFewItems(boolean justHit, int looting) {
		if(!GameRules.boolRule("edibleCows")) {
			super.dropFewItems(justHit, looting);
		} else {
			int amount = this.rand.nextInt(3) + this.rand.nextInt(1 + looting);
	
			int i;
			for(i = 0; i < amount; ++i) {
				if(this.rand.nextInt(3) != 0) {
					this.dropItem(Item.leather.shiftedIndex, 1);
				} else {
					if(this.isBurning()) {
						this.dropItem(Item.beefCooked.shiftedIndex, 1);
					} else {
						this.dropItem(Item.beefRaw.shiftedIndex, 1);
					}
				}
			}
	
		}
	}

	public boolean interact(EntityPlayer entityPlayer1) {
		ItemStack itemStack2 = entityPlayer1.inventory.getCurrentItem();
		if(itemStack2 != null && itemStack2.itemID == Item.bucketEmpty.shiftedIndex) {
			entityPlayer1.inventory.setInventorySlotContents(entityPlayer1.inventory.currentItem, new ItemStack(Item.bucketMilk));
			return true;
		} else {
			return super.interact(entityPlayer1);
		}
	}

	public EntityAnimal spawnBabyAnimal(EntityAnimal entityAnimal1) {
		return new EntityCow(this.worldObj);
	}
}
