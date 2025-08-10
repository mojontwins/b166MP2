package de.matthiasmann.twl;

import de.matthiasmann.twl.renderer.MouseCursor;
import de.matthiasmann.twl.utils.TintAnimator;

public class ResizableFrame extends Widget {
	public static final de.matthiasmann.twl.renderer.AnimationState.StateKey STATE_FADE = de.matthiasmann.twl.renderer.AnimationState.StateKey.get("fade");
	private String title = "";
	private final MouseCursor[] cursors = new MouseCursor[ResizableFrame.DragMode.values().length];
	private ResizableFrame.ResizableAxis resizableAxis = ResizableFrame.ResizableAxis.BOTH;
	private ResizableFrame.DragMode dragMode = ResizableFrame.DragMode.NONE;
	private int dragStartX;
	private int dragStartY;
	private int dragInitialLeft;
	private int dragInitialTop;
	private int dragInitialRight;
	private int dragInitialBottom;
	private Color fadeColorInactive = Color.WHITE;
	private int fadeDurationActivate;
	private int fadeDurationDeactivate;
	private int fadeDurationShow;
	private int fadeDurationHide;
	private TextWidget titleWidget;
	private int titleAreaTop;
	private int titleAreaLeft;
	private int titleAreaRight;
	private int titleAreaBottom;
	private boolean hasCloseButton;
	private Button closeButton;
	private int closeButtonX;
	private int closeButtonY;
	private boolean hasResizeHandle;
	private Widget resizeHandle;
	private int resizeHandleX;
	private int resizeHandleY;
	private ResizableFrame.DragMode resizeHandleDragMode;
	private static int[] $SWITCH_TABLE$de$matthiasmann$twl$ResizableFrame$DragMode;

	public ResizableFrame() {
		this.setCanAcceptKeyboardFocus(true);
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
		if(this.titleWidget != null) {
			this.titleWidget.setCharSequence(title);
		}

	}

	public ResizableFrame.ResizableAxis getResizableAxis() {
		return this.resizableAxis;
	}

	public void setResizableAxis(ResizableFrame.ResizableAxis resizableAxis) {
		if(resizableAxis == null) {
			throw new NullPointerException("resizableAxis");
		} else {
			this.resizableAxis = resizableAxis;
			if(this.resizeHandle != null) {
				this.layoutResizeHandle();
			}

		}
	}

	public boolean hasTitleBar() {
		return this.titleWidget != null && this.titleWidget.getParent() == this;
	}

	public void addCloseCallback(Runnable cb) {
		if(this.closeButton == null) {
			this.closeButton = new Button();
			this.closeButton.setTheme("closeButton");
			this.closeButton.setCanAcceptKeyboardFocus(false);
			this.add(this.closeButton);
			this.layoutCloseButton();
		}

		this.closeButton.setVisible(this.hasCloseButton);
		this.closeButton.addCallback(cb);
	}

	public void removeCloseCallback(Runnable cb) {
		if(this.closeButton != null) {
			this.closeButton.removeCallback(cb);
			this.closeButton.setVisible(this.closeButton.hasCallbacks());
		}

	}

	public void setVisible(boolean visible) {
		if(visible) {
			TintAnimator tintAnimator = this.getTintAnimator();
			if(tintAnimator != null && tintAnimator.hasTint() || !super.isVisible()) {
				this.fadeTo(this.hasKeyboardFocus() ? Color.WHITE : this.fadeColorInactive, this.fadeDurationShow);
			}
		} else if(super.isVisible()) {
			this.fadeToHide(this.fadeDurationHide);
		}

	}

	public void setHardVisible(boolean visible) {
		super.setVisible(visible);
	}

