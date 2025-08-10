package de.matthiasmann.twl;

public abstract class ValueAdjuster extends Widget {
	public static final de.matthiasmann.twl.renderer.AnimationState.StateKey STATE_EDIT_ACTIVE = de.matthiasmann.twl.renderer.AnimationState.StateKey.get("editActive");
	private static final int INITIAL_DELAY = 300;
	private static final int REPEAT_DELAY = 75;
	private final DraggableButton label = new DraggableButton(this.getAnimationState(), true);
	private final EditField editField = new EditField(this.getAnimationState());
	private final Button decButton = new Button(this.getAnimationState(), true);
	private final Button incButton = new Button(this.getAnimationState(), true);
	private final Runnable timerCallback;
	private final ValueAdjuster.L listeners;
	private Timer timer;
	private String displayPrefix;
	private String displayPrefixTheme = "";
	private boolean useMouseWheel = true;
	private boolean acceptValueOnFocusLoss = true;
	private boolean wasInEditOnFocusLost;
	private int width;
	private static int[] $SWITCH_TABLE$de$matthiasmann$twl$Event$Type;

	public ValueAdjuster() {
		this.label.setClip(true);
		this.label.setTheme("valueDisplay");
		this.editField.setTheme("valueEdit");
		this.decButton.setTheme("decButton");
		this.incButton.setTheme("incButton");
		Runnable cbUpdateTimer = new Runnable() {
			public void run() {
				ValueAdjuster.this.updateTimer();
			}
		};
		this.timerCallback = new Runnable() {
			public void run() {
				ValueAdjuster.this.onTimer(75);
			}
		};
		this.decButton.getModel().addStateCallback(cbUpdateTimer);
		this.incButton.getModel().addStateCallback(cbUpdateTimer);
		this.listeners = new ValueAdjuster.L();
		this.label.addCallback(this.listeners);
		this.label.setListener(this.listeners);
		this.editField.setVisible(false);
		this.editField.addCallback(this.listeners);
		this.add(this.label);
		this.add(this.editField);
		this.add(this.decButton);
		this.add(this.incButton);
		this.setCanAcceptKeyboardFocus(true);
		this.setDepthFocusTraversal(false);
	}

	public String getDisplayPrefix() {
		return this.displayPrefix;
	}

	public void setDisplayPrefix(String displayPrefix) {
		this.displayPrefix = displayPrefix;
		this.setDisplayText();
	}

	public boolean isUseMouseWheel() {
		return this.useMouseWheel;
	}

	public void setAcceptValueOnFocusLoss(boolean acceptValueOnFocusLoss) {
		this.acceptValueOnFocusLoss = acceptValueOnFocusLoss;
	}

	public boolean isAcceptValueOnFocusLoss() {
		return this.acceptValueOnFocusLoss;
	}

	public void setUseMouseWheel(boolean useMouseWheel) {
		this.useMouseWheel = useMouseWheel;
	}

	public void setTooltipContent(Object tooltipContent) {
		super.setTooltipContent(tooltipContent);
		this.label.setTooltipContent(tooltipContent);
	}

	public void startEdit() {
		if(this.label.isVisible()) {
			this.editField.setErrorMessage((Object)null);
			this.editField.setText(this.onEditStart());
			this.editField.setVisible(true);
			this.editField.requestKeyboardFocus();
			this.editField.selectAll();
			this.editField.getAnimationState().setAnimationState(EditField.STATE_HOVER, this.label.getModel().isHover());
			this.label.setVisible(false);
			this.getAnimationState().setAnimationState(STATE_EDIT_ACTIVE, true);
		}

	}

	public void cancelEdit() {
		if(this.editField.isVisible()) {
			this.onEditCanceled();
			this.label.setVisible(true);
			this.editField.setVisible(false);
			this.label.getModel().setHover(this.editField.getAnimationState().getAnimationState(Label.STATE_HOVER));
			this.getAnimationState().setAnimationState(STATE_EDIT_ACTIVE, false);
		}

	}

	public void cancelOrAcceptEdit() {
		if(this.editField.isVisible()) {
			if(this.acceptValueOnFocusLoss) {
				this.onEditEnd(this.editField.getText());
			}

			this.cancelEdit();
		}

	}

	protected void applyTheme(ThemeInfo themeInfo) {
		super.applyTheme(themeInfo);
		this.applyThemeValueAdjuster(themeInfo);
	}

	protected void applyThemeValueAdjuster(ThemeInfo themeInfo) {
		this.width = themeInfo.getParameter("width", 100);
		this.displayPrefixTheme = themeInfo.getParameter("displayPrefix", "");
		this.useMouseWheel = themeInfo.getParameter("useMouseWheel", this.useMouseWheel);
	}

	public int getMinWidth() {
		int minWidth = super.getMinWidth();
		minWidth = Math.max(minWidth, this.getBorderHorizontal() + this.decButton.getMinWidth() + Math.max(this.width, this.label.getMinWidth()) + this.incButton.getMinWidth());
		return minWidth;
	}

	public int getMinHeight() {
		int minHeight = this.label.getMinHeight();
		minHeight = Math.max(minHeight, this.decButton.getMinHeight());
		minHeight = Math.max(minHeight, this.incButton.getMinHeight());
		minHeight += this.getBorderVertical();
		return Math.max(minHeight, super.getMinHeight());
	}

	public int getPreferredInnerWidth() {
		return this.decButton.getPreferredWidth() + Math.max(this.width, this.label.getPreferredWidth()) + this.incButton.getPreferredWidth();
	}

	public int getPreferredInnerHeight() {
		return Math.max(Math.max(this.decButton.getPreferredHeight(), this.incButton.getPreferredHeight()), this.label.getPreferredHeight());
	}

