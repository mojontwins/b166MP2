package de.matthiasmann.twl.theme;

import de.matthiasmann.twl.Border;
import de.matthiasmann.twl.Color;
import de.matthiasmann.twl.renderer.Image;
import de.matthiasmann.twl.renderer.MouseCursor;
import de.matthiasmann.twl.renderer.Renderer;
import de.matthiasmann.twl.renderer.Texture;
import de.matthiasmann.twl.utils.StateExpression;
import de.matthiasmann.twl.utils.TextUtil;
import de.matthiasmann.twl.utils.XMLParser;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.xmlpull.v1.XmlPullParserException;

class ImageManager {
	private final Renderer renderer;
	private final TreeMap images;
	private final TreeMap cursors;
	private Texture currentTexture;
	static final EmptyImage NONE = new EmptyImage(0, 0);
	private static final int[] SPLIT_WEIGHTS_3 = new int[]{0, 1, 0};
	private static final int[] SPLIT_WEIGHTS_1 = new int[]{1};

	ImageManager(Renderer renderer) {
		this.renderer = renderer;
		this.images = new TreeMap();
		this.cursors = new TreeMap();
		this.images.put("none", NONE);
	}

	Image getImage(String name) {
		return (Image)this.images.get(name);
	}

	Image getReferencedImage(XMLParser xmlp) throws XmlPullParserException {
		String ref = xmlp.getAttributeNotNull("ref");
		return this.getReferencedImage(xmlp, ref);
	}

	Image getReferencedImage(XMLParser xmlp, String ref) throws XmlPullParserException {
		if(ref.endsWith(".*")) {
			throw xmlp.error("wildcard mapping not allowed");
		} else {
			Image img = (Image)this.images.get(ref);
			if(img == null) {
				throw xmlp.error("referenced image \"" + ref + "\" not found");
			} else {
				return img;
			}
		}
	}

	MouseCursor getReferencedCursor(XMLParser xmlp, String ref) throws XmlPullParserException {
		MouseCursor cursor = this.getCursor(ref);
		if(cursor == null) {
			throw xmlp.error("referenced cursor \"" + ref + "\" not found");
		} else {
			return cursor;
		}
	}

	Map getImages(String ref, String name) {
		return ParserUtil.resolve(this.images, ref, name);
	}

	public MouseCursor getCursor(String name) {
		return (MouseCursor)this.cursors.get(name);
	}

	Map getCursors(String ref, String name) {
		return ParserUtil.resolve(this.cursors, ref, name);
	}

	void parseImages(XMLParser xmlp, URL baseUrl) throws XmlPullParserException, IOException {
		xmlp.require(2, (String)null, (String)null);
		Texture texture = null;
		String fileName = xmlp.getAttributeValue((String)null, "file");
		String name;
		String tagName;
		if(fileName != null) {
			name = xmlp.getAttributeValue((String)null, "format");
			tagName = xmlp.getAttributeValue((String)null, "filter");
			xmlp.getAttributeValue((String)null, "comment");

			try {
				texture = this.renderer.loadTexture(new URL(baseUrl, fileName), name, tagName);
				if(texture == null) {
					throw new NullPointerException("loadTexture returned null");
				}
			} catch (IOException iOException11) {
				throw xmlp.error("Unable to load image file: " + fileName, iOException11);
			}
		}

		this.currentTexture = texture;

		try {
			xmlp.nextTag();

			while(!xmlp.isEndTag()) {
				name = xmlp.getAttributeNotNull("name");
				this.checkImageName(name, xmlp);
				tagName = xmlp.getName();
				if("cursor".equals(xmlp.getName())) {
					this.parseCursor(xmlp, name);
				} else {
					Image image = this.parseImage(xmlp, tagName);
					this.images.put(name, image);
				}

				xmlp.require(3, (String)null, tagName);
				xmlp.nextTag();
			}
		} finally {
			this.currentTexture = null;
			if(texture != null) {
				texture.themeLoadingDone();
			}

		}

	}

	private void checkImageName(String name, XMLParser xmlp) throws XmlPullParserException, XmlPullParserException {
		ParserUtil.checkNameNotEmpty(name, xmlp);
		if(this.images.containsKey(name)) {
			throw xmlp.error("image \"" + name + "\" already defined");
		}
	}

