package de.matthiasmann.twl;

import de.matthiasmann.twl.model.BooleanModel;
import de.matthiasmann.twl.model.ToggleButtonModel;
import de.matthiasmann.twl.model.TreeTableModel;
import de.matthiasmann.twl.model.TreeTableNode;
import de.matthiasmann.twl.utils.CallbackSupport;
import de.matthiasmann.twl.utils.HashEntry;
import de.matthiasmann.twl.utils.SizeSequence;

public class TreeTable extends TableBase {
	private final TreeTable.ModelChangeListener modelChangeListener;
	private final TreeTable.TreeLeafCellRenderer leafRenderer;
	private final TreeTable.TreeNodeCellRenderer nodeRenderer;
	private TreeTable.NodeState[] nodeStateTable;
	private int nodeStateTableSize;
	TreeTableModel model;
	private TreeTable.NodeState rootNodeState;

	public TreeTable() {
		this.modelChangeListener = new TreeTable.ModelChangeListener();
		this.nodeStateTable = new TreeTable.NodeState[64];
		this.leafRenderer = new TreeTable.TreeLeafCellRenderer();
		this.nodeRenderer = new TreeTable.TreeNodeCellRenderer();
		this.hasCellWidgetCreators = true;
		ActionMap am = this.getOrCreateActionMap();
		am.addMapping("expandLeadRow", (Object)this, (String)"setLeadRowExpanded", new Object[]{Boolean.TRUE}, 1);
		am.addMapping("collapseLeadRow", (Object)this, (String)"setLeadRowExpanded", new Object[]{Boolean.FALSE}, 1);
	}

	public TreeTable(TreeTableModel model) {
		this();
		this.setModel(model);
	}

	public void setModel(TreeTableModel model) {
		if(this.model != null) {
			this.model.removeChangeListener(this.modelChangeListener);
		}

		this.columnHeaderModel = model;
		this.model = model;
		this.nodeStateTable = new TreeTable.NodeState[64];
		this.nodeStateTableSize = 0;
		if(this.model != null) {
			this.model.addChangeListener(this.modelChangeListener);
			this.rootNodeState = this.createNodeState(model);
			this.rootNodeState.level = -1;
			this.rootNodeState.expanded = true;
			this.rootNodeState.initChildSizes();
			this.numRows = this.computeNumRows();
			this.numColumns = model.getNumColumns();
		} else {
			this.rootNodeState = null;
			this.numRows = 0;
			this.numColumns = 0;
		}

		this.modelAllChanged();
		this.invalidateLayout();
	}

	protected void applyTheme(ThemeInfo themeInfo) {
		super.applyTheme(themeInfo);
		this.applyThemeTreeTable(themeInfo);
	}

	protected void applyThemeTreeTable(ThemeInfo themeInfo) {
		this.applyCellRendererTheme(this.leafRenderer);
		this.applyCellRendererTheme(this.nodeRenderer);
	}

	public int getRowFromNode(TreeTableNode node) {
		int position = -1;

		for(TreeTableNode parent = node.getParent(); parent != null; parent = parent.getParent()) {
			TreeTable.NodeState ns = (TreeTable.NodeState)HashEntry.get(this.nodeStateTable, parent);
			if(ns == null) {
				return -1;
			}

			int idx = parent.getChildIndex(node);
			if(idx < 0) {
				return -1;
			}

			if(ns.childSizes == null) {
				if(!ns.expanded) {
					return -1;
				}

				ns.initChildSizes();
			}

			idx = ns.childSizes.getPosition(idx);
			position += idx + 1;
			node = parent;
		}

		return position;
	}

	public int getRowFromNodeExpand(TreeTableNode node) {
		if(node.getParent() != null) {
			TreeTableNode parent = node.getParent();
			int row = this.getRowFromNodeExpand(parent);
			int idx = parent.getChildIndex(node);
			TreeTable.NodeState ns = (TreeTable.NodeState)HashEntry.get(this.nodeStateTable, parent);
			ns.setValue(true);
			if(ns.childSizes == null) {
				ns.initChildSizes();
			}

			return row + 1 + ns.childSizes.getPosition(idx);
		} else {
			return -1;
		}
	}

