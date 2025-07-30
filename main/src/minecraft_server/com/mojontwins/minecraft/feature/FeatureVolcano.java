package com.mojontwins.minecraft.feature;

import java.util.Arrays;
import java.util.Random;

import net.minecraft.world.level.World;
import net.minecraft.world.level.biome.BiomeGenBase;
import net.minecraft.world.level.chunk.Chunk;
import net.minecraft.world.level.chunk.IChunkProvider;
import net.minecraft.world.level.tile.Block;
import net.minecraft.world.level.tile.entity.TileEntityMobSpawnerOneshot;

public class FeatureVolcano extends FeatureDynamicSchematic {
	// I'm making this configurable so I can generate bigger volcanos in other mods with taller worlds
	private static final int volcanoHeight = 64;
	private static final int oreChance = 16;
	private int width;
	
	public FeatureVolcano(World world, int originChunkX, int originChunkZ, FeatureProvider featureProvider) {
		super(world, originChunkX, originChunkZ, featureProvider);
	}

	@Override
	public int getFeatureHeight() {
		return volcanoHeight;
	}

	@Override
	public void generateSchematic(World world, Random rand, BiomeGenBase biome, int chunkX, int chunkZ) {
		short empty = -1;
		short fill = 1;
		short hollow = 0;
		
		int hollowFromTop = 12;
		
		// Generate our volcano!
		this.width = (1 + this.getFeatureRadius() * 2) * 16;

		// Fill the whole schematic with -1s
		for(short[][] arr1: this.schematic) {
			for(short[] arr2: arr1) {
				Arrays.fill(arr2, empty);
			}
		}
		
		// Draw the top of the volcano
		int iniSqSize = 1 + 2 * this.getFeatureRadius();
		int iniSqPos = this.width / 2 - iniSqSize / 2;
		
		for(int x = 0; x < iniSqSize; x ++) {
			for(int z = 0; z < iniSqSize; z ++) {
				this.schematic[iniSqPos + x][iniSqPos + z][volcanoHeight - 2] = fill;
			}
		}
		
		this.growVolcanoFrom(volcanoHeight - 2, rand, 0.3F, fill);
		
		// Draw the top of the volcano
		int holeSqSize = 5 + 2 * this.getFeatureRadius();
		int holeSqPos = this.width / 2 - holeSqSize / 2;
		
		// Make a hole
		for(int x = 0; x < holeSqSize; x ++) {
			for(int z = 0; z < holeSqSize; z ++) {
				this.schematic[holeSqPos + x][holeSqPos + z][volcanoHeight - hollowFromTop] = hollow;
			}
		}
		
		this.growVolcanoFrom(volcanoHeight - hollowFromTop, rand, 0.2F, hollow);

		// Remove top
		for(int y = volcanoHeight - hollowFromTop; y < volcanoHeight; y ++) {
			for(int x = 0; x < this.width; x ++) {
				for(int z = 0; z < this.width; z ++) {
					this.schematic[x][z][y] = empty;
				}
			}
		}
		

		// Spherical hole
		int radius = iniSqSize + 3;
		int radiusSq = radius * radius;
		int x0 = this.width / 2;
		int y0 = volcanoHeight - hollowFromTop;
		for(int x = 0; x < radius; x ++) {
			int xx = x * x;
			for(int z = 0; z < radius; z ++) {
				int zz = z * z;
				for(int y = 0; y < radius; y ++) {
					if(xx + zz + y * y < radiusSq) {
						this.schematic[x0 + x][x0 + z][y0 - y] = hollow;
						this.schematic[x0 - x][x0 + z][y0 - y] = hollow;
						this.schematic[x0 + x][x0 - z][y0 - y] = hollow;
						this.schematic[x0 - x][x0 - z][y0 - y] = hollow;
					}
				}
			}
		}

		
		// Replace blocks
		for(int x = 0; x < this.width; x ++) {
			for(int z = 0; z < this.width; z ++) {
				
				boolean first = true;
				boolean doReplace = false;
				
				for(int y = volcanoHeight - 1; y > 0; y --) {
					
					if(first && this.schematic[x][z][y] == fill) {
						first = false;
						doReplace = true;
						
						// Add lava
						if(rand.nextInt(200) == 0) {
							this.schematic[x][z][y + 1] = (short)Block.lavaMoving.blockID;
						}
					}
					
					if(doReplace) {
						if(this.schematic[x][z][y] == hollow) {
							doReplace = false;
							
							// Add lava
							if(rand.nextInt(500) == 0) {
								this.schematic[x][z][y + 1] = (short)Block.lavaMoving.blockID;
							}
						}
						
						// Add ores
						if(rand.nextInt(oreChance) == 0) {
							int choice = rand.nextInt(100);

							if (choice < 65) {
								this.schematic[x][z][y] = (short)Block.oreCoal.blockID;
							} else if (choice < 88) {
								this.schematic[x][z][y] = (short)Block.oreIron.blockID;
							} else if (choice < 96) {
								this.schematic[x][z][y] = (short)Block.oreGold.blockID;
							} else if (choice < 99) {
								this.schematic[x][z][y] = (short)Block.oreLapis.blockID;
							} else {
								this.schematic[x][z][y] = (short)Block.oreDiamond.blockID;
							}
						} else {
							this.schematic[x][z][y] = (short)(Block.stone.blockID | (1 << 8));
						}
					}
				}
				
				// Lava at the bottom
				if(this.schematic[x][z][0] == hollow) this.schematic[x][z][0] = (short)Block.lavaStill.blockID;
			}
		}
		
		// Add small island
		this.generateIrregularBlob(x0, 2, rand, 8, 8, 8);
		this.generateIrregularBlob(x0, 1, rand, 8, 16, 8);
		
		System.out.println ("Volcano @ " + this.centerX + " " + this.centerZ);
	}
	
