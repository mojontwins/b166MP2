package net.minecraft.client.renderer.entity;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.mojontwins.utils.Idx2uvF;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.entity.Entity;

public class RenderSnowball extends Render {
	private int itemIconIndex;

	public RenderSnowball(int i1) {
		this.itemIconIndex = i1;
	}

	public void doRender(Entity entity1, double d2, double d4, double d6, float f8, float f9) {
		GL11.glPushMatrix();
		GL11.glTranslatef((float)d2, (float)d4, (float)d6);
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		GL11.glScalef(0.5F, 0.5F, 0.5F);
		this.loadTexture("/gui/items.png");
		Tessellator tessellator10 = Tessellator.instance;
		/*
		if(this.itemIconIndex == 154) {
			int i11 = PotionHelper.func_40358_a(((EntityPotion)entity1).getPotionDamage(), false);
			float f12 = (float)(i11 >> 16 & 255) / 255.0F;
			float f13 = (float)(i11 >> 8 & 255) / 255.0F;
			float f14 = (float)(i11 & 255) / 255.0F;
			GL11.glColor3f(f12, f13, f14);
			GL11.glPushMatrix();
			this.func_40265_a(tessellator10, 141);
			GL11.glPopMatrix();
			GL11.glColor3f(1.0F, 1.0F, 1.0F);
		}
		*/

		this.func_40265_a(tessellator10, this.itemIconIndex);
		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		GL11.glPopMatrix();
	}

	private void func_40265_a(Tessellator tessellator1, int i2) {
		/*
		float f3 = (float)(i2 % 16 * 16 + 0) / 256.0F;
		float f4 = (float)(i2 % 16 * 16 + 16) / 256.0F;
		float f5 = (float)(i2 / 16 * 16 + 0) / 256.0F;
		float f6 = (float)(i2 / 16 * 16 + 16) / 256.0F;
		*/
		
		Idx2uvF.calc(i2);
		double f3 = Idx2uvF.u1;
		double f4 = Idx2uvF.u2;
		double f5 = Idx2uvF.v1;
		double f6 = Idx2uvF.v2;
		
		float f7 = 1.0F;
		float f8 = 0.5F;
		float f9 = 0.25F;
		GL11.glRotatef(180.0F - this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(-this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
		tessellator1.startDrawingQuads();
		tessellator1.setNormal(0.0F, 1.0F, 0.0F);
		tessellator1.addVertexWithUV((double)(0.0F - f8), (double)(0.0F - f9), 0.0D, f3, f6);
		tessellator1.addVertexWithUV((double)(f7 - f8), (double)(0.0F - f9), 0.0D, f4, f6);
		tessellator1.addVertexWithUV((double)(f7 - f8), (double)(f7 - f9), 0.0D, f4, f5);
		tessellator1.addVertexWithUV((double)(0.0F - f8), (double)(f7 - f9), 0.0D, f3, f5);
		tessellator1.draw();
	}
}
