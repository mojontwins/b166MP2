package net.minecraft.src;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import net.minecraft.server.MinecraftServer;

public class NetServerHandler extends NetHandler implements ICommandListener {
	public static Logger logger = Logger.getLogger("Minecraft");
	public NetworkManager netManager;
	public boolean connectionClosed = false;
	private MinecraftServer mcServer;
	private EntityPlayerMP playerEntity;
	private int field_15_f;
	private int field_22004_g;
	private int playerInAirTime;
	private boolean field_22003_h;
	private double lastPosX;
	private double lastPosY;
	private double lastPosZ;
	private boolean hasMoved = true;
	private Map field_10_k = new HashMap();

	public NetServerHandler(MinecraftServer minecraftServer1, NetworkManager networkManager2, EntityPlayerMP entityPlayerMP3) {
		this.mcServer = minecraftServer1;
		this.netManager = networkManager2;
		networkManager2.setNetHandler(this);
		this.playerEntity = entityPlayerMP3;
		entityPlayerMP3.playerNetServerHandler = this;
	}

	public void handlePackets() {
		this.field_22003_h = false;
		this.netManager.processReadPackets();
		if(this.field_15_f - this.field_22004_g > 20) {
			this.sendPacket(new Packet0KeepAlive());
		}

	}

	public void kickPlayer(String string1) {
		this.playerEntity.func_30003_A();
		this.sendPacket(new Packet255KickDisconnect(string1));
		this.netManager.serverShutdown();
		this.mcServer.configManager.sendPacketToAllPlayers(new Packet3Chat("\u00a7e" + this.playerEntity.username + " left the game."));
		this.mcServer.configManager.playerLoggedOut(this.playerEntity);
		this.connectionClosed = true;
	}

	public void handleMovementTypePacket(Packet27Position packet27Position1) {
		this.playerEntity.setMovementType(packet27Position1.func_22031_c(), packet27Position1.func_22028_e(), packet27Position1.func_22032_g(), packet27Position1.func_22030_h(), packet27Position1.func_22029_d(), packet27Position1.func_22033_f());
	}

