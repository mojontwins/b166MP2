package net.minecraft.world.entity.animal;

import java.util.List;

import com.mojang.nbt.NBTTagCompound;

import net.minecraft.src.MathHelper;
import net.minecraft.world.entity.DataWatchers;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.monster.EntityMob;
import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemFood;
import net.minecraft.world.item.ItemSeeds;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.World;
import net.minecraft.world.level.pathfinder.PathEntity;

public abstract class EntityMoCAnimal extends EntityAnimal {
	protected int temper;
	protected boolean isEntityJumping;
	public EntityLiving roper;

	public EntityMoCAnimal(World world) {
		super(world);
		this.setEdad(0);
		this.setIsAdult(false);
	}

	@Override
	protected void entityInit() {
		super.entityInit();

		this.dataWatcher.addObject(DataWatchers.DW_EDAD, Integer.valueOf(0)); // int ageTicks / "edad"
		this.dataWatcher.addObject(DataWatchers.DW_TYPE, Integer.valueOf(0)); // int type
		this.dataWatcher.addObject(DataWatchers.DW_ISADULT, Byte.valueOf((byte) 0)); // isAdult - 0 false 1 true
	}

	public boolean getIsAdult() {
		return (this.dataWatcher.getWatchableObjectByte(DataWatchers.DW_ISADULT) == 1);
	}

	public void setIsAdult(boolean flag) {
		byte input = (byte) (flag ? 1 : 0);
		this.dataWatcher.updateObject(DataWatchers.DW_ISADULT, Byte.valueOf(input));
	}

	public int getEdad() {
		return this.dataWatcher.getWatchableObjectInt(DataWatchers.DW_EDAD);
	}

	public void setEdad(int i) {
		this.dataWatcher.updateObject(DataWatchers.DW_EDAD, Integer.valueOf(i));
	}

	public int getType() {
		return this.dataWatcher.getWatchableObjectInt(DataWatchers.DW_TYPE);
	}

	public void setType(int i) {
		this.dataWatcher.updateObject(DataWatchers.DW_TYPE, Integer.valueOf(i));
	}

	public boolean getIsJumping() {
		return this.isEntityJumping;
	}

	public int getTemper() {
		return this.temper;
	}

	public void setTemper(int temper) {
		this.temper = temper;
	}

	public EntityLiving getRoper() {
		return this.roper;
	}

	public void setRoper(EntityLiving roper) {
		this.roper = roper;
	}

	public boolean isItemEdible(Item item1) {
		return (item1 instanceof ItemFood) || (item1 instanceof ItemSeeds) || item1 == Item.wheat || item1 == Item.sugar
				|| item1 == Item.cake || item1 == Item.egg;
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbttagcompound) {
		super.writeEntityToNBT(nbttagcompound);
		nbttagcompound.setBoolean("Adult", this.getIsAdult());
		nbttagcompound.setInteger("Edad", this.getEdad());
		nbttagcompound.setInteger("Type", this.getType());
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbttagcompound) {
		super.readEntityFromNBT(nbttagcompound);
		this.setIsAdult(nbttagcompound.getBoolean("Adult"));
		this.setEdad(nbttagcompound.getInteger("Edad"));
		this.setType(nbttagcompound.getInteger("Type"));
	}

	public void faceLocation(int i, int j, int k, float f) {
		double var4 = i + 0.5D - this.posX;
		double var8 = k + 0.5D - this.posZ;
		double var6 = j + 0.5D - this.posY;
		double var14 = MathHelper.sqrt_double(var4 * var4 + var8 * var8);
		float var12 = (float) (Math.atan2(var8, var4) * 180.0D / Math.PI) - 90.0F;
		float var13 = (float) (-(Math.atan2(var6, var14) * 180.0D / Math.PI));
		this.rotationPitch = -this.updateRotation(this.rotationPitch, var13, f);
		this.rotationYaw = this.updateRotation(this.rotationYaw, var12, f);
	}

	private float updateRotation(float par1, float par2, float par3) {
		float var4;

		for (var4 = par2 - par1; var4 < -180.0F; var4 += 360.0F) {
			;
		}

		while (var4 >= 180.0F) {
			var4 -= 360.0F;
		}

		if (var4 > par3) {
			var4 = par3;
		}

		if (var4 < -par3) {
			var4 = -par3;
		}

		return par1 + var4;
	}

	public void getMyOwnPath(Entity entity, float f) {
		PathEntity pathentity = this.worldObj.getPathEntityToEntity(this, entity, 16F, true, false, false, true);
		if (pathentity != null) {
			this.setPathToEntity(pathentity);
		}
	}

	protected EntityLiving getClosestEntityLiving(Entity entity, double d) {
		double d1 = -1D;
		EntityLiving entityliving = null;
		List<Entity> list = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox.expand(d, d, d));
		for (int i = 0; i < list.size(); i++) {
			Entity entity1 = list.get(i);

			if (this.entitiesToIgnore(entity1)) {
				continue;
			}

			double d2 = entity1.getDistanceSq(entity.posX, entity.posY, entity.posZ);
			if (((d < 0.0D) || (d2 < (d * d))) && ((d1 == -1D) || (d2 < d1))
					&& ((EntityLiving) entity1).canEntityBeSeen(entity)) {
				d1 = d2;
				entityliving = (EntityLiving) entity1;
			}
		}

		return entityliving;
	}

	public boolean entitiesToIgnore(Entity entity) {
		return ((!(entity instanceof EntityLiving)) || (entity instanceof EntityMob) || (entity instanceof EntityPlayer)
				|| (entity instanceof EntityWolf) || (entity.width >= this.width || entity.height >= this.height));
	}

	public boolean isMyFavoriteFood(ItemStack par1ItemStack) {
		return false;
	}

	public boolean isNotScared() {
		return false;
	}

}