	protected void applyThemeResizableFrame(ThemeInfo themeInfo) {
		ResizableFrame.DragMode[] resizableFrame$DragMode5;
		int i4 = (resizableFrame$DragMode5 = ResizableFrame.DragMode.values()).length;

		for(int i3 = 0; i3 < i4; ++i3) {
			ResizableFrame.DragMode m = resizableFrame$DragMode5[i3];
			if(m.cursorName != null) {
				this.cursors[m.ordinal()] = themeInfo.getMouseCursor(m.cursorName);
			} else {
				this.cursors[m.ordinal()] = null;
			}
		}

		this.titleAreaTop = themeInfo.getParameter("titleAreaTop", 0);
		this.titleAreaLeft = themeInfo.getParameter("titleAreaLeft", 0);
		this.titleAreaRight = themeInfo.getParameter("titleAreaRight", 0);
		this.titleAreaBottom = themeInfo.getParameter("titleAreaBottom", 0);
		this.closeButtonX = themeInfo.getParameter("closeButtonX", 0);
		this.closeButtonY = themeInfo.getParameter("closeButtonY", 0);
		this.hasCloseButton = themeInfo.getParameter("hasCloseButton", false);
		this.hasResizeHandle = themeInfo.getParameter("hasResizeHandle", false);
		this.resizeHandleX = themeInfo.getParameter("resizeHandleX", 0);
		this.resizeHandleY = themeInfo.getParameter("resizeHandleY", 0);
		this.fadeColorInactive = themeInfo.getParameter("fadeColorInactive", Color.WHITE);
		this.fadeDurationActivate = themeInfo.getParameter("fadeDurationActivate", 0);
		this.fadeDurationDeactivate = themeInfo.getParameter("fadeDurationDeactivate", 0);
		this.fadeDurationShow = themeInfo.getParameter("fadeDurationShow", 0);
		this.fadeDurationHide = themeInfo.getParameter("fadeDurationHide", 0);
		this.invalidateLayout();
		if(super.isVisible() && !this.hasKeyboardFocus() && (this.getTintAnimator() != null || !Color.WHITE.equals(this.fadeColorInactive))) {
			this.fadeTo(this.fadeColorInactive, 0);
		}

	}

	protected void applyTheme(ThemeInfo themeInfo) {
		super.applyTheme(themeInfo);
		this.applyThemeResizableFrame(themeInfo);
	}

	protected void updateTintAnimation() {
		TintAnimator tintAnimator = this.getTintAnimator();
		tintAnimator.update();
		if(!tintAnimator.isFadeActive() && tintAnimator.isZeroAlpha()) {
			this.setHardVisible(false);
		}

	}

	protected void fadeTo(Color color, int duration) {
		this.allocateTint().fadeTo(color, duration);
		if(!super.isVisible() && color.getA() != 0) {
			this.setHardVisible(true);
		}

	}

	protected void fadeToHide(int duration) {
		if(duration <= 0) {
			this.setHardVisible(false);
		} else {
			this.allocateTint().fadeToHide(duration);
		}

	}

	private TintAnimator allocateTint() {
		TintAnimator tintAnimator = this.getTintAnimator();
		if(tintAnimator == null) {
			tintAnimator = new TintAnimator(new TintAnimator.AnimationStateTimeSource(this.getAnimationState(), STATE_FADE));
			this.setTintAnimator(tintAnimator);
			if(!super.isVisible()) {
				tintAnimator.fadeToHide(0);
			}
		}

		return tintAnimator;
	}

	protected boolean isFrameElement(Widget widget) {
		return widget == this.titleWidget || widget == this.closeButton || widget == this.resizeHandle;
	}

	protected void layout() {
		int minWidth = this.getMinWidth();
		int minHeight = this.getMinHeight();
		int i;
		int n;
		if(this.getWidth() < minWidth || this.getHeight() < minHeight) {
			i = Math.max(this.getWidth(), minWidth);
			n = Math.max(this.getHeight(), minHeight);
			if(this.getParent() != null) {
				int child = Math.min(this.getX(), this.getParent().getInnerRight() - i);
				int y = Math.min(this.getY(), this.getParent().getInnerBottom() - n);
				this.setPosition(child, y);
			}

			this.setSize(i, n);
		}

		i = 0;

		for(n = this.getNumChildren(); i < n; ++i) {
			Widget widget7 = this.getChild(i);
			if(!this.isFrameElement(widget7)) {
				this.layoutChildFullInnerArea(widget7);
			}
		}

		this.layoutTitle();
		this.layoutCloseButton();
		this.layoutResizeHandle();
	}

