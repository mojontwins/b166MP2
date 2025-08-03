package net.minecraft.world.entity.player;

import java.util.Iterator;
import java.util.List;

import com.mojang.nbt.NBTTagCompound;
import com.mojang.nbt.NBTTagList;

import net.minecraft.src.MathHelper;
import net.minecraft.world.GameRules;
import net.minecraft.world.entity.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EnumAction;
import net.minecraft.world.entity.FoodStats;
import net.minecraft.world.entity.animal.EntityWolf;
import net.minecraft.world.entity.item.EntityItem;
import net.minecraft.world.entity.monster.EntityCreeper;
import net.minecraft.world.entity.monster.EntityGhast;
import net.minecraft.world.entity.monster.EntityMob;
import net.minecraft.world.entity.projectile.EntityArrow;
import net.minecraft.world.entity.projectile.EntityFishHook;
import net.minecraft.world.inventory.Container;
import net.minecraft.world.inventory.ContainerPlayer;
import net.minecraft.world.inventory.IInventory;
import net.minecraft.world.inventory.InventoryPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.World;
import net.minecraft.world.level.chunk.ChunkCoordinates;
import net.minecraft.world.level.chunk.IChunkProvider;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.tile.Block;
import net.minecraft.world.level.tile.BlockBed;
import net.minecraft.world.level.tile.entity.TileEntityDispenser;
import net.minecraft.world.level.tile.entity.TileEntityFurnace;
import net.minecraft.world.level.tile.entity.TileEntitySign;
import net.minecraft.world.phys.AxisAlignedBB;
import net.minecraft.world.phys.Vec3D;

public abstract class EntityPlayer extends EntityLiving {
	public InventoryPlayer inventory = new InventoryPlayer(this);
	public Container inventorySlots;
	public Container craftingInventory;
	protected FoodStats foodStats = new FoodStats();
	protected int flyToggleTimer = 0;
	public byte field_9371_f = 0;
	public int score = 0;
	public float prevCameraYaw;
	public float cameraYaw;
	public boolean isSwinging = false;
	public int swingProgressInt = 0;
	public String username;
	public int dimension;
	public String playerCloakUrl;
	public int xpCooldown = 0;
	public double field_20066_r;
	public double field_20065_s;
	public double field_20064_t;
	public double field_20063_u;
	public double field_20062_v;
	public double field_20061_w;
	protected boolean sleeping;
	public ChunkCoordinates playerLocation;
	private int sleepTimer;
	public float field_22063_x;
	public float field_22062_y;
	public float field_22061_z;
	private ChunkCoordinates spawnChunk;
	private int spawnDimension;
	public int timeUntilPortal = 20;
	protected boolean inPortal = false;
	public float timeInPortal;
	public float prevTimeInPortal;
	public PlayerCapabilities capabilities = new PlayerCapabilities();
	public int experienceLevel;
	public int experienceTotal;
	public float experience;
	private ItemStack itemInUse;
	private int itemInUseCount;
	protected float speedOnGround = 0.1F;
	protected float speedInAir = 0.02F;
	public EntityFishHook fishEntity = null;
	public boolean pressingJump;
	private boolean dontCheckSpawnCoordinates = false;

	public EntityPlayer(World world1) {
		super(world1);
		this.inventorySlots = new ContainerPlayer(this.inventory, !world1.isRemote);
		this.craftingInventory = this.inventorySlots;
		this.yOffset = 1.62F;
		ChunkCoordinates chunkCoordinates2 = world1.getSpawnPoint();
		this.setLocationAndAngles((double)chunkCoordinates2.posX + 0.5D, (double)(chunkCoordinates2.posY + 1), (double)chunkCoordinates2.posZ + 0.5D, 0.0F, 0.0F);
		this.entityType = "humanoid";
		this.field_9353_B = 180.0F;
		this.fireResistance = 20;
		this.texture = "/mob/char.png";
	}

	public int getMaxHealth() {
		return 20;
	}

	protected void entityInit() {
		super.entityInit();
		this.dataWatcher.addObject(16, (byte)0);
		this.dataWatcher.addObject(17, (byte)0);
	}

	public ItemStack getItemInUse() {
		return this.itemInUse;
	}

	public int getItemInUseCount() {
		return this.itemInUseCount;
	}

	public boolean isUsingItem() {
		return this.itemInUse != null;
	}

	public int getItemInUseDuration() {
		return this.isUsingItem() ? this.itemInUse.getMaxItemUseDuration() - this.itemInUseCount : 0;
	}

	public void stopUsingItem() {
		if(this.itemInUse != null) {
			this.itemInUse.onPlayerStoppedUsing(this.worldObj, this, this.itemInUseCount);
		}

		this.clearItemInUse();
	}

	public void clearItemInUse() {
		this.itemInUse = null;
		this.itemInUseCount = 0;
		if(!this.worldObj.isRemote) {
			this.setEating(false);
		}

	}

	public boolean isBlocking() {
		return this.isUsingItem() && Item.itemsList[this.itemInUse.itemID].getItemUseAction(this.itemInUse) == EnumAction.block;
	}

