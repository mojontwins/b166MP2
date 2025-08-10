package net.minecraft.src;

import de.matthiasmann.twl.Widget;

import java.util.ArrayList;

public class WidgetSingleRow extends Widget {
	public int xSpacing = 3;
	public int defaultwidth = 150;
	public int defaultheight = 20;
	public boolean overridewidth = false;
	public boolean overrideheight = false;
	protected ArrayList wiggets = new ArrayList();
	protected ArrayList heights = new ArrayList();
	protected ArrayList widths = new ArrayList();

	public WidgetSingleRow(int defwidth, int defheight, Widget... widgets_) {
		this.setTheme("");
		this.defaultwidth = defwidth;
		this.defaultheight = defheight;

		for(int i = 0; i < widgets_.length; ++i) {
			this.add(widgets_[i]);
		}

	}

	public void add(Widget w) {
		this.add(w, this.defaultwidth, this.defaultheight);
	}

	public void add(Widget w, int width, int height) {
		this.wiggets.add(w);
		this.heights.add(height);
		this.widths.add(width);
		super.add(w);
	}

	public boolean removeChild(Widget w) {
		int idx = this.wiggets.indexOf(w);
		this.wiggets.remove(idx);
		this.heights.remove(idx);
		this.widths.remove(idx);
		return super.removeChild(w);
	}

	public Widget removeChild(int idx) {
		this.wiggets.remove(idx);
		this.heights.remove(idx);
		this.widths.remove(idx);
		return super.removeChild(idx);
	}

	private int getHeight(int idx) {
		return ((Integer)this.heights.get(idx)).intValue() >= 0 ? ((Integer)this.heights.get(idx)).intValue() : ((Widget)this.wiggets.get(idx)).getPreferredHeight();
	}

	private int getWidth(int idx) {
		return ((Integer)this.widths.get(idx)).intValue() >= 0 ? ((Integer)this.widths.get(idx)).intValue() : ((Widget)this.wiggets.get(idx)).getPreferredWidth();
	}

	public int getPreferredWidth() {
		int totalwidth = (this.widths.size() - 1) * this.xSpacing;
		totalwidth = totalwidth >= 0 ? totalwidth : 0;

		for(int i = 0; i < this.widths.size(); ++i) {
			totalwidth += this.getWidth(i);
		}

		return totalwidth;
	}

	public int getPreferredHeight() {
		int maxheights = 0;

		for(int i = 0; i < this.heights.size(); ++i) {
			if(this.getHeight(i) > maxheights) {
				maxheights = this.getHeight(i);
			}
		}

		return maxheights;
	}

	public void layout() {
		int curXpos = 0;

		for(int i = 0; i < this.wiggets.size(); ++i) {
			Widget w = (Widget)this.wiggets.get(i);
			w.setPosition(curXpos + this.getX(), this.getY());
			w.setSize(this.getWidth(i), this.getHeight(i));
			curXpos += this.getWidth(i) + this.xSpacing;
		}

	}
}