	protected void layoutTitle() {
		int titleX = this.getTitleX(this.titleAreaLeft);
		int titleY = this.getTitleY(this.titleAreaTop);
		int titleWidth = Math.max(0, this.getTitleX(this.titleAreaRight) - titleX);
		int titleHeight = Math.max(0, this.getTitleY(this.titleAreaBottom) - titleY);
		if(this.titleAreaLeft != this.titleAreaRight && this.titleAreaTop != this.titleAreaBottom) {
			if(this.titleWidget == null) {
				this.titleWidget = new TextWidget(this.getAnimationState());
				this.titleWidget.setTheme("title");
				this.titleWidget.setMouseCursor(this.cursors[ResizableFrame.DragMode.POSITION.ordinal()]);
				this.titleWidget.setCharSequence(this.title);
				this.titleWidget.setClip(true);
			}

			if(this.titleWidget.getParent() == null) {
				this.insertChild(this.titleWidget, 0);
			}

			this.titleWidget.setPosition(titleX, titleY);
			this.titleWidget.setSize(titleWidth, titleHeight);
		} else if(this.titleWidget != null && this.titleWidget.getParent() == this) {
			this.titleWidget.destroy();
			this.removeChild(this.titleWidget);
		}

	}

	protected void layoutCloseButton() {
		if(this.closeButton != null) {
			this.closeButton.adjustSize();
			this.closeButton.setPosition(this.getTitleX(this.closeButtonX), this.getTitleY(this.closeButtonY));
			this.closeButton.setVisible(this.closeButton.hasCallbacks() && this.hasCloseButton);
		}

	}

	protected void layoutResizeHandle() {
		if(this.hasResizeHandle && this.resizeHandle == null) {
			this.resizeHandle = new Widget(this.getAnimationState(), true);
			this.resizeHandle.setTheme("resizeHandle");
			super.insertChild(this.resizeHandle, 0);
		}

		if(this.resizeHandle != null) {
			if(this.resizeHandleX > 0) {
				if(this.resizeHandleY > 0) {
					this.resizeHandleDragMode = ResizableFrame.DragMode.CORNER_TL;
				} else {
					this.resizeHandleDragMode = ResizableFrame.DragMode.CORNER_TR;
				}
			} else if(this.resizeHandleY > 0) {
				this.resizeHandleDragMode = ResizableFrame.DragMode.CORNER_BL;
			} else {
				this.resizeHandleDragMode = ResizableFrame.DragMode.CORNER_BR;
			}

			this.resizeHandle.adjustSize();
			this.resizeHandle.setPosition(this.getTitleX(this.resizeHandleX), this.getTitleY(this.resizeHandleY));
			this.resizeHandle.setVisible(this.hasResizeHandle && this.resizableAxis == ResizableFrame.ResizableAxis.BOTH);
		} else {
			this.resizeHandleDragMode = ResizableFrame.DragMode.NONE;
		}

	}

	protected void keyboardFocusGained() {
		this.fadeTo(Color.WHITE, this.fadeDurationActivate);
	}

	protected void keyboardFocusLost() {
		if(!this.hasOpenPopups() && super.isVisible()) {
			this.fadeTo(this.fadeColorInactive, this.fadeDurationDeactivate);
		}

	}

	public int getMinWidth() {
		int minWidth = super.getMinWidth();
		int i = 0;

		for(int n = this.getNumChildren(); i < n; ++i) {
			Widget child = this.getChild(i);
			if(!this.isFrameElement(child)) {
				minWidth = Math.max(minWidth, child.getMinWidth() + this.getBorderHorizontal());
			}
		}

		if(this.hasTitleBar() && this.titleAreaRight < 0) {
			minWidth = Math.max(minWidth, this.titleWidget.getPreferredWidth() + this.titleAreaLeft - this.titleAreaRight);
		}

		return minWidth;
	}

