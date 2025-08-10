package de.matthiasmann.twl;

import de.matthiasmann.twl.renderer.Image;
import de.matthiasmann.twl.utils.CallbackSupport;

public class Scrollbar extends Widget {
	private static final int INITIAL_DELAY = 300;
	private static final int REPEAT_DELAY = 75;
	private final Scrollbar.Orientation orientation;
	private final Button btnUpLeft;
	private final Button btnDownRight;
	private final DraggableButton thumb;
	private final DraggableButton.DragListener dragListener;
	private final Runnable timerCallback;
	private Timer timer;
	private int trackClicked;
	private int trackClickLimit;
	private Runnable[] callbacks;
	private Image trackImageUpLeft;
	private Image trackImageDownRight;
	private int pageSize;
	private int stepSize;
	private boolean scaleThumb;
	private int minValue;
	private int maxValue;
	private int value;

	public Scrollbar() {
		this(Scrollbar.Orientation.VERTICAL);
	}

	public Scrollbar(Scrollbar.Orientation orientation) {
		this.orientation = orientation;
		this.btnUpLeft = new Button();
		this.btnDownRight = new Button();
		this.thumb = new DraggableButton();
		Runnable cbUpdateTimer = new Runnable() {
			public void run() {
				Scrollbar.this.updateTimer();
			}
		};
		if(orientation == Scrollbar.Orientation.HORIZONTAL) {
			this.setTheme("hscrollbar");
			this.btnUpLeft.setTheme("leftbutton");
			this.btnDownRight.setTheme("rightbutton");
		} else {
			this.setTheme("vscrollbar");
			this.btnUpLeft.setTheme("upbutton");
			this.btnDownRight.setTheme("downbutton");
		}

		this.dragListener = new DraggableButton.DragListener() {
			private int startValue;

			public void dragStarted() {
				this.startValue = Scrollbar.this.getValue();
			}

			public void dragged(int deltaX, int deltaY) {
				int mouseDelta;
				if(Scrollbar.this.getOrientation() == Scrollbar.Orientation.HORIZONTAL) {
					mouseDelta = deltaX;
				} else {
					mouseDelta = deltaY;
				}

				int delta = (Scrollbar.this.getMaxValue() - Scrollbar.this.getMinValue()) * mouseDelta / Scrollbar.this.calcThumbArea();
				int newValue = Scrollbar.this.range(this.startValue + delta);
				Scrollbar.this.setValue(newValue);
			}

			public void dragStopped() {
			}
		};
		this.btnUpLeft.setCanAcceptKeyboardFocus(false);
		this.btnUpLeft.getModel().addStateCallback(cbUpdateTimer);
		this.btnDownRight.setCanAcceptKeyboardFocus(false);
		this.btnDownRight.getModel().addStateCallback(cbUpdateTimer);
		this.thumb.setCanAcceptKeyboardFocus(false);
		this.thumb.setTheme("thumb");
		this.thumb.setListener(this.dragListener);
		this.timerCallback = new Runnable() {
			public void run() {
				Scrollbar.this.onTimer(75);
			}
		};
		this.add(this.btnUpLeft);
		this.add(this.btnDownRight);
		this.add(this.thumb);
		this.pageSize = 10;
		this.stepSize = 1;
		this.maxValue = 100;
		this.setSize(30, 200);
		this.setDepthFocusTraversal(false);
	}

	public void addCallback(Runnable cb) {
		this.callbacks = (Runnable[])CallbackSupport.addCallbackToList(this.callbacks, cb, Runnable.class);
	}

	public void removeCallback(Runnable cb) {
		this.callbacks = (Runnable[])CallbackSupport.removeCallbackFromList(this.callbacks, cb);
	}

	protected void doCallback() {
		CallbackSupport.fireCallbacks(this.callbacks);
	}

	public Scrollbar.Orientation getOrientation() {
		return this.orientation;
	}

	public int getValue() {
		return this.value;
	}

	public void setValue(int current) {
		this.setValue(current, true);
	}

	public void setValue(int value, boolean fireCallbacks) {
		value = this.range(value);
		int oldValue = this.value;
		if(oldValue != value) {
			this.value = value;
			this.setThumbPos();
			this.firePropertyChange("value", oldValue, value);
			if(fireCallbacks) {
				this.doCallback();
			}
		}

	}

	public void scroll(int amount) {
		if(this.minValue < this.maxValue) {
			this.setValue(this.value + amount);
		} else {
			this.setValue(this.value - amount);
		}

	}

