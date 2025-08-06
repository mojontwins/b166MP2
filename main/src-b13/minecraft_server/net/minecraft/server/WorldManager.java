package net.minecraft.server;

import net.minecraft.network.packet.Packet61DoorChange;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.level.IWorldAccess;
import net.minecraft.world.level.tile.entity.TileEntity;

public class WorldManager implements IWorldAccess {
	private MinecraftServer mcServer;
	private WorldServer world;

	public WorldManager(MinecraftServer minecraftServer1, WorldServer worldServer2) {
		this.mcServer = minecraftServer1;
		this.world = worldServer2;
	}

	public void spawnParticle(String string1, double d2, double d4, double d6, double d8, double d10, double d12) {
	}

	public void obtainEntitySkin(Entity entity1) {
		this.mcServer.getEntityTracker(this.world.worldProvider.worldType).trackEntity(entity1);
	}

	public void releaseEntitySkin(Entity entity1) {
		this.mcServer.getEntityTracker(this.world.worldProvider.worldType).untrackEntity(entity1);
	}

	public void playSound(String string1, double d2, double d4, double d6, float f8, float f9) {
	}

	public void markBlockRangeNeedsUpdate(int i1, int i2, int i3, int i4, int i5, int i6) {
	}

	public void markBlockNeedsUpdate(int i1, int i2, int i3) {
		this.mcServer.configManager.markBlockNeedsUpdate(i1, i2, i3, this.world.worldProvider.worldType);
	}

	public void updateAllRenderers() {
	}
	
	public void markBlockNeedsUpdate2(int i1, int i2, int i3) {
	}

	public void playRecord(String string1, int i2, int i3, int i4) {
	}

	public void doNothingWithTileEntity(int i1, int i2, int i3, TileEntity tileEntity4) {
		this.mcServer.configManager.sentTileEntityToPlayer(i1, i2, i3, tileEntity4);
	}

	public void playAuxSFX(EntityPlayer entityPlayer1, int i2, int i3, int i4, int i5, int i6) {
		this.mcServer.configManager.s_func_28171_a(entityPlayer1, (double)i3, (double)i4, (double)i5, 64.0D, this.world.worldProvider.worldType, new Packet61DoorChange(i2, i3, i4, i5, i6));
	}
	
	public void showString(String s) {
		this.mcServer.configManager.sendChatMessageToAllPlayers(s);
	}
}
