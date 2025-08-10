package de.matthiasmann.twl.theme;

import de.matthiasmann.twl.Color;
import de.matthiasmann.twl.renderer.AnimationState;
import de.matthiasmann.twl.renderer.Image;

public class EmptyImage implements Image {
	private final int width;
	private final int height;

	public EmptyImage(int width, int height) {
		this.width = width;
		this.height = height;
	}

	public void draw(AnimationState as, int x, int y) {
	}

	public void draw(AnimationState as, int x, int y, int width, int height) {
	}

	public int getWidth() {
		return this.width;
	}

	public int getHeight() {
		return this.height;
	}

	public Image createTintedVersion(Color color) {
		return this;
	}
}
