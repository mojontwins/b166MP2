package de.matthiasmann.twl;

import de.matthiasmann.twl.renderer.Font;
import de.matthiasmann.twl.utils.CallbackSupport;
import de.matthiasmann.twl.utils.TextUtil;

public class Label extends TextWidget {
	private boolean autoSize;
	private Widget labelFor;
	private CallbackWithReason[] callbacks;

	public Label() {
		this((AnimationState)null, false);
	}

	public Label(AnimationState animState) {
		this(animState, false);
	}

	public Label(AnimationState animState, boolean inherit) {
		super(animState, inherit);
		this.autoSize = true;
	}

	public Label(String text) {
		this();
		this.setText(text);
	}

	public void addCallback(CallbackWithReason cb) {
		this.callbacks = (CallbackWithReason[])CallbackSupport.addCallbackToList(this.callbacks, cb, CallbackWithReason.class);
	}

	public void removeCallback(CallbackWithReason cb) {
		this.callbacks = (CallbackWithReason[])CallbackSupport.removeCallbackFromList(this.callbacks, cb);
	}

	protected void doCallback(Label.CallbackReason reason) {
		CallbackSupport.fireCallbacks(this.callbacks, reason);
	}

	public boolean isAutoSize() {
		return this.autoSize;
	}

	public void setAutoSize(boolean autoSize) {
		this.autoSize = autoSize;
	}

	public void setFont(Font font) {
		super.setFont(font);
		if(this.autoSize) {
			this.invalidateLayout();
		}

	}

	public String getText() {
		return super.getCharSequence().toString();
	}

	public void setText(String text) {
		text = TextUtil.notNull(text);
		if(!text.equals(this.getText())) {
			super.setCharSequence(text);
			if(this.autoSize) {
				this.invalidateLayout();
			}
		}

	}

	public Object getTooltipContent() {
		Object toolTipContent = super.getTooltipContent();
		return toolTipContent == null && this.labelFor != null ? this.labelFor.getTooltipContent() : toolTipContent;
	}

	public Widget getLabelFor() {
		return this.labelFor;
	}

	public void setLabelFor(Widget labelFor) {
		if(labelFor == this) {
			throw new IllegalArgumentException("labelFor == this");
		} else {
			this.labelFor = labelFor;
		}
	}

	protected void applyThemeLabel(ThemeInfo themeInfo) {
		String themeText = (String)themeInfo.getParameterValue("text", false, String.class);
		if(themeText != null) {
			this.setText(themeText);
		}

	}

	protected void applyTheme(ThemeInfo themeInfo) {
		super.applyTheme(themeInfo);
		this.applyThemeLabel(themeInfo);
	}

	public boolean requestKeyboardFocus() {
		return this.labelFor != null ? this.labelFor.requestKeyboardFocus() : super.requestKeyboardFocus();
	}

	public int getMinWidth() {
		return Math.max(super.getMinWidth(), this.getPreferredWidth());
	}

	public int getMinHeight() {
		return Math.max(super.getMinHeight(), this.getPreferredHeight());
	}

	protected boolean handleEvent(Event evt) {
		this.handleMouseHover(evt);
		if(evt.isMouseEvent()) {
			if(evt.getType() == Event.Type.MOUSE_CLICKED) {
				switch(evt.getMouseClickCount()) {
				case 1:
					this.handleClick(false);
					break;
				case 2:
					this.handleClick(true);
				}
			}

			return evt.getType() != Event.Type.MOUSE_WHEEL;
		} else {
			return false;
		}
	}

	protected void handleClick(boolean doubleClick) {
		this.doCallback(doubleClick ? Label.CallbackReason.DOUBLE_CLICK : Label.CallbackReason.CLICK);
	}

	public static enum CallbackReason {
		CLICK,
		DOUBLE_CLICK;
	}
}
