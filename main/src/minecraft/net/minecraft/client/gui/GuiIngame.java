package net.minecraft.client.gui;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import net.minecraft.client.GameSettingsKeys;
import net.minecraft.client.GameSettingsValues;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.EntityClientPlayerMP;
import net.minecraft.client.multiplayer.NetClientHandler;
import net.minecraft.client.renderer.FontRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.src.MathHelper;
import net.minecraft.util.StringTranslate;
import net.minecraft.world.GameRules;
import net.minecraft.world.Version;
import net.minecraft.world.entity.FoodStats;
import net.minecraft.world.inventory.InventoryPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Seasons;
import net.minecraft.world.level.biome.BiomeGenBase;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.tile.Block;

public class GuiIngame extends Gui {
	private static RenderItem itemRenderer = new RenderItem();
	private List<ChatLine> chatMessageList = new ArrayList<ChatLine>();
	private List<String> messageList = new ArrayList<String>();
	private Random rand = new Random();
	private Minecraft mc;
	private String onScreenMessage = "";
	private int onScreenMessageTimeout = 0;
	private boolean fancyText = false;
	
	private int firstMessageIdx = 0;
	private boolean field_50018_o = false;
	public float damageGuiPartialTime;
	float prevVignetteBrightness = 1.0F;
	private int updateCounter;

	public GuiIngame(Minecraft mc) {
		this.mc = mc;
	}

