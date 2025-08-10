package de.matthiasmann.twl.model;

import java.util.Comparator;
import java.util.Random;

public class ReorderListModel extends AbstractListModel {
	private final ListModel base;
	private final ListModel.ChangeListener listener;
	private int[] reorderList;
	private int size;
	private static final int INSERTIONSORT_THRESHOLD = 7;

	public ReorderListModel(ListModel base) {
		this.base = base;
		this.reorderList = new int[0];
		this.listener = new ListModel.ChangeListener() {
			public void entriesInserted(int first, int last) {
				ReorderListModel.this.entriesInserted(first, last);
			}

			public void entriesDeleted(int first, int last) {
				ReorderListModel.this.entriesDeleted(first, last);
			}

			public void entriesChanged(int first, int last) {
			}

			public void allChanged() {
				ReorderListModel.this.buildNewList();
			}
		};
		base.addChangeListener(this.listener);
		this.buildNewList();
	}

	public void destroy() {
		this.base.removeChangeListener(this.listener);
	}

	public int getNumEntries() {
		return this.size;
	}

	public Object getEntry(int index) {
		int remappedIndex = this.reorderList[index];
		return this.base.getEntry(remappedIndex);
	}

	public Object getEntryTooltip(int index) {
		int remappedIndex = this.reorderList[index];
		return this.base.getEntryTooltip(remappedIndex);
	}

	public boolean matchPrefix(int index, String prefix) {
		int remappedIndex = this.reorderList[index];
		return this.base.matchPrefix(remappedIndex, prefix);
	}

	public int findEntry(Object o) {
		int[] list = this.reorderList;
		int i = 0;

		for(int n = this.size; i < n; ++i) {
			Object entry = this.base.getEntry(list[i]);
			if(entry == o || entry != null && entry.equals(o)) {
				return i;
			}
		}

		return -1;
	}

	public void shuffle() {
		Random r = new Random();

		int j;
		int temp;
		for(int i = this.size; i > 1; this.reorderList[j] = temp) {
			j = r.nextInt(i--);
			temp = this.reorderList[i];
			this.reorderList[i] = this.reorderList[j];
		}

		this.fireAllChanged();
	}

	public void sort(Comparator c) {
		int[] aux = new int[this.size];
		System.arraycopy(this.reorderList, 0, aux, 0, this.size);
		this.mergeSort(aux, this.reorderList, 0, this.size, c);
		this.fireAllChanged();
	}

	private void mergeSort(int[] src, int[] dest, int low, int high, Comparator c) {
		int length = high - low;
		int mid;
		int i;
		if(length < 7) {
			for(mid = low; mid < high; ++mid) {
				for(i = mid; i > low && this.compare(dest, i - 1, i, c) > 0; --i) {
					swap(dest, i, i - 1);
				}
			}

		} else {
			mid = low + high >>> 1;
			this.mergeSort(dest, src, low, mid, c);
			this.mergeSort(dest, src, mid, high, c);
			if(this.compare(src, mid - 1, mid, c) <= 0) {
				System.arraycopy(src, low, dest, low, length);
			} else {
				i = low;
				int p = low;

				for(int q = mid; i < high; ++i) {
					if(q < high && (p >= mid || this.compare(src, p, q, c) > 0)) {
						dest[i] = src[q++];
					} else {
						dest[i] = src[p++];
					}
				}

			}
		}
	}

	private int compare(int[] list, int a, int b, Comparator c) {
		int aIdx = list[a];
		int bIdx = list[b];
		Object objA = this.base.getEntry(aIdx);
		Object objB = this.base.getEntry(bIdx);
		return c.compare(objA, objB);
	}

	private static void swap(int[] x, int a, int b) {
		int t = x[a];
		x[a] = x[b];
		x[b] = t;
	}

	private void buildNewList() {
		this.size = this.base.getNumEntries();
		this.reorderList = new int[this.size + 1024];

		for(int i = 0; i < this.size; this.reorderList[i] = i++) {
		}

		this.fireAllChanged();
	}

	private void entriesInserted(int first, int last) {
		int delta = last - first + 1;

		int oldSize;
		for(oldSize = 0; oldSize < this.size; ++oldSize) {
			if(this.reorderList[oldSize] >= first) {
				this.reorderList[oldSize] += delta;
			}
		}

		if(this.size + delta > this.reorderList.length) {
			int[] i6 = new int[Math.max(this.size * 2, this.size + delta + 1024)];
			System.arraycopy(this.reorderList, 0, i6, 0, this.size);
			this.reorderList = i6;
		}

		oldSize = this.size;

		for(int i = 0; i < delta; ++i) {
			this.reorderList[this.size++] = first + i;
		}

		this.fireEntriesInserted(oldSize, this.size - 1);
	}

	private void entriesDeleted(int first, int last) {
		int delta = last - first + 1;

		for(int i = 0; i < this.size; ++i) {
			int entry = this.reorderList[i];
			if(entry >= first) {
				if(entry <= last) {
					this.entriesDeletedCopy(first, last, i);
					return;
				}

				this.reorderList[i] = entry - delta;
			}
		}

	}

	private void entriesDeletedCopy(int first, int last, int i) {
		int delta = last - first + 1;
		int oldSize = this.size;

		int j;
		for(j = i; i < oldSize; ++i) {
			int entry = this.reorderList[i];
			if(entry >= first) {
				if(entry <= last) {
					--this.size;
					this.fireEntriesDeleted(j, j);
					continue;
				}

				entry -= delta;
			}

			this.reorderList[j++] = entry;
		}

		assert this.size == j;

	}
}
