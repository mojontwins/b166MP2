package org.xmlpull.v1.builder.impl;

import org.xmlpull.v1.builder.XmlBuilderException;
import org.xmlpull.v1.builder.XmlNamespace;

public class XmlNamespaceImpl implements XmlNamespace {
	private String namespaceName;
	private String prefix;

	XmlNamespaceImpl(String namespaceName) {
		if(namespaceName == null) {
			throw new XmlBuilderException("namespace name can not be null");
		} else {
			this.namespaceName = namespaceName;
		}
	}

	XmlNamespaceImpl(String prefix, String namespaceName) {
		this.prefix = prefix;
		if(namespaceName == null) {
			throw new XmlBuilderException("namespace name can not be null");
		} else if(prefix != null && prefix.indexOf(58) != -1) {
			throw new XmlBuilderException("prefix \'" + prefix + "\' for namespace \'" + namespaceName + "\' can not contain colon (:)");
		} else {
			this.namespaceName = namespaceName;
		}
	}

	public String getPrefix() {
		return this.prefix;
	}

	public String getNamespaceName() {
		return this.namespaceName;
	}

	public boolean equals(Object other) {
		if(other == this) {
			return true;
		} else if(other == null) {
			return false;
		} else if(!(other instanceof XmlNamespace)) {
			return false;
		} else {
			XmlNamespace otherNamespace = (XmlNamespace)other;
			return this.getNamespaceName().equals(otherNamespace.getNamespaceName());
		}
	}

	public String toString() {
		return "{prefix=\'" + this.prefix + "\',namespaceName=\'" + this.namespaceName + "\'}";
	}
}
