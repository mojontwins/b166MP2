package net.minecraft.client.renderer.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.FontRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.src.MathHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class RenderLiving extends Render {
	protected ModelBase mainModel;
	protected ModelBase renderPassModel;

	public RenderLiving(ModelBase modelBase1, float f2) {
		this.mainModel = modelBase1;
		this.shadowSize = f2;
	}

	public void setRenderPassModel(ModelBase modelBase1) {
		this.renderPassModel = modelBase1;
	}

	private float func_48418_a(float f1, float f2, float f3) {
		float f4;
		for(f4 = f2 - f1; f4 < -180.0F; f4 += 360.0F) {
		}

		while(f4 >= 180.0F) {
			f4 -= 360.0F;
		}

		return f1 + f3 * f4;
	}

	public void doRenderLiving(EntityLiving entityLiving1, double d2, double d4, double d6, float f8, float f9) {
		GL11.glPushMatrix();
		GL11.glDisable(GL11.GL_CULL_FACE);
		this.mainModel.onGround = this.renderSwingProgress(entityLiving1, f9);
		if(this.renderPassModel != null) {
			this.renderPassModel.onGround = this.mainModel.onGround;
		}

		this.mainModel.isRiding = entityLiving1.isRiding();
		if(this.renderPassModel != null) {
			this.renderPassModel.isRiding = this.mainModel.isRiding;
		}

		this.mainModel.isChild = entityLiving1.isChild();
		if(this.renderPassModel != null) {
			this.renderPassModel.isChild = this.mainModel.isChild;
		}

		try {
			float f10 = this.func_48418_a(entityLiving1.prevRenderYawOffset, entityLiving1.renderYawOffset, f9);
			float f11 = this.func_48418_a(entityLiving1.prevRotationYawHead, entityLiving1.rotationYawHead, f9);
			float f12 = entityLiving1.prevRotationPitch + (entityLiving1.rotationPitch - entityLiving1.prevRotationPitch) * f9;
			this.renderLivingAt(entityLiving1, d2, d4, d6);
			float f13 = this.handleRotationFloat(entityLiving1, f9);
			this.rotateCorpse(entityLiving1, f13, f10, f9);
			float f14 = 0.0625F;
			GL11.glEnable(GL12.GL_RESCALE_NORMAL);
			GL11.glScalef(-1.0F, -1.0F, 1.0F);
			this.preRenderCallback(entityLiving1, f9);
			GL11.glTranslatef(0.0F, -24.0F * f14 - 0.0078125F, 0.0F);
			float f15 = entityLiving1.prevLegYaw + (entityLiving1.legYaw - entityLiving1.prevLegYaw) * f9;
			float f16 = entityLiving1.field_703_S - entityLiving1.legYaw * (1.0F - f9);
			if(entityLiving1.isChild()) {
				f16 *= 3.0F;
			}

			if(f15 > 1.0F) {
				f15 = 1.0F;
			}

			GL11.glEnable(GL11.GL_ALPHA_TEST);
			this.mainModel.setLivingAnimations(entityLiving1, f16, f15, f9);
			this.renderModel(entityLiving1, f16, f15, f13, f11 - f10, f12, f14);

			int i18;
			float f19;
			float f20;
			float f22;
			for(int i17 = 0; i17 < 4; ++i17) {
				i18 = this.shouldRenderPass(entityLiving1, i17, f9);
				if(i18 > 0) {
					this.renderPassModel.setLivingAnimations(entityLiving1, f16, f15, f9);
					this.renderPassModel.render(entityLiving1, f16, f15, f13, f11 - f10, f12, f14);
					if(i18 == 15) {
						f19 = (float)entityLiving1.ticksExisted + f9;
						this.loadTexture("%blur%/misc/glint.png");
						GL11.glEnable(GL11.GL_BLEND);
						f20 = 0.5F;
						GL11.glColor4f(f20, f20, f20, 1.0F);
						GL11.glDepthFunc(GL11.GL_EQUAL);
						GL11.glDepthMask(false);

						for(int i21 = 0; i21 < 2; ++i21) {
							GL11.glDisable(GL11.GL_LIGHTING);
							f22 = 0.76F;
							GL11.glColor4f(0.5F * f22, 0.25F * f22, 0.8F * f22, 1.0F);
							GL11.glBlendFunc(GL11.GL_SRC_COLOR, GL11.GL_ONE);
							GL11.glMatrixMode(GL11.GL_TEXTURE);
							GL11.glLoadIdentity();
							float f23 = f19 * (0.001F + (float)i21 * 0.003F) * 20.0F;
							float f24 = 0.33333334F;
							GL11.glScalef(f24, f24, f24);
							GL11.glRotatef(30.0F - (float)i21 * 60.0F, 0.0F, 0.0F, 1.0F);
							GL11.glTranslatef(0.0F, f23, 0.0F);
							GL11.glMatrixMode(GL11.GL_MODELVIEW);
							this.renderPassModel.render(entityLiving1, f16, f15, f13, f11 - f10, f12, f14);
						}

						GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
						GL11.glMatrixMode(GL11.GL_TEXTURE);
						GL11.glDepthMask(true);
						GL11.glLoadIdentity();
						GL11.glMatrixMode(GL11.GL_MODELVIEW);
						GL11.glEnable(GL11.GL_LIGHTING);
						GL11.glDisable(GL11.GL_BLEND);
						GL11.glDepthFunc(GL11.GL_LEQUAL);
					}

					GL11.glDisable(GL11.GL_BLEND);
					GL11.glEnable(GL11.GL_ALPHA_TEST);
				}
			}

			this.renderEquippedItems(entityLiving1, f9);
			float f26 = entityLiving1.getBrightness(f9);
			i18 = this.getColorMultiplier(entityLiving1, f26, f9);
			OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
			if((i18 >> 24 & 255) > 0 || entityLiving1.hurtTime > 0 || entityLiving1.deathTime > 0) {
				GL11.glDisable(GL11.GL_TEXTURE_2D);
				GL11.glDisable(GL11.GL_ALPHA_TEST);
				GL11.glEnable(GL11.GL_BLEND);
				GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
				GL11.glDepthFunc(GL11.GL_EQUAL);
				if(entityLiving1.hurtTime > 0 || entityLiving1.deathTime > 0) {
					GL11.glColor4f(f26, 0.0F, 0.0F, 0.4F);
					this.mainModel.render(entityLiving1, f16, f15, f13, f11 - f10, f12, f14);

					for(int i27 = 0; i27 < 4; ++i27) {
						if(this.inheritRenderPass(entityLiving1, i27, f9) >= 0) {
							GL11.glColor4f(f26, 0.0F, 0.0F, 0.4F);
							this.renderPassModel.render(entityLiving1, f16, f15, f13, f11 - f10, f12, f14);
						}
					}
				}

				if((i18 >> 24 & 255) > 0) {
					f19 = (float)(i18 >> 16 & 255) / 255.0F;
					f20 = (float)(i18 >> 8 & 255) / 255.0F;
					float f28 = (float)(i18 & 255) / 255.0F;
					f22 = (float)(i18 >> 24 & 255) / 255.0F;
					GL11.glColor4f(f19, f20, f28, f22);
					this.mainModel.render(entityLiving1, f16, f15, f13, f11 - f10, f12, f14);

					for(int i29 = 0; i29 < 4; ++i29) {
						if(this.inheritRenderPass(entityLiving1, i29, f9) >= 0) {
							GL11.glColor4f(f19, f20, f28, f22);
							this.renderPassModel.render(entityLiving1, f16, f15, f13, f11 - f10, f12, f14);
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

		OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glPopMatrix();
		this.passSpecialRender(entityLiving1, d2, d4, d6);
	}

	protected void renderModel(EntityLiving entityLiving1, float f2, float f3, float f4, float f5, float f6, float f7) {
		this.loadDownloadableImageTexture(entityLiving1.skinUrl, entityLiving1.getTexture());
		this.mainModel.render(entityLiving1, f2, f3, f4, f5, f6, f7);
	}

	protected void renderLivingAt(EntityLiving entityLiving1, double d2, double d4, double d6) {
		GL11.glTranslatef((float)d2, (float)d4, (float)d6);
	}

	protected void rotateCorpse(EntityLiving entityLiving1, float f2, float f3, float f4) {
		GL11.glRotatef(180.0F - f3, 0.0F, 1.0F, 0.0F);
		if(entityLiving1.deathTime > 0) {
			float f5 = ((float)entityLiving1.deathTime + f4 - 1.0F) / 20.0F * 1.6F;
			f5 = MathHelper.sqrt_float(f5);
			if(f5 > 1.0F) {
				f5 = 1.0F;
			}

			GL11.glRotatef(f5 * this.getDeathMaxRotation(entityLiving1), 0.0F, 0.0F, 1.0F);
		}

	}

	protected float renderSwingProgress(EntityLiving entityLiving1, float f2) {
		return entityLiving1.getSwingProgress(f2);
	}

	protected float handleRotationFloat(EntityLiving entityLiving1, float f2) {
		return (float)entityLiving1.ticksExisted + f2;
	}

	protected void renderEquippedItems(EntityLiving entityLiving1, float f2) {
	}

	protected int inheritRenderPass(EntityLiving entityLiving1, int i2, float f3) {
		return this.shouldRenderPass(entityLiving1, i2, f3);
	}

	protected int shouldRenderPass(EntityLiving entityLiving1, int i2, float f3) {
		return -1;
	}

	protected float getDeathMaxRotation(EntityLiving entityLiving1) {
		return 90.0F;
	}

	protected int getColorMultiplier(EntityLiving entityLiving1, float f2, float f3) {
		return 0;
	}

	protected void preRenderCallback(EntityLiving entityLiving1, float f2) {
	}

	protected void passSpecialRender(EntityLiving entityLiving1, double d2, double d4, double d6) {
		if(Minecraft.isDebugInfoEnabled()) {
			this.renderLivingLabel(entityLiving1, Integer.toString(entityLiving1.entityId)  + " " + Integer.toString(entityLiving1.health), d2, d4, d6, 64);
		}

	}

	protected void renderLivingLabel(EntityLiving entityLiving1, String string2, double d3, double d5, double d7, int i9) {
		float f10 = entityLiving1.getDistanceToEntity(this.renderManager.livingPlayer);
		if(f10 <= (float)i9) {
			FontRenderer fontRenderer11 = this.getFontRendererFromRenderManager();
			float f12 = 1.6F;
			float f13 = 0.016666668F * f12;
			GL11.glPushMatrix();
			GL11.glTranslatef((float)d3 + 0.0F, (float)d5 + 2.3F, (float)d7);
			GL11.glNormal3f(0.0F, 1.0F, 0.0F);
			GL11.glRotatef(-this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
			GL11.glRotatef(this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
			GL11.glScalef(-f13, -f13, f13);
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glDepthMask(false);
			GL11.glDisable(GL11.GL_DEPTH_TEST);
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			Tessellator tessellator14 = Tessellator.instance;
			byte b15 = 0;
			if(string2.equals("deadmau5")) {
				b15 = -10;
			}

			GL11.glDisable(GL11.GL_TEXTURE_2D);
			tessellator14.startDrawingQuads();
			int i16 = fontRenderer11.getStringWidth(string2) / 2;
			tessellator14.setColorRGBA_F(0.0F, 0.0F, 0.0F, 0.25F);
			tessellator14.addVertex((double)(-i16 - 1), (double)(-1 + b15), 0.0D);
			tessellator14.addVertex((double)(-i16 - 1), (double)(8 + b15), 0.0D);
			tessellator14.addVertex((double)(i16 + 1), (double)(8 + b15), 0.0D);
			tessellator14.addVertex((double)(i16 + 1), (double)(-1 + b15), 0.0D);
			tessellator14.draw();
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			fontRenderer11.drawString(string2, -fontRenderer11.getStringWidth(string2) / 2, b15, 553648127);
			GL11.glEnable(GL11.GL_DEPTH_TEST);
			GL11.glDepthMask(true);
			fontRenderer11.drawString(string2, -fontRenderer11.getStringWidth(string2) / 2, b15, -1);
			GL11.glEnable(GL11.GL_LIGHTING);
			GL11.glDisable(GL11.GL_BLEND);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			GL11.glPopMatrix();
		}
	}

	public void doRender(Entity entity1, double d2, double d4, double d6, float f8, float f9) {
		this.doRenderLiving((EntityLiving)entity1, d2, d4, d6, f8, f9);
	}
}
