package org.xmlpull.mxp1;

import java.io.Reader;

import org.xmlpull.v1.XmlPullParserException;

public class MXParserCachingStrings extends MXParser implements Cloneable {
	protected static final boolean CACHE_STATISTICS = false;
	protected static final boolean TRACE_SIZING = false;
	protected static final int INITIAL_CAPACITY = 13;
	protected int cacheStatCalls;
	protected int cacheStatWalks;
	protected int cacheStatResets;
	protected int cacheStatRehash;
	protected static final int CACHE_LOAD = 77;
	protected int cacheEntriesCount;
	protected int cacheEntriesThreshold;
	protected char[][] keys;
	protected String[] values;

	public Object clone() throws CloneNotSupportedException {
		if(super.reader != null && !(super.reader instanceof Cloneable)) {
			throw new CloneNotSupportedException("reader used in parser must implement Cloneable!");
		} else {
			MXParserCachingStrings cloned = (MXParserCachingStrings)super.clone();
			if(super.reader != null) {
				try {
					Object e = super.reader.getClass().getMethod("clone", (Class[])null).invoke(super.reader, (Object[])null);
					cloned.reader = (Reader)e;
				} catch (Exception exception4) {
					CloneNotSupportedException ee = new CloneNotSupportedException("failed to call clone() on reader " + super.reader + ":" + exception4);
					ee.initCause(exception4);
					throw ee;
				}
			}

			if(this.keys != null) {
				cloned.keys = (char[][])((char[][])this.keys.clone());
			}

			if(this.values != null) {
				cloned.values = (String[])((String[])this.values.clone());
			}

			if(super.elRawName != null) {
				cloned.elRawName = this.cloneCCArr(super.elRawName);
			}

			if(super.elRawNameEnd != null) {
				cloned.elRawNameEnd = (int[])((int[])super.elRawNameEnd.clone());
			}

			if(super.elRawNameLine != null) {
				cloned.elRawNameLine = (int[])((int[])super.elRawNameLine.clone());
			}

			if(super.elName != null) {
				cloned.elName = (String[])((String[])super.elName.clone());
			}

			if(super.elPrefix != null) {
				cloned.elPrefix = (String[])((String[])super.elPrefix.clone());
			}

			if(super.elUri != null) {
				cloned.elUri = (String[])((String[])super.elUri.clone());
			}

			if(super.elNamespaceCount != null) {
				cloned.elNamespaceCount = (int[])((int[])super.elNamespaceCount.clone());
			}

			if(super.attributeName != null) {
				cloned.attributeName = (String[])((String[])super.attributeName.clone());
			}

			if(super.attributeNameHash != null) {
				cloned.attributeNameHash = (int[])((int[])super.attributeNameHash.clone());
			}

			if(super.attributePrefix != null) {
				cloned.attributePrefix = (String[])((String[])super.attributePrefix.clone());
			}

			if(super.attributeUri != null) {
				cloned.attributeUri = (String[])((String[])super.attributeUri.clone());
			}

			if(super.attributeValue != null) {
				cloned.attributeValue = (String[])((String[])super.attributeValue.clone());
			}

			if(super.namespacePrefix != null) {
				cloned.namespacePrefix = (String[])((String[])super.namespacePrefix.clone());
			}

			if(super.namespacePrefixHash != null) {
				cloned.namespacePrefixHash = (int[])((int[])super.namespacePrefixHash.clone());
			}

			if(super.namespaceUri != null) {
				cloned.namespaceUri = (String[])((String[])super.namespaceUri.clone());
			}

			if(super.entityName != null) {
				cloned.entityName = (String[])((String[])super.entityName.clone());
			}

			if(super.entityNameBuf != null) {
				cloned.entityNameBuf = this.cloneCCArr(super.entityNameBuf);
			}

			if(super.entityNameHash != null) {
				cloned.entityNameHash = (int[])((int[])super.entityNameHash.clone());
			}

			if(super.entityReplacementBuf != null) {
				cloned.entityReplacementBuf = this.cloneCCArr(super.entityReplacementBuf);
			}

			if(super.entityReplacement != null) {
				cloned.entityReplacement = (String[])((String[])super.entityReplacement.clone());
			}

			if(super.buf != null) {
				cloned.buf = (char[])((char[])super.buf.clone());
			}

			if(super.pc != null) {
				cloned.pc = (char[])((char[])super.pc.clone());
			}

			if(super.charRefOneCharBuf != null) {
				cloned.charRefOneCharBuf = (char[])((char[])super.charRefOneCharBuf.clone());
			}

			return cloned;
		}
	}

