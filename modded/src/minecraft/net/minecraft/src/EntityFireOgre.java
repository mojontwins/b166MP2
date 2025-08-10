package net.minecraft.src;

public class EntityFireOgre extends EntityOgre implements IMob {
	public EntityFireOgre(World world1) {
		super(world1);
		this.attackStrength = 3;
		this.attackRange = 16.0D;
		this.ogreboolean = false;
		this.texture = "/mob/fireogre.png";
		this.setSize(1.5F, 4.0F);
		this.health = 35;
		this.bogrefire = true;
		this.destroyForce = ((Float)mod_mocreatures.fogreStrength.get()).floatValue();
		this.isImmuneToFire = true;
		this.frequencyA = 35;
	}

	protected int getDropItemId() {
		return BlockFire.fire.blockID;
	}

	public void onLivingUpdate() {
		this.findPlayerToAttack();
		if(this.ogrehasenemy && this.rand.nextInt(this.frequencyA) == 0) {
			this.ogreattack = true;
			this.attackTime = 15;
		}

		if(this.worldObj.isDaytime()) {
			float f1 = this.getEntityBrightness(1.0F);
			if(f1 > 0.5F && this.worldObj.canBlockSeeTheSky(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ)) && this.rand.nextFloat() * 30.0F < (f1 - 0.4F) * 2.0F) {
				this.health -= 5;
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
		return ((Integer)mod_mocreatures.fogrefreq.get()).intValue() > 0 && this.worldObj.difficultySetting >= ((Integer)mod_mocreatures.fogreSpawnDifficulty.get()).intValue() + 1 && super.d2();
	}

	public int getMaxSpawnedInChunk() {
		return 2;
	}
}
