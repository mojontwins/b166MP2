package net.minecraft.src;

public class SettingFloat extends Setting {
	public float step;
	public float min;
	public float max;

	public SettingFloat(String title) {
		this(title, 0.0F, 0.0F, 0.1F, 1.0F);
	}

	public SettingFloat(String title, float _value) {
		this(title, _value, 0.0F, 0.1F, 1.0F);
	}

	public SettingFloat(String title, float _value, float _min, float _max) {
		this(title, _value, _min, 0.1F, _max);
	}

	public SettingFloat(String title, float _value, float _min, float _step, float _max) {
		this.values.put("", _value);
		this.defvalue = _value;
		this.min = _min;
		this.step = _step;
		this.max = _max;
		this.backendname = title;
		if(this.min > this.max) {
			float t = this.min;
			this.min = this.max;
			this.max = t;
		}

	}

	public String toString(String context) {
		return "" + this.get(context);
	}

	public void fromString(String s, String context) {
		this.values.put(context, new Float(s));
		if(this.gui != null) {
			this.gui.update();
		}

	}

	public void set(Float v, String context) {
		if(this.step > 0.0F) {
			this.values.put(context, (float)Math.round(v.floatValue() / this.step) * this.step);
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

	public Float get(String context) {
		return this.values.get(context) != null ? (Float)this.values.get(context) : (this.values.get("") != null ? (Float)this.values.get("") : (Float)this.defvalue);
	}

	public Object get(String string1) {
		return this.get(string1);
	}

	public void set(Object object1, String string2) {
		this.set((Float)object1, string2);
	}
}
