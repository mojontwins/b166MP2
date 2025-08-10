package de.matthiasmann.twl.model;

public abstract class SimpleListModel extends AbstractListModel {
	public Object getEntryTooltip(int index) {
		return null;
	}

	public boolean matchPrefix(int index, String prefix) {
		Object entry = this.getEntry(index);
		return entry != null ? entry.toString().regionMatches(true, 0, prefix, 0, prefix.length()) : false;
	}
}
