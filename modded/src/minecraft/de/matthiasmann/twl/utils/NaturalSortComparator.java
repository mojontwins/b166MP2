package de.matthiasmann.twl.utils;

import java.util.Comparator;

public class NaturalSortComparator {
	public static final Comparator stringComparator = new Comparator() {
		public int compare(String n1, String n2) {
			return NaturalSortComparator.naturalCompare(n1, n2);
		}

		public int compare(Object object1, Object object2) {
			return this.compare((String)object1, (String)object2);
		}
	};
	public static final Comparator stringPathComparator = new Comparator() {
		public int compare(String n1, String n2) {
			return NaturalSortComparator.naturalCompareWithPaths(n1, n2);
		}

		public int compare(Object object1, Object object2) {
			return this.compare((String)object1, (String)object2);
		}
	};

	private static int findDiff(String s1, int idx1, String s2, int idx2) {
		int len = Math.min(s1.length() - idx1, s2.length() - idx2);

		for(int i = 0; i < len; ++i) {
			char c1 = s1.charAt(idx1 + i);
			char c2 = s2.charAt(idx2 + i);
			if(c1 != c2 && Character.toLowerCase(c1) != Character.toLowerCase(c2)) {
				return i;
			}
		}

		return len;
	}

	private static int findNumberStart(String s, int i) {
		while(i > 0 && Character.isDigit(s.charAt(i - 1))) {
			--i;
		}

		return i;
	}

	private static int findNumberEnd(String s, int i) {
		for(int len = s.length(); i < len && Character.isDigit(s.charAt(i)); ++i) {
		}

		return i;
	}

	public static int naturalCompareWithPaths(String n1, String n2) {
		int diffOffset = findDiff(n1, 0, n2, 0);
		int idx0 = n1.indexOf(47, diffOffset);
		int idx1 = n2.indexOf(47, diffOffset);
		return (idx0 ^ idx1) < 0 ? idx0 : naturalCompare(n1, n2, diffOffset, diffOffset);
	}

	public static int naturalCompare(String n1, String n2) {
		return naturalCompare(n1, n2, 0, 0);
	}

	private static int naturalCompare(String n1, String n2, int i1, int i2) {
		while(true) {
			int diffOffset = findDiff(n1, i1, n2, i2);
			i1 += diffOffset;
			i2 += diffOffset;
			if(i1 != n1.length() && i2 != n2.length()) {
				char c1 = n1.charAt(i1);
				char c2 = n2.charAt(i2);
				if(Character.isDigit(c1) || Character.isDigit(c2)) {
					int cl1 = findNumberStart(n1, i1);
					int cl2 = findNumberStart(n2, i2);
					if(Character.isDigit(n1.charAt(cl1)) && Character.isDigit(n2.charAt(cl2))) {
						i1 = findNumberEnd(n1, cl1 + 1);
						i2 = findNumberEnd(n2, cl2 + 1);

						try {
							long value1 = Long.parseLong(n1.substring(cl1, i1), 10);
							long value2 = Long.parseLong(n2.substring(cl2, i2), 10);
							if(value1 == value2) {
								continue;
							}

							return Long.signum(value1 - value2);
						} catch (NumberFormatException numberFormatException13) {
						}
					}
				}

				char cl11 = Character.toLowerCase(c1);
				char cl21 = Character.toLowerCase(c2);

				assert cl11 != cl21;

				return cl11 - cl21;
			}

			return n1.length() - n2.length();
		}
	}
}
