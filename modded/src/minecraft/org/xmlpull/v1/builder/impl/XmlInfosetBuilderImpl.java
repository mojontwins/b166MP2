package org.xmlpull.v1.builder.impl;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Iterator;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;
import org.xmlpull.v1.builder.XmlAttribute;
import org.xmlpull.v1.builder.XmlBuilderException;
import org.xmlpull.v1.builder.XmlCharacters;
import org.xmlpull.v1.builder.XmlComment;
import org.xmlpull.v1.builder.XmlContainer;
import org.xmlpull.v1.builder.XmlDocument;
import org.xmlpull.v1.builder.XmlElement;
import org.xmlpull.v1.builder.XmlInfosetBuilder;
import org.xmlpull.v1.builder.XmlNamespace;
import org.xmlpull.v1.builder.XmlSerializable;

public class XmlInfosetBuilderImpl extends XmlInfosetBuilder {
	private static final String PROPERTY_XMLDECL_STANDALONE = "http://xmlpull.org/v1/doc/properties.html#xmldecl-standalone";
	private static final String PROPERTY_XMLDECL_VERSION = "http://xmlpull.org/v1/doc/properties.html#xmldecl-version";

	public XmlDocument newDocument(String version, Boolean standalone, String characterEncoding) {
		return new XmlDocumentImpl(version, standalone, characterEncoding);
	}

	public XmlElement newFragment(String elementName) {
		return new XmlElementImpl((XmlNamespace)null, elementName);
	}

	public XmlElement newFragment(String elementNamespaceName, String elementName) {
		return new XmlElementImpl(elementNamespaceName, elementName);
	}

	public XmlElement newFragment(XmlNamespace elementNamespace, String elementName) {
		return new XmlElementImpl(elementNamespace, elementName);
	}

	public XmlNamespace newNamespace(String namespaceName) {
		return new XmlNamespaceImpl((String)null, namespaceName);
	}

	public XmlNamespace newNamespace(String prefix, String namespaceName) {
		return new XmlNamespaceImpl(prefix, namespaceName);
	}

	public XmlDocument parse(XmlPullParser pp) {
		XmlDocument doc = this.parseDocumentStart(pp);
		XmlElement root = this.parseFragment(pp);
		doc.setDocumentElement(root);
		return doc;
	}

	public Object parseItem(XmlPullParser pp) {
		try {
			int e = pp.getEventType();
			if(e == 2) {
				return this.parseStartTag(pp);
			} else if(e == 4) {
				return pp.getText();
			} else if(e == 0) {
				return this.parseDocumentStart(pp);
			} else {
				throw new XmlBuilderException("currently unsupported event type " + XmlPullParser.TYPES[e] + pp.getPositionDescription());
			}
		} catch (XmlPullParserException xmlPullParserException3) {
			throw new XmlBuilderException("could not parse XML item", xmlPullParserException3);
		}
	}

	private XmlDocument parseDocumentStart(XmlPullParser pp) {
		XmlDocumentImpl doc = null;

		try {
			if(pp.getEventType() != 0) {
				throw new XmlBuilderException("parser must be positioned on beginning of document and not " + pp.getPositionDescription());
			} else {
				pp.next();
				String e = (String)pp.getProperty("http://xmlpull.org/v1/doc/properties.html#xmldecl-version");
				Boolean xmlDeclStandalone = (Boolean)pp.getProperty("http://xmlpull.org/v1/doc/properties.html#xmldecl-standalone");
				String characterEncoding = pp.getInputEncoding();
				doc = new XmlDocumentImpl(e, xmlDeclStandalone, characterEncoding);
				return doc;
			}
		} catch (XmlPullParserException xmlPullParserException6) {
			throw new XmlBuilderException("could not parse XML document prolog", xmlPullParserException6);
		} catch (IOException iOException7) {
			throw new XmlBuilderException("could not read XML document prolog", iOException7);
		}
	}

