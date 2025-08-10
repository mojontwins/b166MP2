package de.matthiasmann.twl.utils;

import java.util.Arrays;

public class SparseGrid {
	SparseGrid.Node root;
	int numLevels;

	public SparseGrid(int pageSize) {
		this.root = new SparseGrid.Node(pageSize);
		this.numLevels = 1;
	}

	public SparseGrid.Entry get(int row, int column) {
		if(this.root.size > 0) {
			int levels = this.numLevels;
			Object e = this.root;

			do {
				SparseGrid.Node node = (SparseGrid.Node)e;
				int pos = node.findPos(row, column, node.size);
				if(pos == node.size) {
					return null;
				}

				e = node.children[pos];
				--levels;
			} while(levels > 0);

			assert e != null;

			if(((SparseGrid.Entry)e).compare(row, column) == 0) {
				return (SparseGrid.Entry)e;
			}
		}

		return null;
	}

	public void set(int row, int column, SparseGrid.Entry entry) {
		entry.row = row;
		entry.column = column;
		if(this.root.size == 0) {
			this.root.insertAt(0, entry);
			this.root.updateRowColumn();
		} else if(!this.root.insert(entry, this.numLevels)) {
			this.splitRoot();
			this.root.insert(entry, this.numLevels);
		}

	}

	public SparseGrid.Entry remove(int row, int column) {
		if(this.root.size == 0) {
			return null;
		} else {
			SparseGrid.Entry e = this.root.remove(row, column, this.numLevels);
			if(e != null) {
				this.maybeRemoveRoot();
			}

			return e;
		}
	}

	public void insertRows(int row, int count) {
		if(count > 0 && this.root.size > 0) {
			this.root.insertRows(row, count, this.numLevels);
		}

	}

	public void insertColumns(int column, int count) {
		if(count > 0 && this.root.size > 0) {
			this.root.insertColumns(column, count, this.numLevels);
		}

	}

	public void removeRows(int row, int count) {
		if(count > 0) {
			this.root.removeRows(row, count, this.numLevels);
			this.maybeRemoveRoot();
		}

	}

	public void removeColumns(int column, int count) {
		if(count > 0) {
			this.root.removeColumns(column, count, this.numLevels);
			this.maybeRemoveRoot();
		}

	}

	public void iterate(int startRow, int startColumn, int endRow, int endColumn, SparseGrid.GridFunction func) {
		if(this.root.size > 0) {
			int levels = this.numLevels;
			Object e = this.root;

			SparseGrid.Node node;
			int pos;
			do {
				node = (SparseGrid.Node)e;
				pos = node.findPos(startRow, startColumn, node.size - 1);
				e = node.children[pos];
				--levels;
			} while(levels > 0);

			assert e != null;

			if(((SparseGrid.Entry)e).compare(startRow, startColumn) < 0) {
				return;
			}

			do {
				for(int size = node.size; pos < size; ++pos) {
					SparseGrid.Entry sparseGrid$Entry11 = node.children[pos];
					if(sparseGrid$Entry11.row > endRow) {
						return;
					}

					if(sparseGrid$Entry11.column >= startColumn && sparseGrid$Entry11.column <= endColumn) {
						func.apply(sparseGrid$Entry11.row, sparseGrid$Entry11.column, sparseGrid$Entry11);
					}
				}

				pos = 0;
				node = node.next;
			} while(node != null);
		}

	}

	public boolean isEmpty() {
		return this.root.size == 0;
	}

	public void clear() {
		Arrays.fill(this.root.children, (Object)null);
		this.root.size = 0;
		this.numLevels = 1;
	}

	private void maybeRemoveRoot() {
		while(this.numLevels > 1 && this.root.size == 1) {
			this.root = (SparseGrid.Node)this.root.children[0];
			this.root.prev = null;
			this.root.next = null;
			--this.numLevels;
		}

		if(this.root.size == 0) {
			this.numLevels = 1;
		}

	}

	private void splitRoot() {
		SparseGrid.Node newNode = this.root.split();
		SparseGrid.Node newRoot = new SparseGrid.Node(this.root.children.length);
		newRoot.children[0] = this.root;
		newRoot.children[1] = newNode;
		newRoot.size = 2;
		this.root = newRoot;
		++this.numLevels;
	}

