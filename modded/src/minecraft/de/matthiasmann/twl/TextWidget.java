package de.matthiasmann.twl;

import de.matthiasmann.twl.renderer.Font;
import de.matthiasmann.twl.renderer.FontCache;
import de.matthiasmann.twl.utils.TextUtil;

public class TextWidget extends Widget {
	public static final de.matthiasmann.twl.renderer.AnimationState.StateKey STATE_HOVER = de.matthiasmann.twl.renderer.AnimationState.StateKey.get("hover");
	public static final de.matthiasmann.twl.renderer.AnimationState.StateKey STATE_TEXT_CHANGED = de.matthiasmann.twl.renderer.AnimationState.StateKey.get("textChanged");
	public static final de.matthiasmann.twl.renderer.AnimationState.StateKey STATE_TEXT_SELECTION = de.matthiasmann.twl.renderer.AnimationState.StateKey.get("textSelection");
	private static final int NOT_CACHED = -1;
	private Font font;
	private FontCache cache;
	private CharSequence text;
	private int cachedTextWidth;
	private int numTextLines;
	private boolean useCache;
	private boolean cacheDirty;
	private Alignment alignment;

	public TextWidget() {
		this((AnimationState)null, false);
	}

	public TextWidget(AnimationState animState) {
		this(animState, false);
	}

	public TextWidget(AnimationState animState, boolean inherit) {
		super(animState, inherit);
		this.cachedTextWidth = -1;
		this.useCache = true;
		this.alignment = Alignment.TOPLEFT;
		this.text = "";
	}

	public Font getFont() {
		return this.font;
	}

	public void setFont(Font font) {
		if(this.cache != null) {
			this.cache.destroy();
			this.cache = null;
		}

		this.font = font;
		this.cachedTextWidth = -1;
		if(this.useCache) {
			this.cacheDirty = true;
		}

	}

	protected void setCharSequence(CharSequence text) {
		if(text == null) {
			throw new NullPointerException("text");
		} else {
			this.text = text;
			this.cachedTextWidth = -1;
			this.numTextLines = TextUtil.countNumLines(text);
			this.cacheDirty = true;
			this.getAnimationState().resetAnimationTime(STATE_TEXT_CHANGED);
		}
	}

	protected CharSequence getCharSequence() {
		return this.text;
	}

	public boolean hasText() {
		return this.numTextLines > 0;
	}

	public boolean isMultilineText() {
		return this.numTextLines > 1;
	}

	public int getNumTextLines() {
		return this.numTextLines;
	}

	public Alignment getAlignment() {
		return this.alignment;
	}

	public void setAlignment(Alignment alignment) {
		if(alignment == null) {
			throw new NullPointerException("alignment");
		} else {
			if(this.alignment != alignment) {
				this.alignment = alignment;
				this.cacheDirty = true;
			}

		}
	}

	public boolean isCache() {
		return this.useCache;
	}

	public void setCache(boolean cache) {
		if(this.useCache != cache) {
			this.useCache = cache;
			this.cacheDirty = true;
		}

	}

	protected void applyThemeTextWidget(ThemeInfo themeInfo) {
		this.setFont(themeInfo.getFont("font"));
		this.setAlignment((Alignment)themeInfo.getParameter("textAlignment", Alignment.TOPLEFT));
	}

	protected void applyTheme(ThemeInfo themeInfo) {
		super.applyTheme(themeInfo);
		this.applyThemeTextWidget(themeInfo);
	}

	public void destroy() {
		if(this.cache != null) {
			this.cache.destroy();
			this.cache = null;
		}

		super.destroy();
	}

	protected int computeTextX() {
		int x = this.getInnerX();
		byte pos = this.alignment.hpos;
		return pos > 0 ? x + (this.getInnerWidth() - this.computeTextWidth()) * pos / 2 : x;
	}

	protected int computeTextY() {
		int y = this.getInnerY();
		byte pos = this.alignment.vpos;
		return pos > 0 ? y + (this.getInnerHeight() - this.computeTextHeight()) * pos / 2 : y;
	}

