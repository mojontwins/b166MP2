package net.minecraft.src;

import java.util.Iterator;
import java.util.List;

public abstract class EntityPlayer extends EntityLiving {
	public InventoryPlayer inventory = new InventoryPlayer(this);
	public Container personalCraftingInventory;
	public Container currentCraftingInventory;
	public byte field_9152_am = 0;
	public int score = 0;
	public float field_9150_ao;
	public float field_9149_ap;
	public boolean isSwinging = false;
	public int swingProgressInt = 0;
	public String username;
	public int dimension;
	public double field_20047_ay;
	public double field_20046_az;
	public double field_20051_aA;
	public double field_20050_aB;
	public double field_20049_aC;
	public double field_20048_aD;
	protected boolean sleeping;
	public ChunkCoordinates playerLocation;
	private int sleepTimer;
	public float field_22066_z;
	public float field_22067_A;
	private ChunkCoordinates spawnChunk;
	private ChunkCoordinates field_27995_d;
	public int field_28016_C = 20;
	protected boolean field_28015_D = false;
	public float field_28014_E;
	private int damageRemainder = 0;
	public EntityFish fishEntity = null;

	public EntityPlayer(World world1) {
		super(world1);
		this.personalCraftingInventory = new ContainerPlayer(this.inventory, !world1.singleplayerWorld);
		this.currentCraftingInventory = this.personalCraftingInventory;
		this.yOffset = 1.62F;
		ChunkCoordinates chunkCoordinates2 = world1.getSpawnPoint();
		this.setLocationAndAngles((double)chunkCoordinates2.posX + 0.5D, (double)(chunkCoordinates2.posY + 1), (double)chunkCoordinates2.posZ + 0.5D, 0.0F, 0.0F);
		this.health = 20;
		this.entityType = "humanoid";
		this.field_9117_aI = 180.0F;
		this.fireResistance = 20;
		this.texture = "/mob/char.png";
	}

	protected void entityInit() {
		super.entityInit();
		this.dataWatcher.addObject(16, (byte)0);
	}

	public void onUpdate() {
		if(this.func_30001_K()) {
			++this.sleepTimer;
			if(this.sleepTimer > 100) {
				this.sleepTimer = 100;
			}

			if(!this.worldObj.singleplayerWorld) {
				if(!this.isInBed()) {
					this.wakeUpPlayer(true, true, false);
				} else if(this.worldObj.isDaytime()) {
					this.wakeUpPlayer(false, true, true);
				}
			}
		} else if(this.sleepTimer > 0) {
			++this.sleepTimer;
			if(this.sleepTimer >= 110) {
				this.sleepTimer = 0;
			}
		}

		super.onUpdate();
		if(!this.worldObj.singleplayerWorld && this.currentCraftingInventory != null && !this.currentCraftingInventory.canInteractWith(this)) {
			this.usePersonalCraftingInventory();
			this.currentCraftingInventory = this.personalCraftingInventory;
		}

		this.field_20047_ay = this.field_20050_aB;
		this.field_20046_az = this.field_20049_aC;
		this.field_20051_aA = this.field_20048_aD;
		double d1 = this.posX - this.field_20050_aB;
		double d3 = this.posY - this.field_20049_aC;
		double d5 = this.posZ - this.field_20048_aD;
		double d7 = 10.0D;
		if(d1 > d7) {
			this.field_20047_ay = this.field_20050_aB = this.posX;
		}

		if(d5 > d7) {
			this.field_20051_aA = this.field_20048_aD = this.posZ;
		}

		if(d3 > d7) {
			this.field_20046_az = this.field_20049_aC = this.posY;
		}

		if(d1 < -d7) {
			this.field_20047_ay = this.field_20050_aB = this.posX;
		}

		if(d5 < -d7) {
			this.field_20051_aA = this.field_20048_aD = this.posZ;
		}

		if(d3 < -d7) {
			this.field_20046_az = this.field_20049_aC = this.posY;
		}

		this.field_20050_aB += d1 * 0.25D;
		this.field_20048_aD += d5 * 0.25D;
		this.field_20049_aC += d3 * 0.25D;
		this.addStat(StatList.field_25114_j, 1);
		if(this.ridingEntity == null) {
			this.field_27995_d = null;
		}

	}

