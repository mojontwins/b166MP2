package de.matthiasmann.twl;

public class MenuAction extends MenuElement {
	private Runnable cb;

	public MenuAction() {
	}

	public MenuAction(Runnable cb) {
		this.cb = cb;
	}

	public MenuAction(String name, Runnable cb) {
		super(name);
		this.cb = cb;
	}

	public Runnable getCallback() {
		return this.cb;
	}

	public void setCallback(Runnable cb) {
		this.cb = cb;
	}

	protected Widget createMenuWidget(MenuManager mm, int level) {
		MenuElement.MenuBtn b = new MenuElement.MenuBtn();
		this.setWidgetTheme(b, "button");
		if(this.cb != null) {
			b.addCallback(this.cb);
		}

		b.addCallback(mm.getCloseCallback());
		return b;
	}
}
