package net.minecraft.client.renderer.tile;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.tile.Block;

public class RenderBlockClassicPiston {
	public static boolean RenderWorldBlock(RenderBlocks renderBlocks1, IBlockAccess iBlockAccess2, int i3, int i4, int i5, Block block6, int i7) {
		int i8;
		if(block6 != Block.classicPistonBase && block6 != Block.classicStickyPistonBase) {
			i8 = iBlockAccess2.getBlockMetadata(i3, i4, i5);
			switch(i8) {
			case 0:
				block6.setBlockBounds(0.1F, 0.18F, 0.1F, 0.9F, 1.0F, 0.9F);
				renderBlocks1.renderStandardBlock(block6, i3, i4, i5);
				block6.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.18F, 1.0F);
				renderBlocks1.renderStandardBlock(block6, i3, i4, i5);
				break;
			case 1:
				block6.setBlockBounds(0.1F, 0.0F, 0.1F, 0.9F, 0.82F, 0.9F);
				renderBlocks1.renderStandardBlock(block6, i3, i4, i5);
				block6.setBlockBounds(0.0F, 0.82F, 0.0F, 1.0F, 1.0F, 1.0F);
				renderBlocks1.renderStandardBlock(block6, i3, i4, i5);
				break;
			case 2:
				block6.setBlockBounds(0.1F, 0.1F, 0.18F, 0.9F, 0.9F, 1.0F);
				renderBlocks1.renderStandardBlock(block6, i3, i4, i5);
				block6.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.18F);
				renderBlocks1.renderStandardBlock(block6, i3, i4, i5);
				break;
			case 3:
				block6.setBlockBounds(0.1F, 0.1F, 0.0F, 0.9F, 0.9F, 0.82F);
				renderBlocks1.renderStandardBlock(block6, i3, i4, i5);
				block6.setBlockBounds(0.0F, 0.0F, 0.82F, 1.0F, 1.0F, 1.0F);
				renderBlocks1.renderStandardBlock(block6, i3, i4, i5);
				break;
			case 4:
				block6.setBlockBounds(0.18F, 0.1F, 0.1F, 1.0F, 0.9F, 0.9F);
				renderBlocks1.renderStandardBlock(block6, i3, i4, i5);
				block6.setBlockBounds(0.0F, 0.0F, 0.0F, 0.18F, 1.0F, 1.0F);
				renderBlocks1.renderStandardBlock(block6, i3, i4, i5);
				break;
			case 5:
				block6.setBlockBounds(0.0F, 0.1F, 0.1F, 0.82F, 0.9F, 0.9F);
				renderBlocks1.renderStandardBlock(block6, i3, i4, i5);
				block6.setBlockBounds(0.82F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
				renderBlocks1.renderStandardBlock(block6, i3, i4, i5);
			}

			block6.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
			return true;
		} else {
			i8 = iBlockAccess2.getBlockMetadata(i3, i4, i5);
			boolean z9 = (i8 & 8) != 0;
			i8 &= 7;
			if(z9) {
				switch(i8) {
				case 0:
					block6.setBlockBounds(0.0F, 0.22F, 0.0F, 1.0F, 1.0F, 1.0F);
					renderBlocks1.renderStandardBlock(block6, i3, i4, i5);
					block6.setBlockBounds(0.1F, 0.0F, 0.1F, 0.9F, 0.22F, 0.9F);
					renderBlocks1.renderStandardBlock(block6, i3, i4, i5);
					break;
				case 1:
					block6.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.78F, 1.0F);
					renderBlocks1.renderStandardBlock(block6, i3, i4, i5);
					block6.setBlockBounds(0.1F, 0.78F, 0.1F, 0.9F, 1.0F, 0.9F);
					renderBlocks1.renderStandardBlock(block6, i3, i4, i5);
					break;
				case 2:
					block6.setBlockBounds(0.0F, 0.0F, 0.22F, 1.0F, 1.0F, 1.0F);
					renderBlocks1.renderStandardBlock(block6, i3, i4, i5);
					block6.setBlockBounds(0.1F, 0.1F, 0.0F, 0.9F, 0.9F, 0.22F);
					renderBlocks1.renderStandardBlock(block6, i3, i4, i5);
					break;
				case 3:
					block6.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.78F);
					renderBlocks1.renderStandardBlock(block6, i3, i4, i5);
					block6.setBlockBounds(0.1F, 0.1F, 0.78F, 0.9F, 0.9F, 1.0F);
					renderBlocks1.renderStandardBlock(block6, i3, i4, i5);
					break;
				case 4:
					block6.setBlockBounds(0.22F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
					renderBlocks1.renderStandardBlock(block6, i3, i4, i5);
					block6.setBlockBounds(0.0F, 0.1F, 0.1F, 0.22F, 0.9F, 0.9F);
					renderBlocks1.renderStandardBlock(block6, i3, i4, i5);
					break;
				case 5:
					block6.setBlockBounds(0.0F, 0.0F, 0.0F, 0.78F, 1.0F, 1.0F);
					renderBlocks1.renderStandardBlock(block6, i3, i4, i5);
					block6.setBlockBounds(0.78F, 0.1F, 0.1F, 1.0F, 0.9F, 0.9F);
					renderBlocks1.renderStandardBlock(block6, i3, i4, i5);
				}
			} else {
				switch(i8) {
				case 0:
					block6.setBlockBounds(0.0F, 0.22F, 0.0F, 1.0F, 1.0F, 1.0F);
					renderBlocks1.renderStandardBlock(block6, i3, i4, i5);
					block6.setBlockBounds(0.1F, 0.18F, 0.1F, 0.9F, 0.22F, 0.9F);
					renderBlocks1.renderStandardBlock(block6, i3, i4, i5);
					block6.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.18F, 1.0F);
					renderBlocks1.renderStandardBlock(block6, i3, i4, i5);
					break;
				case 1:
					block6.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.78F, 1.0F);
					renderBlocks1.renderStandardBlock(block6, i3, i4, i5);
					block6.setBlockBounds(0.1F, 0.78F, 0.1F, 0.9F, 0.82F, 0.9F);
					renderBlocks1.renderStandardBlock(block6, i3, i4, i5);
					block6.setBlockBounds(0.0F, 0.82F, 0.0F, 1.0F, 1.0F, 1.0F);
					renderBlocks1.renderStandardBlock(block6, i3, i4, i5);
					break;
				case 2:
					block6.setBlockBounds(0.0F, 0.0F, 0.22F, 1.0F, 1.0F, 1.0F);
					renderBlocks1.renderStandardBlock(block6, i3, i4, i5);
					block6.setBlockBounds(0.1F, 0.1F, 0.18F, 0.9F, 0.9F, 0.22F);
					renderBlocks1.renderStandardBlock(block6, i3, i4, i5);
					block6.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.18F);
					renderBlocks1.renderStandardBlock(block6, i3, i4, i5);
					break;
				case 3:
					block6.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.78F);
					renderBlocks1.renderStandardBlock(block6, i3, i4, i5);
					block6.setBlockBounds(0.1F, 0.1F, 0.78F, 0.9F, 0.9F, 0.82F);
					renderBlocks1.renderStandardBlock(block6, i3, i4, i5);
					block6.setBlockBounds(0.0F, 0.0F, 0.82F, 1.0F, 1.0F, 1.0F);
					renderBlocks1.renderStandardBlock(block6, i3, i4, i5);
					break;
				case 4:
					block6.setBlockBounds(0.22F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
					renderBlocks1.renderStandardBlock(block6, i3, i4, i5);
					block6.setBlockBounds(0.18F, 0.1F, 0.1F, 0.22F, 0.9F, 0.9F);
					renderBlocks1.renderStandardBlock(block6, i3, i4, i5);
					block6.setBlockBounds(0.0F, 0.0F, 0.0F, 0.18F, 1.0F, 1.0F);
					renderBlocks1.renderStandardBlock(block6, i3, i4, i5);
					break;
				case 5:
					block6.setBlockBounds(0.0F, 0.0F, 0.0F, 0.78F, 1.0F, 1.0F);
					renderBlocks1.renderStandardBlock(block6, i3, i4, i5);
					block6.setBlockBounds(0.78F, 0.1F, 0.1F, 0.82F, 0.9F, 0.9F);
					renderBlocks1.renderStandardBlock(block6, i3, i4, i5);
					block6.setBlockBounds(0.82F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
					renderBlocks1.renderStandardBlock(block6, i3, i4, i5);
				}
			}

			block6.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
			return true;
		}
	}

	public static void RenderInvBlock(RenderBlocks renderBlocks1, Block block2, int i3, int i4) {
		block2.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.78F, 1.0F);
		renderBlockBounds(renderBlocks1, Tessellator.instance, block2);
		block2.setBlockBounds(0.0F, 0.82F, 0.0F, 1.0F, 1.0F, 1.0F);
		renderBlockBounds(renderBlocks1, Tessellator.instance, block2);
		block2.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
	}

	private static void renderBlockBounds(RenderBlocks renderBlocks1, Tessellator tessellator2, Block block3) {
		byte b4 = 0;
		GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
		tessellator2.startDrawingQuads();
		tessellator2.setNormal(0.0F, -1.0F, 0.0F);
		renderBlocks1.renderBottomFace(block3, 0.0D, 0.0D, 0.0D, block3.getBlockTextureFromSideAndMetadata(0, b4));
		tessellator2.draw();
		tessellator2.startDrawingQuads();
		tessellator2.setNormal(0.0F, 1.0F, 0.0F);
		renderBlocks1.renderTopFace(block3, 0.0D, 0.0D, 0.0D, block3.getBlockTextureFromSideAndMetadata(1, b4));
		tessellator2.draw();
		tessellator2.startDrawingQuads();
		tessellator2.setNormal(0.0F, 0.0F, -1.0F);
		renderBlocks1.renderEastFace(block3, 0.0D, 0.0D, 0.0D, block3.getBlockTextureFromSideAndMetadata(2, b4));
		tessellator2.draw();
		tessellator2.startDrawingQuads();
		tessellator2.setNormal(0.0F, 0.0F, 1.0F);
		renderBlocks1.renderWestFace(block3, 0.0D, 0.0D, 0.0D, block3.getBlockTextureFromSideAndMetadata(3, b4));
		tessellator2.draw();
		tessellator2.startDrawingQuads();
		tessellator2.setNormal(-1.0F, 0.0F, 0.0F);
		renderBlocks1.renderNorthFace(block3, 0.0D, 0.0D, 0.0D, block3.getBlockTextureFromSideAndMetadata(4, b4));
		tessellator2.draw();
		tessellator2.startDrawingQuads();
		tessellator2.setNormal(1.0F, 0.0F, 0.0F);
		renderBlocks1.renderSouthFace(block3, 0.0D, 0.0D, 0.0D, block3.getBlockTextureFromSideAndMetadata(5, b4));
		tessellator2.draw();
		GL11.glTranslatef(0.5F, 0.5F, 0.5F);
	}
}
