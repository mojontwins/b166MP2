package net.minecraft.world.level.chunk;

import net.minecraft.world.level.tile.Block;

public class ExtendedBlockStorage {
	private int yBase;
	private int blockRefCount;
	private int tickRefCount;
	private byte[] blockLSBArray;
	private NibbleArray blockMSBArray;
	private NibbleArray blockMetadataArray;
	private NibbleArray blocklightArray;
	private NibbleArray skylightArray;

	public ExtendedBlockStorage(int yBase) {
		this.yBase = yBase;
		this.blockLSBArray = new byte[4096];
		this.blockMetadataArray = new NibbleArray(this.blockLSBArray.length, 4);
		this.skylightArray = new NibbleArray(this.blockLSBArray.length, 4);
		this.blocklightArray = new NibbleArray(this.blockLSBArray.length, 4);
	}
	
	public void resetSkyLightArray() {
		this.skylightArray = new NibbleArray(this.blockLSBArray.length, 4);
	}

	public int getExtBlockID(int x, int y, int z) {
		int id = this.blockLSBArray[y << 8 | z << 4 | x] & 255;
		return this.blockMSBArray != null ? this.blockMSBArray.get(x, y, z) << 8 | id : id;
	}

	public void setExtBlockID(int x, int y, int z, int id) {
		int prevId = this.blockLSBArray[y << 8 | z << 4 | x] & 255;
		if(this.blockMSBArray != null) {
			prevId |= this.blockMSBArray.get(x, y, z) << 8;
		}

		if(prevId == 0 && id != 0) {
			++this.blockRefCount;
			if(Block.blocksList[id] != null && Block.blocksList[id].getTickRandomly()) {
				++this.tickRefCount;
			}
		} else if(prevId != 0 && id == 0) {
			--this.blockRefCount;
			if(Block.blocksList[prevId] != null && Block.blocksList[prevId].getTickRandomly()) {
				--this.tickRefCount;
			}
		} else if(Block.blocksList[prevId] != null && Block.blocksList[prevId].getTickRandomly() && (Block.blocksList[id] == null || !Block.blocksList[id].getTickRandomly())) {
			--this.tickRefCount;
		} else if((Block.blocksList[prevId] == null || !Block.blocksList[prevId].getTickRandomly()) && Block.blocksList[id] != null && Block.blocksList[id].getTickRandomly()) {
			++this.tickRefCount;
		}

		this.blockLSBArray[y << 8 | z << 4 | x] = (byte)(id & 255);
		if(id > 255) {
			if(this.blockMSBArray == null) {
				this.blockMSBArray = new NibbleArray(this.blockLSBArray.length, 4);
			}

			this.blockMSBArray.set(x, y, z, (id & 3840) >> 8);
		} else if(this.blockMSBArray != null) {
			this.blockMSBArray.set(x, y, z, 0);
		}

	}

	public int getExtBlockMetadata(int x, int y, int z) {
		return this.blockMetadataArray.get(x, y, z);
	}

	public void setExtBlockMetadata(int x, int y, int z, int id) {
		this.blockMetadataArray.set(x, y, z, id);
	}

	public boolean getIsEmpty() {
		return this.blockRefCount == 0;
	}

	public boolean getNeedsRandomTick() {
		return this.tickRefCount > 0;
	}

	public int getYLocation() {
		return this.yBase;
	}

	public void setExtSkylightValue(int x, int y, int z, int id) {
		this.skylightArray.set(x, y, z, id);
	}

	public int getExtSkylightValue(int x, int y, int z) {
		return this.skylightArray.get(x, y, z);
	}

	public void setExtBlocklightValue(int x, int y, int z, int id) {
		this.blocklightArray.set(x, y, z, id);
	}

	public int getExtBlocklightValue(int x, int y, int z) {
		return this.blocklightArray.get(x, y, z);
	}

	public void cleanupAndUpdateCounters() {
		this.blockRefCount = 0;
		this.tickRefCount = 0;

		for(int x = 0; x < 16; ++x) {
			for(int y = 0; y < 16; ++y) {
				for(int z = 0; z < 16; ++z) {
					int id = this.getExtBlockID(x, y, z);
					if(id > 0) {
						if(Block.blocksList[id] == null) {
							this.blockLSBArray[y << 8 | z << 4 | x] = 0;
							if(this.blockMSBArray != null) {
								this.blockMSBArray.set(x, y, z, 0);
							}
						} else {
							++this.blockRefCount;
							if(Block.blocksList[id].getTickRandomly()) {
								++this.tickRefCount;
							}
						}
					}
				}
			}
		}

	}

	public void whoKnows() {
	}

	public int blockCount() {
		return this.blockRefCount;
	}

	public byte[] getBlockLSBArray() {
		return this.blockLSBArray;
	}

	public void resetMSBarray() {
		this.blockMSBArray = null;
	}

	public NibbleArray getBlockMSBArray() {
		return this.blockMSBArray;
	}

	public NibbleArray getMetadataArray() {
		return this.blockMetadataArray;
	}

	public NibbleArray getBlocklightArray() {
		return this.blocklightArray;
	}

	public NibbleArray getSkylightArray() {
		return this.skylightArray;
	}

	public void setBlockLSBArray(byte[] b1) {
		this.blockLSBArray = b1;
	}

	public void setBlockMSBArray(NibbleArray nibbleArray) {
		this.blockMSBArray = nibbleArray;
	}

	public void setBlockMetadataArray(NibbleArray nibbleArray) {
		this.blockMetadataArray = nibbleArray;
	}

	public void setBlocklightArray(NibbleArray nibbleArray) {
		this.blocklightArray = nibbleArray;
	}

	public void setSkylightArray(NibbleArray nibbleArray) {
		this.skylightArray = nibbleArray;
	}

	public NibbleArray createBlockMSBArray() {
		this.blockMSBArray = new NibbleArray(this.blockLSBArray.length, 4);
		return this.blockMSBArray;
	}
}
