package de.matthiasmann.twl.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class SimpleTableModel extends AbstractTableModel {
	private final String[] columnHeaders;
	private final ArrayList rows;

	public SimpleTableModel(String[] columnHeaders) {
		if(columnHeaders.length < 1) {
			throw new IllegalArgumentException("must have atleast one column");
		} else {
			this.columnHeaders = (String[])columnHeaders.clone();
			this.rows = new ArrayList();
		}
	}

	public int getNumColumns() {
		return this.columnHeaders.length;
	}

	public String getColumnHeaderText(int column) {
		return this.columnHeaders[column];
	}

	public void setColumnHeaderText(int column, String text) {
		if(text == null) {
			throw new NullPointerException("text");
		} else {
			this.columnHeaders[column] = text;
			this.fireColumnHeaderChanged(column);
		}
	}

	public int getNumRows() {
		return this.rows.size();
	}

	public Object getCell(int row, int column) {
		return ((Object[])this.rows.get(row))[column];
	}

	public void setCell(int row, int column, Object data) {
		((Object[])this.rows.get(row))[column] = data;
		this.fireCellChanged(row, column);
	}

	public void addRow(Object... data) {
		this.insertRow(this.rows.size(), data);
	}

	public void addRows(Collection rows) {
		this.insertRows(rows.size(), rows);
	}

	public void insertRow(int index, Object... data) {
		this.rows.add(index, this.createRowData(data));
		this.fireRowsInserted(index, 1);
	}

	public void insertRows(int index, Collection rows) {
		if(!rows.isEmpty()) {
			ArrayList rowData = new ArrayList();
			Iterator iterator5 = rows.iterator();

			while(iterator5.hasNext()) {
				Object[] row = (Object[])iterator5.next();
				rowData.add(this.createRowData(row));
			}

			this.rows.addAll(index, rowData);
			this.fireRowsInserted(index, rowData.size());
		}

	}

	public void deleteRow(int index) {
		this.rows.remove(index);
		this.fireRowsDeleted(index, 1);
	}

	public void deleteRows(int index, int count) {
		int numRows = this.rows.size();
		if(index >= 0 && count >= 0 && index < numRows && count <= numRows - index) {
			if(count > 0) {
				int i = count;

				while(i-- > 0) {
					this.rows.remove(index + i);
				}

				this.fireRowsDeleted(index, count);
			}

		} else {
			throw new IndexOutOfBoundsException("index=" + index + " count=" + count + " numRows=" + numRows);
		}
	}

	private Object[] createRowData(Object[] data) {
		Object[] rowData = new Object[this.getNumColumns()];
		System.arraycopy(data, 0, rowData, 0, Math.min(rowData.length, data.length));
		return rowData;
	}
}
