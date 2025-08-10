package de.matthiasmann.twl;

import de.matthiasmann.twl.model.AutoCompletionDataSource;
import de.matthiasmann.twl.model.BitfieldBooleanModel;
import de.matthiasmann.twl.model.FileSystemAutoCompletionDataSource;
import de.matthiasmann.twl.model.FileSystemModel;
import de.matthiasmann.twl.model.FileSystemTreeModel;
import de.matthiasmann.twl.model.IntegerModel;
import de.matthiasmann.twl.model.MRUListModel;
import de.matthiasmann.twl.model.PersistentIntegerModel;
import de.matthiasmann.twl.model.PersistentMRUListModel;
import de.matthiasmann.twl.model.SimpleIntegerModel;
import de.matthiasmann.twl.model.SimpleListModel;
import de.matthiasmann.twl.model.SimpleMRUListModel;
import de.matthiasmann.twl.model.ToggleButtonModel;
import de.matthiasmann.twl.model.TreeTableModel;
import de.matthiasmann.twl.model.TreeTableNode;
import de.matthiasmann.twl.utils.CallbackSupport;
import de.matthiasmann.twl.utils.NaturalSortComparator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.prefs.Preferences;

public class FileSelector extends DialogLayout {
	public static final FileSelector.NamedFileFilter AllFilesFilter = new FileSelector.NamedFileFilter("All files", (FileSystemModel.FileFilter)null);
	private final IntegerModel flags;
	private final MRUListModel folderMRU;
	final MRUListModel filesMRU;
	private final TreeComboBox currentFolder;
	private final Label labelCurrentFolder;
	private final FileTable fileTable;
	private final ScrollPane fileTableSP;
	private final Button btnUp;
	private final Button btnHome;
	private final Button btnFolderMRU;
	private final Button btnFilesMRU;
	private final Button btnOk;
	private final Button btnCancel;
	private final Button btnRefresh;
	private final Button btnShowFolders;
	private final Button btnShowHidden;
	private final ComboBox fileFilterBox;
	private final FileSelector.FileFiltersModel fileFiltersModel;
	private final EditFieldAutoCompletionWindow autoCompletion;
	private boolean allowFolderSelection;
	private FileSelector.Callback[] callbacks;
	private FileSelector.NamedFileFilter activeFileFilter;
	FileSystemModel fsm;
	private FileSystemTreeModel model;
	private Widget userWidgetBottom;
	private Widget userWidgetRight;
	private Object fileToSelectOnSetCurrentNode;

	public FileSelector() {
		this((Preferences)null, (String)null);
	}

