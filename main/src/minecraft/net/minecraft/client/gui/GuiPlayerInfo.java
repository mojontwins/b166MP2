package net.minecraft.client.gui;

public class GuiPlayerInfo {
	public final String name;
	private final String nameinLowerCase;
	public int responseTime;

	public GuiPlayerInfo(String string1) {
		this.name = string1;
		this.nameinLowerCase = string1.toLowerCase();
	}

	public boolean nameStartsWith(String string1) {
		return this.nameinLowerCase.startsWith(string1);
	}
}
