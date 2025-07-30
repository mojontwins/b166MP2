package net.minecraft.src;

public class PlayerHash {
	private transient PlayerHashEntry[] hashArray = new PlayerHashEntry[16];
	private transient int numHashElements;
	private int capacity = 12;
	private final float percentUsable = 0.75F;
	private transient volatile int field_950_e;

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

	public Object getValueByKey(long j1) {
		int i3 = getHashedKey(j1);

		for(PlayerHashEntry playerHashEntry4 = this.hashArray[getHashIndex(i3, this.hashArray.length)]; playerHashEntry4 != null; playerHashEntry4 = playerHashEntry4.nextEntry) {
			if(playerHashEntry4.key == j1) {
				return playerHashEntry4.value;
			}
		}

		return null;
	}

	public void add(long j1, Object object3) {
		int i4 = getHashedKey(j1);
		int i5 = getHashIndex(i4, this.hashArray.length);

		for(PlayerHashEntry playerHashEntry6 = this.hashArray[i5]; playerHashEntry6 != null; playerHashEntry6 = playerHashEntry6.nextEntry) {
			if(playerHashEntry6.key == j1) {
				playerHashEntry6.value = object3;
			}
		}

		++this.field_950_e;
		this.createKey(i4, j1, object3, i5);
	}

	private void resizeTable(int i1) {
		PlayerHashEntry[] playerHashEntry2 = this.hashArray;
		int i3 = playerHashEntry2.length;
		if(i3 == 1073741824) {
			this.capacity = Integer.MAX_VALUE;
		} else {
			PlayerHashEntry[] playerHashEntry4 = new PlayerHashEntry[i1];
			this.copyHashTableTo(playerHashEntry4);
			this.hashArray = playerHashEntry4;
			this.capacity = (int)((float)i1 * this.percentUsable);
		}
	}

	private void copyHashTableTo(PlayerHashEntry[] playerHashEntry1) {
		PlayerHashEntry[] playerHashEntry2 = this.hashArray;
		int i3 = playerHashEntry1.length;

		for(int i4 = 0; i4 < playerHashEntry2.length; ++i4) {
			PlayerHashEntry playerHashEntry5 = playerHashEntry2[i4];
			if(playerHashEntry5 != null) {
				playerHashEntry2[i4] = null;

				PlayerHashEntry playerHashEntry6;
				do {
					playerHashEntry6 = playerHashEntry5.nextEntry;
					int i7 = getHashIndex(playerHashEntry5.field_1026_d, i3);
					playerHashEntry5.nextEntry = playerHashEntry1[i7];
					playerHashEntry1[i7] = playerHashEntry5;
					playerHashEntry5 = playerHashEntry6;
				} while(playerHashEntry6 != null);
			}
		}

	}

	public Object remove(long j1) {
		PlayerHashEntry playerHashEntry3 = this.removeKey(j1);
		return playerHashEntry3 == null ? null : playerHashEntry3.value;
	}

	final PlayerHashEntry removeKey(long j1) {
		int i3 = getHashedKey(j1);
		int i4 = getHashIndex(i3, this.hashArray.length);
		PlayerHashEntry playerHashEntry5 = this.hashArray[i4];

		PlayerHashEntry playerHashEntry6;
		PlayerHashEntry playerHashEntry7;
		for(playerHashEntry6 = playerHashEntry5; playerHashEntry6 != null; playerHashEntry6 = playerHashEntry7) {
			playerHashEntry7 = playerHashEntry6.nextEntry;
			if(playerHashEntry6.key == j1) {
				++this.field_950_e;
				--this.numHashElements;
				if(playerHashEntry5 == playerHashEntry6) {
					this.hashArray[i4] = playerHashEntry7;
				} else {
					playerHashEntry5.nextEntry = playerHashEntry7;
				}

				return playerHashEntry6;
			}

			playerHashEntry5 = playerHashEntry6;
		}

		return playerHashEntry6;
	}

	private void createKey(int i1, long j2, Object object4, int i5) {
		PlayerHashEntry playerHashEntry6 = this.hashArray[i5];
		this.hashArray[i5] = new PlayerHashEntry(i1, j2, object4, playerHashEntry6);
		if(this.numHashElements++ >= this.capacity) {
			this.resizeTable(2 * this.hashArray.length);
		}

	}

	static int getHashCode(long j0) {
		return getHashedKey(j0);
	}
}
