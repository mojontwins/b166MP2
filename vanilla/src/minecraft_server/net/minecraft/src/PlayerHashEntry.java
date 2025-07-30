package net.minecraft.src;

class PlayerHashEntry {
	final long key;
	Object value;
	PlayerHashEntry nextEntry;
	final int field_1026_d;

	PlayerHashEntry(int i1, long j2, Object object4, PlayerHashEntry playerHashEntry5) {
		this.value = object4;
		this.nextEntry = playerHashEntry5;
		this.key = j2;
		this.field_1026_d = i1;
	}

	public final long func_736_a() {
		return this.key;
	}

	public final Object func_735_b() {
		return this.value;
	}

	public final boolean equals(Object object1) {
		if(!(object1 instanceof PlayerHashEntry)) {
			return false;
		} else {
			PlayerHashEntry playerHashEntry2 = (PlayerHashEntry)object1;
			Long long3 = this.func_736_a();
			Long long4 = playerHashEntry2.func_736_a();
			if(long3 == long4 || long3 != null && long3.equals(long4)) {
				Object object5 = this.func_735_b();
				Object object6 = playerHashEntry2.func_735_b();
				if(object5 == object6 || object5 != null && object5.equals(object6)) {
					return true;
				}
			}

			return false;
		}
	}

	public final int hashCode() {
		return PlayerHash.getHashCode(this.key);
	}

	public final String toString() {
		return this.func_736_a() + "=" + this.func_735_b();
	}
}
