package de.matthiasmann.twl.theme;

class ThemeChildImpl {
	final ThemeManager manager;
	final ThemeInfoImpl parent;

	ThemeChildImpl(ThemeManager manager, ThemeInfoImpl parent) {
		this.manager = manager;
		this.parent = parent;
	}

	protected String getParentDescription() {
		return this.parent != null ? ", defined in " + this.parent.getThemePath() : "";
	}
}
