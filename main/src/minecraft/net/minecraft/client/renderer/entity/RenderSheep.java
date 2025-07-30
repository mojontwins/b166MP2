package net.minecraft.client.renderer.entity;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.model.ModelBase;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.animal.EntitySheep;

public class RenderSheep extends RenderLiving {
	public RenderSheep(ModelBase modelBase1, ModelBase modelBase2, float f3) {
		super(modelBase1, f3);
		this.setRenderPassModel(modelBase2);
	}

	protected int setWoolColorAndRender(EntitySheep entitySheep1, int i2, float f3) {
		if(i2 == 0 && !entitySheep1.getSheared()) {
			this.loadTexture("/mob/sheep_fur.png");
			float f4 = 1.0F;
			int i5 = entitySheep1.getFleeceColor();
			GL11.glColor3f(f4 * EntitySheep.fleeceColorTable[i5][0], f4 * EntitySheep.fleeceColorTable[i5][1], f4 * EntitySheep.fleeceColorTable[i5][2]);
			return 1;
		} else {
			return -1;
		}
	}

	public void doRenderSheep(EntitySheep entitySheep1, double d2, double d4, double d6, float f8, float f9) {
		super.doRenderLiving(entitySheep1, d2, d4, d6, f8, f9);
	}

	protected int shouldRenderPass(EntityLiving entityLiving1, int i2, float f3) {
		return this.setWoolColorAndRender((EntitySheep)entityLiving1, i2, f3);
	}

	public void doRenderLiving(EntityLiving entityLiving1, double d2, double d4, double d6, float f8, float f9) {
		this.doRenderSheep((EntitySheep)entityLiving1, d2, d4, d6, f8, f9);
	}

	public void doRender(Entity entity1, double d2, double d4, double d6, float f8, float f9) {
		this.doRenderSheep((EntitySheep)entity1, d2, d4, d6, f8, f9);
	}
}
