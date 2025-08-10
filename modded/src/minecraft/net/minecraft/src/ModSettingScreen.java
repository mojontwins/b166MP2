package net.minecraft.src;

import de.matthiasmann.twl.Widget;

import java.util.ArrayList;

public class ModSettingScreen {
	public static ArrayList modscreens = new ArrayList();
	public static String guicontext = "";
	public Widget thewidget;
	public WidgetClassicTwocolumn w;
	public String buttontitle;
	public String nicename;

	public ModSettingScreen(String name) {
		this(name, name);
	}

	public ModSettingScreen(String _nicename, String _buttontitle) {
		modscreens.add(this);
		this.buttontitle = _buttontitle;
		this.nicename = _nicename;
		this.w = new WidgetClassicTwocolumn(new Widget[0]);
		this.thewidget = new WidgetSimplewindow(this.w, this.nicename);
	}

	public ModSettingScreen(Widget _w, String _buttontitle) {
		modscreens.add(this);
		this.buttontitle = _buttontitle;
		this.thewidget = _w;
	}

	public void append(Widget newwidget) {
		if(this.w != null) {
			this.w.add(newwidget);
		} else {
			this.thewidget.add(newwidget);
		}

	}

	public void remove(Widget child) {
		if(this.w != null) {
			this.w.removeChild(child);
		} else {
			this.thewidget.removeChild(child);
		}

	}
}
