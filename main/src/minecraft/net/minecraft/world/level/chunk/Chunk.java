package net.minecraft.world.level.chunk;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.mojontwins.utils.BlockUtils;

import net.minecraft.src.MathHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPosition;
import net.minecraft.world.level.EnumSkyBlock;
import net.minecraft.world.level.World;
import net.minecraft.world.level.WorldChunkManager;
import net.minecraft.world.level.biome.BiomeGenBase;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.tile.Block;
import net.minecraft.world.level.tile.entity.TileEntity;
import net.minecraft.world.phys.AxisAlignedBB;

public class Chunk {
	public static boolean isLit;
	public ExtendedBlockStorage[] storageArrays;
	private byte[] blockBiomeArray;
	public int[] precipitationHeightMap;
	public boolean[] updateSkylightColumns;
	public boolean isChunkLoaded;
	public World worldObj;
	public int[] heightMap;
	public byte[] landSurfaceHeightMap;
	public final int xPosition;
	public final int zPosition;
	public Map<ChunkPosition, TileEntity> chunkTileEntityMap;
	public List<Entity>[] entityLists;
	public boolean isTerrainPopulated;
	public boolean isModified;
	public boolean hasEntities;
	public long lastSaveTime;
	public boolean field_50120_o;

	public int heightMapMinimum;

	@SuppressWarnings("unchecked")
	public Chunk(World world1, int i2, int i3) {
		this.storageArrays = new ExtendedBlockStorage[16];
		this.blockBiomeArray = new byte[256];
		this.precipitationHeightMap = new int[256];
		this.updateSkylightColumns = new boolean[256];
		this.chunkTileEntityMap = new HashMap<ChunkPosition, TileEntity>();
		this.isTerrainPopulated = false;
		this.isModified = false;
		this.hasEntities = false;
		this.lastSaveTime = 0L;
		this.field_50120_o = false;

		this.entityLists = new List[16];
		this.worldObj = world1;
		this.xPosition = i2;
		this.zPosition = i3;
		this.heightMap = new int[256];
		this.landSurfaceHeightMap = new byte[256];

		for(int i4 = 0; i4 < this.entityLists.length; ++i4) {
			this.entityLists[i4] = new ArrayList<Entity>();
		}

		Arrays.fill(this.precipitationHeightMap, -999);
		Arrays.fill(this.blockBiomeArray, (byte)-1);
	}
	
	public Chunk(World world, byte[] blockArray, int chunkX, int chunkZ) {
		this(world, chunkX, chunkZ);
		int blockArrayHeight = blockArray.length / 256;
		int index = 0;
		for(int x = 0; x < 16; ++x) {
			for(int z = 0; z < 16; ++z) {
				for(int y = 0; y < blockArrayHeight; ++y) {
					int blockID = blockArray[index ++] & 0xff;
					if(blockID != 0) {
						int ySection = y >> 4;
						if(this.storageArrays[ySection] == null) {
							this.storageArrays[ySection] = new ExtendedBlockStorage(ySection << 4);
						}

						this.storageArrays[ySection].setExtBlockID(x, y & 15, z, blockID);
					}
				}
			}
		}

		this.generateLandSurfaceHeightMap();
	}

	// Use this constructor to build chunks using block IDs > 255 in the base array (i.e. The Freezer)
	public Chunk(World world, short[] blockArray, int chunkX, int chunkZ) {
		this(world, chunkX, chunkZ);
		int blockArrayHeight = blockArray.length / 256;
		int index = 0;
		for(int x = 0; x < 16; ++x) {
			for(int z = 0; z < 16; ++z) {
				for(int y = 0; y < blockArrayHeight; ++y) {
					int blockID = blockArray[index ++] & 0xfff;
					
					if(blockID != 0) {
						int ySection = y >> 4;
						if(this.storageArrays[ySection] == null) {
							this.storageArrays[ySection] = new ExtendedBlockStorage(ySection << 4);
						}

						this.storageArrays[ySection].setExtBlockID(x, y & 15, z, blockID);
					}
				}
			}
		}

		this.generateLandSurfaceHeightMap();
	}
	
	public Chunk(World world, byte[] ids, byte[] metadata, int chunkX, int chunkZ) {
		this(world, chunkX, chunkZ);
		int height = ids.length / 256;

		for(int x = 0; x < 16; ++x) {
			for(int z = 0; z < 16; ++z) {
				for(int y = 0; y < height; ++y) {
					int index = x << 11 | z << 7 | y;
					int id = ids[index] & 255;
					int meta = metadata[index] & 15;
					if(id != 0) {
						int chunkY = y >> 4;
						if(this.storageArrays[chunkY] == null) {
							this.storageArrays[chunkY] = new ExtendedBlockStorage(chunkY << 4);
						}

						this.storageArrays[chunkY].setExtBlockID(x, y & 15, z, id);
						this.storageArrays[chunkY].setExtBlockMetadata(x, y & 15, z, meta);
					}
				}
			}
		}

		this.generateLandSurfaceHeightMap();
	}
	
	public boolean isAtLocation(int i1, int i2) {
		return i1 == this.xPosition && i2 == this.zPosition;
	}

	public int getHeightValue(int i1, int i2) {
		return this.heightMap[i2 << 4 | i1];
	}
	
	public int getLandSurfaceHeightValue(int i1, int i2) {
		return this.landSurfaceHeightMap[i2 << 4 | i1];
	}
	
