package net.minecraft.src;

public class SettingText extends Setting {
	public SettingText(String title, String defaulttext) {
		this.values.put("", defaulttext);
		this.defvalue = defaulttext;
		this.backendname = title;
	}

	public void fromString(String s, String context) {
		this.values.put(context, s);
		if(this.gui != null) {
			this.gui.update();
		}

	}

	public String toString(String context) {
		return this.get(context);
	}

	public String get(String context) {
		return this.values.get(context) != null ? (String)this.values.get(context) : (this.values.get("") != null ? (String)this.values.get("") : (String)this.defvalue);
	}

	public void set(String v, String context) {
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
		this.set((String)object1, string2);
	}
}