	public void handleFlying(Packet10Flying packet10Flying1) {
		WorldServer worldServer2 = this.mcServer.getWorldManager(this.playerEntity.dimension);
		this.field_22003_h = true;
		double d3;
		if(!this.hasMoved) {
			d3 = packet10Flying1.yPosition - this.lastPosY;
			if(packet10Flying1.xPosition == this.lastPosX && d3 * d3 < 0.01D && packet10Flying1.zPosition == this.lastPosZ) {
				this.hasMoved = true;
			}
		}

		if(this.hasMoved) {
			double d5;
			double d7;
			double d9;
			double d13;
			if(this.playerEntity.ridingEntity != null) {
				float f26 = this.playerEntity.rotationYaw;
				float f4 = this.playerEntity.rotationPitch;
				this.playerEntity.ridingEntity.updateRiderPosition();
				d5 = this.playerEntity.posX;
				d7 = this.playerEntity.posY;
				d9 = this.playerEntity.posZ;
				double d27 = 0.0D;
				d13 = 0.0D;
				if(packet10Flying1.rotating) {
					f26 = packet10Flying1.yaw;
					f4 = packet10Flying1.pitch;
				}

				if(packet10Flying1.moving && packet10Flying1.yPosition == -999.0D && packet10Flying1.stance == -999.0D) {
					d27 = packet10Flying1.xPosition;
					d13 = packet10Flying1.zPosition;
				}

				this.playerEntity.onGround = packet10Flying1.onGround;
				this.playerEntity.onUpdateEntity(true);
				this.playerEntity.moveEntity(d27, 0.0D, d13);
				this.playerEntity.setPositionAndRotation(d5, d7, d9, f26, f4);
				this.playerEntity.motionX = d27;
				this.playerEntity.motionZ = d13;
				if(this.playerEntity.ridingEntity != null) {
					worldServer2.func_12017_b(this.playerEntity.ridingEntity, true);
				}

				if(this.playerEntity.ridingEntity != null) {
					this.playerEntity.ridingEntity.updateRiderPosition();
				}

				this.mcServer.configManager.func_613_b(this.playerEntity);
				this.lastPosX = this.playerEntity.posX;
				this.lastPosY = this.playerEntity.posY;
				this.lastPosZ = this.playerEntity.posZ;
				worldServer2.updateEntity(this.playerEntity);
				return;
			}

			if(this.playerEntity.func_30001_K()) {
				this.playerEntity.onUpdateEntity(true);
				this.playerEntity.setPositionAndRotation(this.lastPosX, this.lastPosY, this.lastPosZ, this.playerEntity.rotationYaw, this.playerEntity.rotationPitch);
				worldServer2.updateEntity(this.playerEntity);
				return;
			}

			d3 = this.playerEntity.posY;
			this.lastPosX = this.playerEntity.posX;
			this.lastPosY = this.playerEntity.posY;
			this.lastPosZ = this.playerEntity.posZ;
			d5 = this.playerEntity.posX;
			d7 = this.playerEntity.posY;
			d9 = this.playerEntity.posZ;
			float f11 = this.playerEntity.rotationYaw;
			float f12 = this.playerEntity.rotationPitch;
			if(packet10Flying1.moving && packet10Flying1.yPosition == -999.0D && packet10Flying1.stance == -999.0D) {
				packet10Flying1.moving = false;
			}

			if(packet10Flying1.moving) {
				d5 = packet10Flying1.xPosition;
				d7 = packet10Flying1.yPosition;
				d9 = packet10Flying1.zPosition;
				d13 = packet10Flying1.stance - packet10Flying1.yPosition;
				if(!this.playerEntity.func_30001_K() && (d13 > 1.65D || d13 < 0.1D)) {
					this.kickPlayer("Illegal stance");
					logger.warning(this.playerEntity.username + " had an illegal stance: " + d13);
					return;
				}

				if(Math.abs(packet10Flying1.xPosition) > 3.2E7D || Math.abs(packet10Flying1.zPosition) > 3.2E7D) {
					this.kickPlayer("Illegal position");
					return;
				}
			}

			if(packet10Flying1.rotating) {
				f11 = packet10Flying1.yaw;
				f12 = packet10Flying1.pitch;
			}

			this.playerEntity.onUpdateEntity(true);
			this.playerEntity.ySize = 0.0F;
			this.playerEntity.setPositionAndRotation(this.lastPosX, this.lastPosY, this.lastPosZ, f11, f12);
			if(!this.hasMoved) {
				return;
			}

			d13 = d5 - this.playerEntity.posX;
			double d15 = d7 - this.playerEntity.posY;
			double d17 = d9 - this.playerEntity.posZ;
			double d19 = d13 * d13 + d15 * d15 + d17 * d17;
			if(d19 > 100.0D) {
				logger.warning(this.playerEntity.username + " moved too quickly!");
				this.kickPlayer("You moved too quickly :( (Hacking?)");
				return;
			}

			float f21 = 0.0625F;
			boolean z22 = worldServer2.getCollidingBoundingBoxes(this.playerEntity, this.playerEntity.boundingBox.copy().getInsetBoundingBox((double)f21, (double)f21, (double)f21)).size() == 0;
			this.playerEntity.moveEntity(d13, d15, d17);
			d13 = d5 - this.playerEntity.posX;
			d15 = d7 - this.playerEntity.posY;
			if(d15 > -0.5D || d15 < 0.5D) {
				d15 = 0.0D;
			}

			d17 = d9 - this.playerEntity.posZ;
			d19 = d13 * d13 + d15 * d15 + d17 * d17;
			boolean z23 = false;
			if(d19 > 0.0625D && !this.playerEntity.func_30001_K()) {
				z23 = true;
				logger.warning(this.playerEntity.username + " moved wrongly!");
				System.out.println("Got position " + d5 + ", " + d7 + ", " + d9);
				System.out.println("Expected " + this.playerEntity.posX + ", " + this.playerEntity.posY + ", " + this.playerEntity.posZ);
			}

			this.playerEntity.setPositionAndRotation(d5, d7, d9, f11, f12);
			boolean z24 = worldServer2.getCollidingBoundingBoxes(this.playerEntity, this.playerEntity.boundingBox.copy().getInsetBoundingBox((double)f21, (double)f21, (double)f21)).size() == 0;
			if(z22 && (z23 || !z24) && !this.playerEntity.func_30001_K()) {
				this.teleportTo(this.lastPosX, this.lastPosY, this.lastPosZ, f11, f12);
				return;
			}

			AxisAlignedBB axisAlignedBB25 = this.playerEntity.boundingBox.copy().expand((double)f21, (double)f21, (double)f21).addCoord(0.0D, -0.55D, 0.0D);
			if(!this.mcServer.allowFlight && !worldServer2.func_27069_b(axisAlignedBB25)) {
				if(d15 >= -0.03125D) {
					++this.playerInAirTime;
					if(this.playerInAirTime > 80) {
						logger.warning(this.playerEntity.username + " was kicked for floating too long!");
						this.kickPlayer("Flying is not enabled on this server");
						return;
					}
				}
			} else {
				this.playerInAirTime = 0;
			}

			this.playerEntity.onGround = packet10Flying1.onGround;
			this.mcServer.configManager.func_613_b(this.playerEntity);
			this.playerEntity.handleFalling(this.playerEntity.posY - d3, packet10Flying1.onGround);
		}

	}

