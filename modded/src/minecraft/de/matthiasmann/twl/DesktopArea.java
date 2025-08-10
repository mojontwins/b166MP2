package de.matthiasmann.twl;

public class DesktopArea extends Widget {
	public DesktopArea() {
		this.setFocusKeyEnabled(false);
	}

	protected void keyboardFocusChildChanged(Widget child) {
		super.keyboardFocusChildChanged(child);
		if(child != null) {
			int fromIdx = this.getChildIndex(child);

			assert fromIdx >= 0;

			int numChildren = this.getNumChildren();
			if(fromIdx < numChildren - 1) {
				this.moveChild(fromIdx, numChildren - 1);
			}
		}

	}

	protected void layout() {
		this.restrictChildrenToInnerArea();
	}

	protected void restrictChildrenToInnerArea() {
		int top = this.getInnerY();
		int left = this.getInnerX();
		int right = this.getInnerRight();
		int bottom = this.getInnerBottom();
		int width = Math.max(0, right - left);
		int height = Math.max(0, bottom - top);
		int i = 0;

		for(int n = this.getNumChildren(); i < n; ++i) {
			Widget w = this.getChild(i);
			w.setSize(Math.min(Math.max(width, w.getMinWidth()), w.getWidth()), Math.min(Math.max(height, w.getMinHeight()), w.getHeight()));
			w.setPosition(Math.max(left, Math.min(right - w.getWidth(), w.getX())), Math.max(top, Math.min(bottom - w.getHeight(), w.getY())));
		}

	}
}
