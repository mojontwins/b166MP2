package de.matthiasmann.twl;

import de.matthiasmann.twl.model.DefaultTableSelectionModel;
import de.matthiasmann.twl.model.TableSelectionModel;

public class TableRowSelectionManager implements TableSelectionManager {
	protected final ActionMap actionMap;
	protected final TableSelectionModel selectionModel;
	protected TableBase tableBase;
	protected static final int TOGGLE = 0;
	protected static final int EXTEND = 1;
	protected static final int SET = 2;
	protected static final int MOVE = 3;

	public TableRowSelectionManager(TableSelectionModel selectionModel) {
		if(selectionModel == null) {
			throw new NullPointerException("selectionModel");
		} else {
			this.selectionModel = selectionModel;
			this.actionMap = new ActionMap();
			this.actionMap.addMapping(this);
		}
	}

	public TableRowSelectionManager() {
		this(new DefaultTableSelectionModel());
	}

	public void setAssociatedTable(TableBase base) {
		if(this.tableBase != base) {
			if(this.tableBase != null && base != null) {
				throw new IllegalStateException("selection manager still in use");
			}

			this.tableBase = base;
			this.modelChanged();
		}

	}

	public TableSelectionManager.SelectionGranularity getSelectionGranularity() {
		return TableSelectionManager.SelectionGranularity.ROWS;
	}

	public boolean handleKeyStrokeAction(String action, Event event) {
		return this.actionMap.invoke(action, event);
	}

	public boolean handleMouseEvent(int row, int column, Event event) {
		boolean isShift = (event.getModifiers() & 9) != 0;
		boolean isCtrl = (event.getModifiers() & 36) != 0;
		if(event.getType() == Event.Type.MOUSE_BTNDOWN && event.getMouseButton() == 0) {
			this.handleMouseDown(row, column, isShift, isCtrl);
			return true;
		} else {
			return event.getType() == Event.Type.MOUSE_CLICKED ? this.handleMouseClick(row, column, isShift, isCtrl) : false;
		}
	}

	public boolean isRowSelected(int row) {
		return this.selectionModel.isSelected(row);
	}

	public boolean isCellSelected(int row, int column) {
		return false;
	}

	public int getLeadRow() {
		return this.selectionModel.getLeadIndex();
	}

	public int getLeadColumn() {
		return -1;
	}

	public void modelChanged() {
		this.selectionModel.clearSelection();
		this.selectionModel.setAnchorIndex(-1);
		this.selectionModel.setLeadIndex(-1);
	}

	public void rowsInserted(int index, int count) {
		this.selectionModel.rowsInserted(index, count);
	}

	public void rowsDeleted(int index, int count) {
		this.selectionModel.rowsDeleted(index, count);
	}

	public void columnInserted(int index, int count) {
	}

	public void columnsDeleted(int index, int count) {
	}

	@ActionMap.Action
	public void selectNextRow() {
		this.handleRelativeAction(1, 2);
	}

	@ActionMap.Action
	public void selectPreviousRow() {
		this.handleRelativeAction(-1, 2);
	}

	@ActionMap.Action
	public void selectNextPage() {
		this.handleRelativeAction(this.getPageSize(), 2);
	}

	@ActionMap.Action
	public void selectPreviousPage() {
		this.handleRelativeAction(-this.getPageSize(), 2);
	}

	@ActionMap.Action
	public void selectFirstRow() {
		int numRows = this.getNumRows();
		if(numRows > 0) {
			this.handleAbsoluteAction(0, 2);
		}

	}

	@ActionMap.Action
	public void selectLastRow() {
		int numRows = this.getNumRows();
		if(numRows > 0) {
			this.handleRelativeAction(numRows - 1, 2);
		}

	}

	@ActionMap.Action
	public void extendSelectionToNextRow() {
		this.handleRelativeAction(1, 1);
	}

	@ActionMap.Action
	public void extendSelectionToPreviousRow() {
		this.handleRelativeAction(-1, 1);
	}

	@ActionMap.Action
	public void extendSelectionToNextPage() {
		this.handleRelativeAction(this.getPageSize(), 1);
	}

	@ActionMap.Action
	public void extendSelectionToPreviousPage() {
		this.handleRelativeAction(-this.getPageSize(), 1);
	}

