package de.matthiasmann.twl.model;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

public class PersistentEnumModel extends AbstractEnumModel {
	private final Preferences prefs;
	private final String prefKey;
	private Enum value;

	public PersistentEnumModel(Preferences prefs, String prefKey, Enum defaultValue) {
		this(prefs, prefKey, defaultValue.getDeclaringClass(), defaultValue);
	}

	public PersistentEnumModel(Preferences prefs, String prefKey, Class enumClass, Enum defaultValue) {
		super(enumClass);
		if(prefs == null) {
			throw new NullPointerException("prefs");
		} else if(prefKey == null) {
			throw new NullPointerException("prefKey");
		} else if(defaultValue == null) {
			throw new NullPointerException("value");
		} else {
			this.prefs = prefs;
			this.prefKey = prefKey;
			Enum storedValue = defaultValue;
			String storedStr = prefs.get(prefKey, (String)null);
			if(storedStr != null) {
				try {
					storedValue = Enum.valueOf(enumClass, storedStr);
				} catch (IllegalArgumentException illegalArgumentException8) {
					Logger.getLogger(PersistentEnumModel.class.getName()).log(Level.WARNING, "Unable to parse value \'" + storedStr + "\' of key \'" + prefKey + "\' of type " + enumClass, illegalArgumentException8);
				}
			}

			this.setValue(storedValue);
		}
	}

	public Enum getValue() {
		return this.value;
	}

	public void setValue(Enum value) {
		if(value == null) {
			throw new NullPointerException("value");
		} else {
			if(this.value != value) {
				this.value = value;
				this.storeSetting();
				this.doCallback();
			}

		}
	}

	private void storeSetting() {
		if(this.prefs != null) {
			this.prefs.put(this.prefKey, this.value.name());
		}

	}
}
