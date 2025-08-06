package net.minecraft.client.gui;

class GuiChatConfirmLink extends GuiConfirmOpenLink {
	final ChatClickData field_50056_a;
	final GuiChat field_50055_b;

	GuiChatConfirmLink(GuiChat guiChat1, GuiScreen guiScreen2, String string3, int i4, ChatClickData chatClickData5) {
		super(guiScreen2, string3, i4);
		this.field_50055_b = guiChat1;
		this.field_50056_a = chatClickData5;
	}

	public void func_50052_d() {
		func_50050_a(this.field_50056_a.func_50088_a());
	}
}