	public void scrollToArea(int start, int size, int extra) {
		if(size > 0) {
			if(extra < 0) {
				extra = 0;
			}

			int end = start + size;
			start = this.range(start);
			int pos = this.value;
			int startWithExtra = this.range(start - extra);
			if(startWithExtra < pos) {
				pos = startWithExtra;
			}

			int pageEnd = pos + this.pageSize;
			int endWithExtra = end + extra;
			if(endWithExtra > pageEnd) {
				pos = this.range(endWithExtra - this.pageSize);
				if(pos > startWithExtra) {
					size = end - start;
					pos = start - Math.max(0, this.pageSize - size) / 2;
				}
			}

			this.setValue(pos);
		}
	}

	public int getMinValue() {
		return this.minValue;
	}

	public int getMaxValue() {
		return this.maxValue;
	}

	public void setMinMaxValue(int minValue, int maxValue) {
		if(maxValue < minValue) {
			throw new IllegalArgumentException("maxValue < minValue");
		} else {
			this.minValue = minValue;
			this.maxValue = maxValue;
			this.value = this.range(this.value);
			this.setThumbPos();
			this.thumb.setVisible(minValue != maxValue);
		}
	}

	public int getPageSize() {
		return this.pageSize;
	}

	public void setPageSize(int pageSize) {
		if(pageSize < 1) {
			throw new IllegalArgumentException("pageSize < 1");
		} else {
			this.pageSize = pageSize;
			if(this.scaleThumb) {
				this.setThumbPos();
			}

		}
	}

	public int getStepSize() {
		return this.stepSize;
	}

	public void setStepSize(int stepSize) {
		if(stepSize < 1) {
			throw new IllegalArgumentException("stepSize < 1");
		} else {
			this.stepSize = stepSize;
		}
	}

	public boolean isScaleThumb() {
		return this.scaleThumb;
	}

	public void setScaleThumb(boolean scaleThumb) {
		this.scaleThumb = scaleThumb;
		this.setThumbPos();
	}

	public void externalDragStart() {
		this.thumb.getAnimationState().setAnimationState(Button.STATE_PRESSED, true);
		this.dragListener.dragStarted();
	}

	public void externalDragged(int deltaX, int deltaY) {
		this.dragListener.dragged(deltaX, deltaY);
	}

	public void externalDragStopped() {
		this.dragListener.dragStopped();
		this.thumb.getAnimationState().setAnimationState(Button.STATE_PRESSED, false);
	}

	public boolean isUpLeftButtonArmed() {
		return this.btnUpLeft.getModel().isArmed();
	}

	public boolean isDownRightButtonArmed() {
		return this.btnDownRight.getModel().isArmed();
	}

	public boolean isThumbDragged() {
		return this.thumb.getModel().isPressed();
	}

	public void setThumbTooltipContent(Object tooltipContent) {
		this.thumb.setTooltipContent(tooltipContent);
	}

	public Object getThumbTooltipContent() {
		return this.thumb.getTooltipContent();
	}

	protected void applyTheme(ThemeInfo themeInfo) {
		super.applyTheme(themeInfo);
		this.applyThemeScrollbar(themeInfo);
	}

	protected void applyThemeScrollbar(ThemeInfo themeInfo) {
		this.setScaleThumb(themeInfo.getParameter("scaleThumb", false));
		if(this.orientation == Scrollbar.Orientation.HORIZONTAL) {
			this.trackImageUpLeft = (Image)themeInfo.getParameterValue("trackImageLeft", false, Image.class);
			this.trackImageDownRight = (Image)themeInfo.getParameterValue("trackImageRight", false, Image.class);
		} else {
			this.trackImageUpLeft = (Image)themeInfo.getParameterValue("trackImageUp", false, Image.class);
			this.trackImageDownRight = (Image)themeInfo.getParameterValue("trackImageDown", false, Image.class);
		}

	}

