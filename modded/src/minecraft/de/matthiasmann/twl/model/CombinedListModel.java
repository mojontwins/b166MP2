package de.matthiasmann.twl.model;

import java.util.ArrayList;

public class CombinedListModel extends SimpleListModel {
	private final ArrayList sublists = new ArrayList();
	private int[] sublistStarts = new int[1];
	private CombinedListModel.SubListsModel subListsModel;

	public int getNumEntries() {
		return this.sublistStarts[this.sublistStarts.length - 1];
	}

	public Object getEntry(int index) {
		CombinedListModel.Sublist sl = this.getSublistForIndex(index);
		if(sl != null) {
			return sl.getEntry(index - sl.startIndex);
		} else {
			throw new IndexOutOfBoundsException();
		}
	}

	public Object getEntryTooltip(int index) {
		CombinedListModel.Sublist sl = this.getSublistForIndex(index);
		if(sl != null) {
			return sl.getEntryTooltip(index - sl.startIndex);
		} else {
			throw new IndexOutOfBoundsException();
		}
	}

	public int getNumSubLists() {
		return this.sublists.size();
	}

	public void addSubList(ListModel model) {
		this.addSubList(this.sublists.size(), model);
	}

	public void addSubList(int index, ListModel model) {
		CombinedListModel.Sublist sl = new CombinedListModel.Sublist(model);
		this.sublists.add(index, sl);
		this.adjustStartOffsets();
		int numEntries = sl.getNumEntries();
		if(numEntries > 0) {
			this.fireEntriesInserted(sl.startIndex, sl.startIndex + numEntries - 1);
		}

		if(this.subListsModel != null) {
			this.subListsModel.fireEntriesInserted(index, index);
		}

	}

	public int findSubList(ListModel model) {
		for(int i = 0; i < this.sublists.size(); ++i) {
			CombinedListModel.Sublist sl = (CombinedListModel.Sublist)this.sublists.get(i);
			if(sl.list == model) {
				return i;
			}
		}

		return -1;
	}

	public void removeAllSubLists() {
		for(int i = 0; i < this.sublists.size(); ++i) {
			((CombinedListModel.Sublist)this.sublists.get(i)).removeChangeListener();
		}

		this.sublists.clear();
		this.adjustStartOffsets();
		this.fireAllChanged();
		if(this.subListsModel != null) {
			this.subListsModel.fireAllChanged();
		}

	}

	public boolean removeSubList(ListModel model) {
		int index = this.findSubList(model);
		if(index >= 0) {
			this.removeSubList(index);
			return true;
		} else {
			return false;
		}
	}

	public ListModel removeSubList(int index) {
		CombinedListModel.Sublist sl = (CombinedListModel.Sublist)this.sublists.remove(index);
		sl.removeChangeListener();
		this.adjustStartOffsets();
		int numEntries = sl.getNumEntries();
		if(numEntries > 0) {
			this.fireEntriesDeleted(sl.startIndex, sl.startIndex + numEntries - 1);
		}

		if(this.subListsModel != null) {
			this.subListsModel.fireEntriesDeleted(index, index);
		}

		return sl.list;
	}

	public ListModel getModelForSubLists() {
		if(this.subListsModel == null) {
			this.subListsModel = new CombinedListModel.SubListsModel();
		}

		return this.subListsModel;
	}

	public int getStartIndexOfSublist(int sublistIndex) {
		return ((CombinedListModel.Sublist)this.sublists.get(sublistIndex)).startIndex;
	}

	private CombinedListModel.Sublist getSublistForIndex(int index) {
		int[] offsets = this.sublistStarts;
		int lo = 0;
		int hi = offsets.length - 1;

		while(lo <= hi) {
			int sl = lo + hi >>> 1;
			int delta = offsets[sl] - index;
			if(delta <= 0) {
				lo = sl + 1;
			}

			if(delta > 0) {
				hi = sl - 1;
			}
		}

		if(lo > 0 && lo <= this.sublists.size()) {
			CombinedListModel.Sublist sl1 = (CombinedListModel.Sublist)this.sublists.get(lo - 1);

			assert sl1.startIndex <= index;

			return sl1;
		} else {
			return null;
		}
	}

	void adjustStartOffsets() {
		int[] offsets = new int[this.sublists.size() + 1];
		int startIdx = 0;

		for(int idx = 0; idx < this.sublists.size(); offsets[idx] = startIdx) {
			CombinedListModel.Sublist sl = (CombinedListModel.Sublist)this.sublists.get(idx);
			sl.startIndex = startIdx;
			startIdx += sl.getNumEntries();
			++idx;
		}

		this.sublistStarts = offsets;
	}

	class SubListsModel extends SimpleListModel {
		public int getNumEntries() {
			return CombinedListModel.this.sublists.size();
		}

		public ListModel getEntry(int index) {
			return ((CombinedListModel.Sublist)CombinedListModel.this.sublists.get(index)).list;
		}

		public Object getEntry(int i1) {
			return this.getEntry(i1);
		}
	}

	class Sublist implements ListModel.ChangeListener {
		final ListModel list;
		int startIndex;

		public Sublist(ListModel list) {
			this.list = list;
			this.list.addChangeListener(this);
		}

		public void removeChangeListener() {
			this.list.removeChangeListener(this);
		}

		public boolean matchPrefix(int index, String prefix) {
			return this.list.matchPrefix(index, prefix);
		}

		public int getNumEntries() {
			return this.list.getNumEntries();
		}

		public Object getEntryTooltip(int index) {
			return this.list.getEntryTooltip(index);
		}

		public Object getEntry(int index) {
			return this.list.getEntry(index);
		}

		public void entriesInserted(int first, int last) {
			CombinedListModel.this.adjustStartOffsets();
			CombinedListModel.this.fireEntriesInserted(this.startIndex + first, this.startIndex + last);
		}

		public void entriesDeleted(int first, int last) {
			CombinedListModel.this.adjustStartOffsets();
			CombinedListModel.this.fireEntriesDeleted(this.startIndex + first, this.startIndex + last);
		}

		public void entriesChanged(int first, int last) {
			CombinedListModel.this.fireEntriesChanged(this.startIndex + first, this.startIndex + last);
		}

		public void allChanged() {
			CombinedListModel.this.fireAllChanged();
		}
	}
}
