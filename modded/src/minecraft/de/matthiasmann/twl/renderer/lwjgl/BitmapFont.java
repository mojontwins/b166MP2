package de.matthiasmann.twl.renderer.lwjgl;

import de.matthiasmann.twl.HAlignment;
import de.matthiasmann.twl.renderer.FontCache;
import de.matthiasmann.twl.utils.ParameterStringParser;
import de.matthiasmann.twl.utils.TextUtil;
import de.matthiasmann.twl.utils.XMLParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.HashMap;

import org.lwjgl.opengl.GL11;
import org.xmlpull.v1.XmlPullParserException;

public class BitmapFont {
	private static final int LOG2_PAGE_SIZE = 9;
	private static final int PAGE_SIZE = 512;
	private static final int PAGES = 128;
	private final LWJGLTexture texture;
	private final BitmapFont.Glyph[][] glyphs;
	private final int lineHeight;
	private final int baseLine;
	private final int spaceWidth;
	private final int ex;

	public BitmapFont(LWJGLRenderer renderer, XMLParser xmlp, URL baseUrl) throws XmlPullParserException, IOException {
		xmlp.require(2, (String)null, "font");
		xmlp.nextTag();
		xmlp.require(2, (String)null, "info");
		xmlp.ignoreOtherAttributes();
		xmlp.nextTag();
		xmlp.require(3, (String)null, "info");
		xmlp.nextTag();
		xmlp.require(2, (String)null, "common");
		this.lineHeight = xmlp.parseIntFromAttribute("lineHeight");
		this.baseLine = xmlp.parseIntFromAttribute("base");
		if(xmlp.parseIntFromAttribute("pages", 1) != 1) {
			throw new UnsupportedOperationException("multi page fonts not supported");
		} else if(xmlp.parseIntFromAttribute("packed", 0) != 0) {
			throw new UnsupportedOperationException("packed fonts not supported");
		} else {
			xmlp.ignoreOtherAttributes();
			xmlp.nextTag();
			xmlp.require(3, (String)null, "common");
			xmlp.nextTag();
			xmlp.require(2, (String)null, "pages");
			xmlp.nextTag();
			xmlp.require(2, (String)null, "page");
			int pageId = Integer.parseInt(xmlp.getAttributeValue((String)null, "id"));
			if(pageId != 0) {
				throw new UnsupportedOperationException("only page id 0 supported");
			} else {
				String textureName = xmlp.getAttributeValue((String)null, "file");
				this.texture = renderer.load(new URL(baseUrl, textureName), LWJGLTexture.Format.ALPHA, LWJGLTexture.Filter.NEAREST);
				xmlp.nextTag();
				xmlp.require(3, (String)null, "page");
				xmlp.nextTag();
				xmlp.require(3, (String)null, "pages");
				xmlp.nextTag();
				xmlp.require(2, (String)null, "chars");
				xmlp.ignoreOtherAttributes();
				xmlp.nextTag();
				this.glyphs = new BitmapFont.Glyph[128][];

				int g;
				int gx;
				int amount;
				while(!xmlp.isEndTag()) {
					xmlp.require(2, (String)null, "char");
					g = xmlp.parseIntFromAttribute("id");
					gx = xmlp.parseIntFromAttribute("x");
					amount = xmlp.parseIntFromAttribute("y");
					int w = xmlp.parseIntFromAttribute("width");
					int h = xmlp.parseIntFromAttribute("height");
					if(xmlp.parseIntFromAttribute("page", 0) != 0) {
						throw xmlp.error("Multiple pages not supported");
					}

					int chnl = xmlp.parseIntFromAttribute("chnl", 0);
					BitmapFont.Glyph g1 = new BitmapFont.Glyph(gx, amount, w, h, this.texture.getTexWidth(), this.texture.getTexHeight());
					g1.xoffset = Short.parseShort(xmlp.getAttributeNotNull("xoffset"));
					g1.yoffset = Short.parseShort(xmlp.getAttributeNotNull("yoffset"));
					g1.xadvance = Short.parseShort(xmlp.getAttributeNotNull("xadvance"));
					this.addGlyph(g, g1);
					xmlp.nextTag();
					xmlp.require(3, (String)null, "char");
					xmlp.nextTag();
				}

				xmlp.require(3, (String)null, "chars");
				xmlp.nextTag();
				if(xmlp.isStartTag()) {
					xmlp.require(2, (String)null, "kernings");
					xmlp.ignoreOtherAttributes();
					xmlp.nextTag();

					while(!xmlp.isEndTag()) {
						xmlp.require(2, (String)null, "kerning");
						g = xmlp.parseIntFromAttribute("first");
						gx = xmlp.parseIntFromAttribute("second");
						amount = xmlp.parseIntFromAttribute("amount");
						this.addKerning(g, gx, amount);
						xmlp.nextTag();
						xmlp.require(3, (String)null, "kerning");
						xmlp.nextTag();
					}

					xmlp.require(3, (String)null, "kernings");
					xmlp.nextTag();
				}

				xmlp.require(3, (String)null, "font");
				BitmapFont.Glyph g2 = this.getGlyph(' ');
				this.spaceWidth = g2 != null ? g2.xadvance + g2.width : 1;
				BitmapFont.Glyph gx1 = this.getGlyph('x');
				this.ex = gx1 != null ? gx1.height : 1;
			}
		}
	}

