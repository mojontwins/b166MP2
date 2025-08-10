package net.minecraft.src;

import de.matthiasmann.twl.EditField;
import de.matthiasmann.twl.Label;
import de.matthiasmann.twl.model.StringModel;

public class WidgetText extends WidgetSetting implements StringModel {
	public SettingText value;
	public EditField e;
	public Label l;
	public int setmode = 0;

	public WidgetText(SettingText setting, String title) {
		super(title);
		this.setTheme("");
		this.value = setting;
		this.value.gui = this;
		ModSettings.dbgout("0");
		this.e = new EditField();
		ModSettings.dbgout("1");
		ModSettings.dbgout("2");
		this.add(this.e);
		this.l = new Label();
		this.l.setText(String.format("%s: ", new Object[]{this.nicename}));
		this.add(this.l);
		this.e.setModel(this);
		this.update();
		ModSettings.dbgout("3");
	}

	public void layout() {
		this.l.setPosition(this.getX(), this.getY() + this.getHeight() / 2 - this.l.computeTextHeight() / 2);
		this.l.setSize(this.l.computeTextWidth(), this.l.computeTextHeight());
		this.e.setPosition(this.getX() + this.l.computeTextWidth(), this.getY());
		this.e.setSize(this.getWidth() - this.l.computeTextWidth(), this.getHeight());
	}

	public String userString() {
		return String.format("%s: %s", new Object[]{this.nicename, this.value.get(ModSettingScreen.guicontext)});
	}

	public void addCallback(Runnable callback) {
		ModSettings.dbgout("TextinputSetting.addcallback is a noop right now");
	}

	public String getValue() {
		return (String)this.value.get();
	}

	public void removeCallback(Runnable callback) {
		ModSettings.dbgout("TextinputSetting.removecallback is a noop right now");
	}

	public void setValue(String _value) {
		GuiModScreen.clicksound();
		ModSettings.dbgout(String.format("setvalue %s", new Object[]{this.e.getText()}));
		if(this.setmode <= 0) {
			this.setmode = -1;
			this.value.set(this.e.getText(), ModSettingScreen.guicontext);
			this.setmode = 0;
		}

	}

	public void update() {
		ModSettings.dbgout("update");
		this.l.setText(String.format("%s: ", new Object[]{this.nicename}));
		if(this.setmode >= 0) {
			this.setmode = 1;
			this.e.setText(this.value.get(ModSettingScreen.guicontext));
			this.setmode = 0;
		}

		ModSettings.dbgout(String.format("update %s", new Object[]{this.e.getText()}));
	}
}
