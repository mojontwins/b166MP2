package net.minecraft.src;

import de.matthiasmann.twl.model.SimpleFloatModel;

public class WidgetInt extends WidgetSetting implements Runnable {
	public int displaylen;
	public String formatstring;
	public WidgetSlider s;
	public SettingInt value;

	public WidgetInt(SettingInt setting, String title) {
		this(setting, title, 15);
	}

	public WidgetInt(SettingInt setting, String title, int _displaylen) {
		super(title);
		this.setTheme("");
		this.displaylen = _displaylen;
		this.value = setting;
		this.value.gui = this;
		SimpleFloatModel smodel = new SimpleFloatModel((float)this.value.min, (float)this.value.max, (float)((Integer)this.value.get()).intValue());
		this.s = new WidgetSlider(smodel);
		this.s.setFormat(String.format("%s: %%.0f", new Object[]{this.nicename}));
		if(this.value.step > 1 && this.value.step <= this.value.max) {
			this.s.setStepSize((float)this.value.step);
		}

		smodel.addCallback(this);
		this.add(this.s);
		this.update();
	}

	public String userString() {
		String l = String.format("%d", new Object[]{this.displaylen});
		return String.format("%s: %." + l + "d", new Object[]{this.nicename, this.value.get(ModSettingScreen.guicontext)});
	}

	public void update() {
		this.s.setValue((float)this.value.get(ModSettingScreen.guicontext).intValue());
		this.s.setFormat(String.format("%s: %%.0f", new Object[]{this.nicename}));
		ModSettings.dbgout("update " + this.value.get(ModSettingScreen.guicontext) + " -> " + (int)this.s.getValue());
	}

	public void run() {
		ModSettings.dbgout("run " + (int)this.s.getValue());
		this.value.set((int)this.s.getValue(), ModSettingScreen.guicontext);
	}
}