	private Border getBorder(Image image, Border border) {
		if(border == null && image instanceof HasBorder) {
			border = ((HasBorder)image).getBorder();
		}

		return border;
	}

	private void parseCursor(XMLParser xmlp, String name) throws IOException, XmlPullParserException {
		String ref = xmlp.getAttributeValue((String)null, "ref");
		MouseCursor cursor;
		if(ref != null) {
			cursor = (MouseCursor)this.cursors.get(ref);
		} else {
			ImageManager.ImageParams imageParams = new ImageManager.ImageParams();
			this.parseRectFromAttribute(xmlp, imageParams);
			int hotSpotX = xmlp.parseIntFromAttribute("hotSpotX");
			int hotSpotY = xmlp.parseIntFromAttribute("hotSpotY");
			String imageRefStr = xmlp.getAttributeValue((String)null, "imageRef");
			Image imageRef = null;
			if(imageRefStr != null) {
				imageRef = this.getReferencedImage(xmlp, imageRefStr);
			}

			cursor = this.currentTexture.createCursor(imageParams.x, imageParams.y, imageParams.w, imageParams.h, hotSpotX, hotSpotY, imageRef);
		}

		if(cursor != null) {
			this.cursors.put(name, cursor);
		}

		xmlp.nextTag();
	}

	private Image parseImage(XMLParser xmlp, String tagName) throws XmlPullParserException, IOException {
		ImageManager.ImageParams params = new ImageManager.ImageParams();
		params.condition = ParserUtil.parseCondition(xmlp);
		return this.parseImageNoCond(xmlp, tagName, params);
	}

	private Image parseImageNoCond(XMLParser xmlp, String tagName, ImageManager.ImageParams params) throws XmlPullParserException, IOException {
		this.parseStdAttributes(xmlp, params);
		Image image = this.parseImageDelegate(xmlp, tagName, params);
		return this.adjustImage(image, params);
	}

	private Image adjustImage(Image image, ImageManager.ImageParams params) {
		Border border = this.getBorder((Image)image, params.border);
		if(params.tintColor != null && !Color.WHITE.equals(params.tintColor)) {
			image = ((Image)image).createTintedVersion(params.tintColor);
		}

		if(params.repeatX || params.repeatY) {
			image = new RepeatImage((Image)image, border, params.repeatX, params.repeatY);
		}

		Border imgBorder = this.getBorder((Image)image, (Border)null);
		if(border != null && border != imgBorder || params.inset != null || params.center || params.condition != null || params.sizeOverwriteH >= 0 || params.sizeOverwriteV >= 0) {
			image = new ImageAdjustments((Image)image, border, params.inset, params.sizeOverwriteH, params.sizeOverwriteV, params.center, params.condition);
		}

		return (Image)image;
	}

	private Image parseImageDelegate(XMLParser xmlp, String tagName, ImageManager.ImageParams params) throws XmlPullParserException, IOException {
		if("area".equals(tagName)) {
			return this.parseArea(xmlp, params);
		} else if("alias".equals(tagName)) {
			return this.parseAlias(xmlp);
		} else if("composed".equals(tagName)) {
			return this.parseComposed(xmlp, params);
		} else if("select".equals(tagName)) {
			return this.parseStateSelect(xmlp, params);
		} else if("grid".equals(tagName)) {
			return this.parseGrid(xmlp, params);
		} else if("animation".equals(tagName)) {
			return this.parseAnimation(xmlp, params);
		} else {
			throw xmlp.error("Unexpected \'" + tagName + "\'");
		}
	}

	private Image parseComposed(XMLParser xmlp, ImageManager.ImageParams params) throws IOException, XmlPullParserException {
		ArrayList layers = new ArrayList();
		xmlp.nextTag();

		while(!xmlp.isEndTag()) {
			xmlp.require(2, (String)null, (String)null);
			String tagName = xmlp.getName();
			Image image = this.parseImage(xmlp, tagName);
			layers.add(image);
			params.border = this.getBorder(image, params.border);
			xmlp.require(3, (String)null, tagName);
			xmlp.nextTag();
		}

		switch(layers.size()) {
		case 0:
			return NONE;
		case 1:
			return (Image)layers.get(0);
		default:
			return new ComposedImage((Image[])layers.toArray(new Image[layers.size()]), params.border);
		}
	}

