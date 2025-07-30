package net.minecraft.world.level.chunk.storage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.zip.GZIPInputStream;

import com.mojang.nbt.CompressedStreamTools;
import com.mojang.nbt.NBTTagCompound;

import net.minecraft.world.level.WorldInfo;

public class SaveFormatOld implements ISaveFormat {
	protected final File savesDirectory;

	public SaveFormatOld(File file1) {
		if(!file1.exists()) {
			file1.mkdirs();
		}

		this.savesDirectory = file1;
	}

	public String getFormatName() {
		return "Old Format";
	}

	public List<SaveFormatComparator> getSaveList() {
		ArrayList<SaveFormatComparator> arrayList1 = new ArrayList<SaveFormatComparator>();

		for(int i2 = 0; i2 < 5; ++i2) {
			String string3 = "World" + (i2 + 1);
			WorldInfo worldInfo4 = this.getWorldInfo(string3);
			if(worldInfo4 != null) {
				arrayList1.add(new SaveFormatComparator(string3, "", worldInfo4.getLastTimePlayed(), worldInfo4.getSizeOnDisk(), worldInfo4.getGameType(), false, worldInfo4.isHardcoreModeEnabled()));
			}
		}

		return arrayList1;
	}

	public void flushCache() {
	}

	public WorldInfo getWorldInfo(String string1) {
		File file2 = new File(this.savesDirectory, string1);
		if(!file2.exists()) {
			return null;
		} else {
			File file3 = new File(file2, "level.dat");
			NBTTagCompound nBTTagCompound4;
			NBTTagCompound nBTTagCompound5;
			if(file3.exists()) {
				try {
					nBTTagCompound4 = CompressedStreamTools.readCompressed(new FileInputStream(file3));
					nBTTagCompound5 = nBTTagCompound4.getCompoundTag("Data");
					return new WorldInfo(nBTTagCompound5);
				} catch (Exception exception7) {
					exception7.printStackTrace();
				}
			}

			file3 = new File(file2, "level.dat_old");
			if(file3.exists()) {
				try {
					nBTTagCompound4 = CompressedStreamTools.readCompressed(new FileInputStream(file3));
					nBTTagCompound5 = nBTTagCompound4.getCompoundTag("Data");
					return new WorldInfo(nBTTagCompound5);
				} catch (Exception exception6) {
					exception6.printStackTrace();
				}
			}

			return null;
		}
	}

	public ISaveHandler getSaveLoader(String string1, boolean z2) {
		return new SaveHandler(this.savesDirectory, string1, z2);
	}

	public boolean isOldMapFormat(String string1) {
		return false;
	}

	public boolean convertMapFormat(String string1, IProgressUpdate iProgressUpdate2) {
		return false;
	}

	public void converMapToMCRegion(String levelName, IProgressUpdate progress) {
		progress.setLoadingProgress(0);
		ArrayList<ChunkFile> chunkFilesSurface = new ArrayList<ChunkFile>();
		ArrayList<File> chunkFoldersSurface = new ArrayList<File>();
		ArrayList<ChunkFile> chunkFilesNether = new ArrayList<ChunkFile>();
		ArrayList<File> chunkFoldersNether = new ArrayList<File>();

		File baseDir = new File(this.savesDirectory, levelName);
		File netherDir = new File(baseDir, "DIM-1");

		System.out.println("Scanning folders...");

		this.scanFolder(baseDir, chunkFilesSurface, chunkFoldersSurface);
		if(netherDir.exists()) {
			this.scanFolder(netherDir, chunkFilesNether, chunkFoldersNether);
		}

		int totalConversions = chunkFilesSurface.size() + chunkFilesNether.size() + chunkFoldersSurface.size() + chunkFoldersNether.size();
		System.out.println("Total conversion count is " + totalConversions);

		this.convertToMcr(baseDir, chunkFilesSurface, 0, totalConversions, progress);
		this.convertToMcr(netherDir, chunkFilesNether, chunkFilesSurface.size(), totalConversions, progress);
		
		WorldInfo worldInfo = this.getWorldInfo(levelName);
		worldInfo.setSaveVersion(19132);
		
		ISaveHandler iSaveHandler11 = this.getSaveLoader(levelName, false);
		iSaveHandler11.saveWorldInfo(worldInfo);
		this.deleteOldFolders(chunkFoldersSurface, chunkFilesSurface.size() + chunkFilesNether.size(), totalConversions, progress);
		if(netherDir.exists()) {
			this.deleteOldFolders(chunkFoldersNether, chunkFilesSurface.size() + chunkFilesNether.size() + chunkFoldersSurface.size(), totalConversions, progress);
		}

	}
	
