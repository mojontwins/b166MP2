package net.minecraft.src;

public class EntityItem extends Entity {
	public ItemStack item;
	private int field_803_e;
	public int age = 0;
	public int delayBeforeCanPickup;
	private int health = 5;
	public float field_804_d = (float)(Math.random() * Math.PI * 2.0D);

	public EntityItem(World paramfb, double paramDouble1, double paramDouble2, double paramDouble3, ItemStack paramiw) {
		super(paramfb);
		this.setSize(0.25F, 0.25F);
		this.yOffset = this.height / 2.0F;
		this.setPosition(paramDouble1, paramDouble2, paramDouble3);
		this.item = paramiw;
		this.rotationYaw = (float)(Math.random() * 360.0D);
		this.motionX = (double)((float)(Math.random() * 0.2000000029802322D - 0.1000000014901161D));
		this.motionY = 0.2000000029802322D;
		this.motionZ = (double)((float)(Math.random() * 0.2000000029802322D - 0.1000000014901161D));
	}

	protected boolean canTriggerWalking() {
		return false;
	}

	public EntityItem(World paramfb) {
		super(paramfb);
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
			this.motionY = 0.2000000029802322D;
			this.motionX = (double)((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F);
			this.motionZ = (double)((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F);
			this.worldObj.playSoundAtEntity(this, "random.fizz", 0.4F, 2.0F + this.rand.nextFloat() * 0.4F);
		}

		this.func_28014_c(this.posX, (this.boundingBox.minY + this.boundingBox.maxY) / 2.0D, this.posZ);
		this.moveEntity(this.motionX, this.motionY, this.motionZ);
		float f1 = 0.98F;
		if(this.onGround) {
			f1 = 0.5880001F;
			int i = this.worldObj.getBlockId(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.boundingBox.minY) - 1, MathHelper.floor_double(this.posZ));
			if(i > 0) {
				f1 = Block.blocksList[i].slipperiness * 0.98F;
			}
		}

		this.motionX *= (double)f1;
		this.motionY *= (double)0.98F;
		this.motionZ *= (double)f1;
		if(this.onGround) {
			this.motionY *= -0.5D;
		}

		++this.field_803_e;
		++this.age;
		if(this.age >= 6000) {
			this.setEntityDead();
		}

	}

	public boolean handleWaterMovement() {
		return this.worldObj.handleMaterialAcceleration(this.boundingBox, Material.water, this);
	}

	protected void dealFireDamage(int paramInt) {
		this.attackEntityFrom((Entity)null, paramInt);
	}

	public boolean attackEntityFrom(Entity paramsi, int paramInt) {
		this.setBeenAttacked();
		this.health -= paramInt;
		if(this.health <= 0) {
			this.setEntityDead();
		}

		return false;
	}

	public void writeEntityToNBT(NBTTagCompound paramnq) {
		paramnq.setShort("Health", (short)((byte)this.health));
		paramnq.setShort("Age", (short)this.age);
		paramnq.setCompoundTag("Item", this.item.writeToNBT(new NBTTagCompound()));
	}

	public void readEntityFromNBT(NBTTagCompound paramnq) {
		this.health = paramnq.getShort("Health") & 255;
		this.age = paramnq.getShort("Age");
		NBTTagCompound localnq = paramnq.getCompoundTag("Item");
		this.item = new ItemStack(localnq);
	}

	public void onCollideWithPlayer(EntityPlayer paramgq) {
		if(!this.worldObj.multiplayerWorld) {
			int i = this.item.stackSize;
			if(this.delayBeforeCanPickup == 0 && paramgq.inventory.addItemStackToInventory(this.item)) {
				if(this.item.itemID == Block.wood.blockID) {
					paramgq.triggerAchievement(AchievementList.mineWood);
				}

				if(this.item.itemID == Item.leather.shiftedIndex) {
					paramgq.triggerAchievement(AchievementList.killCow);
				}

				ModLoader.OnItemPickup(paramgq, this.item);
				this.worldObj.playSoundAtEntity(this, "random.pop", 0.2F, ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
				paramgq.onItemPickup(this, i);
				if(this.item.stackSize <= 0) {
					this.setEntityDead();
				}
			}

		}
	}
}
