package net.minecraft.client.renderer.entity;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelQuadruped;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.animal.EntityMooshroom;
import net.minecraft.world.level.tile.Block;

public class RenderMooshroom extends RenderLiving {
	public RenderMooshroom(ModelBase modelBase1, float f2) {
		super(modelBase1, f2);
	}

	public void func_40273_a(EntityMooshroom entityMooshroom1, double d2, double d4, double d6, float f8, float f9) {
		super.doRenderLiving(entityMooshroom1, d2, d4, d6, f8, f9);
	}

	protected void func_40272_a(EntityMooshroom entityMooshroom1, float f2) {
		super.renderEquippedItems(entityMooshroom1, f2);
		if(!entityMooshroom1.isChild()) {
			this.loadTexture("/terrain.png");
			GL11.glEnable(GL11.GL_CULL_FACE);
			GL11.glPushMatrix();
			GL11.glScalef(1.0F, -1.0F, 1.0F);
			GL11.glTranslatef(0.2F, 0.4F, 0.5F);
			GL11.glRotatef(42.0F, 0.0F, 1.0F, 0.0F);
			this.renderBlocks.renderBlockAsItem(Block.mushroomRed, 0, 1.0F);
			GL11.glTranslatef(0.1F, 0.0F, -0.6F);
			GL11.glRotatef(42.0F, 0.0F, 1.0F, 0.0F);
			this.renderBlocks.renderBlockAsItem(Block.mushroomRed, 0, 1.0F);
			GL11.glPopMatrix();
			GL11.glPushMatrix();
			((ModelQuadruped)this.mainModel).head.postRender(0.0625F);
			GL11.glScalef(1.0F, -1.0F, 1.0F);
			GL11.glTranslatef(0.0F, 0.75F, -0.2F);
			GL11.glRotatef(12.0F, 0.0F, 1.0F, 0.0F);
			this.renderBlocks.renderBlockAsItem(Block.mushroomRed, 0, 1.0F);
			GL11.glPopMatrix();
			GL11.glDisable(GL11.GL_CULL_FACE);
		}
	}

	protected void renderEquippedItems(EntityLiving entityLiving1, float f2) {
		this.func_40272_a((EntityMooshroom)entityLiving1, f2);
	}

	public void doRenderLiving(EntityLiving entityLiving1, double d2, double d4, double d6, float f8, float f9) {
		this.func_40273_a((EntityMooshroom)entityLiving1, d2, d4, d6, f8, f9);
	}

	public void doRender(Entity entity1, double d2, double d4, double d6, float f8, float f9) {
		this.func_40273_a((EntityMooshroom)entity1, d2, d4, d6, f8, f9);
	}
}
