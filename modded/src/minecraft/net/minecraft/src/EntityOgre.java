package net.minecraft.src;

public class EntityOgre extends EntityMob implements IMob {
	public int frequencyA;
	public float destroyForce;
	public boolean ogrehasenemy;
	public boolean ogreattack;
	public boolean bogrefire;
	public boolean ogreboolean;
	protected double attackRange;

	public EntityOgre(World world1) {
		super(world1);
		this.attackStrength = 3;
		this.attackRange = 16.0D;
		this.ogreboolean = false;
		this.texture = "/mob/ogre.png";
		this.setSize(1.5F, 4.0F);
		this.health = 35;
		this.bogrefire = false;
		this.ogreattack = false;
		this.ogrehasenemy = false;
		this.destroyForce = ((Float)mod_mocreatures.ogreStrength.get()).floatValue();
		this.isImmuneToFire = false;
		this.frequencyA = 30;
	}

	public void writeEntityToNBT(NBTTagCompound nBTTagCompound1) {
		super.writeEntityToNBT(nBTTagCompound1);
		nBTTagCompound1.setBoolean("OgreBoolean", this.ogreboolean);
		nBTTagCompound1.setBoolean("OgreAttack", this.ogreattack);
	}

	public void readEntityFromNBT(NBTTagCompound nBTTagCompound1) {
		super.readEntityFromNBT(nBTTagCompound1);
		this.ogreboolean = nBTTagCompound1.getBoolean("OgreBoolean");
		this.ogreattack = nBTTagCompound1.getBoolean("OgreAttack");
	}

	protected String getLivingSound() {
		return "ogre";
	}

	protected String getHurtSound() {
		return "ogrehurt";
	}

	protected String getDeathSound() {
		return "ogredying";
	}

	protected int getDropItemId() {
		return Block.obsidian.blockID;
	}

	protected Entity findPlayerToAttack() {
		float f1 = this.getEntityBrightness(1.0F);
		if(f1 < 0.5F) {
			EntityPlayer entityPlayer2 = this.worldObj.getClosestPlayerToEntity(this, this.attackRange);
			if(entityPlayer2 != null && this.worldObj.difficultySetting > 0) {
				this.ogrehasenemy = true;
				return entityPlayer2;
			}
		}

		this.ogrehasenemy = false;
		return null;
	}

	public boolean attackEntityFrom(Entity entity1, int i2) {
		if(super.attackEntityFrom(entity1, i2)) {
			if(this.riddenByEntity != entity1 && this.ridingEntity != entity1) {
				if(entity1 != this && this.worldObj.difficultySetting > 0) {
					this.playerToAttack = entity1;
					this.ogrehasenemy = true;
				}

				return true;
			} else {
				return true;
			}
		} else {
			return false;
		}
	}

	public void onLivingUpdate() {
		this.findPlayerToAttack();
		if(this.ogrehasenemy && this.rand.nextInt(this.frequencyA) == 0) {
			this.ogreattack = true;
			this.attackTime = 15;
		}

		super.onLivingUpdate();
	}

	protected void attackEntity(Entity entity1, float f2) {
		if((double)f2 < 2.5D && entity1.boundingBox.maxY > this.boundingBox.minY && entity1.boundingBox.minY < this.boundingBox.maxY && this.worldObj.difficultySetting > 0) {
			entity1.attackEntityFrom(this, this.attackStrength);
		}

	}

	public void DestroyingOgre() {
		Destroyer.DestroyBlast(this.worldObj, this, this.posX, this.posY + 1.0D, this.posZ, this.destroyForce, this.bogrefire);
	}

	public void setEntityDead() {
		super.setEntityDead();
	}

	public boolean getCanSpawnHere() {
		return ((Integer)mod_mocreatures.ogrefreq.get()).intValue() > 0 && this.worldObj.difficultySetting >= ((Integer)mod_mocreatures.ogreSpawnDifficulty.get()).intValue() + 1 && super.getCanSpawnHere();
	}

	public boolean d2() {
		return super.getCanSpawnHere();
	}

	public int getMaxSpawnedInChunk() {
		return 3;
	}

	protected void dropFewItems() {
		int i1 = this.rand.nextInt(3) + 1;

		for(int i2 = 0; i2 < i1; ++i2) {
			this.entityDropItem(new ItemStack(this.getDropItemId(), 1, 0), 0.0F);
		}

	}
}
