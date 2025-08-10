package de.matthiasmann.twl.textarea;

import java.io.IOException;
import java.io.Reader;

class Parser {
	public static final int YYEOF = -1;
	private static final int ZZ_BUFFERSIZE = 16384;
	public static final int YYSTRING1 = 6;
	public static final int YYINITIAL = 0;
	public static final int YYSTYLE = 2;
	public static final int YYVALUE = 4;
	public static final int YYSTRING2 = 8;
	private static final String ZZ_CMAP_PACKED = "\t\u0000\u0001\u0003\u0001\u0002\u0001\u0000\u0001\u0003\u0001\u0001\u0012\u0000\u0001\u0003\u0001\u0000\u0001\u0012\u0001\f\u0003\u0000\u0001\u0011\u0002\u0000\u0001\u0005\u0001\u0000\u0001\n\u0001\u0006\u0001\t\u0001\u0004\n\b\u0001\u000f\u0001\u0010\u0002\u0000\u0001\u000b\u0002\u0000\u001a\u0007\u0004\u0000\u0001\u0007\u0001\u0000\u001a\u0007\u0001\r\u0001\u0000\u0001\u000e\uff82\u0000";
	private static final char[] ZZ_CMAP = zzUnpackCMap("\t\u0000\u0001\u0003\u0001\u0002\u0001\u0000\u0001\u0003\u0001\u0001\u0012\u0000\u0001\u0003\u0001\u0000\u0001\u0012\u0001\f\u0003\u0000\u0001\u0011\u0002\u0000\u0001\u0005\u0001\u0000\u0001\n\u0001\u0006\u0001\t\u0001\u0004\n\b\u0001\u000f\u0001\u0010\u0002\u0000\u0001\u000b\u0002\u0000\u001a\u0007\u0004\u0000\u0001\u0007\u0001\u0000\u001a\u0007\u0001\r\u0001\u0000\u0001\u000e\uff82\u0000");
	private static final int[] ZZ_ACTION = zzUnpackAction();
	private static final String ZZ_ACTION_PACKED_0 = "\u0005\u0000\u0001\u0001\u0001\u0002\u0001\u0001\u0001\u0003\u0001\u0001\u0001\u0004\u0001\u0005\u0001\u0006\u0001\u0007\u0001\b\u0001\t\u0002\n\u0001\u0001\u0001\u000b\u0001\f\u0001\r\u0001\u000e\u0001\u000f\u0001\u0010\u0001\u0011\u0001\u000e\u0001\u0012\u0001\u000e\u0001\u0013\u0004\u0000";
	private static final int[] ZZ_ROWMAP = zzUnpackRowMap();
	private static final String ZZ_ROWMAP_PACKED_0 = "\u0000\u0000\u0000\u0013\u0000&\u00009\u0000L\u0000_\u0000r\u0000\u0085\u0000_\u0000\u0098\u0000\u00ab\u0000_\u0000_\u0000_\u0000_\u0000_\u0000\u00be\u0000_\u0000\u00d1\u0000\u00e4\u0000_\u0000_\u0000\u00f7\u0000_\u0000_\u0000_\u0000\u010a\u0000_\u0000\u011d\u0000_\u0000\u0130\u0000\u0143\u0000\u0156\u0000\u0169";
	private static final int[] ZZ_TRANS = zzUnpackTrans();
	private static final String ZZ_TRANS_PACKED_0 = "\u0001\u0006\u0003\u0007\u0001\b\u0001\t\u0001\n\u0001\u000b\u0001\u0006\u0001\f\u0001\r\u0001\u000e\u0001\u000f\u0001\u0010\u0006\u0006\u0001\u0011\u0002\u0012\u0001\b\u0001\u0006\u0001\u0013\u0001\u0014\u0006\u0006\u0001\u0015\u0001\u0016\u0003\u0006\u000e\u0017\u0001\u0015\u0001\u0017\u0001\u0018\u0001\u0019\u0001\u001a\u0011\u001b\u0001\u001c\u0001\u001b\u0012\u001d\u0001\u001e\u0014\u0000\u0003\u0007\u0014\u0000\u0001\u001f\u0014\u0000\u0001\u000b\u0011\u0000\u0003\u000b\f\u0000\u0001\u0012\u0017\u0000\u0001\u0014\u0011\u0000\u0003\u0014\n\u0000\u000e\u0017\u0001\u0000\u0001\u0017\u0003\u0000\u0011\u001b\u0001\u0000\u0001\u001b\u0012\u001d\u0001\u0000\u0005 \u0001!\u0012 \u0001\"\r \u0004\u0000\u0001\u0012\u0001!\r\u0000\u0004 \u0001\u0012\u0001\"\r ";
	private static final int[] ZZ_ATTRIBUTE = zzUnpackAttribute();
	private static final String ZZ_ATTRIBUTE_PACKED_0 = "\u0005\u0000\u0001\t\u0002\u0001\u0001\t\u0002\u0001\u0005\t\u0001\u0001\u0001\t\u0002\u0001\u0002\t\u0001\u0001\u0003\t\u0001\u0001\u0001\t\u0001\u0001\u0001\t\u0004\u0000";
	private Reader zzReader;
	private int zzState;
	private int zzLexicalState = 0;
	private char[] zzBuffer = new char[16384];
	private int zzMarkedPos;
	private int zzCurrentPos;
	private int zzStartRead;
	private int zzEndRead;
	private int yyline;
	private int yycolumn;
	private boolean zzAtEOF;
	static final int EOF = 0;
	static final int IDENT = 1;
	static final int STAR = 2;
	static final int DOT = 3;
	static final int HASH = 4;
	static final int GT = 5;
	static final int COMMA = 6;
	static final int STYLE_BEGIN = 7;
	static final int STYLE_END = 8;
	static final int COLON = 9;
	static final int SEMICOLON = 10;
	boolean sawWhitespace;
	final StringBuilder sb = new StringBuilder();

