package net.minecraft.src;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import net.minecraft.server.MinecraftServer;

public class EntityPlayerMP extends EntityPlayer implements ICrafting {
	public NetServerHandler playerNetServerHandler;
	public MinecraftServer mcServer;
	public ItemInWorldManager itemInWorldManager;
	public double field_9155_d;
	public double field_9154_e;
	public List loadedChunks = new LinkedList();
	public Set field_420_ah = new HashSet();
	private int lastHealth = -99999999;
	private int ticksOfInvuln = 60;
	private ItemStack[] playerInventory = new ItemStack[]{null, null, null, null, null};
	private int currentWindowId = 0;
	public boolean isChangingQuantityOnly;

	public EntityPlayerMP(MinecraftServer minecraftServer1, World world2, String string3, ItemInWorldManager itemInWorldManager4) {
		super(world2);
		itemInWorldManager4.thisPlayer = this;
		this.itemInWorldManager = itemInWorldManager4;
		ChunkCoordinates chunkCoordinates5 = world2.getSpawnPoint();
		int i6 = chunkCoordinates5.posX;
		int i7 = chunkCoordinates5.posZ;
		int i8 = chunkCoordinates5.posY;
		if(!world2.worldProvider.field_4306_c) {
			i6 += this.rand.nextInt(20) - 10;
			i8 = world2.findTopSolidBlock(i6, i7);
			i7 += this.rand.nextInt(20) - 10;
		}

		this.setLocationAndAngles((double)i6 + 0.5D, (double)i8, (double)i7 + 0.5D, 0.0F, 0.0F);
		this.mcServer = minecraftServer1;
		this.stepHeight = 0.0F;
		this.username = string3;
		this.yOffset = 0.0F;
	}

	public void setWorldHandler(World world1) {
		super.setWorldHandler(world1);
		this.itemInWorldManager = new ItemInWorldManager((WorldServer)world1);
		this.itemInWorldManager.thisPlayer = this;
	}

	public void func_20057_k() {
		this.currentCraftingInventory.onCraftGuiOpened(this);
	}

	public ItemStack[] getInventory() {
		return this.playerInventory;
	}

	protected void resetHeight() {
		this.yOffset = 0.0F;
	}

	public float getEyeHeight() {
		return 1.62F;
	}

	public void onUpdate() {
		this.itemInWorldManager.func_328_a();
		--this.ticksOfInvuln;
		this.currentCraftingInventory.updateCraftingMatrix();

		for(int i1 = 0; i1 < 5; ++i1) {
			ItemStack itemStack2 = this.getEquipmentInSlot(i1);
			if(itemStack2 != this.playerInventory[i1]) {
				this.mcServer.getEntityTracker(this.dimension).sendPacketToTrackedPlayers(this, new Packet5PlayerInventory(this.entityId, i1, itemStack2));
				this.playerInventory[i1] = itemStack2;
			}
		}

	}

	public ItemStack getEquipmentInSlot(int i1) {
		return i1 == 0 ? this.inventory.getCurrentItem() : this.inventory.armorInventory[i1 - 1];
	}

	public void onDeath(Entity entity1) {
		this.inventory.dropAllItems();
	}

	public boolean attackEntityFrom(Entity entity1, int i2) {
		if(this.ticksOfInvuln > 0) {
			return false;
		} else {
			if(!this.mcServer.pvpOn) {
				if(entity1 instanceof EntityPlayer) {
					return false;
				}

				if(entity1 instanceof EntityArrow) {
					EntityArrow entityArrow3 = (EntityArrow)entity1;
					if(entityArrow3.owner instanceof EntityPlayer) {
						return false;
					}
				}
			}

			return super.attackEntityFrom(entity1, i2);
		}
	}

	protected boolean isPVPEnabled() {
		return this.mcServer.pvpOn;
	}

	public void heal(int i1) {
		super.heal(i1);
	}

