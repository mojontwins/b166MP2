package net.minecraft.src;

/** @deprecated */
public class WidgetClassicWindow extends WidgetSimplewindow {
	/** @deprecated */
	@Deprecated
	public void init() {
		super.init();
		System.err.println("WidgetClassicWindow is deprecated, please update mods using it to use WSimplewindow");
	}
}
