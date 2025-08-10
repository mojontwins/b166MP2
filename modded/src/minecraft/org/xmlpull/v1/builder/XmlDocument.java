package org.xmlpull.v1.builder;

public interface XmlDocument extends XmlContainer, Cloneable {
	Object clone() throws CloneNotSupportedException;

	Iterable children();

	XmlElement getDocumentElement();

	XmlElement requiredElement(XmlNamespace xmlNamespace1, String string2);

	XmlElement element(XmlNamespace xmlNamespace1, String string2);

	XmlElement element(XmlNamespace xmlNamespace1, String string2, boolean z3);

	Iterable notations();

	Iterable unparsedEntities();

	String getBaseUri();

	String getCharacterEncodingScheme();

	void setCharacterEncodingScheme(String string1);

	Boolean isStandalone();

	String getVersion();

	boolean isAllDeclarationsProcessed();

	void setDocumentElement(XmlElement xmlElement1);

	void addChild(Object object1);

	void insertChild(int i1, Object object2);

	void removeAllChildren();

	XmlComment newComment(String string1);

	XmlComment addComment(String string1);

	XmlDoctype newDoctype(String string1, String string2);

	XmlDoctype addDoctype(String string1, String string2);

	XmlElement addDocumentElement(String string1);

	XmlElement addDocumentElement(XmlNamespace xmlNamespace1, String string2);

	XmlProcessingInstruction newProcessingInstruction(String string1, String string2);

	XmlProcessingInstruction addProcessingInstruction(String string1, String string2);

	void removeAllUnparsedEntities();

	XmlNotation addNotation(String string1, String string2, String string3, String string4);

	void removeAllNotations();
}