	public void generateLandSurfaceHeightMap() {
		int index = 0;
		for(int z = 0; z < 16; z ++) {
			for(int x = 0; x < 16; x ++) {
				int y = 255;
				
				while((( this.getBlockID(x, y, z)) == 0) && y > 0) { y --; }
				this.landSurfaceHeightMap[index ++] = (byte)y;
			}
		}
	}

	public int getTopFilledSegment() {
		for(int i1 = this.storageArrays.length - 1; i1 >= 0; --i1) {
			if(this.storageArrays[i1] != null) {
				return this.storageArrays[i1].getYLocation();
			}
		}

		return 0;
	}

	public ExtendedBlockStorage[] getBlockStorageArray() {
		return this.storageArrays;
	}

	public void generateHeightMap() {
		int y = 0;
		int y0 = this.getTopFilledSegment();
		this.heightMapMinimum = 256;
		
		for(int x = 0; x < 16; ++x) {
			for(int z = 0; z < 16; ++z) {
				this.precipitationHeightMap[x + (z << 4)] = -999;

				for(y = y0 + 16 - 1; y > 0; --y) {
					int blockBelowID = this.getBlockID(x, y - 1, z);
					if(Block.lightOpacity[blockBelowID] != 0) {
						this.heightMap[z << 4 | x] = y;
						
						if (y < this.heightMapMinimum) {
							this.heightMapMinimum = y;
						}
						break;
					}
				}
				
				if(y == 0) this.heightMapMinimum = 0;
			}
		}

		this.isModified = true;
	}
	
	public void clearAllLights() {
		int y0 = this.getTopFilledSegment();
		for(int y = 0; y <= (y0 >> 4); y ++) {
			if(this.storageArrays[y] != null) this.storageArrays[y].resetSkyLightArray();
		}
	}

	public void generateSkylightMap() {
		int y0 = this.getTopFilledSegment();

		int x;
		int z;
		for(x = 0; x < 16; ++x) {
			for(z = 0; z < 16; ++z) {
				this.precipitationHeightMap[x + (z << 4)] = -999;

				for(int y = y0 + 16 - 1; y > 0; --y) {
					if(this.getBlockLightOpacity(x, y - 1, z) != 0) {
						this.heightMap[z << 4 | x] = y;
						break;
					}
				}

				// Initial light from the sky
				if(!this.worldObj.worldProvider.hasNoSky || this.worldObj.worldProvider.isCaveWorld) {
					int light = 15;
					int y = y0 + 16 - 1;

					do {
						light -= this.getBlockLightOpacity(x, y, z);
						if(light > 0) {
							ExtendedBlockStorage extendedBlockStorage6 = this.storageArrays[y >> 4];
							if(extendedBlockStorage6 != null) {
								extendedBlockStorage6.setExtSkylightValue(x, y & 15, z, light);
								this.worldObj.markBlockNeedsUpdate((this.xPosition << 4) + x, y, (this.zPosition << 4) + z);
							}
						}

						--y;
					} while(y > 0 && light > 0);
				}
			}
		}

		this.isModified = true;

	}
	
	public void doNothing() {
	}
	
	/*
	 * This method relights a column from the sky after setting block at x, y, z.
	 */
	private void relightBlock(int x, int y, int z) {
		int i = this.heightMap[z << 4 | x] & 255;

		int j = Math.max(y, i);

		while (j > 0 && this.getBlockLightOpacity(x, j - 1, z) == 0) {
			--j;
		}

		if (j != i) {
			this.heightMap[z << 4 | x] = (short) j;

			int l1 = this.heightMap[z << 4 | x];

			if (l1 < this.heightMapMinimum) {
				this.heightMapMinimum = l1;
			}

			this.isModified = true;
		}
	}

	public int getBlockLightOpacity(int i1, int i2, int i3) {
		return Block.lightOpacity[this.getBlockID(i1, i2, i3)];
	}

	public int getBlockID(int i1, int i2, int i3) {
		if((i2 >> 4) >= this.storageArrays.length || i2 < 0) {
			return 0;
		} else {
			ExtendedBlockStorage extendedBlockStorage4 = this.storageArrays[i2 >> 4];
			return extendedBlockStorage4 != null ? extendedBlockStorage4.getExtBlockID(i1, i2 & 15, i3) : 0;
		}
	}

	public int getBlockMetadata(int i1, int i2, int i3) {
		if((i2 >> 4) >= this.storageArrays.length || i2 < 0) {
			return 0;
		} else {
			ExtendedBlockStorage extendedBlockStorage4 = this.storageArrays[i2 >> 4];
			return extendedBlockStorage4 != null ? extendedBlockStorage4.getExtBlockMetadata(i1, i2 & 15, i3) : 0;
		}
	}

	public boolean setBlockID(int i1, int i2, int i3, int i4) {
		return this.setBlockIDWithMetadata(i1, i2, i3, i4, 0);
	}

