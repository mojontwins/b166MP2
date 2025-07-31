package net.minecraft.world.level.chunk.storage;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.mojang.nbt.CompressedStreamTools;
import com.mojang.nbt.NBTTagCompound;

import net.minecraft.src.MathHelper;
import net.minecraft.world.level.WorldChunkManager;
import net.minecraft.world.level.WorldChunkManagerHell;
import net.minecraft.world.level.WorldInfo;
import net.minecraft.world.level.WorldType;
import net.minecraft.world.level.biome.BiomeGenBase;
import net.minecraft.world.level.chunk.ChunkLoader;

public class AnvilSaveConverter extends SaveFormatOld {
	public AnvilSaveConverter(File file1) {
		super(file1);
	}

	public String getFormatName() {
		return "Anvil";
	}

	public List<SaveFormatComparator> getSaveList() {
		ArrayList<SaveFormatComparator> savesList = new ArrayList<SaveFormatComparator>();

		File[] filesListA = this.savesDirectory.listFiles();
		File[] filesListC = filesListA;
		int numFiles = filesListA.length;

		for(int i = 0; i < numFiles; ++i) {
			File saveFile = filesListC[i];
			if(saveFile.isDirectory()) {
				String saveFileName = saveFile.getName();
				WorldInfo worldInfo = this.getWorldInfo(saveFileName);
				if(worldInfo != null && (worldInfo.getSaveVersion() == SaveHandler.mcRegion || worldInfo.getSaveVersion() == SaveHandler.anvil || worldInfo.getSaveVersion() == SaveHandler.old)) {
					boolean needsConversion = worldInfo.getSaveVersion() != this.getSaveVersion();
					
					String worldName = worldInfo.getWorldName();
					if(worldName == null || MathHelper.stringNullOrLengthZero(worldName)) {
						worldName = saveFileName;
					}

					long seed = 0L;
					savesList.add(new SaveFormatComparator(saveFileName, worldName, worldInfo.getLastTimePlayed(), seed, worldInfo.getGameType(), needsConversion, worldInfo.isHardcoreModeEnabled()));
				}
			}
		}

		return savesList;
	}

	protected int getSaveVersion() {
		return SaveHandler.anvil;
	}

	public void flushCache() {
		RegionFileCache.clearRegionFileReferences();
	}

	public ISaveHandler getSaveLoader(String string1, boolean z2) {
		return new AnvilSaveHandler(this.savesDirectory, string1, z2);
	}

	public boolean isOldMapFormat(String string1) {
		WorldInfo worldInfo2 = this.getWorldInfo(string1);
		return worldInfo2 != null && worldInfo2.getSaveVersion() != this.getSaveVersion();
	}

	public boolean convertMapFormat(String levelName, IProgressUpdate progress) {
		progress.setLoadingProgress(0);

		ArrayList<File> regionsSurface = new ArrayList<File>();
		ArrayList<File> regionsNether = new ArrayList<File>();
		ArrayList<File> regionsTheEnd = new ArrayList<File>();
		
		File baseDir = new File(this.savesDirectory, levelName);
		File netherDir = new File(baseDir, "DIM-1");
		File theEndDir = new File(baseDir, "DIM1");
		
		System.out.println("Scanning folders...");
		
		// Get list of region files.

		this.getRegionFileList(baseDir, regionsSurface);
		if(netherDir.exists()) {
			this.getRegionFileList(netherDir, regionsNether);
		}

		if(theEndDir.exists()) {
			this.getRegionFileList(theEndDir, regionsTheEnd);
		}

		int totalRegions = regionsSurface.size() + regionsNether.size() + regionsTheEnd.size();
		System.out.println("Total conversion count is " + totalRegions);

		WorldInfo worldInfo = this.getWorldInfo(levelName);

		Object chunkManager = null;
		if(worldInfo.getTerrainType() == WorldType.FLAT) {
			chunkManager = new WorldChunkManagerHell(BiomeGenBase.hell, 0.5F, 0.5F);
		} else {
			chunkManager = new WorldChunkManager(worldInfo.getSeed(), worldInfo.getTerrainType());
		}

		this.convertRegionsDir(new File(baseDir, "region"), regionsSurface, (WorldChunkManager)chunkManager, 0, totalRegions, progress);
		this.convertRegionsDir(new File(netherDir, "region"), regionsNether, new WorldChunkManagerHell(BiomeGenBase.hell, 1.0F, 0.0F), regionsSurface.size(), totalRegions, progress);
		this.convertRegionsDir(new File(theEndDir, "region"), regionsTheEnd, new WorldChunkManagerHell(BiomeGenBase.plains, 0.5F, 0.0F), regionsSurface.size() + regionsNether.size(), totalRegions, progress);
		
		// Change save version to ANVIL
		worldInfo.setSaveVersion(SaveHandler.anvil);

		this.makeMcrBackup(levelName);
		ISaveHandler saveHandler = this.getSaveLoader(levelName, false);
		saveHandler.saveWorldInfo(worldInfo);
		
		return true;
	}

