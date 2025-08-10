package de.matthiasmann.twl.theme;

import de.matthiasmann.twl.Color;
import de.matthiasmann.twl.DebugHook;
import de.matthiasmann.twl.ParameterList;
import de.matthiasmann.twl.ParameterMap;
import de.matthiasmann.twl.renderer.Font;
import de.matthiasmann.twl.renderer.Image;
import de.matthiasmann.twl.renderer.MouseCursor;

import java.util.ArrayList;

public class ParameterListImpl extends ThemeChildImpl implements ParameterList {
	final ArrayList params = new ArrayList();

	ParameterListImpl(ThemeManager manager, ThemeInfoImpl parent) {
		super(manager, parent);
	}

	public int getSize() {
		return this.params.size();
	}

	public Font getFont(int idx) {
		Font value = (Font)this.getParameterValue(idx, Font.class);
		return value != null ? value : this.manager.getDefaultFont();
	}

	public Image getImage(int idx) {
		Image img = (Image)this.getParameterValue(idx, Image.class);
		return img == ImageManager.NONE ? null : img;
	}

	public MouseCursor getMouseCursor(int idx) {
		MouseCursor value = (MouseCursor)this.getParameterValue(idx, MouseCursor.class);
		return value;
	}

	public ParameterMap getParameterMap(int idx) {
		ParameterMap value = (ParameterMap)this.getParameterValue(idx, ParameterMap.class);
		return (ParameterMap)(value == null ? this.manager.emptyMap : value);
	}

	public ParameterList getParameterList(int idx) {
		ParameterList value = (ParameterList)this.getParameterValue(idx, ParameterList.class);
		return (ParameterList)(value == null ? this.manager.emptyList : value);
	}

	public boolean getParameter(int idx, boolean defaultValue) {
		Boolean value = (Boolean)this.getParameterValue(idx, Boolean.class);
		return value != null ? value.booleanValue() : defaultValue;
	}

	public int getParameter(int idx, int defaultValue) {
		Integer value = (Integer)this.getParameterValue(idx, Integer.class);
		return value != null ? value.intValue() : defaultValue;
	}

	public float getParameter(int idx, float defaultValue) {
		Float value = (Float)this.getParameterValue(idx, Float.class);
		return value != null ? value.floatValue() : defaultValue;
	}

	public String getParameter(int idx, String defaultValue) {
		String value = (String)this.getParameterValue(idx, String.class);
		return value != null ? value : defaultValue;
	}

	public Color getParameter(int idx, Color defaultValue) {
		Color value = (Color)this.getParameterValue(idx, Color.class);
		return value != null ? value : defaultValue;
	}

	public Enum getParameter(int idx, Enum defaultValue) {
		Class enumType = defaultValue.getDeclaringClass();
		Enum value = (Enum)this.getParameterValue(idx, enumType);
		return value != null ? value : defaultValue;
	}

	public Object getParameterValue(int idx) {
		return this.params.get(idx);
	}

	public Object getParameterValue(int idx, Class clazz) {
		Object value = this.getParameterValue(idx);
		if(value != null && !clazz.isInstance(value)) {
			this.wrongParameterType(idx, clazz, value.getClass());
			return null;
		} else {
			return clazz.cast(value);
		}
	}

	protected void wrongParameterType(int idx, Class expectedType, Class foundType) {
		DebugHook.getDebugHook().wrongParameterType(this, idx, expectedType, foundType, this.getParentDescription());
	}
}
