package org.xmlpull.v1.dom2_builder;

import java.io.IOException;
import java.io.Reader;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

public class DOM2XmlPullBuilder {
	protected Document newDoc() throws XmlPullParserException {
		try {
			DocumentBuilderFactory ex = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = ex.newDocumentBuilder();
			DOMImplementation impl = builder.getDOMImplementation();
			return builder.newDocument();
		} catch (FactoryConfigurationError factoryConfigurationError4) {
			throw new XmlPullParserException("could not configure factory JAXP DocumentBuilderFactory: " + factoryConfigurationError4, (XmlPullParser)null, factoryConfigurationError4);
		} catch (ParserConfigurationException parserConfigurationException5) {
			throw new XmlPullParserException("could not configure parser JAXP DocumentBuilderFactory: " + parserConfigurationException5, (XmlPullParser)null, parserConfigurationException5);
		}
	}

	protected XmlPullParser newParser() throws XmlPullParserException {
		XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
		return factory.newPullParser();
	}

	public Element parse(Reader reader) throws XmlPullParserException, IOException {
		Document docFactory = this.newDoc();
		return this.parse(reader, docFactory);
	}

	public Element parse(Reader reader, Document docFactory) throws XmlPullParserException, IOException {
		XmlPullParser pp = this.newParser();
		pp.setFeature("http://xmlpull.org/v1/doc/features.html#process-namespaces", true);
		pp.setInput(reader);
		pp.next();
		return this.parse(pp, docFactory);
	}

	public Element parse(XmlPullParser pp, Document docFactory) throws XmlPullParserException, IOException {
		Element root = this.parseSubTree(pp, docFactory);
		return root;
	}

	public Element parseSubTree(XmlPullParser pp) throws XmlPullParserException, IOException {
		Document doc = this.newDoc();
		Element root = this.parseSubTree(pp, doc);
		return root;
	}

	public Element parseSubTree(XmlPullParser pp, Document docFactory) throws XmlPullParserException, IOException {
		DOM2XmlPullBuilder.BuildProcess process = new DOM2XmlPullBuilder.BuildProcess();
		return process.parseSubTree(pp, docFactory);
	}

	private static void assertEquals(String expected, String s) {
		if(expected != null && !expected.equals(s) || expected == null && s == null) {
			throw new RuntimeException("expected \'" + expected + "\' but got \'" + s + "\'");
		}
	}

	private static void assertNotNull(Object o) {
		if(o == null) {
			throw new RuntimeException("expected no null value");
		}
	}

	public static void main(String[] args) throws Exception {
	}

	static class SyntheticClass_1 {
	}

	static class BuildProcess {
		private XmlPullParser pp;
		private Document docFactory;
		private boolean scanNamespaces;

		private BuildProcess() {
			this.scanNamespaces = true;
		}

		public Element parseSubTree(XmlPullParser pp, Document docFactory) throws XmlPullParserException, IOException {
			this.pp = pp;
			this.docFactory = docFactory;
			return this.parseSubTree();
		}

		private Element parseSubTree() throws XmlPullParserException, IOException {
			this.pp.require(2, (String)null, (String)null);
			String name = this.pp.getName();
			String ns = this.pp.getNamespace();
			String prefix = this.pp.getPrefix();
			String qname = prefix != null ? prefix + ":" + name : name;
			Element parent = this.docFactory.createElementNS(ns, qname);
			this.declareNamespaces(this.pp, parent);

			for(int text = 0; text < this.pp.getAttributeCount(); ++text) {
				String textEl = this.pp.getAttributeNamespace(text);
				String attrName = this.pp.getAttributeName(text);
				String attrValue = this.pp.getAttributeValue(text);
				if(textEl != null && textEl.length() != 0) {
					String attrPrefix = this.pp.getAttributePrefix(text);
					String attrQname = attrPrefix != null ? attrPrefix + ":" + attrName : attrName;
					parent.setAttributeNS(textEl, attrQname, attrValue);
				} else {
					parent.setAttribute(attrName, attrValue);
				}
			}

			while(this.pp.next() != 3) {
				if(this.pp.getEventType() == 2) {
					Element element12 = this.parseSubTree(this.pp, this.docFactory);
					parent.appendChild(element12);
				} else {
					if(this.pp.getEventType() != 4) {
						throw new XmlPullParserException("unexpected event " + XmlPullParser.TYPES[this.pp.getEventType()], this.pp, (Throwable)null);
					}

					String string13 = this.pp.getText();
					Text text14 = this.docFactory.createTextNode(string13);
					parent.appendChild(text14);
				}
			}

			this.pp.require(3, ns, name);
			return parent;
		}

		private void declareNamespaces(XmlPullParser pp, Element parent) throws DOMException, XmlPullParserException {
			int i;
			if(this.scanNamespaces) {
				this.scanNamespaces = false;
				i = pp.getNamespaceCount(pp.getDepth()) - 1;

				label42:
				for(int i1 = i; i1 >= pp.getNamespaceCount(0); --i1) {
					String prefix = pp.getNamespacePrefix(i1);

					for(int j = i; j > i1; --j) {
						String prefixJ = pp.getNamespacePrefix(j);
						if(prefix != null && prefix.equals(prefixJ) || prefix != null && prefix == prefixJ) {
							continue label42;
						}
					}

					this.declareOneNamespace(pp, i1, parent);
				}
			} else {
				for(i = pp.getNamespaceCount(pp.getDepth() - 1); i < pp.getNamespaceCount(pp.getDepth()); ++i) {
					this.declareOneNamespace(pp, i, parent);
				}
			}

		}

		private void declareOneNamespace(XmlPullParser pp, int i, Element parent) throws DOMException, XmlPullParserException {
			String xmlnsPrefix = pp.getNamespacePrefix(i);
			String xmlnsUri = pp.getNamespaceUri(i);
			String xmlnsDecl = xmlnsPrefix != null ? "xmlns:" + xmlnsPrefix : "xmlns";
			parent.setAttributeNS("http://www.w3.org/2000/xmlns/", xmlnsDecl, xmlnsUri);
		}

		BuildProcess(DOM2XmlPullBuilder.SyntheticClass_1 x0) {
			this();
		}
	}
}
