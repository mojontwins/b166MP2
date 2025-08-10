package de.matthiasmann.twl.renderer;

import de.matthiasmann.twl.Color;

public interface Texture extends Resource {
	int getWidth();

	int getHeight();

	Image getImage(int i1, int i2, int i3, int i4, Color color5, boolean z6);

	MouseCursor createCursor(int i1, int i2, int i3, int i4, int i5, int i6, Image image7);

	void themeLoadingDone();
}
