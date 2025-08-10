package de.matthiasmann.twl;

public class ScrollPane extends Widget {
	public static final de.matthiasmann.twl.renderer.AnimationState.StateKey STATE_DOWNARROW_ARMED = de.matthiasmann.twl.renderer.AnimationState.StateKey.get("downArrowArmed");
	public static final de.matthiasmann.twl.renderer.AnimationState.StateKey STATE_RIGHTARROW_ARMED = de.matthiasmann.twl.renderer.AnimationState.StateKey.get("rightArrowArmed");
	public static final de.matthiasmann.twl.renderer.AnimationState.StateKey STATE_HORIZONTAL_SCROLLBAR_VISIBLE = de.matthiasmann.twl.renderer.AnimationState.StateKey.get("horizontalScrollbarVisible");
	public static final de.matthiasmann.twl.renderer.AnimationState.StateKey STATE_VERTICAL_SCROLLBAR_VISIBLE = de.matthiasmann.twl.renderer.AnimationState.StateKey.get("verticalScrollbarVisible");
	public static final de.matthiasmann.twl.renderer.AnimationState.StateKey STATE_AUTO_SCROLL_UP = de.matthiasmann.twl.renderer.AnimationState.StateKey.get("autoScrollUp");
	public static final de.matthiasmann.twl.renderer.AnimationState.StateKey STATE_AUTO_SCROLL_DOWN = de.matthiasmann.twl.renderer.AnimationState.StateKey.get("autoScrollDown");
	private static final int AUTO_SCROLL_DELAY = 50;
	final Scrollbar scrollbarH;
	final Scrollbar scrollbarV;
	private final Widget contentArea;
	private DraggableButton dragButton;
	private Widget content;
	private ScrollPane.Fixed fixed;
	private Dimension hscrollbarOffset;
	private Dimension vscrollbarOffset;
	private Dimension contentScrollbarSpacing;
	private boolean inLayout;
	private boolean expandContentSize;
	private boolean scrollbarsAlwaysVisible;
	private int scrollbarsToggleFlags;
	private int autoScrollArea;
	private int autoScrollSpeed;
	private Timer autoScrollTimer;
	private int autoScrollDirection;
	private static int[] $SWITCH_TABLE$de$matthiasmann$twl$ScrollPane$Fixed;
	private static int[] $SWITCH_TABLE$de$matthiasmann$twl$Event$Type;

	public ScrollPane() {
		this((Widget)null);
	}

	public ScrollPane(Widget content) {
		this.fixed = ScrollPane.Fixed.NONE;
		this.hscrollbarOffset = Dimension.ZERO;
		this.vscrollbarOffset = Dimension.ZERO;
		this.contentScrollbarSpacing = Dimension.ZERO;
		this.scrollbarH = new Scrollbar(Scrollbar.Orientation.HORIZONTAL);
		this.scrollbarV = new Scrollbar(Scrollbar.Orientation.VERTICAL);
		this.contentArea = new Widget();
		Runnable cb = new Runnable() {
			public void run() {
				ScrollPane.this.scrollContent();
			}
		};
		this.scrollbarH.addCallback(cb);
		this.scrollbarH.setVisible(false);
		this.scrollbarV.addCallback(cb);
		this.scrollbarV.setVisible(false);
		this.contentArea.setClip(true);
		this.contentArea.setTheme("");
		super.insertChild(this.contentArea, 0);
		super.insertChild(this.scrollbarH, 1);
		super.insertChild(this.scrollbarV, 2);
		this.setContent(content);
		this.setCanAcceptKeyboardFocus(true);
	}

	public ScrollPane.Fixed getFixed() {
		return this.fixed;
	}

	public void setFixed(ScrollPane.Fixed fixed) {
		if(fixed == null) {
			throw new NullPointerException("fixed");
		} else {
			if(this.fixed != fixed) {
				this.fixed = fixed;
				this.invalidateLayout();
			}

		}
	}

	public Widget getContent() {
		return this.content;
	}

	public void setContent(Widget content) {
		if(this.content != null) {
			this.contentArea.removeAllChildren();
			this.content = null;
		}

		if(content != null) {
			this.content = content;
			this.contentArea.add(content);
		}

	}

	public boolean isExpandContentSize() {
		return this.expandContentSize;
	}

	public void setExpandContentSize(boolean expandContentSize) {
		if(this.expandContentSize != expandContentSize) {
			this.expandContentSize = expandContentSize;
			this.invalidateLayoutLocally();
		}

	}

