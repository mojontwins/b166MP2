package org.xmlpull.v1;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

public interface XmlSerializer {
	void setFeature(String string1, boolean z2) throws IllegalArgumentException, IllegalStateException;

	boolean getFeature(String string1);

	void setProperty(String string1, Object object2) throws IllegalArgumentException, IllegalStateException;

	Object getProperty(String string1);

	void setOutput(OutputStream outputStream1, String string2) throws IOException, IllegalArgumentException, IllegalStateException;

	void setOutput(Writer writer1) throws IOException, IllegalArgumentException, IllegalStateException;

	void startDocument(String string1, Boolean boolean2) throws IOException, IllegalArgumentException, IllegalStateException;

	void endDocument() throws IOException, IllegalArgumentException, IllegalStateException;

	void setPrefix(String string1, String string2) throws IOException, IllegalArgumentException, IllegalStateException;

	String getPrefix(String string1, boolean z2) throws IllegalArgumentException;

	int getDepth();

	String getNamespace();

	String getName();

	XmlSerializer startTag(String string1, String string2) throws IOException, IllegalArgumentException, IllegalStateException;

	XmlSerializer attribute(String string1, String string2, String string3) throws IOException, IllegalArgumentException, IllegalStateException;

	XmlSerializer endTag(String string1, String string2) throws IOException, IllegalArgumentException, IllegalStateException;

	XmlSerializer text(String string1) throws IOException, IllegalArgumentException, IllegalStateException;

	XmlSerializer text(char[] c1, int i2, int i3) throws IOException, IllegalArgumentException, IllegalStateException;

	void cdsect(String string1) throws IOException, IllegalArgumentException, IllegalStateException;

	void entityRef(String string1) throws IOException, IllegalArgumentException, IllegalStateException;

	void processingInstruction(String string1) throws IOException, IllegalArgumentException, IllegalStateException;

	void comment(String string1) throws IOException, IllegalArgumentException, IllegalStateException;

	void docdecl(String string1) throws IOException, IllegalArgumentException, IllegalStateException;

	void ignorableWhitespace(String string1) throws IOException, IllegalArgumentException, IllegalStateException;

	void flush() throws IOException;
}
