package org.xmlpull.v1.builder;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;
import org.xmlpull.v1.builder.impl.XmlInfosetBuilderImpl;

public abstract class XmlInfosetBuilder {
	protected XmlPullParserFactory factory;

	public static XmlInfosetBuilder newInstance() throws XmlBuilderException {
		XmlInfosetBuilderImpl impl = new XmlInfosetBuilderImpl();

		try {
			impl.factory = XmlPullParserFactory.newInstance(System.getProperty("org.xmlpull.v1.XmlPullParserFactory"), (Class)null);
			impl.factory.setNamespaceAware(true);
			return impl;
		} catch (XmlPullParserException xmlPullParserException2) {
			throw new XmlBuilderException("could not create XmlPull factory:" + xmlPullParserException2, xmlPullParserException2);
		}
	}

	public static XmlInfosetBuilder newInstance(XmlPullParserFactory factory) throws XmlBuilderException {
		if(factory == null) {
			throw new IllegalArgumentException();
		} else {
			XmlInfosetBuilderImpl impl = new XmlInfosetBuilderImpl();
			impl.factory = factory;
			impl.factory.setNamespaceAware(true);
			return impl;
		}
	}

	public XmlPullParserFactory getFactory() throws XmlBuilderException {
		return this.factory;
	}

	public XmlDocument newDocument() throws XmlBuilderException {
		return this.newDocument((String)null, (Boolean)null, (String)null);
	}

	public abstract XmlDocument newDocument(String string1, Boolean boolean2, String string3) throws XmlBuilderException;

	public abstract XmlElement newFragment(String string1) throws XmlBuilderException;

	public abstract XmlElement newFragment(String string1, String string2) throws XmlBuilderException;

	public abstract XmlElement newFragment(XmlNamespace xmlNamespace1, String string2) throws XmlBuilderException;

	public abstract XmlNamespace newNamespace(String string1) throws XmlBuilderException;

	public abstract XmlNamespace newNamespace(String string1, String string2) throws XmlBuilderException;

	public abstract XmlDocument parse(XmlPullParser xmlPullParser1) throws XmlBuilderException;

	public abstract Object parseItem(XmlPullParser xmlPullParser1) throws XmlBuilderException;

	public abstract XmlElement parseStartTag(XmlPullParser xmlPullParser1) throws XmlBuilderException;

	public XmlDocument parseInputStream(InputStream is) throws XmlBuilderException {
		XmlPullParser pp = null;

		try {
			pp = this.factory.newPullParser();
			pp.setInput(is, (String)null);
		} catch (XmlPullParserException xmlPullParserException4) {
			throw new XmlBuilderException("could not start parsing input stream", xmlPullParserException4);
		}

		return this.parse(pp);
	}

	public XmlDocument parseInputStream(InputStream is, String encoding) throws XmlBuilderException {
		XmlPullParser pp = null;

		try {
			pp = this.factory.newPullParser();
			pp.setInput(is, encoding);
		} catch (XmlPullParserException xmlPullParserException5) {
			throw new XmlBuilderException("could not start parsing input stream (encoding=" + encoding + ")", xmlPullParserException5);
		}

		return this.parse(pp);
	}

	public XmlDocument parseReader(Reader reader) throws XmlBuilderException {
		XmlPullParser pp = null;

		try {
			pp = this.factory.newPullParser();
			pp.setInput(reader);
		} catch (XmlPullParserException xmlPullParserException4) {
			throw new XmlBuilderException("could not start parsing input from reader", xmlPullParserException4);
		}

		return this.parse(pp);
	}

	public abstract XmlDocument parseLocation(String string1) throws XmlBuilderException;

	public abstract XmlElement parseFragment(XmlPullParser xmlPullParser1) throws XmlBuilderException;

