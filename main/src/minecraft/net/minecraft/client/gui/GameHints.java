package net.minecraft.client.gui;

import java.util.Random;

public class GameHints {
	public static Random rand = new Random(System.currentTimeMillis());
	
	public static final String[] hints = new String[] {
		"Use hoe on grass to find seeds!",
		"Craft a recipe book with ink and a book!",
		"Walk better on snow with leather boots!",
		"Edit signs using feathers!",
		"Some trees drop apples!",
		"Golden tools do something special!",
		"Wool can be dyed!",
		"Sheep can be dyed!",
		"CTRL+click to place logs straight",
		"Right click spawners with placer eggs",
		"Shears can cut grass, vines and leaves",
		"Break tall grass for seeds!",
	};
	
	public static String getRandomHint() {
		return hints[rand.nextInt(hints.length)];
	}
}
