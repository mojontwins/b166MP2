package net.minecraft.server.player;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.mojang.nbt.NBTTagCompound;

import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet100OpenWindow;
import net.minecraft.network.packet.Packet101CloseWindow;
import net.minecraft.network.packet.Packet103SetSlot;
import net.minecraft.network.packet.Packet104WindowItems;
import net.minecraft.network.packet.Packet105UpdateProgressbar;
import net.minecraft.network.packet.Packet17Sleep;
import net.minecraft.network.packet.Packet18Animation;
import net.minecraft.network.packet.Packet202PlayerAbilities;
import net.minecraft.network.packet.Packet22Collect;
import net.minecraft.network.packet.Packet38EntityStatus;
import net.minecraft.network.packet.Packet39AttachEntity;
import net.minecraft.network.packet.Packet3Chat;
import net.minecraft.network.packet.Packet43Experience;
import net.minecraft.network.packet.Packet51MapChunk;
import net.minecraft.network.packet.Packet5PlayerInventory;
import net.minecraft.network.packet.Packet70GameEvent;
import net.minecraft.network.packet.Packet8UpdateHealth;
import net.minecraft.server.ItemInWorldManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldServer;
import net.minecraft.server.network.EntityTracker;
import net.minecraft.server.network.NetServerHandler;
import net.minecraft.util.StringTranslate;
import net.minecraft.world.entity.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDamageSource;
import net.minecraft.world.entity.EnumAction;
import net.minecraft.world.entity.item.EntityItem;
import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.entity.player.EnumStatus;
import net.minecraft.world.entity.projectile.EntityArrow;
import net.minecraft.world.inventory.Container;
import net.minecraft.world.inventory.ContainerChest;
import net.minecraft.world.inventory.ContainerFurnace;
import net.minecraft.world.inventory.ContainerWorkbench;
import net.minecraft.world.inventory.ICrafting;
import net.minecraft.world.inventory.IInventory;
import net.minecraft.world.inventory.SlotCrafting;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.map.ItemMapBase;
import net.minecraft.world.level.World;
import net.minecraft.world.level.chunk.Chunk;
import net.minecraft.world.level.chunk.ChunkCoordIntPair;
import net.minecraft.world.level.chunk.ChunkCoordinates;
import net.minecraft.world.level.tile.entity.TileEntity;
import net.minecraft.world.level.tile.entity.TileEntityFurnace;

public class EntityPlayerMP extends EntityPlayer implements ICrafting {
	public NetServerHandler playerNetServerHandler;
	public MinecraftServer mcServer;
	public ItemInWorldManager itemInWorldManager;
	public double managedPosX;
	public double managedPosZ;
	public List<ChunkCoordIntPair> loadedChunks = new LinkedList<ChunkCoordIntPair>();
	public Set<ChunkCoordIntPair> listeningChunks = new HashSet<ChunkCoordIntPair>();
	private int lastHealth = -99999999;
	private int s_field_35221_cc = -99999999;
	private boolean s_field_35222_cd = true;
	private int lastExperience = -99999999;
	private int ticksOfInvuln = 60;
	private ItemStack[] playerInventory = new ItemStack[]{null, null, null, null, null};
	private int currentWindowId = 0;
	public boolean isChangingQuantityOnly;
	public int ping;
	public boolean gameOver = false;

	public EntityPlayerMP(MinecraftServer minecraftServer1, World world2, String string3, ItemInWorldManager itemInWorldManager4) {
		super(world2);
		itemInWorldManager4.thisPlayer = this;
		this.itemInWorldManager = itemInWorldManager4;
		ChunkCoordinates chunkCoordinates5 = world2.getSpawnPoint();
		int i6 = chunkCoordinates5.posX;
		int i7 = chunkCoordinates5.posZ;
		int i8 = chunkCoordinates5.posY;
		if(!world2.worldProvider.hasNoSky) {
			i6 += this.rand.nextInt(20) - 10;
			i8 = world2.getTopSolidOrLiquidBlock(i6, i7);
			i7 += this.rand.nextInt(20) - 10;
		}

		this.setLocationAndAngles((double)i6 + 0.5D, (double)i8, (double)i7 + 0.5D, 0.0F, 0.0F);
		this.mcServer = minecraftServer1;
		this.stepHeight = 0.0F;
		this.username = string3;
		this.yOffset = 0.0F;
	}