	protected void paintWidget(GUI gui) {
		int x = this.getInnerX();
		int y = this.getInnerY();
		int w;
		int thumbBottom;
		if(this.orientation == Scrollbar.Orientation.HORIZONTAL) {
			w = this.getInnerHeight();
			if(this.trackImageUpLeft != null) {
				this.trackImageUpLeft.draw(this.getAnimationState(), x, y, this.thumb.getX() - x, w);
			}

			if(this.trackImageDownRight != null) {
				thumbBottom = this.thumb.getRight();
				this.trackImageDownRight.draw(this.getAnimationState(), thumbBottom, y, this.getInnerRight() - thumbBottom, w);
			}
		} else {
			w = this.getInnerWidth();
			if(this.trackImageUpLeft != null) {
				this.trackImageUpLeft.draw(this.getAnimationState(), x, y, w, this.thumb.getY() - y);
			}

			if(this.trackImageDownRight != null) {
				thumbBottom = this.thumb.getBottom();
				this.trackImageDownRight.draw(this.getAnimationState(), x, thumbBottom, w, this.getInnerBottom() - thumbBottom);
			}
		}

	}

	protected void afterAddToGUI(GUI gui) {
		super.afterAddToGUI(gui);
		this.timer = gui.createTimer();
		this.timer.setCallback(this.timerCallback);
		this.timer.setContinuous(true);
	}

	protected void beforeRemoveFromGUI(GUI gui) {
		super.beforeRemoveFromGUI(gui);
		if(this.timer != null) {
			this.timer.stop();
		}

		this.timer = null;
	}

	public boolean handleEvent(Event evt) {
		if(evt.getType() == Event.Type.MOUSE_BTNUP && evt.getMouseButton() == 0) {
			this.trackClicked = 0;
			this.updateTimer();
		}

		if(!super.handleEvent(evt) && evt.getType() == Event.Type.MOUSE_BTNDOWN && evt.getMouseButton() == 0 && this.isMouseInside(evt)) {
			if(this.orientation == Scrollbar.Orientation.HORIZONTAL) {
				this.trackClickLimit = evt.getMouseX();
				if(evt.getMouseX() < this.thumb.getX()) {
					this.trackClicked = -1;
				} else {
					this.trackClicked = 1;
				}
			} else {
				this.trackClickLimit = evt.getMouseY();
				if(evt.getMouseY() < this.thumb.getY()) {
					this.trackClicked = -1;
				} else {
					this.trackClicked = 1;
				}
			}

			this.updateTimer();
		}

		boolean page = (evt.getModifiers() & 36) != 0;
		int step = page ? this.pageSize : this.stepSize;
		if(evt.getType() == Event.Type.KEY_PRESSED) {
			switch(evt.getKeyCode()) {
			case 200:
				if(this.orientation == Scrollbar.Orientation.VERTICAL) {
					this.setValue(this.value - step);
					return true;
				}
				break;
			case 201:
				if(this.orientation == Scrollbar.Orientation.VERTICAL) {
					this.setValue(this.value - this.pageSize);
					return true;
				}
			case 202:
			case 204:
			case 206:
			case 207:
			default:
				break;
			case 203:
				if(this.orientation == Scrollbar.Orientation.HORIZONTAL) {
					this.setValue(this.value - step);
					return true;
				}
				break;
			case 205:
				if(this.orientation == Scrollbar.Orientation.HORIZONTAL) {
					this.setValue(this.value + step);
					return true;
				}
				break;
			case 208:
				if(this.orientation == Scrollbar.Orientation.VERTICAL) {
					this.setValue(this.value + step);
					return true;
				}
				break;
			case 209:
				if(this.orientation == Scrollbar.Orientation.VERTICAL) {
					this.setValue(this.value + this.pageSize);
					return true;
				}
			}
		}

		if(evt.getType() == Event.Type.MOUSE_WHEEL) {
			this.setValue(this.value - step * evt.getMouseWheelDelta());
		}

		return evt.isMouseEvent();
	}

	int range(int current) {
		if(this.minValue < this.maxValue) {
			if(current < this.minValue) {
				current = this.minValue;
			} else if(current > this.maxValue) {
				current = this.maxValue;
			}
		} else if(current > this.minValue) {
			current = this.minValue;
		} else if(current < this.maxValue) {
			current = this.maxValue;
		}

		return current;
	}

	void onTimer(int nextDelay) {
		this.timer.setDelay(nextDelay);
		if(this.trackClicked != 0) {
			int thumbPos;
			if(this.orientation == Scrollbar.Orientation.HORIZONTAL) {
				thumbPos = this.thumb.getX();
			} else {
				thumbPos = this.thumb.getY();
			}

			if((this.trackClickLimit - thumbPos) * this.trackClicked > 0) {
				this.scroll(this.trackClicked * this.pageSize);
			}
		} else if(this.btnUpLeft.getModel().isArmed()) {
			this.scroll(-this.stepSize);
		} else if(this.btnDownRight.getModel().isArmed()) {
			this.scroll(this.stepSize);
		}

	}

