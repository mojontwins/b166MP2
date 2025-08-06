package net.minecraft.world.level.chunk.storage;

public interface IProgressUpdate {
	void displaySavingString(String string1);

	void displayLoadingString(String string1);

	void setLoadingProgress(int i1);
}
