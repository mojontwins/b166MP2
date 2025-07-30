package com.mojontwins.minecraft.feature;

import java.util.Random;

import net.minecraft.world.level.World;
import net.minecraft.world.level.biome.BiomeGenBase;
import net.minecraft.world.level.chunk.Chunk;
import net.minecraft.world.level.chunk.IChunkProvider;

public class FeatureDrawStructureTest extends Feature {

	public FeatureDrawStructureTest(World world, int originChunkX, int originChunkZ, FeatureProvider featureProvider) {	
		super(world, originChunkX, originChunkZ, featureProvider);
	}

	@Override
	public void generate(int chunkX, int chunkZ, Chunk chunk) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void populate(World world, Random rand, int chunkX, int chunkZ) {
		if(chunkX != this.originChunkX || chunkZ != this.originChunkZ) return;
		
		int x = chunkX*16;
		int y = 100;
		int z = chunkZ*16;
		
		// 15 x 20 x 15

		world.setBlockAndMetadataColumn(x + 0, y + 0, z + 0, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 0, y + 0, z + 1, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 0, y + 0, z + 2, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 0, y + 0, z + 3, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 0, y + 0, z + 4, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 0, y + 0, z + 5, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 0, y + 0, z + 6, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 0, y + 0, z + 7, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 0, y + 0, z + 8, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 0, y + 0, z + 9, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 0, y + 0, z + 10, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 0, y + 0, z + 11, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 0, y + 0, z + 12, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 0, y + 0, z + 13, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 0, y + 0, z + 14, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 1, y + 0, z + 0, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 1, y + 0, z + 1, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 1, y + 0, z + 2, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 1, y + 0, z + 3, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 1, y + 0, z + 4, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 1, y + 0, z + 5, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 1, y + 0, z + 6, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 1, y + 0, z + 7, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 1, y + 0, z + 8, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 1, y + 0, z + 9, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 1, y + 0, z + 10, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 1, y + 0, z + 11, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 1, y + 0, z + 12, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 1, y + 0, z + 13, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 1, y + 0, z + 14, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 2, y + 0, z + 0, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 2, y + 0, z + 1, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 2, y + 0, z + 2, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 24, 24, 24, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 2, y + 0, z + 3, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 24, 24, 24, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 2, y + 0, z + 4, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 24, 24, 24, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 2, y + 0, z + 5, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 24, 24, 24, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 2, y + 0, z + 6, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 24, 24, 24, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 2, y + 0, z + 7, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 24, 24, 24, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 2, y + 0, z + 8, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 24, 24, 24, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 2, y + 0, z + 9, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 2, y + 0, z + 10, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 2, y + 0, z + 11, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 2, y + 0, z + 12, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 2, y + 0, z + 13, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 2, y + 0, z + 14, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 3, y + 0, z + 0, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 3, y + 0, z + 1, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 24, 0, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 3, y + 0, z + 2, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 24, 24, 24, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 3, y + 0, z + 3, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 24, 24, 24, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 3, y + 0, z + 4, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 24, 24, 24, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 3, y + 0, z + 5, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 24, 24, 24, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 3, y + 0, z + 6, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 24, 24, 24, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 3, y + 0, z + 7, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 24, 24, 24, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 3, y + 0, z + 8, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 24, 24, 24, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 3, y + 0, z + 9, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 3, y + 0, z + 10, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 3, y + 0, z + 11, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 3, y + 0, z + 12, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 3, y + 0, z + 13, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 3, y + 0, z + 14, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 4, y + 0, z + 0, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 4, y + 0, z + 1, new int[] {24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 4, y + 0, z + 2, new int[] {24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 4, y + 0, z + 3, new int[] {24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 4, y + 0, z + 4, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 24, 24, 24, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 4, y + 0, z + 5, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 24, 24, 24, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 4, y + 0, z + 6, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 24, 24, 24, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 4, y + 0, z + 7, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 24, 24, 24, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 4, y + 0, z + 8, new int[] {24, 24, 24, 0, 0, 0, 0, 0, 0, 24, 24, 24, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 4, y + 0, z + 9, new int[] {24, 24, 24, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 4, y + 0, z + 10, new int[] {24, 24, 24, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 4, y + 0, z + 11, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 4, y + 0, z + 12, new int[] {24, 24, 24, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 4, y + 0, z + 13, new int[] {24, 24, 24, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 4, y + 0, z + 14, new int[] {24, 24, 24, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 5, y + 0, z + 0, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 24, 24, 24, 24, 24, 0});
		world.setBlockAndMetadataColumn(x + 5, y + 0, z + 1, new int[] {24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 0});
		world.setBlockAndMetadataColumn(x + 5, y + 0, z + 2, new int[] {24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 0});
		world.setBlockAndMetadataColumn(x + 5, y + 0, z + 3, new int[] {24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 0});
		world.setBlockAndMetadataColumn(x + 5, y + 0, z + 4, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 24, 24, 24, 24, 24, 0});
		world.setBlockAndMetadataColumn(x + 5, y + 0, z + 5, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 5, y + 0, z + 6, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 5, y + 0, z + 7, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 5, y + 0, z + 8, new int[] {24, 24, 24, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 5, y + 0, z + 9, new int[] {24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 5, y + 0, z + 10, new int[] {24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 5, y + 0, z + 11, new int[] {0, 0, 0, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 5, y + 0, z + 12, new int[] {24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 5, y + 0, z + 13, new int[] {24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 5, y + 0, z + 14, new int[] {24, 24, 24, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 6, y + 0, z + 0, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 24, 24, 24, 24, 24, 0});
		world.setBlockAndMetadataColumn(x + 6, y + 0, z + 1, new int[] {24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 0});
		world.setBlockAndMetadataColumn(x + 6, y + 0, z + 2, new int[] {24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 0});
		world.setBlockAndMetadataColumn(x + 6, y + 0, z + 3, new int[] {24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 11, 24, 24, 0});
		world.setBlockAndMetadataColumn(x + 6, y + 0, z + 4, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 24, 24, 85, 24, 24, 0});
		world.setBlockAndMetadataColumn(x + 6, y + 0, z + 5, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 6, y + 0, z + 6, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 6, y + 0, z + 7, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 6, y + 0, z + 8, new int[] {24, 24, 24, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 6, y + 0, z + 9, new int[] {24, 24, 24, 24, 24, 24, 24, 24, 24, 85, 85, 24, 24, 85, 24, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 6, y + 0, z + 10, new int[] {24, 24, 24, 24, 24, 24, 24, 24, 24, 11, 11, 24, 24, 11, 24, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 6, y + 0, z + 11, new int[] {0, 0, 0, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 6, y + 0, z + 12, new int[] {24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 6, y + 0, z + 13, new int[] {24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 6, y + 0, z + 14, new int[] {24, 24, 24, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 7, y + 0, z + 0, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 24, 24, 24, 24, 24, 0});
		world.setBlockAndMetadataColumn(x + 7, y + 0, z + 1, new int[] {0, 0, 0, 0, 0, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 0});
		world.setBlockAndMetadataColumn(x + 7, y + 0, z + 2, new int[] {0, 0, 0, 0, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 0});
		world.setBlockAndMetadataColumn(x + 7, y + 0, z + 3, new int[] {0, 0, 0, 0, 0, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 0});
		world.setBlockAndMetadataColumn(x + 7, y + 0, z + 4, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 24, 24, 24, 24, 24, 0});
		world.setBlockAndMetadataColumn(x + 7, y + 0, z + 5, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 7, y + 0, z + 6, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 7, y + 0, z + 7, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 7, y + 0, z + 8, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 7, y + 0, z + 9, new int[] {0, 0, 0, 24, 24, 24, 24, 24, 24, 24, 85, 85, 24, 24, 24, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 7, y + 0, z + 10, new int[] {0, 0, 0, 24, 24, 24, 24, 24, 24, 24, 11, 11, 24, 24, 24, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 7, y + 0, z + 11, new int[] {0, 0, 0, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 7, y + 0, z + 12, new int[] {0, 0, 0, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 7, y + 0, z + 13, new int[] {0, 0, 0, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 7, y + 0, z + 14, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 8, y + 0, z + 0, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 24, 24, 24, 24, 24, 0});
		world.setBlockAndMetadataColumn(x + 8, y + 0, z + 1, new int[] {24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 0});
		world.setBlockAndMetadataColumn(x + 8, y + 0, z + 2, new int[] {24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 0});
		world.setBlockAndMetadataColumn(x + 8, y + 0, z + 3, new int[] {24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 11, 24, 24, 0});
		world.setBlockAndMetadataColumn(x + 8, y + 0, z + 4, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 24, 24, 85, 24, 24, 0});
		world.setBlockAndMetadataColumn(x + 8, y + 0, z + 5, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 8, y + 0, z + 6, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 8, y + 0, z + 7, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 8, y + 0, z + 8, new int[] {24, 24, 24, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 8, y + 0, z + 9, new int[] {24, 24, 24, 24, 24, 24, 24, 24, 24, 85, 85, 24, 24, 85, 24, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 8, y + 0, z + 10, new int[] {24, 24, 24, 24, 24, 24, 24, 24, 24, 11, 11, 24, 24, 11, 24, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 8, y + 0, z + 11, new int[] {0, 0, 0, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 8, y + 0, z + 12, new int[] {24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 8, y + 0, z + 13, new int[] {24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 8, y + 0, z + 14, new int[] {24, 24, 24, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 9, y + 0, z + 0, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 24, 24, 24, 24, 24, 0});
		world.setBlockAndMetadataColumn(x + 9, y + 0, z + 1, new int[] {24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 0});
		world.setBlockAndMetadataColumn(x + 9, y + 0, z + 2, new int[] {24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 0});
		world.setBlockAndMetadataColumn(x + 9, y + 0, z + 3, new int[] {24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 0});
		world.setBlockAndMetadataColumn(x + 9, y + 0, z + 4, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 24, 24, 24, 24, 24, 0});
		world.setBlockAndMetadataColumn(x + 9, y + 0, z + 5, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 9, y + 0, z + 6, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 9, y + 0, z + 7, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 9, y + 0, z + 8, new int[] {24, 24, 24, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 9, y + 0, z + 9, new int[] {24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 9, y + 0, z + 10, new int[] {24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 9, y + 0, z + 11, new int[] {0, 0, 0, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 9, y + 0, z + 12, new int[] {24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 9, y + 0, z + 13, new int[] {24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 9, y + 0, z + 14, new int[] {24, 24, 24, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 10, y + 0, z + 0, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 10, y + 0, z + 1, new int[] {24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 10, y + 0, z + 2, new int[] {24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 10, y + 0, z + 3, new int[] {24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 10, y + 0, z + 4, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 24, 24, 24, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 10, y + 0, z + 5, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 24, 24, 24, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 10, y + 0, z + 6, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 24, 24, 24, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 10, y + 0, z + 7, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 24, 24, 24, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 10, y + 0, z + 8, new int[] {24, 24, 24, 0, 0, 0, 0, 0, 0, 24, 24, 24, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 10, y + 0, z + 9, new int[] {24, 24, 24, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 10, y + 0, z + 10, new int[] {24, 24, 24, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 10, y + 0, z + 11, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 10, y + 0, z + 12, new int[] {24, 24, 24, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 10, y + 0, z + 13, new int[] {24, 24, 24, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 10, y + 0, z + 14, new int[] {24, 24, 24, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 11, y + 0, z + 0, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 11, y + 0, z + 1, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 24, 0, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 11, y + 0, z + 2, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 24, 24, 24, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 11, y + 0, z + 3, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 24, 24, 24, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 11, y + 0, z + 4, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 24, 24, 24, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 11, y + 0, z + 5, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 24, 24, 24, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 11, y + 0, z + 6, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 24, 24, 24, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 11, y + 0, z + 7, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 24, 24, 24, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 11, y + 0, z + 8, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 24, 24, 24, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 11, y + 0, z + 9, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 11, y + 0, z + 10, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 11, y + 0, z + 11, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 11, y + 0, z + 12, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 11, y + 0, z + 13, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 11, y + 0, z + 14, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 12, y + 0, z + 0, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 12, y + 0, z + 1, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 12, y + 0, z + 2, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 24, 24, 24, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 12, y + 0, z + 3, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 24, 24, 24, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 12, y + 0, z + 4, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 24, 24, 24, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 12, y + 0, z + 5, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 24, 24, 24, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 12, y + 0, z + 6, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 24, 24, 24, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 12, y + 0, z + 7, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 24, 24, 24, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 12, y + 0, z + 8, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 24, 24, 24, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 12, y + 0, z + 9, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 12, y + 0, z + 10, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 12, y + 0, z + 11, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 12, y + 0, z + 12, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 12, y + 0, z + 13, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 12, y + 0, z + 14, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 13, y + 0, z + 0, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 13, y + 0, z + 1, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 13, y + 0, z + 2, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 13, y + 0, z + 3, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 13, y + 0, z + 4, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 13, y + 0, z + 5, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 13, y + 0, z + 6, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 13, y + 0, z + 7, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 13, y + 0, z + 8, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 13, y + 0, z + 9, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 13, y + 0, z + 10, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 13, y + 0, z + 11, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 13, y + 0, z + 12, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 13, y + 0, z + 13, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 13, y + 0, z + 14, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 14, y + 0, z + 0, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 14, y + 0, z + 1, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 14, y + 0, z + 2, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 14, y + 0, z + 3, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 14, y + 0, z + 4, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 14, y + 0, z + 5, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 14, y + 0, z + 6, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 14, y + 0, z + 7, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 14, y + 0, z + 8, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 14, y + 0, z + 9, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 14, y + 0, z + 10, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 14, y + 0, z + 11, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 14, y + 0, z + 12, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 14, y + 0, z + 13, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
		world.setBlockAndMetadataColumn(x + 14, y + 0, z + 14, new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});


	}

	@Override
	public int getFeatureRadius() {
		return 1;
	}

	@Override
	public int getSpawnChance() {
		return 0;
	}

	@Override
	public boolean shouldSpawn(IChunkProvider chunkProvider, World world, Random rand, BiomeGenBase biome,
			int chunkX, int chunkZ) {
		return false;
	}

}
