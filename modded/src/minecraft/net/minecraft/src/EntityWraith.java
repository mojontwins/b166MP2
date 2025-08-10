package net.minecraft.src;

public class EntityWraith extends EntityFlyerMob implements IMob {
	public EntityWraith(World world1) {
		super(world1);
		this.texture = "/mob/wraith.png";
		this.setSize(1.5F, 1.5F);
		this.isImmuneToFire = false;
		this.c = 3;
		this.health = 10;
		this.moveSpeed = 1.3F;
	}

	protected String getLivingSound() {
		return "wraith";
	}

	protected String getHurtSound() {
		return "wraithhurt";
	}

	protected String getDeathSound() {
		return "wraithdying";
	}

	protected int getDropItemId() {
		return Item.gunpowder.shiftedIndex;
	}

	public void onLivingUpdate() {
		if(this.worldObj.difficultySetting == 1) {
			this.c = 2;
		} else if(this.worldObj.difficultySetting > 1) {
			this.c = 3;
		}

		if(this.worldObj.isDaytime()) {
			float f1 = this.getEntityBrightness(1.0F);
			if(f1 > 0.5F && this.worldObj.canBlockSeeTheSky(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ)) && this.rand.nextFloat() * 30.0F < (f1 - 0.4F) * 2.0F) {
				this.fire = 300;
			}
		}

		super.onLivingUpdate();
	}

	public void setEntityDead() {
		super.setEntityDead();
	}

	public void writeEntityToNBT(NBTTagCompound nBTTagCompound1) {
		super.writeEntityToNBT(nBTTagCompound1);
	}

	public void readEntityFromNBT(NBTTagCompound nBTTagCompound1) {
		super.readEntityFromNBT(nBTTagCompound1);
	}

	public boolean getCanSpawnHere() {
		return ((Integer)mod_mocreatures.wraithfreq.get()).intValue() > 0 && this.worldObj.difficultySetting >= ((Integer)mod_mocreatures.wraithSpawnDifficulty.get()).intValue() + 1 && super.getCanSpawnHere();
	}

	public boolean d2() {
		return super.getCanSpawnHere();
	}
}
