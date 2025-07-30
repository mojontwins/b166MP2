package com.mojontwins.minecraft.feature;

import java.util.Random;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.World;
import net.minecraft.world.level.chunk.Chunk;
import net.minecraft.world.level.tile.Block;
import net.minecraft.world.level.tile.entity.TileEntityChest;
import net.minecraft.world.level.tile.entity.TileEntityMobSpawner;
import net.minecraft.world.level.tile.entity.TileEntityMobSpawnerOneshot;

public abstract class FeatureBuilding {
	public int xAbsolute;
	public int y0;
	public int zAbsolute;

	private int x1;
	private int z1;
	
	public Chunk chunk;
	protected World world;
	
	protected boolean rotated;
	
	public FeatureAABB aabb;
	protected Random buildingRand = new Random();	
	
	public FeatureBuilding(World world, boolean rotated) {
		this.world = world;
		this.rotated = rotated;
	}
	
	public void placeBuilding(int xAbsolute, int y0, int zAbsolute) {
		// Absolute (world) block positions of this building
		this.xAbsolute = xAbsolute;
		this.y0 = y0;
		this.zAbsolute = zAbsolute;	
			
		// The AABB for this building in the XZ plane
		this.aabb = new FeatureAABB(this.xAbsolute, 0, this.zAbsolute, this.xAbsolute + this.getWidth() - 1, 127, this.zAbsolute + this.getLength() - 1);
	}
	
	public void clipBuilding(int xChunk, int zChunk) {
		// Those are the x, z block positions of the chunk it's being painted,
		// NOT then chunk where this building is at, and is used for clipping.
		this.x1 = xChunk << 4;
		this.z1 = zChunk << 4;
		
		// This is the chunk it's being rendered. It's possible this building 
		// is not in this chunk at all. Ideally, the "generate" and "populate"
		// methods won't get called if that's the situation.
		this.chunk = world.getChunkFromChunkCoords(xChunk, zChunk);
		
		// Seed my own random number generator
		this.buildingRand.setSeed(xAbsolute * 341873128712L + zAbsolute * 132897987541L);
	}

	/*
	 * To be refined by each non abstract feature building, the width of the bulding (X dimension)
	 */
	protected abstract int buildingWidth();

	/*
	 * To be refined by each non abstract feature building, the length of the bulding (Z dimension)
	 */
	protected abstract int buildingLength();
	
	/*
	 * To be refined by each non abstract feature building, the height of the bulding (Y dimension)
	 */
	protected abstract int buildingHeight();
	
	/*
	 * Buildings may be rotated, so this always returns the width (X dimension).
	 */
	public int getWidth() {
		return this.rotated ? this.buildingLength() : this.buildingWidth();
	}

	/*
	 * Buildings may be rotated, so this always returns the height (Z dimension).
	 */
	public int getLength() {
		return this.rotated ? this.buildingWidth() : this.buildingLength();
	}
	
	/*
	 * Returns the height of the building (Y dimension).
	 */
	public int getHeight() {
		return this.buildingHeight();
	}
	
	/*
	 * Translates some blocks metadata so they look right when the building is rotated
	 */
	public int translateMetadata(int id, int meta) {
		// Rotation means rotating right, so:
		
		switch(id) {
			case 17:
				// Log 
				
				if(meta == 4) meta = 12; 
				else if(meta == 12) meta = 4;
				break;
			
			case 50:
				// Torch
				switch(meta) {
					case 1: meta = 3; break;
					case 2: meta = 4; break;
					case 3: meta = 1; break;
					case 4: meta = 2; break;
				}
				break;
				
			case 64:
				// Door, change bits 0 and 1:
				// E(0) <-> S(1) : 00 <-> 01
				// W(2) <-> N(3) : 10 <-> 11
				// Bit 0 should be toggled, or "XOR 10", or "^ 2":
				meta = meta ^ 1;
				break;
				
			case 65:
				// Ladder
				switch(meta) {
					case 2: meta = 4; break;
					case 3: meta = 5; break;
					case 4: meta = 2; break;
					case 5: meta = 3; break;
				}
				break;
			
			case 53:
				// StairsCompactPlanks
			case 67:
				// StairsCompactCobblestone
				switch(meta) {
					case 0: meta = 2; break;
					case 1: meta = 3; break;
					case 2: meta = 4; break;
					case 3: meta = 5; break;
				}
		}
		
		return meta;
	}
	
