package net.minecraft.client.gui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeMap;

import net.minecraft.client.GameSettingsValues;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.StringTranslate;

class GuiSlotLanguage extends GuiSlot {
	private ArrayList<String> languageIds;
	private TreeMap<String,String> languageList;
	final GuiLanguage guiLanguage;

	public GuiSlotLanguage(GuiLanguage guiLanguage1) {
		super(guiLanguage1.mc, guiLanguage1.width, guiLanguage1.height, 32, guiLanguage1.height - 65 + 4, 18);
		this.guiLanguage = guiLanguage1;
		this.languageList = StringTranslate.getInstance().getLanguageList();
		this.languageIds = new ArrayList<String>();
		Iterator<String> iterator2 = this.languageList.keySet().iterator();

		while(iterator2.hasNext()) {
			String string3 = (String)iterator2.next();
			this.languageIds.add(string3);
		}

	}

	protected int getSize() {
		return this.languageIds.size();
	}

	protected void elementClicked(int i1, boolean z2) {
		StringTranslate.getInstance().setLanguage((String)this.languageIds.get(i1));
		this.guiLanguage.mc.fontRenderer.setUnicodeFlag(StringTranslate.getInstance().isUnicode());
		GameSettingsValues.language = (String)this.languageIds.get(i1);
		this.guiLanguage.fontRenderer.setBidiFlag(StringTranslate.isBidrectional(GameSettingsValues.language));
		GuiLanguage.getDoneButton(this.guiLanguage).displayString = StringTranslate.getInstance().translateKey("gui.done");
	}

	protected boolean isSelected(int i1) {
		return ((String)this.languageIds.get(i1)).equals(StringTranslate.getInstance().getCurrentLanguage());
	}

	protected int getContentHeight() {
		return this.getSize() * 18;
	}

	protected void drawBackground() {
		this.guiLanguage.drawDefaultBackground();
	}

	protected void drawSlot(int i1, int i2, int i3, int i4, Tessellator tessellator5) {
		this.guiLanguage.fontRenderer.setBidiFlag(true);
		this.guiLanguage.drawCenteredString(this.guiLanguage.fontRenderer, (String)this.languageList.get(this.languageIds.get(i1)), this.guiLanguage.width / 2, i3 + 1, 0xFFFFFF);
		this.guiLanguage.fontRenderer.setBidiFlag(StringTranslate.isBidrectional(GameSettingsValues.language));
	}
}
