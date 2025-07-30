package net.minecraft.src;

import java.io.File;
import java.util.regex.Matcher;

class ChunkFile implements Comparable {
	private final File field_22209_a;
	private final int field_22208_b;
	private final int field_22210_c;

	public ChunkFile(File file1) {
		this.field_22209_a = file1;
		Matcher matcher2 = ChunkFilePattern.field_22119_a.matcher(file1.getName());
		if(matcher2.matches()) {
			this.field_22208_b = Integer.parseInt(matcher2.group(1), 36);
			this.field_22210_c = Integer.parseInt(matcher2.group(2), 36);
		} else {
			this.field_22208_b = 0;
			this.field_22210_c = 0;
		}

	}

	public int a(ChunkFile chunkFile1) {
		int i2 = this.field_22208_b >> 5;
		int i3 = chunkFile1.field_22208_b >> 5;
		if(i2 == i3) {
			int i4 = this.field_22210_c >> 5;
			int i5 = chunkFile1.field_22210_c >> 5;
			return i4 - i5;
		} else {
			return i2 - i3;
		}
	}

	public File func_22207_a() {
		return this.field_22209_a;
	}

	public int func_22205_b() {
		return this.field_22208_b;
	}

	public int func_22204_c() {
		return this.field_22210_c;
	}

	public int compareTo(Object object1) {
		return this.a((ChunkFile)object1);
	}
}
