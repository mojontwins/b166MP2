package net.minecraft.src;

public class EntityPolarBear extends EntityBear {
	public EntityPolarBear(World world1) {
		super(world1);
		this.texture = "/mob/polarbear.png";
		this.attackRange = 1.0D;
		this.health = 30;
	}

	protected Entity findPlayerToAttack() {
		if(this.worldObj.difficultySetting > 0) {
			EntityPlayer entityPlayer1 = this.worldObj.getClosestPlayerToEntity(this, this.attackRange);
			if(entityPlayer1 != null && this.worldObj.difficultySetting > 0) {
				return entityPlayer1;
			}

			if(this.rand.nextInt(20) == 0) {
				EntityLiving entityLiving2 = this.getClosestTarget(this, 8.0D);
				return entityLiving2;
			}
		}

		return null;
	}

	public void onLivingUpdate() {
		if(this.worldObj.difficultySetting == 1) {
			this.attackRange = 5.0D;
			this.force = 3;
		} else if(this.worldObj.difficultySetting > 1) {
			this.attackRange = 8.0D;
			this.force = 5;
		}

		super.onLivingUpdate();
	}

	public int getMaxSpawnedInChunk() {
		return 2;
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
		int i1 = MathHelper.floor_double(this.posY);
		int i2 = MathHelper.floor_double(this.boundingBox.minY);
		int i3 = MathHelper.floor_double(this.motionX);
		return (this.worldObj.getBlockId(i1, i2 - 1, i3) == Block.snow.blockID || this.worldObj.getBlockId(i1, i2, i3) == Block.snow.blockID || this.worldObj.getBlockId(i1, i2 - 1, i3) == Block.blockSnow.blockID || this.worldObj.getBlockId(i1, i2, i3) == Block.blockSnow.blockID) && super.getCanSpawnHere();
	}
}
