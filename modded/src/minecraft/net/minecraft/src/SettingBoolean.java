package net.minecraft.src;

public class SettingBoolean extends Setting {
	public SettingBoolean(String name, Boolean _defvalue) {
		this.defvalue = _defvalue;
		this.values.put("", (Boolean)this.defvalue);
		this.backendname = name;
	}

	public SettingBoolean(String name) {
		this(name, false);
	}

	public String toString(String context) {
		return this.get(context).booleanValue() ? "true" : "false";
	}

	public void fromString(String s, String context) {
		this.values.put(context, s.equals("true"));
		if(this.gui != null) {
			this.gui.update();
		}

	}

	public Boolean get(String context) {
		return this.values.get(context) != null ? (Boolean)this.values.get(context) : (this.values.get("") != null ? (Boolean)this.values.get("") : (Boolean)this.defvalue);
	}

	public void set(Boolean v, String context) {
		this.values.put(context, v);
		if(this.parent != null) {
			this.parent.save(context);
		}

		if(this.gui != null) {
			this.gui.update();
		}

	}

	public Object get(String string1) {
		return this.get(string1);
	}

	public void set(Object object1, String string2) {
		this.set((Boolean)object1, string2);
	}
}
