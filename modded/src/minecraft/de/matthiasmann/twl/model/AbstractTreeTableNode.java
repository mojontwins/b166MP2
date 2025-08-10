package de.matthiasmann.twl.model;

import java.util.ArrayList;

public abstract class AbstractTreeTableNode implements TreeTableNode {
	private final TreeTableNode parent;
	private ArrayList children;
	private boolean leaf;

	protected AbstractTreeTableNode(TreeTableNode parent) {
		if(parent == null) {
			throw new NullPointerException("parent");
		} else {
			this.parent = parent;

			assert this.getTreeTableModel() != null;

		}
	}

	public Object getTooltipContent(int column) {
		return null;
	}

	public final TreeTableNode getParent() {
		return this.parent;
	}

	public boolean isLeaf() {
		return this.leaf;
	}

	public int getNumChildren() {
		return this.children != null ? this.children.size() : 0;
	}

	public TreeTableNode getChild(int idx) {
		return (TreeTableNode)this.children.get(idx);
	}

	public int getChildIndex(TreeTableNode child) {
		if(this.children != null) {
			int i = 0;

			for(int n = this.children.size(); i < n; ++i) {
				if(this.children.get(i) == child) {
					return i;
				}
			}
		}

		return -1;
	}

	protected void setLeaf(boolean leaf) {
		if(this.leaf != leaf) {
			this.leaf = leaf;
			this.fireNodeChanged();
		}

	}

	protected void insertChild(TreeTableNode node, int idx) {
		assert this.getChildIndex(node) < 0;

		assert node.getParent() == this;

		if(this.children == null) {
			this.children = new ArrayList();
		}

		this.children.add(idx, node);
		this.getTreeTableModel().fireNodesAdded(this, idx, 1);
	}

	protected void removeChild(int idx) {
		this.children.remove(idx);
		this.getTreeTableModel().fireNodesRemoved(this, idx, 1);
	}

	protected void removeAllChildren() {
		if(this.children != null) {
			int count = this.children.size();
			this.children.clear();
			this.getTreeTableModel().fireNodesRemoved(this, 0, count);
		}

	}

	protected AbstractTreeTableModel getTreeTableModel() {
		TreeTableNode n = this.parent;

		while(true) {
			TreeTableNode p = n.getParent();
			if(p == null) {
				return (AbstractTreeTableModel)n;
			}

			n = p;
		}
	}

	protected void fireNodeChanged() {
		int selfIdxInParent = this.parent.getChildIndex(this);
		if(selfIdxInParent >= 0) {
			this.getTreeTableModel().fireNodesChanged(this.parent, selfIdxInParent, 1);
		}

	}
}
