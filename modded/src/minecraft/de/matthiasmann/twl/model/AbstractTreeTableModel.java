package de.matthiasmann.twl.model;

import de.matthiasmann.twl.utils.CallbackSupport;

import java.util.ArrayList;

public abstract class AbstractTreeTableModel extends AbstractTableColumnHeaderModel implements TreeTableModel {
	private final ArrayList children = new ArrayList();
	private TreeTableModel.ChangeListener[] callbacks;

	public void addChangeListener(TreeTableModel.ChangeListener listener) {
		this.callbacks = (TreeTableModel.ChangeListener[])CallbackSupport.addCallbackToList(this.callbacks, listener, TreeTableModel.ChangeListener.class);
	}

	public void removeChangeListener(TreeTableModel.ChangeListener listener) {
		this.callbacks = (TreeTableModel.ChangeListener[])CallbackSupport.removeCallbackFromList(this.callbacks, listener);
	}

	public Object getData(int column) {
		return null;
	}

	public Object getTooltipContent(int column) {
		return null;
	}

	public final TreeTableNode getParent() {
		return null;
	}

	public boolean isLeaf() {
		return false;
	}

	public int getNumChildren() {
		return this.children.size();
	}

	public TreeTableNode getChild(int idx) {
		return (TreeTableNode)this.children.get(idx);
	}

	public int getChildIndex(TreeTableNode child) {
		int i = 0;

		for(int n = this.children.size(); i < n; ++i) {
			if(this.children.get(i) == child) {
				return i;
			}
		}

		return -1;
	}

	protected void insertChild(TreeTableNode node, int idx) {
		assert this.getChildIndex(node) < 0;

		assert node.getParent() == this;

		this.children.add(idx, node);
		this.fireNodesAdded(this, idx, 1);
	}

	protected void removeChild(int idx) {
		this.children.remove(idx);
		this.fireNodesRemoved(this, idx, 1);
	}

	protected void removeAllChildren() {
		int count = this.children.size();
		if(count > 0) {
			this.children.clear();
			this.fireNodesRemoved(this, 0, count);
		}

	}

	protected void fireNodesAdded(TreeTableNode parent, int idx, int count) {
		if(this.callbacks != null) {
			TreeTableModel.ChangeListener[] treeTableModel$ChangeListener7 = this.callbacks;
			int i6 = this.callbacks.length;

			for(int i5 = 0; i5 < i6; ++i5) {
				TreeTableModel.ChangeListener cl = treeTableModel$ChangeListener7[i5];
				cl.nodesAdded(parent, idx, count);
			}
		}

	}

	protected void fireNodesRemoved(TreeTableNode parent, int idx, int count) {
		if(this.callbacks != null) {
			TreeTableModel.ChangeListener[] treeTableModel$ChangeListener7 = this.callbacks;
			int i6 = this.callbacks.length;

			for(int i5 = 0; i5 < i6; ++i5) {
				TreeTableModel.ChangeListener cl = treeTableModel$ChangeListener7[i5];
				cl.nodesRemoved(parent, idx, count);
			}
		}

	}

	protected void fireNodesChanged(TreeTableNode parent, int idx, int count) {
		if(this.callbacks != null) {
			TreeTableModel.ChangeListener[] treeTableModel$ChangeListener7 = this.callbacks;
			int i6 = this.callbacks.length;

			for(int i5 = 0; i5 < i6; ++i5) {
				TreeTableModel.ChangeListener cl = treeTableModel$ChangeListener7[i5];
				cl.nodesChanged(parent, idx, count);
			}
		}

	}

	protected void fireColumnInserted(int idx, int count) {
		if(this.callbacks != null) {
			TreeTableModel.ChangeListener[] treeTableModel$ChangeListener6 = this.callbacks;
			int i5 = this.callbacks.length;

			for(int i4 = 0; i4 < i5; ++i4) {
				TreeTableModel.ChangeListener cl = treeTableModel$ChangeListener6[i4];
				cl.columnInserted(idx, count);
			}
		}

	}

	protected void fireColumnDeleted(int idx, int count) {
		if(this.callbacks != null) {
			TreeTableModel.ChangeListener[] treeTableModel$ChangeListener6 = this.callbacks;
			int i5 = this.callbacks.length;

			for(int i4 = 0; i4 < i5; ++i4) {
				TreeTableModel.ChangeListener cl = treeTableModel$ChangeListener6[i4];
				cl.columnDeleted(idx, count);
			}
		}

	}

	protected void fireColumnHeaderChanged(int column) {
		if(this.callbacks != null) {
			TreeTableModel.ChangeListener[] treeTableModel$ChangeListener5 = this.callbacks;
			int i4 = this.callbacks.length;

			for(int i3 = 0; i3 < i4; ++i3) {
				TreeTableModel.ChangeListener cl = treeTableModel$ChangeListener5[i3];
				cl.columnHeaderChanged(column);
			}
		}

	}
}
