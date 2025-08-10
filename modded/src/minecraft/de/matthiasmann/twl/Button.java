package de.matthiasmann.twl;

import de.matthiasmann.twl.model.ButtonModel;
import de.matthiasmann.twl.model.SimpleButtonModel;
import de.matthiasmann.twl.utils.TextUtil;

public class Button extends TextWidget {
	public static final de.matthiasmann.twl.renderer.AnimationState.StateKey STATE_ARMED = de.matthiasmann.twl.renderer.AnimationState.StateKey.get("armed");
	public static final de.matthiasmann.twl.renderer.AnimationState.StateKey STATE_PRESSED = de.matthiasmann.twl.renderer.AnimationState.StateKey.get("pressed");
	public static final de.matthiasmann.twl.renderer.AnimationState.StateKey STATE_SELECTED = de.matthiasmann.twl.renderer.AnimationState.StateKey.get("selected");
	private final Runnable stateChangedCB;
	private ButtonModel model;
	private String themeText;
	private String text;
	private int mouseButton;
	private static int[] $SWITCH_TABLE$de$matthiasmann$twl$Event$Type;

	public Button() {
		this((AnimationState)null, false, (ButtonModel)null);
	}

	public Button(ButtonModel model) {
		this((AnimationState)null, false, model);
	}

	public Button(AnimationState animState) {
		this(animState, false, (ButtonModel)null);
	}

	public Button(AnimationState animState, boolean inherit) {
		this(animState, inherit, (ButtonModel)null);
	}

	public Button(String text) {
		this((AnimationState)null, false, (ButtonModel)null);
		this.setText(text);
	}

	public Button(AnimationState animState, ButtonModel model) {
		this(animState, false, model);
	}

	public Button(AnimationState animState, boolean inherit, ButtonModel model) {
		super(animState, inherit);
		this.mouseButton = 0;
		this.stateChangedCB = new Runnable() {
			public void run() {
				Button.this.modelStateChanged();
			}
		};
		if(model == null) {
			model = new SimpleButtonModel();
		}

		this.setModel((ButtonModel)model);
		this.setCanAcceptKeyboardFocus(true);
	}

	public ButtonModel getModel() {
		return this.model;
	}

	public void setModel(ButtonModel model) {
		if(model == null) {
			throw new NullPointerException("model");
		} else {
			boolean isConnected = this.getGUI() != null;
			if(this.model != null) {
				if(isConnected) {
					this.model.disconnect();
				}

				this.model.removeStateCallback(this.stateChangedCB);
			}

			this.model = model;
			this.model.addStateCallback(this.stateChangedCB);
			if(isConnected) {
				this.model.connect();
			}

			this.modelStateChanged();
			AnimationState as = this.getAnimationState();
			as.dontAnimate(STATE_ARMED);
			as.dontAnimate(STATE_PRESSED);
			as.dontAnimate(STATE_HOVER);
			as.dontAnimate(STATE_SELECTED);
		}
	}

	protected void widgetDisabled() {
		this.disarm();
	}

	public void setEnabled(boolean enabled) {
		this.model.setEnabled(enabled);
	}

	public void addCallback(Runnable callback) {
		this.model.addActionCallback(callback);
	}

	public void removeCallback(Runnable callback) {
		this.model.removeActionCallback(callback);
	}

	public boolean hasCallbacks() {
		return this.model.hasActionCallbacks();
	}

	public String getText() {
		return this.text;
	}

	public void setText(String text) {
		this.text = text;
		this.updateText();
	}

	public int getMouseButton() {
		return this.mouseButton;
	}

	public void setMouseButton(int mouseButton) {
		if(mouseButton >= 0 && mouseButton <= 1) {
			this.mouseButton = mouseButton;
		} else {
			throw new IllegalArgumentException("mouseButton");
		}
	}

	protected void applyTheme(ThemeInfo themeInfo) {
		super.applyTheme(themeInfo);
		this.applyThemeButton(themeInfo);
	}

	protected void applyThemeButton(ThemeInfo themeInfo) {
		this.themeText = (String)themeInfo.getParameterValue("text", false, String.class);
		this.updateText();
	}

	protected void afterAddToGUI(GUI gui) {
		super.afterAddToGUI(gui);
		if(this.model != null) {
			this.model.connect();
		}

	}

	protected void beforeRemoveFromGUI(GUI gui) {
		if(this.model != null) {
			this.model.disconnect();
		}

		super.beforeRemoveFromGUI(gui);
	}

	public int getMinWidth() {
		return Math.max(super.getMinWidth(), this.getPreferredWidth());
	}

	public int getMinHeight() {
		return Math.max(super.getMinHeight(), this.getPreferredHeight());
	}

	protected final void doCallback() {
		this.getModel().fireActionCallback();
	}

	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if(!visible) {
			this.disarm();
		}

	}

	protected void disarm() {
		this.model.setHover(false);
		this.model.setArmed(false);
		this.model.setPressed(false);
	}

	void modelStateChanged() {
		super.setEnabled(this.model.isEnabled());
		AnimationState as = this.getAnimationState();
		as.setAnimationState(STATE_SELECTED, this.model.isSelected());
		as.setAnimationState(STATE_HOVER, this.model.isHover());
		as.setAnimationState(STATE_ARMED, this.model.isArmed());
		as.setAnimationState(STATE_PRESSED, this.model.isPressed());
	}

	void updateText() {
		if(this.text == null) {
			super.setCharSequence(TextUtil.notNull(this.themeText));
		} else {
			super.setCharSequence(this.text);
		}

		this.invalidateLayout();
	}

	protected boolean handleEvent(Event evt) {
		if(evt.isMouseEvent()) {
			boolean hover = evt.getType() != Event.Type.MOUSE_EXITED && this.isMouseInside(evt);
			this.model.setHover(hover);
			this.model.setArmed(hover && this.model.isPressed());
		}

		if(!this.model.isEnabled()) {
			return false;
		} else {
			switch($SWITCH_TABLE$de$matthiasmann$twl$Event$Type()[evt.getType().ordinal()]) {
			case 3:
				if(evt.getMouseButton() == this.mouseButton) {
					this.model.setPressed(true);
					this.model.setArmed(true);
				}
				break;
			case 4:
				if(evt.getMouseButton() == this.mouseButton) {
					this.model.setPressed(false);
					this.model.setArmed(false);
				}
			case 5:
			case 6:
			case 7:
			default:
				break;
			case 8:
				return false;
			case 9:
				switch(evt.getKeyCode()) {
				case 28:
				case 57:
					if(!evt.isKeyRepeated()) {
						this.model.setPressed(true);
						this.model.setArmed(true);
					}

					return true;
				default:
					return evt.isMouseEvent();
				}
			case 10:
				switch(evt.getKeyCode()) {
				case 28:
				case 57:
					this.model.setPressed(false);
					this.model.setArmed(false);
					return true;
				default:
					return evt.isMouseEvent();
				}
			case 11:
				this.model.setHover(false);
			}

			return evt.isMouseEvent();
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
}
