package net.minecraft.world.entity.animal;

import com.mojang.nbt.NBTTagCompound;

import net.minecraft.world.entity.DataWatchers;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ai.EntityAISit;
import net.minecraft.world.level.World;

public abstract class EntityTameable extends EntityAnimal {
	protected EntityAISit aiSit = new EntityAISit(this);

	public EntityTameable(World world1) {
		super(world1);
	}

	protected void entityInit() {
		super.entityInit();
		this.dataWatcher.addObject(DataWatchers.DW_STATUS, (byte)0);
		this.dataWatcher.addObject(DataWatchers.DW_OWNER, "");
	}

	public void writeEntityToNBT(NBTTagCompound compoundTag) {
		super.writeEntityToNBT(compoundTag);
		if(this.getOwnerName() == null) {
			compoundTag.setString("Owner", "");
		} else {
			compoundTag.setString("Owner", this.getOwnerName());
		}

		compoundTag.setBoolean("Sitting", this.isSitting());
	}

	public void readEntityFromNBT(NBTTagCompound compoundTag) {
		super.readEntityFromNBT(compoundTag);
		String string2 = compoundTag.getString("Owner");
		if(string2.length() > 0) {
			this.setOwner(string2);
			this.setTamed(true);
		}

		this.aiSit.func_48407_a(compoundTag.getBoolean("Sitting"));
	}

	protected void spawnHeartOrSmoke(boolean z1) {
		String string2 = "heart";
		if(!z1) {
			string2 = "smoke";
		}

		for(int i3 = 0; i3 < 7; ++i3) {
			double d4 = this.rand.nextGaussian() * 0.02D;
			double d6 = this.rand.nextGaussian() * 0.02D;
			double d8 = this.rand.nextGaussian() * 0.02D;
			this.worldObj.spawnParticle(string2, this.posX + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, this.posY + 0.5D + (double)(this.rand.nextFloat() * this.height), this.posZ + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, d4, d6, d8);
		}

	}

	public void handleHealthUpdate(byte b1) {
		if(b1 == 7) {
			this.spawnHeartOrSmoke(true);
		} else if(b1 == 6) {
			this.spawnHeartOrSmoke(false);
		} else {
			super.handleHealthUpdate(b1);
		}

	}

	public boolean isTamed() {
		return (this.dataWatcher.getWatchableObjectByte(DataWatchers.DW_STATUS) & 4) != 0;
	}

	public void setTamed(boolean z1) {
		byte b2 = this.dataWatcher.getWatchableObjectByte(DataWatchers.DW_STATUS);
		if(z1) {
			this.dataWatcher.updateObject(DataWatchers.DW_STATUS, (byte)(b2 | 4));
		} else {
			this.dataWatcher.updateObject(DataWatchers.DW_STATUS, (byte)(b2 & -5));
		}

	}

	public boolean isSitting() {
		return (this.dataWatcher.getWatchableObjectByte(DataWatchers.DW_STATUS) & 1) != 0;
	}

	public void setSitting(boolean z1) {
		byte b2 = this.dataWatcher.getWatchableObjectByte(DataWatchers.DW_STATUS);
		if(z1) {
			this.dataWatcher.updateObject(DataWatchers.DW_STATUS, (byte)(b2 | 1));
		} else {
			this.dataWatcher.updateObject(DataWatchers.DW_STATUS, (byte)(b2 & -2));
		}

	}

	public String getOwnerName() {
		return this.dataWatcher.getWatchableObjectString(DataWatchers.DW_OWNER);
	}

	public void setOwner(String string1) {
		this.dataWatcher.updateObject(DataWatchers.DW_OWNER, string1);
	}

	public EntityLiving getOwner() {
		return this.worldObj.getPlayerEntityByName(this.getOwnerName());
	}

	public EntityAISit getAiSit() {
		return this.aiSit;
	}
}
