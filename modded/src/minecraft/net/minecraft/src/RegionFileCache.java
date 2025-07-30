package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class RegionFileCache {
	private static final Map field_22195_a = new HashMap();

	public static synchronized RegionFile func_22193_a(File file0, int i1, int i2) {
		File file3 = new File(file0, "region");
		File file4 = new File(file3, "r." + (i1 >> 5) + "." + (i2 >> 5) + ".mcr");
		Reference reference5 = (Reference)field_22195_a.get(file4);
		RegionFile regionFile6;
		if(reference5 != null) {
			regionFile6 = (RegionFile)reference5.get();
			if(regionFile6 != null) {
				return regionFile6;
			}
		}

		if(!file3.exists()) {
			file3.mkdirs();
		}

		if(field_22195_a.size() >= 256) {
			func_22192_a();
		}

		regionFile6 = new RegionFile(file4);
		field_22195_a.put(file4, new SoftReference(regionFile6));
		return regionFile6;
	}

	public static synchronized void func_22192_a() {
		Iterator iterator0 = field_22195_a.values().iterator();

		while(iterator0.hasNext()) {
			Reference reference1 = (Reference)iterator0.next();

			try {
				RegionFile regionFile2 = (RegionFile)reference1.get();
				if(regionFile2 != null) {
					regionFile2.func_22196_b();
				}
			} catch (IOException iOException3) {
				iOException3.printStackTrace();
			}
		}

		field_22195_a.clear();
	}

	public static int func_22191_b(File file0, int i1, int i2) {
		RegionFile regionFile3 = func_22193_a(file0, i1, i2);
		return regionFile3.func_22209_a();
	}

	public static DataInputStream getChunkInputStream(File file0, int i1, int i2) {
		RegionFile regionFile3 = func_22193_a(file0, i1, i2);
		return regionFile3.func_22210_a(i1 & 31, i2 & 31);
	}

	public static DataOutputStream getChunkOutputStream(File file0, int i1, int i2) {
		RegionFile regionFile3 = func_22193_a(file0, i1, i2);
		return regionFile3.func_22205_b(i1 & 31, i2 & 31);
	}
}