	public void translateMetadataList(int []blocks) {
		for(int i = 0; i < blocks.length; i ++) {
			if(blocks[i] > 255) {
				int meta = blocks[i] >> 8;
				int id = blocks[i] & 255;
				
				meta = this.translateMetadata(id, meta);
				
				blocks[i] = (meta << 8) | id;
			}
		}
	}

	/*
	 * Checks if x, y, z is opaque, relative to xAbsolute/zAbsolute and clipped to the current chunk
	 */
	public boolean isBlockOpaqueCubeRelative(int x, int y, int z) {
		y += this.y0;
		int blockID;
		if(this.rotated) {
			z += this.xAbsolute - this.x1;
			x += this.zAbsolute - this.z1;
			if(x < 0 || z < 0 || x > 15 || z > 15) return true; 
			blockID = this.chunk.getBlockID(z, y, x);
		} else {
			x += this.xAbsolute - this.x1;
			z += this.zAbsolute - this.z1;
			if(x < 0 || z < 0 || x > 15 || z > 15) return true; 
			blockID = this.chunk.getBlockID(x, y, z);
		}
		
		return Block.opaqueCubeLookup[blockID];
	}

	/*
	 * Sets block at x, y, z as id, relative to xAbsolute/zAbsolute and clipped to the current chunk
	 */
	public boolean setBlockRelative(int x, int y, int z, int id, int meta) {
		y += this.y0;
		if(this.rotated) {
			meta = this.translateMetadata(id, meta);
			z += this.xAbsolute - this.x1;
			x += this.zAbsolute - this.z1;
			if(x < 0 || z < 0 || x > 15 || z > 15) return false; 		
			return this.chunk.setBlockIDWithMetadata(z, y, x, id, meta);
		} else {
			x += this.xAbsolute - this.x1;
			z += this.zAbsolute - this.z1;
			if(x < 0 || z < 0 || x > 15 || z > 15) return false;		
			return this.chunk.setBlockIDWithMetadata(x, y, z, id, meta);
		}
	}
	
	/*
	 * Gets block at x, y, z as id, relative to xAbsolute/zAbsolute and clipped to the current chunk
	 * Returns -1 if block is out of bounds
	 */
	public int getBlockRelative(int x, int y, int z) {
		y += this.y0;
		if(this.rotated) {
			z += this.xAbsolute - this.x1;
			x += this.zAbsolute - this.z1;
			if(x < 0 || z < 0 || x > 15 || z > 15) return -1; 
			return this.chunk.getBlockID(z, y, x);
		} else {
			x += this.xAbsolute - this.x1;
			z += this.zAbsolute - this.z1;
			if(x < 0 || z < 0 || x > 15 || z > 15) return -1; 
			return this.chunk.getBlockID(x, y, z);
		}
	}
	
	/*
	 * Draws a column of blocks (RLE'd) at x, y0, z, relative to xAbsolute/zAbsolute and clipped to the current chunk,
	 */
	public boolean setBlockColumnRelative(int x, int z, int[] blocks) {
		if(this.rotated) {
			this.translateMetadataList(blocks);
			z += this.xAbsolute - this.x1;
			x += this.zAbsolute - this.z1;
			if(x < 0 || z < 0 || x > 15 || z > 15) return false; 
			return chunk.setBlockIDAndMetadataColumn(z, this.y0, x, blocks);
		} else {
			x += this.xAbsolute - this.x1;
			z += this.zAbsolute - this.z1;
			if(x < 0 || z < 0 || x > 15 || z > 15) return false; 
			return chunk.setBlockIDAndMetadataColumn(x, this.y0, z, blocks);
		}
	}
	
