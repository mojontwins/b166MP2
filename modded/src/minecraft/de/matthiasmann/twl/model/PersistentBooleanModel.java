package de.matthiasmann.twl.model;

import java.util.prefs.Preferences;

public class PersistentBooleanModel extends HasCallback implements BooleanModel {
	private final Preferences prefs;
	private final String prefKey;
	private boolean value;

	public PersistentBooleanModel(Preferences prefs, String prefKey, boolean defaultValue) {
		if(prefs == null) {
			throw new NullPointerException("prefs");
		} else if(prefKey == null) {
			throw new NullPointerException("prefKey");
		} else {
			this.prefs = prefs;
			this.prefKey = prefKey;
			this.value = prefs.getBoolean(prefKey, defaultValue);
		}
	}

	public boolean getValue() {
		return this.value;
	}

	public void setValue(boolean value) {
		if(this.value != value) {
			this.value = value;
			this.storeSettings();
			this.doCallback();
		}

	}

	private void storeSettings() {
		this.prefs.putBoolean(this.prefKey, this.value);
	}
}
