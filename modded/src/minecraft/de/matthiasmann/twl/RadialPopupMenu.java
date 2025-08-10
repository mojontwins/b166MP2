package de.matthiasmann.twl;

import de.matthiasmann.twl.model.ButtonModel;

import java.util.ArrayList;

public class RadialPopupMenu extends PopupWindow {
	private final ArrayList buttons = new ArrayList();
	private int radius;
	private int buttonRadius;
	private int mouseButton;
	int buttonRadiusSqr;

	public RadialPopupMenu(Widget owner) {
		super(owner);
	}

	public int getButtonRadius() {
		return this.buttonRadius;
	}

	public void setButtonRadius(int buttonRadius) {
		if(buttonRadius < 0) {
			throw new IllegalArgumentException("buttonRadius");
		} else {
			this.buttonRadius = buttonRadius;
			this.buttonRadiusSqr = buttonRadius * buttonRadius;
			this.invalidateLayout();
		}
	}

	public int getRadius() {
		return this.radius;
	}

	public void setRadius(int radius) {
		if(radius < 0) {
			throw new IllegalArgumentException("radius");
		} else {
			this.radius = radius;
			this.invalidateLayout();
		}
	}

	public int getMouseButton() {
		return this.mouseButton;
	}

	public void setMouseButton(int mouseButton) {
		if(mouseButton >= 0 && mouseButton <= 1) {
			this.mouseButton = mouseButton;
			int i = 0;

			for(int n = this.buttons.size(); i < n; ++i) {
				((RadialPopupMenu.RoundButton)this.buttons.get(i)).setMouseButton(mouseButton);
			}

		} else {
			throw new IllegalArgumentException("mouseButton");
		}
	}

	public Button addButton(String theme, Runnable cb) {
		RadialPopupMenu.RoundButton button = new RadialPopupMenu.RoundButton();
		button.setTheme(theme);
		button.addCallback(cb);
		button.setMouseButton(this.mouseButton);
		this.addButton(button);
		return button;
	}

	public void removeButton(Button btn) {
		int idx = this.buttons.indexOf(btn);
		if(idx >= 0) {
			this.buttons.remove(idx);
			this.removeChild(btn);
		}

	}

	protected void addButton(RadialPopupMenu.RoundButton button) {
		if(button == null) {
			throw new NullPointerException("button");
		} else {
			this.buttons.add(button);
			this.add(button);
		}
	}

	public boolean openPopup() {
		if(super.openPopup()) {
			if(this.bindMouseDrag(new Runnable() {
				public void run() {
					RadialPopupMenu.this.boundDragEventFinished();
				}
			})) {
				this.setAllButtonsPressed();
			}

			return true;
		} else {
			return false;
		}
	}

	public boolean openPopupAt(int centerX, int centerY) {
		if(this.openPopup()) {
			this.adjustSize();
			Widget parent = this.getParent();
			int width = this.getWidth();
			int height = this.getHeight();
			this.setPosition(limit(centerX - width / 2, parent.getInnerX(), parent.getInnerRight() - width), limit(centerY - height / 2, parent.getInnerY(), parent.getInnerBottom() - height));
			return true;
		} else {
			return false;
		}
	}

	protected static int limit(int value, int min, int max) {
		return value < min ? min : (value > max ? max : value);
	}

	public boolean openPopup(Event evt) {
		if(evt.getType() == Event.Type.MOUSE_BTNDOWN) {
			this.setMouseButton(evt.getMouseButton());
			return this.openPopupAt(evt.getMouseX(), evt.getMouseY());
		} else {
			return false;
		}
	}

	public int getPreferredInnerWidth() {
		return 2 * (this.radius + this.buttonRadius);
	}

	public int getPreferredInnerHeight() {
		return 2 * (this.radius + this.buttonRadius);
	}

	protected void applyTheme(ThemeInfo themeInfo) {
		super.applyTheme(themeInfo);
		this.applyThemeRadialPopupMenu(themeInfo);
	}

	protected void applyThemeRadialPopupMenu(ThemeInfo themeInfo) {
		this.setRadius(themeInfo.getParameter("radius", 40));
		this.setButtonRadius(themeInfo.getParameter("buttonRadius", 40));
	}

	protected void layout() {
		this.layoutRadial();
	}

	protected void layoutRadial() {
		int numButtons = this.buttons.size();
		if(numButtons > 0) {
			int centerX = this.getInnerX() + this.getInnerWidth() / 2;
			int centerY = this.getInnerY() + this.getInnerHeight() / 2;
			float toRad = 6.2831855F / (float)numButtons;

			for(int i = 0; i < numButtons; ++i) {
				float rad = (float)i * toRad;
				int btnCenterX = centerX + (int)((double)this.radius * Math.sin((double)rad));
				int btnCenterY = centerY - (int)((double)this.radius * Math.cos((double)rad));
				RadialPopupMenu.RoundButton button = (RadialPopupMenu.RoundButton)this.buttons.get(i);
				button.setPosition(btnCenterX - this.buttonRadius, btnCenterY - this.buttonRadius);
				button.setSize(2 * this.buttonRadius, 2 * this.buttonRadius);
			}
		}

	}

	protected void setAllButtonsPressed() {
		int i = 0;

		for(int n = this.buttons.size(); i < n; ++i) {
			ButtonModel model = ((RadialPopupMenu.RoundButton)this.buttons.get(i)).getModel();
			model.setPressed(true);
			model.setArmed(model.isHover());
		}

	}

	protected void boundDragEventFinished() {
		this.closePopup();
	}

	protected class RoundButton extends Button {
		public boolean isInside(int x, int y) {
			int dx = x - (this.getX() + this.getWidth() / 2);
			int dy = y - (this.getY() + this.getHeight() / 2);
			return dx * dx + dy * dy <= RadialPopupMenu.this.buttonRadiusSqr;
		}
	}
}
