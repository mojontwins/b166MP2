package de.matthiasmann.twl.theme;

import de.matthiasmann.twl.Border;
import de.matthiasmann.twl.Color;
import de.matthiasmann.twl.renderer.AnimationState;
import de.matthiasmann.twl.renderer.Image;
import de.matthiasmann.twl.utils.StateExpression;

class StateSelectImage implements Image, HasBorder {
	private final Image[] images;
	private final StateExpression[] conditions;
	private final Border border;

	StateSelectImage(Image[] images, StateExpression[] conditions, Border border) {
		assert images.length >= conditions.length;

		assert images.length <= conditions.length + 1;

		this.images = images;
		this.conditions = conditions;
		this.border = border;
	}

	public int getWidth() {
		return this.images[0].getWidth();
	}

	public int getHeight() {
		return this.images[0].getHeight();
	}

	public void draw(AnimationState as, int x, int y) {
		this.draw(as, x, y, this.getWidth(), this.getHeight());
	}

	public void draw(AnimationState as, int x, int y, int width, int height) {
		int idx;
		for(idx = 0; idx < this.conditions.length && !this.conditions[idx].evaluate(as); ++idx) {
		}

		if(idx < this.images.length) {
			this.images[idx].draw(as, x, y, width, height);
		}

	}

	public Border getBorder() {
		return this.border;
	}

	public Image createTintedVersion(Color color) {
		Image[] newImages = new Image[this.images.length];

		for(int i = 0; i < newImages.length; ++i) {
			newImages[i] = this.images[i].createTintedVersion(color);
		}

		return new StateSelectImage(newImages, this.conditions, this.border);
	}
}
