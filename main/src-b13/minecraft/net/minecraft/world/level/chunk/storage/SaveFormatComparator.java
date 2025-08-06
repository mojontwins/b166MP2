package net.minecraft.world.level.chunk.storage;

public class SaveFormatComparator implements Comparable<Object> {
	private final String fileName;
	private final String displayName;
	private final long lastTimePlayed;
	private final long sizeOnDisk;
	private final boolean requiresConversion;
	private final int gameType;
	private final boolean hardcore;

	public SaveFormatComparator(String string1, String string2, long j3, long j5, int i7, boolean z8, boolean z9) {
		this.fileName = string1;
		this.displayName = string2;
		this.lastTimePlayed = j3;
		this.sizeOnDisk = j5;
		this.gameType = i7;
		this.requiresConversion = z8;
		this.hardcore = z9;
	}

	public String getFileName() {
		return this.fileName;
	}

	public String getDisplayName() {
		return this.displayName;
	}

	public boolean requiresConversion() {
		return this.requiresConversion;
	}

	public long getLastTimePlayed() {
		return this.lastTimePlayed;
	}

	public int compareTo(SaveFormatComparator saveFormatComparator1) {
		return this.lastTimePlayed < saveFormatComparator1.lastTimePlayed ? 1 : (this.lastTimePlayed > saveFormatComparator1.lastTimePlayed ? -1 : this.fileName.compareTo(saveFormatComparator1.fileName));
	}

	public int getGameType() {
		return this.gameType;
	}

	public boolean isHardcoreModeEnabled() {
		return this.hardcore;
	}

	public int compareTo(Object object1) {
		return this.compareTo((SaveFormatComparator)object1);
	}

	public long getSizeOnDisk() {
		return sizeOnDisk;
	}
}
