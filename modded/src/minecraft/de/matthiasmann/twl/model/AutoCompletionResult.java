package de.matthiasmann.twl.model;

public abstract class AutoCompletionResult {
	public static final int DEFAULT_CURSOR_POS = -1;
	protected final String text;
	protected final int prefixLength;

	public AutoCompletionResult(String text, int prefixLength) {
		this.text = text;
		this.prefixLength = prefixLength;
	}

	public int getPrefixLength() {
		return this.prefixLength;
	}

	public String getText() {
		return this.text;
	}

	public abstract int getNumResults();

	public abstract String getResult(int i1);

	public int getCursorPosForResult(int idx) {
		return -1;
	}

	public AutoCompletionResult refine(String text, int cursorPos) {
		return null;
	}
}
