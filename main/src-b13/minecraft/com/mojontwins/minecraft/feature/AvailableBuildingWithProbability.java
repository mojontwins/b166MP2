package com.mojontwins.minecraft.feature;

public class AvailableBuildingWithProbability {
	public Class<?> buildingClass;
	public int probability;
	
	public AvailableBuildingWithProbability(Class<?> buildingClass, int probability) {
		this.buildingClass = buildingClass;
		this.probability = probability;
	}
}
