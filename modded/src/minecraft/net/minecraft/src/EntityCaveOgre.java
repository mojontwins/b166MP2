package net.minecraft.src;

public class EntityCaveOgre extends EntityOgre implements IMob {
	public EntityCaveOgre(World world1) {
		super(world1);
		this.attackStrength = 3;
		this.attackRange = 16.0D;
		this.ogreboolean = false;
		this.texture = "/mob/caveogre.png";
		this.setSize(1.5F, 4.0F);
		this.health = 50;
		this.bogrefire = false;
		this.destroyForce = ((Float)mod_mocreatures.cogreStrength.get()).floatValue();
		this.isImmuneToFire = false;
		this.frequencyA = 35;
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

	public boolean maxNumberReached() {
		int i1 = 0;

		for(int i2 = 0; i2 < this.worldObj.loadedEntityList.size(); ++i2) {
			Entity entity3 = (Entity)this.worldObj.loadedEntityList.get(i2);
			if(entity3 instanceof EntityCaveOgre) {
				++i1;
			}
		}

		return false;
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
		return ((Integer)mod_mocreatures.cogrefreq.get()).intValue() > 0 && this.worldObj.difficultySetting >= ((Integer)mod_mocreatures.cogreSpawnDifficulty.get()).intValue() + 1 && !this.worldObj.canBlockSeeTheSky(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ)) && this.posY < 50.0D && super.d2();
	}

	protected int getDropItemId() {
		return Item.diamond.shiftedIndex;
	}
}
