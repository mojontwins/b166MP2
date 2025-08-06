package net.minecraft.client.gui;

import net.minecraft.util.StringTranslate;

public abstract class GuiConfirmOpenLink extends GuiYesNo {
	private String field_50054_a;
	private String field_50053_b;

	public GuiConfirmOpenLink(GuiScreen guiScreen1, String string2, int i3) {
		super(guiScreen1, StringTranslate.getInstance().translateKey("chat.link.confirm"), string2, i3);
		StringTranslate stringTranslate4 = StringTranslate.getInstance();
		this.buttonText1 = stringTranslate4.translateKey("gui.yes");
		this.buttonText2 = stringTranslate4.translateKey("gui.no");
		this.field_50053_b = stringTranslate4.translateKey("chat.copy");
		this.field_50054_a = stringTranslate4.translateKey("chat.link.warning");
	}

	public void initGui() {
		this.controlList.add(new GuiButton(0, this.width / 3 - 83 + 0, this.height / 6 + 96, 100, 20, this.buttonText1));
		this.controlList.add(new GuiButton(2, this.width / 3 - 83 + 105, this.height / 6 + 96, 100, 20, this.field_50053_b));
		this.controlList.add(new GuiButton(1, this.width / 3 - 83 + 210, this.height / 6 + 96, 100, 20, this.buttonText2));
	}

	protected void actionPerformed(GuiButton guiButton1) {
		if(guiButton1.id == 2) {
			this.func_50052_d();
			super.actionPerformed((GuiButton)this.controlList.get(1));
		} else {
			super.actionPerformed(guiButton1);
		}

	}

	public abstract void func_50052_d();

	public void drawScreen(int i1, int i2, float f3) {
		super.drawScreen(i1, i2, f3);
		this.drawCenteredString(this.fontRenderer, this.field_50054_a, this.width / 2, 110, 16764108);
	}
}
