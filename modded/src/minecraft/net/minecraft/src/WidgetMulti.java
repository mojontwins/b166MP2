package net.minecraft.src;

import de.matthiasmann.twl.Button;
import de.matthiasmann.twl.model.SimpleButtonModel;

public class WidgetMulti extends WidgetSetting implements Runnable {
	public SettingMulti value;
	public Button b = null;

	public WidgetMulti(SettingMulti setting, String title) {
		super(title);
		this.setTheme("");
		this.value = setting;
		this.value.gui = this;
		SimpleButtonModel model = new SimpleButtonModel();
		this.b = new Button(model);
		model.addActionCallback(this);
		this.add(this.b);
		this.update();
	}

	public String userString() {
		return this.nicename.length() > 0 ? String.format("%s: %s", new Object[]{this.nicename, this.value.getLabel(ModSettingScreen.guicontext)}) : this.value.getLabel(ModSettingScreen.guicontext);
	}

	public void update() {
		this.b.setText(this.userString());
		ModSettings.dbgout("multi update " + this.userString());
	}

	public void run() {
		this.value.next(ModSettingScreen.guicontext);
		this.update();
		GuiModScreen.clicksound();
	}
}
