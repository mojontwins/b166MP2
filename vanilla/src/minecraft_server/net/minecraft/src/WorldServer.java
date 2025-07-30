package net.minecraft.src;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.server.MinecraftServer;

public class WorldServer extends World {
	public ChunkProviderServer chunkProviderServer;
	public boolean field_819_z = false;
	public boolean levelSaving;
	private MinecraftServer mcServer;
	private MCHash field_20912_E = new MCHash();

	public WorldServer(MinecraftServer minecraftServer1, ISaveHandler iSaveHandler2, String string3, int i4, long j5) {
		super(iSaveHandler2, string3, j5, WorldProvider.func_4091_a(i4));
		this.mcServer = minecraftServer1;
	}

	public void updateEntityWithOptionalForce(Entity entity1, boolean z2) {
		if(!this.mcServer.spawnPeacefulMobs && (entity1 instanceof EntityAnimal || entity1 instanceof EntityWaterMob)) {
			entity1.setEntityDead();
		}

		if(entity1.riddenByEntity == null || !(entity1.riddenByEntity instanceof EntityPlayer)) {
			super.updateEntityWithOptionalForce(entity1, z2);
		}

	}

	public void func_12017_b(Entity entity1, boolean z2) {
		super.updateEntityWithOptionalForce(entity1, z2);
	}

	protected IChunkProvider func_22086_b() {
		IChunkLoader iChunkLoader1 = this.worldFile.func_22092_a(this.worldProvider);
		this.chunkProviderServer = new ChunkProviderServer(this, iChunkLoader1, this.worldProvider.func_2938281_a());
		return this.chunkProviderServer;
	}

	public List getTileEntityList(int i1, int i2, int i3, int i4, int i5, int i6) {
		ArrayList arrayList7 = new ArrayList();

		for(int i8 = 0; i8 < this.loadedTileEntityList.size(); ++i8) {
			TileEntity tileEntity9 = (TileEntity)this.loadedTileEntityList.get(i8);
			if(tileEntity9.xCoord >= i1 && tileEntity9.yCoord >= i2 && tileEntity9.zCoord >= i3 && tileEntity9.xCoord < i4 && tileEntity9.yCoord < i5 && tileEntity9.zCoord < i6) {
				arrayList7.add(tileEntity9);
			}
		}

		return arrayList7;
	}

	public boolean canMineBlock(EntityPlayer entityPlayer1, int i2, int i3, int i4) {
		int i5 = (int)MathHelper.abs((float)(i2 - this.worldInfo.getSpawnX()));
		int i6 = (int)MathHelper.abs((float)(i4 - this.worldInfo.getSpawnZ()));
		if(i5 > i6) {
			i6 = i5;
		}

		return i6 > 16 || this.mcServer.configManager.isOp(entityPlayer1.username);
	}

	protected void obtainEntitySkin(Entity entity1) {
		super.obtainEntitySkin(entity1);
		this.field_20912_E.addKey(entity1.entityId, entity1);
	}

	protected void releaseEntitySkin(Entity entity1) {
		super.releaseEntitySkin(entity1);
		this.field_20912_E.removeObject(entity1.entityId);
	}

	public Entity func_6158_a(int i1) {
		return (Entity)this.field_20912_E.lookup(i1);
	}

	public boolean func_27073_a(Entity entity1) {
		if(super.func_27073_a(entity1)) {
			this.mcServer.configManager.sendPacketToPlayersAroundPoint(entity1.posX, entity1.posY, entity1.posZ, 512.0D, this.worldProvider.worldType, new Packet71Weather(entity1));
			return true;
		} else {
			return false;
		}
	}

	public void func_9206_a(Entity entity1, byte b2) {
		Packet38EntityStatus packet38EntityStatus3 = new Packet38EntityStatus(entity1.entityId, b2);
		this.mcServer.getEntityTracker(this.worldProvider.worldType).sendPacketToTrackedPlayersAndTrackedEntity(entity1, packet38EntityStatus3);
	}

	public Explosion newExplosion(Entity entity1, double d2, double d4, double d6, float f8, boolean z9) {
		Explosion explosion10 = new Explosion(this, entity1, d2, d4, d6, f8);
		explosion10.isFlaming = z9;
		explosion10.doExplosion();
		explosion10.doEffects(false);
		this.mcServer.configManager.sendPacketToPlayersAroundPoint(d2, d4, d6, 64.0D, this.worldProvider.worldType, new Packet60Explosion(d2, d4, d6, f8, explosion10.destroyedBlockPositions));
		return explosion10;
	}

	public void playNoteAt(int i1, int i2, int i3, int i4, int i5) {
		super.playNoteAt(i1, i2, i3, i4, i5);
		this.mcServer.configManager.sendPacketToPlayersAroundPoint((double)i1, (double)i2, (double)i3, 64.0D, this.worldProvider.worldType, new Packet54PlayNoteBlock(i1, i2, i3, i4, i5));
	}

	public void func_30007_w() {
		this.worldFile.func_22093_e();
	}

	protected void func_27066_i() {
		boolean z1 = this.func_27068_v();
		super.func_27066_i();
		if(z1 != this.func_27068_v()) {
			if(z1) {
				this.mcServer.configManager.sendPacketToAllPlayers(new Packet70Bed(2));
			} else {
				this.mcServer.configManager.sendPacketToAllPlayers(new Packet70Bed(1));
			}
		}

	}
}