	public void teleportTo(double d1, double d3, double d5, float f7, float f8) {
		this.hasMoved = false;
		this.lastPosX = d1;
		this.lastPosY = d3;
		this.lastPosZ = d5;
		this.playerEntity.setPositionAndRotation(d1, d3, d5, f7, f8);
		this.playerEntity.playerNetServerHandler.sendPacket(new Packet13PlayerLookMove(d1, d3 + (double)1.62F, d3, d5, f7, f8, false));
	}

	public void handleBlockDig(Packet14BlockDig packet14BlockDig1) {
		WorldServer worldServer2 = this.mcServer.getWorldManager(this.playerEntity.dimension);
		if(packet14BlockDig1.status == 4) {
			this.playerEntity.dropCurrentItem();
		} else {
			boolean z3 = worldServer2.field_819_z = worldServer2.worldProvider.worldType != 0 || this.mcServer.configManager.isOp(this.playerEntity.username);
			boolean z4 = false;
			if(packet14BlockDig1.status == 0) {
				z4 = true;
			}

			if(packet14BlockDig1.status == 2) {
				z4 = true;
			}

			int i5 = packet14BlockDig1.xPosition;
			int i6 = packet14BlockDig1.yPosition;
			int i7 = packet14BlockDig1.zPosition;
			if(z4) {
				double d8 = this.playerEntity.posX - ((double)i5 + 0.5D);
				double d10 = this.playerEntity.posY - ((double)i6 + 0.5D);
				double d12 = this.playerEntity.posZ - ((double)i7 + 0.5D);
				double d14 = d8 * d8 + d10 * d10 + d12 * d12;
				if(d14 > 36.0D) {
					return;
				}
			}

			ChunkCoordinates chunkCoordinates19 = worldServer2.getSpawnPoint();
			int i9 = (int)MathHelper.abs((float)(i5 - chunkCoordinates19.posX));
			int i20 = (int)MathHelper.abs((float)(i7 - chunkCoordinates19.posZ));
			if(i9 > i20) {
				i20 = i9;
			}

			if(packet14BlockDig1.status == 0) {
				if(i20 <= 16 && !z3) {
					this.playerEntity.playerNetServerHandler.sendPacket(new Packet53BlockChange(i5, i6, i7, worldServer2));
				} else {
					this.playerEntity.itemInWorldManager.func_324_a(i5, i6, i7, packet14BlockDig1.face);
				}
			} else if(packet14BlockDig1.status == 2) {
				this.playerEntity.itemInWorldManager.func_22045_b(i5, i6, i7);
				if(worldServer2.getBlockId(i5, i6, i7) != 0) {
					this.playerEntity.playerNetServerHandler.sendPacket(new Packet53BlockChange(i5, i6, i7, worldServer2));
				}
			} else if(packet14BlockDig1.status == 3) {
				double d11 = this.playerEntity.posX - ((double)i5 + 0.5D);
				double d13 = this.playerEntity.posY - ((double)i6 + 0.5D);
				double d15 = this.playerEntity.posZ - ((double)i7 + 0.5D);
				double d17 = d11 * d11 + d13 * d13 + d15 * d15;
				if(d17 < 256.0D) {
					this.playerEntity.playerNetServerHandler.sendPacket(new Packet53BlockChange(i5, i6, i7, worldServer2));
				}
			}

			worldServer2.field_819_z = false;
		}
	}

