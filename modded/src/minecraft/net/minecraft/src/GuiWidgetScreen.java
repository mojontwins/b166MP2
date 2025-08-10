package net.minecraft.src;

import de.matthiasmann.twl.GUI;
import de.matthiasmann.twl.Widget;
import de.matthiasmann.twl.input.lwjgl.LWJGLInput;
import de.matthiasmann.twl.renderer.lwjgl.LWJGLRenderer;
import de.matthiasmann.twl.theme.ThemeManager;

import net.minecraft.client.Minecraft;

public class GuiWidgetScreen extends Widget {
	public static GuiWidgetScreen instance;
	public GUI gui = null;
	public LWJGLRenderer renderer = null;
	public Widget currentwidget = null;
	public ThemeManager theme = null;
	public Minecraft mcinstance;
	public ScreenScaleProxy screensize = null;
	public static int screenwidth;
	public static int screenheight;

	public static GuiWidgetScreen getInstance() {
		if(instance != null) {
			return instance;
		} else {
			try {
				instance = new GuiWidgetScreen();
				instance.renderer = new LWJGLRenderer();
				String e = "twlGuiTheme.xml";
				instance.gui = new GUI(instance, instance.renderer, new LWJGLInput());
				ModSettings.dbgout(GuiWidgetScreen.class.getResource(e).toString());
				instance.theme = ThemeManager.createThemeManager(GuiWidgetScreen.class.getResource(e), instance.renderer);
				if(instance.theme == null) {
					throw new RuntimeException("I don\'t think you installed the theme correctly ...");
				}

				instance.setTheme("");
				instance.gui.applyTheme(instance.theme);
				instance.mcinstance = ModLoader.getMinecraftInstance();
				instance.screensize = new ScreenScaleProxy(instance.mcinstance.displayWidth, instance.mcinstance.displayHeight);
			} catch (Throwable throwable2) {
				throwable2.printStackTrace();
				RuntimeException e2 = new RuntimeException("error loading theme");
				e2.initCause(throwable2);
				throw e2;
			}

			return instance;
		}
	}

	public void setScreen(Widget w) {
		this.gui.resyncTimerAfterPause();
		this.gui.clearKeyboardState();
		this.gui.clearMouseState();
		this.removeAllChildren();
		this.add(w);
		this.currentwidget = w;
	}

	public void resetScreen() {
		this.removeAllChildren();
		this.currentwidget = null;
	}

	public void layout() {
		this.screensize = new ScreenScaleProxy(this.mcinstance.displayWidth, this.mcinstance.displayHeight);
		if(this.currentwidget != null) {
			screenwidth = this.screensize.getScaledWidth();
			screenheight = this.screensize.getScaledHeight();
			this.currentwidget.setSize(screenwidth, screenheight);
			this.currentwidget.setPosition(0, 0);
		}

	}
}
