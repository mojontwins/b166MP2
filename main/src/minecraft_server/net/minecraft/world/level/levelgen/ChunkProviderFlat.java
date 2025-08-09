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
	
	public ChunkProviderFlat(World world, long seed, boolean useStructures) {
		this.worldObj = world;
		this.useStructures = useStructures;
		this.random = new Random(seed);
		
		this.featureProvider = new FeatureProvider(worldObj, this);
	}

	private void generate(byte[] blocks) {
		int h = blocks.length / 256;

		for(int x = 0; x < 16; ++x) {
			for(int z = 0; z < 16; ++z) {
				for(int y = 0; y < h; ++y) {
					int b = 0;
					if(y == 0) {
						b = Block.bedrock.blockID;
					} else if(y <= 2) {
						b = Block.dirt.blockID;
					} else if(y == 3) {
						b = Block.grass.blockID;
					}

					blocks[x << 11 | z << 7 | y] = (byte)b;
				}
			}
		}

	}

	public Chunk loadChunk(int x, int z) {
		return this.provideChunk(x, z);
	}

	public Chunk provideChunk(int x, int z) {
		byte[] blocks = new byte[32768];
		this.generate(blocks);
		Chunk chunk = new Chunk(this.worldObj, blocks, x, z);
		this.featureProvider.getNearestFeatures(x, z, chunk);
		 
		if(this.useStructures) {
		}

		BiomeGenBase[] biomesForGeneration = this.worldObj.getWorldChunkManager().loadBlockGeneratorData((BiomeGenBase[])null, x * 16, z * 16, 16, 16);
		byte[] biomeMap = chunk.getBiomeArray();

		for(int i = 0; i < biomeMap.length; ++i) {
			biomeMap[i] = (byte)biomesForGeneration[i].biomeID;
		}

		chunk.generateSkylightMap();
		return chunk;
	}
	
	public Chunk justGenerate(int x, int z) {
		byte[] blocks = new byte[32768];
		this.generate(blocks);
		Chunk chunk = new Chunk(this.worldObj, blocks, x, z);
		return chunk;
	}

	public boolean chunkExists(int x, int z) {
		return true;
	}

	public void populate(IChunkProvider provider, int chunkX, int chunkZ) {
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

	public boolean saveChunks(boolean z1, IProgressUpdate progress) {
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

	public List<SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, int x, int y, int z) {
		BiomeGenBase biome = this.worldObj.getBiomeGenForCoords(x, z);
		return biome == null ? null : biome.getSpawnableList(creatureType);
	}

	public ChunkPosition findClosestStructure(World world, String string, int x, int y, int z) {
		return null;
	}
}
