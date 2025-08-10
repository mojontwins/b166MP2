package de.matthiasmann.twl.model;

public interface GraphModel {
	int getNumLines();

	GraphLineModel getLine(int i1);

	boolean getScaleLinesIndependant();
}
