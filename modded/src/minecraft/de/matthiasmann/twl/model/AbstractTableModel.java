package de.matthiasmann.twl.model;

import de.matthiasmann.twl.utils.CallbackSupport;

public abstract class AbstractTableModel extends AbstractTableColumnHeaderModel implements TableModel {
	private TableModel.ChangeListener[] callbacks;

	public Object getTooltipContent(int row, int column) {
		return null;
	}

	public void addChangeListener(TableModel.ChangeListener listener) {
		this.callbacks = (TableModel.ChangeListener[])CallbackSupport.addCallbackToList(this.callbacks, listener, TableModel.ChangeListener.class);
	}

	public void removeChangeListener(TableModel.ChangeListener listener) {
		this.callbacks = (TableModel.ChangeListener[])CallbackSupport.removeCallbackFromList(this.callbacks, listener);
	}

	protected void fireRowsInserted(int idx, int count) {
		if(this.callbacks != null) {
			TableModel.ChangeListener[] tableModel$ChangeListener6 = this.callbacks;
			int i5 = this.callbacks.length;

			for(int i4 = 0; i4 < i5; ++i4) {
				TableModel.ChangeListener cl = tableModel$ChangeListener6[i4];
				cl.rowsInserted(idx, count);
			}
		}

	}

	protected void fireRowsDeleted(int idx, int count) {
		if(this.callbacks != null) {
			TableModel.ChangeListener[] tableModel$ChangeListener6 = this.callbacks;
			int i5 = this.callbacks.length;

			for(int i4 = 0; i4 < i5; ++i4) {
				TableModel.ChangeListener cl = tableModel$ChangeListener6[i4];
				cl.rowsDeleted(idx, count);
			}
		}

	}

	protected void fireRowsChanged(int idx, int count) {
		if(this.callbacks != null) {
			TableModel.ChangeListener[] tableModel$ChangeListener6 = this.callbacks;
			int i5 = this.callbacks.length;

			for(int i4 = 0; i4 < i5; ++i4) {
				TableModel.ChangeListener cl = tableModel$ChangeListener6[i4];
				cl.rowsChanged(idx, count);
			}
		}

	}

	protected void fireColumnInserted(int idx, int count) {
		if(this.callbacks != null) {
			TableModel.ChangeListener[] tableModel$ChangeListener6 = this.callbacks;
			int i5 = this.callbacks.length;

			for(int i4 = 0; i4 < i5; ++i4) {
				TableModel.ChangeListener cl = tableModel$ChangeListener6[i4];
				cl.columnInserted(idx, count);
			}
		}

	}

	protected void fireColumnDeleted(int idx, int count) {
		if(this.callbacks != null) {
			TableModel.ChangeListener[] tableModel$ChangeListener6 = this.callbacks;
			int i5 = this.callbacks.length;

			for(int i4 = 0; i4 < i5; ++i4) {
				TableModel.ChangeListener cl = tableModel$ChangeListener6[i4];
				cl.columnDeleted(idx, count);
			}
		}

	}

	protected void fireColumnHeaderChanged(int column) {
		if(this.callbacks != null) {
			TableModel.ChangeListener[] tableModel$ChangeListener5 = this.callbacks;
			int i4 = this.callbacks.length;

			for(int i3 = 0; i3 < i4; ++i3) {
				TableModel.ChangeListener cl = tableModel$ChangeListener5[i3];
				cl.columnHeaderChanged(column);
			}
		}

	}

	protected void fireCellChanged(int row, int column) {
		if(this.callbacks != null) {
			TableModel.ChangeListener[] tableModel$ChangeListener6 = this.callbacks;
			int i5 = this.callbacks.length;

			for(int i4 = 0; i4 < i5; ++i4) {
				TableModel.ChangeListener cl = tableModel$ChangeListener6[i4];
				cl.cellChanged(row, column);
			}
		}

	}

	protected void fireAllChanged() {
		if(this.callbacks != null) {
			TableModel.ChangeListener[] tableModel$ChangeListener4 = this.callbacks;
			int i3 = this.callbacks.length;

			for(int i2 = 0; i2 < i3; ++i2) {
				TableModel.ChangeListener cl = tableModel$ChangeListener4[i2];
				cl.allChanged();
			}
		}

	}
}
