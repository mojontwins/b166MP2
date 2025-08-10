package net.minecraft.src;

import org.lwjgl.input.Keyboard;

public class SettingKey extends Setting {
	public SettingKey(String title, int key) {
		this.defvalue = key;
		this.values.put("", key);
		this.backendname = title;
	}

	public SettingKey(String title, String key) {
		this(title, Keyboard.getKeyIndex(key));
	}

	public String toString(String context) {
		return Keyboard.getKeyName(this.get(context).intValue());
	}

	public void fromString(String s, String context) {
		if(s.equals("UNBOUND")) {
			this.values.put(context, 0);
		} else {
			this.values.put(context, Keyboard.getKeyIndex(s));
		}

		if(this.gui != null) {
			this.gui.update();
		}

	}

	public void set(Integer v, String context) {
		this.values.put(context, v);
		if(this.parent != null) {
			this.parent.save(context);
		}

		if(this.gui != null) {
			this.gui.update();
		}

	}

	public void set(String v, String context) {
		this.set(Keyboard.getKeyIndex(v), context);
	}

	public void set(String v) {
		this.set(v, ModSettings.currentContext);
	}

	public Integer get(String context) {
		return this.values.get(context) != null ? (Integer)this.values.get(context) : (this.values.get("") != null ? (Integer)this.values.get("") : (Integer)this.defvalue);
	}

	public boolean isKeyDown(String context) {
		return this.get(context).intValue() != -1 ? Keyboard.isKeyDown(this.get(context).intValue()) : false;
	}

	public boolean isKeyDown() {
		return this.isKeyDown(ModSettings.currentContext);
	}

	public Object get(String string1) {
		return this.get(string1);
	}

	public void set(Object object1, String string2) {
		this.set((Integer)object1, string2);
	}
}
