package org.xmlpull.v1.wrapper;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;
import org.xmlpull.v1.wrapper.classic.StaticXmlPullParserWrapper;
import org.xmlpull.v1.wrapper.classic.StaticXmlSerializerWrapper;

public class XmlPullWrapperFactory {
	private static final boolean DEBUG = false;
	protected XmlPullParserFactory f;

	public static XmlPullWrapperFactory newInstance() throws XmlPullParserException {
		return new XmlPullWrapperFactory((XmlPullParserFactory)null);
	}

	public static XmlPullWrapperFactory newInstance(XmlPullParserFactory factory) throws XmlPullParserException {
		return new XmlPullWrapperFactory(factory);
	}

	public static XmlPullWrapperFactory newInstance(String classNames, Class context) throws XmlPullParserException {
		XmlPullParserFactory factory = XmlPullParserFactory.newInstance(classNames, context);
		return new XmlPullWrapperFactory(factory);
	}

	protected XmlPullWrapperFactory(XmlPullParserFactory factory) throws XmlPullParserException {
		if(factory != null) {
			this.f = factory;
		} else {
			this.f = XmlPullParserFactory.newInstance();
		}

	}

	public XmlPullParserFactory getFactory() throws XmlPullParserException {
		return this.f;
	}

	public void setFeature(String name, boolean state) throws XmlPullParserException {
		this.f.setFeature(name, state);
	}

	public boolean getFeature(String name) {
		return this.f.getFeature(name);
	}

	public void setNamespaceAware(boolean awareness) {
		this.f.setNamespaceAware(awareness);
	}

	public boolean isNamespaceAware() {
		return this.f.isNamespaceAware();
	}

	public void setValidating(boolean validating) {
		this.f.setValidating(validating);
	}

	public boolean isValidating() {
		return this.f.isValidating();
	}

	public XmlPullParserWrapper newPullParserWrapper() throws XmlPullParserException {
		XmlPullParser pp = this.f.newPullParser();
		return new StaticXmlPullParserWrapper(pp);
	}

	public XmlPullParserWrapper newPullParserWrapper(XmlPullParser pp) throws XmlPullParserException {
		return new StaticXmlPullParserWrapper(pp);
	}

	public XmlSerializerWrapper newSerializerWrapper() throws XmlPullParserException {
		XmlSerializer xs = this.f.newSerializer();
		return new StaticXmlSerializerWrapper(xs, this);
	}

	public XmlSerializerWrapper newSerializerWrapper(XmlSerializer xs) throws XmlPullParserException {
		return new StaticXmlSerializerWrapper(xs, this);
	}
}