	public boolean setBlockIDWithMetadata(int x, int y, int z, int blockID, int metadata) {
		int hmIdx = z << 4 | x;

		// If we are painting over the topmost block in the column, mark it "dirty"
		if(y >= this.precipitationHeightMap[hmIdx] - 1) {
			this.precipitationHeightMap[hmIdx] = -999;
		}

		int height = this.heightMap[hmIdx];
		int existingBlockID = this.getBlockID(x, y, z);
		int existingMetadata = this.getBlockMetadata(x, y, z);
		if(existingBlockID == blockID && existingMetadata == metadata) {
			return false;
		} else {
			ExtendedBlockStorage blockStorage = this.storageArrays[y >> 4];
			boolean aboveTop = false;
			if(blockStorage == null) {
				if(blockID == 0) {
					return false;
				}

				blockStorage = this.storageArrays[y >> 4] = new ExtendedBlockStorage(y & 0xf0);
				aboveTop = y >= height;
			}

			// Write new block ID
			blockStorage.setExtBlockID(x, y & 15, z, blockID);
			
			int absX = this.xPosition * 16 + x;
			int absZ = this.zPosition * 16 + z;
			if(existingBlockID != 0) {
				Block block = Block.blocksList[existingBlockID];
				if(!this.worldObj.isRemote) {
					block.onBlockRemoval(this.worldObj, absX, y, absZ, existingBlockID, existingMetadata);
				//} else if(block instanceof BlockContainer && existingBlockID != blockID) {
				} else if(block.hasTileEntity(this.getBlockMetadata(x, y, z)) && existingBlockID != blockID) {
					this.worldObj.removeBlockTileEntity(absX, y, absZ);
				}
			}

			if(blockStorage.getExtBlockID(x, y & 15, z) != blockID) {
				return false;
			} else {
				// Write new metadata
				blockStorage.setExtBlockMetadata(x, y & 15, z, metadata);
				if(aboveTop) {
					this.generateSkylightMap();
					this.initLightingForRealNotJustHeightmap();
				} else {
					// If block is not 100% transparent
					if(Block.lightOpacity[blockID & 4095] > 0) {
						// And set above current topmost block
						if(y >= height) {
							// Relight from just above this new block
							this.relightBlock(x, y + 1, z);
						}
					} else {
					// Set a 100% transparent block

					// If it is replacing the topmost block, relight from here.
						if(y == height - 1) {
							this.relightBlock(x, y, z);
						}
					}

				}
				this.updateLight(x, y, z);

				TileEntity tileEntity13;
				if(blockID != 0) {
					if(!this.worldObj.isRemote) {
						Block.blocksList[blockID].onBlockAdded(this.worldObj, absX, y, absZ);
					}

					//if(Block.blocksList[blockID] instanceof BlockContainer) {
					if(Block.blocksList[blockID] != null && Block.blocksList[blockID].hasTileEntity(metadata)) {
						tileEntity13 = this.getChunkBlockTileEntity(x, y, z);
						if(tileEntity13 == null) {
							tileEntity13 = Block.blocksList[blockID].getTileEntity(metadata);
							this.worldObj.setBlockTileEntity(absX, y, absZ, tileEntity13);
						}

						if(tileEntity13 != null) {
							tileEntity13.updateContainingBlockInfo();
							tileEntity13.blockMetadata = metadata;
						}
					}
				//} else if(existingBlockID > 0 && Block.blocksList[existingBlockID] instanceof BlockContainer) {
				} else if(Block.blocksList[existingBlockID] != null && Block.blocksList[existingBlockID].hasTileEntity(metadata)) {
					tileEntity13 = this.getChunkBlockTileEntity(x, y, z);
					if(tileEntity13 != null) {
						tileEntity13.updateContainingBlockInfo();
					}
				}

				this.isModified = true;
				return true;
			}
		}
	}
	
	public boolean setBlockIDAndMetadataColumn(int x, int y, int z, int[] id) {
		// Column is bottom to top ordered
		// Every array entry is encoded as id | meta << 12, with id = 0..4095
		
		// Safe version. 
		// TODO: Write faster version.

		for(int i = 0; i < id.length; i ++) {
			int b = id[i];
			if(b >= 0) {
				// Direct block set
				this.setBlockIDWithMetadata(x, y++, z, b & 4095, b >> 12);
			} else if(b < -1) {
				// Run
				int count = -b;
				i++;
				b = id[i];
				if(b == -1) {
					y += count;
				} else {
					int meta = b >> 12;
					int blockID = b & 4095;
					while(count -- > 0) {
						this.setBlockIDWithMetadata(x, y++, z, blockID, meta);
					}
				}
			} else {
				// Gap
				y++;
			}
		}

		return true;
	}
	
	public boolean clearBlockNoLights(int x, int y, int z) {
		int existingBlockID = this.getBlockID(x, y, z);
		int existingMetadata = this.getBlockMetadata(x, y, z);
		if (existingBlockID == 0) return false;
		
		ExtendedBlockStorage blockStorage = this.storageArrays[y >> 4];
		if(blockStorage == null) {
			blockStorage = this.storageArrays[y >> 4] = new ExtendedBlockStorage(y >> 4 << 4);
		}

		blockStorage.setExtBlockID(x, y & 15, z, 0);
		
		if(existingBlockID != 0) {
			int absX = this.xPosition * 16 + x;
			int absZ = this.zPosition * 16 + z;
			//if(Block.blocksList[existingBlockID] instanceof BlockContainer) {
			if(Block.blocksList[existingBlockID] != null && Block.blocksList[existingBlockID].hasTileEntity(existingMetadata)) {
				this.worldObj.removeBlockTileEntity(absX, y, absZ);
			}
		}
		
		blockStorage.setExtBlockMetadata(x, y & 15, z, 0);
		
		return true;
	}
	
