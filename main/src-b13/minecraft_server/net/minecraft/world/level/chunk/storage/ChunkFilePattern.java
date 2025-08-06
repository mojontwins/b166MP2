package net.minecraft.world.level.chunk.storage;

import java.io.File;
import java.io.FilenameFilter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class ChunkFilePattern implements FilenameFilter {
	public static final Pattern filenameRegexp = Pattern.compile("c\\.(-?[0-9a-z]+)\\.(-?[0-9a-z]+)\\.dat");

	ChunkFilePattern() {
	}

	public boolean accept(File file1, String string2) {
		Matcher matcher3 = filenameRegexp.matcher(string2);
		return matcher3.matches();
	}

}
