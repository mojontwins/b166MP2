package net.minecraft.client.gui;

import net.minecraft.client.title.GuiMainMenu;
import net.minecraft.src.MathHelper;
import net.minecraft.util.Translator;
import net.minecraft.world.level.World;

public class GuiIngameMenu extends GuiScreen {
	private int updateCounter2 = 0;

	public void initGui() {
		this.updateCounter2 = 0;
		this.controlList.clear();
		byte b1 = -16;
		this.controlList.add(new GuiButton(1, this.width / 2 - 100, this.height / 4 + 120 + b1, Translator.translateToLocal("menu.returnToMenu")));
		if(this.mc.isMultiplayerWorld()) {
			((GuiButton)this.controlList.get(0)).displayString = Translator.translateToLocal("menu.disconnect");
		}

		this.controlList.add(new GuiButton(4, this.width / 2 - 100, this.height / 4 + 24 + b1, Translator.translateToLocal("menu.returnToGame")));
		this.controlList.add(new GuiButton(0, this.width / 2 - 100, this.height / 4 + 96 + b1, Translator.translateToLocal("menu.options")));
		//this.controlList.add(new GuiButton(5, this.width / 2 - 100, this.height / 4 + 48 + b1, 98, 20, Translator.translateToLocal("gui.achievements")));
		//this.controlList.add(new GuiButton(6, this.width / 2 + 2, this.height / 4 + 48 + b1, 98, 20, Translator.translateToLocal("gui.stats")));
		
		//this.controlList.add(new GuiButton(100, this.width / 2 - 100, this.height / 4 + 48 + b1, "Important game rules"));
	}

	protected void actionPerformed(GuiButton guiButton1) {
		switch(guiButton1.id) {
		case 0:
			this.mc.displayGuiScreen(new GuiOptions(this, this.mc.gameSettings));
			break;
		case 1:
			if(this.mc.isMultiplayerWorld()) {
				this.mc.theWorld.sendQuittingDisconnectingPacket();
			}

			this.mc.changeWorld1((World)null);
			this.mc.displayGuiScreen(new GuiMainMenu());
		case 2:
		case 3:
		default:
			break;
		case 4:
			this.mc.displayGuiScreen((GuiScreen)null);
			this.mc.setIngameFocus();
			break;
		case 5:
			//this.mc.displayGuiScreen(new GuiAchievements(this.mc.statFileWriter));
			break;
		case 6:
			//this.mc.displayGuiScreen(new GuiStats(this, this.mc.statFileWriter));
		case 100:
			this.mc.displayGuiScreen(new GuiGameRules(this, true));
			break;
		}

	}

	public void updateScreen() {
		super.updateScreen();
		++this.updateCounter;
	}

	public void drawScreen(int i1, int i2, float f3) {
		this.drawDefaultBackground();
		boolean z4 = !this.mc.theWorld.quickSaveWorld(this.updateCounter2++);
		if(z4 || this.updateCounter < 20) {
			float f5 = ((float)(this.updateCounter % 10) + f3) / 10.0F;
			f5 = MathHelper.sin(f5 * (float)Math.PI * 2.0F) * 0.2F + 0.8F;
			int i6 = (int)(255.0F * f5);
			this.drawString(this.fontRenderer, "Saving level..", 8, this.height - 16, i6 << 16 | i6 << 8 | i6);
		}

		this.drawCenteredString(this.fontRenderer, "Game menu", this.width / 2, 40, 0xFFFFFF);
		super.drawScreen(i1, i2, f3);
	}
}
