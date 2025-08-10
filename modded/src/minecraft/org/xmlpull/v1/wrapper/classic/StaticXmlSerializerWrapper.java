package org.xmlpull.v1.wrapper.classic;

import java.io.IOException;
import java.io.StringReader;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;
import org.xmlpull.v1.wrapper.XmlPullParserWrapper;
import org.xmlpull.v1.wrapper.XmlPullWrapperFactory;
import org.xmlpull.v1.wrapper.XmlSerializerWrapper;

public class StaticXmlSerializerWrapper extends XmlSerializerDelegate implements XmlSerializerWrapper {
	private static final String PROPERTY_XMLDECL_STANDALONE = "http://xmlpull.org/v1/doc/features.html#xmldecl-standalone";
	private static final boolean TRACE_SIZING = false;
	protected String currentNs;
	protected XmlPullWrapperFactory wf;
	protected XmlPullParserWrapper fragmentParser;
	protected int namespaceEnd = 0;
	protected String[] namespacePrefix = new String[8];
	protected String[] namespaceUri = new String[this.namespacePrefix.length];
	protected int[] namespaceDepth = new int[this.namespacePrefix.length];

	public StaticXmlSerializerWrapper(XmlSerializer xs, XmlPullWrapperFactory wf) {
		super(xs);
		this.wf = wf;
	}

	public String getCurrentNamespaceForElements() {
		return this.currentNs;
	}

	public String setCurrentNamespaceForElements(String value) {
		String old = this.currentNs;
		this.currentNs = value;
		return old;
	}

	public XmlSerializerWrapper attribute(String name, String value) throws IOException, IllegalArgumentException, IllegalStateException {
		super.xs.attribute((String)null, name, value);
		return this;
	}

	public XmlSerializerWrapper startTag(String name) throws IOException, IllegalArgumentException, IllegalStateException {
		super.xs.startTag(this.currentNs, name);
		return this;
	}

	public XmlSerializerWrapper endTag(String name) throws IOException, IllegalArgumentException, IllegalStateException {
		this.endTag(this.currentNs, name);
		return this;
	}

	public XmlSerializerWrapper element(String elementName, String elementText) throws IOException, XmlPullParserException {
		return this.element(this.currentNs, elementName, elementText);
	}

	public XmlSerializerWrapper element(String namespace, String elementName, String elementText) throws IOException, XmlPullParserException {
		if(elementName == null) {
			throw new XmlPullParserException("name for element can not be null");
		} else {
			super.xs.startTag(namespace, elementName);
			if(elementText == null) {
				super.xs.attribute("http://www.w3.org/2001/XMLSchema-instance", "nil", "true");
			} else {
				super.xs.text(elementText);
			}

			super.xs.endTag(namespace, elementName);
			return this;
		}
	}

	private void ensureNamespacesCapacity() {
		int newSize = this.namespaceEnd > 7 ? 2 * this.namespaceEnd : 8;
		String[] newNamespacePrefix = new String[newSize];
		String[] newNamespaceUri = new String[newSize];
		int[] newNamespaceDepth = new int[newSize];
		if(this.namespacePrefix != null) {
			System.arraycopy(this.namespacePrefix, 0, newNamespacePrefix, 0, this.namespaceEnd);
			System.arraycopy(this.namespaceUri, 0, newNamespaceUri, 0, this.namespaceEnd);
			System.arraycopy(this.namespaceDepth, 0, newNamespaceDepth, 0, this.namespaceEnd);
		}

		this.namespacePrefix = newNamespacePrefix;
		this.namespaceUri = newNamespaceUri;
		this.namespaceDepth = newNamespaceDepth;
	}

	public void setPrefix(String prefix, String namespace) throws IOException, IllegalArgumentException, IllegalStateException {
		super.xs.setPrefix(prefix, namespace);
		int depth = this.getDepth();

		for(int pos = this.namespaceEnd - 1; pos >= 0 && this.namespaceDepth[pos] > depth; --pos) {
			--this.namespaceEnd;
		}

		if(this.namespaceEnd >= this.namespacePrefix.length) {
			this.ensureNamespacesCapacity();
		}

		this.namespacePrefix[this.namespaceEnd] = prefix;
		this.namespaceUri[this.namespaceEnd] = namespace;
		++this.namespaceEnd;
	}

