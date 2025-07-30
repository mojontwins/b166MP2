package net.minecraft.world.entity.animal;

import java.util.Random;

import com.mojang.nbt.NBTTagCompound;

import net.minecraft.src.MathHelper;
import net.minecraft.world.GameRules;
import net.minecraft.world.entity.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.IDyeableEntity;
import net.minecraft.world.entity.ai.EntityAIEatGrass;
import net.minecraft.world.entity.ai.EntityAIFollowParent;
import net.minecraft.world.entity.ai.EntityAILookIdle;
import net.minecraft.world.entity.ai.EntityAIMate;
import net.minecraft.world.entity.ai.EntityAIPanic;
import net.minecraft.world.entity.ai.EntityAISwimming;
import net.minecraft.world.entity.ai.EntityAITempt;
import net.minecraft.world.entity.ai.EntityAIWander;
import net.minecraft.world.entity.ai.EntityAIWatchClosest;
import net.minecraft.world.entity.item.EntityItem;
import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.World;
import net.minecraft.world.level.tile.Block;

public class EntitySheep extends EntityAnimal implements IDyeableEntity {
	// Note how fleece color are in reverse order, when compared to dyes in ItemDye,
	// but correspond to wool metadata. I guess this was made this way because sheep
	// dropped colored wool before having the ability to be dyed, and wool metadata
	// was already reversed for reasons unknown.
	
	public static final float[][] fleeceColorTable = new float[][]{	
		{1.0F, 1.0F, 1.0F}, {0.95F, 0.7F, 0.2F}, {0.9F, 0.5F, 0.85F}, {0.6F, 0.7F, 0.95F}, {0.9F, 0.9F, 0.2F}, {0.5F, 0.8F, 0.1F}, {0.95F, 0.7F, 0.8F}, {0.3F, 0.3F, 0.3F}, 
		{0.6F, 0.6F, 0.6F}, {0.3F, 0.6F, 0.7F}, {0.7F, 0.4F, 0.9F}, {0.2F, 0.4F, 0.8F}, {0.5F, 0.4F, 0.3F}, {0.4F, 0.5F, 0.2F}, {0.8F, 0.3F, 0.3F}, {0.1F, 0.1F, 0.1F}
	};

	private int sheepTimer;
	private EntityAIEatGrass aiEatGrass = new EntityAIEatGrass(this);

