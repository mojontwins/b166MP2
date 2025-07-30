package net.minecraft.client.renderer;

import java.util.Random;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.mojontwins.utils.Idx2uvF;
import com.mojontwins.utils.Texels;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.src.MathHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.EntityItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.tile.Block;

public class RenderItem extends Render {
	private RenderBlocks renderBlocks = new RenderBlocks();
	private Random random = new Random();
	public boolean field_27004_a = true;
	public float zLevel = 0.0F;

	public RenderItem() {
		this.shadowSize = 0.15F;
		this.shadowOpaque = 0.75F;
	}

	public void doRenderItem(EntityItem entityItem1, double d2, double d4, double d6, float f8, float f9) {
		this.random.setSeed(187L);
		ItemStack itemStack10 = entityItem1.item;
		GL11.glPushMatrix();
		float f11 = MathHelper.sin(((float)entityItem1.age + f9) / 10.0F + entityItem1.field_804_d) * 0.1F + 0.1F;
		float f12 = (((float)entityItem1.age + f9) / 20.0F + entityItem1.field_804_d) * 57.295776F;
		byte b13 = 1;
		if(entityItem1.item.stackSize > 1) {
			b13 = 2;
		}

		if(entityItem1.item.stackSize > 5) {
			b13 = 3;
		}

		if(entityItem1.item.stackSize > 20) {
			b13 = 4;
		}

		GL11.glTranslatef((float)d2, (float)d4 + f11, (float)d6);
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		int i15;
		float f18;
		float f19;
		float f23;
		if(Block.blocksList[itemStack10.itemID] != null && RenderBlocks.renderItemIn3d(Block.blocksList[itemStack10.itemID].getRenderType())) {
			GL11.glRotatef(f12, 0.0F, 1.0F, 0.0F);
			this.loadTexture(Block.blocksList[itemStack10.itemID].getTextureFile());
			float f21 = 0.25F;
			i15 = Block.blocksList[itemStack10.itemID].getRenderType();
			if(i15 == 1 || i15 == 19 || i15 == 12 || i15 == 2) {
				f21 = 0.5F;
			}

			GL11.glScalef(f21, f21, f21);

			for(int i22 = 0; i22 < b13; ++i22) {
				GL11.glPushMatrix();
				if(i22 > 0) {
					f23 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.2F / f21;
					f18 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.2F / f21;
					f19 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.2F / f21;
					GL11.glTranslatef(f23, f18, f19);
				}

				f23 = 1.0F;
				this.renderBlocks.renderBlockAsItem(Block.blocksList[itemStack10.itemID], itemStack10.getItemDamage(), f23);
				GL11.glPopMatrix();
			}
		} else {
			int i14;
			float f16;
			if(itemStack10.getItem().requiresMultipleRenderPasses()) {
				GL11.glScalef(0.5F, 0.5F, 0.5F);
				this.loadTexture(Item.itemsList[itemStack10.itemID].getTextureFile());

				for(i14 = 0; i14 <= 1; ++i14) {
					i15 = itemStack10.getItem().getIconFromDamageAndRenderpass(itemStack10.getItemDamage(), i14);
					f16 = 1.0F;
					if(this.field_27004_a) {
						int i17 = Item.itemsList[itemStack10.itemID].getColorFromDamage(itemStack10.getItemDamage(), i14);
						f18 = (float)(i17 >> 16 & 255) / 255.0F;
						f19 = (float)(i17 >> 8 & 255) / 255.0F;
						float f20 = (float)(i17 & 255) / 255.0F;
						GL11.glColor4f(f18 * f16, f19 * f16, f20 * f16, 1.0F);
					}

					this.draw2dItem(i15, b13);
				}
			} else {
				GL11.glScalef(0.5F, 0.5F, 0.5F);
				i14 = itemStack10.getIconIndex();
				this.loadTexture(itemStack10.getItem().getTextureFile());

				if(this.field_27004_a) {
					i15 = Item.itemsList[itemStack10.itemID].getColorFromDamage(itemStack10.getItemDamage(), 0);
					f16 = (float)(i15 >> 16 & 255) / 255.0F;
					f23 = (float)(i15 >> 8 & 255) / 255.0F;
					f18 = (float)(i15 & 255) / 255.0F;
					f19 = 1.0F;
					GL11.glColor4f(f16 * f19, f23 * f19, f18 * f19, 1.0F);
				}

				this.draw2dItem(i14, b13);
			}
		}

		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		GL11.glPopMatrix();
	}