	public FileSelector(Preferences prefs, String prefsKey) {
		if(prefs == null != (prefsKey == null)) {
			throw new IllegalArgumentException("\'prefs\' and \'prefsKey\' must both be valid or both null");
		} else {
			if(prefs != null) {
				this.flags = new PersistentIntegerModel(prefs, prefsKey.concat("_Flags"), 0, 65535, 0);
				this.folderMRU = new PersistentMRUListModel(10, String.class, prefs, prefsKey.concat("_foldersMRU"));
				this.filesMRU = new PersistentMRUListModel(20, String.class, prefs, prefsKey.concat("_filesMRU"));
			} else {
				this.flags = new SimpleIntegerModel(0, 65535, 0);
				this.folderMRU = new SimpleMRUListModel(10);
				this.filesMRU = new SimpleMRUListModel(20);
			}

			this.currentFolder = new TreeComboBox();
			this.currentFolder.setTheme("currentFolder");
			this.fileTable = new FileTable();
			this.fileTable.setTheme("fileTable");
			this.fileTable.addCallback(new FileTable.Callback() {
				public void selectionChanged() {
					FileSelector.this.selectionChanged();
				}

				public void sortingChanged() {
				}
			});
			this.btnUp = new Button();
			this.btnUp.setTheme("buttonUp");
			this.btnUp.addCallback(new Runnable() {
				public void run() {
					FileSelector.this.goOneLevelUp();
				}
			});
			this.btnHome = new Button();
			this.btnHome.setTheme("buttonHome");
			this.btnHome.addCallback(new Runnable() {
				public void run() {
					FileSelector.this.goHome();
				}
			});
			this.btnFolderMRU = new Button();
			this.btnFolderMRU.setTheme("buttonFoldersMRU");
			this.btnFolderMRU.addCallback(new Runnable() {
				public void run() {
					FileSelector.this.showFolderMRU();
				}
			});
			this.btnFilesMRU = new Button();
			this.btnFilesMRU.setTheme("buttonFilesMRU");
			this.btnFilesMRU.addCallback(new Runnable() {
				public void run() {
					FileSelector.this.showFilesMRU();
				}
			});
			this.btnOk = new Button();
			this.btnOk.setTheme("buttonOk");
			this.btnOk.addCallback(new Runnable() {
				public void run() {
					FileSelector.this.acceptSelection();
				}
			});
			this.btnCancel = new Button();
			this.btnCancel.setTheme("buttonCancel");
			this.btnCancel.addCallback(new Runnable() {
				public void run() {
					FileSelector.this.fireCanceled();
				}
			});
			this.currentFolder.setPathResolver(new TreeComboBox.PathResolver() {
				public TreeTableNode resolvePath(TreeTableModel model, String path) throws IllegalArgumentException {
					return FileSelector.this.resolvePath(path);
				}
			});
			this.currentFolder.addCallback(new TreeComboBox.Callback() {
				public void selectedNodeChanged(TreeTableNode node, TreeTableNode previousChildNode) {
					FileSelector.this.setCurrentNode(node, previousChildNode);
				}
			});
			this.autoCompletion = new EditFieldAutoCompletionWindow(this.currentFolder.getEditField());
			this.autoCompletion.setUseInvokeAsync(true);
			this.currentFolder.getEditField().setAutoCompletionWindow(this.autoCompletion);
			this.fileTable.setAllowMultiSelection(true);
			this.fileTable.addCallback(new TableBase.Callback() {
				public void mouseDoubleClicked(int row, int column) {
					FileSelector.this.acceptSelection();
				}

				public void mouseRightClick(int row, int column, Event evt) {
				}

				public void columnHeaderClicked(int column) {
				}
			});
			this.activeFileFilter = AllFilesFilter;
			this.fileFiltersModel = new FileSelector.FileFiltersModel();
			this.fileFilterBox = new ComboBox(this.fileFiltersModel);
			this.fileFilterBox.setTheme("fileFiltersBox");
			this.fileFilterBox.setComputeWidthFromModel(true);
			this.fileFilterBox.setVisible(false);
			this.fileFilterBox.addCallback(new Runnable() {
				public void run() {
					FileSelector.this.fileFilterChanged();
				}
			});
			this.labelCurrentFolder = new Label("Folder");
			this.labelCurrentFolder.setLabelFor(this.currentFolder);
			this.fileTableSP = new ScrollPane(this.fileTable);
			Runnable showBtnCallback = new Runnable() {
				public void run() {
					FileSelector.this.refreshFileTable();
				}
			};
			this.btnRefresh = new Button();
			this.btnRefresh.setTheme("buttonRefresh");
			this.btnRefresh.addCallback(showBtnCallback);
			this.btnShowFolders = new Button(new ToggleButtonModel(new BitfieldBooleanModel(this.flags, 0), true));
			this.btnShowFolders.setTheme("buttonShowFolders");
			this.btnShowFolders.addCallback(showBtnCallback);
			this.btnShowHidden = new Button(new ToggleButtonModel(new BitfieldBooleanModel(this.flags, 1), false));
			this.btnShowHidden.setTheme("buttonShowHidden");
			this.btnShowHidden.addCallback(showBtnCallback);
			this.addActionMapping("goOneLevelUp", "goOneLevelUp", new Object[0]);
			this.addActionMapping("acceptSelection", "acceptSelection", new Object[0]);
		}
	}

