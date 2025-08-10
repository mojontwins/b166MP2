package de.matthiasmann.twl.model;

public interface MRUListModel extends ListModel {
	int getMaxEntries();

	int getNumEntries();

	Object getEntry(int i1);

	void addEntry(Object object1);

	void removeEntry(int i1);

	void addChangeListener(ListModel.ChangeListener listModel$ChangeListener1);

	void removeChangeListener(ListModel.ChangeListener listModel$ChangeListener1);
}
