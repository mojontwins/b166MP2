package de.matthiasmann.twl;

public class PopupWindow extends Widget {
	private final Widget owner;
	private boolean closeOnClickedOutside = true;
	private boolean closeOnEscape = true;

	public PopupWindow(Widget owner) {
		if(owner == null) {
			throw new NullPointerException("owner");
		} else {
			this.owner = owner;
		}
	}

	public Widget getOwner() {
		return this.owner;
	}

	public boolean isCloseOnClickedOutside() {
		return this.closeOnClickedOutside;
	}

	public void setCloseOnClickedOutside(boolean closeOnClickedOutside) {
		this.closeOnClickedOutside = closeOnClickedOutside;
	}

	public boolean isCloseOnEscape() {
		return this.closeOnEscape;
	}

	public void setCloseOnEscape(boolean closeOnEscape) {
		this.closeOnEscape = closeOnEscape;
	}

	public boolean openPopup() {
		GUI gui = this.owner.getGUI();
		if(gui != null) {
			super.setVisible(true);
			super.setEnabled(true);
			gui.openPopup(this);
			this.requestKeyboardFocus();
			this.focusFirstChild();
			return this.isOpen();
		} else {
			return false;
		}
	}

	public void openPopupCentered() {
		if(this.openPopup()) {
			this.adjustSize();
			this.centerPopup();
		}

	}

	public void openPopupCentered(int width, int height) {
		if(this.openPopup()) {
			this.setSize(Math.min(this.getParent().getInnerWidth(), width), Math.min(this.getParent().getInnerHeight(), height));
			this.centerPopup();
		}

	}

	public void closePopup() {
		GUI gui = this.getGUI();
		if(gui != null) {
			gui.closePopup(this);
			this.owner.requestKeyboardFocus();
		}

	}

	public final boolean isOpen() {
		return this.getParent() != null;
	}

	public void centerPopup() {
		Widget parent = this.getParent();
		if(parent != null) {
			this.setPosition(parent.getInnerX() + (parent.getInnerWidth() - this.getWidth()) / 2, parent.getInnerY() + (parent.getInnerHeight() - this.getHeight()) / 2);
		}

	}

	public boolean bindMouseDrag(Runnable cb) {
		GUI gui = this.getGUI();
		return gui != null ? gui.bindDragEvent(this, cb) : false;
	}

	public int getPreferredInnerWidth() {
		return BoxLayout.computePreferredWidthVertical(this);
	}

	public int getPreferredInnerHeight() {
		return BoxLayout.computePreferredHeightHorizontal(this);
	}

	public int getPreferredWidth() {
		int parentWidth = this.getParent() != null ? this.getParent().getInnerWidth() : 32767;
		return Math.min(parentWidth, super.getPreferredWidth());
	}

	public int getPreferredHeight() {
		int parentHeight = this.getParent() != null ? this.getParent().getInnerHeight() : 32767;
		return Math.min(parentHeight, super.getPreferredHeight());
	}

	protected void layout() {
		this.layoutChildrenFullInnerArea();
	}

	protected final boolean handleEvent(Event evt) {
		if(this.handleEventPopup(evt)) {
			return true;
		} else if(evt.getType() == Event.Type.MOUSE_CLICKED && !this.isInside(evt.getMouseX(), evt.getMouseY())) {
			this.mouseClickedOutside(evt);
			return true;
		} else if(this.closeOnEscape && evt.isKeyPressedEvent() && evt.getKeyCode() == 1) {
			this.requestPopupClose();
			return true;
		} else {
			return true;
		}
	}

	protected boolean handleEventPopup(Event evt) {
		return super.handleEvent(evt);
	}

	protected final boolean isMouseInside(Event evt) {
		return true;
	}

	protected void requestPopupClose() {
		this.closePopup();
	}

	protected void mouseClickedOutside(Event evt) {
		if(this.closeOnClickedOutside) {
			this.requestPopupClose();
		}

	}
}
