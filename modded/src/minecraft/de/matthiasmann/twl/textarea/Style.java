package de.matthiasmann.twl.textarea;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class Style {
	private final Style parent;
	private final StyleSheetKey styleSheetKey;
	private final Object[] values;

	public Style() {
		this((Style)null, (StyleSheetKey)null);
	}

	public Style(Style parent, StyleSheetKey styleSheetKey) {
		this.parent = parent;
		this.styleSheetKey = styleSheetKey;
		this.values = new Object[StyleAttribute.getNumAttributes()];
	}

	public Style(Style parent, StyleSheetKey styleSheetKey, Map values) {
		this(parent, styleSheetKey);
		if(values != null) {
			this.putAll(values);
		}

	}

	protected Style(Style src) {
		this.parent = src.parent;
		this.styleSheetKey = src.styleSheetKey;
		this.values = (Object[])src.values.clone();
	}

	public Style resolve(StyleAttribute attribute, StyleSheetResolver resolver) {
		return !attribute.isInherited() ? this : doResolve(this, attribute.ordinal(), resolver);
	}

	private static Style doResolve(Style style, int ord, StyleSheetResolver resolver) {
		for(; style.parent != null; style = style.parent) {
			if(style.values[ord] != null) {
				return style;
			}

			if(resolver != null) {
				Style styleSheetStyle = resolver.resolve(style);
				if(styleSheetStyle != null && styleSheetStyle.values[ord] != null) {
					return style;
				}
			}
		}

		return style;
	}

	public Object getNoResolve(StyleAttribute attribute, StyleSheetResolver resolver) {
		Object value = this.values[attribute.ordinal()];
		if(value == null && resolver != null) {
			Style styleSheetStyle = resolver.resolve(this);
			if(styleSheetStyle != null) {
				value = styleSheetStyle.values[attribute.ordinal()];
			}
		}

		return value == null ? attribute.getDefaultValue() : attribute.getDataType().cast(value);
	}

	public Object get(StyleAttribute attribute, StyleSheetResolver resolver) {
		return this.resolve(attribute, resolver).getNoResolve(attribute, resolver);
	}

	public Style getParent() {
		return this.parent;
	}

	public StyleSheetKey getStyleSheetKey() {
		return this.styleSheetKey;
	}

	public Style with(Map values) {
		Style newStyle = new Style(this);
		newStyle.putAll(values);
		return newStyle;
	}

	public Style with(StyleAttribute attribute, Object value) {
		Style newStyle = new Style(this);
		newStyle.put(attribute, value);
		return newStyle;
	}

	protected void put(StyleAttribute attribute, Object value) {
		if(attribute == null) {
			throw new IllegalArgumentException("attribute is null");
		} else if(value != null && !attribute.getDataType().isInstance(value)) {
			throw new IllegalArgumentException("value is a " + value.getClass() + " but must be a " + attribute.getDataType());
		} else {
			this.values[attribute.ordinal()] = value;
		}
	}

	protected void putAll(Map values) {
		Iterator iterator3 = values.entrySet().iterator();

		while(iterator3.hasNext()) {
			Entry e = (Entry)iterator3.next();
			this.put((StyleAttribute)e.getKey(), e.getValue());
		}

	}

	protected void putAll(Style src) {
		int i = 0;

		for(int n = this.values.length; i < n; ++i) {
			Object value = src.values[i];
			if(value != null) {
				this.values[i] = value;
			}
		}

	}

	public Map toMap() {
		HashMap result = new HashMap();

		for(int ord = 0; ord < this.values.length; ++ord) {
			Object value = this.values[ord];
			if(value != null) {
				result.put(StyleAttribute.getAttribute(ord), value);
			}
		}

		return result;
	}
}
