package org.xmlpull.mxp1;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class MXParser implements XmlPullParser {
	protected static final String XML_URI = "http://www.w3.org/XML/1998/namespace";
	protected static final String XMLNS_URI = "http://www.w3.org/2000/xmlns/";
	protected static final String FEATURE_XML_ROUNDTRIP = "http://xmlpull.org/v1/doc/features.html#xml-roundtrip";
	protected static final String FEATURE_NAMES_INTERNED = "http://xmlpull.org/v1/doc/features.html#names-interned";
	protected static final String PROPERTY_XMLDECL_VERSION = "http://xmlpull.org/v1/doc/properties.html#xmldecl-version";
	protected static final String PROPERTY_XMLDECL_STANDALONE = "http://xmlpull.org/v1/doc/properties.html#xmldecl-standalone";
	protected static final String PROPERTY_XMLDECL_CONTENT = "http://xmlpull.org/v1/doc/properties.html#xmldecl-content";
	protected static final String PROPERTY_LOCATION = "http://xmlpull.org/v1/doc/properties.html#location";
	protected boolean allStringsInterned;
	private static final boolean TRACE_SIZING = false;
	protected boolean processNamespaces;
	protected boolean roundtripSupported;
	protected String location;
	protected int lineNumber;
	protected int columnNumber;
	protected boolean seenRoot;
	protected boolean reachedEnd;
	protected int eventType;
	protected boolean emptyElementTag;
	protected int depth;
	protected char[][] elRawName;
	protected int[] elRawNameEnd;
	protected int[] elRawNameLine;
	protected String[] elName;
	protected String[] elPrefix;
	protected String[] elUri;
	protected int[] elNamespaceCount;
	protected int attributeCount;
	protected String[] attributeName;
	protected int[] attributeNameHash;
	protected String[] attributePrefix;
	protected String[] attributeUri;
	protected String[] attributeValue;
	protected int namespaceEnd;
	protected String[] namespacePrefix;
	protected int[] namespacePrefixHash;
	protected String[] namespaceUri;
	protected int entityEnd;
	protected String[] entityName;
	protected char[][] entityNameBuf;
	protected String[] entityReplacement;
	protected char[][] entityReplacementBuf;
	protected int[] entityNameHash;
	protected static final int READ_CHUNK_SIZE = 8192;
	protected Reader reader;
	protected String inputEncoding;
	protected InputStream inputStream;
	protected int bufLoadFactor = 95;
	protected char[] buf = new char[Runtime.getRuntime().freeMemory() > 1000000L ? 8192 : 256];
	protected int bufSoftLimit = this.bufLoadFactor * this.buf.length / 100;
	protected boolean preventBufferCompaction;
	protected int bufAbsoluteStart;
	protected int bufStart;
	protected int bufEnd;
	protected int pos;
	protected int posStart;
	protected int posEnd;
	protected char[] pc = new char[Runtime.getRuntime().freeMemory() > 1000000L ? 8192 : 64];
	protected int pcStart;
	protected int pcEnd;
	protected boolean usePC;
	protected boolean seenStartTag;
	protected boolean seenEndTag;
	protected boolean pastEndTag;
	protected boolean seenAmpersand;
	protected boolean seenMarkup;
	protected boolean seenDocdecl;
	protected boolean tokenize;
	protected String text;
	protected String entityRefName;
	protected String xmlDeclVersion;
	protected Boolean xmlDeclStandalone;
	protected String xmlDeclContent;
	protected char[] charRefOneCharBuf = new char[1];
	protected static final char[] VERSION = "version".toCharArray();
	protected static final char[] NCODING = "ncoding".toCharArray();
	protected static final char[] TANDALONE = "tandalone".toCharArray();
	protected static final char[] YES = "yes".toCharArray();
	protected static final char[] NO = "no".toCharArray();
	protected static final int LOOKUP_MAX = 1024;
	protected static final char LOOKUP_MAX_CHAR = '\u0400';
	protected static boolean[] lookupNameStartChar = new boolean[1024];
	protected static boolean[] lookupNameChar = new boolean[1024];

	protected void resetStringCache() {
	}

	protected String newString(char[] cbuf, int off, int len) {
		return new String(cbuf, off, len);
	}

	protected String newStringIntern(char[] cbuf, int off, int len) {
		return (new String(cbuf, off, len)).intern();
	}

	protected void ensureElementsCapacity() {
		int elStackSize = this.elName != null ? this.elName.length : 0;
		if(this.depth + 1 >= elStackSize) {
			int newSize = (this.depth >= 7 ? 2 * this.depth : 8) + 2;
			boolean needsCopying = elStackSize > 0;
			String[] arr = null;
			arr = new String[newSize];
			if(needsCopying) {
				System.arraycopy(this.elName, 0, arr, 0, elStackSize);
			}

			this.elName = arr;
			arr = new String[newSize];
			if(needsCopying) {
				System.arraycopy(this.elPrefix, 0, arr, 0, elStackSize);
			}

			this.elPrefix = arr;
			arr = new String[newSize];
			if(needsCopying) {
				System.arraycopy(this.elUri, 0, arr, 0, elStackSize);
			}

			this.elUri = arr;
			int[] iarr = new int[newSize];
			if(needsCopying) {
				System.arraycopy(this.elNamespaceCount, 0, iarr, 0, elStackSize);
			} else {
				iarr[0] = 0;
			}

			this.elNamespaceCount = iarr;
			iarr = new int[newSize];
			if(needsCopying) {
				System.arraycopy(this.elRawNameEnd, 0, iarr, 0, elStackSize);
			}

			this.elRawNameEnd = iarr;
			iarr = new int[newSize];
			if(needsCopying) {
				System.arraycopy(this.elRawNameLine, 0, iarr, 0, elStackSize);
			}

			this.elRawNameLine = iarr;
			char[][] carr = new char[newSize][];
			if(needsCopying) {
				System.arraycopy(this.elRawName, 0, carr, 0, elStackSize);
			}

			this.elRawName = carr;
		}

	}

	protected void ensureAttributesCapacity(int size) {
		int attrPosSize = this.attributeName != null ? this.attributeName.length : 0;
		if(size >= attrPosSize) {
			int newSize = size > 7 ? 2 * size : 8;
			boolean needsCopying = attrPosSize > 0;
			String[] arr = null;
			arr = new String[newSize];
			if(needsCopying) {
				System.arraycopy(this.attributeName, 0, arr, 0, attrPosSize);
			}

			this.attributeName = arr;
			arr = new String[newSize];
			if(needsCopying) {
				System.arraycopy(this.attributePrefix, 0, arr, 0, attrPosSize);
			}

			this.attributePrefix = arr;
			arr = new String[newSize];
			if(needsCopying) {
				System.arraycopy(this.attributeUri, 0, arr, 0, attrPosSize);
			}

			this.attributeUri = arr;
			arr = new String[newSize];
			if(needsCopying) {
				System.arraycopy(this.attributeValue, 0, arr, 0, attrPosSize);
			}

			this.attributeValue = arr;
			if(!this.allStringsInterned) {
				int[] iarr = new int[newSize];
				if(needsCopying) {
					System.arraycopy(this.attributeNameHash, 0, iarr, 0, attrPosSize);
				}

				this.attributeNameHash = iarr;
			}

			arr = null;
		}

	}

	protected void ensureNamespacesCapacity(int size) {
		int namespaceSize = this.namespacePrefix != null ? this.namespacePrefix.length : 0;
		if(size >= namespaceSize) {
			int newSize = size > 7 ? 2 * size : 8;
			String[] newNamespacePrefix = new String[newSize];
			String[] newNamespaceUri = new String[newSize];
			if(this.namespacePrefix != null) {
				System.arraycopy(this.namespacePrefix, 0, newNamespacePrefix, 0, this.namespaceEnd);
				System.arraycopy(this.namespaceUri, 0, newNamespaceUri, 0, this.namespaceEnd);
			}

			this.namespacePrefix = newNamespacePrefix;
			this.namespaceUri = newNamespaceUri;
			if(!this.allStringsInterned) {
				int[] newNamespacePrefixHash = new int[newSize];
				if(this.namespacePrefixHash != null) {
					System.arraycopy(this.namespacePrefixHash, 0, newNamespacePrefixHash, 0, this.namespaceEnd);
				}

				this.namespacePrefixHash = newNamespacePrefixHash;
			}
		}

	}

	protected static final int fastHash(char[] ch, int off, int len) {
		if(len == 0) {
			return 0;
		} else {
			char hash = ch[off];
			int hash1 = (hash << 7) + ch[off + len - 1];
			if(len > 16) {
				hash1 = (hash1 << 7) + ch[off + len / 4];
			}

			if(len > 8) {
				hash1 = (hash1 << 7) + ch[off + len / 2];
			}

			return hash1;
		}
	}

	protected void ensureEntityCapacity() {
		int entitySize = this.entityReplacementBuf != null ? this.entityReplacementBuf.length : 0;
		if(this.entityEnd >= entitySize) {
			int newSize = this.entityEnd > 7 ? 2 * this.entityEnd : 8;
			String[] newEntityName = new String[newSize];
			char[][] newEntityNameBuf = new char[newSize][];
			String[] newEntityReplacement = new String[newSize];
			char[][] newEntityReplacementBuf = new char[newSize][];
			if(this.entityName != null) {
				System.arraycopy(this.entityName, 0, newEntityName, 0, this.entityEnd);
				System.arraycopy(this.entityNameBuf, 0, newEntityNameBuf, 0, this.entityEnd);
				System.arraycopy(this.entityReplacement, 0, newEntityReplacement, 0, this.entityEnd);
				System.arraycopy(this.entityReplacementBuf, 0, newEntityReplacementBuf, 0, this.entityEnd);
			}

			this.entityName = newEntityName;
			this.entityNameBuf = newEntityNameBuf;
			this.entityReplacement = newEntityReplacement;
			this.entityReplacementBuf = newEntityReplacementBuf;
			if(!this.allStringsInterned) {
				int[] newEntityNameHash = new int[newSize];
				if(this.entityNameHash != null) {
					System.arraycopy(this.entityNameHash, 0, newEntityNameHash, 0, this.entityEnd);
				}

				this.entityNameHash = newEntityNameHash;
			}
		}

	}

	protected void reset() {
		this.location = null;
		this.lineNumber = 1;
		this.columnNumber = 0;
		this.seenRoot = false;
		this.reachedEnd = false;
		this.eventType = 0;
		this.emptyElementTag = false;
		this.depth = 0;
		this.attributeCount = 0;
		this.namespaceEnd = 0;
		this.entityEnd = 0;
		this.reader = null;
		this.inputEncoding = null;
		this.preventBufferCompaction = false;
		this.bufAbsoluteStart = 0;
		this.bufEnd = this.bufStart = 0;
		this.pos = this.posStart = this.posEnd = 0;
		this.pcEnd = this.pcStart = 0;
		this.usePC = false;
		this.seenStartTag = false;
		this.seenEndTag = false;
		this.pastEndTag = false;
		this.seenAmpersand = false;
		this.seenMarkup = false;
		this.seenDocdecl = false;
		this.xmlDeclVersion = null;
		this.xmlDeclStandalone = null;
		this.xmlDeclContent = null;
		this.resetStringCache();
	}

	public void setFeature(String name, boolean state) throws XmlPullParserException {
		if(name == null) {
			throw new IllegalArgumentException("feature name should not be null");
		} else {
			if("http://xmlpull.org/v1/doc/features.html#process-namespaces".equals(name)) {
				if(this.eventType != 0) {
					throw new XmlPullParserException("namespace processing feature can only be changed before parsing", this, (Throwable)null);
				}

				this.processNamespaces = state;
			} else if("http://xmlpull.org/v1/doc/features.html#names-interned".equals(name)) {
				if(state) {
					throw new XmlPullParserException("interning names in this implementation is not supported");
				}
			} else if("http://xmlpull.org/v1/doc/features.html#process-docdecl".equals(name)) {
				if(state) {
					throw new XmlPullParserException("processing DOCDECL is not supported");
				}
			} else {
				if(!"http://xmlpull.org/v1/doc/features.html#xml-roundtrip".equals(name)) {
					throw new XmlPullParserException("unsupported feature " + name);
				}

				this.roundtripSupported = state;
			}

		}
	}

	public boolean getFeature(String name) {
		if(name == null) {
			throw new IllegalArgumentException("feature name should not be null");
		} else {
			return "http://xmlpull.org/v1/doc/features.html#process-namespaces".equals(name) ? this.processNamespaces : ("http://xmlpull.org/v1/doc/features.html#names-interned".equals(name) ? false : ("http://xmlpull.org/v1/doc/features.html#process-docdecl".equals(name) ? false : ("http://xmlpull.org/v1/doc/features.html#xml-roundtrip".equals(name) ? this.roundtripSupported : false)));
		}
	}

	public void setProperty(String name, Object value) throws XmlPullParserException {
		if("http://xmlpull.org/v1/doc/properties.html#location".equals(name)) {
			this.location = (String)value;
		} else {
			throw new XmlPullParserException("unsupported property: \'" + name + "\'");
		}
	}

	public Object getProperty(String name) {
		if(name == null) {
			throw new IllegalArgumentException("property name should not be null");
		} else {
			return "http://xmlpull.org/v1/doc/properties.html#xmldecl-version".equals(name) ? this.xmlDeclVersion : ("http://xmlpull.org/v1/doc/properties.html#xmldecl-standalone".equals(name) ? this.xmlDeclStandalone : ("http://xmlpull.org/v1/doc/properties.html#xmldecl-content".equals(name) ? this.xmlDeclContent : ("http://xmlpull.org/v1/doc/properties.html#location".equals(name) ? this.location : null)));
		}
	}

	public void setInput(Reader in) throws XmlPullParserException {
		this.reset();
		this.reader = in;
	}

	public void setInput(InputStream inputStream, String inputEncoding) throws XmlPullParserException {
		if(inputStream == null) {
			throw new IllegalArgumentException("input stream can not be null");
		} else {
			this.inputStream = inputStream;

			InputStreamReader reader;
			try {
				if(inputEncoding != null) {
					reader = new InputStreamReader(inputStream, inputEncoding);
				} else {
					reader = new InputStreamReader(inputStream, "UTF-8");
				}
			} catch (UnsupportedEncodingException unsupportedEncodingException5) {
				throw new XmlPullParserException("could not create reader for encoding " + inputEncoding + " : " + unsupportedEncodingException5, this, unsupportedEncodingException5);
			}

			this.setInput(reader);
			this.inputEncoding = inputEncoding;
		}
	}

	public String getInputEncoding() {
		return this.inputEncoding;
	}

	public void defineEntityReplacementText(String entityName, String replacementText) throws XmlPullParserException {
		this.ensureEntityCapacity();
		this.entityName[this.entityEnd] = this.newString(entityName.toCharArray(), 0, entityName.length());
		this.entityNameBuf[this.entityEnd] = entityName.toCharArray();
		this.entityReplacement[this.entityEnd] = replacementText;
		this.entityReplacementBuf[this.entityEnd] = replacementText.toCharArray();
		if(!this.allStringsInterned) {
			this.entityNameHash[this.entityEnd] = fastHash(this.entityNameBuf[this.entityEnd], 0, this.entityNameBuf[this.entityEnd].length);
		}

		++this.entityEnd;
	}

	public int getNamespaceCount(int depth) throws XmlPullParserException {
		if(this.processNamespaces && depth != 0) {
			if(depth >= 0 && depth <= this.depth) {
				return this.elNamespaceCount[depth];
			} else {
				throw new IllegalArgumentException("allowed namespace depth 0.." + this.depth + " not " + depth);
			}
		} else {
			return 0;
		}
	}

	public String getNamespacePrefix(int pos) throws XmlPullParserException {
		if(pos < this.namespaceEnd) {
			return this.namespacePrefix[pos];
		} else {
			throw new XmlPullParserException("position " + pos + " exceeded number of available namespaces " + this.namespaceEnd);
		}
	}

	public String getNamespaceUri(int pos) throws XmlPullParserException {
		if(pos < this.namespaceEnd) {
			return this.namespaceUri[pos];
		} else {
			throw new XmlPullParserException("position " + pos + " exceeded number of available namespaces " + this.namespaceEnd);
		}
	}

	public String getNamespace(String prefix) {
		int i;
		if(prefix != null) {
			for(i = this.namespaceEnd - 1; i >= 0; --i) {
				if(prefix.equals(this.namespacePrefix[i])) {
					return this.namespaceUri[i];
				}
			}

			if("xml".equals(prefix)) {
				return "http://www.w3.org/XML/1998/namespace";
			}

			if("xmlns".equals(prefix)) {
				return "http://www.w3.org/2000/xmlns/";
			}
		} else {
			for(i = this.namespaceEnd - 1; i >= 0; --i) {
				if(this.namespacePrefix[i] == null) {
					return this.namespaceUri[i];
				}
			}
		}

		return null;
	}

	public int getDepth() {
		return this.depth;
	}

	private static int findFragment(int bufMinPos, char[] b, int start, int end) {
		if(start < bufMinPos) {
			start = bufMinPos;
			if(bufMinPos > end) {
				start = end;
			}

			return start;
		} else {
			if(end - start > 65) {
				start = end - 10;
			}

			int i = start + 1;

			char c;
			do {
				--i;
				if(i <= bufMinPos || end - i > 65) {
					break;
				}

				c = b[i];
			} while(c != 60 || start - i <= 10);

			return i;
		}
	}

	public String getPositionDescription() {
		String fragment = null;
		if(this.posStart <= this.pos) {
			int start = findFragment(0, this.buf, this.posStart, this.pos);
			if(start < this.pos) {
				fragment = new String(this.buf, start, this.pos - start);
			}

			if(this.bufAbsoluteStart > 0 || start > 0) {
				fragment = "..." + fragment;
			}
		}

		return " " + XmlPullParser.TYPES[this.eventType] + (fragment != null ? " seen " + this.printable(fragment) + "..." : "") + " " + (this.location != null ? this.location : "") + "@" + this.getLineNumber() + ":" + this.getColumnNumber();
	}

	public int getLineNumber() {
		return this.lineNumber;
	}

	public int getColumnNumber() {
		return this.columnNumber;
	}

	public boolean isWhitespace() throws XmlPullParserException {
		if(this.eventType != 4 && this.eventType != 5) {
			if(this.eventType == 7) {
				return true;
			} else {
				throw new XmlPullParserException("no content available to check for white spaces");
			}
		} else {
			int i;
			if(this.usePC) {
				for(i = this.pcStart; i < this.pcEnd; ++i) {
					if(!this.isS(this.pc[i])) {
						return false;
					}
				}

				return true;
			} else {
				for(i = this.posStart; i < this.posEnd; ++i) {
					if(!this.isS(this.buf[i])) {
						return false;
					}
				}

				return true;
			}
		}
	}

	public String getText() {
		if(this.eventType != 0 && this.eventType != 1) {
			if(this.eventType == 6) {
				return this.text;
			} else {
				if(this.text == null) {
					if(this.usePC && this.eventType != 2 && this.eventType != 3) {
						this.text = new String(this.pc, this.pcStart, this.pcEnd - this.pcStart);
					} else {
						this.text = new String(this.buf, this.posStart, this.posEnd - this.posStart);
					}
				}

				return this.text;
			}
		} else {
			return null;
		}
	}

	public char[] getTextCharacters(int[] holderForStartAndLength) {
		if(this.eventType == 4) {
			if(this.usePC) {
				holderForStartAndLength[0] = this.pcStart;
				holderForStartAndLength[1] = this.pcEnd - this.pcStart;
				return this.pc;
			} else {
				holderForStartAndLength[0] = this.posStart;
				holderForStartAndLength[1] = this.posEnd - this.posStart;
				return this.buf;
			}
		} else if(this.eventType != 2 && this.eventType != 3 && this.eventType != 5 && this.eventType != 9 && this.eventType != 6 && this.eventType != 8 && this.eventType != 7 && this.eventType != 10) {
			if(this.eventType != 0 && this.eventType != 1) {
				throw new IllegalArgumentException("unknown text eventType: " + this.eventType);
			} else {
				holderForStartAndLength[0] = holderForStartAndLength[1] = -1;
				return null;
			}
		} else {
			holderForStartAndLength[0] = this.posStart;
			holderForStartAndLength[1] = this.posEnd - this.posStart;
			return this.buf;
		}
	}

	public String getNamespace() {
		return this.eventType == 2 ? (this.processNamespaces ? this.elUri[this.depth] : "") : (this.eventType == 3 ? (this.processNamespaces ? this.elUri[this.depth] : "") : null);
	}

	public String getName() {
		if(this.eventType == 2) {
			return this.elName[this.depth];
		} else if(this.eventType == 3) {
			return this.elName[this.depth];
		} else if(this.eventType == 6) {
			if(this.entityRefName == null) {
				this.entityRefName = this.newString(this.buf, this.posStart, this.posEnd - this.posStart);
			}

			return this.entityRefName;
		} else {
			return null;
		}
	}

	public String getPrefix() {
		return this.eventType == 2 ? this.elPrefix[this.depth] : (this.eventType == 3 ? this.elPrefix[this.depth] : null);
	}

	public boolean isEmptyElementTag() throws XmlPullParserException {
		if(this.eventType != 2) {
			throw new XmlPullParserException("parser must be on START_TAG to check for empty element", this, (Throwable)null);
		} else {
			return this.emptyElementTag;
		}
	}

	public int getAttributeCount() {
		return this.eventType != 2 ? -1 : this.attributeCount;
	}

	public String getAttributeNamespace(int index) {
		if(this.eventType != 2) {
			throw new IndexOutOfBoundsException("only START_TAG can have attributes");
		} else if(!this.processNamespaces) {
			return "";
		} else if(index >= 0 && index < this.attributeCount) {
			return this.attributeUri[index];
		} else {
			throw new IndexOutOfBoundsException("attribute position must be 0.." + (this.attributeCount - 1) + " and not " + index);
		}
	}

	public String getAttributeName(int index) {
		if(this.eventType != 2) {
			throw new IndexOutOfBoundsException("only START_TAG can have attributes");
		} else if(index >= 0 && index < this.attributeCount) {
			return this.attributeName[index];
		} else {
			throw new IndexOutOfBoundsException("attribute position must be 0.." + (this.attributeCount - 1) + " and not " + index);
		}
	}

	public String getAttributePrefix(int index) {
		if(this.eventType != 2) {
			throw new IndexOutOfBoundsException("only START_TAG can have attributes");
		} else if(!this.processNamespaces) {
			return null;
		} else if(index >= 0 && index < this.attributeCount) {
			return this.attributePrefix[index];
		} else {
			throw new IndexOutOfBoundsException("attribute position must be 0.." + (this.attributeCount - 1) + " and not " + index);
		}
	}

	public String getAttributeType(int index) {
		if(this.eventType != 2) {
			throw new IndexOutOfBoundsException("only START_TAG can have attributes");
		} else if(index >= 0 && index < this.attributeCount) {
			return "CDATA";
		} else {
			throw new IndexOutOfBoundsException("attribute position must be 0.." + (this.attributeCount - 1) + " and not " + index);
		}
	}

	public boolean isAttributeDefault(int index) {
		if(this.eventType != 2) {
			throw new IndexOutOfBoundsException("only START_TAG can have attributes");
		} else if(index >= 0 && index < this.attributeCount) {
			return false;
		} else {
			throw new IndexOutOfBoundsException("attribute position must be 0.." + (this.attributeCount - 1) + " and not " + index);
		}
	}

	public String getAttributeValue(int index) {
		if(this.eventType != 2) {
			throw new IndexOutOfBoundsException("only START_TAG can have attributes");
		} else if(index >= 0 && index < this.attributeCount) {
			return this.attributeValue[index];
		} else {
			throw new IndexOutOfBoundsException("attribute position must be 0.." + (this.attributeCount - 1) + " and not " + index);
		}
	}

	public String getAttributeValue(String namespace, String name) {
		if(this.eventType != 2) {
			throw new IndexOutOfBoundsException("only START_TAG can have attributes" + this.getPositionDescription());
		} else if(name == null) {
			throw new IllegalArgumentException("attribute name can not be null");
		} else {
			int i;
			if(this.processNamespaces) {
				if(namespace == null) {
					namespace = "";
				}

				for(i = 0; i < this.attributeCount; ++i) {
					if((namespace == this.attributeUri[i] || namespace.equals(this.attributeUri[i])) && name.equals(this.attributeName[i])) {
						return this.attributeValue[i];
					}
				}
			} else {
				if(namespace != null && namespace.length() == 0) {
					namespace = null;
				}

				if(namespace != null) {
					throw new IllegalArgumentException("when namespaces processing is disabled attribute namespace must be null");
				}

				for(i = 0; i < this.attributeCount; ++i) {
					if(name.equals(this.attributeName[i])) {
						return this.attributeValue[i];
					}
				}
			}

			return null;
		}
	}

	public int getEventType() throws XmlPullParserException {
		return this.eventType;
	}

	public void require(int type, String namespace, String name) throws XmlPullParserException, IOException {
		if(!this.processNamespaces && namespace != null) {
			throw new XmlPullParserException("processing namespaces must be enabled on parser (or factory) to have possible namespaces declared on elements" + " (position:" + this.getPositionDescription() + ")");
		} else if(type != this.getEventType() || namespace != null && !namespace.equals(this.getNamespace()) || name != null && !name.equals(this.getName())) {
			throw new XmlPullParserException("expected event " + XmlPullParser.TYPES[type] + (name != null ? " with name \'" + name + "\'" : "") + (namespace != null && name != null ? " and" : "") + (namespace != null ? " with namespace \'" + namespace + "\'" : "") + " but got" + (type != this.getEventType() ? " " + XmlPullParser.TYPES[this.getEventType()] : "") + (name != null && this.getName() != null && !name.equals(this.getName()) ? " name \'" + this.getName() + "\'" : "") + (namespace != null && name != null && this.getName() != null && !name.equals(this.getName()) && this.getNamespace() != null && !namespace.equals(this.getNamespace()) ? " and" : "") + (namespace != null && this.getNamespace() != null && !namespace.equals(this.getNamespace()) ? " namespace \'" + this.getNamespace() + "\'" : "") + " (position:" + this.getPositionDescription() + ")");
		}
	}

	public void skipSubTree() throws XmlPullParserException, IOException {
		this.require(2, (String)null, (String)null);
		int level = 1;

		while(level > 0) {
			int eventType = this.next();
			if(eventType == 3) {
				--level;
			} else if(eventType == 2) {
				++level;
			}
		}

	}

	public String nextText() throws XmlPullParserException, IOException {
		if(this.getEventType() != 2) {
			throw new XmlPullParserException("parser must be on START_TAG to read next text", this, (Throwable)null);
		} else {
			int eventType = this.next();
			if(eventType == 4) {
				String result = this.getText();
				eventType = this.next();
				if(eventType != 3) {
					throw new XmlPullParserException("TEXT must be immediately followed by END_TAG and not " + XmlPullParser.TYPES[this.getEventType()], this, (Throwable)null);
				} else {
					return result;
				}
			} else if(eventType == 3) {
				return "";
			} else {
				throw new XmlPullParserException("parser must be on START_TAG or TEXT to read text", this, (Throwable)null);
			}
		}
	}

	public int nextTag() throws XmlPullParserException, IOException {
		this.next();
		if(this.eventType == 4 && this.isWhitespace()) {
			this.next();
		}

		if(this.eventType != 2 && this.eventType != 3) {
			throw new XmlPullParserException("expected START_TAG or END_TAG not " + XmlPullParser.TYPES[this.getEventType()], this, (Throwable)null);
		} else {
			return this.eventType;
		}
	}

	public int next() throws XmlPullParserException, IOException {
		this.tokenize = false;
		return this.nextImpl();
	}

	public int nextToken() throws XmlPullParserException, IOException {
		this.tokenize = true;
		return this.nextImpl();
	}

	protected int nextImpl() throws XmlPullParserException, IOException {
		this.text = null;
		this.pcEnd = this.pcStart = 0;
		this.usePC = false;
		this.bufStart = this.posEnd;
		if(this.pastEndTag) {
			this.pastEndTag = false;
			--this.depth;
			this.namespaceEnd = this.elNamespaceCount[this.depth];
		}

		if(this.emptyElementTag) {
			this.emptyElementTag = false;
			this.pastEndTag = true;
			return this.eventType = 3;
		} else if(this.depth <= 0) {
			return this.seenRoot ? this.parseEpilog() : this.parseProlog();
		} else if(this.seenStartTag) {
			this.seenStartTag = false;
			return this.eventType = this.parseStartTag();
		} else if(this.seenEndTag) {
			this.seenEndTag = false;
			return this.eventType = this.parseEndTag();
		} else {
			char ch;
			if(this.seenMarkup) {
				this.seenMarkup = false;
				ch = 60;
			} else if(this.seenAmpersand) {
				this.seenAmpersand = false;
				ch = 38;
			} else {
				ch = this.more();
			}

			this.posStart = this.pos - 1;
			boolean hadCharData = false;
			boolean needsMerging = false;

			while(true) {
				while(true) {
					int i8;
					int i9;
					if(ch == 60) {
						if(hadCharData && this.tokenize) {
							this.seenMarkup = true;
							return this.eventType = 4;
						}

						ch = this.more();
						if(ch == 47) {
							if(!this.tokenize && hadCharData) {
								this.seenEndTag = true;
								return this.eventType = 4;
							}

							return this.eventType = this.parseEndTag();
						}

						if(ch == 33) {
							ch = this.more();
							if(ch == 45) {
								this.parseComment();
								if(this.tokenize) {
									return this.eventType = 9;
								}

								if(!this.usePC && hadCharData) {
									needsMerging = true;
									break;
								}

								this.posStart = this.pos;
								break;
							}

							if(ch != 91) {
								throw new XmlPullParserException("unexpected character in markup " + this.printable(ch), this, (Throwable)null);
							}

							this.parseCDSect(hadCharData);
							if(this.tokenize) {
								return this.eventType = 5;
							}

							i8 = this.posStart;
							i9 = this.posEnd;
							int i11 = i9 - i8;
							if(i11 > 0) {
								hadCharData = true;
								if(!this.usePC) {
									needsMerging = true;
								}
							}
							break;
						}

						if(ch != 63) {
							if(this.isNameStartChar(ch)) {
								if(!this.tokenize && hadCharData) {
									this.seenStartTag = true;
									return this.eventType = 4;
								}

								return this.eventType = this.parseStartTag();
							}

							throw new XmlPullParserException("unexpected character in markup " + this.printable(ch), this, (Throwable)null);
						}

						this.parsePI();
						if(this.tokenize) {
							return this.eventType = 8;
						}

						if(!this.usePC && hadCharData) {
							needsMerging = true;
							break;
						}

						this.posStart = this.pos;
						break;
					}

					if(ch == 38) {
						if(this.tokenize && hadCharData) {
							this.seenAmpersand = true;
							return this.eventType = 4;
						}

						i8 = this.posStart + this.bufAbsoluteStart;
						i9 = this.posEnd + this.bufAbsoluteStart;
						char[] c10 = this.parseEntityRef();
						if(this.tokenize) {
							return this.eventType = 6;
						}

						if(c10 == null) {
							if(this.entityRefName == null) {
								this.entityRefName = this.newString(this.buf, this.posStart, this.posEnd - this.posStart);
							}

							throw new XmlPullParserException("could not resolve entity named \'" + this.printable(this.entityRefName) + "\'", this, (Throwable)null);
						}

						this.posStart = i8 - this.bufAbsoluteStart;
						this.posEnd = i9 - this.bufAbsoluteStart;
						if(!this.usePC) {
							if(hadCharData) {
								this.joinPC();
								needsMerging = false;
							} else {
								this.usePC = true;
								this.pcStart = this.pcEnd = 0;
							}
						}

						for(int i12 = 0; i12 < c10.length; ++i12) {
							if(this.pcEnd >= this.pc.length) {
								this.ensurePC(this.pcEnd);
							}

							this.pc[this.pcEnd++] = c10[i12];
						}

						hadCharData = true;
						break;
					}

					if(needsMerging) {
						this.joinPC();
						needsMerging = false;
					}

					hadCharData = true;
					boolean normalizedCR = false;
					boolean normalizeInput = !this.tokenize || !this.roundtripSupported;
					boolean seenBracket = false;
					boolean seenBracketBracket = false;

					do {
						if(ch == 93) {
							if(seenBracket) {
								seenBracketBracket = true;
							} else {
								seenBracket = true;
							}
						} else {
							if(seenBracketBracket && ch == 62) {
								throw new XmlPullParserException("characters ]]> are not allowed in content", this, (Throwable)null);
							}

							if(seenBracket) {
								seenBracket = false;
								seenBracketBracket = false;
							}
						}

						if(normalizeInput) {
							if(ch == 13) {
								normalizedCR = true;
								this.posEnd = this.pos - 1;
								if(!this.usePC) {
									if(this.posEnd > this.posStart) {
										this.joinPC();
									} else {
										this.usePC = true;
										this.pcStart = this.pcEnd = 0;
									}
								}

								if(this.pcEnd >= this.pc.length) {
									this.ensurePC(this.pcEnd);
								}

								this.pc[this.pcEnd++] = 10;
							} else if(ch == 10) {
								if(!normalizedCR && this.usePC) {
									if(this.pcEnd >= this.pc.length) {
										this.ensurePC(this.pcEnd);
									}

									this.pc[this.pcEnd++] = 10;
								}

								normalizedCR = false;
							} else {
								if(this.usePC) {
									if(this.pcEnd >= this.pc.length) {
										this.ensurePC(this.pcEnd);
									}

									this.pc[this.pcEnd++] = ch;
								}

								normalizedCR = false;
							}
						}

						ch = this.more();
					} while(ch != 60 && ch != 38);

					this.posEnd = this.pos - 1;
				}

				ch = this.more();
			}
		}
	}

	protected int parseProlog() throws XmlPullParserException, IOException {
		char ch;
		if(this.seenMarkup) {
			ch = this.buf[this.pos - 1];
		} else {
			ch = this.more();
		}

		if(this.eventType == 0) {
			if(ch == 65534) {
				throw new XmlPullParserException("first character in input was UNICODE noncharacter (0xFFFE)- input requires int swapping", this, (Throwable)null);
			}

			if(ch == 65279) {
				ch = this.more();
			}
		}

		this.seenMarkup = false;
		boolean gotS = false;
		this.posStart = this.pos - 1;
		boolean normalizeIgnorableWS = this.tokenize && !this.roundtripSupported;
		boolean normalizedCR = false;

		while(true) {
			if(ch == 60) {
				if(gotS && this.tokenize) {
					this.posEnd = this.pos - 1;
					this.seenMarkup = true;
					return this.eventType = 7;
				}

				ch = this.more();
				if(ch == 63) {
					if(this.parsePI()) {
						if(this.tokenize) {
							return this.eventType = 8;
						}
					} else {
						this.posStart = this.pos;
						gotS = false;
					}
				} else {
					if(ch != 33) {
						if(ch == 47) {
							throw new XmlPullParserException("expected start tag name and not " + this.printable(ch), this, (Throwable)null);
						}

						if(this.isNameStartChar(ch)) {
							this.seenRoot = true;
							return this.parseStartTag();
						}

						throw new XmlPullParserException("expected start tag name and not " + this.printable(ch), this, (Throwable)null);
					}

					ch = this.more();
					if(ch == 68) {
						if(this.seenDocdecl) {
							throw new XmlPullParserException("only one docdecl allowed in XML document", this, (Throwable)null);
						}

						this.seenDocdecl = true;
						this.parseDocdecl();
						if(this.tokenize) {
							return this.eventType = 10;
						}
					} else {
						if(ch != 45) {
							throw new XmlPullParserException("unexpected markup <!" + this.printable(ch), this, (Throwable)null);
						}

						this.parseComment();
						if(this.tokenize) {
							return this.eventType = 9;
						}
					}
				}
			} else {
				if(!this.isS(ch)) {
					throw new XmlPullParserException("only whitespace content allowed before start tag and not " + this.printable(ch), this, (Throwable)null);
				}

				gotS = true;
				if(normalizeIgnorableWS) {
					if(ch == 13) {
						normalizedCR = true;
						if(!this.usePC) {
							this.posEnd = this.pos - 1;
							if(this.posEnd > this.posStart) {
								this.joinPC();
							} else {
								this.usePC = true;
								this.pcStart = this.pcEnd = 0;
							}
						}

						if(this.pcEnd >= this.pc.length) {
							this.ensurePC(this.pcEnd);
						}

						this.pc[this.pcEnd++] = 10;
					} else if(ch == 10) {
						if(!normalizedCR && this.usePC) {
							if(this.pcEnd >= this.pc.length) {
								this.ensurePC(this.pcEnd);
							}

							this.pc[this.pcEnd++] = 10;
						}

						normalizedCR = false;
					} else {
						if(this.usePC) {
							if(this.pcEnd >= this.pc.length) {
								this.ensurePC(this.pcEnd);
							}

							this.pc[this.pcEnd++] = ch;
						}

						normalizedCR = false;
					}
				}
			}

			ch = this.more();
		}
	}

	protected int parseEpilog() throws XmlPullParserException, IOException {
		if(this.eventType == 1) {
			throw new XmlPullParserException("already reached end of XML input", this, (Throwable)null);
		} else if(this.reachedEnd) {
			return this.eventType = 1;
		} else {
			boolean gotS = false;
			boolean normalizeIgnorableWS = this.tokenize && !this.roundtripSupported;
			boolean normalizedCR = false;

			try {
				char ex;
				if(this.seenMarkup) {
					ex = this.buf[this.pos - 1];
				} else {
					ex = this.more();
				}

				this.seenMarkup = false;
				this.posStart = this.pos - 1;
				if(!this.reachedEnd) {
					do {
						if(ex == 60) {
							if(gotS && this.tokenize) {
								this.posEnd = this.pos - 1;
								this.seenMarkup = true;
								return this.eventType = 7;
							}

							ex = this.more();
							if(this.reachedEnd) {
								break;
							}

							if(ex == 63) {
								this.parsePI();
								if(this.tokenize) {
									return this.eventType = 8;
								}
							} else {
								if(ex != 33) {
									if(ex == 47) {
										throw new XmlPullParserException("end tag not allowed in epilog but got " + this.printable(ex), this, (Throwable)null);
									}

									if(this.isNameStartChar(ex)) {
										throw new XmlPullParserException("start tag not allowed in epilog but got " + this.printable(ex), this, (Throwable)null);
									}

									throw new XmlPullParserException("in epilog expected ignorable content and not " + this.printable(ex), this, (Throwable)null);
								}

								ex = this.more();
								if(this.reachedEnd) {
									break;
								}

								if(ex == 68) {
									this.parseDocdecl();
									if(this.tokenize) {
										return this.eventType = 10;
									}
								} else {
									if(ex != 45) {
										throw new XmlPullParserException("unexpected markup <!" + this.printable(ex), this, (Throwable)null);
									}

									this.parseComment();
									if(this.tokenize) {
										return this.eventType = 9;
									}
								}
							}
						} else {
							if(!this.isS(ex)) {
								throw new XmlPullParserException("in epilog non whitespace content is not allowed but got " + this.printable(ex), this, (Throwable)null);
							}

							gotS = true;
							if(normalizeIgnorableWS) {
								if(ex == 13) {
									normalizedCR = true;
									if(!this.usePC) {
										this.posEnd = this.pos - 1;
										if(this.posEnd > this.posStart) {
											this.joinPC();
										} else {
											this.usePC = true;
											this.pcStart = this.pcEnd = 0;
										}
									}

									if(this.pcEnd >= this.pc.length) {
										this.ensurePC(this.pcEnd);
									}

									this.pc[this.pcEnd++] = 10;
								} else if(ex == 10) {
									if(!normalizedCR && this.usePC) {
										if(this.pcEnd >= this.pc.length) {
											this.ensurePC(this.pcEnd);
										}

										this.pc[this.pcEnd++] = 10;
									}

									normalizedCR = false;
								} else {
									if(this.usePC) {
										if(this.pcEnd >= this.pc.length) {
											this.ensurePC(this.pcEnd);
										}

										this.pc[this.pcEnd++] = ex;
									}

									normalizedCR = false;
								}
							}
						}

						ex = this.more();
					} while(!this.reachedEnd);
				}
			} catch (EOFException eOFException5) {
				this.reachedEnd = true;
			}

			if(this.reachedEnd) {
				if(this.tokenize && gotS) {
					this.posEnd = this.pos;
					return this.eventType = 7;
				} else {
					return this.eventType = 1;
				}
			} else {
				throw new XmlPullParserException("internal error in parseEpilog");
			}
		}
	}

	public int parseEndTag() throws XmlPullParserException, IOException {
		char ch = this.more();
		if(!this.isNameStartChar(ch)) {
			throw new XmlPullParserException("expected name start and not " + this.printable(ch), this, (Throwable)null);
		} else {
			this.posStart = this.pos - 3;
			int nameStart = this.pos - 1 + this.bufAbsoluteStart;

			do {
				ch = this.more();
			} while(this.isNameChar(ch));

			int off = nameStart - this.bufAbsoluteStart;
			int len = this.pos - 1 - off;
			char[] cbuf = this.elRawName[this.depth];
			String startname;
			if(this.elRawNameEnd[this.depth] != len) {
				String string9 = new String(cbuf, 0, this.elRawNameEnd[this.depth]);
				startname = new String(this.buf, off, len);
				throw new XmlPullParserException("end tag name </" + startname + "> must match start tag name <" + string9 + ">" + " from line " + this.elRawNameLine[this.depth], this, (Throwable)null);
			} else {
				for(int i = 0; i < len; ++i) {
					if(this.buf[off++] != cbuf[i]) {
						startname = new String(cbuf, 0, len);
						String endname = new String(this.buf, off - i - 1, len);
						throw new XmlPullParserException("end tag name </" + endname + "> must be the same as start tag <" + startname + ">" + " from line " + this.elRawNameLine[this.depth], this, (Throwable)null);
					}
				}

				while(this.isS(ch)) {
					ch = this.more();
				}

				if(ch != 62) {
					throw new XmlPullParserException("expected > to finish end tag not " + this.printable(ch) + " from line " + this.elRawNameLine[this.depth], this, (Throwable)null);
				} else {
					this.posEnd = this.pos;
					this.pastEndTag = true;
					return this.eventType = 3;
				}
			}
		}
	}

	public int parseStartTag() throws XmlPullParserException, IOException {
		++this.depth;
		this.posStart = this.pos - 2;
		this.emptyElementTag = false;
		this.attributeCount = 0;
		int nameStart = this.pos - 1 + this.bufAbsoluteStart;
		int colonPos = -1;
		char ch = this.buf[this.pos - 1];
		if(ch == 58 && this.processNamespaces) {
			throw new XmlPullParserException("when namespaces processing enabled colon can not be at element name start", this, (Throwable)null);
		} else {
			while(true) {
				ch = this.more();
				if(!this.isNameChar(ch)) {
					this.ensureElementsCapacity();
					int elLen = this.pos - 1 - (nameStart - this.bufAbsoluteStart);
					if(this.elRawName[this.depth] == null || this.elRawName[this.depth].length < elLen) {
						this.elRawName[this.depth] = new char[2 * elLen];
					}

					System.arraycopy(this.buf, nameStart - this.bufAbsoluteStart, this.elRawName[this.depth], 0, elLen);
					this.elRawNameEnd[this.depth] = elLen;
					this.elRawNameLine[this.depth] = this.lineNumber;
					String name = null;
					String prefix = null;
					if(this.processNamespaces) {
						if(colonPos != -1) {
							prefix = this.elPrefix[this.depth] = this.newString(this.buf, nameStart - this.bufAbsoluteStart, colonPos - nameStart);
							name = this.elName[this.depth] = this.newString(this.buf, colonPos + 1 - this.bufAbsoluteStart, this.pos - 2 - (colonPos - this.bufAbsoluteStart));
						} else {
							prefix = this.elPrefix[this.depth] = null;
							name = this.elName[this.depth] = this.newString(this.buf, nameStart - this.bufAbsoluteStart, elLen);
						}
					} else {
						name = this.elName[this.depth] = this.newString(this.buf, nameStart - this.bufAbsoluteStart, elLen);
					}

					while(true) {
						while(this.isS(ch)) {
							ch = this.more();
						}

						if(ch == 62) {
							break;
						}

						if(ch == 47) {
							if(this.emptyElementTag) {
								throw new XmlPullParserException("repeated / in tag declaration", this, (Throwable)null);
							}

							this.emptyElementTag = true;
							ch = this.more();
							if(ch != 62) {
								throw new XmlPullParserException("expected > to end empty tag not " + this.printable(ch), this, (Throwable)null);
							}
							break;
						}

						if(!this.isNameStartChar(ch)) {
							throw new XmlPullParserException("start tag unexpected character " + this.printable(ch), this, (Throwable)null);
						}

						ch = this.parseAttribute();
						ch = this.more();
					}

					int j;
					String attr1;
					String attr2;
					if(this.processNamespaces) {
						String string12 = this.getNamespace(prefix);
						if(string12 == null) {
							if(prefix != null) {
								throw new XmlPullParserException("could not determine namespace bound to element prefix " + prefix, this, (Throwable)null);
							}

							string12 = "";
						}

						this.elUri[this.depth] = string12;

						for(j = 0; j < this.attributeCount; ++j) {
							attr1 = this.attributePrefix[j];
							if(attr1 != null) {
								attr2 = this.getNamespace(attr1);
								if(attr2 == null) {
									throw new XmlPullParserException("could not determine namespace bound to attribute prefix " + attr1, this, (Throwable)null);
								}

								this.attributeUri[j] = attr2;
							} else {
								this.attributeUri[j] = "";
							}
						}

						for(j = 1; j < this.attributeCount; ++j) {
							for(int i13 = 0; i13 < j; ++i13) {
								if(this.attributeUri[i13] == this.attributeUri[j] && (this.allStringsInterned && this.attributeName[i13].equals(this.attributeName[j]) || !this.allStringsInterned && this.attributeNameHash[i13] == this.attributeNameHash[j] && this.attributeName[i13].equals(this.attributeName[j]))) {
									attr2 = this.attributeName[i13];
									if(this.attributeUri[i13] != null) {
										attr2 = this.attributeUri[i13] + ":" + attr2;
									}

									String attr21 = this.attributeName[j];
									if(this.attributeUri[j] != null) {
										attr21 = this.attributeUri[j] + ":" + attr21;
									}

									throw new XmlPullParserException("duplicated attributes " + attr2 + " and " + attr21, this, (Throwable)null);
								}
							}
						}
					} else {
						for(int i = 1; i < this.attributeCount; ++i) {
							for(j = 0; j < i; ++j) {
								if(this.allStringsInterned && this.attributeName[j].equals(this.attributeName[i]) || !this.allStringsInterned && this.attributeNameHash[j] == this.attributeNameHash[i] && this.attributeName[j].equals(this.attributeName[i])) {
									attr1 = this.attributeName[j];
									attr2 = this.attributeName[i];
									throw new XmlPullParserException("duplicated attributes " + attr1 + " and " + attr2, this, (Throwable)null);
								}
							}
						}
					}

					this.elNamespaceCount[this.depth] = this.namespaceEnd;
					this.posEnd = this.pos;
					return this.eventType = 2;
				}

				if(ch == 58 && this.processNamespaces) {
					if(colonPos != -1) {
						throw new XmlPullParserException("only one colon is allowed in name of element when namespaces are enabled", this, (Throwable)null);
					}

					colonPos = this.pos - 1 + this.bufAbsoluteStart;
				}
			}
		}
	}

	protected char parseAttribute() throws XmlPullParserException, IOException {
		int prevPosStart = this.posStart + this.bufAbsoluteStart;
		int nameStart = this.pos - 1 + this.bufAbsoluteStart;
		int colonPos = -1;
		char ch = this.buf[this.pos - 1];
		if(ch == 58 && this.processNamespaces) {
			throw new XmlPullParserException("when namespaces processing enabled colon can not be at attribute name start", this, (Throwable)null);
		} else {
			boolean startsWithXmlns = this.processNamespaces && ch == 120;
			int xmlnsPos = 0;

			for(ch = this.more(); this.isNameChar(ch); ch = this.more()) {
				if(this.processNamespaces) {
					if(startsWithXmlns && xmlnsPos < 5) {
						++xmlnsPos;
						if(xmlnsPos == 1) {
							if(ch != 109) {
								startsWithXmlns = false;
							}
						} else if(xmlnsPos == 2) {
							if(ch != 108) {
								startsWithXmlns = false;
							}
						} else if(xmlnsPos == 3) {
							if(ch != 110) {
								startsWithXmlns = false;
							}
						} else if(xmlnsPos == 4) {
							if(ch != 115) {
								startsWithXmlns = false;
							}
						} else if(xmlnsPos == 5 && ch != 58) {
							throw new XmlPullParserException("after xmlns in attribute name must be colonwhen namespaces are enabled", this, (Throwable)null);
						}
					}

					if(ch == 58) {
						if(colonPos != -1) {
							throw new XmlPullParserException("only one colon is allowed in attribute name when namespaces are enabled", this, (Throwable)null);
						}

						colonPos = this.pos - 1 + this.bufAbsoluteStart;
					}
				}
			}

			this.ensureAttributesCapacity(this.attributeCount);
			String name = null;
			String prefix = null;
			if(this.processNamespaces) {
				if(xmlnsPos < 4) {
					startsWithXmlns = false;
				}

				int delimit;
				if(startsWithXmlns) {
					if(colonPos != -1) {
						delimit = this.pos - 2 - (colonPos - this.bufAbsoluteStart);
						if(delimit == 0) {
							throw new XmlPullParserException("namespace prefix is required after xmlns:  when namespaces are enabled", this, (Throwable)null);
						}

						name = this.newString(this.buf, colonPos - this.bufAbsoluteStart + 1, delimit);
					}
				} else {
					if(colonPos != -1) {
						delimit = colonPos - nameStart;
						prefix = this.attributePrefix[this.attributeCount] = this.newString(this.buf, nameStart - this.bufAbsoluteStart, delimit);
						int normalizedCR = this.pos - 2 - (colonPos - this.bufAbsoluteStart);
						name = this.attributeName[this.attributeCount] = this.newString(this.buf, colonPos - this.bufAbsoluteStart + 1, normalizedCR);
					} else {
						prefix = this.attributePrefix[this.attributeCount] = null;
						name = this.attributeName[this.attributeCount] = this.newString(this.buf, nameStart - this.bufAbsoluteStart, this.pos - 1 - (nameStart - this.bufAbsoluteStart));
					}

					if(!this.allStringsInterned) {
						this.attributeNameHash[this.attributeCount] = name.hashCode();
					}
				}
			} else {
				name = this.attributeName[this.attributeCount] = this.newString(this.buf, nameStart - this.bufAbsoluteStart, this.pos - 1 - (nameStart - this.bufAbsoluteStart));
				if(!this.allStringsInterned) {
					this.attributeNameHash[this.attributeCount] = name.hashCode();
				}
			}

			while(this.isS(ch)) {
				ch = this.more();
			}

			if(ch != 61) {
				throw new XmlPullParserException("expected = after attribute name", this, (Throwable)null);
			} else {
				for(ch = this.more(); this.isS(ch); ch = this.more()) {
				}

				char c16 = ch;
				if(ch != 34 && ch != 39) {
					throw new XmlPullParserException("attribute value must start with quotation or apostrophe not " + this.printable(ch), this, (Throwable)null);
				} else {
					boolean z17 = false;
					this.usePC = false;
					this.pcStart = this.pcEnd;
					this.posStart = this.pos;

					while(true) {
						ch = this.more();
						int prefixHash;
						if(ch == c16) {
							if(this.processNamespaces && startsWithXmlns) {
								String string19 = null;
								if(!this.usePC) {
									string19 = this.newStringIntern(this.buf, this.posStart, this.pos - 1 - this.posStart);
								} else {
									string19 = this.newStringIntern(this.pc, this.pcStart, this.pcEnd - this.pcStart);
								}

								this.ensureNamespacesCapacity(this.namespaceEnd);
								prefixHash = -1;
								if(colonPos != -1) {
									if(string19.length() == 0) {
										throw new XmlPullParserException("non-default namespace can not be declared to be empty string", this, (Throwable)null);
									}

									this.namespacePrefix[this.namespaceEnd] = name;
									if(!this.allStringsInterned) {
										prefixHash = this.namespacePrefixHash[this.namespaceEnd] = name.hashCode();
									}
								} else {
									this.namespacePrefix[this.namespaceEnd] = null;
									if(!this.allStringsInterned) {
										prefixHash = this.namespacePrefixHash[this.namespaceEnd] = -1;
									}
								}

								this.namespaceUri[this.namespaceEnd] = string19;
								int startNs = this.elNamespaceCount[this.depth - 1];
								int i = this.namespaceEnd - 1;

								while(true) {
									if(i < startNs) {
										++this.namespaceEnd;
										break;
									}

									if((this.allStringsInterned || name == null) && this.namespacePrefix[i] == name || !this.allStringsInterned && name != null && this.namespacePrefixHash[i] == prefixHash && name.equals(this.namespacePrefix[i])) {
										String s = name == null ? "default" : "\'" + name + "\'";
										throw new XmlPullParserException("duplicated namespace declaration for " + s + " prefix", this, (Throwable)null);
									}

									--i;
								}
							} else {
								if(!this.usePC) {
									this.attributeValue[this.attributeCount] = new String(this.buf, this.posStart, this.pos - 1 - this.posStart);
								} else {
									this.attributeValue[this.attributeCount] = new String(this.pc, this.pcStart, this.pcEnd - this.pcStart);
								}

								++this.attributeCount;
							}

							this.posStart = prevPosStart - this.bufAbsoluteStart;
							return ch;
						}

						if(ch == 60) {
							throw new XmlPullParserException("markup not allowed inside attribute value - illegal < ", this, (Throwable)null);
						}

						if(ch == 38) {
							this.posEnd = this.pos - 1;
							if(!this.usePC) {
								boolean ns = this.posEnd > this.posStart;
								if(ns) {
									this.joinPC();
								} else {
									this.usePC = true;
									this.pcStart = this.pcEnd = 0;
								}
							}

							char[] c18 = this.parseEntityRef();
							if(c18 == null) {
								if(this.entityRefName == null) {
									this.entityRefName = this.newString(this.buf, this.posStart, this.posEnd - this.posStart);
								}

								throw new XmlPullParserException("could not resolve entity named \'" + this.printable(this.entityRefName) + "\'", this, (Throwable)null);
							}

							for(prefixHash = 0; prefixHash < c18.length; ++prefixHash) {
								if(this.pcEnd >= this.pc.length) {
									this.ensurePC(this.pcEnd);
								}

								this.pc[this.pcEnd++] = c18[prefixHash];
							}
						} else if(ch != 9 && ch != 10 && ch != 13) {
							if(this.usePC) {
								if(this.pcEnd >= this.pc.length) {
									this.ensurePC(this.pcEnd);
								}

								this.pc[this.pcEnd++] = ch;
							}
						} else {
							if(!this.usePC) {
								this.posEnd = this.pos - 1;
								if(this.posEnd > this.posStart) {
									this.joinPC();
								} else {
									this.usePC = true;
									this.pcEnd = this.pcStart = 0;
								}
							}

							if(this.pcEnd >= this.pc.length) {
								this.ensurePC(this.pcEnd);
							}

							if(ch != 10 || !z17) {
								this.pc[this.pcEnd++] = 32;
							}
						}

						z17 = ch == 13;
					}
				}
			}
		}
	}

	protected char[] parseEntityRef() throws XmlPullParserException, IOException {
		this.entityRefName = null;
		this.posStart = this.pos;
		char ch = this.more();
		if(ch == 35) {
			char len1 = 0;
			ch = this.more();
			if(ch == 120) {
				label139:
				while(true) {
					while(true) {
						while(true) {
							ch = this.more();
							if(ch < 48 || ch > 57) {
								if(ch < 97 || ch > 102) {
									if(ch < 65 || ch > 70) {
										if(ch != 59) {
											throw new XmlPullParserException("character reference (with hex value) may not contain " + this.printable(ch), this, (Throwable)null);
										}
										break label139;
									}

									len1 = (char)(len1 * 16 + (ch - 55));
								} else {
									len1 = (char)(len1 * 16 + (ch - 87));
								}
							} else {
								len1 = (char)(len1 * 16 + (ch - 48));
							}
						}
					}
				}
			} else {
				while(ch >= 48 && ch <= 57) {
					len1 = (char)(len1 * 10 + (ch - 48));
					ch = this.more();
				}

				if(ch != 59) {
					throw new XmlPullParserException("character reference (with decimal value) may not contain " + this.printable(ch), this, (Throwable)null);
				}
			}

			this.posEnd = this.pos - 1;
			this.charRefOneCharBuf[0] = len1;
			if(this.tokenize) {
				this.text = this.newString(this.charRefOneCharBuf, 0, 1);
			}

			return this.charRefOneCharBuf;
		} else if(!this.isNameStartChar(ch)) {
			throw new XmlPullParserException("entity reference names can not start with character \'" + this.printable(ch) + "\'", this, (Throwable)null);
		} else {
			do {
				ch = this.more();
				if(ch == 59) {
					this.posEnd = this.pos - 1;
					int len = this.posEnd - this.posStart;
					if(len == 2 && this.buf[this.posStart] == 108 && this.buf[this.posStart + 1] == 116) {
						if(this.tokenize) {
							this.text = "<";
						}

						this.charRefOneCharBuf[0] = 60;
						return this.charRefOneCharBuf;
					} else if(len == 3 && this.buf[this.posStart] == 97 && this.buf[this.posStart + 1] == 109 && this.buf[this.posStart + 2] == 112) {
						if(this.tokenize) {
							this.text = "&";
						}

						this.charRefOneCharBuf[0] = 38;
						return this.charRefOneCharBuf;
					} else if(len == 2 && this.buf[this.posStart] == 103 && this.buf[this.posStart + 1] == 116) {
						if(this.tokenize) {
							this.text = ">";
						}

						this.charRefOneCharBuf[0] = 62;
						return this.charRefOneCharBuf;
					} else if(len == 4 && this.buf[this.posStart] == 97 && this.buf[this.posStart + 1] == 112 && this.buf[this.posStart + 2] == 111 && this.buf[this.posStart + 3] == 115) {
						if(this.tokenize) {
							this.text = "\'";
						}

						this.charRefOneCharBuf[0] = 39;
						return this.charRefOneCharBuf;
					} else if(len == 4 && this.buf[this.posStart] == 113 && this.buf[this.posStart + 1] == 117 && this.buf[this.posStart + 2] == 111 && this.buf[this.posStart + 3] == 116) {
						if(this.tokenize) {
							this.text = "\"";
						}

						this.charRefOneCharBuf[0] = 34;
						return this.charRefOneCharBuf;
					} else {
						char[] result = this.lookuEntityReplacement(len);
						if(result != null) {
							return result;
						} else {
							if(this.tokenize) {
								this.text = null;
							}

							return null;
						}
					}
				}
			} while(this.isNameChar(ch));

			throw new XmlPullParserException("entity reference name can not contain character " + this.printable(ch) + "\'", this, (Throwable)null);
		}
	}

	protected char[] lookuEntityReplacement(int entitNameLen) throws XmlPullParserException, IOException {
		int i;
		if(!this.allStringsInterned) {
			i = fastHash(this.buf, this.posStart, this.posEnd - this.posStart);

			label53:
			for(int i1 = this.entityEnd - 1; i1 >= 0; --i1) {
				if(i == this.entityNameHash[i1] && entitNameLen == this.entityNameBuf[i1].length) {
					char[] entityBuf = this.entityNameBuf[i1];

					for(int j = 0; j < entitNameLen; ++j) {
						if(this.buf[this.posStart + j] != entityBuf[j]) {
							continue label53;
						}
					}

					if(this.tokenize) {
						this.text = this.entityReplacement[i1];
					}

					return this.entityReplacementBuf[i1];
				}
			}
		} else {
			this.entityRefName = this.newString(this.buf, this.posStart, this.posEnd - this.posStart);

			for(i = this.entityEnd - 1; i >= 0; --i) {
				if(this.entityRefName == this.entityName[i]) {
					if(this.tokenize) {
						this.text = this.entityReplacement[i];
					}

					return this.entityReplacementBuf[i];
				}
			}
		}

		return null;
	}

	protected void parseComment() throws XmlPullParserException, IOException {
		char ch = this.more();
		if(ch != 45) {
			throw new XmlPullParserException("expected <!-- for comment start", this, (Throwable)null);
		} else {
			if(this.tokenize) {
				this.posStart = this.pos;
			}

			int curLine = this.lineNumber;
			int curColumn = this.columnNumber;

			try {
				boolean ex = this.tokenize && !this.roundtripSupported;
				boolean normalizedCR = false;
				boolean seenDash = false;
				boolean seenDashDash = false;

				while(true) {
					ch = this.more();
					if(seenDashDash && ch != 62) {
						throw new XmlPullParserException("in comment after two dashes (--) next character must be > not " + this.printable(ch), this, (Throwable)null);
					}

					if(ch == 45) {
						if(!seenDash) {
							seenDash = true;
						} else {
							seenDashDash = true;
							seenDash = false;
						}
					} else if(ch == 62) {
						if(seenDashDash) {
							break;
						}

						seenDashDash = false;
						seenDash = false;
					} else {
						seenDash = false;
					}

					if(ex) {
						if(ch == 13) {
							normalizedCR = true;
							if(!this.usePC) {
								this.posEnd = this.pos - 1;
								if(this.posEnd > this.posStart) {
									this.joinPC();
								} else {
									this.usePC = true;
									this.pcStart = this.pcEnd = 0;
								}
							}

							if(this.pcEnd >= this.pc.length) {
								this.ensurePC(this.pcEnd);
							}

							this.pc[this.pcEnd++] = 10;
						} else if(ch == 10) {
							if(!normalizedCR && this.usePC) {
								if(this.pcEnd >= this.pc.length) {
									this.ensurePC(this.pcEnd);
								}

								this.pc[this.pcEnd++] = 10;
							}

							normalizedCR = false;
						} else {
							if(this.usePC) {
								if(this.pcEnd >= this.pc.length) {
									this.ensurePC(this.pcEnd);
								}

								this.pc[this.pcEnd++] = ch;
							}

							normalizedCR = false;
						}
					}
				}
			} catch (EOFException eOFException8) {
				throw new XmlPullParserException("comment started on line " + curLine + " and column " + curColumn + " was not closed", this, eOFException8);
			}

			if(this.tokenize) {
				this.posEnd = this.pos - 3;
				if(this.usePC) {
					this.pcEnd -= 2;
				}
			}

		}
	}

	protected boolean parsePI() throws XmlPullParserException, IOException {
		if(this.tokenize) {
			this.posStart = this.pos;
		}

		int curLine = this.lineNumber;
		int curColumn = this.columnNumber;
		int piTargetStart = this.pos + this.bufAbsoluteStart;
		int piTargetEnd = -1;
		boolean normalizeIgnorableWS = this.tokenize && !this.roundtripSupported;
		boolean normalizedCR = false;

		try {
			boolean ex = false;
			char ch = this.more();
			if(this.isS(ch)) {
				throw new XmlPullParserException("processing instruction PITarget must be exactly after <? and not white space character", this, (Throwable)null);
			}

			while(true) {
				if(ch == 63) {
					ex = true;
				} else if(ch == 62) {
					if(ex) {
						break;
					}

					ex = false;
				} else {
					if(piTargetEnd == -1 && this.isS(ch)) {
						piTargetEnd = this.pos - 1 + this.bufAbsoluteStart;
						if(piTargetEnd - piTargetStart == 3 && (this.buf[piTargetStart] == 120 || this.buf[piTargetStart] == 88) && (this.buf[piTargetStart + 1] == 109 || this.buf[piTargetStart + 1] == 77) && (this.buf[piTargetStart + 2] == 108 || this.buf[piTargetStart + 2] == 76)) {
							if(piTargetStart > 3) {
								throw new XmlPullParserException("processing instruction can not have PITarget with reserveld xml name", this, (Throwable)null);
							}

							if(this.buf[piTargetStart] != 120 && this.buf[piTargetStart + 1] != 109 && this.buf[piTargetStart + 2] != 108) {
								throw new XmlPullParserException("XMLDecl must have xml name in lowercase", this, (Throwable)null);
							}

							this.parseXmlDecl(ch);
							if(this.tokenize) {
								this.posEnd = this.pos - 2;
							}

							int off = piTargetStart - this.bufAbsoluteStart + 3;
							int len = this.pos - 2 - off;
							this.xmlDeclContent = this.newString(this.buf, off, len);
							return false;
						}
					}

					ex = false;
				}

				if(normalizeIgnorableWS) {
					if(ch == 13) {
						normalizedCR = true;
						if(!this.usePC) {
							this.posEnd = this.pos - 1;
							if(this.posEnd > this.posStart) {
								this.joinPC();
							} else {
								this.usePC = true;
								this.pcStart = this.pcEnd = 0;
							}
						}

						if(this.pcEnd >= this.pc.length) {
							this.ensurePC(this.pcEnd);
						}

						this.pc[this.pcEnd++] = 10;
					} else if(ch == 10) {
						if(!normalizedCR && this.usePC) {
							if(this.pcEnd >= this.pc.length) {
								this.ensurePC(this.pcEnd);
							}

							this.pc[this.pcEnd++] = 10;
						}

						normalizedCR = false;
					} else {
						if(this.usePC) {
							if(this.pcEnd >= this.pc.length) {
								this.ensurePC(this.pcEnd);
							}

							this.pc[this.pcEnd++] = ch;
						}

						normalizedCR = false;
					}
				}

				ch = this.more();
			}
		} catch (EOFException eOFException11) {
			throw new XmlPullParserException("processing instruction started on line " + curLine + " and column " + curColumn + " was not closed", this, eOFException11);
		}

		if(piTargetEnd == -1) {
			piTargetEnd = this.pos - 2 + this.bufAbsoluteStart;
		}

		int i10000 = piTargetStart - this.bufAbsoluteStart;
		i10000 = piTargetEnd - this.bufAbsoluteStart;
		if(this.tokenize) {
			this.posEnd = this.pos - 2;
			if(normalizeIgnorableWS) {
				--this.pcEnd;
			}
		}

		return true;
	}

	protected void parseXmlDecl(char ch) throws XmlPullParserException, IOException {
		this.preventBufferCompaction = true;
		this.bufStart = 0;
		ch = this.skipS(ch);
		ch = this.requireInput(ch, VERSION);
		ch = this.skipS(ch);
		if(ch != 61) {
			throw new XmlPullParserException("expected equals sign (=) after version and not " + this.printable(ch), this, (Throwable)null);
		} else {
			ch = this.more();
			ch = this.skipS(ch);
			if(ch != 39 && ch != 34) {
				throw new XmlPullParserException("expected apostrophe (\') or quotation mark (\") after version and not " + this.printable(ch), this, (Throwable)null);
			} else {
				char quotChar = ch;
				int versionStart = this.pos;

				for(ch = this.more(); ch != quotChar; ch = this.more()) {
					if((ch < 97 || ch > 122) && (ch < 65 || ch > 90) && (ch < 48 || ch > 57) && ch != 95 && ch != 46 && ch != 58 && ch != 45) {
						throw new XmlPullParserException("<?xml version value expected to be in ([a-zA-Z0-9_.:] | \'-\') not " + this.printable(ch), this, (Throwable)null);
					}
				}

				int versionEnd = this.pos - 1;
				this.parseXmlDeclWithVersion(versionStart, versionEnd);
				this.preventBufferCompaction = false;
			}
		}
	}

	protected void parseXmlDeclWithVersion(int versionStart, int versionEnd) throws XmlPullParserException, IOException {
		String oldEncoding = this.inputEncoding;
		if(versionEnd - versionStart == 3 && this.buf[versionStart] == 49 && this.buf[versionStart + 1] == 46 && this.buf[versionStart + 2] == 48) {
			this.xmlDeclVersion = this.newString(this.buf, versionStart, versionEnd - versionStart);
			char ch = this.more();
			ch = this.skipS(ch);
			char quotChar;
			int standaloneStart;
			if(ch == 101) {
				ch = this.more();
				ch = this.requireInput(ch, NCODING);
				ch = this.skipS(ch);
				if(ch != 61) {
					throw new XmlPullParserException("expected equals sign (=) after encoding and not " + this.printable(ch), this, (Throwable)null);
				}

				ch = this.more();
				ch = this.skipS(ch);
				if(ch != 39 && ch != 34) {
					throw new XmlPullParserException("expected apostrophe (\') or quotation mark (\") after encoding and not " + this.printable(ch), this, (Throwable)null);
				}

				quotChar = ch;
				standaloneStart = this.pos;
				ch = this.more();
				if((ch < 97 || ch > 122) && (ch < 65 || ch > 90)) {
					throw new XmlPullParserException("<?xml encoding name expected to start with [A-Za-z] not " + this.printable(ch), this, (Throwable)null);
				}

				for(ch = this.more(); ch != quotChar; ch = this.more()) {
					if((ch < 97 || ch > 122) && (ch < 65 || ch > 90) && (ch < 48 || ch > 57) && ch != 46 && ch != 95 && ch != 45) {
						throw new XmlPullParserException("<?xml encoding value expected to be in ([A-Za-z0-9._] | \'-\') not " + this.printable(ch), this, (Throwable)null);
					}
				}

				int encodingEnd = this.pos - 1;
				this.inputEncoding = this.newString(this.buf, standaloneStart, encodingEnd - standaloneStart);
				ch = this.more();
			}

			ch = this.skipS(ch);
			if(ch == 115) {
				ch = this.more();
				ch = this.requireInput(ch, TANDALONE);
				ch = this.skipS(ch);
				if(ch != 61) {
					throw new XmlPullParserException("expected equals sign (=) after standalone and not " + this.printable(ch), this, (Throwable)null);
				}

				ch = this.more();
				ch = this.skipS(ch);
				if(ch != 39 && ch != 34) {
					throw new XmlPullParserException("expected apostrophe (\') or quotation mark (\") after encoding and not " + this.printable(ch), this, (Throwable)null);
				}

				quotChar = ch;
				standaloneStart = this.pos;
				ch = this.more();
				if(ch == 121) {
					ch = this.requireInput(ch, YES);
					this.xmlDeclStandalone = new Boolean(true);
				} else {
					if(ch != 110) {
						throw new XmlPullParserException("expected \'yes\' or \'no\' after standalone and not " + this.printable(ch), this, (Throwable)null);
					}

					ch = this.requireInput(ch, NO);
					this.xmlDeclStandalone = new Boolean(false);
				}

				if(ch != quotChar) {
					throw new XmlPullParserException("expected " + quotChar + " after standalone value not " + this.printable(ch), this, (Throwable)null);
				}

				ch = this.more();
			}

			ch = this.skipS(ch);
			if(ch != 63) {
				throw new XmlPullParserException("expected ?> as last part of <?xml not " + this.printable(ch), this, (Throwable)null);
			} else {
				ch = this.more();
				if(ch != 62) {
					throw new XmlPullParserException("expected ?> as last part of <?xml not " + this.printable(ch), this, (Throwable)null);
				}
			}
		} else {
			throw new XmlPullParserException("only 1.0 is supported as <?xml version not \'" + this.printable(new String(this.buf, versionStart, versionEnd - versionStart)) + "\'", this, (Throwable)null);
		}
	}

	protected void parseDocdecl() throws XmlPullParserException, IOException {
		char ch = this.more();
		if(ch != 79) {
			throw new XmlPullParserException("expected <!DOCTYPE", this, (Throwable)null);
		} else {
			ch = this.more();
			if(ch != 67) {
				throw new XmlPullParserException("expected <!DOCTYPE", this, (Throwable)null);
			} else {
				ch = this.more();
				if(ch != 84) {
					throw new XmlPullParserException("expected <!DOCTYPE", this, (Throwable)null);
				} else {
					ch = this.more();
					if(ch != 89) {
						throw new XmlPullParserException("expected <!DOCTYPE", this, (Throwable)null);
					} else {
						ch = this.more();
						if(ch != 80) {
							throw new XmlPullParserException("expected <!DOCTYPE", this, (Throwable)null);
						} else {
							ch = this.more();
							if(ch != 69) {
								throw new XmlPullParserException("expected <!DOCTYPE", this, (Throwable)null);
							} else {
								this.posStart = this.pos;
								int bracketLevel = 0;
								boolean normalizeIgnorableWS = this.tokenize && !this.roundtripSupported;
								boolean normalizedCR = false;

								while(true) {
									ch = this.more();
									if(ch == 91) {
										++bracketLevel;
									}

									if(ch == 93) {
										--bracketLevel;
									}

									if(ch == 62 && bracketLevel == 0) {
										this.posEnd = this.pos - 1;
										return;
									}

									if(normalizeIgnorableWS) {
										if(ch == 13) {
											normalizedCR = true;
											if(!this.usePC) {
												this.posEnd = this.pos - 1;
												if(this.posEnd > this.posStart) {
													this.joinPC();
												} else {
													this.usePC = true;
													this.pcStart = this.pcEnd = 0;
												}
											}

											if(this.pcEnd >= this.pc.length) {
												this.ensurePC(this.pcEnd);
											}

											this.pc[this.pcEnd++] = 10;
										} else if(ch == 10) {
											if(!normalizedCR && this.usePC) {
												if(this.pcEnd >= this.pc.length) {
													this.ensurePC(this.pcEnd);
												}

												this.pc[this.pcEnd++] = 10;
											}

											normalizedCR = false;
										} else {
											if(this.usePC) {
												if(this.pcEnd >= this.pc.length) {
													this.ensurePC(this.pcEnd);
												}

												this.pc[this.pcEnd++] = ch;
											}

											normalizedCR = false;
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}

	protected void parseCDSect(boolean hadCharData) throws XmlPullParserException, IOException {
		char ch = this.more();
		if(ch != 67) {
			throw new XmlPullParserException("expected <[CDATA[ for comment start", this, (Throwable)null);
		} else {
			ch = this.more();
			if(ch != 68) {
				throw new XmlPullParserException("expected <[CDATA[ for comment start", this, (Throwable)null);
			} else {
				ch = this.more();
				if(ch != 65) {
					throw new XmlPullParserException("expected <[CDATA[ for comment start", this, (Throwable)null);
				} else {
					ch = this.more();
					if(ch != 84) {
						throw new XmlPullParserException("expected <[CDATA[ for comment start", this, (Throwable)null);
					} else {
						ch = this.more();
						if(ch != 65) {
							throw new XmlPullParserException("expected <[CDATA[ for comment start", this, (Throwable)null);
						} else {
							ch = this.more();
							if(ch != 91) {
								throw new XmlPullParserException("expected <![CDATA[ for comment start", this, (Throwable)null);
							} else {
								int cdStart = this.pos + this.bufAbsoluteStart;
								int curLine = this.lineNumber;
								int curColumn = this.columnNumber;
								boolean normalizeInput = !this.tokenize || !this.roundtripSupported;

								try {
									if(normalizeInput && hadCharData && !this.usePC) {
										if(this.posEnd > this.posStart) {
											this.joinPC();
										} else {
											this.usePC = true;
											this.pcStart = this.pcEnd = 0;
										}
									}

									boolean ex = false;
									boolean seenBracketBracket = false;
									boolean normalizedCR = false;

									while(true) {
										ch = this.more();
										if(ch == 93) {
											if(!ex) {
												ex = true;
											} else {
												seenBracketBracket = true;
											}
										} else if(ch == 62) {
											if(ex && seenBracketBracket) {
												break;
											}

											seenBracketBracket = false;
											ex = false;
										} else if(ex) {
											ex = false;
										}

										if(normalizeInput) {
											if(ch == 13) {
												normalizedCR = true;
												this.posStart = cdStart - this.bufAbsoluteStart;
												this.posEnd = this.pos - 1;
												if(!this.usePC) {
													if(this.posEnd > this.posStart) {
														this.joinPC();
													} else {
														this.usePC = true;
														this.pcStart = this.pcEnd = 0;
													}
												}

												if(this.pcEnd >= this.pc.length) {
													this.ensurePC(this.pcEnd);
												}

												this.pc[this.pcEnd++] = 10;
											} else if(ch == 10) {
												if(!normalizedCR && this.usePC) {
													if(this.pcEnd >= this.pc.length) {
														this.ensurePC(this.pcEnd);
													}

													this.pc[this.pcEnd++] = 10;
												}

												normalizedCR = false;
											} else {
												if(this.usePC) {
													if(this.pcEnd >= this.pc.length) {
														this.ensurePC(this.pcEnd);
													}

													this.pc[this.pcEnd++] = ch;
												}

												normalizedCR = false;
											}
										}
									}
								} catch (EOFException eOFException10) {
									throw new XmlPullParserException("CDATA section started on line " + curLine + " and column " + curColumn + " was not closed", this, eOFException10);
								}

								if(normalizeInput && this.usePC) {
									this.pcEnd -= 2;
								}

								this.posStart = cdStart - this.bufAbsoluteStart;
								this.posEnd = this.pos - 3;
							}
						}
					}
				}
			}
		}
	}

	protected void fillBuf() throws IOException, XmlPullParserException {
		if(this.reader == null) {
			throw new XmlPullParserException("reader must be set before parsing is started");
		} else {
			if(this.bufEnd > this.bufSoftLimit) {
				boolean len = this.bufStart > this.bufSoftLimit;
				boolean ret = false;
				if(this.preventBufferCompaction) {
					len = false;
					ret = true;
				} else if(!len) {
					if(this.bufStart < this.buf.length / 2) {
						ret = true;
					} else {
						len = true;
					}
				}

				if(len) {
					System.arraycopy(this.buf, this.bufStart, this.buf, 0, this.bufEnd - this.bufStart);
				} else {
					if(!ret) {
						throw new XmlPullParserException("internal error in fillBuffer()");
					}

					int expectedTagStack = 2 * this.buf.length;
					char[] i = new char[expectedTagStack];
					System.arraycopy(this.buf, this.bufStart, i, 0, this.bufEnd - this.bufStart);
					this.buf = i;
					if(this.bufLoadFactor > 0) {
						this.bufSoftLimit = (int)((long)this.bufLoadFactor * (long)this.buf.length / 100L);
					}
				}

				this.bufEnd -= this.bufStart;
				this.pos -= this.bufStart;
				this.posStart -= this.bufStart;
				this.posEnd -= this.bufStart;
				this.bufAbsoluteStart += this.bufStart;
				this.bufStart = 0;
			}

			int i6 = this.buf.length - this.bufEnd > 8192 ? 8192 : this.buf.length - this.bufEnd;
			int i7 = this.reader.read(this.buf, this.bufEnd, i6);
			if(i7 > 0) {
				this.bufEnd += i7;
			} else if(i7 != -1) {
				throw new IOException("error reading input, returned " + i7);
			} else if(this.bufAbsoluteStart == 0 && this.pos == 0) {
				throw new EOFException("input contained no data");
			} else if(this.seenRoot && this.depth == 0) {
				this.reachedEnd = true;
			} else {
				StringBuffer stringBuffer8 = new StringBuffer();
				if(this.depth > 0) {
					stringBuffer8.append(" - expected end tag");
					if(this.depth > 1) {
						stringBuffer8.append("s");
					}

					stringBuffer8.append(" ");

					String tagName;
					int i9;
					for(i9 = this.depth; i9 > 0; --i9) {
						tagName = new String(this.elRawName[i9], 0, this.elRawNameEnd[i9]);
						stringBuffer8.append("</").append(tagName).append('>');
					}

					stringBuffer8.append(" to close");

					for(i9 = this.depth; i9 > 0; --i9) {
						if(i9 != this.depth) {
							stringBuffer8.append(" and");
						}

						tagName = new String(this.elRawName[i9], 0, this.elRawNameEnd[i9]);
						stringBuffer8.append(" start tag <" + tagName + ">");
						stringBuffer8.append(" from line " + this.elRawNameLine[i9]);
					}

					stringBuffer8.append(", parser stopped on");
				}

				throw new EOFException("no more data available" + stringBuffer8.toString() + this.getPositionDescription());
			}
		}
	}

	protected char more() throws IOException, XmlPullParserException {
		if(this.pos >= this.bufEnd) {
			this.fillBuf();
			if(this.reachedEnd) {
				return (char)65535;
			}
		}

		char ch = this.buf[this.pos++];
		if(ch == 10) {
			++this.lineNumber;
			this.columnNumber = 1;
		} else {
			++this.columnNumber;
		}

		return ch;
	}

	protected void ensurePC(int end) {
		int newSize = end > 8192 ? 2 * end : 16384;
		char[] newPC = new char[newSize];
		System.arraycopy(this.pc, 0, newPC, 0, this.pcEnd);
		this.pc = newPC;
	}

	protected void joinPC() {
		int len = this.posEnd - this.posStart;
		int newEnd = this.pcEnd + len + 1;
		if(newEnd >= this.pc.length) {
			this.ensurePC(newEnd);
		}

		System.arraycopy(this.buf, this.posStart, this.pc, this.pcEnd, len);
		this.pcEnd += len;
		this.usePC = true;
	}

	protected char requireInput(char ch, char[] input) throws XmlPullParserException, IOException {
		for(int i = 0; i < input.length; ++i) {
			if(ch != input[i]) {
				throw new XmlPullParserException("expected " + this.printable(input[i]) + " in " + new String(input) + " and not " + this.printable(ch), this, (Throwable)null);
			}

			ch = this.more();
		}

		return ch;
	}

	protected char requireNextS() throws XmlPullParserException, IOException {
		char ch = this.more();
		if(!this.isS(ch)) {
			throw new XmlPullParserException("white space is required and not " + this.printable(ch), this, (Throwable)null);
		} else {
			return this.skipS(ch);
		}
	}

	protected char skipS(char ch) throws XmlPullParserException, IOException {
		while(this.isS(ch)) {
			ch = this.more();
		}

		return ch;
	}

	private static final void setName(char ch) {
		lookupNameChar[ch] = true;
	}

	private static final void setNameStart(char ch) {
		lookupNameStartChar[ch] = true;
		setName(ch);
	}

	protected boolean isNameStartChar(char ch) {
		return ch < 1024 && lookupNameStartChar[ch] || ch >= 1024 && ch <= 8231 || ch >= 8234 && ch <= 8591 || ch >= 10240 && ch <= 65519;
	}

	protected boolean isNameChar(char ch) {
		return ch < 1024 && lookupNameChar[ch] || ch >= 1024 && ch <= 8231 || ch >= 8234 && ch <= 8591 || ch >= 10240 && ch <= 65519;
	}

	protected boolean isS(char ch) {
		return ch == 32 || ch == 10 || ch == 13 || ch == 9;
	}

	protected String printable(char ch) {
		return ch == 10 ? "\\n" : (ch == 13 ? "\\r" : (ch == 9 ? "\\t" : (ch == 39 ? "\\\'" : (ch <= 127 && ch >= 32 ? "" + ch : "\\u" + Integer.toHexString(ch)))));
	}

	protected String printable(String s) {
		if(s == null) {
			return null;
		} else {
			int sLen = s.length();
			StringBuffer buf = new StringBuffer(sLen + 10);

			for(int i = 0; i < sLen; ++i) {
				buf.append(this.printable(s.charAt(i)));
			}

			s = buf.toString();
			return s;
		}
	}

	static {
		setNameStart(':');

		char ch;
		for(ch = 65; ch <= 90; ++ch) {
			setNameStart(ch);
		}

		setNameStart('_');

		for(ch = 97; ch <= 122; ++ch) {
			setNameStart(ch);
		}

		for(ch = 192; ch <= 767; ++ch) {
			setNameStart(ch);
		}

		for(ch = 880; ch <= 893; ++ch) {
			setNameStart(ch);
		}

		for(ch = 895; ch < 1024; ++ch) {
			setNameStart(ch);
		}

		setName('-');
		setName('.');

		for(ch = 48; ch <= 57; ++ch) {
			setName(ch);
		}

		setName('\u00b7');

		for(ch = 768; ch <= 879; ++ch) {
			setName(ch);
		}

	}
}
