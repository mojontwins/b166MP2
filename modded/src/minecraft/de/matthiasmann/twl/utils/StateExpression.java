package de.matthiasmann.twl.utils;

import de.matthiasmann.twl.renderer.AnimationState;

import java.text.ParseException;
import java.util.ArrayList;

public abstract class StateExpression {
	protected boolean negate;

	public abstract boolean evaluate(AnimationState animationState1);

	public static StateExpression parse(String exp, boolean negate) throws ParseException {
		StateExpression.StringIterator si = new StateExpression.StringIterator(exp);
		StateExpression expr = parse(si);
		if(si.hasMore()) {
			si.unexpected();
		}

		expr.negate ^= negate;
		return expr;
	}

	private static StateExpression parse(StateExpression.StringIterator si) throws ParseException {
		ArrayList children = new ArrayList();
		char kind = 32;

		while(true) {
			if(!si.skipSpaces()) {
				si.unexpected();
			}

			char childArray = si.peek();
			boolean negate = childArray == 33;
			if(negate) {
				++si.pos;
				if(!si.skipSpaces()) {
					si.unexpected();
				}

				childArray = si.peek();
			}

			Object child = null;
			if(Character.isJavaIdentifierStart(childArray)) {
				child = new StateExpression.Check(si.getIdent());
			} else if(childArray == 40) {
				++si.pos;
				child = parse(si);
				si.expect(')');
			} else {
				if(childArray == 41) {
					break;
				}

				si.unexpected();
			}

			((StateExpression)child).negate = negate;
			children.add(child);
			if(!si.skipSpaces()) {
				break;
			}

			childArray = si.peek();
			if("|+^".indexOf(childArray) < 0) {
				break;
			}

			if(children.size() == 1) {
				kind = childArray;
			} else if(kind != childArray) {
				si.expect(kind);
			}

			++si.pos;
		}

		if(children.isEmpty()) {
			si.unexpected();
		}

		assert kind != 32 || children.size() == 1;

		if(children.size() == 1) {
			return (StateExpression)children.get(0);
		} else {
			StateExpression[] stateExpression6 = (StateExpression[])children.toArray(new StateExpression[children.size()]);
			return (StateExpression)(kind == 94 ? new StateExpression.Xor(stateExpression6) : new StateExpression.AndOr(kind, stateExpression6));
		}
	}

	static class AndOr extends StateExpression {
		private final StateExpression[] children;
		private final boolean kind;

		public AndOr(char kind, StateExpression... children) {
			assert kind == 124 || kind == 43;

			this.children = children;
			this.kind = kind == 124;
		}

		public boolean evaluate(AnimationState as) {
			StateExpression[] stateExpression5 = this.children;
			int i4 = this.children.length;

			for(int i3 = 0; i3 < i4; ++i3) {
				StateExpression e = stateExpression5[i3];
				if(this.kind == e.evaluate(as)) {
					return this.kind ^ this.negate;
				}
			}

			return !this.kind ^ this.negate;
		}
	}

	static class Check extends StateExpression {
		private final AnimationState.StateKey state;

		public Check(String state) {
			this.state = AnimationState.StateKey.get(state);
		}

		public boolean evaluate(AnimationState as) {
			return this.negate ^ (as != null && as.getAnimationState(this.state));
		}
	}

	static class StringIterator {
		final String str;
		int pos;

		StringIterator(String str) {
			this.str = str;
		}

		boolean hasMore() {
			return this.pos < this.str.length();
		}

		char peek() {
			return this.str.charAt(this.pos);
		}

		void expect(char what) throws ParseException {
			if(this.hasMore() && this.peek() == what) {
				++this.pos;
			} else {
				throw new ParseException("Expected \'" + what + "\' got " + this.describePosition(), this.pos);
			}
		}

		void unexpected() throws ParseException {
			throw new ParseException("Unexpected " + this.describePosition(), this.pos);
		}

		String describePosition() {
			return this.pos >= this.str.length() ? "end of expression" : "\'" + this.peek() + "\' at " + (this.pos + 1);
		}

		boolean skipSpaces() {
			while(this.hasMore() && Character.isWhitespace(this.peek())) {
				++this.pos;
			}

			return this.hasMore();
		}

		String getIdent() {
			int start;
			for(start = this.pos; this.hasMore() && Character.isJavaIdentifierPart(this.peek()); ++this.pos) {
			}

			return this.str.substring(start, this.pos).intern();
		}
	}

	static class Xor extends StateExpression {
		private final StateExpression[] children;

		public Xor(StateExpression... children) {
			this.children = children;
		}

		public boolean evaluate(AnimationState as) {
			boolean result = this.negate;
			StateExpression[] stateExpression6 = this.children;
			int i5 = this.children.length;

			for(int i4 = 0; i4 < i5; ++i4) {
				StateExpression e = stateExpression6[i4];
				result ^= e.evaluate(as);
			}

			return result;
		}
	}
}