	public boolean setBlockIDWithMetadataNoLights(int x, int y, int z, int blockID, int metadata) {
		int hmIdx = z << 4 | x;

		// If we are painting over the topmost block in the column, mark it "dirty"
		if(y >= this.precipitationHeightMap[hmIdx] - 1) {
			this.precipitationHeightMap[hmIdx] = -999;
		}

		int existingBlockID = this.getBlockID(x, y, z);
		int existingMetadata = this.getBlockMetadata(x, y, z);
		if(existingBlockID == blockID && this.getBlockMetadata(x, y, z) == metadata) {
			return false;
		} else {
			ExtendedBlockStorage blockStorage = this.storageArrays[y >> 4];
			if(blockStorage == null) {
				if(blockID == 0) {
					return false;
				}

				blockStorage = this.storageArrays[y >> 4] = new ExtendedBlockStorage(y >> 4 << 4);
			}

			blockStorage.setExtBlockID(x, y & 15, z, blockID);
			
			int absX = this.xPosition * 16 + x;
			int absZ = this.zPosition * 16 + z;
			if(existingBlockID != 0) {
				if(!this.worldObj.isRemote) {
					Block.blocksList[existingBlockID].onBlockRemoval(this.worldObj, absX, y, absZ, existingBlockID, existingMetadata);
				//} else if(Block.blocksList[existingBlockID] instanceof BlockContainer && existingBlockID != blockID) {
				} else if(Block.blocksList[existingBlockID].hasTileEntity(this.getBlockMetadata(x, y, z)) && existingBlockID != blockID) {
					this.worldObj.removeBlockTileEntity(absX, y, absZ);
				}
			}

			if(blockStorage.getExtBlockID(x, y & 15, z) != blockID) {
				return false;
			} else {
				blockStorage.setExtBlockMetadata(x, y & 15, z, metadata);
				
				TileEntity tileEntity13;
				if(blockID != 0) {
					if(!this.worldObj.isRemote) {
						Block block = Block.blocksList[blockID]; 
						if(block == null) System.out.println ("BAD ID = " + blockID);
						if(block != null) block.onBlockAdded(this.worldObj, absX, y, absZ);
					}

					//if(Block.blocksList[blockID] instanceof BlockContainer) {
					if(Block.blocksList[blockID] != null && Block.blocksList[blockID].hasTileEntity(metadata)) {	
						tileEntity13 = this.getChunkBlockTileEntity(x, y, z);
						if(tileEntity13 == null) {
							tileEntity13 = Block.blocksList[blockID].getTileEntity(metadata);
							this.worldObj.setBlockTileEntity(absX, y, absZ, tileEntity13);
						}

						if(tileEntity13 != null) {
							tileEntity13.updateContainingBlockInfo();
							tileEntity13.blockMetadata = metadata;
						}
					}
				//} else if(existingBlockID > 0 && Block.blocksList[existingBlockID] instanceof BlockContainer) {
				} else if(Block.blocksList[existingBlockID] != null && Block.blocksList[existingBlockID].hasTileEntity(metadata)) {
					tileEntity13 = this.getChunkBlockTileEntity(x, y, z);
					if(tileEntity13 != null) {
						tileEntity13.updateContainingBlockInfo();
					}
				}

				this.isModified = true;
				return true;
			}
		}
	}

	public void setEncodedBlockColumn(int x, int y, int z, short[] column) {
		int hmIdx = z << 4 | x;

		// If we are painting over the topmost block in the column, mark it "dirty"
		if(y + column.length >= this.precipitationHeightMap[hmIdx] - 1) {
			this.precipitationHeightMap[hmIdx] = -999;
		}
		
		int height = this.heightMap[hmIdx];
		boolean aboveTop = false;

		for(int i = 0; i < column.length; i ++) {
			short element = column[i];
			if(element != -1) {
				int blockID = (int)(element & 0xff);
				int metadata = (int)((element >> 8) & 0xf);

				ExtendedBlockStorage blockStorage = this.storageArrays[y >> 4];
				if(blockStorage == null) {
					blockStorage = this.storageArrays[y >> 4] = new ExtendedBlockStorage(y >> 4 << 4);
					aboveTop = y >= height;
				}

				blockStorage.setExtBlockID(x, y & 15, z, blockID);
				blockStorage.setExtBlockMetadata(x, y & 15, z, metadata);

				if(Block.lightOpacity[blockID & 4095] > 0) {
					if(y >= height) {
						this.relightBlock(x, y + 1, z);
					}
				} else if(y == height - 1) {
					this.relightBlock(x, y, z);
				}


			}

			y ++;
		}

		if(aboveTop) {
			this.generateSkylightMap();
			if (!this.worldObj.isRemote) {
				this.initLightingForRealNotJustHeightmap();
			}
		} 
 	}
	
	public boolean setBlockMetadata(int i1, int i2, int i3, int i4) {
		ExtendedBlockStorage extendedBlockStorage5 = this.storageArrays[i2 >> 4];
		if(extendedBlockStorage5 == null) {
			return false;
		} else {
			int i6 = extendedBlockStorage5.getExtBlockMetadata(i1, i2 & 15, i3);
			if(i6 == i4) {
				return false;
			} else {
				this.isModified = true;
				extendedBlockStorage5.setExtBlockMetadata(i1, i2 & 15, i3, i4);
				int i7 = extendedBlockStorage5.getExtBlockID(i1, i2 & 15, i3);
				//if(i7 > 0 && Block.blocksList[i7] instanceof BlockContainer) {
				if(Block.blocksList[i7] != null && Block.blocksList[i7].hasTileEntity(extendedBlockStorage5.getExtBlockMetadata(i1, i2 & 15, i3))) {
					TileEntity tileEntity8 = this.getChunkBlockTileEntity(i1, i2, i3);
					if(tileEntity8 != null) {
						tileEntity8.updateContainingBlockInfo();
						tileEntity8.blockMetadata = i4;
					}
				}

				return true;
			}
		}
	}