	public XmlElement parseFragmentFromInputStream(InputStream is) throws XmlBuilderException {
		XmlPullParser pp = null;

		try {
			pp = this.factory.newPullParser();
			pp.setInput(is, (String)null);

			try {
				pp.nextTag();
			} catch (IOException iOException4) {
				throw new XmlBuilderException("IO error when starting to parse input stream", iOException4);
			}
		} catch (XmlPullParserException xmlPullParserException5) {
			throw new XmlBuilderException("could not start parsing input stream", xmlPullParserException5);
		}

		return this.parseFragment(pp);
	}

	public XmlElement parseFragementFromInputStream(InputStream is, String encoding) throws XmlBuilderException {
		XmlPullParser pp = null;

		try {
			pp = this.factory.newPullParser();
			pp.setInput(is, encoding);

			try {
				pp.nextTag();
			} catch (IOException iOException5) {
				throw new XmlBuilderException("IO error when starting to parse input stream (encoding=" + encoding + ")", iOException5);
			}
		} catch (XmlPullParserException xmlPullParserException6) {
			throw new XmlBuilderException("could not start parsing input stream (encoding=" + encoding + ")", xmlPullParserException6);
		}

		return this.parseFragment(pp);
	}

	public XmlElement parseFragmentFromReader(Reader reader) throws XmlBuilderException {
		XmlPullParser pp = null;

		try {
			pp = this.factory.newPullParser();
			pp.setInput(reader);

			try {
				pp.nextTag();
			} catch (IOException iOException4) {
				throw new XmlBuilderException("IO error when starting to parse from reader", iOException4);
			}
		} catch (XmlPullParserException xmlPullParserException5) {
			throw new XmlBuilderException("could not start parsing input from reader", xmlPullParserException5);
		}

		return this.parseFragment(pp);
	}

	public void skipSubTree(XmlPullParser pp) throws XmlBuilderException {
		try {
			pp.require(2, (String)null, (String)null);
			int e = 1;

			while(e > 0) {
				int eventType = pp.next();
				if(eventType == 3) {
					--e;
				} else if(eventType == 2) {
					++e;
				}
			}

		} catch (XmlPullParserException xmlPullParserException4) {
			throw new XmlBuilderException("could not skip subtree", xmlPullParserException4);
		} catch (IOException iOException5) {
			throw new XmlBuilderException("IO error when skipping subtree", iOException5);
		}
	}

	public abstract void serializeStartTag(XmlElement xmlElement1, XmlSerializer xmlSerializer2) throws XmlBuilderException;

	public abstract void serializeEndTag(XmlElement xmlElement1, XmlSerializer xmlSerializer2) throws XmlBuilderException;

	public abstract void serialize(Object object1, XmlSerializer xmlSerializer2) throws XmlBuilderException;

	public abstract void serializeItem(Object object1, XmlSerializer xmlSerializer2) throws XmlBuilderException;

	public void serializeToOutputStream(Object item, OutputStream os) throws XmlBuilderException {
		this.serializeToOutputStream(item, os, "UTF8");
	}

	public void serializeToOutputStream(Object item, OutputStream os, String encoding) throws XmlBuilderException {
		XmlSerializer ser = null;

		try {
			ser = this.factory.newSerializer();
			ser.setOutput(os, encoding);
		} catch (Exception exception7) {
			throw new XmlBuilderException("could not serialize node to output stream (encoding=" + encoding + ")", exception7);
		}

		this.serialize(item, ser);

		try {
			ser.flush();
		} catch (IOException iOException6) {
			throw new XmlBuilderException("could not flush output", iOException6);
		}
	}

	public void serializeToWriter(Object item, Writer writer) throws XmlBuilderException {
		XmlSerializer ser = null;

		try {
			ser = this.factory.newSerializer();
			ser.setOutput(writer);
		} catch (Exception exception6) {
			throw new XmlBuilderException("could not serialize node to writer", exception6);
		}

		this.serialize(item, ser);

		try {
			ser.flush();
		} catch (IOException iOException5) {
			throw new XmlBuilderException("could not flush output", iOException5);
		}
	}

	public String serializeToString(Object item) throws XmlBuilderException {
		StringWriter sw = new StringWriter();
		this.serializeToWriter(item, sw);
		return sw.toString();
	}
}
