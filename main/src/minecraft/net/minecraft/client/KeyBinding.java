package net.minecraft.client;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.world.IntHashMap;

public class KeyBinding {
	public static List<KeyBinding> keybindArray = new ArrayList<KeyBinding>();
	public static IntHashMap hash = new IntHashMap();
	public String keyDescription;
	public int keyCode;
	public boolean pressed;
	public int pressTime = 0;

	public static void onTick(int i0) {
		KeyBinding keyBinding1 = (KeyBinding)hash.lookup(i0);
		if(keyBinding1 != null) {
			++keyBinding1.pressTime;
		}

	}

	public static void setKeyBindState(int i0, boolean z1) {
		KeyBinding keyBinding2 = (KeyBinding)hash.lookup(i0);
		if(keyBinding2 != null) {
			keyBinding2.pressed = z1;
		}

	}

	public static void unPressAllKeys() {
		Iterator<KeyBinding> iterator0 = keybindArray.iterator();

		while(iterator0.hasNext()) {
			KeyBinding keyBinding1 = (KeyBinding)iterator0.next();
			keyBinding1.unpressKey();
		}

	}

	public static void resetKeyBindingArrayAndHash() {
		hash.clearMap();
		Iterator<KeyBinding> iterator0 = keybindArray.iterator();

		while(iterator0.hasNext()) {
			KeyBinding keyBinding1 = (KeyBinding)iterator0.next();
			hash.addKey(keyBinding1.keyCode, keyBinding1);
		}

	}

	public KeyBinding(String string1, int i2) {
		this.keyDescription = string1;
		this.keyCode = i2;
		keybindArray.add(this);
		hash.addKey(i2, this);
	}

	public boolean isPressed() {
		if(this.pressTime == 0) {
			return false;
		} else {
			--this.pressTime;
			return true;
		}
	}

	private void unpressKey() {
		this.pressTime = 0;
		this.pressed = false;
	}
}