	public int getMinHeight() {
		int minHeight = super.getMinHeight();
		int i = 0;

		for(int n = this.getNumChildren(); i < n; ++i) {
			Widget child = this.getChild(i);
			if(!this.isFrameElement(child)) {
				minHeight = Math.max(minHeight, child.getMinHeight() + this.getBorderVertical());
			}
		}

		return minHeight;
	}

	public int getPreferredInnerWidth() {
		int prefWidth = 0;
		int i = 0;

		for(int n = this.getNumChildren(); i < n; ++i) {
			Widget child = this.getChild(i);
			if(!this.isFrameElement(child)) {
				prefWidth = Math.max(prefWidth, child.getPreferredWidth());
			}
		}

		return prefWidth;
	}

	public int getPreferredWidth() {
		int prefWidth = super.getPreferredWidth();
		if(this.hasTitleBar() && this.titleAreaRight < 0) {
			prefWidth = Math.max(prefWidth, this.titleWidget.getPreferredWidth() + this.titleAreaLeft - this.titleAreaRight);
		}

		return prefWidth;
	}

	public int getPreferredInnerHeight() {
		int prefHeight = 0;
		int i = 0;

		for(int n = this.getNumChildren(); i < n; ++i) {
			Widget child = this.getChild(i);
			if(!this.isFrameElement(child)) {
				prefHeight = Math.max(prefHeight, child.getPreferredHeight());
			}
		}

		return prefHeight;
	}

	public void adjustSize() {
		this.layoutTitle();
		super.adjustSize();
	}

	private int getTitleX(int offset) {
		return offset < 0 ? this.getRight() + offset : this.getX() + offset;
	}

	private int getTitleY(int offset) {
		return offset < 0 ? this.getBottom() + offset : this.getY() + offset;
	}

	protected boolean handleEvent(Event evt) {
		boolean isMouseExit = evt.getType() == Event.Type.MOUSE_EXITED;
		if(isMouseExit && this.resizeHandle != null && this.resizeHandle.isVisible()) {
			this.resizeHandle.getAnimationState().setAnimationState(TextWidget.STATE_HOVER, false);
		}

		if(this.dragMode != ResizableFrame.DragMode.NONE) {
			if(evt.isMouseDragEnd()) {
				this.dragMode = ResizableFrame.DragMode.NONE;
			} else if(evt.getType() == Event.Type.MOUSE_DRAGGED) {
				this.handleMouseDrag(evt);
			}

			return true;
		} else {
			ResizableFrame.DragMode cursorMode = this.getDragMode(evt.getMouseX(), evt.getMouseY());
			MouseCursor cursor = this.cursors[cursorMode.ordinal()];
			this.setMouseCursor(cursor);
			if(!isMouseExit && this.resizeHandle != null && this.resizeHandle.isVisible()) {
				this.resizeHandle.getAnimationState().setAnimationState(TextWidget.STATE_HOVER, this.resizeHandle.isMouseInside(evt));
			}

			if(!evt.isMouseDragEvent() && evt.getType() == Event.Type.MOUSE_BTNDOWN && evt.getMouseButton() == 0 && this.handleMouseDown(evt)) {
				this.setMouseCursor(this.cursors[this.dragMode.ordinal()]);
				return true;
			} else {
				return super.handleEvent(evt) ? true : evt.isMouseEvent();
			}
		}
	}

	private ResizableFrame.DragMode getDragMode(int mx, int my) {
		boolean left = mx < this.getInnerX();
		boolean right = mx >= this.getInnerRight();
		boolean top = my < this.getInnerY();
		boolean bot = my >= this.getInnerBottom();
		if(this.titleWidget != null && this.titleWidget.getParent() == this) {
			if(this.titleWidget.isInside(mx, my)) {
				return ResizableFrame.DragMode.POSITION;
			}

			top = my < this.titleWidget.getY();
		}

		if(this.closeButton != null && this.closeButton.isVisible() && this.closeButton.isInside(mx, my)) {
			return ResizableFrame.DragMode.NONE;
		} else if(this.resizableAxis == ResizableFrame.ResizableAxis.NONE) {
			return ResizableFrame.DragMode.NONE;
		} else if(this.resizeHandle != null && this.resizeHandle.isVisible() && this.resizeHandle.isInside(mx, my)) {
			return this.resizeHandleDragMode;
		} else {
			if(!this.resizableAxis.allowX) {
				left = false;
				right = false;
			}

			if(!this.resizableAxis.allowY) {
				top = false;
				bot = false;
			}

			return left ? (top ? ResizableFrame.DragMode.CORNER_TL : (bot ? ResizableFrame.DragMode.CORNER_BL : ResizableFrame.DragMode.EDGE_LEFT)) : (right ? (top ? ResizableFrame.DragMode.CORNER_TR : (bot ? ResizableFrame.DragMode.CORNER_BR : ResizableFrame.DragMode.EDGE_RIGHT)) : (top ? ResizableFrame.DragMode.EDGE_TOP : (bot ? ResizableFrame.DragMode.EDGE_BOTTOM : ResizableFrame.DragMode.NONE)));
		}
	}

