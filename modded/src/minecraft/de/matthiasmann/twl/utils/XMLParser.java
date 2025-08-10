package de.matthiasmann.twl.utils;

import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.BitSet;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

public class XMLParser implements Closeable {
	private static final Class[] XPP_CLASS = new Class[]{XmlPullParser.class};
	private final XmlPullParser xpp;
	private final String source;
	private final InputStream inputStream;
	private final BitSet unusedAttributes = new BitSet();
	private String loggerName = XMLParser.class.getName();

	public XMLParser(XmlPullParser xpp, String source) {
		if(xpp == null) {
			throw new NullPointerException("xpp");
		} else {
			this.xpp = xpp;
			this.source = source;
			this.inputStream = null;
		}
	}

	public XMLParser(URL url) throws XmlPullParserException, IOException {
		if(url == null) {
			throw new NullPointerException("url");
		} else {
			XmlPullParser xpp_ = null;
			InputStream is = null;
			this.source = url.toString();

			try {
				xpp_ = (XmlPullParser)url.getContent(XPP_CLASS);
			} catch (IOException iOException5) {
			}

			if(xpp_ == null) {
				XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
				factory.setNamespaceAware(false);
				factory.setValidating(false);
				xpp_ = factory.newPullParser();
				is = url.openStream();
				if(is == null) {
					throw new FileNotFoundException(this.source);
				}

				xpp_.setInput(is, "UTF8");
			}

			this.xpp = xpp_;
			this.inputStream = is;
		}
	}

	public void close() throws IOException {
		if(this.inputStream != null) {
			this.inputStream.close();
		}

	}

	public void setLoggerName(String loggerName) {
		this.loggerName = loggerName;
	}

	public int next() throws XmlPullParserException, IOException {
		this.warnUnusedAttributes();
		int type = this.xpp.next();
		this.handleType(type);
		return type;
	}

	public int nextTag() throws XmlPullParserException, IOException {
		this.warnUnusedAttributes();
		int type = this.xpp.nextTag();
		this.handleType(type);
		return type;
	}

	public String nextText() throws XmlPullParserException, IOException {
		this.warnUnusedAttributes();
		return this.xpp.nextText();
	}

	public char[] nextText(int[] startAndLength) throws XmlPullParserException, IOException {
		this.warnUnusedAttributes();

		while(true) {
			int token = this.xpp.nextToken();
			switch(token) {
			case 4:
				return this.xpp.getTextCharacters(startAndLength);
			case 5:
			case 7:
			case 8:
			default:
				this.handleType(token);
				return null;
			case 6:
				String replaced = this.xpp.getText();
				startAndLength[0] = 0;
				startAndLength[1] = replaced.length();
				return replaced.toCharArray();
			case 9:
			}
		}
	}

	public void skipText() throws XmlPullParserException, IOException {
		for(int token = this.xpp.getEventType(); token == 4 || token == 6 || token == 9; token = this.xpp.nextToken()) {
		}

	}

	public boolean isStartTag() throws XmlPullParserException {
		return this.xpp.getEventType() == 2;
	}

	public boolean isEndTag() throws XmlPullParserException {
		return this.xpp.getEventType() == 3;
	}

	public String getPositionDescription() {
		String desc = this.xpp.getPositionDescription();
		return this.source != null ? desc + " in " + this.source : desc;
	}

	public String getName() {
		return this.xpp.getName();
	}

	public void require(int type, String namespace, String name) throws XmlPullParserException, IOException {
		this.xpp.require(type, namespace, name);
	}

	public String getAttributeValue(int index) {
		this.unusedAttributes.clear(index);
		return this.xpp.getAttributeValue(index);
	}

	public String getAttributeNamespace(int index) {
		return this.xpp.getAttributeNamespace(index);
	}

	public String getAttributeName(int index) {
		return this.xpp.getAttributeName(index);
	}

	public int getAttributeCount() {
		return this.xpp.getAttributeCount();
	}

	public String getAttributeValue(String namespace, String name) {
		int i = 0;

		for(int n = this.xpp.getAttributeCount(); i < n; ++i) {
			if((namespace == null || namespace.equals(this.xpp.getAttributeNamespace(i))) && name.equals(this.xpp.getAttributeName(i))) {
				return this.getAttributeValue(i);
			}
		}

		return null;
	}

	public String getAttributeNotNull(String attribute) throws XmlPullParserException {
		String value = this.getAttributeValue((String)null, attribute);
		if(value == null) {
			this.missingAttribute(attribute);
		}

		return value;
	}

	public boolean parseBoolFromAttribute(String attribName) throws XmlPullParserException {
		return this.parseBool(this.getAttributeNotNull(attribName));
	}

	public boolean parseBoolFromText() throws XmlPullParserException, IOException {
		return this.parseBool(this.nextText());
	}

	public boolean parseBoolFromAttribute(String attribName, boolean defaultValue) throws XmlPullParserException {
		String value = this.getAttributeValue((String)null, attribName);
		return value == null ? defaultValue : this.parseBool(value);
	}

