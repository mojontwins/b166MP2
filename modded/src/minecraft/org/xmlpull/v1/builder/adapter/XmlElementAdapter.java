package org.xmlpull.v1.builder.adapter;

import java.util.Iterator;

import org.xmlpull.v1.builder.Iterable;
import org.xmlpull.v1.builder.XmlAttribute;
import org.xmlpull.v1.builder.XmlBuilderException;
import org.xmlpull.v1.builder.XmlContainer;
import org.xmlpull.v1.builder.XmlDocument;
import org.xmlpull.v1.builder.XmlElement;
import org.xmlpull.v1.builder.XmlNamespace;

public class XmlElementAdapter implements XmlElement {
	private XmlElementAdapter topAdapter;
	private XmlElement target;
	private XmlContainer parent;
	static Class class$org$xmlpull$v1$builder$adapter$XmlElementAdapter;
	static Class class$org$xmlpull$v1$builder$XmlElement;

	public XmlElementAdapter(XmlElement target) {
		this.setTarget(target);
	}

	private void setTarget(XmlElement target) {
		this.target = target;
		if(target.getParent() != null) {
			this.parent = target.getParent();
			if(this.parent instanceof XmlDocument) {
				XmlDocument iter = (XmlDocument)this.parent;
				iter.setDocumentElement(this);
			}

			if(this.parent instanceof XmlElement) {
				XmlElement iter1 = (XmlElement)this.parent;
				iter1.replaceChild(this, target);
			}
		}

		Iterator iter2 = target.children();

		while(iter2.hasNext()) {
			Object child = iter2.next();
			this.fixImportedChildParent(child);
		}

	}

	public Object clone() throws CloneNotSupportedException {
		XmlElementAdapter ela = (XmlElementAdapter)super.clone();
		ela.parent = null;
		ela.target = (XmlElement)this.target.clone();
		return ela;
	}

	public XmlElement getTarget() {
		return this.target;
	}

	public XmlElementAdapter getTopAdapter() {
		return this.topAdapter != null ? this.topAdapter : this;
	}

	public void setTopAdapter(XmlElementAdapter adapter) {
		this.topAdapter = adapter;
		if(this.target instanceof XmlElementAdapter) {
			((XmlElementAdapter)this.target).setTopAdapter(adapter);
		}

	}

