package net.minecraft.src;

import java.io.File;
import java.util.List;

public class SaveOldDir extends PlayerNBTManager {
	public SaveOldDir(File file1, String string2, boolean z3) {
		super(file1, string2, z3);
	}

	public IChunkLoader func_22092_a(WorldProvider worldProvider1) {
		File file2 = this.getWorldDir();
		if(worldProvider1 instanceof WorldProviderHell) {
			File file3 = new File(file2, "DIM-1");
			file3.mkdirs();
			return new McRegionChunkLoader(file3);
		} else {
			return new McRegionChunkLoader(file2);
		}
	}

	public void func_22095_a(WorldInfo worldInfo1, List list2) {
		worldInfo1.setVersion(19132);
		super.func_22095_a(worldInfo1, list2);
	}

	public void func_22093_e() {
		RegionFileCache.func_22122_a();
	}
}
