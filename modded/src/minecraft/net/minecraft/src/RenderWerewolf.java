package net.minecraft.src;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class RenderWerewolf extends RenderLiving {
	private ModelWerewolf tempWerewolf;

	public RenderWerewolf(ModelWereHuman modelWereHuman1, ModelBase modelBase2, float f3) {
		super(modelBase2, f3);
		this.setRenderPassModel(modelWereHuman1);
		this.tempWerewolf = (ModelWerewolf)modelBase2;
	}

	protected boolean func_176_a(EntityWerewolf entityWerewolf1, int i2) {
		if(!entityWerewolf1.humanform) {
			entityWerewolf1.texture = "/mob/werewolf.png";
			this.loadTexture("/mob/wereblank.png");
		} else {
			entityWerewolf1.texture = "/mob/wereblank.png";
			this.loadTexture("/mob/werehuman.png");
		}

		return i2 == 0 && !entityWerewolf1.wereboolean;
	}

	protected boolean shouldRenderPass(EntityLiving entityLiving1, int i2, float f3) {
		return this.func_176_a((EntityWerewolf)entityLiving1, i2);
	}

	public void doRenderLiving(EntityLiving entityLiving1, double d2, double d4, double d6, float f8, float f9) {
		EntityWerewolf entityWerewolf10 = (EntityWerewolf)entityLiving1;
		if(entityWerewolf10.humanform) {
			super.doRenderLiving(entityLiving1, d2, d4, d6, f8, f9);
		} else {
			boolean z11 = entityWerewolf10.hunched;
			GL11.glPushMatrix();
			GL11.glDisable(GL11.GL_CULL_FACE);
			this.mainModel.onGround = this.func_167_c(entityLiving1, f9);
			this.mainModel.isRiding = entityLiving1.isRiding();
			if(this.renderPassModel != null) {
				this.renderPassModel.isRiding = this.mainModel.isRiding;
			}

			try {
				float f12 = entityLiving1.prevRenderYawOffset + (entityLiving1.renderYawOffset - entityLiving1.prevRenderYawOffset) * f9;
				float f13 = entityLiving1.prevRotationYaw + (entityLiving1.rotationYaw - entityLiving1.prevRotationYaw) * f9;
				float f14 = entityLiving1.prevRotationPitch + (entityLiving1.rotationPitch - entityLiving1.prevRotationPitch) * f9;
				this.func_22012_b(entityLiving1, d2, d4, d6);
				float f15 = this.func_170_d(entityLiving1, f9);
				this.rotateCorpse(entityLiving1, f15, f12, f9);
				float f16 = 0.0625F;
				GL11.glEnable(GL12.GL_RESCALE_NORMAL);
				GL11.glScalef(-1.0F, -1.0F, 1.0F);
				this.preRenderCallback(entityLiving1, f9);
				GL11.glTranslatef(0.0F, -24.0F * f16 - 0.007813F, 0.0F);
				float f17 = entityLiving1.field_705_Q + (entityLiving1.field_704_R - entityLiving1.field_705_Q) * f9;
				float f18 = entityLiving1.field_703_S - entityLiving1.field_704_R * (1.0F - f9);
				if(f17 > 1.0F) {
					f17 = 1.0F;
				}

				this.loadDownloadableImageTexture(entityLiving1.skinUrl, entityLiving1.getEntityTexture());
				GL11.glEnable(GL11.GL_ALPHA_TEST);
				this.tempWerewolf.render(f18, f17, f15, f13 - f12, f14, f16, z11);

				for(int i19 = 0; i19 < 4; ++i19) {
					if(this.shouldRenderPass(entityLiving1, i19, f9)) {
						this.renderPassModel.render(f18, f17, f15, f13 - f12, f14, f16);
						GL11.glDisable(GL11.GL_BLEND);
						GL11.glEnable(GL11.GL_ALPHA_TEST);
					}
				}

				this.renderEquippedItems(entityLiving1, f9);
				float f27 = entityLiving1.getEntityBrightness(f9);
				int i20 = this.getColorMultiplier(entityLiving1, f27, f9);
				if((i20 >> 24 & 255) > 0 || entityLiving1.hurtTime > 0 || entityLiving1.deathTime > 0) {
					GL11.glDisable(GL11.GL_TEXTURE_2D);
					GL11.glDisable(GL11.GL_ALPHA_TEST);
					GL11.glEnable(GL11.GL_BLEND);
					GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
					GL11.glDepthFunc(GL11.GL_EQUAL);
					if(entityLiving1.hurtTime > 0 || entityLiving1.deathTime > 0) {
						GL11.glColor4f(f27, 0.0F, 0.0F, 0.4F);
						this.tempWerewolf.render(f18, f17, f15, f13 - f12, f14, f16, z11);

						for(int i21 = 0; i21 < 4; ++i21) {
							if(this.shouldRenderPass(entityLiving1, i21, f9)) {
								GL11.glColor4f(f27, 0.0F, 0.0F, 0.4F);
								this.renderPassModel.render(f18, f17, f15, f13 - f12, f14, f16);
							}
						}
					}

					if((i20 >> 24 & 255) > 0) {
						float f28 = (float)(i20 >> 16 & 255) / 255.0F;
						float f22 = (float)(i20 >> 8 & 255) / 255.0F;
						float f23 = (float)(i20 & 255) / 255.0F;
						float f24 = (float)(i20 >> 24 & 255) / 255.0F;
						GL11.glColor4f(f28, f22, f23, f24);
						this.tempWerewolf.render(f18, f17, f15, f13 - f12, f14, f16, z11);

						for(int i25 = 0; i25 < 4; ++i25) {
							if(this.shouldRenderPass(entityLiving1, i25, f9)) {
								GL11.glColor4f(f28, f22, f23, f24);
								this.renderPassModel.render(f18, f17, f15, f13 - f12, f14, f16);
							}
						}
					}

					GL11.glDepthFunc(GL11.GL_LEQUAL);
					GL11.glDisable(GL11.GL_BLEND);
					GL11.glEnable(GL11.GL_ALPHA_TEST);
					GL11.glEnable(GL11.GL_TEXTURE_2D);
				}

				GL11.glDisable(GL12.GL_RESCALE_NORMAL);
			} catch (Exception exception26) {
				exception26.printStackTrace();
			}

			GL11.glEnable(GL11.GL_CULL_FACE);
			GL11.glPopMatrix();
			this.passSpecialRender(entityLiving1, d2, d4, d6);
		}
	}
}
