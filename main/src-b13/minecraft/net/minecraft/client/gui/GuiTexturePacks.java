package net.minecraft.client.gui;

import java.io.File;
import java.net.URI;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.FontRenderer;
import net.minecraft.util.StringTranslate;

import org.lwjgl.Sys;

public class GuiTexturePacks extends GuiScreen {
	protected GuiScreen guiScreen;
	private int refreshTimer = -1;
	private String fileLocation = "";
	private GuiTexturePackSlot guiTexturePackSlot;

	public GuiTexturePacks(GuiScreen guiScreen1) {
		this.guiScreen = guiScreen1;
	}

	public void initGui() {
		StringTranslate stringTranslate1 = StringTranslate.getInstance();
		this.controlList.add(new GuiSmallButton(5, this.width / 2 - 154, this.height - 48, stringTranslate1.translateKey("texturePack.openFolder")));
		this.controlList.add(new GuiSmallButton(6, this.width / 2 + 4, this.height - 48, stringTranslate1.translateKey("gui.done")));
		this.mc.texturePackList.updateAvaliableTexturePacks();
		this.fileLocation = (new File(Minecraft.getMinecraftDir(), "texturepacks")).getAbsolutePath();
		this.guiTexturePackSlot = new GuiTexturePackSlot(this);
		this.guiTexturePackSlot.registerScrollButtons(this.controlList, 7, 8);
	}

	protected void actionPerformed(GuiButton guiButton1) {
		if(guiButton1.enabled) {
			if(guiButton1.id == 5) {
				boolean z2 = false;

				try {
					Class<?> class3 = Class.forName("java.awt.Desktop");
					Object object4 = class3.getMethod("getDesktop", new Class[0]).invoke((Object)null, new Object[0]);
					class3.getMethod("browse", new Class[]{URI.class}).invoke(object4, new Object[]{(new File(Minecraft.getMinecraftDir(), "texturepacks")).toURI()});
				} catch (Throwable throwable5) {
					throwable5.printStackTrace();
					z2 = true;
				}

				if(z2) {
					System.out.println("Opening via Sys class!");
					Sys.openURL("file://" + this.fileLocation);
				}
			} else if(guiButton1.id == 6) {
				this.mc.renderEngine.refreshTextures();
				this.mc.displayGuiScreen(this.guiScreen);
			} else {
				this.guiTexturePackSlot.actionPerformed(guiButton1);
			}

		}
	}

	protected void mouseClicked(int i1, int i2, int i3) {
		super.mouseClicked(i1, i2, i3);
	}

	protected void mouseMovedOrUp(int i1, int i2, int i3) {
		super.mouseMovedOrUp(i1, i2, i3);
	}

	public void drawScreen(int i1, int i2, float f3) {
		this.guiTexturePackSlot.drawScreen(i1, i2, f3);
		if(this.refreshTimer <= 0) {
			this.mc.texturePackList.updateAvaliableTexturePacks();
			this.refreshTimer += 20;
		}

		StringTranslate stringTranslate4 = StringTranslate.getInstance();
		this.drawCenteredString(this.fontRenderer, stringTranslate4.translateKey("texturePack.title"), this.width / 2, 16, 0xFFFFFF);
		this.drawCenteredString(this.fontRenderer, stringTranslate4.translateKey("texturePack.folderInfo"), this.width / 2 - 77, this.height - 26, 8421504);
		super.drawScreen(i1, i2, f3);
	}

	public void updateScreen() {
		super.updateScreen();
		--this.refreshTimer;
	}

	static Minecraft func_22124_a(GuiTexturePacks guiTexturePacks0) {
		return guiTexturePacks0.mc;
	}

	static Minecraft func_22126_b(GuiTexturePacks guiTexturePacks0) {
		return guiTexturePacks0.mc;
	}

	static Minecraft func_22119_c(GuiTexturePacks guiTexturePacks0) {
		return guiTexturePacks0.mc;
	}

	static Minecraft func_22122_d(GuiTexturePacks guiTexturePacks0) {
		return guiTexturePacks0.mc;
	}

	static Minecraft func_22117_e(GuiTexturePacks guiTexturePacks0) {
		return guiTexturePacks0.mc;
	}

	static Minecraft func_35307_f(GuiTexturePacks guiTexturePacks0) {
		return guiTexturePacks0.mc;
	}

	static Minecraft func_35308_g(GuiTexturePacks guiTexturePacks0) {
		return guiTexturePacks0.mc;
	}

	static Minecraft func_22118_f(GuiTexturePacks guiTexturePacks0) {
		return guiTexturePacks0.mc;
	}

	static Minecraft func_22116_g(GuiTexturePacks guiTexturePacks0) {
		return guiTexturePacks0.mc;
	}

	static Minecraft func_22121_h(GuiTexturePacks guiTexturePacks0) {
		return guiTexturePacks0.mc;
	}

	static Minecraft func_22123_i(GuiTexturePacks guiTexturePacks0) {
		return guiTexturePacks0.mc;
	}

	static FontRenderer func_22127_j(GuiTexturePacks guiTexturePacks0) {
		return guiTexturePacks0.fontRenderer;
	}

	static FontRenderer func_22120_k(GuiTexturePacks guiTexturePacks0) {
		return guiTexturePacks0.fontRenderer;
	}

	static FontRenderer func_22125_l(GuiTexturePacks guiTexturePacks0) {
		return guiTexturePacks0.fontRenderer;
	}
}
