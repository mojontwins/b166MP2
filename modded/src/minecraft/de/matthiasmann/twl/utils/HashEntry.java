package de.matthiasmann.twl.utils;

import java.lang.reflect.Array;

public class HashEntry {
	public final Object key;
	final int hash;
	HashEntry next;

	public HashEntry(Object key) {
		this.key = key;
		this.hash = key.hashCode();
	}

	public HashEntry next() {
		return this.next;
	}

	public static HashEntry get(HashEntry[] table, Object key) {
		int hash = key.hashCode();

		HashEntry e;
		for(e = table[hash & table.length - 1]; e != null; e = e.next) {
			if(e.hash == hash) {
				Object k = e.key;
				if(e.key == key || key.equals(k)) {
					break;
				}
			}
		}

		return e;
	}

	public static void insertEntry(HashEntry[] table, HashEntry newEntry) {
		int idx = newEntry.hash & table.length - 1;
		newEntry.next = table[idx];
		table[idx] = newEntry;
	}

	public static HashEntry remove(HashEntry[] table, Object key) {
		int hash = key.hashCode();
		int idx = hash & table.length - 1;
		HashEntry e = table[idx];

		HashEntry p;
		for(p = null; e != null; e = e.next) {
			if(e.hash == hash) {
				Object k = e.key;
				if(e.key == key || key.equals(k)) {
					break;
				}
			}

			p = e;
		}

		if(e != null) {
			if(p != null) {
				p.next = e.next;
			} else {
				table[idx] = e.next;
			}
		}

		return e;
	}

	public static void remove(HashEntry[] table, HashEntry entry) {
		int idx = entry.hash & table.length - 1;
		HashEntry e = table[idx];
		if(e == entry) {
			table[idx] = e.next;
		} else {
			HashEntry p;
			do {
				p = e;
				e = e.next;
			} while(e != entry);

			p.next = e.next;
		}

	}

	public static HashEntry[] maybeResizeTable(HashEntry[] table, int usedCount) {
		if(usedCount * 4 > table.length * 3) {
			table = resizeTable(table, table.length * 2);
		}

		return table;
	}

	private static HashEntry[] resizeTable(HashEntry[] table, int newSize) {
		if(newSize >= 4 && (newSize & newSize - 1) == 0) {
			HashEntry[] newTable = (HashEntry[])Array.newInstance(table.getClass().getComponentType(), newSize);
			int i = 0;

			HashEntry ne;
			for(int n = table.length; i < n; ++i) {
				for(HashEntry e = table[i]; e != null; e = ne) {
					ne = e.next;
					int ni = e.hash & newSize - 1;
					e.next = newTable[ni];
					newTable[ni] = e;
				}
			}

			return newTable;
		} else {
			throw new IllegalArgumentException("newSize");
		}
	}
}