	public void handlePlace(Packet15Place packet15Place1) {
		WorldServer worldServer2 = this.mcServer.getWorldManager(this.playerEntity.dimension);
		ItemStack itemStack3 = this.playerEntity.inventory.getCurrentItem();
		boolean z4 = worldServer2.field_819_z = worldServer2.worldProvider.worldType != 0 || this.mcServer.configManager.isOp(this.playerEntity.username);
		if(packet15Place1.direction == 255) {
			if(itemStack3 == null) {
				return;
			}

			this.playerEntity.itemInWorldManager.func_6154_a(this.playerEntity, worldServer2, itemStack3);
		} else {
			int i5 = packet15Place1.xPosition;
			int i6 = packet15Place1.yPosition;
			int i7 = packet15Place1.zPosition;
			int i8 = packet15Place1.direction;
			ChunkCoordinates chunkCoordinates9 = worldServer2.getSpawnPoint();
			int i10 = (int)MathHelper.abs((float)(i5 - chunkCoordinates9.posX));
			int i11 = (int)MathHelper.abs((float)(i7 - chunkCoordinates9.posZ));
			if(i10 > i11) {
				i11 = i10;
			}

			if(this.hasMoved && this.playerEntity.getDistanceSq((double)i5 + 0.5D, (double)i6 + 0.5D, (double)i7 + 0.5D) < 64.0D && (i11 > 16 || z4)) {
				this.playerEntity.itemInWorldManager.activeBlockOrUseItem(this.playerEntity, worldServer2, itemStack3, i5, i6, i7, i8);
			}

			this.playerEntity.playerNetServerHandler.sendPacket(new Packet53BlockChange(i5, i6, i7, worldServer2));
			if(i8 == 0) {
				--i6;
			}

			if(i8 == 1) {
				++i6;
			}

			if(i8 == 2) {
				--i7;
			}

			if(i8 == 3) {
				++i7;
			}

			if(i8 == 4) {
				--i5;
			}

			if(i8 == 5) {
				++i5;
			}

			this.playerEntity.playerNetServerHandler.sendPacket(new Packet53BlockChange(i5, i6, i7, worldServer2));
		}

		itemStack3 = this.playerEntity.inventory.getCurrentItem();
		if(itemStack3 != null && itemStack3.stackSize == 0) {
			this.playerEntity.inventory.mainInventory[this.playerEntity.inventory.currentItem] = null;
		}

		this.playerEntity.isChangingQuantityOnly = true;
		this.playerEntity.inventory.mainInventory[this.playerEntity.inventory.currentItem] = ItemStack.func_20117_a(this.playerEntity.inventory.mainInventory[this.playerEntity.inventory.currentItem]);
		Slot slot12 = this.playerEntity.currentCraftingInventory.func_20127_a(this.playerEntity.inventory, this.playerEntity.inventory.currentItem);
		this.playerEntity.currentCraftingInventory.updateCraftingMatrix();
		this.playerEntity.isChangingQuantityOnly = false;
		if(!ItemStack.areItemStacksEqual(this.playerEntity.inventory.getCurrentItem(), packet15Place1.itemStack)) {
			this.sendPacket(new Packet103SetSlot(this.playerEntity.currentCraftingInventory.windowId, slot12.id, this.playerEntity.inventory.getCurrentItem()));
		}

		worldServer2.field_819_z = false;
	}

	public void handleErrorMessage(String string1, Object[] object2) {
		logger.info(this.playerEntity.username + " lost connection: " + string1);
		this.mcServer.configManager.sendPacketToAllPlayers(new Packet3Chat("\u00a7e" + this.playerEntity.username + " left the game."));
		this.mcServer.configManager.playerLoggedOut(this.playerEntity);
		this.connectionClosed = true;
	}

	public void registerPacket(Packet packet1) {
		logger.warning(this.getClass() + " wasn\'t prepared to deal with a " + packet1.getClass());
		this.kickPlayer("Protocol error, unexpected packet");
	}

	public void sendPacket(Packet packet1) {
		this.netManager.addToSendQueue(packet1);
		this.field_22004_g = this.field_15_f;
	}

	public void handleBlockItemSwitch(Packet16BlockItemSwitch packet16BlockItemSwitch1) {
		if(packet16BlockItemSwitch1.id >= 0 && packet16BlockItemSwitch1.id <= InventoryPlayer.func_25054_e()) {
			this.playerEntity.inventory.currentItem = packet16BlockItemSwitch1.id;
		} else {
			logger.warning(this.playerEntity.username + " tried to set an invalid carried item");
		}
	}

