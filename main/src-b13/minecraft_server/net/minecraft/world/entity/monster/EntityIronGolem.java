package net.minecraft.world.entity.monster;

import com.mojang.nbt.NBTTagCompound;

import net.minecraft.src.MathHelper;
import net.minecraft.world.entity.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.world.entity.ai.EntityAIDefendVillage;
import net.minecraft.world.entity.ai.EntityAIHurtByTarget;
import net.minecraft.world.entity.ai.EntityAILookAtVillager;
import net.minecraft.world.entity.ai.EntityAILookIdle;
import net.minecraft.world.entity.ai.EntityAIMoveTowardsTarget;
import net.minecraft.world.entity.ai.EntityAIMoveTwardsRestriction;
import net.minecraft.world.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.world.entity.ai.EntityAIWander;
import net.minecraft.world.entity.ai.EntityAIWatchClosest;
import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.World;
import net.minecraft.world.level.tile.Block;

public class EntityIronGolem extends EntityGolem {
	//private int field_48119_b = 0;
	//Village villageObj = null;
	private int field_48120_c;
	private int field_48118_d;

	public EntityIronGolem(World world1) {
		super(world1);
		this.texture = "/mob/villager_golem.png";
		this.setSize(1.4F, 2.9F);
		this.getNavigator().setAvoidsWater(true);
		this.tasks.addTask(1, new EntityAIAttackOnCollide(this, 0.25F, true));
		this.tasks.addTask(2, new EntityAIMoveTowardsTarget(this, 0.22F, 32.0F));
		//this.tasks.addTask(3, new EntityAIMoveThroughVillage(this, 0.16F, true));
		this.tasks.addTask(4, new EntityAIMoveTwardsRestriction(this, 0.16F));
		this.tasks.addTask(5, new EntityAILookAtVillager(this));
		this.tasks.addTask(6, new EntityAIWander(this, 0.16F));
		this.tasks.addTask(7, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
		this.tasks.addTask(8, new EntityAILookIdle(this));
		this.targetTasks.addTask(1, new EntityAIDefendVillage(this));
		this.targetTasks.addTask(2, new EntityAIHurtByTarget(this, false));
		this.targetTasks.addTask(3, new EntityAINearestAttackableTarget(this, EntityMob.class, 16.0F, 0, false, true));
	}

	protected void entityInit() {
		super.entityInit();
		this.dataWatcher.addObject(16, (byte)0);
	}

	public boolean isAIEnabled() {
		return true;
	}

	protected void updateAITick() {
		/*
		if(--this.field_48119_b <= 0) {
			this.field_48119_b = 70 + this.rand.nextInt(50);
			this.villageObj = this.worldObj.villageCollectionObj.findNearestVillage(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ), 32);
			if(this.villageObj == null) {
				this.detachHome();
			} else {
				ChunkCoordinates chunkCoordinates1 = this.villageObj.getCenter();
				this.setHomeArea(chunkCoordinates1.posX, chunkCoordinates1.posY, chunkCoordinates1.posZ, this.villageObj.getVillageRadius());
			}
		}
		*/

		super.updateAITick();
	}

	public int getMaxHealth() {
		return 100;
	}

	protected int decreaseAirSupply(int i1) {
		return i1;
	}

	public void onLivingUpdate() {
		super.onLivingUpdate();
		if(this.field_48120_c > 0) {
			--this.field_48120_c;
		}

		if(this.field_48118_d > 0) {
			--this.field_48118_d;
		}

		if(this.motionX * this.motionX + this.motionZ * this.motionZ > 2.500000277905201E-7D && this.rand.nextInt(5) == 0) {
			int i1 = MathHelper.floor_double(this.posX);
			int i2 = MathHelper.floor_double(this.posY - (double)0.2F - (double)this.yOffset);
			int i3 = MathHelper.floor_double(this.posZ);
			int i4 = this.worldObj.getBlockId(i1, i2, i3);
			if(i4 > 0) {
				this.worldObj.spawnParticle("tilecrack_" + i4, this.posX + ((double)this.rand.nextFloat() - 0.5D) * (double)this.width, this.boundingBox.minY + 0.1D, this.posZ + ((double)this.rand.nextFloat() - 0.5D) * (double)this.width, 4.0D * ((double)this.rand.nextFloat() - 0.5D), 0.5D, ((double)this.rand.nextFloat() - 0.5D) * 4.0D);
			}
		}

	}

	public boolean func_48100_a(Class<?> class1) {
		return this.func_48112_E_() && EntityPlayer.class.isAssignableFrom(class1) ? false : super.func_48100_a(class1);
	}

	public void writeEntityToNBT(NBTTagCompound compoundTag) {
		super.writeEntityToNBT(compoundTag);
		compoundTag.setBoolean("PlayerCreated", this.func_48112_E_());
	}

	public void readEntityFromNBT(NBTTagCompound compoundTag) {
		super.readEntityFromNBT(compoundTag);
		this.setCreatedByPlayer(compoundTag.getBoolean("PlayerCreated"));
	}

	public boolean attackEntityAsMob(Entity entity1) {
		this.field_48120_c = 10;
		this.worldObj.setEntityState(this, (byte)4);
		boolean z2 = entity1.attackEntityFrom(DamageSource.causeMobDamage(this), 7 + this.rand.nextInt(15));
		if(z2) {
			entity1.motionY += (double)0.4F;
		}

		this.worldObj.playSoundAtEntity(this, "mob.irongolem.throw", 1.0F, 1.0F);
		return z2;
	}

	public void handleHealthUpdate(byte b1) {
		if(b1 == 4) {
			this.field_48120_c = 10;
			this.worldObj.playSoundAtEntity(this, "mob.irongolem.throw", 1.0F, 1.0F);
		} else if(b1 == 11) {
			this.field_48118_d = 400;
		} else {
			super.handleHealthUpdate(b1);
		}

	}

	/*
	public Village getVillage() {
		return this.villageObj;
	}
	*/

	public int func_48114_ab() {
		return this.field_48120_c;
	}

	public void func_48116_a(boolean z1) {
		this.field_48118_d = z1 ? 400 : 0;
		this.worldObj.setEntityState(this, (byte)11);
	}

	protected String getLivingSound() {
		return "none";
	}

	protected String getHurtSound() {
		return "mob.irongolem.hit";
	}

	protected String getDeathSound() {
		return "mob.irongolem.death";
	}

	protected void playStepSound(int i1, int i2, int i3, int i4) {
		this.worldObj.playSoundAtEntity(this, "mob.irongolem.walk", 1.0F, 1.0F);
	}

	protected void dropFewItems(boolean z1, int i2) {
		int i3 = this.rand.nextInt(3);

		int i4;
		for(i4 = 0; i4 < i3; ++i4) {
			this.dropItem(Block.plantRed.blockID, 1);
		}

		i4 = 3 + this.rand.nextInt(3);

		for(int i5 = 0; i5 < i4; ++i5) {
			this.dropItem(Item.ingotIron.shiftedIndex, 1);
		}

	}

	public int func_48117_D_() {
		return this.field_48118_d;
	}

	public boolean func_48112_E_() {
		return (this.dataWatcher.getWatchableObjectByte(16) & 1) != 0;
	}

	public void setCreatedByPlayer(boolean z1) {
		byte b2 = this.dataWatcher.getWatchableObjectByte(16);
		if(z1) {
			this.dataWatcher.updateObject(16, (byte)(b2 | 1));
		} else {
			this.dataWatcher.updateObject(16, (byte)(b2 & -2));
		}

	}
}
