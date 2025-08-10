package de.matthiasmann.twl.theme;

import de.matthiasmann.twl.Border;
import de.matthiasmann.twl.Color;
import de.matthiasmann.twl.renderer.AnimationState;
import de.matthiasmann.twl.renderer.Image;
import de.matthiasmann.twl.renderer.SupportsDrawRepeat;

class RepeatImage implements Image, HasBorder, SupportsDrawRepeat {
	private final Image base;
	private final Border border;
	private final boolean repeatX;
	private final boolean repeatY;
	private final SupportsDrawRepeat sdr;

	RepeatImage(Image base, Border border, boolean repeatX, boolean repeatY) {
		assert repeatX || repeatY;

		this.base = base;
		this.border = border;
		this.repeatX = repeatX;
		this.repeatY = repeatY;
		if(base instanceof SupportsDrawRepeat) {
			this.sdr = (SupportsDrawRepeat)base;
		} else {
			this.sdr = this;
		}

	}

	public int getWidth() {
		return this.base.getWidth();
	}

	public int getHeight() {
		return this.base.getHeight();
	}

	public void draw(AnimationState as, int x, int y) {
		this.base.draw(as, x, y);
	}

	public void draw(AnimationState as, int x, int y, int width, int height) {
		int countX = this.repeatX ? Math.max(1, width / this.base.getWidth()) : 1;
		int countY = this.repeatY ? Math.max(1, height / this.base.getHeight()) : 1;
		this.sdr.draw(as, x, y, width, height, countX, countY);
	}

	public void draw(AnimationState as, int x, int y, int width, int height, int repeatCountX, int repeatCountY) {
		while(repeatCountY > 0) {
			int rowHeight = height / repeatCountY;
			int cx = 0;

			int nx;
			for(int xi = 0; xi < repeatCountX; cx = nx) {
				++xi;
				nx = xi * width / repeatCountX;
				this.base.draw(as, x + cx, y, nx - cx, rowHeight);
			}

			y += rowHeight;
			height -= rowHeight;
			--repeatCountY;
		}

	}

	public Border getBorder() {
		return this.border;
	}

	public Image createTintedVersion(Color color) {
		return new RepeatImage(this.base.createTintedVersion(color), this.border, this.repeatX, this.repeatY);
	}
}
