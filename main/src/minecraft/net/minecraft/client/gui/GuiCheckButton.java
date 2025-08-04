package net.minecraft.client.gui;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.FontRenderer;

public class GuiCheckButton extends GuiButton {

	public GuiCheckButton(int id, int x, int y, int w, int h, String caption) {
		super(id, x, y, w, h, caption);
	}

	public void drawButton(Minecraft mc, int x, int y) {
		if(this.drawButton) {
			FontRenderer fr = mc.fontRenderer;
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, mc.renderEngine.getTexture("/gui/gui.png"));
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			
			this.hover = x >= this.xPosition && y >= this.yPosition && x < this.xPosition + this.w && y < this.yPosition + this.h;
			int onOff = this.forcedOn ? 166 : 146;
			if(this.hover) onOff += 40;
			
			this.drawTexturedModalRect(this.xPosition, this.yPosition, 0, onOff, this.w / 2, this.h);
			this.drawTexturedModalRect(this.xPosition + this.w / 2, this.yPosition, 200 - this.w / 2, onOff, this.w / 2, this.h);
			this.mouseDragged(mc, x, y);
			
			int textColor = 14737632;
			if(!this.enabled) {
				textColor = -6250336;
			} else if(this.hover) {
				textColor = 16777120;
			}

			this.drawString(fr, this.displayString, this.xPosition + 36, this.yPosition + (this.h - 8) / 2, textColor);
		}
	}

}
