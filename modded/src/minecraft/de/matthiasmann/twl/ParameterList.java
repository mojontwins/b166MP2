package de.matthiasmann.twl;

import de.matthiasmann.twl.renderer.Font;
import de.matthiasmann.twl.renderer.Image;
import de.matthiasmann.twl.renderer.MouseCursor;

public interface ParameterList {
	int getSize();

	Font getFont(int i1);

	Image getImage(int i1);

	MouseCursor getMouseCursor(int i1);

	ParameterMap getParameterMap(int i1);

	ParameterList getParameterList(int i1);

	boolean getParameter(int i1, boolean z2);

	int getParameter(int i1, int i2);

	float getParameter(int i1, float f2);

	String getParameter(int i1, String string2);

	Color getParameter(int i1, Color color2);

	Enum getParameter(int i1, Enum enum2);

	Object getParameterValue(int i1);

	Object getParameterValue(int i1, Class class2);
}