	private static int[] zzUnpackAction() {
		int[] result = new int[34];
		byte offset = 0;
		zzUnpackAction("\u0005\u0000\u0001\u0001\u0001\u0002\u0001\u0001\u0001\u0003\u0001\u0001\u0001\u0004\u0001\u0005\u0001\u0006\u0001\u0007\u0001\b\u0001\t\u0002\n\u0001\u0001\u0001\u000b\u0001\f\u0001\r\u0001\u000e\u0001\u000f\u0001\u0010\u0001\u0011\u0001\u000e\u0001\u0012\u0001\u000e\u0001\u0013\u0004\u0000", offset, result);
		return result;
	}

	private static int zzUnpackAction(String packed, int offset, int[] result) {
		int i = 0;
		int j = offset;
		int l = packed.length();

		while(i < l) {
			int count = packed.charAt(i++);
			char value = packed.charAt(i++);

			while(true) {
				result[j++] = value;
				--count;
				if(count <= 0) {
					break;
				}
			}
		}

		return j;
	}

	private static int[] zzUnpackRowMap() {
		int[] result = new int[34];
		byte offset = 0;
		zzUnpackRowMap("\u0000\u0000\u0000\u0013\u0000&\u00009\u0000L\u0000_\u0000r\u0000\u0085\u0000_\u0000\u0098\u0000\u00ab\u0000_\u0000_\u0000_\u0000_\u0000_\u0000\u00be\u0000_\u0000\u00d1\u0000\u00e4\u0000_\u0000_\u0000\u00f7\u0000_\u0000_\u0000_\u0000\u010a\u0000_\u0000\u011d\u0000_\u0000\u0130\u0000\u0143\u0000\u0156\u0000\u0169", offset, result);
		return result;
	}

	private static int zzUnpackRowMap(String packed, int offset, int[] result) {
		int i = 0;
		int j = offset;

		int high;
		for(int l = packed.length(); i < l; result[j++] = high | packed.charAt(i++)) {
			high = packed.charAt(i++) << 16;
		}

		return j;
	}

	private static int[] zzUnpackTrans() {
		int[] result = new int[380];
		byte offset = 0;
		zzUnpackTrans("\u0001\u0006\u0003\u0007\u0001\b\u0001\t\u0001\n\u0001\u000b\u0001\u0006\u0001\f\u0001\r\u0001\u000e\u0001\u000f\u0001\u0010\u0006\u0006\u0001\u0011\u0002\u0012\u0001\b\u0001\u0006\u0001\u0013\u0001\u0014\u0006\u0006\u0001\u0015\u0001\u0016\u0003\u0006\u000e\u0017\u0001\u0015\u0001\u0017\u0001\u0018\u0001\u0019\u0001\u001a\u0011\u001b\u0001\u001c\u0001\u001b\u0012\u001d\u0001\u001e\u0014\u0000\u0003\u0007\u0014\u0000\u0001\u001f\u0014\u0000\u0001\u000b\u0011\u0000\u0003\u000b\f\u0000\u0001\u0012\u0017\u0000\u0001\u0014\u0011\u0000\u0003\u0014\n\u0000\u000e\u0017\u0001\u0000\u0001\u0017\u0003\u0000\u0011\u001b\u0001\u0000\u0001\u001b\u0012\u001d\u0001\u0000\u0005 \u0001!\u0012 \u0001\"\r \u0004\u0000\u0001\u0012\u0001!\r\u0000\u0004 \u0001\u0012\u0001\"\r ", offset, result);
		return result;
	}