	public void handleChat(Packet3Chat packet3Chat1) {
		String string2 = packet3Chat1.message;
		if(string2.length() > 100) {
			this.kickPlayer("Chat message too long");
		} else {
			string2 = string2.trim();

			for(int i3 = 0; i3 < string2.length(); ++i3) {
				if(ChatAllowedCharacters.allowedCharacters.indexOf(string2.charAt(i3)) < 0) {
					this.kickPlayer("Illegal characters in chat");
					return;
				}
			}

			if(string2.startsWith("/")) {
				this.handleSlashCommand(string2);
			} else {
				string2 = "<" + this.playerEntity.username + "> " + string2;
				logger.info(string2);
				this.mcServer.configManager.sendPacketToAllPlayers(new Packet3Chat(string2));
			}

		}
	}

	private void handleSlashCommand(String string1) {
		if(string1.toLowerCase().startsWith("/me ")) {
			string1 = "* " + this.playerEntity.username + " " + string1.substring(string1.indexOf(" ")).trim();
			logger.info(string1);
			this.mcServer.configManager.sendPacketToAllPlayers(new Packet3Chat(string1));
		} else if(string1.toLowerCase().startsWith("/kill")) {
			this.playerEntity.attackEntityFrom((Entity)null, 1000);
		} else if(string1.toLowerCase().startsWith("/tell ")) {
			String[] string2 = string1.split(" ");
			if(string2.length >= 3) {
				string1 = string1.substring(string1.indexOf(" ")).trim();
				string1 = string1.substring(string1.indexOf(" ")).trim();
				string1 = "\u00a77" + this.playerEntity.username + " whispers " + string1;
				logger.info(string1 + " to " + string2[1]);
				if(!this.mcServer.configManager.sendPacketToPlayer(string2[1], new Packet3Chat(string1))) {
					this.sendPacket(new Packet3Chat("\u00a7cThere\'s no player by that name online."));
				}
			}
		} else {
			String string3;
			if(this.mcServer.configManager.isOp(this.playerEntity.username)) {
				string3 = string1.substring(1);
				logger.info(this.playerEntity.username + " issued server command: " + string3);
				this.mcServer.addCommand(string3, this);
			} else {
				string3 = string1.substring(1);
				logger.info(this.playerEntity.username + " tried command: " + string3);
			}
		}

	}

	public void handleArmAnimation(Packet18Animation packet18Animation1) {
		if(packet18Animation1.animate == 1) {
			this.playerEntity.swingItem();
		}

	}

	public void func_21001_a(Packet19EntityAction packet19EntityAction1) {
		if(packet19EntityAction1.state == 1) {
			this.playerEntity.func_21043_b(true);
		} else if(packet19EntityAction1.state == 2) {
			this.playerEntity.func_21043_b(false);
		} else if(packet19EntityAction1.state == 3) {
			this.playerEntity.wakeUpPlayer(false, true, true);
			this.hasMoved = false;
		}

	}

	public void handleKickDisconnect(Packet255KickDisconnect packet255KickDisconnect1) {
		this.netManager.networkShutdown("disconnect.quitting", new Object[0]);
	}

	public int getNumChunkDataPackets() {
		return this.netManager.getNumChunkDataPackets();
	}

	public void log(String string1) {
		this.sendPacket(new Packet3Chat("\u00a77" + string1));
	}

	public String getUsername() {
		return this.playerEntity.username;
	}

	public void func_6006_a(Packet7UseEntity packet7UseEntity1) {
		WorldServer worldServer2 = this.mcServer.getWorldManager(this.playerEntity.dimension);
		Entity entity3 = worldServer2.func_6158_a(packet7UseEntity1.targetEntity);
		if(entity3 != null && this.playerEntity.canEntityBeSeen(entity3) && this.playerEntity.getDistanceSqToEntity(entity3) < 36.0D) {
			if(packet7UseEntity1.isLeftClick == 0) {
				this.playerEntity.useCurrentItemOnEntity(entity3);
			} else if(packet7UseEntity1.isLeftClick == 1) {
				this.playerEntity.attackTargetEntityWithCurrentItem(entity3);
			}
		}

	}

	public void handleRespawnPacket(Packet9Respawn packet9Respawn1) {
		if(this.playerEntity.health <= 0) {
			this.playerEntity = this.mcServer.configManager.recreatePlayerEntity(this.playerEntity, 0);
		}
	}

	public void handleCraftingGuiClosedPacked(Packet101CloseWindow packet101CloseWindow1) {
		this.playerEntity.closeCraftingGui();
	}

