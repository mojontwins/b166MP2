package de.matthiasmann.twl;

import java.lang.reflect.Field;
import java.util.HashMap;

public final class Event {
	public static final int MODIFIER_LSHIFT = 1;
	public static final int MODIFIER_LMETA = 2;
	public static final int MODIFIER_LCTRL = 4;
	public static final int MODIFIER_RSHIFT = 8;
	public static final int MODIFIER_RMETA = 16;
	public static final int MODIFIER_RCTRL = 32;
	public static final int MODIFIER_LBUTTON = 64;
	public static final int MODIFIER_RBUTTON = 128;
	public static final int MODIFIER_MBUTTON = 256;
	public static final int MODIFIER_LALT = 512;
	public static final int MODIFIER_RALT = 1024;
	public static final int MODIFIER_SHIFT = 9;
	public static final int MODIFIER_META = 18;
	public static final int MODIFIER_CTRL = 36;
	public static final int MODIFIER_BUTTON = 448;
	public static final int MODIFIER_ALT = 1536;
	public static final int MOUSE_LBUTTON = 0;
	public static final int MOUSE_RBUTTON = 1;
	public static final int MOUSE_MBUTTON = 2;
	public static final char CHAR_NONE = '\u0000';
	public static final int KEY_NONE = 0;
	public static final int KEY_ESCAPE = 1;
	public static final int KEY_1 = 2;
	public static final int KEY_2 = 3;
	public static final int KEY_3 = 4;
	public static final int KEY_4 = 5;
	public static final int KEY_5 = 6;
	public static final int KEY_6 = 7;
	public static final int KEY_7 = 8;
	public static final int KEY_8 = 9;
	public static final int KEY_9 = 10;
	public static final int KEY_0 = 11;
	public static final int KEY_MINUS = 12;
	public static final int KEY_EQUALS = 13;
	public static final int KEY_BACK = 14;
	public static final int KEY_TAB = 15;
	public static final int KEY_Q = 16;
	public static final int KEY_W = 17;
	public static final int KEY_E = 18;
	public static final int KEY_R = 19;
	public static final int KEY_T = 20;
	public static final int KEY_Y = 21;
	public static final int KEY_U = 22;
	public static final int KEY_I = 23;
	public static final int KEY_O = 24;
	public static final int KEY_P = 25;
	public static final int KEY_LBRACKET = 26;
	public static final int KEY_RBRACKET = 27;
	public static final int KEY_RETURN = 28;
	public static final int KEY_LCONTROL = 29;
	public static final int KEY_A = 30;
	public static final int KEY_S = 31;
	public static final int KEY_D = 32;
	public static final int KEY_F = 33;
	public static final int KEY_G = 34;
	public static final int KEY_H = 35;
	public static final int KEY_J = 36;
	public static final int KEY_K = 37;
	public static final int KEY_L = 38;
	public static final int KEY_SEMICOLON = 39;
	public static final int KEY_APOSTROPHE = 40;
	public static final int KEY_GRAVE = 41;
	public static final int KEY_LSHIFT = 42;
	public static final int KEY_BACKSLASH = 43;
	public static final int KEY_Z = 44;
	public static final int KEY_X = 45;
	public static final int KEY_C = 46;
	public static final int KEY_V = 47;
	public static final int KEY_B = 48;
	public static final int KEY_N = 49;
	public static final int KEY_M = 50;
	public static final int KEY_COMMA = 51;
	public static final int KEY_PERIOD = 52;
	public static final int KEY_SLASH = 53;
	public static final int KEY_RSHIFT = 54;
	public static final int KEY_MULTIPLY = 55;
	public static final int KEY_LMENU = 56;
	public static final int KEY_SPACE = 57;
	public static final int KEY_CAPITAL = 58;
	public static final int KEY_F1 = 59;
	public static final int KEY_F2 = 60;
	public static final int KEY_F3 = 61;
	public static final int KEY_F4 = 62;
	public static final int KEY_F5 = 63;
	public static final int KEY_F6 = 64;
	public static final int KEY_F7 = 65;
	public static final int KEY_F8 = 66;
	public static final int KEY_F9 = 67;
	public static final int KEY_F10 = 68;
	public static final int KEY_NUMLOCK = 69;
	public static final int KEY_SCROLL = 70;
	public static final int KEY_NUMPAD7 = 71;
	public static final int KEY_NUMPAD8 = 72;
	public static final int KEY_NUMPAD9 = 73;
	public static final int KEY_SUBTRACT = 74;
	public static final int KEY_NUMPAD4 = 75;
	public static final int KEY_NUMPAD5 = 76;
	public static final int KEY_NUMPAD6 = 77;
	public static final int KEY_ADD = 78;
	public static final int KEY_NUMPAD1 = 79;
	public static final int KEY_NUMPAD2 = 80;
	public static final int KEY_NUMPAD3 = 81;
	public static final int KEY_NUMPAD0 = 82;
	public static final int KEY_DECIMAL = 83;
	public static final int KEY_F11 = 87;
	public static final int KEY_F12 = 88;
	public static final int KEY_F13 = 100;
	public static final int KEY_F14 = 101;
	public static final int KEY_F15 = 102;
	public static final int KEY_KANA = 112;
	public static final int KEY_CONVERT = 121;
	public static final int KEY_NOCONVERT = 123;
	public static final int KEY_YEN = 125;
	public static final int KEY_NUMPADEQUALS = 141;
	public static final int KEY_CIRCUMFLEX = 144;
	public static final int KEY_AT = 145;
	public static final int KEY_COLON = 146;
	public static final int KEY_UNDERLINE = 147;
	public static final int KEY_KANJI = 148;
	public static final int KEY_STOP = 149;
	public static final int KEY_AX = 150;
	public static final int KEY_UNLABELED = 151;
	public static final int KEY_NUMPADENTER = 156;
	public static final int KEY_RCONTROL = 157;
	public static final int KEY_NUMPADCOMMA = 179;
	public static final int KEY_DIVIDE = 181;
	public static final int KEY_SYSRQ = 183;
	public static final int KEY_RMENU = 184;
	public static final int KEY_PAUSE = 197;
	public static final int KEY_HOME = 199;
	public static final int KEY_UP = 200;
	public static final int KEY_PRIOR = 201;
	public static final int KEY_LEFT = 203;
	public static final int KEY_RIGHT = 205;
	public static final int KEY_END = 207;
	public static final int KEY_DOWN = 208;
	public static final int KEY_NEXT = 209;
	public static final int KEY_INSERT = 210;
	public static final int KEY_DELETE = 211;
	public static final int KEY_LMETA = 219;
	public static final int KEY_RMETA = 220;
	public static final int KEY_APPS = 221;
	public static final int KEY_POWER = 222;
	public static final int KEY_SLEEP = 223;
	Event.Type type;
	int mouseX;
	int mouseY;
	int mouseWheelDelta;
	int mouseButton;
	int mouseClickCount;
	boolean dragEvent;
	boolean keyRepeated;
	char keyChar;
	int keyCode;
	int modifier;
	private Event subEvent;
	private static final String[] KEY_NAMES = new String[256];
	private static final HashMap KEY_MAP = new HashMap(256);

