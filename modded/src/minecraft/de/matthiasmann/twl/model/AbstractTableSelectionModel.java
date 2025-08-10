package de.matthiasmann.twl.model;

import de.matthiasmann.twl.utils.CallbackSupport;

public abstract class AbstractTableSelectionModel implements TableSelectionModel {
	protected int leadIndex = -1;
	protected int anchorIndex = -1;
	protected Runnable[] selectionChangeListener;

	public int getAnchorIndex() {
		return this.anchorIndex;
	}

	public int getLeadIndex() {
		return this.leadIndex;
	}

	public void setAnchorIndex(int index) {
		this.anchorIndex = index;
	}

	public void setLeadIndex(int index) {
		this.leadIndex = index;
	}

	public void addSelectionChangeListener(Runnable cb) {
		this.selectionChangeListener = (Runnable[])CallbackSupport.addCallbackToList(this.selectionChangeListener, cb, Runnable.class);
	}

	public void removeSelectionChangeListener(Runnable cb) {
		this.selectionChangeListener = (Runnable[])CallbackSupport.removeCallbackFromList(this.selectionChangeListener, cb);
	}

	public void rowsDeleted(int index, int count) {
		if(this.leadIndex >= index) {
			this.leadIndex = Math.max(index, this.leadIndex - count);
		}

		if(this.anchorIndex >= index) {
			this.anchorIndex = Math.max(index, this.anchorIndex - count);
		}

	}

	public void rowsInserted(int index, int count) {
		if(this.leadIndex >= index) {
			this.leadIndex += count;
		}

		if(this.anchorIndex >= index) {
			this.anchorIndex += count;
		}

	}

	protected void fireSelectionChange() {
		CallbackSupport.fireCallbacks(this.selectionChangeListener);
	}

	protected void updateLeadAndAnchor(int index0, int index1) {
		this.anchorIndex = index0;
		this.leadIndex = index1;
	}
}
