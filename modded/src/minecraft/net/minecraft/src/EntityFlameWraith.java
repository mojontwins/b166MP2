package net.minecraft.src;

public class EntityFlameWraith extends EntityWraith implements IMob {
	protected int burningTime;

	public EntityFlameWraith(World world1) {
		super(world1);
		this.texture = "/mob/flamewraith.png";
		this.setSize(1.5F, 1.5F);
		this.isImmuneToFire = true;
		this.burningTime = 30;
		this.health = 15;
		this.moveSpeed = 1.1F;
	}

	protected int getDropItemId() {
		return Item.redstone.shiftedIndex;
	}

	public void onLivingUpdate() {
		if(this.rand.nextInt(40) == 0) {
			this.fire = 2;
		}

		if(this.worldObj.isDaytime()) {
			float f1 = this.getEntityBrightness(1.0F);
			if(f1 > 0.5F && this.worldObj.canBlockSeeTheSky(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ)) && this.rand.nextFloat() * 30.0F < (f1 - 0.4F) * 2.0F) {
				this.health -= 2;
			}
		}

		super.onLivingUpdate();
	}

	protected void attackEntity(Entity entity1, float f2) {
		if((double)f2 < 2.5D && entity1.boundingBox.maxY > this.boundingBox.minY && entity1.boundingBox.minY < this.boundingBox.maxY) {
			this.attackTime = 20;
			entity1.attackEntityFrom(this, 2);
			entity1.fire = this.burningTime;
		}

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
		return ((Integer)mod_mocreatures.fwraithfreq.get()).intValue() > 0 && this.worldObj.difficultySetting >= ((Integer)mod_mocreatures.fwraithSpawnDifficulty.get()).intValue() + 1 && super.d2();
	}
}