	private void draw2dItem(int i1, int i2) {
		Tessellator tessellator3 = Tessellator.instance;
		
		/*
		float f4 = (float)(i1 % 16 * 16 + 0) / 256.0F;
		float f5 = (float)(i1 % 16 * 16 + 16) / 256.0F;
		float f6 = (float)(i1 / 16 * 16 + 0) / 256.0F;
		float f7 = (float)(i1 / 16 * 16 + 16) / 256.0F;
		*/
		Idx2uvF.calc(i1);
		double f4 = Idx2uvF.u1;
		double f5 = Idx2uvF.u2;
		double f6 = Idx2uvF.v1;
		double f7 = Idx2uvF.v2;
		
		float f8 = 1.0F;
		float f9 = 0.5F;
		float f10 = 0.25F;

		for(int i11 = 0; i11 < i2; ++i11) {
			GL11.glPushMatrix();
			if(i11 > 0) {
				float f12 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.3F;
				float f13 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.3F;
				float f14 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.3F;
				GL11.glTranslatef(f12, f13, f14);
			}

			GL11.glRotatef(180.0F - this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
			tessellator3.startDrawingQuads();
			tessellator3.setNormal(0.0F, 1.0F, 0.0F);
			tessellator3.addVertexWithUV((double)(0.0F - f9), (double)(0.0F - f10), 0.0D, (double)f4, (double)f7);
			tessellator3.addVertexWithUV((double)(f8 - f9), (double)(0.0F - f10), 0.0D, (double)f5, (double)f7);
			tessellator3.addVertexWithUV((double)(f8 - f9), (double)(1.0F - f10), 0.0D, (double)f5, (double)f6);
			tessellator3.addVertexWithUV((double)(0.0F - f9), (double)(1.0F - f10), 0.0D, (double)f4, (double)f6);
			tessellator3.draw();
			GL11.glPopMatrix();
		}

	}

	public void drawItemIntoGui(FontRenderer fontRenderer1, RenderEngine renderEngine2, int i3, int i4, int i5, int i6, int i7) {
		int i10;
		float f11;
		float f12;
		float f13;
		if(Block.blocksList[i3] != null && RenderBlocks.renderItemIn3d(Block.blocksList[i3].getRenderType())) {
			renderEngine2.bindTexture(renderEngine2.getTexture(Block.blocksList[i3].getTextureFile()));
			Block block15 = Block.blocksList[i3];
			GL11.glPushMatrix();
			GL11.glTranslatef((float)(i6 - 2), (float)(i7 + 3), -3.0F + this.zLevel);
			GL11.glScalef(10.0F, 10.0F, 10.0F);
			GL11.glTranslatef(1.0F, 0.5F, 1.0F);
			GL11.glScalef(1.0F, 1.0F, -1.0F);
			GL11.glRotatef(210.0F, 1.0F, 0.0F, 0.0F);
			GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
			i10 = Item.itemsList[i3].getColorFromDamage(i4, 0);
			f11 = (float)(i10 >> 16 & 255) / 255.0F;
			f12 = (float)(i10 >> 8 & 255) / 255.0F;
			f13 = (float)(i10 & 255) / 255.0F;
			if(this.field_27004_a) {
				GL11.glColor4f(f11, f12, f13, 1.0F);
			}

			GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);
			this.renderBlocks.useInventoryTint = this.field_27004_a;
			this.renderBlocks.renderBlockAsItem(block15, i4, 1.0F);
			this.renderBlocks.useInventoryTint = true;
			GL11.glPopMatrix();
		} else {
			int i8;
			if(Item.itemsList[i3].requiresMultipleRenderPasses()) {
				GL11.glDisable(GL11.GL_LIGHTING);
				renderEngine2.bindTexture(renderEngine2.getTexture(Item.itemsList[i3].getTextureFile()));
						
				for(i8 = 0; i8 <= 1; ++i8) {
					int i9 = Item.itemsList[i3].getIconFromDamageAndRenderpass(i4, i8);
					i10 = Item.itemsList[i3].getColorFromDamage(i4, i8);
					f11 = (float)(i10 >> 16 & 255) / 255.0F;
					f12 = (float)(i10 >> 8 & 255) / 255.0F;
					f13 = (float)(i10 & 255) / 255.0F;
					if(this.field_27004_a) {
						GL11.glColor4f(f11, f12, f13, 1.0F);
					}

					//this.renderTexturedQuad(i6, i7, i9 % 16 * 16, i9 / 16 * 16, 16, 16);
					this.renderTexturedQuad(i6, i7, (i9 & 0xf) << 4, i9 & 0xff0, 16, 16);
				}

				GL11.glEnable(GL11.GL_LIGHTING);
			} else if(i5 >= 0) {
				GL11.glDisable(GL11.GL_LIGHTING);
				renderEngine2.bindTexture(renderEngine2.getTexture(Item.itemsList[i3].getTextureFile()));

				i8 = Item.itemsList[i3].getColorFromDamage(i4, 0);
				float f14 = (float)(i8 >> 16 & 255) / 255.0F;
				float f16 = (float)(i8 >> 8 & 255) / 255.0F;
				f11 = (float)(i8 & 255) / 255.0F;
				if(this.field_27004_a) {
					GL11.glColor4f(f14, f16, f11, 1.0F);
				}

				//this.renderTexturedQuad(i6, i7, i5 % 16 * 16, i5 / 16 * 16, 16, 16);
				this.renderTexturedQuad(i6, i7, (i5 & 0xf) << 4, i5 & 0xff0, 16, 16);
				GL11.glEnable(GL11.GL_LIGHTING);
			}
		}

		GL11.glEnable(GL11.GL_CULL_FACE);
	}