	public static XmlElementAdapter castOrWrap(XmlElement el, Class adapterClass) {
		if(el == null) {
			throw new IllegalArgumentException("null element can not be wrapped");
		} else if(!(class$org$xmlpull$v1$builder$adapter$XmlElementAdapter == null ? (class$org$xmlpull$v1$builder$adapter$XmlElementAdapter = class$("org.xmlpull.v1.builder.adapter.XmlElementAdapter")) : class$org$xmlpull$v1$builder$adapter$XmlElementAdapter).isAssignableFrom(adapterClass)) {
			throw new IllegalArgumentException("class for cast/wrap must extend " + (class$org$xmlpull$v1$builder$adapter$XmlElementAdapter == null ? (class$org$xmlpull$v1$builder$adapter$XmlElementAdapter = class$("org.xmlpull.v1.builder.adapter.XmlElementAdapter")) : class$org$xmlpull$v1$builder$adapter$XmlElementAdapter));
		} else {
			XmlElementAdapter e;
			if(!(el instanceof XmlElementAdapter)) {
				try {
					e = (XmlElementAdapter)adapterClass.getConstructor(new Class[]{class$org$xmlpull$v1$builder$XmlElement == null ? (class$org$xmlpull$v1$builder$XmlElement = class$("org.xmlpull.v1.builder.XmlElement")) : class$org$xmlpull$v1$builder$XmlElement}).newInstance(new Object[]{el});
					return e;
				} catch (Exception exception7) {
					throw new XmlBuilderException("could not wrap element " + el, exception7);
				}
			} else {
				e = (XmlElementAdapter)el;
				Class currentAdapClass = e.getClass();
				if(adapterClass.isAssignableFrom(currentAdapClass)) {
					return e;
				} else {
					XmlElementAdapter topAdapter;
					for(topAdapter = e = e.getTopAdapter(); e.topAdapter != null; e = (XmlElementAdapter)e.target) {
						currentAdapClass = e.getClass();
						if(currentAdapClass.isAssignableFrom(adapterClass)) {
							return e;
						}

						if(!(e.target instanceof XmlElementAdapter)) {
							break;
						}
					}

					try {
						e.topAdapter = (XmlElementAdapter)adapterClass.getConstructor(new Class[]{class$org$xmlpull$v1$builder$XmlElement == null ? (class$org$xmlpull$v1$builder$XmlElement = class$("org.xmlpull.v1.builder.XmlElement")) : class$org$xmlpull$v1$builder$XmlElement}).newInstance(new Object[]{topAdapter});
						e.topAdapter.setTopAdapter(e.topAdapter);
						return e.topAdapter;
					} catch (Exception exception6) {
						throw new XmlBuilderException("could not create wrapper of " + adapterClass, exception6);
					}
				}
			}
		}
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

	private XmlElement fixElementParent(XmlElement el) {
		el.setParent(this);
		return el;
	}

	public XmlContainer getRoot() {
		Object root = this.target.getRoot();
		if(root == this.target) {
			root = this;
		}

		return (XmlContainer)root;
	}

	public XmlContainer getParent() {
		return this.parent;
	}

	public void setParent(XmlContainer parent) {
		this.parent = parent;
	}

	public XmlNamespace newNamespace(String prefix, String namespaceName) {
		return this.target.newNamespace(prefix, namespaceName);
	}

	public XmlAttribute attribute(String attributeName) {
		return this.target.attribute(attributeName);
	}

	public XmlAttribute attribute(XmlNamespace attributeNamespaceName, String attributeName) {
		return this.target.attribute(attributeNamespaceName, attributeName);
	}

	public XmlAttribute findAttribute(String attributeNamespaceName, String attributeName) {
		return this.target.findAttribute(attributeNamespaceName, attributeName);
	}

	public Iterator attributes() {
		return this.target.attributes();
	}

	public void removeAllChildren() {
		this.target.removeAllChildren();
	}

	public XmlAttribute addAttribute(String attributeType, String attributePrefix, String attributeNamespace, String attributeName, String attributeValue, boolean specified) {
		return this.target.addAttribute(attributeType, attributePrefix, attributeNamespace, attributeName, attributeValue, specified);
	}

	public String getAttributeValue(String attributeNamespaceName, String attributeName) {
		return this.target.getAttributeValue(attributeNamespaceName, attributeName);
	}

	public XmlAttribute addAttribute(XmlNamespace namespace, String name, String value) {
		return this.target.addAttribute(namespace, name, value);
	}

	public String getNamespaceName() {
		return this.target.getNamespaceName();
	}

	public void ensureChildrenCapacity(int minCapacity) {
		this.target.ensureChildrenCapacity(minCapacity);
	}

	public Iterator namespaces() {
		return this.target.namespaces();
	}

	public void removeAllAttributes() {
		this.target.removeAllAttributes();
	}

	public XmlNamespace getNamespace() {
		return this.target.getNamespace();
	}

	public String getBaseUri() {
		return this.target.getBaseUri();
	}

	public void removeAttribute(XmlAttribute attr) {
		this.target.removeAttribute(attr);
	}

	public XmlNamespace declareNamespace(String prefix, String namespaceName) {
		return this.target.declareNamespace(prefix, namespaceName);
	}

	public void removeAllNamespaceDeclarations() {
		this.target.removeAllNamespaceDeclarations();
	}

	public boolean hasAttributes() {
		return this.target.hasAttributes();
	}

	public XmlAttribute addAttribute(String type, XmlNamespace namespace, String name, String value, boolean specified) {
		return this.target.addAttribute(type, namespace, name, value, specified);
	}

	public XmlNamespace declareNamespace(XmlNamespace namespace) {
		return this.target.declareNamespace(namespace);
	}

	public XmlAttribute addAttribute(String name, String value) {
		return this.target.addAttribute(name, value);
	}

	public boolean hasNamespaceDeclarations() {
		return this.target.hasNamespaceDeclarations();
	}

	public XmlNamespace lookupNamespaceByName(String namespaceName) {
		XmlNamespace ns = this.target.lookupNamespaceByName(namespaceName);
		if(ns == null) {
			XmlContainer p = this.getParent();
			if(p instanceof XmlElement) {
				XmlElement e = (XmlElement)p;
				return e.lookupNamespaceByName(namespaceName);
			}
		}

		return ns;
	}

	public XmlNamespace lookupNamespaceByPrefix(String namespacePrefix) {
		XmlNamespace ns = this.target.lookupNamespaceByPrefix(namespacePrefix);
		if(ns == null) {
			XmlContainer p = this.getParent();
			if(p instanceof XmlElement) {
				XmlElement e = (XmlElement)p;
				return e.lookupNamespaceByPrefix(namespacePrefix);
			}
		}

		return ns;
	}

	public XmlNamespace newNamespace(String namespaceName) {
		return this.target.newNamespace(namespaceName);
	}

	public void setBaseUri(String baseUri) {
		this.target.setBaseUri(baseUri);
	}

	public void setNamespace(XmlNamespace namespace) {
		this.target.setNamespace(namespace);
	}

	public void ensureNamespaceDeclarationsCapacity(int minCapacity) {
		this.target.ensureNamespaceDeclarationsCapacity(minCapacity);
	}

	public String getName() {
		return this.target.getName();
	}

	public void setName(String name) {
		this.target.setName(name);
	}

	public XmlAttribute addAttribute(String type, XmlNamespace namespace, String name, String value) {
		return this.target.addAttribute(type, namespace, name, value);
	}

	public void ensureAttributeCapacity(int minCapacity) {
		this.target.ensureAttributeCapacity(minCapacity);
	}

	public XmlAttribute addAttribute(XmlAttribute attributeValueToAdd) {
		return this.target.addAttribute(attributeValueToAdd);
	}

	public XmlElement element(int position) {
		return this.target.element(position);
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

	public Iterable elements(XmlNamespace n, String name) {
		return this.target.elements(n, name);
	}

	public XmlElement findElementByName(String name, XmlElement elementToStartLooking) {
		return this.target.findElementByName(name, elementToStartLooking);
	}

	public XmlElement newElement(XmlNamespace namespace, String name) {
		return this.target.newElement(namespace, name);
	}

	public XmlElement addElement(XmlElement child) {
		return this.fixElementParent(this.target.addElement(child));
	}

	public XmlElement addElement(int pos, XmlElement child) {
		return this.fixElementParent(this.target.addElement(pos, child));
	}

	public XmlElement addElement(String name) {
		return this.fixElementParent(this.target.addElement(name));
	}

	public XmlElement findElementByName(String namespaceName, String name) {
		return this.target.findElementByName(namespaceName, name);
	}

	public void addChild(Object child) {
		this.target.addChild(child);
		this.fixImportedChildParent(child);
	}

	public void insertChild(int pos, Object childToInsert) {
		this.target.insertChild(pos, childToInsert);
		this.fixImportedChildParent(childToInsert);
	}

	public XmlElement findElementByName(String name) {
		return this.target.findElementByName(name);
	}

	public XmlElement findElementByName(String namespaceName, String name, XmlElement elementToStartLooking) {
		return this.target.findElementByName(namespaceName, name, elementToStartLooking);
	}

	public void removeChild(Object child) {
		this.target.removeChild(child);
	}

	public Iterator children() {
		return this.target.children();
	}

	public Iterable requiredElementContent() {
		return this.target.requiredElementContent();
	}

	public String requiredTextContent() {
		return this.target.requiredTextContent();
	}

	public boolean hasChild(Object child) {
		return this.target.hasChild(child);
	}

	public XmlElement newElement(String namespaceName, String name) {
		return this.target.newElement(namespaceName, name);
	}

	public XmlElement addElement(XmlNamespace namespace, String name) {
		return this.fixElementParent(this.target.addElement(namespace, name));
	}

	public boolean hasChildren() {
		return this.target.hasChildren();
	}

	public void addChild(int pos, Object child) {
		this.target.addChild(pos, child);
		this.fixImportedChildParent(child);
	}

	public void replaceChild(Object newChild, Object oldChild) {
		this.target.replaceChild(newChild, oldChild);
		this.fixImportedChildParent(newChild);
	}

	public XmlElement newElement(String name) {
		return this.target.newElement(name);
	}

	public void replaceChildrenWithText(String textContent) {
		this.target.replaceChildrenWithText(textContent);
	}

	static Class class$(String x0) {
		try {
			return Class.forName(x0);
		} catch (ClassNotFoundException classNotFoundException2) {
			throw new NoClassDefFoundError(classNotFoundException2.getMessage());
		}
	}
}