	public BitmapFont(LWJGLRenderer renderer, Reader reader, URL baseUrl) throws IOException {
		BufferedReader br = new BufferedReader(reader);
		HashMap params = new HashMap();
		parseFntLine(br, "info");
		parseFntLine(parseFntLine(br, "common"), params);
		this.lineHeight = parseInt(params, "lineHeight");
		this.baseLine = parseInt(params, "base");
		if(parseInt(params, "pages", 1) != 1) {
			throw new UnsupportedOperationException("multi page fonts not supported");
		} else if(parseInt(params, "packed", 0) != 0) {
			throw new UnsupportedOperationException("packed fonts not supported");
		} else {
			parseFntLine(parseFntLine(br, "page"), params);
			if(parseInt(params, "id", 0) != 0) {
				throw new UnsupportedOperationException("only page id 0 supported");
			} else {
				this.texture = renderer.load(new URL(baseUrl, getParam(params, "file")), LWJGLTexture.Format.ALPHA, LWJGLTexture.Filter.NEAREST);
				this.glyphs = new BitmapFont.Glyph[128][];
				parseFntLine(parseFntLine(br, "chars"), params);
				int charCount = parseInt(params, "count");

				int kerningCount;
				int g;
				int gx;
				int second;
				int amount;
				for(kerningCount = 0; kerningCount < charCount; ++kerningCount) {
					parseFntLine(parseFntLine(br, "char"), params);
					g = parseInt(params, "id");
					gx = parseInt(params, "x");
					second = parseInt(params, "y");
					amount = parseInt(params, "width");
					int h = parseInt(params, "height");
					if(parseInt(params, "page", 0) != 0) {
						throw new IOException("Multiple pages not supported");
					}

					BitmapFont.Glyph g1 = new BitmapFont.Glyph(gx, second, amount, h, this.texture.getTexWidth(), this.texture.getTexHeight());
					g1.xoffset = parseShort(params, "xoffset");
					g1.yoffset = parseShort(params, "yoffset");
					g1.xadvance = parseShort(params, "xadvance");
					this.addGlyph(g, g1);
				}

				parseFntLine(parseFntLine(br, "kernings"), params);
				kerningCount = parseInt(params, "count");

				for(g = 0; g < kerningCount; ++g) {
					parseFntLine(parseFntLine(br, "kerning"), params);
					gx = parseInt(params, "first");
					second = parseInt(params, "second");
					amount = parseInt(params, "amount");
					this.addKerning(gx, second, amount);
				}

				BitmapFont.Glyph bitmapFont$Glyph14 = this.getGlyph(' ');
				this.spaceWidth = bitmapFont$Glyph14 != null ? bitmapFont$Glyph14.xadvance + bitmapFont$Glyph14.width : 1;
				BitmapFont.Glyph bitmapFont$Glyph15 = this.getGlyph('x');
				this.ex = bitmapFont$Glyph15 != null ? bitmapFont$Glyph15.height : 1;
			}
		}
	}