	void updateTimer() {
		if(this.timer != null) {
			if(this.trackClicked == 0 && !this.btnUpLeft.getModel().isArmed() && !this.btnDownRight.getModel().isArmed()) {
				this.timer.stop();
			} else if(!this.timer.isRunning()) {
				this.onTimer(300);
				this.timer.start();
			}
		}

	}

	public int getMinWidth() {
		return this.orientation == Scrollbar.Orientation.HORIZONTAL ? Math.max(super.getMinWidth(), this.btnUpLeft.getMinWidth() + this.thumb.getMinWidth() + this.btnDownRight.getMinWidth()) : Math.max(super.getMinWidth(), this.thumb.getMinWidth());
	}

	public int getMinHeight() {
		return this.orientation == Scrollbar.Orientation.HORIZONTAL ? Math.max(super.getMinHeight(), this.thumb.getMinHeight()) : Math.max(super.getMinHeight(), this.btnUpLeft.getMinHeight() + this.thumb.getMinHeight() + this.btnDownRight.getMinHeight());
	}

	public int getPreferredWidth() {
		return this.getMinWidth();
	}

	public int getPreferredHeight() {
		return this.getMinHeight();
	}

	protected void layout() {
		if(this.orientation == Scrollbar.Orientation.HORIZONTAL) {
			this.btnUpLeft.setSize(this.btnUpLeft.getPreferredWidth(), this.getHeight());
			this.btnUpLeft.setPosition(this.getX(), this.getY());
			this.btnDownRight.setSize(this.btnUpLeft.getPreferredWidth(), this.getHeight());
			this.btnDownRight.setPosition(this.getX() + this.getWidth() - this.btnDownRight.getWidth(), this.getY());
		} else {
			this.btnUpLeft.setSize(this.getWidth(), this.btnUpLeft.getPreferredHeight());
			this.btnUpLeft.setPosition(this.getX(), this.getY());
			this.btnDownRight.setSize(this.getWidth(), this.btnDownRight.getPreferredHeight());
			this.btnDownRight.setPosition(this.getX(), this.getY() + this.getHeight() - this.btnDownRight.getHeight());
		}

		this.setThumbPos();
	}

	int calcThumbArea() {
		return this.orientation == Scrollbar.Orientation.HORIZONTAL ? Math.max(1, this.getWidth() - this.btnUpLeft.getWidth() - this.thumb.getWidth() - this.btnDownRight.getWidth()) : Math.max(1, this.getHeight() - this.btnUpLeft.getHeight() - this.thumb.getHeight() - this.btnDownRight.getHeight());
	}

	private void setThumbPos() {
		int delta = this.maxValue - this.minValue;
		int thumbHeight;
		long ypos;
		int ypos1;
		if(this.orientation == Scrollbar.Orientation.HORIZONTAL) {
			thumbHeight = this.thumb.getPreferredWidth();
			if(this.scaleThumb) {
				ypos = (long)Math.max(1, this.getWidth() - this.btnUpLeft.getWidth() - this.btnDownRight.getWidth());
				thumbHeight = (int)Math.max((long)thumbHeight, ypos * (long)this.pageSize / (long)(this.pageSize + delta + 1));
			}

			this.thumb.setSize(thumbHeight, this.getHeight());
			ypos1 = this.btnUpLeft.getX() + this.btnUpLeft.getWidth();
			if(delta != 0) {
				ypos1 += (this.value - this.minValue) * this.calcThumbArea() / delta;
			}

			this.thumb.setPosition(ypos1, this.getY());
		} else {
			thumbHeight = this.thumb.getPreferredHeight();
			if(this.scaleThumb) {
				ypos = (long)Math.max(1, this.getHeight() - this.btnUpLeft.getHeight() - this.btnDownRight.getHeight());
				thumbHeight = (int)Math.max((long)thumbHeight, ypos * (long)this.pageSize / (long)(this.pageSize + delta + 1));
			}

			this.thumb.setSize(this.getWidth(), thumbHeight);
			ypos1 = this.btnUpLeft.getY() + this.btnUpLeft.getHeight();
			if(delta != 0) {
				ypos1 += (this.value - this.minValue) * this.calcThumbArea() / delta;
			}

			this.thumb.setPosition(this.getX(), ypos1);
		}

	}

	public static enum Orientation {
		HORIZONTAL,
		VERTICAL;
	}
}
