package de.matthiasmann.twl.theme;

import de.matthiasmann.twl.Border;
import de.matthiasmann.twl.Color;
import de.matthiasmann.twl.renderer.AnimationState;
import de.matthiasmann.twl.renderer.Image;

class ComposedImage implements Image, HasBorder {
	private final Image[] layers;
	private final Border border;

	public ComposedImage(Image[] layers, Border border) {
		this.layers = layers;
		this.border = border;
	}

	public void draw(AnimationState as, int x, int y) {
		this.draw(as, x, y, this.getWidth(), this.getHeight());
	}

	public void draw(AnimationState as, int x, int y, int width, int height) {
		Image[] image9 = this.layers;
		int i8 = this.layers.length;

		for(int i7 = 0; i7 < i8; ++i7) {
			Image layer = image9[i7];
			layer.draw(as, x, y, width, height);
		}

	}

	public int getHeight() {
		return this.layers[0].getHeight();
	}

	public int getWidth() {
		return this.layers[0].getWidth();
	}

	public Border getBorder() {
		return this.border;
	}

	public Image createTintedVersion(Color color) {
		Image[] newLayers = new Image[this.layers.length];

		for(int i = 0; i < newLayers.length; ++i) {
			newLayers[i] = this.layers[i].createTintedVersion(color);
		}

		return new ComposedImage(newLayers, this.border);
	}
}
