package net.minecraft.client.particle;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.RenderEngine;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.level.World;

public class EntityLargeExplodeFX extends EntityFX {
	private int field_35130_a = 0;
	private int field_35129_ay = 0;
	private RenderEngine field_35128_az;
	private float field_35131_aA;

	public EntityLargeExplodeFX(RenderEngine renderEngine1, World world2, double d3, double d5, double d7, double d9, double d11, double d13) {
		super(world2, d3, d5, d7, 0.0D, 0.0D, 0.0D);
		this.field_35128_az = renderEngine1;
		this.field_35129_ay = 6 + this.rand.nextInt(4);
		this.particleRed = this.particleGreen = this.particleBlue = this.rand.nextFloat() * 0.6F + 0.4F;
		this.field_35131_aA = 1.0F - (float)d9 * 0.5F;
	}

	public void renderParticle(Tessellator tessellator1, float f2, float f3, float f4, float f5, float f6, float f7) {
		int i8 = (int)(((float)this.field_35130_a + f2) * 15.0F / (float)this.field_35129_ay);
		if(i8 <= 15) {
			this.field_35128_az.bindTexture(this.field_35128_az.getTexture("/misc/explosion.png"));
			float f9 = (float)(i8 % 4) / 4.0F;
			float f10 = f9 + 0.24975F;
			float f11 = (float)(i8 / 4) / 4.0F;
			float f12 = f11 + 0.24975F;
			float f13 = 2.0F * this.field_35131_aA;
			float f14 = (float)(this.prevPosX + (this.posX - this.prevPosX) * (double)f2 - interpPosX);
			float f15 = (float)(this.prevPosY + (this.posY - this.prevPosY) * (double)f2 - interpPosY);
			float f16 = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * (double)f2 - interpPosZ);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			GL11.glDisable(GL11.GL_LIGHTING);
			RenderHelper.disableStandardItemLighting();
			tessellator1.startDrawingQuads();
			tessellator1.setColorRGBA_F(this.particleRed, this.particleGreen, this.particleBlue, 1.0F);
			tessellator1.setNormal(0.0F, 1.0F, 0.0F);
			tessellator1.setBrightness(240);
			tessellator1.addVertexWithUV((double)(f14 - f3 * f13 - f6 * f13), (double)(f15 - f4 * f13), (double)(f16 - f5 * f13 - f7 * f13), (double)f10, (double)f12);
			tessellator1.addVertexWithUV((double)(f14 - f3 * f13 + f6 * f13), (double)(f15 + f4 * f13), (double)(f16 - f5 * f13 + f7 * f13), (double)f10, (double)f11);
			tessellator1.addVertexWithUV((double)(f14 + f3 * f13 + f6 * f13), (double)(f15 + f4 * f13), (double)(f16 + f5 * f13 + f7 * f13), (double)f9, (double)f11);
			tessellator1.addVertexWithUV((double)(f14 + f3 * f13 - f6 * f13), (double)(f15 - f4 * f13), (double)(f16 + f5 * f13 - f7 * f13), (double)f9, (double)f12);
			tessellator1.draw();
			GL11.glPolygonOffset(0.0F, 0.0F);
			GL11.glEnable(GL11.GL_LIGHTING);
		}
	}

	public int getBrightnessForRender(float f1) {
		return 61680;
	}

	public void onUpdate() {
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		++this.field_35130_a;
		if(this.field_35130_a == this.field_35129_ay) {
			this.setDead();
		}

	}

	public int getFXLayer() {
		return 3;
	}
}
