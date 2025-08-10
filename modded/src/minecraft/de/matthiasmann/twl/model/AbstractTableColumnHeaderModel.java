package de.matthiasmann.twl.model;

import de.matthiasmann.twl.renderer.AnimationState;

public abstract class AbstractTableColumnHeaderModel implements TableColumnHeaderModel {
	private static final AnimationState.StateKey[] EMPTY_STATE_ARRAY = new AnimationState.StateKey[0];

	public AnimationState.StateKey[] getColumnHeaderStates() {
		return EMPTY_STATE_ARRAY;
	}

	public boolean getColumnHeaderState(int column, int stateIdx) {
		return false;
	}
}