	public void updateScrollbarSizes() {
		this.invalidateLayoutLocally();
		this.validateLayout();
	}

	public int getScrollPositionX() {
		return this.scrollbarH.getValue();
	}

	public int getMaxScrollPosX() {
		return this.scrollbarH.getMaxValue();
	}

	public void setScrollPositionX(int pos) {
		this.scrollbarH.setValue(pos);
	}

	public void scrollToAreaX(int start, int size, int extra) {
		this.scrollbarH.scrollToArea(start, size, extra);
	}

	public int getScrollPositionY() {
		return this.scrollbarV.getValue();
	}

	public int getMaxScrollPosY() {
		return this.scrollbarV.getMaxValue();
	}

	public void setScrollPositionY(int pos) {
		this.scrollbarV.setValue(pos);
	}

	public void scrollToAreaY(int start, int size, int extra) {
		this.scrollbarV.scrollToArea(start, size, extra);
	}

	public int getContentAreaWidth() {
		return this.contentArea.getWidth();
	}

	public int getContentAreaHeight() {
		return this.contentArea.getHeight();
	}

	public Scrollbar getHorizontalScrollbar() {
		return this.scrollbarH;
	}

	public Scrollbar getVerticalScrollbar() {
		return this.scrollbarV;
	}

	public DraggableButton.DragListener createDragListener() {
		return new DraggableButton.DragListener() {
			int startScrollX;
			int startScrollY;

			public void dragStarted() {
				this.startScrollX = ScrollPane.this.getScrollPositionX();
				this.startScrollY = ScrollPane.this.getScrollPositionY();
			}

			public void dragged(int deltaX, int deltaY) {
				ScrollPane.this.setScrollPositionX(this.startScrollX - deltaX);
				ScrollPane.this.setScrollPositionY(this.startScrollY - deltaY);
			}

			public void dragStopped() {
			}
		};
	}

	public boolean checkAutoScroll(Event evt) {
		GUI gui = this.getGUI();
		if(gui == null) {
			this.stopAutoScroll();
			return false;
		} else {
			this.autoScrollDirection = this.getAutoScrollDirection(evt);
			if(this.autoScrollDirection == 0) {
				this.stopAutoScroll();
				return false;
			} else {
				this.setAutoScrollMarker();
				if(this.autoScrollTimer == null) {
					this.autoScrollTimer = gui.createTimer();
					this.autoScrollTimer.setContinuous(true);
					this.autoScrollTimer.setDelay(50);
					this.autoScrollTimer.setCallback(new Runnable() {
						public void run() {
							ScrollPane.this.doAutoScroll();
						}
					});
					this.doAutoScroll();
				}

				this.autoScrollTimer.start();
				return true;
			}
		}
	}

	public void stopAutoScroll() {
		if(this.autoScrollTimer != null) {
			this.autoScrollTimer.stop();
		}

		this.autoScrollDirection = 0;
		this.setAutoScrollMarker();
	}

	public static ScrollPane getContainingScrollPane(Widget widget) {
		Widget ca = widget.getParent();
		if(ca != null) {
			Widget sp = ca.getParent();
			if(sp instanceof ScrollPane) {
				ScrollPane scrollPane = (ScrollPane)sp;

				assert scrollPane.getContent() == widget;

				return scrollPane;
			}
		}

		return null;
	}

	public int getMinWidth() {
		int minWidth = super.getMinWidth();
		int border = this.getBorderHorizontal();
		if(this.fixed == ScrollPane.Fixed.HORIZONTAL && this.content != null) {
			minWidth = Math.max(minWidth, this.content.getMinWidth() + border + this.scrollbarV.getMinWidth());
		}

		return minWidth;
	}

	public int getMinHeight() {
		int minHeight = super.getMinHeight();
		int border = this.getBorderVertical();
		if(this.fixed == ScrollPane.Fixed.VERTICAL && this.content != null) {
			minHeight = Math.max(minHeight, this.content.getMinHeight() + border + this.scrollbarH.getMinHeight());
		}

		return minHeight;
	}

