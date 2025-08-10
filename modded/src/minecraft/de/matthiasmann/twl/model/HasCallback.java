package de.matthiasmann.twl.model;

import de.matthiasmann.twl.utils.CallbackSupport;

public class HasCallback {
	private Runnable[] callbacks;

	public void addCallback(Runnable callback) {
		this.callbacks = (Runnable[])CallbackSupport.addCallbackToList(this.callbacks, callback, Runnable.class);
	}

	public void removeCallback(Runnable callback) {
		this.callbacks = (Runnable[])CallbackSupport.removeCallbackFromList(this.callbacks, callback);
	}

	protected void doCallback() {
		CallbackSupport.fireCallbacks(this.callbacks);
	}
}