	protected boolean isMovementBlocked() {
		return this.health <= 0 || this.func_30001_K();
	}

	protected void usePersonalCraftingInventory() {
		this.currentCraftingInventory = this.personalCraftingInventory;
	}

	public void updateRidden() {
		double d1 = this.posX;
		double d3 = this.posY;
		double d5 = this.posZ;
		super.updateRidden();
		this.field_9150_ao = this.field_9149_ap;
		this.field_9149_ap = 0.0F;
		this.func_27015_h(this.posX - d1, this.posY - d3, this.posZ - d5);
	}

	protected void updatePlayerActionState() {
		if(this.isSwinging) {
			++this.swingProgressInt;
			if(this.swingProgressInt >= 8) {
				this.swingProgressInt = 0;
				this.isSwinging = false;
			}
		} else {
			this.swingProgressInt = 0;
		}

		this.swingProgress = (float)this.swingProgressInt / 8.0F;
	}

	public void onLivingUpdate() {
		if(this.worldObj.difficultySetting == 0 && this.health < 20 && this.ticksExisted % 20 * 12 == 0) {
			this.heal(1);
		}

		this.inventory.decrementAnimations();
		this.field_9150_ao = this.field_9149_ap;
		super.onLivingUpdate();
		float f1 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
		float f2 = (float)Math.atan(-this.motionY * (double)0.2F) * 15.0F;
		if(f1 > 0.1F) {
			f1 = 0.1F;
		}

		if(!this.onGround || this.health <= 0) {
			f1 = 0.0F;
		}

		if(this.onGround || this.health <= 0) {
			f2 = 0.0F;
		}

		this.field_9149_ap += (f1 - this.field_9149_ap) * 0.4F;
		this.field_9101_aY += (f2 - this.field_9101_aY) * 0.8F;
		if(this.health > 0) {
			List list3 = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox.expand(1.0D, 0.0D, 1.0D));
			if(list3 != null) {
				for(int i4 = 0; i4 < list3.size(); ++i4) {
					Entity entity5 = (Entity)list3.get(i4);
					if(!entity5.isDead) {
						this.func_171_h(entity5);
					}
				}
			}
		}

	}

	private void func_171_h(Entity entity1) {
		entity1.onCollideWithPlayer(this);
	}

	public void onDeath(Entity entity1) {
		super.onDeath(entity1);
		this.setSize(0.2F, 0.2F);
		this.setPosition(this.posX, this.posY, this.posZ);
		this.motionY = (double)0.1F;
		if(this.username.equals("Notch")) {
			this.dropPlayerItemWithRandomChoice(new ItemStack(Item.appleRed, 1), true);
		}

		this.inventory.dropAllItems();
		if(entity1 != null) {
			this.motionX = (double)(-MathHelper.cos((this.attackedAtYaw + this.rotationYaw) * (float)Math.PI / 180.0F) * 0.1F);
			this.motionZ = (double)(-MathHelper.sin((this.attackedAtYaw + this.rotationYaw) * (float)Math.PI / 180.0F) * 0.1F);
		} else {
			this.motionX = this.motionZ = 0.0D;
		}

		this.yOffset = 0.1F;
		this.addStat(StatList.field_25098_u, 1);
	}

	public void addToPlayerScore(Entity entity1, int i2) {
		this.score += i2;
		if(entity1 instanceof EntityPlayer) {
			this.addStat(StatList.field_25096_w, 1);
		} else {
			this.addStat(StatList.field_25097_v, 1);
		}

	}

	public void dropCurrentItem() {
		this.dropPlayerItemWithRandomChoice(this.inventory.decrStackSize(this.inventory.currentItem, 1), false);
	}

	public void dropPlayerItem(ItemStack itemStack1) {
		this.dropPlayerItemWithRandomChoice(itemStack1, false);
	}

	public void dropPlayerItemWithRandomChoice(ItemStack itemStack1, boolean z2) {
		if(itemStack1 != null) {
			EntityItem entityItem3 = new EntityItem(this.worldObj, this.posX, this.posY - (double)0.3F + (double)this.getEyeHeight(), this.posZ, itemStack1);
			entityItem3.delayBeforeCanPickup = 40;
			float f4 = 0.1F;
			float f5;
			if(z2) {
				f5 = this.rand.nextFloat() * 0.5F;
				float f6 = this.rand.nextFloat() * (float)Math.PI * 2.0F;
				entityItem3.motionX = (double)(-MathHelper.sin(f6) * f5);
				entityItem3.motionZ = (double)(MathHelper.cos(f6) * f5);
				entityItem3.motionY = (double)0.2F;
			} else {
				f4 = 0.3F;
				entityItem3.motionX = (double)(-MathHelper.sin(this.rotationYaw / 180.0F * (float)Math.PI) * MathHelper.cos(this.rotationPitch / 180.0F * (float)Math.PI) * f4);
				entityItem3.motionZ = (double)(MathHelper.cos(this.rotationYaw / 180.0F * (float)Math.PI) * MathHelper.cos(this.rotationPitch / 180.0F * (float)Math.PI) * f4);
				entityItem3.motionY = (double)(-MathHelper.sin(this.rotationPitch / 180.0F * (float)Math.PI) * f4 + 0.1F);
				f4 = 0.02F;
				f5 = this.rand.nextFloat() * (float)Math.PI * 2.0F;
				f4 *= this.rand.nextFloat();
				entityItem3.motionX += Math.cos((double)f5) * (double)f4;
				entityItem3.motionY += (double)((this.rand.nextFloat() - this.rand.nextFloat()) * 0.1F);
				entityItem3.motionZ += Math.sin((double)f5) * (double)f4;
			}

			this.joinEntityItemWithWorld(entityItem3);
			this.addStat(StatList.field_25103_r, 1);
		}
	}

	protected void joinEntityItemWithWorld(EntityItem entityItem1) {
		this.worldObj.entityJoinedWorld(entityItem1);
	}

	public float getCurrentPlayerStrVsBlock(Block block1) {
		float f2 = this.inventory.getStrVsBlock(block1);
		if(this.isInsideOfMaterial(Material.water)) {
			f2 /= 5.0F;
		}

		if(!this.onGround) {
			f2 /= 5.0F;
		}

		return f2;
	}

	public boolean canHarvestBlock(Block block1) {
		return this.inventory.canHarvestBlock(block1);
	}

	public void readEntityFromNBT(NBTTagCompound nBTTagCompound1) {
		super.readEntityFromNBT(nBTTagCompound1);
		NBTTagList nBTTagList2 = nBTTagCompound1.getTagList("Inventory");
		this.inventory.readFromNBT(nBTTagList2);
		this.dimension = nBTTagCompound1.getInteger("Dimension");
		this.sleeping = nBTTagCompound1.getBoolean("Sleeping");
		this.sleepTimer = nBTTagCompound1.getShort("SleepTimer");
		if(this.sleeping) {
			this.playerLocation = new ChunkCoordinates(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ));
			this.wakeUpPlayer(true, true, false);
		}

		if(nBTTagCompound1.hasKey("SpawnX") && nBTTagCompound1.hasKey("SpawnY") && nBTTagCompound1.hasKey("SpawnZ")) {
			this.spawnChunk = new ChunkCoordinates(nBTTagCompound1.getInteger("SpawnX"), nBTTagCompound1.getInteger("SpawnY"), nBTTagCompound1.getInteger("SpawnZ"));
		}

	}

	public void writeEntityToNBT(NBTTagCompound nBTTagCompound1) {
		super.writeEntityToNBT(nBTTagCompound1);
		nBTTagCompound1.setTag("Inventory", this.inventory.writeToNBT(new NBTTagList()));
		nBTTagCompound1.setInteger("Dimension", this.dimension);
		nBTTagCompound1.setBoolean("Sleeping", this.sleeping);
		nBTTagCompound1.setShort("SleepTimer", (short)this.sleepTimer);
		if(this.spawnChunk != null) {
			nBTTagCompound1.setInteger("SpawnX", this.spawnChunk.posX);
			nBTTagCompound1.setInteger("SpawnY", this.spawnChunk.posY);
			nBTTagCompound1.setInteger("SpawnZ", this.spawnChunk.posZ);
		}

	}

	public void displayGUIChest(IInventory iInventory1) {
	}

	public void displayWorkbenchGUI(int i1, int i2, int i3) {
	}

	public void onItemPickup(Entity entity1, int i2) {
	}

	public float getEyeHeight() {
		return 0.12F;
	}

	protected void resetHeight() {
		this.yOffset = 1.62F;
	}

	public boolean attackEntityFrom(Entity entity1, int i2) {
		this.age = 0;
		if(this.health <= 0) {
			return false;
		} else {
			if(this.func_30001_K() && !this.worldObj.singleplayerWorld) {
				this.wakeUpPlayer(true, true, false);
			}

			if(entity1 instanceof EntityMob || entity1 instanceof EntityArrow) {
				if(this.worldObj.difficultySetting == 0) {
					i2 = 0;
				}

				if(this.worldObj.difficultySetting == 1) {
					i2 = i2 / 3 + 1;
				}

				if(this.worldObj.difficultySetting == 3) {
					i2 = i2 * 3 / 2;
				}
			}

			if(i2 == 0) {
				return false;
			} else {
				Object object3 = entity1;
				if(entity1 instanceof EntityArrow && ((EntityArrow)entity1).owner != null) {
					object3 = ((EntityArrow)entity1).owner;
				}

				if(object3 instanceof EntityLiving) {
					this.func_25047_a((EntityLiving)object3, false);
				}

				this.addStat(StatList.field_25100_t, i2);
				return super.attackEntityFrom(entity1, i2);
			}
		}
	}

	protected boolean isPVPEnabled() {
		return false;
	}

	protected void func_25047_a(EntityLiving entityLiving1, boolean z2) {
		if(!(entityLiving1 instanceof EntityCreeper) && !(entityLiving1 instanceof EntityGhast)) {
			if(entityLiving1 instanceof EntityWolf) {
				EntityWolf entityWolf3 = (EntityWolf)entityLiving1;
				if(entityWolf3.func_25030_y() && this.username.equals(entityWolf3.getOwner())) {
					return;
				}
			}

			if(!(entityLiving1 instanceof EntityPlayer) || this.isPVPEnabled()) {
				List list7 = this.worldObj.getEntitiesWithinAABB(EntityWolf.class, AxisAlignedBB.getBoundingBoxFromPool(this.posX, this.posY, this.posZ, this.posX + 1.0D, this.posY + 1.0D, this.posZ + 1.0D).expand(16.0D, 4.0D, 16.0D));
				Iterator iterator4 = list7.iterator();

				while(true) {
					EntityWolf entityWolf6;
					do {
						do {
							do {
								do {
									if(!iterator4.hasNext()) {
										return;
									}

									Entity entity5 = (Entity)iterator4.next();
									entityWolf6 = (EntityWolf)entity5;
								} while(!entityWolf6.func_25030_y());
							} while(entityWolf6.getEntityToAttack() != null);
						} while(!this.username.equals(entityWolf6.getOwner()));
					} while(z2 && entityWolf6.getIsSitting());

					entityWolf6.setIsSitting(false);
					entityWolf6.setEntityToAttack(entityLiving1);
				}
			}
		}
	}

	protected void damageEntity(int i1) {
		int i2 = 25 - this.inventory.getTotalArmorValue();
		int i3 = i1 * i2 + this.damageRemainder;
		this.inventory.damageArmor(i1);
		i1 = i3 / 25;
		this.damageRemainder = i3 % 25;
		super.damageEntity(i1);
	}

	public void displayGUIFurnace(TileEntityFurnace tileEntityFurnace1) {
	}

	public void displayGUIDispenser(TileEntityDispenser tileEntityDispenser1) {
	}

	public void displayGUIEditSign(TileEntitySign tileEntitySign1) {
	}

	public void useCurrentItemOnEntity(Entity entity1) {
		if(!entity1.interact(this)) {
			ItemStack itemStack2 = this.getCurrentEquippedItem();
			if(itemStack2 != null && entity1 instanceof EntityLiving) {
				itemStack2.useItemOnEntity((EntityLiving)entity1);
				if(itemStack2.stackSize <= 0) {
					itemStack2.func_577_a(this);
					this.destroyCurrentEquippedItem();
				}
			}

		}
	}

	public ItemStack getCurrentEquippedItem() {
		return this.inventory.getCurrentItem();
	}

	public void destroyCurrentEquippedItem() {
		this.inventory.setInventorySlotContents(this.inventory.currentItem, (ItemStack)null);
	}

	public double getYOffset() {
		return (double)(this.yOffset - 0.5F);
	}

	public void swingItem() {
		this.swingProgressInt = -1;
		this.isSwinging = true;
	}

	public void attackTargetEntityWithCurrentItem(Entity entity1) {
		int i2 = this.inventory.getDamageVsEntity(entity1);
		if(i2 > 0) {
			if(this.motionY < 0.0D) {
				++i2;
			}

			entity1.attackEntityFrom(this, i2);
			ItemStack itemStack3 = this.getCurrentEquippedItem();
			if(itemStack3 != null && entity1 instanceof EntityLiving) {
				itemStack3.hitEntity((EntityLiving)entity1, this);
				if(itemStack3.stackSize <= 0) {
					itemStack3.func_577_a(this);
					this.destroyCurrentEquippedItem();
				}
			}

			if(entity1 instanceof EntityLiving) {
				if(entity1.isEntityAlive()) {
					this.func_25047_a((EntityLiving)entity1, true);
				}

				this.addStat(StatList.field_25102_s, i2);
			}
		}

	}

	public void onItemStackChanged(ItemStack itemStack1) {
	}

	public void setEntityDead() {
		super.setEntityDead();
		this.personalCraftingInventory.onCraftGuiClosed(this);
		if(this.currentCraftingInventory != null) {
			this.currentCraftingInventory.onCraftGuiClosed(this);
		}

	}

	public boolean isEntityInsideOpaqueBlock() {
		return !this.sleeping && super.isEntityInsideOpaqueBlock();
	}

	public EnumStatus goToSleep(int i1, int i2, int i3) {
		if(!this.worldObj.singleplayerWorld) {
			label53: {
				if(!this.func_30001_K() && this.isEntityAlive()) {
					if(this.worldObj.worldProvider.field_6167_c) {
						return EnumStatus.NOT_POSSIBLE_HERE;
					}

					if(this.worldObj.isDaytime()) {
						return EnumStatus.NOT_POSSIBLE_NOW;
					}

					if(Math.abs(this.posX - (double)i1) <= 3.0D && Math.abs(this.posY - (double)i2) <= 2.0D && Math.abs(this.posZ - (double)i3) <= 3.0D) {
						break label53;
					}

					return EnumStatus.TOO_FAR_AWAY;
				}

				return EnumStatus.OTHER_PROBLEM;
			}
		}

		this.setSize(0.2F, 0.2F);
		this.yOffset = 0.2F;
		if(this.worldObj.blockExists(i1, i2, i3)) {
			int i4 = this.worldObj.getBlockMetadata(i1, i2, i3);
			int i5 = BlockBed.func_22019_c(i4);
			float f6 = 0.5F;
			float f7 = 0.5F;
			switch(i5) {
			case 0:
				f7 = 0.9F;
				break;
			case 1:
				f6 = 0.1F;
				break;
			case 2:
				f7 = 0.1F;
				break;
			case 3:
				f6 = 0.9F;
			}

			this.func_22059_e(i5);
			this.setPosition((double)((float)i1 + f6), (double)((float)i2 + 0.9375F), (double)((float)i3 + f7));
		} else {
			this.setPosition((double)((float)i1 + 0.5F), (double)((float)i2 + 0.9375F), (double)((float)i3 + 0.5F));
		}

		this.sleeping = true;
		this.sleepTimer = 0;
		this.playerLocation = new ChunkCoordinates(i1, i2, i3);
		this.motionX = this.motionZ = this.motionY = 0.0D;
		if(!this.worldObj.singleplayerWorld) {
			this.worldObj.updateAllPlayersSleepingFlag();
		}

		return EnumStatus.OK;
	}

	private void func_22059_e(int i1) {
		this.field_22066_z = 0.0F;
		this.field_22067_A = 0.0F;
		switch(i1) {
		case 0:
			this.field_22067_A = -1.8F;
			break;
		case 1:
			this.field_22066_z = 1.8F;
			break;
		case 2:
			this.field_22067_A = 1.8F;
			break;
		case 3:
			this.field_22066_z = -1.8F;
		}

	}

	public void wakeUpPlayer(boolean z1, boolean z2, boolean z3) {
		this.setSize(0.6F, 1.8F);
		this.resetHeight();
		ChunkCoordinates chunkCoordinates4 = this.playerLocation;
		ChunkCoordinates chunkCoordinates5 = this.playerLocation;
		if(chunkCoordinates4 != null && this.worldObj.getBlockId(chunkCoordinates4.posX, chunkCoordinates4.posY, chunkCoordinates4.posZ) == Block.bed.blockID) {
			BlockBed.func_22022_a(this.worldObj, chunkCoordinates4.posX, chunkCoordinates4.posY, chunkCoordinates4.posZ, false);
			chunkCoordinates5 = BlockBed.func_22021_g(this.worldObj, chunkCoordinates4.posX, chunkCoordinates4.posY, chunkCoordinates4.posZ, 0);
			if(chunkCoordinates5 == null) {
				chunkCoordinates5 = new ChunkCoordinates(chunkCoordinates4.posX, chunkCoordinates4.posY + 1, chunkCoordinates4.posZ);
			}

			this.setPosition((double)((float)chunkCoordinates5.posX + 0.5F), (double)((float)chunkCoordinates5.posY + this.yOffset + 0.1F), (double)((float)chunkCoordinates5.posZ + 0.5F));
		}

		this.sleeping = false;
		if(!this.worldObj.singleplayerWorld && z2) {
			this.worldObj.updateAllPlayersSleepingFlag();
		}

		if(z1) {
			this.sleepTimer = 0;
		} else {
			this.sleepTimer = 100;
		}

		if(z3) {
			this.setSpawnChunk(this.playerLocation);
		}

	}

	private boolean isInBed() {
		return this.worldObj.getBlockId(this.playerLocation.posX, this.playerLocation.posY, this.playerLocation.posZ) == Block.bed.blockID;
	}

	public static ChunkCoordinates func_25051_a(World world0, ChunkCoordinates chunkCoordinates1) {
		IChunkProvider iChunkProvider2 = world0.getChunkProvider();
		iChunkProvider2.loadChunk(chunkCoordinates1.posX - 3 >> 4, chunkCoordinates1.posZ - 3 >> 4);
		iChunkProvider2.loadChunk(chunkCoordinates1.posX + 3 >> 4, chunkCoordinates1.posZ - 3 >> 4);
		iChunkProvider2.loadChunk(chunkCoordinates1.posX - 3 >> 4, chunkCoordinates1.posZ + 3 >> 4);
		iChunkProvider2.loadChunk(chunkCoordinates1.posX + 3 >> 4, chunkCoordinates1.posZ + 3 >> 4);
		if(world0.getBlockId(chunkCoordinates1.posX, chunkCoordinates1.posY, chunkCoordinates1.posZ) != Block.bed.blockID) {
			return null;
		} else {
			ChunkCoordinates chunkCoordinates3 = BlockBed.func_22021_g(world0, chunkCoordinates1.posX, chunkCoordinates1.posY, chunkCoordinates1.posZ, 0);
			return chunkCoordinates3;
		}
	}

	public boolean func_30001_K() {
		return this.sleeping;
	}

	public boolean isPlayerFullyAsleep() {
		return this.sleeping && this.sleepTimer >= 100;
	}

	public void func_22061_a(String string1) {
	}

	public ChunkCoordinates getSpawnChunk() {
		return this.spawnChunk;
	}

	public void setSpawnChunk(ChunkCoordinates chunkCoordinates1) {
		if(chunkCoordinates1 != null) {
			this.spawnChunk = new ChunkCoordinates(chunkCoordinates1);
		} else {
			this.spawnChunk = null;
		}

	}

	public void func_27017_a(StatBase statBase1) {
		this.addStat(statBase1, 1);
	}

	public void addStat(StatBase statBase1, int i2) {
	}

	protected void jump() {
		super.jump();
		this.addStat(StatList.field_25106_q, 1);
	}

	public void moveEntityWithHeading(float f1, float f2) {
		double d3 = this.posX;
		double d5 = this.posY;
		double d7 = this.posZ;
		super.moveEntityWithHeading(f1, f2);
		this.func_25045_g(this.posX - d3, this.posY - d5, this.posZ - d7);
	}

	private void func_25045_g(double d1, double d3, double d5) {
		if(this.ridingEntity == null) {
			int i7;
			if(this.isInsideOfMaterial(Material.water)) {
				i7 = Math.round(MathHelper.sqrt_double(d1 * d1 + d3 * d3 + d5 * d5) * 100.0F);
				if(i7 > 0) {
					this.addStat(StatList.field_25108_p, i7);
				}
			} else if(this.func_27011_Z()) {
				i7 = Math.round(MathHelper.sqrt_double(d1 * d1 + d5 * d5) * 100.0F);
				if(i7 > 0) {
					this.addStat(StatList.field_25112_l, i7);
				}
			} else if(this.isOnLadder()) {
				if(d3 > 0.0D) {
					this.addStat(StatList.field_25110_n, (int)Math.round(d3 * 100.0D));
				}
			} else if(this.onGround) {
				i7 = Math.round(MathHelper.sqrt_double(d1 * d1 + d5 * d5) * 100.0F);
				if(i7 > 0) {
					this.addStat(StatList.field_25113_k, i7);
				}
			} else {
				i7 = Math.round(MathHelper.sqrt_double(d1 * d1 + d5 * d5) * 100.0F);
				if(i7 > 25) {
					this.addStat(StatList.field_25109_o, i7);
				}
			}

		}
	}

	private void func_27015_h(double d1, double d3, double d5) {
		if(this.ridingEntity != null) {
			int i7 = Math.round(MathHelper.sqrt_double(d1 * d1 + d3 * d3 + d5 * d5) * 100.0F);
			if(i7 > 0) {
				if(this.ridingEntity instanceof EntityMinecart) {
					this.addStat(StatList.field_27095_r, i7);
					if(this.field_27995_d == null) {
						this.field_27995_d = new ChunkCoordinates(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ));
					} else if(this.field_27995_d.getSqDistanceTo(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ)) >= 1000.0D) {
						this.addStat(AchievementList.field_27102_q, 1);
					}
				} else if(this.ridingEntity instanceof EntityBoat) {
					this.addStat(StatList.field_27094_s, i7);
				} else if(this.ridingEntity instanceof EntityPig) {
					this.addStat(StatList.field_27093_t, i7);
				}
			}
		}

	}

	protected void fall(float f1) {
		if(f1 >= 2.0F) {
			this.addStat(StatList.field_25111_m, (int)Math.round((double)f1 * 100.0D));
		}

		super.fall(f1);
	}

	public void func_27010_a(EntityLiving entityLiving1) {
		if(entityLiving1 instanceof EntityMob) {
			this.func_27017_a(AchievementList.field_27100_s);
		}

	}

	public void setInPortal() {
		if(this.field_28016_C > 0) {
			this.field_28016_C = 10;
		} else {
			this.field_28015_D = true;
		}
	}
}
