package net.minecraft.world.entity.animal;

import com.mojang.nbt.NBTTagCompound;

import net.minecraft.world.GameRules;
import net.minecraft.world.entity.DataWatchers;
import net.minecraft.world.entity.EntityLightningBolt;
import net.minecraft.world.entity.ai.EntityAIFollowParent;
import net.minecraft.world.entity.ai.EntityAILookIdle;
import net.minecraft.world.entity.ai.EntityAIMate;
import net.minecraft.world.entity.ai.EntityAIPanic;
import net.minecraft.world.entity.ai.EntityAISwimming;
import net.minecraft.world.entity.ai.EntityAITempt;
import net.minecraft.world.entity.ai.EntityAIWander;
import net.minecraft.world.entity.ai.EntityAIWatchClosest;
import net.minecraft.world.entity.monster.EntityPigZombie;
import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.World;

public class EntityPig extends EntityAnimal {
	public EntityPig(World world1) {
		super(world1);
		this.texture = "/mob/pig.png";
		this.setSize(0.9F, 0.9F);
		this.getNavigator().setAvoidsWater(true);
		float f2 = 0.25F;
		this.tasks.addTask(0, new EntityAISwimming(this));
		this.tasks.addTask(1, new EntityAIPanic(this, 0.38F));
		if (GameRules.boolRule("canBreedAnimals")) {
			this.tasks.addTask(2, new EntityAIMate(this, f2));
		}
		this.tasks.addTask(3, new EntityAITempt(this, 0.25F, Item.wheat.shiftedIndex, false));
		this.tasks.addTask(4, new EntityAIFollowParent(this, 0.28F));
		this.tasks.addTask(5, new EntityAIWander(this, f2));
		this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
		this.tasks.addTask(7, new EntityAILookIdle(this));
	}

	public boolean isAIEnabled() {
		return true;
	}

	public int getMaxHealth() {
		return 10;
	}

	protected void entityInit() {
		super.entityInit();
		this.dataWatcher.addObject(DataWatchers.DW_STATUS, (byte)0);
	}

	public void writeEntityToNBT(NBTTagCompound compoundTag) {
		super.writeEntityToNBT(compoundTag);
		compoundTag.setBoolean("Saddle", this.getSaddled());
	}

	public void readEntityFromNBT(NBTTagCompound compoundTag) {
		super.readEntityFromNBT(compoundTag);
		this.setSaddled(compoundTag.getBoolean("Saddle"));
	}

	protected String getLivingSound() {
		return "mob.pig";
	}

	protected String getHurtSound() {
		return "mob.pig";
	}

	protected String getDeathSound() {
		return "mob.pigdeath";
	}

	public boolean interact(EntityPlayer entityPlayer1) {
		if(super.interact(entityPlayer1)) {
			return true;
		} else if(!this.getSaddled() || this.worldObj.isRemote || this.riddenByEntity != null && this.riddenByEntity != entityPlayer1) {
			return false;
		} else {
			entityPlayer1.mountEntity(this);
			return true;
		}
	}

	protected int getDropItemId() {
		return this.isBurning() ? Item.porkCooked.shiftedIndex : Item.porkRaw.shiftedIndex;
	}

	public boolean getSaddled() {
		return (this.dataWatcher.getWatchableObjectByte(DataWatchers.DW_STATUS) & 1) != 0;
	}

	public void setSaddled(boolean z1) {
		if(z1) {
			this.dataWatcher.updateObject(DataWatchers.DW_STATUS, (byte)1);
		} else {
			this.dataWatcher.updateObject(DataWatchers.DW_STATUS, (byte)0);
		}

	}

	public void onStruckByLightning(EntityLightningBolt entityLightningBolt1) {
		if(!this.worldObj.isRemote) {
			EntityPigZombie entityPigZombie2 = new EntityPigZombie(this.worldObj);
			entityPigZombie2.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
			this.worldObj.spawnEntityInWorld(entityPigZombie2);
			this.setDead();
		}
	}

	protected void fall(float f1) {
		super.fall(f1);
	}

	public EntityAnimal spawnBabyAnimal(EntityAnimal entityAnimal1) {
		return new EntityPig(this.worldObj);
	}
}