	public static BitmapFont loadFont(LWJGLRenderer renderer, URL url) throws IOException {
		boolean startTagSeen = false;

		try {
			XMLParser ex = new XMLParser(url);

			BitmapFont isr1;
			try {
				ex.require(0, (String)null, (String)null);
				ex.nextTag();
				startTagSeen = true;
				isr1 = new BitmapFont(renderer, ex, url);
			} finally {
				ex.close();
			}

			return isr1;
		} catch (XmlPullParserException xmlPullParserException16) {
			if(startTagSeen) {
				throw (IOException)(new IOException()).initCause(xmlPullParserException16);
			} else {
				InputStream is = url.openStream();

				BitmapFont bitmapFont7;
				try {
					InputStreamReader isr = new InputStreamReader(is, "UTF8");
					bitmapFont7 = new BitmapFont(renderer, isr, url);
				} finally {
					is.close();
				}

				return bitmapFont7;
			}
		}
	}

	public int getBaseLine() {
		return this.baseLine;
	}

	public int getLineHeight() {
		return this.lineHeight;
	}

	public int getSpaceWidth() {
		return this.spaceWidth;
	}

	public int getEM() {
		return this.lineHeight;
	}

	public int getEX() {
		return this.ex;
	}

	public void destroy() {
		this.texture.destroy();
	}

	private void addGlyph(int idx, BitmapFont.Glyph g) {
		if(idx <= 65535) {
			BitmapFont.Glyph[] page = this.glyphs[idx >> 9];
			if(page == null) {
				this.glyphs[idx >> 9] = page = new BitmapFont.Glyph[512];
			}

			page[idx & 511] = g;
		}

	}

	private void addKerning(int first, int second, int amount) {
		if(first >= 0 && first <= 65535 && second >= 0 && second <= 65535) {
			BitmapFont.Glyph g = this.getGlyph((char)first);
			if(g != null) {
				g.setKerning(second, amount);
			}
		}

	}

	private BitmapFont.Glyph getGlyph(char ch) {
		BitmapFont.Glyph[] page = this.glyphs[ch >> 9];
		return page != null ? page[ch & 511] : null;
	}

	public int computeTextWidth(CharSequence str, int start, int end) {
		int width = 0;
		BitmapFont.Glyph lastGlyph = null;

		while(start < end) {
			lastGlyph = this.getGlyph(str.charAt(start++));
			if(lastGlyph != null) {
				width = lastGlyph.xadvance;
				break;
			}
		}

		while(start < end) {
			char ch = str.charAt(start++);
			BitmapFont.Glyph g = this.getGlyph(ch);
			if(g != null) {
				width += lastGlyph.getKerning(ch);
				lastGlyph = g;
				width += g.xadvance;
			}
		}

		return width;
	}

	public int computeVisibleGlpyhs(CharSequence str, int start, int end, int availWidth) {
		int index = start;
		int width = 0;

		for(BitmapFont.Glyph lastGlyph = null; index < end; ++index) {
			char ch = str.charAt(index);
			BitmapFont.Glyph g = this.getGlyph(ch);
			if(g != null) {
				if(lastGlyph != null) {
					width += lastGlyph.getKerning(ch);
				}

				lastGlyph = g;
				if(width + g.width + g.xoffset > availWidth) {
					break;
				}

				width += g.xadvance;
			}
		}

		return index - start;
	}