	protected void createLayout() {
		this.setHorizontalGroup((DialogLayout.Group)null);
		this.setVerticalGroup((DialogLayout.Group)null);
		this.removeAllChildren();
		this.add(this.fileTableSP);
		this.add(this.fileFilterBox);
		this.add(this.btnOk);
		this.add(this.btnCancel);
		this.add(this.btnRefresh);
		this.add(this.btnShowFolders);
		this.add(this.btnShowHidden);
		this.add(this.labelCurrentFolder);
		this.add(this.currentFolder);
		this.add(this.btnFolderMRU);
		this.add(this.btnUp);
		DialogLayout.Group hCurrentFolder = this.createSequentialGroup().addWidget(this.labelCurrentFolder).addWidget(this.currentFolder).addWidget(this.btnFolderMRU).addWidget(this.btnUp).addWidget(this.btnHome);
		DialogLayout.Group vCurrentFolder = this.createParallelGroup().addWidget(this.labelCurrentFolder).addWidget(this.currentFolder).addWidget(this.btnFolderMRU).addWidget(this.btnUp).addWidget(this.btnHome);
		DialogLayout.Group hButtonGroup = this.createSequentialGroup().addWidget(this.btnRefresh).addGap(-2).addWidget(this.btnShowFolders).addWidget(this.btnShowHidden).addWidget(this.fileFilterBox).addGap("buttonBarLeft").addWidget(this.btnFilesMRU).addGap("buttonBarSpacer").addWidget(this.btnOk).addGap("buttonBarSpacer").addWidget(this.btnCancel).addGap("buttonBarRight");
		DialogLayout.Group vButtonGroup = this.createParallelGroup().addWidget(this.btnRefresh).addWidget(this.btnShowFolders).addWidget(this.btnShowHidden).addWidget(this.fileFilterBox).addWidget(this.btnFilesMRU).addWidget(this.btnOk).addWidget(this.btnCancel);
		DialogLayout.Group horz = this.createParallelGroup().addGroup(hCurrentFolder).addWidget(this.fileTableSP);
		DialogLayout.Group vert = this.createSequentialGroup().addGroup(vCurrentFolder).addWidget(this.fileTableSP);
		if(this.userWidgetBottom != null) {
			horz.addWidget(this.userWidgetBottom);
			vert.addWidget(this.userWidgetBottom);
		}

		if(this.userWidgetRight != null) {
			horz = this.createParallelGroup().addGroup(this.createSequentialGroup().addGroup(horz).addWidget(this.userWidgetRight));
			vert = this.createSequentialGroup().addGroup(this.createParallelGroup().addGroup(vert).addWidget(this.userWidgetRight));
		}

		this.setHorizontalGroup(horz.addGroup(hButtonGroup));
		this.setVerticalGroup(vert.addGroup(vButtonGroup));
	}

	protected void afterAddToGUI(GUI gui) {
		super.afterAddToGUI(gui);
		this.createLayout();
	}

	public FileSystemModel getFileSystemModel() {
		return this.fsm;
	}

	public void setFileSystemModel(FileSystemModel fsm) {
		this.fsm = fsm;
		if(fsm == null) {
			this.model = null;
			this.currentFolder.setModel((TreeTableModel)null);
			this.fileTable.setCurrentFolder((FileSystemModel)null, (Object)null);
			this.autoCompletion.setDataSource((AutoCompletionDataSource)null);
		} else {
			this.model = new FileSystemTreeModel(fsm);
			this.model.setSorter(new FileSelector.NameSorter(fsm));
			this.currentFolder.setModel(this.model);
			this.currentFolder.setSeparator(fsm.getSeparator());
			this.autoCompletion.setDataSource(new FileSystemAutoCompletionDataSource(fsm, FileSystemTreeModel.FolderFilter.instance));
			if(this.folderMRU.getNumEntries() == 0 || !this.gotoFolderFromMRU(0) || !this.goHome()) {
				this.setCurrentNode(this.model);
			}
		}

	}

	public boolean getAllowMultiSelection() {
		return this.fileTable.getAllowMultiSelection();
	}

	public void setAllowMultiSelection(boolean allowMultiSelection) {
		this.fileTable.setAllowMultiSelection(allowMultiSelection);
	}

	public boolean getAllowFolderSelection() {
		return this.allowFolderSelection;
	}

