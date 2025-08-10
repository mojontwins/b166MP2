package de.matthiasmann.twl;

import de.matthiasmann.twl.utils.TextUtil;

import java.util.Locale;

public final class KeyStroke {
	private static final int SHIFT = 1;
	private static final int CTRL = 2;
	private static final int META = 4;
	private static final int ALT = 8;
	private static final int CMD = 20;
	private final int modifier;
	private final int keyCode;
	private final char keyChar;
	private final String action;

	private KeyStroke(int modifier, int keyCode, char keyChar, String action) {
		this.modifier = modifier;
		this.keyCode = keyCode;
		this.keyChar = keyChar;
		this.action = action;
	}

	public String getAction() {
		return this.action;
	}

	public String getStroke() {
		StringBuilder sb = new StringBuilder();
		if((this.modifier & 1) == 1) {
			sb.append("shift ");
		}

		if((this.modifier & 2) == 2) {
			sb.append("ctrl ");
		}

		if((this.modifier & 8) == 8) {
			sb.append("alt ");
		}

		if((this.modifier & 20) == 20) {
			sb.append("cmd ");
		} else if((this.modifier & 4) == 4) {
			sb.append("meta ");
		}

		if(this.keyCode != 0) {
			sb.append(Event.getKeyNameForCode(this.keyCode));
		} else {
			sb.append("typed ").append(this.keyChar);
		}

		return sb.toString();
	}

	public boolean equals(Object obj) {
		if(obj instanceof KeyStroke) {
			KeyStroke other = (KeyStroke)obj;
			return this.modifier == other.modifier && this.keyCode == other.keyCode && this.keyChar == other.keyChar;
		} else {
			return false;
		}
	}

	public int hashCode() {
		byte hash = 5;
		int hash1 = 83 * hash + this.modifier;
		hash1 = 83 * hash1 + this.keyCode;
		hash1 = 83 * hash1 + this.keyChar;
		return hash1;
	}

	public static KeyStroke parse(String stroke, String action) {
		if(stroke == null) {
			throw new NullPointerException("stroke");
		} else if(action == null) {
			throw new NullPointerException("action");
		} else {
			int idx = TextUtil.skipSpaces(stroke, 0);
			int modifers = 0;
			char keyChar = 0;
			int keyCode = 0;
			boolean typed = false;

			boolean end;
			int endIdx;
			for(end = false; idx < stroke.length(); idx = TextUtil.skipSpaces(stroke, endIdx + 1)) {
				endIdx = TextUtil.indexOf(stroke, ' ', idx);
				String part = stroke.substring(idx, endIdx);
				if(end) {
					throw new IllegalArgumentException("Unexpected: " + part);
				}

				if(typed) {
					if(part.length() != 1) {
						throw new IllegalArgumentException("Expected single character after \'typed\'");
					}

					keyChar = part.charAt(0);
					if(keyChar == 0) {
						throw new IllegalArgumentException("Unknown character: " + part);
					}

					end = true;
				} else if(!"ctrl".equalsIgnoreCase(part) && !"control".equalsIgnoreCase(part)) {
					if("shift".equalsIgnoreCase(part)) {
						modifers |= 1;
					} else if("meta".equalsIgnoreCase(part)) {
						modifers |= 4;
					} else if("cmd".equalsIgnoreCase(part)) {
						modifers |= 20;
					} else if("alt".equalsIgnoreCase(part)) {
						modifers |= 8;
					} else if("typed".equalsIgnoreCase(part)) {
						typed = true;
					} else {
						keyCode = Event.getKeyCodeForName(part.toUpperCase(Locale.ENGLISH));
						if(keyCode == 0) {
							throw new IllegalArgumentException("Unknown key: " + part);
						}

						end = true;
					}
				} else {
					modifers |= 2;
				}
			}

			if(!end) {
				throw new IllegalArgumentException("Unexpected end of string");
			} else {
				return new KeyStroke(modifers, keyCode, keyChar, action);
			}
		}
	}

	public static KeyStroke fromEvent(Event event, String action) {
		if(event == null) {
			throw new NullPointerException("event");
		} else if(action == null) {
			throw new NullPointerException("action");
		} else if(event.getType() != Event.Type.KEY_PRESSED) {
			throw new IllegalArgumentException("Event is not a Type.KEY_PRESSED");
		} else {
			int modifiers = convertModifier(event);
			return new KeyStroke(modifiers, event.getKeyCode(), '\u0000', action);
		}
	}

	boolean match(Event e, int mappedEventModifiers) {
		return mappedEventModifiers != this.modifier ? false : (this.keyCode != 0 && this.keyCode != e.getKeyCode() ? false : this.keyChar == 0 || e.hasKeyChar() && this.keyChar == e.getKeyChar());
	}

	static int convertModifier(Event event) {
		int eventModifiers = event.getModifiers();
		int modifiers = 0;
		if((eventModifiers & 9) != 0) {
			modifiers |= 1;
		}

		if((eventModifiers & 36) != 0) {
			modifiers |= 2;
		}

		if((eventModifiers & 18) != 0) {
			modifiers |= 4;
		}

		if((eventModifiers & 2) != 0) {
			modifiers |= 20;
		}

		if((eventModifiers & 1536) != 0) {
			modifiers |= 8;
		}

		return modifiers;
	}
}
