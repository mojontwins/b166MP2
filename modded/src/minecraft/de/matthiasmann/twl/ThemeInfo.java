package de.matthiasmann.twl;

public interface ThemeInfo extends ParameterMap {
	ThemeInfo getChildTheme(String string1);

	String getThemePath();
}
