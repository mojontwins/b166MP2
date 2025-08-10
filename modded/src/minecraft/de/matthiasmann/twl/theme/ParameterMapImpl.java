package de.matthiasmann.twl.theme;

import de.matthiasmann.twl.Color;
import de.matthiasmann.twl.DebugHook;
import de.matthiasmann.twl.ParameterList;
import de.matthiasmann.twl.ParameterMap;
import de.matthiasmann.twl.renderer.Font;
import de.matthiasmann.twl.renderer.Image;
import de.matthiasmann.twl.renderer.MouseCursor;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

class ParameterMapImpl extends ThemeChildImpl implements ParameterMap {
	final HashMap params = new HashMap();

	ParameterMapImpl(ThemeManager manager, ThemeInfoImpl parent) {
		super(manager, parent);
	}

	public Font getFont(String name) {
		Font value = (Font)this.getParameterValue(name, true, Font.class);
		return value != null ? value : this.manager.getDefaultFont();
	}

	public Image getImage(String name) {
		Image img = (Image)this.getParameterValue(name, true, Image.class);
		return img == ImageManager.NONE ? null : img;
	}

	public MouseCursor getMouseCursor(String name) {
		MouseCursor value = (MouseCursor)this.getParameterValue(name, false, MouseCursor.class);
		return value;
	}

	public ParameterMap getParameterMap(String name) {
		ParameterMap value = (ParameterMap)this.getParameterValue(name, true, ParameterMap.class);
		return (ParameterMap)(value == null ? this.manager.emptyMap : value);
	}

	public ParameterList getParameterList(String name) {
		ParameterList value = (ParameterList)this.getParameterValue(name, true, ParameterList.class);
		return (ParameterList)(value == null ? this.manager.emptyList : value);
	}

	public boolean getParameter(String name, boolean defaultValue) {
		Boolean value = (Boolean)this.getParameterValue(name, true, Boolean.class);
		return value != null ? value.booleanValue() : defaultValue;
	}

	public int getParameter(String name, int defaultValue) {
		Integer value = (Integer)this.getParameterValue(name, true, Integer.class);
		return value != null ? value.intValue() : defaultValue;
	}

	public float getParameter(String name, float defaultValue) {
		Float value = (Float)this.getParameterValue(name, true, Float.class);
		return value != null ? value.floatValue() : defaultValue;
	}

	public String getParameter(String name, String defaultValue) {
		String value = (String)this.getParameterValue(name, true, String.class);
		return value != null ? value : defaultValue;
	}

	public Color getParameter(String name, Color defaultValue) {
		Color value = (Color)this.getParameterValue(name, true, Color.class);
		return value != null ? value : defaultValue;
	}

	public Enum getParameter(String name, Enum defaultValue) {
		Class enumType = defaultValue.getDeclaringClass();
		Enum value = (Enum)this.getParameterValue(name, true, enumType);
		return value != null ? value : defaultValue;
	}

	public Object getParameterValue(String name, boolean warnIfNotPresent) {
		Object value = this.params.get(name);
		if(value == null && warnIfNotPresent) {
			this.missingParameter(name, (Class)null);
		}

		return value;
	}

	public Object getParameterValue(String name, boolean warnIfNotPresent, Class clazz) {
		return this.getParameterValue(name, warnIfNotPresent, clazz, (Object)null);
	}

	public Object getParameterValue(String name, boolean warnIfNotPresent, Class clazz, Object defaultValue) {
		Object value = this.params.get(name);
		if(value == null && warnIfNotPresent) {
			this.missingParameter(name, clazz);
		}

		if(!clazz.isInstance(value)) {
			if(value != null) {
				this.wrongParameterType(name, clazz, value.getClass());
			}

			return defaultValue;
		} else {
			return clazz.cast(value);
		}
	}

	protected void wrongParameterType(String paramName, Class expectedType, Class foundType) {
		DebugHook.getDebugHook().wrongParameterType(this, paramName, expectedType, foundType, this.getParentDescription());
	}

	protected void missingParameter(String paramName, Class dataType) {
		DebugHook.getDebugHook().missingParameter(this, paramName, this.getParentDescription(), dataType);
	}

	protected void replacingWithDifferentType(String paramName, Class oldType, Class newType) {
		DebugHook.getDebugHook().replacingWithDifferentType(this, paramName, oldType, newType, this.getParentDescription());
	}

	void addParameters(Map params) {
		Iterator iterator3 = params.entrySet().iterator();

		while(iterator3.hasNext()) {
			Entry e = (Entry)iterator3.next();
			String paramName = (String)e.getKey();
			Object value = e.getValue();
			Object old = this.params.put(paramName, value);
			if(old != null) {
				Class oldClass = old.getClass();
				Class newClass = value != null ? value.getClass() : null;
				if(oldClass != newClass) {
					this.replacingWithDifferentType(paramName, oldClass, newClass);
				}
			}
		}

	}
}