	private void generateIrregularBlob(int x0, int y0, Random rand, int dOffset, int dispersion, int maxPoints) {
		int dOffsetSq = dOffset * dOffset;
		
		FeatureVolcano.SimplePoint [] points = new FeatureVolcano.SimplePoint [maxPoints];
		
		for(int i = 0; i < maxPoints; i ++) {
			points[i] = new FeatureVolcano.SimplePoint(
					x0 + rand.nextInt(dispersion) - dispersion / 2,
					x0 + rand.nextInt(dispersion) - dispersion / 2
				);
		}
		
		for(int x = 0; x < width; x ++) {
			for(int z = 0; z < width; z ++) {
				for(FeatureVolcano.SimplePoint point : points) {
					if(point.distanceSq(x, z) < dOffsetSq) {
						this.schematic[x][z][y0] = (short)Block.cobblestone.blockID;
					}
				}
			}
		}
	}
	
	private void growVolcanoFrom(int topY, Random rand, float chance, short value) {
		// Based on the original idea behind BD volcanos, this does it in 1 pass
		// Each loop it should propagate down "value", then expand that slice horizontally
		
		for(int y = topY - 1; y >= 0; y --) {
			// Copy from top slice
			for(int x = 1; x < this.width - 1; x ++) {
				for(int z = 1; z < this.width - 1; z ++) {
					if(this.schematic[x][z][y + 1] == value) {
						this.schematic[x][z][y] = value;
					} else {
						int expand = 0;
						if(this.schematic[x - 1][z][y + 1] == value) expand ++;
						if(this.schematic[x + 1][z][y + 1] == value) expand ++;
						if(this.schematic[x][z - 1][y + 1] == value) expand ++;
						if(this.schematic[x][z + 1][y + 1] == value) expand ++;
						
						if(expand > 0) {
							if(expand > 1 || rand.nextFloat() < chance) {
								this.schematic[x][z][y] = value;
							}
						}
					}
				}
			}
		}
	}

	@Override
	public int getY0() {
		return this.world.getWorldInfo().getTerrainType().getSeaLevel(this.world);
	}

	@Override
	public int getFeatureRadius() {
		return 1 + volcanoHeight / 64;
	}

	@Override
	public int getSpawnChance() {
		return 16;
	}

	@Override
	public boolean shouldSpawn(IChunkProvider chunkProvider, World world, Random rand, BiomeGenBase biome, int chunkX,
			int chunkZ) {
		return false;
		//return biome instanceof BiomeGenDesert;
	}
	
	@Override
	public int minimumSeparation() {
		return 3;
	}
	
	@Override
	public void generate(int chunkX, int chunkZ, Chunk chunk) {
		this.drawPieceOnGeneration(chunkX, chunkZ, chunk);
		chunk.generateLandSurfaceHeightMap();
	}

	@Override
	public void populate(World world, Random rand, int chunkX, int chunkZ) {
		// Add population
		if(chunkX == this.originChunkX && chunkZ == this.originChunkZ) {
			for(int i = 0; i < 3; i ++) {
				int x = (chunkX << 4) | rand.nextInt(16);
				int y = 3 + this.getY0();
				int z = (chunkZ << 4) | rand.nextInt(16);
				if(world.isAirBlock(x, y, z)) {
					world.setBlockWithNotify(x, y, z, Block.mobSpawnerOneshot.blockID);
					TileEntityMobSpawnerOneshot spawner = (TileEntityMobSpawnerOneshot)world.getBlockTileEntity(x, y, z);
					spawner.setMobID("PigZombie");
				}
			}
		}
	}
	
	public class SimplePoint {
		public final int x;
		public final int y;
		
		public SimplePoint(int x, int y) {
			this.x = x;
			this.y = y;
		}
		
		public int distanceSq(int x, int y) {
			int dx = Math.abs(this.x - x);
			int dy = Math.abs(this.y - y);
			return dx * dx + dy * dy;
		}

		public int taxyDistanceFrom(int x, int y) {
			return Math.abs(this.x - x) + Math.abs(this.y - y);
		}
	}
}