	public static class Entry {
		int row;
		int column;

		int compare(int row, int column) {
			int diff = this.row - row;
			if(diff == 0) {
				diff = this.column - column;
			}

			return diff;
		}
	}

	public interface GridFunction {
		void apply(int i1, int i2, SparseGrid.Entry sparseGrid$Entry3);
	}

	static class Node extends SparseGrid.Entry {
		final SparseGrid.Entry[] children;
		int size;
		SparseGrid.Node next;
		SparseGrid.Node prev;

		public Node(int size) {
			this.children = new SparseGrid.Entry[size];
		}

		boolean insert(SparseGrid.Entry e, int levels) {
			--levels;
			if(levels == 0) {
				return this.insertLeaf(e);
			} else {
				while(true) {
					int pos = this.findPos(e.row, e.column, this.size - 1);

					assert pos < this.size;

					SparseGrid.Node node = (SparseGrid.Node)this.children[pos];
					if(node.insert(e, levels)) {
						this.updateRowColumn();
						return true;
					}

					if(this.isFull()) {
						return false;
					}

					SparseGrid.Node node2 = node.split();
					this.insertAt(pos + 1, node2);
				}
			}
		}

		boolean insertLeaf(SparseGrid.Entry e) {
			int pos = this.findPos(e.row, e.column, this.size);
			if(pos < this.size) {
				SparseGrid.Entry c = this.children[pos];

				assert c.getClass() != SparseGrid.Node.class;

				int cmp = c.compare(e.row, e.column);
				if(cmp == 0) {
					this.children[pos] = e;
					return true;
				}

				assert cmp > 0;
			}

			if(this.isFull()) {
				return false;
			} else {
				this.insertAt(pos, e);
				return true;
			}
		}

		SparseGrid.Entry remove(int row, int column, int levels) {
			--levels;
			if(levels == 0) {
				return this.removeLeaf(row, column);
			} else {
				int pos = this.findPos(row, column, this.size - 1);

				assert pos < this.size;

				SparseGrid.Node node = (SparseGrid.Node)this.children[pos];
				SparseGrid.Entry e = node.remove(row, column, levels);
				if(e != null) {
					if(node.size == 0) {
						this.removeNodeAt(pos);
					} else if(node.isBelowHalf()) {
						this.tryMerge(pos);
					}

					this.updateRowColumn();
				}

				return e;
			}
		}

		SparseGrid.Entry removeLeaf(int row, int column) {
			int pos = this.findPos(row, column, this.size);
			if(pos == this.size) {
				return null;
			} else {
				SparseGrid.Entry c = this.children[pos];

				assert c.getClass() != SparseGrid.Node.class;

				int cmp = c.compare(row, column);
				if(cmp == 0) {
					this.removeAt(pos);
					if(pos == this.size && this.size > 0) {
						this.updateRowColumn();
					}

					return c;
				} else {
					return null;
				}
			}
		}

		int findPos(int row, int column, int high) {
			int low = 0;

			while(low < high) {
				int mid = low + high >>> 1;
				SparseGrid.Entry e = this.children[mid];
				int cmp = e.compare(row, column);
				if(cmp > 0) {
					high = mid;
				} else {
					if(cmp >= 0) {
						return mid;
					}

					low = mid + 1;
				}
			}

			return low;
		}

		void insertRows(int row, int count, int levels) {
			--levels;
			int i;
			SparseGrid.Entry sparseGrid$Entry6;
			if(levels > 0) {
				i = this.size;

				while(i-- > 0) {
					SparseGrid.Node e = (SparseGrid.Node)this.children[i];
					if(e.row < row) {
						break;
					}

					e.insertRows(row, count, levels);
				}
			} else {
				for(i = this.size; i-- > 0; sparseGrid$Entry6.row += count) {
					sparseGrid$Entry6 = this.children[i];
					if(sparseGrid$Entry6.row < row) {
						break;
					}
				}
			}

			this.updateRowColumn();
		}

