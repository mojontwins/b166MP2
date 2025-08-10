package de.matthiasmann.twl.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class SimpleGraphModel implements GraphModel {
	private final ArrayList lines;
	private boolean scaleLinesIndependant;

	public SimpleGraphModel() {
		this.lines = new ArrayList();
	}

	public SimpleGraphModel(GraphLineModel... lines) {
		this((Collection)Arrays.asList(lines));
	}

	public SimpleGraphModel(Collection lines) {
		this.lines = new ArrayList(lines);
	}

	public GraphLineModel getLine(int idx) {
		return (GraphLineModel)this.lines.get(idx);
	}

	public int getNumLines() {
		return this.lines.size();
	}

	public boolean getScaleLinesIndependant() {
		return this.scaleLinesIndependant;
	}

	public void setScaleLinesIndependant(boolean scaleLinesIndependant) {
		this.scaleLinesIndependant = scaleLinesIndependant;
	}

	public void addLine(GraphLineModel line) {
		this.insertLine(this.lines.size(), line);
	}

	public void insertLine(int idx, GraphLineModel line) {
		if(line == null) {
			throw new NullPointerException("line");
		} else if(this.indexOfLine(line) >= 0) {
			throw new IllegalArgumentException("line already added");
		} else {
			this.lines.add(idx, line);
		}
	}

	public int indexOfLine(GraphLineModel line) {
		int i = 0;

		for(int n = this.lines.size(); i < n; ++i) {
			if(this.lines.get(i) == line) {
				return i;
			}
		}

		return -1;
	}

	public GraphLineModel removeLine(int idx) {
		return (GraphLineModel)this.lines.remove(idx);
	}
}
