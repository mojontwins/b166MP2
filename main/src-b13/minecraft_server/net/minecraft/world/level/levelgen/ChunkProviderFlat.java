package net.minecraft.world.level.levelgen;

import java.util.List;
import java.util.Random;

import com.mojontwins.minecraft.feature.FeatureProvider;

import net.minecraft.world.entity.EnumCreatureType;
import net.minecraft.world.level.ChunkPosition;
import net.minecraft.world.level.SpawnListEntry;
import net.minecraft.world.level.World;
import net.minecraft.world.level.biome.BiomeGenBase;
import net.minecraft.world.level.chunk.Chunk;
import net.minecraft.world.level.chunk.IChunkProvider;
import net.minecraft.world.level.chunk.storage.IProgressUpdate;
import net.minecraft.world.level.tile.Block;

public class ChunkProviderFlat implements IChunkProvider {
	private World worldObj;
	private Random random;
	private final boolean useStructures;
	
	// Multi-chunk features	
	public FeatureProvider featureProvider;
	
	public ChunkProviderFlat(World world1, long j2, boolean z4) {
		this.worldObj = world1;
		this.useStructures = z4;
		this.random = new Random(j2);
		
		this.featureProvider = new FeatureProvider(worldObj, this);
	}

	private void generate(byte[] b1) {
		int i2 = b1.length / 256;

		for(int i3 = 0; i3 < 16; ++i3) {
			for(int i4 = 0; i4 < 16; ++i4) {
				for(int i5 = 0; i5 < i2; ++i5) {
					int i6 = 0;
					if(i5 == 0) {
						i6 = Block.bedrock.blockID;
					} else if(i5 <= 2) {
						i6 = Block.dirt.blockID;
					} else if(i5 == 3) {
						i6 = Block.grass.blockID;
					}

					b1[i3 << 11 | i4 << 7 | i5] = (byte)i6;
				}
			}
		}

	}

	public Chunk loadChunk(int i1, int i2) {
		return this.provideChunk(i1, i2);
	}

	public Chunk provideChunk(int i1, int i2) {
		byte[] b3 = new byte[32768];
		this.generate(b3);
		Chunk chunk4 = new Chunk(this.worldObj, b3, i1, i2);
		this.featureProvider.getNearestFeatures(i1, i2, chunk4);
		 
		if(this.useStructures) {
		}

		BiomeGenBase[] biomeGenBase5 = this.worldObj.getWorldChunkManager().loadBlockGeneratorData((BiomeGenBase[])null, i1 * 16, i2 * 16, 16, 16);
		byte[] b6 = chunk4.getBiomeArray();

		for(int i7 = 0; i7 < b6.length; ++i7) {
			b6[i7] = (byte)biomeGenBase5[i7].biomeID;
		}

		chunk4.generateSkylightMap();
		return chunk4;
	}
	
	public Chunk justGenerate(int posX, int posZ) {
		byte[] b3 = new byte[32768];
		this.generate(b3);
		Chunk chunk4 = new Chunk(this.worldObj, b3, posX, posZ);
		return chunk4;
	}

	public boolean chunkExists(int i1, int i2) {
		return true;
	}

	public void populate(IChunkProvider iChunkProvider1, int chunkX, int chunkZ) {
		//int x0 = chunkX * 16;
		//int z0 = chunkZ * 16;
		//BiomeGenBase biomeGenBase6 = this.worldObj.getBiomeGenForCoords(x0 + 16, z0 + 16);
		
		this.random.setSeed(this.worldObj.getSeed());
		long j4 = this.random.nextLong() / 2L * 2L + 1L;
		long j6 = this.random.nextLong() / 2L * 2L + 1L;
		this.random.setSeed((long)chunkX * j4 + (long)chunkZ * j6 ^ this.worldObj.getSeed());
		if(this.useStructures) {
			// Features 
			this.featureProvider.populateFeatures(worldObj, this.random, chunkX, chunkZ);
		}

	}

	public boolean saveChunks(boolean z1, IProgressUpdate iProgressUpdate2) {
		return true;
	}

	public boolean unload100OldestChunks() {
		return false;
	}

	public boolean canSave() {
		return true;
	}

	public String makeString() {
		return "FlatLevelSource";
	}

	public List<SpawnListEntry> getPossibleCreatures(EnumCreatureType enumCreatureType1, int i2, int i3, int i4) {
		BiomeGenBase biomeGenBase5 = this.worldObj.getBiomeGenForCoords(i2, i4);
		return biomeGenBase5 == null ? null : biomeGenBase5.getSpawnableList(enumCreatureType1);
	}

	public ChunkPosition findClosestStructure(World world1, String string2, int i3, int i4, int i5) {
		return null;
	}
}
