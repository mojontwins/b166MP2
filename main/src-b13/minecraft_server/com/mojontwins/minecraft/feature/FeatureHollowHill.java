package com.mojontwins.minecraft.feature;

import java.util.Random;

import net.minecraft.world.level.World;
import net.minecraft.world.level.biome.BiomeGenBase;
import net.minecraft.world.level.chunk.Chunk;
import net.minecraft.world.level.chunk.IChunkProvider;
import net.minecraft.world.level.levelgen.feature.WorldGenSeaweed;
import net.minecraft.world.level.levelgen.feature.WorldGenWaterlily;
import net.minecraft.world.level.tile.Block;

public class FeatureHollowHill extends Feature {
	public double diameter;
	public double hillHeight;
	public int featureRadius;

	public FeatureHollowHill(World world, int originChunkX, int originChunkZ, FeatureProvider featureProvider) {
		super(world, originChunkX, originChunkZ, featureProvider);
	}
	
	@Override
	public void setup(World world, Random rand, BiomeGenBase biomeGenBase, int chunkX, int chunkZ) {
		this.featureRadius = 3;
		switch(rand.nextInt(6)) {
		case 0:
		case 1:
		case 2:
			this.featureRadius --;
		case 3:
		case 4:
			this.featureRadius --;
		}

		this.diameter = (2 * this.featureRadius + 1) * 16.0D;
		this.hillHeight = this.diameter / 3.0D;
		
		System.out.println ("Hollow hill @ " + this.centerX + " " + this.centerZ);
	}
	
	@Override
	public boolean shouldSpawn(IChunkProvider chunkProvider, World world, Random rand, BiomeGenBase biome, int chunkX, int chunkZ) {
		return 
				/*
				biome instanceof BiomeGenForest &&
				Math.abs(chunkX - (world.getWorldInfo().getSpawnX() >> 4)) > 8 && 
				Math.abs(chunkZ - (world.getWorldInfo().getSpawnZ() >> 4)) > 8;	
				*/
				false;
	}
	
	@Override
	public void generate(int chunkX, int chunkZ, Chunk chunk) {
		double radius = this.diameter / 2;
		
		int rx = (chunkX << 4) - this.centerX;
		for(int x = 0; x < 16; x ++) {
			int rz = (chunkZ << 4) - this.centerZ;
			for(int z = 0; z < 16; z ++) {
				double d = Math.sqrt(rx*rx + rz*rz);
				if(d <= radius) {
					
					// Raise terrain using an scaled cosine function.
					// cos goes from 1 to 0 for angles 0 to PI/2 radians
					// We have to scale such range from 0 to radius
					
					int heightMapIndex = z << 4 | x;
					
					// Land surface height map ignores water so this may be well under 64!
					int height = chunk.landSurfaceHeightMap[heightMapIndex];
					
					// Add the cosine (the dome-shaped deformation)
					int newHeight = height + (int) Math.floor(this.hillHeight * Math.cos(d * Math.PI / this.diameter));
					if(newHeight >= 120) newHeight = 120;
					
					// Raise to height
					// Note how this debumps original orography, sea level centered
					int y = height > 64 ? 64 + (height - 64) / 4 : height;
					for(; y < newHeight; y ++) {
						chunk.setBlockIDSimple(x, y, z, y < newHeight - 4 ? 0 : Block.stone.blockID, 0);
					}
					
					// Remove water
					/*
					int y = 64;
					while(y > 8) {
						y --;
						Block b = Block.blocksList[chunk.blocks[index + y]];
						if(b != null && (b.blockMaterial == Material.water || b.blockMaterial == Material.ice)) {
							chunk.blocks[index + y] = (byte)Block.sand.blockID;
						} else break;
					}
					*/
					
					chunk.landSurfaceHeightMap[heightMapIndex] = (byte)newHeight;
				}
				rz ++;

			}
			rx ++;
		}
	}

	@Override
	public void populate(World world, Random rand, int chunkX, int chunkZ) {
		// Redo this chunk x chunk
		int x0 = chunkX * 16;
		int z0 = chunkZ * 16;
		
		if(chunkX == this.originChunkX && chunkZ == this.originChunkZ) {
			//(new TFGenHollowHill(this.featureRadius)).generate(world, rand, chunkX * 16 + 8, 64, chunkZ * 16 + 8);
			//(new TFGenHillMaze(this.featureRadius)).generate(world, rand, chunkX * 16 + 8, 48, chunkZ * 16 + 8);
		}
		
		int x, y, z;
		
		// Generate Lilypads
		for(int i = 0; i < 8; i ++) {
			x = x0 + rand.nextInt(16) + 8;
			z = z0 + rand.nextInt(16) + 8;
			
			for(y = rand.nextInt(128); y > 0 && world.getBlockId(x, y - 1, z) == 0; y --) {}
			
			(new WorldGenWaterlily()).generate(world, rand, x, y, z);
		}
		
		// Generate algae
		for (int i = 0; i < 32; i ++) {
			x = x0 + rand.nextInt(16) + 8;
			z = z0 + rand.nextInt(16) + 8;
			y = world.getHeightValueUnderWater (x, z);
			(new WorldGenSeaweed()).generate(world,  rand, x, y, z);
		}
		
		// Generate coral
		/*
		for (int i = 0; i < 32; i ++) {
			x = x0 + rand.nextInt(16);
			z = z0 + rand.nextInt(16);
			y = world.getHeightValueUnderWater (x, z) + 1;

			if(Block.coral.canBlockStay(world, x, y, z)) {
				world.setBlockAndMetadataWithNotify(x, y, z, Block.coral.blockID, 8 | rand.nextInt(3));
			}
		}
		*/
	}
	

	@Override
	public int minimumSeparation() {
		return 1;
	}

	@Override
	public int getFeatureRadius() {
		return this.featureRadius;
	}

	@Override
	public int getSpawnChance() {
		return 6;
	}
}
