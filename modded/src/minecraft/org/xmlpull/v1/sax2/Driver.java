package org.xmlpull.v1.sax2;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

public class Driver implements Locator, XMLReader, Attributes {
	protected static final String DECLARATION_HANDLER_PROPERTY = "http://xml.org/sax/properties/declaration-handler";
	protected static final String LEXICAL_HANDLER_PROPERTY = "http://xml.org/sax/properties/lexical-handler";
	protected static final String NAMESPACES_FEATURE = "http://xml.org/sax/features/namespaces";
	protected static final String NAMESPACE_PREFIXES_FEATURE = "http://xml.org/sax/features/namespace-prefixes";
	protected static final String VALIDATION_FEATURE = "http://xml.org/sax/features/validation";
	protected static final String APACHE_SCHEMA_VALIDATION_FEATURE = "http://apache.org/xml/features/validation/schema";
	protected static final String APACHE_DYNAMIC_VALIDATION_FEATURE = "http://apache.org/xml/features/validation/dynamic";
	protected ContentHandler contentHandler = new DefaultHandler();
	protected ErrorHandler errorHandler = new DefaultHandler();
	protected String systemId;
	protected XmlPullParser pp;

	public Driver() throws XmlPullParserException {
		XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
		factory.setNamespaceAware(true);
		this.pp = factory.newPullParser();
	}

	public Driver(XmlPullParser pp) throws XmlPullParserException {
		this.pp = pp;
	}

	public int getLength() {
		return this.pp.getAttributeCount();
	}

	public String getURI(int index) {
		return this.pp.getAttributeNamespace(index);
	}

	public String getLocalName(int index) {
		return this.pp.getAttributeName(index);
	}

	public String getQName(int index) {
		String prefix = this.pp.getAttributePrefix(index);
		return prefix != null ? prefix + ':' + this.pp.getAttributeName(index) : this.pp.getAttributeName(index);
	}

	public String getType(int index) {
		return this.pp.getAttributeType(index);
	}

	public String getValue(int index) {
		return this.pp.getAttributeValue(index);
	}

	public int getIndex(String uri, String localName) {
		for(int i = 0; i < this.pp.getAttributeCount(); ++i) {
			if(this.pp.getAttributeNamespace(i).equals(uri) && this.pp.getAttributeName(i).equals(localName)) {
				return i;
			}
		}

		return -1;
	}

	public int getIndex(String qName) {
		for(int i = 0; i < this.pp.getAttributeCount(); ++i) {
			if(this.pp.getAttributeName(i).equals(qName)) {
				return i;
			}
		}

		return -1;
	}

	public String getType(String uri, String localName) {
		for(int i = 0; i < this.pp.getAttributeCount(); ++i) {
			if(this.pp.getAttributeNamespace(i).equals(uri) && this.pp.getAttributeName(i).equals(localName)) {
				return this.pp.getAttributeType(i);
			}
		}

		return null;
	}

	public String getType(String qName) {
		for(int i = 0; i < this.pp.getAttributeCount(); ++i) {
			if(this.pp.getAttributeName(i).equals(qName)) {
				return this.pp.getAttributeType(i);
			}
		}

		return null;
	}

	public String getValue(String uri, String localName) {
		return this.pp.getAttributeValue(uri, localName);
	}

	public String getValue(String qName) {
		return this.pp.getAttributeValue((String)null, qName);
	}

	public String getPublicId() {
		return null;
	}

	public String getSystemId() {
		return this.systemId;
	}

	public int getLineNumber() {
		return this.pp.getLineNumber();
	}

	public int getColumnNumber() {
		return this.pp.getColumnNumber();
	}

	public boolean getFeature(String name) throws SAXNotRecognizedException, SAXNotSupportedException {
		return "http://xml.org/sax/features/namespaces".equals(name) ? this.pp.getFeature("http://xmlpull.org/v1/doc/features.html#process-namespaces") : ("http://xml.org/sax/features/namespace-prefixes".equals(name) ? this.pp.getFeature("http://xmlpull.org/v1/doc/features.html#report-namespace-prefixes") : ("http://xml.org/sax/features/validation".equals(name) ? this.pp.getFeature("http://xmlpull.org/v1/doc/features.html#validation") : this.pp.getFeature(name)));
	}