	public void setAllowFolderSelection(boolean allowFolderSelection) {
		this.allowFolderSelection = allowFolderSelection;
		this.selectionChanged();
	}

	public void addCallback(FileSelector.Callback callback) {
		this.callbacks = (FileSelector.Callback[])CallbackSupport.addCallbackToList(this.callbacks, callback, FileSelector.Callback.class);
	}

	public void removeCallback(FileSelector.Callback callback) {
		this.callbacks = (FileSelector.Callback[])CallbackSupport.removeCallbackFromList(this.callbacks, callback);
	}

	public Widget getUserWidgetBottom() {
		return this.userWidgetBottom;
	}

	public void setUserWidgetBottom(Widget userWidgetBottom) {
		this.userWidgetBottom = userWidgetBottom;
		this.createLayout();
	}

	public Widget getUserWidgetRight() {
		return this.userWidgetRight;
	}

	public void setUserWidgetRight(Widget userWidgetRight) {
		this.userWidgetRight = userWidgetRight;
		this.createLayout();
	}

	public FileTable getFileTable() {
		return this.fileTable;
	}

	public void setOkButtonEnabled(boolean enabled) {
		this.btnOk.setEnabled(enabled);
	}

	public Object getCurrentFolder() {
		TreeTableNode node = this.currentFolder.getCurrentNode();
		return node instanceof FileSystemTreeModel.FolderNode ? ((FileSystemTreeModel.FolderNode)node).getFolder() : null;
	}

	public boolean setCurrentFolder(Object folder) {
		FileSystemTreeModel.FolderNode node = this.model.getNodeForFolder(folder);
		if(node != null) {
			this.setCurrentNode(node);
			return true;
		} else {
			return false;
		}
	}

	public boolean selectFile(Object file) {
		if(this.fsm == null) {
			return false;
		} else {
			Object parent = this.fsm.getParent(file);
			return this.setCurrentFolder(parent) ? this.fileTable.setSelection(file) : false;
		}
	}

	public void addFileFilter(FileSelector.NamedFileFilter filter) {
		if(filter == null) {
			throw new NullPointerException("filter");
		} else {
			this.fileFiltersModel.addFileFilter(filter);
			this.fileFilterBox.setVisible(this.fileFiltersModel.getNumEntries() > 0);
			if(this.fileFilterBox.getSelected() < 0) {
				this.fileFilterBox.setSelected(0);
			}

		}
	}

	public void removeFileFilter(FileSelector.NamedFileFilter filter) {
		if(filter == null) {
			throw new NullPointerException("filter");
		} else {
			this.fileFiltersModel.removeFileFilter(filter);
			if(this.fileFiltersModel.getNumEntries() == 0) {
				this.fileFilterBox.setVisible(false);
				this.setFileFilter(AllFilesFilter);
			}

		}
	}

	public void removeAllFileFilters() {
		this.fileFiltersModel.removeAll();
		this.fileFilterBox.setVisible(false);
		this.setFileFilter(AllFilesFilter);
	}

	public void setFileFilter(FileSelector.NamedFileFilter filter) {
		if(filter == null) {
			throw new NullPointerException("filter");
		} else {
			int idx = this.fileFiltersModel.findFilter(filter);
			if(idx < 0) {
				throw new IllegalArgumentException("filter not registered");
			} else {
				this.fileFilterBox.setSelected(idx);
			}
		}
	}

	public FileSelector.NamedFileFilter getFileFilter() {
		return this.activeFileFilter;
	}

	public boolean getShowFolders() {
		return this.btnShowFolders.getModel().isSelected();
	}

	public void setShowFolders(boolean showFolders) {
		this.btnShowFolders.getModel().setSelected(showFolders);
	}

	public boolean getShowHidden() {
		return this.btnShowHidden.getModel().isSelected();
	}

	public void setShowHidden(boolean showHidden) {
		this.btnShowHidden.getModel().setSelected(showHidden);
	}

