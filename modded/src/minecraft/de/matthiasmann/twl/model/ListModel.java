package de.matthiasmann.twl.model;

public interface ListModel {
	int getNumEntries();

	Object getEntry(int i1);

	Object getEntryTooltip(int i1);

	boolean matchPrefix(int i1, String string2);

	void addChangeListener(ListModel.ChangeListener listModel$ChangeListener1);

	void removeChangeListener(ListModel.ChangeListener listModel$ChangeListener1);

	public interface ChangeListener {
		void entriesInserted(int i1, int i2);

		void entriesDeleted(int i1, int i2);

		void entriesChanged(int i1, int i2);

		void allChanged();
	}
}
