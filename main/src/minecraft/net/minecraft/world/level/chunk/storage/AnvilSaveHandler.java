package net.minecraft.world.level.chunk.storage;

import java.io.File;
import java.util.List;

import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.level.WorldInfo;
import net.minecraft.world.level.chunk.IChunkLoader;
import net.minecraft.world.level.dimension.WorldProvider;

public class AnvilSaveHandler extends SaveHandler {
	public AnvilSaveHandler(File file1, String string2, boolean z3) {
		super(file1, string2, z3);
	}

	public IChunkLoader getChunkLoader(WorldProvider worldProvider1) {
		File file2 = this.getSaveDirectory();
		File file3;
		/*
		if(worldProvider1 instanceof WorldProviderHell) {
			file3 = new File(file2, "DIM-1");
			file3.mkdirs();
			return new AnvilChunkLoader(file3);
		} else if(worldProvider1 instanceof WorldProviderEnd) {
			file3 = new File(file2, "DIM1");
			file3.mkdirs();
			return new AnvilChunkLoader(file3);
		} else {
			return new AnvilChunkLoader(file2);
		}
		*/
		
		if(worldProvider1.getSaveFolder() != null) {
			file3 = new File(file2, worldProvider1.getSaveFolder());
			file3.mkdirs();
			return new AnvilChunkLoader(file3);
		} else {
			return new AnvilChunkLoader(file2);
		}
	}

	public void saveWorldInfoAndPlayer(WorldInfo worldInfo1, List<EntityPlayer> list2) {
		worldInfo1.setSaveVersion(SaveHandler.anvil);
		super.saveWorldInfoAndPlayer(worldInfo1, list2);
	}

	public void s_func_22093_e() {
		try {
			ThreadedFileIOBase.threadedIOInstance.waitForFinish();
		} catch (InterruptedException interruptedException2) {
			interruptedException2.printStackTrace();
		}

		RegionFileCache.clearRegionFileReferences();
	}
}