	private static int zzUnpackTrans(String packed, int offset, int[] result) {
		int i = 0;
		int j = offset;
		int l = packed.length();

		while(i < l) {
			int count = packed.charAt(i++);
			char value = packed.charAt(i++);
			int i8 = value - 1;

			while(true) {
				result[j++] = i8;
				--count;
				if(count <= 0) {
					break;
				}
			}
		}

		return j;
	}

	private static int[] zzUnpackAttribute() {
		int[] result = new int[34];
		byte offset = 0;
		zzUnpackAttribute("\u0005\u0000\u0001\t\u0002\u0001\u0001\t\u0002\u0001\u0005\t\u0001\u0001\u0001\t\u0002\u0001\u0002\t\u0001\u0001\u0003\t\u0001\u0001\u0001\t\u0001\u0001\u0001\t\u0004\u0000", offset, result);
		return result;
	}

	private static int zzUnpackAttribute(String packed, int offset, int[] result) {
		int i = 0;
		int j = offset;
		int l = packed.length();

		while(i < l) {
			int count = packed.charAt(i++);
			char value = packed.charAt(i++);

			while(true) {
				result[j++] = value;
				--count;
				if(count <= 0) {
					break;
				}
			}
		}

		return j;
	}

	private void append() {
		this.sb.append(this.zzBuffer, this.zzStartRead, this.zzMarkedPos - this.zzStartRead);
	}

	public void unexpected() throws IOException {
		throw new IOException("Unexpected \"" + this.yytext() + "\" at line " + this.yyline + ", column " + this.yycolumn);
	}

	public void expect(int token) throws IOException {
		if(this.yylex() != token) {
			this.unexpected();
		}

	}

	Parser(Reader in) {
		this.zzReader = in;
	}

	private static char[] zzUnpackCMap(String packed) {
		char[] map = new char[65536];
		int i = 0;
		int j = 0;

		while(i < 70) {
			int count = packed.charAt(i++);
			char value = packed.charAt(i++);

			while(true) {
				map[j++] = value;
				--count;
				if(count <= 0) {
					break;
				}
			}
		}

		return map;
	}

	private boolean zzRefill() throws IOException {
		if(this.zzStartRead > 0) {
			System.arraycopy(this.zzBuffer, this.zzStartRead, this.zzBuffer, 0, this.zzEndRead - this.zzStartRead);
			this.zzEndRead -= this.zzStartRead;
			this.zzCurrentPos -= this.zzStartRead;
			this.zzMarkedPos -= this.zzStartRead;
			this.zzStartRead = 0;
		}

		if(this.zzCurrentPos >= this.zzBuffer.length) {
			char[] numRead = new char[this.zzCurrentPos * 2];
			System.arraycopy(this.zzBuffer, 0, numRead, 0, this.zzBuffer.length);
			this.zzBuffer = numRead;
		}

		int numRead1 = this.zzReader.read(this.zzBuffer, this.zzEndRead, this.zzBuffer.length - this.zzEndRead);
		if(numRead1 > 0) {
			this.zzEndRead += numRead1;
			return false;
		} else if(numRead1 == 0) {
			int c = this.zzReader.read();
			if(c == -1) {
				return true;
			} else {
				this.zzBuffer[this.zzEndRead++] = (char)c;
				return false;
			}
		} else {
			return true;
		}
	}

	public final void yybegin(int newState) {
		this.zzLexicalState = newState;
	}

	public final String yytext() {
		return new String(this.zzBuffer, this.zzStartRead, this.zzMarkedPos - this.zzStartRead);
	}

	private void zzScanError(String message) {
		throw new Error(message);
	}

