package net.minecraft.world.level.chunk;

import java.util.List;

import net.minecraft.world.entity.EnumCreatureType;
import net.minecraft.world.level.ChunkPosition;
import net.minecraft.world.level.SpawnListEntry;
import net.minecraft.world.level.World;
import net.minecraft.world.level.chunk.storage.IProgressUpdate;

public interface IChunkProvider {
	boolean chunkExists(int i1, int i2);

	Chunk provideChunk(int i1, int i2);

	Chunk loadChunk(int i1, int i2);

	void populate(IChunkProvider iChunkProvider1, int i2, int i3);

	boolean saveChunks(boolean z1, IProgressUpdate iProgressUpdate2);

	boolean unload100OldestChunks();

	boolean canSave();

	String makeString();

	List<SpawnListEntry> getPossibleCreatures(EnumCreatureType enumCreatureType1, int i2, int i3, int i4);

	ChunkPosition findClosestStructure(World world1, String string2, int i3, int i4, int i5);

	public Chunk justGenerate(int posX, int posZ);

}
