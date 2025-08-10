package de.matthiasmann.twl;

import de.matthiasmann.twl.model.AbstractTableModel;
import de.matthiasmann.twl.model.DefaultTableSelectionModel;
import de.matthiasmann.twl.model.FileSystemModel;
import de.matthiasmann.twl.model.SortOrder;
import de.matthiasmann.twl.model.TableModel;
import de.matthiasmann.twl.model.TableSelectionModel;
import de.matthiasmann.twl.model.TableSingleSelectionModel;
import de.matthiasmann.twl.utils.CallbackSupport;
import de.matthiasmann.twl.utils.NaturalSortComparator;

import java.text.DateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;

public class FileTable extends Table {
	private final FileTable.FileTableModel fileTableModel = new FileTable.FileTableModel();
	private final Runnable selectionChangedListener;
	private TableSelectionModel fileTableSelectionModel;
	private TableSearchWindow tableSearchWindow;
	private FileTable.SortColumn sortColumn = FileTable.SortColumn.NAME;
	private SortOrder sortOrder = SortOrder.ASCENDING;
	private boolean allowMultiSelection;
	private FileSystemModel.FileFilter fileFilter = null;
	private boolean showFolders = true;
	private boolean showHidden = false;
	private FileSystemModel fsm;
	private Object currentFolder;
	private FileTable.Callback[] fileTableCallbacks;
	static FileTable.Entry[] EMPTY = new FileTable.Entry[0];

	public FileTable() {
		this.setModel(this.fileTableModel);
		this.selectionChangedListener = new Runnable() {
			public void run() {
				FileTable.this.selectionChanged();
			}
		};
	}

	public void addCallback(FileTable.Callback callback) {
		this.fileTableCallbacks = (FileTable.Callback[])CallbackSupport.addCallbackToList(this.fileTableCallbacks, callback, FileTable.Callback.class);
	}

	public void removeCallback(FileTable.Callback callback) {
		this.fileTableCallbacks = (FileTable.Callback[])CallbackSupport.removeCallbackFromList(this.fileTableCallbacks, callback);
	}

	public boolean getShowFolders() {
		return this.showFolders;
	}

	public void setShowFolders(boolean showFolders) {
		if(this.showFolders != showFolders) {
			this.showFolders = showFolders;
			this.refreshFileTable();
		}

	}

	public boolean getShowHidden() {
		return this.showHidden;
	}

	public void setShowHidden(boolean showHidden) {
		if(this.showHidden != showHidden) {
			this.showHidden = showHidden;
			this.refreshFileTable();
		}

	}

	public void setFileFilter(FileSystemModel.FileFilter filter) {
		this.fileFilter = filter;
		this.refreshFileTable();
	}

	public FileSystemModel.FileFilter getFileFilter() {
		return this.fileFilter;
	}

	public FileTable.Entry[] getSelection() {
		return this.fileTableModel.getEntries(this.fileTableSelectionModel.getSelection());
	}

	public void setSelection(Object... files) {
		this.fileTableSelectionModel.clearSelection();
		Object[] object5 = files;
		int i4 = files.length;

		for(int i3 = 0; i3 < i4; ++i3) {
			Object file = object5[i3];
			int idx = this.fileTableModel.findFile(file);
			if(idx >= 0) {
				this.fileTableSelectionModel.addSelection(idx, idx);
			}
		}

	}

	public boolean setSelection(Object file) {
		this.fileTableSelectionModel.clearSelection();
		int idx = this.fileTableModel.findFile(file);
		if(idx >= 0) {
			this.fileTableSelectionModel.addSelection(idx, idx);
			this.scrollToRow(idx);
			return true;
		} else {
			return false;
		}
	}

	public void setSortColumn(FileTable.SortColumn column) {
		if(column == null) {
			throw new NullPointerException("column");
		} else {
			if(this.sortColumn != column) {
				this.sortColumn = column;
				this.sortingChanged();
			}

		}
	}

	public void setSortOrder(SortOrder order) {
		if(order == null) {
			throw new NullPointerException("order");
		} else {
			if(this.sortOrder != order) {
				this.sortOrder = order;
				this.sortingChanged();
			}

		}
	}

	public boolean getAllowMultiSelection() {
		return this.allowMultiSelection;
	}

