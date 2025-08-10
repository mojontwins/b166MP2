package de.matthiasmann.twl.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class SimpleChangableListModel extends SimpleListModel {
	private final ArrayList content;

	public SimpleChangableListModel() {
		this.content = new ArrayList();
	}

	public SimpleChangableListModel(Collection content) {
		this.content = new ArrayList(content);
	}

	public SimpleChangableListModel(Object... content) {
		this.content = new ArrayList(Arrays.asList(content));
	}

	public Object getEntry(int index) {
		return this.content.get(index);
	}

	public int getNumEntries() {
		return this.content.size();
	}

	public void addElement(Object element) {
		this.insertElement(this.getNumEntries(), element);
	}

	public void addElements(Collection elements) {
		this.insertElements(this.getNumEntries(), elements);
	}

	public void addElements(Object... elements) {
		this.insertElements(this.getNumEntries(), elements);
	}

	public void insertElement(int idx, Object element) {
		this.content.add(idx, element);
		this.fireEntriesInserted(idx, idx);
	}

	public void insertElements(int idx, Collection elements) {
		this.content.addAll(idx, elements);
		this.fireEntriesInserted(idx, idx + elements.size() - 1);
	}

	public void insertElements(int idx, Object... elements) {
		this.insertElements(idx, (Collection)Arrays.asList(elements));
	}

	public Object removeElement(int idx) {
		Object result = this.content.remove(idx);
		this.fireEntriesDeleted(idx, idx);
		return result;
	}

	public Object setElement(int idx, Object element) {
		Object result = this.content.set(idx, element);
		this.fireEntriesChanged(idx, idx);
		return result;
	}

	public int findElement(Object element) {
		return this.content.indexOf(element);
	}

	public void clear() {
		this.content.clear();
		this.fireAllChanged();
	}
}
