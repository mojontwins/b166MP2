package de.matthiasmann.twl.renderer.lwjgl;

import de.matthiasmann.twl.Color;
import de.matthiasmann.twl.Rect;
import de.matthiasmann.twl.renderer.AnimationState;
import de.matthiasmann.twl.renderer.CacheContext;
import de.matthiasmann.twl.renderer.DynamicImage;
import de.matthiasmann.twl.renderer.Font;
import de.matthiasmann.twl.renderer.LineRenderer;
import de.matthiasmann.twl.renderer.MouseCursor;
import de.matthiasmann.twl.renderer.Renderer;
import de.matthiasmann.twl.renderer.Texture;

import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Cursor;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;

public class LWJGLRenderer implements Renderer, LineRenderer {
	public static final AnimationState.StateKey STATE_LEFT_MOUSE_BUTTON = AnimationState.StateKey.get("leftMouseButton");
	public static final AnimationState.StateKey STATE_MIDDLE_MOUSE_BUTTON = AnimationState.StateKey.get("middleMouseButton");
	public static final AnimationState.StateKey STATE_RIGHT_MOUSE_BUTTON = AnimationState.StateKey.get("rightMouseButton");
	private final IntBuffer ib16 = BufferUtils.createIntBuffer(16);
	final int maxTextureSize;
	private int viewportX;
	private int viewportY;
	private int width;
	private int height;
	private boolean hasScissor;
	private final TintStack tintStateRoot = new TintStack();
	private final Cursor emptyCursor;
	private boolean useQuadsForLines;
	private boolean useSWMouseCursors;
	private SWCursor swCursor;
	private int mouseX;
	private int mouseY;
	private LWJGLCacheContext cacheContext;
	final LWJGLRenderer.SWCursorAnimState swCursorAnimState;
	final ArrayList textureAreas = new ArrayList();
	final ArrayList dynamicImages = new ArrayList();
	TintStack tintStack = this.tintStateRoot;

	public LWJGLRenderer() throws LWJGLException {
		this.syncViewportSize();
		GL11.glGetInteger(GL11.GL_MAX_TEXTURE_SIZE, this.ib16);
		this.maxTextureSize = this.ib16.get(0);
		if(Mouse.isCreated()) {
			int minCursorSize = Cursor.getMinCursorSize();
			IntBuffer tmp = BufferUtils.createIntBuffer(minCursorSize * minCursorSize);
			this.emptyCursor = new Cursor(minCursorSize, minCursorSize, minCursorSize / 2, minCursorSize / 2, 1, tmp, (IntBuffer)null);
		} else {
			this.emptyCursor = null;
		}

		this.swCursorAnimState = new LWJGLRenderer.SWCursorAnimState();
	}

	public boolean isUseQuadsForLines() {
		return this.useQuadsForLines;
	}

	public void setUseQuadsForLines(boolean useQuadsForLines) {
		this.useQuadsForLines = useQuadsForLines;
	}

	public boolean isUseSWMouseCursors() {
		return this.useSWMouseCursors;
	}

	public void setUseSWMouseCursors(boolean useSWMouseCursors) {
		this.useSWMouseCursors = useSWMouseCursors;
	}

	public CacheContext createNewCacheContext() {
		return new LWJGLCacheContext(this);
	}

	private LWJGLCacheContext activeCacheContext() {
		if(this.cacheContext == null) {
			this.setActiveCacheContext(this.createNewCacheContext());
		}

		return this.cacheContext;
	}

	public CacheContext getActiveCacheContext() {
		return this.activeCacheContext();
	}

	public void setActiveCacheContext(CacheContext cc) throws IllegalStateException {
		if(cc == null) {
			throw new NullPointerException();
		} else if(!cc.isValid()) {
			throw new IllegalStateException("CacheContext is invalid");
		} else if(!(cc instanceof LWJGLCacheContext)) {
			throw new IllegalArgumentException("CacheContext object not from this renderer");
		} else {
			LWJGLCacheContext lwjglCC = (LWJGLCacheContext)cc;
			if(lwjglCC.renderer != this) {
				throw new IllegalArgumentException("CacheContext object not from this renderer");
			} else {
				this.cacheContext = lwjglCC;

				try {
					Iterator iterator4 = this.textureAreas.iterator();

					while(iterator4.hasNext()) {
						TextureArea ta = (TextureArea)iterator4.next();
						ta.destroyRepeatCache();
					}
				} finally {
					this.textureAreas.clear();
				}

			}
		}
	}

