package de.matthiasmann.twl.theme;

import de.matthiasmann.twl.Border;
import de.matthiasmann.twl.Color;
import de.matthiasmann.twl.renderer.AnimationState;
import de.matthiasmann.twl.renderer.Image;
import de.matthiasmann.twl.utils.StateExpression;

class ImageAdjustments implements Image, HasBorder {
	final Image image;
	final Border border;
	final Border inset;
	final int sizeOverwriteH;
	final int sizeOverwriteV;
	final boolean center;
	final StateExpression condition;

	ImageAdjustments(Image image, Border border, Border inset, int sizeOverwriteH, int sizeOverwriteV, boolean center, StateExpression condition) {
		this.image = image;
		this.border = border;
		this.inset = inset;
		this.sizeOverwriteH = sizeOverwriteH;
		this.sizeOverwriteV = sizeOverwriteV;
		this.center = center;
		this.condition = condition;
	}

	public int getWidth() {
		return this.sizeOverwriteH >= 0 ? this.sizeOverwriteH : (this.inset != null ? this.image.getWidth() + this.inset.getBorderLeft() + this.inset.getBorderRight() : this.image.getWidth());
	}

	public int getHeight() {
		return this.sizeOverwriteV >= 0 ? this.sizeOverwriteV : (this.inset != null ? this.image.getHeight() + this.inset.getBorderTop() + this.inset.getBorderBottom() : this.image.getHeight());
	}

	public void draw(AnimationState as, int x, int y, int width, int height) {
		if(this.condition == null || this.condition.evaluate(as)) {
			if(this.inset != null) {
				x += this.inset.getBorderLeft();
				y += this.inset.getBorderTop();
				width = Math.max(0, width - this.inset.getBorderLeft() - this.inset.getBorderRight());
				height = Math.max(0, height - this.inset.getBorderTop() - this.inset.getBorderBottom());
			}

			if(this.center) {
				int w = Math.min(width, this.image.getWidth());
				int h = Math.min(height, this.image.getHeight());
				x += (width - w) / 2;
				y += (height - h) / 2;
				width = w;
				height = h;
			}

			this.image.draw(as, x, y, width, height);
		}

	}

	public void draw(AnimationState as, int x, int y) {
		this.draw(as, x, y, this.image.getWidth(), this.image.getHeight());
	}

	public Border getBorder() {
		return this.border;
	}

	public Image createTintedVersion(Color color) {
		return new ImageAdjustments(this.image.createTintedVersion(color), this.border, this.inset, this.sizeOverwriteH, this.sizeOverwriteV, this.center, this.condition);
	}
}
