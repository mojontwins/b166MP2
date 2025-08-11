package net.minecraft.world.entity.monster;

import com.mojang.nbt.NBTTagCompound;

import net.minecraft.world.entity.DamageSource;
import net.minecraft.world.entity.DataWatchers;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLightningBolt;
import net.minecraft.world.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.world.entity.ai.EntityAIAvoidEntity;
import net.minecraft.world.entity.ai.EntityAICreeperSwell;
import net.minecraft.world.entity.ai.EntityAIHurtByTarget;
import net.minecraft.world.entity.ai.EntityAILookIdle;
import net.minecraft.world.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.world.entity.ai.EntityAISwimming;
import net.minecraft.world.entity.ai.EntityAIWander;
import net.minecraft.world.entity.ai.EntityAIWatchClosest;
import net.minecraft.world.entity.animal.EntityOcelot;
import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.World;

public class EntityCreeper extends EntityMob implements IMob {
	int timeSinceIgnited;
	int lastActiveTime;

	public EntityCreeper(World world1) {
		super(world1);
		this.texture = "/mob/creeper.png";
		this.tasks.addTask(1, new EntityAISwimming(this));
		this.tasks.addTask(2, new EntityAICreeperSwell(this));
		this.tasks.addTask(3, new EntityAIAvoidEntity(this, EntityOcelot.class, 6.0F, 0.25F, 0.3F));
		this.tasks.addTask(4, new EntityAIAttackOnCollide(this, 0.25F, false));
		this.tasks.addTask(5, new EntityAIWander(this, 0.2F));
		this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
		this.tasks.addTask(6, new EntityAILookIdle(this));
		this.targetTasks.addTask(1, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 16.0F, 0, true));
		this.targetTasks.addTask(2, new EntityAIHurtByTarget(this, false));
	}

	public boolean isAIEnabled() {
		return true;
	}

	public int getMaxHealth() {
		return 20;
	}

	protected void entityInit() {
		super.entityInit();
		this.dataWatcher.addObject(DataWatchers.DW_STATUS, (byte)-1);
		this.dataWatcher.addObject(DataWatchers.DW_TYPE, (byte)0);
	}

	public void writeEntityToNBT(NBTTagCompound compoundTag) {
		super.writeEntityToNBT(compoundTag);
		if(this.dataWatcher.getWatchableObjectByte(DataWatchers.DW_TYPE) == 1) {
			compoundTag.setBoolean("powered", true);
		}

	}

	public void readEntityFromNBT(NBTTagCompound compoundTag) {
		super.readEntityFromNBT(compoundTag);
		this.dataWatcher.updateObject(DataWatchers.DW_TYPE, (byte)(compoundTag.getBoolean("powered") ? 1 : 0));
	}

	public void onUpdate() {
		if(this.isEntityAlive()) {
			this.lastActiveTime = this.timeSinceIgnited;
			int i1 = this.getCreeperState();
			if(i1 > 0 && this.timeSinceIgnited == 0) {
				this.worldObj.playSoundAtEntity(this, "random.fuse", 1.0F, 0.5F);
			}

			this.timeSinceIgnited += i1;
			if(this.timeSinceIgnited < 0) {
				this.timeSinceIgnited = 0;
			}

			if(this.timeSinceIgnited >= 30) {
				this.timeSinceIgnited = 30;
				if(!this.worldObj.isRemote) {
					if(this.getPowered()) {
						this.worldObj.createExplosion(this, this.posX, this.posY, this.posZ, 6.0F);
					} else {
						this.worldObj.createExplosion(this, this.posX, this.posY, this.posZ, 3.0F);
					}

					this.setDead();
				}
			}
		}

		super.onUpdate();
	}

	protected String getHurtSound() {
		return "mob.creeper";
	}

	protected String getDeathSound() {
		return "mob.creeperdeath";
	}

	public void onDeath(DamageSource damageSource1) {
		super.onDeath(damageSource1);
		if(damageSource1.getEntity() instanceof EntitySkeleton) {
			this.dropItem(Item.record13.shiftedIndex + this.rand.nextInt(10), 1);
		}

	}

	public boolean attackEntityAsMob(Entity entity1) {
		return true;
	}

	public boolean getPowered() {
		return this.dataWatcher.getWatchableObjectByte(DataWatchers.DW_TYPE) == 1;
	}

	public float setCreeperFlashTime(float f1) {
		return ((float)this.lastActiveTime + (float)(this.timeSinceIgnited - this.lastActiveTime) * f1) / 28.0F;
	}

	protected int getDropItemId() {
		return Item.gunpowder.shiftedIndex;
	}

	public int getCreeperState() {
		return this.dataWatcher.getWatchableObjectByte(DataWatchers.DW_STATUS);
	}

	public void setCreeperState(int i1) {
		this.dataWatcher.updateObject(DataWatchers.DW_STATUS, (byte)i1);
	}

	public void onStruckByLightning(EntityLightningBolt entityLightningBolt1) {
		super.onStruckByLightning(entityLightningBolt1);
		this.dataWatcher.updateObject(DataWatchers.DW_TYPE, (byte)1);
	}
}
