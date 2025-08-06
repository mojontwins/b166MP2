package net.minecraft.world;

class IntHashMapEntry {
	final int hashEntry;
	Object valueEntry;
	IntHashMapEntry nextEntry;
	final int slotHash;

	IntHashMapEntry(int i1, int i2, Object object3, IntHashMapEntry intHashMapEntry4) {
		this.valueEntry = object3;
		this.nextEntry = intHashMapEntry4;
		this.hashEntry = i2;
		this.slotHash = i1;
	}

	public final int getHash() {
		return this.hashEntry;
	}

	public final Object getValue() {
		return this.valueEntry;
	}

	public final boolean equals(Object object1) {
		if(!(object1 instanceof IntHashMapEntry)) {
			return false;
		} else {
			IntHashMapEntry intHashMapEntry2 = (IntHashMapEntry)object1;
			Integer integer3 = this.getHash();
			Integer integer4 = intHashMapEntry2.getHash();
			if(integer3 == integer4 || integer3 != null && integer3.equals(integer4)) {
				Object object5 = this.getValue();
				Object object6 = intHashMapEntry2.getValue();
				if(object5 == object6 || object5 != null && object5.equals(object6)) {
					return true;
				}
			}

			return false;
		}
	}

	public final int hashCode() {
		return IntHashMap.getHash(this.hashEntry);
	}

	public final String toString() {
		return this.getHash() + "=" + this.getValue();
	}
}