	public void goOneLevelUp() {
		TreeTableNode node = this.currentFolder.getCurrentNode();
		TreeTableNode parent = node.getParent();
		if(parent != null) {
			this.setCurrentNode(parent, node);
		}

	}

	public boolean goHome() {
		if(this.fsm != null) {
			Object folder = this.fsm.getSpecialFolder("user.home");
			if(folder != null) {
				return this.setCurrentFolder(folder);
			}
		}

		return false;
	}

	public void acceptSelection() {
		FileTable.Entry[] selection = this.fileTable.getSelection();
		if(selection.length == 1) {
			FileTable.Entry entry = selection[0];
			if(entry != null && entry.isFolder) {
				this.setCurrentFolder(entry.obj);
				return;
			}
		}

		this.fireAcceptCallback(selection);
	}

	void fileFilterChanged() {
		int idx = this.fileFilterBox.getSelected();
		if(idx >= 0) {
			FileSelector.NamedFileFilter filter = this.fileFiltersModel.getFileFilter(idx);
			this.activeFileFilter = filter;
			this.fileTable.setFileFilter(filter.getFileFilter());
		}

	}

	void fireAcceptCallback(FileTable.Entry[] selection) {
		if(this.callbacks != null) {
			Object[] objects = new Object[selection.length];

			for(int cb = 0; cb < selection.length; ++cb) {
				FileTable.Entry e = selection[cb];
				if(e.isFolder && !this.allowFolderSelection) {
					return;
				}

				objects[cb] = e.obj;
			}

			this.addToMRU(selection);
			FileSelector.Callback[] fileSelector$Callback6 = this.callbacks;
			int i5 = this.callbacks.length;

			for(int i8 = 0; i8 < i5; ++i8) {
				FileSelector.Callback fileSelector$Callback7 = fileSelector$Callback6[i8];
				fileSelector$Callback7.filesSelected(objects);
			}
		}

	}

	void fireCanceled() {
		if(this.callbacks != null) {
			FileSelector.Callback[] fileSelector$Callback4 = this.callbacks;
			int i3 = this.callbacks.length;

			for(int i2 = 0; i2 < i3; ++i2) {
				FileSelector.Callback cb = fileSelector$Callback4[i2];
				cb.canceled();
			}
		}

	}

	void selectionChanged() {
		boolean foldersSelected = false;
		boolean filesSelected = false;
		FileTable.Entry[] selection = this.fileTable.getSelection();
		FileTable.Entry[] fileTable$Entry7 = selection;
		int i6 = selection.length;

		int i5;
		for(i5 = 0; i5 < i6; ++i5) {
			FileTable.Entry cb = fileTable$Entry7[i5];
			if(cb.isFolder) {
				foldersSelected = true;
			} else {
				filesSelected = true;
			}
		}

		if(this.allowFolderSelection) {
			this.btnOk.setEnabled(filesSelected || foldersSelected);
		} else {
			this.btnOk.setEnabled(filesSelected && !foldersSelected);
		}

		if(this.callbacks != null) {
			FileSelector.Callback[] fileSelector$Callback9 = this.callbacks;
			i6 = this.callbacks.length;

			for(i5 = 0; i5 < i6; ++i5) {
				FileSelector.Callback fileSelector$Callback8 = fileSelector$Callback9[i5];
				if(fileSelector$Callback8 instanceof FileSelector.Callback2) {
					((FileSelector.Callback2)fileSelector$Callback8).selectionChanged(selection);
				}
			}
		}

	}

	protected void setCurrentNode(TreeTableNode node, TreeTableNode childToSelect) {
		if(childToSelect instanceof FileSystemTreeModel.FolderNode) {
			this.fileToSelectOnSetCurrentNode = ((FileSystemTreeModel.FolderNode)childToSelect).getFolder();
		}

		this.setCurrentNode(node);
	}