	public void fragment(String xmlFragment) throws IOException, IllegalArgumentException, IllegalStateException, XmlPullParserException {
		StringBuffer buf = new StringBuffer(xmlFragment.length() + this.namespaceEnd * 30);
		buf.append("<fragment");

		label44:
		for(int s = this.namespaceEnd - 1; s >= 0; --s) {
			String prefix = this.namespacePrefix[s];

			for(int i = this.namespaceEnd - 1; i > s; --i) {
				if(prefix.equals(this.namespacePrefix[i])) {
					continue label44;
				}
			}

			buf.append(" xmlns");
			if(prefix.length() > 0) {
				buf.append(':').append(prefix);
			}

			buf.append("=\'");
			buf.append(this.escapeAttributeValue(this.namespaceUri[s]));
			buf.append("\'");
		}

		buf.append(">");
		buf.append(xmlFragment);
		buf.append("</fragment>");
		if(this.fragmentParser == null) {
			this.fragmentParser = this.wf.newPullParserWrapper();
		}

		String string6 = buf.toString();
		this.fragmentParser.setInput(new StringReader(string6));
		this.fragmentParser.nextTag();
		this.fragmentParser.require(2, (String)null, "fragment");

		while(true) {
			this.fragmentParser.nextToken();
			if(this.fragmentParser.getDepth() == 1 && this.fragmentParser.getEventType() == 3) {
				this.fragmentParser.require(3, (String)null, "fragment");
				return;
			}

			this.event(this.fragmentParser);
		}
	}

	public void event(XmlPullParser pp) throws XmlPullParserException, IOException {
		int eventType = pp.getEventType();
		switch(eventType) {
		case 0:
			Boolean standalone = (Boolean)pp.getProperty("http://xmlpull.org/v1/doc/features.html#xmldecl-standalone");
			this.startDocument(pp.getInputEncoding(), standalone);
			break;
		case 1:
			this.endDocument();
			break;
		case 2:
			this.writeStartTag(pp);
			break;
		case 3:
			this.endTag(pp.getNamespace(), pp.getName());
			break;
		case 4:
			if(pp.getDepth() > 0) {
				this.text(pp.getText());
			} else {
				this.ignorableWhitespace(pp.getText());
			}
			break;
		case 5:
			this.cdsect(pp.getText());
			break;
		case 6:
			this.entityRef(pp.getName());
			break;
		case 7:
			String s = pp.getText();
			this.ignorableWhitespace(s);
			break;
		case 8:
			this.processingInstruction(pp.getText());
			break;
		case 9:
			this.comment(pp.getText());
			break;
		case 10:
			this.docdecl(pp.getText());
		}

	}

	private void writeStartTag(XmlPullParser pp) throws XmlPullParserException, IOException {
		int i;
		if(!pp.getFeature("http://xmlpull.org/v1/doc/features.html#report-namespace-prefixes")) {
			i = pp.getNamespaceCount(pp.getDepth() - 1);
			int nsEnd = pp.getNamespaceCount(pp.getDepth());

			for(int i1 = i; i1 < nsEnd; ++i1) {
				String prefix = pp.getNamespacePrefix(i1);
				String ns = pp.getNamespaceUri(i1);
				this.setPrefix(prefix, ns);
			}
		}

		this.startTag(pp.getNamespace(), pp.getName());

		for(i = 0; i < pp.getAttributeCount(); ++i) {
			this.attribute(pp.getAttributeNamespace(i), pp.getAttributeName(i), pp.getAttributeValue(i));
		}

	}

