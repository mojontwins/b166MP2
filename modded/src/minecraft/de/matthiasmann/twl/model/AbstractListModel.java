package de.matthiasmann.twl.model;

import de.matthiasmann.twl.utils.CallbackSupport;

public abstract class AbstractListModel implements ListModel {
	private ListModel.ChangeListener[] listeners;

	public void addChangeListener(ListModel.ChangeListener listener) {
		this.listeners = (ListModel.ChangeListener[])CallbackSupport.addCallbackToList(this.listeners, listener, ListModel.ChangeListener.class);
	}

	public void removeChangeListener(ListModel.ChangeListener listener) {
		this.listeners = (ListModel.ChangeListener[])CallbackSupport.removeCallbackFromList(this.listeners, listener);
	}

	protected void fireEntriesInserted(int first, int last) {
		if(this.listeners != null) {
			ListModel.ChangeListener[] listModel$ChangeListener6 = this.listeners;
			int i5 = this.listeners.length;

			for(int i4 = 0; i4 < i5; ++i4) {
				ListModel.ChangeListener cl = listModel$ChangeListener6[i4];
				cl.entriesInserted(first, last);
			}
		}

	}

	protected void fireEntriesDeleted(int first, int last) {
		if(this.listeners != null) {
			ListModel.ChangeListener[] listModel$ChangeListener6 = this.listeners;
			int i5 = this.listeners.length;

			for(int i4 = 0; i4 < i5; ++i4) {
				ListModel.ChangeListener cl = listModel$ChangeListener6[i4];
				cl.entriesDeleted(first, last);
			}
		}

	}

	protected void fireEntriesChanged(int first, int last) {
		if(this.listeners != null) {
			ListModel.ChangeListener[] listModel$ChangeListener6 = this.listeners;
			int i5 = this.listeners.length;

			for(int i4 = 0; i4 < i5; ++i4) {
				ListModel.ChangeListener cl = listModel$ChangeListener6[i4];
				cl.entriesChanged(first, last);
			}
		}

	}

	protected void fireAllChanged() {
		if(this.listeners != null) {
			ListModel.ChangeListener[] listModel$ChangeListener4 = this.listeners;
			int i3 = this.listeners.length;

			for(int i2 = 0; i2 < i3; ++i2) {
				ListModel.ChangeListener cl = listModel$ChangeListener4[i2];
				cl.allChanged();
			}
		}

	}
}
