package de.matthiasmann.twl.model;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;

public class FileSystemTreeModel extends AbstractTreeTableModel {
	private final FileSystemModel fsm;
	private final boolean includeLastModified;
	protected Comparator sorter;
	static final FileSystemTreeModel.FolderNode[] NO_CHILDREN = new FileSystemTreeModel.FolderNode[0];

	public FileSystemTreeModel(FileSystemModel fsm, boolean includeLastModified) {
		this.fsm = fsm;
		this.includeLastModified = includeLastModified;
		this.insertRoots();
	}

	public FileSystemTreeModel(FileSystemModel fsm) {
		this(fsm, false);
	}

	public int getNumColumns() {
		return this.includeLastModified ? 2 : 1;
	}

	public String getColumnHeaderText(int column) {
		switch(column) {
		case 0:
			return "Folder";
		case 1:
			return "Last modified";
		default:
			return "";
		}
	}

	public FileSystemModel getFileSystemModel() {
		return this.fsm;
	}

	public void insertRoots() {
		this.removeAllChildren();
		Object[] object4;
		int i3 = (object4 = this.fsm.listRoots()).length;

		for(int i2 = 0; i2 < i3; ++i2) {
			Object root = object4[i2];
			this.insertChild(new FileSystemTreeModel.FolderNode(this, this.fsm, root), this.getNumChildren());
		}

	}

	public FileSystemTreeModel.FolderNode getNodeForFolder(Object obj) {
		Object parent = this.fsm.getParent(obj);
		Object parentNode;
		if(parent == null) {
			parentNode = this;
		} else {
			parentNode = this.getNodeForFolder(parent);
		}

		if(parentNode != null) {
			for(int i = 0; i < ((TreeTableNode)parentNode).getNumChildren(); ++i) {
				FileSystemTreeModel.FolderNode node = (FileSystemTreeModel.FolderNode)((TreeTableNode)parentNode).getChild(i);
				if(this.fsm.equals(node.folder, obj)) {
					return node;
				}
			}
		}

		return null;
	}

	public Comparator getSorter() {
		return this.sorter;
	}

	public void setSorter(Comparator sorter) {
		if(this.sorter != sorter) {
			this.sorter = sorter;
			this.insertRoots();
		}

	}

	public static final class FolderFilter implements FileSystemModel.FileFilter {
		public static final FileSystemTreeModel.FolderFilter instance = new FileSystemTreeModel.FolderFilter();

		public boolean accept(FileSystemModel model, Object file) {
			return model.isFolder(file);
		}
	}

	public static class FolderNode implements TreeTableNode {
		private final TreeTableNode parent;
		private final FileSystemModel fsm;
		final Object folder;
		FileSystemTreeModel.FolderNode[] children;

		protected FolderNode(TreeTableNode parent, FileSystemModel fsm, Object folder) {
			this.parent = parent;
			this.fsm = fsm;
			this.folder = folder;
		}

		public Object getFolder() {
			return this.folder;
		}

		public Object getData(int column) {
			switch(column) {
			case 0:
				return this.fsm.getName(this.folder);
			case 1:
				return this.getlastModified();
			default:
				return null;
			}
		}

		public Object getTooltipContent(int column) {
			StringBuilder sb = new StringBuilder(this.fsm.getPath(this.folder));
			Date lastModified = this.getlastModified();
			if(lastModified != null) {
				sb.append("\nLast modified: ").append(lastModified);
			}

			return sb.toString();
		}

		public TreeTableNode getChild(int idx) {
			return this.children[idx];
		}

		public int getChildIndex(TreeTableNode child) {
			int i = 0;

			for(int n = this.children.length; i < n; ++i) {
				if(this.children[i] == child) {
					return i;
				}
			}

			return -1;
		}

		public int getNumChildren() {
			if(this.children == null) {
				this.collectChilds();
			}

			return this.children.length;
		}

		public TreeTableNode getParent() {
			return this.parent;
		}

		public boolean isLeaf() {
			return false;
		}

		public FileSystemTreeModel getTreeModel() {
			TreeTableNode node;
			TreeTableNode nodeParent;
			for(node = this.parent; (nodeParent = node.getParent()) != null; node = nodeParent) {
			}

			return (FileSystemTreeModel)node;
		}

		private void collectChilds() {
			this.children = FileSystemTreeModel.NO_CHILDREN;

			try {
				Object[] ex = this.fsm.listFolder(this.folder, FileSystemTreeModel.FolderFilter.instance);
				if(ex != null && ex.length > 0) {
					Comparator sorter = this.getTreeModel().sorter;
					if(sorter != null) {
						Arrays.sort(ex, sorter);
					}

					FileSystemTreeModel.FolderNode[] newChildren = new FileSystemTreeModel.FolderNode[ex.length];

					for(int i = 0; i < ex.length; ++i) {
						newChildren[i] = new FileSystemTreeModel.FolderNode(this, this.fsm, ex[i]);
					}

					this.children = newChildren;
				}
			} catch (Exception exception5) {
				exception5.printStackTrace();
			}

		}

		private Date getlastModified() {
			return this.parent instanceof FileSystemTreeModel ? null : new Date(this.fsm.getLastModified(this.folder));
		}
	}
}
