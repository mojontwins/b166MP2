package net.minecraft.server.player;

import net.minecraft.world.entity.player.EntityPlayer;

public interface IPlayerFileData {
	void writePlayerData(EntityPlayer entityPlayer1);

	void readPlayerData(EntityPlayer entityPlayer1);

	String[] s_func_52007_g();
}
