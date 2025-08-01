package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;

import org.lwjgl.opengl.GL11;

public class GuiSlider extends GuiButton {
	public float sliderValue = 1.0F;
	public boolean dragging = false;
	private EnumOptions idFloat = null;

	public GuiSlider(int i1, int i2, int i3, EnumOptions enumOptions4, String string5, float f6) {
		super(i1, i2, i3, 150, 20, string5);
		this.idFloat = enumOptions4;
		this.sliderValue = f6;
	}

	protected int getHoverState(boolean z1) {
		return 0;
	}

	protected void mouseDragged(Minecraft mc, int i2, int i3) {
		if(this.drawButton) {
			if(this.dragging) {
				this.sliderValue = (float)(i2 - (this.xPosition + 4)) / (float)(this.w - 8);
				if(this.sliderValue < 0.0F) {
					this.sliderValue = 0.0F;
				}

				if(this.sliderValue > 1.0F) {
					this.sliderValue = 1.0F;
				}

				mc.gameSettings.setOptionFloatValue(this.idFloat, this.sliderValue);
				this.displayString = mc.gameSettings.getKeyBinding(this.idFloat);
			}

			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			this.drawTexturedModalRect(this.xPosition + (int)(this.sliderValue * (float)(this.w - 8)), this.yPosition, 0, 66, 4, 20);
			this.drawTexturedModalRect(this.xPosition + (int)(this.sliderValue * (float)(this.w - 8)) + 4, this.yPosition, 196, 66, 4, 20);
		}
	}

	public boolean mousePressed(Minecraft mc, int i2, int i3) {
		if(super.mousePressed(mc, i2, i3)) {
			this.sliderValue = (float)(i2 - (this.xPosition + 4)) / (float)(this.w - 8);
			if(this.sliderValue < 0.0F) {
				this.sliderValue = 0.0F;
			}

			if(this.sliderValue > 1.0F) {
				this.sliderValue = 1.0F;
			}

			mc.gameSettings.setOptionFloatValue(this.idFloat, this.sliderValue);
			this.displayString = mc.gameSettings.getKeyBinding(this.idFloat);
			this.dragging = true;
			return true;
		} else {
			return false;
		}
	}

	public void mouseReleased(int i1, int i2) {
		this.dragging = false;
	}
}
