package net.minecraft.client.multiplayer;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.mojontwins.minecraft.commands.TileEntityCommandBlock;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiDisconnected;
import net.minecraft.client.gui.GuiDownloadTerrain;
import net.minecraft.client.gui.GuiPlayerInfo;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.particle.EntityCrit2FX;
import net.minecraft.client.particle.EntityPickupFX;
import net.minecraft.client.player.EntityOtherPlayerMP;
import net.minecraft.client.player.EntityPlayerSP;
import net.minecraft.network.NetHandler;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.WatchableObject;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet0KeepAlive;
import net.minecraft.network.packet.Packet100OpenWindow;
import net.minecraft.network.packet.Packet101CloseWindow;
import net.minecraft.network.packet.Packet103SetSlot;
import net.minecraft.network.packet.Packet104WindowItems;
import net.minecraft.network.packet.Packet105UpdateProgressbar;
import net.minecraft.network.packet.Packet106Transaction;
import net.minecraft.network.packet.Packet10Flying;
import net.minecraft.network.packet.Packet130UpdateSign;
import net.minecraft.network.packet.Packet131MapData;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.network.packet.Packet17Sleep;
import net.minecraft.network.packet.Packet18Animation;
import net.minecraft.network.packet.Packet1Login;
import net.minecraft.network.packet.Packet201PlayerInfo;
import net.minecraft.network.packet.Packet202PlayerAbilities;
import net.minecraft.network.packet.Packet20NamedEntitySpawn;
import net.minecraft.network.packet.Packet21PickupSpawn;
import net.minecraft.network.packet.Packet22Collect;
import net.minecraft.network.packet.Packet23VehicleSpawn;
import net.minecraft.network.packet.Packet24MobSpawn;
import net.minecraft.network.packet.Packet255KickDisconnect;
import net.minecraft.network.packet.Packet25EntityPainting;
import net.minecraft.network.packet.Packet28EntityVelocity;
import net.minecraft.network.packet.Packet29DestroyEntity;
import net.minecraft.network.packet.Packet2Handshake;
import net.minecraft.network.packet.Packet30Entity;
import net.minecraft.network.packet.Packet34EntityTeleport;
import net.minecraft.network.packet.Packet35EntityHeadRotation;
import net.minecraft.network.packet.Packet38EntityStatus;
import net.minecraft.network.packet.Packet39AttachEntity;
import net.minecraft.network.packet.Packet3Chat;
import net.minecraft.network.packet.Packet40EntityMetadata;
import net.minecraft.network.packet.Packet43Experience;
import net.minecraft.network.packet.Packet4UpdateTime;
import net.minecraft.network.packet.Packet50PreChunk;
import net.minecraft.network.packet.Packet51MapChunk;
import net.minecraft.network.packet.Packet52MultiBlockChange;
import net.minecraft.network.packet.Packet53BlockChange;
import net.minecraft.network.packet.Packet54PlayNoteBlock;
import net.minecraft.network.packet.Packet5PlayerInventory;
import net.minecraft.network.packet.Packet60Explosion;
import net.minecraft.network.packet.Packet61DoorChange;
import net.minecraft.network.packet.Packet6SpawnPosition;
import net.minecraft.network.packet.Packet70GameEvent;
import net.minecraft.network.packet.Packet71Weather;
import net.minecraft.network.packet.Packet88MovingPiston;
import net.minecraft.network.packet.Packet89SetArmor;
import net.minecraft.network.packet.Packet8UpdateHealth;
import net.minecraft.network.packet.Packet90ArmoredMobSpawn;
import net.minecraft.network.packet.Packet91UpdateCommandBlock;
import net.minecraft.network.packet.Packet95UpdateDayOfTheYear;
import net.minecraft.network.packet.Packet9Respawn;
import net.minecraft.src.MathHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLightningBolt;
import net.minecraft.world.entity.EntityList;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityPainting;
import net.minecraft.world.entity.item.EntityBoat;
import net.minecraft.world.entity.item.EntityFallingSand;
import net.minecraft.world.entity.item.EntityItem;
import net.minecraft.world.entity.item.EntityMinecart;
import net.minecraft.world.entity.item.EntityMovingPiston;
import net.minecraft.world.entity.item.EntityTNTPrimed;
import net.minecraft.world.entity.monster.EntityArmoredMob;
import net.minecraft.world.entity.monster.IArmoredMob;
import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.entity.projectile.EntityArrow;
import net.minecraft.world.entity.projectile.EntityEgg;
import net.minecraft.world.entity.projectile.EntityFireball;
import net.minecraft.world.entity.projectile.EntityFishHook;
import net.minecraft.world.entity.projectile.EntitySmallFireball;
import net.minecraft.world.entity.projectile.EntitySnowball;
import net.minecraft.world.inventory.Container;
import net.minecraft.world.inventory.InventoryBasic;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.map.ItemMap;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.World;
import net.minecraft.world.level.WorldSettings;
import net.minecraft.world.level.chunk.Chunk;
import net.minecraft.world.level.chunk.ChunkCoordinates;
import net.minecraft.world.level.tile.Block;
import net.minecraft.world.level.tile.entity.TileEntity;
import net.minecraft.world.level.tile.entity.TileEntityDispenser;
import net.minecraft.world.level.tile.entity.TileEntityFurnace;
import net.minecraft.world.level.tile.entity.TileEntityMobSpawner;
import net.minecraft.world.level.tile.entity.TileEntitySign;

public class NetClientHandler extends NetHandler {
	private boolean disconnected = false;
	private NetworkManager netManager;
	public String field_1209_a;
	private Minecraft mc;
	private WorldClient worldClient;
	private boolean hasUpdatedPosition = false; // This flag is used to keep the GUI screen on ("loading...") until a Packet10Flying packet is received.
	private Map<String, GuiPlayerInfo> playerInfoMap = new HashMap<String, GuiPlayerInfo>();
	public List<GuiPlayerInfo> playerNames = new ArrayList<GuiPlayerInfo>();
	public int currentServerMaxPlayers = 20;
	Random rand = new Random();

