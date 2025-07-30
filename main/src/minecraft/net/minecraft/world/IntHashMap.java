package net.minecraft.world;

import java.util.HashSet;
import java.util.Set;

public class IntHashMap {
	private transient IntHashMapEntry[] slots = new IntHashMapEntry[16];
	private transient int count;
	private int threshold = 12;
	private final float growFactor = 0.75F;
	private transient volatile int versionStamp;
	private Set<Integer> keySet = new HashSet<Integer>();

	private static int computeHash(int i0) {
		i0 ^= i0 >>> 20 ^ i0 >>> 12;
		return i0 ^ i0 >>> 7 ^ i0 >>> 4;
	}

	private static int getSlotIndex(int i0, int i1) {
		return i0 & i1 - 1;
	}

	public Object lookup(int i1) {
		int i2 = computeHash(i1);

		for(IntHashMapEntry intHashMapEntry3 = this.slots[getSlotIndex(i2, this.slots.length)]; intHashMapEntry3 != null; intHashMapEntry3 = intHashMapEntry3.nextEntry) {
			if(intHashMapEntry3.hashEntry == i1) {
				return intHashMapEntry3.valueEntry;
			}
		}

		return null;
	}

	public boolean containsItem(int i1) {
		return this.lookupEntry(i1) != null;
	}

	final IntHashMapEntry lookupEntry(int i1) {
		int i2 = computeHash(i1);

		for(IntHashMapEntry intHashMapEntry3 = this.slots[getSlotIndex(i2, this.slots.length)]; intHashMapEntry3 != null; intHashMapEntry3 = intHashMapEntry3.nextEntry) {
			if(intHashMapEntry3.hashEntry == i1) {
				return intHashMapEntry3;
			}
		}

		return null;
	}

	public void addKey(int i1, Object object2) {
		this.keySet.add(i1);
		int i3 = computeHash(i1);
		int i4 = getSlotIndex(i3, this.slots.length);

		for(IntHashMapEntry intHashMapEntry5 = this.slots[i4]; intHashMapEntry5 != null; intHashMapEntry5 = intHashMapEntry5.nextEntry) {
			if(intHashMapEntry5.hashEntry == i1) {
				intHashMapEntry5.valueEntry = object2;
			}
		}

		++this.versionStamp;
		this.insert(i3, i1, object2, i4);
	}

	private void grow(int i1) {
		IntHashMapEntry[] intHashMapEntry2 = this.slots;
		int i3 = intHashMapEntry2.length;
		if(i3 == 1073741824) {
			this.threshold = Integer.MAX_VALUE;
		} else {
			IntHashMapEntry[] intHashMapEntry4 = new IntHashMapEntry[i1];
			this.copyTo(intHashMapEntry4);
			this.slots = intHashMapEntry4;
			this.threshold = (int)((float)i1 * this.growFactor);
		}
	}

	private void copyTo(IntHashMapEntry[] intHashMapEntry1) {
		IntHashMapEntry[] intHashMapEntry2 = this.slots;
		int i3 = intHashMapEntry1.length;

		for(int i4 = 0; i4 < intHashMapEntry2.length; ++i4) {
			IntHashMapEntry intHashMapEntry5 = intHashMapEntry2[i4];
			if(intHashMapEntry5 != null) {
				intHashMapEntry2[i4] = null;

				IntHashMapEntry intHashMapEntry6;
				do {
					intHashMapEntry6 = intHashMapEntry5.nextEntry;
					int i7 = getSlotIndex(intHashMapEntry5.slotHash, i3);
					intHashMapEntry5.nextEntry = intHashMapEntry1[i7];
					intHashMapEntry1[i7] = intHashMapEntry5;
					intHashMapEntry5 = intHashMapEntry6;
				} while(intHashMapEntry6 != null);
			}
		}

	}

	public Object removeObject(int i1) {
		this.keySet.remove(i1);
		IntHashMapEntry intHashMapEntry2 = this.removeEntry(i1);
		return intHashMapEntry2 == null ? null : intHashMapEntry2.valueEntry;
	}

	final IntHashMapEntry removeEntry(int i1) {
		int i2 = computeHash(i1);
		int i3 = getSlotIndex(i2, this.slots.length);
		IntHashMapEntry intHashMapEntry4 = this.slots[i3];

		IntHashMapEntry intHashMapEntry5;
		IntHashMapEntry intHashMapEntry6;
		for(intHashMapEntry5 = intHashMapEntry4; intHashMapEntry5 != null; intHashMapEntry5 = intHashMapEntry6) {
			intHashMapEntry6 = intHashMapEntry5.nextEntry;
			if(intHashMapEntry5.hashEntry == i1) {
				++this.versionStamp;
				--this.count;
				if(intHashMapEntry4 == intHashMapEntry5) {
					this.slots[i3] = intHashMapEntry6;
				} else {
					intHashMapEntry4.nextEntry = intHashMapEntry6;
				}

				return intHashMapEntry5;
			}

			intHashMapEntry4 = intHashMapEntry5;
		}

		return intHashMapEntry5;
	}

	public void clearMap() {
		++this.versionStamp;
		IntHashMapEntry[] intHashMapEntry1 = this.slots;

		for(int i2 = 0; i2 < intHashMapEntry1.length; ++i2) {
			intHashMapEntry1[i2] = null;
		}

		this.count = 0;
	}

	private void insert(int i1, int i2, Object object3, int i4) {
		IntHashMapEntry intHashMapEntry5 = this.slots[i4];
		this.slots[i4] = new IntHashMapEntry(i1, i2, object3, intHashMapEntry5);
		if(this.count++ >= this.threshold) {
			this.grow(2 * this.slots.length);
		}

	}

	public Set<Integer> getKeySet() {
		return this.keySet;
	}

	static int getHash(int i0) {
		return computeHash(i0);
	}

	public int getVersionStamp() {
		return versionStamp;
	}

	public void setVersionStamp(int versionStamp) {
		this.versionStamp = versionStamp;
	}
}
