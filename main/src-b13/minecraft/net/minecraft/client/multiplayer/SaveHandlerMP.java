package net.minecraft.client.multiplayer;

import java.io.File;
import java.util.List;

import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.level.WorldInfo;
import net.minecraft.world.level.chunk.IChunkLoader;
import net.minecraft.world.level.chunk.storage.ISaveHandler;
import net.minecraft.world.level.dimension.WorldProvider;

public class SaveHandlerMP implements ISaveHandler {
	public WorldInfo loadWorldInfo() {
		return null;
	}

	public void checkSessionLock() {
	}

	public IChunkLoader getChunkLoader(WorldProvider worldProvider1) {
		return null;
	}

	public void saveWorldInfoAndPlayer(WorldInfo worldInfo1, List<EntityPlayer> list2) {
	}

	public void saveWorldInfo(WorldInfo worldInfo1) {
	}

	public File getMapFileFromName(String string1) {
		return null;
	}

	public String getSaveDirectoryName() {
		return "none";
	}

	@Override
	public ISaveHandler getPlayerNBTManager() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void s_func_22093_e() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void writePlayerData(EntityPlayer entityPlayer1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void readPlayerData(EntityPlayer entityPlayer1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String[] s_func_52007_g() {
		// TODO Auto-generated method stub
		return null;
	}
}