	private Image parseStateSelect(XMLParser xmlp, ImageManager.ImageParams params) throws IOException, XmlPullParserException {
		ArrayList stateImages = new ArrayList();
		ArrayList conditions = new ArrayList();
		xmlp.nextTag();

		while(!xmlp.isEndTag()) {
			xmlp.require(2, (String)null, (String)null);
			StateExpression image = ParserUtil.parseCondition(xmlp);
			String tagName = xmlp.getName();
			Image image1 = this.parseImageNoCond(xmlp, tagName, new ImageManager.ImageParams());
			stateImages.add(image1);
			params.border = this.getBorder(image1, params.border);
			xmlp.require(3, (String)null, tagName);
			xmlp.nextTag();
			if(image == null) {
				break;
			}

			conditions.add(image);
		}

		if(conditions.size() < 1) {
			throw xmlp.error("state select image needs atleast 1 condition");
		} else {
			StateSelectImage image2 = new StateSelectImage((Image[])stateImages.toArray(new Image[stateImages.size()]), (StateExpression[])conditions.toArray(new StateExpression[conditions.size()]), params.border);
			return image2;
		}
	}

	private Image parseArea(XMLParser xmlp, ImageManager.ImageParams params) throws IOException, XmlPullParserException {
		this.parseRectFromAttribute(xmlp, params);
		boolean tiled = xmlp.parseBoolFromAttribute("tiled", false);
		int[] splitx = parseSplit2(xmlp, "splitx", Math.abs(params.w));
		int[] splity = parseSplit2(xmlp, "splity", Math.abs(params.h));
		Object image;
		if(splitx == null && splity == null) {
			image = this.createImage(xmlp, params.x, params.y, params.w, params.h, params.tintColor, tiled);
		} else {
			boolean noCenter = xmlp.parseBoolFromAttribute("nocenter", false);
			int columns = splitx != null ? 3 : 1;
			int rows = splity != null ? 3 : 1;
			Image[] imageParts = new Image[columns * rows];
			int r = 0;

			for(int idx = 0; r < rows; ++r) {
				int imgY;
				int imgH;
				if(splity != null) {
					imgY = params.y + splity[r];
					imgH = (splity[r + 1] - splity[r]) * Integer.signum(params.h);
				} else {
					imgY = params.y;
					imgH = params.h;
				}

				for(int c = 0; c < columns; ++idx) {
					int imgX;
					int imgW;
					if(splitx != null) {
						imgX = params.x + splitx[c];
						imgW = (splitx[c + 1] - splitx[c]) * Integer.signum(params.w);
					} else {
						imgX = params.x;
						imgW = params.w;
					}

					boolean isCenter = r == rows / 2 && c == columns / 2;
					if(noCenter && isCenter) {
						imageParts[idx] = new EmptyImage(imgW, imgH);
					} else {
						imageParts[idx] = this.createImage(xmlp, imgX, imgY, imgW, imgH, params.tintColor, isCenter & tiled);
					}

					++c;
				}
			}

			image = new GridImage(imageParts, splitx != null ? SPLIT_WEIGHTS_3 : SPLIT_WEIGHTS_1, splity != null ? SPLIT_WEIGHTS_3 : SPLIT_WEIGHTS_1, params.border);
		}

		xmlp.nextTag();
		params.tintColor = null;
		if(tiled) {
			params.repeatX = false;
			params.repeatY = false;
		}

		return (Image)image;
	}

	private Image parseAlias(XMLParser xmlp) throws XmlPullParserException, XmlPullParserException, IOException {
		Image image = this.getReferencedImage(xmlp);
		xmlp.nextTag();
		return image;
	}

