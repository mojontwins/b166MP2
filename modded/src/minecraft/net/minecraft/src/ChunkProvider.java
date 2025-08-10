package net.minecraft.src;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ChunkProvider implements IChunkProvider {
	private Set field_28065_a = new HashSet();
	private Chunk field_28064_b;
	private IChunkProvider field_28070_c;
	private IChunkLoader field_28069_d;
	private Map field_28068_e = new HashMap();
	private List field_28067_f = new ArrayList();
	private World field_28066_g;

	public ChunkProvider(World paramfb, IChunkLoader parambe, IChunkProvider paramcj) {
		this.field_28064_b = new EmptyChunk(paramfb, new byte[32768], 0, 0);
		this.field_28066_g = paramfb;
		this.field_28069_d = parambe;
		this.field_28070_c = paramcj;
	}

	public boolean chunkExists(int paramInt1, int paramInt2) {
		return this.field_28068_e.containsKey(ChunkCoordIntPair.chunkXZ2Int(paramInt1, paramInt2));
	}

	public Chunk func_538_d(int paramInt1, int paramInt2) {
		int i = ChunkCoordIntPair.chunkXZ2Int(paramInt1, paramInt2);
		this.field_28065_a.remove(i);
		Chunk localli = (Chunk)this.field_28068_e.get(i);
		if(localli == null) {
			localli = this.func_28061_d(paramInt1, paramInt2);
			if(localli == null) {
				if(this.field_28070_c == null) {
					localli = this.field_28064_b;
				} else {
					localli = this.field_28070_c.provideChunk(paramInt1, paramInt2);
				}
			}

			this.field_28068_e.put(i, localli);
			this.field_28067_f.add(localli);
			if(localli != null) {
				localli.func_4143_d();
				localli.onChunkLoad();
			}

			if(!localli.isTerrainPopulated && this.chunkExists(paramInt1 + 1, paramInt2 + 1) && this.chunkExists(paramInt1, paramInt2 + 1) && this.chunkExists(paramInt1 + 1, paramInt2)) {
				this.populate(this, paramInt1, paramInt2);
			}

			if(this.chunkExists(paramInt1 - 1, paramInt2) && !this.provideChunk(paramInt1 - 1, paramInt2).isTerrainPopulated && this.chunkExists(paramInt1 - 1, paramInt2 + 1) && this.chunkExists(paramInt1, paramInt2 + 1) && this.chunkExists(paramInt1 - 1, paramInt2)) {
				this.populate(this, paramInt1 - 1, paramInt2);
			}

			if(this.chunkExists(paramInt1, paramInt2 - 1) && !this.provideChunk(paramInt1, paramInt2 - 1).isTerrainPopulated && this.chunkExists(paramInt1 + 1, paramInt2 - 1) && this.chunkExists(paramInt1, paramInt2 - 1) && this.chunkExists(paramInt1 + 1, paramInt2)) {
				this.populate(this, paramInt1, paramInt2 - 1);
			}

			if(this.chunkExists(paramInt1 - 1, paramInt2 - 1) && !this.provideChunk(paramInt1 - 1, paramInt2 - 1).isTerrainPopulated && this.chunkExists(paramInt1 - 1, paramInt2 - 1) && this.chunkExists(paramInt1, paramInt2 - 1) && this.chunkExists(paramInt1 - 1, paramInt2)) {
				this.populate(this, paramInt1 - 1, paramInt2 - 1);
			}
		}

		return localli;
	}

	public Chunk provideChunk(int paramInt1, int paramInt2) {
		Chunk localli = (Chunk)this.field_28068_e.get(ChunkCoordIntPair.chunkXZ2Int(paramInt1, paramInt2));
		return localli == null ? this.func_538_d(paramInt1, paramInt2) : localli;
	}

	private Chunk func_28061_d(int paramInt1, int paramInt2) {
		if(this.field_28069_d == null) {
			return null;
		} else {
			try {
				Chunk localException = this.field_28069_d.loadChunk(this.field_28066_g, paramInt1, paramInt2);
				if(localException != null) {
					localException.lastSaveTime = this.field_28066_g.getWorldTime();
				}

				return localException;
			} catch (Exception exception4) {
				exception4.printStackTrace();
				return null;
			}
		}
	}

	private void func_28063_a(Chunk paramli) {
		if(this.field_28069_d != null) {
			try {
				this.field_28069_d.saveExtraChunkData(this.field_28066_g, paramli);
			} catch (Exception exception3) {
				exception3.printStackTrace();
			}

		}
	}

	private void func_28062_b(Chunk paramli) {
		if(this.field_28069_d != null) {
			try {
				paramli.lastSaveTime = this.field_28066_g.getWorldTime();
				this.field_28069_d.saveChunk(this.field_28066_g, paramli);
			} catch (IOException iOException3) {
				iOException3.printStackTrace();
			}

		}
	}

	public void populate(IChunkProvider paramcj, int paramInt1, int paramInt2) {
		Chunk localli = this.provideChunk(paramInt1, paramInt2);
		if(!localli.isTerrainPopulated) {
			localli.isTerrainPopulated = true;
			if(this.field_28070_c != null) {
				this.field_28070_c.populate(paramcj, paramInt1, paramInt2);
				ModLoader.PopulateChunk(this.field_28070_c, paramInt1 << 4, paramInt2 << 4, this.field_28066_g);
				localli.setChunkModified();
			}
		}

	}

	public boolean saveChunks(boolean paramBoolean, IProgressUpdate paramxs) {
		int i = 0;

		for(int j = 0; j < this.field_28067_f.size(); ++j) {
			Chunk localli = (Chunk)this.field_28067_f.get(j);
			if(paramBoolean && !localli.neverSave) {
				this.func_28063_a(localli);
			}

			if(localli.needsSaving(paramBoolean)) {
				this.func_28062_b(localli);
				localli.isModified = false;
				++i;
				if(i == 24 && !paramBoolean) {
					return false;
				}
			}
		}

		if(paramBoolean) {
			if(this.field_28069_d == null) {
				return true;
			}

			this.field_28069_d.saveExtraData();
		}

		return true;
	}

	public boolean func_532_a() {
		for(int i = 0; i < 100; ++i) {
			if(!this.field_28065_a.isEmpty()) {
				Integer localInteger = (Integer)this.field_28065_a.iterator().next();
				Chunk localli = (Chunk)this.field_28068_e.get(localInteger);
				localli.onChunkUnload();
				this.func_28062_b(localli);
				this.func_28063_a(localli);
				this.field_28065_a.remove(localInteger);
				this.field_28068_e.remove(localInteger);
				this.field_28067_f.remove(localli);
			}
		}

		if(this.field_28069_d != null) {
			this.field_28069_d.func_814_a();
		}

		return this.field_28070_c.func_532_a();
	}

	public boolean func_536_b() {
		return true;
	}

	public String makeString() {
		return "ServerChunkCache: " + this.field_28068_e.size() + " Drop: " + this.field_28065_a.size();
	}
}
