package net.minecraft.src;

import de.matthiasmann.twl.Widget;

import java.util.HashMap;

public abstract class Setting extends Widget {
	public HashMap values = new HashMap();
	public Object defvalue;
	public String backendname;
	public WidgetSetting gui = null;
	public ModSettings parent = null;

	public abstract String toString(String string1);

	public abstract void fromString(String string1, String string2);

	public void reset() {
		this.reset(ModSettings.currentContext);
	}

	public void reset(String context) {
		this.set(this.defvalue, context);
	}

	public void copyContext(String srccontext, String destcontext) {
		this.values.put(destcontext, this.values.get(srccontext));
	}

	public abstract void set(Object object1, String string2);

	public void set(Object v) {
		this.set(v, ModSettings.currentContext);
	}

	public abstract Object get(String string1);

	public Object get() {
		return this.get(ModSettings.currentContext);
	}
}