	private static int[] parseSplit2(XMLParser xmlp, String attribName, int size) throws XmlPullParserException {
		String splitStr = xmlp.getAttributeValue((String)null, attribName);
		if(splitStr == null) {
			return null;
		} else {
			int comma = splitStr.indexOf(44);
			if(comma < 0) {
				throw xmlp.error(attribName + " requires 2 values");
			} else {
				try {
					int[] ex = new int[4];
					int tmp = 0;
					int start = 0;

					while(tmp < 2) {
						String part = splitStr.substring(start, comma).trim();
						if(part.length() == 0) {
							throw new NumberFormatException("empty string");
						}

						int off = 0;
						byte sign = 1;
						switch(part.charAt(0)) {
						case 'B':
						case 'R':
						case 'b':
						case 'r':
							off = size;
							sign = -1;
						case 'L':
						case 'T':
						case 'l':
						case 't':
							part = part.substring(1).trim();
						default:
							int value = Integer.parseInt(part);
							ex[tmp + 1] = Math.max(0, Math.min(size, off + sign * value));
							start = comma + 1;
							comma = splitStr.length();
							++tmp;
						}
					}

					if(ex[1] > ex[2]) {
						tmp = ex[1];
						ex[1] = ex[2];
						ex[2] = tmp;
					}

					ex[3] = size;
					return ex;
				} catch (NumberFormatException numberFormatException12) {
					throw xmlp.error("Unable to parse " + attribName + ": \"" + splitStr + "\"", numberFormatException12);
				}
			}
		}
	}

	private void parseSubImages(XMLParser xmlp, Image[] textures) throws XmlPullParserException, IOException {
		for(int i = 0; i < textures.length; ++i) {
			xmlp.require(2, (String)null, (String)null);
			String tagName = xmlp.getName();
			textures[i] = this.parseImage(xmlp, tagName);
			xmlp.require(3, (String)null, tagName);
			xmlp.nextTag();
		}

	}

	private Image parseGrid(XMLParser xmlp, ImageManager.ImageParams params) throws IOException, XmlPullParserException {
		try {
			int[] ex = ParserUtil.parseIntArrayFromAttribute(xmlp, "weightsX");
			int[] weightsY = ParserUtil.parseIntArrayFromAttribute(xmlp, "weightsY");
			Image[] textures = new Image[ex.length * weightsY.length];
			xmlp.nextTag();
			this.parseSubImages(xmlp, textures);
			GridImage image = new GridImage(textures, ex, weightsY, params.border);
			return image;
		} catch (IllegalArgumentException illegalArgumentException7) {
			throw xmlp.error("Invalid value", illegalArgumentException7);
		}
	}

	private void parseAnimElements(XMLParser xmlp, String tagName, ArrayList frames) throws XmlPullParserException, IOException {
		if("repeat".equals(tagName)) {
			frames.add(this.parseAnimRepeat(xmlp));
		} else if("frame".equals(tagName)) {
			frames.add(this.parseAnimFrame(xmlp));
		} else {
			if(!"frames".equals(tagName)) {
				throw xmlp.unexpected();
			}

			this.parseAnimFrames(xmlp, frames);
		}

	}

	private AnimatedImage.Img parseAnimFrame(XMLParser xmlp) throws XmlPullParserException, IOException {
		int duration = xmlp.parseIntFromAttribute("duration");
		if(duration < 0) {
			throw new IllegalArgumentException("duration must be >= 0 ms");
		} else {
			ImageManager.AnimParams animParams = this.parseAnimParams(xmlp);
			Image image = this.getReferencedImage(xmlp);
			AnimatedImage.Img img = new AnimatedImage.Img(duration, image, animParams.tintColor, animParams.zoomX, animParams.zoomY, animParams.zoomCenterX, animParams.zoomCenterY);
			xmlp.nextTag();
			return img;
		}
	}

	private ImageManager.AnimParams parseAnimParams(XMLParser xmlp) throws XmlPullParserException {
		ImageManager.AnimParams params = new ImageManager.AnimParams();
		params.tintColor = ParserUtil.parseColorFromAttribute(xmlp, "tint", Color.WHITE);
		float zoom = xmlp.parseFloatFromAttribute("zoom", 1.0F);
		params.zoomX = xmlp.parseFloatFromAttribute("zoomX", zoom);
		params.zoomY = xmlp.parseFloatFromAttribute("zoomY", zoom);
		params.zoomCenterX = xmlp.parseFloatFromAttribute("zoomCenterX", 0.5F);
		params.zoomCenterY = xmlp.parseFloatFromAttribute("zoomCenterY", 0.5F);
		return params;
	}

