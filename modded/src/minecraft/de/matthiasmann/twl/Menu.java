package de.matthiasmann.twl;

import de.matthiasmann.twl.model.BooleanModel;

import java.util.ArrayList;
import java.util.Iterator;

public class Menu extends MenuElement implements Iterable {
	public static final de.matthiasmann.twl.renderer.AnimationState.StateKey STATE_HAS_OPEN_MENUS = de.matthiasmann.twl.renderer.AnimationState.StateKey.get("hasOpenMenus");
	private final ArrayList elements = new ArrayList();
	private String popupTheme;

	public Menu() {
	}

	public Menu(String name) {
		super(name);
	}

	public String getPopupTheme() {
		return this.popupTheme;
	}

	public void setPopupTheme(String popupTheme) {
		String oldPopupTheme = this.popupTheme;
		this.popupTheme = popupTheme;
		this.firePropertyChange("popupTheme", oldPopupTheme, this.popupTheme);
	}

	public Iterator iterator() {
		return this.elements.iterator();
	}

	public MenuElement get(int index) {
		return (MenuElement)this.elements.get(index);
	}

	public int getNumElements() {
		return this.elements.size();
	}

	public void clear() {
		this.elements.clear();
	}

	public Menu add(MenuElement e) {
		this.elements.add(e);
		return this;
	}

	public Menu add(String name, Runnable cb) {
		return this.add(new MenuAction(name, cb));
	}

	public Menu add(String name, BooleanModel model) {
		return this.add(new MenuCheckbox(name, model));
	}

	public Menu addSpacer() {
		return this.add(new MenuSpacer());
	}

	public void createMenuBar(Widget container) {
		MenuManager mm = new MenuManager(container, true);
		Widget[] widget6;
		int i5 = (widget6 = this.createWidgets(mm, 0)).length;

		for(int i4 = 0; i4 < i5; ++i4) {
			Widget w = widget6[i4];
			container.add(w);
		}

	}

	public Widget createMenuBar() {
		DialogLayout l = new DialogLayout();
		this.setWidgetTheme(l, "menubar");
		MenuManager mm = new MenuManager(l, true);
		Widget[] widgets = this.createWidgets(mm, 0);
		l.setHorizontalGroup(l.createSequentialGroup().addWidgetsWithGap("menuitem", widgets));
		l.setVerticalGroup(l.createParallelGroup(widgets));
		l.getHorizontalGroup().addGap();
		return l;
	}

	public MenuManager openPopupMenu(Widget parent) {
		MenuManager mm = new MenuManager(parent, false);
		mm.openSubMenu(0, this, parent, true);
		return mm;
	}

	public MenuManager openPopupMenu(Widget parent, int x, int y) {
		MenuManager mm = new MenuManager(parent, false);
		Widget popup = mm.openSubMenu(0, this, parent, false);
		if(popup != null) {
			popup.setPosition(x, y);
		}

		return mm;
	}

	protected Widget createMenuWidget(MenuManager mm, int level) {
		Menu.SubMenuBtn smb = new Menu.SubMenuBtn(mm, level);
		this.setWidgetTheme(smb, "submenu");
		return smb;
	}

	private Widget[] createWidgets(MenuManager mm, int level) {
		Widget[] widgets = new Widget[this.elements.size()];
		int i = 0;

		for(int n = this.elements.size(); i < n; ++i) {
			MenuElement e = (MenuElement)this.elements.get(i);
			widgets[i] = e.createMenuWidget(mm, level);
		}

		return widgets;
	}

	DialogLayout createPopup(MenuManager mm, int level, Widget btn) {
		Widget[] widgets = this.createWidgets(mm, level);
		Menu.MenuPopup popup = new Menu.MenuPopup(btn, level);
		if(this.popupTheme != null) {
			popup.setTheme(this.popupTheme);
		}

		popup.setHorizontalGroup(popup.createParallelGroup(widgets));
		popup.setVerticalGroup(popup.createSequentialGroup().addWidgetsWithGap("menuitem", widgets));
		return popup;
	}

	static class MenuPopup extends DialogLayout {
		private final Widget btn;
		final int level;

		MenuPopup(Widget btn, int level) {
			this.btn = btn;
			this.level = level;
		}

		protected void afterAddToGUI(GUI gui) {
			super.afterAddToGUI(gui);
			this.btn.getAnimationState().setAnimationState(Menu.STATE_HAS_OPEN_MENUS, true);
		}

		protected void beforeRemoveFromGUI(GUI gui) {
			this.btn.getAnimationState().setAnimationState(Menu.STATE_HAS_OPEN_MENUS, false);
			super.beforeRemoveFromGUI(gui);
		}

		protected boolean handleEvent(Event evt) {
			return super.handleEvent(evt) || evt.isMouseEventNoWheel();
		}
	}

	class SubMenuBtn extends MenuElement.MenuBtn implements Runnable {
		private final MenuManager mm;
		private final int level;

		public SubMenuBtn(MenuManager mm, int level) {
			super();
			this.mm = mm;
			this.level = level;
			this.addCallback(this);
		}

		public void run() {
			this.mm.openSubMenu(this.level, Menu.this, this, true);
		}
	}
}
