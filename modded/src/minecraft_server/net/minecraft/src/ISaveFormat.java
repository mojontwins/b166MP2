package net.minecraft.src;

public interface ISaveFormat {
	boolean isOldSaveType(String string1);

	boolean converMapToMCRegion(String string1, IProgressUpdate iProgressUpdate2);
}
