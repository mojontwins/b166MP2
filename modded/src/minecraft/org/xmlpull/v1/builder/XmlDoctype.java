package org.xmlpull.v1.builder;

import java.util.Iterator;

public interface XmlDoctype extends XmlContainer {
	String getSystemIdentifier();

	String getPublicIdentifier();

	Iterator children();

	XmlDocument getParent();

	XmlProcessingInstruction addProcessingInstruction(String string1, String string2);

	void removeAllProcessingInstructions();
}