	public void setFeature(String name, boolean value) throws SAXNotRecognizedException, SAXNotSupportedException {
		try {
			if("http://xml.org/sax/features/namespaces".equals(name)) {
				this.pp.setFeature("http://xmlpull.org/v1/doc/features.html#process-namespaces", value);
			} else if("http://xml.org/sax/features/namespace-prefixes".equals(name)) {
				if(this.pp.getFeature("http://xmlpull.org/v1/doc/features.html#report-namespace-prefixes") != value) {
					this.pp.setFeature("http://xmlpull.org/v1/doc/features.html#report-namespace-prefixes", value);
				}
			} else if("http://xml.org/sax/features/validation".equals(name)) {
				this.pp.setFeature("http://xmlpull.org/v1/doc/features.html#validation", value);
			} else {
				this.pp.setFeature(name, value);
			}

		} catch (XmlPullParserException xmlPullParserException4) {
			throw new SAXNotSupportedException("problem with setting feature " + name + ": " + xmlPullParserException4);
		}
	}

	public Object getProperty(String name) throws SAXNotRecognizedException, SAXNotSupportedException {
		return "http://xml.org/sax/properties/declaration-handler".equals(name) ? null : ("http://xml.org/sax/properties/lexical-handler".equals(name) ? null : this.pp.getProperty(name));
	}

	public void setProperty(String name, Object value) throws SAXNotRecognizedException, SAXNotSupportedException {
		if("http://xml.org/sax/properties/declaration-handler".equals(name)) {
			throw new SAXNotSupportedException("not supported setting property " + name);
		} else if("http://xml.org/sax/properties/lexical-handler".equals(name)) {
			throw new SAXNotSupportedException("not supported setting property " + name);
		} else {
			try {
				this.pp.setProperty(name, value);
			} catch (XmlPullParserException xmlPullParserException4) {
				throw new SAXNotSupportedException("not supported set property " + name + ": " + xmlPullParserException4);
			}
		}
	}

	public void setEntityResolver(EntityResolver resolver) {
	}

	public EntityResolver getEntityResolver() {
		return null;
	}

	public void setDTDHandler(DTDHandler handler) {
	}

	public DTDHandler getDTDHandler() {
		return null;
	}

	public void setContentHandler(ContentHandler handler) {
		this.contentHandler = handler;
	}

	public ContentHandler getContentHandler() {
		return this.contentHandler;
	}

	public void setErrorHandler(ErrorHandler handler) {
		this.errorHandler = handler;
	}

	public ErrorHandler getErrorHandler() {
		return this.errorHandler;
	}

	public void parse(InputSource source) throws SAXException, IOException {
		this.systemId = source.getSystemId();
		this.contentHandler.setDocumentLocator(this);
		Reader reader = source.getCharacterStream();

		SAXParseException saxException;
		try {
			if(reader == null) {
				Object ex = source.getByteStream();
				String saxException2 = source.getEncoding();
				if(ex == null) {
					this.systemId = source.getSystemId();
					if(this.systemId == null) {
						SAXParseException nue1 = new SAXParseException("null source systemId", this);
						this.errorHandler.fatalError(nue1);
						return;
					}

					try {
						URL nue = new URL(this.systemId);
						ex = nue.openStream();
					} catch (MalformedURLException malformedURLException9) {
						try {
							ex = new FileInputStream(this.systemId);
						} catch (FileNotFoundException fileNotFoundException8) {
							SAXParseException saxException1 = new SAXParseException("could not open file with systemId " + this.systemId, this, fileNotFoundException8);
							this.errorHandler.fatalError(saxException1);
							return;
						}
					}
				}

				this.pp.setInput((InputStream)ex, saxException2);
			} else {
				this.pp.setInput(reader);
			}
		} catch (XmlPullParserException xmlPullParserException10) {
			saxException = new SAXParseException("parsing initialization error: " + xmlPullParserException10, this, xmlPullParserException10);
			this.errorHandler.fatalError(saxException);
			return;
		}

		try {
			this.contentHandler.startDocument();
			this.pp.next();
			if(this.pp.getEventType() != 2) {
				SAXParseException ex1 = new SAXParseException("expected start tag not" + this.pp.getPositionDescription(), this);
				this.errorHandler.fatalError(ex1);
				return;
			}
		} catch (XmlPullParserException xmlPullParserException11) {
			saxException = new SAXParseException("parsing initialization error: " + xmlPullParserException11, this, xmlPullParserException11);
			this.errorHandler.fatalError(saxException);
			return;
		}

		this.parseSubTree(this.pp);
		this.contentHandler.endDocument();
	}

