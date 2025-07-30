package net.minecraft.world.level.chunk;

import java.io.IOException;

import net.minecraft.world.level.World;

public interface IChunkLoader {
	Chunk loadChunk(World world1, int i2, int i3) throws IOException;

	void saveChunk(World world1, Chunk chunk2) throws IOException;

	void saveExtraChunkData(World world1, Chunk chunk2) throws IOException;

	void chunkTick();

	void saveExtraData();
}
