package de.matthiasmann.twl.textarea;

import de.matthiasmann.twl.utils.ParameterStringParser;
import de.matthiasmann.twl.utils.TextUtil;

import java.util.HashMap;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CSSStyle extends Style {
	static final HashMap PRE = new HashMap();
	static final HashMap BREAKWORD = new HashMap();
	static final HashMap OLT = new HashMap();

	static {
		PRE.put("pre", Boolean.TRUE);
		PRE.put("normal", Boolean.FALSE);
		BREAKWORD.put("normal", Boolean.FALSE);
		BREAKWORD.put("break-word", Boolean.TRUE);
		OrderedListType upper_alpha = new OrderedListType("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
		OrderedListType lower_alpha = new OrderedListType("abcdefghijklmnopqrstuvwxyz");
		OLT.put("decimal", OrderedListType.DECIMAL);
		OLT.put("upper-alpha", upper_alpha);
		OLT.put("lower-alpha", lower_alpha);
		OLT.put("upper-latin", upper_alpha);
		OLT.put("lower-latin", lower_alpha);
		OLT.put("upper-roman", createRoman(false));
		OLT.put("lower-roman", createRoman(true));
		OLT.put("lower-greek", new OrderedListType("\u03b1\u03b2\u03b3\u03b4\u03b5\u03b6\u03b7\u03b8\u03b9\u03ba\u03bb\u03bc\u03bd\u03be\u03bf\u03c0\u03c1\u03c3\u03c4\u03c5\u03c6\u03c7\u03c8\u03c9"));
		OLT.put("upper-norwegian", new OrderedListType("ABCDEFGHIJKLMNOPQRSTUVWXYZ\u00c6\u00d8\u00c5"));
		OLT.put("lower-norwegian", new OrderedListType("abcdefghijklmnopqrstuvwxyz\u00e6\u00f8\u00e5"));
		OLT.put("upper-russian-short", new OrderedListType("\u0410\u0411\u0412\u0413\u0414\u0415\u0416\u0417\u0418\u041a\u041b\u041c\u041d\u041e\u041f\u0420\u0421\u0422\u0423\u0424\u0425\u0426\u0427\u0428\u0429\u042d\u042e\u042f"));
		OLT.put("lower-russian-short", new OrderedListType("\u0430\u0431\u0432\u0433\u0434\u0435\u0436\u0437\u0438\u043a\u043b\u043c\u043d\u043e\u043f\u0440\u0441\u0442\u0443\u0444\u0445\u0446\u0447\u0448\u0449\u044d\u044e\u044f"));
	}

	protected CSSStyle() {
	}

	public CSSStyle(String cssStyle) {
		this.parseCSS(cssStyle);
	}

	public CSSStyle(Style parent, StyleSheetKey styleSheetKey, String cssStyle) {
		super(parent, styleSheetKey);
		this.parseCSS(cssStyle);
	}

	private void parseCSS(String style) {
		ParameterStringParser psp = new ParameterStringParser(style, ';', ':');
		psp.setTrim(true);

		while(psp.next()) {
			try {
				this.parseCSSAttribute(psp.getKey(), psp.getValue());
			} catch (IllegalArgumentException illegalArgumentException4) {
				Logger.getLogger(CSSStyle.class.getName()).log(Level.SEVERE, "Unable to parse CSS attribute: " + psp.getKey() + "=" + psp.getValue(), illegalArgumentException4);
			}
		}

	}

	protected void parseCSSAttribute(String key, String value) {
		if(key.startsWith("margin")) {
			this.parseBox(key.substring(6), value, StyleAttribute.MARGIN);
		} else if(key.startsWith("padding")) {
			this.parseBox(key.substring(7), value, StyleAttribute.PADDING);
		} else if("text-indent".equals(key)) {
			this.parseValueUnit(StyleAttribute.TEXT_IDENT, value);
		} else if(!"font-family".equals(key) && !"font".equals(key)) {
			if("text-align".equals(key)) {
				this.parseEnum(StyleAttribute.HORIZONTAL_ALIGNMENT, value);
			} else if("vertical-align".equals(key)) {
				this.parseEnum(StyleAttribute.VERTICAL_ALIGNMENT, value);
			} else if("white-space".equals(key)) {
				this.parseEnum(StyleAttribute.PREFORMATTED, PRE, value);
			} else if("word-wrap".equals(key)) {
				this.parseEnum(StyleAttribute.BREAKWORD, BREAKWORD, value);
			} else if("list-style-image".equals(key)) {
				this.parseURL(StyleAttribute.LIST_STYLE_IMAGE, value);
			} else if("list-style-type".equals(key)) {
				this.parseEnum(StyleAttribute.LIST_STYLE_TYPE, OLT, value);
			} else if("clear".equals(key)) {
				this.parseEnum(StyleAttribute.CLEAR, value);
			} else if("float".equals(key)) {
				this.parseEnum(StyleAttribute.FLOAT_POSITION, value);
			} else if("display".equals(key)) {
				this.parseEnum(StyleAttribute.DISPLAY, value);
			} else if("width".equals(key)) {
				this.parseValueUnit(StyleAttribute.WIDTH, value);
			} else if("height".equals(key)) {
				this.parseValueUnit(StyleAttribute.HEIGHT, value);
			} else if("background-image".equals(key)) {
				this.parseURL(StyleAttribute.BACKGROUND_IMAGE, value);
			} else {
				throw new IllegalArgumentException("Unsupported key: " + key);
			}
		} else {
			this.put(StyleAttribute.FONT_NAME, value);
		}
	}

	private void parseBox(String key, String value, BoxAttribute box) {
		if("-top".equals(key)) {
			this.parseValueUnit(box.top, value);
		} else if("-left".equals(key)) {
			this.parseValueUnit(box.left, value);
		} else if("-right".equals(key)) {
			this.parseValueUnit(box.right, value);
		} else if("-bottom".equals(key)) {
			this.parseValueUnit(box.bottom, value);
		} else if("".equals(key)) {
			Value[] vu = this.parseValueUnits(value);
			switch(vu.length) {
			case 1:
				this.put(box.top, vu[0]);
				this.put(box.left, vu[0]);
				this.put(box.right, vu[0]);
				this.put(box.bottom, vu[0]);
				break;
			case 2:
				this.put(box.top, vu[0]);
				this.put(box.left, vu[1]);
				this.put(box.right, vu[1]);
				this.put(box.bottom, vu[0]);
				break;
			case 3:
				this.put(box.top, vu[0]);
				this.put(box.left, vu[1]);
				this.put(box.right, vu[1]);
				this.put(box.bottom, vu[2]);
				break;
			case 4:
				this.put(box.top, vu[0]);
				this.put(box.left, vu[3]);
				this.put(box.right, vu[1]);
				this.put(box.bottom, vu[2]);
				break;
			default:
				throw new IllegalArgumentException("Invalid number of margin values: " + vu.length);
			}
		}

	}

	private Value parseValueUnit(String value) {
		byte suffixLength = 2;
		Value.Unit unit;
		if(value.endsWith("px")) {
			unit = Value.Unit.PX;
		} else if(value.endsWith("em")) {
			unit = Value.Unit.EM;
		} else if(value.endsWith("ex")) {
			unit = Value.Unit.EX;
		} else {
			if(!value.endsWith("%")) {
				if("0".equals(value)) {
					return Value.ZERO_PX;
				}

				if("auto".equals(value)) {
					return Value.AUTO;
				}

				throw new IllegalArgumentException("Unknown numeric suffix: " + value);
			}

			suffixLength = 1;
			unit = Value.Unit.PERCENT;
		}

		String numberPart = value.substring(0, value.length() - suffixLength).trim();
		return new Value(Float.parseFloat(numberPart), unit);
	}

	private Value[] parseValueUnits(String value) {
		String[] parts = value.split("\\s+");
		Value[] result = new Value[parts.length];

		for(int i = 0; i < parts.length; ++i) {
			result[i] = this.parseValueUnit(parts[i]);
		}

		return result;
	}

	private void parseValueUnit(StyleAttribute attribute, String value) {
		this.put(attribute, this.parseValueUnit(value));
	}

	private void parseEnum(StyleAttribute attribute, HashMap map, String value) {
		Object obj = map.get(value);
		if(obj == null) {
			throw new IllegalArgumentException("Unknown value: " + value);
		} else {
			this.put(attribute, obj);
		}
	}

	private void parseEnum(StyleAttribute attribute, String value) {
		Enum obj = Enum.valueOf(attribute.getDataType(), value.toUpperCase(Locale.ENGLISH));
		this.put(attribute, obj);
	}

	private void parseURL(StyleAttribute attribute, String value) {
		if(value.startsWith("url(") && value.endsWith(")")) {
			value = value.substring(4, value.length() - 1).trim();
			if(value.startsWith("\"") && value.endsWith("\"") || value.startsWith("\'") && value.endsWith("\'")) {
				value = value.substring(1, value.length() - 1);
			}
		}

		this.put(attribute, value);
	}

	static OrderedListType createRoman(final boolean lowercase) {
		return new OrderedListType() {
			public String format(int nr) {
				if(nr >= 1 && nr <= 39999) {
					String str = TextUtil.toRomanNumberString(nr);
					return lowercase ? str.toLowerCase() : str;
				} else {
					return Integer.toString(nr);
				}
			}
		};
	}
}
