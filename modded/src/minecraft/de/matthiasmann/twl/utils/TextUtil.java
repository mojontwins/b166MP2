package de.matthiasmann.twl.utils;

public final class TextUtil {
	private static final String ROMAN_NUMBERS = "\u2182M\u2182\u2181M\u2181MCMDCDCXCLXLXIXVIVI";
	private static final String ROMAN_VALUES = "\u2710\u2328\u1388\u0fa0\u03e8\u0384\u01f4\u0190dZ2(\n\t\u0005\u0004\u0001";
	public static final int MAX_ROMAN_INTEGER = 39999;

	public static int countNumLines(CharSequence str) {
		int n = str.length();
		int count = 0;
		if(n > 0) {
			++count;

			for(int i = 0; i < n; ++i) {
				if(str.charAt(i) == 10) {
					++count;
				}
			}
		}

		return count;
	}

	public static String stripNewLines(String str) {
		int idx = str.lastIndexOf(10);
		if(idx < 0) {
			return str;
		} else {
			StringBuilder sb = new StringBuilder(str);

			do {
				if(sb.charAt(idx) == 10) {
					sb.deleteCharAt(idx);
				}

				--idx;
			} while(idx >= 0);

			return sb.toString();
		}
	}

	public static String limitStringLength(String str, int length) {
		return str.length() > length ? str.substring(0, length) : str;
	}

	public static String notNull(String str) {
		return str == null ? "" : str;
	}

	public static int indexOf(CharSequence cs, char ch, int start) {
		int n;
		for(n = cs.length(); start < n; ++start) {
			if(cs.charAt(start) == ch) {
				return start;
			}
		}

		return n;
	}

	public static int indexOf(String str, char ch, int start) {
		int idx = str.indexOf(ch, start);
		return idx < 0 ? str.length() : idx;
	}

	public static int skipSpaces(CharSequence s, int start) {
		return skipSpaces(s, start, s.length());
	}

	public static int skipSpaces(CharSequence s, int start, int end) {
		while(start < end && Character.isWhitespace(s.charAt(start))) {
			++start;
		}

		return start;
	}

	public static String createString(char ch, int len) {
		char[] buf = new char[len];

		for(int i = 0; i < len; ++i) {
			buf[i] = ch;
		}

		return new String(buf);
	}

	public static int[] parseIntArray(String str) throws NumberFormatException {
		int count = countElements(str);
		int[] result = new int[count];
		int idx = 0;

		for(int pos = 0; idx < count; ++idx) {
			int comma = indexOf(str, ',', pos);
			result[idx] = Integer.parseInt(str.substring(pos, comma));
			pos = comma + 1;
		}

		return result;
	}

	public static int countElements(String str) {
		int count = 0;

		for(int pos = 0; pos < str.length(); pos = indexOf(str, ',', pos) + 1) {
			++count;
		}

		return count;
	}

	public static String toPrintableString(char ch) {
		return Character.isISOControl(ch) ? '\\' + Integer.toOctalString(ch) : Character.toString(ch);
	}

	public static String toRomanNumberString(int value) throws IllegalArgumentException {
		if(value >= 1 && value <= 39999) {
			StringBuilder sb = new StringBuilder();
			int idxValues = 0;
			int idxNumbers = 0;

			do {
				char romanValue = "\u2710\u2328\u1388\u0fa0\u03e8\u0384\u01f4\u0190dZ2(\n\t\u0005\u0004\u0001".charAt(idxValues);

				int romanNumberLen;
				for(romanNumberLen = (idxValues & 1) + 1; value >= romanValue; value -= romanValue) {
					sb.append("\u2182M\u2182\u2181M\u2181MCMDCDCXCLXLXIXVIVI", idxNumbers, idxNumbers + romanNumberLen);
				}

				idxNumbers += romanNumberLen;
				++idxValues;
			} while(idxValues < 17);

			return sb.toString();
		} else {
			throw new IllegalArgumentException();
		}
	}

	public static String toCharListNumber(int value, String list) throws IllegalArgumentException {
		if(value < 1) {
			throw new IllegalArgumentException("value");
		} else {
			int pos = 16;
			char[] tmp = new char[pos];

			do {
				--pos;
				--value;
				tmp[pos] = list.charAt(value % list.length());
				value /= list.length();
			} while(value > 0);

			return new String(tmp, pos, tmp.length - pos);
		}
	}
}
