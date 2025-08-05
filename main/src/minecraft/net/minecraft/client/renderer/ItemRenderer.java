package net.minecraft.client.renderer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.mojontwins.utils.Idx2uvF;
import com.mojontwins.utils.Texels;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.EntityPlayerSP;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.src.MathHelper;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EnumAction;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.map.ItemMap;
import net.minecraft.world.item.map.MapData;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.tile.Block;

public class ItemRenderer {
	private Minecraft mc;
	private ItemStack itemToRender = null;
	private float equippedProgress = 0.0F;
	private float prevEquippedProgress = 0.0F;
	private RenderBlocks renderBlocksInstance = new RenderBlocks();
	private int equippedItemSlot = -1;
	private MapItemRenderer mapItemRenderer;

	public ItemRenderer(Minecraft mc) {
		this.mc = mc;
		this.mapItemRenderer = new MapItemRenderer(mc.fontRenderer, mc.renderEngine);
		
	}

	public void renderItem(EntityLiving entityLiving1, ItemStack itemStack2, int i3) {
		GL11.glPushMatrix();
		if(Block.blocksList[itemStack2.itemID] != null && RenderBlocks.renderItemIn3d(Block.blocksList[itemStack2.itemID].getRenderType())) {
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.mc.renderEngine.getTexture(itemStack2.getItem().getTextureFile()));
			this.renderBlocksInstance.renderBlockAsItem(Block.blocksList[itemStack2.itemID], itemStack2.getItemDamage(), 1.0F);
		} else {
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.mc.renderEngine.getTexture(itemStack2.getItem().getTextureFile()));

			Tessellator tessellator4 = Tessellator.instance;

			Idx2uvF.calc(entityLiving1.getItemIcon(itemStack2, i3));
			double f6 = Idx2uvF.u1;
			double f7 = Idx2uvF.u2;
			double f8 = Idx2uvF.v1 + .0001D; 	// TODO : Figure out why I need this shit
			double f9 = Idx2uvF.v2;
			
			float f10 = 0.0F;
			float f11 = 0.3F;
			GL11.glEnable(GL12.GL_RESCALE_NORMAL);
			GL11.glTranslatef(-f10, -f11, 0.0F);
			float f12 = 1.5F;
			GL11.glScalef(f12, f12, f12);
			GL11.glRotatef(50.0F, 0.0F, 1.0F, 0.0F);
			GL11.glRotatef(335.0F, 0.0F, 0.0F, 1.0F);
			GL11.glTranslatef(-0.9375F, -0.0625F, 0.0F);
			this.renderItemIn2D(tessellator4, f7, f8, f6, f9);
			if(itemStack2 != null && itemStack2.hasEffect() && i3 == 0) {
				GL11.glDepthFunc(GL11.GL_EQUAL);
				GL11.glDisable(GL11.GL_LIGHTING);
				this.mc.renderEngine.bindTexture(this.mc.renderEngine.getTexture("%blur%/misc/glint.png"));
				GL11.glEnable(GL11.GL_BLEND);
				GL11.glBlendFunc(GL11.GL_SRC_COLOR, GL11.GL_ONE);
				float f13 = 0.76F;
				GL11.glColor4f(0.5F * f13, 0.25F * f13, 0.8F * f13, 1.0F);
				GL11.glMatrixMode(GL11.GL_TEXTURE);
				GL11.glPushMatrix();
				float f14 = 0.125F;
				GL11.glScalef(f14, f14, f14);
				float f15 = (float)(System.currentTimeMillis() % 3000L) / 3000.0F * 8.0F;
				GL11.glTranslatef(f15, 0.0F, 0.0F);
				GL11.glRotatef(-50.0F, 0.0F, 0.0F, 1.0F);
				this.renderItemIn2D(tessellator4, 0.0F, 0.0F, 1.0F, 1.0F);
				GL11.glPopMatrix();
				GL11.glPushMatrix();
				GL11.glScalef(f14, f14, f14);
				f15 = (float)(System.currentTimeMillis() % 4873L) / 4873.0F * 8.0F;
				GL11.glTranslatef(-f15, 0.0F, 0.0F);
				GL11.glRotatef(10.0F, 0.0F, 0.0F, 1.0F);
				this.renderItemIn2D(tessellator4, 0.0F, 0.0F, 1.0F, 1.0F);
				GL11.glPopMatrix();
				GL11.glMatrixMode(GL11.GL_MODELVIEW);
				GL11.glDisable(GL11.GL_BLEND);
				GL11.glEnable(GL11.GL_LIGHTING);
				GL11.glDepthFunc(GL11.GL_LEQUAL);
			}

			GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		}

		GL11.glPopMatrix();
	}

	private void renderItemIn2D(Tessellator tessellator1, double u1, double v1, double u2, double v2) {
		float size = 1.0F;
		float width = 0.0625F;

		// Front & back

		tessellator1.startDrawingQuads();
		tessellator1.setNormal(0.0F, 0.0F, 1.0F);
		tessellator1.addVertexWithUV(0.0D, 0.0D, 0.0D, (double)u1, (double)v2);
		tessellator1.addVertexWithUV((double)size, 0.0D, 0.0D, (double)u2, (double)v2);
		tessellator1.addVertexWithUV((double)size, 1.0D, 0.0D, (double)u2, (double)v1);
		tessellator1.addVertexWithUV(0.0D, 1.0D, 0.0D, (double)u1, (double)v1);
		tessellator1.draw();
		tessellator1.startDrawingQuads();
		tessellator1.setNormal(0.0F, 0.0F, -1.0F);
		tessellator1.addVertexWithUV(0.0D, 1.0D, (double)(0.0F - width), (double)u1, (double)v1);
		tessellator1.addVertexWithUV((double)size, 1.0D, (double)(0.0F - width), (double)u2, (double)v1);
		tessellator1.addVertexWithUV((double)size, 0.0D, (double)(0.0F - width), (double)u2, (double)v2);
		tessellator1.addVertexWithUV(0.0D, 0.0D, (double)(0.0F - width), (double)u1, (double)v2);
		tessellator1.draw();

		// Edges, horizontal

		tessellator1.startDrawingQuads();
		tessellator1.setNormal(-1.0F, 0.0F, 0.0F);

		int i8;
		double f9;
		double f10;
		double f11;
		for(i8 = 0; i8 < 16; ++i8) {
			f9 = (double)i8 / 16.0D;	// 0 < f9 < 1.0F
			f10 = u1 + (u2 - u1) * f9 - Texels.texelsUd(0.5D); // 0.001953125D;
			f11 = size * f9;
			tessellator1.addVertexWithUV((double)f11, 0.0D, (double)(0.0F - width), (double)f10, (double)v2);
			tessellator1.addVertexWithUV((double)f11, 0.0D, 0.0D, (double)f10, (double)v2);
			tessellator1.addVertexWithUV((double)f11, 1.0D, 0.0D, (double)f10, (double)v1);
			tessellator1.addVertexWithUV((double)f11, 1.0D, (double)(0.0F - width), (double)f10, (double)v1);
		}

		tessellator1.draw();
		tessellator1.startDrawingQuads();
		tessellator1.setNormal(1.0F, 0.0F, 0.0F);

		for(i8 = 0; i8 < 16; ++i8) {
			f9 = i8 / 16.0D;
			f10 = u1 + (u2 - u1) * f9 - Texels.texelsUd(0.5D); // 0.001953125D;
			f11 = size * f9 + 0.0625D;
			tessellator1.addVertexWithUV((double)f11, 1.0D, (double)(0.0F - width), (double)f10, (double)v1);
			tessellator1.addVertexWithUV((double)f11, 1.0D, 0.0D, (double)f10, (double)v1);
			tessellator1.addVertexWithUV((double)f11, 0.0D, 0.0D, (double)f10, (double)v2);
			tessellator1.addVertexWithUV((double)f11, 0.0D, (double)(0.0F - width), (double)f10, (double)v2);
		}

		tessellator1.draw();

		// Edges, vertical

		tessellator1.startDrawingQuads();
		tessellator1.setNormal(0.0F, 1.0F, 0.0F);

		for(i8 = 0; i8 < 16; ++i8) {
			f9 = i8 / 16.0D;
			f10 = v2 + (v1 - v2) * f9 - Texels.texelsVd(0.5D); // 0.001953125D;
			f11 = size * f9 + 0.0625D;
			tessellator1.addVertexWithUV(0.0D, (double)f11, 0.0D, (double)u1, (double)f10);
			tessellator1.addVertexWithUV((double)size, (double)f11, 0.0D, (double)u2, (double)f10);
			tessellator1.addVertexWithUV((double)size, (double)f11, (double)(0.0F - width), (double)u2, (double)f10);
			tessellator1.addVertexWithUV(0.0D, (double)f11, (double)(0.0F - width), (double)u1, (double)f10);
		}

		tessellator1.draw();
		tessellator1.startDrawingQuads();
		tessellator1.setNormal(0.0F, -1.0F, 0.0F);

		for(i8 = 0; i8 < 16; ++i8) {
			f9 = i8 / 16.0D;
			f10 = v2 + (v1 - v2) * f9 - Texels.texelsVd (0.5D); // 0.001953125D;
			f11 = size * f9;
			tessellator1.addVertexWithUV((double)size, (double)f11, 0.0D, (double)u2, (double)f10);
			tessellator1.addVertexWithUV(0.0D, (double)f11, 0.0D, (double)u1, (double)f10);
			tessellator1.addVertexWithUV(0.0D, (double)f11, (double)(0.0F - width), (double)u1, (double)f10);
			tessellator1.addVertexWithUV((double)size, (double)f11, (double)(0.0F - width), (double)u2, (double)f10);
		}

		tessellator1.draw();
	}

	public void renderItemInFirstPerson(float f1) {
		float f2 = this.prevEquippedProgress + (this.equippedProgress - this.prevEquippedProgress) * f1;
		EntityPlayerSP entityPlayerSP3 = this.mc.thePlayer;
		float f4 = entityPlayerSP3.prevRotationPitch + (entityPlayerSP3.rotationPitch - entityPlayerSP3.prevRotationPitch) * f1;
		GL11.glPushMatrix();
		GL11.glRotatef(f4, 1.0F, 0.0F, 0.0F);
		GL11.glRotatef(entityPlayerSP3.prevRotationYaw + (entityPlayerSP3.rotationYaw - entityPlayerSP3.prevRotationYaw) * f1, 0.0F, 1.0F, 0.0F);
		RenderHelper.enableStandardItemLighting();
		GL11.glPopMatrix();
		float f6;
		float f7;
		if(entityPlayerSP3 instanceof EntityPlayerSP) {
			EntityPlayerSP entityPlayerSP5 = (EntityPlayerSP)entityPlayerSP3;
			f6 = entityPlayerSP5.prevRenderArmPitch + (entityPlayerSP5.renderArmPitch - entityPlayerSP5.prevRenderArmPitch) * f1;
			f7 = entityPlayerSP5.prevRenderArmYaw + (entityPlayerSP5.renderArmYaw - entityPlayerSP5.prevRenderArmYaw) * f1;
			GL11.glRotatef((entityPlayerSP3.rotationPitch - f6) * 0.1F, 1.0F, 0.0F, 0.0F);
			GL11.glRotatef((entityPlayerSP3.rotationYaw - f7) * 0.1F, 0.0F, 1.0F, 0.0F);
		}

		ItemStack itemStack14 = this.itemToRender;
		f6 = this.mc.theWorld.getLightBrightness(MathHelper.floor_double(entityPlayerSP3.posX), MathHelper.floor_double(entityPlayerSP3.posY), MathHelper.floor_double(entityPlayerSP3.posZ));
		f6 = 1.0F;
		int i15 = this.mc.theWorld.getLightBrightnessForSkyBlocks(MathHelper.floor_double(entityPlayerSP3.posX), MathHelper.floor_double(entityPlayerSP3.posY), MathHelper.floor_double(entityPlayerSP3.posZ), 0);
		int i8 = i15 % 65536;
		int i9 = i15 / 65536;

		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)i8 / 1.0F, (float)i9 / 1.0F);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		float f10;
		float f16;
		float f18;
		if(itemStack14 != null) {
			i15 = Item.itemsList[itemStack14.itemID].getColorFromDamage(itemStack14.getItemDamage(), 0);
			f16 = (float)(i15 >> 16 & 255) / 255.0F;
			f18 = (float)(i15 >> 8 & 255) / 255.0F;
			f10 = (float)(i15 & 255) / 255.0F;
			GL11.glColor4f(f6 * f16, f6 * f18, f6 * f10, 1.0F);
		} else {
			GL11.glColor4f(f6, f6, f6, 1.0F);
		}

		float f11;
		float f13;
		if(itemStack14 != null && itemStack14.getItem() instanceof ItemMap) {
			GL11.glPushMatrix();
			f7 = 0.8F;
			f16 = entityPlayerSP3.getSwingProgress(f1);
			f18 = MathHelper.sin(f16 * (float)Math.PI);
			f10 = MathHelper.sin(MathHelper.sqrt_float(f16) * (float)Math.PI);
			GL11.glTranslatef(-f10 * 0.4F, MathHelper.sin(MathHelper.sqrt_float(f16) * (float)Math.PI * 2.0F) * 0.2F, -f18 * 0.2F);
			f16 = 1.0F - f4 / 45.0F + 0.1F;
			if(f16 < 0.0F) {
				f16 = 0.0F;
			}

			if(f16 > 1.0F) {
				f16 = 1.0F;
			}

			f16 = -MathHelper.cos(f16 * (float)Math.PI) * 0.5F + 0.5F;
			GL11.glTranslatef(0.0F, 0.0F * f7 - (1.0F - f2) * 1.2F - f16 * 0.5F + 0.04F, -0.9F * f7);
			GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
			GL11.glRotatef(f16 * -85.0F, 0.0F, 0.0F, 1.0F);
			GL11.glEnable(GL12.GL_RESCALE_NORMAL);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.mc.renderEngine.getTextureForDownloadableImage(this.mc.thePlayer.skinUrl, this.mc.thePlayer.getTexture()));

			for(i9 = 0; i9 < 2; ++i9) {
				int i24 = i9 * 2 - 1;
				GL11.glPushMatrix();
				GL11.glTranslatef(-0.0F, -0.6F, 1.1F * (float)i24);
				GL11.glRotatef((float)(-45 * i24), 1.0F, 0.0F, 0.0F);
				GL11.glRotatef(-90.0F, 0.0F, 0.0F, 1.0F);
				GL11.glRotatef(59.0F, 0.0F, 0.0F, 1.0F);
				GL11.glRotatef((float)(-65 * i24), 0.0F, 1.0F, 0.0F);
				Render render22 = RenderManager.instance.getEntityRenderObject(this.mc.thePlayer);
				RenderPlayer renderPlayer26 = (RenderPlayer)render22;
				f13 = 1.0F;
				GL11.glScalef(f13, f13, f13);
				renderPlayer26.drawFirstPersonHand();
				GL11.glPopMatrix();
			}

			f18 = entityPlayerSP3.getSwingProgress(f1);
			f10 = MathHelper.sin(f18 * f18 * (float)Math.PI);
			f11 = MathHelper.sin(MathHelper.sqrt_float(f18) * (float)Math.PI);
			GL11.glRotatef(-f10 * 20.0F, 0.0F, 1.0F, 0.0F);
			GL11.glRotatef(-f11 * 20.0F, 0.0F, 0.0F, 1.0F);
			GL11.glRotatef(-f11 * 80.0F, 1.0F, 0.0F, 0.0F);
			f18 = 0.38F;
			GL11.glScalef(f18, f18, f18);
			GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
			GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
			GL11.glTranslatef(-1.0F, -1.0F, 0.0F);
			f10 = 0.015625F;
			GL11.glScalef(f10, f10, f10);
			this.mc.renderEngine.bindTexture(this.mc.renderEngine.getTexture("/misc/mapbg.png"));
			Tessellator tessellator23 = Tessellator.instance;
			GL11.glNormal3f(0.0F, 0.0F, -1.0F);
			tessellator23.startDrawingQuads();
			byte b27 = 7;
			tessellator23.addVertexWithUV((double)(0 - b27), (double)(128 + b27), 0.0D, 0.0D, 1.0D);
			tessellator23.addVertexWithUV((double)(128 + b27), (double)(128 + b27), 0.0D, 1.0D, 1.0D);
			tessellator23.addVertexWithUV((double)(128 + b27), (double)(0 - b27), 0.0D, 1.0D, 0.0D);
			tessellator23.addVertexWithUV((double)(0 - b27), (double)(0 - b27), 0.0D, 0.0D, 0.0D);
			tessellator23.draw();
			MapData mapData25 = Item.map.getMapData(itemStack14, this.mc.theWorld);
			/*if(itemStack14.getItem() instanceof ItemTFMagicMap) {
				this.TFMagicMapItemRenderer.renderMap(this.mc.thePlayer, this.mc.renderEngine, mapData25);
			} else*/ {
				this.mapItemRenderer.renderMap(this.mc.thePlayer, this.mc.renderEngine, mapData25);
			}
			GL11.glPopMatrix();
		} else if(itemStack14 != null) {
			GL11.glPushMatrix();
			f7 = 0.8F;
			float f12;
			if(entityPlayerSP3.getItemInUseCount() > 0) {
				EnumAction enumAction17 = itemStack14.getItemUseAction();
				if(enumAction17 == EnumAction.eat || enumAction17 == EnumAction.drink) {
					f18 = (float)entityPlayerSP3.getItemInUseCount() - f1 + 1.0F;
					f10 = 1.0F - f18 / (float)itemStack14.getMaxItemUseDuration();
					f12 = 1.0F - f10;
					f12 = f12 * f12 * f12;
					f12 = f12 * f12 * f12;
					f12 = f12 * f12 * f12;
					f13 = 1.0F - f12;
					GL11.glTranslatef(0.0F, MathHelper.abs(MathHelper.cos(f18 / 4.0F * (float)Math.PI) * 0.1F) * (float)((double)f10 > 0.2D ? 1 : 0), 0.0F);
					GL11.glTranslatef(f13 * 0.6F, -f13 * 0.5F, 0.0F);
					GL11.glRotatef(f13 * 90.0F, 0.0F, 1.0F, 0.0F);
					GL11.glRotatef(f13 * 10.0F, 1.0F, 0.0F, 0.0F);
					GL11.glRotatef(f13 * 30.0F, 0.0F, 0.0F, 1.0F);
				}
			} else {
				f16 = entityPlayerSP3.getSwingProgress(f1);
				f18 = MathHelper.sin(f16 * (float)Math.PI);
				f10 = MathHelper.sin(MathHelper.sqrt_float(f16) * (float)Math.PI);
				GL11.glTranslatef(-f10 * 0.4F, MathHelper.sin(MathHelper.sqrt_float(f16) * (float)Math.PI * 2.0F) * 0.2F, -f18 * 0.2F);
			}

			GL11.glTranslatef(0.7F * f7, -0.65F * f7 - (1.0F - f2) * 0.6F, -0.9F * f7);
			GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
			GL11.glEnable(GL12.GL_RESCALE_NORMAL);
			f16 = entityPlayerSP3.getSwingProgress(f1);
			f18 = MathHelper.sin(f16 * f16 * (float)Math.PI);
			f10 = MathHelper.sin(MathHelper.sqrt_float(f16) * (float)Math.PI);
			GL11.glRotatef(-f18 * 20.0F, 0.0F, 1.0F, 0.0F);
			GL11.glRotatef(-f10 * 20.0F, 0.0F, 0.0F, 1.0F);
			GL11.glRotatef(-f10 * 80.0F, 1.0F, 0.0F, 0.0F);
			f16 = 0.4F;
			GL11.glScalef(f16, f16, f16);
			if(entityPlayerSP3.getItemInUseCount() > 0) {
				EnumAction enumAction20 = itemStack14.getItemUseAction();
				if(enumAction20 == EnumAction.block) {
					GL11.glTranslatef(-0.5F, 0.2F, 0.0F);
					GL11.glRotatef(30.0F, 0.0F, 1.0F, 0.0F);
					GL11.glRotatef(-80.0F, 1.0F, 0.0F, 0.0F);
					GL11.glRotatef(60.0F, 0.0F, 1.0F, 0.0F);
				} else if(enumAction20 == EnumAction.bow) {
					GL11.glRotatef(-18.0F, 0.0F, 0.0F, 1.0F);
					GL11.glRotatef(-12.0F, 0.0F, 1.0F, 0.0F);
					GL11.glRotatef(-8.0F, 1.0F, 0.0F, 0.0F);
					GL11.glTranslatef(-0.9F, 0.2F, 0.0F);
					f10 = (float)itemStack14.getMaxItemUseDuration() - ((float)entityPlayerSP3.getItemInUseCount() - f1 + 1.0F);
					f11 = f10 / 20.0F;
					f11 = (f11 * f11 + f11 * 2.0F) / 3.0F;
					if(f11 > 1.0F) {
						f11 = 1.0F;
					}

					if(f11 > 0.1F) {
						GL11.glTranslatef(0.0F, MathHelper.sin((f10 - 0.1F) * 1.3F) * 0.01F * (f11 - 0.1F), 0.0F);
					}

					GL11.glTranslatef(0.0F, 0.0F, f11 * 0.1F);
					GL11.glRotatef(-335.0F, 0.0F, 0.0F, 1.0F);
					GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
					GL11.glTranslatef(0.0F, 0.5F, 0.0F);
					f12 = 1.0F + f11 * 0.2F;
					GL11.glScalef(1.0F, 1.0F, f12);
					GL11.glTranslatef(0.0F, -0.5F, 0.0F);
					GL11.glRotatef(50.0F, 0.0F, 1.0F, 0.0F);
					GL11.glRotatef(335.0F, 0.0F, 0.0F, 1.0F);
				}
			}

			if(itemStack14.getItem().shouldRotateAroundWhenRendering()) {
				GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
			}

			if(itemStack14.getItem().requiresMultipleRenderPasses()) {
				this.renderItem(entityPlayerSP3, itemStack14, 0);
				i9 = Item.itemsList[itemStack14.itemID].getColorFromDamage(itemStack14.getItemDamage(), 1);
				f10 = (float)(i9 >> 16 & 255) / 255.0F;
				f11 = (float)(i9 >> 8 & 255) / 255.0F;
				f12 = (float)(i9 & 255) / 255.0F;
				GL11.glColor4f(f6 * f10, f6 * f11, f6 * f12, 1.0F);
				this.renderItem(entityPlayerSP3, itemStack14, 1);
			} else {
				this.renderItem(entityPlayerSP3, itemStack14, 0);
			}

			GL11.glPopMatrix();
		} else {
			GL11.glPushMatrix();
			f7 = 0.8F;
			f16 = entityPlayerSP3.getSwingProgress(f1);
			f18 = MathHelper.sin(f16 * (float)Math.PI);
			f10 = MathHelper.sin(MathHelper.sqrt_float(f16) * (float)Math.PI);
			GL11.glTranslatef(-f10 * 0.3F, MathHelper.sin(MathHelper.sqrt_float(f16) * (float)Math.PI * 2.0F) * 0.4F, -f18 * 0.4F);
			GL11.glTranslatef(0.8F * f7, -0.75F * f7 - (1.0F - f2) * 0.6F, -0.9F * f7);
			GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
			GL11.glEnable(GL12.GL_RESCALE_NORMAL);
			f16 = entityPlayerSP3.getSwingProgress(f1);
			f18 = MathHelper.sin(f16 * f16 * (float)Math.PI);
			f10 = MathHelper.sin(MathHelper.sqrt_float(f16) * (float)Math.PI);
			GL11.glRotatef(f10 * 70.0F, 0.0F, 1.0F, 0.0F);
			GL11.glRotatef(-f18 * 20.0F, 0.0F, 0.0F, 1.0F);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.mc.renderEngine.getTextureForDownloadableImage(this.mc.thePlayer.skinUrl, this.mc.thePlayer.getTexture()));
			GL11.glTranslatef(-1.0F, 3.6F, 3.5F);
			GL11.glRotatef(120.0F, 0.0F, 0.0F, 1.0F);
			GL11.glRotatef(200.0F, 1.0F, 0.0F, 0.0F);
			GL11.glRotatef(-135.0F, 0.0F, 1.0F, 0.0F);
			GL11.glScalef(1.0F, 1.0F, 1.0F);
			GL11.glTranslatef(5.6F, 0.0F, 0.0F);
			Render render19 = RenderManager.instance.getEntityRenderObject(this.mc.thePlayer);
			RenderPlayer renderPlayer21 = (RenderPlayer)render19;
			f10 = 1.0F;
			GL11.glScalef(f10, f10, f10);
			renderPlayer21.drawFirstPersonHand();
			GL11.glPopMatrix();
		}

		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		RenderHelper.disableStandardItemLighting();
	}

	public void renderOverlays(float f1) {
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		int i2;
		if(this.mc.thePlayer.isBurning()) {
			i2 = this.mc.renderEngine.getTexture("/terrain.png");
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, i2);
			this.renderFireInFirstPerson(f1);
		}

		if(this.mc.thePlayer.isEntityInsideOpaqueBlock()) {
			i2 = MathHelper.floor_double(this.mc.thePlayer.posX);
			int i3 = MathHelper.floor_double(this.mc.thePlayer.posY);
			int i4 = MathHelper.floor_double(this.mc.thePlayer.posZ);
			int i5 = this.mc.renderEngine.getTexture("/terrain.png");
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, i5);
			int i6 = this.mc.theWorld.getBlockId(i2, i3, i4);
			if(this.mc.theWorld.isBlockNormalCube(i2, i3, i4)) {
				this.renderInsideOfBlock(f1, Block.blocksList[i6].getBlockTextureFromSide(2));
			} else {
				for(int i7 = 0; i7 < 8; ++i7) {
					float f8 = ((float)((i7 >> 0) % 2) - 0.5F) * this.mc.thePlayer.width * 0.9F;
					float f9 = ((float)((i7 >> 1) % 2) - 0.5F) * this.mc.thePlayer.height * 0.2F;
					float f10 = ((float)((i7 >> 2) % 2) - 0.5F) * this.mc.thePlayer.width * 0.9F;
					int i11 = MathHelper.floor_float((float)i2 + f8);
					int i12 = MathHelper.floor_float((float)i3 + f9);
					int i13 = MathHelper.floor_float((float)i4 + f10);
					if(this.mc.theWorld.isBlockNormalCube(i11, i12, i13)) {
						i6 = this.mc.theWorld.getBlockId(i11, i12, i13);
					}
				}
			}

			if(Block.blocksList[i6] != null) {
				this.renderInsideOfBlock(f1, Block.blocksList[i6].getBlockTextureFromSide(2));
			}
		}

		if(this.mc.thePlayer.isInsideOfMaterial(Material.water)) {
			i2 = this.mc.renderEngine.getTexture("/misc/water.png");
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, i2);
			this.renderWarpedTextureOverlay(f1);
		}

		GL11.glEnable(GL11.GL_ALPHA_TEST);
	}

	private void renderInsideOfBlock(float f1, int i2) {
		Tessellator tessellator3 = Tessellator.instance;
		this.mc.thePlayer.getBrightness(f1);
		float f4 = 0.1F;
		GL11.glColor4f(f4, f4, f4, 0.5F);
		GL11.glPushMatrix();
		float f5 = -1.0F;
		float f6 = 1.0F;
		float f7 = -1.0F;
		float f8 = 1.0F;
		float f9 = -0.5F;
		float f10 = 0.0078125F;
		float f11 = (float)(i2 % 16) / 256.0F - f10;
		float f12 = ((float)(i2 % 16) + 15.99F) / 256.0F + f10;
		float f13 = (float)(i2 / 16) / 256.0F - f10;
		float f14 = ((float)(i2 / 16) + 15.99F) / 256.0F + f10;
		tessellator3.startDrawingQuads();
		tessellator3.addVertexWithUV((double)f5, (double)f7, (double)f9, (double)f12, (double)f14);
		tessellator3.addVertexWithUV((double)f6, (double)f7, (double)f9, (double)f11, (double)f14);
		tessellator3.addVertexWithUV((double)f6, (double)f8, (double)f9, (double)f11, (double)f13);
		tessellator3.addVertexWithUV((double)f5, (double)f8, (double)f9, (double)f12, (double)f13);
		tessellator3.draw();
		GL11.glPopMatrix();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	}

	private void renderWarpedTextureOverlay(float f1) {
		Tessellator tessellator2 = Tessellator.instance;
		float f3 = this.mc.thePlayer.getBrightness(f1);
		GL11.glColor4f(f3, f3, f3, 0.5F);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glPushMatrix();
		float f4 = 4.0F;
		float f5 = -1.0F;
		float f6 = 1.0F;
		float f7 = -1.0F;
		float f8 = 1.0F;
		float f9 = -0.5F;
		float f10 = -this.mc.thePlayer.rotationYaw / 64.0F;
		float f11 = this.mc.thePlayer.rotationPitch / 64.0F;
		tessellator2.startDrawingQuads();
		tessellator2.addVertexWithUV((double)f5, (double)f7, (double)f9, (double)(f4 + f10), (double)(f4 + f11));
		tessellator2.addVertexWithUV((double)f6, (double)f7, (double)f9, (double)(0.0F + f10), (double)(f4 + f11));
		tessellator2.addVertexWithUV((double)f6, (double)f8, (double)f9, (double)(0.0F + f10), (double)(0.0F + f11));
		tessellator2.addVertexWithUV((double)f5, (double)f8, (double)f9, (double)(f4 + f10), (double)(0.0F + f11));
		tessellator2.draw();
		GL11.glPopMatrix();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glDisable(GL11.GL_BLEND);
	}

	private void renderFireInFirstPerson(float f1) {
		Tessellator tessellator2 = Tessellator.instance;
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.9F);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		float f3 = 1.0F;

		for(int i4 = 0; i4 < 2; ++i4) {
			GL11.glPushMatrix();
			int i5 = Block.fire.blockIndexInTexture + i4 * 16;

			Idx2uvF.calc(i5);
			double f8 = Idx2uvF.u1;
			double f9 = Idx2uvF.u2;
			double f10 = Idx2uvF.v1;
			double f11 = Idx2uvF.v2;
			
			float f12 = (0.0F - f3) / 2.0F;
			float f13 = f12 + f3;
			float f14 = 0.0F - f3 / 2.0F;
			float f15 = f14 + f3;
			float f16 = -0.5F;
			GL11.glTranslatef((float)(-(i4 * 2 - 1)) * 0.24F, -0.3F, 0.0F);
			GL11.glRotatef((float)(i4 * 2 - 1) * 10.0F, 0.0F, 1.0F, 0.0F);
			tessellator2.startDrawingQuads();
			tessellator2.addVertexWithUV((double)f12, (double)f14, (double)f16, (double)f9, (double)f11);
			tessellator2.addVertexWithUV((double)f13, (double)f14, (double)f16, (double)f8, (double)f11);
			tessellator2.addVertexWithUV((double)f13, (double)f15, (double)f16, (double)f8, (double)f10);
			tessellator2.addVertexWithUV((double)f12, (double)f15, (double)f16, (double)f9, (double)f10);
			tessellator2.draw();
			GL11.glPopMatrix();
		}

		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glDisable(GL11.GL_BLEND);
	}

	public void updateEquippedItem() {
		this.prevEquippedProgress = this.equippedProgress;
		EntityPlayerSP entityPlayerSP1 = this.mc.thePlayer;
		ItemStack itemStack2 = entityPlayerSP1.inventory.getCurrentItem();
		boolean z4 = this.equippedItemSlot == entityPlayerSP1.inventory.currentItem && itemStack2 == this.itemToRender;
		if(this.itemToRender == null && itemStack2 == null) {
			z4 = true;
		}

		if(itemStack2 != null && this.itemToRender != null && itemStack2 != this.itemToRender && itemStack2.itemID == this.itemToRender.itemID && itemStack2.getItemDamage() == this.itemToRender.getItemDamage()) {
			this.itemToRender = itemStack2;
			z4 = true;
		}

		float f5 = 0.4F;
		float f6 = z4 ? 1.0F : 0.0F;
		float f7 = f6 - this.equippedProgress;
		if(f7 < -f5) {
			f7 = -f5;
		}

		if(f7 > f5) {
			f7 = f5;
		}

		this.equippedProgress += f7;
		if(this.equippedProgress < 0.1F) {
			this.itemToRender = itemStack2;
			this.equippedItemSlot = entityPlayerSP1.inventory.currentItem;
		}

	}

	public void resetEquippedProgress() {
		this.equippedProgress = 0.0F;
	}

}
