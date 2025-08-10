package org.xmlpull.v1.builder.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.xmlpull.v1.builder.Iterable;
import org.xmlpull.v1.builder.XmlAttribute;
import org.xmlpull.v1.builder.XmlBuilderException;
import org.xmlpull.v1.builder.XmlCharacters;
import org.xmlpull.v1.builder.XmlContained;
import org.xmlpull.v1.builder.XmlContainer;
import org.xmlpull.v1.builder.XmlDocument;
import org.xmlpull.v1.builder.XmlElement;
import org.xmlpull.v1.builder.XmlNamespace;

public class XmlElementImpl implements XmlElement {
	private XmlContainer parent;
	private XmlNamespace namespace;
	private String name;
	private List attrs;
	private List nsList;
	private List children;
	private static final Iterator EMPTY_ITERATOR = new XmlElementImpl.EmptyIterator(null);
	private static final Iterable EMPTY_ITERABLE = new Iterable() {
		public Iterator iterator() {
			return XmlElementImpl.EMPTY_ITERATOR;
		}
	};

	public Object clone() throws CloneNotSupportedException {
		XmlElementImpl cloned = (XmlElementImpl)super.clone();
		cloned.parent = null;
		cloned.attrs = this.cloneList(cloned, this.attrs);
		cloned.nsList = this.cloneList(cloned, this.nsList);
		cloned.children = this.cloneList(cloned, this.children);
		if(cloned.children != null) {
			for(int i = 0; i < cloned.children.size(); ++i) {
				Object member = cloned.children.get(i);
				if(member instanceof XmlContained) {
					XmlContained contained = (XmlContained)member;
					if(contained.getParent() == this) {
						contained.setParent((XmlContainer)null);
						contained.setParent(cloned);
					}
				}
			}
		}

		return cloned;
	}

	private List cloneList(XmlElementImpl cloned, List list) throws CloneNotSupportedException {
		if(list == null) {
			return null;
		} else {
			ArrayList newList = new ArrayList(list.size());

			for(int i = 0; i < list.size(); ++i) {
				Object member = list.get(i);
				Object newMember;
				if(!(member instanceof XmlNamespace) && !(member instanceof String)) {
					if(member instanceof XmlElement) {
						XmlElement e = (XmlElement)member;
						newMember = e.clone();
					} else if(member instanceof XmlAttribute) {
						XmlAttribute xmlAttribute9 = (XmlAttribute)member;
						newMember = new XmlAttributeImpl(cloned, xmlAttribute9.getType(), xmlAttribute9.getNamespace(), xmlAttribute9.getName(), xmlAttribute9.getValue(), xmlAttribute9.isSpecified());
					} else {
						if(!(member instanceof Cloneable)) {
							throw new CloneNotSupportedException();
						}

						try {
							newMember = member.getClass().getMethod("clone", (Class[])null).invoke(member, (Object[])null);
						} catch (Exception exception8) {
							throw new CloneNotSupportedException("failed to call clone() on  " + member + exception8);
						}
					}
				} else {
					newMember = member;
				}

				newList.add(newMember);
			}

			return newList;
		}
	}

	XmlElementImpl(String name) {
		this.name = name;
	}

	XmlElementImpl(XmlNamespace namespace, String name) {
		this.namespace = namespace;
		this.name = name;
	}

	XmlElementImpl(String namespaceName, String name) {
		if(namespaceName != null) {
			this.namespace = new XmlNamespaceImpl((String)null, namespaceName);
		}

		this.name = name;
	}

	public XmlContainer getRoot() {
		Object root;
		XmlElement el;
		for(root = this; root instanceof XmlElement; root = el.getParent()) {
			el = (XmlElement)root;
			if(el.getParent() == null) {
				break;
			}
		}

		return (XmlContainer)root;
	}

	public XmlContainer getParent() {
		return this.parent;
	}

	public void setParent(XmlContainer parent) {
		if(parent != null && parent instanceof XmlDocument) {
			XmlDocument doc = (XmlDocument)parent;
			if(doc.getDocumentElement() != this) {
				throw new XmlBuilderException("this element must be root document element to have document set as parent but already different element is set as root document element");
			}
		}

		this.parent = parent;
	}

	public XmlNamespace getNamespace() {
		return this.namespace;
	}

	public String getNamespaceName() {
		return this.namespace != null ? this.namespace.getNamespaceName() : null;
	}

