package net.minecraft.src;

public class GuiIngameMenu extends GuiScreen {
	private int updateCounter2 = 0;
	private int updateCounter = 0;

	public void initGui() {
		this.updateCounter2 = 0;
		this.controlList.clear();
		byte byte0 = -16;
		this.controlList.add(new GuiButton(1, this.width / 2 - 100, this.height / 4 + 120 + byte0, "Save and quit to title"));
		if(this.mc.isMultiplayerWorld()) {
			((GuiButton)this.controlList.get(0)).displayString = "Disconnect";
		}

		this.controlList.add(new GuiButton(4, this.width / 2 - 100, this.height / 4 + 24 + byte0, "Back to game"));
		this.controlList.add(new GuiButton(0, this.width / 2 - 100, this.height / 4 + 96 + byte0, "Options..."));
		this.controlList.add(new GuiButton(5, this.width / 2 - 100, this.height / 4 + 48 + byte0, 98, 20, StatCollector.translateToLocal("gui.achievements")));
		this.controlList.add(new GuiButton(6, this.width / 2 + 2, this.height / 4 + 48 + byte0, 98, 20, StatCollector.translateToLocal("gui.stats")));
		this.controlList.add(new GuiButton(15, this.width / 2 - 100, this.height / 4 + byte0 + 144, "World Mod Options"));
	}

	protected void actionPerformed(GuiButton ka1) {
		if(ka1.id == 0) {
			this.mc.displayGuiScreen(new GuiOptions(this, this.mc.gameSettings));
		}

		if(ka1.id == 1) {
			this.mc.statFileWriter.func_25100_a(StatList.leaveGameStat, 1);
			if(this.mc.isMultiplayerWorld()) {
				this.mc.theWorld.sendQuittingDisconnectingPacket();
			}

			this.mc.changeWorld1((World)null);
			this.mc.displayGuiScreen(new GuiMainMenu());
		}

		if(ka1.id == 4) {
			this.mc.displayGuiScreen((GuiScreen)null);
			this.mc.setIngameFocus();
		}

		if(ka1.id == 5) {
			this.mc.displayGuiScreen(new GuiAchievements(this.mc.statFileWriter));
		}

		if(ka1.id == 6) {
			this.mc.displayGuiScreen(new GuiStats(this, this.mc.statFileWriter));
		}

		if(ka1.id == 15) {
			ModSettingScreen.guicontext = ModSettings.currentContext;
			WidgetSetting.updateAll();
			GuiModScreen.show((GuiModScreen)(new GuiModSelect(this)));
		}

	}

	public void updateScreen() {
		super.updateScreen();
		++this.updateCounter;
	}

	public void drawScreen(int j, int k, float f) {
		this.drawDefaultBackground();
		boolean flag = !this.mc.theWorld.func_650_a(this.updateCounter2++);
		if(flag || this.updateCounter < 20) {
			float f1 = ((float)(this.updateCounter % 10) + f) / 10.0F;
			f1 = MathHelper.sin(f1 * 3.141593F * 2.0F) * 0.2F + 0.8F;
			int l = (int)(255.0F * f1);
			this.drawString(this.fontRenderer, "Saving level..", 8, this.height - 16, l << 16 | l << 8 | l);
		}

		this.drawCenteredString(this.fontRenderer, "Game menu", this.width / 2, 40, 0xFFFFFF);
		super.drawScreen(j, k, f);
	}
}