	public NetClientHandler(Minecraft mc, String string2, int i3) throws UnknownHostException, IOException {
		this.mc = mc;
		Socket socket4 = new Socket(InetAddress.getByName(string2), i3);
		this.netManager = new NetworkManager(socket4, "Client", this);
	}

	public void processReadPackets() {
		if(!this.disconnected) {
			this.netManager.processReadPackets();
		}

		this.netManager.wakeThreads();
	}

	public void handleLogin(Packet1Login packet) {
		System.out.println ("Received login info, seasons: " + packet.enableSeasons);
		this.mc.playerController = new PlayerControllerMP(this.mc, this);
		this.worldClient = new WorldClient(this, new WorldSettings(0L, packet.serverMode, false, false, packet.enableSeasons, packet.terrainType), packet.dimension, packet.difficultySetting);
		this.worldClient.isRemote = true;
		this.mc.changeWorld(this.worldClient);
		this.mc.thePlayer.dimension = packet.dimension;
		this.mc.displayGuiScreen(new GuiDownloadTerrain(this));
		this.mc.thePlayer.entityId = packet.protocolVersion;
		this.currentServerMaxPlayers = packet.maxPlayers;
		((PlayerControllerMP)this.mc.playerController).setCreative(packet.serverMode == 1);
	}

	public void handlePickupSpawn(Packet21PickupSpawn packet21PickupSpawn1) {
		double d2 = (double)packet21PickupSpawn1.xPosition / 32.0D;
		double d4 = (double)packet21PickupSpawn1.yPosition / 32.0D;
		double d6 = (double)packet21PickupSpawn1.zPosition / 32.0D;
		EntityItem entityItem8 = new EntityItem(this.worldClient, d2, d4, d6, new ItemStack(packet21PickupSpawn1.itemID, 
				packet21PickupSpawn1.count, packet21PickupSpawn1.itemDamage));
		entityItem8.motionX = (double)packet21PickupSpawn1.rotation / 128.0D;
		entityItem8.motionY = (double)packet21PickupSpawn1.pitch / 128.0D;
		entityItem8.motionZ = (double)packet21PickupSpawn1.roll / 128.0D;
		entityItem8.serverPosX = packet21PickupSpawn1.xPosition;
		entityItem8.serverPosY = packet21PickupSpawn1.yPosition;
		entityItem8.serverPosZ = packet21PickupSpawn1.zPosition;
		this.worldClient.addEntityToWorld(packet21PickupSpawn1.entityId, entityItem8);
	}

	public void handleVehicleSpawn(Packet23VehicleSpawn packet) {
		double x = (double)packet.xPosition / 32.0D;
		double y = (double)packet.yPosition / 32.0D;
		double z = (double)packet.zPosition / 32.0D;
		Entity entity = null;
		
		if(packet.type == 10) {
			entity = new EntityMinecart(this.worldClient, x, y, z, 0);
			
		} else if(packet.type == 11) {
			entity = new EntityMinecart(this.worldClient, x, y, z, 1);
			
		} else if(packet.type == 12) {
			entity = new EntityMinecart(this.worldClient, x, y, z, 2);
			
		} else if(packet.type == 90) {
			entity = new EntityFishHook(this.worldClient, x, y, z);
			
		} else if(packet.type == 60) {
			entity = new EntityArrow(this.worldClient, x, y, z);
			
		} else if(packet.type == 61) {
			entity = new EntitySnowball(this.worldClient, x, y, z);

		} else if(packet.type == 63) {
			entity = new EntityFireball(this.worldClient, x, y, z, (double)packet.speedX / 8000.0D, (double)packet.speedY / 8000.0D, (double)packet.speedZ / 8000.0D);
			packet.throwerEntityId = 0;
			
		} else if(packet.type == 64) {
			entity = new EntitySmallFireball(this.worldClient, x, y, z, (double)packet.speedX / 8000.0D, (double)packet.speedY / 8000.0D, (double)packet.speedZ / 8000.0D);
			packet.throwerEntityId = 0;
			
		} else if(packet.type == 62) {
			entity = new EntityEgg(this.worldClient, x, y, z);
			
		} else if(packet.type == 1) {
			entity = new EntityBoat(this.worldClient, x, y, z);
			
		} else if(packet.type == 50) {
			entity = new EntityTNTPrimed(this.worldClient, x, y, z);
	
		} else if(packet.type == 70) {
			entity = new EntityFallingSand(this.worldClient, x, y, z, Block.sand.blockID);
			
		} else if(packet.type == 71) {
			entity = new EntityFallingSand(this.worldClient, x, y, z, Block.gravel.blockID);
			
		} 

		if(entity != null) {
			((Entity)entity).serverPosX = packet.xPosition;
			((Entity)entity).serverPosY = packet.yPosition;
			((Entity)entity).serverPosZ = packet.zPosition;
			((Entity)entity).rotationYaw = 0.0F;
			((Entity)entity).rotationPitch = 0.0F;
			Entity[] entity9 = ((Entity)entity).getParts();
			if(entity9 != null) {
				int i10 = packet.entityId - ((Entity)entity).entityId;

				for(int i11 = 0; i11 < entity9.length; ++i11) {
					entity9[i11].entityId += i10;
				}
			}

			((Entity)entity).entityId = packet.entityId;
			this.worldClient.addEntityToWorld(packet.entityId, (Entity)entity);
			if(packet.throwerEntityId > 0) {
				if(packet.type == 60 || packet.type == 105) {
					Entity entity12 = this.getEntityByID(packet.throwerEntityId);
					if(entity12 instanceof EntityLiving) {
						((EntityArrow)entity).shootingEntity = (EntityLiving)entity12;
					}
				}

				((Entity)entity).setVelocity((double)packet.speedX / 8000.0D, (double)packet.speedY / 8000.0D, (double)packet.speedZ / 8000.0D);
			}
		}

	}
	
	@Override
	public void handleMovingPiston(Packet88MovingPiston packet) {
		EntityMovingPiston piston = new EntityMovingPiston(this.worldClient, packet.x, packet.y, packet.z, packet.sticky);
		piston.xmove = packet.xmove;
		piston.ymove = packet.ymove;
		piston.zmove = packet.zmove;
		piston.data = packet.data;
		piston.entityId = packet.entityId;
		this.worldClient.addEntityToWorld(packet.entityId, (Entity)piston);
	}

