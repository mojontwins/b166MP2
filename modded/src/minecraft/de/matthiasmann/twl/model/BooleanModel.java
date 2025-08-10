package de.matthiasmann.twl.model;

public interface BooleanModel {
	boolean getValue();

	void setValue(boolean z1);

	void addCallback(Runnable runnable1);

	void removeCallback(Runnable runnable1);
}
