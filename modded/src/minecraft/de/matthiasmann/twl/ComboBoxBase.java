package de.matthiasmann.twl;

public abstract class ComboBoxBase extends Widget {
	public static final de.matthiasmann.twl.renderer.AnimationState.StateKey STATE_COMBOBOX_KEYBOARD_FOCUS = de.matthiasmann.twl.renderer.AnimationState.StateKey.get("comboboxKeyboardFocus");
	protected final Button button = new Button(this.getAnimationState());
	protected final PopupWindow popup = new PopupWindow(this);

	protected ComboBoxBase() {
		this.button.addCallback(new Runnable() {
			public void run() {
				ComboBoxBase.this.openPopup();
			}
		});
		this.add(this.button);
		this.setCanAcceptKeyboardFocus(true);
		this.setDepthFocusTraversal(false);
	}

	protected abstract Widget getLabel();

	protected boolean openPopup() {
		if(this.popup.openPopup()) {
			this.setPopupSize();
			return true;
		} else {
			return false;
		}
	}

	public int getPreferredInnerWidth() {
		return this.getLabel().getPreferredWidth() + this.button.getPreferredWidth();
	}

	public int getPreferredInnerHeight() {
		return Math.max(this.getLabel().getPreferredHeight(), this.button.getPreferredHeight());
	}

	public int getMinWidth() {
		int minWidth = super.getMinWidth();
		minWidth = Math.max(minWidth, this.getLabel().getMinWidth() + this.button.getMinWidth());
		return minWidth;
	}

	public int getMinHeight() {
		int minInnerHeight = Math.max(this.getLabel().getMinHeight(), this.button.getMinHeight());
		return Math.max(super.getMinHeight(), minInnerHeight + this.getBorderVertical());
	}

	protected void setPopupSize() {
		int minHeight = this.popup.getMinHeight();
		int popupHeight = computeSize(minHeight, this.popup.getPreferredHeight(), this.popup.getMaxHeight());
		int popupMaxBottom = this.popup.getParent().getInnerBottom();
		if(this.getBottom() + minHeight > popupMaxBottom) {
			if(this.getY() - popupHeight >= this.popup.getParent().getInnerY()) {
				this.popup.setPosition(this.getX(), this.getY() - popupHeight);
			} else {
				this.popup.setPosition(this.getX(), popupMaxBottom - minHeight);
			}
		} else {
			this.popup.setPosition(this.getX(), this.getBottom());
		}

		popupHeight = Math.min(popupHeight, popupMaxBottom - this.popup.getY());
		this.popup.setSize(this.getWidth(), popupHeight);
	}

	protected void layout() {
		int btnWidth = this.button.getPreferredWidth();
		int innerHeight = this.getInnerHeight();
		this.button.setPosition(this.getInnerRight() - btnWidth, this.getInnerY());
		this.button.setSize(btnWidth, innerHeight);
		this.getLabel().setSize(Math.max(0, this.button.getX() - this.getInnerX()), innerHeight);
	}

	protected void sizeChanged() {
		super.sizeChanged();
		if(this.popup.isOpen()) {
			this.setPopupSize();
		}

	}

	private void setRecursive(Widget w, de.matthiasmann.twl.renderer.AnimationState.StateKey what, boolean state) {
		w.getAnimationState().setAnimationState(what, state);

		for(int i = 0; i < w.getNumChildren(); ++i) {
			Widget child = w.getChild(i);
			this.setRecursive(child, what, state);
		}

	}

	protected void keyboardFocusGained() {
		super.keyboardFocusGained();
		this.setRecursive(this.getLabel(), STATE_COMBOBOX_KEYBOARD_FOCUS, true);
	}

	protected void keyboardFocusLost() {
		super.keyboardFocusLost();
		this.setRecursive(this.getLabel(), STATE_COMBOBOX_KEYBOARD_FOCUS, false);
	}
}
