package org.xmlpull.v1.builder;

import java.util.Iterator;

public interface XmlElement extends XmlContainer, XmlContained, Cloneable {
	String NO_NAMESPACE = "";

	Object clone() throws CloneNotSupportedException;

	String getBaseUri();

	void setBaseUri(String string1);

	XmlContainer getRoot();

	XmlContainer getParent();

	void setParent(XmlContainer xmlContainer1);

	XmlNamespace getNamespace();

	String getNamespaceName();

	void setNamespace(XmlNamespace xmlNamespace1);

	String getName();

	void setName(String string1);

	Iterator attributes();

	XmlAttribute addAttribute(XmlAttribute xmlAttribute1);

	XmlAttribute addAttribute(String string1, String string2);

	XmlAttribute addAttribute(XmlNamespace xmlNamespace1, String string2, String string3);

	XmlAttribute addAttribute(String string1, XmlNamespace xmlNamespace2, String string3, String string4);

	XmlAttribute addAttribute(String string1, XmlNamespace xmlNamespace2, String string3, String string4, boolean z5);

	XmlAttribute addAttribute(String string1, String string2, String string3, String string4, String string5, boolean z6);

	void ensureAttributeCapacity(int i1);

	String getAttributeValue(String string1, String string2);

	XmlAttribute attribute(String string1);

	XmlAttribute attribute(XmlNamespace xmlNamespace1, String string2);

	/** @deprecated */
	XmlAttribute findAttribute(String string1, String string2);

	boolean hasAttributes();

	void removeAttribute(XmlAttribute xmlAttribute1);

	void removeAllAttributes();

	Iterator namespaces();

	XmlNamespace declareNamespace(String string1, String string2);

	XmlNamespace declareNamespace(XmlNamespace xmlNamespace1);

	void ensureNamespaceDeclarationsCapacity(int i1);

	boolean hasNamespaceDeclarations();

	XmlNamespace lookupNamespaceByPrefix(String string1);

	XmlNamespace lookupNamespaceByName(String string1);

	XmlNamespace newNamespace(String string1);

	XmlNamespace newNamespace(String string1, String string2);

	void removeAllNamespaceDeclarations();

	Iterator children();

	void addChild(Object object1);

	void addChild(int i1, Object object2);

	XmlElement addElement(XmlElement xmlElement1);

	XmlElement addElement(int i1, XmlElement xmlElement2);

	XmlElement addElement(String string1);

	XmlElement addElement(XmlNamespace xmlNamespace1, String string2);

	boolean hasChildren();

	boolean hasChild(Object object1);

	void ensureChildrenCapacity(int i1);

	/** @deprecated */
	XmlElement findElementByName(String string1);

	/** @deprecated */
	XmlElement findElementByName(String string1, String string2);

	/** @deprecated */
	XmlElement findElementByName(String string1, XmlElement xmlElement2);

	/** @deprecated */
	XmlElement findElementByName(String string1, String string2, XmlElement xmlElement3);

	XmlElement element(int i1);

	XmlElement requiredElement(XmlNamespace xmlNamespace1, String string2) throws XmlBuilderException;

	XmlElement element(XmlNamespace xmlNamespace1, String string2);

	XmlElement element(XmlNamespace xmlNamespace1, String string2, boolean z3);

	Iterable elements(XmlNamespace xmlNamespace1, String string2);

	void insertChild(int i1, Object object2);

	XmlElement newElement(String string1);

	XmlElement newElement(XmlNamespace xmlNamespace1, String string2);

	XmlElement newElement(String string1, String string2);

	void removeAllChildren();

	void removeChild(Object object1);

	void replaceChild(Object object1, Object object2);

	Iterable requiredElementContent();

	String requiredTextContent();

	void replaceChildrenWithText(String string1);
}
