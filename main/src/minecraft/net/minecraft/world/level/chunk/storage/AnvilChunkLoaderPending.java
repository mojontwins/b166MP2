package net.minecraft.world.level.chunk.storage;

import com.mojang.nbt.NBTTagCompound;

import net.minecraft.world.level.chunk.ChunkCoordIntPair;

class AnvilChunkLoaderPending {
	public final ChunkCoordIntPair field_48427_a;
	public final NBTTagCompound field_48426_b;

	public AnvilChunkLoaderPending(ChunkCoordIntPair chunkCoordIntPair1, NBTTagCompound nBTTagCompound2) {
		this.field_48427_a = chunkCoordIntPair1;
		this.field_48426_b = nBTTagCompound2;
	}
}
