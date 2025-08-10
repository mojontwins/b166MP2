package de.matthiasmann.twl;

import de.matthiasmann.twl.model.FileSystemModel;
import de.matthiasmann.twl.model.FileSystemTreeModel;
import de.matthiasmann.twl.model.JavaFileSystemModel;
import de.matthiasmann.twl.model.SimpleListModel;
import de.matthiasmann.twl.utils.CallbackSupport;
import de.matthiasmann.twl.utils.NaturalSortComparator;

import java.util.Arrays;
import java.util.Comparator;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FolderBrowser extends Widget {
	final FileSystemModel fsm;
	final ListBox listbox;
	final FolderBrowser.FolderModel model;
	private final BoxLayout curFolderGroup;
	private Runnable[] selectionChangedCallbacks;
	Comparator folderComparator;
	private Object currentFolder;
	private Runnable[] callbacks;

	public FolderBrowser() {
		this(JavaFileSystemModel.getInstance());
	}

	public FolderBrowser(FileSystemModel fsm) {
		if(fsm == null) {
			throw new NullPointerException("fsm");
		} else {
			this.fsm = fsm;
			this.model = new FolderBrowser.FolderModel();
			this.listbox = new ListBox(this.model);
			this.curFolderGroup = new BoxLayout();
			this.curFolderGroup.setTheme("currentpathbox");
			this.curFolderGroup.setScroll(true);
			this.curFolderGroup.setClip(true);
			this.curFolderGroup.setAlignment(Alignment.BOTTOM);
			this.listbox.addCallback(new CallbackWithReason() {
				private Object lastSelection;

				public void callback(ListBox.CallbackReason reason) {
					if(FolderBrowser.this.listbox.getSelected() != -1 && reason.actionRequested()) {
						FolderBrowser.this.setCurrentFolder(FolderBrowser.this.model.getFolder(FolderBrowser.this.listbox.getSelected()));
					}

					Object selection = FolderBrowser.this.getSelectedFolder();
					if(selection != this.lastSelection) {
						this.lastSelection = selection;
						FolderBrowser.this.fireSelectionChangedCallback();
					}

				}

				public void callback(Enum enum1) {
					this.callback((ListBox.CallbackReason)enum1);
				}
			});
			this.add(this.listbox);
			this.add(this.curFolderGroup);
			this.setCurrentFolder((Object)null);
		}
	}

	public void addCallback(Runnable cb) {
		this.callbacks = (Runnable[])CallbackSupport.addCallbackToList(this.callbacks, cb, Runnable.class);
	}

	public void removeCallback(Runnable cb) {
		this.callbacks = (Runnable[])CallbackSupport.removeCallbackFromList(this.callbacks, cb);
	}

	protected void doCallback() {
		CallbackSupport.fireCallbacks(this.callbacks);
	}

	public Comparator getFolderComparator() {
		return this.folderComparator;
	}

	public void setFolderComparator(Comparator folderComparator) {
		this.folderComparator = folderComparator;
	}

	public FileSystemModel getFileSystemModel() {
		return this.fsm;
	}

	public Object getCurrentFolder() {
		return this.currentFolder;
	}

	public boolean setCurrentFolder(Object folder) {
		if(this.model.listFolders(folder)) {
			if(folder == null && this.model.getNumEntries() == 1 && this.setCurrentFolder(this.model.getFolder(0))) {
				return true;
			} else {
				this.currentFolder = folder;
				this.listbox.setSelected(-1);
				this.rebuildCurrentFolderGroup();
				this.doCallback();
				return true;
			}
		} else {
			return false;
		}
	}

	public boolean goToParentFolder() {
		if(this.currentFolder != null) {
			Object current = this.currentFolder;
			if(this.setCurrentFolder(this.fsm.getParent(current))) {
				this.selectFolder(current);
				return true;
			}
		}

		return false;
	}

	public Object getSelectedFolder() {
		return this.listbox.getSelected() != -1 ? this.model.getFolder(this.listbox.getSelected()) : null;
	}

	public boolean selectFolder(Object current) {
		int idx = this.model.findFolder(current);
		this.listbox.setSelected(idx);
		return idx != -1;
	}

	public void addSelectionChangedCallback(Runnable cb) {
		this.callbacks = (Runnable[])CallbackSupport.addCallbackToList(this.selectionChangedCallbacks, cb, Runnable.class);
	}

	public void removeSelectionChangedCallback(Runnable cb) {
		this.selectionChangedCallbacks = (Runnable[])CallbackSupport.removeCallbackFromList(this.selectionChangedCallbacks, cb);
	}

	protected void fireSelectionChangedCallback() {
		CallbackSupport.fireCallbacks(this.selectionChangedCallbacks);
	}

	public boolean handleEvent(Event evt) {
		if(evt.isKeyPressedEvent()) {
			switch(evt.getKeyCode()) {
			case 14:
				this.goToParentFolder();
				return true;
			}
		}

		return super.handleEvent(evt);
	}

	private void rebuildCurrentFolderGroup() {
		this.curFolderGroup.removeAllChildren();
		this.recursiveAddFolder(this.currentFolder, (Object)null);
	}

	private void recursiveAddFolder(final Object folder, final Object subFolder) {
		if(folder != null) {
			this.recursiveAddFolder(this.fsm.getParent(folder), folder);
		}

		if(this.curFolderGroup.getNumChildren() > 0) {
			Label name = new Label(this.fsm.getSeparator());
			name.setTheme("pathseparator");
			this.curFolderGroup.add(name);
		}

		String name1 = this.getFolderName(folder);
		if(name1.endsWith(this.fsm.getSeparator())) {
			name1 = name1.substring(0, name1.length() - 1);
		}

		Button btn = new Button(name1);
		btn.addCallback(new Runnable() {
			public void run() {
				if(FolderBrowser.this.setCurrentFolder(folder)) {
					FolderBrowser.this.selectFolder(subFolder);
				}

				FolderBrowser.this.listbox.requestKeyboardFocus();
			}
		});
		btn.setTheme("pathbutton");
		this.curFolderGroup.add(btn);
	}

	public void adjustSize() {
	}

	protected void layout() {
		this.curFolderGroup.setPosition(this.getInnerX(), this.getInnerY());
		this.curFolderGroup.setSize(this.getInnerWidth(), this.curFolderGroup.getHeight());
		this.listbox.setPosition(this.getInnerX(), this.curFolderGroup.getBottom());
		this.listbox.setSize(this.getInnerWidth(), Math.max(0, this.getInnerBottom() - this.listbox.getY()));
	}

	String getFolderName(Object folder) {
		return folder != null ? this.fsm.getName(folder) : "ROOT";
	}

	class FolderModel extends SimpleListModel {
		private Object[] folders = new Object[0];

		public boolean listFolders(Object parent) {
			Object[] newFolders;
			if(parent == null) {
				newFolders = FolderBrowser.this.fsm.listRoots();
			} else {
				newFolders = FolderBrowser.this.fsm.listFolder(parent, FileSystemTreeModel.FolderFilter.instance);
			}

			if(newFolders == null) {
				Logger.getLogger(FolderBrowser.FolderModel.class.getName()).log(Level.WARNING, "can\'\'t list folder: {0}", parent);
				return false;
			} else {
				Arrays.sort(newFolders, new FileSelector.NameSorter(FolderBrowser.this.fsm, FolderBrowser.this.folderComparator != null ? FolderBrowser.this.folderComparator : NaturalSortComparator.stringComparator));
				this.folders = newFolders;
				this.fireAllChanged();
				return true;
			}
		}

		public int getNumEntries() {
			return this.folders.length;
		}

		public Object getFolder(int index) {
			return this.folders[index];
		}

		public Object getEntry(int index) {
			Object folder = this.getFolder(index);
			return FolderBrowser.this.getFolderName(folder);
		}

		public int findFolder(Object folder) {
			int idx = FolderBrowser.this.fsm.find(this.folders, folder);
			return idx < 0 ? -1 : idx;
		}
	}
}