	@Override
	public void handleWeather(Packet71Weather packet71Weather1) {
		double d2 = (double)packet71Weather1.posX / 32.0D;
		double d4 = (double)packet71Weather1.posY / 32.0D;
		double d6 = (double)packet71Weather1.posZ / 32.0D;
		EntityLightningBolt entityLightningBolt8 = null;
		if(packet71Weather1.isLightningBolt == 1) {
			entityLightningBolt8 = new EntityLightningBolt(this.worldClient, d2, d4, d6);
		}

		if(entityLightningBolt8 != null) {
			entityLightningBolt8.serverPosX = packet71Weather1.posX;
			entityLightningBolt8.serverPosY = packet71Weather1.posY;
			entityLightningBolt8.serverPosZ = packet71Weather1.posZ;
			entityLightningBolt8.rotationYaw = 0.0F;
			entityLightningBolt8.rotationPitch = 0.0F;
			entityLightningBolt8.entityId = packet71Weather1.entityID;
			this.worldClient.addWeatherEffect(entityLightningBolt8);
		}

	}

	public void handleEntityPainting(Packet25EntityPainting packet25EntityPainting1) {
		EntityPainting entityPainting2 = new EntityPainting(this.worldClient, packet25EntityPainting1.xPosition, 
				packet25EntityPainting1.yPosition, packet25EntityPainting1.zPosition, packet25EntityPainting1.direction, 
				packet25EntityPainting1.title);
		this.worldClient.addEntityToWorld(packet25EntityPainting1.entityId, entityPainting2);
	}

	public void handleEntityVelocity(Packet28EntityVelocity packet28EntityVelocity1) {
		Entity entity2 = this.getEntityByID(packet28EntityVelocity1.entityId);
		if(entity2 != null) {
			entity2.setVelocity((double)packet28EntityVelocity1.motionX / 8000.0D, 
					(double)packet28EntityVelocity1.motionY / 8000.0D, 
					(double)packet28EntityVelocity1.motionZ / 8000.0D);
		}
	}

	public void handleEntityMetadata(Packet40EntityMetadata packet40EntityMetadata1) {
		Entity entity2 = this.getEntityByID(packet40EntityMetadata1.entityId);
		if(entity2 != null && packet40EntityMetadata1.getMetadata() != null) {
			entity2.getDataWatcher().updateWatchedObjectsFromList(packet40EntityMetadata1.getMetadata());
		}

	}

	public void handleNamedEntitySpawn(Packet20NamedEntitySpawn packet20NamedEntitySpawn1) {
		double d2 = (double)packet20NamedEntitySpawn1.xPosition / 32.0D;
		double d4 = (double)packet20NamedEntitySpawn1.yPosition / 32.0D;
		double d6 = (double)packet20NamedEntitySpawn1.zPosition / 32.0D;
		float f8 = (float)(packet20NamedEntitySpawn1.rotation * 360) / 256.0F;
		float f9 = (float)(packet20NamedEntitySpawn1.pitch * 360) / 256.0F;
		EntityOtherPlayerMP entityOtherPlayerMP10 = new EntityOtherPlayerMP(this.mc.theWorld, 
				packet20NamedEntitySpawn1.name);
		entityOtherPlayerMP10.prevPosX = entityOtherPlayerMP10.lastTickPosX = (double)(entityOtherPlayerMP10.serverPosX = packet20NamedEntitySpawn1.xPosition);
		entityOtherPlayerMP10.prevPosY = entityOtherPlayerMP10.lastTickPosY = (double)(entityOtherPlayerMP10.serverPosY = packet20NamedEntitySpawn1.yPosition);
		entityOtherPlayerMP10.prevPosZ = entityOtherPlayerMP10.lastTickPosZ = (double)(entityOtherPlayerMP10.serverPosZ = packet20NamedEntitySpawn1.zPosition);
		int i11 = packet20NamedEntitySpawn1.currentItem;
		if(i11 == 0) {
			entityOtherPlayerMP10.inventory.mainInventory[entityOtherPlayerMP10.inventory.currentItem] = null;
		} else {
			entityOtherPlayerMP10.inventory.mainInventory[entityOtherPlayerMP10.inventory.currentItem] = new ItemStack(
					i11, 1, 0);
		}

		entityOtherPlayerMP10.setPositionAndRotation(d2, d4, d6, f8, f9);
		this.worldClient.addEntityToWorld(packet20NamedEntitySpawn1.entityId, entityOtherPlayerMP10);
	}

	public void handleEntityTeleport(Packet34EntityTeleport packet34EntityTeleport1) {
		Entity entity2 = this.getEntityByID(packet34EntityTeleport1.entityId);
		if(entity2 != null) {
			entity2.serverPosX = packet34EntityTeleport1.xPosition;
			entity2.serverPosY = packet34EntityTeleport1.yPosition;
			entity2.serverPosZ = packet34EntityTeleport1.zPosition;
			double d3 = (double)entity2.serverPosX / 32.0D;
			double d5 = (double)entity2.serverPosY / 32.0D + 0.015625D;
			double d7 = (double)entity2.serverPosZ / 32.0D;
			float f9 = (float)(packet34EntityTeleport1.yaw * 360) / 256.0F;
			float f10 = (float)(packet34EntityTeleport1.pitch * 360) / 256.0F;
			entity2.setPositionAndRotation2(d3, d5, d7, f9, f10, 3);
		}
	}

	public void handleEntity(Packet30Entity packet30Entity1) {
		Entity entity2 = this.getEntityByID(packet30Entity1.entityId);
		if(entity2 != null) {
			entity2.serverPosX += packet30Entity1.xPosition;
			entity2.serverPosY += packet30Entity1.yPosition;
			entity2.serverPosZ += packet30Entity1.zPosition;
			double d3 = (double)entity2.serverPosX / 32.0D;
			double d5 = (double)entity2.serverPosY / 32.0D;
			double d7 = (double)entity2.serverPosZ / 32.0D;
			float f9 = packet30Entity1.rotating ? (float)(packet30Entity1.yaw * 360) / 256.0F : entity2.rotationYaw;
			float f10 = packet30Entity1.rotating ? (float)(packet30Entity1.pitch * 360) / 256.0F : entity2.rotationPitch;
			entity2.setPositionAndRotation2(d3, d5, d7, f9, f10, 3);
		}
	}

