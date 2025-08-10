package de.matthiasmann.twl.model;

public interface TreeTableNode {
	Object getData(int i1);

	Object getTooltipContent(int i1);

	TreeTableNode getParent();

	boolean isLeaf();

	int getNumChildren();

	TreeTableNode getChild(int i1);

	int getChildIndex(TreeTableNode treeTableNode1);
}
