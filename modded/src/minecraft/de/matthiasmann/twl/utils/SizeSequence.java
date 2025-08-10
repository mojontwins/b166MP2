package de.matthiasmann.twl.utils;

import java.util.Arrays;

public class SizeSequence {
	private static final int INITIAL_CAPACITY = 64;
	protected int[] table;
	protected int size;
	protected int defaultValue;

	public SizeSequence() {
		this(64);
	}

	public SizeSequence(int initialCapacity) {
		this.table = new int[initialCapacity];
	}

	public int size() {
		return this.size;
	}

	public int getPosition(int index) {
		int low = 0;
		int high = this.size;
		int result = 0;

		while(low < high) {
			int mid = low + high >>> 1;
			if(index <= mid) {
				high = mid;
			} else {
				result += this.table[mid];
				low = mid + 1;
			}
		}

		return result;
	}

	public int getEndPosition() {
		int low = 0;
		int high = this.size;

		int result;
		int mid;
		for(result = 0; low < high; low = mid + 1) {
			mid = low + high >>> 1;
			result += this.table[mid];
		}

		return result;
	}

	public int getIndex(int position) {
		int low = 0;
		int high = this.size;

		while(low < high) {
			int mid = low + high >>> 1;
			int pos = this.table[mid];
			if(position < pos) {
				high = mid;
			} else {
				low = mid + 1;
				position -= pos;
			}
		}

		return low;
	}

	public int getSize(int index) {
		return this.getPosition(index + 1) - this.getPosition(index);
	}

	public boolean setSize(int index, int size) {
		int delta = size - this.getSize(index);
		if(delta != 0) {
			this.adjustSize(index, delta);
			return true;
		} else {
			return false;
		}
	}

	protected void adjustSize(int index, int delta) {
		int low = 0;
		int high = this.size;

		while(low < high) {
			int mid = low + high >>> 1;
			if(index <= mid) {
				this.table[mid] += delta;
				high = mid;
			} else {
				low = mid + 1;
			}
		}

	}

	protected int toSizes(int low, int high, int[] dst) {
		int subResult;
		int mid;
		for(subResult = 0; low < high; low = mid + 1) {
			mid = low + high >>> 1;
			int pos = this.table[mid];
			dst[mid] = pos - this.toSizes(low, mid, dst);
			subResult += pos;
		}

		return subResult;
	}

	protected int fromSizes(int low, int high) {
		int subResult;
		int mid;
		for(subResult = 0; low < high; low = mid + 1) {
			mid = low + high >>> 1;
			int pos = this.table[mid] + this.fromSizes(low, mid);
			this.table[mid] = pos;
			subResult += pos;
		}

		return subResult;
	}

	public void insert(int index, int count) {
		int newSize = this.size + count;
		if(newSize >= this.table.length) {
			int[] sizes = new int[newSize];
			this.toSizes(0, this.size, sizes);
			this.table = sizes;
		} else {
			this.toSizes(0, this.size, this.table);
		}

		System.arraycopy(this.table, index, this.table, index + count, this.size - index);
		this.size = newSize;
		this.initializeSizes(index, count);
		this.fromSizes(0, newSize);
	}

	public void remove(int index, int count) {
		this.toSizes(0, this.size, this.table);
		int newSize = this.size - count;
		System.arraycopy(this.table, index + count, this.table, index, newSize - index);
		this.size = newSize;
		this.fromSizes(0, newSize);
	}

	public void initializeAll(int count) {
		if(this.table.length < count) {
			this.table = new int[count];
		}

		this.size = count;
		this.initializeSizes(0, count);
		this.fromSizes(0, count);
	}

	public void setDefaultValue(int defaultValue) {
		this.defaultValue = defaultValue;
	}

	protected void initializeSizes(int index, int count) {
		Arrays.fill(this.table, index, index + count, this.defaultValue);
	}
}
