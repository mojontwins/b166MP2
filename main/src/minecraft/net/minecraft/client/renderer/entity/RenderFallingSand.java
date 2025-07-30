package net.minecraft.client.renderer.entity;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.src.MathHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.EntityFallingSand;
import net.minecraft.world.level.World;
import net.minecraft.world.level.tile.Block;

public class RenderFallingSand extends Render {
	private RenderBlocks renderBlocks = new RenderBlocks();

	public RenderFallingSand() {
		this.shadowSize = 0.5F;
	}

	public void doRenderFallingSand(EntityFallingSand entityFallingSand1, double d2, double d4, double d6, float f8, float f9) {
		GL11.glPushMatrix();
		GL11.glTranslatef((float)d2, (float)d4, (float)d6);
		this.loadTexture("/terrain.png");
		Block block10 = Block.blocksList[entityFallingSand1.blockID];
		World world11 = entityFallingSand1.getWorld();
		GL11.glDisable(GL11.GL_LIGHTING);
		this.renderBlocks.renderBlockFallingSand(block10, world11, MathHelper.floor_double(entityFallingSand1.posX), MathHelper.floor_double(entityFallingSand1.posY), MathHelper.floor_double(entityFallingSand1.posZ));
		
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glPopMatrix();
	}

	public void doRender(Entity entity1, double d2, double d4, double d6, float f8, float f9) {
		this.doRenderFallingSand((EntityFallingSand)entity1, d2, d4, d6, f8, f9);
	}
}