	public void setAllowMultiSelection(boolean allowMultiSelection) {
		this.allowMultiSelection = allowMultiSelection;
		if(this.fileTableSelectionModel != null) {
			this.fileTableSelectionModel.removeSelectionChangeListener(this.selectionChangedListener);
		}

		if(this.tableSearchWindow != null) {
			this.tableSearchWindow.setModel((TableModel)null, 0);
		}

		if(allowMultiSelection) {
			this.fileTableSelectionModel = new DefaultTableSelectionModel();
		} else {
			this.fileTableSelectionModel = new TableSingleSelectionModel();
		}

		this.fileTableSelectionModel.addSelectionChangeListener(this.selectionChangedListener);
		this.tableSearchWindow = new TableSearchWindow(this, this.fileTableSelectionModel);
		this.tableSearchWindow.setModel(this.fileTableModel, 0);
		this.setSelectionManager(new TableRowSelectionManager(this.fileTableSelectionModel));
		this.setKeyboardSearchHandler(this.tableSearchWindow);
		this.selectionChanged();
	}

	public FileSystemModel getFileSystemModel() {
		return this.fsm;
	}

	public final Object getCurrentFolder() {
		return this.currentFolder;
	}

	public final boolean isRoot() {
		return this.currentFolder == null;
	}

	public void setCurrentFolder(FileSystemModel fsm, Object folder) {
		this.fsm = fsm;
		this.currentFolder = folder;
		this.refreshFileTable();
	}

	public void refreshFileTable() {
		Object[] objs = this.collectObjects();
		if(objs != null) {
			int lastFileIdx = objs.length;
			FileTable.Entry[] entries = new FileTable.Entry[lastFileIdx];
			int numFolders = 0;
			boolean isRoot = this.isRoot();

			for(int i = 0; i < objs.length; ++i) {
				FileTable.Entry e = new FileTable.Entry(this.fsm, objs[i], isRoot);
				if(e.isFolder) {
					entries[numFolders++] = e;
				} else {
					--lastFileIdx;
					entries[lastFileIdx] = e;
				}
			}

			Arrays.sort(entries, 0, numFolders, FileTable.NameComparator.instance);
			this.sortFilesAndUpdateModel(entries, numFolders);
		} else {
			this.sortFilesAndUpdateModel(EMPTY, 0);
		}

		if(this.tableSearchWindow != null) {
			this.tableSearchWindow.cancelSearch();
		}

	}

	protected void selectionChanged() {
		if(this.fileTableCallbacks != null) {
			FileTable.Callback[] fileTable$Callback4 = this.fileTableCallbacks;
			int i3 = this.fileTableCallbacks.length;

			for(int i2 = 0; i2 < i3; ++i2) {
				FileTable.Callback cb = fileTable$Callback4[i2];
				cb.selectionChanged();
			}
		}

	}

	protected void sortingChanged() {
		this.setSortArrows();
		this.sortFilesAndUpdateModel();
		if(this.fileTableCallbacks != null) {
			FileTable.Callback[] fileTable$Callback4 = this.fileTableCallbacks;
			int i3 = this.fileTableCallbacks.length;

			for(int i2 = 0; i2 < i3; ++i2) {
				FileTable.Callback cb = fileTable$Callback4[i2];
				cb.sortingChanged();
			}
		}

	}

	private Object[] collectObjects() {
		if(this.fsm == null) {
			return null;
		} else if(this.isRoot()) {
			return this.fsm.listRoots();
		} else {
			Object filter = this.fileFilter;
			if(filter != null || !this.getShowFolders() || !this.getShowHidden()) {
				filter = new FileTable.FileFilterWrapper((FileSystemModel.FileFilter)filter, this.getShowFolders(), this.getShowHidden());
			}

			return this.fsm.listFolder(this.currentFolder, (FileSystemModel.FileFilter)filter);
		}
	}

	private void sortFilesAndUpdateModel(FileTable.Entry[] entries, int numFolders) {
		FileTable.StateSnapshot snapshot = this.makeSnapshot();
		Arrays.sort(entries, numFolders, entries.length, this.sortOrder.map(this.sortColumn.comparator));
		this.fileTableModel.setData(entries, numFolders);
		this.restoreSnapshot(snapshot);
	}

	protected void columnHeaderClicked(int column) {
		super.columnHeaderClicked(column);
		FileTable.SortColumn thisColumn = FileTable.SortColumn.values()[column];
		if(this.sortColumn == thisColumn) {
			this.setSortOrder(this.sortOrder.invert());
		} else {
			this.setSortColumn(thisColumn);
		}

	}

	protected void updateColumnHeaderNumbers() {
		super.updateColumnHeaderNumbers();
		this.setSortArrows();
	}

	protected void setSortArrows() {
		this.setColumnSortOrderAnimationState(this.sortColumn.ordinal(), this.sortOrder);
	}

	private void sortFilesAndUpdateModel() {
		this.sortFilesAndUpdateModel(this.fileTableModel.entries, this.fileTableModel.numFolders);
	}