	public void readEntityFromNBT(NBTTagCompound compoundTag) {
		super.readEntityFromNBT(compoundTag);
		if(compoundTag.hasKey("playerGameType")) {
			this.itemInWorldManager.toggleGameType(compoundTag.getInteger("playerGameType"));
		}

	}

	public void writeEntityToNBT(NBTTagCompound compoundTag) {
		super.writeEntityToNBT(compoundTag);
		compoundTag.setInteger("playerGameType", this.itemInWorldManager.getGameType());
	}

	public void setWorld(World world1) {
		super.setWorld(world1);
	}

	public void removeExperience(int i1) {
		super.removeExperience(i1);
		this.lastExperience = -1;
	}

	public void s_func_20057_k() {
		this.craftingInventory.onCraftGuiOpened(this);
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
		this.itemInWorldManager.updateBlockRemoving();
		--this.ticksOfInvuln;
		this.craftingInventory.updateCraftingResults();

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

	public void onDeath(DamageSource damageSource1) {
		this.mcServer.configManager.sendPacketToAllPlayers(new Packet3Chat(damageSource1.getDeathMessage(this)));
		this.inventory.dropAllItems();
	}

	public boolean attackEntityFrom(DamageSource damageSource1, int i2) {
		if(this.ticksOfInvuln > 0) {
			return false;
		} else {
			if(!this.mcServer.pvpOn && damageSource1 instanceof EntityDamageSource) {
				Entity entity3 = damageSource1.getEntity();
				if(entity3 instanceof EntityPlayer) {
					return false;
				}

				if(entity3 instanceof EntityArrow) {
					EntityArrow entityArrow4 = (EntityArrow)entity3;
					if(entityArrow4.shootingEntity instanceof EntityPlayer) {
						return false;
					}
				}
			}

			return super.attackEntityFrom(damageSource1, i2);
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
			
			if(itemStack3 != null && Item.itemsList[itemStack3.itemID].s_func_28019_b() && this.playerNetServerHandler.getNumChunkDataPackets() <= 2) {
				Packet packet4 = ((ItemMapBase)Item.itemsList[itemStack3.itemID]).getUpdatePacket(itemStack3, this.worldObj, this);
				if(packet4 != null) {
					this.playerNetServerHandler.sendPacket(packet4);
				}
			}
		}

		if(z1 && !this.loadedChunks.isEmpty()) {
			ChunkCoordIntPair chunkCoordIntPair10 = (ChunkCoordIntPair)this.loadedChunks.get(0);
			double d11 = chunkCoordIntPair10.s_func_48477_a(this);

			for(int i5 = 0; i5 < this.loadedChunks.size(); ++i5) {
				ChunkCoordIntPair chunkCoordIntPair6 = (ChunkCoordIntPair)this.loadedChunks.get(i5);
				double d7 = chunkCoordIntPair6.s_func_48477_a(this);
				if(d7 < d11) {
					chunkCoordIntPair10 = chunkCoordIntPair6;
					d11 = d7;
				}
			}

			if(chunkCoordIntPair10 != null) {
				boolean z14 = false;
				if(this.playerNetServerHandler.getNumChunkDataPackets() < 4) {
					z14 = true;
				}

				if(z14) {
					WorldServer worldServer15 = this.mcServer.getWorldManager(this.dimension);
					if(worldServer15.blockExists(chunkCoordIntPair10.chunkXPos << 4, 0, chunkCoordIntPair10.chunkZPos << 4)) {
						Chunk chunk16 = worldServer15.getChunkFromChunkCoords(chunkCoordIntPair10.chunkXPos, chunkCoordIntPair10.chunkZPos);
						if(chunk16.isTerrainPopulated) {
							this.loadedChunks.remove(chunkCoordIntPair10);
							this.playerNetServerHandler.sendPacket(new Packet51MapChunk(worldServer15.getChunkFromChunkCoords(chunkCoordIntPair10.chunkXPos, chunkCoordIntPair10.chunkZPos), true, 0));
							List<TileEntity> list8 = worldServer15.getTileEntityList(chunkCoordIntPair10.chunkXPos * 16, 0, chunkCoordIntPair10.chunkZPos * 16, chunkCoordIntPair10.chunkXPos * 16 + 16, 256, chunkCoordIntPair10.chunkZPos * 16 + 16);

							for(int i9 = 0; i9 < list8.size(); ++i9) {
								this.getTileEntityInfo((TileEntity)list8.get(i9));
							}
						}
					}
				}
			}
		}

		if(this.inPortal) {
			if(this.mcServer.propertyManagerObj.getBooleanProperty("allow-nether", true)) {
				if(this.craftingInventory != this.inventorySlots) {
					this.closeScreen();
				}

				if(this.ridingEntity != null) {
					this.mountEntity(this.ridingEntity);
				} else {
					this.timeInPortal += 0.0125F;
					if(this.timeInPortal >= 1.0F) {
						this.timeInPortal = 1.0F;
						this.timeUntilPortal = 10;
						byte b13;
						if(this.dimension == -1) {
							b13 = 0;
						} else {
							b13 = -1;
						}

						this.mcServer.configManager.sendPlayerToOtherDimension(this, b13);
						this.lastExperience = -1;
						this.lastHealth = -1;
						this.s_field_35221_cc = -1;
					}
				}

				this.inPortal = false;
			}
		} else {
			if(this.timeInPortal > 0.0F) {
				this.timeInPortal -= 0.05F;
			}

			if(this.timeInPortal < 0.0F) {
				this.timeInPortal = 0.0F;
			}
		}

		if(this.timeUntilPortal > 0) {
			--this.timeUntilPortal;
		}

		if(this.getHealth() != this.lastHealth || this.s_field_35221_cc != this.foodStats.getFoodLevel() || this.foodStats.getSaturationLevel() == 0.0F != this.s_field_35222_cd) {
			this.playerNetServerHandler.sendPacket(new Packet8UpdateHealth(this.getHealth(), this.foodStats.getFoodLevel(), this.foodStats.getSaturationLevel()));
			this.lastHealth = this.getHealth();
			this.s_field_35221_cc = this.foodStats.getFoodLevel();
			this.s_field_35222_cd = this.foodStats.getSaturationLevel() == 0.0F;
		}

		if(this.experienceTotal != this.lastExperience) {
			this.lastExperience = this.experienceTotal;
			this.playerNetServerHandler.sendPacket(new Packet43Experience(this.experience, this.experienceTotal, this.experienceLevel));
		}

	}

	public void travelToTheEnd(int i1) {
		if(this.dimension == 1 && i1 == 1) {
			this.worldObj.setEntityDead(this);
			this.gameOver = true;
			this.playerNetServerHandler.sendPacket(new Packet70GameEvent(4, 0));
		} else {
			ChunkCoordinates chunkCoordinates2 = this.mcServer.getWorldManager(i1).getEntrancePortalLocation();
			if(chunkCoordinates2 != null) {
				this.playerNetServerHandler.teleportTo((double)chunkCoordinates2.posX, (double)chunkCoordinates2.posY, (double)chunkCoordinates2.posZ, 0.0F, 0.0F);
			}

			this.mcServer.configManager.sendPlayerToOtherDimension(this, 1);
			this.lastExperience = -1;
			this.lastHealth = -1;
			this.s_field_35221_cc = -1;
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
		this.craftingInventory.updateCraftingResults();
	}

	public void swingItem() {
		if(!this.isSwinging) {
			this.swingProgressInt = -1;
			this.isSwinging = true;
			EntityTracker entityTracker1 = this.mcServer.getEntityTracker(this.dimension);
			entityTracker1.sendPacketToTrackedPlayers(this, new Packet18Animation(this, 1));
		}

	}

	public void s_func_22068_s() {
	}
	
	public EnumStatus sleepInBedAt(int i1, int i2, int i3) {
		EnumStatus enumStatus4 = super.sleepInBedAt(i1, i2, i3);
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
		if(this.isPlayerSleeping()) {
			EntityTracker tracker = this.mcServer.getEntityTracker(this.dimension);
			tracker.sendPacketToTrackedPlayersAndTrackedEntity(this, new Packet18Animation(this, 3));
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
		this.craftingInventory = new ContainerWorkbench(this.inventory, this.worldObj, i1, i2, i3);
		this.craftingInventory.windowId = this.currentWindowId;
		this.craftingInventory.onCraftGuiOpened(this);
	}

	public void displayGUIChest(IInventory iInventory1) {
		this.getNextWidowId();
		this.playerNetServerHandler.sendPacket(new Packet100OpenWindow(this.currentWindowId, 0, iInventory1.getInvName(), iInventory1.getSizeInventory()));
		this.craftingInventory = new ContainerChest(this.inventory, iInventory1);
		this.craftingInventory.windowId = this.currentWindowId;
		this.craftingInventory.onCraftGuiOpened(this);
	}

	public void displayGUIFurnace(TileEntityFurnace tileEntityFurnace1) {
		this.getNextWidowId();
		this.playerNetServerHandler.sendPacket(new Packet100OpenWindow(this.currentWindowId, 2, tileEntityFurnace1.getInvName(), tileEntityFurnace1.getSizeInventory()));
		this.craftingInventory = new ContainerFurnace(this.inventory, tileEntityFurnace1);
		this.craftingInventory.windowId = this.currentWindowId;
		this.craftingInventory.onCraftGuiOpened(this);
	}

	public void updateCraftingInventorySlot(Container container1, int i2, ItemStack itemStack3) {
		if(!(container1.getSlot(i2) instanceof SlotCrafting)) {
			if(!this.isChangingQuantityOnly) {
				this.playerNetServerHandler.sendPacket(new Packet103SetSlot(container1.windowId, i2, itemStack3));
			}
		}
	}

	public void s_func_28017_a(Container container1) {
		this.updateCraftingInventory(container1, container1.getInventory());
	}

	public void updateCraftingInventory(Container container1, List<ItemStack> list2) {
		this.playerNetServerHandler.sendPacket(new Packet104WindowItems(container1.windowId, list2));
		this.playerNetServerHandler.sendPacket(new Packet103SetSlot(-1, -1, this.inventory.getItemStack()));
	}

	public void updateCraftingInventoryInfo(Container container1, int i2, int i3) {
		this.playerNetServerHandler.sendPacket(new Packet105UpdateProgressbar(container1.windowId, i2, i3));
	}

	public void onItemStackChanged(ItemStack itemStack1) {
	}

	public void closeScreen() {
		this.playerNetServerHandler.sendPacket(new Packet101CloseWindow(this.craftingInventory.windowId));
		this.closeCraftingGui();
	}

	public void updateHeldItem() {
		if(!this.isChangingQuantityOnly) {
			this.playerNetServerHandler.sendPacket(new Packet103SetSlot(-1, -1, this.inventory.getItemStack()));
		}
	}

	public void closeCraftingGui() {
		this.craftingInventory.onCraftGuiClosed(this);
		this.craftingInventory = this.inventorySlots;
	}

	public void s_func_30002_A() {
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

	public void s_func_30001_B() {
		this.lastHealth = -99999999;
	}

	public void addChatMessage(String string1) {
		StringTranslate stringTranslate2 = StringTranslate.getInstance();
		String string3 = stringTranslate2.translateKey(string1);
		this.playerNetServerHandler.sendPacket(new Packet3Chat(string3));
	}

	protected void onItemUseFinish() {
		this.playerNetServerHandler.sendPacket(new Packet38EntityStatus(this.entityId, (byte)9));
		super.onItemUseFinish();
	}

	public void setItemInUse(ItemStack itemStack1, int i2) {
		super.setItemInUse(itemStack1, i2);
		if(itemStack1 != null && itemStack1.getItem() != null && itemStack1.getItem().getItemUseAction(itemStack1) == EnumAction.eat) {
			EntityTracker entityTracker3 = this.mcServer.getEntityTracker(this.dimension);
			entityTracker3.sendPacketToTrackedPlayersAndTrackedEntity(this, new Packet18Animation(this, 5));
		}

	}

	public void setPositionAndUpdate(double d1, double d3, double d5) {
		this.playerNetServerHandler.teleportTo(d1, d3, d5, this.rotationYaw, this.rotationPitch);
	}

	public void onCriticalHit(Entity entity1) {
		EntityTracker entityTracker2 = this.mcServer.getEntityTracker(this.dimension);
		entityTracker2.sendPacketToTrackedPlayersAndTrackedEntity(this, new Packet18Animation(entity1, 6));
	}

	public void onEnchantmentCritical(Entity entity1) {
		EntityTracker entityTracker2 = this.mcServer.getEntityTracker(this.dimension);
		entityTracker2.sendPacketToTrackedPlayersAndTrackedEntity(this, new Packet18Animation(entity1, 7));
	}

	public void func_50009_aI() {
		if(this.playerNetServerHandler != null) {
			this.playerNetServerHandler.sendPacket(new Packet202PlayerAbilities(this.capabilities));
		}
	}

	@Override
	public void func_6420_o() {
		// TODO Auto-generated method stub
		
	}
}
