package de.matthiasmann.twl.renderer;

public interface FontCache extends Resource {
	int getWidth();

	int getHeight();

	void draw(AnimationState animationState1, int i2, int i3);
}
