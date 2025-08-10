package org.xmlpull.v1.wrapper.classic;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

import org.xmlpull.v1.XmlSerializer;

public class XmlSerializerDelegate implements XmlSerializer {
	protected XmlSerializer xs;

	public XmlSerializerDelegate(XmlSerializer serializer) {
		this.xs = serializer;
	}

	public String getName() {
		return this.xs.getName();
	}

	public void setPrefix(String prefix, String namespace) throws IOException, IllegalArgumentException, IllegalStateException {
		this.xs.setPrefix(prefix, namespace);
	}

	public void setOutput(OutputStream os, String encoding) throws IOException, IllegalArgumentException, IllegalStateException {
		this.xs.setOutput(os, encoding);
	}

	public void endDocument() throws IOException, IllegalArgumentException, IllegalStateException {
		this.xs.endDocument();
	}

	public void comment(String text) throws IOException, IllegalArgumentException, IllegalStateException {
		this.xs.comment(text);
	}

	public int getDepth() {
		return this.xs.getDepth();
	}

	public void setProperty(String name, Object value) throws IllegalArgumentException, IllegalStateException {
		this.xs.setProperty(name, value);
	}

	public void cdsect(String text) throws IOException, IllegalArgumentException, IllegalStateException {
		this.xs.cdsect(text);
	}

	public void setFeature(String name, boolean state) throws IllegalArgumentException, IllegalStateException {
		this.xs.setFeature(name, state);
	}

	public void entityRef(String text) throws IOException, IllegalArgumentException, IllegalStateException {
		this.xs.entityRef(text);
	}

	public void processingInstruction(String text) throws IOException, IllegalArgumentException, IllegalStateException {
		this.xs.processingInstruction(text);
	}

	public void setOutput(Writer writer) throws IOException, IllegalArgumentException, IllegalStateException {
		this.xs.setOutput(writer);
	}

	public void docdecl(String text) throws IOException, IllegalArgumentException, IllegalStateException {
		this.xs.docdecl(text);
	}

	public void flush() throws IOException {
		this.xs.flush();
	}

	public Object getProperty(String name) {
		return this.xs.getProperty(name);
	}

	public XmlSerializer startTag(String namespace, String name) throws IOException, IllegalArgumentException, IllegalStateException {
		return this.xs.startTag(namespace, name);
	}

	public void ignorableWhitespace(String text) throws IOException, IllegalArgumentException, IllegalStateException {
		this.xs.ignorableWhitespace(text);
	}

	public XmlSerializer text(String text) throws IOException, IllegalArgumentException, IllegalStateException {
		return this.xs.text(text);
	}

	public boolean getFeature(String name) {
		return this.xs.getFeature(name);
	}

	public XmlSerializer attribute(String namespace, String name, String value) throws IOException, IllegalArgumentException, IllegalStateException {
		return this.xs.attribute(namespace, name, value);
	}

	public void startDocument(String encoding, Boolean standalone) throws IOException, IllegalArgumentException, IllegalStateException {
		this.xs.startDocument(encoding, standalone);
	}

	public String getPrefix(String namespace, boolean generatePrefix) throws IllegalArgumentException {
		return this.xs.getPrefix(namespace, generatePrefix);
	}

	public String getNamespace() {
		return this.xs.getNamespace();
	}

	public XmlSerializer endTag(String namespace, String name) throws IOException, IllegalArgumentException, IllegalStateException {
		return this.xs.endTag(namespace, name);
	}

	public XmlSerializer text(char[] buf, int start, int len) throws IOException, IllegalArgumentException, IllegalStateException {
		return this.xs.text(buf, start, len);
	}
}