	public void func_20007_a(Packet102WindowClick packet102WindowClick1) {
		if(this.playerEntity.currentCraftingInventory.windowId == packet102WindowClick1.window_Id && this.playerEntity.currentCraftingInventory.getCanCraft(this.playerEntity)) {
			ItemStack itemStack2 = this.playerEntity.currentCraftingInventory.func_27085_a(packet102WindowClick1.inventorySlot, packet102WindowClick1.mouseClick, packet102WindowClick1.field_27039_f, this.playerEntity);
			if(ItemStack.areItemStacksEqual(packet102WindowClick1.itemStack, itemStack2)) {
				this.playerEntity.playerNetServerHandler.sendPacket(new Packet106Transaction(packet102WindowClick1.window_Id, packet102WindowClick1.action, true));
				this.playerEntity.isChangingQuantityOnly = true;
				this.playerEntity.currentCraftingInventory.updateCraftingMatrix();
				this.playerEntity.updateHeldItem();
				this.playerEntity.isChangingQuantityOnly = false;
			} else {
				this.field_10_k.put(this.playerEntity.currentCraftingInventory.windowId, packet102WindowClick1.action);
				this.playerEntity.playerNetServerHandler.sendPacket(new Packet106Transaction(packet102WindowClick1.window_Id, packet102WindowClick1.action, false));
				this.playerEntity.currentCraftingInventory.setCanCraft(this.playerEntity, false);
				ArrayList arrayList3 = new ArrayList();

				for(int i4 = 0; i4 < this.playerEntity.currentCraftingInventory.inventorySlots.size(); ++i4) {
					arrayList3.add(((Slot)this.playerEntity.currentCraftingInventory.inventorySlots.get(i4)).getStack());
				}

				this.playerEntity.updateCraftingInventory(this.playerEntity.currentCraftingInventory, arrayList3);
			}
		}

	}

	public void func_20008_a(Packet106Transaction packet106Transaction1) {
		Short short2 = (Short)this.field_10_k.get(this.playerEntity.currentCraftingInventory.windowId);
		if(short2 != null && packet106Transaction1.shortWindowId == short2.shortValue() && this.playerEntity.currentCraftingInventory.windowId == packet106Transaction1.windowId && !this.playerEntity.currentCraftingInventory.getCanCraft(this.playerEntity)) {
			this.playerEntity.currentCraftingInventory.setCanCraft(this.playerEntity, true);
		}

	}

	public void func_20005_a(Packet130UpdateSign packet130UpdateSign1) {
		WorldServer worldServer2 = this.mcServer.getWorldManager(this.playerEntity.dimension);
		if(worldServer2.blockExists(packet130UpdateSign1.xPosition, packet130UpdateSign1.yPosition, packet130UpdateSign1.zPosition)) {
			TileEntity tileEntity3 = worldServer2.getBlockTileEntity(packet130UpdateSign1.xPosition, packet130UpdateSign1.yPosition, packet130UpdateSign1.zPosition);
			if(tileEntity3 instanceof TileEntitySign) {
				TileEntitySign tileEntitySign4 = (TileEntitySign)tileEntity3;
				if(!tileEntitySign4.getIsEditAble()) {
					this.mcServer.logWarning("Player " + this.playerEntity.username + " just tried to change non-editable sign");
					return;
				}
			}

			int i6;
			int i9;
			for(i9 = 0; i9 < 4; ++i9) {
				boolean z5 = true;
				if(packet130UpdateSign1.signLines[i9].length() > 15) {
					z5 = false;
				} else {
					for(i6 = 0; i6 < packet130UpdateSign1.signLines[i9].length(); ++i6) {
						if(ChatAllowedCharacters.allowedCharacters.indexOf(packet130UpdateSign1.signLines[i9].charAt(i6)) < 0) {
							z5 = false;
						}
					}
				}

				if(!z5) {
					packet130UpdateSign1.signLines[i9] = "!?";
				}
			}

			if(tileEntity3 instanceof TileEntitySign) {
				i9 = packet130UpdateSign1.xPosition;
				int i10 = packet130UpdateSign1.yPosition;
				i6 = packet130UpdateSign1.zPosition;
				TileEntitySign tileEntitySign7 = (TileEntitySign)tileEntity3;

				for(int i8 = 0; i8 < 4; ++i8) {
					tileEntitySign7.signText[i8] = packet130UpdateSign1.signLines[i8];
				}

				tileEntitySign7.onInventoryChanged();
				worldServer2.markBlockNeedsUpdate(i9, i10, i6);
			}
		}

	}

	public boolean isServerHandler() {
		return true;
	}
}
