package net.minecraft.src;

import net.minecraft.client.Minecraft;

import org.lwjgl.input.Keyboard;

public class StatStringFormatKeyInv implements IStatStringFormat {
	final Minecraft field_27344_a;

	public StatStringFormatKeyInv(Minecraft minecraft1) {
		this.field_27344_a = minecraft1;
	}

	public String formatString(String string1) {
		return String.format(string1, new Object[]{Keyboard.getKeyName(this.field_27344_a.gameSettings.keyBindInventory.keyCode)});
	}
}
