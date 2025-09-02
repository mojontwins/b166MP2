package net.minecraft.world.entity.monster;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

import com.mojang.nbt.NBTTagCompound;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.animal.EntityMocAnimalTameable;
import net.minecraft.world.entity.animal.EntityWolf;
import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.level.World;

public abstract class EntityMocMob extends EntityMob implements IMoCreature {
	protected int maxHealth;
	protected int type;

	public EntityMocMob(World world) {
		super(world);
		this.setTamed(false);
		this.selectType();
	}

	public void selectType() {
		this.type = 1;
	}

	public void writeSpawnData(DataOutputStream data) throws IOException {
		data.writeInt(this.type);
	}

	public void readSpawnData(DataInputStream data) throws IOException {
		this.type = data.readInt();
		this.selectType();
	}

	protected void entityInit() {
		super.entityInit();
		this.dataWatcher.addObject(15, (byte)0);
		this.dataWatcher.addObject(16, (byte)0);
		this.dataWatcher.addObject(17, String.valueOf(""));
		this.dataWatcher.addObject(18, 0);
	}

	public boolean getDisplayName() {
		return this.getName() != null && !this.getName().equals("");
	}

	public boolean getIsAdult() {
		return this.dataWatcher.getWatchableObjectByte(15) == 1;
	}

	public boolean getIsTamed() {
		return this.dataWatcher.getWatchableObjectByte(16) == 1;
	}

	public String getName() {
		return this.dataWatcher.getWatchableObjectString(17);
	}

	public int getEdad() {
		return this.dataWatcher.getWatchableObjectInt(18);
	}

	public void setEdad(int i) {
		if(!this.worldObj.isRemote) {
			this.dataWatcher.updateObject(18, i);
		}
	}

	public void setAdult(boolean flag) {
		if(!this.worldObj.isRemote) {
			byte input = (byte)(flag ? 1 : 0);
			this.dataWatcher.updateObject(15, input);
		}
	}

	public void setName(String name) {
		if(!this.worldObj.isRemote) {
			this.dataWatcher.updateObject(17, String.valueOf(name));
		}
	}

	public void setTamed(boolean flag) {
		if(!this.worldObj.isRemote) {
			byte input = (byte)(flag ? 1 : 0);
			this.dataWatcher.updateObject(16, input);
		}
	}

	protected EntityLiving getClosestEntityLiving(Entity entity, double d) {
		double d1 = -1.0D;
		EntityLiving entityliving = null;
		List<Entity> list = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox.expand(d, d, d));

		for(int i = 0; i < list.size(); ++i) {
			Entity entity1 = (Entity)list.get(i);
			if(!this.entitiesToIgnore(entity1)) {
				double d2 = entity1.getDistanceSq(entity.posX, entity.posY, entity.posZ);
				if((d < 0.0D || d2 < d * d) && (d1 == -1.0D || d2 < d1) && ((EntityLiving)entity1).canEntityBeSeen(entity)) {
					d1 = d2;
					entityliving = (EntityLiving)entity1;
				}
			}
		}

		return entityliving;
	}

	public boolean entitiesToIgnore(Entity entity) {
		return !(entity instanceof EntityLiving) || 
				entity instanceof EntityPlayer && this.getIsTamed() || 
				this.getIsTamed() && entity instanceof EntityMocAnimalTameable && ((EntityMocAnimalTameable)entity).getIsTamed() || 
				entity instanceof EntityWolf;
	}

	public int getMaxHealth() {
		return 20;
	}

	public boolean checkSpawningBiome() {
		return true;
	}

	public void writeEntityToNBT(NBTTagCompound nbttagcompound) {
		super.writeEntityToNBT(nbttagcompound);
		nbttagcompound.setBoolean("Tamed", this.getIsTamed());
		nbttagcompound.setBoolean("Adult", this.getIsAdult());
		nbttagcompound.setInteger("Edad", this.getEdad());
		nbttagcompound.setString("Name", this.getName());
		nbttagcompound.setInteger("TypeInt", this.type);
	}

	public void readEntityFromNBT(NBTTagCompound nbttagcompound) {
		super.readEntityFromNBT(nbttagcompound);
		this.setTamed(nbttagcompound.getBoolean("Tamed"));
		this.setAdult(nbttagcompound.getBoolean("Adult"));
		this.setEdad(nbttagcompound.getInteger("Edad"));
		this.setName(nbttagcompound.getString("Name"));
		this.type = nbttagcompound.getInteger("TypeInt");
		this.selectType();
	}
}
