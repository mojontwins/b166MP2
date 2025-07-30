package net.minecraft.world;

public class LongHashMap {
	private transient LongHashMapEntry[] hashArray = new LongHashMapEntry[16];
	private transient int numHashElements;
	private int capacity = 12;
	private final float percentUseable = 0.75F;
	private transient volatile int modCount;

	private static int getHashedKey(long j0) {
		return hash((int)(j0 ^ j0 >>> 32));
	}

	private static int hash(int i0) {
		i0 ^= i0 >>> 20 ^ i0 >>> 12;
		return i0 ^ i0 >>> 7 ^ i0 >>> 4;
	}

	private static int getHashIndex(int i0, int i1) {
		return i0 & i1 - 1;
	}

	public int getNumHashElements() {
		return this.numHashElements;
	}

	public Object getValueByKey(long j1) {
		int i3 = getHashedKey(j1);

		for(LongHashMapEntry longHashMapEntry4 = this.hashArray[getHashIndex(i3, this.hashArray.length)]; longHashMapEntry4 != null; longHashMapEntry4 = longHashMapEntry4.nextEntry) {
			if(longHashMapEntry4.key == j1) {
				return longHashMapEntry4.value;
			}
		}

		return null;
	}

	public boolean containsItem(long j1) {
		return this.getEntry(j1) != null;
	}

	final LongHashMapEntry getEntry(long j1) {
		int i3 = getHashedKey(j1);

		for(LongHashMapEntry longHashMapEntry4 = this.hashArray[getHashIndex(i3, this.hashArray.length)]; longHashMapEntry4 != null; longHashMapEntry4 = longHashMapEntry4.nextEntry) {
			if(longHashMapEntry4.key == j1) {
				return longHashMapEntry4;
			}
		}

		return null;
	}

	public void add(long j1, Object object3) {
		int i4 = getHashedKey(j1);
		int i5 = getHashIndex(i4, this.hashArray.length);

		for(LongHashMapEntry longHashMapEntry6 = this.hashArray[i5]; longHashMapEntry6 != null; longHashMapEntry6 = longHashMapEntry6.nextEntry) {
			if(longHashMapEntry6.key == j1) {
				longHashMapEntry6.value = object3;
			}
		}

		++this.modCount;
		this.createKey(i4, j1, object3, i5);
	}

	private void resizeTable(int i1) {
		LongHashMapEntry[] longHashMapEntry2 = this.hashArray;
		int i3 = longHashMapEntry2.length;
		if(i3 == 1073741824) {
			this.capacity = Integer.MAX_VALUE;
		} else {
			LongHashMapEntry[] longHashMapEntry4 = new LongHashMapEntry[i1];
			this.copyHashTableTo(longHashMapEntry4);
			this.hashArray = longHashMapEntry4;
			this.capacity = (int)((float)i1 * this.percentUseable);
		}
	}

	private void copyHashTableTo(LongHashMapEntry[] longHashMapEntry1) {
		LongHashMapEntry[] longHashMapEntry2 = this.hashArray;
		int i3 = longHashMapEntry1.length;

		for(int i4 = 0; i4 < longHashMapEntry2.length; ++i4) {
			LongHashMapEntry longHashMapEntry5 = longHashMapEntry2[i4];
			if(longHashMapEntry5 != null) {
				longHashMapEntry2[i4] = null;

				LongHashMapEntry longHashMapEntry6;
				do {
					longHashMapEntry6 = longHashMapEntry5.nextEntry;
					int i7 = getHashIndex(longHashMapEntry5.hash, i3);
					longHashMapEntry5.nextEntry = longHashMapEntry1[i7];
					longHashMapEntry1[i7] = longHashMapEntry5;
					longHashMapEntry5 = longHashMapEntry6;
				} while(longHashMapEntry6 != null);
			}
		}

	}

	public Object remove(long j1) {
		LongHashMapEntry longHashMapEntry3 = this.removeKey(j1);
		return longHashMapEntry3 == null ? null : longHashMapEntry3.value;
	}

	final LongHashMapEntry removeKey(long j1) {
		int i3 = getHashedKey(j1);
		int i4 = getHashIndex(i3, this.hashArray.length);
		LongHashMapEntry longHashMapEntry5 = this.hashArray[i4];

		LongHashMapEntry longHashMapEntry6;
		LongHashMapEntry longHashMapEntry7;
		for(longHashMapEntry6 = longHashMapEntry5; longHashMapEntry6 != null; longHashMapEntry6 = longHashMapEntry7) {
			longHashMapEntry7 = longHashMapEntry6.nextEntry;
			if(longHashMapEntry6.key == j1) {
				++this.modCount;
				--this.numHashElements;
				if(longHashMapEntry5 == longHashMapEntry6) {
					this.hashArray[i4] = longHashMapEntry7;
				} else {
					longHashMapEntry5.nextEntry = longHashMapEntry7;
				}

				return longHashMapEntry6;
			}

			longHashMapEntry5 = longHashMapEntry6;
		}

		return longHashMapEntry6;
	}

	private void createKey(int i1, long j2, Object object4, int i5) {
		LongHashMapEntry longHashMapEntry6 = this.hashArray[i5];
		this.hashArray[i5] = new LongHashMapEntry(i1, j2, object4, longHashMapEntry6);
		if(this.numHashElements++ >= this.capacity) {
			this.resizeTable(2 * this.hashArray.length);
		}

	}

	static int getHashCode(long j0) {
		return getHashedKey(j0);
	}

	public int getModCount() {
		return modCount;
	}

	public void setModCount(int modCount) {
		this.modCount = modCount;
	}
}
