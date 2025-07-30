package net.minecraft.src;

import java.io.File;
import java.io.FileInputStream;

public class SaveFormatOld implements ISaveFormat {
	protected final File field_22106_a;

	public SaveFormatOld(File file1) {
		if(!file1.exists()) {
			file1.mkdirs();
		}

		this.field_22106_a = file1;
	}

	public WorldInfo getWorldInfo(String string1) {
		File file2 = new File(this.field_22106_a, string1);
		if(!file2.exists()) {
			return null;
		} else {
			File file3 = new File(file2, "level.dat");
			NBTTagCompound nBTTagCompound4;
			NBTTagCompound nBTTagCompound5;
			if(file3.exists()) {
				try {
					nBTTagCompound4 = CompressedStreamTools.func_770_a(new FileInputStream(file3));
					nBTTagCompound5 = nBTTagCompound4.getCompoundTag("Data");
					return new WorldInfo(nBTTagCompound5);
				} catch (Exception exception7) {
					exception7.printStackTrace();
				}
			}

			file3 = new File(file2, "level.dat_old");
			if(file3.exists()) {
				try {
					nBTTagCompound4 = CompressedStreamTools.func_770_a(new FileInputStream(file3));
					nBTTagCompound5 = nBTTagCompound4.getCompoundTag("Data");
					return new WorldInfo(nBTTagCompound5);
				} catch (Exception exception6) {
					exception6.printStackTrace();
				}
			}

			return null;
		}
	}

	protected static void func_22104_a(File[] file0) {
		for(int i1 = 0; i1 < file0.length; ++i1) {
			if(file0[i1].isDirectory()) {
				func_22104_a(file0[i1].listFiles());
			}

			file0[i1].delete();
		}

	}

	public ISaveHandler func_22105_a(String string1, boolean z2) {
		return new PlayerNBTManager(this.field_22106_a, string1, z2);
	}

	public boolean isOldSaveType(String string1) {
		return false;
	}

	public boolean converMapToMCRegion(String string1, IProgressUpdate iProgressUpdate2) {
		return false;
	}
}
