package net.minecraft.client.gui;

import java.util.Random;

public class GameHints {
	public static Random rand = new Random(System.currentTimeMillis());
	
	public static final String[] hints = new String[] {
		"Use hoe on grass to find seeds!",
		"Craft a recipe book with ink and a book!",
		"Walk better on snow with leather boots!",
		"Edit signs using feathers!",
		"Sponges may be powered to remove water!",
		"Some trees drop apples!",
		"Use a compass on crying obsidian!",
		"Golden tools do something special!"
	};
	
	public static String getRandomHint() {
		return hints[rand.nextInt(hints.length)];
	}
}
