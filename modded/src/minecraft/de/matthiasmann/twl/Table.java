package de.matthiasmann.twl;

import de.matthiasmann.twl.model.TableModel;
import de.matthiasmann.twl.model.TreeTableNode;

public class Table extends TableBase {
	private final TableModel.ChangeListener modelChangeListener;
	TableModel model;

	public Table() {
		this.modelChangeListener = new Table.ModelChangeListener();
	}

	public Table(TableModel model) {
		this();
		this.setModel(model);
	}

	public TableModel getModel() {
		return this.model;
	}

	public void setModel(TableModel model) {
		if(this.model != null) {
			this.model.removeChangeListener(this.modelChangeListener);
		}

		this.columnHeaderModel = model;
		this.model = model;
		if(this.model != null) {
			this.numRows = model.getNumRows();
			this.numColumns = model.getNumColumns();
			this.model.addChangeListener(this.modelChangeListener);
		} else {
			this.numRows = 0;
			this.numColumns = 0;
		}

		this.modelAllChanged();
	}

	protected Object getCellData(int row, int column, TreeTableNode node) {
		return this.model.getCell(row, column);
	}

	protected TreeTableNode getNodeFromRow(int row) {
		return null;
	}

	protected Object getTooltipContentFromRow(int row, int column) {
		return this.model.getTooltipContent(row, column);
	}

	class ModelChangeListener implements TableModel.ChangeListener {
		public void rowsInserted(int idx, int count) {
			Table.this.numRows = Table.this.model.getNumRows();
			Table.this.modelRowsInserted(idx, count);
		}

		public void rowsDeleted(int idx, int count) {
			Table.this.checkRowRange(idx, count);
			Table.this.numRows = Table.this.model.getNumRows();
			Table.this.modelRowsDeleted(idx, count);
		}

		public void rowsChanged(int idx, int count) {
			Table.this.modelRowsChanged(idx, count);
		}

		public void columnDeleted(int idx, int count) {
			Table.this.checkColumnRange(idx, count);
			Table.this.numColumns = Table.this.model.getNumColumns();
			Table.this.modelColumnsDeleted(count, count);
		}

		public void columnInserted(int idx, int count) {
			Table.this.numColumns = Table.this.model.getNumColumns();
			Table.this.modelColumnsInserted(count, count);
		}

		public void columnHeaderChanged(int column) {
			Table.this.modelColumnHeaderChanged(column);
		}

		public void cellChanged(int row, int column) {
			Table.this.modelCellChanged(row, column);
		}

		public void allChanged() {
			Table.this.numRows = Table.this.model.getNumRows();
			Table.this.numColumns = Table.this.model.getNumColumns();
			Table.this.modelAllChanged();
		}
	}
}
