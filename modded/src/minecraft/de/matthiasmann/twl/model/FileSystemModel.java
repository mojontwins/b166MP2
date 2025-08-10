package de.matthiasmann.twl.model;

import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.ReadableByteChannel;

public interface FileSystemModel {
	String SPECIAL_FOLDER_HOME = "user.home";

	String getSeparator();

	Object getFile(String string1);

	Object getParent(Object object1);

	boolean isFolder(Object object1);

	boolean isFile(Object object1);

	boolean isHidden(Object object1);

	String getName(Object object1);

	String getPath(Object object1);

	String getRelativePath(Object object1, Object object2);

	long getSize(Object object1);

	long getLastModified(Object object1);

	boolean equals(Object object1, Object object2);

	int find(Object[] object1, Object object2);

	Object[] listRoots();

	Object[] listFolder(Object object1, FileSystemModel.FileFilter fileSystemModel$FileFilter2);

	Object getSpecialFolder(String string1);

	InputStream openStream(Object object1) throws IOException;

	ReadableByteChannel openChannel(Object object1) throws IOException;

	public interface FileFilter {
		boolean accept(FileSystemModel fileSystemModel1, Object object2);
	}
}
