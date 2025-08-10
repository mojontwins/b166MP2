package de.matthiasmann.twl.renderer;

import de.matthiasmann.twl.Rect;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.Map;

public interface Renderer {
	long getTimeMillis();

	boolean startRenderering();

	void endRendering();

	int getWidth();

	int getHeight();

	CacheContext createNewCacheContext();

	void setActiveCacheContext(CacheContext cacheContext1) throws IllegalStateException;

	CacheContext getActiveCacheContext();

	Font loadFont(URL uRL1, Map map2, Collection collection3) throws IOException;

	Texture loadTexture(URL uRL1, String string2, String string3) throws IOException;

	LineRenderer getLineRenderer();

	DynamicImage createDynamicImage(int i1, int i2);

	void setClipRect(Rect rect1);

	void setCursor(MouseCursor mouseCursor1);

	void setMousePosition(int i1, int i2);

	void setMouseButton(int i1, boolean z2);

	void pushGlobalTintColor(float f1, float f2, float f3, float f4);

	void popGlobalTintColor();
}