	private void makeMcrBackup(String string1) {
		File file2 = new File(this.savesDirectory, string1);
		if(!file2.exists()) {
			System.out.println("Warning: Unable to create level.dat_mcr backup");
		} else {
			File file3 = new File(file2, "level.dat");
			if(!file3.exists()) {
				System.out.println("Warning: Unable to create level.dat_mcr backup");
			} else {
				File file4 = new File(file2, "level.dat_mcr");
				if(!file3.renameTo(file4)) {
					System.out.println("Warning: Unable to create level.dat_mcr backup");
				}

			}
		}
	}

	private void convertRegionsDir(File regionsDir, ArrayList<File> regionFileList, WorldChunkManager chunkManager, int curRegionNumber, int totalRegions, IProgressUpdate progress) {
		Iterator<File> it = regionFileList.iterator();

		while(it.hasNext()) {
			File regionFile = (File)it.next();
			this.convertRegionFile(regionsDir, regionFile, chunkManager, curRegionNumber, totalRegions, progress);
			++curRegionNumber;
			int percent = (int)Math.round(100.0D * (double)curRegionNumber / (double)totalRegions);
			progress.setLoadingProgress(percent);
		}

	}

	// You could add metadata 4->8 bit conversion here too, so mcr regions are converted to Anvil8bmd regions.
	
	private void convertRegionFile(File regionsDir, File regionsFile, WorldChunkManager chunkManager, int curRegionNumber, int totalRegions, IProgressUpdate progress) {
		try {
			String fileName = regionsFile.getName();

			RegionFile mcrFile = new RegionFile(regionsFile);
			RegionFile mcaFile = new RegionFile(new File(regionsDir, fileName.substring(0, fileName.length() - ".mcr".length()) + ".mca"));

			// Convert chunks

			for(int chunkX = 0; chunkX < 32; ++chunkX) {
				for(int chunkZ = 0; chunkZ < 32; ++chunkZ) {
					if(mcrFile.isChunkSaved(chunkX, chunkZ) && !mcaFile.isChunkSaved(chunkX, chunkZ)) {
						DataInputStream dis = mcrFile.getChunkDataInputStream(chunkX, chunkZ);
						if(dis == null) {
							System.out.println("Failed to fetch input stream");
						} else {
							NBTTagCompound nbt = CompressedStreamTools.read((DataInput)dis);
							dis.close();
							NBTTagCompound levelNbt = nbt.getCompoundTag("Level");
							AnvilConverterData dataOldFormat = ChunkLoader.load(levelNbt);

							NBTTagCompound nbtOut = new NBTTagCompound();
							NBTTagCompound levelOut = new NBTTagCompound();
							nbtOut.setTag("Level", levelOut);

							ChunkLoader.convertToAnvilFormat(dataOldFormat, levelOut, chunkManager);
							
							DataOutputStream dos = mcaFile.getChunkDataOutputStream(chunkX, chunkZ);
							CompressedStreamTools.write(nbtOut, (DataOutput)dos);
							dos.close();
						}
					}
				}

				int a = (int)Math.round(100.0D * (double)(curRegionNumber * 1024) / (double)(totalRegions * 1024));
				int b = (int)Math.round(100.0D * (double)((chunkX + 1) * 32 + curRegionNumber * 1024) / (double)(totalRegions * 1024));
				if(b > a) {
					progress.setLoadingProgress(b);
				}
			}

			mcrFile.close();
			mcaFile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void getRegionFileList(File dir, ArrayList<File> regionFiles) {
		File regionDir = new File(dir, "region");
		
		// Will get all .mcr files.
		File[] fileArray = regionDir.listFiles(new AnvilSaveConverterFileFilter(this));
		
		if(fileArray != null) {
			File[] fileArray2 = fileArray;
			int numFiles = fileArray.length;

			for(int i7 = 0; i7 < numFiles; ++i7) {
				File regionFile = fileArray2[i7];
				regionFiles.add(regionFile);
			}
		}

	}
}
