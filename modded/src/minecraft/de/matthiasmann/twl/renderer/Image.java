package de.matthiasmann.twl.renderer;

import de.matthiasmann.twl.Color;

public interface Image {
	int getWidth();

	int getHeight();

	void draw(AnimationState animationState1, int i2, int i3);

	void draw(AnimationState animationState1, int i2, int i3, int i4, int i5);

	Image createTintedVersion(Color color1);
}
