package net.minecraft.world.level.chunk;

import com.mojang.nbt.NBTTagCompound;
import com.mojang.nbt.NBTTagList;

import net.minecraft.world.level.WorldChunkManager;
import net.minecraft.world.level.chunk.storage.AnvilConverterData;

public class ChunkLoader {
	public static AnvilConverterData load(NBTTagCompound nBTTagCompound0) {
		int i1 = nBTTagCompound0.getInteger("xPos");
		int i2 = nBTTagCompound0.getInteger("zPos");
		AnvilConverterData anvilConverterData3 = new AnvilConverterData(i1, i2);
		anvilConverterData3.blocks = nBTTagCompound0.getByteArray("Blocks");
		anvilConverterData3.data = new NibbleArrayReader(nBTTagCompound0.getByteArray("Data"), 7);
		anvilConverterData3.skyLight = new NibbleArrayReader(nBTTagCompound0.getByteArray("SkyLight"), 7);
		anvilConverterData3.blockLight = new NibbleArrayReader(nBTTagCompound0.getByteArray("BlockLight"), 7);
		anvilConverterData3.heightmap = nBTTagCompound0.getByteArray("HeightMap");
		anvilConverterData3.terrainPopulated = nBTTagCompound0.getBoolean("TerrainPopulated");
		anvilConverterData3.entities = nBTTagCompound0.getTagList("Entities");
		anvilConverterData3.tileEntities = nBTTagCompound0.getTagList("TileEntities");
		anvilConverterData3.tileTicks = nBTTagCompound0.getTagList("TileTicks");

		try {
			anvilConverterData3.lastUpdated = nBTTagCompound0.getLong("LastUpdate");
		} catch (ClassCastException classCastException5) {
			anvilConverterData3.lastUpdated = (long)nBTTagCompound0.getInteger("LastUpdate");
		}

		return anvilConverterData3;
	}

	public static void convertToAnvilFormat(AnvilConverterData anvilConverterData0, NBTTagCompound compoundTag, WorldChunkManager worldChunkManager2) {
		compoundTag.setInteger("xPos", anvilConverterData0.x);
		compoundTag.setInteger("zPos", anvilConverterData0.z);
		compoundTag.setLong("LastUpdate", anvilConverterData0.lastUpdated);
		int[] i3 = new int[anvilConverterData0.heightmap.length];

		for(int i4 = 0; i4 < anvilConverterData0.heightmap.length; ++i4) {
			i3[i4] = anvilConverterData0.heightmap[i4];
		}

		compoundTag.func_48183_a("HeightMap", i3);
		compoundTag.setBoolean("TerrainPopulated", anvilConverterData0.terrainPopulated);
		NBTTagList nBTTagList16 = new NBTTagList("Sections");

		int i7;
		for(int i5 = 0; i5 < 8; ++i5) {
			boolean z6 = true;

			for(i7 = 0; i7 < 16 && z6; ++i7) {
				for(int i8 = 0; i8 < 16 && z6; ++i8) {
					for(int i9 = 0; i9 < 16; ++i9) {
						int i10 = i7 << 11 | i9 << 7 | i8 + (i5 << 4);
						byte b11 = anvilConverterData0.blocks[i10];
						if(b11 != 0) {
							z6 = false;
							break;
						}
					}
				}
			}

			if(!z6) {
				byte[] b19 = new byte[4096];
				NibbleArray nibbleArray20 = new NibbleArray(b19.length, 4);
				NibbleArray nibbleArray21 = new NibbleArray(b19.length, 4);
				NibbleArray nibbleArray22 = new NibbleArray(b19.length, 4);

				for(int i11 = 0; i11 < 16; ++i11) {
					for(int i12 = 0; i12 < 16; ++i12) {
						for(int i13 = 0; i13 < 16; ++i13) {
							int i14 = i11 << 11 | i13 << 7 | i12 + (i5 << 4);
							byte b15 = anvilConverterData0.blocks[i14];
							b19[i12 << 8 | i13 << 4 | i11] = (byte)(b15 & 255);
							nibbleArray20.set(i11, i12, i13, anvilConverterData0.data.get(i11, i12 + (i5 << 4), i13));
							nibbleArray21.set(i11, i12, i13, anvilConverterData0.skyLight.get(i11, i12 + (i5 << 4), i13));
							nibbleArray22.set(i11, i12, i13, anvilConverterData0.blockLight.get(i11, i12 + (i5 << 4), i13));
						}
					}
				}

				NBTTagCompound nBTTagCompound24 = new NBTTagCompound();
				nBTTagCompound24.setByte("Y", (byte)(i5 & 255));
				nBTTagCompound24.setByteArray("Blocks", b19);
				nBTTagCompound24.setByteArray("Data", nibbleArray20.data);
				nBTTagCompound24.setByteArray("SkyLight", nibbleArray21.data);
				nBTTagCompound24.setByteArray("BlockLight", nibbleArray22.data);
				nBTTagList16.appendTag(nBTTagCompound24);
			}
		}

		compoundTag.setTag("Sections", nBTTagList16);
		byte[] b17 = new byte[256];

		for(int i18 = 0; i18 < 16; ++i18) {
			for(i7 = 0; i7 < 16; ++i7) {
				b17[i7 << 4 | i18] = (byte)(worldChunkManager2.getBiomeGenAt(anvilConverterData0.x << 4 | i18, anvilConverterData0.z << 4 | i7).biomeID & 255);
			}
		}

		compoundTag.setByteArray("Biomes", b17);
		compoundTag.setTag("Entities", anvilConverterData0.entities);
		compoundTag.setTag("TileEntities", anvilConverterData0.tileEntities);
		if(anvilConverterData0.tileTicks != null) {
			compoundTag.setTag("TileTicks", anvilConverterData0.tileTicks);
		}

	}
}
