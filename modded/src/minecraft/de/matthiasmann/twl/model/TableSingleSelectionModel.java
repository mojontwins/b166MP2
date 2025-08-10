package de.matthiasmann.twl.model;

public class TableSingleSelectionModel extends AbstractTableSelectionModel {
	public static final int NO_SELECTION = -1;
	private int selection;

	public void rowsInserted(int index, int count) {
		boolean changed = false;
		if(this.selection >= index) {
			this.selection += count;
			changed = true;
		}

		super.rowsInserted(index, count);
		if(changed) {
			this.fireSelectionChange();
		}

	}

	public void rowsDeleted(int index, int count) {
		boolean changed = false;
		if(this.selection >= index) {
			if(this.selection < index + count) {
				this.selection = -1;
			} else {
				this.selection -= count;
			}

			changed = true;
		}

		super.rowsDeleted(index, count);
		if(changed) {
			this.fireSelectionChange();
		}

	}

	public void clearSelection() {
		if(this.hasSelection()) {
			this.selection = -1;
			this.fireSelectionChange();
		}

	}

	public void setSelection(int index0, int index1) {
		this.updateLeadAndAnchor(index0, index1);
		this.selection = index1;
		this.fireSelectionChange();
	}

	public void addSelection(int index0, int index1) {
		this.updateLeadAndAnchor(index0, index1);
		this.selection = index1;
		this.fireSelectionChange();
	}

	public void invertSelection(int index0, int index1) {
		this.updateLeadAndAnchor(index0, index1);
		if(this.selection == index1) {
			this.selection = -1;
		} else {
			this.selection = index1;
		}

		this.fireSelectionChange();
	}

	public void removeSelection(int index0, int index1) {
		this.updateLeadAndAnchor(index0, index1);
		if(this.hasSelection()) {
			int first = Math.min(index0, index1);
			int last = Math.max(index0, index1);
			if(this.selection >= first && this.selection <= last) {
				this.selection = -1;
			}

			this.fireSelectionChange();
		}

	}

	public boolean isSelected(int index) {
		return this.selection == index;
	}

	public boolean hasSelection() {
		return this.selection >= 0;
	}

	public int getFirstSelected() {
		return this.selection;
	}

	public int getLastSelected() {
		return this.selection;
	}

	public int[] getSelection() {
		return this.selection >= 0 ? new int[]{this.selection} : new int[0];
	}
}
