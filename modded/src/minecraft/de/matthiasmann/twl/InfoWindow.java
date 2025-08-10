package de.matthiasmann.twl;

public class InfoWindow extends Widget {
	private final Widget owner;

	public InfoWindow(Widget owner) {
		if(owner == null) {
			throw new NullPointerException("owner");
		} else {
			this.owner = owner;
		}
	}

	public Widget getOwner() {
		return this.owner;
	}

	public boolean isOpen() {
		return this.getParent() != null;
	}

	public boolean openInfo() {
		if(this.getParent() != null) {
			return true;
		} else if(isParentInfoWindow(this.owner)) {
			return false;
		} else {
			GUI gui = this.owner.getGUI();
			if(gui != null) {
				gui.openInfo(this);
				this.focusFirstChild();
				return true;
			} else {
				return false;
			}
		}
	}

	public void closeInfo() {
		GUI gui = this.getGUI();
		if(gui != null) {
			gui.closeInfo(this);
		}

	}

	protected void infoWindowClosed() {
	}

	protected void layout() {
		this.layoutChildrenFullInnerArea();
	}

	public int getMinWidth() {
		return BoxLayout.computeMinWidthVertical(this);
	}

	public int getMinHeight() {
		return BoxLayout.computeMinHeightHorizontal(this);
	}

	public int getPreferredInnerWidth() {
		return BoxLayout.computePreferredWidthVertical(this);
	}

	public int getPreferredInnerHeight() {
		return BoxLayout.computePreferredHeightHorizontal(this);
	}

	private static boolean isParentInfoWindow(Widget w) {
		while(w != null) {
			if(w instanceof InfoWindow) {
				return true;
			}

			w = w.getParent();
		}

		return false;
	}
}
