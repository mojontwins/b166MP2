package net.minecraft.src;

import de.matthiasmann.twl.Widget;
import de.matthiasmann.twl.renderer.lwjgl.LWJGLRenderer;
import de.matthiasmann.twl.renderer.lwjgl.RenderScale;

import net.minecraft.client.Minecraft;

public class GuiModScreen extends GuiScreen {
	public GuiScreen parentScreen;
	public boolean drawbg = true;
	public Widget mainwidget;
	public static GuiModScreen currentscreen;
	private int t = 0;

	protected GuiModScreen(GuiScreen by1) {
		this.parentScreen = by1;
		currentscreen = this;
		this.field_948_f = false;
	}

	public GuiModScreen(GuiScreen by1, Widget w) {
		this.mainwidget = w;
		this.parentScreen = by1;
		currentscreen = this;
		this.field_948_f = false;
	}

	public static void back() {
		if(currentscreen != null) {
			Minecraft m = ModLoader.getMinecraftInstance();
			m.displayGuiScreen(currentscreen.parentScreen);
			if(currentscreen.parentScreen instanceof GuiModScreen) {
				currentscreen = (GuiModScreen)currentscreen.parentScreen;
				currentscreen.setActive();
			} else {
				currentscreen = null;
			}
		}

	}

	public static void show(Widget screen) {
		Minecraft m = ModLoader.getMinecraftInstance();
		GuiModScreen scr = new GuiModScreen(currentscreen, screen);
		m.displayGuiScreen(scr);
		scr.setActive();
	}

	public static void show(GuiModScreen screen) {
		Minecraft m = ModLoader.getMinecraftInstance();
		m.displayGuiScreen(screen);
		screen.setActive();
	}

	public static void clicksound() {
		Minecraft m = ModLoader.getMinecraftInstance();
		m.sndManager.playSoundFX("random.click", 1.0F, 1.0F);
	}

	private void setActive() {
		GuiWidgetScreen.getInstance().setScreen(this.mainwidget);
	}

	public void handleInput() {
	}

	public void drawScreen(int j, int k, float f) {
		if(this.drawbg) {
			this.drawDefaultBackground();
		}

		LWJGLRenderer r = (LWJGLRenderer)GuiWidgetScreen.getInstance().gui.getRenderer();
		ScreenScaleProxy screensize = new ScreenScaleProxy(GuiWidgetScreen.getInstance().mcinstance.displayWidth, GuiWidgetScreen.getInstance().mcinstance.displayHeight);
		RenderScale.scale = screensize.scaleFactor;
		r.syncViewportSize();
		GuiWidgetScreen.getInstance().gui.update();
	}
}
