package net.minecraft.world.level.chunk.storage;

import java.io.File;
import java.util.regex.Matcher;

class ChunkFile implements Comparable<Object> {
	private final File chunkFile;
	private final int xPos;
	private final int zPos;

	public ChunkFile(File file1) {
		this.chunkFile = file1;
		Matcher matcher2 = ChunkFilePattern.filenameRegexp.matcher(file1.getName());
		if(matcher2.matches()) {
			this.xPos = Integer.parseInt(matcher2.group(1), 36);
			this.zPos = Integer.parseInt(matcher2.group(2), 36);
		} else {
			this.xPos = 0;
			this.zPos = 0;
		}

	}

	public int doCompare(ChunkFile chunkFile1) {
		int i2 = this.xPos >> 5;
		int i3 = chunkFile1.xPos >> 5;
		if(i2 == i3) {
			int i4 = this.zPos >> 5;
			int i5 = chunkFile1.zPos >> 5;
			return i4 - i5;
		} else {
			return i2 - i3;
		}
	}

	public File getFile() {
		return this.chunkFile;
	}

	public int getXpos() {
		return this.xPos;
	}

	public int getZpos() {
		return this.zPos;
	}

	public int compareTo(Object object1) {
		return this.doCompare((ChunkFile)object1);
	}
}
