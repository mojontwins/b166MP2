package net.minecraft.src;

import net.minecraft.server.MinecraftServer;

public class WorldServerMulti extends WorldServer {
	public WorldServerMulti(MinecraftServer minecraftServer1, ISaveHandler iSaveHandler2, String string3, int i4, long j5, WorldServer worldServer7) {
		super(minecraftServer1, iSaveHandler2, string3, i4, j5);
		this.field_28105_z = worldServer7.field_28105_z;
	}
}
