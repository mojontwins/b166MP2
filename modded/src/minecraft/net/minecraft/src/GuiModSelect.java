package net.minecraft.src;

import de.matthiasmann.twl.Button;
import de.matthiasmann.twl.Widget;
import de.matthiasmann.twl.model.SimpleButtonModel;

public class GuiModSelect extends GuiModScreen {
	protected GuiModSelect(GuiScreen by1) {
		super(by1);
		WidgetClassicTwocolumn w = new WidgetClassicTwocolumn(new Widget[0]);

		for(int i = 0; i < ModSettingScreen.modscreens.size(); ++i) {
			ModSettingScreen m = (ModSettingScreen)ModSettingScreen.modscreens.get(i);
			Button b = new Button(m.buttontitle);
			SimpleButtonModel z = new SimpleButtonModel();
			z.addActionCallback(new ModCallback(1, new Integer(i)));
			b.setModel(z);
			w.add(b);
		}

		this.mainwidget = new WidgetSimplewindow(w, "Select a Mod");
	}
}
