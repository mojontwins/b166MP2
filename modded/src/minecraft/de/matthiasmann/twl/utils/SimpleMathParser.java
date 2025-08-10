package de.matthiasmann.twl.utils;

import java.text.ParseException;

public class SimpleMathParser {
	final String str;
	final SimpleMathParser.Interpreter interpreter;
	int pos;

	private SimpleMathParser(String str, SimpleMathParser.Interpreter interpreter) {
		this.str = str;
		this.interpreter = interpreter;
	}

	public static void interpret(String str, SimpleMathParser.Interpreter interpreter) throws ParseException {
		(new SimpleMathParser(str, interpreter)).parse(false);
	}

	public static int interpretArray(String str, SimpleMathParser.Interpreter interpreter) throws ParseException {
		return (new SimpleMathParser(str, interpreter)).parse(true);
	}

	private int parse(boolean allowArray) throws ParseException {
		try {
			if(this.peek() == -1) {
				if(allowArray) {
					return 0;
				}

				this.unexpected(-1);
			}

			int ex = 0;

			while(true) {
				++ex;
				this.parseAddSub();
				int ch = this.peek();
				if(ch == -1) {
					return ex;
				}

				if(ch != 44 || !allowArray) {
					this.unexpected(ch);
				}

				++this.pos;
			}
		} catch (ParseException parseException4) {
			throw parseException4;
		} catch (Exception exception5) {
			throw (ParseException)(new ParseException("Unable to execute", this.pos)).initCause(exception5);
		}
	}

	private void parseAddSub() throws ParseException {
		this.parseMulDiv();

		while(true) {
			int ch = this.peek();
			switch(ch) {
			case 43:
				++this.pos;
				this.parseMulDiv();
				this.interpreter.add();
				break;
			case 44:
			default:
				return;
			case 45:
				++this.pos;
				this.parseMulDiv();
				this.interpreter.sub();
			}
		}
	}

	private void parseMulDiv() throws ParseException {
		this.parseIdentOrConst();

		while(true) {
			int ch = this.peek();
			switch(ch) {
			case 42:
				++this.pos;
				this.parseIdentOrConst();
				this.interpreter.mul();
				break;
			case 47:
				++this.pos;
				this.parseIdentOrConst();
				this.interpreter.div();
				break;
			default:
				return;
			}
		}
	}

	private void parseIdentOrConst() throws ParseException {
		int ch = this.peek();
		if(Character.isJavaIdentifierStart((char)ch)) {
			String ident = this.parseIdent();
			ch = this.peek();
			if(ch == 40) {
				++this.pos;
				this.parseCall(ident);
				return;
			}

			this.interpreter.accessVariable(ident);

			for(; ch == 46 || ch == 91; ch = this.peek()) {
				++this.pos;
				if(ch == 46) {
					String field = this.parseIdent();
					this.interpreter.accessField(field);
				} else {
					this.parseIdentOrConst();
					this.expect(93);
					this.interpreter.accessArray();
				}
			}
		} else if(ch != 46 && ch != 45 && ch != 43 && !Character.isDigit((char)ch)) {
			if(ch == 40) {
				++this.pos;
				this.parseAddSub();
				this.expect(41);
			}
		} else {
			this.parseConst();
		}

	}

	private void parseCall(String name) throws ParseException {
		int count = 1;
		this.parseAddSub();

		while(true) {
			int ch = this.peek();
			if(ch == 41) {
				++this.pos;
				this.interpreter.callFunction(name, count);
				return;
			}

			if(ch == 44) {
				++this.pos;
				++count;
				this.parseAddSub();
			} else {
				this.unexpected(ch);
			}
		}
	}

	private void parseConst() throws ParseException {
		int len = this.str.length();
		int start = this.pos;
		switch(this.str.charAt(this.pos)) {
		case '+':
			start = ++this.pos;
		case ',':
		case '.':
		case '/':
		default:
			break;
		case '-':
			++this.pos;
			break;
		case '0':
			if(this.pos + 1 < len && this.str.charAt(this.pos + 1) == 120) {
				this.pos += 2;
				this.parseHexInt();
				return;
			}
		}

		while(this.pos < len && Character.isDigit(this.str.charAt(this.pos))) {
			++this.pos;
		}

		Object n;
		if(this.pos < len && this.str.charAt(this.pos) == 46) {
			++this.pos;

			while(this.pos < len && Character.isDigit(this.str.charAt(this.pos))) {
				++this.pos;
			}

			if(this.pos - start <= 1) {
				this.unexpected(-1);
			}

			n = Float.valueOf(this.str.substring(start, this.pos));
		} else {
			n = Integer.valueOf(this.str.substring(start, this.pos));
		}

		this.interpreter.loadConst((Number)n);
	}

	private void parseHexInt() throws ParseException {
		int len = this.str.length();

		int start;
		for(start = this.pos; this.pos < len && "0123456789abcdefABCDEF".indexOf(this.str.charAt(this.pos)) >= 0; ++this.pos) {
		}

		if(this.pos - start > 8) {
			throw new ParseException("number to large at " + this.pos, this.pos);
		} else {
			if(this.pos == start) {
				this.unexpected(this.peek());
			}

			this.interpreter.loadConst((int)Long.parseLong(this.str.substring(start, this.pos), 16));
		}
	}

	private boolean skipSpaces() {
		while(this.pos != this.str.length()) {
			if(!Character.isWhitespace(this.str.charAt(this.pos))) {
				return true;
			}

			++this.pos;
		}

		return false;
	}

	private int peek() {
		return this.skipSpaces() ? this.str.charAt(this.pos) : -1;
	}

	private String parseIdent() {
		int start;
		for(start = this.pos; this.pos < this.str.length() && Character.isJavaIdentifierPart(this.str.charAt(this.pos)); ++this.pos) {
		}

		return this.str.substring(start, this.pos);
	}

	private void expect(int what) throws ParseException {
		int ch = this.peek();
		if(ch != what) {
			this.unexpected(ch);
		} else {
			++this.pos;
		}

	}

	private void unexpected(int ch) throws ParseException {
		if(ch < 0) {
			throw new ParseException("Unexpected end of string", this.pos);
		} else {
			throw new ParseException("Unexpected character \'" + (char)ch + "\' at " + this.pos, this.pos);
		}
	}

	public interface Interpreter {
		void accessVariable(String string1);

		void accessField(String string1);

		void accessArray();

		void loadConst(Number number1);

		void add();

		void sub();

		void mul();

		void div();

		void callFunction(String string1, int i2);
	}
}
