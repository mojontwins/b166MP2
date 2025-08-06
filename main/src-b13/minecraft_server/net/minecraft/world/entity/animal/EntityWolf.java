package net.minecraft.world.entity.animal;

import com.mojang.nbt.NBTTagCompound;

import net.minecraft.src.MathHelper;
import net.minecraft.world.GameRules;
import net.minecraft.world.entity.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.world.entity.ai.EntityAIBeg;
import net.minecraft.world.entity.ai.EntityAIFollowOwner;
import net.minecraft.world.entity.ai.EntityAIHurtByTarget;
import net.minecraft.world.entity.ai.EntityAILeapAtTarget;
import net.minecraft.world.entity.ai.EntityAILookIdle;
import net.minecraft.world.entity.ai.EntityAIMate;
import net.minecraft.world.entity.ai.EntityAIOwnerHurtByTarget;
import net.minecraft.world.entity.ai.EntityAIOwnerHurtTarget;
import net.minecraft.world.entity.ai.EntityAISwimming;
import net.minecraft.world.entity.ai.EntityAITargetNonTamed;
import net.minecraft.world.entity.ai.EntityAIWander;
import net.minecraft.world.entity.ai.EntityAIWatchClosest;
import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.entity.projectile.EntityArrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemFood;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.World;
import net.minecraft.world.level.pathfinder.PathEntity;

public class EntityWolf extends EntityTameable {
	private boolean looksWithInterest = false;
	private float field_25048_b;
	private float field_25054_c;
	private boolean isShaking;
	private boolean field_25052_g;
	private float timeWolfIsShaking;
	private float prevTimeWolfIsShaking;

