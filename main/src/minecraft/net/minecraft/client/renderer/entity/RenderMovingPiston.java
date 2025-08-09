package net.minecraft.client.renderer.entity;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.EntityMovingPiston;
import net.minecraft.world.level.World;
import net.minecraft.world.level.tile.Block;

public class RenderMovingPiston extends Render {
	private RenderBlocks a = new RenderBlocks();

	public RenderMovingPiston() {
		this.shadowSize = 0.5F;
	}

	public void doRender(Entity entity1, double d2, double d4, double d6, float f8, float f9) {
		EntityMovingPiston movingPiston10 = (EntityMovingPiston)entity1;
		GL11.glPushMatrix();
		GL11.glTranslatef((float)d2, (float)d4, (float)d6);
		this.loadTexture("/terrain.png");
		World world11 = movingPiston10.k();
		int i12 = movingPiston10.getXRender();
		int i13 = movingPiston10.getYRender();
		int i14 = movingPiston10.getZRender();
		GL11.glDisable(GL11.GL_LIGHTING);
		if(movingPiston10.blockID != Block.classicPiston.blockID && movingPiston10.blockID != Block.classicStickyPiston.blockID) {
			this.a.renderBlockFallingSand(Block.blocksList[movingPiston10.blockID], world11, i12, i13, i14);
		} else {
			this.renderPiston(Block.blocksList[movingPiston10.blockID], world11, i12, i13, i14, movingPiston10.data);
		}

		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glPopMatrix();
	}

	private void renderPiston(Block block1, World world2, int i3, int i4, int i5, int i6) {
		switch(i6) {
		case 0:
			block1.setBlockBounds(0.1F, 0.18F, 0.1F, 0.9F, 1.0F, 0.9F);
			this.a.renderBlockFallingSand(block1, world2, i3, i4, i5);
			block1.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.18F, 1.0F);
			this.a.renderBlockFallingSand(block1, world2, i3, i4, i5);
			break;
		case 1:
			block1.setBlockBounds(0.1F, 0.0F, 0.1F, 0.9F, 0.82F, 0.9F);
			this.a.renderBlockFallingSand(block1, world2, i3, i4, i5);
			block1.setBlockBounds(0.0F, 0.82F, 0.0F, 1.0F, 1.0F, 1.0F);
			this.a.renderBlockFallingSand(block1, world2, i3, i4, i5);
			break;
		case 2:
			block1.setBlockBounds(0.1F, 0.1F, 0.18F, 0.9F, 0.9F, 1.0F);
			this.a.renderBlockFallingSand(block1, world2, i3, i4, i5);
			block1.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.18F);
			this.a.renderBlockFallingSand(block1, world2, i3, i4, i5);
			break;
		case 3:
			block1.setBlockBounds(0.1F, 0.1F, 0.0F, 0.9F, 0.9F, 0.82F);
			this.a.renderBlockFallingSand(block1, world2, i3, i4, i5);
			block1.setBlockBounds(0.0F, 0.0F, 0.82F, 1.0F, 1.0F, 1.0F);
			this.a.renderBlockFallingSand(block1, world2, i3, i4, i5);
			break;
		case 4:
			block1.setBlockBounds(0.18F, 0.1F, 0.1F, 1.0F, 0.9F, 0.9F);
			this.a.renderBlockFallingSand(block1, world2, i3, i4, i5);
			block1.setBlockBounds(0.0F, 0.0F, 0.0F, 0.18F, 1.0F, 1.0F);
			this.a.renderBlockFallingSand(block1, world2, i3, i4, i5);
			break;
		case 5:
			block1.setBlockBounds(0.0F, 0.1F, 0.1F, 0.82F, 0.9F, 0.9F);
			this.a.renderBlockFallingSand(block1, world2, i3, i4, i5);
			block1.setBlockBounds(0.82F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
			this.a.renderBlockFallingSand(block1, world2, i3, i4, i5);
		}

		block1.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
	}
}
