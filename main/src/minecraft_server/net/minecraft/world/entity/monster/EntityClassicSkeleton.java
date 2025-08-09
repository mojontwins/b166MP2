package net.minecraft.world.entity.monster;

import com.mojang.nbt.NBTTagCompound;

import net.minecraft.src.MathHelper;
import net.minecraft.world.GameRules;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.EntityArrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.World;

public class EntityClassicSkeleton extends EntityArmoredMob {
	public EntityClassicSkeleton(World world1) {
		super(world1);
		this.texture = "/mob/skeleton.png";
		if(GameRules.boolRule("skeletonsWithBows")) this.inventory.setInventorySlotContents(0, new ItemStack(Item.bow));
		this.health = this.getMaxHealth();
	}

	protected String getLivingSound() {
		return "mob.skeleton";
	}

	protected String getHurtSound() {
		return "mob.skeletonhurt";
	}

	protected String getDeathSound() {
		return "mob.skeletonhurt";
	}

	public void onLivingUpdate() {
		if(this.worldObj.isDaytime() && !this.worldObj.isRemote) {
			float f1 = this.getBrightness(1.0F);
			if(f1 > 0.5F && this.worldObj.canBlockSeeTheSky(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ)) && this.rand.nextFloat() * 30.0F < (f1 - 0.4F) * 2.0F) {
				this.setFire(8);
			}
		}

		super.onLivingUpdate();
	}

	protected void attackEntity(Entity entity, float f2) {
		if(f2 < 10.0F) {
			double dx = entity.posX - this.posX;
			double dz = entity.posZ - this.posZ;
			
			if(this.attackTime == 0) {
				EntityArrow arrow = new EntityArrow(this.worldObj, this, 1.0F);
				++arrow.posY;
				
				double dy = entity.posY + (double)entity.getEyeHeight() - (double)0.2F - arrow.posY;
				float yAdjust = MathHelper.sqrt_double(dx * dx + dz * dz) * 0.2F;
				
				this.worldObj.playSoundAtEntity(this, "random.bow", 1.0F, 1.0F / (this.rand.nextFloat() * 0.4F + 0.8F));
				this.worldObj.spawnEntityInWorld(arrow);
				
				arrow.setArrowHeading(dx, dy + (double)yAdjust, dz, 0.6F, 12.0F);
				this.attackTime = 30;
			}

			this.rotationYaw = (float)(Math.atan2(dz, dx) * 180.0D / (double)(float)Math.PI) - 90.0F;
			this.hasAttacked = true;
		}

	}

	public void writeEntityToNBT(NBTTagCompound compoundTag) {
		super.writeEntityToNBT(compoundTag);
	}

	public void readEntityFromNBT(NBTTagCompound compoundTag) {
		super.readEntityFromNBT(compoundTag);
	}

	protected int getDropItemId() {
		return Item.arrow.shiftedIndex;
	}

	protected void dropFewItems() {
		int i1 = this.rand.nextInt(3);

		int i2;
		for(i2 = 0; i2 < i1; ++i2) {
			this.dropItem(Item.arrow.shiftedIndex, 1);
		}

		i1 = this.rand.nextInt(3);

		for(i2 = 0; i2 < i1; ++i2) {
			this.dropItem(Item.bone.shiftedIndex, 1);
		}

	}

	public ItemStack getHeldItem() {
		return this.inventory.getHeldItem();
	}
	
	public boolean burnsInWinter() {
		return true;
	}
	
	@Override
	public int getMaxHealth() {
		return 20;
	}
}
