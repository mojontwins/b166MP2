package de.matthiasmann.twl.utils;

import java.io.IOException;
import java.io.Reader;

public class MultiStringReader extends Reader {
	private final String[] strings;
	private String cur;
	private int nr;
	private int pos;

	public MultiStringReader(String... strings) {
		this.strings = strings;
	}

	public int read(char[] cbuf, int off, int len) throws IOException {
		while(this.cur == null || this.pos == this.cur.length()) {
			if(this.nr == this.strings.length) {
				return -1;
			}

			this.cur = this.strings[this.nr++];
			this.pos = 0;
		}

		int remain = this.cur.length() - this.pos;
		if(len > remain) {
			len = remain;
		}

		this.cur.getChars(this.pos, this.pos += len, cbuf, off);
		return len;
	}

	public void close() {
	}
}
