package de.matthiasmann.twl.model;

import de.matthiasmann.twl.utils.CallbackSupport;

public abstract class AbstractOptionModel implements BooleanModel {
	Runnable[] callbacks;
	Runnable srcCallback;

	public void addCallback(Runnable callback) {
		if(callback == null) {
			throw new NullPointerException("callback");
		} else {
			if(this.callbacks == null) {
				this.srcCallback = new Runnable() {
					boolean lastValue = AbstractOptionModel.this.getValue();

					public void run() {
						boolean value = AbstractOptionModel.this.getValue();
						if(this.lastValue != value) {
							this.lastValue = value;
							CallbackSupport.fireCallbacks(AbstractOptionModel.this.callbacks);
						}

					}
				};
				this.callbacks = new Runnable[]{callback};
				this.installSrcCallback(this.srcCallback);
			} else {
				this.callbacks = (Runnable[])CallbackSupport.addCallbackToList(this.callbacks, callback, Runnable.class);
			}

		}
	}

	public void removeCallback(Runnable callback) {
		this.callbacks = (Runnable[])CallbackSupport.removeCallbackFromList(this.callbacks, callback);
		if(this.callbacks == null && this.srcCallback != null) {
			this.removeSrcCallback(this.srcCallback);
			this.srcCallback = null;
		}

	}

	protected abstract void installSrcCallback(Runnable runnable1);

	protected abstract void removeSrcCallback(Runnable runnable1);
}