	protected void setCurrentNode(TreeTableNode node) {
		this.currentFolder.setCurrentNode(node);
		this.refreshFileTable();
		if(this.callbacks != null) {
			Object curFolder = this.getCurrentFolder();
			FileSelector.Callback[] fileSelector$Callback6 = this.callbacks;
			int i5 = this.callbacks.length;

			for(int i4 = 0; i4 < i5; ++i4) {
				FileSelector.Callback cb = fileSelector$Callback6[i4];
				if(cb instanceof FileSelector.Callback2) {
					((FileSelector.Callback2)cb).folderChanged(curFolder);
				}
			}
		}

		if(this.fileToSelectOnSetCurrentNode != null) {
			this.fileTable.setSelection(this.fileToSelectOnSetCurrentNode);
			this.fileToSelectOnSetCurrentNode = null;
		}

	}

	void refreshFileTable() {
		this.fileTable.setShowFolders(this.btnShowFolders.getModel().isSelected());
		this.fileTable.setShowHidden(this.btnShowHidden.getModel().isSelected());
		this.fileTable.setCurrentFolder(this.fsm, this.getCurrentFolder());
	}

	TreeTableNode resolvePath(String path) throws IllegalArgumentException {
		Object obj = this.fsm.getFile(path);
		this.fileToSelectOnSetCurrentNode = null;
		if(obj != null) {
			if(this.fsm.isFile(obj)) {
				this.fileToSelectOnSetCurrentNode = obj;
				obj = this.fsm.getParent(obj);
			}

			FileSystemTreeModel.FolderNode node = this.model.getNodeForFolder(obj);
			if(node != null) {
				return node;
			}
		}

		throw new IllegalArgumentException("Could not resolve: " + path);
	}

	void showFolderMRU() {
		final PopupWindow popup = new PopupWindow(this);
		final ListBox listBox = new ListBox(this.folderMRU);
		popup.setTheme("fileselector-folderMRUpopup");
		popup.add(listBox);
		if(popup.openPopup()) {
			popup.setInnerSize(this.getInnerWidth() * 2 / 3, this.getInnerHeight() * 2 / 3);
			popup.setPosition(this.btnFolderMRU.getX() - popup.getWidth(), this.btnFolderMRU.getY());
			listBox.addCallback(new CallbackWithReason() {
				public void callback(ListBox.CallbackReason reason) {
					if(reason.actionRequested()) {
						popup.closePopup();
						int idx = listBox.getSelected();
						if(idx >= 0) {
							FileSelector.this.gotoFolderFromMRU(idx);
						}
					}

				}

				public void callback(Enum enum1) {
					this.callback((ListBox.CallbackReason)enum1);
				}
			});
		}

	}

	void showFilesMRU() {
		final PopupWindow popup = new PopupWindow(this);
		DialogLayout layout = new DialogLayout();
		final ListBox listBox = new ListBox(this.filesMRU);
		Button popupBtnOk = new Button();
		Button popupBtnCancel = new Button();
		popupBtnOk.setTheme("buttonOk");
		popupBtnCancel.setTheme("buttonCancel");
		popup.setTheme("fileselector-filesMRUpopup");
		popup.add(layout);
		layout.add(listBox);
		layout.add(popupBtnOk);
		layout.add(popupBtnCancel);
		DialogLayout.Group hBtnGroup = layout.createSequentialGroup().addGap().addWidget(popupBtnOk).addWidget(popupBtnCancel);
		DialogLayout.Group vBtnGroup = layout.createParallelGroup().addWidget(popupBtnOk).addWidget(popupBtnCancel);
		layout.setHorizontalGroup(layout.createParallelGroup().addWidget(listBox).addGroup(hBtnGroup));
		layout.setVerticalGroup(layout.createSequentialGroup().addWidget(listBox).addGroup(vBtnGroup));
		if(popup.openPopup()) {
			popup.setInnerSize(this.getInnerWidth() * 2 / 3, this.getInnerHeight() * 2 / 3);
			popup.setPosition(this.getInnerX() + (this.getInnerWidth() - popup.getWidth()) / 2, this.btnFilesMRU.getY() - popup.getHeight());
			final Runnable okCB = new Runnable() {
				public void run() {
					int idx = listBox.getSelected();
					if(idx >= 0) {
						Object obj = FileSelector.this.fsm.getFile((String)FileSelector.this.filesMRU.getEntry(idx));
						if(obj != null) {
							popup.closePopup();
							FileSelector.this.fireAcceptCallback(new FileTable.Entry[]{new FileTable.Entry(FileSelector.this.fsm, obj, FileSelector.this.fsm.getParent(obj) == null)});
						} else {
							FileSelector.this.filesMRU.removeEntry(idx);
						}
					}

				}
			};
			popupBtnOk.addCallback(okCB);
			popupBtnCancel.addCallback(new Runnable() {
				public void run() {
					popup.closePopup();
				}
			});
			listBox.addCallback(new CallbackWithReason() {
				public void callback(ListBox.CallbackReason reason) {
					if(reason.actionRequested()) {
						okCB.run();
					}

				}

				public void callback(Enum enum1) {
					this.callback((ListBox.CallbackReason)enum1);
				}
			});
		}

	}

