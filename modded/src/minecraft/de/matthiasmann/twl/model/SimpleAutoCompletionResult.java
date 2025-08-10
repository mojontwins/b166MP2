package de.matthiasmann.twl.model;

import java.util.Collection;

public class SimpleAutoCompletionResult extends AutoCompletionResult {
	private final String[] results;

	public SimpleAutoCompletionResult(String text, int prefixLength, Collection results) {
		super(text, prefixLength);
		this.results = (String[])results.toArray(new String[results.size()]);
	}

	public SimpleAutoCompletionResult(String text, int prefixLength, String... results) {
		super(text, prefixLength);
		this.results = (String[])results.clone();
	}

	public int getNumResults() {
		return this.results.length;
	}

	public String getResult(int idx) {
		return this.results[idx];
	}
}