	/*
	 * Draws a 3D box using id / meta, clipped, relative, rotated.
	 */
	public boolean drawClippedBoxRelative(int x1, int y1, int z1, int x2, int y2, int z2, int id, int meta) {
		y1 += this.y0;
		y2 += this.y0;
		
		if(this.rotated) {
			meta = this.translateMetadata(id, meta);
			
			z1 += this.xAbsolute - this.x1;
			z2 += this.xAbsolute - this.x1;
			x1 += this.zAbsolute - this.z1;
			x2 += this.zAbsolute - this.z1;

			// Completely outside
			if(x1 > 15 || x2 < 0 || z1 > 15 || z2 < 0) return false;
			
			// Clip to chunk
			if(x1 < 0) x1 = 0;
			if(x2 > 15) x2 = 15;
			if(z1 < 0) z1 = 0;
			if(z2 > 15) z2 = 15;
			
			// Paint clipped
			for(int x = x1; x <= x2; x ++) {
				for(int z = z1; z <= z2; z ++) {
					for(int y = y1; y <= y2; y ++) {
						this.chunk.setBlockIDWithMetadata(z, y, x, id, meta);
					}
				}
			}			
		} else {
			x1 += this.xAbsolute - this.x1;
			x2 += this.xAbsolute - this.x1;
			z1 += this.zAbsolute - this.z1;
			z2 += this.zAbsolute - this.z1;
			
			// Completely outside
			if(x1 > 15 || x2 < 0 || z1 > 15 || z2 < 0) return false;
			
			// Clip to chunk
			if(x1 < 0) x1 = 0;
			if(x2 > 15) x2 = 15;
			if(z1 < 0) z1 = 0;
			if(z2 > 15) z2 = 15;
			
			// Paint clipped
			for(int x = x1; x <= x2; x ++) {
				for(int z = z1; z <= z2; z ++) {
					for(int y = y1; y <= y2; y ++) {
						this.chunk.setBlockIDWithMetadata(x, y, z, id, meta);
					}
				}
			}
		}
		
		return true;
	}
	
	/*
	 * Draws a 3D box usin id / meta, clipped, relative, rotated.
	 * This version will only substitute solid blocks, which is useful to create "open" dungeons
	 */
	public boolean drawClippedBoxRelativeOnSolid(int x1, int y1, int z1, int x2, int y2, int z2, int id, int meta) {
		y1 += this.y0;
		y2 += this.y0;
		
		if(this.rotated) {
			meta = this.translateMetadata(id, meta);
			
			z1 += this.xAbsolute - this.x1;
			z2 += this.xAbsolute - this.x1;
			x1 += this.zAbsolute - this.z1;
			x2 += this.zAbsolute - this.z1;
			
			// Completely outside
			if(x1 > 15 || x2 < 0 || z1 > 15 || z2 < 0) return false;
			
			// Clip to chunk
			if(x1 < 0) x1 = 0;
			if(x2 > 15) x2 = 15;
			if(z1 < 0) z1 = 0;
			if(z2 > 15) z2 = 15;
			
			// Paint clipped
			for(int x = x1; x <= x2; x ++) {
				for(int z = z1; z <= z2; z ++) {
					for(int y = y1; y <= y2; y ++) {					
						Block block = Block.blocksList[this.chunk.getBlockID(z, y, x)];
						if(block != null && block.isOpaqueCube()) {
							this.chunk.setBlockIDWithMetadata(z, y, x, id, meta);
						}
					}
				}
			}			
		} else {
			x1 += this.xAbsolute - this.x1;
			x2 += this.xAbsolute - this.x1;
			z1 += this.zAbsolute - this.z1;
			z2 += this.zAbsolute - this.z1;
			
			// Completely outside
			if(x1 > 15 || x2 < 0 || z1 > 15 || z2 < 0) return false;
			
			// Clip to chunk
			if(x1 < 0) x1 = 0;
			if(x2 > 15) x2 = 15;
			if(z1 < 0) z1 = 0;
			if(z2 > 15) z2 = 15;
			
			// Paint clipped
			for(int x = x1; x <= x2; x ++) {
				for(int z = z1; z <= z2; z ++) {
					for(int y = y1; y <= y2; y ++) {
						Block block = Block.blocksList[this.chunk.getBlockID(x, y, z)];
						if(block != null && block.isOpaqueCube()) {
							this.chunk.setBlockIDWithMetadata(x, y, z, id, meta);
						}
					}
				}
			}
		}
		
		return true;
	}
	
