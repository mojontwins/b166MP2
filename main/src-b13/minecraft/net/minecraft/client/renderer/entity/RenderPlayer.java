package net.minecraft.client.renderer.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.player.EntityPlayerSP;
import net.minecraft.client.renderer.FontRenderer;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.src.MathHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EnumAction;
import net.minecraft.world.entity.IArmorTextureProvider;
import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemArmor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.tile.Block;

import org.lwjgl.opengl.GL11;

public class RenderPlayer extends RenderLiving {
	private ModelBiped modelBipedMain = (ModelBiped)this.mainModel;
	private ModelBiped modelArmorChestplate = new ModelBiped(1.0F);
	private ModelBiped modelArmor = new ModelBiped(0.5F);
	public static final String[] armorFilenamePrefix = new String[]{
			"cloth", 
			"chain",
			"iron", 
			"diamond", 
			"gold"
		};

	public RenderPlayer() {
		super(new ModelBiped(0.0F), 0.5F);
	}

	public static String getArmorTexture(ItemStack itemStack, int renderIndex, int part) {
		if(itemStack.getItem() instanceof IArmorTextureProvider) {
			return ((IArmorTextureProvider)(itemStack.getItem())).getArmorTextureFile(itemStack);
		} else {
			return "/armor/" + armorFilenamePrefix[renderIndex] + "_" + (part == 2 ? 2 : 1) + ".png";
		}
	}
	
	protected int setArmorModel(EntityPlayer thePlayer, int part, float f3) {
		ItemStack itemStack = thePlayer.inventory.armorItemInSlot(3 - part);
		if(itemStack != null) {
			Item item = itemStack.getItem();
			if(item instanceof ItemArmor) {
				ItemArmor itemArmor6 = (ItemArmor)item;
				this.loadTexture(getArmorTexture(itemStack, itemArmor6.renderIndex, part));
				ModelBiped modelBiped7 = part == 2 ? this.modelArmor : this.modelArmorChestplate;
				modelBiped7.bipedHead.showModel = part == 0;
				modelBiped7.bipedHeadwear.showModel = part == 0;
				modelBiped7.bipedBody.showModel = part == 1 || part == 2;
				modelBiped7.bipedRightArm.showModel = part == 1;
				modelBiped7.bipedLeftArm.showModel = part == 1;
				modelBiped7.bipedRightLeg.showModel = part == 2 || part == 3;
				modelBiped7.bipedLeftLeg.showModel = part == 2 || part == 3;
				this.setRenderPassModel(modelBiped7);
				if(itemStack.isItemEnchanted()) {
					return 15;
				}

				return 1;
			}
		}

		return -1;
	}

	public void renderPlayer(EntityPlayer entityPlayer1, double d2, double d4, double d6, float f8, float f9) {
		ItemStack itemStack10 = entityPlayer1.inventory.getCurrentItem();
		this.modelArmorChestplate.heldItemRight = this.modelArmor.heldItemRight = this.modelBipedMain.heldItemRight = itemStack10 != null ? 1 : 0;
		if(itemStack10 != null && entityPlayer1.getItemInUseCount() > 0) {
			EnumAction enumAction11 = itemStack10.getItemUseAction();
			if(enumAction11 == EnumAction.block) {
				this.modelArmorChestplate.heldItemRight = this.modelArmor.heldItemRight = this.modelBipedMain.heldItemRight = 3;
			} else if(enumAction11 == EnumAction.bow) {
				this.modelArmorChestplate.aimedBow = this.modelArmor.aimedBow = this.modelBipedMain.aimedBow = true;
			}
		}

		this.modelArmorChestplate.isSneak = this.modelArmor.isSneak = this.modelBipedMain.isSneak = entityPlayer1.isSneaking();
		double d13 = d4 - (double)entityPlayer1.yOffset;
		if(entityPlayer1.isSneaking() && !(entityPlayer1 instanceof EntityPlayerSP)) {
			d13 -= 0.125D;
		}

		super.doRenderLiving(entityPlayer1, d2, d13, d6, f8, f9);
		this.modelArmorChestplate.aimedBow = this.modelArmor.aimedBow = this.modelBipedMain.aimedBow = false;
		this.modelArmorChestplate.isSneak = this.modelArmor.isSneak = this.modelBipedMain.isSneak = false;
		this.modelArmorChestplate.heldItemRight = this.modelArmor.heldItemRight = this.modelBipedMain.heldItemRight = 0;
	}

