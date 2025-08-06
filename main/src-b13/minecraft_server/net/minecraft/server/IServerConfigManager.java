package net.minecraft.server;

import net.minecraft.server.player.EntityPlayerMP;

public interface IServerConfigManager {

	public EntityPlayerMP getPlayerEntity(String string);

	public MinecraftServer getServer();

}