	protected int drawText(int x, int y, CharSequence str, int start, int end) {
		int startX = x;
		BitmapFont.Glyph lastGlyph = null;

		while(start < end) {
			lastGlyph = this.getGlyph(str.charAt(start++));
			if(lastGlyph != null) {
				lastGlyph.draw(x, y);
				x += lastGlyph.xadvance;
				break;
			}
		}

		while(start < end) {
			char ch = str.charAt(start++);
			BitmapFont.Glyph g = this.getGlyph(ch);
			if(g != null) {
				x += lastGlyph.getKerning(ch);
				lastGlyph = g;
				g.draw(x, y);
				x += g.xadvance;
			}
		}

		return x - startX;
	}

	protected int drawMultiLineText(int x, int y, CharSequence str, int width, HAlignment align) {
		int start = 0;

		int numLines;
		for(numLines = 0; start < str.length(); ++numLines) {
			int lineEnd = TextUtil.indexOf(str, '\n', start);
			int xoff = 0;
			if(align != HAlignment.LEFT) {
				int lineWidth = this.computeTextWidth(str, start, lineEnd);
				xoff = width - lineWidth;
				if(align == HAlignment.CENTER) {
					xoff /= 2;
				}
			}

			this.drawText(x + xoff, y, str, start, lineEnd);
			start = lineEnd + 1;
			y += this.lineHeight;
		}

		return numLines;
	}

	public void computeMultiLineInfo(CharSequence str, int width, HAlignment align, int[] multiLineInfo) {
		int start = 0;

		int lineEnd;
		for(int idx = 0; start < str.length(); start = lineEnd + 1) {
			lineEnd = TextUtil.indexOf(str, '\n', start);
			int lineWidth = this.computeTextWidth(str, start, lineEnd);
			int xoff = width - lineWidth;
			if(align == HAlignment.LEFT) {
				xoff = 0;
			} else if(align == HAlignment.CENTER) {
				xoff /= 2;
			}

			multiLineInfo[idx++] = lineWidth << 16 | xoff & 65535;
		}

	}

	protected void beginLine() {
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glBegin(GL11.GL_QUADS);
	}

	protected void endLine() {
		GL11.glEnd();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}

	public void drawMultiLineLines(int x, int y, int[] multiLineInfo, int numLines) {
		this.beginLine();

		try {
			for(int i = 0; i < numLines; ++i) {
				int info = multiLineInfo[i];
				int xoff = x + (short)info;
				int lineWidth = info >>> 16;
				GL11.glVertex2i(xoff, y);
				GL11.glVertex2i(xoff + lineWidth, y);
				GL11.glVertex2i(xoff + lineWidth, y + 1);
				GL11.glVertex2i(xoff, y + 1);
				y += this.lineHeight;
			}
		} finally {
			this.endLine();
		}

	}

	public void drawLine(int x0, int y, int x1) {
		this.beginLine();
		GL11.glVertex2i(x0, y);
		GL11.glVertex2i(x1, y);
		GL11.glVertex2i(x1, y + 1);
		GL11.glVertex2i(x0, y + 1);
		this.endLine();
	}

	public int computeMultiLineTextWidth(CharSequence str) {
		int start = 0;

		int width;
		int lineEnd;
		for(width = 0; start < str.length(); start = lineEnd + 1) {
			lineEnd = TextUtil.indexOf(str, '\n', start);
			int lineWidth = this.computeTextWidth(str, start, lineEnd);
			width = Math.max(width, lineWidth);
		}

		return width;
	}