	private FileTable.StateSnapshot makeSnapshot() {
		return new FileTable.StateSnapshot(this.fileTableModel.getEntry(this.fileTableSelectionModel.getLeadIndex()), this.fileTableModel.getEntry(this.fileTableSelectionModel.getAnchorIndex()), this.fileTableModel.getEntries(this.fileTableSelectionModel.getSelection()));
	}

	private void restoreSnapshot(FileTable.StateSnapshot snapshot) {
		FileTable.Entry[] fileTable$Entry5 = snapshot.selected;
		int i4 = snapshot.selected.length;

		int anchorIndex;
		for(anchorIndex = 0; anchorIndex < i4; ++anchorIndex) {
			FileTable.Entry leadIndex = fileTable$Entry5[anchorIndex];
			int idx = this.fileTableModel.findEntry(leadIndex);
			if(idx >= 0) {
				this.fileTableSelectionModel.addSelection(idx, idx);
			}
		}

		int i7 = this.fileTableModel.findEntry(snapshot.leadEntry);
		anchorIndex = this.fileTableModel.findEntry(snapshot.anchorEntry);
		this.fileTableSelectionModel.setLeadIndex(i7);
		this.fileTableSelectionModel.setAnchorIndex(anchorIndex);
		this.scrollToRow(Math.max(0, i7));
	}

	public interface Callback {
		void selectionChanged();

		void sortingChanged();
	}

	public static final class Entry {
		public final FileSystemModel fsm;
		public final Object obj;
		public final String name;
		public final boolean isFolder;
		public final long size;
		public final Date lastModified;

		public Entry(FileSystemModel fsm, Object obj, boolean isRoot) {
			this.fsm = fsm;
			this.obj = obj;
			this.name = fsm.getName(obj);
			if(isRoot) {
				this.isFolder = true;
				this.lastModified = null;
			} else {
				this.isFolder = fsm.isFolder(obj);
				this.lastModified = new Date(fsm.getLastModified(obj));
			}

			if(this.isFolder) {
				this.size = 0L;
			} else {
				this.size = fsm.getSize(obj);
			}

		}

		public String getExtension() {
			int idx = this.name.lastIndexOf(46);
			return idx >= 0 ? this.name.substring(idx + 1) : "";
		}

		public String getPath() {
			return this.fsm.getPath(this.obj);
		}

		public boolean equals(Object o) {
			if(o != null && this.getClass() == o.getClass()) {
				FileTable.Entry that = (FileTable.Entry)o;
				return this.fsm == that.fsm && this.fsm.equals(this.obj, that.obj);
			} else {
				return false;
			}
		}

		public int hashCode() {
			return this.obj != null ? this.obj.hashCode() : 203;
		}
	}

	static class ExtensionComparator implements Comparator {
		static final FileTable.ExtensionComparator instance = new FileTable.ExtensionComparator();

		public int compare(FileTable.Entry o1, FileTable.Entry o2) {
			return NaturalSortComparator.naturalCompare(o1.getExtension(), o2.getExtension());
		}

		public int compare(Object object1, Object object2) {
			return this.compare((FileTable.Entry)object1, (FileTable.Entry)object2);
		}
	}

	private static class FileFilterWrapper implements FileSystemModel.FileFilter {
		private final FileSystemModel.FileFilter base;
		private final boolean showFolder;
		private final boolean showHidden;

		public FileFilterWrapper(FileSystemModel.FileFilter base, boolean showFolder, boolean showHidden) {
			this.base = base;
			this.showFolder = showFolder;
			this.showHidden = showHidden;
		}

		public boolean accept(FileSystemModel fsm, Object file) {
			return !this.showHidden && fsm.isHidden(file) ? false : (fsm.isFolder(file) ? this.showFolder : this.base == null || this.base.accept(fsm, file));
		}
	}

	static class FileTableModel extends AbstractTableModel {
		private final DateFormat dateFormat = DateFormat.getDateInstance();
		FileTable.Entry[] entries = FileTable.EMPTY;
		int numFolders;
		static String[] COLUMN_HEADER = new String[]{"File name", "Type", "Size", "Last modified"};
		static String[] SIZE_UNITS = new String[]{" MB", " KB", " B"};
		static long[] SIZE_FACTORS = new long[]{1048576L, 1024L, 1L};

		public void setData(FileTable.Entry[] entries, int numFolders) {
			this.fireRowsDeleted(0, this.getNumRows());
			this.entries = entries;
			this.numFolders = numFolders;
			this.fireRowsInserted(0, this.getNumRows());
		}

		public String getColumnHeaderText(int column) {
			return COLUMN_HEADER[column];
		}

		public int getNumColumns() {
			return COLUMN_HEADER.length;
		}

