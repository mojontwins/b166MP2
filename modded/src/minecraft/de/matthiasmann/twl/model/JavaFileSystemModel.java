package de.matthiasmann.twl.model;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.ReadableByteChannel;

public class JavaFileSystemModel implements FileSystemModel {
	private static final JavaFileSystemModel instance = new JavaFileSystemModel();

	public static JavaFileSystemModel getInstance() {
		return instance;
	}

	public String getSeparator() {
		return File.separator;
	}

	public Object getFile(String path) {
		File file = new File(path);
		return file.exists() ? file : null;
	}

	public Object getParent(Object file) {
		return ((File)file).getParentFile();
	}

	public boolean isFolder(Object file) {
		return ((File)file).isDirectory();
	}

	public boolean isFile(Object file) {
		return ((File)file).isFile();
	}

	public boolean isHidden(Object file) {
		return ((File)file).isHidden();
	}

	public String getName(Object file) {
		String name = ((File)file).getName();
		return name.length() == 0 ? file.toString() : name;
	}

	public String getPath(Object file) {
		return ((File)file).getPath();
	}

	public String getRelativePath(Object from, Object to) {
		return getRelativePath(this, from, to);
	}

	public static String getRelativePath(FileSystemModel fsm, Object from, Object to) {
		int levelFrom = countLevel(fsm, from);
		int levelTo = countLevel(fsm, to);
		int prefixes = 0;
		StringBuilder sb = new StringBuilder();

		while(!fsm.equals(from, to)) {
			int diff = levelTo - levelFrom;
			if(diff <= 0) {
				++prefixes;
				--levelFrom;
				from = fsm.getParent(from);
			}

			if(diff >= 0) {
				sb.insert(0, '/');
				sb.insert(0, fsm.getName(to));
				--levelTo;
				to = fsm.getParent(to);
			}
		}

		while(prefixes-- > 0) {
			sb.insert(0, "../");
		}

		return sb.toString();
	}

	public static int countLevel(FileSystemModel fsm, Object file) {
		int level;
		for(level = 0; file != null; ++level) {
			file = fsm.getParent(file);
		}

		return level;
	}

	public static int countLevel(FileSystemModel fsm, Object parent, Object child) {
		int level;
		for(level = 0; fsm.equals(child, parent); ++level) {
			if(child == null) {
				return -1;
			}

			child = fsm.getParent(child);
		}

		return level;
	}

	public long getLastModified(Object file) {
		try {
			return ((File)file).lastModified();
		} catch (Throwable throwable3) {
			return -1L;
		}
	}

	public long getSize(Object file) {
		try {
			return ((File)file).length();
		} catch (Throwable throwable3) {
			return -1L;
		}
	}

	public boolean equals(Object file1, Object file2) {
		return file1 != null && file1.equals(file2);
	}

	public int find(Object[] list, Object file) {
		if(file == null) {
			return -1;
		} else {
			for(int i = 0; i < list.length; ++i) {
				if(file.equals(list[i])) {
					return i;
				}
			}

			return -1;
		}
	}

	public Object[] listRoots() {
		return File.listRoots();
	}

	public Object[] listFolder(Object file, final FileSystemModel.FileFilter filter) {
		try {
			return filter == null ? ((File)file).listFiles() : ((File)file).listFiles(new FileFilter() {
				public boolean accept(File pathname) {
					return filter.accept(JavaFileSystemModel.this, pathname);
				}
			});
		} catch (Throwable throwable4) {
			return null;
		}
	}

	public Object getSpecialFolder(String key) {
		File file = null;
		if("user.home".equals(key)) {
			file = new File(System.getProperty("user.home"));
		}

		return file != null && file.canRead() && file.isDirectory() ? file : null;
	}

	public ReadableByteChannel openChannel(Object file) throws IOException {
		return (new FileInputStream((File)file)).getChannel();
	}

	public InputStream openStream(Object file) throws IOException {
		return new FileInputStream((File)file);
	}
}
