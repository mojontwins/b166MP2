package de.matthiasmann.twl.model;

import de.matthiasmann.twl.utils.CallbackSupport;

import java.util.ArrayList;

public class SimpleMRUListModel implements MRUListModel {
	protected final ArrayList entries;
	protected final int maxEntries;
	protected ListModel.ChangeListener[] listeners;

	public SimpleMRUListModel(int maxEntries) {
		if(maxEntries <= 1) {
			throw new IllegalArgumentException("maxEntries <= 1");
		} else {
			this.entries = new ArrayList();
			this.maxEntries = maxEntries;
		}
	}

	public int getMaxEntries() {
		return this.maxEntries;
	}

	public int getNumEntries() {
		return this.entries.size();
	}

	public Object getEntry(int index) {
		return this.entries.get(index);
	}

	public void addEntry(Object entry) {
		int idx = this.entries.indexOf(entry);
		if(idx >= 0) {
			this.doDeleteEntry(idx);
		} else if(this.entries.size() == this.maxEntries) {
			this.doDeleteEntry(this.maxEntries - 1);
		}

		this.entries.add(0, entry);
		if(this.listeners != null) {
			ListModel.ChangeListener[] listModel$ChangeListener6 = this.listeners;
			int i5 = this.listeners.length;

			for(int i4 = 0; i4 < i5; ++i4) {
				ListModel.ChangeListener cl = listModel$ChangeListener6[i4];
				cl.entriesInserted(0, 0);
			}
		}

		this.saveEntries();
	}

	public void removeEntry(int index) {
		if(index < 0 && index >= this.entries.size()) {
			throw new IndexOutOfBoundsException();
		} else {
			this.doDeleteEntry(index);
			this.saveEntries();
		}
	}

	public void addChangeListener(ListModel.ChangeListener listener) {
		this.listeners = (ListModel.ChangeListener[])CallbackSupport.addCallbackToList(this.listeners, listener, ListModel.ChangeListener.class);
	}

	public void removeChangeListener(ListModel.ChangeListener listener) {
		this.listeners = (ListModel.ChangeListener[])CallbackSupport.removeCallbackFromList(this.listeners, listener);
	}

	protected void doDeleteEntry(int idx) {
		this.entries.remove(idx);
		if(this.listeners != null) {
			ListModel.ChangeListener[] listModel$ChangeListener5 = this.listeners;
			int i4 = this.listeners.length;

			for(int i3 = 0; i3 < i4; ++i3) {
				ListModel.ChangeListener cl = listModel$ChangeListener5[i3];
				cl.entriesDeleted(idx, idx);
			}
		}

	}

	protected void saveEntries() {
	}

	public Object getEntryTooltip(int index) {
		return null;
	}

	public boolean matchPrefix(int index, String prefix) {
		return false;
	}
}
