package net.minecraft.server;

import net.minecraft.world.level.WorldSettings;
import net.minecraft.world.level.WorldType;
import net.minecraft.world.level.chunk.storage.ISaveHandler;

public class WorldServerMulti extends WorldServer {
	public WorldServerMulti(MinecraftServer minecraftServer1, ISaveHandler iSaveHandler2, String string3, int i4, WorldSettings worldSettings5, WorldServer worldServer6, WorldType worldType) {
		super(minecraftServer1, iSaveHandler2, string3, i4, worldSettings5, worldType);
		this.mapStorage = worldServer6.mapStorage;
	}
}
