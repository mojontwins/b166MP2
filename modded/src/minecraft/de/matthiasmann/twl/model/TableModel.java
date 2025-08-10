package de.matthiasmann.twl.model;

public interface TableModel extends TableColumnHeaderModel {
	int getNumRows();

	Object getCell(int i1, int i2);

	Object getTooltipContent(int i1, int i2);

	void addChangeListener(TableModel.ChangeListener tableModel$ChangeListener1);

	void removeChangeListener(TableModel.ChangeListener tableModel$ChangeListener1);

	public interface ChangeListener extends TableColumnHeaderModel.ColumnHeaderChangeListener {
		void rowsInserted(int i1, int i2);

		void rowsDeleted(int i1, int i2);

		void rowsChanged(int i1, int i2);

		void cellChanged(int i1, int i2);

		void allChanged();
	}
}
