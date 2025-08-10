package org.xmlpull.v1.builder.impl;

import org.xmlpull.v1.builder.XmlComment;
import org.xmlpull.v1.builder.XmlContainer;

public class XmlCommentImpl implements XmlComment {
	private XmlContainer owner_;
	private String content_;

	XmlCommentImpl(XmlContainer owner, String content) {
		this.owner_ = owner;
		this.content_ = content;
		if(content == null) {
			throw new IllegalArgumentException("comment content can not be null");
		}
	}

	public String getContent() {
		return this.content_;
	}

	public XmlContainer getParent() {
		return this.owner_;
	}
}
