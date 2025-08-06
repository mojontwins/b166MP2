package net.minecraft.world.level.chunk.storage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class RegionFileCacheMCRegion {
	private static final Map<File,Reference<RegionFile>> cache = new HashMap<File,Reference<RegionFile>>();

	public static synchronized RegionFile getRegionFile(File file0, int i1, int i2) {
		File file3 = new File(file0, "region");
		File file4 = new File(file3, "r." + (i1 >> 5) + "." + (i2 >> 5) + ".mcr");
		Reference<RegionFile> reference5 = (Reference<RegionFile>)cache.get(file4);
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

		if(cache.size() >= 256) {
			closeRegionFiles();
		}

		regionFile6 = new RegionFile(file4);
		cache.put(file4, new SoftReference<RegionFile>(regionFile6));
		return regionFile6;
	}

	public static synchronized void closeRegionFiles() {
		Iterator<Reference<RegionFile>> iterator0 = cache.values().iterator();

		while(iterator0.hasNext()) {
			Reference<RegionFile> reference1 = (Reference<RegionFile>)iterator0.next();

			try {
				RegionFile regionFile2 = (RegionFile)reference1.get();
				if(regionFile2 != null) {
					regionFile2.close();
				}
			} catch (IOException iOException3) {
				iOException3.printStackTrace();
			}
		}

		cache.clear();
	}

	public static DataInputStream getChunkInputStream(File file0, int i1, int i2) {
		RegionFile regionFile3 = getRegionFile(file0, i1, i2);
		return regionFile3.getChunkDataInputStream(i1 & 31, i2 & 31);
	}

	public static DataOutputStream getChunkOutputStream(File file0, int i1, int i2) {
		RegionFile regionFile3 = getRegionFile(file0, i1, i2);
		return regionFile3.getChunkDataOutputStream(i1 & 31, i2 & 31);
	}
}
