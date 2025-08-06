package net.minecraft.client.gui;

import java.util.Iterator;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.title.GuiMainMenu;
import net.minecraft.util.Translator;
import net.minecraft.world.level.World;
import net.minecraft.world.level.chunk.storage.ISaveFormat;

public class GuiGameOver extends GuiScreen {
	private int cooldownTimer;

	public void initGui() {
		this.controlList.clear();
		if(this.mc.theWorld.getWorldInfo().isHardcoreModeEnabled()) {
			this.controlList.add(new GuiButton(1, this.width / 2 - 100, this.height / 4 + 96, Translator.translateToLocal("deathScreen.deleteWorld")));
		} else {
			this.controlList.add(new GuiButton(1, this.width / 2 - 100, this.height / 4 + 72, Translator.translateToLocal("deathScreen.respawn")));
			this.controlList.add(new GuiButton(2, this.width / 2 - 100, this.height / 4 + 96, Translator.translateToLocal("deathScreen.titleScreen")));
			if(this.mc.session == null) {
				((GuiButton)this.controlList.get(1)).enabled = false;
			}
		}

		GuiButton guiButton2;
		for(Iterator<GuiButton> iterator1 = this.controlList.iterator(); iterator1.hasNext(); guiButton2.enabled = false) {
			guiButton2 = (GuiButton)iterator1.next();
		}

	}

	protected void keyTyped(char c1, int i2) {
	}

	protected void actionPerformed(GuiButton guiButton1) {
		switch(guiButton1.id) {
		case 1:
			if(this.mc.theWorld.getWorldInfo().isHardcoreModeEnabled()) {
				String string2 = this.mc.theWorld.getSaveHandler().getSaveDirectoryName();
				this.mc.exitToMainMenu("Deleting world");
				ISaveFormat iSaveFormat3 = this.mc.getSaveLoader();
				iSaveFormat3.flushCache();
				iSaveFormat3.deleteWorldDirectory(string2);
				this.mc.displayGuiScreen(new GuiMainMenu());
			} else {
				this.mc.thePlayer.respawnPlayer();
				this.mc.displayGuiScreen((GuiScreen)null);
			}
			break;
		case 2:
			if(this.mc.isMultiplayerWorld()) {
				this.mc.theWorld.sendQuittingDisconnectingPacket();
			}

			this.mc.changeWorld1((World)null);
			this.mc.displayGuiScreen(new GuiMainMenu());
		}

	}

	public void drawScreen(int i1, int i2, float f3) {
		this.drawGradientRect(0, 0, this.width, this.height, 1615855616, -1602211792);
		GL11.glPushMatrix();
		GL11.glScalef(2.0F, 2.0F, 2.0F);
		boolean z4 = this.mc.theWorld.getWorldInfo().isHardcoreModeEnabled();
		String string5 = z4 ? Translator.translateToLocal("deathScreen.title.hardcore") : Translator.translateToLocal("deathScreen.title");
		this.drawCenteredString(this.fontRenderer, string5, this.width / 2 / 2, 30, 0xFFFFFF);
		GL11.glPopMatrix();
		if(z4) {
			this.drawCenteredString(this.fontRenderer, Translator.translateToLocal("deathScreen.hardcoreInfo"), this.width / 2, 144, 0xFFFFFF);
		}

		this.drawCenteredString(this.fontRenderer, Translator.translateToLocal("deathScreen.score") + ": \u00a7e" + this.mc.thePlayer.getScore(), this.width / 2, 100, 0xFFFFFF);
		super.drawScreen(i1, i2, f3);
	}

	public boolean doesGuiPauseGame() {
		return false;
	}

	public void updateScreen() {
		super.updateScreen();
		++this.cooldownTimer;
		GuiButton guiButton2;
		if(this.cooldownTimer == 20) {
			for(Iterator<GuiButton> iterator1 = this.controlList.iterator(); iterator1.hasNext(); guiButton2.enabled = true) {
				guiButton2 = (GuiButton)iterator1.next();
			}
		}

	}
}