	protected void paintWidget(GUI gui) {
		this.paintLabelText(this.getAnimationState());
	}

	protected void paintLabelText(de.matthiasmann.twl.renderer.AnimationState animState) {
		if(this.cacheDirty) {
			this.updateCache();
		}

		if(this.hasText() && this.font != null) {
			int x = this.computeTextX();
			int y = this.computeTextY();
			if(this.cache != null) {
				this.cache.draw(animState, x, y);
			} else if(this.numTextLines > 1) {
				this.font.drawMultiLineText(animState, x, y, this.text, this.computeTextWidth(), this.alignment.fontHAlignment);
			} else {
				this.font.drawText(animState, x, y, this.text);
			}
		}

	}

	protected void paintWithSelection(AnimationState animState, int start, int end) {
		this.paintWithSelection(animState, start, end, 0, this.text.length(), this.computeTextY());
	}

	protected void paintWithSelection(AnimationState animState, int start, int end, int lineStart, int lineEnd, int y) {
		if(this.cacheDirty) {
			this.updateCache();
		}

		if(this.hasText() && this.font != null) {
			int x = this.computeTextX();
			start = limit(start, lineStart, lineEnd);
			end = limit(end, lineStart, lineEnd);
			if(start > lineStart) {
				x += this.font.drawText(animState, x, y, this.text, lineStart, start);
			}

			if(end > start) {
				animState.setAnimationState(STATE_TEXT_SELECTION, true);
				x += this.font.drawText(animState, x, y, this.text, start, end);
				animState.setAnimationState(STATE_TEXT_SELECTION, false);
			}

			if(end < lineEnd) {
				this.font.drawText(animState, x, y, this.text, end, lineEnd);
			}
		}

	}

	private static int limit(int value, int min, int max) {
		return value < min ? min : (value > max ? max : value);
	}

	public int getPreferredInnerWidth() {
		int prefWidth = super.getPreferredInnerWidth();
		if(this.hasText() && this.font != null) {
			prefWidth = Math.max(prefWidth, this.computeTextWidth());
		}

		return prefWidth;
	}

	public int getPreferredInnerHeight() {
		int prefHeight = super.getPreferredInnerHeight();
		if(this.hasText() && this.font != null) {
			prefHeight = Math.max(prefHeight, this.computeTextHeight());
		}

		return prefHeight;
	}

	public int computeRelativeCursorPositionX(int charIndex) {
		return this.computeRelativeCursorPositionX(0, charIndex);
	}

	public int computeRelativeCursorPositionX(int startIndex, int charIndex) {
		return this.font != null && charIndex > startIndex ? this.font.computeTextWidth(this.text, startIndex, charIndex) : 0;
	}

	public int computeTextWidth() {
		if(this.font == null) {
			return 0;
		} else {
			if(this.cachedTextWidth == -1 || this.cacheDirty) {
				if(this.numTextLines > 1) {
					this.cachedTextWidth = this.font.computeMultiLineTextWidth(this.text);
				} else {
					this.cachedTextWidth = this.font.computeTextWidth(this.text);
				}
			}

			return this.cachedTextWidth;
		}
	}

	public int computeTextHeight() {
		return this.font != null ? Math.max(1, this.numTextLines) * this.font.getLineHeight() : 0;
	}

	private void updateCache() {
		this.cacheDirty = false;
		if(this.useCache && this.hasText() && this.font != null) {
			if(this.numTextLines > 1) {
				this.cache = this.font.cacheMultiLineText(this.cache, this.text, this.font.computeMultiLineTextWidth(this.text), this.alignment.fontHAlignment);
			} else {
				this.cache = this.font.cacheText(this.cache, this.text);
			}

			if(this.cache != null) {
				this.cachedTextWidth = this.cache.getWidth();
			}
		} else {
			this.destroy();
		}

	}

	protected void handleMouseHover(Event evt) {
		if(evt.isMouseEvent() && !this.hasSharedAnimationState()) {
			this.getAnimationState().setAnimationState(STATE_HOVER, evt.getType() != Event.Type.MOUSE_EXITED);
		}

	}
}