	public FontCache cacheMultiLineText(LWJGLFontCache cache, CharSequence str, int width, HAlignment align) {
		if(cache.startCompile()) {
			int numLines = 0;

			try {
				if(this.prepare()) {
					try {
						numLines = this.drawMultiLineText(0, 0, str, width, align);
					} finally {
						this.cleanup();
					}

					this.computeMultiLineInfo(str, width, align, cache.getMultiLineInfo(numLines));
				}
			} finally {
				cache.endCompile(width, numLines * this.lineHeight);
			}

			return cache;
		} else {
			return null;
		}
	}

	public FontCache cacheText(LWJGLFontCache cache, CharSequence str, int start, int end) {
		if(cache.startCompile()) {
			int width = 0;

			try {
				if(this.prepare()) {
					try {
						width = this.drawText(0, 0, str, start, end);
					} finally {
						this.cleanup();
					}
				}
			} finally {
				cache.endCompile(width, this.getLineHeight());
			}

			return cache;
		} else {
			return null;
		}
	}

	protected boolean prepare() {
		if(this.texture.bind()) {
			GL11.glBegin(GL11.GL_QUADS);
			return true;
		} else {
			return false;
		}
	}

	protected void cleanup() {
		GL11.glEnd();
	}

	private static String parseFntLine(BufferedReader br, String tag) throws IOException {
		String line = br.readLine();
		if(line != null && line.length() > tag.length() && line.charAt(tag.length()) == 32 && line.startsWith(tag)) {
			return line;
		} else {
			throw new IOException("\'" + tag + "\' line expected");
		}
	}

	private static void parseFntLine(String line, HashMap params) {
		params.clear();
		ParameterStringParser psp = new ParameterStringParser(line, ' ', '=');

		while(psp.next()) {
			params.put(psp.getKey(), psp.getValue());
		}

	}

	private static String getParam(HashMap params, String key) throws IOException {
		String value = (String)params.get(key);
		if(value == null) {
			throw new IOException("Required parameter \'" + key + "\' not found");
		} else {
			return value;
		}
	}

	private static int parseInt(HashMap params, String key) throws IOException {
		String value = getParam(params, key);

		try {
			return Integer.parseInt(value);
		} catch (IllegalArgumentException illegalArgumentException4) {
			throw canParseParam(key, value, illegalArgumentException4);
		}
	}

	private static int parseInt(HashMap params, String key, int defaultValue) throws IOException {
		String value = (String)params.get(key);
		if(value == null) {
			return defaultValue;
		} else {
			try {
				return Integer.parseInt(value);
			} catch (IllegalArgumentException illegalArgumentException5) {
				throw canParseParam(key, value, illegalArgumentException5);
			}
		}
	}

	private static short parseShort(HashMap params, String key) throws IOException {
		String value = getParam(params, key);

		try {
			return Short.parseShort(value);
		} catch (IllegalArgumentException illegalArgumentException4) {
			throw canParseParam(key, value, illegalArgumentException4);
		}
	}

	private static IOException canParseParam(String key, String value, IllegalArgumentException ex) {
		return (IOException)(new IOException("Can\'t parse parameter: " + key + '=' + value)).initCause(ex);
	}

	static class Glyph extends TextureAreaBase {
		short xoffset;
		short yoffset;
		short xadvance;
		byte[][] kerning;

		public Glyph(int x, int y, int width, int height, int texWidth, int texHeight) {
			super(x, y, width, height, (float)texWidth, (float)texHeight);
		}

		void draw(int x, int y) {
			this.drawQuad(x + this.xoffset, y + this.yoffset, this.width, this.height);
		}

		int getKerning(char ch) {
			if(this.kerning != null) {
				byte[] page = this.kerning[ch >>> 9];
				if(page != null) {
					return page[ch & 511];
				}
			}

			return 0;
		}

		void setKerning(int ch, int value) {
			if(this.kerning == null) {
				this.kerning = new byte[128][];
			}

			byte[] page = this.kerning[ch >>> 9];
			if(page == null) {
				this.kerning[ch >>> 9] = page = new byte[512];
			}

			page[ch & 511] = (byte)value;
		}
	}
}