	public int getSavedLightValue(EnumSkyBlock enumSkyBlock1, int i2, int i3, int i4) {
		ExtendedBlockStorage extendedBlockStorage5 = this.storageArrays[i3 >> 4];
		return extendedBlockStorage5 == null ? enumSkyBlock1.defaultLightValue : (enumSkyBlock1 == EnumSkyBlock.Sky ? extendedBlockStorage5.getExtSkylightValue(i2, i3 & 15, i4) : (enumSkyBlock1 == EnumSkyBlock.Block ? extendedBlockStorage5.getExtBlocklightValue(i2, i3 & 15, i4) : enumSkyBlock1.defaultLightValue));
	}

	public void setLightValue(EnumSkyBlock enumSkyBlock1, int i2, int i3, int i4, int i5) {
		ExtendedBlockStorage extendedBlockStorage6 = this.storageArrays[i3 >> 4];
		if(extendedBlockStorage6 == null) {
			extendedBlockStorage6 = this.storageArrays[i3 >> 4] = new ExtendedBlockStorage(i3 >> 4 << 4);
			this.generateSkylightMap();
		}

		this.isModified = true;
		if(enumSkyBlock1 == EnumSkyBlock.Sky) {
			if(!this.worldObj.worldProvider.hasNoSky || this.worldObj.worldProvider.isCaveWorld) {
				extendedBlockStorage6.setExtSkylightValue(i2, i3 & 15, i4, i5);
			}
		} else {
			if(enumSkyBlock1 != EnumSkyBlock.Block) {
				return;
			}

			extendedBlockStorage6.setExtBlocklightValue(i2, i3 & 15, i4, i5);
		}

	}

	public int getBlockLightValue(int i1, int i2, int i3, int i4) {
		ExtendedBlockStorage extendedBlockStorage5 = this.storageArrays[i2 >> 4];
		if(extendedBlockStorage5 == null) {
			return (!this.worldObj.worldProvider.hasNoSky  || this.worldObj.worldProvider.isCaveWorld) && i4 < EnumSkyBlock.Sky.defaultLightValue ? EnumSkyBlock.Sky.defaultLightValue - i4 : 0;
		} else {
			//int i6 = this.worldObj.worldProvider.hasNoSky && !this.worldObj.worldProvider.isCaveWorld ? 0 : extendedBlockStorage5.getExtSkylightValue(i1, i2 & 15, i3);
			
			int i6 = (this.worldObj.worldProvider.isCaveWorld || !this.worldObj.worldProvider.hasNoSky) ? extendedBlockStorage5.getExtSkylightValue(i1, i2 & 15, i3) : 0;
			
			if(i6 > 0) {
				isLit = true;
			}

			i6 -= i4;
			int i7 = extendedBlockStorage5.getExtBlocklightValue(i1, i2 & 15, i3);
			if(i7 > i6) {
				i6 = i7;
			}

			return i6;
		}
	}

	public void addEntity(Entity entity1) {
		this.hasEntities = true;
		int i2 = MathHelper.floor_double(entity1.posX / 16.0D);
		int i3 = MathHelper.floor_double(entity1.posZ / 16.0D);
		if(i2 != this.xPosition || i3 != this.zPosition) {
			System.out.println("Wrong location! " + entity1);
			Thread.dumpStack();
		}

		int i4 = MathHelper.floor_double(entity1.posY / 16.0D);
		if(i4 < 0) {
			i4 = 0;
		}

		if(i4 >= this.entityLists.length) {
			i4 = this.entityLists.length - 1;
		}

		entity1.addedToChunk = true;
		entity1.chunkCoordX = this.xPosition;
		entity1.chunkCoordY = i4;
		entity1.chunkCoordZ = this.zPosition;
		this.entityLists[i4].add(entity1);
	}

	public void removeEntity(Entity entity1) {
		this.removeEntityAtIndex(entity1, entity1.chunkCoordY);
	}

	public void removeEntityAtIndex(Entity entity1, int i2) {
		if(i2 < 0) {
			i2 = 0;
		}

		if(i2 >= this.entityLists.length) {
			i2 = this.entityLists.length - 1;
		}

		this.entityLists[i2].remove(entity1);
	}

	public boolean canBlockSeeTheSky(int i1, int i2, int i3) {
		return i2 >= this.heightMap[i3 << 4 | i1];
	}

