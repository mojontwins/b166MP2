package net.minecraft.util;

import java.io.File;

public class UtilDir {

	public static long dirSize(File f) {
		long folderLength = 0;
		File[] files = f.listFiles();

		for (File file : files) {
			if (file.isFile()) {
				folderLength += file.length();
			} else if (f.isDirectory()) {
				folderLength += dirSize(file);
			}
		}

		return folderLength;
	}
}