	protected void renderName(EntityPlayer entityPlayer1, double d2, double d4, double d6) {
		if(Minecraft.isGuiEnabled() && entityPlayer1 != this.renderManager.livingPlayer) {
			float f8 = 1.6F;
			float f9 = 0.016666668F * f8;
			float f10 = entityPlayer1.getDistanceToEntity(this.renderManager.livingPlayer);
			float f11 = entityPlayer1.isSneaking() ? 32.0F : 64.0F;
			if(f10 < f11) {
				String string12 = entityPlayer1.username;
				if(!entityPlayer1.isSneaking()) {
					if(entityPlayer1.isPlayerSleeping()) {
						this.renderLivingLabel(entityPlayer1, string12, d2, d4 - 1.5D, d6, 64);
					} else {
						this.renderLivingLabel(entityPlayer1, string12, d2, d4, d6, 64);
					}
				} else {
					FontRenderer fontRenderer13 = this.getFontRendererFromRenderManager();
					GL11.glPushMatrix();
					GL11.glTranslatef((float)d2 + 0.0F, (float)d4 + 2.3F, (float)d6);
					GL11.glNormal3f(0.0F, 1.0F, 0.0F);
					GL11.glRotatef(-this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
					GL11.glRotatef(this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
					GL11.glScalef(-f9, -f9, f9);
					GL11.glDisable(GL11.GL_LIGHTING);
					GL11.glTranslatef(0.0F, 0.25F / f9, 0.0F);
					GL11.glDepthMask(false);
					GL11.glEnable(GL11.GL_BLEND);
					GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
					Tessellator tessellator14 = Tessellator.instance;
					GL11.glDisable(GL11.GL_TEXTURE_2D);
					tessellator14.startDrawingQuads();
					int i15 = fontRenderer13.getStringWidth(string12) / 2;
					tessellator14.setColorRGBA_F(0.0F, 0.0F, 0.0F, 0.25F);
					tessellator14.addVertex((double)(-i15 - 1), -1.0D, 0.0D);
					tessellator14.addVertex((double)(-i15 - 1), 8.0D, 0.0D);
					tessellator14.addVertex((double)(i15 + 1), 8.0D, 0.0D);
					tessellator14.addVertex((double)(i15 + 1), -1.0D, 0.0D);
					tessellator14.draw();
					GL11.glEnable(GL11.GL_TEXTURE_2D);
					GL11.glDepthMask(true);
					fontRenderer13.drawString(string12, -fontRenderer13.getStringWidth(string12) / 2, 0, 553648127);
					GL11.glEnable(GL11.GL_LIGHTING);
					GL11.glDisable(GL11.GL_BLEND);
					GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
					GL11.glPopMatrix();
				}
			}
		}

	}

	protected void renderSpecials(EntityPlayer entityPlayer1, float f2) {
		super.renderEquippedItems(entityPlayer1, f2);
		ItemStack itemStack3 = entityPlayer1.inventory.armorItemInSlot(3);
		if(itemStack3 != null && itemStack3.getItem().shiftedIndex < 256) {
			GL11.glPushMatrix();
			this.modelBipedMain.bipedHead.postRender(0.0625F);
			if(RenderBlocks.renderItemIn3d(Block.blocksList[itemStack3.itemID].getRenderType())) {
				float f4 = 0.625F;
				GL11.glTranslatef(0.0F, -0.25F, 0.0F);
				GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
				GL11.glScalef(f4, -f4, f4);
			}

			this.renderManager.itemRenderer.renderItem(entityPlayer1, itemStack3, 0);
			GL11.glPopMatrix();
		}

		float f6;
		if(entityPlayer1.username.equals("deadmau5") && this.loadDownloadableImageTexture(entityPlayer1.skinUrl, (String)null)) {
			for(int i19 = 0; i19 < 2; ++i19) {
				float f5 = entityPlayer1.prevRotationYaw + (entityPlayer1.rotationYaw - entityPlayer1.prevRotationYaw) * f2 - (entityPlayer1.prevRenderYawOffset + (entityPlayer1.renderYawOffset - entityPlayer1.prevRenderYawOffset) * f2);
				f6 = entityPlayer1.prevRotationPitch + (entityPlayer1.rotationPitch - entityPlayer1.prevRotationPitch) * f2;
				GL11.glPushMatrix();
				GL11.glRotatef(f5, 0.0F, 1.0F, 0.0F);
				GL11.glRotatef(f6, 1.0F, 0.0F, 0.0F);
				GL11.glTranslatef(0.375F * (float)(i19 * 2 - 1), 0.0F, 0.0F);
				GL11.glTranslatef(0.0F, -0.375F, 0.0F);
				GL11.glRotatef(-f6, 1.0F, 0.0F, 0.0F);
				GL11.glRotatef(-f5, 0.0F, 1.0F, 0.0F);
				float f7 = 1.3333334F;
				GL11.glScalef(f7, f7, f7);
				this.modelBipedMain.renderEars(0.0625F);
				GL11.glPopMatrix();
			}
		}

		float f10;
		if(this.loadDownloadableImageTexture(entityPlayer1.playerCloakUrl, (String)null)) {
			GL11.glPushMatrix();
			GL11.glTranslatef(0.0F, 0.0F, 0.125F);
			double d20 = entityPlayer1.field_20066_r + (entityPlayer1.field_20063_u - entityPlayer1.field_20066_r) * (double)f2 - (entityPlayer1.prevPosX + (entityPlayer1.posX - entityPlayer1.prevPosX) * (double)f2);
			double d23 = entityPlayer1.field_20065_s + (entityPlayer1.field_20062_v - entityPlayer1.field_20065_s) * (double)f2 - (entityPlayer1.prevPosY + (entityPlayer1.posY - entityPlayer1.prevPosY) * (double)f2);
			double d8 = entityPlayer1.field_20064_t + (entityPlayer1.field_20061_w - entityPlayer1.field_20064_t) * (double)f2 - (entityPlayer1.prevPosZ + (entityPlayer1.posZ - entityPlayer1.prevPosZ) * (double)f2);
			f10 = entityPlayer1.prevRenderYawOffset + (entityPlayer1.renderYawOffset - entityPlayer1.prevRenderYawOffset) * f2;
			double d11 = (double)MathHelper.sin(f10 * (float)Math.PI / 180.0F);
			double d13 = (double)(-MathHelper.cos(f10 * (float)Math.PI / 180.0F));
			float f15 = (float)d23 * 10.0F;
			if(f15 < -6.0F) {
				f15 = -6.0F;
			}

			if(f15 > 32.0F) {
				f15 = 32.0F;
			}

			float f16 = (float)(d20 * d11 + d8 * d13) * 100.0F;
			float f17 = (float)(d20 * d13 - d8 * d11) * 100.0F;
			if(f16 < 0.0F) {
				f16 = 0.0F;
			}

			float f18 = entityPlayer1.prevCameraYaw + (entityPlayer1.cameraYaw - entityPlayer1.prevCameraYaw) * f2;
			f15 += MathHelper.sin((entityPlayer1.prevDistanceWalkedModified + (entityPlayer1.distanceWalkedModified - entityPlayer1.prevDistanceWalkedModified) * f2) * 6.0F) * 32.0F * f18;
			if(entityPlayer1.isSneaking()) {
				f15 += 25.0F;
			}

			GL11.glRotatef(6.0F + f16 / 2.0F + f15, 1.0F, 0.0F, 0.0F);
			GL11.glRotatef(f17 / 2.0F, 0.0F, 0.0F, 1.0F);
			GL11.glRotatef(-f17 / 2.0F, 0.0F, 1.0F, 0.0F);
			GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
			this.modelBipedMain.renderCloak(0.0625F);
			GL11.glPopMatrix();
		}

		ItemStack itemStack21 = entityPlayer1.inventory.getCurrentItem();
		if(itemStack21 != null) {
			GL11.glPushMatrix();
			this.modelBipedMain.bipedRightArm.postRender(0.0625F);
			GL11.glTranslatef(-0.0625F, 0.4375F, 0.0625F);
			if(entityPlayer1.fishEntity != null) {
				itemStack21 = new ItemStack(Item.stick);
			}

			EnumAction enumAction22 = null;
			if(entityPlayer1.getItemInUseCount() > 0) {
				enumAction22 = itemStack21.getItemUseAction();
			}

			if(itemStack21.itemID < 256 && RenderBlocks.renderItemIn3d(Block.blocksList[itemStack21.itemID].getRenderType())) {
				f6 = 0.5F;
				GL11.glTranslatef(0.0F, 0.1875F, -0.3125F);
				f6 *= 0.75F;
				GL11.glRotatef(20.0F, 1.0F, 0.0F, 0.0F);
				GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
				GL11.glScalef(f6, -f6, f6);
			} else if(itemStack21.itemID == Item.bow.shiftedIndex) {
				f6 = 0.625F;
				GL11.glTranslatef(0.0F, 0.125F, 0.3125F);
				GL11.glRotatef(-20.0F, 0.0F, 1.0F, 0.0F);
				GL11.glScalef(f6, -f6, f6);
				GL11.glRotatef(-100.0F, 1.0F, 0.0F, 0.0F);
				GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
			} else if(Item.itemsList[itemStack21.itemID].isFull3D()) {
				f6 = 0.625F;
				if(Item.itemsList[itemStack21.itemID].shouldRotateAroundWhenRendering()) {
					GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
					GL11.glTranslatef(0.0F, -0.125F, 0.0F);
				}

				if(entityPlayer1.getItemInUseCount() > 0 && enumAction22 == EnumAction.block) {
					GL11.glTranslatef(0.05F, 0.0F, -0.1F);
					GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
					GL11.glRotatef(-10.0F, 1.0F, 0.0F, 0.0F);
					GL11.glRotatef(-60.0F, 0.0F, 0.0F, 1.0F);
				}

				GL11.glTranslatef(0.0F, 0.1875F, 0.0F);
				GL11.glScalef(f6, -f6, f6);
				GL11.glRotatef(-100.0F, 1.0F, 0.0F, 0.0F);
				GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
			} else {
				f6 = 0.375F;
				GL11.glTranslatef(0.25F, 0.1875F, -0.1875F);
				GL11.glScalef(f6, f6, f6);
				GL11.glRotatef(60.0F, 0.0F, 0.0F, 1.0F);
				GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
				GL11.glRotatef(20.0F, 0.0F, 0.0F, 1.0F);
			}

			if(itemStack21.getItem().requiresMultipleRenderPasses()) {
				for(int i25 = 0; i25 <= 1; ++i25) {
					int i24 = itemStack21.getItem().getColorFromDamage(itemStack21.getItemDamage(), i25);
					float f26 = (float)(i24 >> 16 & 255) / 255.0F;
					float f9 = (float)(i24 >> 8 & 255) / 255.0F;
					f10 = (float)(i24 & 255) / 255.0F;
					GL11.glColor4f(f26, f9, f10, 1.0F);
					this.renderManager.itemRenderer.renderItem(entityPlayer1, itemStack21, i25);
				}
			} else {
				this.renderManager.itemRenderer.renderItem(entityPlayer1, itemStack21, 0);
			}

			GL11.glPopMatrix();
		}

	}

	protected void renderPlayerScale(EntityPlayer entityPlayer1, float f2) {
		float f3 = 0.9375F;
		GL11.glScalef(f3, f3, f3);
	}

	public void drawFirstPersonHand() {
		this.modelBipedMain.onGround = 0.0F;
		this.modelBipedMain.setRotationAngles(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
		this.modelBipedMain.bipedRightArm.render(0.0625F);
	}

	protected void renderPlayerSleep(EntityPlayer entityPlayer1, double d2, double d4, double d6) {
		if(entityPlayer1.isEntityAlive() && entityPlayer1.isPlayerSleeping()) {
			super.renderLivingAt(entityPlayer1, d2 + (double)entityPlayer1.field_22063_x, d4 + (double)entityPlayer1.field_22062_y, d6 + (double)entityPlayer1.field_22061_z);
		} else {
			super.renderLivingAt(entityPlayer1, d2, d4, d6);
		}

	}

	protected void rotatePlayer(EntityPlayer entityPlayer1, float f2, float f3, float f4) {
		if(entityPlayer1.isEntityAlive() && entityPlayer1.isPlayerSleeping()) {
			GL11.glRotatef(entityPlayer1.getBedOrientationInDegrees(), 0.0F, 1.0F, 0.0F);
			GL11.glRotatef(this.getDeathMaxRotation(entityPlayer1), 0.0F, 0.0F, 1.0F);
			GL11.glRotatef(270.0F, 0.0F, 1.0F, 0.0F);
		} else {
			super.rotateCorpse(entityPlayer1, f2, f3, f4);
		}

	}

	protected void passSpecialRender(EntityLiving entityLiving1, double d2, double d4, double d6) {
		this.renderName((EntityPlayer)entityLiving1, d2, d4, d6);
	}

	protected void preRenderCallback(EntityLiving entityLiving1, float f2) {
		this.renderPlayerScale((EntityPlayer)entityLiving1, f2);
	}

	protected int shouldRenderPass(EntityLiving entityLiving1, int i2, float f3) {
		return this.setArmorModel((EntityPlayer)entityLiving1, i2, f3);
	}

	protected void renderEquippedItems(EntityLiving entityLiving1, float f2) {
		this.renderSpecials((EntityPlayer)entityLiving1, f2);
	}

	protected void rotateCorpse(EntityLiving entityLiving1, float f2, float f3, float f4) {
		this.rotatePlayer((EntityPlayer)entityLiving1, f2, f3, f4);
	}

	protected void renderLivingAt(EntityLiving entityLiving1, double d2, double d4, double d6) {
		this.renderPlayerSleep((EntityPlayer)entityLiving1, d2, d4, d6);
	}

	public void doRenderLiving(EntityLiving entityLiving1, double d2, double d4, double d6, float f8, float f9) {
		this.renderPlayer((EntityPlayer)entityLiving1, d2, d4, d6, f8, f9);
	}

	public void doRender(Entity entity1, double d2, double d4, double d6, float f8, float f9) {
		this.renderPlayer((EntityPlayer)entity1, d2, d4, d6, f8, f9);
	}
}