	public int yylex() throws IOException {
		int zzEndReadL = this.zzEndRead;
		char[] zzBufferL = this.zzBuffer;
		char[] zzCMapL = ZZ_CMAP;
		int[] zzTransL = ZZ_TRANS;
		int[] zzRowMapL = ZZ_ROWMAP;
		int[] zzAttrL = ZZ_ATTRIBUTE;

		while(true) {
			int zzMarkedPosL = this.zzMarkedPos;
			boolean zzR = false;

			int zzCurrentPosL;
			for(zzCurrentPosL = this.zzStartRead; zzCurrentPosL < zzMarkedPosL; ++zzCurrentPosL) {
				switch(zzBufferL[zzCurrentPosL]) {
				case '\n':
					if(zzR) {
						zzR = false;
					} else {
						++this.yyline;
						this.yycolumn = 0;
					}
					break;
				case '\u000b':
				case '\f':
				case '\u0085':
				case '\u2028':
				case '\u2029':
					++this.yyline;
					this.yycolumn = 0;
					zzR = false;
					break;
				case '\r':
					++this.yyline;
					this.yycolumn = 0;
					zzR = true;
					break;
				default:
					zzR = false;
					++this.yycolumn;
				}
			}

			boolean zzNext;
			if(zzR) {
				if(zzMarkedPosL < zzEndReadL) {
					zzNext = zzBufferL[zzMarkedPosL] == 10;
				} else if(this.zzAtEOF) {
					zzNext = false;
				} else {
					boolean zzAttributes = this.zzRefill();
					zzEndReadL = this.zzEndRead;
					zzMarkedPosL = this.zzMarkedPos;
					zzBufferL = this.zzBuffer;
					if(zzAttributes) {
						zzNext = false;
					} else {
						zzNext = zzBufferL[zzMarkedPosL] == 10;
					}
				}

				if(zzNext) {
					--this.yyline;
				}
			}

			int zzAction = -1;
			zzCurrentPosL = this.zzCurrentPos = this.zzStartRead = zzMarkedPosL;
			this.zzState = this.zzLexicalState / 2;

			int zzInput;
			while(true) {
				if(zzCurrentPosL < zzEndReadL) {
					zzInput = zzBufferL[zzCurrentPosL++];
				} else {
					if(this.zzAtEOF) {
						zzInput = -1;
						break;
					}

					this.zzCurrentPos = zzCurrentPosL;
					this.zzMarkedPos = zzMarkedPosL;
					zzNext = this.zzRefill();
					zzCurrentPosL = this.zzCurrentPos;
					zzMarkedPosL = this.zzMarkedPos;
					zzBufferL = this.zzBuffer;
					zzEndReadL = this.zzEndRead;
					if(zzNext) {
						zzInput = -1;
						break;
					}

					zzInput = zzBufferL[zzCurrentPosL++];
				}

				int i15 = zzTransL[zzRowMapL[this.zzState] + zzCMapL[zzInput]];
				if(i15 == -1) {
					break;
				}

				this.zzState = i15;
				int i14 = zzAttrL[this.zzState];
				if((i14 & 1) == 1) {
					zzAction = this.zzState;
					zzMarkedPosL = zzCurrentPosL;
					if((i14 & 8) == 8) {
						break;
					}
				}
			}

			this.zzMarkedPos = zzMarkedPosL;
			switch(zzAction < 0 ? zzAction : ZZ_ACTION[zzAction]) {
			case 1:
				this.unexpected();
				break;
			case 2:
				this.sawWhitespace = true;
				break;
			case 3:
				this.sawWhitespace = false;
				return 2;
			case 4:
				this.sawWhitespace = false;
				return 1;
			case 5:
				return 3;
			case 6:
				return 6;
			case 7:
				return 5;
			case 8:
				return 4;
			case 9:
				this.yybegin(2);
				return 7;
			case 10:
			case 20:
			case 21:
			case 22:
			case 23:
			case 24:
			case 25:
			case 26:
			case 27:
			case 28:
			case 29:
			case 30:
			case 31:
			case 32:
			case 33:
			case 34:
			case 35:
			case 36:
			case 37:
			case 38:
				break;
			case 11:
				return 1;
			case 12:
				this.yybegin(0);
				return 8;
			case 13:
				this.yybegin(4);
				this.sb.setLength(0);
				return 9;
			case 14:
				this.append();
				break;
			case 15:
				this.yybegin(2);
				return 10;
			case 16:
				this.yybegin(6);
				this.sb.append('\'');
				break;
			case 17:
				this.yybegin(8);
				this.sb.append('\"');
				break;
			case 18:
				this.yybegin(4);
				this.sb.append('\'');
				break;
			case 19:
				this.yybegin(4);
				this.sb.append('\"');
				break;
			default:
				if(zzInput == -1 && this.zzStartRead == this.zzCurrentPos) {
					this.zzAtEOF = true;
					return 0;
				}

				this.zzScanError("Error: could not match input");
			}
		}
	}
}