	public TileEntity getChunkBlockTileEntity(int i1, int i2, int i3) {
		ChunkPosition chunkPosition4 = new ChunkPosition(i1, i2, i3);
		TileEntity tileEntity5 = (TileEntity)this.chunkTileEntityMap.get(chunkPosition4);
		if(tileEntity5 == null) {
			int i6 = this.getBlockID(i1, i2, i3);
			int meta = this.getBlockMetadata(i1, i2, i3);
			if(i6 <= 0 || !Block.blocksList[i6].hasTileEntity()) {
				return null;
		}
		
			if(tileEntity5 == null) {
				//tileEntity5 = ((BlockContainer)Block.blocksList[i6]).getBlockEntity();
				tileEntity5 = Block.blocksList[i6].getTileEntity(meta);
				this.worldObj.setBlockTileEntity(this.xPosition * 16 + i1, i2, this.zPosition * 16 + i3, tileEntity5);
			}

			tileEntity5 = (TileEntity)this.chunkTileEntityMap.get(chunkPosition4);
			}

		if(tileEntity5 != null && tileEntity5.isInvalid()) {
			this.chunkTileEntityMap.remove(chunkPosition4);
			return null;
		} else {
			return tileEntity5;
		}
	}

	public void addTileEntity(TileEntity tileEntity1) {
		int i2 = tileEntity1.xCoord - this.xPosition * 16;
		int i3 = tileEntity1.yCoord;
		int i4 = tileEntity1.zCoord - this.zPosition * 16;
		this.setChunkBlockTileEntity(i2, i3, i4, tileEntity1);
		if(this.isChunkLoaded) {
			this.worldObj.loadedTileEntityList.add(tileEntity1);
		}

	}

	public void setChunkBlockTileEntity(int i1, int i2, int i3, TileEntity tileEntity4) {
		ChunkPosition chunkPosition5 = new ChunkPosition(i1, i2, i3);
		tileEntity4.worldObj = this.worldObj;
		tileEntity4.xCoord = this.xPosition * 16 + i1;
		tileEntity4.yCoord = i2;
		tileEntity4.zCoord = this.zPosition * 16 + i3;
		//if(this.getBlockID(i1, i2, i3) != 0 && Block.blocksList[this.getBlockID(i1, i2, i3)] instanceof BlockContainer) {
		int blockID = this.getBlockID(i1, i2, i3);
		if(Block.blocksList[blockID] != null && Block.blocksList[blockID].hasTileEntity(this.getBlockMetadata(i1, i2, i3))) {
			tileEntity4.validate();
			this.chunkTileEntityMap.put(chunkPosition5, tileEntity4);
		}

	}

	public void removeChunkBlockTileEntity(int i1, int i2, int i3) {
		ChunkPosition chunkPosition4 = new ChunkPosition(i1, i2, i3);
		if(this.isChunkLoaded) {
			TileEntity tileEntity5 = (TileEntity)this.chunkTileEntityMap.remove(chunkPosition4);

			if(tileEntity5 != null) {
				tileEntity5.invalidate();
			}
		}

	}

	public void onChunkLoad() {
		this.isChunkLoaded = true;
		this.worldObj.addTileEntity(this.chunkTileEntityMap.values());

		for(int i1 = 0; i1 < this.entityLists.length; ++i1) {
			this.worldObj.addLoadedEntities(this.entityLists[i1]);
		}

	}

	public void onChunkUnload() {
		this.isChunkLoaded = false;
		Iterator<TileEntity> iterator1 = this.chunkTileEntityMap.values().iterator();

		while(iterator1.hasNext()) {
			TileEntity tileEntity2 = (TileEntity)iterator1.next();
			this.worldObj.markTileEntityForDespawn(tileEntity2);
		}

		for(int i3 = 0; i3 < this.entityLists.length; ++i3) {
			this.worldObj.unloadEntities(this.entityLists[i3]);
		}

	}

	public void setChunkModified() {
		this.isModified = true;
	}

	public void getEntitiesWithinAABBForEntity(Entity entity1, AxisAlignedBB axisAlignedBB2, List<Entity> list3) {
		int i4 = MathHelper.floor_double((axisAlignedBB2.minY - 2.0D) / 16.0D);
		int i5 = MathHelper.floor_double((axisAlignedBB2.maxY + 2.0D) / 16.0D);
		if(i4 < 0) {
			i4 = 0;
		}

		if(i5 >= this.entityLists.length) {
			i5 = this.entityLists.length - 1;
		}

		for(int i6 = i4; i6 <= i5; ++i6) {
			List<Entity> list7 = this.entityLists[i6];

			for(int i8 = 0; i8 < list7.size(); ++i8) {
				Entity entity9 = (Entity)list7.get(i8);
				if(entity9 != entity1 && entity9.boundingBox.intersectsWith(axisAlignedBB2)) {
					list3.add(entity9);
					Entity[] entity10 = entity9.getParts();
					if(entity10 != null) {
						for(int i11 = 0; i11 < entity10.length; ++i11) {
							entity9 = entity10[i11];
							if(entity9 != entity1 && entity9.boundingBox.intersectsWith(axisAlignedBB2)) {
								list3.add(entity9);
							}
						}
					}
				}
			}
		}

	}

	public void getEntitiesOfTypeWithinAAAB(Class<?> class1, AxisAlignedBB axisAlignedBB2, List<Entity> list3) {
		int i4 = MathHelper.floor_double((axisAlignedBB2.minY - 2.0D) / 16.0D);
		int i5 = MathHelper.floor_double((axisAlignedBB2.maxY + 2.0D) / 16.0D);
		if(i4 < 0) {
			i4 = 0;
		} else if(i4 >= this.entityLists.length) {
			i4 = this.entityLists.length - 1;
		}

		if(i5 >= this.entityLists.length) {
			i5 = this.entityLists.length - 1;
		} else if(i5 < 0) {
			i5 = 0;
		}

		for(int i6 = i4; i6 <= i5; ++i6) {
			List<Entity> list7 = this.entityLists[i6];

			for(int i8 = 0; i8 < list7.size(); ++i8) {
				Entity entity9 = (Entity)list7.get(i8);
				if(class1.isAssignableFrom(entity9.getClass()) && entity9.boundingBox.intersectsWith(axisAlignedBB2)) {
					list3.add(entity9);
				}
			}
		}

	}