	public void syncViewportSize() {
		this.ib16.clear();
		GL11.glGetInteger(GL11.GL_VIEWPORT, this.ib16);
		this.viewportX = this.ib16.get(0);
		this.viewportY = this.ib16.get(1);
		this.width = this.ib16.get(2);
		this.height = this.ib16.get(3);
	}

	public long getTimeMillis() {
		long res = Sys.getTimerResolution();
		long time = Sys.getTime();
		if(res != 1000L) {
			time = time * 1000L / res;
		}

		return time;
	}

	public boolean startRenderering() {
		if(this.width > 0 && this.height > 0) {
			this.hasScissor = false;
			this.tintStack = this.tintStateRoot;
			GL11.glPushAttrib(847876);
			GL11.glMatrixMode(GL11.GL_PROJECTION);
			GL11.glPushMatrix();
			GL11.glLoadIdentity();
			GL11.glOrtho(0.0D, (double)this.width, (double)this.height, 0.0D, -1.0D, 1.0D);
			GL11.glMatrixMode(GL11.GL_MODELVIEW);
			GL11.glPushMatrix();
			GL11.glLoadIdentity();
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glEnable(GL11.GL_LINE_SMOOTH);
			GL11.glDisable(GL11.GL_DEPTH_TEST);
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
			RenderScale.doscale();
			return true;
		} else {
			return false;
		}
	}

	public void endRendering() {
		if(this.swCursor != null) {
			this.tintStack = this.tintStateRoot;
			this.swCursor.render(this.mouseX, this.mouseY);
		}

		RenderScale.descale();
		GL11.glPopMatrix();
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glPopMatrix();
		GL11.glPopAttrib();
	}

	public int getHeight() {
		return this.height;
	}

	public int getWidth() {
		return this.width;
	}

	public Font loadFont(URL baseUrl, Map parameter, Collection conditionalParameter) throws IOException {
		String fileName = (String)parameter.get("filename");
		if(fileName == null) {
			throw new IllegalArgumentException("filename parameter required");
		} else {
			URL url = new URL(baseUrl, fileName);
			BitmapFont bmFont = this.activeCacheContext().loadBitmapFont(url);
			return new LWJGLFont(this, bmFont, parameter, conditionalParameter);
		}
	}

	public Texture loadTexture(URL url, String formatStr, String filterStr) throws IOException {
		LWJGLTexture.Format format = LWJGLTexture.Format.COLOR;
		LWJGLTexture.Filter filter = LWJGLTexture.Filter.NEAREST;
		if(formatStr != null) {
			try {
				format = LWJGLTexture.Format.valueOf(formatStr.toUpperCase(Locale.ENGLISH));
			} catch (IllegalArgumentException illegalArgumentException8) {
				this.getLogger().log(Level.WARNING, "Unknown texture format: {0}", formatStr);
			}
		}

		if(filterStr != null) {
			try {
				filter = LWJGLTexture.Filter.valueOf(filterStr.toUpperCase(Locale.ENGLISH));
			} catch (IllegalArgumentException illegalArgumentException7) {
				this.getLogger().log(Level.WARNING, "Unknown texture filter: {0}", filterStr);
			}
		}

		return this.load(url, format, filter);
	}

	public LineRenderer getLineRenderer() {
		return this;
	}

