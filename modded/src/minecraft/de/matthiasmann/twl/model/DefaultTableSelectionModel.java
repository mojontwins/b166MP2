package de.matthiasmann.twl.model;

import java.util.BitSet;

public class DefaultTableSelectionModel extends AbstractTableSelectionModel {
	private final BitSet value = new BitSet();
	private int minIndex = Integer.MAX_VALUE;
	private int maxIndex = Integer.MIN_VALUE;

	public int getFirstSelected() {
		return this.minIndex;
	}

	public int getLastSelected() {
		return this.maxIndex;
	}

	public boolean hasSelection() {
		return this.maxIndex >= this.minIndex;
	}

	public boolean isSelected(int index) {
		return this.value.get(index);
	}

	private void clearBit(int idx) {
		if(this.value.get(idx)) {
			this.value.clear(idx);
			if(idx == this.minIndex) {
				this.minIndex = this.value.nextSetBit(this.minIndex + 1);
				if(this.minIndex < 0) {
					this.minIndex = Integer.MAX_VALUE;
					this.maxIndex = Integer.MIN_VALUE;
					return;
				}
			}

			if(idx == this.maxIndex) {
				do {
					--this.maxIndex;
				} while(this.maxIndex >= this.minIndex && !this.value.get(this.maxIndex));
			}
		}

	}

	private void setBit(int idx) {
		if(!this.value.get(idx)) {
			this.value.set(idx);
			if(idx < this.minIndex) {
				this.minIndex = idx;
			}

			if(idx > this.maxIndex) {
				this.maxIndex = idx;
			}
		}

	}

	private void toggleBit(int idx) {
		if(this.value.get(idx)) {
			this.clearBit(idx);
		} else {
			this.setBit(idx);
		}

	}

	public void clearSelection() {
		if(this.hasSelection()) {
			this.minIndex = Integer.MAX_VALUE;
			this.maxIndex = Integer.MIN_VALUE;
			this.value.clear();
			this.fireSelectionChange();
		}

	}

	public void setSelection(int index0, int index1) {
		this.updateLeadAndAnchor(index0, index1);
		this.minIndex = Math.min(index0, index1);
		this.maxIndex = Math.max(index0, index1);
		this.value.clear();
		this.value.set(this.minIndex, this.maxIndex + 1);
		this.fireSelectionChange();
	}

	public void addSelection(int index0, int index1) {
		this.updateLeadAndAnchor(index0, index1);
		int min = Math.min(index0, index1);
		int max = Math.max(index0, index1);

		for(int i = min; i <= max; ++i) {
			this.setBit(i);
		}

		this.fireSelectionChange();
	}

	public void invertSelection(int index0, int index1) {
		this.updateLeadAndAnchor(index0, index1);
		int min = Math.min(index0, index1);
		int max = Math.max(index0, index1);

		for(int i = min; i <= max; ++i) {
			this.toggleBit(i);
		}

		this.fireSelectionChange();
	}

	public void removeSelection(int index0, int index1) {
		this.updateLeadAndAnchor(index0, index1);
		if(this.hasSelection()) {
			int min = Math.min(index0, index1);
			int max = Math.max(index0, index1);

			for(int i = min; i <= max; ++i) {
				this.clearBit(i);
			}

			this.fireSelectionChange();
		}

	}

	public int[] getSelection() {
		int[] result = new int[this.value.cardinality()];
		int idx = -1;

		for(int i = 0; (idx = this.value.nextSetBit(idx + 1)) >= 0; ++i) {
			result[i] = idx;
		}

		return result;
	}

	public void rowsInserted(int index, int count) {
		if(index <= this.maxIndex) {
			for(int i = this.maxIndex; i >= index; --i) {
				if(this.value.get(i)) {
					this.value.set(i + count);
				} else {
					this.value.clear(i + count);
				}
			}

			this.value.clear(index, index + count);
			this.maxIndex += count;
			if(index <= this.minIndex) {
				this.minIndex += count;
			}
		}

		super.rowsInserted(index, count);
	}

	public void rowsDeleted(int index, int count) {
		if(index <= this.maxIndex) {
			for(int i = index; i <= this.maxIndex; ++i) {
				if(this.value.get(i + count)) {
					this.value.set(i);
				} else {
					this.value.clear(i);
				}
			}

			this.minIndex = this.value.nextSetBit(0);
			if(this.minIndex < 0) {
				this.minIndex = Integer.MAX_VALUE;
				this.maxIndex = Integer.MIN_VALUE;
			} else {
				while(this.maxIndex >= this.minIndex && !this.value.get(this.maxIndex)) {
					--this.maxIndex;
				}
			}
		}

		super.rowsDeleted(index, count);
	}
}
