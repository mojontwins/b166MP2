package net.minecraft.client.gui.inventory;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.Translator;
import net.minecraft.world.entity.player.EntityPlayer;

public class GuiInventory extends GuiContainer {
	private float xSize_lo;
	private float ySize_lo;

	public GuiInventory(EntityPlayer entityPlayer1) {
		super(entityPlayer1.inventorySlots);
		this.allowUserInput = true;
	}

	public void updateScreen() {
		if (this.mc.playerController.isInCreativeMode()) {
			this.mc.displayGuiScreen(new GuiContainerCreative(this.mc.thePlayer));
		}

	}

	public void initGui() {
		this.controlList.clear();
		if (this.mc.playerController.isInCreativeMode()) {
			this.mc.displayGuiScreen(new GuiContainerCreative(this.mc.thePlayer));
		} else {
			super.initGui();

		}
	}

	protected void drawGuiContainerForegroundLayer() {
		this.fontRenderer.drawString(Translator.translateToLocal("container.crafting"), 86, 16, 4210752);
	}

	public void drawScreen(int i1, int i2, float f3) {
		super.drawScreen(i1, i2, f3);
		this.xSize_lo = (float) i1;
		this.ySize_lo = (float) i2;
	}

	protected void drawGuiContainerBackgroundLayer(float f1, int i2, int i3) {
		int i4 = this.mc.renderEngine.getTexture("/gui/inventory.png");
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.renderEngine.bindTexture(i4);
		int i5 = this.guiLeft;
		int i6 = this.guiTop;
		this.drawTexturedModalRect(i5, i6, 0, 0, this.xSize, this.ySize);
		drawGuyInGui(
				this.mc, 
				i5 + 51, i6 + 75, 30, 
				(float) (i5 + 51) - this.xSize_lo,
				(float) (i6 + 75 - 50) - this.ySize_lo
			);
	}

	public static void drawGuyInGui(Minecraft par0Minecraft, int par1, int par2, int par3, float par4, float par5) {
		GL11.glEnable(GL11.GL_COLOR_MATERIAL);
		GL11.glPushMatrix();
		GL11.glTranslatef((float) par1, (float) par2, 50.0F);
		GL11.glScalef((float) (-par3), (float) par3, (float) par3);
		GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
		float var6 = par0Minecraft.thePlayer.renderYawOffset;
		float var7 = par0Minecraft.thePlayer.rotationYaw;
		float var8 = par0Minecraft.thePlayer.rotationPitch;
		GL11.glRotatef(135.0F, 0.0F, 1.0F, 0.0F);
		RenderHelper.enableStandardItemLighting();
		GL11.glRotatef(-135.0F, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(-((float) Math.atan((double) (par5 / 40.0F))) * 20.0F, 1.0F, 0.0F, 0.0F);
		par0Minecraft.thePlayer.renderYawOffset = (float) Math.atan((double) (par4 / 40.0F)) * 20.0F;
		par0Minecraft.thePlayer.rotationYaw = (float) Math.atan((double) (par4 / 40.0F)) * 40.0F;
		par0Minecraft.thePlayer.rotationPitch = -((float) Math.atan((double) (par5 / 40.0F))) * 20.0F;
		par0Minecraft.thePlayer.rotationYawHead = par0Minecraft.thePlayer.rotationYaw;
		GL11.glTranslatef(0.0F, par0Minecraft.thePlayer.yOffset, 0.0F);
		RenderManager.instance.playerViewY = 180.0F;
		RenderManager.instance.renderEntityWithPosYaw(par0Minecraft.thePlayer, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F);
		par0Minecraft.thePlayer.renderYawOffset = var6;
		par0Minecraft.thePlayer.rotationYaw = var7;
		par0Minecraft.thePlayer.rotationPitch = var8;
		GL11.glPopMatrix();
		RenderHelper.disableStandardItemLighting();
		GL11.glDisable(GL12.GL_RESCALE_NORMAL);

		// This is new in r1.5.2 and solves the previous issue
		OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);

	}

	protected void actionPerformed(GuiButton guiButton1) {

	}

}
