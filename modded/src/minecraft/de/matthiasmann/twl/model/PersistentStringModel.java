package de.matthiasmann.twl.model;

import java.util.prefs.Preferences;

public class PersistentStringModel extends HasCallback implements StringModel {
	private final Preferences prefs;
	private final String prefKey;
	private String value;

	public PersistentStringModel(Preferences prefs, String prefKey, String defaultValue) {
		if(prefs == null) {
			throw new NullPointerException("prefs");
		} else if(prefKey == null) {
			throw new NullPointerException("prefKey");
		} else if(defaultValue == null) {
			throw new NullPointerException("defaultValue");
		} else {
			this.prefs = prefs;
			this.prefKey = prefKey;
			this.value = prefs.get(prefKey, defaultValue);
		}
	}

	public String getValue() {
		return this.value;
	}

	public void setValue(String value) {
		if(value == null) {
			throw new NullPointerException("value");
		} else {
			if(!this.value.equals(value)) {
				this.value = value;
				this.prefs.put(this.prefKey, value);
				this.doCallback();
			}

		}
	}
}
