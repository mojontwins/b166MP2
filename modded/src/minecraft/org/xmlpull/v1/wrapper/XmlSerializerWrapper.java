package org.xmlpull.v1.wrapper;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

public interface XmlSerializerWrapper extends XmlSerializer {
	String NO_NAMESPACE = "";
	String XSI_NS = "http://www.w3.org/2001/XMLSchema-instance";
	String XSD_NS = "http://www.w3.org/2001/XMLSchema";

	String getCurrentNamespaceForElements();

	String setCurrentNamespaceForElements(String string1);

	XmlSerializerWrapper attribute(String string1, String string2) throws IOException, IllegalArgumentException, IllegalStateException;

	XmlSerializerWrapper startTag(String string1) throws IOException, IllegalArgumentException, IllegalStateException;

	XmlSerializerWrapper endTag(String string1) throws IOException, IllegalArgumentException, IllegalStateException;

	XmlSerializerWrapper element(String string1, String string2, String string3) throws IOException, XmlPullParserException;

	XmlSerializerWrapper element(String string1, String string2) throws IOException, XmlPullParserException;

	void fragment(String string1) throws IOException, IllegalArgumentException, IllegalStateException, XmlPullParserException;

	void event(XmlPullParser xmlPullParser1) throws IOException, IllegalArgumentException, IllegalStateException, XmlPullParserException;

	String escapeText(String string1) throws IllegalArgumentException;

	String escapeAttributeValue(String string1) throws IllegalArgumentException;
}