	public EntityWolf(World world1) {
		super(world1);
		this.texture = "/mob/wolf.png";
		this.setSize(0.6F, 0.8F);
		this.moveSpeed = 0.3F;
		this.getNavigator().setAvoidsWater(true);
		this.tasks.addTask(1, new EntityAISwimming(this));
		this.tasks.addTask(2, this.aiSit);
		this.tasks.addTask(3, new EntityAILeapAtTarget(this, 0.4F));
		this.tasks.addTask(4, new EntityAIAttackOnCollide(this, this.moveSpeed, true));
		this.tasks.addTask(5, new EntityAIFollowOwner(this, this.moveSpeed, 10.0F, 2.0F, 144.0F));
		if (GameRules.boolRule("canBreedAnimals")) {
			this.tasks.addTask(6, new EntityAIMate(this, this.moveSpeed));
		}
		this.tasks.addTask(7, new EntityAIWander(this, this.moveSpeed));
		this.tasks.addTask(8, new EntityAIBeg(this, 8.0F));
		this.tasks.addTask(9, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
		this.tasks.addTask(9, new EntityAILookIdle(this));
		this.targetTasks.addTask(1, new EntityAIOwnerHurtByTarget(this));
		this.targetTasks.addTask(2, new EntityAIOwnerHurtTarget(this));
		this.targetTasks.addTask(3, new EntityAIHurtByTarget(this, true));
		this.targetTasks.addTask(4, new EntityAITargetNonTamed(this, EntitySheep.class, 16.0F, 200, false));
	}

	public boolean isAIEnabled() {
		return true;
	}

	public void setAttackTarget(EntityLiving entityLiving1) {
		super.setAttackTarget(entityLiving1);
		if(entityLiving1 instanceof EntityPlayer) {
			this.setAngry(true);
		}

	}

	protected void updateAITick() {
		this.dataWatcher.updateObject(18, this.getHealth());
	}

	public int getMaxHealth() {
		return this.isTamed() ? 20 : 8;
	}

	protected void entityInit() {
		super.entityInit();
		this.dataWatcher.addObject(18, Integer.valueOf(this.getHealth()));
	}

	protected boolean canTriggerWalking() {
		return false;
	}

	public String getTexture() {
		return this.isTamed() ? "/mob/wolf_tame.png" : (this.isAngry() ? "/mob/wolf_angry.png" : super.getTexture());
	}

	public void writeEntityToNBT(NBTTagCompound compoundTag) {
		super.writeEntityToNBT(compoundTag);
		compoundTag.setBoolean("Angry", this.isAngry());
	}

	public void readEntityFromNBT(NBTTagCompound compoundTag) {
		super.readEntityFromNBT(compoundTag);
		this.setAngry(compoundTag.getBoolean("Angry"));
	}

	protected boolean canDespawn() {
		return this.isAngry();
	}

	protected String getLivingSound() {
		return this.isAngry() ? "mob.wolf.growl" : (this.rand.nextInt(3) == 0 ? (this.isTamed() && this.dataWatcher.getWatchableObjectInt(18) < 10 ? "mob.wolf.whine" : "mob.wolf.panting") : "mob.wolf.bark");
	}

	protected String getHurtSound() {
		return "mob.wolf.hurt";
	}

	protected String getDeathSound() {
		return "mob.wolf.death";
	}

	protected float getSoundVolume() {
		return 0.4F;
	}

	protected int getDropItemId() {
		return -1;
	}

	public void onLivingUpdate() {
		super.onLivingUpdate();
		if(!this.worldObj.isRemote && this.isShaking && !this.field_25052_g && !this.hasPath() && this.onGround) {
			this.field_25052_g = true;
			this.timeWolfIsShaking = 0.0F;
			this.prevTimeWolfIsShaking = 0.0F;
			this.worldObj.setEntityState(this, (byte)8);
		}

	}

	public void onUpdate() {
		super.onUpdate();
		this.field_25054_c = this.field_25048_b;
		if(this.looksWithInterest) {
			this.field_25048_b += (1.0F - this.field_25048_b) * 0.4F;
		} else {
			this.field_25048_b += (0.0F - this.field_25048_b) * 0.4F;
		}

		if(this.looksWithInterest) {
			this.numTicksToChaseTarget = 10;
		}

		if(this.isWet()) {
			this.isShaking = true;
			this.field_25052_g = false;
			this.timeWolfIsShaking = 0.0F;
			this.prevTimeWolfIsShaking = 0.0F;
		} else if((this.isShaking || this.field_25052_g) && this.field_25052_g) {
			if(this.timeWolfIsShaking == 0.0F) {
				this.worldObj.playSoundAtEntity(this, "mob.wolf.shake", this.getSoundVolume(), (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
			}

			this.prevTimeWolfIsShaking = this.timeWolfIsShaking;
			this.timeWolfIsShaking += 0.05F;
			if(this.prevTimeWolfIsShaking >= 2.0F) {
				this.isShaking = false;
				this.field_25052_g = false;
				this.prevTimeWolfIsShaking = 0.0F;
				this.timeWolfIsShaking = 0.0F;
			}

			if(this.timeWolfIsShaking > 0.4F) {
				float f1 = (float)this.boundingBox.minY;
				int i2 = (int)(MathHelper.sin((this.timeWolfIsShaking - 0.4F) * (float)Math.PI) * 7.0F);

				for(int i3 = 0; i3 < i2; ++i3) {
					float f4 = (this.rand.nextFloat() * 2.0F - 1.0F) * this.width * 0.5F;
					float f5 = (this.rand.nextFloat() * 2.0F - 1.0F) * this.width * 0.5F;
					this.worldObj.spawnParticle("splash", this.posX + (double)f4, (double)(f1 + 0.8F), this.posZ + (double)f5, this.motionX, this.motionY, this.motionZ);
				}
			}
		}

	}

	public boolean getWolfShaking() {
		return this.isShaking;
	}

	public float getShadingWhileShaking(float f1) {
		return 0.75F + (this.prevTimeWolfIsShaking + (this.timeWolfIsShaking - this.prevTimeWolfIsShaking) * f1) / 2.0F * 0.25F;
	}

	public float getShakeAngle(float f1, float f2) {
		float f3 = (this.prevTimeWolfIsShaking + (this.timeWolfIsShaking - this.prevTimeWolfIsShaking) * f1 + f2) / 1.8F;
		if(f3 < 0.0F) {
			f3 = 0.0F;
		} else if(f3 > 1.0F) {
			f3 = 1.0F;
		}

		return MathHelper.sin(f3 * (float)Math.PI) * MathHelper.sin(f3 * (float)Math.PI * 11.0F) * 0.15F * (float)Math.PI;
	}

	public float getInterestedAngle(float f1) {
		return (this.field_25054_c + (this.field_25048_b - this.field_25054_c) * f1) * 0.15F * (float)Math.PI;
	}

	public float getEyeHeight() {
		return this.height * 0.8F;
	}

	public int getVerticalFaceSpeed() {
		return this.isSitting() ? 20 : super.getVerticalFaceSpeed();
	}

	public boolean attackEntityFrom(DamageSource damageSource1, int i2) {
		Entity entity3 = damageSource1.getEntity();
		this.aiSit.func_48407_a(false);
		if(entity3 != null && !(entity3 instanceof EntityPlayer) && !(entity3 instanceof EntityArrow)) {
			i2 = (i2 + 1) / 2;
		}

		return super.attackEntityFrom(damageSource1, i2);
	}

	public boolean attackEntityAsMob(Entity entity1) {
		int i2 = this.isTamed() ? 4 : 2;
		return entity1.attackEntityFrom(DamageSource.causeMobDamage(this), i2);
	}

	public boolean interact(EntityPlayer entityPlayer1) {
		ItemStack itemStack2 = entityPlayer1.inventory.getCurrentItem();
		if(!this.isTamed()) {
			if(itemStack2 != null && itemStack2.itemID == Item.bone.shiftedIndex && !this.isAngry()) {
				if(!entityPlayer1.capabilities.isCreativeMode) {
					--itemStack2.stackSize;
				}

				if(itemStack2.stackSize <= 0) {
					entityPlayer1.inventory.setInventorySlotContents(entityPlayer1.inventory.currentItem, (ItemStack)null);
				}

				if(!this.worldObj.isRemote) {
					if(this.rand.nextInt(3) == 0) {
						this.setTamed(true);
						this.setPathToEntity((PathEntity)null);
						this.setAttackTarget((EntityLiving)null);
						this.aiSit.func_48407_a(true);
						this.setEntityHealth(20);
						this.setOwner(entityPlayer1.username);
						this.spawnHeartOrSmoke(true);
						this.worldObj.setEntityState(this, (byte)7);
					} else {
						this.spawnHeartOrSmoke(false);
						this.worldObj.setEntityState(this, (byte)6);
					}
				}

				return true;
			}
		} else {
			if(itemStack2 != null && Item.itemsList[itemStack2.itemID] instanceof ItemFood) {
				ItemFood itemFood3 = (ItemFood)Item.itemsList[itemStack2.itemID];
				if(itemFood3.isWolfsFavoriteMeat() && this.dataWatcher.getWatchableObjectInt(18) < 20) {
					if(!entityPlayer1.capabilities.isCreativeMode) {
						--itemStack2.stackSize;
					}

					this.heal(itemFood3.getHealAmount());
					if(itemStack2.stackSize <= 0) {
						entityPlayer1.inventory.setInventorySlotContents(entityPlayer1.inventory.currentItem, (ItemStack)null);
					}

					return true;
				}
			}

			if(entityPlayer1.username.equalsIgnoreCase(this.getOwnerName()) && !this.worldObj.isRemote && !this.isWheat(itemStack2)) {
				this.aiSit.func_48407_a(!this.isSitting());
				this.isJumping = false;
				this.setPathToEntity((PathEntity)null);
			}
		}

		return super.interact(entityPlayer1);
	}

	public void handleHealthUpdate(byte b1) {
		if(b1 == 8) {
			this.field_25052_g = true;
			this.timeWolfIsShaking = 0.0F;
			this.prevTimeWolfIsShaking = 0.0F;
		} else {
			super.handleHealthUpdate(b1);
		}

	}

	public float getTailRotation() {
		return this.isAngry() ? 1.5393804F : (this.isTamed() ? (0.55F - (float)(20 - this.dataWatcher.getWatchableObjectInt(18)) * 0.02F) * (float)Math.PI : 0.62831855F);
	}

	public boolean isWheat(ItemStack itemStack1) {
		return itemStack1 == null ? false : (!(Item.itemsList[itemStack1.itemID] instanceof ItemFood) ? false : ((ItemFood)Item.itemsList[itemStack1.itemID]).isWolfsFavoriteMeat());
	}

	public int getMaxSpawnedInChunk() {
		return 8;
	}

	public boolean isAngry() {
		return (this.dataWatcher.getWatchableObjectByte(16) & 2) != 0;
	}

	public void setAngry(boolean z1) {
		byte b2 = this.dataWatcher.getWatchableObjectByte(16);
		if(z1) {
			this.dataWatcher.updateObject(16, (byte)(b2 | 2));
		} else {
			this.dataWatcher.updateObject(16, (byte)(b2 & -3));
		}

	}

	public EntityAnimal spawnBabyAnimal(EntityAnimal entityAnimal1) {
		EntityWolf entityWolf2 = new EntityWolf(this.worldObj);
		entityWolf2.setOwner(this.getOwnerName());
		entityWolf2.setTamed(true);
		return entityWolf2;
	}

	public void func_48150_h(boolean z1) {
		this.looksWithInterest = z1;
	}

	public boolean func_48135_b(EntityAnimal entityAnimal1) {
		if(entityAnimal1 == this) {
			return false;
		} else if(!this.isTamed()) {
			return false;
		} else if(!(entityAnimal1 instanceof EntityWolf)) {
			return false;
		} else {
			EntityWolf entityWolf2 = (EntityWolf)entityAnimal1;
			return !entityWolf2.isTamed() ? false : (entityWolf2.isSitting() ? false : this.isInLove() && entityWolf2.isInLove());
		}
	}
}