	public boolean needsSaving(boolean z1) {
		if(z1) {
			if(this.hasEntities && this.worldObj.getWorldTime() != this.lastSaveTime) {
				return true;
			}
		} else if(this.hasEntities && this.worldObj.getWorldTime() >= this.lastSaveTime + 600L) {
			return true;
		}

		return this.isModified;
	}

	public Random getRandomWithSeed(long j1) {
		return new Random(this.worldObj.getSeed() + (long)(this.xPosition * this.xPosition * 4987142) + (long)(this.xPosition * 5947611) + (long)(this.zPosition * this.zPosition) * 4392871L + (long)(this.zPosition * 389711) ^ j1);
	}

	public boolean isEmpty() {
		return false;
	}

	public void removeUnknownBlocks() {
		ExtendedBlockStorage[] extendedBlockStorage1 = this.storageArrays;
		int i2 = extendedBlockStorage1.length;

		for(int i3 = 0; i3 < i2; ++i3) {
			ExtendedBlockStorage extendedBlockStorage4 = extendedBlockStorage1[i3];
			if(extendedBlockStorage4 != null) {
				extendedBlockStorage4.whoKnows();
			}
		}

	}

	public void populateChunk(IChunkProvider iChunkProvider1, IChunkProvider iChunkProvider2, int i3, int i4) {
		if(!this.isTerrainPopulated && iChunkProvider1.chunkExists(i3 + 1, i4 + 1) && iChunkProvider1.chunkExists(i3, i4 + 1) && iChunkProvider1.chunkExists(i3 + 1, i4)) {
			iChunkProvider1.populate(iChunkProvider2, i3, i4);
		}

		if(iChunkProvider1.chunkExists(i3 - 1, i4) && !iChunkProvider1.provideChunk(i3 - 1, i4).isTerrainPopulated && iChunkProvider1.chunkExists(i3 - 1, i4 + 1) && iChunkProvider1.chunkExists(i3, i4 + 1) && iChunkProvider1.chunkExists(i3 - 1, i4 + 1)) {
			iChunkProvider1.populate(iChunkProvider2, i3 - 1, i4);
		}

		if(iChunkProvider1.chunkExists(i3, i4 - 1) && !iChunkProvider1.provideChunk(i3, i4 - 1).isTerrainPopulated && iChunkProvider1.chunkExists(i3 + 1, i4 - 1) && iChunkProvider1.chunkExists(i3 + 1, i4 - 1) && iChunkProvider1.chunkExists(i3 + 1, i4)) {
			iChunkProvider1.populate(iChunkProvider2, i3, i4 - 1);
		}

		if(iChunkProvider1.chunkExists(i3 - 1, i4 - 1) && !iChunkProvider1.provideChunk(i3 - 1, i4 - 1).isTerrainPopulated && iChunkProvider1.chunkExists(i3, i4 - 1) && iChunkProvider1.chunkExists(i3 - 1, i4)) {
			iChunkProvider1.populate(iChunkProvider2, i3 - 1, i4 - 1);
		}

	}

	public int getPrecipitationHeight(int i1, int i2) {
		int i3 = i1 | i2 << 4;
		int i4 = this.precipitationHeightMap[i3];
		if(i4 == -999) {
			int i5 = this.getTopFilledSegment() + 15;
			i4 = -1;

			while(true) {
				while(i5 > 0 && i4 == -1) {
					int i6 = this.getBlockID(i1, i5, i2);
					Material material7 = i6 == 0 ? Material.air : Block.blocksList[i6].blockMaterial;
					if(!material7.blocksMovement() && !material7.isLiquid()) {
						--i5;
					} else {
						i4 = i5 + 1;
					}
				}

				this.precipitationHeightMap[i3] = i4;
				break;
			}
		}

		return i4;
	}

	public ChunkCoordIntPair getChunkCoordIntPair() {
		return new ChunkCoordIntPair(this.xPosition, this.zPosition);
	}

	public boolean getAreLevelsEmpty(int i1, int i2) {
		if(i1 < 0) {
			i1 = 0;
		}

		if(i2 >= 256) {
			i2 = 255;
		}

		for(int i3 = i1; i3 <= i2; i3 += 16) {
			ExtendedBlockStorage extendedBlockStorage4 = this.storageArrays[i3 >> 4];
			if(extendedBlockStorage4 != null && !extendedBlockStorage4.getIsEmpty()) {
				return false;
			}
		}

		return true;
	}

	public void setStorageArrays(ExtendedBlockStorage[] extendedBlockStorage1) {
		this.storageArrays = extendedBlockStorage1;
	}