	public TreeTableNode getNodeFromRow(int row) {
		TreeTable.NodeState ns = this.rootNodeState;

		while(true) {
			int idx;
			if(ns.childSizes == null) {
				idx = Math.min(((TreeTableNode)ns.key).getNumChildren() - 1, row);
				row -= idx + 1;
			} else {
				idx = ns.childSizes.getIndex(row);
				row -= ns.childSizes.getPosition(idx) + 1;
			}

			if(row < 0) {
				return ((TreeTableNode)ns.key).getChild(idx);
			}

			assert ns.children[idx] != null;

			ns = ns.children[idx];
		}
	}

	public void collapseAll() {
		for(int i = 0; i < this.nodeStateTable.length; ++i) {
			for(TreeTable.NodeState ns = this.nodeStateTable[i]; ns != null; ns = (TreeTable.NodeState)ns.next()) {
				if(ns != this.rootNodeState) {
					ns.setValue(false);
				}
			}
		}

	}

	public boolean isRowExpanded(int row) {
		this.checkRowIndex(row);
		TreeTableNode node = this.getNodeFromRow(row);
		TreeTable.NodeState ns = (TreeTable.NodeState)HashEntry.get(this.nodeStateTable, node);
		return ns != null && ns.expanded;
	}

	public void setRowExpanded(int row, boolean expanded) {
		this.checkRowIndex(row);
		TreeTableNode node = this.getNodeFromRow(row);
		TreeTable.NodeState state = this.getOrCreateNodeState(node);
		state.setValue(expanded);
	}

	public void setLeadRowExpanded(boolean expanded) {
		TableSelectionManager sm = this.getSelectionManager();
		if(sm != null) {
			int row = sm.getLeadRow();
			if(row >= 0 && row < this.numRows) {
				this.setRowExpanded(row, expanded);
			}
		}

	}

	protected TreeTable.NodeState getOrCreateNodeState(TreeTableNode node) {
		TreeTable.NodeState ns = (TreeTable.NodeState)HashEntry.get(this.nodeStateTable, node);
		if(ns == null) {
			ns = this.createNodeState(node);
		}

		return ns;
	}

	protected TreeTable.NodeState createNodeState(TreeTableNode node) {
		TreeTableNode parent = node.getParent();
		TreeTable.NodeState nsParent = null;
		if(parent != null) {
			nsParent = (TreeTable.NodeState)HashEntry.get(this.nodeStateTable, parent);

			assert nsParent != null;
		}

		TreeTable.NodeState newNS = new TreeTable.NodeState(node, nsParent);
		this.nodeStateTable = (TreeTable.NodeState[])HashEntry.maybeResizeTable(this.nodeStateTable, ++this.nodeStateTableSize);
		HashEntry.insertEntry(this.nodeStateTable, newNS);
		return newNS;
	}

	protected void expandedChanged(TreeTable.NodeState ns) {
		TreeTableNode node = (TreeTableNode)ns.key;
		int count = ns.getChildRows();
		int size = ns.expanded ? count : 0;

		for(TreeTableNode parent = node.getParent(); parent != null; parent = parent.getParent()) {
			TreeTable.NodeState row = (TreeTable.NodeState)HashEntry.get(this.nodeStateTable, parent);
			if(row.childSizes == null) {
				row.initChildSizes();
			}

			int scrollPane = ((TreeTableNode)row.key).getChildIndex(node);
			row.childSizes.setSize(scrollPane, size + 1);
			size = row.childSizes.getEndPosition();
			node = parent;
		}

		this.numRows = this.computeNumRows();
		int row1 = this.getRowFromNode((TreeTableNode)ns.key);
		if(ns.expanded) {
			this.modelRowsInserted(row1 + 1, count);
		} else {
			this.modelRowsDeleted(row1 + 1, count);
		}

		this.modelRowsChanged(row1, 1);
		if(ns.expanded) {
			ScrollPane scrollPane1 = ScrollPane.getContainingScrollPane(this);
			if(scrollPane1 != null) {
				scrollPane1.validateLayout();
				int rowStart = this.getRowStartPosition(row1);
				int rowEnd = this.getRowEndPosition(row1 + count);
				int height = rowEnd - rowStart;
				scrollPane1.scrollToAreaY(rowStart, height, this.rowHeight / 2);
			}
		}

	}

