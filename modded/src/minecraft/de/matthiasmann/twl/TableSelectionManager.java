package de.matthiasmann.twl;

public interface TableSelectionManager {
	void setAssociatedTable(TableBase tableBase1);

	TableSelectionManager.SelectionGranularity getSelectionGranularity();

	boolean handleKeyStrokeAction(String string1, Event event2);

	boolean handleMouseEvent(int i1, int i2, Event event3);

	boolean isRowSelected(int i1);

	boolean isCellSelected(int i1, int i2);

	int getLeadRow();

	int getLeadColumn();

	void modelChanged();

	void rowsInserted(int i1, int i2);

	void rowsDeleted(int i1, int i2);

	void columnInserted(int i1, int i2);

	void columnsDeleted(int i1, int i2);

	public static enum SelectionGranularity {
		ROWS,
		CELLS;
	}
}
