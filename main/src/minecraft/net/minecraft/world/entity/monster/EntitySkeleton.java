package net.minecraft.world.entity.monster;

import net.minecraft.src.MathHelper;
import net.minecraft.world.entity.DamageSource;
import net.minecraft.world.entity.EnumCreatureAttribute;
import net.minecraft.world.entity.ai.EntityAIArrowAttack;
import net.minecraft.world.entity.ai.EntityAIFleeSun;
import net.minecraft.world.entity.ai.EntityAIHurtByTarget;
import net.minecraft.world.entity.ai.EntityAILookIdle;
import net.minecraft.world.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.world.entity.ai.EntityAIRestrictSun;
import net.minecraft.world.entity.ai.EntityAISwimming;
import net.minecraft.world.entity.ai.EntityAIWander;
import net.minecraft.world.entity.ai.EntityAIWatchClosest;
import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.World;

public class EntitySkeleton extends EntityArmoredMob implements IMob {
	private static final ItemStack defaultHeldItem = new ItemStack(Item.bow, 1);

	public EntitySkeleton(World world1) {
		super(world1);
		this.texture = "/mob/skeleton.png";
		this.moveSpeed = 0.25F;
		this.tasks.addTask(1, new EntityAISwimming(this));
		this.tasks.addTask(2, new EntityAIRestrictSun(this));
		this.tasks.addTask(3, new EntityAIFleeSun(this, this.moveSpeed));
		this.tasks.addTask(4, new EntityAIArrowAttack(this, this.moveSpeed, 1, 60));
		this.tasks.addTask(5, new EntityAIWander(this, this.moveSpeed));
		this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
		this.tasks.addTask(6, new EntityAILookIdle(this));
		this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
		this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 16.0F, 0, true));
	}

	public boolean isAIEnabled() {
		return true;
	}

	public int getMaxHealth() {
		return 20;
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

	public ItemStack getHeldItem() {
		return defaultHeldItem;
	}

	public EnumCreatureAttribute getCreatureAttribute() {
		return EnumCreatureAttribute.UNDEAD;
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

	public void onDeath(DamageSource damageSource1) {
		super.onDeath(damageSource1);

	}

	protected int getDropItemId() {
		return Item.arrow.shiftedIndex;
	}

	protected void dropFewItems(boolean z1, int i2) {
		int i3 = this.rand.nextInt(3 + i2);

		int i4;
		for(i4 = 0; i4 < i3; ++i4) {
			this.dropItem(Item.arrow.shiftedIndex, 1);
		}

		i3 = this.rand.nextInt(3 + i2);

		for(i4 = 0; i4 < i3; ++i4) {
			this.dropItem(Item.bone.shiftedIndex, 1);
		}

	}

	protected void dropRareDrop(int i1) {
		if(i1 > 0) {
			ItemStack itemStack2 = new ItemStack(Item.bow);
			//EnchantmentHelper.addRandomEnchantment(this.rand, itemStack2, 5);
			this.entityDropItem(itemStack2, 0.0F);
		} else {
			this.dropItem(Item.bow.shiftedIndex, 1);
		}

	}
}
