package de.matthiasmann.twl.utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TypeMapping {
	TypeMapping.Entry[] table = new TypeMapping.Entry[16];
	int size;

	public void put(Class clazz, Object value) {
		if(value == null) {
			throw new NullPointerException("value");
		} else {
			this.removeCached();
			TypeMapping.Entry entry = (TypeMapping.Entry)HashEntry.get(this.table, clazz);
			if(entry != null) {
				HashEntry.remove(this.table, (HashEntry)entry);
				--this.size;
			}

			this.insert(new TypeMapping.Entry(clazz, value, false));
		}
	}

	public Object get(Class clazz) {
		TypeMapping.Entry entry = (TypeMapping.Entry)HashEntry.get(this.table, clazz);
		return entry != null ? entry.value : this.slowGet(clazz);
	}

	public Set getUniqueValues() {
		HashSet result = new HashSet();
		TypeMapping.Entry[] typeMapping$Entry5 = this.table;
		int i4 = this.table.length;

		for(int i3 = 0; i3 < i4; ++i3) {
			for(TypeMapping.Entry e = typeMapping$Entry5[i3]; e != null; e = (TypeMapping.Entry)e.next()) {
				if(!e.isCache) {
					result.add(e.value);
				}
			}
		}

		return result;
	}

	public Map getEntries() {
		HashMap result = new HashMap();
		TypeMapping.Entry[] typeMapping$Entry5 = this.table;
		int i4 = this.table.length;

		for(int i3 = 0; i3 < i4; ++i3) {
			for(TypeMapping.Entry e = typeMapping$Entry5[i3]; e != null; e = (TypeMapping.Entry)e.next()) {
				if(!e.isCache) {
					result.put((Class)e.key, e.value);
				}
			}
		}

		return result;
	}

	private Object slowGet(Class clazz) {
		TypeMapping.Entry entry = null;
		Class baseClass = clazz;

		label29:
		do {
			Class[] class7;
			int i6 = (class7 = baseClass.getInterfaces()).length;

			for(int i5 = 0; i5 < i6; ++i5) {
				Class value = class7[i5];
				entry = (TypeMapping.Entry)HashEntry.get(this.table, value);
				if(entry != null) {
					break label29;
				}
			}

			baseClass = baseClass.getSuperclass();
			if(baseClass == null) {
				break;
			}

			entry = (TypeMapping.Entry)HashEntry.get(this.table, baseClass);
		} while(entry == null);

		Object object8 = entry != null ? entry.value : null;
		this.insert(new TypeMapping.Entry(clazz, object8, true));
		return object8;
	}

	private void insert(TypeMapping.Entry newEntry) {
		this.table = (TypeMapping.Entry[])HashEntry.maybeResizeTable(this.table, this.size);
		HashEntry.insertEntry(this.table, newEntry);
		++this.size;
	}

	private void removeCached() {
		TypeMapping.Entry[] typeMapping$Entry4 = this.table;
		int i3 = this.table.length;

		TypeMapping.Entry n;
		for(int i2 = 0; i2 < i3; ++i2) {
			for(TypeMapping.Entry e = typeMapping$Entry4[i2]; e != null; e = n) {
				n = (TypeMapping.Entry)e.next();
				if(e.isCache) {
					HashEntry.remove(this.table, (HashEntry)e);
					--this.size;
				}
			}
		}

	}

	static class Entry extends HashEntry {
		final Object value;
		final boolean isCache;

		public Entry(Class key, Object value, boolean isCache) {
			super(key);
			this.value = value;
			this.isCache = isCache;
		}
	}
}
