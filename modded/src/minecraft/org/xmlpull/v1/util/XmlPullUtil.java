package org.xmlpull.v1.util;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

public class XmlPullUtil {
	public static final String XSI_NS = "http://www.w3.org/2001/XMLSchema-instance";

	public static String getAttributeValue(XmlPullParser pp, String name) {
		return pp.getAttributeValue("", name);
	}

	public static String getPITarget(XmlPullParser pp) throws IllegalStateException {
		int eventType;
		try {
			eventType = pp.getEventType();
		} catch (XmlPullParserException xmlPullParserException4) {
			throw new IllegalStateException("could not determine parser state: " + xmlPullParserException4 + pp.getPositionDescription());
		}

		if(eventType != 8) {
			throw new IllegalStateException("parser must be on processing instruction and not " + XmlPullParser.TYPES[eventType] + pp.getPositionDescription());
		} else {
			String PI = pp.getText();

			for(int i = 0; i < PI.length(); ++i) {
				if(isS(PI.charAt(i))) {
					return PI.substring(0, i);
				}
			}

			return PI;
		}
	}

	public static String getPIData(XmlPullParser pp) throws IllegalStateException {
		int eventType;
		try {
			eventType = pp.getEventType();
		} catch (XmlPullParserException xmlPullParserException5) {
			throw new IllegalStateException("could not determine parser state: " + xmlPullParserException5 + pp.getPositionDescription());
		}

		if(eventType != 8) {
			throw new IllegalStateException("parser must be on processing instruction and not " + XmlPullParser.TYPES[eventType] + pp.getPositionDescription());
		} else {
			String PI = pp.getText();
			int pos = -1;

			for(int i = 0; i < PI.length(); ++i) {
				if(isS(PI.charAt(i))) {
					pos = i;
				} else if(pos > 0) {
					return PI.substring(i);
				}
			}

			return "";
		}
	}

	private static boolean isS(char ch) {
		return ch == 32 || ch == 10 || ch == 13 || ch == 9;
	}

	public static void skipSubTree(XmlPullParser pp) throws XmlPullParserException, IOException {
		pp.require(2, (String)null, (String)null);
		int level = 1;

		while(level > 0) {
			int eventType = pp.next();
			if(eventType == 3) {
				--level;
			} else if(eventType == 2) {
				++level;
			}
		}

	}

	public static void nextStartTag(XmlPullParser pp) throws XmlPullParserException, IOException {
		if(pp.nextTag() != 2) {
			throw new XmlPullParserException("expected START_TAG and not " + pp.getPositionDescription());
		}
	}

	public static void nextStartTag(XmlPullParser pp, String name) throws XmlPullParserException, IOException {
		pp.nextTag();
		pp.require(2, (String)null, name);
	}

	public static void nextStartTag(XmlPullParser pp, String namespace, String name) throws XmlPullParserException, IOException {
		pp.nextTag();
		pp.require(2, namespace, name);
	}

	public static void nextEndTag(XmlPullParser pp, String namespace, String name) throws XmlPullParserException, IOException {
		pp.nextTag();
		pp.require(3, namespace, name);
	}

	public static String nextText(XmlPullParser pp, String namespace, String name) throws IOException, XmlPullParserException {
		if(name == null) {
			throw new XmlPullParserException("name for element can not be null");
		} else {
			pp.require(2, namespace, name);
			return pp.nextText();
		}
	}

	public static String getRequiredAttributeValue(XmlPullParser pp, String namespace, String name) throws IOException, XmlPullParserException {
		String value = pp.getAttributeValue(namespace, name);
		if(value == null) {
			throw new XmlPullParserException("required attribute " + name + " is not present");
		} else {
			return value;
		}
	}

	public static void nextEndTag(XmlPullParser pp) throws XmlPullParserException, IOException {
		if(pp.nextTag() != 3) {
			throw new XmlPullParserException("expected END_TAG and not" + pp.getPositionDescription());
		}
	}

	public static boolean matches(XmlPullParser pp, int type, String namespace, String name) throws XmlPullParserException {
		boolean matches = type == pp.getEventType() && (namespace == null || namespace.equals(pp.getNamespace())) && (name == null || name.equals(pp.getName()));
		return matches;
	}

	public static void writeSimpleElement(XmlSerializer serializer, String namespace, String elementName, String elementText) throws IOException, XmlPullParserException {
		if(elementName == null) {
			throw new XmlPullParserException("name for element can not be null");
		} else {
			serializer.startTag(namespace, elementName);
			if(elementText == null) {
				serializer.attribute("http://www.w3.org/2001/XMLSchema-instance", "nil", "true");
			} else {
				serializer.text(elementText);
			}

			serializer.endTag(namespace, elementName);
		}
	}
}