	private char[][] cloneCCArr(char[][] ccarr) {
		char[][] cca = (char[][])((char[][])ccarr.clone());

		for(int i = 0; i < cca.length; ++i) {
			if(cca[i] != null) {
				cca[i] = (char[])((char[])cca[i].clone());
			}
		}

		return cca;
	}

	public MXParserCachingStrings() {
		super.allStringsInterned = true;
		this.initStringCache();
	}

	public void setFeature(String name, boolean state) throws XmlPullParserException {
		if("http://xmlpull.org/v1/doc/features.html#names-interned".equals(name)) {
			if(super.eventType != 0) {
				throw new XmlPullParserException("interning names feature can only be changed before parsing", this, (Throwable)null);
			}

			super.allStringsInterned = state;
			if(!state && this.keys != null) {
				this.resetStringCache();
			}
		} else {
			super.setFeature(name, state);
		}

	}

	public boolean getFeature(String name) {
		return "http://xmlpull.org/v1/doc/features.html#names-interned".equals(name) ? super.allStringsInterned : super.getFeature(name);
	}

	public void finalize() {
	}

	protected String newString(char[] cbuf, int off, int len) {
		return super.allStringsInterned ? this.newStringIntern(cbuf, off, len) : super.newString(cbuf, off, len);
	}

	protected String newStringIntern(char[] cbuf, int off, int len) {
		if(this.cacheEntriesCount >= this.cacheEntriesThreshold) {
			this.rehash();
		}

		int offset = MXParser.fastHash(cbuf, off, len) % this.keys.length;

		char[] k1;
		for(Object k = null; (k1 = this.keys[offset]) != null && !keysAreEqual(k1, 0, k1.length, cbuf, off, len); offset = (offset + 1) % this.keys.length) {
		}

		if(k1 != null) {
			return this.values[offset];
		} else {
			k1 = new char[len];
			System.arraycopy(cbuf, off, k1, 0, len);
			String v = (new String(k1)).intern();
			this.keys[offset] = k1;
			this.values[offset] = v;
			++this.cacheEntriesCount;
			return v;
		}
	}

	protected void initStringCache() {
		if(this.keys == null) {
			this.cacheEntriesThreshold = 10;
			if(this.cacheEntriesThreshold >= 13) {
				throw new RuntimeException("internal error: threshold must be less than capacity: 13");
			}

			this.keys = new char[13][];
			this.values = new String[13];
			this.cacheEntriesCount = 0;
		}

	}

	protected void resetStringCache() {
		this.initStringCache();
	}

	private void rehash() {
		int newSize = 2 * this.keys.length + 1;
		this.cacheEntriesThreshold = newSize * 77 / 100;
		if(this.cacheEntriesThreshold >= newSize) {
			throw new RuntimeException("internal error: threshold must be less than capacity: " + newSize);
		} else {
			char[][] newKeys = new char[newSize][];
			String[] newValues = new String[newSize];

			for(int i = 0; i < this.keys.length; ++i) {
				char[] k = this.keys[i];
				this.keys[i] = null;
				String v = this.values[i];
				this.values[i] = null;
				if(k != null) {
					int newOffset = MXParser.fastHash(k, 0, k.length) % newSize;

					char[] c9;
					for(Object newk = null; (c9 = newKeys[newOffset]) != null; newOffset = (newOffset + 1) % newSize) {
						if(keysAreEqual(c9, 0, c9.length, k, 0, k.length)) {
							throw new RuntimeException("internal cache error: duplicated keys: " + new String(c9) + " and " + new String(k));
						}
					}

					newKeys[newOffset] = k;
					newValues[newOffset] = v;
				}
			}

			this.keys = newKeys;
			this.values = newValues;
		}
	}

	private static final boolean keysAreEqual(char[] a, int astart, int alength, char[] b, int bstart, int blength) {
		if(alength != blength) {
			return false;
		} else {
			for(int i = 0; i < alength; ++i) {
				if(a[astart + i] != b[bstart + i]) {
					return false;
				}
			}

			return true;
		}
	}
}
