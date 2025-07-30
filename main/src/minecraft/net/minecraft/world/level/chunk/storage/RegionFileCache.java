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

public class RegionFileCache {
	private static final Map<File, SoftReference<RegionFile>> regionsByFilename = new HashMap<File, SoftReference<RegionFile>>();

	public static synchronized RegionFile createOrLoadRegionFile(File file0, int i1, int i2) {
		File file3 = new File(file0, "region");
		File file4 = new File(file3, "r." + (i1 >> 5) + "." + (i2 >> 5) + ".mca");
		Reference<?> reference5 = (Reference<?>)regionsByFilename.get(file4);
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

		if(regionsByFilename.size() >= 256) {
			clearRegionFileReferences();
		}

		regionFile6 = new RegionFile(file4);
		regionsByFilename.put(file4, new SoftReference<RegionFile>(regionFile6));
		return regionFile6;
	}

	public static synchronized void clearRegionFileReferences() {
		Iterator<SoftReference<RegionFile>> iterator0 = regionsByFilename.values().iterator();

		while(iterator0.hasNext()) {
			Reference<?> reference1 = (Reference<?>)iterator0.next();

			try {
				RegionFile regionFile2 = (RegionFile)reference1.get();
				if(regionFile2 != null) {
					regionFile2.close();
				}
			} catch (IOException iOException3) {
				iOException3.printStackTrace();
			}
		}

		regionsByFilename.clear();
	}

	public static DataInputStream getChunkInputStream(File file0, int i1, int i2) {
		RegionFile regionFile3 = createOrLoadRegionFile(file0, i1, i2);
		return regionFile3.getChunkDataInputStream(i1 & 31, i2 & 31);
	}

	public static DataOutputStream getChunkOutputStream(File file0, int i1, int i2) {
		RegionFile regionFile3 = createOrLoadRegionFile(file0, i1, i2);
		return regionFile3.getChunkDataOutputStream(i1 & 31, i2 & 31);
	}
}
