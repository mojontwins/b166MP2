package net.minecraft.world.level.chunk;

import net.minecraft.world.level.EnumSkyBlock;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.World;
import net.minecraft.world.level.WorldChunkManager;
import net.minecraft.world.level.biome.BiomeGenBase;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.tile.Block;
import net.minecraft.world.level.tile.BlockStairs;
import net.minecraft.world.level.tile.BlockStep;
import net.minecraft.world.level.tile.entity.TileEntity;

public class ChunkCache implements IBlockAccess {
	private int chunkX;
	private int chunkZ;
	private Chunk[][] chunkArray;
	private boolean areChunksEmpty;
	private World worldObj;

	public ChunkCache(World world1, int i2, int i3, int i4, int i5, int i6, int i7) {
		this.worldObj = world1;
		this.chunkX = i2 >> 4;
		this.chunkZ = i4 >> 4;
		int i8 = i5 >> 4;
		int i9 = i7 >> 4;
		this.chunkArray = new Chunk[i8 - this.chunkX + 1][i9 - this.chunkZ + 1];
		this.areChunksEmpty = true;

		for(int i10 = this.chunkX; i10 <= i8; ++i10) {
			for(int i11 = this.chunkZ; i11 <= i9; ++i11) {
				Chunk chunk12 = world1.getChunkFromChunkCoords(i10, i11);
				if(chunk12 != null) {
					this.chunkArray[i10 - this.chunkX][i11 - this.chunkZ] = chunk12;
					if(!chunk12.getAreLevelsEmpty(i3, i6)) {
						this.areChunksEmpty = false;
					}
				}
			}
		}

	}

	public boolean getAreChunksEmpty() {
		return this.areChunksEmpty;
	}

	public int getBlockId(int i1, int i2, int i3) {
		if(i2 < 0) {
			return 0;
		} else if(i2 >= 256) {
			return 0;
		} else {
			int i4 = (i1 >> 4) - this.chunkX;
			int i5 = (i3 >> 4) - this.chunkZ;
			if(i4 >= 0 && i4 < this.chunkArray.length && i5 >= 0 && i5 < this.chunkArray[i4].length) {
				Chunk chunk6 = this.chunkArray[i4][i5];
				return chunk6 == null ? 0 : chunk6.getBlockID(i1 & 15, i2, i3 & 15);
			} else {
				return 0;
			}
		}
	}
	
	public Block getBlock(int x, int y, int z) {
		return Block.blocksList[this.getBlockId(x, y, z)];
	}

	public TileEntity getBlockTileEntity(int i1, int i2, int i3) {
		int i4 = (i1 >> 4) - this.chunkX;
		int i5 = (i3 >> 4) - this.chunkZ;
		return this.chunkArray[i4][i5].getChunkBlockTileEntity(i1 & 15, i2, i3 & 15);
	}

	public float getBrightness(int i1, int i2, int i3, int i4) {
		int i5 = this.getLightValue(i1, i2, i3);
		if(i5 < i4) {
			i5 = i4;
		}

		return this.worldObj.worldProvider.lightBrightnessTable[i5];
	}

	public int getLightBrightnessForSkyBlocks(int i1, int i2, int i3, int i4) {
		int i5 = this.getSkyBlockTypeBrightness(EnumSkyBlock.Sky, i1, i2, i3);
		int i6 = this.getSkyBlockTypeBrightness(EnumSkyBlock.Block, i1, i2, i3);
		if(i6 < i4) {
			i6 = i4;
		}

		return i5 << 20 | i6 << 4;
	}

	public float getLightBrightness(int i1, int i2, int i3) {
		return this.worldObj.worldProvider.lightBrightnessTable[this.getLightValue(i1, i2, i3)];
	}

	public int getLightValue(int i1, int i2, int i3) {
		return this.getLightValueExt(i1, i2, i3, true);
	}

	public int getLightValueExt(int i1, int i2, int i3, boolean z4) {
		{
			int i5;
			int i6;
			if(z4) {
				i5 = this.getBlockId(i1, i2, i3);
				if(i5 == Block.stairSingle.blockID || i5 == Block.tilledField.blockID || i5 == Block.stairCompactPlanks.blockID || i5 == Block.stairCompactCobblestone.blockID) {
					i6 = this.getLightValueExt(i1, i2 + 1, i3, false);
					int i7 = this.getLightValueExt(i1 + 1, i2, i3, false);
					int i8 = this.getLightValueExt(i1 - 1, i2, i3, false);
					int i9 = this.getLightValueExt(i1, i2, i3 + 1, false);
					int i10 = this.getLightValueExt(i1, i2, i3 - 1, false);
					if(i7 > i6) {
						i6 = i7;
					}

					if(i8 > i6) {
						i6 = i8;
					}

					if(i9 > i6) {
						i6 = i9;
					}

					if(i10 > i6) {
						i6 = i10;
					}

					return i6;
				}
			}

			if(i2 < 0) {
				return 0;
			} else if(i2 >= 256) {
				i5 = 15 - this.worldObj.skylightSubtracted;
				if(i5 < 0) {
					i5 = 0;
				}

				return i5;
			} else {
				i5 = (i1 >> 4) - this.chunkX;
				i6 = (i3 >> 4) - this.chunkZ;
				return this.chunkArray[i5][i6].getBlockLightValue(i1 & 15, i2, i3 & 15, this.worldObj.skylightSubtracted);
			}
		}
	}

