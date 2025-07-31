package net.minecraft.server;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.network.packet.Packet38EntityStatus;
import net.minecraft.network.packet.Packet54PlayNoteBlock;
import net.minecraft.network.packet.Packet60Explosion;
import net.minecraft.network.packet.Packet70GameEvent;
import net.minecraft.network.packet.Packet71Weather;
import net.minecraft.src.MathHelper;
import net.minecraft.world.IntHashMap;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.INpc;
import net.minecraft.world.entity.animal.EntityAnimal;
import net.minecraft.world.entity.animal.EntityWaterMob;
import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.World;
import net.minecraft.world.level.WorldSettings;
import net.minecraft.world.level.WorldType;
import net.minecraft.world.level.chunk.IChunkLoader;
import net.minecraft.world.level.chunk.IChunkProvider;
import net.minecraft.world.level.chunk.storage.ISaveHandler;
import net.minecraft.world.level.dimension.WorldProvider;
import net.minecraft.world.level.tile.entity.TileEntity;

public class WorldServer extends World {
	public ChunkProviderServer chunkProviderServer;
	public boolean disableSpawnProtection = false;
	public boolean levelSaving;
	private MinecraftServer mcServer;
	private IntHashMap entityInstanceIdMap;

	public WorldServer(MinecraftServer minecraftServer1, ISaveHandler iSaveHandler2, String string3, int i4, WorldSettings worldSettings5, WorldType worldType) {
		super(iSaveHandler2, string3, worldSettings5, WorldProvider.getProviderForDimension(i4, worldType));
		this.mcServer = minecraftServer1;
		if(this.entityInstanceIdMap == null) {
			this.entityInstanceIdMap = new IntHashMap();
		}

	}

	public void updateEntityWithOptionalForce(Entity entity1, boolean z2) {
		if(!this.mcServer.spawnPeacefulMobs && (entity1 instanceof EntityAnimal || entity1 instanceof EntityWaterMob)) {
			entity1.setDead();
		}

		if(!this.mcServer.s_field_44002_p && entity1 instanceof INpc) {
			entity1.setDead();
		}

		if(entity1.riddenByEntity == null || !(entity1.riddenByEntity instanceof EntityPlayer)) {
			super.updateEntityWithOptionalForce(entity1, z2);
		}

	}

	public void s_func_12017_b(Entity entity1, boolean z2) {
		super.updateEntityWithOptionalForce(entity1, z2);
	}

	protected IChunkProvider createChunkProvider() {
		IChunkLoader iChunkLoader1 = this.saveHandler.getChunkLoader(this.worldProvider);
		this.chunkProviderServer = new ChunkProviderServer(this, iChunkLoader1, this.worldProvider.getChunkProvider());
		return this.chunkProviderServer;
	}

	public List<TileEntity> getTileEntityList(int i1, int i2, int i3, int i4, int i5, int i6) {
		ArrayList<TileEntity> arrayList7 = new ArrayList<TileEntity>();

		for(int i8 = 0; i8 < this.loadedTileEntityList.size(); ++i8) {
			TileEntity tileEntity9 = (TileEntity)this.loadedTileEntityList.get(i8);
			if(tileEntity9.xCoord >= i1 && tileEntity9.yCoord >= i2 && tileEntity9.zCoord >= i3 && tileEntity9.xCoord < i4 && tileEntity9.yCoord < i5 && tileEntity9.zCoord < i6) {
				arrayList7.add(tileEntity9);
			}
		}

		return arrayList7;
	}

	public boolean canMineBlock(EntityPlayer entityPlayer1, int i2, int i3, int i4) {
		int i5 = MathHelper.abs(i2 - this.worldInfo.getSpawnX());
		int i6 = MathHelper.abs(i4 - this.worldInfo.getSpawnZ());
		if(i5 > i6) {
			i6 = i5;
		}

		return i6 > 16 || this.mcServer.configManager.isOp(entityPlayer1.username);
	}

	protected void generateSpawnPoint() {
		if(this.entityInstanceIdMap == null) {
			this.entityInstanceIdMap = new IntHashMap();
		}

		super.generateSpawnPoint();
	}

	protected void obtainEntitySkin(Entity entity1) {
		super.obtainEntitySkin(entity1);
		this.entityInstanceIdMap.addKey(entity1.entityId, entity1);
		Entity[] entity2 = entity1.getParts();
		if(entity2 != null) {
			for(int i3 = 0; i3 < entity2.length; ++i3) {
				this.entityInstanceIdMap.addKey(entity2[i3].entityId, entity2[i3]);
			}
		}

	}

	protected void releaseEntitySkin(Entity entity1) {
		super.releaseEntitySkin(entity1);
		this.entityInstanceIdMap.removeObject(entity1.entityId);
		Entity[] entity2 = entity1.getParts();
		if(entity2 != null) {
			for(int i3 = 0; i3 < entity2.length; ++i3) {
				this.entityInstanceIdMap.removeObject(entity2[i3].entityId);
			}
		}

	}

	public Entity getEntityById(int i1) {
		return (Entity)this.entityInstanceIdMap.lookup(i1);
	}

	public boolean addWeatherEffect(Entity entity1) {
		if(super.addWeatherEffect(entity1)) {
			this.mcServer.configManager.sendPacketToPlayersAroundPoint(entity1.posX, entity1.posY, entity1.posZ, 512.0D, this.worldProvider.worldType, new Packet71Weather(entity1));
			return true;
		} else {
			return false;
		}
	}

	public void setEntityState(Entity entity1, byte b2) {
		Packet38EntityStatus packet38EntityStatus3 = new Packet38EntityStatus(entity1.entityId, b2);
		this.mcServer.getEntityTracker(this.worldProvider.worldType).sendPacketToTrackedPlayersAndTrackedEntity(entity1, packet38EntityStatus3);
	}

	public Explosion newExplosion(Entity entity1, double d2, double d4, double d6, float f8, boolean z9) {
		Explosion explosion10 = new Explosion(this, entity1, d2, d4, d6, f8);
		explosion10.isFlaming = z9;
		explosion10.doExplosionA();
		explosion10.doExplosionB(false);
		this.mcServer.configManager.sendPacketToPlayersAroundPoint(d2, d4, d6, 64.0D, this.worldProvider.worldType, new Packet60Explosion(d2, d4, d6, f8, explosion10.destroyedBlockPositions));
		return explosion10;
	}

	public void playNoteAt(int i1, int i2, int i3, int i4, int i5) {
		super.playNoteAt(i1, i2, i3, i4, i5);
		this.mcServer.configManager.sendPacketToPlayersAroundPoint((double)i1, (double)i2, (double)i3, 64.0D, this.worldProvider.worldType, new Packet54PlayNoteBlock(i1, i2, i3, i4, i5));
	}

	public void s_func_30006_w() {
		this.saveHandler.s_func_22093_e();
	}

	protected void updateWeather() {
		boolean snowing = this.worldInfo.isSnowing();
		boolean raining = this.worldInfo.isRaining();
		boolean thundering = this.worldInfo.isThundering();
		
		super.updateWeather();

		if (snowing != this.worldInfo.isSnowing() || raining != this.worldInfo.isRaining() || thundering != this.worldInfo.isThundering()) {
			this.mcServer.configManager.sendPacketToAllPlayers(new Packet70GameEvent(this.worldInfo.isRaining(), this.worldInfo.isSnowing(), this.worldInfo.isThundering()));
		}

	}
	
}
