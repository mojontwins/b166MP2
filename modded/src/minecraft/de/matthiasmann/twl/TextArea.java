package de.matthiasmann.twl;

import de.matthiasmann.twl.renderer.Font;
import de.matthiasmann.twl.renderer.FontCache;
import de.matthiasmann.twl.renderer.Image;
import de.matthiasmann.twl.renderer.MouseCursor;
import de.matthiasmann.twl.textarea.OrderedListType;
import de.matthiasmann.twl.textarea.Style;
import de.matthiasmann.twl.textarea.StyleAttribute;
import de.matthiasmann.twl.textarea.StyleSheet;
import de.matthiasmann.twl.textarea.StyleSheetResolver;
import de.matthiasmann.twl.textarea.TextAreaModel;
import de.matthiasmann.twl.textarea.Value;
import de.matthiasmann.twl.utils.CallbackSupport;
import de.matthiasmann.twl.utils.TextUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TextArea extends Widget {
	public static final de.matthiasmann.twl.renderer.AnimationState.StateKey STATE_HOVER = de.matthiasmann.twl.renderer.AnimationState.StateKey.get("hover");
	private final HashMap widgets;
	private final HashMap widgetResolvers;
	private final HashMap userImages;
	private final ArrayList imageResolvers;
	StyleSheetResolver styleClassResolver;
	private final Runnable modelCB;
	private TextAreaModel model;
	private ParameterMap fonts;
	private ParameterMap images;
	private Font defaultFont;
	private TextArea.Callback[] callbacks;
	private MouseCursor mouseCursorNormal;
	private MouseCursor mouseCursorLink;
	private DraggableButton.DragListener dragListener;
	final TextArea.LClip layoutRoot;
	final ArrayList allBGImages;
	private boolean inLayoutCode;
	private boolean forceRelayout;
	private int lastMouseX;
	private int lastMouseY;
	private boolean lastMouseInside;
	private boolean dragging;
	private int dragStartX;
	private int dragStartY;
	TextArea.LElement curLElementUnderMouse;
	private static int[] $SWITCH_TABLE$de$matthiasmann$twl$textarea$TextAreaModel$HAlignment;
	private static int[] $SWITCH_TABLE$de$matthiasmann$twl$textarea$Value$Unit;

	public TextArea() {
		this.widgets = new HashMap();
		this.widgetResolvers = new HashMap();
		this.userImages = new HashMap();
		this.imageResolvers = new ArrayList();
		this.layoutRoot = new TextArea.LClip((TextAreaModel.Element)null);
		this.layoutRoot.lineInfo = new char[0];
		this.allBGImages = new ArrayList();
		this.modelCB = new Runnable() {
			public void run() {
				TextArea.this.forceRelayout();
			}
		};
	}

	public TextArea(TextAreaModel model) {
		this();
		this.setModel(model);
	}

	public TextAreaModel getModel() {
		return this.model;
	}

	public void setModel(TextAreaModel model) {
		if(this.model != null) {
			this.model.removeCallback(this.modelCB);
		}

		this.model = model;
		if(model != null) {
			model.addCallback(this.modelCB);
		}

		this.forceRelayout();
	}

	public void registerWidget(String name, Widget widget) {
		if(name == null) {
			throw new NullPointerException("name");
		} else if(widget.getParent() != null) {
			throw new IllegalArgumentException("Widget must not have a parent");
		} else if(!this.widgets.containsKey(name) && !this.widgetResolvers.containsKey(name)) {
			if(this.widgets.containsValue(widget)) {
				throw new IllegalArgumentException("widget already registered");
			} else {
				this.widgets.put(name, widget);
			}
		} else {
			throw new IllegalArgumentException("widget name already in registered");
		}
	}

	public void registerWidgetResolver(String name, TextArea.WidgetResolver resolver) {
		if(name == null) {
			throw new NullPointerException("name");
		} else if(resolver == null) {
			throw new NullPointerException("resolver");
		} else if(!this.widgets.containsKey(name) && !this.widgetResolvers.containsKey(name)) {
			this.widgetResolvers.put(name, resolver);
		} else {
			throw new IllegalArgumentException("widget name already in registered");
		}
	}

	public void unregisterWidgetResolver(String name) {
		if(name == null) {
			throw new NullPointerException("name");
		} else {
			this.widgetResolvers.remove(name);
		}
	}

	public void unregisterWidget(String name) {
		if(name == null) {
			throw new NullPointerException("name");
		} else {
			Widget w = (Widget)this.widgets.get(name);
			if(w != null) {
				int idx = this.getChildIndex(w);
				if(idx >= 0) {
					super.removeChild(idx);
					this.forceRelayout();
				}
			}

		}
	}

	public void unregisterAllWidgets() {
		this.widgets.clear();
		super.removeAllChildren();
		this.forceRelayout();
	}

	public void registerImage(String name, Image image) {
		if(name == null) {
			throw new NullPointerException("name");
		} else {
			this.userImages.put(name, image);
		}
	}

	public void registerImageResolver(TextArea.ImageResolver resolver) {
		if(resolver == null) {
			throw new NullPointerException("resolver");
		} else {
			if(!this.imageResolvers.contains(resolver)) {
				this.imageResolvers.add(resolver);
			}

		}
	}

	public void unregisterImage(String name) {
		this.userImages.remove(name);
	}

	public void unregisterImageResolver(TextArea.ImageResolver imageResolver) {
		this.imageResolvers.remove(imageResolver);
	}

	public void addCallback(TextArea.Callback cb) {
		this.callbacks = (TextArea.Callback[])CallbackSupport.addCallbackToList(this.callbacks, cb, TextArea.Callback.class);
	}

	public void removeCallback(TextArea.Callback cb) {
		this.callbacks = (TextArea.Callback[])CallbackSupport.removeCallbackFromList(this.callbacks, cb);
	}

	public DraggableButton.DragListener getDragListener() {
		return this.dragListener;
	}

	public void setDragListener(DraggableButton.DragListener dragListener) {
		this.dragListener = dragListener;
	}

	public StyleSheetResolver getStyleClassResolver() {
		return this.styleClassResolver;
	}

	public void setStyleClassResolver(StyleSheetResolver styleClassResolver) {
		this.styleClassResolver = styleClassResolver;
		this.forceRelayout();
	}

	public void setDefaultStyleSheet() {
		try {
			StyleSheet ex = new StyleSheet();
			ex.parse("p,ul{margin-bottom:1em}");
			this.setStyleClassResolver(ex);
		} catch (IOException iOException2) {
			Logger.getLogger(TextArea.class.getName()).log(Level.SEVERE, "Can\'t create default style sheet", iOException2);
		}

	}

	public Rect getElementRect(TextAreaModel.Element element) {
		int[] offset = new int[2];
		TextArea.LElement le = this.layoutRoot.find(element, offset);
		return le != null ? new Rect(le.x + offset[0], le.y + offset[1], le.width, le.height) : null;
	}

	protected void applyTheme(ThemeInfo themeInfo) {
		super.applyTheme(themeInfo);
		this.applyThemeTextArea(themeInfo);
	}

	protected void applyThemeTextArea(ThemeInfo themeInfo) {
		this.fonts = themeInfo.getParameterMap("fonts");
		this.images = themeInfo.getParameterMap("images");
		this.defaultFont = themeInfo.getFont("font");
		this.mouseCursorNormal = themeInfo.getMouseCursor("mouseCursor");
		this.mouseCursorLink = themeInfo.getMouseCursor("mouseCursor.link");
		this.forceRelayout();
	}

	public void insertChild(Widget child, int index) {
		throw new UnsupportedOperationException("use registerWidget");
	}

	public void removeAllChildren() {
		throw new UnsupportedOperationException("use registerWidget");
	}

	public Widget removeChild(int index) {
		throw new UnsupportedOperationException("use registerWidget");
	}

	public int getPreferredInnerWidth() {
		return this.getInnerWidth();
	}

	public int getPreferredInnerHeight() {
		this.validateLayout();
		return this.layoutRoot.height;
	}

	public int getPreferredWidth() {
		int maxWidth = this.getMaxWidth();
		return maxWidth > 0 ? maxWidth : computeSize(this.getMinWidth(), super.getPreferredWidth(), maxWidth);
	}

	public void setMaxSize(int width, int height) {
		if(width != this.getMaxWidth()) {
			this.invalidateLayout();
		}

		super.setMaxSize(width, height);
	}

	public void setMinSize(int width, int height) {
		if(width != this.getMinWidth()) {
			this.invalidateLayout();
		}

		super.setMinSize(width, height);
	}

	protected void layout() {
		int targetWidth = computeSize(this.getMinWidth(), this.getWidth(), this.getMaxWidth());
		targetWidth -= this.getBorderHorizontal();
		if(this.layoutRoot.width != targetWidth || this.forceRelayout) {
			this.layoutRoot.width = targetWidth;
			this.inLayoutCode = true;
			this.forceRelayout = false;
			if(this.styleClassResolver != null) {
				this.styleClassResolver.startLayout();
			}

			this.clearLayout();
			TextArea.Box box = new TextArea.Box(this.layoutRoot, 0, 0, 0);

			try {
				if(this.model != null) {
					this.layoutElements(box, this.model);
					box.finish();
					this.layoutRoot.adjustWidget(this.getInnerX(), this.getInnerY());
				}

				this.updateMouseHover();
			} finally {
				this.inLayoutCode = false;
			}

			if(this.styleClassResolver != null) {
				this.styleClassResolver.layoutFinished();
			}

			if(this.layoutRoot.height != box.curY) {
				this.layoutRoot.height = box.curY;
				this.invalidateLayout();
			}
		}

	}

	protected void paintWidget(GUI gui) {
		ArrayList bi = this.allBGImages;
		int innerX = this.getInnerX();
		int innerY = this.getInnerY();
		AnimationState as = this.getAnimationState();
		int i = 0;

		for(int n = bi.size(); i < n; ++i) {
			((TextArea.LImage)bi.get(i)).draw(innerX, innerY, as);
		}

		this.layoutRoot.draw(innerX, innerY, as);
	}

	protected void sizeChanged() {
		if(!this.inLayoutCode) {
			this.invalidateLayout();
		}

	}

	protected void childAdded(Widget child) {
	}

	protected void childRemoved(Widget exChild) {
	}

	protected void allChildrenRemoved() {
	}

	public void destroy() {
		super.destroy();
		this.clearLayout();
		this.forceRelayout();
	}

	protected boolean handleEvent(Event evt) {
		if(super.handleEvent(evt)) {
			return true;
		} else if(!evt.isMouseEvent()) {
			return false;
		} else {
			Event.Type eventType = evt.getType();
			if(this.dragging) {
				if(eventType == Event.Type.MOUSE_DRAGGED && this.dragListener != null) {
					this.dragListener.dragged(evt.getMouseX() - this.dragStartX, evt.getMouseY() - this.dragStartY);
				}

				if(evt.isMouseDragEnd()) {
					if(this.dragListener != null) {
						this.dragListener.dragStopped();
					}

					this.dragging = false;
					this.updateMouseHover(evt);
				}

				return true;
			} else {
				this.updateMouseHover(evt);
				if(eventType == Event.Type.MOUSE_WHEEL) {
					return false;
				} else {
					if(eventType == Event.Type.MOUSE_BTNDOWN) {
						this.dragStartX = evt.getMouseX();
						this.dragStartY = evt.getMouseY();
					}

					if(eventType == Event.Type.MOUSE_DRAGGED) {
						assert !this.dragging;

						this.dragging = true;
						if(this.dragListener != null) {
							this.dragListener.dragStarted();
						}

						return true;
					} else {
						if(eventType == Event.Type.MOUSE_CLICKED && this.curLElementUnderMouse != null && this.curLElementUnderMouse.href != null) {
							String href = this.curLElementUnderMouse.href;
							if(this.callbacks != null) {
								TextArea.Callback[] textArea$Callback7 = this.callbacks;
								int i6 = this.callbacks.length;

								for(int i5 = 0; i5 < i6; ++i5) {
									TextArea.Callback l = textArea$Callback7[i5];
									l.handleLinkClicked(href);
								}
							}
						}

						return true;
					}
				}
			}
		}
	}

	protected Object getTooltipContentAt(int mouseX, int mouseY) {
		return this.curLElementUnderMouse != null && this.curLElementUnderMouse.element instanceof TextAreaModel.ImageElement ? ((TextAreaModel.ImageElement)this.curLElementUnderMouse.element).getToolTip() : super.getTooltipContentAt(mouseX, mouseY);
	}

	private void updateMouseHover(Event evt) {
		this.lastMouseInside = this.isMouseInside(evt);
		this.lastMouseX = evt.getMouseX();
		this.lastMouseY = evt.getMouseY();
		this.updateMouseHover();
	}

	private void updateMouseHover() {
		TextArea.LElement le = null;
		if(this.lastMouseInside) {
			le = this.layoutRoot.find(this.lastMouseX - this.getInnerX(), this.lastMouseY - this.getInnerY());
		}

		if(this.curLElementUnderMouse != le) {
			this.curLElementUnderMouse = le;
			this.updateTooltip();
		}

		if(le != null && le.href != null) {
			this.setMouseCursor(this.mouseCursorLink);
		} else {
			this.setMouseCursor(this.mouseCursorNormal);
		}

	}

	void forceRelayout() {
		this.forceRelayout = true;
		this.invalidateLayout();
	}

	private void clearLayout() {
		this.layoutRoot.destroy();
		this.allBGImages.clear();
		super.removeAllChildren();
	}

	private void layoutElements(TextArea.Box box, Iterable elements) {
		Iterator iterator4 = elements.iterator();

		while(iterator4.hasNext()) {
			TextAreaModel.Element e = (TextAreaModel.Element)iterator4.next();
			this.layoutElement(box, e);
		}

	}

	private void layoutElement(TextArea.Box box, TextAreaModel.Element e) {
		box.clearFloater((TextAreaModel.Clear)e.getStyle().get(StyleAttribute.CLEAR, this.styleClassResolver));
		if(e instanceof TextAreaModel.TextElement) {
			this.layoutTextElement(box, (TextAreaModel.TextElement)e);
		} else {
			if(box.wasPreformatted) {
				box.nextLine(false);
				box.wasPreformatted = false;
			}

			if(e instanceof TextAreaModel.ParagraphElement) {
				this.layoutParagraphElement(box, (TextAreaModel.ParagraphElement)e);
			} else if(e instanceof TextAreaModel.ImageElement) {
				this.layoutImageElement(box, (TextAreaModel.ImageElement)e);
			} else if(e instanceof TextAreaModel.WidgetElement) {
				this.layoutWidgetElement(box, (TextAreaModel.WidgetElement)e);
			} else if(e instanceof TextAreaModel.ListElement) {
				this.layoutListElement(box, (TextAreaModel.ListElement)e);
			} else if(e instanceof TextAreaModel.OrderedListElement) {
				this.layoutOrderedListElement(box, (TextAreaModel.OrderedListElement)e);
			} else if(e instanceof TextAreaModel.BlockElement) {
				this.layoutBlockElement(box, (TextAreaModel.BlockElement)e);
			} else if(e instanceof TextAreaModel.TableElement) {
				this.layoutTableElement(box, (TextAreaModel.TableElement)e);
			} else if(e instanceof TextAreaModel.LinkElement) {
				this.layoutLinkElement(box, (TextAreaModel.LinkElement)e);
			} else if(e instanceof TextAreaModel.ContainerElement) {
				this.layoutContainerElement(box, (TextAreaModel.ContainerElement)e);
			} else {
				Logger.getLogger(TextArea.class.getName()).log(Level.SEVERE, "Unknown Element subclass: {0}", e.getClass());
			}
		}

	}

	private void layoutImageElement(TextArea.Box box, TextAreaModel.ImageElement ie) {
		Image image = this.selectImage(ie.getImageName());
		if(image != null) {
			TextArea.LImage li = new TextArea.LImage(ie, image);
			li.href = box.href;
			this.layout(box, ie, li);
		}
	}

	private void layoutWidgetElement(TextArea.Box box, TextAreaModel.WidgetElement we) {
		Widget widget = (Widget)this.widgets.get(we.getWidgetName());
		if(widget == null) {
			TextArea.WidgetResolver lw = (TextArea.WidgetResolver)this.widgetResolvers.get(we.getWidgetName());
			if(lw != null) {
				widget = lw.resolveWidget(we.getWidgetName(), we.getWidgetParam());
			}

			if(widget == null) {
				return;
			}
		}

		if(widget.getParent() != null) {
			Logger.getLogger(TextArea.class.getName()).log(Level.SEVERE, "Widget already added: {0}", widget);
		} else {
			super.insertChild(widget, this.getNumChildren());
			widget.adjustSize();
			TextArea.LWidget lw1 = new TextArea.LWidget(we, widget);
			lw1.width = widget.getWidth();
			lw1.height = widget.getHeight();
			this.layout(box, we, lw1);
		}
	}

	private void layout(TextArea.Box box, TextAreaModel.Element e, TextArea.LElement le) {
		Style style = e.getStyle();
		TextAreaModel.FloatPosition floatPosition = (TextAreaModel.FloatPosition)style.get(StyleAttribute.FLOAT_POSITION, this.styleClassResolver);
		TextAreaModel.Display display = (TextAreaModel.Display)style.get(StyleAttribute.DISPLAY, this.styleClassResolver);
		le.marginTop = (short)this.convertToPX0(style, StyleAttribute.MARGIN_TOP, box.boxWidth);
		le.marginLeft = (short)this.convertToPX0(style, StyleAttribute.MARGIN_LEFT, box.boxWidth);
		le.marginRight = (short)this.convertToPX0(style, StyleAttribute.MARGIN_RIGHT, box.boxWidth);
		le.marginBottom = (short)this.convertToPX0(style, StyleAttribute.MARGIN_BOTTOM, box.boxWidth);
		int autoHeight = le.height;
		int width = this.convertToPX(style, StyleAttribute.WIDTH, box.boxWidth, le.width);
		if(width > 0) {
			if(le.width > 0) {
				autoHeight = width * le.height / le.width;
			}

			le.width = width;
		}

		int height = this.convertToPX(style, StyleAttribute.HEIGHT, le.height, autoHeight);
		if(height > 0) {
			le.height = height;
		}

		this.layout(box, e, le, floatPosition, display);
	}

	private void layout(TextArea.Box box, TextAreaModel.Element e, TextArea.LElement le, TextAreaModel.FloatPosition floatPos, TextAreaModel.Display display) {
		boolean leftRight = floatPos != TextAreaModel.FloatPosition.NONE;
		if(leftRight || display != TextAreaModel.Display.INLINE) {
			box.nextLine(false);
			if(!leftRight) {
				box.curY = box.computeTopPadding(le.marginTop);
				box.checkFloaters();
			}
		}

		box.advancePastFloaters(le.width, le.marginLeft, le.marginRight);
		if(le.width > box.lineWidth) {
			le.width = box.lineWidth;
		}

		if(leftRight) {
			if(floatPos == TextAreaModel.FloatPosition.RIGHT) {
				le.x = box.computeRightPadding(le.marginRight) - le.width;
				box.objRight.add(le);
			} else {
				le.x = box.computeLeftPadding(le.marginLeft);
				box.objLeft.add(le);
			}
		} else if(display == TextAreaModel.Display.INLINE) {
			if(box.getRemaining() < le.width && !box.isAtStartOfLine()) {
				box.nextLine(false);
			}

			le.x = box.getXAndAdvance(le.width);
		} else {
			switch($SWITCH_TABLE$de$matthiasmann$twl$textarea$TextAreaModel$HAlignment()[((TextAreaModel.HAlignment)e.getStyle().get(StyleAttribute.HORIZONTAL_ALIGNMENT, this.styleClassResolver)).ordinal()]) {
			case 2:
				le.x = box.computeRightPadding(le.marginRight) - le.width;
				break;
			case 3:
			case 4:
				le.x = box.lineStartX + (box.lineWidth - le.width) / 2;
				break;
			default:
				le.x = box.computeLeftPadding(le.marginLeft);
			}
		}

		box.layout.add(le);
		if(leftRight) {
			assert box.lineStartIdx == box.layout.size() - 1;

			++box.lineStartIdx;
			le.y = box.computeTopPadding(le.marginTop);
			box.computePadding();
		} else if(display != TextAreaModel.Display.INLINE) {
			box.nextLine(false);
		}

	}

	int convertToPX(Style style, StyleAttribute attribute, int full, int auto) {
		style = style.resolve(attribute, this.styleClassResolver);
		Value valueUnit = (Value)style.getNoResolve(attribute, this.styleClassResolver);
		Font font = null;
		if(valueUnit.unit.isFontBased()) {
			font = this.selectFont(style);
			if(font == null) {
				return 0;
			}
		}

		float value = valueUnit.value;
		switch($SWITCH_TABLE$de$matthiasmann$twl$textarea$Value$Unit()[valueUnit.unit.ordinal()]) {
		case 2:
			value *= (float)font.getEM();
			break;
		case 3:
			value *= (float)font.getEX();
			break;
		case 4:
			value *= (float)full * 0.01F;
			break;
		case 5:
			return auto;
		}

		return value >= 32767.0F ? 32767 : (value <= -32768.0F ? -32768 : Math.round(value));
	}

	int convertToPX0(Style style, StyleAttribute attribute, int full) {
		return Math.max(0, this.convertToPX(style, attribute, full, 0));
	}

	private Font selectFont(Style style) {
		String fontName = (String)style.get(StyleAttribute.FONT_NAME, this.styleClassResolver);
		if(fontName != null && this.fonts != null) {
			Font font = this.fonts.getFont(fontName);
			if(font != null) {
				return font;
			}
		}

		return this.defaultFont;
	}

	private Image selectImage(Style style, StyleAttribute element) {
		String imageName = (String)style.get(element, this.styleClassResolver);
		return imageName != null ? this.selectImage(imageName) : null;
	}

	private Image selectImage(String name) {
		Image image = (Image)this.userImages.get(name);
		if(image != null) {
			return image;
		} else {
			for(int i = 0; i < this.imageResolvers.size(); ++i) {
				image = ((TextArea.ImageResolver)this.imageResolvers.get(i)).resolveImage(name);
				if(image != null) {
					return image;
				}
			}

			return this.images != null ? this.images.getImage(name) : null;
		}
	}

	private void layoutParagraphElement(TextArea.Box box, TextAreaModel.ParagraphElement pe) {
		Style style = pe.getStyle();
		Font font = this.selectFont(style);
		this.doMarginTop(box, style);
		TextArea.LElement anchor = box.addAnchor(pe);
		box.setupTextParams(style, font, true);
		this.layoutElements(box, pe);
		if(box.textAlignment == TextAreaModel.HAlignment.JUSTIFY) {
			box.textAlignment = TextAreaModel.HAlignment.LEFT;
		}

		box.nextLine(false);
		box.inParagraph = false;
		anchor.height = box.curY - anchor.y;
		this.doMarginBottom(box, style);
	}

	private void layoutTextElement(TextArea.Box box, TextAreaModel.TextElement te) {
		String text = te.getText();
		Style style = te.getStyle();
		Font font = this.selectFont(style);
		boolean pre = ((Boolean)style.get(StyleAttribute.PREFORMATTED, this.styleClassResolver)).booleanValue();
		if(font != null) {
			box.setupTextParams(style, font, false);
			if(pre && !box.wasPreformatted) {
				box.nextLine(false);
			}

			int end;
			for(int idx = 0; idx < text.length(); idx = end) {
				end = TextUtil.indexOf(text, '\n', idx);
				if(pre) {
					this.layoutTextPre(box, te, font, text, idx, end);
				} else {
					this.layoutText(box, te, font, text, idx, end);
				}

				if(end < text.length() && text.charAt(end) == 10) {
					++end;
					box.nextLine(true);
				}
			}

			box.wasPreformatted = pre;
		}
	}

	private void layoutText(TextArea.Box box, TextAreaModel.TextElement te, Font font, String text, int textStart, int textEnd) {
		int idx;
		for(idx = textStart; textStart < textEnd && isSkip(text.charAt(textStart)); ++textStart) {
		}

		boolean endsWithSpace;
		for(endsWithSpace = false; textEnd > textStart && isSkip(text.charAt(textEnd - 1)); --textEnd) {
			endsWithSpace = true;
		}

		if(textStart > idx && box.prevOnLineEndsNotWithSpace()) {
			box.curX += font.getSpaceWidth();
		}

		Boolean breakWord = null;
		idx = textStart;

		while(true) {
			int end;
			boolean advancePastFloaters;
			while(true) {
				if(idx >= textEnd) {
					if(!box.isAtStartOfLine() && endsWithSpace) {
						box.curX += font.getSpaceWidth();
					}

					return;
				}

				assert !isSkip(text.charAt(idx));

				end = idx;
				int visibleEnd = idx;
				if(box.textAlignment != TextAreaModel.HAlignment.JUSTIFY) {
					end = idx + font.computeVisibleGlpyhs(text, idx, textEnd, box.getRemaining());
					visibleEnd = end;
					if(end < textEnd) {
						while(end > idx && isPunctuation(text.charAt(end))) {
							--end;
						}

						if(!isBreak(text.charAt(end))) {
							while(end > idx && !isBreak(text.charAt(end - 1))) {
								--end;
							}
						}
					}

					while(end > idx && isSkip(text.charAt(end - 1))) {
						--end;
					}
				}

				advancePastFloaters = false;
				if(end != idx) {
					break;
				}

				if(box.textAlignment == TextAreaModel.HAlignment.JUSTIFY || !box.nextLine(false)) {
					if(breakWord == null) {
						breakWord = (Boolean)te.getStyle().get(StyleAttribute.BREAKWORD, this.styleClassResolver);
					}

					if(breakWord.booleanValue()) {
						if(visibleEnd == idx) {
							end = idx + 1;
						} else {
							end = visibleEnd;
						}
					} else {
						while(end < textEnd && !isBreak(text.charAt(end))) {
							++end;
						}

						while(end < textEnd && isPunctuation(text.charAt(end))) {
							++end;
						}
					}

					advancePastFloaters = true;
					break;
				}
			}

			if(idx < end) {
				TextArea.LText lt = new TextArea.LText(te, font, text, idx, end);
				if(advancePastFloaters) {
					box.advancePastFloaters(lt.width, box.marginLeft, box.marginRight);
				}

				if(box.textAlignment == TextAreaModel.HAlignment.JUSTIFY && box.getRemaining() < lt.width) {
					box.nextLine(false);
				}

				int width = lt.width;
				if(end < textEnd && isSkip(text.charAt(end))) {
					width += font.getSpaceWidth();
				}

				lt.x = box.getXAndAdvance(width);
				lt.marginTop = (short)box.marginTop;
				lt.href = box.href;
				box.layout.add(lt);
			}

			for(idx = end; idx < textEnd && isSkip(text.charAt(idx)); ++idx) {
			}
		}
	}

	private void layoutTextPre(TextArea.Box box, TextAreaModel.TextElement te, Font font, String text, int textStart, int textEnd) {
		int idx = textStart;

		while(true) {
			int end;
			for(; idx < textEnd; idx = end) {
				int tabIdx;
				if(text.charAt(idx) == 9) {
					++idx;
					tabIdx = box.computeNextTabStop(font);
					if(tabIdx < box.lineWidth) {
						box.curX = tabIdx;
					} else if(!box.isAtStartOfLine()) {
						break;
					}
				}

				tabIdx = text.indexOf(9, idx);
				end = textEnd;
				if(tabIdx >= 0 && tabIdx < textEnd) {
					end = tabIdx;
				}

				if(end > idx) {
					int count = font.computeVisibleGlpyhs(text, idx, end, box.getRemaining());
					if(count == 0 && !box.isAtStartOfLine()) {
						break;
					}

					end = idx + Math.max(1, count);
					TextArea.LText lt = new TextArea.LText(te, font, text, idx, end);
					lt.x = box.getXAndAdvance(lt.width);
					lt.marginTop = (short)box.marginTop;
					box.layout.add(lt);
				}
			}

			if(idx >= textEnd) {
				return;
			}

			box.nextLine(false);
		}
	}

	private void doMarginTop(TextArea.Box box, Style style) {
		int marginTop = this.convertToPX0(style, StyleAttribute.MARGIN_TOP, box.boxWidth);
		box.nextLine(false);
		box.advanceToY(box.computeTopPadding(marginTop));
	}

	private void doMarginBottom(TextArea.Box box, Style style) {
		int marginBottom = this.convertToPX0(style, StyleAttribute.MARGIN_BOTTOM, box.boxWidth);
		box.setMarginBottom(marginBottom);
	}

	private void layoutContainerElement(TextArea.Box box, TextAreaModel.ContainerElement ce) {
		Style style = ce.getStyle();
		this.doMarginTop(box, style);
		box.addAnchor(ce);
		this.layoutElements(box, ce);
		this.doMarginBottom(box, style);
	}

	private void layoutLinkElement(TextArea.Box box, TextAreaModel.LinkElement le) {
		String oldHref = box.href;
		box.href = le.getHREF();
		this.layoutContainerElement(box, le);
		box.href = oldHref;
	}

	private void layoutListElement(TextArea.Box box, TextAreaModel.ListElement le) {
		Style style = le.getStyle();
		this.doMarginTop(box, style);
		Image image = this.selectImage(style, StyleAttribute.LIST_STYLE_IMAGE);
		if(image != null) {
			TextArea.LImage li = new TextArea.LImage(le, image);
			li.width += this.convertToPX0(style, StyleAttribute.PADDING_LEFT, box.boxWidth);
			this.layout(box, le, li, TextAreaModel.FloatPosition.LEFT, TextAreaModel.Display.BLOCK);
			int imageHeight = li.height;
			li.height = 32767;
			this.layoutElements(box, le);
			li.height = imageHeight;
			box.objLeft.remove(li);
			box.advanceToY(li.bottom());
			box.computePadding();
		} else {
			this.layoutElements(box, le);
			box.nextLine(false);
		}

		this.doMarginBottom(box, style);
	}

	private void layoutOrderedListElement(TextArea.Box box, TextAreaModel.OrderedListElement ole) {
		Style style = ole.getStyle();
		Font font = this.selectFont(style);
		if(font != null) {
			this.doMarginTop(box, style);
			TextArea.LElement anchor = box.addAnchor(ole);
			int start = Math.max(1, ole.getStart());
			int count = ole.getNumElements();
			OrderedListType type = (OrderedListType)style.get(StyleAttribute.LIST_STYLE_TYPE, this.styleClassResolver);
			String[] labels = new String[count];
			int maxLabelWidth = this.convertToPX0(style, StyleAttribute.PADDING_LEFT, box.boxWidth);

			int i;
			for(i = 0; i < count; ++i) {
				labels[i] = type.format(start + i).concat(". ");
				int label = font.computeTextWidth(labels[i]);
				maxLabelWidth = Math.max(maxLabelWidth, label);
			}

			for(i = 0; i < count; ++i) {
				String string18 = labels[i];
				TextAreaModel.Element li = ole.getElement(i);
				Style liStyle = li.getStyle();
				this.doMarginTop(box, liStyle);
				TextArea.LText lt = new TextArea.LText(ole, font, string18, 0, string18.length());
				int labelWidth = lt.width;
				int labelHeight = lt.height;
				lt.width += this.convertToPX0(liStyle, StyleAttribute.PADDING_LEFT, box.boxWidth);
				this.layout(box, ole, lt, TextAreaModel.FloatPosition.LEFT, TextAreaModel.Display.BLOCK);
				lt.x += Math.max(0, maxLabelWidth - labelWidth);
				lt.height = 32767;
				this.layoutElement(box, li);
				lt.height = labelHeight;
				box.objLeft.remove(lt);
				box.advanceToY(lt.bottom());
				box.computePadding();
				this.doMarginBottom(box, liStyle);
			}

			anchor.height = box.curY - anchor.y;
			this.doMarginBottom(box, style);
		}
	}

	private TextArea.Box layoutBox(TextArea.LClip clip, int continerWidth, int paddingLeft, int paddingRight, TextAreaModel.ContainerElement ce) {
		Style style = ce.getStyle();
		int paddingTop = this.convertToPX0(style, StyleAttribute.PADDING_TOP, continerWidth);
		int paddingBottom = this.convertToPX0(style, StyleAttribute.PADDING_BOTTOM, continerWidth);
		int marginBottom = this.convertToPX0(style, StyleAttribute.MARGIN_BOTTOM, continerWidth);
		TextArea.Box box = new TextArea.Box(clip, paddingLeft, paddingRight, paddingTop);
		this.layoutElements(box, ce);
		box.finish();
		clip.height = box.curY + paddingBottom;
		clip.height = Math.max(clip.height, this.convertToPX(style, StyleAttribute.HEIGHT, clip.height, clip.height));
		clip.marginBottom = (short)Math.max(marginBottom, box.marginBottomAbs - box.curY);
		return box;
	}

	private void layoutBlockElement(TextArea.Box box, TextAreaModel.BlockElement be) {
		box.nextLine(false);
		Style style = be.getStyle();
		TextAreaModel.FloatPosition floatPosition = (TextAreaModel.FloatPosition)style.get(StyleAttribute.FLOAT_POSITION, this.styleClassResolver);
		TextArea.LImage bgImage = this.createBGImage(box, be);
		int marginTop = this.convertToPX0(style, StyleAttribute.MARGIN_TOP, box.boxWidth);
		int marginLeft = this.convertToPX0(style, StyleAttribute.MARGIN_LEFT, box.boxWidth);
		int marginRight = this.convertToPX0(style, StyleAttribute.MARGIN_RIGHT, box.boxWidth);
		int bgX = box.computeLeftPadding(marginLeft);
		int bgY = box.computeTopPadding(marginTop);
		int remaining = Math.max(0, box.computeRightPadding(marginRight) - bgX);
		int bgWidth;
		if(floatPosition == TextAreaModel.FloatPosition.NONE) {
			bgWidth = remaining;
		} else {
			bgWidth = this.convertToPX(style, StyleAttribute.WIDTH, box.boxWidth, box.lineWidth);
		}

		int paddingLeft = this.convertToPX0(style, StyleAttribute.PADDING_LEFT, bgWidth);
		int paddingRight = this.convertToPX0(style, StyleAttribute.PADDING_RIGHT, bgWidth);
		bgWidth += paddingLeft + paddingRight;
		if(floatPosition != TextAreaModel.FloatPosition.NONE) {
			box.advancePastFloaters(bgWidth, marginLeft, marginRight);
			bgX = box.computeLeftPadding(marginLeft);
			bgY = Math.max(bgY, box.curY);
			remaining = Math.max(0, box.computeRightPadding(marginRight) - bgX);
		}

		bgWidth = Math.max(0, Math.min(bgWidth, remaining));
		if(floatPosition == TextAreaModel.FloatPosition.RIGHT) {
			bgX = box.computeRightPadding(marginRight) - bgWidth;
		}

		TextArea.LClip clip = new TextArea.LClip(be);
		clip.x = bgX;
		clip.y = bgY;
		clip.width = bgWidth;
		clip.marginLeft = (short)marginLeft;
		clip.marginRight = (short)marginRight;
		box.layout.add(clip);
		this.layoutBox(clip, box.boxWidth, paddingLeft, paddingRight, be);
		box.lineStartIdx = box.layout.size();
		if(floatPosition == TextAreaModel.FloatPosition.NONE) {
			box.advanceToY(bgY + clip.height);
			box.setMarginBottom(clip.marginBottom);
		} else {
			if(floatPosition == TextAreaModel.FloatPosition.RIGHT) {
				box.objRight.add(clip);
			} else {
				box.objLeft.add(clip);
			}

			box.computePadding();
		}

		if(bgImage != null) {
			bgImage.x = bgX;
			bgImage.y = bgY;
			bgImage.width = bgWidth;
			bgImage.height = clip.height;
		}

	}

	private void layoutTableElement(TextArea.Box box, TextAreaModel.TableElement te) {
		int numColumns = te.getNumColumns();
		int numRows = te.getNumRows();
		int cellSpacing = te.getCellSpacing();
		int cellPadding = te.getCellPadding();
		Style tableStyle = te.getStyle();
		if(numColumns != 0 && numRows != 0) {
			this.doMarginTop(box, tableStyle);
			TextArea.LElement anchor = box.addAnchor(te);
			int left = box.computeLeftPadding(this.convertToPX0(tableStyle, StyleAttribute.MARGIN_LEFT, box.boxWidth));
			int right = box.computeRightPadding(this.convertToPX0(tableStyle, StyleAttribute.MARGIN_RIGHT, box.boxWidth));
			int tableWidth = Math.min(right - left, this.convertToPX0(tableStyle, StyleAttribute.WIDTH, box.boxWidth));
			if(tableWidth <= 0) {
				tableWidth = Math.max(0, right - left);
			}

			int[] columnWidth = new int[numColumns];
			int[] columnSpacing = new int[numColumns + 1];
			int columnWidthSum = 0;
			int columnsWithoutWidth = 0;
			columnSpacing[0] = Math.max(cellSpacing, this.convertToPX0(tableStyle, StyleAttribute.PADDING_LEFT, box.boxWidth));

			int columnSpacingSum;
			int availableColumnWidth;
			int tableBGImage;
			int bgImages;
			int row;
			Style rowStyle;
			for(columnSpacingSum = 0; columnSpacingSum < numColumns; ++columnSpacingSum) {
				availableColumnWidth = 0;
				tableBGImage = 0;
				bgImages = 0;

				for(row = 0; row < numRows; ++row) {
					TextAreaModel.TableCellElement rowBGImage = te.getCell(row, columnSpacingSum);
					if(rowBGImage != null && rowBGImage.getColspan() == 1) {
						rowStyle = rowBGImage.getStyle();
						availableColumnWidth = Math.max(availableColumnWidth, this.convertToPX(rowStyle, StyleAttribute.WIDTH, tableWidth, tableWidth / numColumns));
						tableBGImage = Math.max(tableBGImage, this.convertToPX(rowStyle, StyleAttribute.MARGIN_LEFT, tableWidth, 0));
						bgImages = Math.max(bgImages, this.convertToPX(rowStyle, StyleAttribute.MARGIN_LEFT, tableWidth, 0));
					}
				}

				columnWidth[columnSpacingSum] = availableColumnWidth;
				columnSpacing[columnSpacingSum] = Math.max(columnSpacing[columnSpacingSum], tableBGImage);
				columnSpacing[columnSpacingSum + 1] = Math.max(cellSpacing, bgImages);
				columnWidthSum += availableColumnWidth;
				if(availableColumnWidth <= 0) {
					++columnsWithoutWidth;
				}
			}

			columnSpacing[numColumns] = Math.max(columnSpacing[numColumns], this.convertToPX0(tableStyle, StyleAttribute.PADDING_RIGHT, box.boxWidth));
			columnSpacingSum = 0;
			int[] i32 = columnSpacing;
			bgImages = columnSpacing.length;

			for(tableBGImage = 0; tableBGImage < bgImages; ++tableBGImage) {
				availableColumnWidth = i32[tableBGImage];
				columnSpacingSum += availableColumnWidth;
			}

			if(columnsWithoutWidth > 0) {
				availableColumnWidth = Math.max(0, tableWidth - columnSpacingSum - columnWidthSum);

				for(tableBGImage = 0; tableBGImage < numColumns; ++tableBGImage) {
					if(columnWidth[tableBGImage] <= 0) {
						bgImages = availableColumnWidth / columnsWithoutWidth;
						columnWidth[tableBGImage] = bgImages;
						--columnsWithoutWidth;
						availableColumnWidth -= bgImages;
						columnWidthSum += bgImages;
					}
				}
			}

			availableColumnWidth = Math.max(0, tableWidth - columnSpacingSum);
			if(availableColumnWidth != columnWidthSum && columnWidthSum > 0) {
				tableBGImage = availableColumnWidth;
				bgImages = columnWidthSum;

				for(row = 0; row < numColumns; ++row) {
					int i35 = columnWidth[row];
					int i37 = bgImages > 0 ? i35 * tableBGImage / bgImages : 0;
					columnWidth[row] = i37;
					tableBGImage -= i37;
					bgImages -= i35;
				}
			}

			TextArea.LImage textArea$LImage33 = this.createBGImage(box, te);
			box.textAlignment = TextAreaModel.HAlignment.LEFT;
			box.curY += Math.max(cellSpacing, this.convertToPX0(tableStyle, StyleAttribute.PADDING_TOP, box.boxWidth));
			TextArea.LImage[] textArea$LImage34 = new TextArea.LImage[numColumns];

			for(row = 0; row < numRows; ++row) {
				if(row > 0) {
					box.curY += cellSpacing;
				}

				TextArea.LImage textArea$LImage36 = null;
				rowStyle = te.getRowStyle(row);
				int x;
				if(rowStyle != null) {
					x = this.convertToPX0(rowStyle, StyleAttribute.MARGIN_TOP, tableWidth);
					box.curY = box.computeTopPadding(x);
					Image col = this.selectImage(rowStyle, StyleAttribute.BACKGROUND_IMAGE);
					if(col != null) {
						textArea$LImage36 = new TextArea.LImage(te, col);
						textArea$LImage36.y = box.curY;
						textArea$LImage36.x = left;
						textArea$LImage36.width = tableWidth;
						box.clip.bgImages.add(textArea$LImage36);
					}

					box.curY += this.convertToPX0(rowStyle, StyleAttribute.PADDING_TOP, tableWidth);
					box.minLineHeight = this.convertToPX0(rowStyle, StyleAttribute.HEIGHT, tableWidth);
				}

				x = left;

				int i38;
				for(i38 = 0; i38 < numColumns; ++i38) {
					x += columnSpacing[i38];
					TextAreaModel.TableCellElement bgImage = te.getCell(row, i38);
					int width = columnWidth[i38];
					if(bgImage != null) {
						for(int cellStyle = 1; cellStyle < bgImage.getColspan(); ++cellStyle) {
							width += columnSpacing[i38 + cellStyle] + columnWidth[i38 + cellStyle];
						}

						Style style40 = bgImage.getStyle();
						int paddingLeft = Math.max(cellPadding, this.convertToPX0(style40, StyleAttribute.PADDING_LEFT, tableWidth));
						int paddingRight = Math.max(cellPadding, this.convertToPX0(style40, StyleAttribute.PADDING_RIGHT, tableWidth));
						TextArea.LImage bgImage1 = this.createBGImage(box, bgImage);
						if(bgImage1 != null) {
							bgImage1.x = x;
							bgImage1.width = width;
							textArea$LImage34[i38] = bgImage1;
						}

						TextArea.LClip clip = new TextArea.LClip(bgImage);
						clip.x = x;
						clip.y = box.curY;
						clip.width = width;
						clip.marginTop = (short)this.convertToPX0(style40, StyleAttribute.MARGIN_TOP, tableWidth);
						box.layout.add(clip);
						this.layoutBox(clip, tableWidth, paddingLeft, paddingRight, bgImage);
						i38 += Math.max(0, bgImage.getColspan() - 1);
					}

					x += width;
				}

				box.nextLine(false);

				for(i38 = 0; i38 < numColumns; ++i38) {
					TextArea.LImage textArea$LImage39 = textArea$LImage34[i38];
					if(textArea$LImage39 != null) {
						textArea$LImage39.height = box.curY - textArea$LImage39.y;
						textArea$LImage34[i38] = null;
					}
				}

				if(rowStyle != null) {
					box.curY += this.convertToPX0(rowStyle, StyleAttribute.PADDING_BOTTOM, tableWidth);
					if(textArea$LImage36 != null) {
						textArea$LImage36.height = box.curY - textArea$LImage36.y;
					}

					this.doMarginBottom(box, rowStyle);
				}
			}

			box.curY += Math.max(cellSpacing, this.convertToPX0(tableStyle, StyleAttribute.PADDING_BOTTOM, box.boxWidth));
			box.checkFloaters();
			if(textArea$LImage33 != null) {
				textArea$LImage33.height = box.curY - textArea$LImage33.y;
				textArea$LImage33.x = left;
				textArea$LImage33.width = tableWidth;
			}

			anchor.x = left;
			anchor.width = tableWidth;
			anchor.height = box.curY - anchor.y;
			this.doMarginBottom(box, tableStyle);
		}
	}

	private TextArea.LImage createBGImage(TextArea.Box box, TextAreaModel.Element element) {
		Image image = this.selectImage(element.getStyle(), StyleAttribute.BACKGROUND_IMAGE);
		if(image != null) {
			TextArea.LImage bgImage = new TextArea.LImage(element, image);
			bgImage.y = box.curY;
			box.clip.bgImages.add(bgImage);
			return bgImage;
		} else {
			return null;
		}
	}

	static boolean isSkip(char ch) {
		return Character.isWhitespace(ch);
	}

	static boolean isPunctuation(char ch) {
		return ":;,.-!?".indexOf(ch) >= 0;
	}

	static boolean isBreak(char ch) {
		return Character.isWhitespace(ch) || isPunctuation(ch);
	}

	static int[] $SWITCH_TABLE$de$matthiasmann$twl$textarea$TextAreaModel$HAlignment() {
		int[] i10000 = $SWITCH_TABLE$de$matthiasmann$twl$textarea$TextAreaModel$HAlignment;
		if($SWITCH_TABLE$de$matthiasmann$twl$textarea$TextAreaModel$HAlignment != null) {
			return i10000;
		} else {
			int[] i0 = new int[TextAreaModel.HAlignment.values().length];

			try {
				i0[TextAreaModel.HAlignment.CENTER.ordinal()] = 3;
			} catch (NoSuchFieldError noSuchFieldError4) {
			}

			try {
				i0[TextAreaModel.HAlignment.JUSTIFY.ordinal()] = 4;
			} catch (NoSuchFieldError noSuchFieldError3) {
			}

			try {
				i0[TextAreaModel.HAlignment.LEFT.ordinal()] = 1;
			} catch (NoSuchFieldError noSuchFieldError2) {
			}

			try {
				i0[TextAreaModel.HAlignment.RIGHT.ordinal()] = 2;
			} catch (NoSuchFieldError noSuchFieldError1) {
			}

			$SWITCH_TABLE$de$matthiasmann$twl$textarea$TextAreaModel$HAlignment = i0;
			return i0;
		}
	}

	static int[] $SWITCH_TABLE$de$matthiasmann$twl$textarea$Value$Unit() {
		int[] i10000 = $SWITCH_TABLE$de$matthiasmann$twl$textarea$Value$Unit;
		if($SWITCH_TABLE$de$matthiasmann$twl$textarea$Value$Unit != null) {
			return i10000;
		} else {
			int[] i0 = new int[Value.Unit.values().length];

			try {
				i0[Value.Unit.AUTO.ordinal()] = 5;
			} catch (NoSuchFieldError noSuchFieldError5) {
			}

			try {
				i0[Value.Unit.EM.ordinal()] = 2;
			} catch (NoSuchFieldError noSuchFieldError4) {
			}

			try {
				i0[Value.Unit.EX.ordinal()] = 3;
			} catch (NoSuchFieldError noSuchFieldError3) {
			}

			try {
				i0[Value.Unit.PERCENT.ordinal()] = 4;
			} catch (NoSuchFieldError noSuchFieldError2) {
			}

			try {
				i0[Value.Unit.PX.ordinal()] = 1;
			} catch (NoSuchFieldError noSuchFieldError1) {
			}

			$SWITCH_TABLE$de$matthiasmann$twl$textarea$Value$Unit = i0;
			return i0;
		}
	}

	class Box {
		final TextArea.LClip clip;
		final ArrayList layout;
		final ArrayList objLeft = new ArrayList();
		final ArrayList objRight = new ArrayList();
		final StringBuilder lineInfo = new StringBuilder();
		final int boxLeft;
		final int boxWidth;
		final int boxMarginOffsetLeft;
		final int boxMarginOffsetRight;
		int curY;
		int curX;
		int lineStartIdx;
		int lastProcessedAnchorIdx;
		int marginTop;
		int marginLeft;
		int marginRight;
		int marginBottomAbs;
		int marginBottomNext;
		int lineStartX;
		int lineWidth;
		int fontLineHeight;
		int minLineHeight;
		int lastLineEnd;
		int lastLineBottom;
		boolean inParagraph;
		boolean wasAutoBreak;
		boolean wasPreformatted;
		TextAreaModel.HAlignment textAlignment;
		String href;
		private static int[] $SWITCH_TABLE$de$matthiasmann$twl$textarea$TextAreaModel$HAlignment;
		private static int[] $SWITCH_TABLE$de$matthiasmann$twl$textarea$TextAreaModel$VAlignment;

		public Box(TextArea.LClip clip, int paddingLeft, int paddingRight, int paddingTop) {
			this.clip = clip;
			this.layout = clip.layout;
			this.boxLeft = paddingLeft;
			this.boxWidth = Math.max(0, clip.width - paddingLeft - paddingRight);
			this.boxMarginOffsetLeft = paddingLeft;
			this.boxMarginOffsetRight = paddingRight;
			this.curX = this.boxLeft;
			this.curY = paddingTop;
			this.lineStartX = this.boxLeft;
			this.lineWidth = this.boxWidth;
			this.textAlignment = TextAreaModel.HAlignment.LEFT;

			assert this.layout.isEmpty();

		}

		void computePadding() {
			int left = this.computeLeftPadding(this.marginLeft);
			int right = this.computeRightPadding(this.marginRight);
			this.lineStartX = left;
			this.lineWidth = Math.max(0, right - left);
			if(this.isAtStartOfLine()) {
				this.curX = this.lineStartX;
			}

		}

		int computeLeftPadding(int marginLeft) {
			int left = this.boxLeft + Math.max(0, marginLeft - this.boxMarginOffsetLeft);
			int i = 0;

			for(int n = this.objLeft.size(); i < n; ++i) {
				TextArea.LElement e = (TextArea.LElement)this.objLeft.get(i);
				left = Math.max(left, e.x + e.width + Math.max(e.marginRight, marginLeft));
			}

			return left;
		}

		int computeRightPadding(int marginRight) {
			int right = this.boxLeft + this.boxWidth - Math.max(0, marginRight - this.boxMarginOffsetRight);
			int i = 0;

			for(int n = this.objRight.size(); i < n; ++i) {
				TextArea.LElement e = (TextArea.LElement)this.objRight.get(i);
				right = Math.min(right, e.x - Math.max(e.marginLeft, marginRight));
			}

			return right;
		}

		int computePaddingWidth(int marginLeft, int marginRight) {
			return Math.max(0, this.computeRightPadding(marginRight) - this.computeLeftPadding(marginLeft));
		}

		int computeTopPadding(int marginTop) {
			return Math.max(this.marginBottomAbs, this.curY + marginTop);
		}

		void setMarginBottom(int marginBottom) {
			if(this.isAtStartOfLine()) {
				this.marginBottomAbs = Math.max(this.marginBottomAbs, this.curY + marginBottom);
			} else {
				this.marginBottomNext = Math.max(this.marginBottomNext, marginBottom);
			}

		}

		int getRemaining() {
			return Math.max(0, this.lineWidth - this.curX + this.lineStartX);
		}

		int getXAndAdvance(int amount) {
			int x = this.curX;
			this.curX = x + amount;
			return x;
		}

		boolean isAtStartOfLine() {
			return this.lineStartIdx == this.layout.size();
		}

		boolean prevOnLineEndsNotWithSpace() {
			int layoutSize = this.layout.size();
			if(this.lineStartIdx < layoutSize) {
				TextArea.LElement le = (TextArea.LElement)this.layout.get(layoutSize - 1);
				if(le instanceof TextArea.LText) {
					TextArea.LText lt = (TextArea.LText)le;
					return !Character.isWhitespace(lt.text.charAt(lt.end - 1));
				} else {
					return true;
				}
			} else {
				return false;
			}
		}

		void checkFloaters() {
			this.removeObjFromList(this.objLeft);
			this.removeObjFromList(this.objRight);
			this.computePadding();
		}

		void clearFloater(TextAreaModel.Clear clear) {
			if(clear != TextAreaModel.Clear.NONE) {
				int targetY = -1;
				int i;
				int n;
				TextArea.LElement le;
				if(clear == TextAreaModel.Clear.LEFT || clear == TextAreaModel.Clear.BOTH) {
					i = 0;

					for(n = this.objLeft.size(); i < n; ++i) {
						le = (TextArea.LElement)this.objLeft.get(i);
						if(le.height != 32767) {
							targetY = Math.max(targetY, le.y + le.height);
						}
					}
				}

				if(clear == TextAreaModel.Clear.RIGHT || clear == TextAreaModel.Clear.BOTH) {
					i = 0;

					for(n = this.objRight.size(); i < n; ++i) {
						le = (TextArea.LElement)this.objRight.get(i);
						targetY = Math.max(targetY, le.y + le.height);
					}
				}

				if(targetY >= 0) {
					this.advanceToY(targetY);
				}
			}

		}

		void advanceToY(int targetY) {
			this.nextLine(false);
			if(targetY > this.curY) {
				this.curY = targetY;
				this.checkFloaters();
			}

		}

		void advancePastFloaters(int requiredWidth, int marginLeft, int marginRight) {
			if(this.computePaddingWidth(marginLeft, marginRight) < requiredWidth) {
				this.nextLine(false);

				do {
					int targetY = Integer.MAX_VALUE;
					TextArea.LElement le;
					if(!this.objLeft.isEmpty()) {
						le = (TextArea.LElement)this.objLeft.get(this.objLeft.size() - 1);
						if(le.height != 32767) {
							targetY = Math.min(targetY, le.bottom());
						}
					}

					if(!this.objRight.isEmpty()) {
						le = (TextArea.LElement)this.objRight.get(this.objRight.size() - 1);
						targetY = Math.min(targetY, le.bottom());
					}

					if(targetY == Integer.MAX_VALUE || targetY < this.curY) {
						return;
					}

					this.curY = targetY;
					this.checkFloaters();
				} while(this.computePaddingWidth(marginLeft, marginRight) < requiredWidth);
			}

		}

		boolean nextLine(boolean force) {
			if(!this.isAtStartOfLine() || !this.wasAutoBreak && force) {
				int targetY = this.curY;
				int lineHeight = this.minLineHeight;
				if(this.isAtStartOfLine()) {
					lineHeight = Math.max(lineHeight, this.fontLineHeight);
				} else {
					for(int lastElement = this.lineStartIdx; lastElement < this.layout.size(); ++lastElement) {
						TextArea.LElement remaining = (TextArea.LElement)this.layout.get(lastElement);
						lineHeight = Math.max(lineHeight, remaining.height);
					}

					int idx;
					TextArea.LElement textArea$LElement12;
					TextArea.LElement textArea$LElement10 = (TextArea.LElement)this.layout.get(this.layout.size() - 1);
					int i11 = this.lineStartX + this.lineWidth - (textArea$LElement10.x + textArea$LElement10.width);
					int le;
					TextArea.LElement le1;
					label73:
					switch($SWITCH_TABLE$de$matthiasmann$twl$textarea$TextAreaModel$HAlignment()[this.textAlignment.ordinal()]) {
					case 2:
						idx = this.lineStartIdx;

						while(true) {
							if(idx >= this.layout.size()) {
								break label73;
							}

							textArea$LElement12 = (TextArea.LElement)this.layout.get(idx);
							textArea$LElement12.x += i11;
							++idx;
						}
					case 3:
						idx = i11 / 2;
						le = this.lineStartIdx;

						while(true) {
							if(le >= this.layout.size()) {
								break label73;
							}

							le1 = (TextArea.LElement)this.layout.get(le);
							le1.x += idx;
							++le;
						}
					case 4:
						if(i11 < this.lineWidth / 4) {
							idx = this.layout.size() - this.lineStartIdx;

							for(le = 1; le < idx; ++le) {
								le1 = (TextArea.LElement)this.layout.get(this.lineStartIdx + le);
								int offset = i11 * le / (idx - 1);
								le1.x += offset;
							}
						}
					}

					for(idx = this.lineStartIdx; idx < this.layout.size(); ++idx) {
						textArea$LElement12 = (TextArea.LElement)this.layout.get(idx);
						switch($SWITCH_TABLE$de$matthiasmann$twl$textarea$TextAreaModel$VAlignment()[((TextAreaModel.VAlignment)textArea$LElement12.element.getStyle().get(StyleAttribute.VERTICAL_ALIGNMENT, TextArea.this.styleClassResolver)).ordinal()]) {
						case 1:
							textArea$LElement12.y = 0;
							break;
						case 2:
							textArea$LElement12.y = (lineHeight - textArea$LElement12.height) / 2;
							break;
						case 3:
							textArea$LElement12.y = lineHeight - textArea$LElement12.height;
							break;
						case 4:
							textArea$LElement12.y = 0;
							textArea$LElement12.height = lineHeight;
						}

						targetY = Math.max(targetY, this.computeTopPadding(textArea$LElement12.marginTop - textArea$LElement12.y));
						this.marginBottomNext = Math.max(this.marginBottomNext, textArea$LElement12.bottom() - lineHeight);
					}

					for(idx = this.lineStartIdx; idx < this.layout.size(); ++idx) {
						textArea$LElement12 = (TextArea.LElement)this.layout.get(idx);
						textArea$LElement12.y += targetY;
					}
				}

				this.processAnchors(targetY, lineHeight);
				this.minLineHeight = 0;
				this.lineStartIdx = this.layout.size();
				this.wasAutoBreak = !force;
				this.curY = targetY + lineHeight;
				this.marginBottomAbs = Math.max(this.marginBottomAbs, this.curY + this.marginBottomNext);
				this.marginBottomNext = 0;
				this.marginTop = 0;
				this.checkFloaters();
				return true;
			} else {
				return false;
			}
		}

		void finish() {
			this.nextLine(false);
			this.clearFloater(TextAreaModel.Clear.BOTH);
			this.processAnchors(this.curY, 0);
			int lineInfoLength = this.lineInfo.length();
			this.clip.lineInfo = new char[lineInfoLength];
			this.lineInfo.getChars(0, lineInfoLength, this.clip.lineInfo, 0);
		}

		int computeNextTabStop(Font font) {
			int x = this.curX - this.lineStartX + font.getSpaceWidth();
			int tabSize = 8 * font.getEM();
			return this.curX + tabSize - x % tabSize;
		}

		private void removeObjFromList(ArrayList list) {
			int i = list.size();

			while(i-- > 0) {
				TextArea.LElement e = (TextArea.LElement)list.get(i);
				if(e.bottom() <= this.curY) {
					list.remove(i);
				}
			}

		}

		void setupTextParams(Style style, Font font, boolean isParagraphStart) {
			if(font != null) {
				this.fontLineHeight = font.getLineHeight();
			} else {
				this.fontLineHeight = 0;
			}

			if(isParagraphStart) {
				this.nextLine(false);
				this.inParagraph = true;
			}

			if(isParagraphStart || !this.inParagraph && this.isAtStartOfLine()) {
				this.marginLeft = TextArea.this.convertToPX0(style, StyleAttribute.MARGIN_LEFT, this.boxWidth);
				this.marginRight = TextArea.this.convertToPX0(style, StyleAttribute.MARGIN_RIGHT, this.boxWidth);
				this.textAlignment = (TextAreaModel.HAlignment)style.get(StyleAttribute.HORIZONTAL_ALIGNMENT, TextArea.this.styleClassResolver);
				this.computePadding();
				this.curX = Math.max(0, this.lineStartX + TextArea.this.convertToPX(style, StyleAttribute.TEXT_IDENT, this.boxWidth, 0));
			}

			this.marginTop = TextArea.this.convertToPX0(style, StyleAttribute.MARGIN_TOP, this.boxWidth);
		}

		TextArea.LElement addAnchor(TextAreaModel.Element e) {
			TextArea.LElement le = new TextArea.LElement(e);
			le.y = this.curY;
			le.x = this.boxLeft;
			le.width = this.boxWidth;
			this.clip.anchors.add(le);
			return le;
		}

		private void processAnchors(int y, int height) {
			while(this.lastProcessedAnchorIdx < this.clip.anchors.size()) {
				TextArea.LElement le = (TextArea.LElement)this.clip.anchors.get(this.lastProcessedAnchorIdx++);
				if(le.height == 0) {
					le.y = y;
					le.height = height;
				}
			}

			if(this.lineStartIdx > this.lastLineEnd) {
				this.lineInfo.append('\u0000').append((char)(this.lineStartIdx - this.lastLineEnd));
			}

			if(y > this.lastLineBottom) {
				this.lineInfo.append((char)y).append('\u0000');
			}

			this.lastLineBottom = y + height;
			this.lineInfo.append((char)this.lastLineBottom).append((char)(this.layout.size() - this.lineStartIdx));
			this.lastLineEnd = this.layout.size();
		}

		static int[] $SWITCH_TABLE$de$matthiasmann$twl$textarea$TextAreaModel$HAlignment() {
			int[] i10000 = $SWITCH_TABLE$de$matthiasmann$twl$textarea$TextAreaModel$HAlignment;
			if($SWITCH_TABLE$de$matthiasmann$twl$textarea$TextAreaModel$HAlignment != null) {
				return i10000;
			} else {
				int[] i0 = new int[TextAreaModel.HAlignment.values().length];

				try {
					i0[TextAreaModel.HAlignment.CENTER.ordinal()] = 3;
				} catch (NoSuchFieldError noSuchFieldError4) {
				}

				try {
					i0[TextAreaModel.HAlignment.JUSTIFY.ordinal()] = 4;
				} catch (NoSuchFieldError noSuchFieldError3) {
				}

				try {
					i0[TextAreaModel.HAlignment.LEFT.ordinal()] = 1;
				} catch (NoSuchFieldError noSuchFieldError2) {
				}

				try {
					i0[TextAreaModel.HAlignment.RIGHT.ordinal()] = 2;
				} catch (NoSuchFieldError noSuchFieldError1) {
				}

				$SWITCH_TABLE$de$matthiasmann$twl$textarea$TextAreaModel$HAlignment = i0;
				return i0;
			}
		}

		static int[] $SWITCH_TABLE$de$matthiasmann$twl$textarea$TextAreaModel$VAlignment() {
			int[] i10000 = $SWITCH_TABLE$de$matthiasmann$twl$textarea$TextAreaModel$VAlignment;
			if($SWITCH_TABLE$de$matthiasmann$twl$textarea$TextAreaModel$VAlignment != null) {
				return i10000;
			} else {
				int[] i0 = new int[TextAreaModel.VAlignment.values().length];

				try {
					i0[TextAreaModel.VAlignment.BOTTOM.ordinal()] = 3;
				} catch (NoSuchFieldError noSuchFieldError4) {
				}

				try {
					i0[TextAreaModel.VAlignment.FILL.ordinal()] = 4;
				} catch (NoSuchFieldError noSuchFieldError3) {
				}

				try {
					i0[TextAreaModel.VAlignment.MIDDLE.ordinal()] = 2;
				} catch (NoSuchFieldError noSuchFieldError2) {
				}

				try {
					i0[TextAreaModel.VAlignment.TOP.ordinal()] = 1;
				} catch (NoSuchFieldError noSuchFieldError1) {
				}

				$SWITCH_TABLE$de$matthiasmann$twl$textarea$TextAreaModel$VAlignment = i0;
				return i0;
			}
		}
	}

	public interface Callback {
		void handleLinkClicked(String string1);
	}

	public interface ImageResolver {
		Image resolveImage(String string1);
	}

	class LClip extends TextArea.LElement {
		final ArrayList layout = new ArrayList();
		final ArrayList bgImages = new ArrayList();
		final ArrayList anchors = new ArrayList();
		char[] lineInfo;

		public LClip(TextAreaModel.Element element) {
			super(element);
		}

		void draw(int offX, int offY, AnimationState as) {
			offX += this.x;
			offY += this.y;
			GUI gui = TextArea.this.getGUI();
			gui.clipEnter(offX, offY, this.width, this.height);

			try {
				if(!gui.clipEmpty()) {
					this.drawNoClip(offX, offY, as);
				}
			} finally {
				gui.clipLeave();
			}

		}

		void drawNoClip(int offX, int offY, AnimationState as) {
			ArrayList ll = this.layout;
			TextAreaModel.Element hoverElement;
			if(TextArea.this.curLElementUnderMouse != null) {
				hoverElement = TextArea.this.curLElementUnderMouse.element;
			} else {
				hoverElement = null;
			}

			int i = 0;

			for(int n = ll.size(); i < n; ++i) {
				TextArea.LElement le = (TextArea.LElement)ll.get(i);
				as.setAnimationState(TextArea.STATE_HOVER, hoverElement == le.element);
				le.draw(offX, offY, as);
			}

		}

		void adjustWidget(int offX, int offY) {
			offX += this.x;
			offY += this.y;
			int i = 0;

			int n;
			for(n = this.layout.size(); i < n; ++i) {
				((TextArea.LElement)this.layout.get(i)).adjustWidget(offX, offY);
			}

			offX -= TextArea.this.getInnerX();
			offY -= TextArea.this.getInnerY();
			i = 0;

			for(n = this.bgImages.size(); i < n; ++i) {
				TextArea.LImage img = (TextArea.LImage)this.bgImages.get(i);
				img.x += offX;
				img.y += offY;
				TextArea.this.allBGImages.add(img);
			}

		}

		void destroy() {
			int i = 0;

			for(int n = this.layout.size(); i < n; ++i) {
				((TextArea.LElement)this.layout.get(i)).destroy();
			}

			this.layout.clear();
			this.bgImages.clear();
		}

		TextArea.LElement find(int x, int y) {
			x -= this.x;
			y -= this.y;
			char lineTop = 0;
			int layoutIdx = 0;
			int lineIdx = 0;

			while(lineIdx < this.lineInfo.length && y >= lineTop) {
				char lineBottom = this.lineInfo[lineIdx++];
				char layoutCount = this.lineInfo[lineIdx++];
				if(layoutCount > 0) {
					if(lineBottom == 0 || y < lineBottom) {
						for(int prev = 0; prev < layoutCount; ++prev) {
							TextArea.LElement i = (TextArea.LElement)this.layout.get(layoutIdx + prev);
							if(i.isInside(x, y)) {
								return i.find(x, y);
							}
						}

						if(lineBottom > 0 && x >= ((TextArea.LElement)this.layout.get(layoutIdx)).x) {
							TextArea.LElement textArea$LElement11 = null;

							for(int i12 = 0; i12 < layoutCount; ++i12) {
								TextArea.LElement le = (TextArea.LElement)this.layout.get(layoutIdx + i12);
								if(le.x >= x && (textArea$LElement11 == null || textArea$LElement11.element == le.element)) {
									return le;
								}

								textArea$LElement11 = le;
							}
						}
					}

					layoutIdx += layoutCount;
				}

				if(lineBottom > 0) {
					lineTop = lineBottom;
				}
			}

			return null;
		}

		TextArea.LElement find(TextAreaModel.Element element, int[] offset) {
			if(this.element == element) {
				return this;
			} else {
				TextArea.LElement match = this.find(this.layout, element, offset);
				if(match == null) {
					match = this.find(this.anchors, element, offset);
				}

				return match;
			}
		}

		private TextArea.LElement find(ArrayList l, TextAreaModel.Element e, int[] offset) {
			int i = 0;

			for(int n = l.size(); i < n; ++i) {
				TextArea.LElement match = ((TextArea.LElement)l.get(i)).find(e, offset);
				if(match != null) {
					if(offset != null) {
						offset[0] += this.x;
						offset[1] += this.y;
					}

					return match;
				}
			}

			return null;
		}
	}

	static class LElement {
		final TextAreaModel.Element element;
		int x;
		int y;
		int width;
		int height;
		short marginTop;
		short marginLeft;
		short marginRight;
		short marginBottom;
		String href;

		public LElement(TextAreaModel.Element element) {
			this.element = element;
		}

		void adjustWidget(int offX, int offY) {
		}

		void draw(int offX, int offY, AnimationState as) {
		}

		void destroy() {
		}

		boolean isInside(int x, int y) {
			return x >= this.x && x < this.x + this.width && y >= this.y && y < this.y + this.height;
		}

		TextArea.LElement find(int x, int y) {
			return this;
		}

		TextArea.LElement find(TextAreaModel.Element element, int[] offset) {
			return this.element == element ? this : null;
		}

		int bottom() {
			return this.y + this.height + this.marginBottom;
		}
	}

	static class LImage extends TextArea.LElement {
		final Image img;

		public LImage(TextAreaModel.Element element, Image img) {
			super(element);
			this.img = img;
			this.width = img.getWidth();
			this.height = img.getHeight();
		}

		void draw(int offX, int offY, AnimationState as) {
			this.img.draw(as, this.x + offX, this.y + offY, this.width, this.height);
		}
	}

	static class LText extends TextArea.LElement {
		final Font font;
		final String text;
		final int start;
		final int end;
		FontCache cache;

		public LText(TextAreaModel.Element element, Font font, String text, int start, int end) {
			super(element);
			this.font = font;
			this.text = text;
			this.start = start;
			this.end = end;
			this.cache = font.cacheText((FontCache)null, text, start, end);
			this.height = font.getLineHeight();
			if(this.cache != null) {
				this.width = this.cache.getWidth();
			} else {
				this.width = font.computeTextWidth(text, start, end);
			}

		}

		void draw(int offX, int offY, AnimationState as) {
			if(this.cache != null) {
				this.cache.draw(as, this.x + offX, this.y + offY);
			} else {
				this.font.drawText(as, this.x + offX, this.y + offY, this.text, this.start, this.end);
			}

		}

		void destroy() {
			if(this.cache != null) {
				this.cache.destroy();
				this.cache = null;
			}

		}
	}

	static class LWidget extends TextArea.LElement {
		final Widget widget;

		public LWidget(TextAreaModel.Element element, Widget widget) {
			super(element);
			this.widget = widget;
		}

		void adjustWidget(int offX, int offY) {
			this.widget.setPosition(this.x + offX, this.y + offY);
			this.widget.setSize(this.width, this.height);
		}
	}

	public interface WidgetResolver {
		Widget resolveWidget(String string1, String string2);
	}
}
