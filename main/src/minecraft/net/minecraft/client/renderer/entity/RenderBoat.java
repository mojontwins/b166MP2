package net.minecraft.client.renderer.entity;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBoat;
import net.minecraft.src.MathHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.EntityBoat;

public class RenderBoat extends Render {
	protected ModelBase modelBoat;

	public RenderBoat() {
		this.shadowSize = 0.5F;
		this.modelBoat = new ModelBoat();
	}

	public void renderBoat(EntityBoat entityBoat1, double d2, double d4, double d6, float f8, float f9) {
		GL11.glPushMatrix();
		GL11.glTranslatef((float)d2, (float)d4, (float)d6);
		GL11.glRotatef(180.0F - f8, 0.0F, 1.0F, 0.0F);
		float f10 = (float)entityBoat1.getTimeSinceHit() - f9;
		float f11 = (float)entityBoat1.getDamageTaken() - f9;
		if(f11 < 0.0F) {
			f11 = 0.0F;
		}

		if(f10 > 0.0F) {
			GL11.glRotatef(MathHelper.sin(f10) * f10 * f11 / 10.0F * (float)entityBoat1.getForwardDirection(), 1.0F, 0.0F, 0.0F);
		}

		this.loadTexture("/terrain.png");
		float f12 = 0.75F;
		GL11.glScalef(f12, f12, f12);
		GL11.glScalef(1.0F / f12, 1.0F / f12, 1.0F / f12);
		this.loadTexture("/item/boat.png");
		GL11.glScalef(-1.0F, -1.0F, 1.0F);
		this.modelBoat.render(entityBoat1, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
		GL11.glPopMatrix();
	}

	public void doRender(Entity entity1, double d2, double d4, double d6, float f8, float f9) {
		this.renderBoat((EntityBoat)entity1, d2, d4, d6, f8, f9);
	}
}
