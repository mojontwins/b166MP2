package net.minecraft.src;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ChunkProviderServer implements IChunkProvider {
	private Set field_725_a = new HashSet();
	private Chunk dummyChunk;
	private IChunkProvider serverChunkGenerator;
	private IChunkLoader field_729_d;
	public boolean chunkLoadOverride = false;
	private Map id2ChunkMap = new HashMap();
	private List field_727_f = new ArrayList();
	private WorldServer world;

	public ChunkProviderServer(WorldServer worldServer1, IChunkLoader iChunkLoader2, IChunkProvider iChunkProvider3) {
		this.dummyChunk = new EmptyChunk(worldServer1, new byte[32768], 0, 0);
		this.world = worldServer1;
		this.field_729_d = iChunkLoader2;
		this.serverChunkGenerator = iChunkProvider3;
	}

	public boolean chunkExists(int i1, int i2) {
		return this.id2ChunkMap.containsKey(ChunkCoordIntPair.chunkXZ2Int(i1, i2));
	}

	public void func_374_c(int i1, int i2) {
		ChunkCoordinates chunkCoordinates3 = this.world.getSpawnPoint();
		int i4 = i1 * 16 + 8 - chunkCoordinates3.posX;
		int i5 = i2 * 16 + 8 - chunkCoordinates3.posZ;
		short s6 = 128;
		if(i4 < -s6 || i4 > s6 || i5 < -s6 || i5 > s6) {
			this.field_725_a.add(ChunkCoordIntPair.chunkXZ2Int(i1, i2));
		}

	}

	public Chunk loadChunk(int i1, int i2) {
		int i3 = ChunkCoordIntPair.chunkXZ2Int(i1, i2);
		this.field_725_a.remove(i3);
		Chunk chunk4 = (Chunk)this.id2ChunkMap.get(i3);
		if(chunk4 == null) {
			chunk4 = this.func_4063_e(i1, i2);
			if(chunk4 == null) {
				if(this.serverChunkGenerator == null) {
					chunk4 = this.dummyChunk;
				} else {
					chunk4 = this.serverChunkGenerator.provideChunk(i1, i2);
				}
			}

			this.id2ChunkMap.put(i3, chunk4);
			this.field_727_f.add(chunk4);
			if(chunk4 != null) {
				chunk4.func_4053_c();
				chunk4.onChunkLoad();
			}

			if(!chunk4.isTerrainPopulated && this.chunkExists(i1 + 1, i2 + 1) && this.chunkExists(i1, i2 + 1) && this.chunkExists(i1 + 1, i2)) {
				this.populate(this, i1, i2);
			}

			if(this.chunkExists(i1 - 1, i2) && !this.provideChunk(i1 - 1, i2).isTerrainPopulated && this.chunkExists(i1 - 1, i2 + 1) && this.chunkExists(i1, i2 + 1) && this.chunkExists(i1 - 1, i2)) {
				this.populate(this, i1 - 1, i2);
			}

			if(this.chunkExists(i1, i2 - 1) && !this.provideChunk(i1, i2 - 1).isTerrainPopulated && this.chunkExists(i1 + 1, i2 - 1) && this.chunkExists(i1, i2 - 1) && this.chunkExists(i1 + 1, i2)) {
				this.populate(this, i1, i2 - 1);
			}

			if(this.chunkExists(i1 - 1, i2 - 1) && !this.provideChunk(i1 - 1, i2 - 1).isTerrainPopulated && this.chunkExists(i1 - 1, i2 - 1) && this.chunkExists(i1, i2 - 1) && this.chunkExists(i1 - 1, i2)) {
				this.populate(this, i1 - 1, i2 - 1);
			}
		}

		return chunk4;
	}

	public Chunk provideChunk(int i1, int i2) {
		Chunk chunk3 = (Chunk)this.id2ChunkMap.get(ChunkCoordIntPair.chunkXZ2Int(i1, i2));
		return chunk3 == null ? (!this.world.worldChunkLoadOverride && !this.chunkLoadOverride ? this.dummyChunk : this.loadChunk(i1, i2)) : chunk3;
	}

	private Chunk func_4063_e(int i1, int i2) {
		if(this.field_729_d == null) {
			return null;
		} else {
			try {
				Chunk chunk3 = this.field_729_d.loadChunk(this.world, i1, i2);
				if(chunk3 != null) {
					chunk3.lastSaveTime = this.world.getWorldTime();
				}

				return chunk3;
			} catch (Exception exception4) {
				exception4.printStackTrace();
				return null;
			}
		}
	}

	private void func_375_a(Chunk chunk1) {
		if(this.field_729_d != null) {
			try {
				this.field_729_d.saveExtraChunkData(this.world, chunk1);
			} catch (Exception exception3) {
				exception3.printStackTrace();
			}

		}
	}

	private void func_373_b(Chunk chunk1) {
		if(this.field_729_d != null) {
			try {
				chunk1.lastSaveTime = this.world.getWorldTime();
				this.field_729_d.saveChunk(this.world, chunk1);
			} catch (IOException iOException3) {
				iOException3.printStackTrace();
			}

		}
	}

	public void populate(IChunkProvider iChunkProvider1, int i2, int i3) {
		Chunk chunk4 = this.provideChunk(i2, i3);
		if(!chunk4.isTerrainPopulated) {
			chunk4.isTerrainPopulated = true;
			if(this.serverChunkGenerator != null) {
				this.serverChunkGenerator.populate(iChunkProvider1, i2, i3);
				chunk4.setChunkModified();
			}
		}

	}

	public boolean saveChunks(boolean z1, IProgressUpdate iProgressUpdate2) {
		int i3 = 0;

		for(int i4 = 0; i4 < this.field_727_f.size(); ++i4) {
			Chunk chunk5 = (Chunk)this.field_727_f.get(i4);
			if(z1 && !chunk5.neverSave) {
				this.func_375_a(chunk5);
			}

			if(chunk5.needsSaving(z1)) {
				this.func_373_b(chunk5);
				chunk5.isModified = false;
				++i3;
				if(i3 == 24 && !z1) {
					return false;
				}
			}
		}

		if(z1) {
			if(this.field_729_d == null) {
				return true;
			}

			this.field_729_d.saveExtraData();
		}

		return true;
	}

	public boolean func_361_a() {
		if(!this.world.levelSaving) {
			for(int i1 = 0; i1 < 100; ++i1) {
				if(!this.field_725_a.isEmpty()) {
					Integer integer2 = (Integer)this.field_725_a.iterator().next();
					Chunk chunk3 = (Chunk)this.id2ChunkMap.get(integer2);
					chunk3.onChunkUnload();
					this.func_373_b(chunk3);
					this.func_375_a(chunk3);
					this.field_725_a.remove(integer2);
					this.id2ChunkMap.remove(integer2);
					this.field_727_f.remove(chunk3);
				}
			}

			if(this.field_729_d != null) {
				this.field_729_d.func_661_a();
			}
		}

		return this.serverChunkGenerator.func_361_a();
	}

	public boolean func_364_b() {
		return !this.world.levelSaving;
	}
}
