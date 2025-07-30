package net.minecraft.client.gui;

import org.lwjgl.opengl.GL11;

import com.mojontwins.utils.TextureAtlasSize;

import net.minecraft.client.renderer.FontRenderer;
import net.minecraft.client.renderer.Tessellator;

public class Gui {
	protected float zLevel = 0.0F;

	protected void drawHorizontalLine(int i1, int i2, int i3, int i4) {
		if(i2 < i1) {
			int i5 = i1;
			i1 = i2;
			i2 = i5;
		}

		drawRect(i1, i3, i2 + 1, i3 + 1, i4);
	}

	protected void drawVerticalLine(int i1, int i2, int i3, int i4) {
		if(i3 < i2) {
			int i5 = i2;
			i2 = i3;
			i3 = i5;
		}

		drawRect(i1, i2 + 1, i1 + 1, i3, i4);
	}

	public static void drawRect(int i0, int i1, int i2, int i3, int i4) {
		int i5;
		if(i0 < i2) {
			i5 = i0;
			i0 = i2;
			i2 = i5;
		}

		if(i1 < i3) {
			i5 = i1;
			i1 = i3;
			i3 = i5;
		}

		float f10 = (float)(i4 >> 24 & 255) / 255.0F;
		float f6 = (float)(i4 >> 16 & 255) / 255.0F;
		float f7 = (float)(i4 >> 8 & 255) / 255.0F;
		float f8 = (float)(i4 & 255) / 255.0F;
		Tessellator tessellator9 = Tessellator.instance;
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glColor4f(f6, f7, f8, f10);
		tessellator9.startDrawingQuads();
		tessellator9.addVertex((double)i0, (double)i3, 0.0D);
		tessellator9.addVertex((double)i2, (double)i3, 0.0D);
		tessellator9.addVertex((double)i2, (double)i1, 0.0D);
		tessellator9.addVertex((double)i0, (double)i1, 0.0D);
		tessellator9.draw();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_BLEND);
	}

	protected void drawGradientRect(int i1, int i2, int i3, int i4, int i5, int i6) {
		float f7 = (float)(i5 >> 24 & 255) / 255.0F;
		float f8 = (float)(i5 >> 16 & 255) / 255.0F;
		float f9 = (float)(i5 >> 8 & 255) / 255.0F;
		float f10 = (float)(i5 & 255) / 255.0F;
		float f11 = (float)(i6 >> 24 & 255) / 255.0F;
		float f12 = (float)(i6 >> 16 & 255) / 255.0F;
		float f13 = (float)(i6 >> 8 & 255) / 255.0F;
		float f14 = (float)(i6 & 255) / 255.0F;
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glShadeModel(GL11.GL_SMOOTH);
		Tessellator tessellator15 = Tessellator.instance;
		tessellator15.startDrawingQuads();
		tessellator15.setColorRGBA_F(f8, f9, f10, f7);
		tessellator15.addVertex((double)i3, (double)i2, (double)this.zLevel);
		tessellator15.addVertex((double)i1, (double)i2, (double)this.zLevel);
		tessellator15.setColorRGBA_F(f12, f13, f14, f11);
		tessellator15.addVertex((double)i1, (double)i4, (double)this.zLevel);
		tessellator15.addVertex((double)i3, (double)i4, (double)this.zLevel);
		tessellator15.draw();
		GL11.glShadeModel(GL11.GL_FLAT);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}

	public void drawCenteredString(FontRenderer fontRenderer1, String string2, int i3, int i4, int i5) {
		fontRenderer1.drawStringWithShadow(string2, i3 - fontRenderer1.getStringWidth(string2) / 2, i4, i5);
	}

	public void drawString(FontRenderer fontRenderer1, String string2, int i3, int i4, int i5) {
		fontRenderer1.drawStringWithShadow(string2, i3, i4, i5);
	}

	public void drawTexturedModalRect(int i1, int i2, int i3, int i4, int i5, int i6) {
		float f7 = 0.00390625F;
		float f8 = 0.00390625F;
		Tessellator tessellator9 = Tessellator.instance;
		tessellator9.startDrawingQuads();
		tessellator9.addVertexWithUV((double)(i1 + 0), (double)(i2 + i6), (double)this.zLevel, (double)((float)(i3 + 0) * f7), (double)((float)(i4 + i6) * f8));
		tessellator9.addVertexWithUV((double)(i1 + i5), (double)(i2 + i6), (double)this.zLevel, (double)((float)(i3 + i5) * f7), (double)((float)(i4 + i6) * f8));
		tessellator9.addVertexWithUV((double)(i1 + i5), (double)(i2 + 0), (double)this.zLevel, (double)((float)(i3 + i5) * f7), (double)((float)(i4 + 0) * f8));
		tessellator9.addVertexWithUV((double)(i1 + 0), (double)(i2 + 0), (double)this.zLevel, (double)((float)(i3 + 0) * f7), (double)((float)(i4 + 0) * f8));
		tessellator9.draw();
	}
	
	public void drawTexturedModalRectAtlasAdjusted(int i1, int i2, int i3, int i4, int i5, int i6) {
		float f7 = TextureAtlasSize.w;
		float f8 = TextureAtlasSize.h;
		Tessellator tessellator9 = Tessellator.instance;
		tessellator9.startDrawingQuads();
		tessellator9.addVertexWithUV((double)(i1 + 0), (double)(i2 + i6), (double)this.zLevel, (double)((float)(i3 + 0) / f7), (double)((float)(i4 + i6) / f8));
		tessellator9.addVertexWithUV((double)(i1 + i5), (double)(i2 + i6), (double)this.zLevel, (double)((float)(i3 + i5) / f7), (double)((float)(i4 + i6) / f8));
		tessellator9.addVertexWithUV((double)(i1 + i5), (double)(i2 + 0), (double)this.zLevel, (double)((float)(i3 + i5) / f7), (double)((float)(i4 + 0) / f8));
		tessellator9.addVertexWithUV((double)(i1 + 0), (double)(i2 + 0), (double)this.zLevel, (double)((float)(i3 + 0) / f7), (double)((float)(i4 + 0) / f8));
		tessellator9.draw();
	}
	
	public String twoDigits(int d) {
		if (d < 10) return "0" + d;
		else return "" + d;
	}
}