	public void handleEntityHeadRotation(Packet35EntityHeadRotation packet35EntityHeadRotation1) {
		Entity entity2 = this.getEntityByID(packet35EntityHeadRotation1.entityId);
		if(entity2 != null) {
			float f3 = (float)(packet35EntityHeadRotation1.headRotationYaw * 360) / 256.0F;
			entity2.setRotationYawHead(f3);
		}
	}

	public void handleDestroyEntity(Packet29DestroyEntity packet29DestroyEntity1) {
		this.worldClient.removeEntityFromWorld(packet29DestroyEntity1.entityId);
	}

	public void handleFlying(Packet10Flying packet10Flying1) {
		EntityPlayerSP entityPlayerSP2 = this.mc.thePlayer;
		double d3 = entityPlayerSP2.posX;
		double d5 = entityPlayerSP2.posY;
		double d7 = entityPlayerSP2.posZ;
		float f9 = entityPlayerSP2.rotationYaw;
		float f10 = entityPlayerSP2.rotationPitch;
		if(packet10Flying1.moving) {
			d3 = packet10Flying1.xPosition;
			d5 = packet10Flying1.yPosition;
			d7 = packet10Flying1.zPosition;
		}

		if(packet10Flying1.rotating) {
			f9 = packet10Flying1.yaw;
			f10 = packet10Flying1.pitch;
		}

		entityPlayerSP2.ySize = 0.0F;
		entityPlayerSP2.motionX = entityPlayerSP2.motionY = entityPlayerSP2.motionZ = 0.0D;
		entityPlayerSP2.setPositionAndRotation(d3, d5, d7, f9, f10);
		packet10Flying1.xPosition = entityPlayerSP2.posX;
		packet10Flying1.yPosition = entityPlayerSP2.boundingBox.minY;
		packet10Flying1.zPosition = entityPlayerSP2.posZ;
		packet10Flying1.stance = entityPlayerSP2.posY;
		this.netManager.addToSendQueue(packet10Flying1);
		if(!this.hasUpdatedPosition) {
			this.mc.thePlayer.prevPosX = this.mc.thePlayer.posX;
			this.mc.thePlayer.prevPosY = this.mc.thePlayer.posY;
			this.mc.thePlayer.prevPosZ = this.mc.thePlayer.posZ;
			this.hasUpdatedPosition = true;
			this.mc.displayGuiScreen((GuiScreen)null);
		}

	}

	public void handlePreChunk(Packet50PreChunk packet50PreChunk1) {
		this.worldClient.doPreChunk(packet50PreChunk1.xPosition, packet50PreChunk1.yPosition, packet50PreChunk1.mode);
	}

	public void handleMultiBlockChange(Packet52MultiBlockChange packet52MultiBlockChange1) {
		int i2 = packet52MultiBlockChange1.xPosition * 16;
		int i3 = packet52MultiBlockChange1.zPosition * 16;
		if(packet52MultiBlockChange1.metadataArray != null) {
			DataInputStream dataInputStream4 = new DataInputStream(new ByteArrayInputStream(packet52MultiBlockChange1.metadataArray));

			try {
				for(int i5 = 0; i5 < packet52MultiBlockChange1.size; ++i5) {
					short s6 = dataInputStream4.readShort();
					short s7 = dataInputStream4.readShort();
					int i8 = (s7 & 4095) >> 4;
					int i9 = s7 & 15;
					int i10 = s6 >> 12 & 15;
					int i11 = s6 >> 8 & 15;
					int i12 = s6 & 255;
					this.worldClient.setBlockAndMetadataAndInvalidate(i10 + i2, i12, i11 + i3, i8, i9);
				}
			} catch (IOException iOException13) {
			}

		}
	}

	public void handleMapChunk(Packet51MapChunk packet) {
		this.worldClient.invalidateBlockReceiveRegion(packet.chunkX << 4, 0, packet.chunkZ << 4, (packet.chunkX << 4) + 15, 256, (packet.chunkZ << 4) + 15);
		Chunk chunk = this.worldClient.getChunkFromChunkCoords(packet.chunkX, packet.chunkZ);
		if(packet.includeInitialize && chunk == null) {
			this.worldClient.doPreChunk(packet.chunkX, packet.chunkZ, true);
			chunk = this.worldClient.getChunkFromChunkCoords(packet.chunkX, packet.chunkZ);
		}

		if(chunk != null) {
			chunk.setChunkData(packet.chunkData, packet.usedSubchunks, packet.blockMSBSubchunks, packet.includeInitialize);
			this.worldClient.markBlocksDirty(packet.chunkX << 4, 0, packet.chunkZ << 4, (packet.chunkX << 4) + 15, 256, (packet.chunkZ << 4) + 15);

		}

	}

	public void handleBlockChange(Packet53BlockChange packet) {
		this.worldClient.setBlockAndMetadataAndInvalidate(
				packet.xPosition, packet.yPosition, packet.zPosition, 
				packet.type, packet.metadata
			);
	}

	public void handleKickDisconnect(Packet255KickDisconnect packet255KickDisconnect1) {
		this.netManager.networkShutdown("disconnect.kicked", new Object[0]);
		this.disconnected = true;
		this.mc.changeWorld((World)null);
		this.mc.displayGuiScreen(new GuiDisconnected("disconnect.disconnected", "disconnect.genericReason", 
				new Object[]{packet255KickDisconnect1.reason}));
	}

	public void handleErrorMessage(String string1, Object[] object2) {
		if(!this.disconnected) {
			this.disconnected = true;
			this.mc.changeWorld((World)null);
			this.mc.displayGuiScreen(new GuiDisconnected("disconnect.lost", string1, object2));
		}
	}