	public boolean drawClippedBoxRelativeWithTwoRandomIds(int x1, int y1, int z1, int x2, int y2, int z2, int id, int id2, int meta, int chance) {
		y1 += this.y0;
		y2 += this.y0;
		
		if(this.rotated) {
			meta = this.translateMetadata(id, meta);
			
			z1 += this.xAbsolute - this.x1;
			z2 += this.xAbsolute - this.x1;
			x1 += this.zAbsolute - this.z1;
			x2 += this.zAbsolute - this.z1;
			
			// Completely outside
			if(x1 > 15 || x2 < 0 || z1 > 15 || z2 < 0) return false;
			
			// Clip to chunk
			if(x1 < 0) x1 = 0;
			if(x2 > 15) x2 = 15;
			if(z1 < 0) z1 = 0;
			if(z2 > 15) z2 = 15;
			
			// Paint clipped
			for(int x = x1; x <= x2; x ++) {
				for(int z = z1; z <= z2; z ++) {
					for(int y = y1; y <= y2; y ++) {					
						Block block = Block.blocksList[this.chunk.getBlockID(z, y, x)];
						if(block != null && block.isOpaqueCube()) {
							this.chunk.setBlockIDWithMetadata(z, y, x, this.world.rand.nextInt(chance) == 0 ? id : id2, meta);
						}
					}
				}
			}			
		} else {
			x1 += this.xAbsolute - this.x1;
			x2 += this.xAbsolute - this.x1;
			z1 += this.zAbsolute - this.z1;
			z2 += this.zAbsolute - this.z1;
			
			// Completely outside
			if(x1 > 15 || x2 < 0 || z1 > 15 || z2 < 0) return false;
			
			// Clip to chunk
			if(x1 < 0) x1 = 0;
			if(x2 > 15) x2 = 15;
			if(z1 < 0) z1 = 0;
			if(z2 > 15) z2 = 15;
			
			// Paint clipped
			for(int x = x1; x <= x2; x ++) {
				for(int z = z1; z <= z2; z ++) {
					for(int y = y1; y <= y2; y ++) {
						Block block = Block.blocksList[this.chunk.getBlockID(x, y, z)];
						if(block != null && block.isOpaqueCube()) {
							this.chunk.setBlockIDWithMetadata(x, y, z, this.world.rand.nextInt(chance) == 0 ? id : id2, meta);
						}
					}
				}
			}
		}
		
		return true;
	}
	
	public void addChestRelative(int x, int y, int z) {
		if(this.rotated) {
			this.addChest(z + this.xAbsolute, y + this.y0, x + this.zAbsolute);
		} else {
			this.addChest(x + this.xAbsolute, y + this.y0, z + this.zAbsolute);
		}
	}
	
	public void addSpawnerRelative(int x, int y, int z, String mobID) {
		if(this.rotated) {
			this.addSpawner(z + this.xAbsolute, this.y0 + y, x + this.zAbsolute, mobID);
		} else {
			this.addSpawner(x + this.xAbsolute, this.y0 + y, z + this.zAbsolute, mobID);
		}
	}
	