	private void parseAnimFrames(XMLParser xmlp, ArrayList frames) throws XmlPullParserException, IOException {
		ImageManager.ImageParams params = new ImageManager.ImageParams();
		this.parseRectFromAttribute(xmlp, params);
		int duration = xmlp.parseIntFromAttribute("duration");
		if(duration < 1) {
			throw new IllegalArgumentException("duration must be >= 1 ms");
		} else {
			int count = xmlp.parseIntFromAttribute("count");
			if(count < 1) {
				throw new IllegalArgumentException("count must be >= 1");
			} else {
				ImageManager.AnimParams animParams = this.parseAnimParams(xmlp);
				int xOffset = xmlp.parseIntFromAttribute("offsetx", 0);
				int yOffset = xmlp.parseIntFromAttribute("offsety", 0);
				if(count > 1 && xOffset == 0 && yOffset == 0) {
					throw new IllegalArgumentException("offsets required for multiple frames");
				} else {
					for(int i = 0; i < count; ++i) {
						Image image = this.createImage(xmlp, params.x, params.y, params.w, params.h, Color.WHITE, false);
						AnimatedImage.Img img = new AnimatedImage.Img(duration, image, animParams.tintColor, animParams.zoomX, animParams.zoomY, animParams.zoomCenterX, animParams.zoomCenterY);
						frames.add(img);
						params.x += xOffset;
						params.y += yOffset;
					}

					xmlp.nextTag();
				}
			}
		}
	}

	private AnimatedImage.Repeat parseAnimRepeat(XMLParser xmlp) throws XmlPullParserException, IOException {
		String strRepeatCount = xmlp.getAttributeValue((String)null, "count");
		int repeatCount = 0;
		if(strRepeatCount != null) {
			repeatCount = Integer.parseInt(strRepeatCount);
			if(repeatCount <= 0) {
				throw new IllegalArgumentException("Invalid repeat count");
			}
		}

		boolean lastRepeatsEndless = false;
		boolean hasWarned = false;
		ArrayList children = new ArrayList();
		xmlp.nextTag();

		while(xmlp.isStartTag()) {
			if(lastRepeatsEndless && !hasWarned) {
				hasWarned = true;
				this.getLogger().log(Level.WARNING, "Animation frames after an endless repeat won\'\'t be displayed: {0}", xmlp.getPositionDescription());
			}

			String tagName = xmlp.getName();
			this.parseAnimElements(xmlp, tagName, children);
			AnimatedImage.Element e = (AnimatedImage.Element)children.get(children.size() - 1);
			lastRepeatsEndless = e instanceof AnimatedImage.Repeat && ((AnimatedImage.Repeat)e).repeatCount == 0;
			xmlp.require(3, (String)null, tagName);
			xmlp.nextTag();
		}

		return new AnimatedImage.Repeat((AnimatedImage.Element[])children.toArray(new AnimatedImage.Element[children.size()]), repeatCount);
	}

	private Border getBorder(AnimatedImage.Element e) {
		if(e instanceof AnimatedImage.Repeat) {
			AnimatedImage.Repeat i = (AnimatedImage.Repeat)e;
			AnimatedImage.Element[] animatedImage$Element6 = i.children;
			int i5 = i.children.length;

			for(int i4 = 0; i4 < i5; ++i4) {
				AnimatedImage.Element c = animatedImage$Element6[i4];
				Border border = this.getBorder(c);
				if(border != null) {
					return border;
				}
			}
		} else if(e instanceof AnimatedImage.Img) {
			AnimatedImage.Img animatedImage$Img8 = (AnimatedImage.Img)e;
			if(animatedImage$Img8.image instanceof HasBorder) {
				return ((HasBorder)animatedImage$Img8.image).getBorder();
			}
		}

		return null;
	}

