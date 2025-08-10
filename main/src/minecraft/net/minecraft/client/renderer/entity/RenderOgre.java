package net.minecraft.client.renderer.entity;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelOgre2;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.monster.EntityOgre;

public class RenderOgre extends RenderLiving {
	private ModelOgre2 tempOgre;

	public RenderOgre(ModelOgre2 model, ModelBase modelBase, float f3) {
		super(model, f3);
		this.setRenderPassModel(modelBase);
		this.tempOgre = model;
	}

	protected int renderInnerOgre(EntityOgre ogre, int pass) {
		if(pass == 0) {
			this.loadTexture("/mob/ogreb.png");
			return 1;
		}

		return -1;
	}

	protected int shouldRenderPass(EntityLiving living, int i2, float f3) {
		return this.renderInnerOgre((EntityOgre)living, i2);
	}

	public void doRenderLiving(EntityLiving living, double d2, double d4, double d6, float f8, float f9) {
		EntityOgre ogre = (EntityOgre)living;
		if(living.attackTime <= 0 && ogre.ogreattack) {
			ogre.ogreattack = false;
			ogre.DestroyingOgre();
		}

		GL11.glPushMatrix();
		GL11.glDisable(GL11.GL_CULL_FACE);
		this.mainModel.onGround = this.renderSwingProgress(living, f9);
		this.mainModel.isRiding = living.isRiding();
		if(this.renderPassModel != null) {
			this.renderPassModel.isRiding = this.mainModel.isRiding;
		}

		try {
			float f11 = living.prevRenderYawOffset + (living.renderYawOffset - living.prevRenderYawOffset) * f9;
			float f12 = living.prevRotationYaw + (living.rotationYaw - living.prevRotationYaw) * f9;
			float f13 = living.prevRotationPitch + (living.rotationPitch - living.prevRotationPitch) * f9;
			this.renderLivingAt(living, d2, d4, d6);
			float f14 = this.handleRotationFloat(living, f9);
			this.rotateCorpse(living, f14, f11, f9);
			float f15 = 0.0625F;
			GL11.glEnable(GL12.GL_RESCALE_NORMAL);
			GL11.glScalef(-1.0F, -1.0F, 1.0F);
			this.preRenderCallback(living, f9);
			GL11.glTranslatef(0.0F, -24.0F * f15 - 0.007813F, 0.0F);
			float f16 = living.prevLegYaw + (living.legYaw - living.prevLegYaw) * f9;
			float f17 = living.field_703_S - living.legYaw * (1.0F - f9);
			if(f16 > 1.0F) {
				f16 = 1.0F;
			}

			this.loadDownloadableImageTexture(living.skinUrl, living.getTexture());
			GL11.glEnable(GL11.GL_ALPHA_TEST);
			this.tempOgre.render(f17, f16, f14, f12 - f11, f13, f15, ogre.ogreattack);

			for(int i18 = 0; i18 < 4; ++i18) {
				if(this.shouldRenderPass(living, i18, f9) > 0) {
					this.renderPassModel.render(living, f17, f16, f14, f12 - f11, f13, f15);
					GL11.glDisable(GL11.GL_BLEND);
					GL11.glEnable(GL11.GL_ALPHA_TEST);
				}
			}

			this.renderEquippedItems(living, f9);
			float f26 = living.getBrightness(f9);
			int i19 = this.getColorMultiplier(living, f26, f9);
			if((i19 >> 24 & 255) > 0 || living.hurtTime > 0 || living.deathTime > 0) {
				GL11.glDisable(GL11.GL_TEXTURE_2D);
				GL11.glDisable(GL11.GL_ALPHA_TEST);
				GL11.glEnable(GL11.GL_BLEND);
				GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
				GL11.glDepthFunc(GL11.GL_EQUAL);
				if(living.hurtTime > 0 || living.deathTime > 0) {
					GL11.glColor4f(f26, 0.0F, 0.0F, 0.4F);
					this.tempOgre.render(f17, f16, f14, f12 - f11, f13, f15, ogre.ogreattack);

					for(int i20 = 0; i20 < 4; ++i20) {
						if(this.shouldRenderPass(living, i20, f9) > 0) {
							GL11.glColor4f(f26, 0.0F, 0.0F, 0.4F);
							this.renderPassModel.render(living, f17, f16, f14, f12 - f11, f13, f15);
						}
					}
				}

				if((i19 >> 24 & 255) > 0) {
					float f27 = (float)(i19 >> 16 & 255) / 255.0F;
					float f21 = (float)(i19 >> 8 & 255) / 255.0F;
					float f22 = (float)(i19 & 255) / 255.0F;
					float f23 = (float)(i19 >> 24 & 255) / 255.0F;
					GL11.glColor4f(f27, f21, f22, f23);
					this.tempOgre.render(f17, f16, f14, f12 - f11, f13, f15, ogre.ogreattack);

					for(int i24 = 0; i24 < 4; ++i24) {
						if(this.shouldRenderPass(living, i24, f9) > 0) {
							GL11.glColor4f(f27, f21, f22, f23);
							this.renderPassModel.render(living, f17, f16, f14, f12 - f11, f13, f15);
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
		this.passSpecialRender(living, d2, d4, d6);
	}
}
