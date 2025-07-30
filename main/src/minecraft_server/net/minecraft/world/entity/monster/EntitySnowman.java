package net.minecraft.world.entity.monster;

import com.mojang.nbt.NBTTagCompound;

import net.minecraft.src.MathHelper;
import net.minecraft.world.entity.DamageSource;
import net.minecraft.world.entity.ai.EntityAIArrowAttack;
import net.minecraft.world.entity.ai.EntityAILookIdle;
import net.minecraft.world.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.world.entity.ai.EntityAIWander;
import net.minecraft.world.entity.ai.EntityAIWatchClosest;
import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.World;
import net.minecraft.world.level.tile.Block;

public class EntitySnowman extends EntityGolem {
	public EntitySnowman(World world1) {
		super(world1);
		this.texture = "/mob/snowman.png";
		this.setSize(0.4F, 1.8F);
		this.getNavigator().setAvoidsWater(true);
		this.tasks.addTask(1, new EntityAIArrowAttack(this, 0.25F, 2, 20));
		this.tasks.addTask(2, new EntityAIWander(this, 0.2F));
		this.tasks.addTask(3, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
		this.tasks.addTask(4, new EntityAILookIdle(this));
		this.targetTasks.addTask(1, new EntityAINearestAttackableTarget(this, EntityMob.class, 16.0F, 0, true));
	}

	public boolean isAIEnabled() {
		return true;
	}

	public int getMaxHealth() {
		return 4;
	}

	public void onLivingUpdate() {
		super.onLivingUpdate();
		if(this.isWet()) {
			this.attackEntityFrom(DamageSource.drown, 1);
		}

		int i1 = MathHelper.floor_double(this.posX);
		int i2 = MathHelper.floor_double(this.posZ);
		if(this.worldObj.getBiomeGenForCoords(i1, i2).getFloatTemperature() > 1.0F) {
			this.attackEntityFrom(DamageSource.onFire, 1);
		}

		for(i1 = 0; i1 < 4; ++i1) {
			i2 = MathHelper.floor_double(this.posX + (double)((float)(i1 % 2 * 2 - 1) * 0.25F));
			int i3 = MathHelper.floor_double(this.posY);
			int i4 = MathHelper.floor_double(this.posZ + (double)((float)(i1 / 2 % 2 * 2 - 1) * 0.25F));
			if(this.worldObj.getBlockId(i2, i3, i4) == 0 && this.worldObj.getBiomeGenForCoords(i2, i4).getFloatTemperature() < 0.8F && Block.snow.canPlaceBlockAt(this.worldObj, i2, i3, i4)) {
				this.worldObj.setBlockWithNotify(i2, i3, i4, Block.snow.blockID);
			}
		}

	}

	public void writeEntityToNBT(NBTTagCompound compoundTag) {
		super.writeEntityToNBT(compoundTag);
	}

	public void readEntityFromNBT(NBTTagCompound compoundTag) {
		super.readEntityFromNBT(compoundTag);
	}

	protected int getDropItemId() {
		return Item.snowball.shiftedIndex;
	}

	protected void dropFewItems(boolean z1, int i2) {
		int i3 = this.rand.nextInt(16);

		for(int i4 = 0; i4 < i3; ++i4) {
			this.dropItem(Item.snowball.shiftedIndex, 1);
		}

	}
}