	protected int computeNumRows() {
		return this.rootNodeState.childSizes.getEndPosition();
	}

	protected Object getCellData(int row, int column, TreeTableNode node) {
		if(node == null) {
			node = this.getNodeFromRow(row);
		}

		return node.getData(column);
	}

	protected TableBase.CellRenderer getCellRenderer(int row, int col, TreeTableNode node) {
		if(node == null) {
			node = this.getNodeFromRow(row);
		}

		if(col == 0) {
			Object data = node.getData(col);
			if(node.isLeaf()) {
				this.leafRenderer.setCellData(row, col, data, node);
				return this.leafRenderer;
			} else {
				TreeTable.NodeState nodeState = this.getOrCreateNodeState(node);
				this.nodeRenderer.setCellData(row, col, data, nodeState);
				return this.nodeRenderer;
			}
		} else {
			return super.getCellRenderer(row, col, node);
		}
	}

	protected Object getTooltipContentFromRow(int row, int column) {
		TreeTableNode node = this.getNodeFromRow(row);
		return node != null ? node.getTooltipContent(column) : null;
	}

	private boolean updateParentSizes(TreeTable.NodeState ns) {
		while(ns.expanded && ns.parent != null) {
			TreeTable.NodeState parent = ns.parent;
			int idx = ((TreeTableNode)parent.key).getChildIndex((TreeTableNode)ns.key);

			assert parent.childSizes.size() == ((TreeTableNode)parent.key).getNumChildren();

			parent.childSizes.setSize(idx, ns.getChildRows() + 1);
			ns = parent;
		}

		this.numRows = this.computeNumRows();
		if(ns.parent == null) {
			return true;
		} else {
			return false;
		}
	}

	protected void modelNodesAdded(TreeTableNode parent, int idx, int count) {
		TreeTable.NodeState ns = (TreeTable.NodeState)HashEntry.get(this.nodeStateTable, parent);
		if(ns != null) {
			if(ns.childSizes != null) {
				assert idx <= ns.childSizes.size();

				ns.childSizes.insert(idx, count);

				assert ns.childSizes.size() == parent.getNumChildren();
			}

			if(ns.children != null) {
				TreeTable.NodeState[] row = new TreeTable.NodeState[parent.getNumChildren()];
				System.arraycopy(ns.children, 0, row, 0, idx);
				System.arraycopy(ns.children, idx, row, idx + count, ns.children.length - idx);
				ns.children = row;
			}

			if(this.updateParentSizes(ns)) {
				int row1 = this.getRowFromNode(parent.getChild(idx));

				assert row1 < this.numRows;

				this.modelRowsInserted(row1, count);
			}
		}

	}

	protected void recursiveRemove(TreeTable.NodeState ns) {
		if(ns != null) {
			--this.nodeStateTableSize;
			HashEntry.remove(this.nodeStateTable, (HashEntry)ns);
			if(ns.children != null) {
				TreeTable.NodeState[] treeTable$NodeState5 = ns.children;
				int i4 = ns.children.length;

				for(int i3 = 0; i3 < i4; ++i3) {
					TreeTable.NodeState nsChild = treeTable$NodeState5[i3];
					this.recursiveRemove(nsChild);
				}
			}
		}

	}

