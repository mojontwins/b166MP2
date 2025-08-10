package de.matthiasmann.twl.theme;

import de.matthiasmann.twl.DebugHook;
import de.matthiasmann.twl.ThemeInfo;

import java.util.HashMap;

class ThemeInfoImpl extends ParameterMapImpl implements ThemeInfo {
	private final String name;
	final HashMap children;
	boolean maybeUsedFromWildcard;
	String wildcardImportPath;

	public ThemeInfoImpl(ThemeManager manager, String name, ThemeInfoImpl parent) {
		super(manager, parent);
		this.name = name;
		this.children = new HashMap();
	}

	void copy(ThemeInfoImpl src) {
		this.children.putAll(src.children);
		this.params.putAll(src.params);
		this.wildcardImportPath = src.wildcardImportPath;
	}

	public String getName() {
		return this.name;
	}

	public ThemeInfo getChildTheme(String theme) {
		ThemeInfo info = (ThemeInfo)this.children.get(theme);
		if(info == null) {
			if(this.wildcardImportPath != null) {
				info = this.manager.resolveWildcard(this.wildcardImportPath, theme);
			}

			if(info == null) {
				DebugHook.getDebugHook().missingChildTheme(this, theme);
			}
		}

		return info;
	}

	public String getThemePath() {
		return this.getThemePath(0).toString();
	}

	private StringBuilder getThemePath(int length) {
		length += this.getName().length();
		StringBuilder sb;
		if(this.parent != null) {
			sb = this.parent.getThemePath(length + 1);
			sb.append('.');
		} else {
			sb = new StringBuilder(length);
		}

		sb.append(this.getName());
		return sb;
	}
}
