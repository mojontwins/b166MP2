package net.minecraft.client.multiplayer;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.world.LongHashMap;
import net.minecraft.world.entity.EnumCreatureType;
import net.minecraft.world.level.ChunkPosition;
import net.minecraft.world.level.SpawnListEntry;
import net.minecraft.world.level.World;
import net.minecraft.world.level.chunk.Chunk;
import net.minecraft.world.level.chunk.ChunkCoordIntPair;
import net.minecraft.world.level.chunk.EmptyChunk;
import net.minecraft.world.level.chunk.IChunkProvider;
import net.minecraft.world.level.chunk.storage.IProgressUpdate;

public class ChunkProviderClient implements IChunkProvider {
	private Chunk blankChunk;
	private LongHashMap chunkMapping = new LongHashMap();
	private List<Chunk> chunkList = new ArrayList<Chunk>();
	private World worldObj;

	public ChunkProviderClient(World world1) {
		this.blankChunk = new EmptyChunk(world1, 0, 0);
		this.worldObj = world1;
	}

	public boolean chunkExists(int i1, int i2) {
		return true;
	}

	public void func_539_c(int i1, int i2) {
		Chunk chunk3 = this.provideChunk(i1, i2);
		if(!chunk3.isEmpty()) {
			chunk3.onChunkUnload();
		}

		this.chunkMapping.remove(ChunkCoordIntPair.chunkXZ2Int(i1, i2));
		this.chunkList.remove(chunk3);
	}

	public Chunk loadChunk(int i1, int i2) {
		Chunk chunk3 = new Chunk(this.worldObj, i1, i2);
		this.chunkMapping.add(ChunkCoordIntPair.chunkXZ2Int(i1, i2), chunk3);
		chunk3.isChunkLoaded = true;
		return chunk3;
	}

	public Chunk provideChunk(int i1, int i2) {
		Chunk chunk3 = (Chunk)this.chunkMapping.getValueByKey(ChunkCoordIntPair.chunkXZ2Int(i1, i2));
		return chunk3 == null ? this.blankChunk : chunk3;
	}

	public boolean saveChunks(boolean z1, IProgressUpdate iProgressUpdate2) {
		return true;
	}
	
	public Chunk justGenerate(int posX, int posZ) {
		return this.provideChunk(posX, posZ);
	}

	public boolean unload100OldestChunks() {
		return false;
	}

	public boolean canSave() {
		return false;
	}

	public void populate(IChunkProvider iChunkProvider1, int i2, int i3) {
	}

	public String makeString() {
		return "MultiplayerChunkCache: " + this.chunkMapping.getNumHashElements();
	}

	public List<SpawnListEntry> getPossibleCreatures(EnumCreatureType enumCreatureType1, int i2, int i3, int i4) {
		return null;
	}

	public ChunkPosition findClosestStructure(World world1, String string2, int i3, int i4, int i5) {
		return null;
	}
}
