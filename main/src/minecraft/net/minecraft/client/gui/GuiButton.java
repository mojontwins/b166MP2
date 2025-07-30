package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.FontRenderer;

import org.lwjgl.opengl.GL11;

public class GuiButton extends Gui {
	protected int w;
	protected int h;
	public int xPosition;
	public int yPosition;
	public String displayString;
	public int id;
	public boolean enabled;
	public boolean drawButton;
	public boolean forcedOn = false;

	public GuiButton(int id, int x, int y, String caption) {
		this(id, x, y, 200, 20, caption);
	}

	public GuiButton(int id, int x, int y, int w, int h, String caption) {
		this.enabled = true;
		this.drawButton = true;
		this.id = id;
		this.xPosition = x;
		this.yPosition = y;
		this.w = w;
		this.h = h;
		this.displayString = caption;
	}

	protected int getHoverState(boolean hovering) {
		byte texOffset = 1;
		
		if(this.forcedOn) {
			texOffset = 2;
		} else if(!this.enabled) {
			texOffset = 0;
		} else if(hovering) {
			texOffset = 2;
		}

		return texOffset;
	}

	public void drawButton(Minecraft mc, int x, int y) {
		if(this.drawButton) {
			FontRenderer fr = mc.fontRenderer;
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, mc.renderEngine.getTexture("/gui/gui.png"));
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			
			boolean hover = x >= this.xPosition && y >= this.yPosition && x < this.xPosition + this.w && y < this.yPosition + this.h;
			int i6 = this.getHoverState(hover);
			
			this.drawTexturedModalRect(this.xPosition, this.yPosition, 0, 46 + i6 * 20, this.w / 2, this.h);
			this.drawTexturedModalRect(this.xPosition + this.w / 2, this.yPosition, 200 - this.w / 2, 46 + i6 * 20, this.w / 2, this.h);
			this.mouseDragged(mc, x, y);
			
			int textColor = 14737632;
			if(!this.enabled) {
				textColor = -6250336;
			} else if(hover || this.forcedOn) {
				textColor = 16777120;
			}

			this.drawCenteredString(fr, this.displayString, this.xPosition + this.w / 2, this.yPosition + (this.h - 8) / 2, textColor);
		}
	}

	protected void mouseDragged(Minecraft mc, int x, int y) {
	}

	public void mouseReleased(int x, int y) {
	}

	public boolean mousePressed(Minecraft mc, int x, int y) {
		return this.enabled && this.drawButton && x >= this.xPosition && y >= this.yPosition && x < this.xPosition + this.w && y < this.yPosition + this.h;
	}
}