	public int getPreferredInnerWidth() {
		if(this.content != null) {
			switch($SWITCH_TABLE$de$matthiasmann$twl$ScrollPane$Fixed()[this.fixed.ordinal()]) {
			case 2:
				int prefWidth = computeSize(this.content.getMinWidth(), this.content.getPreferredWidth(), this.content.getMaxWidth());
				if(this.scrollbarV.isVisible()) {
					prefWidth += this.scrollbarV.getPreferredWidth();
				}

				return prefWidth;
			case 3:
				return this.content.getPreferredWidth();
			}
		}

		return 0;
	}

	public int getPreferredInnerHeight() {
		if(this.content != null) {
			switch($SWITCH_TABLE$de$matthiasmann$twl$ScrollPane$Fixed()[this.fixed.ordinal()]) {
			case 2:
				return this.content.getPreferredHeight();
			case 3:
				int prefHeight = computeSize(this.content.getMinHeight(), this.content.getPreferredHeight(), this.content.getMaxHeight());
				if(this.scrollbarH.isVisible()) {
					prefHeight += this.scrollbarH.getPreferredHeight();
				}

				return prefHeight;
			}
		}

		return 0;
	}

	public void insertChild(Widget child, int index) {
		throw new UnsupportedOperationException("use setContent");
	}

	public void removeAllChildren() {
		throw new UnsupportedOperationException("use setContent");
	}

	public Widget removeChild(int index) {
		throw new UnsupportedOperationException("use setContent");
	}

	protected void applyTheme(ThemeInfo themeInfo) {
		super.applyTheme(themeInfo);
		this.applyThemeScrollPane(themeInfo);
	}

	protected void applyThemeScrollPane(ThemeInfo themeInfo) {
		this.autoScrollArea = themeInfo.getParameter("autoScrollArea", 5);
		this.autoScrollSpeed = themeInfo.getParameter("autoScrollSpeed", this.autoScrollArea * 2);
		this.hscrollbarOffset = (Dimension)themeInfo.getParameterValue("hscrollbarOffset", false, Dimension.class, Dimension.ZERO);
		this.vscrollbarOffset = (Dimension)themeInfo.getParameterValue("vscrollbarOffset", false, Dimension.class, Dimension.ZERO);
		this.contentScrollbarSpacing = (Dimension)themeInfo.getParameterValue("contentScrollbarSpacing", false, Dimension.class, Dimension.ZERO);
		this.scrollbarsAlwaysVisible = themeInfo.getParameter("scrollbarsAlwaysVisible", false);
		boolean hasDragButton = themeInfo.getParameter("hasDragButton", false);
		if(hasDragButton && this.dragButton == null) {
			this.dragButton = new DraggableButton();
			this.dragButton.setTheme("dragButton");
			this.dragButton.setListener(new DraggableButton.DragListener() {
				public void dragStarted() {
					ScrollPane.this.scrollbarH.externalDragStart();
					ScrollPane.this.scrollbarV.externalDragStart();
				}

				public void dragged(int deltaX, int deltaY) {
					ScrollPane.this.scrollbarH.externalDragged(deltaX, deltaY);
					ScrollPane.this.scrollbarV.externalDragged(deltaX, deltaY);
				}

				public void dragStopped() {
					ScrollPane.this.scrollbarH.externalDragStopped();
					ScrollPane.this.scrollbarV.externalDragStopped();
				}
			});
			super.insertChild(this.dragButton, 3);
		} else if(!hasDragButton && this.dragButton != null) {
			assert super.getChild(3) == this.dragButton;

			super.removeChild(3);
			this.dragButton = null;
		}

	}

	protected int getAutoScrollDirection(Event evt) {
		if(this.content instanceof ScrollPane.AutoScrollable) {
			return ((ScrollPane.AutoScrollable)this.content).getAutoScrollDirection(evt, this.autoScrollArea);
		} else {
			if(this.contentArea.isMouseInside(evt)) {
				int mouseY = evt.getMouseY();
				int areaY = this.contentArea.getY();
				if(mouseY - areaY <= this.autoScrollArea || this.contentArea.getBottom() - mouseY <= this.autoScrollArea) {
					if(mouseY < areaY + this.contentArea.getHeight() / 2) {
						return -1;
					}

					return 1;
				}
			}

			return 0;
		}
	}

	public void validateLayout() {
		if(!this.inLayout) {
			try {
				this.inLayout = true;
				if(this.content != null) {
					this.content.validateLayout();
				}

				super.validateLayout();
			} finally {
				this.inLayout = false;
			}
		}

	}