	protected void modelNodesRemoved(TreeTableNode parent, int idx, int count) {
		TreeTable.NodeState ns = (TreeTable.NodeState)HashEntry.get(this.nodeStateTable, parent);
		if(ns != null) {
			int rowsBase = this.getRowFromNode(parent) + 1;
			int rowsStart = rowsBase + idx;
			int rowsEnd = rowsBase + idx + count;
			if(ns.childSizes != null) {
				assert ns.childSizes.size() == parent.getNumChildren() + count;

				rowsStart = rowsBase + ns.childSizes.getPosition(idx);
				rowsEnd = rowsBase + ns.childSizes.getPosition(idx + count);
				ns.childSizes.remove(idx, count);

				assert ns.childSizes.size() == parent.getNumChildren();
			}

			if(ns.children != null) {
				int numChildren;
				for(numChildren = 0; numChildren < count; ++numChildren) {
					this.recursiveRemove(ns.children[idx + numChildren]);
				}

				numChildren = parent.getNumChildren();
				if(numChildren > 0) {
					TreeTable.NodeState[] newChilds = new TreeTable.NodeState[numChildren];
					System.arraycopy(ns.children, 0, newChilds, 0, idx);
					System.arraycopy(ns.children, idx + count, newChilds, idx, newChilds.length - idx);
					ns.children = newChilds;
				} else {
					ns.children = null;
				}
			}

			if(this.updateParentSizes(ns)) {
				this.modelRowsDeleted(rowsStart, rowsEnd - rowsStart);
			}
		}

	}

	protected boolean isVisible(TreeTable.NodeState ns) {
		while(ns.expanded && ns.parent != null) {
			ns = ns.parent;
		}

		return ns.expanded;
	}

	protected void modelNodesChanged(TreeTableNode parent, int idx, int count) {
		TreeTable.NodeState ns = (TreeTable.NodeState)HashEntry.get(this.nodeStateTable, parent);
		if(ns != null && this.isVisible(ns)) {
			int rowsBase = this.getRowFromNode(parent) + 1;
			int rowsStart = rowsBase + idx;
			int rowsEnd = rowsBase + idx + count;
			if(ns.childSizes != null) {
				rowsStart = rowsBase + ns.childSizes.getPosition(idx);
				rowsEnd = rowsBase + ns.childSizes.getPosition(idx + count);
			}

			this.modelRowsChanged(rowsStart, rowsEnd - rowsStart);
		}

	}

	static int getLevel(TreeTableNode node) {
		int level;
		for(level = -2; node != null; node = node.getParent()) {
			++level;
		}

		return level;
	}

	protected class ModelChangeListener implements TreeTableModel.ChangeListener {
		public void nodesAdded(TreeTableNode parent, int idx, int count) {
			TreeTable.this.modelNodesAdded(parent, idx, count);
		}

		public void nodesRemoved(TreeTableNode parent, int idx, int count) {
			TreeTable.this.modelNodesRemoved(parent, idx, count);
		}

		public void nodesChanged(TreeTableNode parent, int idx, int count) {
			TreeTable.this.modelNodesChanged(parent, idx, count);
		}

		public void columnInserted(int idx, int count) {
			TreeTable.this.numColumns = TreeTable.this.model.getNumColumns();
			TreeTable.this.modelColumnsInserted(idx, count);
		}

		public void columnDeleted(int idx, int count) {
			TreeTable.this.numColumns = TreeTable.this.model.getNumColumns();
			TreeTable.this.modelColumnsDeleted(idx, count);
		}

		public void columnHeaderChanged(int column) {
			TreeTable.this.modelColumnHeaderChanged(column);
		}
	}

	protected class NodeState extends HashEntry implements BooleanModel {
		final TreeTable.NodeState parent;
		boolean expanded;
		boolean hasNoChildren;
		SizeSequence childSizes;
		TreeTable.NodeState[] children;
		Runnable[] callbacks;
		int level;

		public NodeState(TreeTableNode key, TreeTable.NodeState parent) {
			super(key);
			this.parent = parent;
			this.level = parent != null ? parent.level + 1 : 0;
			if(parent != null) {
				if(parent.children == null) {
					parent.children = new TreeTable.NodeState[((TreeTableNode)parent.key).getNumChildren()];
				}

				parent.children[((TreeTableNode)parent.key).getChildIndex(key)] = this;
			}

		}

		public void addCallback(Runnable callback) {
			this.callbacks = (Runnable[])CallbackSupport.addCallbackToList(this.callbacks, callback, Runnable.class);
		}

		public void removeCallback(Runnable callback) {
			this.callbacks = (Runnable[])CallbackSupport.removeCallbackFromList(this.callbacks, callback);
		}

		public boolean getValue() {
			return this.expanded;
		}

