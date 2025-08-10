package net.minecraft.src;

import de.matthiasmann.twl.Button;
import de.matthiasmann.twl.model.SimpleButtonModel;

public class WidgetBoolean extends WidgetSetting implements Runnable {
	public String ttext;
	public String ftext;
	public Button b;
	public SettingBoolean value;

	public WidgetBoolean(SettingBoolean setting, String title) {
		this(setting, title, "true", "false");
	}

	public WidgetBoolean(SettingBoolean setting, String title, String _ttext, String _ftext) {
		super(title);
		this.value = null;
		this.setTheme("");
		this.ttext = _ttext;
		this.ftext = _ftext;
		SimpleButtonModel bmodel = new SimpleButtonModel();
		this.b = new Button(bmodel);
		bmodel.addActionCallback(this);
		this.add(this.b);
		this.value = setting;
		this.value.gui = this;
		this.update();
	}

	public String userString() {
		return this.value != null ? (this.nicename.length() > 0 ? String.format("%s: %s", new Object[]{this.nicename, this.value.get(ModSettingScreen.guicontext).booleanValue() ? this.ttext : this.ftext}) : (this.value.get(ModSettingScreen.guicontext).booleanValue() ? this.ttext : this.ftext)) : (this.nicename.length() > 0 ? String.format("%s: %s", new Object[]{this.nicename, "no value"}) : "no value or title");
	}

	public void update() {
		this.b.setText(this.userString());
	}

	public void run() {
		if(this.value != null) {
			this.value.set(!this.value.get(ModSettingScreen.guicontext).booleanValue(), ModSettingScreen.guicontext);
		}

		this.update();
		GuiModScreen.clicksound();
	}
}
