package com.mojontwins.minecraft.feature;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import net.minecraft.world.level.World;
import net.minecraft.world.level.biome.BiomeGenBase;
import net.minecraft.world.level.chunk.Chunk;

public abstract class FeatureDynamicSchematic extends Feature {	
	/*
	 * Dynamic schematic features have a schematic rendered to a big array on creation.
	 * Array covers the whole feature, horizontally, but may have variable height.
	 * 
	 * This base class adds methods to render a portion of the array in the current chunk
	 * both during generation and population.
	 */
	
	// schematic is [x][z][y]
	public short schematic [][][];
	
	// Store special blocks here to process later
	public List<StructureBlockData> specialBlocks = new ArrayList<StructureBlockData>();
	
	// AABB of the whole structure
	public FeatureAABB aabb;

	public FeatureDynamicSchematic(World world, int originChunkX, int originChunkZ, FeatureProvider featureProvider) {
		super(world, originChunkX, originChunkZ, featureProvider);
		this.featureAABB.y1 = this.getY0();
		this.featureAABB.y2 = 127;
	}

	public abstract int getFeatureHeight();

	public abstract void generateSchematic(World world, Random rand, BiomeGenBase biome, int chunkX, int chunkZ);
	
	public abstract int getY0();
	
	@Override
	public void setup(World world, Random rand, BiomeGenBase biome, int chunkX, int chunkZ) {
		this.schematic = new short[(1 + this.getFeatureRadius() * 2) * 16][(1 + this.getFeatureRadius() * 2) * 16][this.getFeatureHeight()];
		this.aabb =new FeatureAABB(0, 0, 0, this.schematic.length - 1, this.schematic[0][0].length - 1, this.schematic[0].length -1);
		this.generateSchematic(world, rand, biome, chunkX, chunkZ);
	}
	
	protected int getPieceX(int chunkX) {
		return chunkX - this.originChunkX + this.getFeatureRadius();
	}
	
	protected int getPieceZ(int chunkZ) {
		return chunkZ - this.originChunkZ + this.getFeatureRadius();
	}
	
	public void addSpecialBlock(int x, int y, int z, int blockID, int meta) {
		this.specialBlocks.add(new StructureBlockData(x, y, z, blockID, meta));
	}
	
	public void drawPieceOnGeneration(int chunkX, int chunkZ, Chunk chunk) {
		// Piece to draw is:
		int pieceXoffs = this.getPieceX(chunkX) << 4;
		int pieceZoffs = this.getPieceZ(chunkZ) << 4;
		
		int y0 = this.getY0();
		if(y0 < 1) return;
		
		// Decode & copy blocks
		for(int x = 0; x < 16; x ++) {
			for(int z = 0; z < 16; z ++) {
				int y = y0;

				for(int i = 0; i < this.getFeatureHeight(); i ++) {
					short blockAndMeta = this.schematic[pieceXoffs + x][pieceZoffs + z][i];
					
					if(blockAndMeta != -1) {
						byte blockID = (byte)(blockAndMeta & 0xff);
						byte metadata = (byte)(blockAndMeta >> 8);
												
						chunk.setBlockIDSimple(x, y, z, blockID, metadata);
					}
					
					y ++;
					
					if(y == 128) break;
				}
			}
		}
	}
	
	public void drawPieceOnPopulation(World world, Random rand, int chunkX, int chunkZ) {
		// Piece to draw is:
		int pieceXoffs = this.getPieceX(chunkX) << 4;
		int pieceZoffs = this.getPieceZ(chunkZ) << 4;
		
		Chunk chunk = world.getChunkFromChunkCoords(chunkX, chunkZ);
		
		// Decode & copy blocks
		for(int x = 0; x < 16; x ++) {
			for(int z = 0; z < 16; z ++) {
				int y = this.getY0();
				
				for(int i = 0; i < this.getFeatureHeight(); i ++) {
					short blockAndMeta = this.schematic[pieceXoffs + x][pieceZoffs + z][i];
					
					if(blockAndMeta != -1) {
						byte blockID = (byte)(blockAndMeta & 0xff);
						byte metadata = (byte)(blockAndMeta >> 8);
						
						chunk.setBlockIDWithMetadata(x, y, z, blockID, metadata);
					}
					
					y ++;
					
					if (y == 128) break;
				}
			}
		}
	}
	
	// This method is to be called from populate
	public void drawSpecialBlocksForChunk(World world, Random rand, int chunkX, int chunkZ) {
		int pieceXoffs = this.getPieceX(chunkX) << 4;
		int pieceZoffs = this.getPieceZ(chunkZ) << 4;
		int y0 = this.getY0();
		
		Iterator<StructureBlockData> it = this.specialBlocks.iterator();
		while(it.hasNext()) {
			StructureBlockData s = it.next();
			
			if(s.x >= pieceXoffs && s.x < pieceXoffs + 15 && s.z >= pieceZoffs && s.z < pieceZoffs + 16) {
				int xAbs = this.featureAABB.x1 + s.x;
				int zAbs = this.featureAABB.z1 + s.z;
				int yAbs = y0 + s.y;
				
				world.setBlockAndMetadata(xAbs, yAbs, zAbs, s.blockID, s.blockMetadata);
				this.onSpecialBlockSet(xAbs, yAbs, zAbs, s.blockID, s.blockMetadata);
			}
		}
	}
	
	// You should override these methods accordingly
	public void onSpecialBlockSet(int x, int y, int z, int id, int meta) {
	}
	
	/*
	 * Draws a sphere radius r centered around x0, y0, z0
	 */
	public void sphere(int x0, int y0, int z0, int r, short s) {
		int rSq = r * r;
		for(int x = 0; x < r; x ++) {
			int xx = x * x;
			for(int z = 0; z < r; z ++) {
				int zz = z * z;
				for(int y = 0; y < r; y ++) {
					if(y * y + xx + zz <= rSq) {
						this.schematic[x0 + x][z0 + z][y0 + y] = s;
						this.schematic[x0 - x][z0 + z][y0 + y] = s;
						this.schematic[x0 + x][z0 - z][y0 + y] = s;
						this.schematic[x0 - x][z0 - z][y0 + y] = s;
						this.schematic[x0 + x][z0 + z][y0 - y] = s;
						this.schematic[x0 - x][z0 + z][y0 - y] = s;
						this.schematic[x0 + x][z0 - z][y0 - y] = s;
						this.schematic[x0 - x][z0 - z][y0 - y] = s;
					}
				}
			}
		}
	}
	
	/* 
	 * Fill whole schematic
	 */
	public void fillSchematic(short v) {
		// Fill the whole schematic with v
		for(short[][] arr1: this.schematic) {
			for(short[] arr2: arr1) {
				Arrays.fill(arr2, v);
			}
		}
	}
}