	public void onUpdateEntity(boolean z1) {
		super.onUpdate();

		for(int i2 = 0; i2 < this.inventory.getSizeInventory(); ++i2) {
			ItemStack itemStack3 = this.inventory.getStackInSlot(i2);
			if(itemStack3 != null && Item.itemsList[itemStack3.itemID].func_28019_b() && this.playerNetServerHandler.getNumChunkDataPackets() <= 2) {
				Packet packet4 = ((ItemMapBase)Item.itemsList[itemStack3.itemID]).func_28022_b(itemStack3, this.worldObj, this);
				if(packet4 != null) {
					this.playerNetServerHandler.sendPacket(packet4);
				}
			}
		}

		if(z1 && !this.loadedChunks.isEmpty()) {
			ChunkCoordIntPair chunkCoordIntPair7 = (ChunkCoordIntPair)this.loadedChunks.get(0);
			if(chunkCoordIntPair7 != null) {
				boolean z8 = false;
				if(this.playerNetServerHandler.getNumChunkDataPackets() < 4) {
					z8 = true;
				}

				if(z8) {
					WorldServer worldServer9 = this.mcServer.getWorldManager(this.dimension);
					this.loadedChunks.remove(chunkCoordIntPair7);
					this.playerNetServerHandler.sendPacket(new Packet51MapChunk(chunkCoordIntPair7.chunkXPos * 16, 0, chunkCoordIntPair7.chunkZPos * 16, 16, 128, 16, worldServer9));
					List list5 = worldServer9.getTileEntityList(chunkCoordIntPair7.chunkXPos * 16, 0, chunkCoordIntPair7.chunkZPos * 16, chunkCoordIntPair7.chunkXPos * 16 + 16, 128, chunkCoordIntPair7.chunkZPos * 16 + 16);

					for(int i6 = 0; i6 < list5.size(); ++i6) {
						this.getTileEntityInfo((TileEntity)list5.get(i6));
					}
				}
			}
		}

		if(this.field_28015_D) {
			if(this.mcServer.propertyManagerObj.getBooleanProperty("allow-nether", true)) {
				if(this.ridingEntity != null) {
					this.mountEntity(this.ridingEntity);
				} else {
					this.field_28014_E += 0.0125F;
					if(this.field_28014_E >= 1.0F) {
						this.field_28014_E = 1.0F;
						this.field_28016_C = 10;
						this.mcServer.configManager.sendPlayerToOtherDimension(this);
					}
				}

				this.field_28015_D = false;
			}
		} else {
			if(this.field_28014_E > 0.0F) {
				this.field_28014_E -= 0.05F;
			}

			if(this.field_28014_E < 0.0F) {
				this.field_28014_E = 0.0F;
			}
		}

		if(this.field_28016_C > 0) {
			--this.field_28016_C;
		}

		if(this.health != this.lastHealth) {
			this.playerNetServerHandler.sendPacket(new Packet8UpdateHealth(this.health));
			this.lastHealth = this.health;
		}

	}

	private void getTileEntityInfo(TileEntity tileEntity1) {
		if(tileEntity1 != null) {
			Packet packet2 = tileEntity1.getDescriptionPacket();
			if(packet2 != null) {
				this.playerNetServerHandler.sendPacket(packet2);
			}
		}

	}

	public void onLivingUpdate() {
		super.onLivingUpdate();
	}

	public void onItemPickup(Entity entity1, int i2) {
		if(!entity1.isDead) {
			EntityTracker entityTracker3 = this.mcServer.getEntityTracker(this.dimension);
			if(entity1 instanceof EntityItem) {
				entityTracker3.sendPacketToTrackedPlayers(entity1, new Packet22Collect(entity1.entityId, this.entityId));
			}

			if(entity1 instanceof EntityArrow) {
				entityTracker3.sendPacketToTrackedPlayers(entity1, new Packet22Collect(entity1.entityId, this.entityId));
			}
		}

		super.onItemPickup(entity1, i2);
		this.currentCraftingInventory.updateCraftingMatrix();
	}

	public void swingItem() {
		if(!this.isSwinging) {
			this.swingProgressInt = -1;
			this.isSwinging = true;
			EntityTracker entityTracker1 = this.mcServer.getEntityTracker(this.dimension);
			entityTracker1.sendPacketToTrackedPlayers(this, new Packet18Animation(this, 1));
		}

	}

	public void func_22068_s() {
	}

