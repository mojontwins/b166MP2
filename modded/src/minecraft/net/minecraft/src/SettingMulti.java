package net.minecraft.src;

public class SettingMulti extends Setting {
	public String[] labels;

	public SettingMulti(String title, int def, String... _labels) {
		if(_labels.length != 0) {
			this.values.put("", def);
			this.defvalue = def;
			this.labels = _labels;
			this.backendname = title;
		}
	}

	public SettingMulti(String title, String... _labels) {
		this(title, 0, _labels);
	}

	public String toString(String context) {
		return this.labels[this.get(context).intValue()];
	}

	public void fromString(String s, String context) {
		int x = -1;

		for(int i = 0; i < this.labels.length; ++i) {
			if(this.labels[i].equals(s)) {
				x = i;
			}
		}

		if(x != -1) {
			this.values.put(context, x);
		} else {
			this.values.put(context, (new Float(s)).intValue());
		}

		ModSettings.dbgout("fromstring multi " + s);
		if(this.gui != null) {
			this.gui.update();
		}

	}

	public void set(String v, String context) {
		int x = -1;

		for(int i = 0; i < this.labels.length; ++i) {
			if(this.labels[i].equals(v)) {
				x = i;
			}
		}

		if(x != -1) {
			this.set(x, context);
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

	public void set(String v) {
		this.set(v, ModSettings.currentContext);
	}

	public Integer get(String context) {
		return this.values.get(context) != null ? (Integer)this.values.get(context) : (this.values.get("") != null ? (Integer)this.values.get("") : (Integer)this.defvalue);
	}

	public String getLabel(String context) {
		return this.labels[this.get(context).intValue()];
	}

	public String getLabel() {
		return this.labels[((Integer)this.get()).intValue()];
	}

	public void next(String context) {
		int tempvalue;
		for(tempvalue = this.get(context).intValue() + 1; tempvalue >= this.labels.length; tempvalue -= this.labels.length) {
		}

		this.set(tempvalue, context);
	}

	public void next() {
		this.next(ModSettings.currentContext);
	}

	public Object get(String string1) {
		return this.get(string1);
	}

	public void set(Object object1, String string2) {
		this.set((Integer)object1, string2);
	}
}