	public EntitySheep(World world1) {
		super(world1);
		this.texture = "/mob/sheep.png";
		this.setSize(0.9F, 1.3F);
		float f2 = 0.23F;
		this.getNavigator().setAvoidsWater(true);
		this.tasks.addTask(0, new EntityAISwimming(this));
		this.tasks.addTask(1, new EntityAIPanic(this, 0.38F));
		if (GameRules.boolRule("canBreedAnimals")) {
			this.tasks.addTask(2, new EntityAIMate(this, f2));
		}
		this.tasks.addTask(3, new EntityAITempt(this, 0.25F, Item.wheat.shiftedIndex, false));
		this.tasks.addTask(4, new EntityAIFollowParent(this, 0.25F));
		this.tasks.addTask(5, this.aiEatGrass);
		this.tasks.addTask(6, new EntityAIWander(this, f2));
		this.tasks.addTask(7, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
		this.tasks.addTask(8, new EntityAILookIdle(this));
	}

	@Override
	public boolean attackEntityFrom(DamageSource damageSource, int i2) {
		// Reinstated for b1.6.6 - punch sheep to get cloth. {
		Entity entity1 = damageSource.getEntity();
		if(entity1 != null && !this.worldObj.isRemote && !this.getSheared() && entity1 instanceof EntityLiving) {
			this.setSheared(true);
			int i3 = 1 + this.rand.nextInt(3);

			for(int i4 = 0; i4 < i3; ++i4) {
				EntityItem entityItem5 = this.entityDropItem(new ItemStack(Block.cloth.blockID, 1, this.getFleeceColor()), 1.0F);
				entityItem5.motionY += (double)(this.rand.nextFloat() * 0.05F);
				entityItem5.motionX += (double)((this.rand.nextFloat() - this.rand.nextFloat()) * 0.1F);
				entityItem5.motionZ += (double)((this.rand.nextFloat() - this.rand.nextFloat()) * 0.1F);
			}
		}
		// }
		
		return super.attackEntityFrom(damageSource, i2);
	}

	
	protected boolean isAIEnabled() {
		return true;
	}

	protected void updateAITasks() {
		this.sheepTimer = this.aiEatGrass.getTimerValue();
		super.updateAITasks();
	}

	public void onLivingUpdate() {
		if(this.worldObj.isRemote) {
			this.sheepTimer = Math.max(0, this.sheepTimer - 1);
		}

		super.onLivingUpdate();
	}

	public int getMaxHealth() {
		return 8;
	}

	protected void entityInit() {
		super.entityInit();
		this.dataWatcher.addObject(16, new Byte((byte)0));
	}

	protected void dropFewItems(boolean z1, int i2) {
		if(!this.getSheared()) {
			this.entityDropItem(new ItemStack(Block.cloth.blockID, 1, this.getFleeceColor()), 0.0F);
		}

	}

	protected int getDropItemId() {
		return Block.cloth.blockID;
	}

	public void handleHealthUpdate(byte b1) {
		if(b1 == 10) {
			this.sheepTimer = 40;
		} else {
			super.handleHealthUpdate(b1);
		}

	}

	public float func_44003_c(float f1) {
		return this.sheepTimer <= 0 ? 0.0F : (this.sheepTimer >= 4 && this.sheepTimer <= 36 ? 1.0F : (this.sheepTimer < 4 ? ((float)this.sheepTimer - f1) / 4.0F : -((float)(this.sheepTimer - 40) - f1) / 4.0F));
	}

	public float func_44002_d(float f1) {
		if(this.sheepTimer > 4 && this.sheepTimer <= 36) {
			float f2 = ((float)(this.sheepTimer - 4) - f1) / 32.0F;
			return 0.62831855F + 0.21991149F * MathHelper.sin(f2 * 28.7F);
		} else {
			return this.sheepTimer > 0 ? 0.62831855F : this.rotationPitch / 57.295776F;
		}
	}

	public boolean interact(EntityPlayer entityPlayer1) {
		ItemStack itemStack2 = entityPlayer1.inventory.getCurrentItem();
		if(itemStack2 != null && itemStack2.itemID == Item.shears.shiftedIndex && !this.getSheared() && !this.isChild()) {
			if(!this.worldObj.isRemote) {
				this.setSheared(true);
				int i3 = 1 + this.rand.nextInt(3);

				for(int i4 = 0; i4 < i3; ++i4) {
					EntityItem entityItem5 = this.entityDropItem(new ItemStack(Block.cloth.blockID, 1, this.getFleeceColor()), 1.0F);
					entityItem5.motionY += (double)(this.rand.nextFloat() * 0.05F);
					entityItem5.motionX += (double)((this.rand.nextFloat() - this.rand.nextFloat()) * 0.1F);
					entityItem5.motionZ += (double)((this.rand.nextFloat() - this.rand.nextFloat()) * 0.1F);
				}
			}

			itemStack2.damageItem(1, entityPlayer1);
		}

		return super.interact(entityPlayer1);
	}

	public void writeEntityToNBT(NBTTagCompound compoundTag) {
		super.writeEntityToNBT(compoundTag);
		compoundTag.setBoolean("Sheared", this.getSheared());
		compoundTag.setByte("Color", (byte)this.getFleeceColor());
	}

	public void readEntityFromNBT(NBTTagCompound compoundTag) {
		super.readEntityFromNBT(compoundTag);
		this.setSheared(compoundTag.getBoolean("Sheared"));
		this.setFleeceColor(compoundTag.getByte("Color"));
	}

	protected String getLivingSound() {
		return "mob.sheep";
	}

	protected String getHurtSound() {
		return "mob.sheep";
	}

	protected String getDeathSound() {
		return "mob.sheep";
	}

	public int getFleeceColor() {
		return this.dataWatcher.getWatchableObjectByte(16) & 15;
	}

	public void setFleeceColor(int i1) {
		byte b2 = this.dataWatcher.getWatchableObjectByte(16);
		this.dataWatcher.updateObject(16, (byte)(b2 & 240 | i1 & 15));
	}

	public boolean getSheared() {
		return (this.dataWatcher.getWatchableObjectByte(16) & 16) != 0;
	}

	public void setSheared(boolean z1) {
		byte b2 = this.dataWatcher.getWatchableObjectByte(16);
		if(z1) {
			this.dataWatcher.updateObject(16, (byte)(b2 | 16));
		} else {
			this.dataWatcher.updateObject(16, (byte)(b2 & -17));
		}

	}

	public static int getRandomFleeceColor(Random random0) {
		int i1 = random0.nextInt(100);
		return i1 < 5 ? 15 : (i1 < 10 ? 7 : (i1 < 15 ? 8 : (i1 < 18 ? 12 : (random0.nextInt(500) == 0 ? 6 : 0))));
	}

	public EntityAnimal spawnBabyAnimal(EntityAnimal entityAnimal1) {
		EntitySheep entitySheep2 = (EntitySheep)entityAnimal1;
		EntitySheep entitySheep3 = new EntitySheep(this.worldObj);
		if(this.rand.nextBoolean()) {
			entitySheep3.setFleeceColor(this.getFleeceColor());
		} else {
			entitySheep3.setFleeceColor(entitySheep2.getFleeceColor());
		}

		return entitySheep3;
	}

	public void eatGrassBonus() {
		this.setSheared(false);
		if(this.isChild()) {
			int i1 = this.getGrowingAge() + 1200;
			if(i1 > 0) {
				i1 = 0;
			}

			this.setGrowingAge(i1);
		}

	}

	@Override
	public boolean admitsDyeing() {
		return !this.getSheared();
	}

	@Override
	public int getDyeColor() {
		return this.getDyeColor();
	}

	@Override
	public void setDyeColor(int dyeColor) {
		this.setDyeColor(dyeColor);
	}
}
