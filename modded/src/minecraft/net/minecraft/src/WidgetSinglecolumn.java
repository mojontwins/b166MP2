package net.minecraft.src;

import de.matthiasmann.twl.Widget;

public class WidgetSinglecolumn extends WidgetClassicTwocolumn {
	public WidgetSinglecolumn(Widget... w) {
		super(w);
		this.defaultwidth = 200;
	}

	public void layout() {
		for(int i = 0; i < this.getNumChildren(); ++i) {
			Widget w = this.getChild(i);
			int height = this.defaultheight;
			if(!this.overrideheight) {
				height = w.getPreferredHeight();
			}

			int width = this.defaultwidth;
			if(!this.overridewidth) {
				width = w.getPreferredWidth();
			}

			w.setSize(width, height);
			w.setPosition(this.getX() + this.getWidth() / 2 - w.getWidth() / 2, this.getY() + 24 * i);
		}

	}

	public int getPreferredWidth() {
		return Math.max(this.getParent().getWidth(), this.defaultwidth);
	}

	public int getPreferredHeight() {
		return 24 * this.getNumChildren() + 1;
	}
}