		void insertColumns(int column, int count, int levels) {
			--levels;
			int i;
			if(levels > 0) {
				for(i = 0; i < this.size; ++i) {
					SparseGrid.Node e = (SparseGrid.Node)this.children[i];
					e.insertColumns(column, count, levels);
				}
			} else {
				for(i = 0; i < this.size; ++i) {
					SparseGrid.Entry sparseGrid$Entry6 = this.children[i];
					if(sparseGrid$Entry6.column >= column) {
						sparseGrid$Entry6.column += count;
					}
				}
			}

			this.updateRowColumn();
		}

		boolean removeRows(int row, int count, int levels) {
			--levels;
			if(levels > 0) {
				boolean i = false;
				int e = this.size;

				while(e-- > 0) {
					SparseGrid.Node n = (SparseGrid.Node)this.children[e];
					if(n.row < row) {
						break;
					}

					if(n.removeRows(row, count, levels)) {
						this.removeNodeAt(e);
					} else {
						i |= n.isBelowHalf();
					}
				}

				if(i && this.size > 1) {
					this.tryMerge();
				}
			} else {
				int i7 = this.size;

				while(i7-- > 0) {
					SparseGrid.Entry sparseGrid$Entry8 = this.children[i7];
					if(sparseGrid$Entry8.row < row) {
						break;
					}

					sparseGrid$Entry8.row -= count;
					if(sparseGrid$Entry8.row < row) {
						this.removeAt(i7);
					}
				}
			}

			if(this.size == 0) {
				return true;
			} else {
				this.updateRowColumn();
				return false;
			}
		}

		boolean removeColumns(int column, int count, int levels) {
			--levels;
			if(levels > 0) {
				boolean i = false;
				int e = this.size;

				while(e-- > 0) {
					SparseGrid.Node n = (SparseGrid.Node)this.children[e];
					if(n.removeColumns(column, count, levels)) {
						this.removeNodeAt(e);
					} else {
						i |= n.isBelowHalf();
					}
				}

				if(i && this.size > 1) {
					this.tryMerge();
				}
			} else {
				int i7 = this.size;

				while(i7-- > 0) {
					SparseGrid.Entry sparseGrid$Entry8 = this.children[i7];
					if(sparseGrid$Entry8.column >= column) {
						sparseGrid$Entry8.column -= count;
						if(sparseGrid$Entry8.column < column) {
							this.removeAt(i7);
						}
					}
				}
			}

			if(this.size == 0) {
				return true;
			} else {
				this.updateRowColumn();
				return false;
			}
		}

		void insertAt(int idx, SparseGrid.Entry what) {
			System.arraycopy(this.children, idx, this.children, idx + 1, this.size - idx);
			this.children[idx] = what;
			if(idx == this.size++) {
				this.updateRowColumn();
			}

		}

		void removeAt(int idx) {
			--this.size;
			System.arraycopy(this.children, idx + 1, this.children, idx, this.size - idx);
			this.children[this.size] = null;
		}

		void removeNodeAt(int idx) {
			SparseGrid.Node n = (SparseGrid.Node)this.children[idx];
			if(n.next != null) {
				n.next.prev = n.prev;
			}

			if(n.prev != null) {
				n.prev.next = n.next;
			}

			n.next = null;
			n.prev = null;
			this.removeAt(idx);
		}

		void tryMerge() {
			if(this.size == 2) {
				this.tryMerge2(0);
			} else {
				int i = this.size - 1;

				while(i-- > 1) {
					if(this.tryMerge3(i)) {
						--i;
					}
				}
			}

		}

		void tryMerge(int pos) {
			switch(this.size) {
			case 0:
			case 1:
				break;
			case 2:
				this.tryMerge2(0);
				break;
			default:
				if(pos + 1 == this.size) {
					this.tryMerge3(pos - 1);
				} else if(pos == 0) {
					this.tryMerge3(1);
				} else {
					this.tryMerge3(pos);
				}
			}

		}

		private void tryMerge2(int pos) {
			SparseGrid.Node n1 = (SparseGrid.Node)this.children[pos];
			SparseGrid.Node n2 = (SparseGrid.Node)this.children[pos + 1];
			if(n1.isBelowHalf() || n2.isBelowHalf()) {
				int sumSize = n1.size + n2.size;
				if(sumSize < this.children.length) {
					System.arraycopy(n2.children, 0, n1.children, n1.size, n2.size);
					n1.size = sumSize;
					n1.updateRowColumn();
					this.removeNodeAt(pos + 1);
				} else {
					Object[] temp = this.collect2(sumSize, n1, n2);
					this.distribute2(temp, n1, n2);
				}
			}

		}

