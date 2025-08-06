package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;

import org.lwjgl.opengl.GL11;

public class GuiButtonLanguage extends GuiButton {
	public GuiButtonLanguage(int i1, int i2, int i3) {
		super(i1, i2, i3, 20, 20, "");
	}

	public void drawButton(Minecraft mc, int i2, int i3) {
		if(this.drawButton) {
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, mc.renderEngine.getTexture("/gui/gui.png"));
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			boolean z4 = i2 >= this.xPosition && i3 >= this.yPosition && i2 < this.xPosition + this.w && i3 < this.yPosition + this.h;
			int i5 = 106;
			if(z4) {
				i5 += this.h;
			}

			this.drawTexturedModalRect(this.xPosition, this.yPosition, 0, i5, this.w, this.h);
		}
	}
}
