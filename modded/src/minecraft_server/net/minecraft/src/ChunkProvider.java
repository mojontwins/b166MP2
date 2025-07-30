package net.minecraft.src;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ChunkProvider implements IChunkProvider {
	private Set field_28062_a = new HashSet();
	private Chunk field_28061_b;
	private IChunkProvider chunkGenerator;
	private IChunkLoader field_28066_d;
	private Map field_28065_e = new HashMap();
	private List field_28064_f = new ArrayList();
	private World field_28063_g;

	public ChunkProvider(World world1, IChunkLoader iChunkLoader2, IChunkProvider iChunkProvider3) {
		this.field_28061_b = new EmptyChunk(world1, new byte[32768], 0, 0);
		this.field_28063_g = world1;
		this.field_28066_d = iChunkLoader2;
		this.chunkGenerator = iChunkProvider3;
	}

	public boolean chunkExists(int i1, int i2) {
		return this.field_28065_e.containsKey(ChunkCoordIntPair.chunkXZ2Int(i1, i2));
	}

	public Chunk loadChunk(int i1, int i2) {
		int i3 = ChunkCoordIntPair.chunkXZ2Int(i1, i2);
		this.field_28062_a.remove(i3);
		Chunk chunk4 = (Chunk)this.field_28065_e.get(i3);
		if(chunk4 == null) {
			chunk4 = this.func_28058_d(i1, i2);
			if(chunk4 == null) {
				if(this.chunkGenerator == null) {
					chunk4 = this.field_28061_b;
				} else {
					chunk4 = this.chunkGenerator.provideChunk(i1, i2);
				}
			}

			this.field_28065_e.put(i3, chunk4);
			this.field_28064_f.add(chunk4);
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
		Chunk chunk3 = (Chunk)this.field_28065_e.get(ChunkCoordIntPair.chunkXZ2Int(i1, i2));
		return chunk3 == null ? this.loadChunk(i1, i2) : chunk3;
	}

	private Chunk func_28058_d(int i1, int i2) {
		if(this.field_28066_d == null) {
			return null;
		} else {
			try {
				Chunk chunk3 = this.field_28066_d.loadChunk(this.field_28063_g, i1, i2);
				if(chunk3 != null) {
					chunk3.lastSaveTime = this.field_28063_g.getWorldTime();
				}

				return chunk3;
			} catch (Exception exception4) {
				exception4.printStackTrace();
				return null;
			}
		}
	}

	private void func_28060_a(Chunk chunk1) {
		if(this.field_28066_d != null) {
			try {
				this.field_28066_d.saveExtraChunkData(this.field_28063_g, chunk1);
			} catch (Exception exception3) {
				exception3.printStackTrace();
			}

		}
	}

	private void func_28059_b(Chunk chunk1) {
		if(this.field_28066_d != null) {
			try {
				chunk1.lastSaveTime = this.field_28063_g.getWorldTime();
				this.field_28066_d.saveChunk(this.field_28063_g, chunk1);
			} catch (IOException iOException3) {
				iOException3.printStackTrace();
			}

		}
	}

	public void populate(IChunkProvider iChunkProvider1, int i2, int i3) {
		Chunk chunk4 = this.provideChunk(i2, i3);
		if(!chunk4.isTerrainPopulated) {
			chunk4.isTerrainPopulated = true;
			if(this.chunkGenerator != null) {
				this.chunkGenerator.populate(iChunkProvider1, i2, i3);
				chunk4.setChunkModified();
			}
		}

	}

	public boolean saveChunks(boolean z1, IProgressUpdate iProgressUpdate2) {
		int i3 = 0;

		for(int i4 = 0; i4 < this.field_28064_f.size(); ++i4) {
			Chunk chunk5 = (Chunk)this.field_28064_f.get(i4);
			if(z1 && !chunk5.neverSave) {
				this.func_28060_a(chunk5);
			}

			if(chunk5.needsSaving(z1)) {
				this.func_28059_b(chunk5);
				chunk5.isModified = false;
				++i3;
				if(i3 == 24 && !z1) {
					return false;
				}
			}
		}

		if(z1) {
			if(this.field_28066_d == null) {
				return true;
			}

			this.field_28066_d.saveExtraData();
		}

		return true;
	}

	public boolean func_361_a() {
		for(int i1 = 0; i1 < 100; ++i1) {
			if(!this.field_28062_a.isEmpty()) {
				Integer integer2 = (Integer)this.field_28062_a.iterator().next();
				Chunk chunk3 = (Chunk)this.field_28065_e.get(integer2);
				chunk3.onChunkUnload();
				this.func_28059_b(chunk3);
				this.func_28060_a(chunk3);
				this.field_28062_a.remove(integer2);
				this.field_28065_e.remove(integer2);
				this.field_28064_f.remove(chunk3);
			}
		}

		if(this.field_28066_d != null) {
			this.field_28066_d.func_661_a();
		}

		return this.chunkGenerator.func_361_a();
	}

	public boolean func_364_b() {
		return true;
	}
}
