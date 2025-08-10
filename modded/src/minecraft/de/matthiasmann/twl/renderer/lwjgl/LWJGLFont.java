package de.matthiasmann.twl.renderer.lwjgl;

import de.matthiasmann.twl.Color;
import de.matthiasmann.twl.HAlignment;
import de.matthiasmann.twl.renderer.AnimationState;
import de.matthiasmann.twl.renderer.Font;
import de.matthiasmann.twl.renderer.FontCache;
import de.matthiasmann.twl.renderer.FontParameter;
import de.matthiasmann.twl.utils.StateExpression;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class LWJGLFont implements Font {
	static final int STYLE_UNDERLINE = 1;
	static final int STYLE_LINETHROUGH = 2;
	private final LWJGLRenderer renderer;
	private final BitmapFont font;
	private final LWJGLFont.FontState[] fontStates;
	private int[] multiLineInfo;

	LWJGLFont(LWJGLRenderer renderer, BitmapFont font, Map params, Collection condParams) {
		this.renderer = renderer;
		this.font = font;
		ArrayList states = new ArrayList();
		Iterator iterator7 = condParams.iterator();

		while(iterator7.hasNext()) {
			FontParameter p = (FontParameter)iterator7.next();
			HashMap effective = new HashMap(params);
			effective.putAll(p.getParams());
			states.add(this.createFontState(p.getCondition(), effective));
		}

		states.add(this.createFontState((StateExpression)null, params));
		this.fontStates = (LWJGLFont.FontState[])states.toArray(new LWJGLFont.FontState[states.size()]);
	}

	private LWJGLFont.FontState createFontState(StateExpression cond, Map params) {
		String colorStr = (String)params.get("color");
		if(colorStr == null) {
			throw new IllegalArgumentException("color needs to be defined");
		} else {
			int offsetX = parseInt((String)params.get("offsetX"), 0);
			int offsetY = parseInt((String)params.get("offsetY"), 0);
			int style = 0;
			int underlineOffset = parseInt((String)params.get("underlineOffset"), 0);
			Color color = Color.parserColor(colorStr);
			if(color == null) {
				throw new IllegalArgumentException("unknown color name: " + colorStr);
			} else {
				if(parseBoolean((String)params.get("underline"))) {
					style |= 1;
				}

				if(parseBoolean((String)params.get("linethrough"))) {
					style |= 2;
				}

				LWJGLFont.FontState p = new LWJGLFont.FontState(cond, color, offsetX, offsetY, style, underlineOffset);
				return p;
			}
		}
	}

	private static int parseInt(String valueStr, int defaultValue) {
		return valueStr == null ? defaultValue : Integer.parseInt(valueStr);
	}

	private static boolean parseBoolean(String valueStr) {
		return valueStr == null ? false : Boolean.parseBoolean(valueStr);
	}

	LWJGLFont.FontState evalFontState(AnimationState as) {
		int i = 0;

		for(int n = this.fontStates.length - 1; i < n && !this.fontStates[i].condition.evaluate(as); ++i) {
		}

		return this.fontStates[i];
	}

	private int[] getMultiLineInfo(int numLines) {
		if(this.multiLineInfo == null || this.multiLineInfo.length < numLines) {
			this.multiLineInfo = new int[numLines];
		}

		return this.multiLineInfo;
	}

	public int drawText(AnimationState as, int x, int y, CharSequence str) {
		return this.drawText(as, x, y, str, 0, str.length());
	}

	public int drawText(AnimationState as, int x, int y, CharSequence str, int start, int end) {
		LWJGLFont.FontState fontState = this.evalFontState(as);
		x += fontState.offsetX;
		y += fontState.offsetY;
		if(!this.font.prepare()) {
			return 0;
		} else {
			int width;
			try {
				this.renderer.tintStack.setColor(fontState.color);
				width = this.font.drawText(x, y, str, start, end);
			} finally {
				this.font.cleanup();
			}

			this.drawLine(fontState, x, y, width);
			return width;
		}
	}

	public int drawMultiLineText(AnimationState as, int x, int y, CharSequence str, int width, HAlignment align) {
		LWJGLFont.FontState fontState = this.evalFontState(as);
		x += fontState.offsetX;
		y += fontState.offsetY;
		if(!this.font.prepare()) {
			return 0;
		} else {
			int numLines;
			try {
				this.renderer.tintStack.setColor(fontState.color);
				numLines = this.font.drawMultiLineText(x, y, str, width, align);
			} finally {
				this.font.cleanup();
			}

			if(fontState.style != 0) {
				int[] info = this.getMultiLineInfo(numLines);
				this.font.computeMultiLineInfo(str, width, align, info);
				this.drawLines(fontState, x, y, info, numLines);
			}

			return numLines * this.font.getLineHeight();
		}
	}

	void drawLines(LWJGLFont.FontState fontState, int x, int y, int[] info, int numLines) {
		if((fontState.style & 1) != 0) {
			this.font.drawMultiLineLines(x, y + this.font.getBaseLine() + fontState.underlineOffset, info, numLines);
		}

		if((fontState.style & 2) != 0) {
			this.font.drawMultiLineLines(x, y + this.font.getLineHeight() / 2, info, numLines);
		}

	}

	void drawLine(LWJGLFont.FontState fontState, int x, int y, int width) {
		if((fontState.style & 1) != 0) {
			this.font.drawLine(x, y + this.font.getBaseLine() + fontState.underlineOffset, x + width);
		}

		if((fontState.style & 2) != 0) {
			this.font.drawLine(x, y + this.font.getLineHeight() / 2, x + width);
		}

	}

	public int computeVisibleGlpyhs(CharSequence str, int start, int end, int availWidth) {
		return this.font.computeVisibleGlpyhs(str, start, end, availWidth);
	}

	public int computeTextWidth(CharSequence str) {
		return this.font.computeTextWidth(str, 0, str.length());
	}

	public int computeTextWidth(CharSequence str, int start, int end) {
		return this.font.computeTextWidth(str, start, end);
	}

	public int computeMultiLineTextWidth(CharSequence str) {
		return this.font.computeMultiLineTextWidth(str);
	}

	public FontCache cacheText(FontCache prevCache, CharSequence str) {
		return this.cacheText(prevCache, str, 0, str.length());
	}

	public FontCache cacheText(FontCache prevCache, CharSequence str, int start, int end) {
		LWJGLFontCache cache = (LWJGLFontCache)prevCache;
		if(cache == null) {
			cache = new LWJGLFontCache(this.renderer, this);
		}

		return this.font.cacheText(cache, str, start, end);
	}

	public FontCache cacheMultiLineText(FontCache prevCache, CharSequence str, int width, HAlignment align) {
		LWJGLFontCache cache = (LWJGLFontCache)prevCache;
		if(cache == null) {
			cache = new LWJGLFontCache(this.renderer, this);
		}

		return this.font.cacheMultiLineText(cache, str, width, align);
	}

	public int getSpaceWidth() {
		return this.font.getSpaceWidth();
	}

	public int getLineHeight() {
		return this.font.getLineHeight();
	}

	public int getBaseLine() {
		return this.font.getBaseLine();
	}

	public int getEM() {
		return this.font.getEM();
	}

	public int getEX() {
		return this.font.getEX();
	}

	public void destroy() {
		this.font.destroy();
	}

	static class FontState {
		final StateExpression condition;
		final Color color;
		final int offsetX;
		final int offsetY;
		final int style;
		final int underlineOffset;

		public FontState(StateExpression condition, Color color, int offsetX, int offsetY, int style, int underlineOffset) {
			this.condition = condition;
			this.color = color;
			this.offsetX = offsetX;
			this.offsetY = offsetY;
			this.style = style;
			this.underlineOffset = underlineOffset;
		}
	}
}
