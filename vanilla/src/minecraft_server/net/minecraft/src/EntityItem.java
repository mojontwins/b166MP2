package net.minecraft.src;

public class EntityItem extends Entity {
	public ItemStack item;
	private int field_9170_e;
	public int age = 0;
	public int delayBeforeCanPickup;
	private int health = 5;
	public float field_432_ae = (float)(Math.random() * Math.PI * 2.0D);

	public EntityItem(World world1, double d2, double d4, double d6, ItemStack itemStack8) {
		super(world1);
		this.setSize(0.25F, 0.25F);
		this.yOffset = this.height / 2.0F;
		this.setPosition(d2, d4, d6);
		this.item = itemStack8;
		this.rotationYaw = (float)(Math.random() * 360.0D);
		this.motionX = (double)((float)(Math.random() * (double)0.2F - (double)0.1F));
		this.motionY = (double)0.2F;
		this.motionZ = (double)((float)(Math.random() * (double)0.2F - (double)0.1F));
	}

	protected boolean func_25017_l() {
		return false;
	}

	public EntityItem(World world1) {
		super(world1);
		this.setSize(0.25F, 0.25F);
		this.yOffset = this.height / 2.0F;
	}

	protected void entityInit() {
	}

	public void onUpdate() {
		super.onUpdate();
		if(this.delayBeforeCanPickup > 0) {
			--this.delayBeforeCanPickup;
		}

		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		this.motionY -= (double)0.04F;
		if(this.worldObj.getBlockMaterial(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ)) == Material.lava) {
			this.motionY = (double)0.2F;
			this.motionX = (double)((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F);
			this.motionZ = (double)((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F);
			this.worldObj.playSoundAtEntity(this, "random.fizz", 0.4F, 2.0F + this.rand.nextFloat() * 0.4F);
		}

		this.func_28005_g(this.posX, (this.boundingBox.minY + this.boundingBox.maxY) / 2.0D, this.posZ);
		this.moveEntity(this.motionX, this.motionY, this.motionZ);
		float f1 = 0.98F;
		if(this.onGround) {
			f1 = 0.58800006F;
			int i2 = this.worldObj.getBlockId(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.boundingBox.minY) - 1, MathHelper.floor_double(this.posZ));
			if(i2 > 0) {
				f1 = Block.blocksList[i2].slipperiness * 0.98F;
			}
		}

		this.motionX *= (double)f1;
		this.motionY *= (double)0.98F;
		this.motionZ *= (double)f1;
		if(this.onGround) {
			this.motionY *= -0.5D;
		}

		++this.field_9170_e;
		++this.age;
		if(this.age >= 6000) {
			this.setEntityDead();
		}

	}

	public boolean handleWaterMovement() {
		return this.worldObj.handleMaterialAcceleration(this.boundingBox, Material.water, this);
	}

	protected void dealFireDamage(int i1) {
		this.attackEntityFrom((Entity)null, i1);
	}

	public boolean attackEntityFrom(Entity entity1, int i2) {
		this.setBeenAttacked();
		this.health -= i2;
		if(this.health <= 0) {
			this.setEntityDead();
		}

		return false;
	}

	public void writeEntityToNBT(NBTTagCompound nBTTagCompound1) {
		nBTTagCompound1.setShort("Health", (short)((byte)this.health));
		nBTTagCompound1.setShort("Age", (short)this.age);
		nBTTagCompound1.setCompoundTag("Item", this.item.writeToNBT(new NBTTagCompound()));
	}

	public void readEntityFromNBT(NBTTagCompound nBTTagCompound1) {
		this.health = nBTTagCompound1.getShort("Health") & 255;
		this.age = nBTTagCompound1.getShort("Age");
		NBTTagCompound nBTTagCompound2 = nBTTagCompound1.getCompoundTag("Item");
		this.item = new ItemStack(nBTTagCompound2);
	}

	public void onCollideWithPlayer(EntityPlayer entityPlayer1) {
		if(!this.worldObj.singleplayerWorld) {
			int i2 = this.item.stackSize;
			if(this.delayBeforeCanPickup == 0 && entityPlayer1.inventory.addItemStackToInventory(this.item)) {
				if(this.item.itemID == Block.wood.blockID) {
					entityPlayer1.func_27017_a(AchievementList.field_25131_c);
				}

				if(this.item.itemID == Item.leather.shiftedIndex) {
					entityPlayer1.func_27017_a(AchievementList.field_27099_t);
				}

				this.worldObj.playSoundAtEntity(this, "random.pop", 0.2F, ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
				entityPlayer1.onItemPickup(this, i2);
				if(this.item.stackSize <= 0) {
					this.setEntityDead();
				}
			}

		}
	}
}
