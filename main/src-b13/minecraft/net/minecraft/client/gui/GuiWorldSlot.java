package net.minecraft.client.gui;

import java.util.Date;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.src.MathHelper;
import net.minecraft.util.Translator;
import net.minecraft.world.level.chunk.storage.SaveFormatComparator;

class GuiWorldSlot extends GuiSlot {
	final GuiSelectWorld parentWorldGui;

	public GuiWorldSlot(GuiSelectWorld guiSelectWorld1) {
		super(guiSelectWorld1.mc, guiSelectWorld1.width, guiSelectWorld1.height, 32, guiSelectWorld1.height - 64, 36);
		this.parentWorldGui = guiSelectWorld1;
	}

	protected int getSize() {
		return GuiSelectWorld.getSize(this.parentWorldGui).size();
	}

	protected void elementClicked(int i1, boolean z2) {
		GuiSelectWorld.onElementSelected(this.parentWorldGui, i1);
		boolean z3 = GuiSelectWorld.getSelectedWorld(this.parentWorldGui) >= 0 && GuiSelectWorld.getSelectedWorld(this.parentWorldGui) < this.getSize();
		GuiSelectWorld.getSelectButton(this.parentWorldGui).enabled = z3;
		GuiSelectWorld.getRenameButton(this.parentWorldGui).enabled = z3;
		GuiSelectWorld.getDeleteButton(this.parentWorldGui).enabled = z3;
		if(z2 && z3) {
			this.parentWorldGui.selectWorld(i1);
		}

	}

	protected boolean isSelected(int i1) {
		return i1 == GuiSelectWorld.getSelectedWorld(this.parentWorldGui);
	}

	protected int getContentHeight() {
		return GuiSelectWorld.getSize(this.parentWorldGui).size() * 36;
	}

	protected void drawBackground() {
		this.parentWorldGui.drawDefaultBackground();
	}

	protected void drawSlot(int i1, int i2, int i3, int i4, Tessellator tessellator5) {
		SaveFormatComparator saveFormatComparator6 = (SaveFormatComparator)GuiSelectWorld.getSize(this.parentWorldGui).get(i1);
		String string7 = saveFormatComparator6.getDisplayName();
		if(string7 == null || MathHelper.stringNullOrLengthZero(string7)) {
			string7 = GuiSelectWorld.getLocalizedWorldName(this.parentWorldGui) + " " + (i1 + 1);
		}

		String string8 = saveFormatComparator6.getFileName();
		string8 = string8 + " (" + GuiSelectWorld.getDateFormatter(this.parentWorldGui).format(new Date(saveFormatComparator6.getLastTimePlayed()));
		string8 = string8 + ")";
		String string9 = "";
		if(saveFormatComparator6.requiresConversion()) {
			string9 = GuiSelectWorld.getLocalizedMustConvert(this.parentWorldGui) + " " + string9;
		} else {
			string9 = GuiSelectWorld.getLocalizedGameMode(this.parentWorldGui)[saveFormatComparator6.getGameType()];
			if(saveFormatComparator6.isHardcoreModeEnabled()) {
				string9 = "\u00a74" + Translator.translateToLocal("gameMode.hardcore") + "\u00a78";
			}
		}

		this.parentWorldGui.drawString(this.parentWorldGui.fontRenderer, string7, i2 + 2, i3 + 1, 0xFFFFFF);
		this.parentWorldGui.drawString(this.parentWorldGui.fontRenderer, string8, i2 + 2, i3 + 12, 8421504);
		this.parentWorldGui.drawString(this.parentWorldGui.fontRenderer, string9, i2 + 2, i3 + 12 + 10, 8421504);
	}
}
