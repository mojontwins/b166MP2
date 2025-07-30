package net.minecraft.world.level.chunk.storage;

import java.io.File;
import java.io.FilenameFilter;

class AnvilSaveConverterFileFilter implements FilenameFilter {
	final AnvilSaveConverter parent;

	AnvilSaveConverterFileFilter(AnvilSaveConverter anvilSaveConverter1) {
		this.parent = anvilSaveConverter1;
	}

	public boolean accept(File file1, String string2) {
		return string2.endsWith(".mcr");
	}
}