	private boolean handleMouseDown(Event evt) {
		int mx = evt.getMouseX();
		int my = evt.getMouseY();
		this.dragStartX = mx;
		this.dragStartY = my;
		this.dragInitialLeft = this.getX();
		this.dragInitialTop = this.getY();
		this.dragInitialRight = this.getRight();
		this.dragInitialBottom = this.getBottom();
		this.dragMode = this.getDragMode(mx, my);
		return this.dragMode != ResizableFrame.DragMode.NONE;
	}

	private void handleMouseDrag(Event evt) {
		int dx = evt.getMouseX() - this.dragStartX;
		int dy = evt.getMouseY() - this.dragStartY;
		int minWidth = this.getMinWidth();
		int minHeight = this.getMinHeight();
		int maxWidth = this.getMaxWidth();
		int maxHeight = this.getMaxHeight();
		if(maxWidth > 0 && maxWidth < minWidth) {
			maxWidth = minWidth;
		}

		if(maxHeight > 0 && maxHeight < minHeight) {
			maxHeight = minHeight;
		}

		int left = this.dragInitialLeft;
		int top = this.dragInitialTop;
		int right = this.dragInitialRight;
		int bottom = this.dragInitialBottom;
		int minY;
		int maxY;
		int height;
		switch($SWITCH_TABLE$de$matthiasmann$twl$ResizableFrame$DragMode()[this.dragMode.ordinal()]) {
		case 2:
		case 6:
		case 9:
			left = Math.min(left + dx, right - minWidth);
			if(maxWidth > 0) {
				left = Math.max(left, Math.min(this.dragInitialLeft, right - maxWidth));
			}
		case 3:
		case 5:
		default:
			break;
		case 4:
		case 7:
		case 8:
			right = Math.max(right + dx, left + minWidth);
			if(maxWidth > 0) {
				right = Math.min(right, Math.max(this.dragInitialRight, left + maxWidth));
			}
			break;
		case 10:
			if(this.getParent() != null) {
				minY = this.getParent().getInnerX();
				maxY = this.getParent().getInnerRight();
				height = this.dragInitialRight - this.dragInitialLeft;
				left = Math.max(minY, Math.min(maxY - height, left + dx));
				right = Math.min(maxY, Math.max(minY + height, right + dx));
			} else {
				left += dx;
				right += dx;
			}
		}

		switch($SWITCH_TABLE$de$matthiasmann$twl$ResizableFrame$DragMode()[this.dragMode.ordinal()]) {
		case 3:
		case 6:
		case 7:
			top = Math.min(top + dy, bottom - minHeight);
			if(maxHeight > 0) {
				top = Math.max(top, Math.min(this.dragInitialTop, bottom - maxHeight));
			}
		case 4:
		default:
			break;
		case 5:
		case 8:
		case 9:
			bottom = Math.max(bottom + dy, top + minHeight);
			if(maxHeight > 0) {
				bottom = Math.min(bottom, Math.max(this.dragInitialBottom, top + maxHeight));
			}
			break;
		case 10:
			if(this.getParent() != null) {
				minY = this.getParent().getInnerY();
				maxY = this.getParent().getInnerHeight();
				height = this.dragInitialBottom - this.dragInitialTop;
				top = Math.max(minY, Math.min(maxY - height, top + dy));
				bottom = Math.min(maxY, Math.max(minY + height, bottom + dy));
			} else {
				top += dy;
				bottom += dy;
			}
		}

		this.setArea(top, left, right, bottom);
	}

