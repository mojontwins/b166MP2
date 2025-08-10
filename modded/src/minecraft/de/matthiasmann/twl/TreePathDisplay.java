package de.matthiasmann.twl;

import de.matthiasmann.twl.model.TreeTableNode;
import de.matthiasmann.twl.utils.CallbackSupport;

public class TreePathDisplay extends Widget {
	private final BoxLayout pathBox = new TreePathDisplay.PathBox();
	private final EditField editField;
	private TreePathDisplay.Callback[] callbacks;
	private String separator = "/";
	private TreeTableNode currentNode;
	private boolean allowEdit;

	public TreePathDisplay() {
		this.pathBox.setScroll(true);
		this.pathBox.setClip(true);
		this.editField = new TreePathDisplay.PathEditField((TreePathDisplay.PathEditField)null);
		this.editField.setVisible(false);
		this.add(this.pathBox);
		this.add(this.editField);
	}

	public void addCallback(TreePathDisplay.Callback cb) {
		this.callbacks = (TreePathDisplay.Callback[])CallbackSupport.addCallbackToList(this.callbacks, cb, TreePathDisplay.Callback.class);
	}

	public void removeCallback(TreePathDisplay.Callback cb) {
		this.callbacks = (TreePathDisplay.Callback[])CallbackSupport.removeCallbackFromList(this.callbacks, cb);
	}

	public TreeTableNode getCurrentNode() {
		return this.currentNode;
	}

	public void setCurrentNode(TreeTableNode currentNode) {
		this.currentNode = currentNode;
		this.rebuildPathBox();
	}

	public String getSeparator() {
		return this.separator;
	}

	public void setSeparator(String separator) {
		this.separator = separator;
		this.rebuildPathBox();
	}

	public boolean isAllowEdit() {
		return this.allowEdit;
	}

	public void setAllowEdit(boolean allowEdit) {
		this.allowEdit = allowEdit;
		this.rebuildPathBox();
	}

	public void setEditErrorMessage(String msg) {
		this.editField.setErrorMessage(msg);
	}

	public EditField getEditField() {
		return this.editField;
	}

	protected String getTextFromNode(TreeTableNode node) {
		Object data = node.getData(0);
		String text = data != null ? data.toString() : "";
		if(text.endsWith(this.separator)) {
			text = text.substring(0, text.length() - 1);
		}

		return text;
	}

	private void rebuildPathBox() {
		this.pathBox.removeAllChildren();
		if(this.currentNode != null) {
			this.recursiveAddNode(this.currentNode, (TreeTableNode)null);
		}

	}

	private void recursiveAddNode(final TreeTableNode node, final TreeTableNode child) {
		if(node.getParent() != null) {
			this.recursiveAddNode(node.getParent(), node);
			Button btn = new Button(this.getTextFromNode(node));
			btn.setTheme("node");
			btn.addCallback(new Runnable() {
				public void run() {
					TreePathDisplay.this.firePathElementClicked(node, child);
				}
			});
			this.pathBox.add(btn);
			Label l = new Label(this.separator);
			l.setTheme("separator");
			if(this.allowEdit) {
				l.addCallback(new CallbackWithReason() {
					public void callback(Label.CallbackReason reason) {
						if(reason == Label.CallbackReason.DOUBLE_CLICK) {
							TreePathDisplay.this.editPath(node);
						}

					}

					public void callback(Enum enum1) {
						this.callback((Label.CallbackReason)enum1);
					}
				});
			}

			this.pathBox.add(l);
		}

	}

	void endEdit() {
		this.editField.setVisible(false);
		this.requestKeyboardFocus();
	}

	void editPath(TreeTableNode cursorAfterNode) {
		StringBuilder sb = new StringBuilder();
		int cursorPos = 0;
		if(this.currentNode != null) {
			cursorPos = this.recursiveAddPath(sb, this.currentNode, cursorAfterNode);
		}

		this.editField.setErrorMessage((Object)null);
		this.editField.setText(sb.toString());
		this.editField.setCursorPos(cursorPos, false);
		this.editField.setVisible(true);
		this.editField.requestKeyboardFocus();
	}

	private int recursiveAddPath(StringBuilder sb, TreeTableNode node, TreeTableNode cursorAfterNode) {
		int cursorPos = 0;
		if(node.getParent() != null) {
			cursorPos = this.recursiveAddPath(sb, node.getParent(), cursorAfterNode);
			sb.append(this.getTextFromNode(node)).append(this.separator);
		}

		return node == cursorAfterNode ? sb.length() : cursorPos;
	}

	protected boolean fireResolvePath(String text) {
		if(this.callbacks != null) {
			TreePathDisplay.Callback[] treePathDisplay$Callback5 = this.callbacks;
			int i4 = this.callbacks.length;

			for(int i3 = 0; i3 < i4; ++i3) {
				TreePathDisplay.Callback cb = treePathDisplay$Callback5[i3];
				if(cb.resolvePath(text)) {
					return true;
				}
			}
		}

		return false;
	}

	protected void firePathElementClicked(TreeTableNode node, TreeTableNode child) {
		if(this.callbacks != null) {
			TreePathDisplay.Callback[] treePathDisplay$Callback6 = this.callbacks;
			int i5 = this.callbacks.length;

			for(int i4 = 0; i4 < i5; ++i4) {
				TreePathDisplay.Callback cb = treePathDisplay$Callback6[i4];
				cb.pathElementClicked(node, child);
			}
		}

	}

	public int getPreferredInnerWidth() {
		return this.pathBox.getPreferredWidth();
	}

	public int getPreferredInnerHeight() {
		return Math.max(this.pathBox.getPreferredHeight(), this.editField.getPreferredHeight());
	}

	public int getMinHeight() {
		int minInnerHeight = Math.max(this.pathBox.getMinHeight(), this.editField.getMinHeight());
		return Math.max(super.getMinHeight(), minInnerHeight + this.getBorderVertical());
	}

	protected void layout() {
		this.layoutChildFullInnerArea(this.pathBox);
		this.layoutChildFullInnerArea(this.editField);
	}

	public interface Callback {
		void pathElementClicked(TreeTableNode treeTableNode1, TreeTableNode treeTableNode2);

		boolean resolvePath(String string1);
	}

	private class PathBox extends BoxLayout {
		public PathBox() {
			super(BoxLayout.Direction.HORIZONTAL);
		}

		protected boolean handleEvent(Event evt) {
			if(evt.isMouseEvent()) {
				if(evt.getType() == Event.Type.MOUSE_CLICKED && evt.getMouseClickCount() == 2) {
					TreePathDisplay.this.editPath(TreePathDisplay.this.getCurrentNode());
					return true;
				} else {
					return evt.getType() != Event.Type.MOUSE_WHEEL;
				}
			} else {
				return super.handleEvent(evt);
			}
		}
	}

	private class PathEditField extends EditField {
		private PathEditField() {
		}

		protected void keyboardFocusLost() {
			if(!this.hasOpenPopups()) {
				this.setVisible(false);
			}

		}

		protected void doCallback(int key) {
			super.doCallback(key);
			switch(key) {
			case 1:
				TreePathDisplay.this.endEdit();
				break;
			case 28:
				if(TreePathDisplay.this.fireResolvePath(TreePathDisplay.this.getEditField().getText())) {
					TreePathDisplay.this.endEdit();
				}
			}

		}

		PathEditField(TreePathDisplay.PathEditField treePathDisplay$PathEditField2) {
			this();
		}
	}
}