	static {
		try {
			Field[] field3;
			int i2 = (field3 = Event.class.getFields()).length;

			for(int i1 = 0; i1 < i2; ++i1) {
				Field f = field3[i1];
				String name = f.getName();
				if(name.startsWith("KEY_")) {
					Integer code = (Integer)f.get((Object)null);
					name = name.substring(4);
					KEY_NAMES[code.intValue()] = name;
					KEY_MAP.put(name, code);
				}
			}
		} catch (Throwable throwable6) {
		}

	}

	public final Event.Type getType() {
		return this.type;
	}

	public final boolean isMouseEvent() {
		return this.type.isMouseEvent;
	}

	public final boolean isMouseEventNoWheel() {
		return this.type.isMouseEvent && this.type != Event.Type.MOUSE_WHEEL;
	}

	public final boolean isKeyEvent() {
		return this.type.isKeyEvent;
	}

	public final boolean isKeyPressedEvent() {
		return this.type == Event.Type.KEY_PRESSED;
	}

	public final boolean isMouseDragEvent() {
		return this.dragEvent;
	}

	public final boolean isMouseDragEnd() {
		return (this.modifier & 448) == 0;
	}

	public final int getMouseX() {
		return this.mouseX;
	}

	public final int getMouseY() {
		return this.mouseY;
	}