	@ActionMap.Action
	public void extendSelectionToFirstRow() {
		int numRows = this.getNumRows();
		if(numRows > 0) {
			this.handleAbsoluteAction(0, 1);
		}

	}

	@ActionMap.Action
	public void extendSelectionToLastRow() {
		int numRows = this.getNumRows();
		if(numRows > 0) {
			this.handleRelativeAction(numRows - 1, 1);
		}

	}

	@ActionMap.Action
	public void moveLeadToNextRow() {
		this.handleRelativeAction(1, 3);
	}

	@ActionMap.Action
	public void moveLeadToPreviousRow() {
		this.handleRelativeAction(-1, 3);
	}

	@ActionMap.Action
	public void moveLeadToNextPage() {
		this.handleRelativeAction(this.getPageSize(), 3);
	}

	@ActionMap.Action
	public void moveLeadToPreviousPage() {
		this.handleRelativeAction(-this.getPageSize(), 3);
	}

	@ActionMap.Action
	public void moveLeadToFirstRow() {
		int numRows = this.getNumRows();
		if(numRows > 0) {
			this.handleAbsoluteAction(0, 3);
		}

	}

	@ActionMap.Action
	public void moveLeadToLastRow() {
		int numRows = this.getNumRows();
		if(numRows > 0) {
			this.handleAbsoluteAction(numRows - 1, 3);
		}

	}

	@ActionMap.Action
	public void toggleSelectionOnLeadRow() {
		int leadIndex = this.selectionModel.getLeadIndex();
		if(leadIndex > 0) {
			this.selectionModel.invertSelection(leadIndex, leadIndex);
		}

	}

	@ActionMap.Action
	public void selectAll() {
		int numRows = this.getNumRows();
		if(numRows > 0) {
			this.selectionModel.setSelection(0, numRows - 1);
		}

	}

	@ActionMap.Action
	public void selectNone() {
		this.selectionModel.clearSelection();
	}

	protected void handleRelativeAction(int delta, int mode) {
		int numRows = this.getNumRows();
		if(numRows > 0) {
			int leadIndex = Math.max(0, this.selectionModel.getLeadIndex());
			int index = Math.max(0, Math.min(numRows - 1, leadIndex + delta));
			this.handleAbsoluteAction(index, mode);
		}

	}

	protected void handleAbsoluteAction(int index, int mode) {
		if(this.tableBase != null) {
			this.tableBase.adjustScrollPosition(index);
		}

		switch(mode) {
		case 0:
			this.selectionModel.invertSelection(index, index);
			break;
		case 1:
			int anchorIndex = Math.max(0, this.selectionModel.getAnchorIndex());
			this.selectionModel.setSelection(anchorIndex, index);
			break;
		case 2:
		default:
			this.selectionModel.setSelection(index, index);
			break;
		case 3:
			this.selectionModel.setLeadIndex(index);
		}

	}

	protected void handleMouseDown(int row, int column, boolean isShift, boolean isCtrl) {
		if(row >= 0 && row < this.getNumRows()) {
			this.tableBase.adjustScrollPosition(row);
			int anchorIndex = this.selectionModel.getAnchorIndex();
			boolean anchorSelected;
			if(anchorIndex == -1) {
				anchorIndex = 0;
				anchorSelected = false;
			} else {
				anchorSelected = this.selectionModel.isSelected(anchorIndex);
			}

			if(isCtrl) {
				if(isShift) {
					if(anchorSelected) {
						this.selectionModel.addSelection(anchorIndex, row);
					} else {
						this.selectionModel.removeSelection(anchorIndex, row);
					}
				} else if(this.selectionModel.isSelected(row)) {
					this.selectionModel.removeSelection(row, row);
				} else {
					this.selectionModel.addSelection(row, row);
				}
			} else if(isShift) {
				this.selectionModel.setSelection(anchorIndex, row);
			} else {
				this.selectionModel.setSelection(row, row);
			}
		} else if(!isShift) {
			this.selectionModel.clearSelection();
		}

	}

	protected boolean handleMouseClick(int row, int column, boolean isShift, boolean isCtrl) {
		return false;
	}

	protected int getNumRows() {
		return this.tableBase != null ? this.tableBase.getNumRows() : 0;
	}

	protected int getPageSize() {
		return this.tableBase != null ? Math.max(1, this.tableBase.getNumVisibleRows()) : 1;
	}
}