		private boolean tryMerge3(int pos) {
			SparseGrid.Node n0 = (SparseGrid.Node)this.children[pos - 1];
			SparseGrid.Node n1 = (SparseGrid.Node)this.children[pos];
			SparseGrid.Node n2 = (SparseGrid.Node)this.children[pos + 1];
			if(n0.isBelowHalf() || n1.isBelowHalf() || n2.isBelowHalf()) {
				int sumSize = n0.size + n1.size + n2.size;
				if(sumSize < this.children.length) {
					System.arraycopy(n1.children, 0, n0.children, n0.size, n1.size);
					System.arraycopy(n2.children, 0, n0.children, n0.size + n1.size, n2.size);
					n0.size = sumSize;
					n0.updateRowColumn();
					this.removeNodeAt(pos + 1);
					this.removeNodeAt(pos);
					return true;
				}

				Object[] temp = this.collect3(sumSize, n0, n1, n2);
				if(sumSize < 2 * this.children.length) {
					this.distribute2(temp, n0, n1);
					this.removeNodeAt(pos + 1);
				} else {
					this.distribute3(temp, n0, n1, n2);
				}
			}

			return false;
		}

		private Object[] collect2(int sumSize, SparseGrid.Node n0, SparseGrid.Node n1) {
			Object[] temp = new Object[sumSize];
			System.arraycopy(n0.children, 0, temp, 0, n0.size);
			System.arraycopy(n1.children, 0, temp, n0.size, n1.size);
			return temp;
		}

		private Object[] collect3(int sumSize, SparseGrid.Node n0, SparseGrid.Node n1, SparseGrid.Node n2) {
			Object[] temp = new Object[sumSize];
			System.arraycopy(n0.children, 0, temp, 0, n0.size);
			System.arraycopy(n1.children, 0, temp, n0.size, n1.size);
			System.arraycopy(n2.children, 0, temp, n0.size + n1.size, n2.size);
			return temp;
		}

		private void distribute2(Object[] src, SparseGrid.Node n0, SparseGrid.Node n1) {
			int sumSize = src.length;
			n0.size = sumSize / 2;
			n1.size = sumSize - n0.size;
			System.arraycopy(src, 0, n0.children, 0, n0.size);
			System.arraycopy(src, n0.size, n1.children, 0, n1.size);
			n0.updateRowColumn();
			n1.updateRowColumn();
		}

		private void distribute3(Object[] src, SparseGrid.Node n0, SparseGrid.Node n1, SparseGrid.Node n2) {
			int sumSize = src.length;
			n0.size = sumSize / 3;
			n1.size = (sumSize - n0.size) / 2;
			n2.size = sumSize - (n0.size + n1.size);
			System.arraycopy(src, 0, n0.children, 0, n0.size);
			System.arraycopy(src, n0.size, n1.children, 0, n1.size);
			System.arraycopy(src, n0.size + n1.size, n2.children, 0, n2.size);
			n0.updateRowColumn();
			n1.updateRowColumn();
			n2.updateRowColumn();
		}

		boolean isFull() {
			return this.size == this.children.length;
		}

		boolean isBelowHalf() {
			return this.size * 2 < this.children.length;
		}

		SparseGrid.Node split() {
			SparseGrid.Node newNode = new SparseGrid.Node(this.children.length);
			int size1 = this.size / 2;
			int size2 = this.size - size1;
			System.arraycopy(this.children, size1, newNode.children, 0, size2);
			Arrays.fill(this.children, size1, this.size, (Object)null);
			newNode.size = size2;
			newNode.updateRowColumn();
			newNode.prev = this;
			newNode.next = this.next;
			this.size = size1;
			this.updateRowColumn();
			this.next = newNode;
			if(newNode.next != null) {
				newNode.next.prev = newNode;
			}

			return newNode;
		}

		void updateRowColumn() {
			SparseGrid.Entry e = this.children[this.size - 1];
			this.row = e.row;
			this.column = e.column;
		}
	}
}