	protected void childInvalidateLayout(Widget child) {
		if(child == this.contentArea) {
			this.invalidateLayoutLocally();
		} else {
			super.childInvalidateLayout(child);
		}

	}

	protected void paintWidget(GUI gui) {
		this.scrollbarsToggleFlags = 0;
	}

	protected void layout() {
		if(this.content != null) {
			int innerWidth = this.getInnerWidth();
			int innerHeight = this.getInnerHeight();
			int availWidth = innerWidth;
			int availHeight = innerHeight;
			innerWidth += this.vscrollbarOffset.getX();
			innerHeight += this.hscrollbarOffset.getY();
			int scrollbarHX = this.hscrollbarOffset.getX();
			int scrollbarHY = innerHeight;
			int scrollbarVX = innerWidth;
			int scrollbarVY = this.vscrollbarOffset.getY();
			boolean visibleH = false;
			boolean visibleV = false;
			int requiredWidth;
			int requiredHeight;
			switch($SWITCH_TABLE$de$matthiasmann$twl$ScrollPane$Fixed()[this.fixed.ordinal()]) {
			case 2:
				requiredWidth = availWidth;
				requiredHeight = this.content.getPreferredHeight();
				break;
			case 3:
				requiredWidth = this.content.getPreferredWidth();
				requiredHeight = availHeight;
				break;
			default:
				requiredWidth = this.content.getPreferredWidth();
				requiredHeight = this.content.getPreferredHeight();
			}

			int hScrollbarMax = 0;
			int vScrollbarMax = 0;
			boolean repeat;
			int pageSizeX;
			if(availWidth > 0 && availHeight > 0) {
				do {
					repeat = false;
					if(this.fixed != ScrollPane.Fixed.HORIZONTAL) {
						hScrollbarMax = Math.max(0, requiredWidth - availWidth);
						if(hScrollbarMax > 0 || this.scrollbarsAlwaysVisible || (this.scrollbarsToggleFlags & 3) == 3) {
							repeat |= !visibleH;
							visibleH = true;
							pageSizeX = this.scrollbarH.getPreferredHeight();
							scrollbarHY = innerHeight - pageSizeX;
							availHeight = Math.max(0, scrollbarHY - this.contentScrollbarSpacing.getY());
						}
					} else {
						hScrollbarMax = 0;
						requiredWidth = availWidth;
					}

					if(this.fixed != ScrollPane.Fixed.VERTICAL) {
						vScrollbarMax = Math.max(0, requiredHeight - availHeight);
						if(vScrollbarMax > 0 || this.scrollbarsAlwaysVisible || (this.scrollbarsToggleFlags & 12) == 12) {
							repeat |= !visibleV;
							visibleV = true;
							pageSizeX = this.scrollbarV.getPreferredWidth();
							scrollbarVX = innerWidth - pageSizeX;
							availWidth = Math.max(0, scrollbarVX - this.contentScrollbarSpacing.getX());
						}
					} else {
						vScrollbarMax = 0;
						requiredHeight = availHeight;
					}
				} while(repeat);
			}

			if(visibleH && !this.scrollbarH.isVisible()) {
				this.scrollbarsToggleFlags |= 1;
			}

			if(!visibleH && this.scrollbarH.isVisible()) {
				this.scrollbarsToggleFlags |= 2;
			}

			if(visibleV && !this.scrollbarV.isVisible()) {
				this.scrollbarsToggleFlags |= 4;
			}

			if(!visibleV && this.scrollbarV.isVisible()) {
				this.scrollbarsToggleFlags |= 8;
			}

			if(visibleH != this.scrollbarH.isVisible() || visibleV != this.scrollbarV.isVisible()) {
				this.invalidateLayoutLocally();
			}

			int pageSizeY;
			if(this.content instanceof ScrollPane.CustomPageSize) {
				ScrollPane.CustomPageSize animationState = (ScrollPane.CustomPageSize)this.content;
				pageSizeX = animationState.getPageSizeX(availWidth);
				pageSizeY = animationState.getPageSizeY(availHeight);
			} else {
				pageSizeX = availWidth;
				pageSizeY = availHeight;
			}

			this.scrollbarH.setVisible(visibleH);
			this.scrollbarH.setMinMaxValue(0, hScrollbarMax);
			this.scrollbarH.setSize(Math.max(0, scrollbarVX - scrollbarHX), Math.max(0, innerHeight - scrollbarHY));
			this.scrollbarH.setPosition(this.getInnerX() + scrollbarHX, this.getInnerY() + scrollbarHY);
			this.scrollbarH.setPageSize(Math.max(1, pageSizeX));
			this.scrollbarH.setStepSize(Math.max(1, pageSizeX / 10));
			this.scrollbarV.setVisible(visibleV);
			this.scrollbarV.setMinMaxValue(0, vScrollbarMax);
			this.scrollbarV.setSize(Math.max(0, innerWidth - scrollbarVX), Math.max(0, scrollbarHY - scrollbarVY));
			this.scrollbarV.setPosition(this.getInnerX() + scrollbarVX, this.getInnerY() + scrollbarVY);
			this.scrollbarV.setPageSize(Math.max(1, pageSizeY));
			this.scrollbarV.setStepSize(Math.max(1, pageSizeY / 10));
			if(this.dragButton != null) {
				this.dragButton.setVisible(visibleH && visibleV);
				this.dragButton.setSize(Math.max(0, innerWidth - scrollbarVX), Math.max(0, innerHeight - scrollbarHY));
				this.dragButton.setPosition(this.getInnerX() + scrollbarVX, this.getInnerY() + scrollbarHY);
			}

			this.contentArea.setPosition(this.getInnerX(), this.getInnerY());
			this.contentArea.setSize(availWidth, availHeight);
			if(this.content instanceof ScrollPane.Scrollable) {
				this.content.setPosition(this.contentArea.getX(), this.contentArea.getY());
				this.content.setSize(availWidth, availHeight);
			} else if(this.expandContentSize) {
				this.content.setSize(Math.max(availWidth, requiredWidth), Math.max(availHeight, requiredHeight));
			} else {
				this.content.setSize(Math.max(0, requiredWidth), Math.max(0, requiredHeight));
			}

			AnimationState animationState1 = this.getAnimationState();
			animationState1.setAnimationState(STATE_HORIZONTAL_SCROLLBAR_VISIBLE, visibleH);
			animationState1.setAnimationState(STATE_VERTICAL_SCROLLBAR_VISIBLE, visibleV);
			this.scrollContent();
		} else {
			this.scrollbarH.setVisible(false);
			this.scrollbarV.setVisible(false);
		}

	}

