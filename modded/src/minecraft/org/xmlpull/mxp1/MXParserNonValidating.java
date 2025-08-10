package org.xmlpull.mxp1;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParserException;

public class MXParserNonValidating extends MXParserCachingStrings {
	private boolean processDocDecl;

	public void setFeature(String name, boolean state) throws XmlPullParserException {
		if("http://xmlpull.org/v1/doc/features.html#process-docdecl".equals(name)) {
			if(super.eventType != 0) {
				throw new XmlPullParserException("process DOCDECL feature can only be changed before parsing", this, (Throwable)null);
			}

			this.processDocDecl = state;
			if(!state) {
				;
			}
		} else {
			super.setFeature(name, state);
		}

	}

	public boolean getFeature(String name) {
		return "http://xmlpull.org/v1/doc/features.html#process-docdecl".equals(name) ? this.processDocDecl : super.getFeature(name);
	}

	protected char more() throws IOException, XmlPullParserException {
		return super.more();
	}

	protected char[] lookuEntityReplacement(int entitNameLen) throws XmlPullParserException, IOException {
		int i;
		if(!super.allStringsInterned) {
			i = MXParser.fastHash(super.buf, super.posStart, super.posEnd - super.posStart);

			label53:
			for(int i1 = super.entityEnd - 1; i1 >= 0; --i1) {
				if(i == super.entityNameHash[i1] && entitNameLen == super.entityNameBuf[i1].length) {
					char[] entityBuf = super.entityNameBuf[i1];

					for(int j = 0; j < entitNameLen; ++j) {
						if(super.buf[super.posStart + j] != entityBuf[j]) {
							continue label53;
						}
					}

					if(super.tokenize) {
						super.text = super.entityReplacement[i1];
					}

					return super.entityReplacementBuf[i1];
				}
			}
		} else {
			super.entityRefName = this.newString(super.buf, super.posStart, super.posEnd - super.posStart);

			for(i = super.entityEnd - 1; i >= 0; --i) {
				if(super.entityRefName == super.entityName[i]) {
					if(super.tokenize) {
						super.text = super.entityReplacement[i];
					}

					return super.entityReplacementBuf[i];
				}
			}
		}

		return null;
	}

	protected void parseDocdecl() throws XmlPullParserException, IOException {
		boolean oldTokenize = super.tokenize;

		try {
			char ch = this.more();
			if(ch != 79) {
				throw new XmlPullParserException("expected <!DOCTYPE", this, (Throwable)null);
			}

			ch = this.more();
			if(ch != 67) {
				throw new XmlPullParserException("expected <!DOCTYPE", this, (Throwable)null);
			}

			ch = this.more();
			if(ch != 84) {
				throw new XmlPullParserException("expected <!DOCTYPE", this, (Throwable)null);
			}

			ch = this.more();
			if(ch != 89) {
				throw new XmlPullParserException("expected <!DOCTYPE", this, (Throwable)null);
			}

			ch = this.more();
			if(ch != 80) {
				throw new XmlPullParserException("expected <!DOCTYPE", this, (Throwable)null);
			}

			ch = this.more();
			if(ch != 69) {
				throw new XmlPullParserException("expected <!DOCTYPE", this, (Throwable)null);
			}

			super.posStart = super.pos;
			ch = this.requireNextS();
			int nameStart = super.pos;
			ch = this.readName(ch);
			int nameEnd = super.pos;
			ch = this.skipS(ch);
			if(ch == 83 || ch == 80) {
				ch = this.processExternalId(ch);
				ch = this.skipS(ch);
			}

			if(ch == 91) {
				this.processInternalSubset();
			}

			ch = this.skipS(ch);
			if(ch != 62) {
				throw new XmlPullParserException("expected > to finish <[DOCTYPE but got " + this.printable(ch), this, (Throwable)null);
			}

			super.posEnd = super.pos - 1;
		} finally {
			super.tokenize = oldTokenize;
		}

	}

	protected char processExternalId(char ch) throws XmlPullParserException, IOException {
		return ch;
	}

	protected void processInternalSubset() throws XmlPullParserException, IOException {
		while(true) {
			char ch = this.more();
			if(ch == 93) {
				return;
			}

			if(ch == 37) {
				this.processPEReference();
			} else if(this.isS(ch)) {
				this.skipS(ch);
			} else {
				this.processMarkupDecl(ch);
			}
		}
	}

	protected void processPEReference() throws XmlPullParserException, IOException {
	}

	protected void processMarkupDecl(char ch) throws XmlPullParserException, IOException {
		if(ch != 60) {
			throw new XmlPullParserException("expected < for markupdecl in DTD not " + this.printable(ch), this, (Throwable)null);
		} else {
			ch = this.more();
			if(ch == 63) {
				this.parsePI();
			} else {
				if(ch != 33) {
					throw new XmlPullParserException("expected markupdecl in DTD not " + this.printable(ch), this, (Throwable)null);
				}

				ch = this.more();
				if(ch == 45) {
					this.parseComment();
				} else {
					ch = this.more();
					if(ch == 65) {
						this.processAttlistDecl(ch);
					} else if(ch == 69) {
						ch = this.more();
						if(ch == 76) {
							this.processElementDecl(ch);
						} else {
							if(ch != 78) {
								throw new XmlPullParserException("expected ELEMENT or ENTITY after <! in DTD not " + this.printable(ch), this, (Throwable)null);
							}

							this.processEntityDecl(ch);
						}
					} else {
						if(ch != 78) {
							throw new XmlPullParserException("expected markupdecl after <! in DTD not " + this.printable(ch), this, (Throwable)null);
						}

						this.processNotationDecl(ch);
					}
				}
			}

		}
	}

	protected void processElementDecl(char ch) throws XmlPullParserException, IOException {
		ch = this.requireNextS();
		this.readName(ch);
		ch = this.requireNextS();
	}

	protected void processAttlistDecl(char ch) throws XmlPullParserException, IOException {
	}

	protected void processEntityDecl(char ch) throws XmlPullParserException, IOException {
	}

	protected void processNotationDecl(char ch) throws XmlPullParserException, IOException {
	}

	protected char readName(char ch) throws XmlPullParserException, IOException {
		if(this.isNameStartChar(ch)) {
			throw new XmlPullParserException("XML name must start with name start character not " + this.printable(ch), this, (Throwable)null);
		} else {
			while(this.isNameChar(ch)) {
				ch = this.more();
			}

			return ch;
		}
	}
}