	public int getBlockMetadata(int i1, int i2, int i3) {
		if(i2 < 0) {
			return 0;
		} else if(i2 >= 256) {
			return 0;
		} else {
			int i4 = (i1 >> 4) - this.chunkX;
			int i5 = (i3 >> 4) - this.chunkZ;
			return this.chunkArray[i4][i5].getBlockMetadata(i1 & 15, i2, i3 & 15);
		}
	}

	public Material getBlockMaterial(int i1, int i2, int i3) {
		int i4 = this.getBlockId(i1, i2, i3);
		return i4 == 0 ? Material.air : Block.blocksList[i4].blockMaterial;
	}

	public BiomeGenBase getBiomeGenForCoords(int i1, int i2) {
		return this.worldObj.getBiomeGenForCoords(i1, i2);
	}

	public boolean isBlockOpaqueCube(int i1, int i2, int i3) {
		Block block4 = Block.blocksList[this.getBlockId(i1, i2, i3)];
		return block4 == null ? false : block4.isOpaqueCube();
	}

	public boolean isBlockNormalCube(int i1, int i2, int i3) {
		Block block4 = Block.blocksList[this.getBlockId(i1, i2, i3)];
		return block4 == null ? false : block4.blockMaterial.blocksMovement() && block4.renderAsNormalBlock();
	}

	public boolean isAirBlock(int i1, int i2, int i3) {
		Block block4 = Block.blocksList[this.getBlockId(i1, i2, i3)];
		return block4 == null;
	}

	public int getSkyBlockTypeBrightness(EnumSkyBlock enumSkyBlock1, int x, int y, int z) {
		if(y < 0) {
			y = 0;
		}

		if(y >= 256) {
			y = 255;
		}

		if(y >= 0 && y < 256) {
			int blockID = this.getBlockId(x, y, z);
			if(Block.useNeighborBrightness[blockID]) {

				// Add: Upside down blocks must consider different shit
				Block block = Block.blocksList[blockID];
				int bTB;

				if(
						((block instanceof BlockStep) && (this.getBlockMetadata(x, y, z) & 8) != 0) ||
						((block instanceof BlockStairs) && (this.getBlockMetadata(x, y, z) & 4) != 0)
				){
					bTB = this.getSpecialBlockBrightness(enumSkyBlock1, x, y - 1, z);
				} else {
					bTB = this.getSpecialBlockBrightness(enumSkyBlock1, x, y + 1, z);
		}

				int bE = this.getSpecialBlockBrightness(enumSkyBlock1, x + 1, y, z);
				int bW = this.getSpecialBlockBrightness(enumSkyBlock1, x - 1, y, z);
				int bN = this.getSpecialBlockBrightness(enumSkyBlock1, x, y, z + 1);
				int bS = this.getSpecialBlockBrightness(enumSkyBlock1, x, y, z - 1);
				if(bE > bTB) {
					bTB = bE;
				}

				if(bW > bTB) {
					bTB = bW;
				}

				if(bN > bTB) {
					bTB = bN;
				}

				if(bS > bTB) {
					bTB = bS;
				}

				return bTB;
			} else {
				int cx = (x >> 4) - this.chunkX;
				int cz = (z >> 4) - this.chunkZ;
				return this.chunkArray[cx][cz].getSavedLightValue(enumSkyBlock1, x & 15, y, z & 15);
			}
		} else {
			return enumSkyBlock1.defaultLightValue;
		}
	}

	public int getSpecialBlockBrightness(EnumSkyBlock enumSkyBlock1, int i2, int i3, int i4) {
		if(i3 < 0) {
			i3 = 0;
		}

		if(i3 >= 256) {
			i3 = 255;
		}

		if(i3 >= 0 && i3 < 256) {
			int i5 = (i2 >> 4) - this.chunkX;
			int i6 = (i4 >> 4) - this.chunkZ;
			return this.chunkArray[i5][i6].getSavedLightValue(enumSkyBlock1, i2 & 15, i3, i4 & 15);
		} else {
			return enumSkyBlock1.defaultLightValue;
		}
	}

	public int getHeight() {
		return 256;
	}
	
	public boolean doesBlockHaveSolidTopSurface(int x, int y, int z) {
        /*
        Block var4 = Block.blocksList[this.getBlockId(par1, par2, par3)];
        return var4 == null ? false 
        		: 
        			(var4.blockMaterial.isOpaque() && var4.renderAsNormalBlock() ? true : 
        				(var4 instanceof BlockStairs ? (this.getBlockMetadata(par1, par2, par3) & 4) == 4 : 
        					(var4 instanceof BlockStep ? (this.getBlockMetadata(par1, par2, par3) & 8) == 8 : 
        						false)));
        */
		Block block = Block.blocksList[this.getBlockId(x, y, z)];
		
		if(block == null) return false;
		if(block.blockMaterial.isOpaque() && block.renderAsNormalBlock()) return true;
		
		int meta = this.getBlockMetadata(x, y, z);
		if(block instanceof BlockStairs) return (meta & 4) == 4;
		if(block instanceof BlockStep) return (meta & 8) == 8;
		
		return false;
	}

	@Override
	public WorldChunkManager getWorldChunkManager() {
		return this.worldObj.getWorldChunkManager();
	}
}