	public XmlElement parseFragment(XmlPullParser pp) {
		try {
			int e = pp.getDepth();
			int eventType = pp.getEventType();
			if(eventType != 2) {
				throw new XmlBuilderException("expected parser to be on start tag and not " + XmlPullParser.TYPES[eventType] + pp.getPositionDescription());
			} else {
				XmlElement curElem = this.parseStartTag(pp);

				while(true) {
					while(true) {
						eventType = pp.next();
						if(eventType == 2) {
							XmlElement parent1 = this.parseStartTag(pp);
							curElem.addElement(parent1);
							curElem = parent1;
						} else if(eventType == 3) {
							XmlContainer parent = curElem.getParent();
							if(parent == null) {
								if(pp.getDepth() != e) {
									throw new XmlBuilderException("unbalanced input" + pp.getPositionDescription());
								}

								return curElem;
							}

							curElem = (XmlElement)parent;
						} else if(eventType == 4) {
							curElem.addChild(pp.getText());
						}
					}
				}
			}
		} catch (XmlPullParserException xmlPullParserException6) {
			throw new XmlBuilderException("could not build tree from XML", xmlPullParserException6);
		} catch (IOException iOException7) {
			throw new XmlBuilderException("could not read XML tree content", iOException7);
		}
	}

	public XmlElement parseStartTag(XmlPullParser pp) {
		try {
			if(pp.getEventType() != 2) {
				throw new XmlBuilderException("parser must be on START_TAG and not " + pp.getPositionDescription());
			} else {
				String e = pp.getPrefix();
				XmlNamespaceImpl elementNs = new XmlNamespaceImpl(e, pp.getNamespace());
				XmlElementImpl el = new XmlElementImpl(elementNs, pp.getName());

				int i;
				for(i = pp.getNamespaceCount(pp.getDepth() - 1); i < pp.getNamespaceCount(pp.getDepth()); ++i) {
					String prefix = pp.getNamespacePrefix(i);
					el.declareNamespace(prefix == null ? "" : prefix, pp.getNamespaceUri(i));
				}

				for(i = 0; i < pp.getAttributeCount(); ++i) {
					el.addAttribute(pp.getAttributeType(i), pp.getAttributePrefix(i), pp.getAttributeNamespace(i), pp.getAttributeName(i), pp.getAttributeValue(i), !pp.isAttributeDefault(i));
				}

				return el;
			}
		} catch (XmlPullParserException xmlPullParserException7) {
			throw new XmlBuilderException("could not parse XML start tag", xmlPullParserException7);
		}
	}

	public XmlDocument parseLocation(String locationUrl) {
		URL url = null;

		try {
			url = new URL(locationUrl);
		} catch (MalformedURLException malformedURLException5) {
			throw new XmlBuilderException("could not parse URL " + locationUrl, malformedURLException5);
		}

		try {
			return this.parseInputStream(url.openStream());
		} catch (IOException iOException4) {
			throw new XmlBuilderException("could not open connection to URL " + locationUrl, iOException4);
		}
	}

	public void serialize(Object item, XmlSerializer serializer) {
		if(item instanceof Collection) {
			Collection c = (Collection)item;
			Iterator i = c.iterator();

			while(i.hasNext()) {
				this.serialize(i.next(), serializer);
			}
		} else if(item instanceof XmlContainer) {
			this.serializeContainer((XmlContainer)item, serializer);
		} else {
			this.serializeItem(item, serializer);
		}

	}

	private void serializeContainer(XmlContainer node, XmlSerializer serializer) {
		if(node instanceof XmlSerializable) {
			try {
				((XmlSerializable)node).serialize(serializer);
			} catch (IOException iOException4) {
				throw new XmlBuilderException("could not serialize node " + node + ": " + iOException4, iOException4);
			}
		} else if(node instanceof XmlDocument) {
			this.serializeDocument((XmlDocument)node, serializer);
		} else {
			if(!(node instanceof XmlElement)) {
				throw new IllegalArgumentException("could not serialzie unknown XML container " + node.getClass());
			}

			this.serializeFragment((XmlElement)node, serializer);
		}

	}

