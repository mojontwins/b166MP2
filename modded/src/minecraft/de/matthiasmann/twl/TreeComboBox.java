package de.matthiasmann.twl;

import de.matthiasmann.twl.model.TableSingleSelectionModel;
import de.matthiasmann.twl.model.TreeTableModel;
import de.matthiasmann.twl.model.TreeTableNode;
import de.matthiasmann.twl.utils.CallbackSupport;

public class TreeComboBox extends ComboBoxBase {
	private static final String DEFAULT_POPUP_THEME = "treecomboboxPopup";
	final TableSingleSelectionModel selectionModel;
	final TreePathDisplay display;
	final TreeTable table;
	private TreeTableModel model;
	private TreeComboBox.Callback[] callbacks;
	private TreeComboBox.PathResolver pathResolver;
	private boolean suppressCallback;
	boolean suppressTreeSelectionUpdating;

	public TreeComboBox() {
		this.selectionModel = new TableSingleSelectionModel();
		this.display = new TreePathDisplay();
		this.display.setTheme("display");
		this.table = new TreeTable();
		this.table.setSelectionManager(new TableRowSelectionManager(this.selectionModel) {
			protected boolean handleMouseClick(int row, int column, boolean isShift, boolean isCtrl) {
				if(!isShift && !isCtrl && row >= 0 && row < this.getNumRows()) {
					TreeComboBox.this.popup.closePopup();
					return true;
				} else {
					return super.handleMouseClick(row, column, isShift, isCtrl);
				}
			}
		});
		this.display.addCallback(new TreePathDisplay.Callback() {
			public void pathElementClicked(TreeTableNode node, TreeTableNode child) {
				TreeComboBox.this.fireSelectedNodeChanged(node, child);
			}

			public boolean resolvePath(String path) {
				return TreeComboBox.this.resolvePath(path);
			}
		});
		this.selectionModel.addSelectionChangeListener(new Runnable() {
			public void run() {
				int row = TreeComboBox.this.selectionModel.getFirstSelected();
				if(row >= 0) {
					TreeComboBox.this.suppressTreeSelectionUpdating = true;

					try {
						TreeComboBox.this.nodeChanged(TreeComboBox.this.table.getNodeFromRow(row));
					} finally {
						TreeComboBox.this.suppressTreeSelectionUpdating = false;
					}
				}

			}
		});
		ScrollPane scrollPane = new ScrollPane(this.table);
		scrollPane.setFixed(ScrollPane.Fixed.HORIZONTAL);
		this.add(this.display);
		this.popup.setTheme("treecomboboxPopup");
		this.popup.add(scrollPane);
	}

	public TreeComboBox(TreeTableModel model) {
		this();
		this.setModel(model);
	}

	public TreeTableModel getModel() {
		return this.model;
	}

	public void setModel(TreeTableModel model) {
		if(this.model != model) {
			this.model = model;
			this.table.setModel(model);
			this.display.setCurrentNode(model);
		}

	}

	public void setCurrentNode(TreeTableNode node) {
		if(node == null) {
			throw new NullPointerException("node");
		} else {
			this.display.setCurrentNode(node);
			if(this.popup.isOpen()) {
				this.tableSelectToCurrentNode();
			}

		}
	}

	public TreeTableNode getCurrentNode() {
		return this.display.getCurrentNode();
	}

	public void setSeparator(String separator) {
		this.display.setSeparator(separator);
	}

	public String getSeparator() {
		return this.display.getSeparator();
	}

	public TreeComboBox.PathResolver getPathResolver() {
		return this.pathResolver;
	}

	public void setPathResolver(TreeComboBox.PathResolver pathResolver) {
		this.pathResolver = pathResolver;
		this.display.setAllowEdit(pathResolver != null);
	}

	public TreeTable getTreeTable() {
		return this.table;
	}

	public EditField getEditField() {
		return this.display.getEditField();
	}

	public void addCallback(TreeComboBox.Callback callback) {
		this.callbacks = (TreeComboBox.Callback[])CallbackSupport.addCallbackToList(this.callbacks, callback, TreeComboBox.Callback.class);
	}

	public void removeCallback(TreeComboBox.Callback callback) {
		this.callbacks = (TreeComboBox.Callback[])CallbackSupport.removeCallbackFromList(this.callbacks, callback);
	}

	protected void applyTheme(ThemeInfo themeInfo) {
		super.applyTheme(themeInfo);
		this.applyTreeComboboxPopupThemeName(themeInfo);
	}

	protected void applyTreeComboboxPopupThemeName(ThemeInfo themeInfo) {
		this.popup.setTheme(themeInfo.getParameter("popupThemeName", "treecomboboxPopup"));
	}

	protected Widget getLabel() {
		return this.display;
	}

	void fireSelectedNodeChanged(TreeTableNode node, TreeTableNode child) {
		if(this.callbacks != null) {
			TreeComboBox.Callback[] treeComboBox$Callback6 = this.callbacks;
			int i5 = this.callbacks.length;

			for(int i4 = 0; i4 < i5; ++i4) {
				TreeComboBox.Callback cb = treeComboBox$Callback6[i4];
				cb.selectedNodeChanged(node, child);
			}
		}

	}

	boolean resolvePath(String path) {
		if(this.pathResolver != null) {
			try {
				TreeTableNode ex = this.pathResolver.resolvePath(this.model, path);

				assert ex != null;

				this.nodeChanged(ex);
				return true;
			} catch (IllegalArgumentException illegalArgumentException3) {
				this.display.setEditErrorMessage(illegalArgumentException3.getMessage());
			}
		}

		return false;
	}

	void nodeChanged(TreeTableNode node) {
		TreeTableNode oldNode = this.display.getCurrentNode();
		this.display.setCurrentNode(node);
		if(!this.suppressCallback) {
			this.fireSelectedNodeChanged(node, this.getChildOf(node, oldNode));
		}

	}

	private TreeTableNode getChildOf(TreeTableNode parent, TreeTableNode node) {
		while(node != null && node != parent) {
			node = node.getParent();
		}

		return node;
	}

	private void tableSelectToCurrentNode() {
		if(!this.suppressTreeSelectionUpdating) {
			this.table.collapseAll();
			int idx = this.table.getRowFromNodeExpand(this.display.getCurrentNode());
			this.suppressCallback = true;

			try {
				this.selectionModel.setSelection(idx, idx);
			} finally {
				this.suppressCallback = false;
			}

			this.table.scrollToRow(Math.max(0, idx));
		}

	}

	protected boolean openPopup() {
		if(super.openPopup()) {
			this.popup.validateLayout();
			this.tableSelectToCurrentNode();
			return true;
		} else {
			return false;
		}
	}

	public interface Callback {
		void selectedNodeChanged(TreeTableNode treeTableNode1, TreeTableNode treeTableNode2);
	}

	public interface PathResolver {
		TreeTableNode resolvePath(TreeTableModel treeTableModel1, String string2) throws IllegalArgumentException;
	}
}
