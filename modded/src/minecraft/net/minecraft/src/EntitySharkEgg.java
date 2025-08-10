package net.minecraft.src;

public class EntitySharkEgg extends EntityLiving {
	private int tCounter;
	private int lCounter;

	public EntitySharkEgg(World world1) {
		super(world1);
		this.setSize(0.25F, 0.25F);
		this.tCounter = 0;
		this.lCounter = 0;
		this.texture = "/mob/sharkeggt.png";
	}

	public void writeEntityToNBT(NBTTagCompound nBTTagCompound1) {
	}

	public void readEntityFromNBT(NBTTagCompound nBTTagCompound1) {
	}

	protected void entityInit() {
	}

	public void onLivingUpdate() {
		this.moveStrafing = 0.0F;
		this.moveForward = 0.0F;
		this.randomYawVelocity = 0.0F;
		this.moveEntityWithHeading(this.moveStrafing, this.moveForward);
	}

	public void onCollideWithPlayer(EntityPlayer entityPlayer1) {
		if(!this.worldObj.multiplayerWorld) {
			if(this.lCounter > 10 && entityPlayer1.inventory.addItemStackToInventory(new ItemStack(mod_mocreatures.sharkegg, 1))) {
				this.worldObj.playSoundAtEntity(this, "random.pop", 0.2F, ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
				entityPlayer1.onItemPickup(this, 1);
				this.setEntityDead();
			}

		}
	}

	public void onUpdate() {
		super.onUpdate();
		if(this.rand.nextInt(20) == 0) {
			++this.lCounter;
		}

		if(this.inWater && this.rand.nextInt(20) == 0) {
			++this.tCounter;
			if(this.tCounter >= 50) {
				EntityShark entityShark1 = new EntityShark(this.worldObj);
				entityShark1.b = 0.3F;
				entityShark1.tamed = true;
				entityShark1.setPosition(this.posX, this.posY, this.posZ);
				this.worldObj.entityJoinedWorld(entityShark1);
				this.worldObj.playSoundAtEntity(this, "mob.chickenplop", 1.0F, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
				this.setEntityDead();
			}
		}

	}

	public boolean canBreatheUnderwater() {
		return true;
	}

	public boolean handleWaterMovement() {
		return this.worldObj.handleMaterialAcceleration(this.boundingBox, Material.water, this);
	}

	protected String getLivingSound() {
		return null;
	}

	protected String getHurtSound() {
		return null;
	}

	protected String getDeathSound() {
		return null;
	}

	protected float getSoundVolume() {
		return 0.4F;
	}
}
