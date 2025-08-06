package net.minecraft.client.renderer.entity;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelCreeper;
import net.minecraft.src.MathHelper;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.monster.EntityCreeper;

public class RenderCreeper extends RenderLiving {
	private ModelBase modelBase = new ModelCreeper(2.0F);

	public RenderCreeper() {
		super(new ModelCreeper(), 0.5F);
	}

	public RenderCreeper(ModelBase model) {
		super(model, 0.5F);
	}

	protected void updateCreeperScale(EntityCreeper entityCreeper1, float f2) {
		float f4 = entityCreeper1.setCreeperFlashTime(f2);
		float f5 = 1.0F + MathHelper.sin(f4 * 100.0F) * f4 * 0.01F;
		if(f4 < 0.0F) {
			f4 = 0.0F;
		}

		if(f4 > 1.0F) {
			f4 = 1.0F;
		}

		f4 *= f4;
		f4 *= f4;
		float f6 = (1.0F + f4 * 0.4F) * f5;
		float f7 = (1.0F + f4 * 0.1F) / f5;
		GL11.glScalef(f6, f7, f6);
	}

	protected int updateCreeperColorMultiplier(EntityCreeper entityCreeper1, float f2, float f3) {
		float f5 = entityCreeper1.setCreeperFlashTime(f3);
		if((int)(f5 * 10.0F) % 2 == 0) {
			return 0;
		} else {
			int i6 = (int)(f5 * 0.2F * 255.0F);
			if(i6 < 0) {
				i6 = 0;
			}

			if(i6 > 255) {
				i6 = 255;
			}

			short s7 = 255;
			short s8 = 255;
			short s9 = 255;
			return i6 << 24 | s7 << 16 | s8 << 8 | s9;
		}
	}

	protected int renderPowered(EntityCreeper entityCreeper1, int i2, float f3) {
		if(entityCreeper1.getPowered()) {
			if(i2 == 1) {
				float f4 = (float)entityCreeper1.ticksExisted + f3;
				this.loadTexture("/armor/power.png");
				GL11.glMatrixMode(GL11.GL_TEXTURE);
				GL11.glLoadIdentity();
				float f5 = f4 * 0.01F;
				float f6 = f4 * 0.01F;
				GL11.glTranslatef(f5, f6, 0.0F);
				this.setRenderPassModel(this.modelBase);
				GL11.glMatrixMode(GL11.GL_MODELVIEW);
				GL11.glEnable(GL11.GL_BLEND);
				float f7 = 0.5F;
				GL11.glColor4f(f7, f7, f7, 1.0F);
				GL11.glDisable(GL11.GL_LIGHTING);
				GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE);
				return 1;
			}

			if(i2 == 2) {
				GL11.glMatrixMode(GL11.GL_TEXTURE);
				GL11.glLoadIdentity();
				GL11.glMatrixMode(GL11.GL_MODELVIEW);
				GL11.glEnable(GL11.GL_LIGHTING);
				GL11.glDisable(GL11.GL_BLEND);
			}
		}

		return -1;
	}

	protected int inheritedRenderPass(EntityCreeper entityCreeper1, int i2, float f3) {
		return -1;
	}

	protected void preRenderCallback(EntityLiving entityLiving1, float f2) {
		this.updateCreeperScale((EntityCreeper)entityLiving1, f2);
	}

	protected int getColorMultiplier(EntityLiving entityLiving1, float f2, float f3) {
		return this.updateCreeperColorMultiplier((EntityCreeper)entityLiving1, f2, f3);
	}

	protected int shouldRenderPass(EntityLiving entityLiving1, int i2, float f3) {
		return this.renderPowered((EntityCreeper)entityLiving1, i2, f3);
	}

	protected int inheritRenderPass(EntityLiving entityLiving1, int i2, float f3) {
		return this.inheritedRenderPass((EntityCreeper)entityLiving1, i2, f3);
	}
}
