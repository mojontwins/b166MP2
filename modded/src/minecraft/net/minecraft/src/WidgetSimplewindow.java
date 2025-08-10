package net.minecraft.src;

import de.matthiasmann.twl.Button;
import de.matthiasmann.twl.Label;
import de.matthiasmann.twl.ScrollPane;
import de.matthiasmann.twl.Widget;
import de.matthiasmann.twl.model.SimpleButtonModel;

public class WidgetSimplewindow extends Widget {
	public String Title = "";
	public Widget mainwidget = null;
	public Label TitleWidget = null;
	public Button BackButton = null;
	public WidgetSingleRow buttonbar = null;

	protected WidgetSimplewindow() {
		this.Title = "test";
		this.setTheme("");
		this.init();
	}

	public WidgetSimplewindow(Widget w) {
		ScrollPane mainwidget_ = new ScrollPane(w);
		mainwidget_.setFixed(ScrollPane.Fixed.HORIZONTAL);
		this.mainwidget = mainwidget_;
		this.Title = "test";
		this.setTheme("");
		this.init();
	}

	public WidgetSimplewindow(Widget w, String s) {
		ScrollPane mainwidget_ = new ScrollPane(w);
		mainwidget_.setFixed(ScrollPane.Fixed.HORIZONTAL);
		this.mainwidget = mainwidget_;
		this.Title = s;
		this.setTheme("");
		this.init();
	}

	protected void init() {
		this.TitleWidget = new Label(this.Title);
		this.add(this.TitleWidget);
		this.BackButton = new Button(new SimpleButtonModel());
		this.BackButton.getModel().addActionCallback(new ModCallback(0, (Object)null));
		this.BackButton.setText("Back");
		this.buttonbar = new WidgetSingleRow(200, 20, new Widget[]{this.BackButton});
		this.add(this.buttonbar);
		this.add(this.mainwidget);
	}

	public void layout() {
		byte s = 1;
		this.buttonbar.setSize(this.buttonbar.getPreferredWidth(), this.buttonbar.getPreferredHeight());
		this.buttonbar.setPosition(GuiWidgetScreen.screenwidth / 2 - this.buttonbar.getPreferredWidth() / 2, GuiWidgetScreen.screenheight - (this.buttonbar.getPreferredHeight() + 4));
		this.TitleWidget.setPosition(GuiWidgetScreen.screenwidth / 2 - this.TitleWidget.computeTextWidth() / 2, 10 * s);
		this.TitleWidget.setSize(this.TitleWidget.computeTextWidth(), this.TitleWidget.computeTextHeight());
		int hpad = 30 * s;
		int vpad = 40 * s;
		this.mainwidget.setPosition(hpad, vpad);
		this.mainwidget.setSize(GuiWidgetScreen.screenwidth - hpad * 2, GuiWidgetScreen.screenheight - vpad * 2);
	}
}