	public void parse(String systemId) throws SAXException, IOException {
		this.parse(new InputSource(systemId));
	}

	public void parseSubTree(XmlPullParser pp) throws SAXException, IOException {
		this.pp = pp;
		boolean namespaceAware = pp.getFeature("http://xmlpull.org/v1/doc/features.html#process-namespaces");

		try {
			if(pp.getEventType() != 2) {
				throw new SAXException("start tag must be read before skiping subtree" + pp.getPositionDescription());
			}

			int[] ex = new int[2];
			StringBuffer stringBuffer15 = new StringBuffer(16);
			String prefix = null;
			String name = null;
			int level = pp.getDepth() - 1;
			int type = 2;

			do {
				int depth;
				int countPrev;
				int count;
				label70:
				switch(type) {
				case 1:
					return;
				case 2:
					if(!namespaceAware) {
						this.startElement(pp.getNamespace(), pp.getName(), pp.getName());
						break;
					}

					int i16 = pp.getDepth() - 1;
					depth = level > i16 ? pp.getNamespaceCount(i16) : 0;
					countPrev = pp.getNamespaceCount(i16 + 1);

					for(count = depth; count < countPrev; ++count) {
						this.contentHandler.startPrefixMapping(pp.getNamespacePrefix(count), pp.getNamespaceUri(count));
					}

					name = pp.getName();
					prefix = pp.getPrefix();
					if(prefix != null) {
						stringBuffer15.setLength(0);
						stringBuffer15.append(prefix);
						stringBuffer15.append(':');
						stringBuffer15.append(name);
					}

					this.startElement(pp.getNamespace(), name, prefix != null ? stringBuffer15.toString() : name);
					break;
				case 3:
					if(namespaceAware) {
						name = pp.getName();
						prefix = pp.getPrefix();
						if(prefix != null) {
							stringBuffer15.setLength(0);
							stringBuffer15.append(prefix);
							stringBuffer15.append(':');
							stringBuffer15.append(name);
						}

						this.contentHandler.endElement(pp.getNamespace(), name, prefix != null ? stringBuffer15.toString() : name);
						depth = pp.getDepth();
						countPrev = level > depth ? pp.getNamespaceCount(pp.getDepth()) : 0;
						count = pp.getNamespaceCount(pp.getDepth() - 1);
						int i = count - 1;

						while(true) {
							if(i < countPrev) {
								break label70;
							}

							this.contentHandler.endPrefixMapping(pp.getNamespacePrefix(i));
							--i;
						}
					}

					this.contentHandler.endElement(pp.getNamespace(), pp.getName(), pp.getName());
					break;
				case 4:
					char[] chars = pp.getTextCharacters(ex);
					this.contentHandler.characters(chars, ex[0], ex[1]);
				}

				type = pp.next();
			} while(pp.getDepth() > level);
		} catch (XmlPullParserException xmlPullParserException14) {
			SAXParseException saxException = new SAXParseException("parsing error: " + xmlPullParserException14, this, xmlPullParserException14);
			xmlPullParserException14.printStackTrace();
			this.errorHandler.fatalError(saxException);
		}

	}

	protected void startElement(String namespace, String localName, String qName) throws SAXException {
		this.contentHandler.startElement(namespace, localName, qName, this);
	}
}
