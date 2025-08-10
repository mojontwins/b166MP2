package net.minecraft.src;

import de.matthiasmann.twl.Widget;

import java.util.ArrayList;

public abstract class WidgetSetting extends Widget {
	public String nicename;
	public static ArrayList all = new ArrayList();

	public WidgetSetting(String _nicename) {
		this.nicename = _nicename;
		all.add(this);
	}

	public void add(Widget child) {
		String T = child.getTheme();
		if(T.length() == 0) {
			child.setTheme("/-defaults");
		} else if(!T.substring(0, 1).equals("/")) {
			child.setTheme("/" + T);
		}

		super.add(child);
	}

	public void layout() {
		for(int i = 0; i < this.getNumChildren(); ++i) {
			Widget w = this.getChild(i);
			w.setPosition(this.getX(), this.getY());
			w.setSize(this.getWidth(), this.getHeight());
		}

	}

	public static void updateAll() {
		for(int i = 0; i < all.size(); ++i) {
			((WidgetSetting)all.get(i)).update();
		}

	}

	public abstract void update();
}
