package de.matthiasmann.twl.model;

public interface ButtonModel {
	boolean isSelected();

	boolean isPressed();

	boolean isArmed();

	boolean isHover();

	boolean isEnabled();

	void setSelected(boolean z1);

	void setPressed(boolean z1);

	void setArmed(boolean z1);

	void setHover(boolean z1);

	void setEnabled(boolean z1);

	void addActionCallback(Runnable runnable1);

	void removeActionCallback(Runnable runnable1);

	void fireActionCallback();

	boolean hasActionCallbacks();

	void addStateCallback(Runnable runnable1);

	void removeStateCallback(Runnable runnable1);

	void connect();

	void disconnect();
}
