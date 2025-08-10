package de.matthiasmann.twl.model;

public interface StringModel {
	String getValue();

	void setValue(String string1);

	void addCallback(Runnable runnable1);

	void removeCallback(Runnable runnable1);
}