	private void addToMRU(FileTable.Entry[] selection) {
		FileTable.Entry[] fileTable$Entry5 = selection;
		int i4 = selection.length;

		for(int i3 = 0; i3 < i4; ++i3) {
			FileTable.Entry entry = fileTable$Entry5[i3];
			this.filesMRU.addEntry(entry.getPath());
		}

		this.folderMRU.addEntry(this.fsm.getPath(this.getCurrentFolder()));
	}

	boolean gotoFolderFromMRU(int idx) {
		String path = (String)this.folderMRU.getEntry(idx);

		try {
			TreeTableNode ex = this.resolvePath(path);
			this.setCurrentNode(ex);
			return true;
		} catch (IllegalArgumentException illegalArgumentException4) {
			this.folderMRU.removeEntry(idx);
			return false;
		}
	}

	public interface Callback {
		void filesSelected(Object[] object1);

		void canceled();
	}

	public interface Callback2 extends FileSelector.Callback {
		void folderChanged(Object object1);

		void selectionChanged(FileTable.Entry[] fileTable$Entry1);
	}

	static class FileFiltersModel extends SimpleListModel {
		private final ArrayList filters = new ArrayList();

		public FileSelector.NamedFileFilter getFileFilter(int index) {
			return (FileSelector.NamedFileFilter)this.filters.get(index);
		}

		public String getEntry(int index) {
			FileSelector.NamedFileFilter filter = this.getFileFilter(index);
			return filter.getDisplayName();
		}

		public int getNumEntries() {
			return this.filters.size();
		}

		public void addFileFilter(FileSelector.NamedFileFilter filter) {
			int index = this.filters.size();
			this.filters.add(filter);
			this.fireEntriesInserted(index, index);
		}

		public void removeFileFilter(FileSelector.NamedFileFilter filter) {
			int idx = this.filters.indexOf(filter);
			if(idx >= 0) {
				this.filters.remove(idx);
				this.fireEntriesDeleted(idx, idx);
			}

		}

		public int findFilter(FileSelector.NamedFileFilter filter) {
			return this.filters.indexOf(filter);
		}

		void removeAll() {
			this.filters.clear();
			this.fireAllChanged();
		}

		public Object getEntry(int i1) {
			return this.getEntry(i1);
		}
	}

	public static class NameSorter implements Comparator {
		private final FileSystemModel fsm;
		private final Comparator nameComparator;

		public NameSorter(FileSystemModel fsm) {
			this.fsm = fsm;
			this.nameComparator = NaturalSortComparator.stringComparator;
		}

		public NameSorter(FileSystemModel fsm, Comparator nameComparator) {
			this.fsm = fsm;
			this.nameComparator = nameComparator;
		}

		public int compare(Object o1, Object o2) {
			return this.nameComparator.compare(this.fsm.getName(o1), this.fsm.getName(o2));
		}
	}

	public static class NamedFileFilter {
		private final String name;
		private final FileSystemModel.FileFilter fileFilter;

		public NamedFileFilter(String name, FileSystemModel.FileFilter fileFilter) {
			this.name = name;
			this.fileFilter = fileFilter;
		}

		public String getDisplayName() {
			return this.name;
		}

		public FileSystemModel.FileFilter getFileFilter() {
			return this.fileFilter;
		}
	}
}
