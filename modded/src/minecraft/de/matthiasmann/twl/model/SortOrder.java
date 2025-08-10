package de.matthiasmann.twl.model;

import java.util.Collections;
import java.util.Comparator;

public enum SortOrder {
	ASCENDING {
		public Comparator map(Comparator c) {
			return c;
		}

		public SortOrder invert() {
			return DESCENDING;
		}
	},
	DESCENDING {
		public Comparator map(Comparator c) {
			return Collections.reverseOrder(c);
		}

		public SortOrder invert() {
			return ASCENDING;
		}
	};

	private SortOrder() {
	}

	public abstract Comparator map(Comparator comparator1);

	public abstract SortOrder invert();

	SortOrder(SortOrder sortOrder3) {
		this();
	}
}
