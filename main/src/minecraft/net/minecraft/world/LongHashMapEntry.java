package net.minecraft.world;

class LongHashMapEntry {
	final long key;
	Object value;
	LongHashMapEntry nextEntry;
	final int hash;

	LongHashMapEntry(int i1, long j2, Object object4, LongHashMapEntry longHashMapEntry5) {
		this.value = object4;
		this.nextEntry = longHashMapEntry5;
		this.key = j2;
		this.hash = i1;
	}

	public final long getKey() {
		return this.key;
	}

	public final Object getValue() {
		return this.value;
	}

	public final boolean equals(Object object1) {
		if(!(object1 instanceof LongHashMapEntry)) {
			return false;
		} else {
			LongHashMapEntry longHashMapEntry2 = (LongHashMapEntry)object1;
			Long long3 = this.getKey();
			Long long4 = longHashMapEntry2.getKey();
			if(long3 == long4 || long3 != null && long3.equals(long4)) {
				Object object5 = this.getValue();
				Object object6 = longHashMapEntry2.getValue();
				if(object5 == object6 || object5 != null && object5.equals(object6)) {
					return true;
				}
			}

			return false;
		}
	}

	public final int hashCode() {
		return LongHashMap.getHashCode(this.key);
	}

	public final String toString() {
		return this.getKey() + "=" + this.getValue();
	}
}
