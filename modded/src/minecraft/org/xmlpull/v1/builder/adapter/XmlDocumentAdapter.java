package org.xmlpull.v1.builder.adapter;

import org.xmlpull.v1.builder.Iterable;
import org.xmlpull.v1.builder.XmlComment;
import org.xmlpull.v1.builder.XmlContainer;
import org.xmlpull.v1.builder.XmlDoctype;
import org.xmlpull.v1.builder.XmlDocument;
import org.xmlpull.v1.builder.XmlElement;
import org.xmlpull.v1.builder.XmlNamespace;
import org.xmlpull.v1.builder.XmlNotation;
import org.xmlpull.v1.builder.XmlProcessingInstruction;

public class XmlDocumentAdapter implements XmlDocument {
	private XmlDocument target;

	public Object clone() throws CloneNotSupportedException {
		XmlDocumentAdapter ela = (XmlDocumentAdapter)super.clone();
		ela.target = (XmlDocument)this.target.clone();
		return ela;
	}

	public XmlDocumentAdapter(XmlDocument target) {
		this.target = target;
		this.fixImportedChildParent(target.getDocumentElement());
	}

	private void fixImportedChildParent(Object child) {
		if(child instanceof XmlElement) {
			XmlElement childEl = (XmlElement)child;
			XmlContainer childElParent = childEl.getParent();
			if(childElParent == this.target) {
				childEl.setParent(this);
			}
		}

	}

	public Iterable children() {
		return this.target.children();
	}

	public XmlElement getDocumentElement() {
		return this.target.getDocumentElement();
	}

	public XmlElement requiredElement(XmlNamespace n, String name) {
		return this.target.requiredElement(n, name);
	}

	public XmlElement element(XmlNamespace n, String name) {
		return this.target.element(n, name);
	}

	public XmlElement element(XmlNamespace n, String name, boolean create) {
		return this.target.element(n, name, create);
	}

	public Iterable notations() {
		return this.target.notations();
	}

	public Iterable unparsedEntities() {
		return this.target.unparsedEntities();
	}

	public String getBaseUri() {
		return this.target.getBaseUri();
	}

	public String getCharacterEncodingScheme() {
		return this.target.getCharacterEncodingScheme();
	}

	public void setCharacterEncodingScheme(String characterEncoding) {
		this.target.setCharacterEncodingScheme(characterEncoding);
	}

	public Boolean isStandalone() {
		return this.target.isStandalone();
	}

	public String getVersion() {
		return this.target.getVersion();
	}

	public boolean isAllDeclarationsProcessed() {
		return this.target.isAllDeclarationsProcessed();
	}

	public void setDocumentElement(XmlElement rootElement) {
		this.target.setDocumentElement(rootElement);
	}

	public void addChild(Object child) {
		this.target.addChild(child);
	}

	public void insertChild(int pos, Object child) {
		this.target.insertChild(pos, child);
	}

	public void removeAllChildren() {
		this.target.removeAllChildren();
	}

	public XmlComment newComment(String content) {
		return this.target.newComment(content);
	}

	public XmlComment addComment(String content) {
		return this.target.addComment(content);
	}

	public XmlDoctype newDoctype(String systemIdentifier, String publicIdentifier) {
		return this.target.newDoctype(systemIdentifier, publicIdentifier);
	}

	public XmlDoctype addDoctype(String systemIdentifier, String publicIdentifier) {
		return this.target.addDoctype(systemIdentifier, publicIdentifier);
	}

	public XmlElement addDocumentElement(String name) {
		return this.target.addDocumentElement(name);
	}

	public XmlElement addDocumentElement(XmlNamespace namespace, String name) {
		return this.target.addDocumentElement(namespace, name);
	}

	public XmlProcessingInstruction newProcessingInstruction(String target, String content) {
		return this.target.newProcessingInstruction(target, content);
	}

	public XmlProcessingInstruction addProcessingInstruction(String target, String content) {
		return this.target.addProcessingInstruction(target, content);
	}

	public void removeAllUnparsedEntities() {
		this.target.removeAllUnparsedEntities();
	}

	public XmlNotation addNotation(String name, String systemIdentifier, String publicIdentifier, String declarationBaseUri) {
		return this.target.addNotation(name, systemIdentifier, publicIdentifier, declarationBaseUri);
	}

	public void removeAllNotations() {
		this.target.removeAllNotations();
	}
}
