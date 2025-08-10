package de.matthiasmann.twl.model;

import de.matthiasmann.twl.utils.CallbackSupport;

public abstract class AbstractProperty implements Property {
	private Runnable[] valueChangedCallbacks = null;

	public void addValueChangedCallback(Runnable cb) {
		this.valueChangedCallbacks = (Runnable[])CallbackSupport.addCallbackToList(this.valueChangedCallbacks, cb, Runnable.class);
	}

	public void removeValueChangedCallback(Runnable cb) {
		this.valueChangedCallbacks = (Runnable[])CallbackSupport.removeCallbackFromList(this.valueChangedCallbacks, cb);
	}

	protected void fireValueChangedCallback() {
		CallbackSupport.fireCallbacks(this.valueChangedCallbacks);
	}
}
