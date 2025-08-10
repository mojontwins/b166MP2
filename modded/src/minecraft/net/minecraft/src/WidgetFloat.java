package net.minecraft.src;

import de.matthiasmann.twl.model.SimpleFloatModel;

public class WidgetFloat extends WidgetSetting implements Runnable {
	public int displaylen;
	public String formatstring;
	public WidgetSlider slider;
	public SettingFloat value;

	public WidgetFloat(SettingFloat s, String title) {
		this(s, title, 2, "");
	}

	public WidgetFloat(SettingFloat s, String title, int _displaylen) {
		this(s, title, _displaylen, "");
	}

	public WidgetFloat(SettingFloat setting, String title, int _displaylen, String formatstr) {
		super(title);
		this.setTheme("");
		this.displaylen = _displaylen;
		this.formatstring = formatstr;
		this.value = setting;
		this.value.gui = this;
		SimpleFloatModel smodel = new SimpleFloatModel(this.value.min, this.value.max, ((Float)this.value.get()).floatValue());
		smodel.addCallback(this);
		this.slider = new WidgetSlider(smodel);
		if(this.value.step > 0.0F && this.value.step <= this.value.max) {
			this.slider.setStepSize(this.value.step);
		}

		this.slider.setFormat(String.format("%s: %%.%df", new Object[]{this.nicename, this.displaylen}));
		this.add(this.slider);
		this.update();
	}

	public String userString() {
		String l = String.format("%02d", new Object[]{this.displaylen});
		return String.format("%s: %." + l + "f", new Object[]{this.nicename, this.value});
	}

	public void update() {
		this.slider.setValue(this.value.get(ModSettingScreen.guicontext).floatValue());
		this.slider.setFormat(String.format("%s: %%.%df", new Object[]{this.nicename, this.displaylen}));
	}

	public void run() {
		this.value.set(this.slider.getValue(), ModSettingScreen.guicontext);
	}
}