	public void renderItemIntoGUI(FontRenderer fontRenderer1, RenderEngine renderEngine2, ItemStack itemStack3, int i4, int i5) {
		if(itemStack3 != null) {
			this.drawItemIntoGui(fontRenderer1, renderEngine2, itemStack3.itemID, itemStack3.getItemDamage(), itemStack3.getIconIndex(), i4, i5);
			if(itemStack3 != null && itemStack3.hasEffect()) {
				GL11.glDepthFunc(GL11.GL_GREATER);
				GL11.glDisable(GL11.GL_LIGHTING);
				GL11.glDepthMask(false);
				
				renderEngine2.bindTexture(renderEngine2.getTexture("%blur%/misc/glint.png"));
				this.zLevel -= 50.0F;
				
				GL11.glEnable(GL11.GL_BLEND);
				GL11.glBlendFunc(GL11.GL_DST_COLOR, GL11.GL_DST_COLOR);
				GL11.glColor4f(0.5F, 0.25F, 0.8F, 1.0F);
				this.func_40266_a(i4 * 431278612 + i5 * 32178161, i4 - 2, i5 - 2, 20, 20);
				GL11.glDisable(GL11.GL_BLEND);
				
				this.zLevel += 50.0F;
				GL11.glDepthMask(true);
				GL11.glEnable(GL11.GL_LIGHTING);
				GL11.glDepthFunc(GL11.GL_LEQUAL);
			}

		}
	}

	private void func_40266_a(int i1, int i2, int i3, int i4, int i5) {
		for(int i6 = 0; i6 < 2; ++i6) {
			if(i6 == 0) {
				GL11.glBlendFunc(GL11.GL_SRC_COLOR, GL11.GL_ONE);
			}

			if(i6 == 1) {
				GL11.glBlendFunc(GL11.GL_SRC_COLOR, GL11.GL_ONE);
			}

			float f7 = 0.00390625F;
			float f8 = 0.00390625F;
			float f9 = (float)(System.currentTimeMillis() % (long)(3000 + i6 * 1873)) / (3000.0F + (float)(i6 * 1873)) * 256.0F;
			float f10 = 0.0F;
			Tessellator tessellator11 = Tessellator.instance;
			float f12 = 4.0F;
			if(i6 == 1) {
				f12 = -1.0F;
			}

			tessellator11.startDrawingQuads();
			tessellator11.addVertexWithUV((double)(i2 + 0), (double)(i3 + i5), (double)this.zLevel, (double)((f9 + (float)i5 * f12) * f7), (double)((f10 + (float)i5) * f8));
			tessellator11.addVertexWithUV((double)(i2 + i4), (double)(i3 + i5), (double)this.zLevel, (double)((f9 + (float)i4 + (float)i5 * f12) * f7), (double)((f10 + (float)i5) * f8));
			tessellator11.addVertexWithUV((double)(i2 + i4), (double)(i3 + 0), (double)this.zLevel, (double)((f9 + (float)i4) * f7), (double)((f10 + 0.0F) * f8));
			tessellator11.addVertexWithUV((double)(i2 + 0), (double)(i3 + 0), (double)this.zLevel, (double)((f9 + 0.0F) * f7), (double)((f10 + 0.0F) * f8));
			tessellator11.draw();
		}

	}

