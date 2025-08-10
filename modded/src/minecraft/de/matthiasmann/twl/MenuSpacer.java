package de.matthiasmann.twl;

public class MenuSpacer extends MenuElement {
	protected Widget createMenuWidget(MenuManager mm, int level) {
		Widget w = new Widget();
		this.setWidgetTheme(w, "spacer");
		return w;
	}
}