	public void onUpdate() {
		if(this.itemInUse != null) {
			ItemStack itemStack1 = this.inventory.getCurrentItem();
			if(itemStack1 != this.itemInUse) {
				this.clearItemInUse();
			} else {
				// Added (I think this may be forge's ?
				this.itemInUse.getItem().onUsingItemTick(this.itemInUse, this, this.itemInUseCount);
				
				if(this.itemInUseCount <= 25 && this.itemInUseCount % 4 == 0) {
					this.updateItemUse(itemStack1, 5);
				}

				if(--this.itemInUseCount == 0 && !this.worldObj.isRemote) {
					this.onItemUseFinish();
				}
			}
		}

		if(this.xpCooldown > 0) {
			--this.xpCooldown;
		}

		if(this.isPlayerSleeping()) {
			++this.sleepTimer;
			if(this.sleepTimer > 100) {
				this.sleepTimer = 100;
			}
			
			if(!this.worldObj.isRemote) {
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
		if(!this.worldObj.isRemote && this.craftingInventory != null && !this.craftingInventory.canInteractWith(this)) {
			this.closeScreen();
			this.craftingInventory = this.inventorySlots;
		}

		if(this.capabilities.isFlying) {
			for(int i9 = 0; i9 < 8; ++i9) {
			}
		}

		if(this.isBurning() && this.capabilities.disableDamage) {
			this.extinguish();
		}

		this.field_20066_r = this.field_20063_u;
		this.field_20065_s = this.field_20062_v;
		this.field_20064_t = this.field_20061_w;
		double d10 = this.posX - this.field_20063_u;
		double d3 = this.posY - this.field_20062_v;
		double d5 = this.posZ - this.field_20061_w;
		double d7 = 10.0D;
		if(d10 > d7) {
			this.field_20066_r = this.field_20063_u = this.posX;
		}

		if(d5 > d7) {
			this.field_20064_t = this.field_20061_w = this.posZ;
		}

		if(d3 > d7) {
			this.field_20065_s = this.field_20062_v = this.posY;
		}

		if(d10 < -d7) {
			this.field_20066_r = this.field_20063_u = this.posX;
		}

		if(d5 < -d7) {
			this.field_20064_t = this.field_20061_w = this.posZ;
		}

		if(d3 < -d7) {
			this.field_20065_s = this.field_20062_v = this.posY;
		}

		this.field_20063_u += d10 * 0.25D;
		this.field_20061_w += d5 * 0.25D;
		this.field_20062_v += d3 * 0.25D;

		if(this.ridingEntity == null) {
		}

		if(!this.worldObj.isRemote) {
			this.foodStats.onUpdate(this);
		}

	}

	protected void updateItemUse(ItemStack itemStack1, int i2) {
		if(itemStack1.getItemUseAction() == EnumAction.drink) {
			this.worldObj.playSoundAtEntity(this, "random.drink", 0.5F, this.worldObj.rand.nextFloat() * 0.1F + 0.9F);
		}

		if(itemStack1.getItemUseAction() == EnumAction.eat) {
			for(int i3 = 0; i3 < i2; ++i3) {
				Vec3D vec3D4 = Vec3D.createVector(((double)this.rand.nextFloat() - 0.5D) * 0.1D, Math.random() * 0.1D + 0.1D, 0.0D);
				vec3D4.rotateAroundX(-this.rotationPitch * (float)Math.PI / 180.0F);
				vec3D4.rotateAroundY(-this.rotationYaw * (float)Math.PI / 180.0F);
				Vec3D vec3D5 = Vec3D.createVector(((double)this.rand.nextFloat() - 0.5D) * 0.3D, (double)(-this.rand.nextFloat()) * 0.6D - 0.3D, 0.6D);
				vec3D5.rotateAroundX(-this.rotationPitch * (float)Math.PI / 180.0F);
				vec3D5.rotateAroundY(-this.rotationYaw * (float)Math.PI / 180.0F);
				vec3D5 = vec3D5.addVector(this.posX, this.posY + (double)this.getEyeHeight(), this.posZ);
				this.worldObj.spawnParticle("iconcrack_" + itemStack1.getItem().shiftedIndex, vec3D5.xCoord, vec3D5.yCoord, vec3D5.zCoord, vec3D4.xCoord, vec3D4.yCoord + 0.05D, vec3D4.zCoord);
			}

			this.worldObj.playSoundAtEntity(this, "random.eat", 0.5F + 0.5F * (float)this.rand.nextInt(2), (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
		}

	}

	protected void onItemUseFinish() {
		if(this.itemInUse != null) {
			this.updateItemUse(this.itemInUse, 16);
			int i1 = this.itemInUse.stackSize;
			ItemStack itemStack2 = this.itemInUse.onFoodEaten(this.worldObj, this);
			if(itemStack2 != this.itemInUse || itemStack2 != null && itemStack2.stackSize != i1) {
				this.inventory.mainInventory[this.inventory.currentItem] = itemStack2;
				if(itemStack2.stackSize == 0) {
					this.inventory.mainInventory[this.inventory.currentItem] = null;
				}
			}

			this.clearItemInUse();
		}

	}

	public void handleHealthUpdate(byte b1) {
		if(b1 == 9) {
			this.onItemUseFinish();
		} else {
			super.handleHealthUpdate(b1);
		}

	}

	protected boolean isMovementBlocked() {
		return this.getHealth() <= 0 || this.isPlayerSleeping();
	}

	protected void closeScreen() {
		this.craftingInventory = this.inventorySlots;
	}

	public void updateCloak() {
		this.playerCloakUrl = "http://s3.amazonaws.com/MinecraftCloaks/" + this.username + ".png";
		this.cloakUrl = this.playerCloakUrl;
	}

	public void updateRidden() {
		super.updateRidden();
		this.prevCameraYaw = this.cameraYaw;
		this.cameraYaw = 0.0F;
	}

	public void preparePlayerToSpawn() {
		this.yOffset = 1.62F;
		this.setSize(0.6F, 1.8F);
		super.preparePlayerToSpawn();
		this.setEntityHealth(this.getMaxHealth());
		this.deathTime = 0;
	}

	private int getSwingSpeedModifier() {
		int sm = this.inventory.getSwingSpeed();
		// TODO :do things
		return sm;
	}

	protected void updateEntityActionState() {
		int i1 = this.getSwingSpeedModifier();
		if(this.isSwinging) {
			++this.swingProgressInt;
			if(this.swingProgressInt >= i1) {
				this.swingProgressInt = 0;
				this.isSwinging = false;
			}
		} else {
			this.swingProgressInt = 0;
		}

		this.swingProgress = (float)this.swingProgressInt / (float)i1;
	}

	public void onLivingUpdate() {
		if(this.flyToggleTimer > 0) {
			--this.flyToggleTimer;
		}

		if(this.worldObj.difficultySetting == 0 && this.getHealth() < this.getMaxHealth() && this.ticksExisted % 20 * 12 == 0) {
			this.heal(1);
		}

		this.inventory.decrementAnimations();
		this.prevCameraYaw = this.cameraYaw;
		super.onLivingUpdate();
		this.landMovementFactor = this.speedOnGround;
		this.jumpMovementFactor = this.speedInAir;
		if(this.isSprinting()) {
			this.landMovementFactor = (float)((double)this.landMovementFactor + (double)this.speedOnGround * 0.3D);
			this.jumpMovementFactor = (float)((double)this.jumpMovementFactor + (double)this.speedInAir * 0.3D);
		}

		float f1 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
		float f2 = (float)Math.atan(-this.motionY * (double)0.2F) * 15.0F;
		if(f1 > 0.1F) {
			f1 = 0.1F;
		}

		if(!this.onGround || this.getHealth() <= 0) {
			f1 = 0.0F;
		}

		if(this.onGround || this.getHealth() <= 0) {
			f2 = 0.0F;
		}

		this.cameraYaw += (f1 - this.cameraYaw) * 0.4F;
		this.cameraPitch += (f2 - this.cameraPitch) * 0.8F;
		if(this.getHealth() > 0) {
			List<Entity> list3 = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox.expand(1.0D, 0.0D, 1.0D));
			if(list3 != null) {
				for(int i4 = 0; i4 < list3.size(); ++i4) {
					Entity entity5 = (Entity)list3.get(i4);
					if(!entity5.isDead) {
						this.collideWithPlayer(entity5);
					}
				}
			}
		}

	}

	private void collideWithPlayer(Entity entity1) {
		entity1.onCollideWithPlayer(this);
	}

	public int getScore() {
		return this.score;
	}

	public void onDeath(DamageSource damageSource1) {
		super.onDeath(damageSource1);
		this.setSize(0.2F, 0.2F);
		this.setPosition(this.posX, this.posY, this.posZ);
		this.motionY = (double)0.1F;
		if(this.username.equals("Notch")) {
			this.dropPlayerItemWithRandomChoice(new ItemStack(Item.appleRed, 1), true);
		}

		this.inventory.dropAllItems();
		if(damageSource1 != null) {
			this.motionX = (double)(-MathHelper.cos((this.attackedAtYaw + this.rotationYaw) * (float)Math.PI / 180.0F) * 0.1F);
			this.motionZ = (double)(-MathHelper.sin((this.attackedAtYaw + this.rotationYaw) * (float)Math.PI / 180.0F) * 0.1F);
		} else {
			this.motionX = this.motionZ = 0.0D;
		}

		this.yOffset = 0.1F;

	}

	public void addToPlayerScore(Entity entity1, int i2) {
		this.score += i2;

	}

	protected int decreaseAirSupply(int i1) {
		int i2 = 0; // TODO: Add stuff to decrease air decrease
		return i2 > 0 && this.rand.nextInt(i2 + 1) > 0 ? i1 : super.decreaseAirSupply(i1);
	}

	public EntityItem dropOneItem() {
		return this.dropPlayerItemWithRandomChoice(this.inventory.decrStackSize(this.inventory.currentItem, 1), false);
	}

	public EntityItem dropPlayerItem(ItemStack itemStack1) {
		return this.dropPlayerItemWithRandomChoice(itemStack1, false);
	}

	public EntityItem dropPlayerItemWithRandomChoice(ItemStack itemStack1, boolean z2) {
		if(itemStack1 == null) {
			return null;
		} else {
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
			return entityItem3;
		}
	}

	protected void joinEntityItemWithWorld(EntityItem entityItem1) {
		this.worldObj.spawnEntityInWorld(entityItem1);
	}

	public float getCurrentPlayerStrVsBlock(Block block1, int metadata) {
		float f2 = this.inventory.getStrVsBlock(block1);
		float f3 = f2;
		int i4 = 0; // TODO: Add something to increase efficiency
		if(i4 > 0 && this.inventory.canHarvestBlock(block1, metadata)) {
			f3 = f2 + (float)(i4 * i4 + 1);
		}

		// TODO: Stuff to modify dig speed
		/*
		if(this.isPotionActive(Potion.digSpeed)) {
			f3 *= 1.0F + (float)(this.getActivePotionEffect(Potion.digSpeed).getAmplifier() + 1) * 0.2F;
		}

		if(this.isPotionActive(Potion.digSlowdown)) {
			f3 *= 1.0F - (float)(this.getActivePotionEffect(Potion.digSlowdown).getAmplifier() + 1) * 0.2F;
		}
		*/

		if(this.isInsideOfMaterial(Material.water) /* && !EnchantmentHelper.getAquaAffinityModifier(this.inventory)*/) {
			f3 /= 5.0F;
		}

		if(!this.onGround) {
			f3 /= 5.0F;
		}

		return f3;
	}

	public boolean canHarvestBlock(Block block1, int metadata) {
		return this.inventory.canHarvestBlock(block1, metadata);
	}

	public void readEntityFromNBT(NBTTagCompound compoundTag) {
		super.readEntityFromNBT(compoundTag);
		NBTTagList nBTTagList2 = compoundTag.getTagList("Inventory");
		this.inventory.readFromNBT(nBTTagList2);
		this.dimension = compoundTag.getInteger("Dimension");
		this.sleeping = compoundTag.getBoolean("Sleeping");
		this.sleepTimer = compoundTag.getShort("SleepTimer");
		this.experience = compoundTag.getFloat("XpP");
		this.experienceLevel = compoundTag.getInteger("XpLevel");
		this.experienceTotal = compoundTag.getInteger("XpTotal");
		if(this.sleeping) {
			this.playerLocation = new ChunkCoordinates(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ));
			this.wakeUpPlayer(true, true, false);
		}

		if(compoundTag.hasKey("SpawnX") && compoundTag.hasKey("SpawnY") && compoundTag.hasKey("SpawnZ")) {
			this.spawnChunk = new ChunkCoordinates(compoundTag.getInteger("SpawnX"), compoundTag.getInteger("SpawnY"), compoundTag.getInteger("SpawnZ"));
			this.dontCheckSpawnCoordinates = compoundTag.getBoolean("DontVerifySpawn");
		}
		this.spawnDimension = compoundTag.getInteger("SpawnDimension");

		this.foodStats.readNBT(compoundTag);
		this.capabilities.readCapabilitiesFromNBT(compoundTag);
		
	}

	public void writeEntityToNBT(NBTTagCompound compoundTag) {
		super.writeEntityToNBT(compoundTag);
		compoundTag.setTag("Inventory", this.inventory.writeToNBT(new NBTTagList()));
		compoundTag.setInteger("Dimension", this.dimension);
		compoundTag.setBoolean("Sleeping", this.sleeping);
		compoundTag.setShort("SleepTimer", (short)this.sleepTimer);
		compoundTag.setFloat("XpP", this.experience);
		compoundTag.setInteger("XpLevel", this.experienceLevel);
		compoundTag.setInteger("XpTotal", this.experienceTotal);
		if(this.spawnChunk != null) {
			compoundTag.setInteger("SpawnX", this.spawnChunk.posX);
			compoundTag.setInteger("SpawnY", this.spawnChunk.posY);
			compoundTag.setInteger("SpawnZ", this.spawnChunk.posZ);	
			compoundTag.setBoolean("DontVerifySpawn", this.dontCheckSpawnCoordinates);
		}
		compoundTag.setInteger("SpawnDimension", this.spawnDimension);

		this.foodStats.writeNBT(compoundTag);
		this.capabilities.writeCapabilitiesToNBT(compoundTag);
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

	public boolean attackEntityFrom(DamageSource damageSource1, int i2) {
		if(this.capabilities.disableDamage && !damageSource1.canHarmInCreative()) {
			return false;
		} else {
			this.entityAge = 0;
			if(this.getHealth() <= 0) {
				return false;
			} else {
				if(this.isPlayerSleeping() && !this.worldObj.isRemote) {
					this.wakeUpPlayer(true, true, false);
				}

				Entity entity3 = damageSource1.getEntity();
				if(entity3 instanceof EntityMob || entity3 instanceof EntityArrow) {
					if(this.worldObj.difficultySetting == 0) {
						i2 = 0;
					}

					if(this.worldObj.difficultySetting == 1) {
						i2 = i2 / 2 + 1;
					}

					if(this.worldObj.difficultySetting == 3) {
						i2 = i2 * 3 / 2;
					}
				}

				if(i2 == 0) {
					return false;
				} else {
					Entity entity4 = entity3;
					if(entity3 instanceof EntityArrow && ((EntityArrow)entity3).shootingEntity != null) {
						entity4 = ((EntityArrow)entity3).shootingEntity;
					}

					if(entity4 instanceof EntityLiving) {
						this.alertWolves((EntityLiving)entity4, false);
					}

					return super.attackEntityFrom(damageSource1, i2);
				}
			}
		}

	}

	/*
	protected int applyPotionDamageCalculations(DamageSource damageSource1, int i2) {
		int i3 = super.applyPotionDamageCalculations(damageSource1, i2);
		if(i3 <= 0) {
			return 0;
		} else {
			int i4 = EnchantmentHelper.getEnchantmentModifierDamage(this.inventory, damageSource1);
			if(i4 > 20) {
				i4 = 20;
			}

			if(i4 > 0 && i4 <= 20) {
				int i5 = 25 - i4;
				int i6 = i3 * i5 + this.carryoverDamage;
				i3 = i6 / 25;
				this.carryoverDamage = i6 % 25;
			}

			return i3;
		}
	}
	*/

	protected boolean isPVPEnabled() {
		return false;
	}

	protected void alertWolves(EntityLiving entityLiving1, boolean z2) {
		if(!(entityLiving1 instanceof EntityCreeper) && !(entityLiving1 instanceof EntityGhast)) {
			if(entityLiving1 instanceof EntityWolf) {
				EntityWolf entityWolf3 = (EntityWolf)entityLiving1;
				if(entityWolf3.isTamed() && this.username.equals(entityWolf3.getOwnerName())) {
					return;
				}
			}

			if(!(entityLiving1 instanceof EntityPlayer) || this.isPVPEnabled()) {
				List<Entity> list7 = this.worldObj.getEntitiesWithinAABB(EntityWolf.class, AxisAlignedBB.getBoundingBoxFromPool(this.posX, this.posY, this.posZ, this.posX + 1.0D, this.posY + 1.0D, this.posZ + 1.0D).expand(16.0D, 4.0D, 16.0D));
				Iterator<Entity> iterator4 = list7.iterator();

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
								} while(!entityWolf6.isTamed());
							} while(entityWolf6.getEntityToAttack() != null);
						} while(!this.username.equals(entityWolf6.getOwnerName()));
					} while(z2 && entityWolf6.isSitting());

					entityWolf6.setSitting(false);
					entityWolf6.setTarget(entityLiving1);
				}
			}
		}
	}

	protected void damageArmor(int i1) {
		this.inventory.damageArmor(i1);
	}

	public int getTotalArmorValue() {
		return this.inventory.getTotalArmorValue();
	}

	protected void damageEntity(DamageSource damageSource1, int i2) {
		if(!damageSource1.isUnblockable() && this.isBlocking()) {
			i2 = 1 + i2 >> 1;
		}

		i2 = this.applyArmorCalculations(damageSource1, i2);
		i2 = this.applyPotionDamageCalculations(damageSource1, i2);
		this.addExhaustion(damageSource1.getHungerDamage());
		this.health -= i2;
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
					itemStack2.onItemDestroyedByUse(this);
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
		if(!this.isSwinging || this.swingProgressInt >= this.getSwingSpeedModifier() / 2 || this.swingProgressInt < 0) {
			this.swingProgressInt = -1;
			this.isSwinging = true;
		}

	}

	public void attackTargetEntityWithCurrentItem(Entity entity1) {
		if(entity1.canAttackWithItem()) {
			int i2 = this.inventory.getDamageVsEntity(entity1);

			// Modified: items themselves can add knockback by themselves.
			float i3 = this.inventory.getExtraKnockbackVsEntity(entity1);
			int i4 = 0;

			if(this.isSprinting()) {
				++i3;
			}

			if(i2 > 0 || i4 > 0) {
				boolean z5 = this.fallDistance > 0.0F && !this.onGround && !this.isOnLadder() && !this.isInWater() && this.ridingEntity == null && entity1 instanceof EntityLiving;
				if(z5) {
					i2 += this.rand.nextInt(i2 / 2 + 2);
				}

				i2 += i4;
				boolean z6 = entity1.attackEntityFrom(DamageSource.causePlayerDamage(this), i2);
				if(z6) {
					if(i3 > 0) {
						entity1.addVelocity((double)(-MathHelper.sin(this.rotationYaw * (float)Math.PI / 180.0F) * (float)i3 * 0.5F), 0.1D, (double)(MathHelper.cos(this.rotationYaw * (float)Math.PI / 180.0F) * (float)i3 * 0.5F));
						this.motionX *= 0.6D;
						this.motionZ *= 0.6D;
						this.setSprinting(false);
					}

					if(z5) {
						this.onCriticalHit(entity1);
					}

					if(i4 > 0) {
						this.onEnchantmentCritical(entity1);
					}

					this.setLastAttackingEntity(entity1);
				}

				ItemStack itemStack7 = this.getCurrentEquippedItem();
				if(itemStack7 != null && entity1 instanceof EntityLiving) {
					itemStack7.hitEntity((EntityLiving)entity1, this);
					if(itemStack7.stackSize <= 0) {
						itemStack7.onItemDestroyedByUse(this);
						this.destroyCurrentEquippedItem();
					}
				}

				if(entity1 instanceof EntityLiving) {
					if(entity1.isEntityAlive()) {
						this.alertWolves((EntityLiving)entity1, true);
					}

					// TODO: Fire shit
					/*
					int i8 = EnchantmentHelper.getFireAspectModifier(this.inventory, (EntityLiving)entity1);
					if(i8 > 0) {
						entity1.setFire(i8 * 4);
					}
					*/
				}

				this.addExhaustion(0.3F);
			}

		}
	}

	public void onCriticalHit(Entity entity1) {
	}

	public void onEnchantmentCritical(Entity entity1) {
	}

	public void respawnPlayer() {
	}

	public abstract void func_6420_o();

	public void onItemStackChanged(ItemStack itemStack1) {
	}

	public void setDead() {
		super.setDead();
		this.inventorySlots.onCraftGuiClosed(this);
		if(this.craftingInventory != null) {
			this.craftingInventory.onCraftGuiClosed(this);
		}

	}

	public boolean isEntityInsideOpaqueBlock() {
		return !this.sleeping && super.isEntityInsideOpaqueBlock();
	}

	public EnumStatus sleepInBedAt(int i1, int i2, int i3) {
		if(!this.worldObj.isRemote) {
			if(this.isPlayerSleeping() || !this.isEntityAlive()) {
				return EnumStatus.OTHER_PROBLEM;
			}

			if(!this.worldObj.worldProvider.canSleepHere()) {
				return EnumStatus.NOT_POSSIBLE_HERE;
			}

			if(this.worldObj.isDaytime()) {
				return EnumStatus.NOT_POSSIBLE_NOW;
			}

			if(Math.abs(this.posX - (double)i1) > 3.0D || Math.abs(this.posY - (double)i2) > 2.0D || Math.abs(this.posZ - (double)i3) > 3.0D) {
				return EnumStatus.TOO_FAR_AWAY;
			}

			double d4 = 8.0D;
			double d6 = 5.0D;
			List<Entity> list8 = this.worldObj.getEntitiesWithinAABB(EntityMob.class, AxisAlignedBB.getBoundingBoxFromPool((double)i1 - d4, (double)i2 - d6, (double)i3 - d4, (double)i1 + d4, (double)i2 + d6, (double)i3 + d4));
			if(!list8.isEmpty()) {
				return EnumStatus.NOT_SAFE;
			}
		}

		this.setSize(0.2F, 0.2F);
		this.yOffset = 0.2F;
		if(this.worldObj.blockExists(i1, i2, i3)) {
			int i9 = this.worldObj.getBlockMetadata(i1, i2, i3);
			int i5 = BlockBed.getDirection(i9);
			float f10 = 0.5F;
			float f7 = 0.5F;
			switch(i5) {
			case 0:
				f7 = 0.9F;
				break;
			case 1:
				f10 = 0.1F;
				break;
			case 2:
				f7 = 0.1F;
				break;
			case 3:
				f10 = 0.9F;
			}

			this.func_22052_e(i5);
			this.setPosition((double)((float)i1 + f10), (double)((float)i2 + 0.9375F), (double)((float)i3 + f7));
		} else {
			this.setPosition((double)((float)i1 + 0.5F), (double)((float)i2 + 0.9375F), (double)((float)i3 + 0.5F));
		}

		this.sleeping = true;
		this.sleepTimer = 0;
		this.playerLocation = new ChunkCoordinates(i1, i2, i3);
		this.motionX = this.motionZ = this.motionY = 0.0D;
		if(!this.worldObj.isRemote) {
			this.worldObj.updateAllPlayersSleepingFlag();
		}

		return EnumStatus.OK;
	}

	private void func_22052_e(int i1) {
		this.field_22063_x = 0.0F;
		this.field_22061_z = 0.0F;
		switch(i1) {
		case 0:
			this.field_22061_z = -1.8F;
			break;
		case 1:
			this.field_22063_x = 1.8F;
			break;
		case 2:
			this.field_22061_z = 1.8F;
			break;
		case 3:
			this.field_22063_x = -1.8F;
		}

	}

	public void wakeUpPlayer(boolean z1, boolean z2, boolean z3) {
		this.setSize(0.6F, 1.8F);
		this.resetHeight();
		ChunkCoordinates chunkCoordinates4 = this.playerLocation;
		ChunkCoordinates chunkCoordinates5 = this.playerLocation;
		if(chunkCoordinates4 != null && this.worldObj.getBlockId(chunkCoordinates4.posX, chunkCoordinates4.posY, chunkCoordinates4.posZ) == Block.bed.blockID) {
			BlockBed.setBedOccupied(this.worldObj, chunkCoordinates4.posX, chunkCoordinates4.posY, chunkCoordinates4.posZ, false);
			chunkCoordinates5 = BlockBed.getNearestEmptyChunkCoordinates(this.worldObj, chunkCoordinates4.posX, chunkCoordinates4.posY, chunkCoordinates4.posZ, 0);
			if(chunkCoordinates5 == null) {
				chunkCoordinates5 = new ChunkCoordinates(chunkCoordinates4.posX, chunkCoordinates4.posY + 1, chunkCoordinates4.posZ);
			}

			this.setPosition((double)((float)chunkCoordinates5.posX + 0.5F), (double)((float)chunkCoordinates5.posY + this.yOffset + 0.1F), (double)((float)chunkCoordinates5.posZ + 0.5F));
		}

		this.sleeping = false;
		if(!this.worldObj.isRemote && z2) {
			this.worldObj.updateAllPlayersSleepingFlag();
		}

		if(z1) {
			this.sleepTimer = 0;
		} else {
			this.sleepTimer = 100;
		}

		if(z3) {
			this.setSpawnChunk(this.playerLocation);
			this.setSpawnDimension(this.dimension);
			this.setDontCheckSpawnCoordinates(false);
		}

	}

	private boolean isInBed() {
		return this.worldObj.getBlockId(this.playerLocation.posX, this.playerLocation.posY, this.playerLocation.posZ) == Block.bed.blockID;
	}

	public static ChunkCoordinates verifyRespawnCoordinates(World world0, ChunkCoordinates chunkCoordinates1) {
		IChunkProvider iChunkProvider2 = world0.getChunkProvider();
		iChunkProvider2.loadChunk(chunkCoordinates1.posX - 3 >> 4, chunkCoordinates1.posZ - 3 >> 4);
		iChunkProvider2.loadChunk(chunkCoordinates1.posX + 3 >> 4, chunkCoordinates1.posZ - 3 >> 4);
		iChunkProvider2.loadChunk(chunkCoordinates1.posX - 3 >> 4, chunkCoordinates1.posZ + 3 >> 4);
		iChunkProvider2.loadChunk(chunkCoordinates1.posX + 3 >> 4, chunkCoordinates1.posZ + 3 >> 4);
		if(world0.getBlockId(chunkCoordinates1.posX, chunkCoordinates1.posY, chunkCoordinates1.posZ) != Block.bed.blockID) {
			return null;
		} else {
			ChunkCoordinates chunkCoordinates3 = BlockBed.getNearestEmptyChunkCoordinates(world0, chunkCoordinates1.posX, chunkCoordinates1.posY, chunkCoordinates1.posZ, 0);
			return chunkCoordinates3;
		}
	}

	public float getBedOrientationInDegrees() {
		if(this.playerLocation != null) {
			int i1 = this.worldObj.getBlockMetadata(this.playerLocation.posX, this.playerLocation.posY, this.playerLocation.posZ);
			int i2 = BlockBed.getDirection(i1);
			switch(i2) {
			case 0:
				return 90.0F;
			case 1:
				return 0.0F;
			case 2:
				return 270.0F;
			case 3:
				return 180.0F;
			}
		}

		return 0.0F;
	}
	
	public boolean isPlayerSleeping() {
		return this.sleeping;
	}

	public boolean isPlayerFullyAsleep() {
		return this.sleeping && this.sleepTimer >= 100;
	}

	public int getSleepTimer() {
		return this.sleepTimer;
	}

	public void addChatMessage(String string1) {
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

	protected void jump() {
		super.jump();
		if(this.isSprinting()) {
			this.addExhaustion(0.8F);
		} else {
			this.addExhaustion(0.2F);
		}

	}

	public void moveEntityWithHeading(float f1, float f2) {
		if(this.capabilities.isFlying) {
			double d9 = this.motionY;
			float f11 = this.jumpMovementFactor;
			this.jumpMovementFactor = 0.05F;
			super.moveEntityWithHeading(f1, f2);
			this.motionY = d9 * 0.6D;
			this.jumpMovementFactor = f11;
		} else {
			super.moveEntityWithHeading(f1, f2);
		}
	}

	protected void fall(float f1) {
		if(!this.capabilities.allowFlying) {
			super.fall(f1);
		}
	}

	public int getItemIcon(ItemStack itemStack1, int i2) {
		int i3 = super.getItemIcon(itemStack1, i2);
		if(itemStack1.itemID == Item.fishingRod.shiftedIndex && this.fishEntity != null) {
			i3 = itemStack1.getIconIndex() + 16;
		} else {
			if(itemStack1.getItem().requiresMultipleRenderPasses()) {
				return itemStack1.getItem().getIconFromDamageAndRenderpass(itemStack1.getItemDamage(), i2);
			}

			if(this.itemInUse != null && itemStack1.itemID == Item.bow.shiftedIndex) {
				int i4 = itemStack1.getMaxItemUseDuration() - this.itemInUseCount;
				if(i4 >= 18) {
					return 133;
				}

				if(i4 > 13) {
					return 117;
				}

				if(i4 > 0) {
					return 101;
				}
			}
		}

		return i3;
	}

	public void setInPortal() {
		if(this.timeUntilPortal > 0) {
			this.timeUntilPortal = 10;
		} else {
			this.inPortal = true;
		}
	}

	public void addExperience(int i1) {
		this.score += i1;
		int i2 = Integer.MAX_VALUE - this.experienceTotal;
		if(i1 > i2) {
			i1 = i2;
		}

		this.experience += (float)i1 / (float)this.xpBarCap();

		for(this.experienceTotal += i1; this.experience >= 1.0F; this.experience /= (float)this.xpBarCap()) {
			this.experience = (this.experience - 1.0F) * (float)this.xpBarCap();
			this.increaseLevel();
		}

	}

	public void removeExperience(int i1) {
		this.experienceLevel -= i1;
		if(this.experienceLevel < 0) {
			this.experienceLevel = 0;
		}

	}

	public int xpBarCap() {
		return 7 + (this.experienceLevel * 7 >> 1);
	}

	private void increaseLevel() {
		++this.experienceLevel;
	}

	public void addExhaustion(float f1) {
		if(!this.capabilities.disableDamage) {
			if(!this.worldObj.isRemote) {
				this.foodStats.addExhaustion(f1);
			}

		}
	}

	public FoodStats getFoodStats() {
		return this.foodStats;
	}

	public boolean canEat(boolean z1) {
		if (GameRules.boolRule("enableHunger")) {
			return (z1 || this.foodStats.needFood()) && !this.capabilities.disableDamage;
		} else {
			return (z1 || this.health < this.getMaxHealth()) && !this.capabilities.disableDamage;
		}
	}

	public boolean shouldHeal() {
		return this.getHealth() > 0 && this.getHealth() < this.getMaxHealth();
	}

	public void setItemInUse(ItemStack itemStack1, int i2) {
		if(itemStack1 != this.itemInUse) {
			this.itemInUse = itemStack1;
			this.itemInUseCount = i2;
			if(!this.worldObj.isRemote) {
				this.setEating(true);
			}

		}
	}

	public boolean canPlayerEdit(int i1, int i2, int i3) {
		return true;
	}

	protected int getExperiencePoints(EntityPlayer entityPlayer1) {
		int i2 = this.experienceLevel * 7;
		return i2 > 100 ? 100 : i2;
	}

	protected boolean isPlayer() {
		return true;
	}

	public String getUsername() {
		return this.username;
	}

	public void travelToTheEnd(int i1) {
	}

	public void copyPlayer(EntityPlayer entityPlayer1) {
		this.inventory.copyInventory(entityPlayer1.inventory);
		this.health = entityPlayer1.health;
		this.foodStats = entityPlayer1.foodStats;
		this.experienceLevel = entityPlayer1.experienceLevel;
		this.experienceTotal = entityPlayer1.experienceTotal;
		this.experience = entityPlayer1.experience;
		this.score = entityPlayer1.score;
	}

	protected boolean canTriggerWalking() {
		return !this.capabilities.isFlying;
	}

	public void func_50009_aI() {
	}

	public int getSpawnDimension() {
		return spawnDimension;
	}

	public void setSpawnDimension(int spawnDimension) {
		this.spawnDimension = spawnDimension;
	}
	
	public void displayGUIRecipeBook(ItemStack itemStack) {		
	}
	
	public boolean wearingGold() {
		ItemStack itemStack;
		itemStack = this.inventory.armorItemInSlot(3); if(itemStack != null && itemStack.itemID == Item.helmetGold.shiftedIndex) return true;
		itemStack = this.inventory.armorItemInSlot(2); if(itemStack != null && itemStack.itemID == Item.plateGold.shiftedIndex) return true;
		itemStack = this.inventory.armorItemInSlot(1); if(itemStack != null && itemStack.itemID == Item.legsGold.shiftedIndex) return true;
		itemStack = this.inventory.armorItemInSlot(0); if(itemStack != null && itemStack.itemID == Item.bootsGold.shiftedIndex) return true;
		return false;
	}
	
	public ItemStack getCurrentArmor(int par1) {
		return this.inventory.armorItemInSlot(par1);
	}
	
	public boolean isDontCheckSpawnCoordinates() {
		return dontCheckSpawnCoordinates;
	}
	
	public void setDontCheckSpawnCoordinates(boolean dontCheckSpawnCoordinates) {
		this.dontCheckSpawnCoordinates = dontCheckSpawnCoordinates;
	}
	
	public ChunkCoordinates getPlayerCoordinates() {
		return new ChunkCoordinates((int)this.posX, (int)this.posY, (int)this.posZ);
	}
}