	protected void keyboardFocusLost() {
		this.wasInEditOnFocusLost = this.editField.isVisible();
		this.cancelOrAcceptEdit();
		this.label.getAnimationState().setAnimationState(STATE_KEYBOARD_FOCUS, false);
	}

	protected void keyboardFocusGained() {
		this.label.getAnimationState().setAnimationState(STATE_KEYBOARD_FOCUS, true);
	}

	protected void keyboardFocusGained(FocusGainedCause cause, Widget previousWidget) {
		this.keyboardFocusGained();
		this.checkStartEditOnFocusGained(cause, previousWidget);
	}

	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if(!visible) {
			this.cancelEdit();
		}

	}

	protected void widgetDisabled() {
		this.cancelEdit();
	}

	protected void layout() {
		int height = this.getInnerHeight();
		int y = this.getInnerY();
		this.decButton.setPosition(this.getInnerX(), y);
		this.decButton.setSize(this.decButton.getPreferredWidth(), height);
		this.incButton.setPosition(this.getInnerRight() - this.incButton.getPreferredWidth(), y);
		this.incButton.setSize(this.incButton.getPreferredWidth(), height);
		int labelX = this.decButton.getRight();
		int labelWidth = Math.max(0, this.incButton.getX() - labelX);
		this.label.setSize(labelWidth, height);
		this.label.setPosition(labelX, y);
		this.editField.setSize(labelWidth, height);
		this.editField.setPosition(labelX, y);
	}

	protected void setDisplayText() {
		String prefix = this.displayPrefix != null ? this.displayPrefix : this.displayPrefixTheme;
		this.label.setText(prefix.concat(this.formatText()));
	}

	protected abstract String formatText();

	void checkStartEditOnFocusGained(FocusGainedCause cause, Widget previousWidget) {
		if(cause == FocusGainedCause.FOCUS_KEY) {
			if(previousWidget != null && !(previousWidget instanceof ValueAdjuster)) {
				previousWidget = previousWidget.getParent();
			}

			if(previousWidget != this && previousWidget instanceof ValueAdjuster && ((ValueAdjuster)previousWidget).wasInEditOnFocusLost) {
				this.startEdit();
			}
		}

	}

	void onTimer(int nextDelay) {
		this.timer.setDelay(nextDelay);
		if(this.incButton.getModel().isArmed()) {
			this.cancelEdit();
			this.doIncrement();
		} else if(this.decButton.getModel().isArmed()) {
			this.cancelEdit();
			this.doDecrement();
		}

	}

	void updateTimer() {
		if(this.timer != null) {
			if(!this.incButton.getModel().isArmed() && !this.decButton.getModel().isArmed()) {
				this.timer.stop();
			} else if(!this.timer.isRunning()) {
				this.onTimer(300);
				this.timer.start();
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

	protected boolean handleEvent(Event evt) {
		if(evt.isKeyEvent()) {
			if(evt.isKeyPressedEvent() && evt.getKeyCode() == 1 && this.listeners.dragActive) {
				this.listeners.dragActive = false;
				this.onDragCancelled();
				return true;
			}

			if(!this.editField.isVisible()) {
				switch($SWITCH_TABLE$de$matthiasmann$twl$Event$Type()[evt.getType().ordinal()]) {
				case 9:
					switch(evt.getKeyCode()) {
					case 28:
					case 57:
						this.startEdit();
						return true;
					case 203:
						this.doDecrement();
						return true;
					case 205:
						this.doIncrement();
						return true;
					default:
						if(evt.hasKeyCharNoModifiers() && this.shouldStartEdit(evt.getKeyChar())) {
							this.startEdit();
							this.editField.handleEvent(evt);
							return true;
						}
					}
				default:
					return true;
				}
			}
		} else if(!this.editField.isVisible() && this.useMouseWheel && evt.getType() == Event.Type.MOUSE_WHEEL) {
			if(evt.getMouseWheelDelta() < 0) {
				this.doDecrement();
			} else if(evt.getMouseWheelDelta() > 0) {
				this.doIncrement();
			}

			return true;
		}

		return super.handleEvent(evt);
	}

	protected abstract String onEditStart();

	protected abstract boolean onEditEnd(String string1);

	protected abstract String validateEdit(String string1);

	protected abstract void onEditCanceled();

	protected abstract boolean shouldStartEdit(char c1);

	protected abstract void onDragStart();

	protected abstract void onDragUpdate(int i1);

	protected abstract void onDragCancelled();

	protected void onDragEnd() {
	}

	protected abstract void doDecrement();

	protected abstract void doIncrement();

	void handleEditCallback(int key) {
		switch(key) {
		case 1:
			this.cancelEdit();
			break;
		case 28:
			if(this.onEditEnd(this.editField.getText())) {
				this.label.setVisible(true);
				this.editField.setVisible(false);
			}
			break;
		default:
			this.editField.setErrorMessage(this.validateEdit(this.editField.getText()));
		}

	}

	protected abstract void syncWithModel();

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

	class L implements Runnable, DraggableButton.DragListener, EditField.Callback {
		boolean dragActive;

		public void run() {
			ValueAdjuster.this.startEdit();
		}

		public void dragStarted() {
			this.dragActive = true;
			ValueAdjuster.this.onDragStart();
		}

		public void dragged(int deltaX, int deltaY) {
			if(this.dragActive) {
				ValueAdjuster.this.onDragUpdate(deltaX);
			}

		}

		public void dragStopped() {
			this.dragActive = false;
			ValueAdjuster.this.onDragEnd();
		}

		public void callback(int key) {
			ValueAdjuster.this.handleEditCallback(key);
		}
	}

	class ModelCallback implements Runnable {
		public void run() {
			ValueAdjuster.this.syncWithModel();
		}
	}
}