	public void addSpawnerOneshotRelative(int x, int y, int z, String mobID) {
		if(this.rotated) {
			this.addSpawnerOneshot(z + this.xAbsolute, this.y0 + y, x + this.zAbsolute, mobID);
		} else {
			this.addSpawnerOneshot(x + this.xAbsolute, this.y0 + y, z + this.zAbsolute, mobID);
		}
	}
	
	public void addChest(int x, int y, int z) {
		if(x < this.x1 || x > this.x1 + 15 || z < this.z1 || z > this.z1 + 15) return;
		
		this.world.setBlockWithNotify(x, y, z, Block.chest.blockID);
		TileEntityChest tileEntityChest = (TileEntityChest)this.world.getBlockTileEntity(x, y, z);
		
		if(tileEntityChest != null && tileEntityChest.getSizeInventory() > 0) {
			int ni = this.getChestNumberOfItems();
			int level = this.world.rand.nextInt(10);
			
			for(int i = 0; i < ni; ++i) {
				ItemStack itemStack = this.getTreasure(level, this.world.rand);
				tileEntityChest.setInventorySlotContents(i, itemStack);
			}			
		}
	}
	
	public void addSpawner(int x, int y, int z, String mobID) {
		if(x < this.x1 || x > this.x1 + 15 || z < this.z1 || z > this.z1 + 15) return;
	
		this.world.setBlockWithNotify(x, y, z, Block.mobSpawnerOneshot.blockID);
		TileEntityMobSpawner tileEntityMobSpawner = (TileEntityMobSpawner)this.world.getBlockTileEntity(x, y, z);
		if(tileEntityMobSpawner != null) {
			tileEntityMobSpawner.setMobID(mobID);
		}
		
	}
	
	public void addSpawnerOneshot(int x, int y, int z, String mobID) {
		if(x < this.x1 || x > this.x1 + 15 || z < this.z1 || z > this.z1 + 15) return;
		this.world.setBlockWithNotify(x, y, z, Block.mobSpawnerOneshot.blockID);
		TileEntityMobSpawnerOneshot tileEntityMobSpawner = (TileEntityMobSpawnerOneshot)this.world.getBlockTileEntity(x, y, z);
		if(tileEntityMobSpawner != null) {
			tileEntityMobSpawner.setMobID(mobID);
		}
		
	}
	
	public void terraform(int n) {
		// Raises terrain to y - 1, 		
		for(int x = 0; x < this.buildingWidth(); x ++) {
			for(int z = 0; z < this.buildingLength(); z ++) {
				int y = this.y0 - 1;
				while(!this.isBlockOpaqueCubeRelative(x, y, z) && y > 1) {
					this.setBlockRelative(x, y, z, Block.cobblestone.blockID, 0);
					y --;
				}
			}
		}
		
		// clears n blocks tall from y
		/*
		for(int y = this.y0; y < this.y0 + n; y ++) {
			for(int x = 0; x < this.buildingHeight(); x ++) {
				for(int z = 0; z < this.buildingLength(); z ++) {
					this.setBlockRelative(x, y, z, 0);
				}
			}
		}
		*/
	}
	
	protected ItemStack getTreasure(int level, Random rand) {
		return new ItemStack(Item.ingotGold);
	}
	
	protected int getChestNumberOfItems() {
		return this.world.rand.nextInt(4) + 4;
	}

	public abstract void generate();
	
	public void populate(int chunkX, int chunkZ) {
		// Clip building to this chunk boundaries
		this.clipBuilding(chunkX, chunkZ);

		// If building fits, build it.
		if(this.buildingFitsCheck()) this.build();
	}
	
	public int getLandSurfaceAtCenter(int x, int z) {
		return this.world.getLandSurfaceHeightValue(x + this.getWidth() / 2, z + this.getLength() / 2);
	}
	
	public abstract boolean buildingFitsCheck();
	
	/*
	 * Different buildings must implement this method which actually place the blocks which make the building
	 * using methods found in this class such as `setBlockColumnRelative` et al.
	 */
	public abstract void build();
}
