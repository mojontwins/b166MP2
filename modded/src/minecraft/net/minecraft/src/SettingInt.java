package net.minecraft.src;

public class SettingInt extends Setting {
	public int step;
	public int min;
	public int max;

	public SettingInt(String title) {
		this(title, 0, 0, 1, 100);
	}

	public SettingInt(String title, int _value) {
		this(title, _value, 0, 1, 100);
	}

	public SettingInt(String title, int _value, int _min, int _max) {
		this(title, _value, _min, 1, _max);
	}

	public SettingInt(String title, int _value, int _min, int _step, int _max) {
		this.values.put("", _value);
		this.defvalue = _value;
		this.min = _min;
		this.step = _step;
		this.max = _max;
		this.backendname = title;
		if(this.min > this.max) {
			int t = this.min;
			this.min = this.max;
			this.max = t;
		}

	}

	public String toString(String context) {
		return "" + this.get(context);
	}

	public void fromString(String s, String context) {
		this.values.put(context, new Integer(s));
		if(this.gui != null) {
			this.gui.update();
		}

		ModSettings.dbgout("fromstring " + s);
	}

	public void set(Integer v, String context) {
		ModSettings.dbgout("set " + v);
		if(this.step > 1) {
			this.values.put(context, (int)((float)Math.round((float)v.intValue() / (float)this.step) * (float)this.step));
		} else {
			this.values.put(context, v);
		}

		if(this.parent != null) {
			this.parent.save(context);
		}

		if(this.gui != null) {
			this.gui.update();
		}

	}

	public Integer get(String context) {
		return this.values.get(context) != null ? (Integer)this.values.get(context) : (this.values.get("") != null ? (Integer)this.values.get("") : (Integer)this.defvalue);
	}

	public Object get(String string1) {
		return this.get(string1);
	}

	public void set(Object object1, String string2) {
		this.set((Integer)object1, string2);
	}
}
