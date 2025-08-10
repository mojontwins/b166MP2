package de.matthiasmann.twl;

import java.util.IdentityHashMap;

public class MenuManager extends PopupWindow {
	private final boolean isMenuBar;
	private final IdentityHashMap popups;
	private final Runnable closeCB;
	private final Runnable timerCB;
	private boolean mouseOverOwner;
	private Widget lastMouseOverWidget;
	private Timer timer;

	public MenuManager(Widget owner, boolean isMenuBar) {
		super(owner);
		this.isMenuBar = isMenuBar;
		this.popups = new IdentityHashMap();
		this.closeCB = new Runnable() {
			public void run() {
				MenuManager.this.closePopup();
			}
		};
		this.timerCB = new Runnable() {
			public void run() {
				MenuManager.this.popupTimer();
			}
		};
	}

	public Runnable getCloseCallback() {
		return this.closeCB;
	}

	boolean isSubMenuOpen(Menu menu) {
		Widget popup = (Widget)this.popups.get(menu);
		return popup != null ? popup.getParent() == this : false;
	}

	void closeSubMenu(int level) {
		while(this.getNumChildren() > level) {
			this.closeSubMenu();
		}

	}

	Widget openSubMenu(int level, Menu menu, Widget btn, boolean setPosition) {
		Object popup = (Widget)this.popups.get(menu);
		if(popup == null) {
			popup = menu.createPopup(this, level + 1, btn);
			this.popups.put(menu, popup);
		}

		if(((Widget)popup).getParent() == this) {
			this.closeSubMenu(level + 1);
			return (Widget)popup;
		} else {
			if(!this.isOpen()) {
				if(!this.openPopup()) {
					this.closePopup();
					return null;
				}

				this.getParent().layoutChildFullInnerArea(this);
			}

			while(this.getNumChildren() > level) {
				this.closeSubMenu();
			}

			this.add((Widget)popup);
			((Widget)popup).adjustSize();
			if(setPosition) {
				int popupWidth = ((Widget)popup).getWidth();
				int popupX = btn.getRight();
				int popupY = btn.getY();
				if(level == 0) {
					popupX = btn.getX();
					popupY = btn.getBottom();
				}

				if(popupWidth + btn.getRight() > this.getInnerRight()) {
					popupX = btn.getX() - popupWidth;
					if(popupX < this.getInnerX()) {
						popupX = this.getInnerRight() - popupWidth;
					}
				}

				int popupHeight = ((Widget)popup).getHeight();
				if(popupY + popupHeight > this.getInnerBottom()) {
					popupY = Math.max(this.getInnerY(), this.getInnerBottom() - popupHeight);
				}

				((Widget)popup).setPosition(popupX, popupY);
			}

			return (Widget)popup;
		}
	}

	void closeSubMenu() {
		this.removeChild(this.getNumChildren() - 1);
	}

	public void closePopup() {
		this.stopTimer();
		GUI gui = this.getGUI();
		super.closePopup();
		this.removeAllChildren();
		this.popups.clear();
		if(gui != null) {
			gui.resendLastMouseMove();
		}

	}

	protected void afterAddToGUI(GUI gui) {
		super.afterAddToGUI(gui);
		this.timer = gui.createTimer();
		this.timer.setDelay(300);
		this.timer.setCallback(this.timerCB);
	}

	protected void layout() {
	}

	Widget routeMouseEvent(Event evt) {
		this.mouseOverOwner = false;
		Widget widget = super.routeMouseEvent(evt);
		Widget mouseOverWidget;
		if(widget == this && this.isMenuBar && this.getOwner().isMouseInside(evt)) {
			mouseOverWidget = this.getOwner().routeMouseEvent(evt);
			if(mouseOverWidget != null) {
				this.mouseOverOwner = true;
				widget = mouseOverWidget;
			}
		}

		mouseOverWidget = this.getWidgetUnderMouse();
		if(this.lastMouseOverWidget != mouseOverWidget) {
			this.lastMouseOverWidget = mouseOverWidget;
			if(this.isMenuBar && widget.getParent() == this.getOwner() && widget instanceof Menu.SubMenuBtn) {
				this.popupTimer();
			} else {
				this.startTimer();
			}
		}

		return widget;
	}

	protected boolean handleEventPopup(Event evt) {
		if(this.isMenuBar && this.getOwner().handleEvent(evt)) {
			return true;
		} else if(super.handleEventPopup(evt)) {
			return true;
		} else if(evt.getType() == Event.Type.MOUSE_CLICKED) {
			this.mouseClickedOutside(evt);
			return true;
		} else {
			return false;
		}
	}

	Widget getWidgetUnderMouse() {
		return this.mouseOverOwner ? this.getOwner().getWidgetUnderMouse() : super.getWidgetUnderMouse();
	}

	void popupTimer() {
		if(this.lastMouseOverWidget instanceof Menu.SubMenuBtn) {
			((Menu.SubMenuBtn)this.lastMouseOverWidget).run();
		} else if(this.lastMouseOverWidget != this) {
			int level = 0;

			for(Widget w = this.lastMouseOverWidget; w != null; w = w.getParent()) {
				if(w instanceof Menu.MenuPopup) {
					level = ((Menu.MenuPopup)w).level;
					break;
				}
			}

			this.closeSubMenu(level);
		}

	}

	void startTimer() {
		if(this.timer != null) {
			this.timer.stop();
			this.timer.start();
		}

	}

	void stopTimer() {
		if(this.timer != null) {
			this.timer.stop();
		}

	}
}
