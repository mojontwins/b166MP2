package de.matthiasmann.twl;

import de.matthiasmann.twl.renderer.Font;
import de.matthiasmann.twl.renderer.Image;
import de.matthiasmann.twl.renderer.MouseCursor;

public interface ParameterMap {
	Font getFont(String string1);

	Image getImage(String string1);

	MouseCursor getMouseCursor(String string1);

	ParameterMap getParameterMap(String string1);

	ParameterList getParameterList(String string1);

	boolean getParameter(String string1, boolean z2);

	int getParameter(String string1, int i2);

	float getParameter(String string1, float f2);

	String getParameter(String string1, String string2);

	Color getParameter(String string1, Color color2);

	Enum getParameter(String string1, Enum enum2);

	Object getParameterValue(String string1, boolean z2);

	Object getParameterValue(String string1, boolean z2, Class class3);

	Object getParameterValue(String string1, boolean z2, Class class3, Object object4);
}
