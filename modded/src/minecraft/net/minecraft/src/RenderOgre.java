package net.minecraft.src;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class RenderOgre extends RenderLiving {
	private ModelOgre2 tempOgre;

	public RenderOgre(ModelOgre2 modelOgre21, ModelBase modelBase2, float f3) {
		super(modelOgre21, f3);
		this.setRenderPassModel(modelBase2);
		this.tempOgre = modelOgre21;
	}

	protected boolean a(EntityOgre entityOgre1, int i2) {
		this.loadTexture("/mob/ogreb.png");
		return i2 == 0 && !entityOgre1.ogreboolean;
	}

	protected boolean shouldRenderPass(EntityLiving entityLiving1, int i2, float f3) {
		return this.a((EntityOgre)entityLiving1, i2);
	}

	public void doRenderLiving(EntityLiving entityLiving1, double d2, double d4, double d6, float f8, float f9) {
		EntityOgre entityOgre10 = (EntityOgre)entityLiving1;
		if(entityLiving1.attackTime <= 0 && entityOgre10.ogreattack) {
			entityOgre10.ogreattack = false;
			entityOgre10.DestroyingOgre();
		}

		GL11.glPushMatrix();
		GL11.glDisable(GL11.GL_CULL_FACE);
		this.mainModel.onGround = this.func_167_c(entityLiving1, f9);
		this.mainModel.isRiding = entityLiving1.isRiding();
		if(this.renderPassModel != null) {
			this.renderPassModel.isRiding = this.mainModel.isRiding;
		}

		try {
			float f11 = entityLiving1.prevRenderYawOffset + (entityLiving1.renderYawOffset - entityLiving1.prevRenderYawOffset) * f9;
			float f12 = entityLiving1.prevRotationYaw + (entityLiving1.rotationYaw - entityLiving1.prevRotationYaw) * f9;
			float f13 = entityLiving1.prevRotationPitch + (entityLiving1.rotationPitch - entityLiving1.prevRotationPitch) * f9;
			this.func_22012_b(entityLiving1, d2, d4, d6);
			float f14 = this.func_170_d(entityLiving1, f9);
			this.rotateCorpse(entityLiving1, f14, f11, f9);
			float f15 = 0.0625F;
			GL11.glEnable(GL12.GL_RESCALE_NORMAL);
			GL11.glScalef(-1.0F, -1.0F, 1.0F);
			this.preRenderCallback(entityLiving1, f9);
			GL11.glTranslatef(0.0F, -24.0F * f15 - 0.007813F, 0.0F);
			float f16 = entityLiving1.field_705_Q + (entityLiving1.field_704_R - entityLiving1.field_705_Q) * f9;
			float f17 = entityLiving1.field_703_S - entityLiving1.field_704_R * (1.0F - f9);
			if(f16 > 1.0F) {
				f16 = 1.0F;
			}

			this.loadDownloadableImageTexture(entityLiving1.skinUrl, entityLiving1.getEntityTexture());
			GL11.glEnable(GL11.GL_ALPHA_TEST);
			this.tempOgre.render(f17, f16, f14, f12 - f11, f13, f15, entityOgre10.ogreattack);

			for(int i18 = 0; i18 < 4; ++i18) {
				if(this.shouldRenderPass(entityLiving1, i18, f9)) {
					this.renderPassModel.render(f17, f16, f14, f12 - f11, f13, f15);
					GL11.glDisable(GL11.GL_BLEND);
					GL11.glEnable(GL11.GL_ALPHA_TEST);
				}
			}

			this.renderEquippedItems(entityLiving1, f9);
			float f26 = entityLiving1.getEntityBrightness(f9);
			int i19 = this.getColorMultiplier(entityLiving1, f26, f9);
			if((i19 >> 24 & 255) > 0 || entityLiving1.hurtTime > 0 || entityLiving1.deathTime > 0) {
				GL11.glDisable(GL11.GL_TEXTURE_2D);
				GL11.glDisable(GL11.GL_ALPHA_TEST);
				GL11.glEnable(GL11.GL_BLEND);
				GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
				GL11.glDepthFunc(GL11.GL_EQUAL);
				if(entityLiving1.hurtTime > 0 || entityLiving1.deathTime > 0) {
					GL11.glColor4f(f26, 0.0F, 0.0F, 0.4F);
					this.tempOgre.render(f17, f16, f14, f12 - f11, f13, f15, entityOgre10.ogreattack);

					for(int i20 = 0; i20 < 4; ++i20) {
						if(this.shouldRenderPass(entityLiving1, i20, f9)) {
							GL11.glColor4f(f26, 0.0F, 0.0F, 0.4F);
							this.renderPassModel.render(f17, f16, f14, f12 - f11, f13, f15);
						}
					}
				}

				if((i19 >> 24 & 255) > 0) {
					float f27 = (float)(i19 >> 16 & 255) / 255.0F;
					float f21 = (float)(i19 >> 8 & 255) / 255.0F;
					float f22 = (float)(i19 & 255) / 255.0F;
					float f23 = (float)(i19 >> 24 & 255) / 255.0F;
					GL11.glColor4f(f27, f21, f22, f23);
					this.tempOgre.render(f17, f16, f14, f12 - f11, f13, f15, entityOgre10.ogreattack);

					for(int i24 = 0; i24 < 4; ++i24) {
						if(this.shouldRenderPass(entityLiving1, i24, f9)) {
							GL11.glColor4f(f27, f21, f22, f23);
							this.renderPassModel.render(f17, f16, f14, f12 - f11, f13, f15);
						}
					}
				}

				GL11.glDepthFunc(GL11.GL_LEQUAL);
				GL11.glDisable(GL11.GL_BLEND);
				GL11.glEnable(GL11.GL_ALPHA_TEST);
				GL11.glEnable(GL11.GL_TEXTURE_2D);
			}

			GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		} catch (Exception exception25) {
			exception25.printStackTrace();
		}

		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glPopMatrix();
		this.passSpecialRender(entityLiving1, d2, d4, d6);
	}
}
