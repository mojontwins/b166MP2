package de.matthiasmann.twl;

public interface ListBoxDisplay {
	boolean isSelected();

	void setSelected(boolean z1);

	boolean isFocused();

	void setFocused(boolean z1);

	void setData(Object object1);

	void setTooltipContent(Object object1);

	Widget getWidget();

	void addListBoxCallback(CallbackWithReason callbackWithReason1);

	void removeListBoxCallback(CallbackWithReason callbackWithReason1);
}