	private void scanFolder(File dir, ArrayList<ChunkFile> listChunks, ArrayList<File> listFolders) {
		ChunkFolderPattern folderPattern = new ChunkFolderPattern();
		ChunkFilePattern filePattern = new ChunkFilePattern();
		
		File[] folders = dir.listFiles(folderPattern);
		File[] foldersC = folders;
		int numFolders = folders.length;

		for(int i = 0; i < numFolders; ++i) {
			File folder = foldersC[i];
			listFolders.add(folder);
			File[] subfolders = folder.listFiles(folderPattern);
			File[] subfoldersC = subfolders;
			int i13 = subfolders.length;

			for(int j = 0; j < i13; ++j) {
				File subfolder = subfoldersC[j];
				File[] files = subfolder.listFiles(filePattern);
				File[] filesC = files;
				int numFiles = files.length;

				for(int k = 0; k < numFiles; ++k) {
					File file = filesC[k];
					listChunks.add(new ChunkFile(file));
				}
			}
		}

	}
	
	private void convertToMcr(File dir, ArrayList<ChunkFile> chunkFiles, int curConversion, int totalConversion, IProgressUpdate progress) {
		Collections.sort(chunkFiles);
		byte[] buffer = new byte[4096];
		Iterator<ChunkFile> it = chunkFiles.iterator();

		while(it.hasNext()) {
			ChunkFile chunkFile = (ChunkFile)it.next();
			int chunkX = chunkFile.getXpos();
			int chunkZ = chunkFile.getZpos();

			RegionFile mcr = RegionFileCacheMCRegion.getRegionFile(dir, chunkX, chunkZ);

			if(!mcr.isChunkSaved(chunkX & 31, chunkZ & 31)) {
				try {
					DataInputStream dis = new DataInputStream(new GZIPInputStream(new FileInputStream(chunkFile.getFile())));
					DataOutputStream dos = mcr.getChunkDataOutputStream(chunkX & 31, chunkZ & 31);
					
					int size;
					while((size = dis.read(buffer)) != -1) {
						dos.write(buffer, 0, size);
					}

					dos.close();
					dis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			++curConversion;
			int percent = (int)Math.round(100.0D * (double)curConversion / (double)totalConversion);
			progress.setLoadingProgress(percent);
		}

		RegionFileCacheMCRegion.closeRegionFiles();
	}

	private void deleteOldFolders(ArrayList<File> arrayList1, int i2, int i3, IProgressUpdate iProgressUpdate4) {
		Iterator<File> iterator5 = arrayList1.iterator();

		while(iterator5.hasNext()) {
			File file6 = (File)iterator5.next();
			File[] file7 = file6.listFiles();
			deleteFiles(file7);
			file6.delete();
			++i2;
			int i8 = (int)Math.round(100.0D * (double)i2 / (double)i3);
			iProgressUpdate4.setLoadingProgress(i8);
		}

	}

	public void renameWorld(String string1, String string2) {
		File file3 = new File(this.savesDirectory, string1);
		if(file3.exists()) {
			File file4 = new File(file3, "level.dat");
			if(file4.exists()) {
				try {
					NBTTagCompound nBTTagCompound5 = CompressedStreamTools.readCompressed(new FileInputStream(file4));
					NBTTagCompound nBTTagCompound6 = nBTTagCompound5.getCompoundTag("Data");
					nBTTagCompound6.setString("LevelName", string2);
					CompressedStreamTools.writeCompressed(nBTTagCompound5, new FileOutputStream(file4));
				} catch (Exception exception7) {
					exception7.printStackTrace();
				}
			}

		}
	}

	public void deleteWorldDirectory(String string1) {
		File file2 = new File(this.savesDirectory, string1);
		if(file2.exists()) {
			deleteFiles(file2.listFiles());
			file2.delete();
		}
	}

	protected static void deleteFiles(File[] file0) {
		for(int i1 = 0; i1 < file0.length; ++i1) {
			if(file0[i1].isDirectory()) {
				System.out.println("Deleting " + file0[i1]);
				deleteFiles(file0[i1].listFiles());
			}

			file0[i1].delete();
		}

	}
	
	public int getSaveVersion(String levelName) {
		WorldInfo worldInfo = this.getWorldInfo(levelName);
		return worldInfo == null ? 0 : worldInfo.getSaveVersion();
	}


}