	private Image parseAnimation(XMLParser xmlp, ImageManager.ImageParams params) throws XmlPullParserException, IOException {
		try {
			String ex = xmlp.getAttributeNotNull("timeSource");
			int frozenTime = xmlp.parseIntFromAttribute("frozenTime", -1);
			AnimatedImage.Repeat root = this.parseAnimRepeat(xmlp);
			if(params.border == null) {
				params.border = this.getBorder(root);
			}

			AnimatedImage image = new AnimatedImage(this.renderer, root, ex, params.border, params.tintColor == null ? Color.WHITE : params.tintColor, frozenTime);
			params.tintColor = null;
			return image;
		} catch (IllegalArgumentException illegalArgumentException7) {
			throw xmlp.error("Unable to parse", illegalArgumentException7);
		}
	}

	private Image createImage(XMLParser xmlp, int x, int y, int w, int h, Color tintColor, boolean tiled) {
		if(w != 0 && h != 0) {
			if(w < 0) {
				x -= w;
			}

			if(h < 0) {
				y -= h;
			}

			Texture texture = this.currentTexture;
			int texWidth = texture.getWidth();
			int texHeight = texture.getHeight();
			int x1 = x + w;
			int y1 = y + h;
			if(x < 0 || x > texWidth || x1 < 0 || x1 > texWidth || y < 0 || y > texHeight || y1 < 0 || y1 > texHeight) {
				this.getLogger().log(Level.WARNING, "texture partly outside of file: {0}", xmlp.getPositionDescription());
				x = Math.max(0, Math.min(x, texWidth));
				w = Math.max(0, Math.min(x1, texWidth)) - x;
				y = Math.max(0, Math.min(y, texHeight));
				h = Math.max(0, Math.min(y1, texHeight)) - y;
			}

			return texture.getImage(x, y, w, h, tintColor, tiled);
		} else {
			return new EmptyImage(Math.abs(w), Math.abs(h));
		}
	}

	private void parseRectFromAttribute(XMLParser xmlp, ImageManager.ImageParams params) throws XmlPullParserException {
		if(this.currentTexture == null) {
			throw xmlp.error("can\'t create area outside of <imagefile> object");
		} else {
			String xywh = xmlp.getAttributeNotNull("xywh");
			if("*".equals(xywh)) {
				params.x = 0;
				params.y = 0;
				params.w = this.currentTexture.getWidth();
				params.h = this.currentTexture.getHeight();
			} else {
				try {
					int[] ex = TextUtil.parseIntArray(xywh);
					if(ex.length != 4) {
						throw xmlp.error("xywh requires 4 integer arguments");
					}

					params.x = ex[0];
					params.y = ex[1];
					params.w = ex[2];
					params.h = ex[3];
				} catch (IllegalArgumentException illegalArgumentException5) {
					throw xmlp.error("can\'t parse xywh argument", illegalArgumentException5);
				}
			}

		}
	}

	private void parseStdAttributes(XMLParser xmlp, ImageManager.ImageParams params) throws XmlPullParserException {
		params.tintColor = ParserUtil.parseColorFromAttribute(xmlp, "tint", (Color)null);
		params.border = ParserUtil.parseBorderFromAttribute(xmlp, "border");
		params.inset = ParserUtil.parseBorderFromAttribute(xmlp, "inset");
		params.repeatX = xmlp.parseBoolFromAttribute("repeatX", false);
		params.repeatY = xmlp.parseBoolFromAttribute("repeatY", false);
		params.sizeOverwriteH = xmlp.parseIntFromAttribute("sizeOverwriteH", -1);
		params.sizeOverwriteV = xmlp.parseIntFromAttribute("sizeOverwriteV", -1);
		params.center = xmlp.parseBoolFromAttribute("center", false);
	}

	Logger getLogger() {
		return Logger.getLogger(ImageManager.class.getName());
	}

	static class AnimParams {
		Color tintColor;
		float zoomX;
		float zoomY;
		float zoomCenterX;
		float zoomCenterY;
	}

	static class ImageParams {
		int x;
		int y;
		int w;
		int h;
		Color tintColor;
		Border border;
		Border inset;
		boolean repeatX;
		boolean repeatY;
		int sizeOverwriteH = -1;
		int sizeOverwriteV = -1;
		boolean center;
		StateExpression condition;
	}
}