	public DynamicImage createDynamicImage(int width, int height) {
		if(width <= 0) {
			throw new IllegalArgumentException("width");
		} else if(height <= 0) {
			throw new IllegalArgumentException("height");
		} else if(width <= this.maxTextureSize && height <= this.maxTextureSize) {
			ContextCapabilities caps = GLContext.getCapabilities();
			boolean useTextureRectangle = caps.GL_EXT_texture_rectangle || caps.GL_ARB_texture_rectangle;
			if(useTextureRectangle || caps.GL_ARB_texture_non_power_of_two || (width & width - 1) == 0 && (height & height - 1) == 0) {
				int proxyTarget = useTextureRectangle ? 34039 : 32868;
				GL11.glTexImage2D(proxyTarget, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, (ByteBuffer)null);
				this.ib16.clear();
				GL11.glGetTexLevelParameter(proxyTarget, 0, GL11.GL_TEXTURE_WIDTH, this.ib16);
				if(this.ib16.get(0) != width) {
					return null;
				} else {
					int target = useTextureRectangle ? 34037 : 3553;
					int id = this.glGenTexture();
					GL11.glBindTexture(target, id);
					GL11.glTexImage2D(target, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, (ByteBuffer)null);
					GL11.glTexParameteri(target, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
					GL11.glTexParameteri(target, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
					LWJGLDynamicImage image = new LWJGLDynamicImage(this, target, id, width, height, Color.WHITE);
					this.dynamicImages.add(image);
					return image;
				}
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	public void setClipRect(Rect rect) {
		if(rect == null) {
			GL11.glDisable(GL11.GL_SCISSOR_TEST);
			this.hasScissor = false;
		} else {
			GL11.glScissor(this.viewportX + rect.getX() * RenderScale.scale, this.viewportY + this.getHeight() - rect.getBottom() * RenderScale.scale, rect.getWidth() * RenderScale.scale, rect.getHeight() * RenderScale.scale);
			if(!this.hasScissor) {
				GL11.glEnable(GL11.GL_SCISSOR_TEST);
				this.hasScissor = true;
			}
		}

	}

	public void setCursor(MouseCursor cursor) {
		try {
			if(Mouse.isInsideWindow()) {
				this.swCursor = null;
				if(cursor instanceof LWJGLCursor) {
					Mouse.setNativeCursor(((LWJGLCursor)cursor).cursor);
				} else if(cursor instanceof SWCursor) {
					Mouse.setNativeCursor(this.emptyCursor);
					this.swCursor = (SWCursor)cursor;
				} else {
					Mouse.setNativeCursor((Cursor)null);
				}
			}
		} catch (LWJGLException lWJGLException3) {
			Logger.getLogger(LWJGLRenderer.class.getName()).log(Level.WARNING, "Could not set native cursor", lWJGLException3);
		}

	}

	public void setMousePosition(int mouseX, int mouseY) {
		this.mouseX = mouseX;
		this.mouseY = mouseY;
	}

	public void setMouseButton(int button, boolean state) {
		this.swCursorAnimState.setAnimationState(button, state);
	}

	public LWJGLTexture load(URL textureUrl, LWJGLTexture.Format fmt, LWJGLTexture.Filter filter) throws IOException {
		return this.load(textureUrl, fmt, filter, (TexturePostProcessing)null);
	}

	public LWJGLTexture load(URL textureUrl, LWJGLTexture.Format fmt, LWJGLTexture.Filter filter, TexturePostProcessing tpp) throws IOException {
		if(textureUrl == null) {
			throw new NullPointerException("textureUrl");
		} else {
			LWJGLCacheContext cc = this.activeCacheContext();
			return tpp != null ? cc.createTexture(textureUrl, fmt, filter, tpp) : cc.loadTexture(textureUrl, fmt, filter);
		}
	}

	public void pushGlobalTintColor(float r, float g, float b, float a) {
		this.tintStack = this.tintStack.push(r, g, b, a);
	}

	public void popGlobalTintColor() {
		this.tintStack = this.tintStack.pop();
	}

	public void setColor(Color color) {
		this.tintStack.setColor(color);
	}

	public void drawLine(float[] pts, int numPts, float width, Color color, boolean drawAsLoop) {
		if(numPts * 2 > pts.length) {
			throw new ArrayIndexOutOfBoundsException(numPts * 2);
		} else {
			if(numPts >= 2) {
				this.tintStack.setColor(color);
				GL11.glDisable(GL11.GL_TEXTURE_2D);
				if(this.useQuadsForLines) {
					this.drawLinesAsQuads(numPts, pts, width, drawAsLoop);
				} else {
					this.drawLinesAsLines(numPts, pts, width, drawAsLoop);
				}

				GL11.glEnable(GL11.GL_TEXTURE_2D);
			}

		}
	}

	private void drawLinesAsLines(int numPts, float[] pts, float width, boolean drawAsLoop) {
		GL11.glLineWidth(width);
		GL11.glBegin(drawAsLoop ? GL11.GL_LINE_LOOP : GL11.GL_LINE_STRIP);

		for(int i = 0; i < numPts; ++i) {
			GL11.glVertex2f(pts[i * 2 + 0], pts[i * 2 + 1]);
		}

		GL11.glEnd();
	}

	private void drawLinesAsQuads(int numPts, float[] pts, float width, boolean drawAsLoop) {
		width *= 0.5F;
		GL11.glBegin(GL11.GL_QUADS);

		int idx;
		for(idx = 1; idx < numPts; ++idx) {
			drawLineAsQuad(pts[idx * 2 - 2], pts[idx * 2 - 1], pts[idx * 2 + 0], pts[idx * 2 + 1], width);
		}

		if(drawAsLoop) {
			idx = numPts * 2;
			drawLineAsQuad(pts[idx], pts[idx + 1], pts[0], pts[1], width);
		}

		GL11.glEnd();
	}

	private static void drawLineAsQuad(float x0, float y0, float x1, float y1, float w) {
		float dx = x1 - x0;
		float dy = y1 - y0;
		float l = (float)Math.sqrt((double)(dx * dx + dy * dy)) / w;
		dx /= l;
		dy /= l;
		GL11.glVertex2f(x0 - dx + dy, y0 - dy - dx);
		GL11.glVertex2f(x0 - dx - dy, y0 - dy + dx);
		GL11.glVertex2f(x1 + dx - dy, y1 + dy + dx);
		GL11.glVertex2f(x1 + dx + dy, y1 + dy - dx);
	}

	protected void getTintedColor(Color color, float[] result) {
		result[0] = this.tintStack.r * (float)(color.getR() & 255);
		result[1] = this.tintStack.g * (float)(color.getG() & 255);
		result[2] = this.tintStack.b * (float)(color.getB() & 255);
		result[3] = this.tintStack.a * (float)(color.getA() & 255);
	}

	Logger getLogger() {
		return Logger.getLogger(LWJGLRenderer.class.getName());
	}

	int glGenTexture() {
		this.ib16.clear().limit(1);
		GL11.glGenTextures(this.ib16);
		return this.ib16.get(0);
	}

	void glDeleteTexture(int id) {
		this.ib16.clear();
		this.ib16.put(id).flip();
		GL11.glDeleteTextures(this.ib16);
	}

	private static class SWCursorAnimState implements AnimationState {
		private final long[] lastTime = new long[3];
		private final boolean[] active = new boolean[3];

		void setAnimationState(int idx, boolean isActive) {
			if(idx >= 0 && idx < 3 && this.active[idx] != isActive) {
				this.lastTime[idx] = Sys.getTime();
				this.active[idx] = isActive;
			}

		}

		public int getAnimationTime(AnimationState.StateKey state) {
			long curTime = Sys.getTime();
			int idx = this.getMouseButton(state);
			if(idx >= 0) {
				curTime -= this.lastTime[idx];
			}

			return (int)curTime & Integer.MAX_VALUE;
		}

		public boolean getAnimationState(AnimationState.StateKey state) {
			int idx = this.getMouseButton(state);
			return idx >= 0 ? this.active[idx] : false;
		}

		public boolean getShouldAnimateState(AnimationState.StateKey state) {
			return true;
		}

		private int getMouseButton(AnimationState.StateKey key) {
			return key == LWJGLRenderer.STATE_LEFT_MOUSE_BUTTON ? 0 : (key == LWJGLRenderer.STATE_MIDDLE_MOUSE_BUTTON ? 2 : (key == LWJGLRenderer.STATE_RIGHT_MOUSE_BUTTON ? 1 : -1));
		}
	}
}