	public void serializeItem(Object item, XmlSerializer ser) {
		try {
			if(item instanceof XmlSerializable) {
				try {
					((XmlSerializable)item).serialize(ser);
				} catch (IOException iOException4) {
					throw new XmlBuilderException("could not serialize item " + item + ": " + iOException4, iOException4);
				}
			} else if(item instanceof String) {
				ser.text(item.toString());
			} else if(item instanceof XmlCharacters) {
				ser.text(((XmlCharacters)item).getText());
			} else {
				if(!(item instanceof XmlComment)) {
					throw new IllegalArgumentException("could not serialize " + (item != null ? item.getClass() : item));
				}

				ser.comment(((XmlComment)item).getContent());
			}

		} catch (IOException iOException5) {
			throw new XmlBuilderException("serializing XML start tag failed", iOException5);
		}
	}

	public void serializeStartTag(XmlElement el, XmlSerializer ser) {
		try {
			XmlNamespace e = el.getNamespace();
			String elPrefix = e != null ? e.getPrefix() : "";
			if(elPrefix == null) {
				elPrefix = "";
			}

			String nToDeclare = null;
			Iterator iter;
			if(el.hasNamespaceDeclarations()) {
				iter = el.namespaces();

				while(iter.hasNext()) {
					XmlNamespace a = (XmlNamespace)iter.next();
					String nPrefix = a.getPrefix();
					if(!elPrefix.equals(nPrefix)) {
						ser.setPrefix(nPrefix, a.getNamespaceName());
					} else {
						nToDeclare = a.getNamespaceName();
					}
				}
			}

			if(nToDeclare != null) {
				ser.setPrefix(elPrefix, nToDeclare);
			} else if(e != null) {
				String iter1 = e.getNamespaceName();
				if(iter1 == null) {
					iter1 = "";
				}

				String a1 = null;
				if(iter1.length() > 0) {
					ser.getPrefix(iter1, false);
				}

				if(a1 == null) {
					a1 = "";
				}

				if(a1 != elPrefix && !a1.equals(elPrefix)) {
					ser.setPrefix(elPrefix, iter1);
				}
			}

			ser.startTag(el.getNamespaceName(), el.getName());
			if(el.hasAttributes()) {
				iter = el.attributes();

				while(iter.hasNext()) {
					XmlAttribute a2 = (XmlAttribute)iter.next();
					if(a2 instanceof XmlSerializable) {
						((XmlSerializable)a2).serialize(ser);
					} else {
						ser.attribute(a2.getNamespaceName(), a2.getName(), a2.getValue());
					}
				}
			}

		} catch (IOException iOException9) {
			throw new XmlBuilderException("serializing XML start tag failed", iOException9);
		}
	}

	public void serializeEndTag(XmlElement el, XmlSerializer ser) {
		try {
			ser.endTag(el.getNamespaceName(), el.getName());
		} catch (IOException iOException4) {
			throw new XmlBuilderException("serializing XML end tag failed", iOException4);
		}
	}

	private void serializeDocument(XmlDocument doc, XmlSerializer ser) {
		try {
			ser.startDocument(doc.getCharacterEncodingScheme(), doc.isStandalone());
		} catch (IOException iOException5) {
			throw new XmlBuilderException("serializing XML document start failed", iOException5);
		}

		if(doc.getDocumentElement() != null) {
			this.serializeFragment(doc.getDocumentElement(), ser);

			try {
				ser.endDocument();
			} catch (IOException iOException4) {
				throw new XmlBuilderException("serializing XML document end failed", iOException4);
			}
		} else {
			throw new XmlBuilderException("could not serialize document without root element " + doc + ": ");
		}
	}

	private void serializeFragment(XmlElement el, XmlSerializer ser) {
		this.serializeStartTag(el, ser);
		if(el.hasChildren()) {
			Iterator iter = el.children();

			while(iter.hasNext()) {
				Object child = iter.next();
				if(child instanceof XmlSerializable) {
					try {
						((XmlSerializable)child).serialize(ser);
					} catch (IOException iOException6) {
						throw new XmlBuilderException("could not serialize item " + child + ": " + iOException6, iOException6);
					}
				} else if(child instanceof XmlElement) {
					this.serializeFragment((XmlElement)child, ser);
				} else {
					this.serializeItem(child, ser);
				}
			}
		}

		this.serializeEndTag(el, ser);
	}
}
