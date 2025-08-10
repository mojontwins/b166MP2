package net.minecraft.src;

import java.util.List;

public class EntityDeer extends EntityAnimal {
	public int typeint;
	public boolean typechosen;
	public boolean adult;
	public float edad;
	public boolean tamed;

	public EntityDeer(World world1) {
		super(world1);
		this.texture = "/mob/deer.png";
		this.edad = 0.75F;
		this.setSize(0.9F, 1.3F);
		this.health = 10;
		this.adult = true;
		this.moveSpeed = 1.7F;
	}

	public void setType(int i1) {
		this.typeint = i1;
		this.typechosen = false;
		this.chooseType();
	}

	public void chooseType() {
		if(this.typeint == 0) {
			int i1 = this.rand.nextInt(100);
			if(i1 <= 20) {
				this.typeint = 1;
			} else if(i1 <= 70) {
				this.typeint = 2;
			} else {
				this.typeint = 3;
			}
		}

		if(!this.typechosen) {
			if(this.typeint == 1) {
				this.texture = "/mob/deer.png";
				this.health = 15;
			} else if(this.typeint == 2) {
				this.texture = "/mob/deerf.png";
				this.health = 15;
			} else {
				this.texture = "/mob/deerb.png";
				this.health = 5;
				this.adult = false;
			}
		}

		this.setMySpeed(false);
		this.typechosen = true;
	}

	public void setMySpeed(boolean z1) {
		float f2 = 1.0F;
		if(this.typeint == 1) {
			f2 = 1.7F;
		} else if(this.typeint == 2) {
			f2 = 1.9F;
		} else {
			f2 = 1.3F;
		}

		if(z1) {
			f2 *= 2.0F;
		}

		this.moveSpeed = f2;
	}

	protected void fall(float f1) {
	}

	public void onLivingUpdate() {
		super.onLivingUpdate();
		if(this.typeint == 3 && !this.adult && this.rand.nextInt(250) == 0) {
			this.edad += 0.01F;
			if(this.edad >= 1.5F) {
				this.adult = true;
				int i1 = this.rand.nextInt(1);
				this.setType(i1);
			}
		}

		if(this.rand.nextInt(5) == 0) {
			EntityLiving entityLiving2 = this.getBoogey(10.0D);
			if(entityLiving2 != null) {
				this.setMySpeed(true);
				this.runLikeHell(entityLiving2);
			} else {
				this.setMySpeed(false);
			}
		}

	}

	public EntityLiving getBoogey(double d1) {
		double d3 = -1.0D;
		EntityLiving entityLiving5 = null;
		List list6 = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox.expand(d1, 4.0D, d1));

		for(int i7 = 0; i7 < list6.size(); ++i7) {
			Entity entity8 = (Entity)list6.get(i7);
			if(entity8 instanceof EntityLiving && !(entity8 instanceof EntityDeer) && ((double)entity8.width >= 0.5D || (double)entity8.height >= 0.5D)) {
				entityLiving5 = (EntityLiving)entity8;
			}
		}

		return entityLiving5;
	}

	public void runLikeHell(Entity entity1) {
		double d2 = this.posX - entity1.posX;
		double d4 = this.posZ - entity1.posZ;
		double d6 = Math.atan2(d2, d4);
		d6 += (double)(this.rand.nextFloat() - this.rand.nextFloat()) * 0.75D;
		double d8 = this.posX + Math.sin(d6) * 8.0D;
		double d10 = this.posZ + Math.cos(d6) * 8.0D;
		int i12 = MathHelper.floor_double(d8);
		int i13 = MathHelper.floor_double(this.boundingBox.minY);
		int i14 = MathHelper.floor_double(d10);

		for(int i15 = 0; i15 < 16; ++i15) {
			int i16 = i12 + this.rand.nextInt(4) - this.rand.nextInt(4);
			int i17 = i13 + this.rand.nextInt(3) - this.rand.nextInt(3);
			int i18 = i14 + this.rand.nextInt(4) - this.rand.nextInt(4);
			if(i17 > 4 && (this.worldObj.getBlockId(i16, i17, i18) == 0 || this.worldObj.getBlockId(i16, i17, i18) == Block.snow.blockID) && this.worldObj.getBlockId(i16, i17 - 1, i18) != 0) {
				PathEntity pathEntity19 = this.worldObj.getEntityPathToXYZ(this, i16, i17, i18, 16.0F);
				this.setPathToEntity(pathEntity19);
				break;
			}
		}

	}

	protected void updatePlayerActionState() {
		if(this.moveSpeed > 2.0F && this.onGround && this.rand.nextInt(30) == 0 && (this.motionX > 0.1D || this.motionZ > 0.1D || this.motionX < -0.1D || this.motionZ < -0.1D)) {
			this.motionY = 0.6D;
		}

		super.updatePlayerActionState();
	}

	public void writeEntityToNBT(NBTTagCompound nBTTagCompound1) {
		super.writeEntityToNBT(nBTTagCompound1);
		nBTTagCompound1.setInteger("TypeInt", this.typeint);
		nBTTagCompound1.setBoolean("Adult", this.adult);
		nBTTagCompound1.setFloat("Edad", this.edad);
	}

	public void readEntityFromNBT(NBTTagCompound nBTTagCompound1) {
		super.readEntityFromNBT(nBTTagCompound1);
		this.adult = nBTTagCompound1.getBoolean("Adult");
		this.typeint = nBTTagCompound1.getInteger("TypeInt");
		this.edad = nBTTagCompound1.getFloat("Edad");
	}

	protected String getLivingSound() {
		return "deergrunt";
	}

	protected String getHurtSound() {
		return "deerhurt";
	}

	protected String getDeathSound() {
		return "deerdeath";
	}

	protected int getDropItemId() {
		return Item.porkRaw.shiftedIndex;
	}
}