	public void renderGameOverlay(float f1, boolean z2, int i3, int i4) {
		ScaledResolution scaledResolution5 = new ScaledResolution(this.mc.displayWidth, this.mc.displayHeight);
		int i6 = scaledResolution5.getScaledWidth();
		int i7 = scaledResolution5.getScaledHeight();
		FontRenderer fontRenderer8 = this.mc.fontRenderer;
		this.mc.entityRenderer.setupOverlayRendering();
		GL11.glEnable(GL11.GL_BLEND);
		if(Minecraft.isFancyGraphicsEnabled()) {
			this.renderVignette(this.mc.thePlayer.getBrightness(f1), i6, i7);
		} else {
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		}

		ItemStack itemStack9 = this.mc.thePlayer.inventory.armorItemInSlot(3);
		if(GameSettingsValues.thirdPersonView == 0 && itemStack9 != null) {
			if(itemStack9.itemID == Block.pumpkin.blockID) {
				this.renderPumpkinBlur(i6, i7);
			} 
		}

		boolean z11;
		int i12;
		int i13;
		int i16;
		int i17;
		int i19;
		int i20;
		int i22;
		int armorValue;
		int i45;
		if(!this.mc.playerController.func_35643_e()) {
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.mc.renderEngine.getTexture("/gui/gui.png"));
			InventoryPlayer inventoryPlayer31 = this.mc.thePlayer.inventory;
			this.zLevel = -90.0F;
			this.drawTexturedModalRect(i6 / 2 - 91, i7 - 22, 0, 0, 182, 22);
			this.drawTexturedModalRect(i6 / 2 - 91 - 1 + inventoryPlayer31.currentItem * 20, i7 - 22 - 1, 0, 22, 24, 22);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.mc.renderEngine.getTexture("/gui/icons.png"));
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_ONE_MINUS_DST_COLOR, GL11.GL_ONE_MINUS_SRC_COLOR);
			this.drawTexturedModalRect(i6 / 2 - 7, i7 / 2 - 7, 0, 0, 16, 16);
			GL11.glDisable(GL11.GL_BLEND);
			z11 = this.mc.thePlayer.heartsLife / 3 % 2 == 1;
			if(this.mc.thePlayer.heartsLife < 10) {
				z11 = false;
			}

			i12 = this.mc.thePlayer.getHealth();
			i13 = this.mc.thePlayer.prevHealth;
			this.rand.setSeed((long)(this.updateCounter * 312871));
			boolean z14 = false;
			
			FoodStats foodStats15 = this.mc.thePlayer.getFoodStats();
			i16 = foodStats15.getFoodLevel();
			i17 = foodStats15.getPrevFoodLevel();
			
			this.renderBossHealth();
			int i18;
			if(this.mc.playerController.shouldDrawHUD()) {
				i18 = i6 / 2 - 91;
				i19 = i6 / 2 + 91;

				i45 = i7 - 32;
				
				i22 = i45 - 10;
				
				armorValue = this.mc.thePlayer.getTotalArmorValue();
				int i24 = -1;
				
				// TODO: A means of regeneration
				/*
				if(this.mc.thePlayer.isPotionActive(Potion.regeneration)) {
					i24 = this.updateCounter % 25;
				}
				*/				

				int i25;
				int i26;
				int i28;
				int i29;
				for(i25 = 0; i25 < 10; ++i25) {
					i29 = i45;
					
					if(armorValue > 0) {
						if(GameRules.boolRule("enableHunger")) {						
							i26 = i18 + i25 * 8;
							if(i25 * 2 + 1 < armorValue) {
								this.drawTexturedModalRect(i26, i22, 34, 9, 9, 9);
							}
	
							if(i25 * 2 + 1 == armorValue) {
								this.drawTexturedModalRect(i26, i22, 25, 9, 9, 9);
							}
	
							if(i25 * 2 + 1 > armorValue) {
								this.drawTexturedModalRect(i26, i22, 16, 9, 9, 9);
							}
						} else {
							i26 = i6 / 2 + 91 - i25 * 8 - 9;
							if(i25 * 2 + 1 < armorValue) {
								this.drawTexturedModalRect(i26, i29, 36+34, 9, 9, 9);
							}

							if(i25 * 2 + 1 == armorValue) {
								this.drawTexturedModalRect(i26, i29, 36+25, 9, 9, 9);
							}

							if(i25 * 2 + 1 > armorValue) {
								this.drawTexturedModalRect(i26, i29, 36+16, 9, 9, 9);
							}
						}
					}

					i26 = 16;
					// TODO: Modify if player poisoned.
					/*
					if(this.mc.thePlayer.isPotionActive(Potion.poison)) {
						i26 += 36;
					}
					*/

					byte b27 = 0;
					if(z11) {
						b27 = 1;
					}

					i28 = i18 + i25 * 8;
					
					if(i12 <= 4) {
						i29 = i45 + this.rand.nextInt(2);
					}

					if(i25 == i24) {
						i29 -= 2;
					}

					byte b30 = 0;
					if(this.mc.theWorld.getWorldInfo().isHardcoreModeEnabled()) {
						b30 = 5;
					}

					this.drawTexturedModalRect(i28, i29, 16 + b27 * 9, 9 * b30, 9, 9);
					if(z11) {
						if(i25 * 2 + 1 < i13) {
							this.drawTexturedModalRect(i28, i29, i26 + 54, 9 * b30, 9, 9);
						}

						if(i25 * 2 + 1 == i13) {
							this.drawTexturedModalRect(i28, i29, i26 + 63, 9 * b30, 9, 9);
						}
					}

					if(i25 * 2 + 1 < i12) {
						this.drawTexturedModalRect(i28, i29, i26 + 36, 9 * b30, 9, 9);
					}

					if(i25 * 2 + 1 == i12) {
						this.drawTexturedModalRect(i28, i29, i26 + 45, 9 * b30, 9, 9);
					}
				}

				int i51;
				if(GameRules.boolRule("enableHunger")) {
					for(i25 = 0; i25 < 10; ++i25) {
						i26 = i45;
						i51 = 16;
						byte b52 = 0;
						
						// TODO: Modify if hunger
						/*
						if(this.mc.thePlayer.isPotionActive(Potion.hunger)) {
							i51 += 36;
							b52 = 13;
						}
						*/
	
						if(this.mc.thePlayer.getFoodStats().getSaturationLevel() <= 0.0F && this.updateCounter % (i16 * 3 + 1) == 0) {
							i26 = i45 + (this.rand.nextInt(3) - 1);
						}
	
						if(z14) {
							b52 = 1;
						}
	
						i29 = i19 - i25 * 8 - 9;
						this.drawTexturedModalRect(i29, i26, 16 + b52 * 9, 27, 9, 9);
						if(z14) {
							if(i25 * 2 + 1 < i17) {
								this.drawTexturedModalRect(i29, i26, i51 + 54, 27, 9, 9);
							}
	
							if(i25 * 2 + 1 == i17) {
								this.drawTexturedModalRect(i29, i26, i51 + 63, 27, 9, 9);
							}
						}
	
						if(i25 * 2 + 1 < i16) {
							this.drawTexturedModalRect(i29, i26, i51 + 36, 27, 9, 9);
						}
	
						if(i25 * 2 + 1 == i16) {
							this.drawTexturedModalRect(i29, i26, i51 + 45, 27, 9, 9);
						}
					}
				} 

				if(this.mc.thePlayer.isInsideOfMaterial(Material.water)) {
					i25 = this.mc.thePlayer.getAir();
					i26 = (int)Math.ceil((double)(i25 - 2) * 10.0D / 300.0D);
					i51 = (int)Math.ceil((double)i25 * 10.0D / 300.0D) - i26;

					for(i28 = 0; i28 < i26 + i51; ++i28) {
						if(GameRules.boolRule("enableHunger")) {
							if(i28 < i26) {
								this.drawTexturedModalRect(i19 - i28 * 8 - 9, i22, 16, 18, 9, 9);
							} else {
								this.drawTexturedModalRect(i19 - i28 * 8 - 9, i22, 25, 18, 9, 9);
							}
						} else {
							if(i28 < i26) {
								this.drawTexturedModalRect(i18 + i28 * 8, i22, 16, 18, 9, 9);
							} else {
								this.drawTexturedModalRect(i18 + i28 * 8, i22, 25, 18, 9, 9);
							}
						}
					}
				}
			}

			GL11.glDisable(GL11.GL_BLEND);
			GL11.glEnable(GL12.GL_RESCALE_NORMAL);
			RenderHelper.enableGUIStandardItemLighting();

			for(i18 = 0; i18 < 9; ++i18) {
				i19 = i6 / 2 - 90 + i18 * 20 + 2;
				i20 = i7 - 16 - 3;
				this.renderInventorySlot(i18, i19, i20, f1);
			}

			RenderHelper.disableStandardItemLighting();
			GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		}

		float f33;
		if(this.mc.thePlayer.getSleepTimer() > 0) {
			GL11.glDisable(GL11.GL_DEPTH_TEST);
			GL11.glDisable(GL11.GL_ALPHA_TEST);
			int i32 = this.mc.thePlayer.getSleepTimer();
			f33 = (float)i32 / 100.0F;
			if(f33 > 1.0F) {
				f33 = 1.0F - (float)(i32 - 100) / 10.0F;
			}

			i12 = (int)(220.0F * f33) << 24 | 1052704;
			drawRect(0, 0, i6, i7, i12);
			GL11.glEnable(GL11.GL_ALPHA_TEST);
			GL11.glEnable(GL11.GL_DEPTH_TEST);
		}

		int i39;
		int i40;
		if(this.mc.playerController.func_35642_f() && this.mc.thePlayer.experienceLevel > 0) {
			z11 = false;
			i12 = z11 ? 0xFFFFFF : 8453920;
			String string34 = "" + this.mc.thePlayer.experienceLevel;
			i39 = (i6 - fontRenderer8.getStringWidth(string34)) / 2;
			i40 = i7 - 31 - 4;
			fontRenderer8.drawString(string34, i39 + 1, i40, 0);
			fontRenderer8.drawString(string34, i39 - 1, i40, 0);
			fontRenderer8.drawString(string34, i39, i40 + 1, 0);
			fontRenderer8.drawString(string34, i39, i40 - 1, 0);
			fontRenderer8.drawString(string34, i39, i40, i12);
		}

		if(GameSettingsValues.showDebugInfo) {
			GL11.glPushMatrix();
			if(Minecraft.hasPaidCheckTime > 0L) {
				GL11.glTranslatef(0.0F, 32.0F, 0.0F);
			}

			fontRenderer8.drawStringWithShadow("Minecraft " + Version.getVersion() + " (" + this.mc.debug + ")", 2, 2, 0xFFFFFF);
			fontRenderer8.drawStringWithShadow(this.mc.debugInfoRenders(), 2, 12, 0xFFFFFF);
			fontRenderer8.drawStringWithShadow(this.mc.getEntityDebug(), 2, 22, 0xFFFFFF);
			fontRenderer8.drawStringWithShadow(this.mc.debugInfoEntities(), 2, 32, 0xFFFFFF);
			
			String string21 = this.mc.getWorldProviderName();
			fontRenderer8.drawStringWithShadow(string21, i6 - fontRenderer8.getStringWidth(string21) - 2, 22, 0xFFFFFF);
			
			long j35 = Runtime.getRuntime().maxMemory();
			long j36 = Runtime.getRuntime().totalMemory();
			long j41 = Runtime.getRuntime().freeMemory();
			long j42 = j36 - j41;
			String string44 = "Used: " + j42 * 100L / j35 + "% (" + j42 / 1024L / 1024L + "MB) of " + j35 / 1024L / 1024L + "MB";
			this.drawString(fontRenderer8, string44, i6 - fontRenderer8.getStringWidth(string44) - 2, 2, 14737632);
			string44 = "Allocated: " + j36 * 100L / j35 + "% (" + j36 / 1024L / 1024L + "MB)";
			this.drawString(fontRenderer8, string44, i6 - fontRenderer8.getStringWidth(string44) - 2, 12, 14737632);
			
			fontRenderer8.drawStringWithShadow(
					"Pos: " + (int)this.mc.thePlayer.posX + 
					", " + (int)this.mc.thePlayer.posY +
					", " + (int)this.mc.thePlayer.posZ +
					" [" + (MathHelper.floor_double((double)(this.mc.thePlayer.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3) + "]"
				, 2, 42, 0xFFFFFF);
			
			
			if(this.mc.thePlayer.inventory.hasItem(Item.pocketSundial.shiftedIndex)) {
				float timeAdjusted = (float) (this.mc.theWorld.worldInfo.getWorldTime() % 24000);
				fontRenderer8.drawStringWithShadow("Time: " + this.twoDigits((int)((timeAdjusted / 1000.0F) + 6) % 24) + ":" + this.twoDigits((int)((timeAdjusted % 1000.0F) * 60 / 1000)) + " [" + ((float)((int)(this.mc.theWorld.getCelestialAngle(f1) * 100)) / 100F)  + "]", 2, 52, 0xFFFFFF);
			} else {
				fontRenderer8.drawStringWithShadow("Time: You have no clock!", 2, 52, 0xFFFFFF);
			}
			
			int x = MathHelper.floor_double(this.mc.thePlayer.posX);
			int y = MathHelper.floor_double(this.mc.thePlayer.posY);
			int z = MathHelper.floor_double(this.mc.thePlayer.posZ);
			
			if(!this.mc.theWorld.isRemote) {
				
				string21 = "Seed: " + this.mc.theWorld.getSeed();
				this.drawString(fontRenderer8, string21, i6 - fontRenderer8.getStringWidth(string21) - 2, 32, 14737632);
			}
			
			if(this.mc.theWorld != null && this.mc.theWorld.blockExists(x, y, z)) {
				BiomeGenBase biome = this.mc.theWorld.getBiomeGenForCoords(x, z);
				string21 = "Biome: " + biome.biomeName + " (" + biome.weather.name + ")";
				this.drawString(fontRenderer8, string21, i6 - fontRenderer8.getStringWidth(string21) - 2, 42, 14737632);
			}
			
			if(Seasons.activated()) {
				string21 = Seasons.getStringForGui() ;
				this.drawString(fontRenderer8, string21, i6 - fontRenderer8.getStringWidth(string21) - 2, 52, 14737632);
			}
				
			GL11.glPopMatrix();
		} else {
			fontRenderer8.drawStringWithShadow("Minecraft " + Version.getVersion(), 2, 2, 0xFFFFFF);
		}

		if(this.onScreenMessageTimeout > 0) {
			f33 = (float)this.onScreenMessageTimeout - f1;
			i12 = (int)(f33 * 256.0F / 20.0F);
			if(i12 > 255) {
				i12 = 255;
			}

			if(i12 > 0) {
				GL11.glPushMatrix();
				GL11.glTranslatef((float)(i6 / 2), (float)(i7 - 48), 0.0F);
				GL11.glEnable(GL11.GL_BLEND);
				GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
				i13 = 0xFFFFFF;
				if(this.fancyText) {
					i13 = Color.HSBtoRGB(f33 / 50.0F, 0.7F, 0.6F) & 0xFFFFFF;
				}

				fontRenderer8.drawString(this.onScreenMessage, -fontRenderer8.getStringWidth(this.onScreenMessage) / 2, -4, i13 + (i12 << 24));
				GL11.glDisable(GL11.GL_BLEND);
				GL11.glPopMatrix();
			}
		}

		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		GL11.glPushMatrix();
		GL11.glTranslatef(0.0F, (float)(i7 - 48), 0.0F);
		this.func_50010_a(fontRenderer8);
		GL11.glPopMatrix();
		if(this.mc.thePlayer instanceof EntityClientPlayerMP && GameSettingsKeys.keyBindPlayerList.pressed) {
			NetClientHandler netClientHandler37 = ((EntityClientPlayerMP)this.mc.thePlayer).sendQueue;
			List<GuiPlayerInfo> list38 = netClientHandler37.playerNames;
			i13 = netClientHandler37.currentServerMaxPlayers;
			i39 = i13;

			for(i40 = 1; i39 > 20; i39 = (i13 + i40 - 1) / i40) {
				++i40;
			}

			i16 = 300 / i40;
			if(i16 > 150) {
				i16 = 150;
			}

			i17 = (i6 - i40 * i16) / 2;
			byte b43 = 10;
			drawRect(i17 - 1, b43 - 1, i17 + i16 * i40, b43 + 9 * i39, Integer.MIN_VALUE);

			for(i19 = 0; i19 < i13; ++i19) {
				i20 = i17 + i19 % i40 * i16;
				i45 = b43 + i19 / i40 * 9;
				drawRect(i20, i45, i20 + i16 - 1, i45 + 8, 553648127);
				GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
				GL11.glEnable(GL11.GL_ALPHA_TEST);
				if(i19 < list38.size()) {
					GuiPlayerInfo guiPlayerInfo46 = (GuiPlayerInfo)list38.get(i19);
					fontRenderer8.drawStringWithShadow(guiPlayerInfo46.name, i20, i45, 0xFFFFFF);
					this.mc.renderEngine.bindTexture(this.mc.renderEngine.getTexture("/gui/icons.png"));
					byte b47 = 0;
					byte b50;
					if(guiPlayerInfo46.responseTime < 0) {
						b50 = 5;
					} else if(guiPlayerInfo46.responseTime < 150) {
						b50 = 0;
					} else if(guiPlayerInfo46.responseTime < 300) {
						b50 = 1;
					} else if(guiPlayerInfo46.responseTime < 600) {
						b50 = 2;
					} else if(guiPlayerInfo46.responseTime < 1000) {
						b50 = 3;
					} else {
						b50 = 4;
					}

					this.zLevel += 100.0F;
					this.drawTexturedModalRect(i20 + i16 - 12, i45, 0 + b47 * 10, 176 + b50 * 8, 10, 8);
					this.zLevel -= 100.0F;
				}
			}
		}

		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
	}

	private void func_50010_a(FontRenderer fontRenderer1) {
		byte b2 = 10;
		boolean z3 = false;
		int i4 = 0;
		int i5 = this.chatMessageList.size();
		if(i5 > 0) {
			if(this.isChatOpen()) {
				b2 = 20;
				z3 = true;
			}

			int i6;
			int i10;
			for(i6 = 0; i6 + this.firstMessageIdx < this.chatMessageList.size() && i6 < b2; ++i6) {
				if(((ChatLine)this.chatMessageList.get(i6)).updateCounter < 200 || z3) {
					ChatLine chatLine7 = (ChatLine)this.chatMessageList.get(i6 + this.firstMessageIdx);
					double d8 = (double)chatLine7.updateCounter / 200.0D;
					d8 = 1.0D - d8;
					d8 *= 10.0D;
					if(d8 < 0.0D) {
						d8 = 0.0D;
					}

					if(d8 > 1.0D) {
						d8 = 1.0D;
					}

					d8 *= d8;
					i10 = (int)(255.0D * d8);
					if(z3) {
						i10 = 255;
					}

					++i4;
					if(i10 > 2) {
						byte b11 = 3;
						int i12 = -i6 * 9;
						String string13 = chatLine7.message;
						drawRect(b11, i12 - 1, b11 + 320 + 4, i12 + 8, i10 / 2 << 24);
						GL11.glEnable(GL11.GL_BLEND);
						fontRenderer1.drawStringWithShadow(string13, b11, i12, 0xFFFFFF + (i10 << 24));
					}
				}
			}

			if(z3) {
				GL11.glTranslatef(0.0F, (float)fontRenderer1.FONT_HEIGHT, 0.0F);
				i6 = i5 * fontRenderer1.FONT_HEIGHT + i5;
				int i14 = i4 * fontRenderer1.FONT_HEIGHT + i4;
				int i15 = this.firstMessageIdx * i14 / i5;
				int i9 = i14 * i14 / i6;
				if(i6 != i14) {
					i10 = i15 > 0 ? 170 : 96;
					int i16 = this.field_50018_o ? 13382451 : 3355562;
					drawRect(0, -i15, 2, -i15 - i9, i16 + (i10 << 24));
					drawRect(2, -i15, 1, -i15 - i9, 13421772 + (i10 << 24));
				}
			}

		}
	}

	private void renderBossHealth() {

	}

	private void renderPumpkinBlur(int i1, int i2) {
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDepthMask(false);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.mc.renderEngine.getTexture("%blur%/misc/pumpkinblur.png"));
		Tessellator tessellator3 = Tessellator.instance;
		tessellator3.startDrawingQuads();
		tessellator3.addVertexWithUV(0.0D, (double)i2, -90.0D, 0.0D, 1.0D);
		tessellator3.addVertexWithUV((double)i1, (double)i2, -90.0D, 1.0D, 1.0D);
		tessellator3.addVertexWithUV((double)i1, 0.0D, -90.0D, 1.0D, 0.0D);
		tessellator3.addVertexWithUV(0.0D, 0.0D, -90.0D, 0.0D, 0.0D);
		tessellator3.draw();
		GL11.glDepthMask(true);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	}
	
	private void renderVignette(float f1, int i2, int i3) {
		f1 = 1.0F - f1;
		if(f1 < 0.0F) {
			f1 = 0.0F;
		}

		if(f1 > 1.0F) {
			f1 = 1.0F;
		}

		this.prevVignetteBrightness = (float)((double)this.prevVignetteBrightness + (double)(f1 - this.prevVignetteBrightness) * 0.01D);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDepthMask(false);
		GL11.glBlendFunc(GL11.GL_ZERO, GL11.GL_ONE_MINUS_SRC_COLOR);
		GL11.glColor4f(this.prevVignetteBrightness, this.prevVignetteBrightness, this.prevVignetteBrightness, 1.0F);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.mc.renderEngine.getTexture("%blur%/misc/vignette.png"));
		Tessellator tessellator4 = Tessellator.instance;
		tessellator4.startDrawingQuads();
		tessellator4.addVertexWithUV(0.0D, (double)i3, -90.0D, 0.0D, 1.0D);
		tessellator4.addVertexWithUV((double)i2, (double)i3, -90.0D, 1.0D, 1.0D);
		tessellator4.addVertexWithUV((double)i2, 0.0D, -90.0D, 1.0D, 0.0D);
		tessellator4.addVertexWithUV(0.0D, 0.0D, -90.0D, 0.0D, 0.0D);
		tessellator4.draw();
		GL11.glDepthMask(true);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	}

	private void renderInventorySlot(int i1, int i2, int i3, float f4) {
		ItemStack itemStack5 = this.mc.thePlayer.inventory.mainInventory[i1];
		if(itemStack5 != null) {
			float f6 = (float)itemStack5.animationsToGo - f4;
			if(f6 > 0.0F) {
				GL11.glPushMatrix();
				float f7 = 1.0F + f6 / 5.0F;
				GL11.glTranslatef((float)(i2 + 8), (float)(i3 + 12), 0.0F);
				GL11.glScalef(1.0F / f7, (f7 + 1.0F) / 2.0F, 1.0F);
				GL11.glTranslatef((float)(-(i2 + 8)), (float)(-(i3 + 12)), 0.0F);
			}

			itemRenderer.renderItemIntoGUI(this.mc.fontRenderer, this.mc.renderEngine, itemStack5, i2, i3);
			if(f6 > 0.0F) {
				GL11.glPopMatrix();
			}

			itemRenderer.renderItemOverlayIntoGUI(this.mc.fontRenderer, this.mc.renderEngine, itemStack5, i2, i3);
		}
	}

	public void updateTick() {
		if(this.onScreenMessageTimeout > 0) {
			--this.onScreenMessageTimeout;
		}

		++this.updateCounter;

		for(int i1 = 0; i1 < this.chatMessageList.size(); ++i1) {
			++((ChatLine)this.chatMessageList.get(i1)).updateCounter;
		}

	}

	public void clearChatMessages() {
		this.chatMessageList.clear();
		this.messageList.clear();
	}

	public void addChatMessage(String string1) {
		boolean z2 = this.isChatOpen();
		boolean z3 = true;
		Iterator<String> iterator4 = this.mc.fontRenderer.func_50108_c(string1, 320).iterator();

		while(iterator4.hasNext()) {
			String string5 = (String)iterator4.next();
			if(z2 && this.firstMessageIdx > 0) {
				this.field_50018_o = true;
				this.scrollMessages(1);
			}

			if(!z3) {
				string5 = " " + string5;
			}

			z3 = false;
			this.chatMessageList.add(0, new ChatLine(string5));
		}

		while(this.chatMessageList.size() > 100) {
			this.chatMessageList.remove(this.chatMessageList.size() - 1);
		}

	}

	public List<String> getMessageList() {
		return this.messageList;
	}

	public void func_50014_d() {
		this.firstMessageIdx = 0;
		this.field_50018_o = false;
	}

	public void scrollMessages(int i1) {
		this.firstMessageIdx += i1;
		int i2 = this.chatMessageList.size();
		if(this.firstMessageIdx > i2 - 20) {
			this.firstMessageIdx = i2 - 20;
		}

		if(this.firstMessageIdx <= 0) {
			this.firstMessageIdx = 0;
			this.field_50018_o = false;
		}

	}

	public ChatClickData func_50012_a(int i1, int i2) {
		if(!this.isChatOpen()) {
			return null;
		} else {
			ScaledResolution scaledResolution3 = new ScaledResolution(this.mc.displayWidth, this.mc.displayHeight);
			i2 = i2 / scaledResolution3.scaleFactor - 40;
			i1 = i1 / scaledResolution3.scaleFactor - 3;
			if(i1 >= 0 && i2 >= 0) {
				int i4 = Math.min(20, this.chatMessageList.size());
				if(i1 <= 320 && i2 < this.mc.fontRenderer.FONT_HEIGHT * i4 + i4) {
					int i5 = i2 / (this.mc.fontRenderer.FONT_HEIGHT + 1) + this.firstMessageIdx;
					return new ChatClickData(this.mc.fontRenderer, (ChatLine)this.chatMessageList.get(i5), i1, i2 - (i5 - this.firstMessageIdx) * this.mc.fontRenderer.FONT_HEIGHT + i5);
				} else {
					return null;
				}
			} else {
				return null;
			}
		}
	}

	public void setRecordPlayingMessage(String string1) {
		this.onScreenMessage = "Now playing: " + string1;
		this.onScreenMessageTimeout = 60;
		this.fancyText = true;
	}
	
	public void showString (String s) {
		this.onScreenMessage = s;
		this.onScreenMessageTimeout = 60;
		this.fancyText = true;
	}

	public boolean isChatOpen() {
		return this.mc.currentScreen instanceof GuiChat;
	}

	public void addChatMessageTranslate(String string1) {
		StringTranslate stringTranslate2 = StringTranslate.getInstance();
		String string3 = stringTranslate2.translateKey(string1);
		this.addChatMessage(string3);
	}
}
