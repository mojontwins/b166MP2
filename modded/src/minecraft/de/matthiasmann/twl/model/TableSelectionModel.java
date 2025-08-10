package de.matthiasmann.twl.model;

public interface TableSelectionModel {
	void rowsInserted(int i1, int i2);

	void rowsDeleted(int i1, int i2);

	void clearSelection();

	void setSelection(int i1, int i2);

	void addSelection(int i1, int i2);

	void invertSelection(int i1, int i2);

	void removeSelection(int i1, int i2);

	int getLeadIndex();

	int getAnchorIndex();

	void setLeadIndex(int i1);

	void setAnchorIndex(int i1);

	boolean isSelected(int i1);

	boolean hasSelection();

	int getFirstSelected();

	int getLastSelected();

	int[] getSelection();

	void addSelectionChangeListener(Runnable runnable1);

	void removeSelectionChangeListener(Runnable runnable1);
}
