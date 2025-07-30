package net.minecraft.client.renderer.entity;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import net.minecraft.client.model.ModelIronGolem;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.monster.EntityIronGolem;
import net.minecraft.world.level.tile.Block;

public class RenderIronGolem extends RenderLiving {
	private ModelIronGolem field_48422_c = (ModelIronGolem)this.mainModel;

	public RenderIronGolem() {
		super(new ModelIronGolem(), 0.5F);
	}

	public void func_48421_a(EntityIronGolem entityIronGolem1, double d2, double d4, double d6, float f8, float f9) {
		super.doRenderLiving(entityIronGolem1, d2, d4, d6, f8, f9);
	}

	protected void func_48420_a(EntityIronGolem entityIronGolem1, float f2, float f3, float f4) {
		super.rotateCorpse(entityIronGolem1, f2, f3, f4);
		if((double)entityIronGolem1.legYaw >= 0.01D) {
			float f5 = 13.0F;
			float f6 = entityIronGolem1.field_703_S - entityIronGolem1.legYaw * (1.0F - f4) + 6.0F;
			float f7 = (Math.abs(f6 % f5 - f5 * 0.5F) - f5 * 0.25F) / (f5 * 0.25F);
			GL11.glRotatef(6.5F * f7, 0.0F, 0.0F, 1.0F);
		}
	}

	protected void func_48419_a(EntityIronGolem entityIronGolem1, float f2) {
		super.renderEquippedItems(entityIronGolem1, f2);
		if(entityIronGolem1.func_48117_D_() != 0) {
			GL11.glEnable(GL12.GL_RESCALE_NORMAL);
			GL11.glPushMatrix();
			GL11.glRotatef(5.0F + 180.0F * this.field_48422_c.field_48233_c.rotateAngleX / (float)Math.PI, 1.0F, 0.0F, 0.0F);
			GL11.glTranslatef(-0.6875F, 1.25F, -0.9375F);
			GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
			float f3 = 0.8F;
			GL11.glScalef(f3, -f3, f3);
			int i4 = entityIronGolem1.getBrightnessForRender(f2);
			int i5 = i4 % 65536;
			int i6 = i4 / 65536;
			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)i5 / 1.0F, (float)i6 / 1.0F);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			this.loadTexture("/terrain.png");
			this.renderBlocks.renderBlockAsItem(Block.plantRed, 0, 1.0F);
			GL11.glPopMatrix();
			GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		}
	}

	protected void renderEquippedItems(EntityLiving entityLiving1, float f2) {
		this.func_48419_a((EntityIronGolem)entityLiving1, f2);
	}

	protected void rotateCorpse(EntityLiving entityLiving1, float f2, float f3, float f4) {
		this.func_48420_a((EntityIronGolem)entityLiving1, f2, f3, f4);
	}

	public void doRenderLiving(EntityLiving entityLiving1, double d2, double d4, double d6, float f8, float f9) {
		this.func_48421_a((EntityIronGolem)entityLiving1, d2, d4, d6, f8, f9);
	}

	public void doRender(Entity entity1, double d2, double d4, double d6, float f8, float f9) {
		this.func_48421_a((EntityIronGolem)entity1, d2, d4, d6, f8, f9);
	}
}
