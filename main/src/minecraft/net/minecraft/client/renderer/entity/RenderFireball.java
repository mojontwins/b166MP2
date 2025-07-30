package net.minecraft.client.renderer.entity;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.mojontwins.utils.Idx2uvF;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.EntityFireball;

public class RenderFireball extends Render {
	private float field_40269_a;

	public RenderFireball(float f1) {
		this.field_40269_a = f1;
	}

	public void doRenderFireball(EntityFireball entityFireball1, double d2, double d4, double d6, float f8, float f9) {
		GL11.glPushMatrix();
		GL11.glTranslatef((float)d2, (float)d4, (float)d6);
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		float f10 = this.field_40269_a;
		GL11.glScalef(f10 / 1.0F, f10 / 1.0F, f10 / 1.0F);
		byte b11 = 46;
		this.loadTexture("/gui/items.png");
		Tessellator tessellator12 = Tessellator.instance;
		/*
		float f13 = (float)(b11 % 16 * 16 + 0) / 256.0F;
		float f14 = (float)(b11 % 16 * 16 + 16) / 256.0F;
		float f15 = (float)(b11 / 16 * 16 + 0) / 256.0F;
		float f16 = (float)(b11 / 16 * 16 + 16) / 256.0F;
		*/
		Idx2uvF.calc(b11);
		double f13 = Idx2uvF.u1;
		double f14 = Idx2uvF.u2;
		double f15 = Idx2uvF.v1;
		double f16 = Idx2uvF.v2;
		
		float f17 = 1.0F;
		float f18 = 0.5F;
		float f19 = 0.25F;
		GL11.glRotatef(180.0F - this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(-this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
		tessellator12.startDrawingQuads();
		tessellator12.setNormal(0.0F, 1.0F, 0.0F);
		tessellator12.addVertexWithUV((double)(0.0F - f18), (double)(0.0F - f19), 0.0D, f13, f16);
		tessellator12.addVertexWithUV((double)(f17 - f18), (double)(0.0F - f19), 0.0D, f14, f16);
		tessellator12.addVertexWithUV((double)(f17 - f18), (double)(1.0F - f19), 0.0D, f14, f15);
		tessellator12.addVertexWithUV((double)(0.0F - f18), (double)(1.0F - f19), 0.0D, f13, f15);
		tessellator12.draw();
		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		GL11.glPopMatrix();
	}

	public void doRender(Entity entity1, double d2, double d4, double d6, float f8, float f9) {
		this.doRenderFireball((EntityFireball)entity1, d2, d4, d6, f8, f9);
	}
}
