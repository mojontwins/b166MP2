package net.minecraft.src;

public class ChunkCache implements IBlockAccess {
	private int chunkX;
	private int chunkZ;
	private Chunk[][] chunkArray;
	private World worldObj;

	public ChunkCache(World world1, int i2, int i3, int i4, int i5, int i6, int i7) {
		this.worldObj = world1;
		this.chunkX = i2 >> 4;
		this.chunkZ = i4 >> 4;
		int i8 = i5 >> 4;
		int i9 = i7 >> 4;
		this.chunkArray = new Chunk[i8 - this.chunkX + 1][i9 - this.chunkZ + 1];

		for(int i10 = this.chunkX; i10 <= i8; ++i10) {
			for(int i11 = this.chunkZ; i11 <= i9; ++i11) {
				this.chunkArray[i10 - this.chunkX][i11 - this.chunkZ] = world1.getChunkFromChunkCoords(i10, i11);
			}
		}

	}

	public int getBlockId(int i1, int i2, int i3) {
		if(i2 < 0) {
			return 0;
		} else if(i2 >= 128) {
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

	public int getBlockMetadata(int i1, int i2, int i3) {
		if(i2 < 0) {
			return 0;
		} else if(i2 >= 128) {
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

	public boolean isBlockOpaqueCube(int i1, int i2, int i3) {
		Block block4 = Block.blocksList[this.getBlockId(i1, i2, i3)];
		return block4 == null ? false : block4.blockMaterial.getIsSolid() && block4.func_28025_b();
	}
}
