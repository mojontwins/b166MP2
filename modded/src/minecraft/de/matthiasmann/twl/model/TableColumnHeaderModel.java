package de.matthiasmann.twl.model;

import de.matthiasmann.twl.renderer.AnimationState;

public interface TableColumnHeaderModel {
	int getNumColumns();

	AnimationState.StateKey[] getColumnHeaderStates();

	String getColumnHeaderText(int i1);

	boolean getColumnHeaderState(int i1, int i2);

	public interface ColumnHeaderChangeListener {
		void columnInserted(int i1, int i2);

		void columnDeleted(int i1, int i2);

		void columnHeaderChanged(int i1);
	}
}