		public void setValue(boolean value) {
			if(this.expanded != value) {
				this.expanded = value;
				TreeTable.this.expandedChanged(this);
				CallbackSupport.fireCallbacks(this.callbacks);
			}

		}

		void initChildSizes() {
			this.childSizes = new SizeSequence();
			this.childSizes.setDefaultValue(1);
			this.childSizes.initializeAll(((TreeTableNode)this.key).getNumChildren());
		}

		int getChildRows() {
			if(this.childSizes != null) {
				return this.childSizes.getEndPosition();
			} else {
				int childCount = ((TreeTableNode)this.key).getNumChildren();
				this.hasNoChildren = childCount == 0;
				return childCount;
			}
		}

		boolean hasNoChildren() {
			return this.hasNoChildren;
		}
	}

	class TreeLeafCellRenderer implements TableBase.CellRenderer {
		protected int treeIndent;
		protected int level;
		protected Dimension treeButtonSize = new Dimension(5, 5);
		protected TableBase.CellRenderer subRenderer;

		public TreeLeafCellRenderer() {
			TreeTable.this.setClip(true);
		}

		public void applyTheme(ThemeInfo themeInfo) {
			this.treeIndent = themeInfo.getParameter("treeIndent", 10);
			this.treeButtonSize = (Dimension)themeInfo.getParameterValue("treeButtonSize", true, Dimension.class, Dimension.ZERO);
		}

		public String getTheme() {
			return this.getClass().getSimpleName();
		}

		public void setCellData(int row, int column, Object data) {
			throw new UnsupportedOperationException("Don\'t call this method");
		}

		public void setCellData(int row, int column, Object data, TreeTableNode node) {
			this.level = TreeTable.getLevel(node);
			this.setSubRenderer(data);
		}

		protected int getIndentation() {
			return this.level * this.treeIndent + this.treeButtonSize.getX();
		}

		protected void setSubRenderer(Object colData) {
			this.subRenderer = TreeTable.this.getCellRenderer(colData);
			if(this.subRenderer != null) {
				this.subRenderer.setCellData(this.level, TreeTable.this.numColumns, colData);
			}

		}

		public int getColumnSpan() {
			return this.subRenderer != null ? this.subRenderer.getColumnSpan() : 1;
		}

		public int getPreferredHeight() {
			return this.subRenderer != null ? Math.max(this.treeButtonSize.getY(), this.subRenderer.getPreferredHeight()) : this.treeButtonSize.getY();
		}

		public Widget getCellRenderWidget(int x, int y, int width, int height, boolean isSelected) {
			if(this.subRenderer != null) {
				int indent = this.getIndentation();
				Widget widget = this.subRenderer.getCellRenderWidget(x + indent, y, Math.max(0, width - indent), height, isSelected);
				return widget;
			} else {
				return null;
			}
		}
	}

	class TreeNodeCellRenderer extends TreeTable.TreeLeafCellRenderer implements TableBase.CellWidgetCreator {
		private TreeTable.NodeState nodeState;

		TreeNodeCellRenderer() {
			super();
		}

		public Widget updateWidget(Widget existingWidget) {
			if(this.nodeState.hasNoChildren()) {
				return null;
			} else {
				ToggleButton tb = (ToggleButton)existingWidget;
				if(tb == null) {
					tb = new ToggleButton();
					tb.setTheme("treeButton");
				}

				((ToggleButtonModel)tb.getModel()).setModel(this.nodeState);
				return tb;
			}
		}

		public void positionWidget(Widget widget, int x, int y, int w, int h) {
			int indent = this.level * this.treeIndent;
			int availWidth = Math.max(0, w - indent);
			widget.setPosition(x + indent, y + (h - this.treeButtonSize.getY()) / 2);
			widget.setSize(Math.min(availWidth, this.treeButtonSize.getX()), this.treeButtonSize.getY());
		}

		public void setCellData(int row, int column, Object data, TreeTable.NodeState nodeState) {
			assert nodeState != null;

			this.nodeState = nodeState;
			this.setSubRenderer(data);
			this.level = nodeState.level;
		}
	}
}
