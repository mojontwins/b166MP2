package net.minecraft.src;

import de.matthiasmann.twl.Widget;

public class WidgetClassicTwocolumn extends Widget {
	public int defaultwidth = 150;
	public int defaultheight = 20;
	public int defaultpad = 4;
	public boolean overridewidth = true;
	public boolean overrideheight = true;
	public int split = 10;

	public WidgetClassicTwocolumn(Widget... ws) {
		for(int i = 0; i < ws.length; ++i) {
			this.add(ws[i]);
		}

		this.setTheme("");
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
			if(i % 2 == 0) {
				w.setPosition(this.getX() + this.getWidth() / 2 - (150 + this.split / 2), this.getY() + (this.defaultheight + this.defaultpad) * (i >> 1));
			} else {
				w.setPosition(this.getX() + this.getWidth() / 2 + this.split / 2, this.getY() + (this.defaultheight + this.defaultpad) * (i >> 1));
			}
		}

	}

	public int getPreferredWidth() {
		return this.getParent().getWidth();
	}

	public int getPreferredHeight() {
		return (this.defaultheight + this.defaultpad) * (1 * (this.getNumChildren() + 1) >> 1);
	}
}