	public final int getMouseButton() {
		return this.mouseButton;
	}

	public final int getMouseWheelDelta() {
		return this.mouseWheelDelta;
	}

	public final int getMouseClickCount() {
		return this.mouseClickCount;
	}

	public final int getKeyCode() {
		return this.keyCode;
	}

	public final char getKeyChar() {
		return this.keyChar;
	}

	public final boolean hasKeyChar() {
		return this.type == Event.Type.KEY_PRESSED && this.keyChar != 0;
	}

	public final boolean hasKeyCharNoModifiers() {
		boolean MODIFIER_ALTGR = true;
		return this.hasKeyChar() && ((this.modifier & -10) == 0 || (this.modifier & -1029) == 0);
	}

	public final boolean isKeyRepeated() {
		return this.type == Event.Type.KEY_PRESSED && this.keyRepeated;
	}

	public final int getModifiers() {
		return this.modifier;
	}

	final Event createSubEvent(Event.Type newType) {
		if(this.subEvent == null) {
			this.subEvent = new Event();
		}

		this.subEvent.type = newType;
		this.subEvent.mouseX = this.mouseX;
		this.subEvent.mouseY = this.mouseY;
		this.subEvent.mouseButton = this.mouseButton;
		this.subEvent.mouseWheelDelta = this.mouseWheelDelta;
		this.subEvent.mouseClickCount = this.mouseClickCount;
		this.subEvent.dragEvent = this.dragEvent;
		this.subEvent.keyRepeated = this.keyRepeated;
		this.subEvent.keyChar = this.keyChar;
		this.subEvent.keyCode = this.keyCode;
		this.subEvent.modifier = this.modifier;
		return this.subEvent;
	}

	void setModifier(int mask, boolean pressed) {
		if(pressed) {
			this.modifier |= mask;
		} else {
			this.modifier &= ~mask;
		}

	}

	void setModifiers(boolean pressed) {
		short mask;
		switch(this.keyCode) {
		case 29:
			mask = 4;
			break;
		case 42:
			mask = 1;
			break;
		case 54:
			mask = 8;
			break;
		case 56:
			mask = 512;
			break;
		case 157:
			mask = 32;
			break;
		case 184:
			mask = 1024;
			break;
		case 219:
			mask = 2;
			break;
		case 220:
			mask = 16;
			break;
		default:
			return;
		}

		this.setModifier(mask, pressed);
	}

	public static String getKeyNameForCode(int key) {
		return key >= 0 && key < 256 ? KEY_NAMES[key] : null;
	}

	public static int getKeyCodeForName(String name) {
		Integer code = (Integer)KEY_MAP.get(name);
		return code != null ? code.intValue() : 0;
	}

	public static enum Type {
		MOUSE_ENTERED(true, false),
		MOUSE_MOVED(true, false),
		MOUSE_BTNDOWN(true, false),
		MOUSE_BTNUP(true, false),
		MOUSE_CLICKED(true, false),
		MOUSE_DRAGGED(true, false),
		MOUSE_EXITED(true, false),
		MOUSE_WHEEL(true, false),
		KEY_PRESSED(false, true),
		KEY_RELEASED(false, true),
		POPUP_OPENED(false, false),
		POPUP_CLOSED(false, false);

		final boolean isMouseEvent;
		final boolean isKeyEvent;

		private Type(boolean isMouseEvent, boolean isKeyEvent) {
			this.isMouseEvent = isMouseEvent;
			this.isKeyEvent = isKeyEvent;
		}
	}
}
