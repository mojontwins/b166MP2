package net.minecraft.src;

import de.matthiasmann.twl.Event;
import de.matthiasmann.twl.ToggleButton;
import de.matthiasmann.twl.model.SimpleBooleanModel;

import org.lwjgl.input.Keyboard;

public class WidgetKeybinding extends WidgetSetting implements Runnable {
	public SettingKey value;
	public SimpleBooleanModel bmodel;
	public ToggleButton b;
	public int CLEARKEY = 211;
	public int NEVERMINDKEY = 1;

	public WidgetKeybinding(SettingKey setting, String title) {
		super(title);
		this.setTheme("");
		this.value = setting;
		this.value.gui = this;
		this.bmodel = new SimpleBooleanModel(false);
		this.b = new ToggleButton(this.bmodel);
		this.add(this.b);
		this.update();
	}

	public boolean handleEvent(Event evt) {
		if(evt.isKeyEvent() && !evt.isKeyPressedEvent() && this.bmodel.getValue()) {
			System.out.println(Keyboard.getKeyName(evt.getKeyCode()));
			int tmpvalue = evt.getKeyCode();
			if(tmpvalue == this.CLEARKEY) {
				this.value.set(0, ModSettingScreen.guicontext);
			} else if(tmpvalue != this.NEVERMINDKEY) {
				this.value.set(tmpvalue, ModSettingScreen.guicontext);
			}

			this.bmodel.setValue(false);
			this.update();
			GuiModScreen.clicksound();
			return true;
		} else {
			return false;
		}
	}

	public void keyboardFocusLost() {
		GuiModScreen.clicksound();
		this.bmodel.setValue(false);
	}

	public String userString() {
		return String.format("%s: %s", new Object[]{this.nicename, Keyboard.getKeyName(this.value.get(ModSettingScreen.guicontext).intValue())});
	}

	public void update() {
		this.b.setText(this.userString());
	}

	public void run() {
		GuiModScreen.clicksound();
	}
}
