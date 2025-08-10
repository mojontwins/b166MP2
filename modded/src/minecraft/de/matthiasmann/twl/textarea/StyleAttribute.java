package de.matthiasmann.twl.textarea;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;

public final class StyleAttribute {
	private static final ArrayList attributes = new ArrayList();
	public static final StyleAttribute HORIZONTAL_ALIGNMENT = new StyleAttribute(true, TextAreaModel.HAlignment.class, TextAreaModel.HAlignment.LEFT);
	public static final StyleAttribute VERTICAL_ALIGNMENT = new StyleAttribute(true, TextAreaModel.VAlignment.class, TextAreaModel.VAlignment.BOTTOM);
	public static final StyleAttribute TEXT_IDENT = new StyleAttribute(true, Value.class, Value.ZERO_PX);
	public static final StyleAttribute FONT_NAME = new StyleAttribute(true, String.class, "default");
	public static final StyleAttribute LIST_STYLE_IMAGE = new StyleAttribute(true, String.class, "ul-bullet");
	public static final StyleAttribute LIST_STYLE_TYPE = new StyleAttribute(true, OrderedListType.class, OrderedListType.DECIMAL);
	public static final StyleAttribute PREFORMATTED = new StyleAttribute(true, Boolean.class, Boolean.FALSE);
	public static final StyleAttribute BREAKWORD = new StyleAttribute(true, Boolean.class, Boolean.FALSE);
	public static final StyleAttribute CLEAR = new StyleAttribute(false, TextAreaModel.Clear.class, TextAreaModel.Clear.NONE);
	public static final StyleAttribute DISPLAY = new StyleAttribute(false, TextAreaModel.Display.class, TextAreaModel.Display.INLINE);
	public static final StyleAttribute FLOAT_POSITION = new StyleAttribute(false, TextAreaModel.FloatPosition.class, TextAreaModel.FloatPosition.NONE);
	public static final StyleAttribute WIDTH = new StyleAttribute(false, Value.class, Value.ZERO_PX);
	public static final StyleAttribute HEIGHT = new StyleAttribute(false, Value.class, Value.AUTO);
	public static final StyleAttribute BACKGROUND_IMAGE = new StyleAttribute(false, String.class, (Object)null);
	public static final StyleAttribute MARGIN_TOP = new StyleAttribute(false, Value.class, Value.ZERO_PX);
	public static final StyleAttribute MARGIN_LEFT = new StyleAttribute(false, Value.class, Value.ZERO_PX);
	public static final StyleAttribute MARGIN_RIGHT = new StyleAttribute(false, Value.class, Value.ZERO_PX);
	public static final StyleAttribute MARGIN_BOTTOM = new StyleAttribute(false, Value.class, Value.ZERO_PX);
	public static final StyleAttribute PADDING_TOP = new StyleAttribute(false, Value.class, Value.ZERO_PX);
	public static final StyleAttribute PADDING_LEFT = new StyleAttribute(false, Value.class, Value.ZERO_PX);
	public static final StyleAttribute PADDING_RIGHT = new StyleAttribute(false, Value.class, Value.ZERO_PX);
	public static final StyleAttribute PADDING_BOTTOM = new StyleAttribute(false, Value.class, Value.ZERO_PX);
	public static final BoxAttribute MARGIN = new BoxAttribute(MARGIN_TOP, MARGIN_LEFT, MARGIN_RIGHT, MARGIN_BOTTOM);
	public static final BoxAttribute PADDING = new BoxAttribute(PADDING_TOP, PADDING_LEFT, PADDING_RIGHT, PADDING_BOTTOM);
	private final boolean inherited;
	private final Class dataType;
	private final Object defaultValue;
	private final int ordinal;

	public boolean isInherited() {
		return this.inherited;
	}

	public Class getDataType() {
		return this.dataType;
	}

	public Object getDefaultValue() {
		return this.defaultValue;
	}

	public int ordinal() {
		return this.ordinal;
	}

	public String name() {
		try {
			Field[] field4;
			int i3 = (field4 = StyleAttribute.class.getFields()).length;

			for(int i2 = 0; i2 < i3; ++i2) {
				Field f = field4[i2];
				if(Modifier.isStatic(f.getModifiers()) && f.get((Object)null) == this) {
					return f.getName();
				}
			}
		} catch (Throwable throwable5) {
		}

		return "?";
	}

	public String toString() {
		return this.name();
	}

	private StyleAttribute(boolean inherited, Class dataType, Object defaultValue) {
		this.inherited = inherited;
		this.dataType = dataType;
		this.defaultValue = defaultValue;
		this.ordinal = attributes.size();
		attributes.add(this);
	}

	public static int getNumAttributes() {
		return attributes.size();
	}

	public static StyleAttribute getAttribute(int ordinal) throws IndexOutOfBoundsException {
		return (StyleAttribute)attributes.get(ordinal);
	}

	public static StyleAttribute getAttribute(String name) throws IllegalArgumentException {
		try {
			Field f = StyleAttribute.class.getField(name);
			if(Modifier.isStatic(f.getModifiers()) && f.getType() == StyleAttribute.class) {
				return (StyleAttribute)f.get((Object)null);
			}
		} catch (Throwable throwable2) {
		}

		throw new IllegalArgumentException("No style attribute " + name);
	}
}