	public void setChunkData(byte[] b1, int i2, int i3, boolean z4) {
		int i5 = 0;

		int i6;
		for(i6 = 0; i6 < this.storageArrays.length; ++i6) {
			if((i2 & 1 << i6) != 0) {
				if(this.storageArrays[i6] == null) {
					this.storageArrays[i6] = new ExtendedBlockStorage(i6 << 4);
				}

				byte[] b7 = this.storageArrays[i6].getBlockLSBArray();
				System.arraycopy(b1, i5, b7, 0, b7.length);
				i5 += b7.length;
			} else if(z4 && this.storageArrays[i6] != null) {
				this.storageArrays[i6] = null;
			}
		}

		NibbleArray nibbleArray8;
		for(i6 = 0; i6 < this.storageArrays.length; ++i6) {
			if((i2 & 1 << i6) != 0 && this.storageArrays[i6] != null) {
				nibbleArray8 = this.storageArrays[i6].getMetadataArray();
				System.arraycopy(b1, i5, nibbleArray8.data, 0, nibbleArray8.data.length);
				i5 += nibbleArray8.data.length;
			}
		}

		for(i6 = 0; i6 < this.storageArrays.length; ++i6) {
			if((i2 & 1 << i6) != 0 && this.storageArrays[i6] != null) {
				nibbleArray8 = this.storageArrays[i6].getBlocklightArray();
				System.arraycopy(b1, i5, nibbleArray8.data, 0, nibbleArray8.data.length);
				i5 += nibbleArray8.data.length;
			}
		}

		for(i6 = 0; i6 < this.storageArrays.length; ++i6) {
			if((i2 & 1 << i6) != 0 && this.storageArrays[i6] != null) {
				nibbleArray8 = this.storageArrays[i6].getSkylightArray();
				System.arraycopy(b1, i5, nibbleArray8.data, 0, nibbleArray8.data.length);
				i5 += nibbleArray8.data.length;
			}
		}

		for(i6 = 0; i6 < this.storageArrays.length; ++i6) {
			if((i3 & 1 << i6) != 0) {
				if(this.storageArrays[i6] == null) {
					i5 += 2048;
				} else {
					nibbleArray8 = this.storageArrays[i6].getBlockMSBArray();
					if(nibbleArray8 == null) {
						nibbleArray8 = this.storageArrays[i6].createBlockMSBArray();
					}

					System.arraycopy(b1, i5, nibbleArray8.data, 0, nibbleArray8.data.length);
					i5 += nibbleArray8.data.length;
				}
			} else if(z4 && this.storageArrays[i6] != null && this.storageArrays[i6].getBlockMSBArray() != null) {
				this.storageArrays[i6].resetMSBarray();
			}
		}

		if(z4) {
			System.arraycopy(b1, i5, this.blockBiomeArray, 0, this.blockBiomeArray.length);
		}

		for(i6 = 0; i6 < this.storageArrays.length; ++i6) {
			if(this.storageArrays[i6] != null && (i2 & 1 << i6) != 0) {
				this.storageArrays[i6].cleanupAndUpdateCounters();
			}
		}

		this.generateHeightMap();
		Iterator<TileEntity> iterator10 = this.chunkTileEntityMap.values().iterator();

		while(iterator10.hasNext()) {
			TileEntity tileEntity9 = (TileEntity)iterator10.next();
			tileEntity9.updateContainingBlockInfo();
		}

	}

	public BiomeGenBase getBiomeForCoords(int x, int z, WorldChunkManager chunkManager) {
		int biomeID = this.blockBiomeArray[z << 4 | x] & 255;
		if(biomeID == 255) {
			BiomeGenBase biome = chunkManager.getBiomeGenAt((this.xPosition << 4) + x, (this.zPosition << 4) + z);
			biomeID = biome.biomeID;
			this.blockBiomeArray[z << 4 | x] = (byte)(biomeID & 255);
		}

		return BiomeGenBase.biomeList[biomeID] == null ? BiomeGenBase.plains : BiomeGenBase.biomeList[biomeID];
	}

	public byte[] getBiomeArray() {
		return this.blockBiomeArray;
	}

	public void setBiomeArray(byte[] b1) {
		this.blockBiomeArray = b1;
	}

	public boolean isBlockUnderground(int x, int y, int z) {
		while(y < 255) {
			int blockID = this.getBlockID(x, y, z);
			if(BlockUtils.isSoil(blockID)) return true;
			y ++;
		}
		return false;
	}

	public void initLightingForRealNotJustHeightmap() {
		this.worldObj.blockLight.initBlockLight(this.xPosition, this.zPosition);
		if (!this.worldObj.worldProvider.hasNoSky) {
			this.worldObj.skyLight.initSkylight(this.xPosition, this.zPosition);
		}
	}

	public void updateLight(int localX, int worldY, int localZ) {
		int worldX = localX | (this.xPosition << 4);
		int worldZ = localZ | (this.zPosition << 4);

		this.worldObj.blockLight.checkBlockEmittance(worldX, worldY, worldZ);
		if (!this.worldObj.worldProvider.hasNoSky) {
			this.worldObj.skyLight.checkSkyEmittance(worldX, worldY, worldZ);
		}
	}

	public void setBlockIDSimple(int x, int y, int z, int blockID, int metadata) {
		if(blockID != 0) {
			int ySection = y >> 4;
			if(this.storageArrays[ySection] == null) {
				this.storageArrays[ySection] = new ExtendedBlockStorage(ySection << 4);
			}

			this.storageArrays[ySection].setExtBlockID(x, y & 15, z, blockID);
			this.storageArrays[ySection].setExtBlockMetadata(x, y & 15, z, metadata);
		}
	}

}