	protected boolean handleEvent(Event evt) {
		if(evt.isKeyEvent() && this.content != null && this.content.canAcceptKeyboardFocus() && this.content.handleEvent(evt)) {
			this.content.requestKeyboardFocus();
			return true;
		} else if(super.handleEvent(evt)) {
			return true;
		} else {
			switch($SWITCH_TABLE$de$matthiasmann$twl$Event$Type()[evt.getType().ordinal()]) {
			case 8:
				if(this.scrollbarV.isVisible()) {
					return this.scrollbarV.handleEvent(evt);
				}

				return false;
			case 9:
			case 10:
				int keyCode = evt.getKeyCode();
				if(keyCode == 203 || keyCode == 205) {
					return this.scrollbarH.handleEvent(evt);
				} else if(keyCode == 200 || keyCode == 208 || keyCode == 201 || keyCode == 209) {
					return this.scrollbarV.handleEvent(evt);
				}
			default:
				if(evt.isMouseEvent() && this.contentArea.isMouseInside(evt)) {
					return true;
				} else {
					return false;
				}
			}
		}
	}

	protected void paint(GUI gui) {
		if(this.dragButton != null) {
			AnimationState as = this.dragButton.getAnimationState();
			as.setAnimationState(STATE_DOWNARROW_ARMED, this.scrollbarV.isDownRightButtonArmed());
			as.setAnimationState(STATE_RIGHTARROW_ARMED, this.scrollbarH.isDownRightButtonArmed());
		}

		super.paint(gui);
	}

	void scrollContent() {
		if(this.content instanceof ScrollPane.Scrollable) {
			ScrollPane.Scrollable scrollable = (ScrollPane.Scrollable)this.content;
			scrollable.setScrollPosition(this.scrollbarH.getValue(), this.scrollbarV.getValue());
		} else {
			this.content.setPosition(this.contentArea.getX() - this.scrollbarH.getValue(), this.contentArea.getY() - this.scrollbarV.getValue());
		}

	}

