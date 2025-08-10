package net.minecraft.src;

public class EntityDuck extends EntityChicken {
	public EntityDuck(World world1) {
		super(world1);
		this.texture = "/mob/duck.png";
		this.setSize(0.3F, 0.4F);
		this.health = 4;
		this.timeUntilNextEgg = this.rand.nextInt(6000) + 6000;
	}

	protected String getLivingSound() {
		return "duck";
	}

	protected String getHurtSound() {
		return "duckhurt";
	}

	protected String getDeathSound() {
		return "duckhurt";
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
		return ((Integer)mod_mocreatures.duckfreq.get()).intValue() > 0 && super.getCanSpawnHere();
	}
}
