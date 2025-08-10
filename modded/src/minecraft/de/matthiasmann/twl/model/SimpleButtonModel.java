package de.matthiasmann.twl.model;

import de.matthiasmann.twl.utils.CallbackSupport;

public class SimpleButtonModel implements ButtonModel {
	protected static final int STATE_MASK_HOVER = 1;
	protected static final int STATE_MASK_PRESSED = 2;
	protected static final int STATE_MASK_ARMED = 4;
	protected static final int STATE_MASK_DISABLED = 8;
	protected Runnable[] actionCallbacks;
	protected Runnable[] stateCallbacks;
	protected int state;

	public boolean isSelected() {
		return false;
	}

	public boolean isPressed() {
		return (this.state & 2) != 0;
	}

	public boolean isArmed() {
		return (this.state & 4) != 0;
	}

	public boolean isHover() {
		return (this.state & 1) != 0;
	}

	public boolean isEnabled() {
		return (this.state & 8) == 0;
	}

	public void setSelected(boolean selected) {
	}

	public void setPressed(boolean pressed) {
		if(pressed != this.isPressed()) {
			boolean fireAction = !pressed && this.isArmed() && this.isEnabled();
			this.setStateBit(2, pressed);
			this.fireStateCallback();
			if(fireAction) {
				this.buttonAction();
			}
		}

	}

	public void setArmed(boolean armed) {
		if(armed != this.isArmed()) {
			this.setStateBit(4, armed);
			this.fireStateCallback();
		}

	}

	public void setHover(boolean hover) {
		if(hover != this.isHover()) {
			this.setStateBit(1, hover);
			this.fireStateCallback();
		}

	}

	public void setEnabled(boolean enabled) {
		if(enabled != this.isEnabled()) {
			this.setStateBit(8, !enabled);
			this.fireStateCallback();
		}

	}

	protected void buttonAction() {
		this.fireActionCallback();
	}

	protected void setStateBit(int mask, boolean set) {
		if(set) {
			this.state |= mask;
		} else {
			this.state &= ~mask;
		}

	}

	protected void fireStateCallback() {
		CallbackSupport.fireCallbacks(this.stateCallbacks);
	}

	public void fireActionCallback() {
		CallbackSupport.fireCallbacks(this.actionCallbacks);
	}

	public void addActionCallback(Runnable callback) {
		this.actionCallbacks = (Runnable[])CallbackSupport.addCallbackToList(this.actionCallbacks, callback, Runnable.class);
	}

	public void removeActionCallback(Runnable callback) {
		this.actionCallbacks = (Runnable[])CallbackSupport.removeCallbackFromList(this.actionCallbacks, callback);
	}

	public boolean hasActionCallbacks() {
		return this.actionCallbacks != null;
	}

	public void addStateCallback(Runnable callback) {
		this.stateCallbacks = (Runnable[])CallbackSupport.addCallbackToList(this.stateCallbacks, callback, Runnable.class);
	}

	public void removeStateCallback(Runnable callback) {
		this.stateCallbacks = (Runnable[])CallbackSupport.removeCallbackFromList(this.stateCallbacks, callback);
	}

	public void connect() {
	}

	public void disconnect() {
	}
}