	public void quitWithPacket(Packet packet1) {
		if(!this.disconnected) {
			this.netManager.addToSendQueue(packet1);
			this.netManager.serverShutdown();
		}
	}

	public void addToSendQueue(Packet packet1) {
		if(!this.disconnected) {
			this.netManager.addToSendQueue(packet1);
		}
	}

	public void handleCollect(Packet22Collect packet22Collect1) {
		Entity entity2 = this.getEntityByID(packet22Collect1.collectedEntityId);
		Object object3 = (EntityLiving)this.getEntityByID(packet22Collect1.collectorEntityId);
		if(object3 == null) {
			object3 = this.mc.thePlayer;
		}

		if(entity2 != null) {
			this.worldClient.playSoundAtEntity(entity2, "random.pop", 0.2F, 
						((this.rand.nextFloat() - this.rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
			
			this.mc.effectRenderer.addEffect(new EntityPickupFX(this.mc.theWorld, entity2, (Entity)object3, -0.5F));
			this.worldClient.removeEntityFromWorld(packet22Collect1.collectedEntityId);
		}

	}

	public void handleChat(Packet3Chat packet3Chat1) {
		this.mc.ingameGUI.addChatMessage(packet3Chat1.message);
	}

	public void handleAnimation(Packet18Animation packet) {
		Entity theEntity = this.getEntityByID(packet.entityId);
		if(theEntity != null) {
			EntityPlayer thePlayer;
			if(packet.animate == 1) {
				thePlayer = (EntityPlayer)theEntity;
				thePlayer.swingItem();
				
			} else if(packet.animate == 2) {
				theEntity.performHurtAnimation();
				
			} else if(packet.animate == 3) {
				thePlayer = (EntityPlayer)theEntity;
				thePlayer.wakeUpPlayer(false, false, false);
				
			} else if(packet.animate == 4) {
				thePlayer = (EntityPlayer)theEntity;
				thePlayer.func_6420_o();
				
			} else if(packet.animate == 6) {
				this.mc.effectRenderer.addEffect(new EntityCrit2FX(this.mc.theWorld, theEntity));
				
			} else if(packet.animate == 7) {
				EntityCrit2FX entityCrit2FX4 = new EntityCrit2FX(this.mc.theWorld, theEntity, "magicCrit");
				this.mc.effectRenderer.addEffect(entityCrit2FX4);
				
			} else if(packet.animate == 5 && theEntity instanceof EntityOtherPlayerMP) {
				;
			}

		}
	}

	public void handleSleep(Packet17Sleep packet17Sleep1) {
		Entity entity2 = this.getEntityByID(packet17Sleep1.entityID);
		if(entity2 != null) {
			if(packet17Sleep1.field_22046_e == 0) {
				EntityPlayer entityPlayer3 = (EntityPlayer)entity2;
				entityPlayer3.sleepInBedAt(packet17Sleep1.bedX, packet17Sleep1.bedY, packet17Sleep1.bedZ);
			}

		}
	}

	public void handleHandshake(Packet2Handshake packet2Handshake1) {
		boolean z2 = true;
		String string3 = packet2Handshake1.username;
		if(string3 != null && string3.trim().length() != 0) {
			if(!string3.equals("-")) {
				try {
					Long.parseLong(string3, 16);
				} catch (NumberFormatException numberFormatException8) {
					z2 = false;
				}
			}
		} else {
			z2 = false;
		}

		if(!z2) {
			this.netManager.networkShutdown("disconnect.genericReason", new Object[]{"The server responded with an invalid server key"});
		} else if(packet2Handshake1.username.equals("-")) {
			this.addToSendQueue(new Packet1Login(this.mc.session.username, 29));
		} else {
			try {
				URL uRL4 = new URL("http://session.minecraft.net/game/joinserver.jsp?user=" + this.mc.session.username + "&sessionId=" + this.mc.session.sessionId + "&serverId=" + packet2Handshake1.username);
				BufferedReader bufferedReader5 = new BufferedReader(new InputStreamReader(uRL4.openStream()));
				String string6 = bufferedReader5.readLine();
				bufferedReader5.close();
				if(string6.equalsIgnoreCase("ok")) {
					this.addToSendQueue(new Packet1Login(this.mc.session.username, 29));
				} else {
					this.netManager.networkShutdown("disconnect.loginFailedInfo", new Object[]{string6});
				}
			} catch (Exception exception7) {
				exception7.printStackTrace();
				this.netManager.networkShutdown("disconnect.genericReason", new Object[]{"Internal client error: " + exception7.toString()});
			}
		}

	}

	public void disconnect() {
		this.disconnected = true;
		this.netManager.wakeThreads();
		this.netManager.networkShutdown("disconnect.closed", new Object[0]);
	}

	public void handleMobSpawn(Packet24MobSpawn packet) {
		double x = (double)packet.xPosition / 32.0D;
		double y = (double)packet.yPosition / 32.0D;
		double z = (double)packet.zPosition / 32.0D;
		float yaw = (float)(packet.yaw * 360) / 256.0F;
		float pitch = (float)(packet.pitch * 360) / 256.0F;

		EntityLiving theEntity = (EntityLiving)EntityList.createEntityByID(packet.type, this.mc.theWorld);
		theEntity.serverPosX = packet.xPosition;
		theEntity.serverPosY = packet.yPosition;
		theEntity.serverPosZ = packet.zPosition;
		theEntity.rotationYawHead = (float)(packet.yawHead * 360) / 256.0F;
		
		Entity[] entityParts = theEntity.getParts();
		if(entityParts != null) {
			int ids = packet.entityId - theEntity.entityId;

			for(int i = 0; i < entityParts.length; ++i) {
				entityParts[i].entityId += ids;
			}
		}

		theEntity.entityId = packet.entityId;
		theEntity.setPositionAndRotation(x, y, z, yaw, pitch);

		this.worldClient.addEntityToWorld(packet.entityId, theEntity);
		
		List<WatchableObject> watchables = packet.getMetadata();
		if(watchables != null) {
			theEntity.getDataWatcher().updateWatchedObjectsFromList(watchables);
		}

	}
	
	public void handleArmoredMobSpawn(Packet90ArmoredMobSpawn packet) {
		double x = (double)packet.xPosition / 32.0D;
		double y = (double)packet.yPosition / 32.0D;
		double z = (double)packet.zPosition / 32.0D;
		float yaw = (float)(packet.yaw * 360) / 256.0F;
		float pitch = (float)(packet.pitch * 360) / 256.0F;

		EntityArmoredMob theEntity = (EntityArmoredMob)EntityList.createEntityByID(packet.type, this.mc.theWorld);
		theEntity.serverPosX = packet.xPosition;
		theEntity.serverPosY = packet.yPosition;
		theEntity.serverPosZ = packet.zPosition;
		theEntity.rotationYawHead = (float)(packet.yawHead * 360) / 256.0F;
		
		// Add armor
		for(int i = 0; i < 5; i ++) {
			theEntity.inventory.setInventorySlotContents(i, packet.inventory[i]);
		}
		
		Entity[] entityParts = theEntity.getParts();
		if(entityParts != null) {
			int ids = packet.entityId - theEntity.entityId;

			for(int i = 0; i < entityParts.length; ++i) {
				entityParts[i].entityId += ids;
			}
		}

		theEntity.entityId = packet.entityId;
		theEntity.setPositionAndRotation(x, y, z, yaw, pitch);

		this.worldClient.addEntityToWorld(packet.entityId, theEntity);
		
		List<WatchableObject> watchables = packet.getMetadata();
		if(watchables != null) {
			theEntity.getDataWatcher().updateWatchedObjectsFromList(watchables);
		}

	}
	
	public void handleSetArmor(Packet89SetArmor packet) {
		
		int entityId = packet.entityId;
		int armorType = packet.armorType;
		int itemId = packet.itemId;
		
		if(entityId >= 0 && armorType >= 0 && itemId >= 0) {
			Item item = Item.itemsList[itemId];
			if(item != null) {
				Entity entity = this.worldClient.getEntityByID(packet.entityId);
				
				if(armorType == 100) {
					((EntityLiving)entity).setHeldItem(new ItemStack(item));
				} else if(armorType < 4 && entity instanceof IArmoredMob) {
					((IArmoredMob)entity).setArmor(armorType, new ItemStack(item));
				} else {
					System.out.println ("Warning! Armor received for wrong entity!");
				}
			}
		}
	}

	public void handleUpdateTime(Packet4UpdateTime packet4UpdateTime1) {
		this.mc.theWorld.setWorldTime(packet4UpdateTime1.time);
	}

	public void handleSpawnPosition(Packet6SpawnPosition packet6SpawnPosition1) {
		this.mc.thePlayer.setSpawnChunk(new ChunkCoordinates(packet6SpawnPosition1.xPosition, packet6SpawnPosition1.yPosition, packet6SpawnPosition1.zPosition));
		this.mc.theWorld.getWorldInfo().setSpawnPosition(packet6SpawnPosition1.xPosition, packet6SpawnPosition1.yPosition, packet6SpawnPosition1.zPosition);
	}

	public void handleAttachEntity(Packet39AttachEntity packet39AttachEntity1) {
		Object object2 = this.getEntityByID(packet39AttachEntity1.entityId);
		Entity entity3 = this.getEntityByID(packet39AttachEntity1.vehicleEntityId);
		if(packet39AttachEntity1.entityId == this.mc.thePlayer.entityId) {
			object2 = this.mc.thePlayer;
		}

		if(object2 != null) {
			((Entity)object2).mountEntity(entity3);
		}
	}

	public void handleEntityStatus(Packet38EntityStatus packet38EntityStatus1) {
		Entity entity2 = this.getEntityByID(packet38EntityStatus1.entityId);
		if(entity2 != null) {
			entity2.handleHealthUpdate(packet38EntityStatus1.entityStatus);
		}

	}

	private Entity getEntityByID(int i1) {
		return (Entity)(i1 == this.mc.thePlayer.entityId ? this.mc.thePlayer : this.worldClient.getEntityByID(i1));
	}

	public void handleUpdateHealth(Packet8UpdateHealth packet8UpdateHealth1) {
		this.mc.thePlayer.setHealth(packet8UpdateHealth1.healthMP);
		this.mc.thePlayer.getFoodStats().setFoodLevel(packet8UpdateHealth1.food);
		this.mc.thePlayer.getFoodStats().setFoodSaturationLevel(packet8UpdateHealth1.foodSaturation);
	}

	public void handleExperience(Packet43Experience packet43Experience1) {
		this.mc.thePlayer.setXPStats(packet43Experience1.experience, packet43Experience1.experienceTotal, packet43Experience1.experienceLevel);
	}

	public void handleRespawn(Packet9Respawn packet9Respawn1) {
		System.out.println ("Current dimension: " + this.mc.thePlayer.dimension + ", respawn dimension: " + packet9Respawn1.respawnDimension);
		
		if(packet9Respawn1.respawnDimension != this.mc.thePlayer.dimension) {
			this.hasUpdatedPosition = false;
			this.worldClient = new WorldClient(
				this, 
				new WorldSettings(
					0L, 
					packet9Respawn1.creativeMode, 
					false, 
					false, 
					false, 
					packet9Respawn1.terrainType
				), 
				packet9Respawn1.respawnDimension, 
				packet9Respawn1.difficulty
			);
			this.worldClient.isRemote = true;
			this.mc.changeWorld(this.worldClient);
			this.mc.thePlayer.dimension = packet9Respawn1.respawnDimension;
			this.mc.displayGuiScreen(new GuiDownloadTerrain(this));
		}

		this.mc.respawn(true, packet9Respawn1.respawnDimension, false);
		((PlayerControllerMP)this.mc.playerController).setCreative(packet9Respawn1.creativeMode == 1);
	}

	public void handleExplosion(Packet60Explosion packet60Explosion1) {
		Explosion explosion2 = new Explosion(this.mc.theWorld, (Entity)null, packet60Explosion1.explosionX, packet60Explosion1.explosionY, packet60Explosion1.explosionZ, packet60Explosion1.explosionSize);
		explosion2.destroyedBlockPositions = packet60Explosion1.destroyedBlockPositions;
		explosion2.doExplosionB(true);
	}

	public void handleOpenWindow(Packet100OpenWindow packet100OpenWindow1) {
		EntityPlayerSP entityPlayerSP2 = this.mc.thePlayer;
		switch(packet100OpenWindow1.inventoryType) {
		case 0:
			entityPlayerSP2.displayGUIChest(new InventoryBasic(packet100OpenWindow1.windowTitle, packet100OpenWindow1.slotsCount));
			entityPlayerSP2.craftingInventory.windowId = packet100OpenWindow1.windowId;
			break;
		case 1:
			entityPlayerSP2.displayWorkbenchGUI(MathHelper.floor_double(entityPlayerSP2.posX), MathHelper.floor_double(entityPlayerSP2.posY), MathHelper.floor_double(entityPlayerSP2.posZ));
			entityPlayerSP2.craftingInventory.windowId = packet100OpenWindow1.windowId;
			break;
		case 2:
			entityPlayerSP2.displayGUIFurnace(new TileEntityFurnace());
			entityPlayerSP2.craftingInventory.windowId = packet100OpenWindow1.windowId;
			break;
		case 3:
			entityPlayerSP2.displayGUIDispenser(new TileEntityDispenser());
			entityPlayerSP2.craftingInventory.windowId = packet100OpenWindow1.windowId;
			break;
		}

	}

	public void handleSetSlot(Packet103SetSlot packet103SetSlot1) {
		EntityPlayerSP entityPlayerSP2 = this.mc.thePlayer;
		if(packet103SetSlot1.windowId == -1) {
			entityPlayerSP2.inventory.setItemStack(packet103SetSlot1.myItemStack);
		} else if(packet103SetSlot1.windowId == 0 && packet103SetSlot1.itemSlot >= 36 
				&& packet103SetSlot1.itemSlot < 45) {
			ItemStack itemStack3 = entityPlayerSP2.inventorySlots.getSlot(packet103SetSlot1.itemSlot).getStack();
			if(packet103SetSlot1.myItemStack != null 
					&& (itemStack3 == null || itemStack3.stackSize < packet103SetSlot1.myItemStack.stackSize)) {
				packet103SetSlot1.myItemStack.animationsToGo = 5;
			}

			entityPlayerSP2.inventorySlots.putStackInSlot(packet103SetSlot1.itemSlot, packet103SetSlot1.myItemStack);
		} else if(packet103SetSlot1.windowId == entityPlayerSP2.craftingInventory.windowId) {
			entityPlayerSP2.craftingInventory.putStackInSlot(packet103SetSlot1.itemSlot, 
					packet103SetSlot1.myItemStack);
		}

	}

	public void handleTransaction(Packet106Transaction packet106Transaction1) {
		Container container2 = null;
		EntityPlayerSP entityPlayerSP3 = this.mc.thePlayer;
		if(packet106Transaction1.windowId == 0) {
			container2 = entityPlayerSP3.inventorySlots;
		} else if(packet106Transaction1.windowId == entityPlayerSP3.craftingInventory.windowId) {
			container2 = entityPlayerSP3.craftingInventory;
		}

		if(container2 != null) {
			if(packet106Transaction1.accepted) {
				container2.func_20113_a(packet106Transaction1.shortWindowId);
			} else {
				container2.func_20110_b(packet106Transaction1.shortWindowId);
				this.addToSendQueue(new Packet106Transaction(packet106Transaction1.windowId, packet106Transaction1.shortWindowId, true));
			}
		}

	}

	public void handleWindowItems(Packet104WindowItems packet104WindowItems1) {
		EntityPlayerSP entityPlayerSP2 = this.mc.thePlayer;
		if(packet104WindowItems1.windowId == 0) {
			entityPlayerSP2.inventorySlots.putStacksInSlots(packet104WindowItems1.itemStack);
		} else if(packet104WindowItems1.windowId == entityPlayerSP2.craftingInventory.windowId) {
			entityPlayerSP2.craftingInventory.putStacksInSlots(packet104WindowItems1.itemStack);
		}

	}

	public void handleUpdateSign(Packet130UpdateSign packet130UpdateSign1) {
		if(this.mc.theWorld.blockExists(packet130UpdateSign1.xPosition, packet130UpdateSign1.yPosition, packet130UpdateSign1.zPosition)) {
			TileEntity tileEntity2 = this.mc.theWorld.getBlockTileEntity(packet130UpdateSign1.xPosition, packet130UpdateSign1.yPosition, packet130UpdateSign1.zPosition);
			if(tileEntity2 instanceof TileEntitySign) {
				TileEntitySign tileEntitySign3 = (TileEntitySign)tileEntity2;
				if(tileEntitySign3.isEditable()) {
					for(int i4 = 0; i4 < 4; ++i4) {
						tileEntitySign3.signText[i4] = packet130UpdateSign1.signLines[i4];
					}

					tileEntitySign3.onInventoryChanged();
				}
			}
		}

	}

	public void handleTileEntityData(Packet132TileEntityData packet132TileEntityData1) {
		if(this.mc.theWorld.blockExists(packet132TileEntityData1.xPosition, packet132TileEntityData1.yPosition, packet132TileEntityData1.zPosition)) {
			TileEntity tileEntity2 = this.mc.theWorld.getBlockTileEntity(packet132TileEntityData1.xPosition, packet132TileEntityData1.yPosition, packet132TileEntityData1.zPosition);
			if(tileEntity2 != null && packet132TileEntityData1.actionType == 1 && tileEntity2 instanceof TileEntityMobSpawner) {
				((TileEntityMobSpawner)tileEntity2).setMobID(EntityList.getStringFromID(packet132TileEntityData1.customParam1));
			}
		}

	}

	public void handleUpdateProgressbar(Packet105UpdateProgressbar packet105UpdateProgressbar1) {
		EntityPlayerSP entityPlayerSP2 = this.mc.thePlayer;
		this.registerPacket(packet105UpdateProgressbar1);
		if(entityPlayerSP2.craftingInventory != null && entityPlayerSP2.craftingInventory.windowId == packet105UpdateProgressbar1.windowId) {
			entityPlayerSP2.craftingInventory.updateProgressBar(packet105UpdateProgressbar1.progressBar, packet105UpdateProgressbar1.progressBarValue);
		}

	}

	public void handlePlayerInventory(Packet5PlayerInventory packet5PlayerInventory1) {
		Entity entity2 = this.getEntityByID(packet5PlayerInventory1.entityID);
		if(entity2 != null) {
			entity2.outfitWithItem(packet5PlayerInventory1.slot, packet5PlayerInventory1.itemID, packet5PlayerInventory1.itemDamage);
		}

	}

	public void handleCloseWindow(Packet101CloseWindow packet101CloseWindow1) {
		this.mc.thePlayer.closeScreen();
	}

	public void handlePlayNoteBlock(Packet54PlayNoteBlock packet54PlayNoteBlock1) {
		this.mc.theWorld.playNoteAt(packet54PlayNoteBlock1.xLocation, packet54PlayNoteBlock1.yLocation, packet54PlayNoteBlock1.zLocation, packet54PlayNoteBlock1.instrumentType, packet54PlayNoteBlock1.pitch);
	}

	public void handleBed(Packet70GameEvent packet) {
		EntityPlayerSP thePlayer = this.mc.thePlayer;
		int statusID = packet.bedState;
		
		if(statusID >= 0 && statusID < Packet70GameEvent.bedChat.length && Packet70GameEvent.bedChat[statusID] != null) {
			thePlayer.addChatMessage(Packet70GameEvent.bedChat[statusID]);
		}

		// This is encoded differently to vanilla
		if (statusID == 1) {
			this.worldClient.getWorldInfo().setRaining(packet.raining);
			this.worldClient.setRainStrength(packet.raining ? 1.0F : 0.0F);
			this.worldClient.getWorldInfo().setSnowing(packet.snowing);
			this.worldClient.setSnowingStrength(packet.snowing ? 1.0F : 0.0F);
			this.worldClient.getWorldInfo().setThundering(packet.thundering);
			this.worldClient.setThunderingStrength(packet.thundering ? 1.0F : 0.0F);
		}
		
		// Game mode
		if (statusID == 3) {
			((PlayerControllerMP)this.mc.playerController).setCreative(packet.gameMode == 1);
		}
	}

	public void handleDoorChange(Packet61DoorChange packet61DoorChange1) {
		this.mc.theWorld.playAuxSFX(packet61DoorChange1.sfxID, packet61DoorChange1.posX, packet61DoorChange1.posY, packet61DoorChange1.posZ, packet61DoorChange1.auxData);
	}

	public boolean isServerHandler() {
		return false;
	}

	public void handlePlayerInfo(Packet201PlayerInfo packet201PlayerInfo1) {
		GuiPlayerInfo guiPlayerInfo2 = (GuiPlayerInfo)this.playerInfoMap.get(packet201PlayerInfo1.playerName);
		if(guiPlayerInfo2 == null && packet201PlayerInfo1.isConnected) {
			guiPlayerInfo2 = new GuiPlayerInfo(packet201PlayerInfo1.playerName);
			this.playerInfoMap.put(packet201PlayerInfo1.playerName, guiPlayerInfo2);
			this.playerNames.add(guiPlayerInfo2);
		}

		if(guiPlayerInfo2 != null && !packet201PlayerInfo1.isConnected) {
			this.playerInfoMap.remove(packet201PlayerInfo1.playerName);
			this.playerNames.remove(guiPlayerInfo2);
		}

		if(packet201PlayerInfo1.isConnected && guiPlayerInfo2 != null) {
			guiPlayerInfo2.responseTime = packet201PlayerInfo1.ping;
		}

	}

	public void handleKeepAlive(Packet0KeepAlive packet0KeepAlive1) {
		this.addToSendQueue(new Packet0KeepAlive(packet0KeepAlive1.randomId));
	}

	public void func_50100_a(Packet202PlayerAbilities packet202PlayerAbilities1) {
		EntityPlayerSP entityPlayerSP2 = this.mc.thePlayer;
		entityPlayerSP2.capabilities.isFlying = packet202PlayerAbilities1.isFlying;
		entityPlayerSP2.capabilities.isCreativeMode = packet202PlayerAbilities1.isCreativeMode;
		entityPlayerSP2.capabilities.disableDamage = packet202PlayerAbilities1.disableDamage;
		entityPlayerSP2.capabilities.allowFlying = packet202PlayerAbilities1.allowFlying;
	}
	
	public void handleUpdateDayOfTheYear(Packet95UpdateDayOfTheYear packet) {
		System.out.println("Got day of the year = " + packet.dayOfTheYear);
		this.worldClient.performDayOfTheYearUpdate(packet.dayOfTheYear);
	}
	
	public void handleUpdateCommandBlock(Packet91UpdateCommandBlock packet91UpdateCommandBlock) {
		int x = packet91UpdateCommandBlock.x;
		int y = packet91UpdateCommandBlock.y;
		int z = packet91UpdateCommandBlock.z;
		
		if(this.mc.theWorld.blockExists(x, y, z)) {
			TileEntity tileEntity = this.mc.theWorld.getBlockTileEntity(x, y, z);
			if(tileEntity instanceof TileEntityCommandBlock) {
				TileEntityCommandBlock tileEntityCommandBlock = (TileEntityCommandBlock) tileEntity;
				
				tileEntityCommandBlock.command = packet91UpdateCommandBlock.command;
				
				tileEntityCommandBlock.onInventoryChanged();
			}
		}
	}
	
	public void handleMapData(Packet131MapData packet131MapData1) {
		if(packet131MapData1.itemID == Item.map.shiftedIndex) {
			ItemMap.getMPMapData(packet131MapData1.uniqueID, this.mc.theWorld).func_28171_a(packet131MapData1.itemData);
		} else {
			System.out.println("Unknown itemid: " + packet131MapData1.uniqueID);
		}

	}
}
