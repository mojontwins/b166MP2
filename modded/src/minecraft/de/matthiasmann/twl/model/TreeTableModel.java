package de.matthiasmann.twl.model;

public interface TreeTableModel extends TableColumnHeaderModel, TreeTableNode {
	void addChangeListener(TreeTableModel.ChangeListener treeTableModel$ChangeListener1);

	void removeChangeListener(TreeTableModel.ChangeListener treeTableModel$ChangeListener1);

	public interface ChangeListener extends TableColumnHeaderModel.ColumnHeaderChangeListener {
		void nodesAdded(TreeTableNode treeTableNode1, int i2, int i3);

		void nodesRemoved(TreeTableNode treeTableNode1, int i2, int i3);

		void nodesChanged(TreeTableNode treeTableNode1, int i2, int i3);
	}
}