	private void setArea(int top, int left, int right, int bottom) {
		Widget p = this.getParent();
		if(p != null) {
			top = Math.max(top, p.getInnerY());
			left = Math.max(left, p.getInnerX());
			right = Math.min(right, p.getInnerRight());
			bottom = Math.min(bottom, p.getInnerBottom());
		}

		this.setPosition(left, top);
		this.setSize(Math.max(this.getMinWidth(), right - left), Math.max(this.getMinHeight(), bottom - top));
	}

	static int[] $SWITCH_TABLE$de$matthiasmann$twl$ResizableFrame$DragMode() {
		int[] i10000 = $SWITCH_TABLE$de$matthiasmann$twl$ResizableFrame$DragMode;
		if($SWITCH_TABLE$de$matthiasmann$twl$ResizableFrame$DragMode != null) {
			return i10000;
		} else {
			int[] i0 = new int[ResizableFrame.DragMode.values().length];

			try {
				i0[ResizableFrame.DragMode.CORNER_BL.ordinal()] = 9;
			} catch (NoSuchFieldError noSuchFieldError10) {
			}

			try {
				i0[ResizableFrame.DragMode.CORNER_BR.ordinal()] = 8;
			} catch (NoSuchFieldError noSuchFieldError9) {
			}

			try {
				i0[ResizableFrame.DragMode.CORNER_TL.ordinal()] = 6;
			} catch (NoSuchFieldError noSuchFieldError8) {
			}

			try {
				i0[ResizableFrame.DragMode.CORNER_TR.ordinal()] = 7;
			} catch (NoSuchFieldError noSuchFieldError7) {
			}

			try {
				i0[ResizableFrame.DragMode.EDGE_BOTTOM.ordinal()] = 5;
			} catch (NoSuchFieldError noSuchFieldError6) {
			}

			try {
				i0[ResizableFrame.DragMode.EDGE_LEFT.ordinal()] = 2;
			} catch (NoSuchFieldError noSuchFieldError5) {
			}

			try {
				i0[ResizableFrame.DragMode.EDGE_RIGHT.ordinal()] = 4;
			} catch (NoSuchFieldError noSuchFieldError4) {
			}

			try {
				i0[ResizableFrame.DragMode.EDGE_TOP.ordinal()] = 3;
			} catch (NoSuchFieldError noSuchFieldError3) {
			}

			try {
				i0[ResizableFrame.DragMode.NONE.ordinal()] = 1;
			} catch (NoSuchFieldError noSuchFieldError2) {
			}

			try {
				i0[ResizableFrame.DragMode.POSITION.ordinal()] = 10;
			} catch (NoSuchFieldError noSuchFieldError1) {
			}

			$SWITCH_TABLE$de$matthiasmann$twl$ResizableFrame$DragMode = i0;
			return i0;
		}
	}

	private static enum DragMode {
		NONE("mouseCursor"),
		EDGE_LEFT("mouseCursor.left"),
		EDGE_TOP("mouseCursor.top"),
		EDGE_RIGHT("mouseCursor.right"),
		EDGE_BOTTOM("mouseCursor.bottom"),
		CORNER_TL("mouseCursor.top-left"),
		CORNER_TR("mouseCursor.top-right"),
		CORNER_BR("mouseCursor.bottom-right"),
		CORNER_BL("mouseCursor.bottom-left"),
		POSITION("mouseCursor.all");

		final String cursorName;

		private DragMode(String cursorName) {
			this.cursorName = cursorName;
		}
	}

	public static enum ResizableAxis {
		NONE(false, false),
		HORIZONTAL(true, false),
		VERTICAL(false, true),
		BOTH(true, true);

		final boolean allowX;
		final boolean allowY;

		private ResizableAxis(boolean allowX, boolean allowY) {
			this.allowX = allowX;
			this.allowY = allowY;
		}
	}
}