	public void renderItemOverlayIntoGUI(FontRenderer fontRenderer1, RenderEngine renderEngine2, ItemStack itemStack3, int i4, int i5) {
		if(itemStack3 != null) {
			if(itemStack3.stackSize > 1) {
				String string6 = "" + itemStack3.stackSize;
				GL11.glDisable(GL11.GL_LIGHTING);
				GL11.glDisable(GL11.GL_DEPTH_TEST);
				fontRenderer1.drawStringWithShadow(string6, i4 + 19 - 2 - fontRenderer1.getStringWidth(string6), i5 + 6 + 3, 0xFFFFFF);
				GL11.glEnable(GL11.GL_LIGHTING);
				GL11.glEnable(GL11.GL_DEPTH_TEST);
			}

			if(itemStack3.isItemDamaged()) {
				int i11 = (int)Math.round(13.0D - (double)itemStack3.getItemDamageForDisplay() * 13.0D / (double)itemStack3.getMaxDamage());
				int i7 = (int)Math.round(255.0D - (double)itemStack3.getItemDamageForDisplay() * 255.0D / (double)itemStack3.getMaxDamage());
				GL11.glDisable(GL11.GL_LIGHTING);
				GL11.glDisable(GL11.GL_DEPTH_TEST);
				GL11.glDisable(GL11.GL_TEXTURE_2D);
				Tessellator tessellator8 = Tessellator.instance;
				int i9 = 255 - i7 << 16 | i7 << 8;
				int i10 = (255 - i7) / 4 << 16 | 16128;
				this.renderQuad(tessellator8, i4 + 2, i5 + 13, 13, 2, 0);
				this.renderQuad(tessellator8, i4 + 2, i5 + 13, 12, 1, i10);
				this.renderQuad(tessellator8, i4 + 2, i5 + 13, i11, 1, i9);
				GL11.glEnable(GL11.GL_TEXTURE_2D);
				GL11.glEnable(GL11.GL_LIGHTING);
				GL11.glEnable(GL11.GL_DEPTH_TEST);
				GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			}

		}
	}

	private void renderQuad(Tessellator tessellator1, int i2, int i3, int i4, int i5, int i6) {
		tessellator1.startDrawingQuads();
		tessellator1.setColorOpaque_I(i6);
		tessellator1.addVertex((double)(i2 + 0), (double)(i3 + 0), 0.0D);
		tessellator1.addVertex((double)(i2 + 0), (double)(i3 + i5), 0.0D);
		tessellator1.addVertex((double)(i2 + i4), (double)(i3 + i5), 0.0D);
		tessellator1.addVertex((double)(i2 + i4), (double)(i3 + 0), 0.0D);
		tessellator1.draw();
	}

	public void renderTexturedQuad(int i1, int i2, int i3, int i4, int i5, int i6) {
		double f7 = Texels.texelsU(1F);
		double f8 = Texels.texelsV(1F);
		
		Tessellator tessellator9 = Tessellator.instance;
		tessellator9.startDrawingQuads();
		tessellator9.addVertexWithUV((double)(i1 + 0), (double)(i2 + i6), (double)this.zLevel, (i3 + 0) * f7, (i4 + i6) * f8);
		tessellator9.addVertexWithUV((double)(i1 + i5), (double)(i2 + i6), (double)this.zLevel, (i3 + i5) * f7, (i4 + i6) * f8);
		tessellator9.addVertexWithUV((double)(i1 + i5), (double)(i2 + 0), (double)this.zLevel, (i3 + i5) * f7, (i4 + 0) * f8);
		tessellator9.addVertexWithUV((double)(i1 + 0), (double)(i2 + 0), (double)this.zLevel, (i3 + 0) * f7, (i4 + 0) * f8);
		tessellator9.draw();
	}

	public void doRender(Entity entity1, double d2, double d4, double d6, float f8, float f9) {
		this.doRenderItem((EntityItem)entity1, d2, d4, d6, f8, f9);
	}
}