	void setAutoScrollMarker() {
		int scrollPos = this.scrollbarV.getValue();
		AnimationState animationState = this.getAnimationState();
		animationState.setAnimationState(STATE_AUTO_SCROLL_UP, this.autoScrollDirection < 0 && scrollPos > 0);
		animationState.setAnimationState(STATE_AUTO_SCROLL_DOWN, this.autoScrollDirection > 0 && scrollPos < this.scrollbarV.getMaxValue());
	}

	void doAutoScroll() {
		this.scrollbarV.setValue(this.scrollbarV.getValue() + this.autoScrollDirection * this.autoScrollSpeed);
		this.setAutoScrollMarker();
	}

	static int[] $SWITCH_TABLE$de$matthiasmann$twl$ScrollPane$Fixed() {
		int[] i10000 = $SWITCH_TABLE$de$matthiasmann$twl$ScrollPane$Fixed;
		if($SWITCH_TABLE$de$matthiasmann$twl$ScrollPane$Fixed != null) {
			return i10000;
		} else {
			int[] i0 = new int[ScrollPane.Fixed.values().length];

			try {
				i0[ScrollPane.Fixed.HORIZONTAL.ordinal()] = 2;
			} catch (NoSuchFieldError noSuchFieldError3) {
			}

			try {
				i0[ScrollPane.Fixed.NONE.ordinal()] = 1;
			} catch (NoSuchFieldError noSuchFieldError2) {
			}

			try {
				i0[ScrollPane.Fixed.VERTICAL.ordinal()] = 3;
			} catch (NoSuchFieldError noSuchFieldError1) {
			}

			$SWITCH_TABLE$de$matthiasmann$twl$ScrollPane$Fixed = i0;
			return i0;
		}
	}

	static int[] $SWITCH_TABLE$de$matthiasmann$twl$Event$Type() {
		int[] i10000 = $SWITCH_TABLE$de$matthiasmann$twl$Event$Type;
		if($SWITCH_TABLE$de$matthiasmann$twl$Event$Type != null) {
			return i10000;
		} else {
			int[] i0 = new int[Event.Type.values().length];

			try {
				i0[Event.Type.KEY_PRESSED.ordinal()] = 9;
			} catch (NoSuchFieldError noSuchFieldError12) {
			}

			try {
				i0[Event.Type.KEY_RELEASED.ordinal()] = 10;
			} catch (NoSuchFieldError noSuchFieldError11) {
			}

			try {
				i0[Event.Type.MOUSE_BTNDOWN.ordinal()] = 3;
			} catch (NoSuchFieldError noSuchFieldError10) {
			}

			try {
				i0[Event.Type.MOUSE_BTNUP.ordinal()] = 4;
			} catch (NoSuchFieldError noSuchFieldError9) {
			}

			try {
				i0[Event.Type.MOUSE_CLICKED.ordinal()] = 5;
			} catch (NoSuchFieldError noSuchFieldError8) {
			}

			try {
				i0[Event.Type.MOUSE_DRAGGED.ordinal()] = 6;
			} catch (NoSuchFieldError noSuchFieldError7) {
			}

			try {
				i0[Event.Type.MOUSE_ENTERED.ordinal()] = 1;
			} catch (NoSuchFieldError noSuchFieldError6) {
			}

			try {
				i0[Event.Type.MOUSE_EXITED.ordinal()] = 7;
			} catch (NoSuchFieldError noSuchFieldError5) {
			}

			try {
				i0[Event.Type.MOUSE_MOVED.ordinal()] = 2;
			} catch (NoSuchFieldError noSuchFieldError4) {
			}

			try {
				i0[Event.Type.MOUSE_WHEEL.ordinal()] = 8;
			} catch (NoSuchFieldError noSuchFieldError3) {
			}

			try {
				i0[Event.Type.POPUP_CLOSED.ordinal()] = 12;
			} catch (NoSuchFieldError noSuchFieldError2) {
			}

			try {
				i0[Event.Type.POPUP_OPENED.ordinal()] = 11;
			} catch (NoSuchFieldError noSuchFieldError1) {
			}

			$SWITCH_TABLE$de$matthiasmann$twl$Event$Type = i0;
			return i0;
		}
	}

	public interface AutoScrollable {
		int getAutoScrollDirection(Event event1, int i2);
	}

	public interface CustomPageSize {
		int getPageSizeX(int i1);

		int getPageSizeY(int i1);
	}

	public static enum Fixed {
		NONE,
		HORIZONTAL,
		VERTICAL;
	}

	public interface Scrollable {
		void setScrollPosition(int i1, int i2);
	}
}
