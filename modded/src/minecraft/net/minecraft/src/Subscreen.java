package net.minecraft.src;

import de.matthiasmann.twl.Button;
import de.matthiasmann.twl.Widget;
import de.matthiasmann.twl.model.SimpleButtonModel;

import java.util.ArrayList;

public class Subscreen extends Button implements Runnable {
	public ArrayList children = new ArrayList();
	public Widget subscreenwindow;
	public Widget subsubWindow;

	public Subscreen(String menutitle, String buttontitle) {
		super(buttontitle);
		this.setTheme("/button");
		this.subsubWindow = new WidgetClassicTwocolumn(new Widget[0]);
		this.subscreenwindow = new WidgetSimplewindow(this.subsubWindow, menutitle);
		SimpleButtonModel s = new SimpleButtonModel();
		s.addActionCallback(this);
		this.setModel(s);
	}

	public Subscreen(String menutitle, String buttontitle, Widget subwidget) {
		super(buttontitle);
		this.setTheme("/button");
		this.subsubWindow = subwidget;
		this.subscreenwindow = new WidgetSimplewindow(this.subsubWindow, menutitle);
		SimpleButtonModel s = new SimpleButtonModel();
		s.addActionCallback(this);
		this.setModel(s);
	}

	public Subscreen(String buttontitle, Widget subwidget) {
		super(buttontitle);
		this.setTheme("/button");
		this.subsubWindow = subwidget;
		this.subscreenwindow = subwidget;
		SimpleButtonModel s = new SimpleButtonModel();
		s.addActionCallback(this);
		this.setModel(s);
	}

	public void add(Widget w) {
		this.subsubWindow.add(w);
	}

	public void run() {
		GuiModScreen.show(this.subscreenwindow);
	}
}