	public void setNamespace(XmlNamespace namespace) {
		this.namespace = namespace;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String toString() {
		return "name[" + this.name + "]" + (this.namespace != null ? " namespace[" + this.namespace.getNamespaceName() + "]" : "");
	}

	public String getBaseUri() {
		throw new XmlBuilderException("not implemented");
	}

	public void setBaseUri(String baseUri) {
		throw new XmlBuilderException("not implemented");
	}

	public Iterator attributes() {
		return this.attrs == null ? EMPTY_ITERATOR : this.attrs.iterator();
	}

	public XmlAttribute addAttribute(XmlAttribute attributeValueToAdd) {
		if(this.attrs == null) {
			this.ensureAttributeCapacity(5);
		}

		this.attrs.add(attributeValueToAdd);
		return attributeValueToAdd;
	}

	public XmlAttribute addAttribute(XmlNamespace namespace, String name, String value) {
		return this.addAttribute("CDATA", namespace, name, value, false);
	}

	public XmlAttribute addAttribute(String name, String value) {
		return this.addAttribute("CDATA", (XmlNamespace)null, name, value, false);
	}

	public XmlAttribute addAttribute(String attributeType, XmlNamespace namespace, String name, String value) {
		return this.addAttribute(attributeType, namespace, name, value, false);
	}

	public XmlAttribute addAttribute(String attributeType, XmlNamespace namespace, String name, String value, boolean specified) {
		XmlAttributeImpl a = new XmlAttributeImpl(this, attributeType, namespace, name, value, specified);
		return this.addAttribute(a);
	}

	public XmlAttribute addAttribute(String attributeType, String attributePrefix, String attributeNamespace, String attributeName, String attributeValue, boolean specified) {
		XmlNamespace n = this.newNamespace(attributePrefix, attributeNamespace);
		return this.addAttribute(attributeType, n, attributeName, attributeValue, specified);
	}

	public void ensureAttributeCapacity(int minCapacity) {
		if(this.attrs == null) {
			this.attrs = new ArrayList(minCapacity);
		} else {
			((ArrayList)this.attrs).ensureCapacity(minCapacity);
		}

	}

	public String getAttributeValue(String attributeNamespaceName, String attributeName) {
		XmlAttribute xat = this.findAttribute(attributeNamespaceName, attributeName);
		return xat != null ? xat.getValue() : null;
	}

	public boolean hasAttributes() {
		return this.attrs != null && this.attrs.size() > 0;
	}

	public XmlAttribute attribute(String attributeName) {
		return this.attribute((XmlNamespace)null, attributeName);
	}

	public XmlAttribute attribute(XmlNamespace attributeNamespace, String attributeName) {
		return this.findAttribute(attributeNamespace != null ? attributeNamespace.getNamespaceName() : null, attributeName);
	}

	/** @deprecated */
	public XmlAttribute findAttribute(String attributeNamespace, String attributeName) {
		if(attributeName == null) {
			throw new IllegalArgumentException("attribute name ca not ber null");
		} else if(this.attrs == null) {
			return null;
		} else {
			int length = this.attrs.size();

			for(int i = 0; i < length; ++i) {
				XmlAttribute a = (XmlAttribute)this.attrs.get(i);
				String aName = a.getName();
				if(aName == attributeName || attributeName.equals(aName)) {
					if(attributeNamespace != null) {
						String aNamespace = a.getNamespaceName();
						if(attributeNamespace.equals(aNamespace)) {
							return a;
						}

						if(attributeNamespace == "" && aNamespace == null) {
							return a;
						}
					} else {
						if(a.getNamespace() == null) {
							return a;
						}

						if(a.getNamespace().getNamespaceName() == "") {
							return a;
						}
					}
				}
			}

			return null;
		}
	}

	public void removeAllAttributes() {
		this.attrs = null;
	}

	public void removeAttribute(XmlAttribute attr) {
		if(this.attrs == null) {
			throw new XmlBuilderException("this element has no attributes to remove");
		} else {
			for(int i = 0; i < this.attrs.size(); ++i) {
				if(this.attrs.get(i).equals(attr)) {
					this.attrs.remove(i);
					break;
				}
			}

		}
	}

	public XmlNamespace declareNamespace(String prefix, String namespaceName) {
		if(prefix == null) {
			throw new XmlBuilderException("namespace added to element must have not null prefix");
		} else {
			XmlNamespace n = this.newNamespace(prefix, namespaceName);
			return this.declareNamespace(n);
		}
	}

	public XmlNamespace declareNamespace(XmlNamespace n) {
		if(n.getPrefix() == null) {
			throw new XmlBuilderException("namespace added to element must have not null prefix");
		} else {
			if(this.nsList == null) {
				this.ensureNamespaceDeclarationsCapacity(5);
			}

			this.nsList.add(n);
			return n;
		}
	}

	public boolean hasNamespaceDeclarations() {
		return this.nsList != null && this.nsList.size() > 0;
	}

	public XmlNamespace lookupNamespaceByPrefix(String namespacePrefix) {
		if(namespacePrefix == null) {
			throw new IllegalArgumentException("namespace prefix can not be null");
		} else {
			if(this.hasNamespaceDeclarations()) {
				int length = this.nsList.size();

				for(int i = 0; i < length; ++i) {
					XmlNamespace n = (XmlNamespace)this.nsList.get(i);
					if(namespacePrefix.equals(n.getPrefix())) {
						return n;
					}
				}
			}

			return this.parent != null && this.parent instanceof XmlElement ? ((XmlElement)this.parent).lookupNamespaceByPrefix(namespacePrefix) : null;
		}
	}

	public XmlNamespace lookupNamespaceByName(String namespaceName) {
		if(namespaceName == null) {
			throw new IllegalArgumentException("namespace name can not ber null");
		} else {
			if(this.hasNamespaceDeclarations()) {
				int length = this.nsList.size();

				for(int i = 0; i < length; ++i) {
					XmlNamespace n = (XmlNamespace)this.nsList.get(i);
					if(namespaceName.equals(n.getNamespaceName())) {
						return n;
					}
				}
			}

			return this.parent != null && this.parent instanceof XmlElement ? ((XmlElement)this.parent).lookupNamespaceByName(namespaceName) : null;
		}
	}

	public Iterator namespaces() {
		return this.nsList == null ? EMPTY_ITERATOR : this.nsList.iterator();
	}

	public XmlNamespace newNamespace(String namespaceName) {
		return this.newNamespace((String)null, namespaceName);
	}

	public XmlNamespace newNamespace(String prefix, String namespaceName) {
		return new XmlNamespaceImpl(prefix, namespaceName);
	}

	public void ensureNamespaceDeclarationsCapacity(int minCapacity) {
		if(this.nsList == null) {
			this.nsList = new ArrayList(minCapacity);
		} else {
			((ArrayList)this.nsList).ensureCapacity(minCapacity);
		}

	}

	public void removeAllNamespaceDeclarations() {
		this.nsList = null;
	}

	public void addChild(Object child) {
		if(child == null) {
			throw new NullPointerException();
		} else {
			if(this.children == null) {
				this.ensureChildrenCapacity(1);
			}

			this.children.add(child);
		}
	}

	public void addChild(int index, Object child) {
		if(this.children == null) {
			this.ensureChildrenCapacity(1);
		}

		this.children.add(index, child);
	}

	private void checkChildParent(Object child) {
		if(child instanceof XmlContainer) {
			if(child instanceof XmlElement) {
				XmlElement elChild = (XmlElement)child;
				XmlContainer childParent = elChild.getParent();
				if(childParent != null && childParent != this.parent) {
					throw new XmlBuilderException("child must have no parent to be added to this node");
				}
			} else if(child instanceof XmlDocument) {
				throw new XmlBuilderException("docuemet can not be stored as element child");
			}
		}

	}

	private void setChildParent(Object child) {
		if(child instanceof XmlElement) {
			XmlElement elChild = (XmlElement)child;
			elChild.setParent(this);
		}

	}

	public XmlElement addElement(XmlElement element) {
		this.checkChildParent(element);
		this.addChild(element);
		this.setChildParent(element);
		return element;
	}

	public XmlElement addElement(int pos, XmlElement element) {
		this.checkChildParent(element);
		this.addChild(pos, element);
		this.setChildParent(element);
		return element;
	}

	public XmlElement addElement(XmlNamespace namespace, String name) {
		XmlElement el = this.newElement(namespace, name);
		this.addChild(el);
		this.setChildParent(el);
		return el;
	}

	public XmlElement addElement(String name) {
		return this.addElement((XmlNamespace)null, name);
	}

	public Iterator children() {
		return this.children == null ? EMPTY_ITERATOR : this.children.iterator();
	}

	public Iterable requiredElementContent() {
		return this.children == null ? EMPTY_ITERABLE : new Iterable() {
			public Iterator iterator() {
				return new XmlElementImpl.RequiredElementContentIterator(XmlElementImpl.this.children.iterator());
			}
		};
	}

	public String requiredTextContent() {
		if(this.children == null) {
			return "";
		} else if(this.children.size() == 0) {
			return "";
		} else if(this.children.size() == 1) {
			Object i1 = this.children.get(0);
			if(i1 instanceof String) {
				return i1.toString();
			} else if(i1 instanceof XmlCharacters) {
				return ((XmlCharacters)i1).getText();
			} else {
				throw new XmlBuilderException("expected text content and not " + (i1 != null ? i1.getClass() : null) + " with \'" + i1 + "\'");
			}
		} else {
			Iterator i = this.children();
			StringBuffer buf = new StringBuffer();

			while(i.hasNext()) {
				Object child = i.next();
				if(child instanceof String) {
					buf.append(child.toString());
				} else {
					if(!(child instanceof XmlCharacters)) {
						throw new XmlBuilderException("expected text content and not " + child.getClass() + " with \'" + child + "\'");
					}

					buf.append(((XmlCharacters)child).getText());
				}
			}

			return buf.toString();
		}
	}

	public void ensureChildrenCapacity(int minCapacity) {
		if(this.children == null) {
			this.children = new ArrayList(minCapacity);
		} else {
			((ArrayList)this.children).ensureCapacity(minCapacity);
		}

	}

	public XmlElement element(int position) {
		if(this.children == null) {
			return null;
		} else {
			int length = this.children.size();
			int count = 0;
			if(position >= 0 && position < length + 1) {
				for(int pos = 0; pos < length; ++pos) {
					Object child = this.children.get(pos);
					if(child instanceof XmlElement) {
						++count;
						if(count == position) {
							return (XmlElement)child;
						}
					}
				}

				throw new IndexOutOfBoundsException("position " + position + " too big as only " + count + " element(s) available");
			} else {
				throw new IndexOutOfBoundsException("position " + position + " bigger or equal to " + length + " children");
			}
		}
	}

	public XmlElement requiredElement(XmlNamespace n, String name) throws XmlBuilderException {
		XmlElement el = this.element(n, name);
		if(el == null) {
			throw new XmlBuilderException("could not find element with name " + name + " in namespace " + (n != null ? n.getNamespaceName() : null));
		} else {
			return el;
		}
	}

	public XmlElement element(XmlNamespace n, String name) {
		return this.element(n, name, false);
	}

	public XmlElement element(XmlNamespace n, String name, boolean create) {
		XmlElement e = n != null ? this.findElementByName(n.getNamespaceName(), name) : this.findElementByName(name);
		return e != null ? e : (create ? this.addElement(n, name) : null);
	}

	public Iterable elements(final XmlNamespace n, final String name) {
		return new Iterable() {
			public Iterator iterator() {
				return XmlElementImpl.this.new ElementsSimpleIterator(n, name, XmlElementImpl.this.children());
			}
		};
	}

	public XmlElement findElementByName(String name) {
		if(this.children == null) {
			return null;
		} else {
			int length = this.children.size();

			for(int i = 0; i < length; ++i) {
				Object child = this.children.get(i);
				if(child instanceof XmlElement) {
					XmlElement childEl = (XmlElement)child;
					if(name.equals(childEl.getName())) {
						return childEl;
					}
				}
			}

			return null;
		}
	}

	public XmlElement findElementByName(String namespaceName, String name, XmlElement elementToStartLooking) {
		throw new UnsupportedOperationException();
	}

	public XmlElement findElementByName(String name, XmlElement elementToStartLooking) {
		throw new UnsupportedOperationException();
	}

	public XmlElement findElementByName(String namespaceName, String name) {
		if(this.children == null) {
			return null;
		} else {
			int length = this.children.size();

			for(int i = 0; i < length; ++i) {
				Object child = this.children.get(i);
				if(child instanceof XmlElement) {
					XmlElement childEl = (XmlElement)child;
					XmlNamespace namespace = childEl.getNamespace();
					if(namespace != null) {
						if(name.equals(childEl.getName()) && namespaceName.equals(namespace.getNamespaceName())) {
							return childEl;
						}
					} else if(name.equals(childEl.getName()) && namespaceName == null) {
						return childEl;
					}
				}
			}

			return null;
		}
	}

	public boolean hasChild(Object child) {
		if(this.children == null) {
			return false;
		} else {
			for(int i = 0; i < this.children.size(); ++i) {
				if(this.children.get(i) == child) {
					return true;
				}
			}

			return false;
		}
	}

	public boolean hasChildren() {
		return this.children != null && this.children.size() > 0;
	}

	public void insertChild(int pos, Object childToInsert) {
		if(this.children == null) {
			this.ensureChildrenCapacity(1);
		}

		this.children.add(pos, childToInsert);
	}

	public XmlElement newElement(String name) {
		return this.newElement((XmlNamespace)null, name);
	}

	public XmlElement newElement(String namespace, String name) {
		return new XmlElementImpl(namespace, name);
	}

	public XmlElement newElement(XmlNamespace namespace, String name) {
		return new XmlElementImpl(namespace, name);
	}

	public void replaceChild(Object newChild, Object oldChild) {
		if(newChild == null) {
			throw new IllegalArgumentException("new child to replace can not be null");
		} else if(oldChild == null) {
			throw new IllegalArgumentException("old child to replace can not be null");
		} else if(!this.hasChildren()) {
			throw new XmlBuilderException("no children available for replacement");
		} else {
			int pos = this.children.indexOf(oldChild);
			if(pos == -1) {
				throw new XmlBuilderException("could not find child to replace");
			} else {
				this.children.set(pos, newChild);
			}
		}
	}

	public void removeAllChildren() {
		this.children = null;
	}

	public void removeChild(Object child) {
		if(child == null) {
			throw new IllegalArgumentException("child to remove can not be null");
		} else if(!this.hasChildren()) {
			throw new XmlBuilderException("no children to remove");
		} else {
			int pos = this.children.indexOf(child);
			if(pos != -1) {
				this.children.remove(pos);
			}

		}
	}

	public void replaceChildrenWithText(String textContent) {
		this.removeAllChildren();
		this.addChild(textContent);
	}

	private static final boolean isWhiteSpace(String txt) {
		for(int i = 0; i < txt.length(); ++i) {
			if(txt.charAt(i) != 32 && txt.charAt(i) != 10 && txt.charAt(i) != 9 && txt.charAt(i) != 13) {
				return false;
			}
		}

		return true;
	}

	private static class EmptyIterator implements Iterator {
		private EmptyIterator() {
		}

		public boolean hasNext() {
			return false;
		}

		public Object next() {
			throw new XmlBuilderException("this iterator has no content and next() is not allowed");
		}

		public void remove() {
			throw new XmlBuilderException("this iterator has no content and remove() is not allowed");
		}

		EmptyIterator(Object x0) {
			this();
		}
	}

	private static class RequiredElementContentIterator implements Iterator {
		private Iterator children;
		private XmlElement currentEl;

		RequiredElementContentIterator(Iterator children) {
			this.children = children;
			this.findNextEl();
		}

		private void findNextEl() {
			this.currentEl = null;

			while(true) {
				if(this.children.hasNext()) {
					Object child = this.children.next();
					if(!(child instanceof XmlElement)) {
						if(child instanceof String) {
							String xc1 = child.toString();
							if(!XmlElementImpl.isWhiteSpace(xc1)) {
								throw new XmlBuilderException("only whitespace string children allowed for non mixed element content");
							}
							continue;
						}

						if(!(child instanceof XmlCharacters)) {
							throw new XmlBuilderException("only whitespace characters and element children allowed for non mixed element content and not " + child.getClass());
						}

						XmlCharacters xc = (XmlCharacters)child;
						if(Boolean.TRUE.equals(xc.isWhitespaceContent()) && XmlElementImpl.isWhiteSpace(xc.getText())) {
							continue;
						}

						throw new XmlBuilderException("only whitespace characters children allowed for non mixed element content");
					}

					this.currentEl = (XmlElement)child;
				}

				return;
			}
		}

		public boolean hasNext() {
			return this.currentEl != null;
		}

		public Object next() {
			if(this.currentEl == null) {
				throw new XmlBuilderException("this iterator has no content and next() is not allowed");
			} else {
				XmlElement el = this.currentEl;
				this.findNextEl();
				return el;
			}
		}

		public void remove() {
			throw new XmlBuilderException("this iterator does nto support remove()");
		}
	}

	private class ElementsSimpleIterator implements Iterator {
		private Iterator children;
		private XmlElement currentEl;
		private XmlNamespace n;
		private String name;

		ElementsSimpleIterator(XmlNamespace n, String name, Iterator children) {
			this.children = children;
			this.n = n;
			this.name = name;
			this.findNextEl();
		}

		private void findNextEl() {
			this.currentEl = null;

			while(this.children.hasNext()) {
				Object child = this.children.next();
				if(child instanceof XmlElement) {
					XmlElement el = (XmlElement)child;
					if((this.name == null || el.getName() == this.name || this.name.equals(el.getName())) && (this.n == null || el.getNamespace() == this.n || this.n.equals(el.getNamespace()))) {
						this.currentEl = el;
						break;
					}
				}
			}

		}

		public boolean hasNext() {
			return this.currentEl != null;
		}

		public Object next() {
			if(this.currentEl == null) {
				throw new XmlBuilderException("this iterator has no content and next() is not allowed");
			} else {
				XmlElement el = this.currentEl;
				this.findNextEl();
				return el;
			}
		}

		public void remove() {
			throw new XmlBuilderException("this element iterator does nto support remove()");
		}
	}
}