	public String escapeAttributeValue(String value) {
		int posLt = value.indexOf(60);
		int posAmp = value.indexOf(38);
		int posQuot = value.indexOf(34);
		int posApos = value.indexOf(39);
		if(posLt == -1 && posAmp == -1 && posQuot == -1 && posApos == -1) {
			return value;
		} else {
			StringBuffer buf = new StringBuffer(value.length() + 10);
			int pos = 0;

			for(int len = value.length(); pos < len; ++pos) {
				char ch = value.charAt(pos);
				switch(ch) {
				case '\"':
					buf.append("&quot;");
					break;
				case '&':
					buf.append("&amp;");
					break;
				case '\'':
					buf.append("&apos;");
					break;
				case '<':
					buf.append("&lt;");
					break;
				default:
					buf.append(ch);
				}
			}

			return buf.toString();
		}
	}

	public String escapeText(String text) {
		int posLt = text.indexOf(60);
		int posAmp = text.indexOf(38);
		if(posLt == -1 && posAmp == -1) {
			return text;
		} else {
			StringBuffer buf = new StringBuffer(text.length() + 10);
			int pos = 0;

			while(true) {
				while(posLt != -1 || posAmp != -1) {
					if(posLt != -1 && (posLt == -1 || posAmp == -1 || posAmp >= posLt)) {
						if(posAmp != -1 && (posLt == -1 || posAmp == -1 || posLt >= posAmp)) {
							throw new IllegalStateException("wrong state posLt=" + posLt + " posAmp=" + posAmp + " for " + text);
						}

						if(pos < posLt) {
							buf.append(text.substring(pos, posLt));
						}

						buf.append("&lt;");
						pos = posLt + 1;
						posLt = text.indexOf(60, pos);
					} else {
						if(pos < posAmp) {
							buf.append(text.substring(pos, posAmp));
						}

						buf.append("&amp;");
						pos = posAmp + 1;
						posAmp = text.indexOf(38, pos);
					}
				}

				buf.append(text.substring(pos));
				return buf.toString();
			}
		}
	}

	public void writeDouble(double d) throws XmlPullParserException, IOException, IllegalArgumentException {
		if(d == Double.POSITIVE_INFINITY) {
			super.xs.text("INF");
		} else if(d == Double.NEGATIVE_INFINITY) {
			super.xs.text("-INF");
		} else {
			super.xs.text(Double.toString(d));
		}

	}

	public void writeFloat(float f) throws XmlPullParserException, IOException, IllegalArgumentException {
		if(f == Float.POSITIVE_INFINITY) {
			super.xs.text("INF");
		} else if(f == Float.NEGATIVE_INFINITY) {
			super.xs.text("-INF");
		} else {
			super.xs.text(Float.toString(f));
		}

	}

	public void writeInt(int i) throws XmlPullParserException, IOException, IllegalArgumentException {
		super.xs.text(Integer.toString(i));
	}

	public void writeString(String s) throws XmlPullParserException, IOException, IllegalArgumentException {
		if(s == null) {
			throw new IllegalArgumentException("null string can not be written");
		} else {
			super.xs.text(s);
		}
	}

	public void writeDoubleElement(String namespace, String name, double d) throws XmlPullParserException, IOException, IllegalArgumentException {
		super.xs.startTag(namespace, name);
		this.writeDouble(d);
		super.xs.endTag(namespace, name);
	}

	public void writeFloatElement(String namespace, String name, float f) throws XmlPullParserException, IOException, IllegalArgumentException {
		super.xs.startTag(namespace, name);
		this.writeFloat(f);
		super.xs.endTag(namespace, name);
	}

	public void writeIntElement(String namespace, String name, int i) throws XmlPullParserException, IOException, IllegalArgumentException {
		super.xs.startTag(namespace, name);
		this.writeInt(i);
		super.xs.endTag(namespace, name);
	}

	public void writeStringElement(String namespace, String name, String s) throws XmlPullParserException, IOException, IllegalArgumentException {
		super.xs.startTag(namespace, name);
		if(s == null) {
			super.xs.attribute("http://www.w3.org/2001/XMLSchema", "nil", "true");
		} else {
			this.writeString(s);
		}

		super.xs.endTag(namespace, name);
	}
}
