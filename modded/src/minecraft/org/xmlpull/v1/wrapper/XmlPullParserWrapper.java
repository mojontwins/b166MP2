package org.xmlpull.v1.wrapper;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public interface XmlPullParserWrapper extends XmlPullParser {
	String XSI_NS = "http://www.w3.org/2001/XMLSchema-instance";
	String XSD_NS = "http://www.w3.org/2001/XMLSchema";

	String getAttributeValue(String string1);

	String getPITarget() throws IllegalStateException;

	String getPIData() throws IllegalStateException;

	String getRequiredAttributeValue(String string1) throws IOException, XmlPullParserException;

	String getRequiredAttributeValue(String string1, String string2) throws IOException, XmlPullParserException;

	String getRequiredElementText(String string1, String string2) throws IOException, XmlPullParserException;

	boolean isNil() throws IOException, XmlPullParserException;

	boolean matches(int i1, String string2, String string3) throws XmlPullParserException;

	void nextStartTag() throws XmlPullParserException, IOException;

	void nextStartTag(String string1) throws XmlPullParserException, IOException;

	void nextStartTag(String string1, String string2) throws XmlPullParserException, IOException;

	void nextEndTag() throws XmlPullParserException, IOException;

	void nextEndTag(String string1) throws XmlPullParserException, IOException;

	void nextEndTag(String string1, String string2) throws XmlPullParserException, IOException;

	String nextText(String string1, String string2) throws IOException, XmlPullParserException;

	void skipSubTree() throws XmlPullParserException, IOException;
}
