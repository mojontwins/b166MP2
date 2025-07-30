package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.zip.GZIPInputStream;

public class SaveConverterMcRegion extends SaveFormatOld {
	public SaveConverterMcRegion(File file1) {
		super(file1);
	}

	public ISaveHandler func_22105_a(String string1, boolean z2) {
		return new SaveOldDir(this.field_22106_a, string1, z2);
	}

	public boolean isOldSaveType(String string1) {
		WorldInfo worldInfo2 = this.getWorldInfo(string1);
		return worldInfo2 != null && worldInfo2.getVersion() == 0;
	}

	public boolean converMapToMCRegion(String string1, IProgressUpdate iProgressUpdate2) {
		iProgressUpdate2.setLoadingProgress(0);
		ArrayList arrayList3 = new ArrayList();
		ArrayList arrayList4 = new ArrayList();
		ArrayList arrayList5 = new ArrayList();
		ArrayList arrayList6 = new ArrayList();
		File file7 = new File(this.field_22106_a, string1);
		File file8 = new File(file7, "DIM-1");
		System.out.println("Scanning folders...");
		this.func_22108_a(file7, arrayList3, arrayList4);
		if(file8.exists()) {
			this.func_22108_a(file8, arrayList5, arrayList6);
		}

		int i9 = arrayList3.size() + arrayList5.size() + arrayList4.size() + arrayList6.size();
		System.out.println("Total conversion count is " + i9);
		this.func_22107_a(file7, arrayList3, 0, i9, iProgressUpdate2);
		this.func_22107_a(file8, arrayList5, arrayList3.size(), i9, iProgressUpdate2);
		WorldInfo worldInfo10 = this.getWorldInfo(string1);
		worldInfo10.setVersion(19132);
		ISaveHandler iSaveHandler11 = this.func_22105_a(string1, false);
		iSaveHandler11.func_22094_a(worldInfo10);
		this.func_22109_a(arrayList4, arrayList3.size() + arrayList5.size(), i9, iProgressUpdate2);
		if(file8.exists()) {
			this.func_22109_a(arrayList6, arrayList3.size() + arrayList5.size() + arrayList4.size(), i9, iProgressUpdate2);
		}

		return true;
	}

	private void func_22108_a(File file1, ArrayList arrayList2, ArrayList arrayList3) {
		ChunkFolderPattern chunkFolderPattern4 = new ChunkFolderPattern((Empty2)null);
		ChunkFilePattern chunkFilePattern5 = new ChunkFilePattern((Empty2)null);
		File[] file6 = file1.listFiles(chunkFolderPattern4);
		File[] file7 = file6;
		int i8 = file6.length;

		for(int i9 = 0; i9 < i8; ++i9) {
			File file10 = file7[i9];
			arrayList3.add(file10);
			File[] file11 = file10.listFiles(chunkFolderPattern4);
			File[] file12 = file11;
			int i13 = file11.length;

			for(int i14 = 0; i14 < i13; ++i14) {
				File file15 = file12[i14];
				File[] file16 = file15.listFiles(chunkFilePattern5);
				File[] file17 = file16;
				int i18 = file16.length;

				for(int i19 = 0; i19 < i18; ++i19) {
					File file20 = file17[i19];
					arrayList2.add(new ChunkFile(file20));
				}
			}
		}

	}

	private void func_22107_a(File file1, ArrayList arrayList2, int i3, int i4, IProgressUpdate iProgressUpdate5) {
		Collections.sort(arrayList2);
		byte[] b6 = new byte[4096];
		Iterator iterator7 = arrayList2.iterator();

		while(iterator7.hasNext()) {
			ChunkFile chunkFile8 = (ChunkFile)iterator7.next();
			int i9 = chunkFile8.func_22205_b();
			int i10 = chunkFile8.func_22204_c();
			RegionFile regionFile11 = RegionFileCache.func_22123_a(file1, i9, i10);
			if(!regionFile11.isChunkSaved(i9 & 31, i10 & 31)) {
				try {
					DataInputStream dataInputStream12 = new DataInputStream(new GZIPInputStream(new FileInputStream(chunkFile8.func_22207_a())));
					DataOutputStream dataOutputStream13 = regionFile11.getChunkDataOutputStream(i9 & 31, i10 & 31);
					boolean z14 = false;

					int i17;
					while((i17 = dataInputStream12.read(b6)) != -1) {
						dataOutputStream13.write(b6, 0, i17);
					}

					dataOutputStream13.close();
					dataInputStream12.close();
				} catch (IOException iOException15) {
					iOException15.printStackTrace();
				}
			}

			++i3;
			int i16 = (int)Math.round(100.0D * (double)i3 / (double)i4);
			iProgressUpdate5.setLoadingProgress(i16);
		}

		RegionFileCache.func_22122_a();
	}

	private void func_22109_a(ArrayList arrayList1, int i2, int i3, IProgressUpdate iProgressUpdate4) {
		Iterator iterator5 = arrayList1.iterator();

		while(iterator5.hasNext()) {
			File file6 = (File)iterator5.next();
			File[] file7 = file6.listFiles();
			func_22104_a(file7);
			file6.delete();
			++i2;
			int i8 = (int)Math.round(100.0D * (double)i2 / (double)i3);
			iProgressUpdate4.setLoadingProgress(i8);
		}

	}
}