	public EnumStatus goToSleep(int i1, int i2, int i3) {
		EnumStatus enumStatus4 = super.goToSleep(i1, i2, i3);
		if(enumStatus4 == EnumStatus.OK) {
			EntityTracker entityTracker5 = this.mcServer.getEntityTracker(this.dimension);
			Packet17Sleep packet17Sleep6 = new Packet17Sleep(this, 0, i1, i2, i3);
			entityTracker5.sendPacketToTrackedPlayers(this, packet17Sleep6);
			this.playerNetServerHandler.teleportTo(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
			this.playerNetServerHandler.sendPacket(packet17Sleep6);
		}

		return enumStatus4;
	}

	public void wakeUpPlayer(boolean z1, boolean z2, boolean z3) {
		if(this.func_30001_K()) {
			EntityTracker entityTracker4 = this.mcServer.getEntityTracker(this.dimension);
			entityTracker4.sendPacketToTrackedPlayersAndTrackedEntity(this, new Packet18Animation(this, 3));
		}

		super.wakeUpPlayer(z1, z2, z3);
		if(this.playerNetServerHandler != null) {
			this.playerNetServerHandler.teleportTo(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
		}

	}

	public void mountEntity(Entity entity1) {
		super.mountEntity(entity1);
		this.playerNetServerHandler.sendPacket(new Packet39AttachEntity(this, this.ridingEntity));
		this.playerNetServerHandler.teleportTo(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
	}

	protected void updateFallState(double d1, boolean z3) {
	}

	public void handleFalling(double d1, boolean z3) {
		super.updateFallState(d1, z3);
	}

	private void getNextWidowId() {
		this.currentWindowId = this.currentWindowId % 100 + 1;
	}

	public void displayWorkbenchGUI(int i1, int i2, int i3) {
		this.getNextWidowId();
		this.playerNetServerHandler.sendPacket(new Packet100OpenWindow(this.currentWindowId, 1, "Crafting", 9));
		this.currentCraftingInventory = new ContainerWorkbench(this.inventory, this.worldObj, i1, i2, i3);
		this.currentCraftingInventory.windowId = this.currentWindowId;
		this.currentCraftingInventory.onCraftGuiOpened(this);
	}

	public void displayGUIChest(IInventory iInventory1) {
		this.getNextWidowId();
		this.playerNetServerHandler.sendPacket(new Packet100OpenWindow(this.currentWindowId, 0, iInventory1.getInvName(), iInventory1.getSizeInventory()));
		this.currentCraftingInventory = new ContainerChest(this.inventory, iInventory1);
		this.currentCraftingInventory.windowId = this.currentWindowId;
		this.currentCraftingInventory.onCraftGuiOpened(this);
	}

	public void displayGUIFurnace(TileEntityFurnace tileEntityFurnace1) {
		this.getNextWidowId();
		this.playerNetServerHandler.sendPacket(new Packet100OpenWindow(this.currentWindowId, 2, tileEntityFurnace1.getInvName(), tileEntityFurnace1.getSizeInventory()));
		this.currentCraftingInventory = new ContainerFurnace(this.inventory, tileEntityFurnace1);
		this.currentCraftingInventory.windowId = this.currentWindowId;
		this.currentCraftingInventory.onCraftGuiOpened(this);
	}

	public void displayGUIDispenser(TileEntityDispenser tileEntityDispenser1) {
		this.getNextWidowId();
		this.playerNetServerHandler.sendPacket(new Packet100OpenWindow(this.currentWindowId, 3, tileEntityDispenser1.getInvName(), tileEntityDispenser1.getSizeInventory()));
		this.currentCraftingInventory = new ContainerDispenser(this.inventory, tileEntityDispenser1);
		this.currentCraftingInventory.windowId = this.currentWindowId;
		this.currentCraftingInventory.onCraftGuiOpened(this);
	}

	public void updateCraftingInventorySlot(Container container1, int i2, ItemStack itemStack3) {
		if(!(container1.getSlot(i2) instanceof SlotCrafting)) {
			if(!this.isChangingQuantityOnly) {
				this.playerNetServerHandler.sendPacket(new Packet103SetSlot(container1.windowId, i2, itemStack3));
			}
		}
	}

	public void func_28017_a(Container container1) {
		this.updateCraftingInventory(container1, container1.func_28127_b());
	}

	public void updateCraftingInventory(Container container1, List list2) {
		this.playerNetServerHandler.sendPacket(new Packet104WindowItems(container1.windowId, list2));
		this.playerNetServerHandler.sendPacket(new Packet103SetSlot(-1, -1, this.inventory.getItemStack()));
	}

	public void updateCraftingInventoryInfo(Container container1, int i2, int i3) {
		this.playerNetServerHandler.sendPacket(new Packet105UpdateProgressbar(container1.windowId, i2, i3));
	}

	public void onItemStackChanged(ItemStack itemStack1) {
	}

	public void usePersonalCraftingInventory() {
		this.playerNetServerHandler.sendPacket(new Packet101CloseWindow(this.currentCraftingInventory.windowId));
		this.closeCraftingGui();
	}

	public void updateHeldItem() {
		if(!this.isChangingQuantityOnly) {
			this.playerNetServerHandler.sendPacket(new Packet103SetSlot(-1, -1, this.inventory.getItemStack()));
		}
	}

	public void closeCraftingGui() {
		this.currentCraftingInventory.onCraftGuiClosed(this);
		this.currentCraftingInventory = this.personalCraftingInventory;
	}

	public void setMovementType(float f1, float f2, boolean z3, boolean z4, float f5, float f6) {
		this.moveStrafing = f1;
		this.moveForward = f2;
		this.isJumping = z3;
		this.func_21043_b(z4);
		this.rotationPitch = f5;
		this.rotationYaw = f6;
	}

	public void addStat(StatBase statBase1, int i2) {
		if(statBase1 != null) {
			if(!statBase1.field_27058_g) {
				while(i2 > 100) {
					this.playerNetServerHandler.sendPacket(new Packet200Statistic(statBase1.statId, 100));
					i2 -= 100;
				}

				this.playerNetServerHandler.sendPacket(new Packet200Statistic(statBase1.statId, i2));
			}

		}
	}

	public void func_30003_A() {
		if(this.ridingEntity != null) {
			this.mountEntity(this.ridingEntity);
		}

		if(this.riddenByEntity != null) {
			this.riddenByEntity.mountEntity(this);
		}

		if(this.sleeping) {
			this.wakeUpPlayer(true, false, false);
		}

	}

	public void func_30002_B() {
		this.lastHealth = -99999999;
	}

	public void func_22061_a(String string1) {
		StringTranslate stringTranslate2 = StringTranslate.getInstance();
		String string3 = stringTranslate2.translateKey(string1);
		this.playerNetServerHandler.sendPacket(new Packet3Chat(string3));
	}
}