		public Object getCell(int row, int column) {
			FileTable.Entry e = this.entries[row];
			if(e.isFolder) {
				switch(column) {
				case 0:
					return "[" + e.name + "]";
				case 1:
					return "Folder";
				case 2:
					return "";
				case 3:
					return this.formatDate(e.lastModified);
				default:
					return "??";
				}
			} else {
				switch(column) {
				case 0:
					return e.name;
				case 1:
					String ext = e.getExtension();
					return ext.length() == 0 ? "File" : ext + "-file";
				case 2:
					return this.formatFileSize(e.size);
				case 3:
					return this.formatDate(e.lastModified);
				default:
					return "??";
				}
			}
		}

		public Object getTooltipContent(int row, int column) {
			FileTable.Entry e = this.entries[row];
			StringBuilder sb = new StringBuilder(e.name);
			if(!e.isFolder) {
				sb.append("\nSize: ").append(this.formatFileSize(e.size));
			}

			if(e.lastModified != null) {
				sb.append("\nLast modified: ").append(this.formatDate(e.lastModified));
			}

			return sb.toString();
		}

		public int getNumRows() {
			return this.entries.length;
		}

		FileTable.Entry getEntry(int row) {
			return row >= 0 && row < this.entries.length ? this.entries[row] : null;
		}

		int findEntry(FileTable.Entry entry) {
			for(int i = 0; i < this.entries.length; ++i) {
				if(this.entries[i].equals(entry)) {
					return i;
				}
			}

			return -1;
		}

		int findFile(Object file) {
			for(int i = 0; i < this.entries.length; ++i) {
				FileTable.Entry e = this.entries[i];
				if(e.fsm.equals(e.obj, file)) {
					return i;
				}
			}

			return -1;
		}

		FileTable.Entry[] getEntries(int[] selection) {
			int count = selection.length;
			if(count == 0) {
				return FileTable.EMPTY;
			} else {
				FileTable.Entry[] result = new FileTable.Entry[count];

				for(int i = 0; i < count; ++i) {
					result[i] = this.entries[selection[i]];
				}

				return result;
			}
		}

		private String formatFileSize(long size) {
			if(size <= 0L) {
				return "0 B";
			} else {
				int i;
				for(i = 0; size < SIZE_FACTORS[i]; ++i) {
				}

				long value = size * 10L / SIZE_FACTORS[i];
				return Long.toString(value / 10L) + '.' + Character.forDigit((int)(value % 10L), 10) + SIZE_UNITS[i];
			}
		}

		private String formatDate(Date date) {
			return date == null ? "" : this.dateFormat.format(date);
		}
	}

	static class LastModifiedComparator implements Comparator {
		static final FileTable.LastModifiedComparator instance = new FileTable.LastModifiedComparator();

		public int compare(FileTable.Entry o1, FileTable.Entry o2) {
			Date lm1 = o1.lastModified;
			Date lm2 = o2.lastModified;
			return lm1 != null && lm2 != null ? lm1.compareTo(lm2) : (lm1 != null ? 1 : (lm2 != null ? -1 : 0));
		}

		public int compare(Object object1, Object object2) {
			return this.compare((FileTable.Entry)object1, (FileTable.Entry)object2);
		}
	}

	static class NameComparator implements Comparator {
		static final FileTable.NameComparator instance = new FileTable.NameComparator();

		public int compare(FileTable.Entry o1, FileTable.Entry o2) {
			return NaturalSortComparator.naturalCompare(o1.name, o2.name);
		}

		public int compare(Object object1, Object object2) {
			return this.compare((FileTable.Entry)object1, (FileTable.Entry)object2);
		}
	}

	static class SizeComparator implements Comparator {
		static final FileTable.SizeComparator instance = new FileTable.SizeComparator();

		public int compare(FileTable.Entry o1, FileTable.Entry o2) {
			return Long.signum(o1.size - o2.size);
		}

		public int compare(Object object1, Object object2) {
			return this.compare((FileTable.Entry)object1, (FileTable.Entry)object2);
		}
	}

	public static enum SortColumn {
		NAME(FileTable.NameComparator.instance),
		TYPE(FileTable.ExtensionComparator.instance),
		SIZE(FileTable.SizeComparator.instance),
		LAST_MODIFIED(FileTable.LastModifiedComparator.instance);

		final Comparator comparator;

		private SortColumn(Comparator comparator) {
			this.comparator = comparator;
		}
	}

	static class StateSnapshot {
		final FileTable.Entry leadEntry;
		final FileTable.Entry anchorEntry;
		final FileTable.Entry[] selected;

		StateSnapshot(FileTable.Entry leadEntry, FileTable.Entry anchorEntry, FileTable.Entry[] selected) {
			this.leadEntry = leadEntry;
			this.anchorEntry = anchorEntry;
			this.selected = selected;
		}
	}
}