	public int parseIntFromAttribute(String attribName) throws XmlPullParserException {
		return this.parseInt(this.getAttributeNotNull(attribName));
	}

	public int parseIntFromAttribute(String attribName, int defaultValue) throws XmlPullParserException {
		String value = this.getAttributeValue((String)null, attribName);
		return value == null ? defaultValue : this.parseInt(value);
	}

	public float parseFloatFromAttribute(String attribName) throws XmlPullParserException {
		return this.parseFloat(this.getAttributeNotNull(attribName));
	}

	public float parseFloatFromAttribute(String attribName, float defaultValue) throws XmlPullParserException {
		String value = this.getAttributeValue((String)null, attribName);
		return value == null ? defaultValue : this.parseFloat(value);
	}

	public Enum parseEnumFromAttribute(String attribName, Class enumClazz) throws XmlPullParserException {
		return this.parseEnum(enumClazz, this.getAttributeNotNull(attribName));
	}

	public Enum parseEnumFromAttribute(String attribName, Class enumClazz, Enum defaultValue) throws XmlPullParserException {
		String value = this.getAttributeValue((String)null, attribName);
		return value == null ? defaultValue : this.parseEnum(enumClazz, value);
	}

	public Enum parseEnumFromText(Class enumClazz) throws XmlPullParserException, IOException {
		return this.parseEnum(enumClazz, this.nextText());
	}

	public Map getUnusedAttributes() {
		if(this.unusedAttributes.isEmpty()) {
			return Collections.emptyMap();
		} else {
			LinkedHashMap result = new LinkedHashMap();
			int i = -1;

			while((i = this.unusedAttributes.nextSetBit(i + 1)) >= 0) {
				result.put(this.xpp.getAttributeName(i), this.xpp.getAttributeValue(i));
			}

			this.unusedAttributes.clear();
			return result;
		}
	}

	public void ignoreOtherAttributes() {
		this.unusedAttributes.clear();
	}

	public XmlPullParserException error(String msg) {
		return new XmlPullParserException(msg, this.xpp, (Throwable)null);
	}

	public XmlPullParserException error(String msg, Throwable cause) {
		return (XmlPullParserException)(new XmlPullParserException(msg, this.xpp, cause)).initCause(cause);
	}

	public XmlPullParserException unexpected() {
		return new XmlPullParserException("Unexpected \'" + this.xpp.getName() + "\'", this.xpp, (Throwable)null);
	}

	protected Enum parseEnum(Class enumClazz, String value) throws XmlPullParserException {
		try {
			return Enum.valueOf(enumClazz, value.toUpperCase(Locale.ENGLISH));
		} catch (IllegalArgumentException illegalArgumentException5) {
			try {
				return Enum.valueOf(enumClazz, value);
			} catch (IllegalArgumentException illegalArgumentException4) {
				throw new XmlPullParserException("Unknown enum value \"" + value + "\" for enum class " + enumClazz, this.xpp, (Throwable)null);
			}
		}
	}

	protected boolean parseBool(String value) throws XmlPullParserException {
		if("true".equals(value)) {
			return true;
		} else if("false".equals(value)) {
			return false;
		} else {
			throw new XmlPullParserException("boolean value must be \'true\' or \'false\'", this.xpp, (Throwable)null);
		}
	}

	protected int parseInt(String value) throws XmlPullParserException {
		try {
			return Integer.parseInt(value);
		} catch (NumberFormatException numberFormatException3) {
			throw (XmlPullParserException)(new XmlPullParserException("Unable to parse integer", this.xpp, numberFormatException3)).initCause(numberFormatException3);
		}
	}

	protected float parseFloat(String value) throws XmlPullParserException {
		try {
			return Float.parseFloat(value);
		} catch (NumberFormatException numberFormatException3) {
			throw (XmlPullParserException)(new XmlPullParserException("Unable to parse float", this.xpp, numberFormatException3)).initCause(numberFormatException3);
		}
	}

	protected void missingAttribute(String attribute) throws XmlPullParserException {
		throw new XmlPullParserException("missing \'" + attribute + "\' on \'" + this.xpp.getName() + "\'", this.xpp, (Throwable)null);
	}

	protected void handleType(int type) {
		this.unusedAttributes.clear();
		switch(type) {
		case 2:
			this.unusedAttributes.set(0, this.xpp.getAttributeCount());
		default:
		}
	}

	protected void warnUnusedAttributes() {
		if(!this.unusedAttributes.isEmpty()) {
			String positionDescription = this.getPositionDescription();
			int i = -1;

			while((i = this.unusedAttributes.nextSetBit(i + 1)) >= 0) {
				this.getLogger().log(Level.WARNING, "Unused attribute \'\'{0}\'\' on \'\'{1}\'\' at {2}", new Object[]{this.xpp.getAttributeName(i), this.xpp.getName(), positionDescription});
			}
		}

	}

	protected Logger getLogger() {
		return Logger.getLogger(this.loggerName);
	}
}
