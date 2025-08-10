package org.xmlpull.v1.wrapper.classic;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.util.XmlPullUtil;
import org.xmlpull.v1.wrapper.XmlPullParserWrapper;

public class StaticXmlPullParserWrapper extends XmlPullParserDelegate implements XmlPullParserWrapper {
	public StaticXmlPullParserWrapper(XmlPullParser pp) {
		super(pp);
	}

	public String getAttributeValue(String name) {
		return XmlPullUtil.getAttributeValue(super.pp, name);
	}

	public String getRequiredAttributeValue(String name) throws IOException, XmlPullParserException {
		return XmlPullUtil.getRequiredAttributeValue(super.pp, (String)null, name);
	}

	public String getRequiredAttributeValue(String namespace, String name) throws IOException, XmlPullParserException {
		return XmlPullUtil.getRequiredAttributeValue(super.pp, namespace, name);
	}

	public String getRequiredElementText(String namespace, String name) throws IOException, XmlPullParserException {
		if(name == null) {
			throw new XmlPullParserException("name for element can not be null");
		} else {
			String text = null;
			this.nextStartTag(namespace, name);
			if(this.isNil()) {
				this.nextEndTag(namespace, name);
			} else {
				text = super.pp.nextText();
			}

			super.pp.require(3, namespace, name);
			return text;
		}
	}

	public boolean isNil() throws IOException, XmlPullParserException {
		boolean result = false;
		String value = super.pp.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
		if("true".equals(value)) {
			result = true;
		}

		return result;
	}

	public String getPITarget() throws IllegalStateException {
		return XmlPullUtil.getPITarget(super.pp);
	}

	public String getPIData() throws IllegalStateException {
		return XmlPullUtil.getPIData(super.pp);
	}

	public boolean matches(int type, String namespace, String name) throws XmlPullParserException {
		return XmlPullUtil.matches(super.pp, type, namespace, name);
	}

	public void nextStartTag() throws XmlPullParserException, IOException {
		if(super.pp.nextTag() != 2) {
			throw new XmlPullParserException("expected START_TAG and not " + super.pp.getPositionDescription());
		}
	}

	public void nextStartTag(String name) throws XmlPullParserException, IOException {
		super.pp.nextTag();
		super.pp.require(2, (String)null, name);
	}

	public void nextStartTag(String namespace, String name) throws XmlPullParserException, IOException {
		super.pp.nextTag();
		super.pp.require(2, namespace, name);
	}

	public void nextEndTag() throws XmlPullParserException, IOException {
		XmlPullUtil.nextEndTag(super.pp);
	}

	public void nextEndTag(String name) throws XmlPullParserException, IOException {
		XmlPullUtil.nextEndTag(super.pp, (String)null, name);
	}

	public void nextEndTag(String namespace, String name) throws XmlPullParserException, IOException {
		XmlPullUtil.nextEndTag(super.pp, namespace, name);
	}

	public String nextText(String namespace, String name) throws IOException, XmlPullParserException {
		return XmlPullUtil.nextText(super.pp, namespace, name);
	}

	public void skipSubTree() throws XmlPullParserException, IOException {
		XmlPullUtil.skipSubTree(super.pp);
	}

	public double readDouble() throws XmlPullParserException, IOException {
		String value = super.pp.nextText();

		double d;
		try {
			d = Double.parseDouble(value);
		} catch (NumberFormatException numberFormatException5) {
			if(!value.equals("INF") && !value.toLowerCase().equals("infinity")) {
				if(!value.equals("-INF") && !value.toLowerCase().equals("-infinity")) {
					if(!value.equals("NaN")) {
						throw new XmlPullParserException("can\'t parse double value \'" + value + "\'", this, numberFormatException5);
					}

					d = Double.NaN;
				} else {
					d = Double.NEGATIVE_INFINITY;
				}
			} else {
				d = Double.POSITIVE_INFINITY;
			}
		}

		return d;
	}

	public float readFloat() throws XmlPullParserException, IOException {
		String value = super.pp.nextText();

		float f;
		try {
			f = Float.parseFloat(value);
		} catch (NumberFormatException numberFormatException4) {
			if(!value.equals("INF") && !value.toLowerCase().equals("infinity")) {
				if(!value.equals("-INF") && !value.toLowerCase().equals("-infinity")) {
					if(!value.equals("NaN")) {
						throw new XmlPullParserException("can\'t parse float value \'" + value + "\'", this, numberFormatException4);
					}

					f = Float.NaN;
				} else {
					f = Float.NEGATIVE_INFINITY;
				}
			} else {
				f = Float.POSITIVE_INFINITY;
			}
		}

		return f;
	}

	private int parseDigits(String text, int offset, int length) throws XmlPullParserException {
		int value = 0;
		char chr;
		if(length > 9) {
			try {
				value = Integer.parseInt(text.substring(offset, offset + length));
			} catch (NumberFormatException numberFormatException7) {
				throw new XmlPullParserException(numberFormatException7.getMessage());
			}
		} else {
			for(int limit = offset + length; offset < limit; value = value * 10 + (chr - 48)) {
				chr = text.charAt(offset++);
				if(chr < 48 || chr > 57) {
					throw new XmlPullParserException("non-digit in number value", this, (Throwable)null);
				}
			}
		}

		return value;
	}

	private int parseInt(String text) throws XmlPullParserException {
		int offset = 0;
		int limit = text.length();
		if(limit == 0) {
			throw new XmlPullParserException("empty number value", this, (Throwable)null);
		} else {
			boolean negate = false;
			char chr = text.charAt(0);
			if(chr == 45) {
				if(limit > 9) {
					try {
						return Integer.parseInt(text);
					} catch (NumberFormatException numberFormatException7) {
						throw new XmlPullParserException(numberFormatException7.getMessage(), this, (Throwable)null);
					}
				}

				negate = true;
				++offset;
			} else if(chr == 43) {
				++offset;
			}

			if(offset >= limit) {
				throw new XmlPullParserException("Invalid number format", this, (Throwable)null);
			} else {
				int value = this.parseDigits(text, offset, limit - offset);
				return negate ? -value : value;
			}
		}
	}

	public int readInt() throws XmlPullParserException, IOException {
		try {
			int ex = this.parseInt(super.pp.nextText());
			return ex;
		} catch (NumberFormatException numberFormatException2) {
			throw new XmlPullParserException("can\'t parse int value", this, numberFormatException2);
		}
	}

	public String readString() throws XmlPullParserException, IOException {
		String xsiNil = super.pp.getAttributeValue("http://www.w3.org/2001/XMLSchema", "nil");
		if("true".equals(xsiNil)) {
			this.nextEndTag();
			return null;
		} else {
			return super.pp.nextText();
		}
	}

	public double readDoubleElement(String namespace, String name) throws XmlPullParserException, IOException {
		super.pp.require(2, namespace, name);
		return this.readDouble();
	}

	public float readFloatElement(String namespace, String name) throws XmlPullParserException, IOException {
		super.pp.require(2, namespace, name);
		return this.readFloat();
	}

	public int readIntElement(String namespace, String name) throws XmlPullParserException, IOException {
		super.pp.require(2, namespace, name);
		return this.readInt();
	}

	public String readStringElemet(String namespace, String name) throws XmlPullParserException, IOException {
		super.pp.require(2, namespace, name);
		return this.readString();
	}
}
