package net.minecraft.world.level;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.world.GameRules;
import net.minecraft.world.level.biome.BiomeCache;
import net.minecraft.world.level.biome.BiomeGenBase;
import net.minecraft.world.level.levelgen.genlayer.GenLayer;

public class WorldChunkManager {
	private GenLayer genBiomes;
	private GenLayer biomeIndexLayer;
	private BiomeCache biomeCache;
	private List<BiomeGenBase> biomesToSpawnIn;

	protected WorldChunkManager() {
		this.biomeCache = new BiomeCache(this);
		
		this.biomesToSpawnIn = new ArrayList<BiomeGenBase>();
		this.biomesToSpawnIn.add(BiomeGenBase.alpha);

	}

	public WorldChunkManager(long seed, WorldType worldType) {
		this();
			
		if (GameRules.genlayerWorldChunkManager) {
			BiomeGenBase.buildBiomeStructure(worldType.getBiomesForWorldType());
		}
		
		GenLayer[] genLayerArray = GameRules.initializeAllBiomeGenerators(seed, worldType);
		
		this.genBiomes = genLayerArray[0];
		this.biomeIndexLayer = genLayerArray[1];
	}

	public WorldChunkManager(World world1) {
		this(world1.getSeed(), world1.getWorldInfo().getTerrainType());
	}

	public List<BiomeGenBase> getBiomesToSpawnIn() {
		return this.biomesToSpawnIn;
	}

	public BiomeGenBase getBiomeGenAt(int i1, int i2) {
		return this.biomeCache.getBiomeGenAt(i1, i2);
	}

	public float[] getRainfall(float[] f1, int i2, int i3, int i4, int i5) {
		IntCache.resetIntCache();
		if(f1 == null || f1.length < i4 * i5) {
			f1 = new float[i4 * i5];
		}

		int[] i6 = this.biomeIndexLayer.getInts(i2, i3, i4, i5);

		for(int i7 = 0; i7 < i4 * i5; ++i7) {
			float f8 = (float)BiomeGenBase.biomeList[i6[i7]].getIntRainfall() / 65536.0F;
			if(f8 > 1.0F) {
				f8 = 1.0F;
			}

			f1[i7] = f8;
		}

		return f1;
	}

	public float getTemperatureAtHeight(float f1, int i2) {
		return f1;
	}

	public float[] getTemperatures(float[] f1, int i2, int i3, int i4, int i5) {
		IntCache.resetIntCache();
		if(f1 == null || f1.length < i4 * i5) {
			f1 = new float[i4 * i5];
		}

		int[] i6 = this.biomeIndexLayer.getInts(i2, i3, i4, i5);

		for(int i7 = 0; i7 < i4 * i5; ++i7) {
			float f8 = (float)BiomeGenBase.biomeList[i6[i7]].getIntTemperature() / 65536.0F;
			if(f8 > 1.0F) {
				f8 = 1.0F;
			}

			f1[i7] = f8;
		}

		return f1;
	}

	public BiomeGenBase[] getBiomesForGeneration(BiomeGenBase[] biomeGenBase1, int i2, int i3, int i4, int i5) {
		IntCache.resetIntCache();
		if(biomeGenBase1 == null || biomeGenBase1.length < i4 * i5) {
			biomeGenBase1 = new BiomeGenBase[i4 * i5];
		}

		int[] i6 = this.genBiomes.getInts(i2, i3, i4, i5);

		for(int i7 = 0; i7 < i4 * i5; ++i7) {
			biomeGenBase1[i7] = BiomeGenBase.biomeList[i6[i7]];
		}

		return biomeGenBase1;
	}

	public BiomeGenBase[] loadBlockGeneratorData(BiomeGenBase[] biomeGenBase1, int i2, int i3, int i4, int i5) {
		return this.getBiomeGenAt(biomeGenBase1, i2, i3, i4, i5, true);
	}

	public BiomeGenBase[] getBiomeGenAt(BiomeGenBase[] biomeGenBase1, int i2, int i3, int i4, int i5, boolean z6) {
		IntCache.resetIntCache();
		if(biomeGenBase1 == null || biomeGenBase1.length < i4 * i5) {
			biomeGenBase1 = new BiomeGenBase[i4 * i5];
		}

		if(z6 && i4 == 16 && i5 == 16 && (i2 & 15) == 0 && (i3 & 15) == 0) {
			BiomeGenBase[] biomeGenBase9 = this.biomeCache.getCachedBiomes(i2, i3);
			System.arraycopy(biomeGenBase9, 0, biomeGenBase1, 0, i4 * i5);
			return biomeGenBase1;
		} else {
			int[] i7 = this.biomeIndexLayer.getInts(i2, i3, i4, i5);

			for(int i8 = 0; i8 < i4 * i5; ++i8) {
				biomeGenBase1[i8] = BiomeGenBase.biomeList[i7[i8]];
			}

			return biomeGenBase1;
		}
	}

	public boolean areBiomesViable(int i1, int i2, int i3, List<BiomeGenBase> list4) {
		int i5 = i1 - i3 >> 2;
		int i6 = i2 - i3 >> 2;
		int i7 = i1 + i3 >> 2;
		int i8 = i2 + i3 >> 2;
		int i9 = i7 - i5 + 1;
		int i10 = i8 - i6 + 1;
		int[] i11 = this.genBiomes.getInts(i5, i6, i9, i10);

		for(int i12 = 0; i12 < i9 * i10; ++i12) {
			BiomeGenBase biomeGenBase13 = BiomeGenBase.biomeList[i11[i12]];
			if(!list4.contains(biomeGenBase13)) {
				return false;
			}
		}

		return true;
	}

	public ChunkPosition findBiomePosition(int i1, int i2, int i3, List<BiomeGenBase> list4, Random random5) {
		int i6 = i1 - i3 >> 2;
		int i7 = i2 - i3 >> 2;
		int i8 = i1 + i3 >> 2;
		int i9 = i2 + i3 >> 2;
		int i10 = i8 - i6 + 1;
		int i11 = i9 - i7 + 1;
		int[] i12 = this.genBiomes.getInts(i6, i7, i10, i11);
		ChunkPosition chunkPosition13 = null;
		int i14 = 0;

		for(int i15 = 0; i15 < i12.length; ++i15) {
			int i16 = i6 + i15 % i10 << 2;
			int i17 = i7 + i15 / i10 << 2;
			BiomeGenBase biomeGenBase18 = BiomeGenBase.biomeList[i12[i15]];
			if(list4.contains(biomeGenBase18) && (chunkPosition13 == null || random5.nextInt(i14 + 1) == 0)) {
				chunkPosition13 = new ChunkPosition(i16, 0, i17);
				++i14;
			}
		}

		return chunkPosition13;
	}

	public void cleanupCache() {
		this.biomeCache.cleanupCache();
	}
}
